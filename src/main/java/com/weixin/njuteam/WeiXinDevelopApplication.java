package com.weixin.njuteam;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author Zyi
 */
@SpringBootApplication
@EnableCaching
@EnableConfigurationProperties
@EnableEncryptableProperties
@EnableScheduling
public class WeiXinDevelopApplication {

	public static void main(String[] args) {
		SpringApplication.run(WeiXinDevelopApplication.class, args);
	}

}
