package com.weixin.njuteam.dao.manager;

import com.weixin.njuteam.entity.po.manager.nucleic.ManagerNucleicAcidBookingPO;
import com.weixin.njuteam.enums.FinishStatus;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Zyi
 */
@Mapper
@Repository
public interface ManagerNucleicAcidBookingMapper {

	/**
	 * query manager nucleic acid booking persistent object by id
	 *
	 * @param id booking id
	 * @return manager nucleic acid booking persistent object
	 */
	ManagerNucleicAcidBookingPO queryBookingById(long id);

	/**
	 * query manager booking info by manager id and title
	 *
	 * @param managerId manager id
	 * @param title     title
	 * @return manager booking info value object
	 */
	ManagerNucleicAcidBookingPO queryBookingInfoByManagerIdAndTitle(long managerId, String title);

	/**
	 * query manager booking info list
	 *
	 * @param managerId manager id
	 * @return list of manager nucleic acid booking persistent object
	 */
	List<ManagerNucleicAcidBookingPO> queryManagerBookingList(long managerId);

	/**
	 * insert manager booking info
	 *
	 * @param bookingPo booking persistent object
	 * @return the amount of row affected by insert
	 */
	int insertBooking(ManagerNucleicAcidBookingPO bookingPo);

	/**
	 * update manager booking info
	 *
	 * @param bookingPo booking persistent object
	 * @return the amount of row affected by update
	 */
	int updateBooking(ManagerNucleicAcidBookingPO bookingPo);

	/**
	 * update booking info finish status
	 *
	 * @param bookingId    booking id
	 * @param finishStatus finish status
	 * @return the amount of row affected by update
	 */
	int updateBookingFinish(long bookingId, FinishStatus finishStatus);

	/**
	 * delete booking info
	 *
	 * @param id booking id
	 * @return the amount of row affected by delete
	 */
	int deleteBookingById(long id);

	/**
	 * delete manager all booking info
	 *
	 * @param managerId manager id
	 * @return the amount of row affected by delete
	 */
	int deleteAllBooking(long managerId);

}
