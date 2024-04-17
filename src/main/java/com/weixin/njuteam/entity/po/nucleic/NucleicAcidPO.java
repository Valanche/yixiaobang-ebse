package com.weixin.njuteam.entity.po.nucleic;

import com.alibaba.fastjson.annotation.JSONField;
import com.weixin.njuteam.enums.FinishStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.Alias;

import java.util.Date;
import java.util.Optional;

/**
 * @author Zyi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Alias("nucleicAcidPO")
public class NucleicAcidPO {

	/**
	 * 标题
	 */
	private String title;
	/**
	 * 实际检测时间
	 */
	private Date testingTime;
	/**
	 * 检测通知完成状态
	 */
	private FinishStatus testingFinishStatus;
	/**
	 * 上报通知完成状态
	 */
	private FinishStatus reportingFinishStatus;
	/**
	 * 预约通知完成状态
	 */
	private FinishStatus bookingFinishStatus;

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
