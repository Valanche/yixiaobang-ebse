package com.weixin.njuteam.entity.vo.help;

import com.weixin.njuteam.entity.po.help.SeekHelpClickHistoryPO;
import com.weixin.njuteam.entity.vo.help.base.BaseClickHistoryVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * @author Zyi
 */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class SeekHelpClickHistoryVO extends BaseClickHistoryVO {

	/**
	 * 该浏览的帮助信息
	 */
	@ApiModelProperty(value = "该浏览的求助信息", name = "helpInfo", example = "xxx")
	private SeekHelpInfoVO seekHelpInfo;

	public SeekHelpClickHistoryVO(SeekHelpClickHistoryPO historyPo) {
		this.id = historyPo.getId();
		this.userId = historyPo.getUserId();
		this.clickStartTime = historyPo.getClickStartTime();
		this.clickEndTime = historyPo.getClickEndTime();
		if (historyPo.getSeekHelpInfo() != null) {
			this.seekHelpInfo = new SeekHelpInfoVO(historyPo.getSeekHelpInfo());
		}
	}
}
