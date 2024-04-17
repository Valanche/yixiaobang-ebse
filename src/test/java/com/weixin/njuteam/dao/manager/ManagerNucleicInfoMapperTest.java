package com.weixin.njuteam.dao.manager;

import com.weixin.njuteam.entity.po.manager.nucleic.ManagerNucleicAcidInfoPO;
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
public class ManagerNucleicInfoMapperTest {

	private ManagerNucleicAcidInfoMapper infoMapper;

	@Autowired
	public void setInfoMapper(ManagerNucleicAcidInfoMapper infoMapper) {
		this.infoMapper = infoMapper;
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
		ManagerNucleicAcidInfoPO infoPo = ManagerNucleicAcidInfoPO.builder()
			.managerId(managerId)
			.title(title)
			.finishStatus(finishStatus)
			.deadLine(deadLine)
			.build();

		int i = infoMapper.insertInfo(infoPo);
		assertTrue(i > 0);
		id = infoPo.getId();
	}

	@Order(1)
	@Test
	public void queryInfoByIdTest() {
		ManagerNucleicAcidInfoPO infoPo = infoMapper.queryInfoById(id);
		assertNotNull(infoPo);
		assertEquals(title, infoPo.getTitle());
		assertEquals(finishStatus.getValue(), infoPo.getFinishStatus());
		assertEquals(managerId, infoPo.getManagerId());
	}

	@Order(1)
	@Test
	public void queryInfoByManagerIdTest() {
		List<ManagerNucleicAcidInfoPO> infoList = infoMapper.queryManagerInfoList(managerId);
		ManagerNucleicAcidInfoPO infoPo = infoList.get(0);

		assertNotNull(infoPo);
		assertEquals(title, infoPo.getTitle());
		assertEquals(finishStatus.getValue(), infoPo.getFinishStatus());
		assertEquals(managerId, infoPo.getManagerId());
	}

	@Order(2)
	@Test
	public void updateInfoTest() {
		title = RandomTestUtil.getRandomString(30);
		finishStatus = FinishStatus.DONE;

		ManagerNucleicAcidInfoPO infoPo = ManagerNucleicAcidInfoPO.builder()
			.id(id)
			.managerId(managerId)
			.title(title)
			.finishStatus(finishStatus)
			.deadLine(deadLine)
			.build();

		int i = infoMapper.updateInfo(infoPo);
		assertTrue(i > 0);
		ManagerNucleicAcidInfoPO info = infoMapper.queryInfoById(id);

		assertNotNull(info);
		assertEquals(title, info.getTitle());
		assertEquals(finishStatus.getValue(), info.getFinishStatus());
		assertEquals(managerId, info.getManagerId());
	}

	@Order(3)
	@Rollback(value = false)
	@Test
	public void deleteInfoTest() {
		int i = infoMapper.deleteInfoById(id);
		assertTrue(i > 0);
	}
}
