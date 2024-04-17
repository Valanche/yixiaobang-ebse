package com.weixin.njuteam.service;

import com.weixin.njuteam.entity.vo.FriendVO;
import com.weixin.njuteam.entity.vo.UserChatHistoryVO;
import com.weixin.njuteam.entity.vo.UserVO;
import com.weixin.njuteam.util.Pair;

import java.sql.SQLException;
import java.util.List;

/**
 * @author Zyi
 */
public interface UserService {

	/**
	 * 获得openid和session_key
	 *
	 * @param code wx.login()获得的code
	 * @return session_key and open_id
	 */
	String getSessionId(String code);

	/**
	 * 获取access_token
	 *
	 * @return access token
	 */
	String getAccessToken();

	/**
	 * 根据openid获得userid
	 *
	 * @param openId user open id
	 * @return user id
	 */
	long queryUserId(String openId);

	/**
	 * query open id by user id
	 *
	 * @param userId user id
	 * @return open id
	 */
	String queryUserOpenId(Long userId);

	/**
	 * 登录认证
	 *
	 * @param userVo 用户信息
	 * @return 注册后的用户信息
	 * @throws SQLException 插入失败异常
	 */
	UserVO authLogin(UserVO userVo) throws SQLException;

	/**
	 * 通过id获得用户信息
	 *
	 * @param userId user id
	 * @return user value object
	 */
	UserVO queryUserById(Long userId);

	/**
	 * 更新用户信息
	 *
	 * @param userVo 新的用户信息
	 * @return 新的用户信息
	 */
	UserVO updateUser(UserVO userVo);

	/**
	 * 获取用户信息
	 *
	 * @param openId 用户的openid
	 * @return 用户的信息
	 */
	UserVO queryUser(String openId);

	/**
	 * 获取好友信息
	 *
	 * @param id User id
	 * @return 好友信息
	 */
	FriendVO queryFriend(Long id);

	/**
	 * 根据某个用户id查找与该id有关的所有聊天用户
	 * 以及最新的聊天记录和时间
	 *
	 * @param openId user open id
	 * @return 用户聊天列表 及 最新的聊天时间和记录
	 */
	List<Pair<Integer, UserChatHistoryVO>> queryChatHistory(String openId);

	/**
	 * 删除某个用户
	 *
	 * @param openId user open id
	 * @return true if delete successfully, false otherwise
	 */
	boolean deleteUser(String openId);

	/**
	 * delete user by id
	 *
	 * @param id user id
	 * @return true if delete successfully, false otherwise
	 */
	boolean deleteUserById(Long id);
}
