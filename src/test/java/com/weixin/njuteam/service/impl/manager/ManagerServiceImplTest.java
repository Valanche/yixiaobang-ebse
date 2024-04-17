package com.weixin.njuteam.service.impl.manager;

import com.weixin.njuteam.entity.po.manager.ManagerAndStudentPO;
import com.weixin.njuteam.entity.po.manager.ManagerPO;
import com.weixin.njuteam.entity.po.manager.Student;
import com.weixin.njuteam.entity.vo.UserVO;
import com.weixin.njuteam.entity.vo.manager.ManagerAndStudentVO;
import com.weixin.njuteam.entity.vo.manager.ManagerVO;
import com.weixin.njuteam.enums.Role;
import com.weixin.njuteam.service.ManagerService;
import com.weixin.njuteam.service.UserService;
import com.weixin.njuteam.util.EncryptionUtil;
import com.weixin.njuteam.util.RandomTestUtil;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Zyi
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
@Rollback
public class ManagerServiceImplTest {

	private ManagerService managerService;
	private UserService userService;

	@Autowired
	public void setManagerService(ManagerService managerService) {
		this.managerService = managerService;
	}

	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	private static Long managerId;
	private static String managerName = RandomTestUtil.getRandomString(20);
	private static String managerNickName = RandomTestUtil.getRandomString(20);
	private static String password = RandomTestUtil.getRandomString(50);
	private static Role role = Role.COMMON_MANAGER;
	private static String post = RandomTestUtil.getRandomString(20);

	private static Long userId;
	private static String openId = RandomTestUtil.getRandomString(50);
	private static String userNickName = RandomTestUtil.getRandomString(30);
	private static String userName = RandomTestUtil.getRandomString(10);
	private static String school = RandomTestUtil.getRandomString(30);
	private static String institute = RandomTestUtil.getRandomString(30);
	private static String major = RandomTestUtil.getRandomString(30);
	private static String grade = RandomTestUtil.getRandomString(10);
	private static String gender = "男";
	private static String avatarUrl = RandomTestUtil.getRandomString(150);

	@Order(0)
	@Rollback(value = false)
	@Test
	public void insertUserTest() throws SQLException {
		UserVO userVo = UserVO.builder()
			.openId(openId)
			.nickName(userNickName)
			.name(userName)
			.school(school)
			.institute(institute)
			.major(major)
			.grade(grade)
			.gender(gender)
			.avatarUrl(avatarUrl)
			.build();

		UserVO user = userService.authLogin(userVo);

		assertNotNull(user);
		userId = user.getId();
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
	@Test
	public void queryManagerTest() {
		ManagerVO managerVo = managerService.queryManagerById(managerId);

		assertNotNull(managerVo);
		assertEquals(managerId, managerVo.getId());
		assertEquals(managerName, managerVo.getName());
		assertEquals(managerNickName, managerVo.getNickName());
		assertEquals(post, managerVo.getPost());
		assertEquals(role.getValue(), managerVo.getRole());
		assertEquals(school, managerVo.getSchool());
		assertEquals(institute, managerVo.getInstitute());
		assertEquals(major, managerVo.getMajor());
		assertEquals(grade, managerVo.getGrade());
	}

	@Order(1)
	@Test
	public void queryManagerAndStudentTest() {
		ManagerAndStudentVO managerAndStudent = managerService.queryStudentList(managerId);
		List<Student> studentList = managerAndStudent.getStudentList();
		Student student = studentList.get(0);

		assertEquals(managerId, managerAndStudent.getManagerId());
		assertEquals(school, managerAndStudent.getSchool());
		assertEquals(institute, managerAndStudent.getInstitute());
		assertEquals(major, managerAndStudent.getMajor());
		assertEquals(grade, managerAndStudent.getGrade());
		assertEquals(userId, student.getUserId());
		assertEquals(userName, student.getName());
		assertEquals(school, student.getSchool());
		assertEquals(institute, student.getInstitute());
		assertEquals(major, student.getMajor());
		assertEquals(grade, student.getGrade());
	}

	@Order(2)
	@Test
	public void updateManagerTest() {
		managerName = RandomTestUtil.getRandomString(20);
		managerNickName = RandomTestUtil.getRandomString(20);
		password = RandomTestUtil.getRandomString(50);
		role = Role.COMMON_MANAGER;
		post = RandomTestUtil.getRandomString(20);

		ManagerVO managerVo = ManagerVO.builder()
			.name(managerName)
			.nickName(managerNickName)
			.password(password)
			.post(post)
			.role(role)
			.school(school)
			.institute(institute)
			.major(major)
			.grade(grade)
			.build();

		ManagerVO manager = managerService.updateManagerInfo(managerId, managerVo);
		assertNotNull(manager);
		assertEquals(managerId, manager.getId());
		assertEquals(managerName, manager.getName());
		assertEquals(managerNickName, manager.getNickName());
		assertEquals(password, manager.getPassword());
		assertEquals(post, manager.getPost());
		assertEquals(role.getValue(), manager.getRole());
		assertEquals(school, manager.getSchool());
		assertEquals(institute, manager.getInstitute());
		assertEquals(major, manager.getMajor());
		assertEquals(grade, manager.getGrade());
	}

	@Order(3)
	@Rollback(value = false)
	@Test
	public void deleteUserTest() {
		boolean isDeleted = userService.deleteUserById(userId);
		assertTrue(isDeleted);
	}

	@Order(3)
	@Rollback(value = false)
	@Test
	public void deleteMangerTest(){
		boolean isDeleted = managerService.deleteManager(managerId);
		assertTrue(isDeleted);
	}
}
