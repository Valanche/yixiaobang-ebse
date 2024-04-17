package com.weixin.njuteam.dao.nucleic;

import com.weixin.njuteam.entity.po.nucleic.NucleicAcidBookingPO;
import com.weixin.njuteam.entity.po.nucleic.UpdateBookingPO;
import com.weixin.njuteam.enums.FinishStatus;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Zyi
 */
@Mapper
@Repository
public interface NucleicAcidBookingMapper {

	/**
	 * 通过预约通知id查询预约通知
	 *
	 * @param id booking id
	 * @return booking persistent object
	 */
	NucleicAcidBookingPO queryBookingById(long id);

	/**
	 * 通过用户id查询有关的预约通知
	 *
	 * @param userId user id
	 * @return list of booking persistent object
	 */
	List<NucleicAcidBookingPO> queryBookingByUserId(long userId);

	/**
	 * query booking info by user id and title
	 *
	 * @param userId   user id
	 * @param oldTitle title
	 * @return booking persistent object
	 */
	NucleicAcidBookingPO queryBookingByUserIdAndTitle(long userId, String oldTitle);

	/**
	 * 插入新的预约通知
	 *
	 * @param bookingPo booking persistent object
	 * @return the amount of row affected by insert operation
	 */
	int insertBooking(NucleicAcidBookingPO bookingPo);

	/**
	 * 获取用户待预约的数量
	 *
	 * @param userId       user id
	 * @param finishStatus finish status
	 * @return the amount of booking info which is in progress
	 */
	int queryBookingCount(long userId, FinishStatus finishStatus);

	/**
	 * 更新预约通知信息
	 *
	 * @param bookingPo booking persistent object
	 * @return the amount of row affected by update operation
	 */
	int updateBooking(NucleicAcidBookingPO bookingPo);

	/**
	 * update booking info by user id and old title
	 *
	 * @param bookingPo booking persistent object
	 * @return the amount of row affected by update operation
	 */
	int updateBookingByUserIdAndTitle(UpdateBookingPO bookingPo);

	/**
	 * update booking info finish status
	 *
	 * @param bookingId    booking id
	 * @param finishStatus finish status
	 * @return the amount of row affect by update
	 */
	int updateFinish(long bookingId, FinishStatus finishStatus);

	/**
	 * open booking info remind
	 *
	 * @param bookingId    booking id
	 * @param isOpenRemind is open remind
	 * @return the amount of row affect by update
	 */
	int openRemind(long bookingId, boolean isOpenRemind);

	/**
	 * 删除预约通知信息
	 *
	 * @param id booking id
	 * @return the amount of row affected by delete operation
	 */
	int deleteBooking(long id);

	/**
	 * delete booking info by user id and title
	 *
	 * @param userId user id
	 * @param title  title
	 * @return the amount of row affect by delete
	 */
	int deleteBookingInfoByUserIdAndTitle(long userId, String title);
}
