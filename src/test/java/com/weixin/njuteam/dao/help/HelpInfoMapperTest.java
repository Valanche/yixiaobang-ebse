package com.weixin.njuteam.dao.help;

import com.weixin.njuteam.dao.UserMapper;
import com.weixin.njuteam.entity.po.UserPO;
import com.weixin.njuteam.entity.po.help.HelpImage;
import com.weixin.njuteam.entity.po.help.HelpInfoPO;
import com.weixin.njuteam.enums.HelpFinishStatus;
import com.weixin.njuteam.enums.HelpType;
import com.weixin.njuteam.util.RandomTestUtil;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
@Rollback
public class HelpInfoMapperTest {

	private static Long id;
	private static Long userId;
	private static HelpType helpType = HelpType.HELP;
	private static Date publishDate = RandomTestUtil.getRandomDate("2022-03-10", "2022-04-26");
	private static String name = RandomTestUtil.getRandomString(10);
	private static Date deadLine = RandomTestUtil.getRandomDate("2022-03-10", "2022-04-26");
	private static String comment = RandomTestUtil.getRandomString(100);
	private static String tag = RandomTestUtil.getRandomString(5);
	private static HelpFinishStatus finishStatus = HelpFinishStatus.IN_PROGRESS;
	private static List<HelpImage> helpImageList = new ArrayList<>();

	private static String openId = RandomTestUtil.getRandomString(20);
	private static String nickName = RandomTestUtil.getRandomString(20);
	private static String avatarUrl = RandomTestUtil.getRandomString(100);

	private HelpInfoMapper mapper;
	private UserMapper userMapper;

	@Autowired
	public void setMapper(HelpInfoMapper mapper) {
		this.mapper = mapper;
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

		int i = mapper.insertHelpInfo(helpInfo);
		assertTrue(i > 0);
		id = helpInfo.getId();
	}

	@Test
	@Order(2)
	@Rollback(value = false)
	public void insertImageTest() {
		HelpImage helpImage = HelpImage.builder()
			.helpId(id)
			.imageUrl(RandomTestUtil.getRandomString(100))
			.build();

		int i = mapper.insertImage(helpImage);
		assertTrue(i > 0);
		helpImageList.add(helpImage);
	}

	@Test
	@Order(2)
	@Rollback(value = false)
	public void insertImage2Test() {
		HelpImage helpImage = HelpImage.builder()
			.helpId(id)
			.imageUrl(RandomTestUtil.getRandomString(100))
			.build();

		int i = mapper.insertImage(helpImage);
		assertTrue(i > 0);
		helpImageList.add(helpImage);
	}

	@Test
	@Order(3)
	public void queryHelpInfoTest() {
		HelpInfoPO helpInfo = mapper.queryHelpInfoById(id);

		assertEquals(userId, helpInfo.getUserId());
		assertEquals(helpType.getValue(), helpInfo.getHelpType());
		assertEquals(name, helpInfo.getName());
		assertEquals(comment, helpInfo.getComment());
		assertEquals(tag, helpInfo.getTag());
		assertEquals(finishStatus.getValue(), helpInfo.getFinishStatus());

		List<HelpImage> infoHelpImageList = helpInfo.getUrlList();
		assertNotNull(infoHelpImageList);

		for (int i = 0; i < helpImageList.size(); i++) {
			assertEquals(helpImageList.get(i).getId(), infoHelpImageList.get(i).getId());
			assertEquals(helpImageList.get(i).getHelpId(), infoHelpImageList.get(i).getHelpId());
			assertEquals(helpImageList.get(i).getImageUrl(), infoHelpImageList.get(i).getImageUrl());
		}
	}

	@Test
	@Order(3)
	@Rollback(value = false)
	public void SearchKeywordTest() {
		// 关键词搜索
		List<HelpInfoPO> infoList = mapper.queryHelpInfoByKeyword(name);
		HelpInfoPO info = infoList.get(0);

		assertNotNull(info);
		assertEquals(userId, info.getUserId());
		assertEquals(helpType.getValue(), info.getHelpType());
		assertEquals(name, info.getName());
		assertEquals(comment, info.getComment());
		assertEquals(tag, info.getTag());
		assertEquals(finishStatus.getValue(), info.getFinishStatus());
	}

	@Test
	@Order(4)
	public void updateHelpInfoTest() {
		helpType = HelpType.LEND;
		name = RandomTestUtil.getRandomString(10);
		comment = RandomTestUtil.getRandomString(100);
		tag = RandomTestUtil.getRandomString(5);
		finishStatus = HelpFinishStatus.CLOSED;

		HelpInfoPO helpInfo = HelpInfoPO.builder()
			.id(id)
			.userId(userId)
			.helpType(helpType)
			.publishDate(publishDate)
			.name(name)
			.deadLine(deadLine)
			.comment(comment)
			.tag(tag)
			.finishStatus(finishStatus)
			.build();

		int i = mapper.updateHelpInfo(helpInfo);

		assertTrue(i > 0);
		assertEquals(userId, helpInfo.getUserId());
		assertEquals(helpType.getValue(), helpInfo.getHelpType());
		assertEquals(name, helpInfo.getName());
		assertEquals(comment, helpInfo.getComment());
		assertEquals(tag, helpInfo.getTag());
	}

	@Test
	@Order(4)
	public void updateFinishStatusTest() {
		finishStatus = HelpFinishStatus.IN_PROGRESS;

		int i = mapper.updateFinishStatus(id, finishStatus);
		assertTrue(i > 0);
		HelpInfoPO helpInfo = mapper.queryHelpInfoById(id);
		assertEquals(finishStatus.getValue(), helpInfo.getFinishStatus());
	}

	@Test
	@Order(5)
	@Rollback(value = false)
	public void deleteTest() {
		int i = mapper.deleteAllImage(id);
		int j = mapper.deleteHelpInfo(id);

		assertTrue(i > 0);
		assertTrue(j > 0);
	}

	@Test
	@Order(5)
	@Rollback(value = false)
	public void deleteUserTest() {
		int i = userMapper.deleteUserById(userId);

		assertTrue(i > 0);
	}

}
