package com.weixin.njuteam.service;

import com.weixin.njuteam.entity.vo.manager.nucleic.ManagerAndStudentInfoVO;
import com.weixin.njuteam.entity.vo.manager.nucleic.ManagerNucleicAcidInfoVO;
import com.weixin.njuteam.exception.AddNucleicAcidException;
import com.weixin.njuteam.exception.DuplicateTitleException;
import com.weixin.njuteam.exception.NoStudentException;
import com.weixin.njuteam.exception.UpdateNucleicAcidException;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Zyi
 */
public interface ManagerNucleicAcidInfoService {

	/**
	 * query info list by manager id
	 *
	 * @param managerId manager id
	 * @return list of manager nucleic acid info value object
	 */
	List<ManagerNucleicAcidInfoVO> queryManagerInfoList(@NotNull Long managerId);

	/**
	 * query info by id
	 *
	 * @param id info id
	 * @return manager nucleic acid info value object
	 */
	ManagerNucleicAcidInfoVO queryInfoById(@NotNull Long id);

	/**
	 * query student reporting info by manager id and info id
	 *
	 * @param managerId manager id
	 * @param infoId    info id
	 * @return student reporting info
	 * @throws NoStudentException manager don't have student
	 */
	ManagerAndStudentInfoVO queryStudentReportingInfo(@NotNull Long managerId, @NotNull Long infoId) throws NoStudentException;

	/**
	 * query nucleic acid info by manager id and title
	 *
	 * @param managerId manager id
	 * @param title     title
	 * @return manager nucleic acid info value object
	 */
	ManagerNucleicAcidInfoVO queryInfoByManagerIdAndTitle(@NotNull Long managerId, String title);

	/**
	 * insert info
	 *
	 * @param managerId manager id
	 * @param infoVo    info value object
	 * @return manager nucleic acid info value object after insert
	 * @throws AddNucleicAcidException add info exception
	 * @throws DuplicateTitleException duplicate title
	 */
	ManagerNucleicAcidInfoVO insertInfo(@NotNull Long managerId, ManagerNucleicAcidInfoVO infoVo) throws AddNucleicAcidException, DuplicateTitleException;

	/**
	 * update info
	 *
	 * @param managerId manager id
	 * @param infoVo    new info value object
	 * @param oldTitle  old title
	 * @return manager nucleic acid info value object after update
	 * @throws UpdateNucleicAcidException update info exception
	 * @throws DuplicateTitleException    duplicate title
	 */
	ManagerNucleicAcidInfoVO updateInfo(@NotNull Long managerId, ManagerNucleicAcidInfoVO infoVo, String oldTitle) throws UpdateNucleicAcidException, DuplicateTitleException;

	/**
	 * delete info by info id
	 *
	 * @param managerId manager id
	 * @param infoId    info id
	 * @return true if delete successfully, false otherwise
	 */
	boolean deleteInfoById(@NotNull Long managerId, @NotNull Long infoId);

	/**
	 * delete manger all info
	 *
	 * @param managerId manger id
	 * @return true if delete successfully, false otherwise
	 */
	boolean deleteManagerAllInfo(@NotNull Long managerId);
}
