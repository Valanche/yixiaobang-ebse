package com.weixin.njuteam.web.controller.nucleic;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.weixin.njuteam.entity.vo.nucleic.NucleicAcidBookingVO;
import com.weixin.njuteam.entity.vo.nucleic.NucleicAcidTestingVO;
import com.weixin.njuteam.enums.FinishStatus;
import com.weixin.njuteam.enums.StatusCode;
import com.weixin.njuteam.util.RandomTestUtil;
import org.junit.jupiter.api.*;
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
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
@Rollback
public class NucleicAcidTestingControllerTest {

	private static Long id;
	private static Long userId = RandomTestUtil.getRandomId();
	private static Long managerId = RandomTestUtil.getRandomId();
	private static String require = RandomTestUtil.getRandomString(30);
	private static String title = RandomTestUtil.getRandomString(30);
	private static Date startTime = RandomTestUtil.getRandomDate("2000-01-01", "2012-04-22");
	private static Date endTime = RandomTestUtil.getRandomDate("2012-04-23", "2022-04-23");
	private static boolean isOpenRemind = false;
	private static String place = RandomTestUtil.getRandomString(50);
	private static FinishStatus finishStatus = FinishStatus.IN_PROGRESS;

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
	@Order(0)
	@Rollback(value = false)
	public void insertTest() throws Exception {
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

		String jsonStr = JSON.toJSONString(testingVo);

		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/nucleic-acid/testing/insert")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonStr))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn();

		//得到返回代码
		int status = mvcResult.getResponse().getStatus();
		//得到返回结果
		String content = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
		assertEquals(StatusCode.OK, status);
		String data = getData(content);
		NucleicAcidBookingVO newBookingVo = JSONObject.parseObject(data, NucleicAcidBookingVO.class);
		id = newBookingVo.getId();
	}

	@Test
	@Order(1)
	public void queryTest() throws Exception {
		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/nucleic-acid/testing/query/{id}", id)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn();

		//得到返回代码
		int statusCode = mvcResult.getResponse().getStatus();
		//得到返回结果
		String returnContent = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
		assertEquals(StatusCode.OK, statusCode);
		String data = getData(returnContent);
		NucleicAcidTestingVO testingVo = JSONObject.parseObject(data, NucleicAcidTestingVO.class);

		assertNotNull(testingVo);
		assertEquals(userId, testingVo.getUserId());
		assertEquals(require, testingVo.getRequire());
		assertEquals(title, testingVo.getTitle());
		assertEquals(isOpenRemind, testingVo.getIsOpenRemind());
		assertEquals(place, testingVo.getPlace());
		assertEquals(finishStatus.getValue(), testingVo.getFinishStatus());
	}

	@Test
	@Order(2)
	@Rollback(value = false)
	public void deleteTest() throws Exception {
		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.delete("/api/nucleic-acid/testing/delete/{id}", id)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn();

		//得到返回代码
		int status = mvcResult.getResponse().getStatus();
		assertEquals(StatusCode.OK, status);
	}

	private String getData(String content) {
		String[] contents = content.split("\\{");
		String data = "{" + contents[2];

		return data.substring(0, data.length() - 1);
	}
}
