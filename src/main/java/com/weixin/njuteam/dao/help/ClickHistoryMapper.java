package com.weixin.njuteam.dao.help;

import com.weixin.njuteam.entity.po.help.HelpClickHistoryPO;
import com.weixin.njuteam.entity.po.help.SeekHelpClickHistoryPO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Zyi
 */
@Mapper
@Repository
public interface ClickHistoryMapper {

	/**
	 * 获取用户的帮助信息浏览记录
	 *
	 * @param userId user id
	 * @return list of help click history persistent object
	 */
	List<HelpClickHistoryPO> queryUserClickHelpHistory(Long userId);

	/**
	 * 获取用户的求助信息浏览记录
	 *
	 * @param userId user id
	 * @return list of seek help click history persistent object
	 */
	List<SeekHelpClickHistoryPO> queryUserClickSeekHelpHistory(Long userId);

	/**
	 * 获取某条帮助信息浏览记录
	 *
	 * @param clickId click id
	 * @return help click history persistent object
	 */
	HelpClickHistoryPO queryClickHelpHistoryById(Long clickId);

	/**
	 * 获取某条求助信息浏览记录
	 *
	 * @param clickId click id
	 * @return seek click history persistent object
	 */
	SeekHelpClickHistoryPO queryClickSeekHelpHistoryById(Long clickId);

	/**
	 * 删除用户的帮助信息浏览记录
	 *
	 * @param clickId click history id
	 * @return the amount of row affected by delete
	 */
	int deleteClickHelpHistoryById(Long clickId);

	/**
	 * 删除用户的求助信息浏览记录
	 *
	 * @param clickId click id
	 * @return the amount of row affected by delete
	 */
	int deleteClickSeekHelpHistoryById(Long clickId);
}
