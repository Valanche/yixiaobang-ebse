package com.weixin.njuteam.entity.po.help;

import com.weixin.njuteam.entity.vo.help.SeekHelpInfoClickVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.Alias;
import org.springframework.beans.BeanUtils;

import java.util.Date;

/**
 * @author Zyi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Alias("seekHelpInfoClickPO")
public class SeekHelpInfoClickPO {

	/**
	 * 点击id
	 */
	private Long id;
	/**
	 * 用户id
	 */
	private Long userId;
	/**
	 * seek help info id
	 */
	private Long seekHelpId;
	/**
	 * 用户开始看该条信息的时间
	 */
	private Date startTime;
	/**
	 * 用户结束看该条信息的时间
	 */
	private Date endTime;

	public SeekHelpInfoClickPO(SeekHelpInfoClickVO clickVo) {
		BeanUtils.copyProperties(clickVo, this);
	}
}
