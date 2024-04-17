package com.weixin.njuteam.config.cache;

import com.weixin.njuteam.entity.vo.UserVO;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * @author Zyi
 */
@Configuration
public class RedisConfig {

	/**
	 * 配置缓存管理器
	 *
	 * @param factory Redis 线程安全连接工厂
	 * @return 缓存管理器
	 */
	@Bean
	public CacheManager cacheManager(RedisConnectionFactory factory) {
		// 生成两套默认配置，通过 Config 对象即可对缓存进行自定义配置
		RedisCacheConfiguration cacheConfig = RedisCacheConfiguration.defaultCacheConfig()
			// 设置过期时间 10 分钟
			.entryTtl(Duration.ofMinutes(10))
			// 禁止缓存 null 值 不设置也防止缓存穿透
			// .disableCachingNullValues()
			// 设置 key 序列化
			.serializeKeysWith(keyPair())
			// 设置 value 序列化
			.serializeValuesWith(valuePair());

		// 返回 Redis 缓存管理器
		return RedisCacheManager.builder(factory)
			.withCacheConfiguration("user", cacheConfig)
			.withCacheConfiguration("info", cacheConfig)
			.withCacheConfiguration("infoUser", cacheConfig)
			.withCacheConfiguration("booking", cacheConfig)
			.withCacheConfiguration("bookingUser", cacheConfig)
			.withCacheConfiguration("testing", cacheConfig)
			.withCacheConfiguration("testingUser", cacheConfig)
			.withCacheConfiguration("message", cacheConfig)
			.withCacheConfiguration("helpInfo", cacheConfig)
			.withCacheConfiguration("helpInfoKeyword", cacheConfig)
			.withCacheConfiguration("seekHelpInfo", cacheConfig)
			.withCacheConfiguration("seekHelpInfoKeyword", cacheConfig)
			.withCacheConfiguration("helpClick", cacheConfig)
			.withCacheConfiguration("seekHelpClick", cacheConfig)
			.withCacheConfiguration("manager", cacheConfig)
			.withCacheConfiguration("studentList", cacheConfig).build();
	}

	@Bean(name = "openIdGenerator")
	public KeyGenerator userKeyGenerator() {
		return (target, method, params) -> ((UserVO) params[0]).getOpenId();
	}

	@Bean(name = "managerIdGenerator")
	public KeyGenerator managerKeyGenerator() {
		return ((target, method, params) -> String.valueOf(params[0]));
	}

	/**
	 * 配置键序列化
	 *
	 * @return StringRedisSerializer
	 */
	private RedisSerializationContext.SerializationPair<String> keyPair() {
		return RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer());
	}

	/**
	 * 配置值序列化，使用 GenericJackson2JsonRedisSerializer 替换默认序列化
	 *
	 * @return GenericJackson2JsonRedisSerializer
	 */
	private RedisSerializationContext.SerializationPair<Object> valuePair() {
		return RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer());
	}
}
