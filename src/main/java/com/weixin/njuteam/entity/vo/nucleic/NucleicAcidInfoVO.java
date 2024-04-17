package com.weixin.njuteam.entity.vo.nucleic;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.weixin.njuteam.entity.po.nucleic.NucleicAcidInfoPO;
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
 * 小程序客户端看到的通知界面
 *
 * @author Zyi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(value = "核酸上报通知实体类")
public class NucleicAcidInfoVO {

	/**
	 * 通知id
	 */
	@ApiModelProperty(value = "info id", name = "id", example = "1")
	private Long id;
	/**
	 * 被通知的用户id
	 */
	@ApiModelProperty(value = "用户id", name = "userId", example = "1")
	private Long userId;
	/**
	 * 发布该通知的管理员id
	 */
	@ApiModelProperty(value = "管理员id", name = "managerId", example = "1")
	private Long managerId;
	/**
	 * 通知的开始时间
	 */
	@JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
	@JSONField(format = "yyyy/MM/dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy/MM/dd HH:mm:ss")
	@ApiModelProperty(value = "上报截止时间", name = "startTime", example = "2022/04/20")
	private Date deadLine;
	/**
	 * 通知的内容
	 */
	@ApiModelProperty(value = "上报的标题", name = "title", example = "abab")
	private String title;
	/**
	 * 通知是否完成
	 */
	@ApiModelProperty(value = "通知是否完成", name = "status", example = "进行中")
	private FinishStatus status;
	/**
	 * 是否打开通知提醒
	 */
	@ApiModelProperty(value = "是否打开通知提醒", name = "isOpenRemind", example = "false")
	private Boolean isOpenRemind;
	/**
	 * 未做核酸的理由
	 */
	@ApiModelProperty(value = "未做核酸的理由", name = "comment", example = "xxx")
	private String comment;

	public NucleicAcidInfoVO(NucleicAcidInfoPO info) {
		BeanUtils.copyProperties(info, this);
	}

	@JSONField(name = "status")
	public String getStatus() {
		return Optional.ofNullable(status).map(FinishStatus::getValue).orElse(null);
	}

	@JSONField(name = "status")
	public void setStatus(String value) {
		this.status = FinishStatus.getStatusByValue(value);
	}
}
