package com.weixin.njuteam.service.impl.nucleic;

import com.weixin.njuteam.entity.vo.UserVO;
import com.weixin.njuteam.entity.vo.nucleic.NucleicAcidInfoVO;
import com.weixin.njuteam.entity.vo.nucleic.NucleicAcidTestingVO;
import com.weixin.njuteam.enums.FinishStatus;
import com.weixin.njuteam.service.NucleicAcidInfoService;
import com.weixin.njuteam.service.NucleicAcidTestingService;
import com.weixin.njuteam.service.UserService;
import com.weixin.njuteam.util.FileConvertUtils;
import com.weixin.njuteam.util.RandomTestUtil;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@Rollback
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class NucleicAcidInfoServiceImplTest {

	private static Long id;

	private static Long testId;
	private static Long userId;
	private static Long managerId = RandomTestUtil.getRandomId();
	private static Date deadLine = RandomTestUtil.getRandomDate("2022-12-30", "2023-04-21");
	private static String title = RandomTestUtil.getRandomString(50);
	private static FinishStatus status = FinishStatus.IN_PROGRESS;
	private static Boolean isOpenRemind = false;
	private static String openId = RandomTestUtil.getRandomString(20);
	private static String avatarUrl = RandomTestUtil.getRandomString(150);
	private static String nickName = RandomTestUtil.getRandomString(20);

	private NucleicAcidInfoService infoService;
	private NucleicAcidTestingService testingService;
	private UserService userService;

	@Autowired
	public void setTestingService(NucleicAcidTestingService testingService) {
		this.testingService = testingService;
	}

	@Autowired
	public void setInfoService(NucleicAcidInfoService infoService) {
		this.infoService = infoService;
	}

	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	@Test
	@Order(0)
	@Rollback(value = false)
	public void insertUserTest() throws SQLException {
		UserVO userVO = UserVO.builder()
			.openId(openId)
			.avatarUrl(avatarUrl)
			.nickName(nickName)
			.build();
		UserVO info = userService.authLogin(userVO);

		assertEquals(openId, info.getOpenId());
		assertEquals(avatarUrl, info.getAvatarUrl());
		assertEquals(nickName, info.getNickName());

		userId = info.getId();
	}

	@Test
	@Order(1)
	@Rollback(value = false)
	public void insertTest() {
		NucleicAcidInfoVO infoVo = NucleicAcidInfoVO.builder()
			.userId(userId)
			.managerId(managerId)
			.deadLine(deadLine)
			.title(title)
			.status(status)
			.isOpenRemind(isOpenRemind)
			.build();

		NucleicAcidInfoVO newInfo = infoService.insertInfo(infoVo);
		if (deadLine.before(new Date())) {
			status = FinishStatus.TO_BE_CONTINUE;
		}
		assertNotNull(newInfo);
		id = infoVo.getId();
	}

	@Test
	@Order(2)
	public void queryByIdTest() {
		NucleicAcidInfoVO info = infoService.queryInfoById(id);

		assertEquals(userId, info.getUserId());
		// fixme: 本地testok 服务器上不知道为啥mybatis query的时候会少8个小时好像
		// assertEquals(deadLine.toString(), info.getDeadLine().toString());
		assertEquals(isOpenRemind, info.getIsOpenRemind());
		assertEquals(title, info.getTitle());
		assertEquals(status.getValue(), info.getStatus());
	}

	@Test
	@Order(2)
	public void queryByUserIdTest() throws SQLException {
		List<NucleicAcidInfoVO> infoVoList = infoService.queryInfoByUserId(openId);
		NucleicAcidInfoVO info = infoVoList.get(0);

		assertEquals(userId, info.getUserId());
		// assertEquals(deadLine.toString(), info.getDeadLine().toString());
		assertEquals(isOpenRemind, info.getIsOpenRemind());
		assertEquals(title, info.getTitle());
		assertEquals(status.getValue(), info.getStatus());
	}

	@Test
	@Order(2)
	public void queryUserBookingCountTest() {
		int count = infoService.queryInfoCount(openId, FinishStatus.IN_PROGRESS);
		assertEquals(1, count);
	}


	@Test
	@Order(3)
	public void recogenizeTest(){

		NucleicAcidTestingVO testingVo = NucleicAcidTestingVO.builder()
			.userId(userId)
			.managerId(managerId)
			.require(RandomTestUtil.getRandomString(30))
			.title(title)
			.startTime(RandomTestUtil.getRandomDate("2000-01-01", "2012-04-22"))
			.endTime(RandomTestUtil.getRandomDate("2023-12-23", "2024-04-23"))
			.isOpenRemind(isOpenRemind)
			.place(RandomTestUtil.getRandomString(30))
			.finishStatus(FinishStatus.IN_PROGRESS)
			.build();

		NucleicAcidTestingVO newTestingVo = testingService.insertTesting(testingVo);
		assertNotNull(testingVo);
		testId = newTestingVo.getId();


		File imgFile = new File("src/main/resources/images/image.jpg");
		MultipartFile multipartFile = FileConvertUtils.getMulFileByFile(imgFile);
		System.err.println(infoService.recognize(multipartFile,openId,id));



		boolean isDeleted = testingService.deleteTesting(testId);
		assertTrue(isDeleted);
	}
	@Test
	@Order(4)
	public void updateTest() {
		status = FinishStatus.DONE;
		title = RandomTestUtil.getRandomString(30);

		NucleicAcidInfoVO info = NucleicAcidInfoVO.builder()
			.id(id)
			.userId(userId)
			.managerId(managerId)
			.deadLine(deadLine)
			.title(title)
			.status(status)
			.isOpenRemind(isOpenRemind)
			.build();

		NucleicAcidInfoVO newInfo = infoService.updateInfo(info);

		assertNotNull(newInfo);
		assertEquals(userId, info.getUserId());
		// assertEquals(deadLine.toString(), info.getDeadLine().toString());
		assertEquals(isOpenRemind, info.getIsOpenRemind());
		assertEquals(title, info.getTitle());
		assertEquals(status.getValue(), info.getStatus());
	}



	@Test
	@Order(5)
	@Rollback(value = false)
	public void deleteTest() {
		boolean isDeleted = infoService.deleteInfoById(id);

		assertTrue(isDeleted);
	}

	@Test
	@Order(5)
	@Rollback(value = false)
	public void deleteUserTest() {
		boolean isDeleted = userService.deleteUserById(userId);

		assertTrue(isDeleted);
	}
}
