package com.weixin.njuteam.service.impl.help;

import com.weixin.njuteam.entity.vo.UserVO;
import com.weixin.njuteam.entity.vo.help.HelpInfoAndUserVO;
import com.weixin.njuteam.entity.vo.help.HelpInfoVO;
import com.weixin.njuteam.entity.vo.help.HelpSearchHistoryVO;
import com.weixin.njuteam.enums.HelpFinishStatus;
import com.weixin.njuteam.enums.HelpType;
import com.weixin.njuteam.service.HelpInfoService;
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
@Rollback
public class HelpInfoServiceImplTest {

	private static Long id;
	private static Long userId;
	private static HelpType helpType = HelpType.HELP;
	private static Date publishDate = RandomTestUtil.getRandomDate("2022-03-10", "2022-04-26");
	private static String name = RandomTestUtil.getRandomString(10);
	private static Date deadLine = RandomTestUtil.getRandomDate("2022-12-10", "2023-04-26");
	private static String comment = RandomTestUtil.getRandomString(100);
	private static String tag = RandomTestUtil.getRandomString(5);
	private static HelpFinishStatus finishStatus = HelpFinishStatus.IN_PROGRESS;

	private static String openId = RandomTestUtil.getRandomString(20);
	private static String avatarUrl = RandomTestUtil.getRandomString(150);
	private static String nickName = RandomTestUtil.getRandomString(20);

	private static Long historyId;

	private HelpInfoService infoService;
	private UserService userService;

	@Autowired
	public void setInfoService(HelpInfoService infoService) {
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
		HelpInfoVO infoVo = HelpInfoVO.builder()
			.userId(userId)
			.helpType(helpType)
			.publishDate(publishDate)
			.name(name)
			.deadLine(deadLine)
			.comment(comment)
			.tag(tag)
			.finishStatus(finishStatus)
			.build();

		HelpInfoVO newInfo = infoService.insertHelpInfo(infoVo, openId);
		assertNotNull(newInfo);
		if (deadLine.before(new Date())) {
			finishStatus = HelpFinishStatus.CLOSED;
		}
		id = newInfo.getId();
	}

	@Test
	@Order(2)
	public void queryHelpInfoTest() {
		HelpInfoAndUserVO helpInfo = infoService.queryHelpInfoById(id);

		assertNotNull(helpInfo);
		assertEquals(userId, helpInfo.getUserId());
		assertEquals(helpType.getValue(), helpInfo.getHelpType());
		assertEquals(name, helpInfo.getName());
		assertEquals(comment, helpInfo.getComment());
		assertEquals(tag, helpInfo.getTag());
	}

//	@Test
//	@Order(2)
//	public void queryHelpInfoUserTest() {
//		List<HelpInfoVO> infoList = infoService.queryHelpInfoByUserId(openId);
//		HelpInfoVO helpInfo = infoList.get(0);
//
//		assertNotNull(helpInfo);
//		assertEquals(userId, helpInfo.getUserId());
//		assertEquals(helpType.getValue(), helpInfo.getHelpType());
//		assertEquals(name, helpInfo.getName());
//		assertEquals(comment, helpInfo.getComment());
//		assertEquals(tag, helpInfo.getTag());
//		assertEquals(finishStatus.getValue(), helpInfo.getFinishStatus());
//	}

	@Test
	@Order(2)
	public void queryHelpInfoGroupByTagTest() {
		Map<String, List<HelpInfoAndUserVO>> infoMap = infoService.getHelpInfoGroupByTag();
		if (infoMap.get(tag) != null) {
			HelpInfoAndUserVO helpInfo = infoMap.get(tag).get(0);

			assertNotNull(helpInfo);
			assertEquals(userId, helpInfo.getUserId());
			assertEquals(helpType.getValue(), helpInfo.getHelpType());
			assertEquals(name, helpInfo.getName());
			assertEquals(comment, helpInfo.getComment());
			assertEquals(tag, helpInfo.getTag());
			assertEquals(finishStatus.getValue(), helpInfo.getFinishStatus());
		}
	}

	@Test
	@Order(2)
	@Rollback(value = false)
	public void SearchKeywordTest() {
		// 关键词搜索
		List<HelpInfoAndUserVO> infoList = infoService.searchHelpInfo(openId, name);
		if (infoList.size() > 0) {
			HelpInfoAndUserVO info = infoList.get(0);

			assertNotNull(info);
			assertEquals(userId, info.getUserId());
			assertEquals(helpType.getValue(), info.getHelpType());
			assertEquals(name, info.getName());
			assertEquals(comment, info.getComment());
			assertEquals(tag, info.getTag());
			assertEquals(finishStatus.getValue(), info.getFinishStatus());
		}
	}

	@Test
	@Order(3)
	public void querySearchHistoryTest() {
		List<HelpSearchHistoryVO> historyList = infoService.querySearchHistory(openId, 15);
		HelpSearchHistoryVO history = historyList.get(0);
		historyId = history.getId();

		assertNotNull(history);
		assertEquals(name, history.getKeyword());
		assertEquals(userId, history.getUserId());
	}

	@Test
	@Order(4)
	public void updateHelpInfoTest() {
		helpType = HelpType.LEND;
		name = RandomTestUtil.getRandomString(10);
		comment = RandomTestUtil.getRandomString(100);
		tag = RandomTestUtil.getRandomString(5);
		finishStatus = HelpFinishStatus.CLOSED;

		HelpInfoVO helpInfo = HelpInfoVO.builder()
			.id(id)
			.userId(userId)
			.helpType(helpType)
			.publishDate(publishDate)
			.name(name)
			.comment(comment)
			.tag(tag)
			.finishStatus(finishStatus)
			.build();

		HelpInfoVO infoVo = infoService.updateHelpInfo(helpInfo, openId);

		assertEquals(userId, infoVo.getUserId());
		assertEquals(helpType.getValue(), infoVo.getHelpType());
		assertEquals(name, infoVo.getName());
		assertEquals(comment, infoVo.getComment());
		assertEquals(tag, infoVo.getTag());
		assertEquals(finishStatus.getValue(), helpInfo.getFinishStatus());
	}

	@Test
	@Order(4)
	public void updateFinishStatusTest() {
		finishStatus = HelpFinishStatus.IN_PROGRESS;

		HelpInfoVO helpInfo = infoService.updateFinishStatus(id, finishStatus);
		assertEquals(finishStatus.getValue(), helpInfo.getFinishStatus());
	}

	@Test
	@Order(5)
	@Rollback(value = false)
	public void deleteInfoTest() {
		boolean isDeleted = infoService.deleteHelpInfo(id);

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
		boolean isDeleted = infoService.deleteSearchHelpInfoHistory(historyId);

		assertTrue(isDeleted);
	}
}
