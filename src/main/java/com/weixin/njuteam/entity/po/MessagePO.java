package com.weixin.njuteam.entity.po;

import com.weixin.njuteam.entity.vo.MessageVO;
import lombok.*;
import org.apache.ibatis.type.Alias;
import org.springframework.beans.BeanUtils;

import java.util.Date;

/**
 * message persistent object
 *
 * @author Zyi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Alias("messagePO")
public class MessagePO {

	/**
	 * 消息id
	 */
	private Long id;
	/**
	 * 发送消息的用户id
	 */
	private Long senderId;
	/**
	 * 接收消息的用户id
	 */
	private Long receiverId;
	/**
	 * 消息内容
	 */
	private String content;
	/**
	 * 发送的时间
	 */
	private Date sendTime;
	/**
	 * 是否显示时间
	 */
	private Boolean isShowTime;
	/**
	 * 是否已读
	 */
	private Boolean isRead;

	public MessagePO(MessageVO messageVo) {
		BeanUtils.copyProperties(messageVo, this);
	}
}
