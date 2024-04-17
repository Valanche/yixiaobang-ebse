package com.weixin.njuteam.entity.po.help;

import com.weixin.njuteam.entity.vo.help.SeekHelpSearchHistoryVO;
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
@Alias("historyPO")
public class SeekHelpSearchHistoryPO {

	/**
	 * 帮助信息搜索记录id
	 */
	private Long id;
	/**
	 * 该历史记录对应的user id
	 */
	private Long userId;
	/**
	 * 本次搜索的时间
	 */
	private Date searchTime;
	/**
	 * 本次搜索的关键词
	 */
	private String keyword;

	public SeekHelpSearchHistoryPO(SeekHelpSearchHistoryVO historyVo) {
		BeanUtils.copyProperties(historyVo, this);
	}
}
