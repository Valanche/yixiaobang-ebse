package com.weixin.njuteam.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/**
 * @author Zyi
 */
@Slf4j
public class FileUtil {

	private FileUtil() {

	}

	/**
	 * 将图片写到本地
	 *
	 * @param image   图片
	 * @param imgPath 对应路径
	 * @return 文件名
	 */
	public static String writeFile(MultipartFile image, String imgPath) {
		// 自动生成一个随机文件名 注意后缀！
		String postfix = Objects.requireNonNull(image.getOriginalFilename()).split("\\.")[1];
		String imageName = RandomUtil.getRandomUUID() + "." + postfix;
		File file = new File(imgPath);

		byte[] bs = new byte[1024];
		int len;

		if (!file.exists()) {
			file.mkdirs();
		}

		try (InputStream inputStream = image.getInputStream();
			 OutputStream outputStream = new FileOutputStream(imgPath + imageName)) {
			while ((len = inputStream.read(bs)) != -1) {
				outputStream.write(bs, 0, len);
			}
			outputStream.flush();
		} catch (IOException e) {
			log.error("write file error!");
		}

		return imageName;
	}

	public static boolean isIncorrectFileFormat(MultipartFile file, String[] prefixList) {
		if (file == null || file.isEmpty()) {
			return true;
		}

		String fileName = Objects.requireNonNull(file.getOriginalFilename());
		for (String prefix : prefixList) {
			if (fileName.endsWith(prefix)) {
				return false;
			}
		}

		return true;
	}

	public static List<String> writeSyncFile(MultipartFile[] imageList, String prefix) {
		int len = imageList.length;
		ExecutorService executorService = Executors.newFixedThreadPool(len);
		// 每一个任务的list
		List<FutureTask<String>> taskList = new ArrayList<>();
		List<String> imageNameList = new ArrayList<>();

		for (MultipartFile image : imageList) {
			// 吧每一个文件放入异步任务中 去执行
			FutureTask<String> task = new FutureTask<>(new WriteImageTask(image, prefix));
			executorService.execute(task);
			// 吧每一个任务的返回信息放进入
			taskList.add(task);
		}

		// 执行完毕 获得文件名
		for (FutureTask<String> task : taskList) {
			try {
				imageNameList.add(task.get());
			} catch (InterruptedException | ExecutionException e) {
				log.error("multi thread write file error in file util");
				Thread.currentThread().interrupt();
			}
		}

		return imageNameList;
	}
}
