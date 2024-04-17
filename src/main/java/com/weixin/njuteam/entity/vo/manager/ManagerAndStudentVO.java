package com.weixin.njuteam.entity.vo.manager;

import com.weixin.njuteam.entity.po.manager.ManagerAndStudentPO;
import com.weixin.njuteam.entity.po.manager.Student;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.util.List;

/**
 * @author Zyi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ManagerAndStudentVO {

	/**
	 * 管理员id
	 */
	@ApiModelProperty(value = "管理员id", name = "managerId", example = "1")
	private Long managerId;
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
	@ApiModelProperty(value = "年级", name = "grade", example = "本科2020级")
	private String grade;
	/**
	 * 学生列表
	 */
	@ApiModelProperty(value = "学生列表", name = "studentList", example = "xxx")
	private List<Student> studentList;

	public ManagerAndStudentVO(ManagerAndStudentPO managerAndStudentPo) {
		BeanUtils.copyProperties(managerAndStudentPo, this);
	}
}
