package com.weixin.njuteam.config.authorization;

import com.weixin.njuteam.entity.vo.UserVO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Zyi
 */
@Configuration
@ConfigurationProperties(prefix = "jwt.user")
@Slf4j
public class JwtUserConfig {

	@Setter
	private String secret;

	@Setter
	private long expire;

	/**
	 * 签发jwt
	 *
	 * @param user user
	 * @return token
	 */
	public String createUserJwt(UserVO user) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("openid", user.getOpenId());
		claims.put("school", user.getSchool());
		claims.put("institute", user.getInstitute());
		claims.put("major", user.getMajor());
		claims.put("grade", user.getGrade());
		return Jwts.builder()
			// 设置装载内容
			.setClaims(claims)
			// 签发时间
			.setIssuedAt(new Date(System.currentTimeMillis()))
			// 过期时间
			.setExpiration(new Date(System.currentTimeMillis() + expire))
			// jwt主体，用来存放jwt的所有人，可以存用户id
			.setSubject(user.getOpenId())
			.signWith(SignatureAlgorithm.HS256, secret.getBytes(StandardCharsets.UTF_8))
			.compact();
	}

	/**
	 * 解析JWT
	 *
	 * @param token 用户token
	 * @return jwt claims
	 */
	public Claims parseUserJwt(String token) {
		Claims claims;
		try {
			claims = Jwts.parser().setSigningKey(secret.getBytes(StandardCharsets.UTF_8)).parseClaimsJws(token).getBody();
		} catch (ExpiredJwtException e) {
			claims = e.getClaims();
		}

		return claims;
	}

	/**
	 * 验证token是否失效
	 *
	 * @param token token
	 * @return true 是过期, false otherwise
	 */
	public boolean isExpiration(String token) {
		// 不管是否过期，都返回claims对象
		Claims claims = parseUserJwt(token);
		Date expiration = claims.getExpiration();

		// 和当前时间进行对比来判断是否过期
		return new Date(System.currentTimeMillis()).after(expiration);
	}

	/**
	 * 获取token失效时间
	 *
	 * @param token token
	 * @return token expiration time
	 */
	public Date getExpirationDateFromToken(String token) {
		return parseUserJwt(token).getExpiration();
	}
}
