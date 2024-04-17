package com.weixin.njuteam.service;

import com.weixin.njuteam.entity.vo.help.HelpClickHistoryVO;
import com.weixin.njuteam.entity.vo.help.SeekHelpClickHistoryVO;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Zyi
 */
public interface ClickHistoryService {

	/**
	 * query help click history by click id
	 *
	 * @param id click id
	 * @return help click history value object
	 */
	HelpClickHistoryVO queryHelpClickById(@NotNull Long id);

	/**
	 * query seek help click history by click id
	 *
	 * @param id click id
	 * @return seek help click history value object
	 */
	SeekHelpClickHistoryVO querySeekHelpClickById(@NotNull Long id);

	/**
	 * query help click history by user id
	 *
	 * @param openId user open id
	 * @return list of help click history value object
	 */
	List<HelpClickHistoryVO> queryHelpClickByUserId(@NotNull String openId);

	/**
	 * query seek help click history by user id
	 *
	 * @param openId user open id
	 * @return list of seek help click history value object
	 */
	List<SeekHelpClickHistoryVO> querySeekHelpClickByUserId(@NotNull String openId);

	/**
	 * delete help click history by click id
	 *
	 * @param id click id
	 * @return true if delete successfully, false otherwise
	 */
	boolean deleteHelpClickById(@NotNull Long id);

	/**
	 * delete seek help click history by click id
	 *
	 * @param id click id
	 * @return true if delete successfully, false otherwise
	 */
	boolean deleteSeekHelpClickById(@NotNull Long id);
}
