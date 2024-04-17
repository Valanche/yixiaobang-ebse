package com.weixin.njuteam.dao.manager;

import com.weixin.njuteam.entity.po.manager.nucleic.ManagerAndStudentInfoPO;
import com.weixin.njuteam.entity.po.manager.nucleic.ManagerNucleicAcidInfoPO;
import com.weixin.njuteam.enums.FinishStatus;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Zyi
 */
@Mapper
@Repository
public interface ManagerNucleicAcidInfoMapper {

	/**
	 * query manager nucleic acid info persistent object by id
	 *
	 * @param id info id
	 * @return manager nucleic acid info persistent object
	 */
	ManagerNucleicAcidInfoPO queryInfoById(long id);

	/**
	 * query manager nucleic acid info list
	 *
	 * @param managerId manager id
	 * @return list of manager nucleic acid info persistent object
	 */
	List<ManagerNucleicAcidInfoPO> queryManagerInfoList(long managerId);

	/**
	 * query student nucleic acid info result list
	 *
	 * @param infoId    info id
	 * @param managerId manager id
	 * @return list of student reporting info
	 */
	ManagerAndStudentInfoPO queryStudentReportingInfo(long infoId, long managerId);

	/**
	 * query info by manager id and title
	 *
	 * @param managerId manager id
	 * @param title     title
	 * @return manager info persistent object
	 */
	ManagerNucleicAcidInfoPO queryInfoByManagerIdAndTitle(long managerId, String title);

	/**
	 * insert manager nucleic acid info
	 *
	 * @param infoPo info persistent object
	 * @return the amount of row affected by insert
	 */
	int insertInfo(ManagerNucleicAcidInfoPO infoPo);

	/**
	 * update manager nucleic acid info
	 *
	 * @param infoPo info persistent object
	 * @return the amount of row affected by update
	 */
	int updateInfo(ManagerNucleicAcidInfoPO infoPo);

	/**
	 * update testing info finish status
	 *
	 * @param infoId       info id
	 * @param finishStatus finish status
	 * @return the amount of row affected by update
	 */
	int updateInfoFinish(long infoId, FinishStatus finishStatus);

	/**
	 * delete booking nucleic acid info
	 *
	 * @param id info id
	 * @return the amount of row affected by delete
	 */
	int deleteInfoById(long id);

	/**
	 * delete manager all nucleic acid info
	 *
	 * @param managerId manager id
	 * @return the amount of row affected by delete
	 */
	int deleteAllInfo(long managerId);
}
