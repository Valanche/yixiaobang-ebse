package com.weixin.njuteam.entity.vo.manager.nucleic.base;

import com.weixin.njuteam.enums.FinishStatus;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Optional;

/**
 * @author Zyi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class BaseManagerNucleicAcidVO {

	/**
	 * 预约通知id
	 */
	@ApiModelProperty(value = "id", name = "id", example = "1")
	protected Long id;
	/**
	 * 发布该消息的管理员id
	 */
	@ApiModelProperty(value = "管理员id", name = "managerId", example = "1")
	protected Long managerId;
	/**
	 * 完成状态
	 */
	@ApiModelProperty(value = "完成状态", name = "finishStatus", example = "进行中")
	protected FinishStatus finishStatus;
	/**
	 * 该预约通知的title
	 */
	@ApiModelProperty(value = "预约通知标题", name = "title", example = "第十次常态化核酸")
	protected String title;


	public String getFinishStatus() {
		return Optional.ofNullable(finishStatus).map(FinishStatus::getValue).orElse(null);
	}

	public void setFinishStatus(String value) {
		this.finishStatus = FinishStatus.getStatusByValue(value);
	}
}
