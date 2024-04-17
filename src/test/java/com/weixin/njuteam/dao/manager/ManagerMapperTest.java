package com.weixin.njuteam.dao.manager;

import com.weixin.njuteam.dao.UserMapper;
import com.weixin.njuteam.dao.manager.ManagerMapper;
import com.weixin.njuteam.entity.po.UserPO;
import com.weixin.njuteam.entity.po.manager.ManagerAndStudentPO;
import com.weixin.njuteam.entity.po.manager.ManagerPO;
import com.weixin.njuteam.entity.po.manager.Student;
import com.weixin.njuteam.enums.Role;
import com.weixin.njuteam.util.RandomTestUtil;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
@Rollback
public class ManagerMapperTest {

	private ManagerMapper managerMapper;
	private UserMapper userMapper;

	@Autowired
	public void setManagerMapper(ManagerMapper managerMapper) {
		this.managerMapper = managerMapper;
	}

	@Autowired
	public void setUserMapper(UserMapper userMapper) {
		this.userMapper = userMapper;
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
	public void insertUserTest() {
		UserPO userPo = UserPO.builder()
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

		int i = userMapper.insertUser(userPo);

		assertTrue(i > 0);
		userId = userPo.getId();
	}

	@Order(0)
	@Rollback(value = false)
	@Test
	public void insertManagerTest() {
		ManagerPO managerPo = ManagerPO.builder()
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

		int i = managerMapper.insertManager(managerPo);
		assertTrue(i > 0);
		managerId = managerPo.getId();
	}

	@Order(1)
	@Test
	public void queryManagerTest() {
		ManagerPO managerPo = managerMapper.queryManagerById(managerId);

		assertNotNull(managerPo);
		assertEquals(managerId, managerPo.getId());
		assertEquals(managerName, managerPo.getName());
		assertEquals(managerNickName, managerPo.getNickName());
		assertEquals(post, managerPo.getPost());
		assertEquals(role.getValue(), managerPo.getRole());
		assertEquals(school, managerPo.getSchool());
		assertEquals(institute, managerPo.getInstitute());
		assertEquals(major, managerPo.getMajor());
		assertEquals(grade, managerPo.getGrade());
	}

	@Order(1)
	@Test
	public void queryManagerAndStudentTest() {
		ManagerAndStudentPO managerAndStudent = managerMapper.queryManagerAndStudent(managerId);
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

		ManagerPO managerPo = ManagerPO.builder()
			.id(managerId)
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

		int i = managerMapper.updateManager(managerPo);
		assertTrue(i > 0);
		assertNotNull(managerPo);
		assertEquals(managerId, managerPo.getId());
		assertEquals(managerName, managerPo.getName());
		assertEquals(managerNickName, managerPo.getNickName());
		assertEquals(password, managerPo.getPassword());
		assertEquals(post, managerPo.getPost());
		assertEquals(role.getValue(), managerPo.getRole());
		assertEquals(school, managerPo.getSchool());
		assertEquals(institute, managerPo.getInstitute());
		assertEquals(major, managerPo.getMajor());
		assertEquals(grade, managerPo.getGrade());
	}

	@Order(3)
	@Rollback(value = false)
	@Test
	public void deleteManagerTest() {
		int i = managerMapper.deleteManager(managerId);
		assertTrue(i > 0);
	}

	@Order(3)
	@Rollback(value = false)
	@Test
	public void deleteUserTest() {
		int i = userMapper.deleteUserById(userId);
		assertTrue(i > 0);
	}
}
