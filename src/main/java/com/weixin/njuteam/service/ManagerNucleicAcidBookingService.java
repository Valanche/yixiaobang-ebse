package com.weixin.njuteam.service;

import com.weixin.njuteam.entity.vo.manager.nucleic.ManagerNucleicAcidBookingVO;
import com.weixin.njuteam.exception.DuplicateTitleException;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Zyi
 */
public interface ManagerNucleicAcidBookingService {

	/**
	 * query booking info list by manager id
	 *
	 * @param managerId manager id
	 * @return list of manager nucleic acid booking info value object
	 */
	List<ManagerNucleicAcidBookingVO> queryManagerBookingInfoList(@NotNull Long managerId);

	/**
	 * query manager booking info by manager id and title
	 *
	 * @param managerId manager id
	 * @param title     title
	 * @return manager booking info value object
	 */
	ManagerNucleicAcidBookingVO queryBookingInfoByManagerIdAndTitle(@NotNull Long managerId, String title);

	/**
	 * query booking info by id
	 *
	 * @param id booking id
	 * @return manager nucleic acid booking info value object
	 */
	ManagerNucleicAcidBookingVO queryBookingInfoById(@NotNull Long id);

	/**
	 * insert booking info
	 *
	 * @param managerId manager id
	 * @param bookingVo booking info value object
	 * @return manager nucleic acid booking info value object after insert
	 * @throws DuplicateTitleException title duplicate
	 */
	ManagerNucleicAcidBookingVO insertBookingInfo(@NotNull Long managerId, ManagerNucleicAcidBookingVO bookingVo) throws DuplicateTitleException;

	/**
	 * update booking info
	 *
	 * @param managerId manager id
	 * @param bookingVo new booking info value object
	 * @param oldTitle  old title
	 * @return manager nucleic acid booking info value object after update
	 * @throws DuplicateTitleException title duplicate
	 */
	ManagerNucleicAcidBookingVO updateBookingInfo(@NotNull Long managerId, ManagerNucleicAcidBookingVO bookingVo, String oldTitle) throws DuplicateTitleException;

	/**
	 * delete booking info by info id
	 *
	 * @param managerId manager id
	 * @param bookingId booking info id
	 * @return true if delete successfully, false otherwise
	 */
	boolean deleteBookingInfoById(@NotNull Long managerId, @NotNull Long bookingId);

	/**
	 * delete manger all booking info
	 *
	 * @param managerId manger id
	 * @return true if delete successfully, false otherwise
	 */
	boolean deleteManagerAllBookingInfo(@NotNull Long managerId);
}
