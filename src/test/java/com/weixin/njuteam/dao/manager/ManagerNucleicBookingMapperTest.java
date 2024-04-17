package com.weixin.njuteam.dao.manager;

import com.weixin.njuteam.entity.po.manager.nucleic.ManagerNucleicAcidBookingPO;
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
public class ManagerNucleicBookingMapperTest {

	private ManagerNucleicAcidBookingMapper bookingMapper;

	@Autowired
	public void setBookingMapper(ManagerNucleicAcidBookingMapper bookingMapper) {
		this.bookingMapper = bookingMapper;
	}

	private static Long id;
	private static Long managerId = RandomTestUtil.getRandomId();
	private static String title = RandomTestUtil.getRandomString(30);
	private static FinishStatus finishStatus = FinishStatus.IN_PROGRESS;
	private static Date deadLine = RandomTestUtil.getRandomDate("2023-05-01", "2024-07-20");

	@Order(0)
	@Rollback(value = false)
	@Test
	public void insertInfoTest() {
		ManagerNucleicAcidBookingPO bookingPo = ManagerNucleicAcidBookingPO.builder()
			.managerId(managerId)
			.title(title)
			.finishStatus(finishStatus)
			.deadLine(deadLine)
			.build();

		int i = bookingMapper.insertBooking(bookingPo);
		assertTrue(i > 0);
		id = bookingPo.getId();
	}

	@Order(1)
	@Test
	public void queryInfoByIdTest() {
		ManagerNucleicAcidBookingPO bookingPo = bookingMapper.queryBookingById(id);
		assertNotNull(bookingPo);
		assertEquals(title, bookingPo.getTitle());
		assertEquals(finishStatus.getValue(), bookingPo.getFinishStatus());
		assertEquals(managerId, bookingPo.getManagerId());
	}

	@Order(1)
	@Test
	public void queryInfoByManagerIdTest() {
		List<ManagerNucleicAcidBookingPO> bookingList = bookingMapper.queryManagerBookingList(managerId);
		assertTrue(bookingList.size() > 0);
		ManagerNucleicAcidBookingPO bookingPo = bookingList.get(0);

		assertNotNull(bookingPo);
		assertEquals(title, bookingPo.getTitle());
		assertEquals(finishStatus.getValue(), bookingPo.getFinishStatus());
	}

	@Order(2)
	@Test
	public void updateInfoTest() {
		title = RandomTestUtil.getRandomString(30);
		finishStatus = FinishStatus.DONE;

		ManagerNucleicAcidBookingPO bookingPo = ManagerNucleicAcidBookingPO.builder()
			.id(id)
			.managerId(managerId)
			.title(title)
			.finishStatus(finishStatus)
			.deadLine(deadLine)
			.build();

		int i = bookingMapper.updateBooking(bookingPo);
		assertTrue(i > 0);
		ManagerNucleicAcidBookingPO booking = bookingMapper.queryBookingById(id);

		assertNotNull(booking);
		assertEquals(title, booking.getTitle());
		assertEquals(finishStatus.getValue(), booking.getFinishStatus());
		assertEquals(managerId, booking.getManagerId());
	}

	@Order(3)
	@Rollback(value = false)
	@Test
	public void deleteInfoTest() {
		int i = bookingMapper.deleteBookingById(id);
		assertTrue(i > 0);
	}
}
