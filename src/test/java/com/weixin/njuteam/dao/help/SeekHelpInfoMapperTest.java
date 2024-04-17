package com.weixin.njuteam.dao.help;

import com.weixin.njuteam.dao.UserMapper;
import com.weixin.njuteam.entity.po.UserPO;
import com.weixin.njuteam.entity.po.help.SeekHelpImage;
import com.weixin.njuteam.entity.po.help.SeekHelpInfoPO;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
@Rollback
public class SeekHelpInfoMapperTest {

	private static Long id;
	private static Long userId;
	private static SeekHelpType seekHelpType = SeekHelpType.SEEK_HELP;
	private static Date publishDate = RandomTestUtil.getRandomDate("2022-03-10", "2022-04-26");
	private static String name = RandomTestUtil.getRandomString(10);
	private static Date deadLine = RandomTestUtil.getRandomDate("2022-03-10", "2022-04-26");
	private static String comment = RandomTestUtil.getRandomString(100);
	private static String tag = RandomTestUtil.getRandomString(5);
	private static SeekHelpFinishStatus finishStatus = SeekHelpFinishStatus.IN_PROGRESS;
	private static List<SeekHelpImage> imageList = new ArrayList<>();

	private static String openId = RandomTestUtil.getRandomString(20);
	private static String nickName = RandomTestUtil.getRandomString(20);
	private static String avatarUrl = RandomTestUtil.getRandomString(100);

	private SeekHelpInfoMapper mapper;
	private UserMapper userMapper;

	@Autowired
	public void setMapper(SeekHelpInfoMapper mapper) {
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
	public void insertSeekHelpInfoTest() {
		SeekHelpInfoPO helpInfo = SeekHelpInfoPO.builder()
			.userId(userId)
			.seekHelpType(seekHelpType)
			.publishDate(publishDate)
			.name(name)
			.deadLine(deadLine)
			.comment(comment)
			.tag(tag)
			.finishStatus(finishStatus)
			.build();

		int i = mapper.insertSeekInfo(helpInfo);
		assertTrue(i > 0);
		id = helpInfo.getId();
	}

	@Test
	@Order(2)
	@Rollback(value = false)
	public void insertImageTest() {
		SeekHelpImage helpImage = SeekHelpImage.builder()
			.seekHelpId(id)
			.imageUrl(RandomTestUtil.getRandomString(100))
			.build();

		int i = mapper.insertSeekImage(helpImage);
		assertTrue(i > 0);
		imageList.add(helpImage);
	}

	@Test
	@Order(2)
	@Rollback(value = false)
	public void insertImage2Test() {
		SeekHelpImage helpImage = SeekHelpImage.builder()
			.seekHelpId(id)
			.imageUrl(RandomTestUtil.getRandomString(100))
			.build();

		int i = mapper.insertSeekImage(helpImage);
		assertTrue(i > 0);
		imageList.add(helpImage);
	}

	@Test
	@Order(3)
	public void queryHelpInfoTest() {
		SeekHelpInfoPO seekInfo = mapper.querySeekInfoById(id);

		assertEquals(userId, seekInfo.getUserId());
		assertEquals(seekHelpType.getValue(), seekInfo.getSeekHelpType());
		assertEquals(name, seekInfo.getName());
		assertEquals(comment, seekInfo.getComment());
		assertEquals(tag, seekInfo.getTag());
		assertEquals(finishStatus.getValue(), seekInfo.getFinishStatus());

		List<SeekHelpImage> infoHelpImageList = seekInfo.getUrlList();
		assertNotNull(infoHelpImageList);

		for (int i = 0; i < imageList.size(); i++) {
			assertEquals(imageList.get(i).getId(), infoHelpImageList.get(i).getId());
			assertEquals(imageList.get(i).getSeekHelpId(), infoHelpImageList.get(i).getSeekHelpId());
			assertEquals(imageList.get(i).getImageUrl(), infoHelpImageList.get(i).getImageUrl());
		}
	}

	@Test
	@Order(3)
	@Rollback(value = false)
	public void SearchKeywordTest() {
		// 关键词搜索
		List<SeekHelpInfoPO> infoList = mapper.querySeekInfoByKeyword(name);
		SeekHelpInfoPO info = infoList.get(0);

		assertNotNull(info);
		assertEquals(userId, info.getUserId());
		assertEquals(seekHelpType.getValue(), info.getSeekHelpType());
		assertEquals(name, info.getName());
		assertEquals(comment, info.getComment());
		assertEquals(tag, info.getTag());
		assertEquals(finishStatus.getValue(), info.getFinishStatus());
	}

	@Test
	@Order(4)
	public void updateHelpInfoTest() {
		seekHelpType = SeekHelpType.SEEK_LEND;
		name = RandomTestUtil.getRandomString(10);
		comment = RandomTestUtil.getRandomString(100);
		tag = RandomTestUtil.getRandomString(5);
		finishStatus = SeekHelpFinishStatus.NOT_FINISHED;

		SeekHelpInfoPO seekHelpInfo = SeekHelpInfoPO.builder()
			.id(id)
			.userId(userId)
			.seekHelpType(seekHelpType)
			.publishDate(publishDate)
			.name(name)
			.deadLine(deadLine)
			.comment(comment)
			.tag(tag)
			.finishStatus(finishStatus)
			.build();

		int i = mapper.updateSeekInfo(seekHelpInfo);

		assertTrue(i > 0);
		assertEquals(userId, seekHelpInfo.getUserId());
		assertEquals(seekHelpType.getValue(), seekHelpInfo.getSeekHelpType());
		assertEquals(name, seekHelpInfo.getName());
		assertEquals(comment, seekHelpInfo.getComment());
		assertEquals(tag, seekHelpInfo.getTag());
		assertEquals(finishStatus.getValue(), seekHelpInfo.getFinishStatus());
	}

	@Test
	@Order(4)
	public void updateFinishStatusTest() {
		finishStatus = SeekHelpFinishStatus.FINISHED;

		int i = mapper.updateFinishStatus(id, finishStatus);
		assertTrue(i > 0);
		SeekHelpInfoPO helpInfo = mapper.querySeekInfoById(id);
		assertEquals(finishStatus.getValue(), helpInfo.getFinishStatus());
	}

	@Test
	@Order(5)
	@Rollback(value = false)
	public void deleteTest() {
		int i = mapper.deleteAllImage(id);
		int j = mapper.deleteSeekInfo(id);

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
