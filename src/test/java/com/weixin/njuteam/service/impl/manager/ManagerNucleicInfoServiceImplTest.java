package com.weixin.njuteam.service.impl.manager;

import com.weixin.njuteam.dao.UserMapper;
import com.weixin.njuteam.dao.nucleic.NucleicAcidInfoMapper;
import com.weixin.njuteam.entity.po.UserPO;
import com.weixin.njuteam.entity.po.nucleic.NucleicAcidInfoPO;
import com.weixin.njuteam.entity.vo.manager.ManagerVO;
import com.weixin.njuteam.entity.vo.manager.nucleic.ManagerNucleicAcidInfoVO;
import com.weixin.njuteam.enums.FinishStatus;
import com.weixin.njuteam.enums.Role;
import com.weixin.njuteam.service.ManagerNucleicAcidInfoService;
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
public class ManagerNucleicInfoServiceImplTest {

	private ManagerNucleicAcidInfoService infoService;
	private UserMapper userMapper;
	private ManagerService managerService;
	private NucleicAcidInfoMapper infoMapper;

	@Autowired
	public void setInfoService(ManagerNucleicAcidInfoService infoService) {
		this.infoService = infoService;
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
	public void setInfoMapper(NucleicAcidInfoMapper infoMapper) {
		this.infoMapper = infoMapper;
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
	public void insertInfoTest() {
		ManagerNucleicAcidInfoVO infoVo = ManagerNucleicAcidInfoVO.builder()
			.managerId(managerId)
			.title(title)
			.finishStatus(finishStatus)
			.deadLine(deadLine)
			.build();

		ManagerNucleicAcidInfoVO info = infoService.insertInfo(managerId, infoVo);
		assertNotNull(info);
		id = infoVo.getId();
	}

	@Order(2)
	@Test
	public void queryInfoByIdTest() {
		ManagerNucleicAcidInfoVO infoVo = infoService.queryInfoById(id);
		assertNotNull(infoVo);
		assertEquals(title, infoVo.getTitle());
		assertEquals(finishStatus.getValue(), infoVo.getFinishStatus());
		assertEquals(managerId, infoVo.getManagerId());
	}

	@Order(2)
	@Test
	public void queryBookingTest() throws SQLException {
		List<NucleicAcidInfoPO> infoPoList = infoMapper.queryInfoByUserId(userId);
		assertTrue(infoPoList.size() > 0);
		NucleicAcidInfoPO infoPo = infoPoList.get(0);
		assertEquals(userId, infoPo.getUserId());
		assertEquals(title, infoPo.getTitle());
		assertEquals(finishStatus.getValue(), infoPo.getStatus());
	}

	@Order(2)
	@Test
	public void queryInfoByManagerIdTest() {
		List<ManagerNucleicAcidInfoVO> infoList = infoService.queryManagerInfoList(managerId);
		assertTrue(infoList.size() > 0);
		ManagerNucleicAcidInfoVO infoVo = infoList.get(0);

		assertNotNull(infoVo);
		assertEquals(title, infoVo.getTitle());
		assertEquals(finishStatus.getValue(), infoVo.getFinishStatus());
		assertEquals(managerId, infoVo.getManagerId());
	}

	@Order(3)
	@Test
	public void updateInfoTest() {
		String oldTitle = title;
		title = RandomTestUtil.getRandomString(30);
		finishStatus = FinishStatus.DONE;

		ManagerNucleicAcidInfoVO bookingVo = ManagerNucleicAcidInfoVO.builder()
			.id(id)
			.title(title)
			.finishStatus(finishStatus)
			.build();

		ManagerNucleicAcidInfoVO booking = infoService.updateInfo(managerId, bookingVo, oldTitle);
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
		boolean isDeleted = infoService.deleteInfoById(managerId, id);
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
	public void checkDeleteBookingTest() throws SQLException {
		List<NucleicAcidInfoPO> bookingPoList = infoMapper.queryInfoByUserId(userId);
		assertEquals(0, bookingPoList.size());
	}
}
