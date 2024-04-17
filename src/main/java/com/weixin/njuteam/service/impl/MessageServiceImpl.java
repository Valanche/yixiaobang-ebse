package com.weixin.njuteam.service.impl;

import com.weixin.njuteam.config.cache.RedisOperator;
import com.weixin.njuteam.dao.MessageMapper;
import com.weixin.njuteam.entity.po.MessagePO;
import com.weixin.njuteam.entity.vo.MessageVO;
import com.weixin.njuteam.exception.UpdateException;
import com.weixin.njuteam.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 消息转发逻辑层实现
 *
 * @author Zyi
 */
@Service
@CacheConfig(cacheNames = "message")
@Slf4j
public class MessageServiceImpl implements MessageService {

	private final MessageMapper messageMapper;
	private final RedisOperator redisOperator;

	@Autowired
	public MessageServiceImpl(MessageMapper messageMapper, RedisOperator redisOperator) {
		this.messageMapper = messageMapper;
		this.redisOperator = redisOperator;
	}

	@Override
	@Cacheable(cacheNames = "message", key = "#id + '-' + #friendId + '-' + #pageNo")
	public List<MessageVO> queryChatHistory(long id, long friendId, int pageNo, int pageSize) {
		// 这里pageNo是从0开始的！
		int startLine = pageNo * pageSize;
		List<MessagePO> messagePoList = messageMapper.queryChatHistory(id, friendId, startLine, pageSize);

		return convertList(messagePoList);
	}

	@Override
	public List<MessageVO> queryUserMessage(long userId) {
		List<MessagePO> messagePoList = messageMapper.queryUserMessage(userId);

		return convertList(messagePoList);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean insertMessage(MessageVO messageVo) {
		if (messageVo == null) {
			return false;
		}

		// 清除缓存
		redisOperator.delete("message::" + messageVo.getSenderId() + "-" + messageVo.getReceiverId() + "-0");
		redisOperator.delete("message::" + messageVo.getReceiverId() + "-" + messageVo.getSenderId() + "-0");

		// 异常时回滚
		if (messageVo.getSendTime() == null) {
			// 设置最新的时间
			messageVo.setSendTime(new Date(System.currentTimeMillis()));
		}

		MessagePO messagePo = new MessagePO(messageVo);
		int i = messageMapper.insertMessage(messagePo);
		messageVo.setId(messagePo.getId());

		// i是插入操作影响的行数
		return i > 0;
	}

	@Override
	public boolean updateRead(Long senderId, Long receiverId) throws UpdateException {
		int i = messageMapper.updateRead(senderId, receiverId);

		if (i < 0) {
			throw new UpdateException();
		}

		return true;
	}

	@Override
	public boolean deleteMessage(long id) {
		int i = messageMapper.deleteMessage(id);

		return i > 0;
	}

	private List<MessageVO> convertList(List<MessagePO> list) {
		return list.stream().filter(Objects::nonNull).map(MessageVO::new).collect(Collectors.toList());
	}
}
