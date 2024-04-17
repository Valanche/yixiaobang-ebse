package com.weixin.njuteam.service.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.weixin.njuteam.config.AccessTokenTask;
import com.weixin.njuteam.config.cache.RedisOperator;
import com.weixin.njuteam.dao.UserMapper;
import com.weixin.njuteam.entity.po.UserPO;
import com.weixin.njuteam.entity.vo.FriendVO;
import com.weixin.njuteam.entity.vo.MessageVO;
import com.weixin.njuteam.entity.vo.UserChatHistoryVO;
import com.weixin.njuteam.entity.vo.UserVO;
import com.weixin.njuteam.service.MessageService;
import com.weixin.njuteam.service.UserService;
import com.weixin.njuteam.util.Pair;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Zyi
 */
@Service
@Slf4j
@CacheConfig(cacheNames = "user")
@ConfigurationProperties(prefix = "weixin")
public class UserServiceImpl implements UserService {

	private final UserMapper userMapper;
	private final MessageService messageService;
	private final RedisOperator redisOperator;
	private final AccessTokenTask accessTokenTask;
	@Setter
	private String appId;
	@Setter
	private String secret;

	@Setter
	private String grantType;

	private static final String MANAGER_USER_LIST_KEY = "studentList";

	@Autowired
	public UserServiceImpl(UserMapper userMapper, MessageService messageService, RedisOperator redisOperator, AccessTokenTask accessTokenTask) {
		this.userMapper = userMapper;
		this.messageService = messageService;
		this.redisOperator = redisOperator;
		this.accessTokenTask = accessTokenTask;
	}

	@Override
	public String getSessionId(String code) {
		String url = "https://api.weixin.qq.com/sns/jscode2session?appid={0}&secret={1}&js_code={2}&grant_type=authorization_code";
		String replaceUrl = url.replace("{0}", appId).replace("{1}", secret).replace("{2}", code);
		HttpResponse response = HttpRequest.get(replaceUrl).execute();

		return response.body();
	}

	@Override
	public String getAccessToken() {
		String actk = accessTokenTask.getAccessToken();
		if (actk == null){
			log.info("no token");
			accessTokenTask.postAccessToken();
			actk = accessTokenTask.getAccessToken();
		}
		if (actk == null){
			log.info("no token again!");
			actk = "dead";
		}
		return actk;
	}

	@Override
	public long queryUserId(String openId) {
		if (openId == null) {
			return -1;
		}
		return userMapper.queryUserId(openId);
	}

	@Override
	public String queryUserOpenId(Long userId) {
		if (userId == null || userId <= 0) {
			return null;
		}
		return userMapper.queryUserOpenId(userId);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public UserVO authLogin(UserVO userVo) throws SQLException {
		// 判断openid是不是空
		if (userVo.getOpenId() == null || userVo.getOpenId().trim().isEmpty()) {
			return null;
		}

		UserPO userPo = userMapper.queryUserByOpenId(userVo.getOpenId());
		UserPO registerUser = new UserPO(userVo);
		redisOperator.delete("user::" + registerUser.getOpenId());

		if (userPo == null) {
			int i = userMapper.insertUser(registerUser);
			redisOperator.deleteAll(MANAGER_USER_LIST_KEY);
			if (i <= 0) {
				// 说明插入不成功
				throw new SQLException("插入用户不成功！");
			}
		} else if (isNotTheSame(userPo, userVo)) {
			int i = userMapper.updateUser(userPo);
			if (i <= 0) {
				throw new SQLException("更新用户信息不成功! ");
			}
		}

		return userPo == null ? new UserVO(registerUser) : new UserVO(userPo);
	}

	@Override
	public UserVO queryUserById(Long userId) {
		UserPO user = userMapper.queryUserById(userId);
		if (user == null) {
			return null;
		}

		return new UserVO(user);
	}

	@Override
	@CacheEvict(cacheNames = "user", keyGenerator = "openIdGenerator")
	public UserVO updateUser(UserVO userVo) {
		// 判断openid是不是空
		if (userVo == null || userVo.getOpenId() == null || userVo.getOpenId().trim().isEmpty()) {
			return null;
		}

		UserPO userPo = new UserPO(userVo);
		int i = userMapper.updateUser(userPo);
		userVo.setId(userPo.getId());

		// delete cache in manager query all student list
		redisOperator.deleteAll(MANAGER_USER_LIST_KEY);

		// i是更新操作影响的行数，大于0说明更新成功
		return i > 0 ? userVo : null;
	}

	@Override
	@Cacheable(cacheNames = "user", key = "#openId")
	public UserVO queryUser(String openId) {
		if (openId == null || openId.trim().isEmpty()) {
			return null;
		}

		UserPO userPo = userMapper.queryUserByOpenId(openId);

		return userPo == null ? null : new UserVO(userPo);
	}

	@Override
	public FriendVO queryFriend(Long id) {
		UserPO userPo = userMapper.queryUserById(id);

		return FriendVO.builder()
			.id(userPo.getId())
			.nickName(userPo.getNickName())
			.avatarUrl(userPo.getAvatarUrl())
			.build();
	}

	@Override
	public List<Pair<Integer, UserChatHistoryVO>> queryChatHistory(String openId) {
		long id = queryUserId(openId);
		// 首先查找与该用户有关的所有消息记录
		List<MessageVO> messageList = messageService.queryUserMessage(id);
		// key: friendId, value: chat history
		Map<Long, Pair<Integer, UserChatHistoryVO>> chatMap = new HashMap<>();

		for (MessageVO message : messageList) {
			// 加入map并更新时间
			long friendId = getFriendId(message, id);

			// 如果map里已经有该记录
			if (chatMap.containsKey(friendId)) {
				// 检查时间，如果不是最新的则更新时间和聊天内容
				Pair<Integer, UserChatHistoryVO> chatHistory = chatMap.get(friendId);
				if (chatHistory.getValue().getLatestChatDate().before(message.getSendTime())) {
					// 新的记录时间比较新
					// 更新时间和内容
					chatHistory.getValue().setLatestChatDate(message.getSendTime());
					chatHistory.getValue().setContent(message.getContent());
				}
				// 更新未读消息数量
				// 注意这里的未读数量是friend是sender的情况
				if (isFriendSender(message, friendId) && Boolean.TRUE.equals(!message.getIsRead())) {
					chatHistory.setKey(chatHistory.getKey() + 1);
				}
			} else {
				// 否则将当前记录加入
				UserPO friend = userMapper.queryUserById(friendId);
				assert friend != null;
				UserChatHistoryVO chatHistory = UserChatHistoryVO.builder()
					.id(friendId)
					.nickName(friend.getNickName())
					.avatarUrl(friend.getAvatarUrl())
					.content(message.getContent())
					.latestChatDate(message.getSendTime())
					.build();

				int amount = (isFriendSender(message, friendId) && !message.getIsRead()) ? 1 : 0;
				chatMap.put(friendId, new Pair<>(amount, chatHistory));
			}
		}

		List<Pair<Integer, UserChatHistoryVO>> friendList = new ArrayList<>(chatMap.values());
		sortByTime(friendList);

		return friendList;
	}

	@Override
	@CacheEvict(cacheNames = "user", key = "#openId", beforeInvocation = true)
	public boolean deleteUser(String openId) {
		return userMapper.deleteUser(openId) > 0;
	}

	@Override
	public boolean deleteUserById(Long id) {
		return userMapper.deleteUserById(id) > 0;
	}

	private long getFriendId(MessageVO message, long id) {
		// 获得好友的user id
		if (message.getSenderId() == id) {
			// 说明好友是receiver
			return message.getReceiverId();
		} else if (message.getReceiverId() == id) {
			// 说明好友是sender
			return message.getSenderId();
		}

		return -1;
	}

	private boolean isFriendSender(MessageVO messageVO, long id) {
		return messageVO.getSenderId() == id;
	}

	private void sortByTime(List<Pair<Integer, UserChatHistoryVO>> list) {
		list.sort((o1, o2) -> o2.getValue().getLatestChatDate().compareTo(o1.getValue().getLatestChatDate()));
	}

	private boolean isNotTheSame(UserPO userPo, UserVO userVo) {
		return userPo.getNickName().equals(userVo.getNickName()) && userPo.getAvatarUrl().equals(userVo.getAvatarUrl());
	}
}
