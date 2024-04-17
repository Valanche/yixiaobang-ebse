package com.weixin.njuteam.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.weixin.njuteam.entity.vo.MessageVO;
import com.weixin.njuteam.exception.UpdateException;
import com.weixin.njuteam.service.MessageService;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 使用websocket来制作服务端
 *
 * @author Zyi
 */
@Component
@ServerEndpoint("/web-server/{senderId}/{roomId}/{receiverId}")
@Slf4j
public class WebSocketServer {

	/**
	 * 单一通信，线程安全的map
	 */
	private static final Map<Long, WebSocketServer> CONNECTIONS = new ConcurrentHashMap<>();
	private static final Pattern PATTERN = Pattern.compile("[0-9]+[.]?[0-9]*[dD]?");
	private static final String LAST_SEND_MSG = "lastSendMsgTime";
	private static final String TIME_PATTERN = "yyyy/MM/dd HH:mm:ss";
	/**
	 * 记录当前服务器连接了多少的客户端
	 */
	private static volatile int count;
	private static MessageService messageService;
	/**
	 * 与某个客户端之间的对话
	 * 可以由客户端指定
	 */
	private Session session;
	/**
	 * 发送方的id 用于确认唯一的session
	 */
	private Long senderId;
	/**
	 * 接收方的id
	 */
	private Long receiverId;
	/**
	 * 指定房间号 用于确定两个人都在一个房间里
	 */
	private Long roomId;

	private static final String HEART_BEAT_KEY = "msg";
	private static final String HEART_BEAT_TEST = "heartBeat_test";

	@Autowired
	public void setMessageService(MessageService messageService) {
		// 该类需要有空参数的构造函数 所以使用setter来进行依赖注入
		WebSocketServer.messageService = messageService;
	}

	/**
	 * websocket连接建立成功调用该方法
	 *
	 * @param senderId 发送建立连接请求的用户id
	 * @param session  可选参数 两人之间的对话
	 */
	@OnOpen
	public void onOpen(@ApiParam(value = "senderId", required = true) @PathParam(value = "senderId") Long senderId,
					   @ApiParam(value = "receiverId", required = true) @PathParam(value = "receiverId") Long receiverId,
					   @ApiParam(value = "roomId", required = true) @PathParam(value = "roomId") Long roomId,
					   @ApiParam(value = "session") Session session) {
		this.session = session;
		this.receiverId = receiverId;
		this.roomId = roomId;
		this.senderId = senderId;

		if (!CONNECTIONS.containsKey(senderId)) {
			CONNECTIONS.put(senderId, this);
			addCount();
		}

		try {
			updateMessageRead();
		} catch (UpdateException e) {
			log.error("message read update failed!");
			try {
				session.close();
			} catch (IOException ex) {
				log.error("session close failed!" + e.getMessage());
			}
		}
	}

	/**
	 * websocket 连接关闭调用该方法
	 * 不用传senderId是因为连接的时候onOpen()已经传过了
	 */
	@OnClose
	public void onClose() {
		// 把连接从map中移除
		// 减少连接数记录
		if (CONNECTIONS.containsKey(senderId)) {
			CONNECTIONS.remove(senderId);
			decreaseCount();
		}
	}

	/**
	 * 收到客户端消息时调用
	 *
	 * @param message 客户端发送的消息
	 * @param session 可选参数
	 */
	@OnMessage
	public void onMessage(String message, Session session) {
		// 这里的message其实是前端传过来的MessageVO实体
		// 传输用json格式
		// JSONObject来转换
		JSONObject jsonObject = JSON.parseObject(message);

		// 判断是否是心跳检测
		if (jsonObject.containsKey(HEART_BEAT_KEY)) {
			// 说明是心跳检测
			String msg = jsonObject.getString(HEART_BEAT_KEY);
			if (HEART_BEAT_TEST.equals(msg)) {
				try {
					Map<String, Object> returnMsg = new HashMap<>(1);
					returnMsg.put("msg", "heartBeat OK");
					JSONObject json = new JSONObject(returnMsg);
					session.getBasicRemote().sendText(json.toJSONString());
					return;
				} catch (IOException e) {
					log.error("heartBeat send text error!");
				}
			}
		}

		String lastTimeMsg = null;
		// 判断是否有上一条信息 用来判断是否要显示时间

		if (jsonObject.containsKey(LAST_SEND_MSG)) {
			lastTimeMsg = jsonObject.getString(LAST_SEND_MSG);
		}

		// 拿到消息后转发给接收方
		sendMessage(jsonObject, lastTimeMsg);
	}

	/**
	 * 客户端出错时
	 *
	 * @param error   error
	 * @param session session
	 */
	@OnError
	public void onError(Throwable error, Session session) {
		log.error("user: " + senderId + ", error: " + error.getMessage());
		error.printStackTrace();
	}

	private void sendMessage(JSONObject jsonObject, String lastTimeMsg) {
		Long requestReceiverId = jsonObject.getLong("receiverId");
		MessageVO messageVO = MessageVO.builder()
			.senderId(jsonObject.getLong("senderId"))
			.receiverId(requestReceiverId)
			.content(jsonObject.getString("content"))
			.sendTime(jsonObject.getObject("sendTime", Date.class))
			.build();

		// 判断相差时间
		if (lastTimeMsg != null && !lastTimeMsg.trim().isEmpty()) {
			Date now = new Date(System.currentTimeMillis());
			Date lastTime = transferDateFormat(lastTimeMsg);
			long gapMinute = (now.getTime() - lastTime.getTime()) / (1000 * 60);

			// 如果距离上次发送时间超过5分钟，则显示时间
			messageVO.setIsShowTime(gapMinute >= 5);
		} else if (lastTimeMsg == null) {
			// 将isShowTime设为true
			// 说明要获取第一条消息
			messageVO.setIsShowTime(true);
		}

		// 先存入数据库
		boolean isInserted = messageService.insertMessage(messageVO);
		if (isInserted) {
			try {
				// 插入后转发给接收方
				WebSocketServer receiveServer = CONNECTIONS.get(requestReceiverId);
				// 检查两个人是不是在同一个聊天室中
				if (receiveServer != null && roomId.longValue() == receiveServer.roomId.longValue()) {
					// 这里其实就是发送给前端一个json格式的value object 然后由前端进行处理
					receiveServer.session.getBasicRemote().sendText(JSON.toJSONString(messageVO));
				}

				// 回调给发送方
				session.getBasicRemote().sendText(JSON.toJSONString(messageVO));
			} catch (IOException e) {
				log.error("send message failed! reason: " + JSON.toJSONString(messageVO) + ", " + e.getMessage());
			}
		}
	}

	private synchronized void addCount() {
		count++;
	}

	private synchronized void decreaseCount() {
		count--;
	}

	private Date transferDateFormat(String lastTimeMsg) {
		Date lastTime = new Date();
		try {
			if (isNumeric(lastTimeMsg)) {
				// 如果传的是时间戳
				String dateStr = new SimpleDateFormat(TIME_PATTERN).format(Double.parseDouble(lastTimeMsg));
				lastTime = new SimpleDateFormat(TIME_PATTERN).parse(dateStr);
			} else {
				// 如果传的不是时间戳 而是格式化的形式
				lastTime = new SimpleDateFormat(TIME_PATTERN).parse(lastTimeMsg);
			}
		} catch (ParseException e) {
			try {
				lastTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(lastTimeMsg);
			} catch (ParseException ex) {
				log.error("date still parse error! lastTimeMsg: " + lastTimeMsg);
				if (lastTimeMsg.contains("NaN")) {
					try {
						session.getBasicRemote().sendText("消息因为日期原因发送失败！");
					} catch (IOException exc) {
						log.error("send message error in WebSocketServer, " + exc.getMessage());
					}
				}
			}
		}

		return lastTime;
	}

	private boolean isNumeric(String str) {
		// 判断是否是double类型的变量
		Matcher isNum = PATTERN.matcher(str);

		return isNum.matches();
	}

	private void updateMessageRead() throws UpdateException {
		boolean isUpdate = messageService.updateRead(senderId, receiverId);
		if (!isUpdate) {
			throw new UpdateException("update message read error!");
		}
	}
}
