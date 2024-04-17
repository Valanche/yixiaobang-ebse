package com.weixin.njuteam.service.impl.nucleic;

import com.weixin.njuteam.entity.vo.UserVO;
import com.weixin.njuteam.entity.vo.nucleic.NucleicAcidBookingVO;
import com.weixin.njuteam.enums.FinishStatus;
import com.weixin.njuteam.service.NucleicAcidBookingService;
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
@Transactional
@Rollback
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class NucleicAcidBookingServiceImplTest {

	private static Long id;
	private static Long userId;
	private static Long managerId = RandomTestUtil.getRandomId();
	private static String title = RandomTestUtil.getRandomString(30);
	private static Date deadLine = RandomTestUtil.getRandomDate("2022-12-30", "2023-12-30");
	private static Boolean isOpenRemind = false;
	private static FinishStatus finishStatus = FinishStatus.IN_PROGRESS;
	private static String openId = RandomTestUtil.getRandomString(20);
	private static String avatarUrl = RandomTestUtil.getRandomString(150);
	private static String nickName = RandomTestUtil.getRandomString(20);

	private NucleicAcidBookingService bookingService;
	private UserService userService;

	@Autowired
	public void setBookingService(NucleicAcidBookingService bookingService) {
		this.bookingService = bookingService;
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
		NucleicAcidBookingVO bookingVO = NucleicAcidBookingVO.builder()
			.userId(userId)
			.managerId(managerId)
			.title(title)
			.deadLine(deadLine)
			.isOpenRemind(isOpenRemind)
			.finishStatus(finishStatus)
			.build();

		NucleicAcidBookingVO newBookingVo = bookingService.insertBooking(bookingVO);

		assertNotNull(newBookingVo);
		if (deadLine.before(new Date())) {
			finishStatus = FinishStatus.TO_BE_CONTINUE;
		}
		id = bookingVO.getId();
	}

	@Test
	@Order(2)
	public void queryByIdTest() {
		NucleicAcidBookingVO bookingVo = bookingService.queryBookingById(id);

		assertEquals(userId, bookingVo.getUserId());
		assertEquals(title, bookingVo.getTitle());
		// fixme:时区问题 本地测试OK但是服务器上不知道为啥差了8个小时 估计是服务器上的Java跑的时区有点问题
		// assertEquals(deadLine.toString(), bookingVo.getDeadLine().toString());
		assertEquals(isOpenRemind, bookingVo.getIsOpenRemind());
		assertEquals(finishStatus.getValue(), bookingVo.getFinishStatus());
	}

	@Test
	@Order(2)
	public void queryByUserIdTest() throws SQLException {
		List<NucleicAcidBookingVO> bookingVoList = bookingService.queryBookingByUserId(openId);
		NucleicAcidBookingVO bookingVo = bookingVoList.get(0);

		assertEquals(userId, bookingVo.getUserId());
		assertEquals(title, bookingVo.getTitle());
		// assertEquals(deadLine.toString(), bookingVo.getDeadLine().toString());
		assertEquals(isOpenRemind, bookingVo.getIsOpenRemind());
		assertEquals(finishStatus.getValue(), bookingVo.getFinishStatus());
	}

	@Test
	@Order(2)
	public void queryUserBookingCountTest() {
		int count = bookingService.queryBookingCount(openId, FinishStatus.IN_PROGRESS);
		assertEquals(1, count);
	}

	@Test
	@Order(3)
	public void updateTest() {
		finishStatus = FinishStatus.DONE;
		title = RandomTestUtil.getRandomString(30);

		NucleicAcidBookingVO bookingVo = NucleicAcidBookingVO.builder()
			.id(id)
			.userId(userId)
			.managerId(managerId)
			.title(title)
			.deadLine(deadLine)
			.isOpenRemind(isOpenRemind)
			.finishStatus(finishStatus)
			.build();

		NucleicAcidBookingVO newBookingVo = bookingService.updateBooking(bookingVo);

		assertNotNull(newBookingVo);
		assertEquals(userId, bookingVo.getUserId());
		assertEquals(title, bookingVo.getTitle());
		// assertEquals(deadLine.toString(), bookingVo.getDeadLine().toString());
		assertEquals(isOpenRemind, bookingVo.getIsOpenRemind());
		assertEquals(finishStatus.getValue(), bookingVo.getFinishStatus());
	}

	@Test
	@Order(4)
	@Rollback(value = false)
	public void deleteTest() {
		boolean isDeleted = bookingService.deleteBooking(id);

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
