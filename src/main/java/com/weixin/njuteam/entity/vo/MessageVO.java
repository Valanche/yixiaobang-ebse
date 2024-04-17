package com.weixin.njuteam.entity.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.weixin.njuteam.entity.po.MessagePO;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * message value object
 *
 * @author Zyi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageVO {

	/**
	 * 消息id
	 */
	@ApiModelProperty(value = "消息id", name = "id", example = "1")
	private Long id;
	/**
	 * 发送消息的用户id
	 */
	@ApiModelProperty(value = "发送消息的用户id", name = "senderId", example = "1")
	private Long senderId;
	/**
	 * 接收消息的用户id
	 */
	@ApiModelProperty(value = "接收消息的用户id", name = "receiverId", example = "1")
	private Long receiverId;
	/**
	 * 消息内容
	 */
	@ApiModelProperty(value = "消息内容", name = "content", example = "阿巴阿巴")
	private String content;
	/**
	 * 发送的时间
	 */
	@JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
	@JSONField(format = "yyyy/MM/dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy/MM/dd HH:mm:ss")
	@ApiModelProperty(value = "发送该消息的时间", name = "sendTime", example = "2022/04/19 23:05")
	private Date sendTime;
	/**
	 * 是否需要在前端展示时间
	 */
	@ApiModelProperty(value = "是否需要在前端展示时间", name = "isShowTime", example = "true")
	private Boolean isShowTime;
	/**
	 * 是否已读
	 */
	@ApiModelProperty(value = "是否已经读了该消息", name = "isRead", example = "false")
	private Boolean isRead;

	public MessageVO(MessagePO messagePo) {
		// 第三个参数代表要忽略的属性
		BeanUtils.copyProperties(messagePo, this);
	}
}
