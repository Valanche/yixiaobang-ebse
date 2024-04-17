package com.weixin.njuteam.dao.nucleic;

import com.weixin.njuteam.entity.po.InfoAssociatedTestingPO;
import com.weixin.njuteam.entity.po.nucleic.NucleicAcidInfoPO;
import com.weixin.njuteam.entity.po.nucleic.NucleicAcidTestingPO;
import com.weixin.njuteam.enums.FinishStatus;
import com.weixin.njuteam.util.RandomTestUtil;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
@Rollback
public class NucleicAcidInfoMapperTest {

	private static Date deadLine = RandomTestUtil.getRandomDate("2000-01-01", "2020-04-21");
	private static Long infoId;
	private static Long userId = RandomTestUtil.getRandomId();
	private static Long managerId = RandomTestUtil.getRandomId();
	private static String title = RandomTestUtil.getRandomString(50);
	private static FinishStatus status = FinishStatus.IN_PROGRESS;
	private static String imageName = RandomTestUtil.getRandomString(20);
	private static Boolean isInfoOpenRemind = false;
	private static Long testingId;
	private static String require = RandomTestUtil.getRandomString(30);
	private static Date startTime = RandomTestUtil.getRandomDate("2000-01-01", "2012-04-22");
	private static Date endTime = RandomTestUtil.getRandomDate("2012-04-23", "2022-04-23");
	private static boolean isTestingOpenRemind = false;
	private static String place = RandomTestUtil.getRandomString(50);
	private static FinishStatus finishStatus = FinishStatus.IN_PROGRESS;

	private NucleicAcidInfoMapper mapper;
	private NucleicAcidTestingMapper testingMapper;

	@Autowired
	public void setMapper(NucleicAcidInfoMapper mapper) {
		this.mapper = mapper;
	}

	@Autowired
	public void setTestingMapper(NucleicAcidTestingMapper testingMapper) {
		this.testingMapper = testingMapper;
	}

	@Test
	@Order(0)
	@Rollback(value = false)
	public void insertInfoTest() {
		NucleicAcidInfoPO infoPo = NucleicAcidInfoPO.builder()
			.userId(userId)
			.managerId(managerId)
			.deadLine(deadLine)
			.title(title)
			.status(status)
			.imageName(imageName)
			.isOpenRemind(isInfoOpenRemind)
			.build();

		int i = mapper.insertInfo(infoPo);
		assertTrue(i > 0);

		infoId = infoPo.getId();
	}

	@Test
	@Order(0)
	@Rollback(value = false)
	public void insertTestingTest() {
		NucleicAcidTestingPO testingPo = NucleicAcidTestingPO.builder()
			.userId(userId)
			.managerId(managerId)
			.require(require)
			.title(title)
			.startTime(startTime)
			.endTime(endTime)
			.isOpenRemind(isTestingOpenRemind)
			.place(place)
			.finishStatus(finishStatus)
			.build();

		int i = testingMapper.insertTesting(testingPo);

		assertTrue(i > 0);
		testingId = testingPo.getId();
	}

	@Test
	@Order(1)
	public void queryResultTest() {
		InfoAssociatedTestingPO resultPO = mapper.queryTestingByInfoId(infoId, userId);

		assertNotNull(resultPO);
		assertEquals(infoId, resultPO.getInfoId());
		assertEquals(testingId, resultPO.getTestingId());
		assertEquals(title, resultPO.getTitle());
	}


	@Test
	@Order(1)
	public void queryTest() {
		NucleicAcidInfoPO info = mapper.queryInfoById(infoId);

		assertEquals(userId, info.getUserId());
		// assertEquals(deadLine.toString(), info.getDeadLine().toString());
		assertEquals(isInfoOpenRemind, info.getIsOpenRemind());
		assertEquals(title, info.getTitle());
		assertEquals(status.getValue(), info.getStatus());
	}

	@Test
	@Order(1)
	public void queryUserBookingCountTest() {
		int count = mapper.queryInfoCount(userId, FinishStatus.IN_PROGRESS);
		assertEquals(1, count);
	}

	@Test
	@Order(2)
	public void updateInfoTest() {
		title = RandomTestUtil.getRandomString(30);
		imageName = RandomTestUtil.getRandomString(50);
		isInfoOpenRemind = true;

		NucleicAcidInfoPO infoPo = NucleicAcidInfoPO.builder()
			.id(infoId)
			.userId(userId)
			.managerId(managerId)
			.deadLine(deadLine)
			.title(title)
			.imageName(imageName)
			.isOpenRemind(isInfoOpenRemind)
			.build();

		int i = mapper.updateInfo(infoPo);
		NucleicAcidInfoPO info = mapper.queryInfoById(infoId);

		assertTrue(i > 0);
		assertEquals(userId, info.getUserId());
		// assertEquals(deadLine.toString(), info.getDeadLine().toString());
		assertEquals(isInfoOpenRemind, info.getIsOpenRemind());
		assertEquals(title, info.getTitle());
		assertEquals(status.getValue(), info.getStatus());
	}

	@Test
	@Order(3)
	public void updateStatusTest() {
		status = FinishStatus.DONE;
		int i = mapper.updateRecordFinish(infoId, status);
		assertTrue(i > 0);
		NucleicAcidInfoPO info = mapper.queryInfoById(infoId);
		assertEquals(status.getValue(), info.getStatus());
	}

	@Test
	@Order(4)
	@Rollback(value = false)
	public void deleteTest() {
		int i = mapper.deleteInfo(infoId);

		assertTrue(i > 0);
	}

	@Test
	@Order(4)
	@Rollback(value = false)
	public void deleteTestingTest() {
		int i = testingMapper.deleteTesting(testingId);

		assertTrue(i > 0);
	}
}
