package com.weixin.njuteam.dao.nucleic;

import com.weixin.njuteam.entity.po.nucleic.NucleicAcidTestingPO;
import com.weixin.njuteam.entity.po.nucleic.UpdateTestingPO;
import com.weixin.njuteam.enums.FinishStatus;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * @author Zyi
 */
@Mapper
@Repository
public interface NucleicAcidTestingMapper {

	/**
	 * query testing persistent object by testing id
	 *
	 * @param id testing id
	 * @return testing persistent object
	 */
	NucleicAcidTestingPO queryTestingById(long id);

	/**
	 * query testing info by user id and title
	 *
	 * @param userId   user id
	 * @param oldTitle title
	 * @return testing persistent object
	 */
	NucleicAcidTestingPO queryTestingByUserIdAndTitle(long userId, String oldTitle);

	/**
	 * query testing info about user
	 *
	 * @param userId user id
	 * @return list of testing info about user
	 * @throws SQLException query testing info fail
	 */
	List<NucleicAcidTestingPO> queryTestingByUserId(long userId) throws SQLException;

	/**
	 * 获取用户待检测的通知数量
	 *
	 * @param userId       user id
	 * @param finishStatus finish status
	 * @return amount of testing info which is in progress
	 */
	int queryTestingCount(long userId, FinishStatus finishStatus);

	/**
	 * insert new testing info
	 *
	 * @param testingPo testing persistent object
	 * @return the amount of row affect by insert
	 */
	int insertTesting(NucleicAcidTestingPO testingPo);

	/**
	 * update testing info in db
	 *
	 * @param testingPo testing persistent object
	 * @return the amount of row affect by update
	 */
	int updateTesting(NucleicAcidTestingPO testingPo);

	/**
	 * update testing info by user id and old title
	 *
	 * @param testingPo testing persistent object
	 * @return the amount of row affected by update operation
	 */
	int updateTestingByUserIdAndTitle(UpdateTestingPO testingPo);

	/**
	 * update testing true time
	 *
	 * @param testingId testing id
	 * @param trueTime  true time
	 * @return true if update successfully, false otherwise
	 */
	int updateTrueTime(Long testingId, Date trueTime);

	/**
	 * update testing info finish status
	 *
	 * @param testingId    testing id
	 * @param finishStatus finish status
	 * @return the amount of row affect by update
	 */
	int updateFinish(long testingId, FinishStatus finishStatus);

	/**
	 * open testing info remind
	 *
	 * @param testingId    testing id
	 * @param isOpenRemind is open remind
	 * @return the amount of row affect by update
	 */
	int openRemind(long testingId, boolean isOpenRemind);

	/**
	 * delete testing info in db
	 *
	 * @param id testing id
	 * @return the amount of row affect by delete
	 */
	int deleteTesting(long id);

	/**
	 * delete testing info by user id and title
	 *
	 * @param userId user id
	 * @param title  title
	 * @return the amount of row affect by delete
	 */
	int deleteTestingInfoByUserIdAndTitle(long userId, String title);
}
