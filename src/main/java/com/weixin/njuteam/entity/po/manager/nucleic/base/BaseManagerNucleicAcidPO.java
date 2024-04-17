package com.weixin.njuteam.entity.po.manager.nucleic.base;

import com.weixin.njuteam.enums.FinishStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Optional;

/**
 * @author Zyi
 */
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Data
public abstract class BaseManagerNucleicAcidPO {

	/**
	 * 预约通知id
	 */
	protected Long id;
	/**
	 * 发布该消息的管理员id
	 */
	protected Long managerId;
	/**
	 * 完成状态
	 */
	protected FinishStatus finishStatus;
	/**
	 * 该预约通知的title
	 */
	protected String title;

	public String getFinishStatus() {
		return Optional.ofNullable(finishStatus).map(FinishStatus::getValue).orElse(null);
	}

	public void setFinishStatus(String value) {
		this.finishStatus = FinishStatus.getStatusByValue(value);
	}
}
