package com.weixin.njuteam.util;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * OCR 图像识别工具
 * 百度PaddleOCR
 *
 * @author Zyi
 */
@Slf4j
public class OCRUtil {

	private static final String LINE_SEPARATOR = System.lineSeparator();
	private static final String FILE_SEPARATOR = File.separator;
	private static final String SCRIPT_NAME = "/root/OCR/OCR.py".replace("/", FILE_SEPARATOR);
	private static final String IMG_PATH = "/root/WeixinData/image/%s".replace("/", FILE_SEPARATOR);
	private static final String COMMAND = "python3 " + SCRIPT_NAME + " " + IMG_PATH;
	private static final String ERR_PATH = "/root/WeixinData/log/error.txt".replace("/", FILE_SEPARATOR);
	/**
	 * 这个字符的作用仅仅只是为了后面筛选能少几行
	 * 其实不用也可以
	 */
	private static final String MARK_STR = "ppocr DEBUG";
	private static Process proc;

	private OCRUtil() {

	}

	//abandoned func, not in use
	public static String doOCR(String imageName) {

		String newCommand = String.format(COMMAND, imageName);
		StringBuilder res = new StringBuilder();

		try {
			proc = Runtime.getRuntime().exec(newCommand);
		} catch (IOException e) {
			log.error("open runtime process error!" + e.getLocalizedMessage());
		}

		try (BufferedReader err = new BufferedReader(new InputStreamReader(proc.getErrorStream(), StandardCharsets.UTF_8));
			 BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream(), StandardCharsets.UTF_8))) {
			String line;
			errOutput(err);

			while ((line = in.readLine()) != null) {
				if (!line.contains(MARK_STR)) {
					res.append(line).append(LINE_SEPARATOR);
				}
			}
		} catch (IOException e) {
			log.error(e.getMessage());
		}

		return res.toString();
	}

	private static void errOutput(BufferedReader err) throws IOException {
		String line;
		// the buffered writer is used in local test
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(ERR_PATH))) {
			while ((line = err.readLine()) != null) {
				writer.write(line);
				writer.write(LINE_SEPARATOR);
			}

			writer.flush();
		} catch (IOException e) {
			log.error("err message output error in OCRUtil, " + e.getMessage());
		}
	}

}
