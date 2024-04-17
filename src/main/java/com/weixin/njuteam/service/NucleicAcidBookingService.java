package com.weixin.njuteam.service;

import com.weixin.njuteam.entity.vo.nucleic.NucleicAcidBookingVO;
import com.weixin.njuteam.enums.FinishStatus;
import com.weixin.njuteam.exception.AddTaskException;

import javax.validation.constraints.NotNull;
import java.sql.SQLException;
import java.util.List;

/**
 * @author Zyi
 */
public interface NucleicAcidBookingService {

	/**
	 * query booking info by booking id
	 *
	 * @param id booking id
	 * @return nucleic acid booking value object
	 */
	NucleicAcidBookingVO queryBookingById(@NotNull Long id);

	/**
	 * query booking info by user id
	 * query booking info which belong to the user
	 *
	 * @param openId user open id
	 * @return list of booking value object
	 * @throws SQLException query booking info fail
	 */
	List<NucleicAcidBookingVO> queryBookingByUserId(@NotNull String openId) throws SQLException;

	/**
	 * 获取用户待预约的数量
	 *
	 * @param openId       user open id
	 * @param finishStatus finish status
	 * @return the amount of booking info which is in progress
	 */
	int queryBookingCount(@NotNull String openId, FinishStatus finishStatus);

	/**
	 * insert booking info to db
	 *
	 * @param bookingVo booking value object
	 * @return true if insert successfully, false otherwise
	 * @throws AddTaskException add task failed
	 */
	NucleicAcidBookingVO insertBooking(NucleicAcidBookingVO bookingVo) throws AddTaskException;

	/**
	 * update booking info to db
	 *
	 * @param bookingVo booking value object
	 * @return true if update successfully, false otherwise
	 */
	NucleicAcidBookingVO updateBooking(NucleicAcidBookingVO bookingVo);

	/**
	 * update booking info
	 *
	 * @param bookingVo booking value object
	 * @param oldTitle  old title
	 * @return true if update successfully, false otherwise
	 */
	NucleicAcidBookingVO updateBookingByUserIdAndTitle(NucleicAcidBookingVO bookingVo, String oldTitle);

	/**
	 * update booking info to Finished
	 *
	 * @param openId    user open id
	 * @param bookingId booking id
	 * @return booking info after updating
	 */
	NucleicAcidBookingVO updateFinish(@NotNull String openId, @NotNull Long bookingId);

	/**
	 * open booking info remind
	 *
	 * @param openId       user open id
	 * @param bookingId    booking info id
	 * @param isOpenRemind is open remind
	 * @return booking info value object after open remind
	 */
	NucleicAcidBookingVO openRemind(@NotNull String openId, @NotNull Long bookingId, Boolean isOpenRemind);

	/**
	 * delete booking by booking id
	 *
	 * @param id booking id
	 * @return true if delete successfully, false otherwise
	 */
	boolean deleteBooking(@NotNull Long id);

	/**
	 * delete booking info by user id and title
	 *
	 * @param userId user id
	 * @param title  title
	 * @return true if delete successfully, false otherwise
	 */
	boolean deleteBookingInfoByUserIdAndTitle(@NotNull Long userId, String title);
}
