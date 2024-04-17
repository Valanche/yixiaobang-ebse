package com.weixin.njuteam.dao.help;

import com.weixin.njuteam.dao.UserMapper;
import com.weixin.njuteam.entity.po.UserPO;
import com.weixin.njuteam.entity.po.help.*;
import com.weixin.njuteam.enums.HelpFinishStatus;
import com.weixin.njuteam.enums.HelpType;
import com.weixin.njuteam.enums.SeekHelpFinishStatus;
import com.weixin.njuteam.enums.SeekHelpType;
import com.weixin.njuteam.util.RandomTestUtil;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
@Rollback
public class ClickHistoryMapperTest {

	private static Long helpClickId;
	private static Long seekHelpClickId;
	private static Date clickStartTime = RandomTestUtil.getRandomDate("2022-03-10", "2023-03-10");
	private static Date clickEndTime = new Date(clickStartTime.getTime() + 1000);

	private static Long helpId;
	private static Long userId;
	private static HelpType helpType = HelpType.HELP;
	private static Date publishDate = RandomTestUtil.getRandomDate("2022-03-10", "2022-04-26");
	private static String name = RandomTestUtil.getRandomString(10);
	private static Date deadLine = RandomTestUtil.getRandomDate("2022-03-10", "2022-04-26");
	private static String comment = RandomTestUtil.getRandomString(100);
	private static String tag = RandomTestUtil.getRandomString(5);
	private static HelpFinishStatus finishStatus = HelpFinishStatus.IN_PROGRESS;

	private static Long seekHelpId;
	private static SeekHelpType seekHelpType = SeekHelpType.SEEK_HELP;
	private static SeekHelpFinishStatus seekFinishStatus = SeekHelpFinishStatus.IN_PROGRESS;

	private static String openId = RandomTestUtil.getRandomString(20);
	private static String nickName = RandomTestUtil.getRandomString(20);
	private static String avatarUrl = RandomTestUtil.getRandomString(100);

	private ClickHistoryMapper clickHistoryMapper;
	private HelpInfoMapper helpInfoMapper;
	private SeekHelpInfoMapper seekHelpInfoMapper;
	private UserMapper userMapper;

	@Autowired
	public void setClickHistoryMapper(ClickHistoryMapper clickHistoryMapper) {
		this.clickHistoryMapper = clickHistoryMapper;
	}

	@Autowired
	public void setHelpInfoMapper(HelpInfoMapper helpInfoMapper) {
		this.helpInfoMapper = helpInfoMapper;
	}

	@Autowired
	public void setSeekHelpInfoMapper(SeekHelpInfoMapper seekHelpInfoMapper) {
		this.seekHelpInfoMapper = seekHelpInfoMapper;
	}

	@Autowired
	public void setUserMapper(UserMapper userMapper) {
		this.userMapper = userMapper;
	}

	@Test
	@Order(0)
	@Rollback(value = false)
	public void insertUserTest() {
		UserPO userPo = UserPO.builder()
			.openId(openId)
			.avatarUrl(avatarUrl)
			.nickName(nickName)
			.build();

		int i = userMapper.insertUser(userPo);

		assertTrue(i > 0);
		assertEquals(openId, userPo.getOpenId());
		assertEquals(avatarUrl, userPo.getAvatarUrl());
		assertEquals(nickName, userPo.getNickName());

		userId = userPo.getId();
	}

	@Test
	@Order(1)
	@Rollback(value = false)
	public void insertHelpInfoTest() {
		HelpInfoPO helpInfo = HelpInfoPO.builder()
			.userId(userId)
			.helpType(helpType)
			.publishDate(publishDate)
			.name(name)
			.deadLine(deadLine)
			.comment(comment)
			.tag(tag)
			.finishStatus(finishStatus)
			.build();

		int i = helpInfoMapper.insertHelpInfo(helpInfo);
		assertTrue(i > 0);
		helpId = helpInfo.getId();
	}

	@Test
	@Order(1)
	@Rollback(value = false)
	public void insertSeekHelpInfoTest() {
		SeekHelpInfoPO helpInfo = SeekHelpInfoPO.builder()
			.userId(userId)
			.seekHelpType(seekHelpType)
			.publishDate(publishDate)
			.name(name)
			.deadLine(deadLine)
			.comment(comment)
			.tag(tag)
			.finishStatus(seekFinishStatus)
			.build();

		int i = seekHelpInfoMapper.insertSeekInfo(helpInfo);
		assertTrue(i > 0);
		seekHelpId = helpInfo.getId();
	}

	@Test
	@Order(2)
	@Rollback(value = false)
	public void insertHelpClickTest() {
		HelpInfoClickPO clickPo = HelpInfoClickPO.builder()
			.helpId(helpId)
			.userId(userId)
			.startTime(clickStartTime)
			.endTime(clickEndTime)
			.build();

		int i = helpInfoMapper.insertClick(clickPo);
		assertTrue(i > 0);
		helpClickId = clickPo.getId();
	}

	@Test
	@Order(2)
	@Rollback(value = false)
	public void insertSeekHelpClickTest() {
		SeekHelpInfoClickPO clickPo = SeekHelpInfoClickPO.builder()
			.seekHelpId(seekHelpId)
			.userId(userId)
			.startTime(clickStartTime)
			.endTime(clickEndTime)
			.build();

		int i = seekHelpInfoMapper.insertClick(clickPo);
		assertTrue(i > 0);
		seekHelpClickId = clickPo.getId();
	}

	@Test
	@Order(3)
	public void queryHelpClickTest() {
		HelpClickHistoryPO clickPo = clickHistoryMapper.queryClickHelpHistoryById(helpClickId);

		assertEquals(helpClickId, clickPo.getId());
		assertEquals(userId, clickPo.getUserId());

		HelpInfoPO helpInfo = clickPo.getHelpInfo();
		assertNotNull(helpInfo);
		assertEquals(userId, helpInfo.getUserId());
		assertEquals(helpType.getValue(), helpInfo.getHelpType());
		assertEquals(name, helpInfo.getName());
		assertEquals(comment, helpInfo.getComment());
		assertEquals(tag, helpInfo.getTag());
		assertEquals(seekFinishStatus.getValue(), helpInfo.getFinishStatus());
	}

	@Test
	@Order(3)
	public void querySeekHelpClickTest() {
		SeekHelpClickHistoryPO clickPo = clickHistoryMapper.queryClickSeekHelpHistoryById(seekHelpClickId);

		assertEquals(seekHelpClickId, clickPo.getId());
		assertEquals(userId, clickPo.getUserId());

		SeekHelpInfoPO seekHelpInfo = clickPo.getSeekHelpInfo();
		assertNotNull(seekHelpInfo);
		assertEquals(userId, seekHelpInfo.getUserId());
		assertEquals(seekHelpType.getValue(), seekHelpInfo.getSeekHelpType());
		assertEquals(name, seekHelpInfo.getName());
		assertEquals(comment, seekHelpInfo.getComment());
		assertEquals(tag, seekHelpInfo.getTag());
		assertEquals(finishStatus.getValue(), seekHelpInfo.getFinishStatus());
	}

	@Test
	@Order(5)
	@Rollback(value = false)
	public void deleteHelpInfoTest() {
		int i = helpInfoMapper.deleteAllImage(helpId);
		int j = helpInfoMapper.deleteHelpInfo(helpId);

		assertTrue(i >= 0);
		assertTrue(j > 0);
	}

	@Test
	@Order(5)
	@Rollback(value = false)
	public void deleteUserTest() {
		int i = userMapper.deleteUserById(userId);

		assertTrue(i > 0);
	}

	@Test
	@Order(5)
	@Rollback(value = false)
	public void deleteSeekHelpInfoTest() {
		int i = seekHelpInfoMapper.deleteSeekInfo(seekHelpId);
		assertTrue(i > 0);
	}

	@Test
	@Order(5)
	@Rollback(value = false)
	public void deleteHelpClickTest() {
		int i = clickHistoryMapper.deleteClickHelpHistoryById(helpClickId);
		assertTrue(i > 0);
	}

	@Test
	@Order(5)
	@Rollback(value = false)
	public void deleteSeekHelpClickTest() {
		int i = clickHistoryMapper.deleteClickSeekHelpHistoryById(seekHelpClickId);
		assertTrue(i > 0);
	}
}
