package com.weixin.njuteam.entity.po.manager;

import com.weixin.njuteam.entity.vo.manager.ManagerVO;
import com.weixin.njuteam.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.Alias;
import org.springframework.beans.BeanUtils;

import java.util.Optional;

/**
 * @author Zyi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Alias("managerPO")
public class ManagerPO {

	/**
	 * id
	 */
	private Long id;
	/**
	 * 姓名
	 */
	private String name;
	/**
	 * 昵称
	 */
	private String nickName;
	/**
	 * 密码
	 */
	private String password;
	/**
	 * 角色权限
	 */
	private Role role;
	/**
	 * 职位
	 */
	private String post;
	/**
	 * 学校
	 */
	private String school;
	/**
	 * 院系
	 */
	private String institute;
	/**
	 * 专业
	 */
	private String major;
	/**
	 * 年级
	 */
	private String grade;
	/**
	 * 管理员头像url
	 */
	private String avatarUrl;
	/**
	 * 管理员微信openid
	 */
	private String openId;
	/**
	 * 管理员性别
	 */
	private String gender;

	public ManagerPO(ManagerVO managerVo) {
		BeanUtils.copyProperties(managerVo, this);
	}

	public String getRole() {
		return Optional.ofNullable(role).map(Role::getValue).orElse(null);
	}

	public void setRole(String value) {
		this.role = Role.getRoleByValue(value);
	}
}
