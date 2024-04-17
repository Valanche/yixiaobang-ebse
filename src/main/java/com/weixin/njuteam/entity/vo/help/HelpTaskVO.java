package com.weixin.njuteam.entity.vo.help;

import com.weixin.njuteam.enums.HelpFinishStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Optional;

/**
 * @author Zyi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HelpTaskVO {

	/**
	 * 该帮助通知的id
	 */
	private Long helpId;
	/**
	 * 截止时间，到点自动提交
	 */
	private Date deadLine;
	/**
	 * 要更新的状态
	 */
	private HelpFinishStatus finishStatus;

	public String getFinishStatus() {
		return Optional.ofNullable(finishStatus).map(HelpFinishStatus::getValue).orElse(null);
	}

	public void setFinishStatus(String value) {
		this.finishStatus = HelpFinishStatus.getStatusByValue(value);
	}
}
