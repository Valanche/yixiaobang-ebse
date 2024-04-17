package com.weixin.njuteam.dao;

import com.weixin.njuteam.entity.po.UserPO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author Zyi
 */
@Mapper
@Repository
public interface UserMapper {

	/**
	 * 通过openid来查询对应的用户
	 *
	 * @param openId open id
	 * @return 用户信息，如果不存在则返回null
	 */
	UserPO queryUserByOpenId(String openId);

	/**
	 * 获取用户的聊天列表
	 *
	 * @param id user id
	 * @return 用户的聊天列表
	 */
	UserPO queryUserById(long id);

	/**
	 * 获取user id
	 *
	 * @param openId user open id
	 * @return user id
	 */
	int queryUserId(String openId);

	/**
	 * query user open id
	 *
	 * @param userId user id
	 * @return user open id
	 */
	String queryUserOpenId(long userId);

	/**
	 * 注册
	 *
	 * @param userPo 用户信息
	 * @return the amount of row affected by insert
	 */
	int insertUser(UserPO userPo);

	/**
	 * 更新用户信息
	 *
	 * @param userPo 新的用户信息
	 * @return the amount of row affected by update
	 */
	int updateUser(UserPO userPo);

	/**
	 * 删除用户信息
	 *
	 * @param openId user open id
	 * @return the amount of row affected by delete
	 */
	int deleteUser(String openId);

	/**
	 * 删除用户信息
	 *
	 * @param id user id
	 * @return the amount of row affected by delete
	 */
	int deleteUserById(long id);
}
