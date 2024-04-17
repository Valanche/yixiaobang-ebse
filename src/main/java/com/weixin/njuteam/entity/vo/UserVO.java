package com.weixin.njuteam.entity.vo;

import com.weixin.njuteam.entity.po.UserPO;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

/**
 * 微信不提供性别和所在地区等信息了，所以这边只需要昵称和头像url即可
 *
 * @author Zyi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserVO {

	/**
	 * 数据库中的用户id
	 */
	@ApiModelProperty(value = "用户id", name = "id", example = "1")
	private Long id;
	/**
	 * 微信平台上的openid
	 */
	@ApiModelProperty(value = "微信平台openId", name = "openId", example = "xxx")
	private String openId;
	/**
	 * 用户昵称
	 */
	@ApiModelProperty(value = "用户昵称", name = "nickName", example = "name")
	private String nickName;
	/**
	 * 用户真实姓名
	 */
	@ApiModelProperty(value = "用户真实姓名", name = "name", example = "xx")
	private String name;
	/**
	 * 用户的学校
	 */
	@ApiModelProperty(value = "用户所在学校", name = "school", example = "南京大学")
	private String school;
	/**
	 * 用户的院系
	 */
	@ApiModelProperty(value = "用户所在院系", name = "institute", example = "软件学院")
	private String institute;
	/**
	 * 用户的专业
	 */
	@ApiModelProperty(value = "用户所在专业", name = "major", example = "软件工程")
	private String major;
	/**
	 * 用户的年级
	 */
	@ApiModelProperty(value = "用户年级", name = "grade", example = "大一")
	private String grade;
	/**
	 * 用户的性别
	 */
	@ApiModelProperty(value = "用户性别", name = "gender", example = "男")
	private String gender;
	/**
	 * 头像url
	 */
	@ApiModelProperty(value = "用户头像url", name = "avatarURL", example = "xxx")
	private String avatarUrl;

	public UserVO(String openId, String nickName, String avatarUrl) {
		this.openId = openId;
		this.nickName = nickName;
		this.avatarUrl = avatarUrl;
	}

	public UserVO(UserPO userPo) {
		BeanUtils.copyProperties(userPo, this);
	}
}
