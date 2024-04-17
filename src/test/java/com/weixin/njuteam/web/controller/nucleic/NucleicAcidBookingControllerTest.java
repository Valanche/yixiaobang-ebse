package com.weixin.njuteam.web.controller.nucleic;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.weixin.njuteam.entity.vo.nucleic.NucleicAcidBookingVO;
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Rollback
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class NucleicAcidBookingControllerTest {

	private static Long id;
	private static Long userId = RandomTestUtil.getRandomId();
	private static Long managerId = RandomTestUtil.getRandomId();
	private static String title = RandomTestUtil.getRandomString(30);
	private static Date deadLine = RandomTestUtil.getRandomDate("1500-01-01", "2022-04-21");
	private static Boolean isOpenRemind = false;
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
		NucleicAcidBookingVO bookingVO = NucleicAcidBookingVO.builder()
			.userId(userId)
			.managerId(managerId)
			.title(title)
			.deadLine(deadLine)
			.isOpenRemind(isOpenRemind)
			.finishStatus(finishStatus)
			.build();

		String jsonStr = JSON.toJSONString(bookingVO);

		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/nucleic-acid/booking/insert")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonStr))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn();

		//得到返回代码
		int status = mvcResult.getResponse().getStatus();
		//得到返回结果
		String content = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
		Assertions.assertEquals(StatusCode.OK, status);
		String data = getData(content);
		NucleicAcidBookingVO newBookingVo = JSONObject.parseObject(data, NucleicAcidBookingVO.class);
		id = newBookingVo.getId();
	}

	@Test
	@Order(1)
	public void queryTest() throws Exception {
		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/nucleic-acid/booking/query/{id}", id)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn();

		//得到返回代码
		int status = mvcResult.getResponse().getStatus();
		//得到返回结果
		String content = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
		Assertions.assertEquals(StatusCode.OK, status);
		String data = getData(content);
		NucleicAcidBookingVO bookingVo = JSONObject.parseObject(data, NucleicAcidBookingVO.class);

		assertEquals(userId, bookingVo.getUserId());
		assertEquals(title, bookingVo.getTitle());
		// assertEquals(deadLine.toString(), bookingVo.getDeadLine().toString());
		assertEquals(isOpenRemind, bookingVo.getIsOpenRemind());
		assertEquals(finishStatus.getValue(), bookingVo.getFinishStatus());
	}

	@Test
	@Order(2)
	@Rollback(value = false)
	public void deleteTest() throws Exception {
		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.delete("/api/nucleic-acid/booking/delete/{id}", id)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn();
	}

	private String getData(String content) {
		String[] contents = content.split("\\{");
		String data = "{" + contents[2];

		return data.substring(0, data.length() - 1);
	}
}
