package com.weixin.njuteam.util;

import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.Callable;

/**
 * @author Zyi
 */
public class WriteImageTask implements Callable<String> {

	private final MultipartFile image;
	private final String prefix;

	public WriteImageTask(MultipartFile image, String prefix) {
		super();
		this.image = image;
		this.prefix = prefix;
	}

	@Override
	public String call() throws Exception {
		if (FileUtil.isIncorrectFileFormat(image, new String[]{".png", ".jpg", "jpeg"})) {
			return "Format Error";
		}
		return TencentCOS.uploadImage(image, prefix);
	}
}
