package com.weixin.njuteam.entity.po.manager;

import com.weixin.njuteam.entity.vo.manager.ManagerAndStudentVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.Alias;
import org.springframework.beans.BeanUtils;

import java.util.List;

/**
 * 连接管理员和用户的实体类
 *
 * @author Zyi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Alias("managerAndStudentPO")
public class ManagerAndStudentPO {

	/**
	 * 管理员id
	 */
	private Long managerId;
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
	 * 学生列表
	 */
	private List<Student> studentList;

	public ManagerAndStudentPO(ManagerAndStudentVO managerAndStudentVo) {
		BeanUtils.copyProperties(managerAndStudentVo, this);
	}

}
