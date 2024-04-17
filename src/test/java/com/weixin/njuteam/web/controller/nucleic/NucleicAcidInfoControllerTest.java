package com.weixin.njuteam.web.controller.nucleic;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.qcloud.cos.utils.IOUtils;
import com.weixin.njuteam.entity.vo.UserVO;
import com.weixin.njuteam.entity.vo.nucleic.NucleicAcidBookingVO;
import com.weixin.njuteam.entity.vo.nucleic.NucleicAcidInfoVO;
import com.weixin.njuteam.entity.vo.nucleic.RecognizeResultVO;
import com.weixin.njuteam.enums.FinishStatus;
import com.weixin.njuteam.enums.StatusCode;
import com.weixin.njuteam.util.FileConvertUtils;
import com.weixin.njuteam.util.RandomTestUtil;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
@Rollback
public class NucleicAcidInfoControllerTest {

	private static Long id;
	private static Long userId = RandomTestUtil.getRandomId();

	private static Long managerId = RandomTestUtil.getRandomId();
	private static Date deadLine = RandomTestUtil.getRandomDate("2000-01-01", "2020-04-21");
	private static String title = RandomTestUtil.getRandomString(50);
	private static FinishStatus status = FinishStatus.IN_PROGRESS;
	private static Boolean isOpenRemind = false;

	private WebApplicationContext webApplicationContext;
	private MockMvc mockMvc;

	private static String token;

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
		NucleicAcidInfoVO infoVo = NucleicAcidInfoVO.builder()
			.userId(userId)
			.managerId(managerId)
			.deadLine(deadLine)
			.title(title)
			.status(status)
			.isOpenRemind(isOpenRemind)
			.build();

		String jsonStr = JSON.toJSONString(infoVo);

		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/nucleic-acid/info/insert")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonStr))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn();

		//得到返回代码
		int status = mvcResult.getResponse().getStatus();
		//得到返回结果
		String content = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
		System.err.println(content);
		assertEquals(StatusCode.OK, status);
		String data = getData(content);
		NucleicAcidBookingVO newBookingVo = JSONObject.parseObject(data, NucleicAcidBookingVO.class);
		id = newBookingVo.getId();
	}

	@Test
	@Order(1)
	@Rollback(value = false)
	public void queryTest() throws Exception {
		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/nucleic-acid/info/query/{id}", id)
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
		NucleicAcidInfoVO info = JSONObject.parseObject(data, NucleicAcidInfoVO.class);

		assertEquals(userId, info.getUserId());
		assertEquals(title, info.getTitle());
		assertEquals(status.getValue(), info.getStatus());
		assertEquals(isOpenRemind, info.getIsOpenRemind());
	}

//	@Test
//	@Order(2)
//	@Rollback(value = false)
//	public void loginTest() throws Exception {
//
//		String openId = RandomTestUtil.getRandomString(10);
//		UserVO userVO = UserVO.builder()
//			.id(id)
//			.name(RandomTestUtil.getRandomString(10))
//			.major(RandomTestUtil.getRandomString(10))
//			.openId(openId)
//			.nickName(RandomTestUtil.getRandomString(10))
//			.gender("男")
//			.grade("2")
//			.school(RandomTestUtil.getRandomString(10))
//			.institute(RandomTestUtil.getRandomString(10))
//			.build();
//
//		String jsonStr = JSON.toJSONString(userVO);
//
//		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/user/login")
//				.accept(MediaType.APPLICATION_JSON)
//				.contentType(MediaType.APPLICATION_JSON)
//				.content(jsonStr))
//			.andExpect(MockMvcResultMatchers.status().isOk())
//			.andReturn();
//
//		int status = mvcResult.getResponse().getStatus();
//		//得到返回结果
//		String content = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
//		System.err.println(content);
//		assertEquals(StatusCode.OK, status);
//		String data = getData(content);
////		System.err.println(data);
//		token = data.substring(data.indexOf(':') + 2, data.length() - 2);
////		System.err.println(token);
//	}
//
//
//	@Test
//	@Order(3)
//	public void imageTest() throws Exception {
//		File imgFile = new File("src/main/resources/images/image.jpg");
//		MultipartFile multipartFile = FileConvertUtils.getMulFileByFile(imgFile);
//
//		MockMultipartFile image = new MockMultipartFile("image","src/main/resources/images/image.jpg",MediaType.IMAGE_JPEG_VALUE,multipartFile.getBytes());
//
//		Long infoId = id;
//		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.multipart("/api/nucleic-acid/info/upload/{infoId}", infoId)
//				.file(image)
//				.header("Authorization", token)
//				.accept(MediaType.APPLICATION_JSON)
//
//			)
//			.andExpect(MockMvcResultMatchers.status().isOk())
//			.andReturn();
//
//		int status = mvcResult.getResponse().getStatus();
//		//得到返回结果
//		String content = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
//		assertEquals(StatusCode.OK, status);
//		String data = getData(content);
//		RecognizeResultVO recognizeResultVO = JSONObject.parseObject(data, RecognizeResultVO.class);
//	}

	@Test
	@Order(4)
	@Rollback(value = false)
	public void deleteTest() throws Exception {
		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.delete("/api/nucleic-acid/info/delete/{id}", id)
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
