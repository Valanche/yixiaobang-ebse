package com.weixin.njuteam.config.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Zyi
 */
@Component
@Slf4j
public class RedisOperator {

	private final StringRedisTemplate redisTemplate;

	@Autowired
	public RedisOperator(StringRedisTemplate redisTemplate) {
		RedisSerializer<String> stringSerializer = new StringRedisSerializer();
		GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer();
		redisTemplate.setKeySerializer(stringSerializer);
		redisTemplate.setValueSerializer(serializer);
		redisTemplate.setHashKeySerializer(stringSerializer);
		redisTemplate.setHashValueSerializer(serializer);
		this.redisTemplate = redisTemplate;
	}

	public void set(String key, String value) {
		redisTemplate.opsForValue().setIfPresent(key, value);
	}

	public String get(String key) {
		return redisTemplate.opsForValue().get(key);
	}

	public boolean contains(String key) {
		return Boolean.TRUE.equals(redisTemplate.hasKey(key));
	}

	public void delete(String key) {
		if (contains(key)) {
			log.info("delete key: " + key + " successfully");
			redisTemplate.delete(key);
		}
	}

	public void deleteAll(String pattern) {
		Set<String> keys = scan(pattern + "*");
		if (keys != null && !keys.isEmpty()) {
			redisTemplate.delete(keys);
		}
	}

	private Set<String> scan(String pattern) {
		return redisTemplate.execute((RedisCallback<Set<String>>) connection -> {
			Set<String> keysTmp = new HashSet<>();
			Cursor<byte[]> cursor = connection.scan(ScanOptions.scanOptions().match(pattern).count(1000).build());
			while (cursor.hasNext()) {
				keysTmp.add(new String(cursor.next()));
			}
			return keysTmp;
		});
	}
}
