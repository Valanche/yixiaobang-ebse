package com.weixin.njuteam.entity.po.nucleic;

import com.alibaba.fastjson.annotation.JSONField;
import com.weixin.njuteam.enums.FinishStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.Alias;
import org.springframework.beans.BeanUtils;

import java.util.Date;
import java.util.Optional;

/**
 * @author Zyi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Alias("updateTestingPO")
public class UpdateTestingPO {

	/**
	 * testing id
	 */
	private Long id;
	/**
	 * user id
	 */
	private Long userId;
	/**
	 * manager id
	 */
	private Long managerId;
	/**
	 * 检测的标题
	 */
	private String title;
	/**
	 * 检测要求
	 */
	private String require;
	/**
	 * 检测开始时间
	 */
	private Date startTime;
	/**
	 * 检测结束时间
	 */
	private Date endTime;
	/**
	 * 真正检测时间
	 */
	private Date trueTime;
	/**
	 * 是否打开提醒
	 */
	private Boolean isOpenRemind;
	/**
	 * 检测地点
	 */
	private String place;
	/**
	 * 完成状态
	 */
	private FinishStatus finishStatus;
	/**
	 * 旧标题
	 */
	private String oldTitle;

	public UpdateTestingPO(NucleicAcidTestingPO testingPo, String oldTitle) {
		BeanUtils.copyProperties(testingPo, this);
		this.oldTitle = oldTitle;
	}

	@JSONField(name = "finishStatus")
	public String getFinishStatus() {
		return Optional.ofNullable(finishStatus).map(FinishStatus::getValue).orElse(null);
	}

	@JSONField(name = "finishStatus")
	public void setFinishStatus(String value) {
		this.finishStatus = FinishStatus.getStatusByValue(value);
	}
}
