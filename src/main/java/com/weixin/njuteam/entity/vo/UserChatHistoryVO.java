package com.weixin.njuteam.entity.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 用户聊天对象记录
 * 另一种实现方法是查出对应的messagePO 和 userPO 然后转换成新的VO
 *
 * @author Zyi
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserChatHistoryVO {

	/**
	 * 微信openId
	 */
	@ApiModelProperty(value = "数据库主键id", name = "id", example = "1")
	private Long id;
	/**
	 * 昵称
	 */
	@ApiModelProperty(value = "昵称", name = "nickName", example = "xxx")
	private String nickName;
	/**
	 * 头像url
	 */
	@ApiModelProperty(value = "头像url", name = "avatarUrl", example = "xxx")
	private String avatarUrl;
	/**
	 * 聊天内容
	 */
	@ApiModelProperty(value = "聊天信息", name = "content", example = "xxx")
	private String content;
	/**
	 * 最新的聊天时间
	 */
	@JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
	@JSONField(format = "yyyy/MM/dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy/MM/dd HH:mm:ss")
	@ApiModelProperty(value = "最新的聊天时间", name = "latestChatDate", example = "yyyy/MM/dd HH:mm:ss")
	private Date latestChatDate;
}
