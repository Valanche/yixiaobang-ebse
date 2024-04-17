package com.weixin.njuteam.entity.po;

import com.weixin.njuteam.entity.vo.UserVO;
import lombok.*;
import org.apache.ibatis.type.Alias;
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
@ToString
@Alias("userPO")
public class UserPO {

	/**
	 * 数据库中的用户id
	 */
	private Long id;
	/**
	 * 微信平台上的openid
	 */
	private String openId;
	/**
	 * 用户昵称
	 */
	private String nickName;
	/**
	 * 用户真实姓名
	 */
	private String name;
	/**
	 * 用户的学校
	 */
	private String school;
	/**
	 * 用户的院系
	 */
	private String institute;
	/**
	 * 用户的专业
	 */
	private String major;
	/**
	 * 用户的年级
	 */
	private String grade;
	/**
	 * 用户的性别
	 */
	private String gender;
	/**
	 * 头像url
	 */
	private String avatarUrl;

	public UserPO(UserVO userVo) {
		BeanUtils.copyProperties(userVo, this);
	}
}
