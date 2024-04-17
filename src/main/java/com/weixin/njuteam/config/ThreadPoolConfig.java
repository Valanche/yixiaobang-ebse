package com.weixin.njuteam.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * @author Zyi
 */
@Configuration
public class ThreadPoolConfig {

	@Bean
	public ThreadPoolTaskScheduler syncScheduler() {
		ThreadPoolTaskScheduler syncScheduler = new ThreadPoolTaskScheduler();
		// 线程池的大小为20
		syncScheduler.setPoolSize(20);
		// 这里给线程设置名字，主要是为了在项目能够更快速的定位错误。
		syncScheduler.setThreadGroupName("syncTg");
		syncScheduler.setThreadNamePrefix("syncThread-");
		syncScheduler.initialize();

		return syncScheduler;
	}
}
