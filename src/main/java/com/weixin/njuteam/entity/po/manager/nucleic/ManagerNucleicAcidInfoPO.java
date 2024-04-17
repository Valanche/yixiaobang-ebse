package com.weixin.njuteam.entity.po.manager.nucleic;

import com.weixin.njuteam.entity.po.manager.nucleic.base.BaseManagerNucleicAcidPO;
import com.weixin.njuteam.entity.vo.manager.nucleic.ManagerNucleicAcidInfoVO;
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
@Alias("managerInfoPo")
public class ManagerNucleicAcidInfoPO extends BaseManagerNucleicAcidPO {

	/**
	 * info deadline
	 */
	private Date deadLine;

	public ManagerNucleicAcidInfoPO(ManagerNucleicAcidInfoVO infoVo) {
		BeanUtils.copyProperties(infoVo, this);
	}
}
