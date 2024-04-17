package com.weixin.njuteam.entity.vo.nucleic;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.weixin.njuteam.entity.po.nucleic.NucleicAcidBookingPO;
import com.weixin.njuteam.enums.FinishStatus;
import io.swagger.annotations.ApiModel;
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
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "核酸预约通知实体类")
public class NucleicAcidBookingVO {

	/**
	 * 预约通知id
	 */
	@ApiModelProperty(value = "预约通知id", name = "id", example = "1")
	private Long id;
	/**
	 * 通知对应的用户id
	 */
	@ApiModelProperty(value = "用户id", name = "userId", example = "1")
	private Long userId;
	/**
	 * 发布该通知的管理员id
	 */
	@ApiModelProperty(value = "管理员id", name = "managerId", example = "1")
	private Long managerId;
	/**
	 * 通知标题
	 */
	@ApiModelProperty(value = "通知标题", name = "title", example = "第八次全员核酸")
	private String title;
	/**
	 * 预约截止时间 format: yyyy/MM/dd HH:mm:ss
	 */
	@JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
	@JSONField(format = "yyyy/MM/dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy/MM/dd HH:mm:ss")
	@ApiModelProperty(value = "通知截止时间", name = "deadLine", example = "2022/04/21")
	private Date deadLine;
	/**
	 * 是否打开提醒
	 */
	@ApiModelProperty(value = "是否打开提醒", name = "isOpenRemind", example = "false")
	private Boolean isOpenRemind;
	/**
	 * 是否完成
	 */
	@ApiModelProperty(value = "完成状态", name = "finishStatus", example = "进行中")
	private FinishStatus finishStatus;

	public NucleicAcidBookingVO(NucleicAcidBookingPO bookingPo) {
		BeanUtils.copyProperties(bookingPo, this);
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
