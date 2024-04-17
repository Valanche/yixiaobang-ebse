package com.weixin.njuteam.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Zyi
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FriendVO {

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
}
