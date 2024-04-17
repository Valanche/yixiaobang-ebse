package com.weixin.njuteam.entity.po.manager.nucleic;

import com.weixin.njuteam.entity.vo.manager.nucleic.ManagerAndStudentInfoVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.Alias;
import org.springframework.beans.BeanUtils;

import java.util.List;

/**
 * @author Zyi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Alias("managerAndStudentInfoPO")
public class ManagerAndStudentInfoPO {

	/**
	 * 对应的info id
	 */
	private Long infoId;
	/**
	 * 对应的管理员id
	 */
	private Long managerId;
	/**
	 * 学生上报信息列表
	 */
	private List<StudentReportingInfo> studentList;

	public void setStudentList(List<StudentReportingInfo> studentList) {
		this.studentList = studentList;
	}

	public ManagerAndStudentInfoPO(ManagerAndStudentInfoVO infoVo) {
		BeanUtils.copyProperties(infoVo, this);
	}
}
