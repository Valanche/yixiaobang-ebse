package com.weixin.njuteam.service;

import com.weixin.njuteam.entity.vo.manager.nucleic.ManagerNucleicAcidTestingVO;
import com.weixin.njuteam.exception.AddNucleicAcidException;
import com.weixin.njuteam.exception.DuplicateTitleException;
import com.weixin.njuteam.exception.UpdateNucleicAcidException;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Zyi
 */
public interface ManagerNucleicAcidTestingService {

	/**
	 * query testing info list by manager id
	 *
	 * @param managerId manager id
	 * @return list of manager nucleic acid testing info value object
	 */
	List<ManagerNucleicAcidTestingVO> queryManagerTestingInfoList(@NotNull Long managerId);

	/**
	 * query testing info by id
	 *
	 * @param id testing id
	 * @return manager nucleic acid testing info value object
	 */
	ManagerNucleicAcidTestingVO queryTestingInfoById(@NotNull Long id);

	/**
	 * insert testing info
	 *
	 * @param managerId manager id
	 * @param testingVo testing info value object
	 * @return manager nucleic acid testing info value object after insert
	 * @throws AddNucleicAcidException user insert testing info fail
	 * @throws DuplicateTitleException title duplicate
	 */
	ManagerNucleicAcidTestingVO insertTestingInfo(@NotNull Long managerId, ManagerNucleicAcidTestingVO testingVo) throws AddNucleicAcidException, DuplicateTitleException;

	/**
	 * update testing info
	 *
	 * @param managerId manager id
	 * @param testingVo new testing info value object
	 * @param oldTitle  old title
	 * @return manager nucleic acid testing info value object after update
	 * @throws UpdateNucleicAcidException student update nucleic acid fail
	 * @throws DuplicateTitleException    duplicate title
	 */
	ManagerNucleicAcidTestingVO updateTestingInfo(@NotNull Long managerId, ManagerNucleicAcidTestingVO testingVo, String oldTitle) throws UpdateNucleicAcidException, DuplicateTitleException;

	/**
	 * delete testing info by info id
	 *
	 * @param managerId manager id
	 * @param testingId testing info id
	 * @return true if delete successfully, false otherwise
	 */
	boolean deleteTestingInfoById(@NotNull Long managerId, @NotNull Long testingId);

	/**
	 * delete manger all testing info
	 *
	 * @param managerId manger id
	 * @return true if delete successfully, false otherwise
	 */
	boolean deleteManagerAllTestingInfo(@NotNull Long managerId);
}
