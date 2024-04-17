package com.weixin.njuteam.util;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.model.GetObjectRequest;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.region.Region;
import com.weixin.njuteam.exception.FileDeleteException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/**
 * @author Zyi
 */
@Slf4j
public class TencentCOS {

	private static final String BUCKET_NAME = "wechat-mini-z-1315540018";
	private static final String PREFIX = "https://wechat-mini-z-1315540018.cos.ap-nanjing.myqcloud.com/";
	private static final String SECRET_ID = "AKIDIVVHpDMM8j5D6l01qQt76qT0VvHYyCCw";
	private static final String SECRET_KEY = "jEVXLxLURDnSzIRNEvPgjYAbu40iEZhb";
	private static final COSCredentials CREDENTIALS = new BasicCOSCredentials(SECRET_ID, SECRET_KEY);
	private static final ClientConfig CLIENT_CONFIG = new ClientConfig(new Region("ap-nanjing"));

	private TencentCOS() {

	}

	public static String uploadImage(MultipartFile image, String prefix) throws CosClientException, IOException {
		if (image == null || image.isEmpty()) {
			return null;
		}
		// 获取文件名
		String fileName = Objects.requireNonNull(image.getOriginalFilename());
		// 获取文件后缀
		String suffix = fileName.substring(fileName.lastIndexOf("."));
		// 用uuid作为文件名，防止生成的临时文件重复
		File tempFile = File.createTempFile(RandomUtil.getRandomUUID(), suffix);
		// 将MultipartFile转为File
		image.transferTo(tempFile);

		//调用腾讯云工具上传文件
		String cloudFileName = uploadImage(tempFile, prefix);
		deleteFile(tempFile);

		return cloudFileName;
	}

	private static String uploadImage(File image, String prefix) throws CosClientException {
		// 生成cos客户端
		COSClient cosclient = new COSClient(CREDENTIALS, CLIENT_CONFIG);
		String fileName = image.getName();
		try {
			String substring = fileName.substring(fileName.lastIndexOf("."));
			String UUID = RandomUtil.getRandomUUID();
			// 指定要上传到 COS 上的路径
			fileName = prefix + UUID + substring;
			PutObjectRequest putObjectRequest = new PutObjectRequest(BUCKET_NAME, fileName, image);
			PutObjectResult putObjectResult = cosclient.putObject(putObjectRequest);
			if (putObjectResult == null) {
				log.error("image upload error!");
			}
		} catch (Exception e) {
			log.error("tencent COS error! " + e.getMessage());
		} finally {
			// 关闭客户端(关闭后台线程)
			cosclient.shutdown();
		}

		return PREFIX + fileName;
	}

	/**
	 * 删除文件
	 *
	 * @param key
	 */
	public static void deleteImage(String key) throws CosClientException {
		// 生成cos客户端
		COSClient cosclient = new COSClient(CREDENTIALS, CLIENT_CONFIG);
		// 指定要删除的 bucket 和路径
		cosclient.deleteObject(BUCKET_NAME, key);
		// 关闭客户端(关闭后台线程)
		cosclient.shutdown();
	}

	private static void deleteFile(File... fileList) {
		for (File file : fileList) {
			if (file.exists()) {
				boolean isDelete = file.delete();
				if (!isDelete) {
					throw new FileDeleteException("file delete error, fileName = " + file.getName());
				}
			}
		}
	}

}
