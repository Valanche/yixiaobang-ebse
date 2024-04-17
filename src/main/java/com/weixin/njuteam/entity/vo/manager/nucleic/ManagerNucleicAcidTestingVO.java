package com.weixin.njuteam.entity.vo.manager.nucleic;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.weixin.njuteam.entity.po.manager.nucleic.ManagerNucleicAcidTestingPO;
import com.weixin.njuteam.entity.vo.manager.nucleic.base.BaseManagerNucleicAcidVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.beans.BeanUtils;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author Zyi
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ManagerNucleicAcidTestingVO extends BaseManagerNucleicAcidVO {

	/**
	 * testing require
	 */
	@ApiModelProperty(value = "检测要求", name = "require", example = "带好口罩和校园卡")
	private String require;
	/**
	 * testing place
	 */
	@ApiModelProperty(value = "检测地点", name = "place", example = "方肇周")
	private String place;
	/**
	 * testing start time
	 */
	@JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
	@JSONField(format = "yyyy/MM/dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy/MM/dd HH:mm:ss")
	@ApiModelProperty(value = "检测开始时间", name = "startTime", example = "2022/05/12 23:46:00")
	private Date startTime;
	/**
	 * testing end time
	 */
	@JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
	@JSONField(format = "yyyy/MM/dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy/MM/dd HH:mm:ss")
	@ApiModelProperty(value = "检测结束时间", name = "endTime", example = "2022/05/12 23:46:00")
	private Date endTime;

	public ManagerNucleicAcidTestingVO(ManagerNucleicAcidTestingPO testingPo) {
		BeanUtils.copyProperties(testingPo, this);
	}
}
