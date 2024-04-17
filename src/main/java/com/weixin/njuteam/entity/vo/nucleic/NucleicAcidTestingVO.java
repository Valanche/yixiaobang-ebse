package com.weixin.njuteam.entity.vo.nucleic;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.weixin.njuteam.entity.po.nucleic.NucleicAcidTestingPO;
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
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(value = "核酸检测通知实体类")
public class NucleicAcidTestingVO {

	/**
	 * testing id
	 */
	@ApiModelProperty(value = "testing id", name = "id", example = "1")
	private Long id;
	/**
	 * user id
	 */
	@ApiModelProperty(value = "user id", name = "userId", example = "1")
	private Long userId;
	/**
	 * manager id
	 */
	@ApiModelProperty(value = "manager id", name = "managerId", example = "1")
	private Long managerId;
	/**
	 * 检测的标题
	 */
	@ApiModelProperty(value = "核酸标题", name = "title", example = "第八次常态化核酸")
	private String title;
	/**
	 * 检测要求
	 */
	@ApiModelProperty(value = "检测要求", name = "require", example = "abab")
	private String require;
	/**
	 * 检测开始时间
	 */
	@JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
	@JSONField(format = "yyyy/MM/dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy/MM/dd HH:mm:ss")
	@ApiModelProperty(value = "检测开始时间", name = "startTime", example = "2022/04/20 10:10:10")
	private Date startTime;
	/**
	 * 检测结束时间
	 */
	@JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
	@JSONField(format = "yyyy/MM/dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy/MM/dd HH:mm:ss")
	@ApiModelProperty(value = "检测结束时间", name = "startTime", example = "2022/04/20 10:10:10")
	private Date endTime;
	/**
	 * 真正检测时间
	 */
	@JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
	@JSONField(format = "yyyy/MM/dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy/MM/dd HH:mm:ss")
	@ApiModelProperty(value = "真正检测时间 不需要传", name = "startTime", example = "2022/04/20 10:10:10")
	private Date trueTime;
	/**
	 * 是否打开提醒
	 */
	@ApiModelProperty(value = "是否打开提醒", name = "isOpenRemind", example = "false")
	private Boolean isOpenRemind;
	/**
	 * 检测地点
	 */
	@ApiModelProperty(value = "核酸检测地点", name = "place", example = "abab")
	private String place;
	/**
	 * 完成状态
	 */
	@ApiModelProperty(value = "完成的状态", name = "finishStatus", example = "进行中")
	private FinishStatus finishStatus;

	public NucleicAcidTestingVO(NucleicAcidTestingPO testingPo) {
		BeanUtils.copyProperties(testingPo, this);
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
