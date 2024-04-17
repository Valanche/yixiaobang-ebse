package com.weixin.njuteam.dao.help;

import com.weixin.njuteam.entity.po.help.HelpImage;
import com.weixin.njuteam.entity.po.help.HelpInfoClickPO;
import com.weixin.njuteam.entity.po.help.HelpInfoPO;
import com.weixin.njuteam.entity.po.help.HelpSearchHistoryPO;
import com.weixin.njuteam.enums.HelpFinishStatus;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Zyi
 */
@Mapper
@Repository
public interface HelpInfoMapper {

	/**
	 * get all help info persistent object which is in progress.
	 *
	 * @return list of help info persistent object
	 */
	List<HelpInfoPO> queryHelpInfoInProgress();

	/**
	 * query help info by id
	 *
	 * @param id help info id
	 * @return help persistent object
	 */
	HelpInfoPO queryHelpInfoById(long id);

	/**
	 * query user's help info list
	 *
	 * @param userId user id
	 * @return list of help info which belong to the user
	 */
	List<HelpInfoPO> queryHelpInfoByUserId(long userId);

	/**
	 * search help info by keyword
	 *
	 * @param keyword keyword
	 * @return list of help info which is contains the keyword
	 */
	List<HelpInfoPO> queryHelpInfoByKeyword(String keyword);

	/**
	 * query help info search history by user id
	 *
	 * @param userId user id
	 * @param size   search history size
	 * @return list of help info search history persistent object
	 */
	List<HelpSearchHistoryPO> querySearchHistory(long userId, int size);

	/**
	 * query help image
	 *
	 * @param imageId image id
	 * @return help image
	 */
	HelpImage queryHelpImage(long imageId);

	/**
	 * query help info click persistent object
	 *
	 * @param userId userid
	 * @param helpId help info id
	 * @return help info click persistent object
	 */
	HelpInfoClickPO queryClick(long userId, long helpId);

	/**
	 * insert help info
	 *
	 * @param helpInfoPo help info persistent object
	 * @return the amount of row affect by insert
	 */
	int insertHelpInfo(HelpInfoPO helpInfoPo);

	/**
	 * insert user's search history
	 *
	 * @param historyPo help info search history persistent object
	 * @return the amount of row affect by insert
	 */
	int insertSearchHistory(HelpSearchHistoryPO historyPo);

	/**
	 * insert image
	 *
	 * @param helpImage image
	 * @return the amount of row affect by insert
	 */
	int insertImage(HelpImage helpImage);

	/**
	 * insert user click
	 *
	 * @param clickPo click persistent object
	 * @return the amount of row affect by insert
	 */
	int insertClick(HelpInfoClickPO clickPo);

	/**
	 * update click end
	 *
	 * @param clickId click id
	 * @return the amount of row affect by update
	 */
	int updateClickEndTime(long clickId);

	/**
	 * update new click history
	 *
	 * @param clickId click id
	 * @return the amount of row affect by update
	 */
	int updateClick(long clickId);

	/**
	 * update help info
	 *
	 * @param helpInfoPo help info persistent object
	 * @return the amount of row affect by update
	 */
	int updateHelpInfo(HelpInfoPO helpInfoPo);

	/**
	 * update image
	 *
	 * @param helpImage image
	 * @return the amount of row affect by update
	 */
	int updateImage(HelpImage helpImage);

	/**
	 * update help info finish status
	 *
	 * @param helpId       help id
	 * @param finishStatus help finish status
	 * @return the amount of row affect by update
	 */
	int updateFinishStatus(long helpId, HelpFinishStatus finishStatus);

	/**
	 * delete help info
	 *
	 * @param id info id
	 * @return the amount of row affect by delete
	 */
	int deleteHelpInfo(long id);

	/**
	 * delete all image by help info id
	 *
	 * @param helpId help info id
	 * @return the amount of row affect by delete
	 */
	int deleteAllImage(long helpId);

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
	 * @return the amount of row affect by delete
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
