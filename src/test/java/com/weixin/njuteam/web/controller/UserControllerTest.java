package com.weixin.njuteam.web.controller;

import com.alibaba.fastjson.JSON;
import com.weixin.njuteam.entity.vo.UserVO;
import com.weixin.njuteam.enums.StatusCode;
import com.weixin.njuteam.util.RandomTestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
@Rollback
public class UserControllerTest {
	private static Long id = RandomTestUtil.getRandomId();
	private static String name = RandomTestUtil.getRandomString(10);
	private static String major = RandomTestUtil.getRandomString(10);
	private static String openId = RandomTestUtil.getRandomString(30);

	private static String nickName = RandomTestUtil.getRandomString(20);
	private static String gender = "男";
	private static String grade = RandomTestUtil.getRandomString(1);
	private static String school = RandomTestUtil.getRandomString(10);
	private static String institute = RandomTestUtil.getRandomString(10);

	private static String token = null;
	private WebApplicationContext webApplicationContext;
	private MockMvc mockMvc;

	@Autowired
	public void setWebApplicationContext(WebApplicationContext webApplicationContext) {
		this.webApplicationContext = webApplicationContext;
	}

	@BeforeEach
	public void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	@Test
	public void loginTest() throws Exception{
		UserVO userVO = UserVO.builder()
			.id(id)
			.name(name)
			.major(major)
			.openId(openId)
			.nickName(nickName)
			.gender(gender)
			.grade(grade)
			.school(school)
			.institute(institute)
			.build();

		String jsonStr  = JSON.toJSONString(userVO);

		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/user/login")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON)
			.content(jsonStr))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn();

		int status = mvcResult.getResponse().getStatus();
		//得到返回结果
		String content = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
//		System.err.println(content);
		assertEquals(StatusCode.OK, status);
		String data = getData(content);
		System.err.println(data);
		token = data.substring(data.indexOf(':')+2,data.length()-2);
		System.err.println(token);

	}

	private String getData(String content) {
		String[] contents = content.split("\\{");
		String data = "{" + contents[2];

		return data.substring(0, data.length() - 1);
	}
}
