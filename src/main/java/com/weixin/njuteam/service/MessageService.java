package com.weixin.njuteam.service;

import com.weixin.njuteam.entity.vo.MessageVO;
import com.weixin.njuteam.exception.UpdateException;

import java.util.List;

/**
 * 消息转发逻辑层接口
 *
 * @author Zyi
 */
public interface MessageService {

	/**
	 * 获得聊天记录
	 *
	 * @param id       user id
	 * @param friendId friend id
	 * @param pageNo   页数
	 * @param pageSize 页大小
	 * @return list of message
	 */
	List<MessageVO> queryChatHistory(long id, long friendId, int pageNo, int pageSize);

	/**
	 * 获得某个用户的所有聊天记录
	 *
	 * @param userId user id
	 * @return list of message value object
	 */
	List<MessageVO> queryUserMessage(long userId);

	/**
	 * 插入新的消息记录
	 *
	 * @param messageVo message
	 * @return true if insert successfully, false otherwise
	 */
	boolean insertMessage(MessageVO messageVo);

	/**
	 * 更新消息已读状态
	 *
	 * @param senderId   sender id
	 * @param receiverId receiver id
	 * @return true if update successfully, false otherwise
	 * @throws UpdateException update failed
	 */
	boolean updateRead(Long senderId, Long receiverId) throws UpdateException;

	/**
	 * 删除某条消息
	 *
	 * @param id message id
	 * @return true if delete successfully, false otherwise
	 */
	boolean deleteMessage(long id);
}
