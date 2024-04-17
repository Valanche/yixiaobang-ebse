package com.weixin.njuteam.dao.manager;

import com.weixin.njuteam.entity.po.manager.nucleic.ManagerNucleicAcidTestingPO;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
@Rollback
public class ManagerNucleicTestingMapperTest {

	private ManagerNucleicAcidTestingMapper testingMapper;

	@Autowired
	public void setTestingMapper(ManagerNucleicAcidTestingMapper testingMapper) {
		this.testingMapper = testingMapper;
	}

	private static Long id;
	private static Long managerId = RandomTestUtil.getRandomId();
	private static String title = RandomTestUtil.getRandomString(30);
	private static FinishStatus finishStatus = FinishStatus.IN_PROGRESS;
	private static String require = RandomTestUtil.getRandomString(20);
	private static String place = RandomTestUtil.getRandomString(20);
	private static Date startTime = RandomTestUtil.getRandomDate("2020-05-01", "2021-05-01");
	private static Date endTime = RandomTestUtil.getRandomDate("2023-05-01", "2024-07-20");

	@Order(0)
	@Rollback(value = false)
	@Test
	public void insertInfoTest() {
		ManagerNucleicAcidTestingPO testingPo = ManagerNucleicAcidTestingPO.builder()
			.managerId(managerId)
			.title(title)
			.require(require)
			.place(place)
			.finishStatus(finishStatus)
			.startTime(startTime)
			.endTime(endTime)
			.build();

		int i = testingMapper.insertTestingInfo(testingPo);
		assertTrue(i > 0);
		id = testingPo.getId();
	}

	@Order(1)
	@Test
	public void queryInfoByIdTest() {
		ManagerNucleicAcidTestingPO testingPo = testingMapper.queryTestingById(id);

		assertNotNull(testingPo);
		assertEquals(id, testingPo.getId());
		assertEquals(title, testingPo.getTitle());
		assertEquals(require, testingPo.getRequire());
		assertEquals(place, testingPo.getPlace());
		assertEquals(finishStatus.getValue(), testingPo.getFinishStatus());
		assertEquals(managerId, testingPo.getManagerId());
	}

	@Order(1)
	@Test
	public void queryInfoByManagerIdTest() {
		List<ManagerNucleicAcidTestingPO> infoList = testingMapper.queryManagerTestingList(managerId);
		ManagerNucleicAcidTestingPO testingPo = infoList.get(0);

		assertNotNull(testingPo);
		assertEquals(id, testingPo.getId());
		assertEquals(title, testingPo.getTitle());
		assertEquals(require, testingPo.getRequire());
		assertEquals(place, testingPo.getPlace());
		assertEquals(finishStatus.getValue(), testingPo.getFinishStatus());
		assertEquals(managerId, testingPo.getManagerId());
	}

	@Order(2)
	@Test
	public void updateInfoTest() {
		title = RandomTestUtil.getRandomString(30);
		require = RandomTestUtil.getRandomString(30);
		place = RandomTestUtil.getRandomString(20);
		finishStatus = FinishStatus.DONE;

		ManagerNucleicAcidTestingPO testingPo = ManagerNucleicAcidTestingPO.builder()
			.id(id)
			.managerId(managerId)
			.title(title)
			.require(require)
			.place(place)
			.finishStatus(finishStatus)
			.startTime(startTime)
			.endTime(endTime)
			.build();

		int i = testingMapper.updateTestingInfo(testingPo);
		assertTrue(i > 0);
		ManagerNucleicAcidTestingPO testing = testingMapper.queryTestingById(id);

		assertNotNull(testing);
		assertEquals(id, testing.getId());
		assertEquals(title, testing.getTitle());
		assertEquals(require, testing.getRequire());
		assertEquals(place, testing.getPlace());
		assertEquals(finishStatus.getValue(), testing.getFinishStatus());
		assertEquals(managerId, testing.getManagerId());
	}

	@Order(3)
	@Rollback(value = false)
	@Test
	public void deleteInfoTest() {
		int i = testingMapper.deleteTestingInfoById(id);
		assertTrue(i > 0);
	}
}
