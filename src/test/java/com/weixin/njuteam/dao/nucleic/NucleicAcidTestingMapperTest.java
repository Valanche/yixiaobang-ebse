package com.weixin.njuteam.dao.nucleic;

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

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
@Rollback
public class NucleicAcidTestingMapperTest {

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

	private NucleicAcidTestingMapper testingMapper;

	@Autowired
	public void setTestingMapper(NucleicAcidTestingMapper testingMapper) {
		this.testingMapper = testingMapper;
	}

	@Test
	@Rollback(value = false)
	@Order(0)
	public void insertTest() {
		NucleicAcidTestingPO testingPo = NucleicAcidTestingPO.builder()
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

		int i = testingMapper.insertTesting(testingPo);

		assertTrue(i > 0);
		id = testingPo.getId();
	}

	@Test
	@Order(1)
	public void queryByIdTest() {
		NucleicAcidTestingPO testingPO = testingMapper.queryTestingById(id);

		assertNotNull(testingPO);
		assertEquals(userId, testingPO.getUserId());
		assertEquals(require, testingPO.getRequire());
		assertEquals(title, testingPO.getTitle());
		assertEquals(isOpenRemind, testingPO.getIsOpenRemind());
		assertEquals(place, testingPO.getPlace());
		assertEquals(finishStatus.getValue(), testingPO.getFinishStatus());
	}

	@Test
	@Order(1)
	public void queryByUserIdTest() throws SQLException {
		List<NucleicAcidTestingPO> testingPoList = testingMapper.queryTestingByUserId(userId);
		NucleicAcidTestingPO testingPO = testingPoList.get(0);

		assertNotNull(testingPO);
		assertEquals(userId, testingPO.getUserId());
		assertEquals(require, testingPO.getRequire());
		assertEquals(title, testingPO.getTitle());
		assertEquals(isOpenRemind, testingPO.getIsOpenRemind());
		assertEquals(place, testingPO.getPlace());
		assertEquals(finishStatus.getValue(), testingPO.getFinishStatus());
	}

	@Test
	@Order(1)
	public void queryUserBookingCountTest() {
		int count = testingMapper.queryTestingCount(userId, FinishStatus.IN_PROGRESS);
		assertEquals(1, count);
	}

	@Test
	@Order(2)
	public void updateTest() {
		require = RandomTestUtil.getRandomString(50);
		title = RandomTestUtil.getRandomString(50);
		isOpenRemind = true;
		finishStatus = FinishStatus.DONE;
		place = RandomTestUtil.getRandomString(50);

		NucleicAcidTestingPO testingPO = NucleicAcidTestingPO.builder()
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

		assertNotNull(testingPO);
		assertEquals(userId, testingPO.getUserId());
		assertEquals(require, testingPO.getRequire());
		assertEquals(title, testingPO.getTitle());
		assertEquals(isOpenRemind, testingPO.getIsOpenRemind());
		assertEquals(place, testingPO.getPlace());
		assertEquals(finishStatus.getValue(), testingPO.getFinishStatus());
	}

	@Test
	@Order(3)
	@Rollback(value = false)
	public void deleteTest() {
		int i = testingMapper.deleteTesting(id);

		assertTrue(i > 0);
	}

}
