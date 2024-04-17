package com.weixin.njuteam.dao.nucleic;

import com.weixin.njuteam.entity.po.nucleic.NucleicAcidBookingPO;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
@Rollback
public class NucleicAcidBookingMapperTest {

	private static long id;
	private static long userId = RandomTestUtil.getRandomId();
	private static long managerId = RandomTestUtil.getRandomId();
	private static String title = RandomTestUtil.getRandomString(20);
	private static Date deadLine = RandomTestUtil.getRandomDate("1500-01-01", "2022-04-21");
	private static boolean isOpenRemind = false;
	private static FinishStatus finishStatus = FinishStatus.IN_PROGRESS;

	private NucleicAcidBookingMapper mapper;

	@Autowired
	public void setMapper(NucleicAcidBookingMapper mapper) {
		this.mapper = mapper;
	}

	@Test
	@Order(0)
	@Rollback(value = false)
	public void insertTest() {
		NucleicAcidBookingPO bookingPo = NucleicAcidBookingPO.builder()
			.userId(userId)
			.managerId(managerId)
			.title(title)
			.deadLine(deadLine)
			.isOpenRemind(isOpenRemind)
			.finishStatus(finishStatus)
			.build();

		int i = mapper.insertBooking(bookingPo);
		assertTrue(i > 0);

		id = bookingPo.getId();
	}

	@Test
	@Order(1)
	public void queryByIdTest() {
		NucleicAcidBookingPO bookingPo = mapper.queryBookingById(id);

		assertEquals(userId, bookingPo.getUserId());
		assertEquals(title, bookingPo.getTitle());
		// fixme: 有时候时间会有偏差的问题
		// assertEquals(deadLine.toString(), bookingPo.getDeadLine().toString());
		assertEquals(isOpenRemind, bookingPo.getIsOpenRemind());
		assertEquals(finishStatus.getValue(), bookingPo.getFinishStatus());
	}

	@Test
	@Order(1)
	public void queryByUserIdTest() {
		List<NucleicAcidBookingPO> bookingPoList = mapper.queryBookingByUserId(userId);
		NucleicAcidBookingPO bookingPo = bookingPoList.get(0);

		assertEquals(userId, bookingPo.getUserId());
		assertEquals(title, bookingPo.getTitle());
		// assertEquals(deadLine.toString(), bookingPo.getDeadLine().toString());
		assertEquals(isOpenRemind, bookingPo.getIsOpenRemind());
		assertEquals(finishStatus.getValue(), bookingPo.getFinishStatus());
	}

	@Test
	@Order(1)
	public void queryUserBookingCountTest() {
		int count = mapper.queryBookingCount(userId, FinishStatus.IN_PROGRESS);
		assertEquals(1, count);
	}

	@Test
	@Order(2)
	public void updateTest() {
		finishStatus = FinishStatus.DONE;
		title = RandomTestUtil.getRandomString(30);

		NucleicAcidBookingPO bookingPo = NucleicAcidBookingPO.builder()
			.id(id)
			.userId(userId)
			.managerId(managerId)
			.title(title)
			.deadLine(deadLine)
			.isOpenRemind(isOpenRemind)
			.finishStatus(finishStatus)
			.build();

		int i = mapper.updateBooking(bookingPo);

		assertTrue(i > 0);
		assertEquals(userId, bookingPo.getUserId());
		assertEquals(title, bookingPo.getTitle());
		// assertEquals(deadLine.toString(), bookingPo.getDeadLine().toString());
		assertEquals(isOpenRemind, bookingPo.getIsOpenRemind());
		assertEquals(finishStatus.getValue(), bookingPo.getFinishStatus());
	}

	@Test
	@Order(3)
	@Rollback(value = false)
	public void deleteTest() {
		int i = mapper.deleteBooking(id);

		assertTrue(i > 0);
	}
}
