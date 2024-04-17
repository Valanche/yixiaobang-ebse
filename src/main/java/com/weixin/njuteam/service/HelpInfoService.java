package com.weixin.njuteam.service;

import com.weixin.njuteam.entity.po.help.HelpImage;
import com.weixin.njuteam.entity.po.help.HelpInfoPO;
import com.weixin.njuteam.entity.vo.help.HelpInfoAndUserVO;
import com.weixin.njuteam.entity.vo.help.HelpInfoClickVO;
import com.weixin.njuteam.entity.vo.help.HelpInfoVO;
import com.weixin.njuteam.entity.vo.help.HelpSearchHistoryVO;
import com.weixin.njuteam.enums.HelpFinishStatus;
import com.weixin.njuteam.exception.AddTaskException;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * @author Zyi
 */
public interface HelpInfoService {

	/**
	 * query all help info
	 *
	 * @return list of help info persistent object
	 */
	List<HelpInfoPO> queryAllHelpInfo();

	/**
	 * query help info by info id
	 *
	 * @param id info id
	 * @return help info value object
	 */
	HelpInfoAndUserVO queryHelpInfoById(@NotNull Long id);

	/**
	 * query help info by user id
	 *
	 * @param openId user open id
	 * @return list of help info value object which belong to the user
	 */
	List<HelpInfoVO> queryHelpInfoByUserId(@NotNull String openId);

	/**
	 * get help info group by tag
	 *
	 * @return map, key : tag name, value : list of help info value object
	 */
	Map<String, List<HelpInfoAndUserVO>> getHelpInfoGroupByTag();

	/**
	 * query help info by keyword
	 *
	 * @param openId  user open id
	 * @param keyword keyword
	 * @return list of help info value object
	 */
	List<HelpInfoAndUserVO> searchHelpInfo(@NotNull String openId, String keyword);

	/**
	 * query recommend help info by history
	 *
	 * @param openId user open id
	 * @return help info value object
	 */
	List<HelpInfoAndUserVO> getRecommendList(@NotNull String openId);

	/**
	 * query help info search history
	 *
	 * @param openId user open id
	 * @param size   search history size
	 * @return list of help info search history value object
	 */
	List<HelpSearchHistoryVO> querySearchHistory(@NotNull String openId, int size);

	/**
	 * insert help info
	 *
	 * @param helpInfoVo help info value object
	 * @param openId     user open id
	 * @return help info value object after insert
	 * @throws AddTaskException add task failed
	 */
	HelpInfoVO insertHelpInfo(HelpInfoVO helpInfoVo, @NotNull String openId) throws AddTaskException;

	/**
	 * insert image list
	 *
	 * @param helpId    help info id
	 * @param imageList image list
	 * @return list of image list
	 */
	List<HelpImage> insertImageList(@NotNull Long helpId, MultipartFile[] imageList);

	/**
	 * user click help info page
	 *
	 * @param openId user open id
	 * @param helpId help info id
	 * @return help info click value object
	 */
	HelpInfoClickVO clickHelpInfo(@NotNull String openId, @NotNull Long helpId);

	/**
	 * update click end
	 *
	 * @param clickId click id
	 * @return true if update successfully, false otherwise
	 */
	boolean endClick(@NotNull Long clickId);

	/**
	 * update help info
	 *
	 * @param helpInfoVo help info value object
	 * @param openId     user open id
	 * @return help info value object after update
	 */
	HelpInfoVO updateHelpInfo(HelpInfoVO helpInfoVo, @NotNull String openId);

	/**
	 * update image list
	 *
	 * @param helpId    help info id
	 * @param imageList image list
	 * @return list of image list after update
	 */
	List<HelpImage> updateImageList(@NotNull Long helpId, MultipartFile[] imageList);

	/**
	 * update help info finish status
	 *
	 * @param helpId       help id
	 * @param finishStatus help info finish status
	 * @return help info value object
	 */
	HelpInfoVO updateFinishStatus(@NotNull Long helpId, HelpFinishStatus finishStatus);

	/**
	 * delete help info
	 *
	 * @param id info id
	 * @return true if delete successfully, false otherwise
	 */
	boolean deleteHelpInfo(@NotNull Long id);

	/**
	 * delete image
	 *
	 * @param imageId image id
	 * @return true if delete successfully, false otherwise
	 */
	boolean deleteImage(@NotNull Long imageId);

	/**
	 * delete search history
	 *
	 * @param historyId history id
	 * @return true if delete successfully, false otherwise
	 */
	boolean deleteSearchHelpInfoHistory(@NotNull Long historyId);

	/**
	 * delete all help info search history
	 *
	 * @param openId user open id
	 * @return true if delete successfully, false otherwise
	 */
	boolean deleteAllHistory(@NotNull String openId);
}
