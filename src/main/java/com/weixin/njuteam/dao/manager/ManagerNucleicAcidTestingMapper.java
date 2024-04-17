package com.weixin.njuteam.dao.manager;

import com.weixin.njuteam.entity.po.manager.nucleic.ManagerNucleicAcidTestingPO;
import com.weixin.njuteam.enums.FinishStatus;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Zyi
 */
@Mapper
@Repository
public interface ManagerNucleicAcidTestingMapper {

	/**
	 * query manager nucleic acid testing persistent object by id
	 *
	 * @param id info id
	 * @return manager nucleic acid testing persistent object
	 */
	ManagerNucleicAcidTestingPO queryTestingById(long id);

	/**
	 * query manager nucleic acid info list
	 *
	 * @param managerId manager id
	 * @return list of manager nucleic acid info persistent object
	 */
	List<ManagerNucleicAcidTestingPO> queryManagerTestingList(long managerId);

	/**
	 * query manager testing persistent object by manager id and title
	 *
	 * @param managerId manager id
	 * @param title     title
	 * @return manager nucleic acid testing info persistent object
	 */
	ManagerNucleicAcidTestingPO queryTestingByManagerIdAndTitle(long managerId, String title);

	/**
	 * insert manager nucleic acid testing info
	 *
	 * @param testingPo testing persistent object
	 * @return the amount of row affected by insert
	 */
	int insertTestingInfo(ManagerNucleicAcidTestingPO testingPo);

	/**
	 * update manager nucleic acid testing info
	 *
	 * @param testingPo testing persistent object
	 * @return the amount of row affected by update
	 */
	int updateTestingInfo(ManagerNucleicAcidTestingPO testingPo);

	/**
	 * update testing info finish status
	 *
	 * @param testingId    testing id
	 * @param finishStatus finish status
	 * @return the amount of row affected by update
	 */
	int updateTestingFinish(long testingId, FinishStatus finishStatus);

	/**
	 * delete booking nucleic acid testing info
	 *
	 * @param id info id
	 * @return the amount of row affected by delete
	 */
	int deleteTestingInfoById(long id);

	/**
	 * delete manager all nucleic acid testing info
	 *
	 * @param managerId manager id
	 * @return the amount of row affected by delete
	 */
	int deleteAllTestingInfo(long managerId);
}
