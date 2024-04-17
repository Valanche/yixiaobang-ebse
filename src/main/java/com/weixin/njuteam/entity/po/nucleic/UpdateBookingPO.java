package com.weixin.njuteam.entity.po.nucleic;

import com.alibaba.fastjson.annotation.JSONField;
import com.weixin.njuteam.enums.FinishStatus;
import lombok.*;
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
@ToString
@Alias("updateBookingPO")
public class UpdateBookingPO {

	/**
	 * 预约通知id
	 */
	private Long id;
	/**
	 * 通知对应的用户id
	 */
	private Long userId;
	/**
	 * 发布该通知的管理员id
	 */
	private Long managerId;
	/**
	 * 通知标题
	 */
	private String title;
	/**
	 * 预约截止时间 format: yyyy-MM-dd HH:mm:ss
	 */
	private Date deadLine;
	/**
	 * 是否打开提醒
	 */
	private Boolean isOpenRemind;
	/**
	 * 是否完成
	 */
	private FinishStatus finishStatus;
	/**
	 * 旧标题
	 */
	private String oldTitle;

	public UpdateBookingPO(NucleicAcidBookingPO bookingPo, String oldTitle) {
		BeanUtils.copyProperties(bookingPo, this);
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
