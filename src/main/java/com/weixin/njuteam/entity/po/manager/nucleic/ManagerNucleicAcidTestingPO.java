package com.weixin.njuteam.entity.po.manager.nucleic;

import com.weixin.njuteam.entity.po.manager.nucleic.base.BaseManagerNucleicAcidPO;
import com.weixin.njuteam.entity.vo.manager.nucleic.ManagerNucleicAcidTestingVO;
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
@Alias("managerTestingPo")
public class ManagerNucleicAcidTestingPO extends BaseManagerNucleicAcidPO {

	/**
	 * testing require
	 */
	private String require;
	/**
	 * testing place
	 */
	private String place;
	/**
	 * testing start time
	 */
	private Date startTime;
	/**
	 * testing end time
	 */
	private Date endTime;

	public ManagerNucleicAcidTestingPO(ManagerNucleicAcidTestingVO testingVo) {
		BeanUtils.copyProperties(testingVo, this);
	}
}
