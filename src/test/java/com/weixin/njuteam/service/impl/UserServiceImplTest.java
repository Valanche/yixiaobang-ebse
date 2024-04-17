package com.weixin.njuteam.service.impl;

import com.weixin.njuteam.entity.vo.UserVO;
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

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Rollback
@Transactional
public class UserServiceImplTest {

	private static long id;
	private static String openId = RandomTestUtil.getRandomString(20);
	private static String avatarUrl = RandomTestUtil.getRandomString(150);
	private static String nickName = RandomTestUtil.getRandomString(20);

	private UserService userService;

	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	@Test
	@Order(0)
	@Rollback(value = false)
	public void insertTest() throws SQLException {
		UserVO userVO = UserVO.builder()
			.openId(openId)
			.avatarUrl(avatarUrl)
			.nickName(nickName)
			.build();
		UserVO info = userService.authLogin(userVO);

		assertEquals(openId, info.getOpenId());
		assertEquals(avatarUrl, info.getAvatarUrl());
		assertEquals(nickName, info.getNickName());

		id = info.getId();
	}

	@Test
	@Order(1)
	public void queryTest() {
		UserVO user = userService.queryUser(openId);

		assertEquals(id, user.getId());
		assertEquals(openId, user.getOpenId());
		assertEquals(avatarUrl, user.getAvatarUrl());
		assertEquals(nickName, user.getNickName());
	}

	@Test
	@Order(2)
	public void updateTest() {
		avatarUrl = RandomTestUtil.getRandomString(160);
		nickName = RandomTestUtil.getRandomString(20);

		UserVO newUser = UserVO.builder()
			.avatarUrl(avatarUrl)
			.nickName(nickName)
			.openId(openId)
			.build();

		UserVO userVo = userService.updateUser(newUser);

		assertNotNull(userVo);
		assertEquals(openId, newUser.getOpenId());
		assertEquals(avatarUrl, newUser.getAvatarUrl());
		assertEquals(nickName, newUser.getNickName());
	}

	@Test
	@Order(3)
	@Rollback(value = false)
	public void deleteTest() {
		assertTrue(userService.deleteUser(openId));
	}
}
