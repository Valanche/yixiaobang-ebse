package com.weixin.njuteam.entity.vo.manager.nucleic;

import com.weixin.njuteam.entity.po.manager.nucleic.ManagerAndStudentInfoPO;
import com.weixin.njuteam.entity.po.manager.nucleic.StudentReportingInfo;
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
public class ManagerAndStudentInfoVO {

	/**
	 * 对应的管理员id
	 */
	@ApiModelProperty(value = "管理员id", name = "managerId", example = "1")
	private Long managerId;
	/**
	 * 对应的info id
	 */
	@ApiModelProperty(value = "上报信息id", name = "infoId", example = "1")
	private Long infoId;
	/**
	 * 学生上报信息列表
	 */
	@ApiModelProperty(value = "学生上报信息列表", name = "studentList", example = "xxx")
	private List<StudentReportingInfo> studentList;

	public ManagerAndStudentInfoVO(ManagerAndStudentInfoPO infoPo) {
		BeanUtils.copyProperties(infoPo, this);
	}
}
