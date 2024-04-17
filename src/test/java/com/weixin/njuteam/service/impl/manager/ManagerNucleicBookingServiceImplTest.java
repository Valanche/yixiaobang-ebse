package com.weixin.njuteam.service.impl.manager;

import com.weixin.njuteam.dao.UserMapper;
import com.weixin.njuteam.dao.nucleic.NucleicAcidBookingMapper;
import com.weixin.njuteam.entity.po.UserPO;
import com.weixin.njuteam.entity.po.nucleic.NucleicAcidBookingPO;
import com.weixin.njuteam.entity.vo.manager.ManagerVO;
import com.weixin.njuteam.entity.vo.manager.nucleic.ManagerNucleicAcidBookingVO;
import com.weixin.njuteam.enums.FinishStatus;
import com.weixin.njuteam.enums.Role;
import com.weixin.njuteam.service.ManagerNucleicAcidBookingService;
import com.weixin.njuteam.service.ManagerService;
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

/**
 * @author Zyi
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
@Rollback
public class ManagerNucleicBookingServiceImplTest {

	private ManagerNucleicAcidBookingService bookingService;
	private UserMapper userMapper;
	private ManagerService managerService;
	private NucleicAcidBookingMapper bookingMapper;

	@Autowired
	public void setBookingService(ManagerNucleicAcidBookingService bookingService) {
		this.bookingService = bookingService;
	}

	@Autowired
	public void setUserMapper(UserMapper userMapper) {
		this.userMapper = userMapper;
	}

	@Autowired
	public void setManagerService(ManagerService managerService) {
		this.managerService = managerService;
	}

	@Autowired
	public void setBookingMapper(NucleicAcidBookingMapper bookingMapper) {
		this.bookingMapper = bookingMapper;
	}

	private static Long id;
	private static Long managerId;
	private static String title = RandomTestUtil.getRandomString(30);
	private static FinishStatus finishStatus = FinishStatus.IN_PROGRESS;
	private static Date deadLine = RandomTestUtil.getRandomDate("2023-05-01", "2024-07-20");

	private static Long userId;
	private static String openId = RandomTestUtil.getRandomString(50);
	private static String nickName = RandomTestUtil.getRandomString(30);
	private static String school = RandomTestUtil.getRandomString(30);
	private static String institute = RandomTestUtil.getRandomString(30);
	private static String major = RandomTestUtil.getRandomString(30);
	private static String grade = RandomTestUtil.getRandomString(10);
	private static String avatarUrl = RandomTestUtil.getRandomString(150);

	private static String managerName = RandomTestUtil.getRandomString(20);
	private static String managerNickName = RandomTestUtil.getRandomString(20);
	private static String password = RandomTestUtil.getRandomString(50);
	private static Role role = Role.COMMON_MANAGER;
	private static String post = RandomTestUtil.getRandomString(20);

	@Order(0)
	@Rollback(value = false)
	@Test
	public void insertUserTest() throws SQLException {
		UserPO userPo = UserPO.builder()
			.openId(openId)
			.school(school)
			.institute(institute)
			.major(major)
			.grade(grade)
			.nickName(nickName)
			.avatarUrl(avatarUrl)
			.nickName(nickName)
			.build();
		int i = userMapper.insertUser(userPo);
		assertTrue(i > 0);
		userId = userPo.getId();
		assertNotNull(userId);
	}

	@Order(0)
	@Rollback(value = false)
	@Test
	public void insertManagerTest() {
		ManagerVO managerVo = ManagerVO.builder()
			.name(managerName)
			.nickName(managerNickName)
			.post(post)
			.password(password)
			.role(role)
			.school(school)
			.institute(institute)
			.major(major)
			.grade(grade)
			.build();

		ManagerVO manager = managerService.register(managerVo);
		assertNotNull(manager);
		managerId = manager.getId();
	}

	@Order(1)
	@Rollback(value = false)
	@Test
	public void insertBookingTest() {
		ManagerNucleicAcidBookingVO bookingVo = ManagerNucleicAcidBookingVO.builder()
			.managerId(managerId)
			.title(title)
			.finishStatus(finishStatus)
			.deadLine(deadLine)
			.build();

		ManagerNucleicAcidBookingVO booking = bookingService.insertBookingInfo(managerId, bookingVo);
		assertNotNull(booking);
		id = bookingVo.getId();
	}

	@Order(2)
	@Test
	public void queryInfoByIdTest() {
		ManagerNucleicAcidBookingVO bookingVo = bookingService.queryBookingInfoById(id);
		assertNotNull(bookingVo);
		assertEquals(title, bookingVo.getTitle());
		assertEquals(finishStatus.getValue(), bookingVo.getFinishStatus());
		assertEquals(managerId, bookingVo.getManagerId());
	}

	@Order(2)
	@Test
	public void queryBookingTest() {
		List<NucleicAcidBookingPO> bookingPoList = bookingMapper.queryBookingByUserId(userId);
		assertTrue(bookingPoList.size() > 0);
		NucleicAcidBookingPO bookingPo = bookingPoList.get(0);
		assertEquals(userId, bookingPo.getUserId());
		assertEquals(title, bookingPo.getTitle());
		assertEquals(finishStatus.getValue(), bookingPo.getFinishStatus());
	}

	@Order(2)
	@Test
	public void queryInfoByManagerIdTest() {
		List<ManagerNucleicAcidBookingVO> bookingList = bookingService.queryManagerBookingInfoList(managerId);
		assertTrue(bookingList.size() > 0);
		ManagerNucleicAcidBookingVO bookingVo = bookingList.get(0);

		assertNotNull(bookingVo);
		assertEquals(title, bookingVo.getTitle());
		assertEquals(finishStatus.getValue(), bookingVo.getFinishStatus());
	}

	@Order(3)
	@Test
	public void updateInfoTest() {
		String oldTitle = title;
		title = RandomTestUtil.getRandomString(30);
		finishStatus = FinishStatus.DONE;

		ManagerNucleicAcidBookingVO bookingVo = ManagerNucleicAcidBookingVO.builder()
			.id(id)
			.title(title)
			.finishStatus(finishStatus)
			.build();

		ManagerNucleicAcidBookingVO booking = bookingService.updateBookingInfo(managerId, bookingVo, oldTitle);
		assertNotNull(booking);

		assertNotNull(booking);
		assertEquals(title, booking.getTitle());
		assertEquals(finishStatus.getValue(), booking.getFinishStatus());
		assertEquals(managerId, booking.getManagerId());
	}

	@Order(4)
	@Rollback(value = false)
	@Test
	public void deleteInfoTest() {
		boolean isDeleted = bookingService.deleteBookingInfoById(managerId, id);
		assertTrue(isDeleted);
	}

	@Order(4)
	@Rollback(value = false)
	@Test
	public void deleteUserTest() {
		int i = userMapper.deleteUserById(userId);
		assertTrue(i > 0);
	}

	@Order(4)
	@Rollback(value = false)
	@Test
	public void deleteMangerTest() {
		boolean isDeleted = managerService.deleteManager(managerId);
		assertTrue(isDeleted);
	}

	@Order(5)
	@Test
	public void checkDeleteBookingTest() {
		List<NucleicAcidBookingPO> bookingPoList = bookingMapper.queryBookingByUserId(userId);
		assertEquals(0, bookingPoList.size());
	}
}
