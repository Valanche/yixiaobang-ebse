package com.weixin.njuteam.service.impl.help;

import com.weixin.njuteam.service.RecommendService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
@Rollback
public class RecommendServiceImplTest {
	private RecommendService recommendService;

	@Autowired
	public void setRecommendService(RecommendService recommendService) {
		this.recommendService = recommendService;
	}

	@Test
	@Order(0)
	public void queryTest() {
		System.out.println(recommendService.queryWordSimilarity("我测你的码","丁真"));
	}

	@Test
	public void queryTest2(){
		System.out.println(recommendService.querySentenceSimilarity("我测你的码","想买锐克5找我丁真就对了"));
	}


}
