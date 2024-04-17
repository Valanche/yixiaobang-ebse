package com.weixin.njuteam.entity.vo.nucleic;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.weixin.njuteam.entity.po.nucleic.NucleicAcidPO;
import com.weixin.njuteam.enums.FinishStatus;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.Optional;

/**
 * @author Zyi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NucleicAcidVO {

	/**
	 * 标题
	 */
	@ApiModelProperty(value = "核酸标题", name = "title", example = "第五次常规核酸")
	private String title;
	/**
	 * 实际检测时间
	 */
	@JsonFormat(pattern = "yyyy/MM/dd", timezone = "GMT+8")
	@JSONField(format = "yyyy/MM/dd")
	@DateTimeFormat(pattern = "yyyy/MM/dd")
	@ApiModelProperty(value = "实际检测时间", name = "testingTime", example = "2022/04/23")
	private Date testingTime;
	/**
	 * 检测通知完成状态
	 */
	@ApiModelProperty(value = "检测通知完成的状态", name = "finishStatus", example = "进行中")
	private FinishStatus testingFinishStatus;
	/**
	 * 上报通知完成状态
	 */
	@ApiModelProperty(value = "上报通知完成的状态", name = "finishStatus", example = "进行中")
	private FinishStatus reportingFinishStatus;
	/**
	 * 预约通知完成状态
	 */
	@ApiModelProperty(value = "预约通知完成的状态", name = "finishStatus", example = "进行中")
	private FinishStatus bookingFinishStatus;

	public NucleicAcidVO(NucleicAcidPO acidPo) {
		BeanUtils.copyProperties(acidPo, this);
	}

	@JSONField(name = "testingFinishStatus")
	public String getTestingFinishStatus() {
		return Optional.ofNullable(testingFinishStatus).map(FinishStatus::getValue).orElse(null);
	}

	@JSONField(name = "testingFinishStatus")
	public void setTestingFinishStatus(String value) {
		this.testingFinishStatus = FinishStatus.getStatusByValue(value);
	}

	@JSONField(name = "reportingFinishStatus")
	public String getReportingFinishStatus() {
		return Optional.ofNullable(reportingFinishStatus).map(FinishStatus::getValue).orElse(null);
	}

	@JSONField(name = "reportingFinishStatus")
	public void setReportingFinishStatus(String value) {
		this.reportingFinishStatus = FinishStatus.getStatusByValue(value);
	}

	@JSONField(name = "bookingFinishStatus")
	public String getBookingFinishStatus() {
		return Optional.ofNullable(bookingFinishStatus).map(FinishStatus::getValue).orElse(null);
	}

	@JSONField(name = "bookingFinishStatus")
	public void setBookingFinishStatus(String value) {
		this.bookingFinishStatus = FinishStatus.getStatusByValue(value);
	}
}
