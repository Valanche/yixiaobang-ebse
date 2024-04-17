package com.weixin.njuteam.service;

import com.weixin.njuteam.entity.vo.manager.ManagerAndStudentVO;
import com.weixin.njuteam.entity.vo.manager.ManagerVO;
import com.weixin.njuteam.exception.ManagerExistException;
import com.weixin.njuteam.exception.NickNameDuplicateException;
import com.weixin.njuteam.exception.PasswordException;

import javax.validation.constraints.NotNull;
import java.sql.SQLException;

/**
 * @author Zyi
 */
public interface ManagerService {

	/**
	 * query manager value object by manager id
	 *
	 * @param id manager id
	 * @return manager value object
	 */
	ManagerVO queryManagerById(@NotNull Long id);

	/**
	 * query manager value object by manager info
	 *
	 * @param school    manager school
	 * @param institute manager institute
	 * @param major     manager major
	 * @param grade     manager grade
	 * @return manager value object
	 */
	ManagerVO queryManagerByInfo(String school, String institute, String major, String grade);

	/**
	 * query manager and student list
	 *
	 * @param id manager id
	 * @return manager and student value object
	 */
	ManagerAndStudentVO queryStudentList(@NotNull Long id);

	/**
	 * manager register
	 *
	 * @param managerVo manager value object
	 * @return manager manager value object after register
	 * @throws PasswordException          password null error
	 * @throws NickNameDuplicateException nickname duplicate
	 * @throws ManagerExistException      manager exist
	 */
	ManagerVO register(ManagerVO managerVo) throws PasswordException, NickNameDuplicateException, ManagerExistException;

	/**
	 * manager login
	 *
	 * @param nickName manager nickname
	 * @param password manager password
	 * @return manger value object after login
	 * @throws PasswordException password error
	 */
	ManagerVO authLogin(String nickName, String password) throws PasswordException;

	/**
	 * manager Wechat Mini Program login
	 *
	 * @param managerVo manager value object
	 * @return manager info after login
	 * @throws SQLException               insert or query db exception
	 * @throws NickNameDuplicateException nickname duplicate
	 */
	ManagerVO authLogin(ManagerVO managerVo) throws SQLException, NickNameDuplicateException;

	/**
	 * update manager info
	 *
	 * @param id        manager id
	 * @param managerVo manager value object
	 * @return manager value object after update
	 */
	ManagerVO updateManagerInfo(@NotNull Long id, ManagerVO managerVo);

	/**
	 * update manager password
	 *
	 * @param id       manager id
	 * @param password manager password
	 * @return manager value object after update
	 */
	ManagerVO updateManagerPassword(@NotNull Long id, String password);

	/**
	 * delete manager
	 *
	 * @param id manager id
	 * @return true if delete successfully, false otherwise
	 */
	boolean deleteManager(@NotNull Long id);
}
