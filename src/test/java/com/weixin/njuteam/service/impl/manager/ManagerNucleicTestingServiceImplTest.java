package com.weixin.njuteam.service.impl.manager;

import com.weixin.njuteam.dao.UserMapper;
import com.weixin.njuteam.dao.nucleic.NucleicAcidBookingMapper;
import com.weixin.njuteam.dao.nucleic.NucleicAcidTestingMapper;
import com.weixin.njuteam.entity.po.UserPO;
import com.weixin.njuteam.entity.po.nucleic.NucleicAcidBookingPO;
import com.weixin.njuteam.entity.po.nucleic.NucleicAcidTestingPO;
import com.weixin.njuteam.entity.vo.manager.ManagerVO;
import com.weixin.njuteam.entity.vo.manager.nucleic.ManagerNucleicAcidBookingVO;
import com.weixin.njuteam.entity.vo.manager.nucleic.ManagerNucleicAcidTestingVO;
import com.weixin.njuteam.enums.FinishStatus;
import com.weixin.njuteam.enums.Role;
import com.weixin.njuteam.service.ManagerNucleicAcidBookingService;
import com.weixin.njuteam.service.ManagerNucleicAcidTestingService;
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
public class ManagerNucleicTestingServiceImplTest {

	private ManagerNucleicAcidTestingService testingService;
	private UserMapper userMapper;
	private ManagerService managerService;
	private NucleicAcidTestingMapper testingMapper;
	private ManagerNucleicAcidBookingService bookingService;

	@Autowired
	public void setTestingService(ManagerNucleicAcidTestingService testingService) {
		this.testingService = testingService;
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
	public void setBookingMapper(ManagerNucleicAcidBookingService bookingService) {
		this.bookingService = bookingService;
	}

	@Autowired
	public void setTestingMapper(NucleicAcidTestingMapper testingMapper) {
		this.testingMapper = testingMapper;
	}

	private static Long id;
	private static Long managerId;
	private static String title = RandomTestUtil.getRandomString(30);
	private static FinishStatus finishStatus = FinishStatus.IN_PROGRESS;
	private static String require = RandomTestUtil.getRandomString(20);
	private static String place = RandomTestUtil.getRandomString(20);
	private static Date startTime = RandomTestUtil.getRandomDate("2020-05-01", "2021-05-01");
	private static Date endTime = RandomTestUtil.getRandomDate("2023-05-01", "2024-07-20");

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

	private static Long bookingId;

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
	public void insertTestingInfo() {
		ManagerNucleicAcidTestingVO testingVo = ManagerNucleicAcidTestingVO.builder()
			.managerId(managerId)
			.title(title)
			.finishStatus(finishStatus)
			.require(require)
			.place(place)
			.startTime(startTime)
			.endTime(endTime)
			.build();

		ManagerNucleicAcidTestingVO testing = testingService.insertTestingInfo(managerId, testingVo);
		assertNotNull(testing);
		id = testingVo.getId();
	}

	@Order(2)
	@Test
	public void queryTestingInfoById() {
		ManagerNucleicAcidTestingVO testingVo = testingService.queryTestingInfoById(id);
		assertNotNull(testingVo);
		assertEquals(id, testingVo.getId());
		assertEquals(title, testingVo.getTitle());
		assertEquals(require, testingVo.getRequire());
		assertEquals(place, testingVo.getPlace());
		assertEquals(finishStatus.getValue(), testingVo.getFinishStatus());
		assertEquals(managerId, testingVo.getManagerId());
	}

	@Order(2)
	@Test
	public void queryUserTestingTest() throws SQLException {
		List<NucleicAcidTestingPO> testingPoList = testingMapper.queryTestingByUserId(userId);
		assertTrue(testingPoList.size() > 0);
		NucleicAcidTestingPO testingPo = testingPoList.get(0);
		assertEquals(userId, testingPo.getUserId());
		assertEquals(title, testingPo.getTitle());
		assertEquals(require, testingPo.getRequire());
		assertEquals(place, testingPo.getPlace());
		assertEquals(finishStatus.getValue(), testingPo.getFinishStatus());
	}

	@Order(2)
	@Test
	public void queryTestingInfoByManagerIdTest() {
		List<ManagerNucleicAcidTestingVO> testingList = testingService.queryManagerTestingInfoList(managerId);
		assertTrue(testingList.size() > 0);
		ManagerNucleicAcidTestingVO testingVo = testingList.get(0);

		assertNotNull(testingVo);
		assertEquals(id, testingVo.getId());
		assertEquals(title, testingVo.getTitle());
		assertEquals(require, testingVo.getRequire());
		assertEquals(place, testingVo.getPlace());
		assertEquals(finishStatus.getValue(), testingVo.getFinishStatus());
		assertEquals(managerId, testingVo.getManagerId());
	}

	@Order(2)
	@Test
	public void queryBookingInfoTest() {
		List<ManagerNucleicAcidBookingVO> bookingList = bookingService.queryManagerBookingInfoList(managerId);
		assertTrue(bookingList.size() > 0);
		ManagerNucleicAcidBookingVO bookingVo = bookingList.get(0);

		assertNotNull(bookingVo);
		assertEquals(title, bookingVo.getTitle());
		assertEquals(finishStatus.getValue(), bookingVo.getFinishStatus());
	}

	@Order(3)
	@Test
	public void updateTestingInfoTest() {
		String oldTitle = title;
		title = RandomTestUtil.getRandomString(30);
		require = RandomTestUtil.getRandomString(30);
		place = RandomTestUtil.getRandomString(20);
		finishStatus = FinishStatus.DONE;

		ManagerNucleicAcidTestingVO testingVo = ManagerNucleicAcidTestingVO.builder()
			.id(id)
			.managerId(managerId)
			.title(title)
			.finishStatus(finishStatus)
			.require(require)
			.place(place)
			.build();

		ManagerNucleicAcidTestingVO testing = testingService.updateTestingInfo(managerId, testingVo, oldTitle);
		assertNotNull(testing);

		assertNotNull(testing);
		assertEquals(id, testing.getId());
		assertEquals(title, testing.getTitle());
		assertEquals(require, testing.getRequire());
		assertEquals(place, testing.getPlace());
		assertEquals(finishStatus.getValue(), testing.getFinishStatus());
		assertEquals(managerId, testing.getManagerId());
	}

	@Order(4)
	@Rollback(value = false)
	@Test
	public void deleteTestingInfoTest() {
		boolean isDeleted = testingService.deleteTestingInfoById(managerId, id);
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
	@Rollback(value = false)
	@Test
	public void deleteBookingTest() {
		boolean isDeleted = bookingService.deleteManagerAllBookingInfo(managerId);
		assertTrue(isDeleted);
	}

	@Order(5)
	@Test
	public void checkDeleteTestingTest() throws SQLException {
		List<NucleicAcidTestingPO> testingPoList = testingMapper.queryTestingByUserId(userId);
		assertEquals(0, testingPoList.size());
	}
}
