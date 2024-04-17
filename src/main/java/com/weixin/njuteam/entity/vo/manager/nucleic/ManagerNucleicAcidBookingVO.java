package com.weixin.njuteam.entity.vo.manager.nucleic;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.weixin.njuteam.entity.po.manager.nucleic.ManagerNucleicAcidBookingPO;
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
public class ManagerNucleicAcidBookingVO extends BaseManagerNucleicAcidVO {

	/**
	 * 该预约通知的截止日期
	 */
	@JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
	@JSONField(format = "yyyy/MM/dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy/MM/dd HH:mm:ss")
	@ApiModelProperty(value = "预约截止时间", name = "deadLine", example = "2022/05/12 21:00:00")
	private Date deadLine;

	public ManagerNucleicAcidBookingVO(ManagerNucleicAcidBookingPO bookingPo) {
		BeanUtils.copyProperties(bookingPo, this);
	}
}
