package com.weixin.njuteam.dao.manager;

import com.weixin.njuteam.entity.po.manager.ManagerAndStudentPO;
import com.weixin.njuteam.entity.po.manager.ManagerPO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Zyi
 */
@Mapper
@Repository
public interface ManagerMapper {

	/**
	 * query manager by manager id
	 *
	 * @param id id
	 * @return manager persistent object
	 */
	ManagerPO queryManagerById(long id);

	/**
	 * query manager by open id
	 *
	 * @param openId manager open id
	 * @return manager persistent object
	 */
	ManagerPO queryManagerByOpenId(String openId);

	/**
	 * query manager value object by manager info
	 *
	 * @param school    manager school
	 * @param institute manager institute
	 * @param major     manager major
	 * @param grade     manager grade
	 * @return manager value object
	 */
	ManagerPO queryManagerByInfo(String school, String institute, String major, String grade);

	/**
	 * query all manager
	 *
	 * @return all manager
	 */
	List<ManagerPO> queryAllManager();

	/**
	 * query manager by nickname
	 *
	 * @param nickName manager nickname
	 * @return manager persistent object
	 */
	ManagerPO queryManagerByNickName(String nickName);

	/**
	 * query manager by info key
	 *
	 * @param school    school
	 * @param institute institute
	 * @param major     major
	 * @param grade     grade
	 * @return manager nickname
	 */
	String queryManagerByKey(String school, String institute, String major, String grade);

	/**
	 * query manager and student by manger
	 *
	 * @param managerId managerId
	 * @return manager and student list persistent object
	 */
	ManagerAndStudentPO queryManagerAndStudent(long managerId);

	/**
	 * insert new manager
	 *
	 * @param managerPo manager persistent object
	 * @return the amount of row affected by insert
	 */
	int insertManager(ManagerPO managerPo);

	/**
	 * update manager info
	 *
	 * @param managerPo manager persistent object
	 * @return the amount of row affected by update
	 */
	int updateManager(ManagerPO managerPo);

	/**
	 * update manager password
	 *
	 * @param id       manager id
	 * @param password manager password
	 * @return the amount of row affected by update
	 */
	int updateManagerPassword(long id, String password);

	/**
	 * delete manager by manager id
	 *
	 * @param id manager id
	 * @return the amount of row affected by delete
	 */
	int deleteManager(long id);

	/**
	 * delete manager by manager open id
	 *
	 * @param openId manager open id
	 * @return the amount of row affected by delete
	 */
	int deleteManagerByOpenId(String openId);
}
