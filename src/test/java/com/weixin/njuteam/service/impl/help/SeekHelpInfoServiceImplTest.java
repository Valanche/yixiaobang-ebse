package com.weixin.njuteam.service.impl.help;

import com.weixin.njuteam.entity.vo.UserVO;
import com.weixin.njuteam.entity.vo.help.*;
import com.weixin.njuteam.enums.SeekHelpFinishStatus;
import com.weixin.njuteam.enums.SeekHelpType;
import com.weixin.njuteam.service.SeekHelpInfoService;
import com.weixin.njuteam.service.UserService;
import com.weixin.njuteam.util.RandomTestUtil;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
@Rollback
public class SeekHelpInfoServiceImplTest {

	private static Long id;
	private static Long userId;
	private static SeekHelpType seekHelpType = SeekHelpType.SEEK_HELP;
	private static Date publishDate = RandomTestUtil.getRandomDate("2022-03-10", "2022-04-26");
	private static String name = RandomTestUtil.getRandomString(10);
	private static Date deadLine = RandomTestUtil.getRandomDate("2023-03-10", "2023-04-26");
	private static String comment = RandomTestUtil.getRandomString(100);
	private static String tag = RandomTestUtil.getRandomString(5);
	private static SeekHelpFinishStatus finishStatus = SeekHelpFinishStatus.IN_PROGRESS;

	private static String openId = RandomTestUtil.getRandomString(20);
	private static String avatarUrl = RandomTestUtil.getRandomString(150);
	private static String nickName = RandomTestUtil.getRandomString(20);

	private static Long historyId;

	private SeekHelpInfoService infoService;
	private UserService userService;

	@Autowired
	public void setInfoService(SeekHelpInfoService infoService) {
		this.infoService = infoService;
	}

	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	@Test
	@Order(0)
	@Rollback(value = false)
	public void insertUserTest() throws SQLException {
		UserVO userVO = UserVO.builder()
			.openId(openId)
			.avatarUrl(avatarUrl)
			.nickName(nickName)
			.build();

		UserVO info = userService.authLogin(userVO);

		assertEquals(openId, info.getOpenId());
		assertEquals(avatarUrl, info.getAvatarUrl());
		assertEquals(nickName, info.getNickName());

		userId = info.getId();
	}

	@Test
	@Order(1)
	@Rollback(value = false)
	public void insertHelpInfoTest() {
		SeekHelpInfoVO infoVo = SeekHelpInfoVO.builder()
			.userId(userId)
			.seekHelpType(seekHelpType)
			.publishDate(publishDate)
			.name(name)
			.deadLine(deadLine)
			.comment(comment)
			.tag(tag)
			.finishStatus(finishStatus)
			.build();

		SeekHelpInfoVO newInfo = infoService.insertSeekInfo(infoVo, openId);
		assertNotNull(newInfo);
		if (deadLine.before(new Date())) {
			finishStatus = SeekHelpFinishStatus.NOT_FINISHED;
		}
		id = newInfo.getId();
	}

	@Test
	@Order(2)
	public void queryHelpInfoTest() {
		SeekHelpInfoAndUserVO seekInfo = infoService.querySeekInfoById(id);

		assertNotNull(seekInfo);
		assertEquals(userId, seekInfo.getUserId());
		assertEquals(seekHelpType.getValue(), seekInfo.getSeekHelpType());
		assertEquals(name, seekInfo.getName());
		assertEquals(comment, seekInfo.getComment());
		assertEquals(tag, seekInfo.getTag());
		assertEquals(finishStatus.getValue(), seekInfo.getFinishStatus());
	}

//	@Test
//	@Order(2)
//	public void queryHelpInfoUserTest() {
//		List<SeekHelpInfoVO> infoList = infoService.querySeekInfoByUserId(openId);
//		SeekHelpInfoVO seekInfo = infoList.get(0);
//
//		assertNotNull(seekInfo);
//		assertEquals(userId, seekInfo.getUserId());
//		assertEquals(seekHelpType.getValue(), seekInfo.getSeekHelpType());
//		assertEquals(name, seekInfo.getName());
//		assertEquals(comment, seekInfo.getComment());
//		assertEquals(tag, seekInfo.getTag());
//		assertEquals(finishStatus.getValue(), seekInfo.getFinishStatus());
//	}

	@Test
	@Order(2)
	public void getSeekHelpInfoGroupByTagTest() {
		Map<String, List<SeekHelpInfoAndUserVO>> seekInfoMap = infoService.getSeekInfoGroupByTag();
		if (seekInfoMap.get(tag) != null) {
			SeekHelpInfoAndUserVO seekInfo = seekInfoMap.get(tag).get(0);

			assertNotNull(seekInfo);
			assertEquals(userId, seekInfo.getUserId());
			assertEquals(seekHelpType.getValue(), seekInfo.getSeekHelpType());
			assertEquals(name, seekInfo.getName());
			assertEquals(comment, seekInfo.getComment());
			assertEquals(tag, seekInfo.getTag());
			assertEquals(finishStatus.getValue(), seekInfo.getFinishStatus());
		}
	}

	@Test
	@Order(2)
	@Rollback(value = false)
	public void SearchKeywordTest() {
		// 关键词搜索
		List<SeekHelpInfoAndUserVO> infoList = infoService.searchSeekInfo(openId, name);
		if (infoList.size() > 0) {
			SeekHelpInfoAndUserVO seekInfo = infoList.get(0);

			assertNotNull(seekInfo);
			assertEquals(userId, seekInfo.getUserId());
			assertEquals(seekHelpType.getValue(), seekInfo.getSeekHelpType());
			assertEquals(name, seekInfo.getName());
			assertEquals(comment, seekInfo.getComment());
			assertEquals(tag, seekInfo.getTag());
			assertEquals(finishStatus.getValue(), seekInfo.getFinishStatus());
		}
	}

	@Test
	@Order(3)
	public void querySearchHistoryTest() {
		List<SeekHelpSearchHistoryVO> historyList = infoService.querySearchHistory(openId, -1);
		SeekHelpSearchHistoryVO history = historyList.get(0);
		historyId = history.getId();

		assertNotNull(history);
		assertEquals(name, history.getKeyword());
		assertEquals(userId, history.getUserId());
	}

	@Test
	@Order(4)
	public void updateHelpInfoTest() {
		seekHelpType = SeekHelpType.SEEK_LEND;
		name = RandomTestUtil.getRandomString(10);
		comment = RandomTestUtil.getRandomString(100);
		tag = RandomTestUtil.getRandomString(5);
		finishStatus = SeekHelpFinishStatus.NOT_FINISHED;

		SeekHelpInfoVO helpInfo = SeekHelpInfoVO.builder()
			.id(id)
			.userId(userId)
			.seekHelpType(seekHelpType)
			.publishDate(publishDate)
			.name(name)
			.comment(comment)
			.tag(tag)
			.finishStatus(finishStatus)
			.build();

		SeekHelpInfoVO seekInfo = infoService.updateSeekInfo(helpInfo, openId);

		assertEquals(userId, seekInfo.getUserId());
		assertEquals(seekHelpType.getValue(), seekInfo.getSeekHelpType());
		assertEquals(name, seekInfo.getName());
		assertEquals(comment, seekInfo.getComment());
		assertEquals(tag, seekInfo.getTag());
		assertEquals(finishStatus.getValue(), seekInfo.getFinishStatus());
	}

	@Test
	@Order(4)
	public void updateFinishStatusTest() {
		finishStatus = SeekHelpFinishStatus.FINISHED;

		SeekHelpInfoVO helpInfo = infoService.updateFinishStatus(id, finishStatus);
		assertEquals(finishStatus.getValue(), helpInfo.getFinishStatus());
	}

	@Test
	@Order(5)
	@Rollback(value = false)
	public void deleteInfoTest() {
		boolean isDeleted = infoService.deleteSeekInfo(id);

		assertTrue(isDeleted);
	}

	@Test
	@Order(5)
	@Rollback(value = false)
	public void deleteUserTest() {
		boolean isDeleted = userService.deleteUserById(userId);

		assertTrue(isDeleted);
	}

	@Test
	@Order(5)
	@Rollback(value = false)
	public void deleteSearchTest() {
		boolean isDeleted = infoService.deleteSearchHistory(historyId);

		assertTrue(isDeleted);
	}
}
