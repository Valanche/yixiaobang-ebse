package com.weixin.njuteam.config;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Zyi
 */
@Component
@ConfigurationProperties(prefix = "baidu")
@Slf4j
@SuppressWarnings("all")
public class NLPClient {

	private static String appId;
	private static String APIKey;
	private static String secretKey;
	String s = System.lineSeparator();
	private String accessToken;
	private Date endTime;
	static final OkHttpClient HTTP_CLIENT = new OkHttpClient().newBuilder().build();

	private NLPClient() {
		// private constructor
		// do nothing, just to use the singleton
		endTime = new Date(System.currentTimeMillis());
	}

	public static NLPClient getInstance() {
		return InnerClass.getInstance();
	}

	public void setAppId(String appId) {
		NLPClient.appId = appId;
	}

	public void setAPIKey(String APIKey) {
		NLPClient.APIKey = APIKey;
	}

	public void setSecretKey(String secretKey) {
		NLPClient.secretKey = secretKey;
	}

	/**
	 * 判断access_token是否过期
	 *
	 * @return true if access token is expired, false otherwise
	 */
	private boolean isExpiration() {
		Date now = new Date(System.currentTimeMillis());

		return now.after(endTime);
	}

	/**
	 * get access_token
	 */
	private void getAccessToken() {
		String grantType = "client_credentials";
		String url = "https://aip.baidubce.com/oauth/2.0/token?grant_type={0}&client_id={1}&client_secret={2}";
		url = url.replace("{0}", grantType).replace("{1}", APIKey).replace("{2}", secretKey);
		HttpResponse response = HttpRequest.post(url).execute();

		if (response.isOk()) {
			JSONObject jsonObject = JSONObject.parseObject(response.body());
			accessToken = jsonObject.getString("access_token");
			String expire = jsonObject.getString("expires_in");
			// expire单位为秒，System.currentTimeMillis为毫秒
			endTime = new Date(System.currentTimeMillis() + Long.parseLong(expire) * 1000);
		} else {
			log.error("Baidu Access Token error: " + response.body());
		}
	}

	/**
	 * 获得两个词语之间的相似度
	 *
	 * @param firstWord  第一个词语
	 * @param secondWord 第二个词语
	 * @return 相似度
	 */
	private String getWordSimilarity(String firstWord, String secondWord) throws IOException {
		String url = "https://aip.baidubce.com/rpc/2.0/nlp/v2/word_emb_sim";
		if (accessToken == null || isExpiration()) {
			getAccessToken();
		}

		MediaType mediaType = MediaType.parse("application/json");
		String jsonBody = "{\"word_1\":\"" + firstWord + "\",\"word_2\":\"" + secondWord + "\"}";
		RequestBody body = RequestBody.create(mediaType,jsonBody);
		return sendPost2(url,body);
	}

	private String getSentenceSimilarity(String sentence1, String sentence2) throws IOException {
		String url = "https://aip.baidubce.com/rpc/2.0/nlp/v2/simnet";
		if (accessToken == null || isExpiration()) {
			getAccessToken();
		}

		MediaType mediaType = MediaType.parse("application/json");
		String jsonBody = "{\n    \"text_1\":\"" + sentence1 + "\",\n    \"text_2\":\"" + sentence2 + "\"\n}";
		RequestBody body = RequestBody.create(mediaType,jsonBody);
		return sendPost2(url, body);
	}

	private String getWordVector(String word) throws IOException {
		String url = "https://aip.baidubce.com/rpc/2.0/nlp/v2/word_emb_vec";
		if (accessToken == null | isExpiration()) {
			getAccessToken();
		}

		MediaType mediaType = MediaType.parse("application/json");
		String jsonBody = "{\n    \"word\":\"" + word + "\"\n}";
		RequestBody body = RequestBody.create(mediaType,jsonBody);
		return sendPost2(url,body);
	}

//	private String sendPost(String jsonBody, String url) throws IOException {
//		url += "?charset=UTF-8&access_token=" + accessToken;
//		HttpRequest request = HttpRequest.post(url).header("Content-Type", "application/json").body(jsonBody);
//
//		try {
//			HttpResponse response = request.execute();
//			return response.body();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		return null;
//	}

	private String sendPost2(String url, RequestBody body) throws IOException {
		url += "?charset=UTF-8&access_token=" + accessToken;

		Request request =new Request.Builder()
			.url(url)
			.method("POST",body)
			.addHeader("Content-Type", "application/json")
			.addHeader("Accept", "application/json")
			.build();
		Response response = HTTP_CLIENT.newCall(request).execute();
		return response.body().string();

	}

	public double calWordSimilarity(String word1, String word2) {
		String res = null;
		try {
			res = getWordSimilarity(word1, word2);
		} catch (IOException e) {
			log.error("get word similarity error! " + e.getMessage());
		}

		JSONObject jsonObject = JSON.parseObject(res);
		if (!jsonObject.containsKey("score") && jsonObject.containsKey("error_code")) {
			log.info("result: " + res);
			return 0.0;
		}
		return jsonObject.getDouble("score");
	}

	public double calSentenceSimilarity(String sentence1, String sentence2) {
		String res = null;
		try {
			res = getSentenceSimilarity(sentence1, sentence2);
		} catch (IOException e) {
			log.error("get sentence similarity error! " + e.getMessage());
		}
		JSONObject jsonObject = JSON.parseObject(res);
		if (!jsonObject.containsKey("score") && jsonObject.containsKey("error_code")) {
			log.info("result: " + res);
			return 0.0;
		}

		return jsonObject.getDouble("score");
	}

	@SuppressWarnings("unchecked")
	public List<Double> calWordVector(String word) {
		String res = null;
		try {
			res = getWordVector(word);
		} catch (IOException e) {
			log.error("get word vector error! " + e.getMessage());
		}
		JSONObject jsonObject = JSON.parseObject(res);
		if (!jsonObject.containsKey("vec") && jsonObject.containsKey("err_code")) {
			log.info("result:" + res);
			return new ArrayList<>();
		}

		return (List<Double>) jsonObject.get("vec");
	}

	private static class InnerClass {
		private static NLPClient instance = new NLPClient();

		public static NLPClient getInstance() {
			return instance;
		}
	}
}
