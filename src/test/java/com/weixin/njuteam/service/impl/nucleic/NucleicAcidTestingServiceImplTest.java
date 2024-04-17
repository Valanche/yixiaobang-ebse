package com.weixin.njuteam.service.impl.nucleic;

import com.weixin.njuteam.entity.vo.UserVO;
import com.weixin.njuteam.entity.vo.nucleic.NucleicAcidTestingVO;
import com.weixin.njuteam.enums.FinishStatus;
import com.weixin.njuteam.service.NucleicAcidTestingService;
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

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
@Rollback
public class NucleicAcidTestingServiceImplTest {

	private static Long id;
	private static Long userId;
	private static Long managerId = RandomTestUtil.getRandomId();
	private static String require = RandomTestUtil.getRandomString(30);
	private static String title = RandomTestUtil.getRandomString(30);
	private static Date startTime = RandomTestUtil.getRandomDate("2000-01-01", "2012-04-22");
	private static Date endTime = RandomTestUtil.getRandomDate("2023-12-23", "2024-04-23");
	private static boolean isOpenRemind = false;
	private static String place = RandomTestUtil.getRandomString(50);
	private static FinishStatus finishStatus = FinishStatus.IN_PROGRESS;
	private static String openId = RandomTestUtil.getRandomString(20);
	private static String avatarUrl = RandomTestUtil.getRandomString(150);
	private static String nickName = RandomTestUtil.getRandomString(20);

	private NucleicAcidTestingService testingService;
	private UserService userService;

	@Autowired
	public void setTestingService(NucleicAcidTestingService testingService) {
		this.testingService = testingService;
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
	public void insertTest() {
		NucleicAcidTestingVO testingVo = NucleicAcidTestingVO.builder()
			.userId(userId)
			.managerId(managerId)
			.require(require)
			.title(title)
			.startTime(startTime)
			.endTime(endTime)
			.isOpenRemind(isOpenRemind)
			.place(place)
			.finishStatus(finishStatus)
			.build();

		NucleicAcidTestingVO newTestingVo = testingService.insertTesting(testingVo);
		if (endTime.before(new Date())) {
			finishStatus = FinishStatus.TO_BE_CONTINUE;
		}
		assertNotNull(testingVo);
		id = newTestingVo.getId();
	}

	@Test
	@Order(2)
	public void queryByIdTest() {
		NucleicAcidTestingVO testingVo = testingService.queryTestingById(id);

		assertNotNull(testingVo);
		assertEquals(userId, testingVo.getUserId());
		assertEquals(require, testingVo.getRequire());
		assertEquals(title, testingVo.getTitle());
		// fixme: 时间问题
		// assertEquals(testingDate, testingVo.getTestingTime());
		assertEquals(isOpenRemind, testingVo.getIsOpenRemind());
		assertEquals(place, testingVo.getPlace());
		assertEquals(finishStatus.getValue(), testingVo.getFinishStatus());
	}

	@Test
	@Order(2)
	public void queryByUserIdTest() throws SQLException {
		List<NucleicAcidTestingVO> testingVoList = testingService.queryTestingByUserId(openId);
		NucleicAcidTestingVO testingVo = testingVoList.get(0);

		assertNotNull(testingVo);
		assertEquals(userId, testingVo.getUserId());
		assertEquals(require, testingVo.getRequire());
		assertEquals(title, testingVo.getTitle());
		assertEquals(isOpenRemind, testingVo.getIsOpenRemind());
		// assertEquals(testingDate, testingVo.getTestingTime());
		assertEquals(place, testingVo.getPlace());
		assertEquals(finishStatus.getValue(), testingVo.getFinishStatus());
	}

	@Test
	@Order(2)
	public void queryUserBookingCountTest() {
		int count = testingService.queryTestingCount(openId, FinishStatus.IN_PROGRESS);
		assertEquals(1, count);
	}

	@Test
	@Order(3)
	public void updateTest() {
		require = RandomTestUtil.getRandomString(50);
		title = RandomTestUtil.getRandomString(50);
		isOpenRemind = true;
		finishStatus = FinishStatus.DONE;
		place = RandomTestUtil.getRandomString(50);

		NucleicAcidTestingVO testingVo = NucleicAcidTestingVO.builder()
			.id(id)
			.userId(userId)
			.managerId(managerId)
			.require(require)
			.title(title)
			.startTime(startTime)
			.endTime(endTime)
			.isOpenRemind(isOpenRemind)
			.place(place)
			.finishStatus(finishStatus)
			.build();

		NucleicAcidTestingVO newTestingVo = testingService.updateTesting(testingVo);

		assertNotNull(newTestingVo);
		assertEquals(userId, newTestingVo.getUserId());
		assertEquals(managerId, newTestingVo.getManagerId());
		assertEquals(require, newTestingVo.getRequire());
		assertEquals(title, newTestingVo.getTitle());
		assertEquals(isOpenRemind, newTestingVo.getIsOpenRemind());
		// assertEquals(testingDate, testingVo.getTestingTime());
		assertEquals(place, newTestingVo.getPlace());
		assertEquals(finishStatus.getValue(), newTestingVo.getFinishStatus());
	}

	@Test
	@Order(4)
	@Rollback(value = false)
	public void deleteTest() {
		boolean isDeleted = testingService.deleteTesting(id);

		assertTrue(isDeleted);
	}

	@Test
	@Order(4)
	@Rollback(value = false)
	public void deleteUserTest() {
		boolean isDeleted = userService.deleteUserById(userId);

		assertTrue(isDeleted);
	}
}
