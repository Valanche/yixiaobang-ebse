package com.weixin.njuteam.entity.po.manager.nucleic;

import com.weixin.njuteam.entity.po.manager.nucleic.base.BaseManagerNucleicAcidPO;
import com.weixin.njuteam.entity.vo.manager.nucleic.ManagerNucleicAcidBookingVO;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.apache.ibatis.type.Alias;
import org.springframework.beans.BeanUtils;

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
@Alias("managerBookingPo")
public class ManagerNucleicAcidBookingPO extends BaseManagerNucleicAcidPO {

	/**
	 * 该预约通知的截止日期
	 */
	private Date deadLine;

	public ManagerNucleicAcidBookingPO(ManagerNucleicAcidBookingVO bookingVo) {
		BeanUtils.copyProperties(bookingVo, this);
	}
}
