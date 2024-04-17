package com.weixin.njuteam.dao;

import com.weixin.njuteam.entity.po.UserPO;
import com.weixin.njuteam.util.RandomTestUtil;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
@Rollback
public class UserMapperTest {

	private static Long id;
	private static String openId = RandomTestUtil.getRandomString(50);
	private static String nickName = RandomTestUtil.getRandomString(30);
	private static String name = RandomTestUtil.getRandomString(10);
	private static String school = RandomTestUtil.getRandomString(30);
	private static String institute = RandomTestUtil.getRandomString(30);
	private static String major = RandomTestUtil.getRandomString(30);
	private static String grade = RandomTestUtil.getRandomString(10);
	private static String gender = "男";
	private static String avatarUrl = RandomTestUtil.getRandomString(150);

	private UserMapper userMapper;

	@Autowired
	public void setUserMapper(UserMapper userMapper) {
		this.userMapper = userMapper;
	}

	@Test
	@Order(0)
	@Rollback(value = false)
	public void insertTest() {
		UserPO userPo = UserPO.builder()
			.openId(openId)
			.nickName(nickName)
			.name(name)
			.school(school)
			.institute(institute)
			.major(major)
			.grade(grade)
			.gender(gender)
			.avatarUrl(avatarUrl)
			.build();

		int i = userMapper.insertUser(userPo);

		assertTrue(i > 0);
		id = userPo.getId();
	}

	@Test
	@Order(1)
	public void queryUserByIdTest() {
		UserPO user = userMapper.queryUserById(id);

		assertEquals(openId, user.getOpenId());
		assertEquals(nickName, user.getNickName());
		assertEquals(name, user.getName());
		assertEquals(school, user.getSchool());
		assertEquals(institute, user.getInstitute());
		assertEquals(major, user.getMajor());
		assertEquals(grade, user.getGrade());
		assertEquals(gender, user.getGender());
		assertEquals(avatarUrl, user.getAvatarUrl());
	}

	@Test
	@Order(1)
	public void queryUserByOpenIdTest() {
		UserPO user = userMapper.queryUserByOpenId(openId);

		assertEquals(id, user.getId());
		assertEquals(nickName, user.getNickName());
		assertEquals(name, user.getName());
		assertEquals(school, user.getSchool());
		assertEquals(institute, user.getInstitute());
		assertEquals(major, user.getMajor());
		assertEquals(grade, user.getGrade());
		assertEquals(gender, user.getGender());
		assertEquals(avatarUrl, user.getAvatarUrl());
	}

	@Test
	@Order(2)
	public void updateUserTest() {
		nickName = RandomTestUtil.getRandomString(30);
		name = RandomTestUtil.getRandomString(10);
		school = RandomTestUtil.getRandomString(30);
		institute = RandomTestUtil.getRandomString(30);
		major = RandomTestUtil.getRandomString(30);
		grade = RandomTestUtil.getRandomString(10);
		gender = "女";
		avatarUrl = RandomTestUtil.getRandomString(150);

		UserPO user = UserPO.builder()
			.id(id)
			.openId(openId)
			.nickName(nickName)
			.name(name)
			.school(school)
			.institute(institute)
			.major(major)
			.grade(grade)
			.gender(gender)
			.avatarUrl(avatarUrl)
			.build();

		int i = userMapper.updateUser(user);

		assertTrue(i > 0);
		assertEquals(id, user.getId());
		assertEquals(nickName, user.getNickName());
		assertEquals(name, user.getName());
		assertEquals(school, user.getSchool());
		assertEquals(institute, user.getInstitute());
		assertEquals(major, user.getMajor());
		assertEquals(grade, user.getGrade());
		assertEquals(gender, user.getGender());
		assertEquals(avatarUrl, user.getAvatarUrl());
	}

	@Test
	@Order(3)
	@Rollback(value = false)
	public void deleteUserTest() {
		int i = userMapper.deleteUser(openId);

		assertTrue(i > 0);
	}
}
