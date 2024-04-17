package com.weixin.njuteam.service;

import com.weixin.njuteam.entity.vo.nucleic.NucleicAcidTestingVO;
import com.weixin.njuteam.enums.FinishStatus;
import com.weixin.njuteam.exception.AddTaskException;

import javax.validation.constraints.NotNull;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * @author Zyi
 */
public interface NucleicAcidTestingService {

	/**
	 * query testing by id
	 *
	 * @param id testing id
	 * @return testing value object
	 */
	NucleicAcidTestingVO queryTestingById(@NotNull Long id);

	/**
	 * query testing by user id
	 *
	 * @param openId user open id
	 * @return list of testing value object
	 * @throws SQLException query testing info fail
	 */
	List<NucleicAcidTestingVO> queryTestingByUserId(@NotNull String openId) throws SQLException;

	/**
	 * 获取用户待检测的数量
	 *
	 * @param openId       user open id
	 * @param finishStatus finish status
	 * @return the count of testing info which is in progress
	 */
	int queryTestingCount(@NotNull String openId, FinishStatus finishStatus);

	/**
	 * insert testing info
	 *
	 * @param testingVo testing value object
	 * @return new testing value object
	 * @throws AddTaskException add task failed
	 */
	NucleicAcidTestingVO insertTesting(NucleicAcidTestingVO testingVo) throws AddTaskException;

	/**
	 * update testing info
	 *
	 * @param testingVo testing value object
	 * @return new testing value object
	 */
	NucleicAcidTestingVO updateTesting(NucleicAcidTestingVO testingVo);

	/**
	 * update testing info
	 *
	 * @param testingVo booking value object
	 * @param oldTitle  old title
	 * @return true if update successfully, false otherwise
	 */
	NucleicAcidTestingVO updateTestingByUserIdAndTitle(NucleicAcidTestingVO testingVo, String oldTitle);

	/**
	 * update testing info to Finished
	 *
	 * @param openId    user open id
	 * @param testingId testing id
	 * @return testing info after updating
	 */
	NucleicAcidTestingVO updateFinish(@NotNull String openId, @NotNull Long testingId);

	/**
	 * update testing info not finish
	 *
	 * @param title testing info title
	 * @param userId user id
	 * @return true if update successfully, false otherwise
	 */
	boolean updateNotFinish(@NotNull String title, @NotNull Long userId);

	/**
	 * open testing info remind
	 *
	 * @param openId       user open id
	 * @param testingId    testing id
	 * @param isOpenRemind is open remind
	 * @return testing info value object after open remind
	 */
	NucleicAcidTestingVO openRemind(@NotNull String openId, @NotNull Long testingId, Boolean isOpenRemind);

	/**
	 * update testing true time
	 *
	 * @param testingId testing id
	 * @param trueTime  true time
	 * @return true if update successfully, false otherwise
	 */
	boolean updateTrueTime(@NotNull Long testingId, Date trueTime);

	/**
	 * delete testing info
	 *
	 * @param id testing id
	 * @return true if delete successfully, false otherwise
	 */
	boolean deleteTesting(@NotNull Long id);

	/**
	 * delete testing info by user id and title
	 *
	 * @param userId user id
	 * @param title  title
	 * @return true if delete successfully, false otherwise
	 */
	boolean deleteTestingInfoByUserIdAndTitle(@NotNull Long userId, String title);
}
