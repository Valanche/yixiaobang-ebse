package com.weixin.njuteam.entity.vo.manager;

import com.weixin.njuteam.entity.po.manager.ManagerPO;
import com.weixin.njuteam.enums.Role;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.util.Optional;

/**
 * @author Zyi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ManagerVO {

	/**
	 * id
	 */
	@ApiModelProperty(value = "id", name = "id", example = "1")
	private Long id;
	/**
	 * 姓名
	 */
	@ApiModelProperty(value = "姓名", name = "name", example = "xxx")
	private String name;
	/**
	 * 昵称
	 */
	@ApiModelProperty(value = "昵称", name = "nickName", example = "xxx")
	private String nickName;
	/**
	 * 密码
	 */
	@ApiModelProperty(value = "密码", name = "password", example = "xxx")
	private String password;
	/**
	 * 角色权限
	 */
	@ApiModelProperty(value = "角色权限", name = "role", example = "普通管理员")
	private Role role;
	/**
	 * 职位
	 */
	@ApiModelProperty(value = "职位", name = "post", example = "辅导员")
	private String post;
	/**
	 * 学校
	 */
	@ApiModelProperty(value = "学校", name = "school", example = "南京大学")
	private String school;
	/**
	 * 院系
	 */
	@ApiModelProperty(value = "院系", name = "institute", example = "软件学院")
	private String institute;
	/**
	 * 专业
	 */
	@ApiModelProperty(value = "专业", name = "major", example = "软件工程")
	private String major;
	/**
	 * 年级
	 */
	@ApiModelProperty(value = "年级", name = "grade", example = "本科2020")
	private String grade;
	/**
	 * 管理员头像url
	 */
	@ApiModelProperty(value = "管理员头像url", name = "avatarUrl", example = "xxx")
	private String avatarUrl;
	/**
	 * 管理员微信openid
	 */
	@ApiModelProperty(value = "管理员微信openid", name = "openId", example = "xxx")
	private String openId;
	/**
	 * 管理员性别
	 */
	@ApiModelProperty(value = "管理员性别", name = "gender", example = "不清楚")
	private String gender;

	public ManagerVO(ManagerPO managerPo) {
		BeanUtils.copyProperties(managerPo, this);
	}

	public String getRole() {
		return Optional.ofNullable(role).map(Role::getValue).orElse(null);
	}

	public void setRole(String value) {
		this.role = Role.getRoleByValue(value);
	}
}
