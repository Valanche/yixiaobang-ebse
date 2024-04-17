package com.weixin.njuteam.service;

import com.weixin.njuteam.entity.po.help.SeekHelpImage;
import com.weixin.njuteam.entity.vo.help.SeekHelpInfoAndUserVO;
import com.weixin.njuteam.entity.vo.help.SeekHelpInfoClickVO;
import com.weixin.njuteam.entity.vo.help.SeekHelpInfoVO;
import com.weixin.njuteam.entity.vo.help.SeekHelpSearchHistoryVO;
import com.weixin.njuteam.enums.SeekHelpFinishStatus;
import com.weixin.njuteam.exception.AddTaskException;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * @author Zyi
 */
public interface SeekHelpInfoService {

	/**
	 * query seek help info by info id
	 *
	 * @param id info id
	 * @return seek help info value object
	 */
	SeekHelpInfoAndUserVO querySeekInfoById(@NotNull Long id);

	/**
	 * query seek help info by user id
	 *
	 * @param openId user open id
	 * @return list of seek help info value object which belong to the user
	 */
	List<SeekHelpInfoVO> querySeekInfoByUserId(@NotNull String openId);

	/**
	 * get seek info group by tag
	 *
	 * @return map, key : tag, value : list of seek help info value object
	 */
	Map<String, List<SeekHelpInfoAndUserVO>> getSeekInfoGroupByTag();

	/**
	 * get recommend seek help info list
	 *
	 * @param openId user open id
	 * @return list of seek help info value object
	 */
	List<SeekHelpInfoAndUserVO> getRecommendList(@NotNull String openId);

	/**
	 * query seek help info by keyword
	 *
	 * @param openId  user open id
	 * @param keyword keyword
	 * @return list of seek help info value object
	 */
	List<SeekHelpInfoAndUserVO> searchSeekInfo(@NotNull String openId, String keyword);

	/**
	 * query seek help info search history
	 *
	 * @param openId user open id
	 * @param size   search history size
	 * @return list of seek help info search history value object
	 */
	List<SeekHelpSearchHistoryVO> querySearchHistory(@NotNull String openId, int size);

	/**
	 * insert seek help info
	 *
	 * @param seekInfoVo seek help info value object
	 * @param openId     user open id
	 * @return seek help info value object after insert
	 * @throws AddTaskException add task failed
	 */
	SeekHelpInfoVO insertSeekInfo(SeekHelpInfoVO seekInfoVo, @NotNull String openId) throws AddTaskException;

	/**
	 * user click help info page
	 *
	 * @param openId     user open id
	 * @param seekHelpId seek help info id
	 * @return help info click value object
	 */
	SeekHelpInfoClickVO clickHelpInfo(@NotNull String openId, @NotNull Long seekHelpId);

	/**
	 * update click end
	 *
	 * @param clickId click id
	 * @return true if update successfully, false otherwise
	 */
	boolean endClick(@NotNull Long clickId);

	/**
	 * insert image list
	 *
	 * @param seekHelpId seek help info id
	 * @param imageList  image list
	 * @return list of image list
	 */
	List<SeekHelpImage> insertImageList(@NotNull Long seekHelpId, MultipartFile[] imageList);

	/**
	 * update seek help info
	 *
	 * @param seekInfoVo seek help info value object
	 * @param openId     user open id
	 * @return seek help info value object after update
	 */
	SeekHelpInfoVO updateSeekInfo(SeekHelpInfoVO seekInfoVo, @NotNull String openId);

	/**
	 * update image list
	 *
	 * @param seekHelpId seek help info id
	 * @param imageList  image list
	 * @return list of image list after update
	 */
	List<SeekHelpImage> updateImageList(@NotNull Long seekHelpId, MultipartFile[] imageList);

	/**
	 * update seek help info finish status
	 *
	 * @param seekHelpId   seek help id
	 * @param finishStatus seek help info finish status
	 * @return seek help info value object
	 */
	SeekHelpInfoVO updateFinishStatus(@NotNull Long seekHelpId, SeekHelpFinishStatus finishStatus);

	/**
	 * delete seek help info
	 *
	 * @param id seek info id
	 * @return true if delete successfully, false otherwise
	 */
	boolean deleteSeekInfo(@NotNull Long id);

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
	boolean deleteSearchHistory(@NotNull Long historyId);

	/**
	 * delete all help info search history
	 *
	 * @param openId user open id
	 * @return true if delete successfully, false otherwise
	 */
	boolean deleteAllHistory(@NotNull String openId);
}
