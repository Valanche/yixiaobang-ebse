package com.weixin.njuteam.config;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author Zyi
 */
@Component
@Slf4j
public class AccessTokenTask {

	private static final String APP_ID = "wx863ec99d19ec3d90";

	private static final String SECRET = "d194e8e33e2597b9166984117df1d415";

	private static final String GRANT_TYPE = "client_credential";

	@Getter
	private String accessToken;

	/**
	 * 定时获取access_token
	 */
	@Scheduled(cron = "0 0 0/2 * * *")
	public void postAccessToken() {
		String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type={0}&appid={1}&secret={2}";
		String replaceUrl = url.replace("{0}", GRANT_TYPE).replace("{1}", APP_ID).replace("{2}", SECRET);
		String res = null;
		try (HttpResponse response = HttpUtil.createGet(replaceUrl).execute()) {
			res = response.body();
		} catch (Exception e) {
			log.error("get access token error, message: " + e.getLocalizedMessage());
		}

		JSONObject jsonObject = JSON.parseObject(res);
		this.accessToken = jsonObject.getString("access_token");
		if (accessToken == null){
			accessToken = "no token, why?";
		}
	}

}
