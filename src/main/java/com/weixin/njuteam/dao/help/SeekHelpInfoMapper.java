package com.weixin.njuteam.dao.help;

import com.weixin.njuteam.entity.po.help.SeekHelpImage;
import com.weixin.njuteam.entity.po.help.SeekHelpInfoClickPO;
import com.weixin.njuteam.entity.po.help.SeekHelpInfoPO;
import com.weixin.njuteam.entity.po.help.SeekHelpSearchHistoryPO;
import com.weixin.njuteam.enums.SeekHelpFinishStatus;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Zyi
 */
@Mapper
@Repository
public interface SeekHelpInfoMapper {

	/**
	 * query seek help info by id
	 *
	 * @param id seek help info id
	 * @return seek help info persistent object
	 */
	SeekHelpInfoPO querySeekInfoById(long id);

	/**
	 * query user's seek help info list
	 *
	 * @param userId user id
	 * @return list of seek help info which belong to the user
	 */
	List<SeekHelpInfoPO> querySeekInfoByUserId(long userId);

	/**
	 * get all seek help info persistent object
	 *
	 * @return list of seek help info persistent object
	 */
	List<SeekHelpInfoPO> querySeekInfoInProgress();

	/**
	 * search seek help info by keyword
	 *
	 * @param keyword keyword
	 * @return list of seek help info which is contains the keyword
	 */
	List<SeekHelpInfoPO> querySeekInfoByKeyword(String keyword);

	/**
	 * query seek help info search history by user id
	 *
	 * @param userId user id
	 * @param size   search history size
	 * @return list of seek help info search history persistent object
	 */
	List<SeekHelpSearchHistoryPO> querySearchHistory(long userId, int size);

	/**
	 * query seek help image
	 *
	 * @param imageId image id
	 * @return seek help image
	 */
	SeekHelpImage querySeekHelpImage(long imageId);

	/**
	 * query seek help info click persistent object
	 *
	 * @param userId     userid
	 * @param seekHelpId seek help info id
	 * @return seek help info click persistent object
	 */
	SeekHelpInfoClickPO queryClick(long userId, long seekHelpId);

	/**
	 * insert seek help info
	 *
	 * @param seekHelpInfoPo seek help info persistent object
	 * @return the amount of row affect by insert
	 */
	int insertSeekInfo(SeekHelpInfoPO seekHelpInfoPo);

	/**
	 * insert image
	 *
	 * @param seekHelpImage seek help image
	 * @return the amount of row affect by insert
	 */
	int insertSeekImage(SeekHelpImage seekHelpImage);

	/**
	 * insert user click
	 *
	 * @param clickPo click persistent object
	 * @return the amount of row affect by insert
	 */
	int insertClick(SeekHelpInfoClickPO clickPo);

	/**
	 * update click end
	 *
	 * @param clickId click id
	 * @return the amount of row affect by update
	 */
	int updateClickEndTime(long clickId);

	/**
	 * insert user's search history
	 *
	 * @param historyPo seek help info search history persistent object
	 * @return the amount of row affect by insert
	 */
	int insertSearchHistory(SeekHelpSearchHistoryPO historyPo);

	/**
	 * update seek help info
	 *
	 * @param seekInfoPo seek help info persistent object
	 * @return the amount of row affect by update
	 */
	int updateSeekInfo(SeekHelpInfoPO seekInfoPo);

	/**
	 * update seek help image
	 *
	 * @param seekImage image
	 * @return the amount of row affect by update
	 */
	int updateSeekImage(SeekHelpImage seekImage);

	/**
	 * update help info finish status
	 *
	 * @param seekId       seek help id
	 * @param finishStatus help finish status
	 * @return the amount of row affect by update
	 */
	int updateFinishStatus(long seekId, SeekHelpFinishStatus finishStatus);

	/**
	 * update new click history
	 *
	 * @param clickId click id
	 * @return the amount of row affect by update
	 */
	int updateClick(long clickId);

	/**
	 * delete seek help info
	 *
	 * @param id seek help info id
	 * @return the amount of row affect by delete
	 */
	int deleteSeekInfo(long id);

	/**
	 * delete all image by seek help info id
	 *
	 * @param seekHelpId seek help info id
	 * @return the amount of row affect by delete
	 */
	int deleteAllImage(long seekHelpId);

	/**
	 * delete image by image id
	 *
	 * @param imageId image id
	 * @return the amount of row affect by delete
	 */
	int deleteImage(long imageId);

	/**
	 * delete search history
	 *
	 * @param historyId history id
	 * @return true if delete successfully, false otherwise
	 */
	int deleteSearchHistory(long historyId);

	/**
	 * delete all help info search history
	 *
	 * @param userId user id
	 * @return the amount of row affect by delete
	 */
	int deleteAllSearchHistory(long userId);
}
