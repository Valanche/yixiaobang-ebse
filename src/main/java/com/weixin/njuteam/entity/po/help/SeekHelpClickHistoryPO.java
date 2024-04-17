package com.weixin.njuteam.entity.po.help;

import com.weixin.njuteam.entity.po.help.base.BaseClickHistoryPO;
import com.weixin.njuteam.entity.vo.help.SeekHelpClickHistoryVO;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.apache.ibatis.type.Alias;

/**
 * @author Zyi
 */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Alias("seekHelpClickHistoryPO")
public class SeekHelpClickHistoryPO extends BaseClickHistoryPO {

	/**
	 * 该浏览的帮助信息
	 */
	private SeekHelpInfoPO seekHelpInfo;

	public SeekHelpClickHistoryPO(SeekHelpClickHistoryVO historyVo) {
		this.id = historyVo.getId();
		this.userId = historyVo.getUserId();
		this.clickStartTime = historyVo.getClickStartTime();
		this.clickEndTime = historyVo.getClickEndTime();
		this.seekHelpInfo = new SeekHelpInfoPO(historyVo.getSeekHelpInfo());
	}
}
