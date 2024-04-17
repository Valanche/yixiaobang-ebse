package com.weixin.njuteam.dao;

import com.weixin.njuteam.entity.po.MessagePO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Zyi
 */
@Mapper
@Repository
public interface MessageMapper {

	/**
	 * 获取两个用户之间的聊天记录
	 *
	 * @param id        user id
	 * @param friendId  另一方的id
	 * @param startLine 聊天记录开始的行数
	 * @param pageSize  需要获取的聊天记录行数
	 * @return 聊天记录
	 */
	List<MessagePO> queryChatHistory(long id, long friendId, int startLine, int pageSize);

	/**
	 * 获取与该用户有关的所有信息
	 * 即该用户可以是 sender or receiver
	 *
	 * @param userId user id
	 * @return message list
	 */
	List<MessagePO> queryUserMessage(long userId);

	/**
	 * 插入消息
	 *
	 * @param messagePo 新的消息po
	 * @return 插入操作影响的行数
	 */
	int insertMessage(MessagePO messagePo);

	/**
	 * update message read status
	 *
	 * @param senderId   sender id
	 * @param receiverId receiver id
	 * @return the amount of row affect by update
	 */
	int updateRead(long senderId, long receiverId);

	/**
	 * 删除某条信息
	 *
	 * @param id message id
	 * @return 删除操作影响的行数
	 */
	int deleteMessage(long id);
}
