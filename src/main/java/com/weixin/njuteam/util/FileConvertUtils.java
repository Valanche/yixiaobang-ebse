package com.weixin.njuteam.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

/**
 * 创建时间：2019年3月8日 下午4:25:55
 * 项目名称：shsc-batchUpload-server
 * 类说明：File流转为MultipartFile流
 * @author guobinhui
 * @since JDK 1.8.0_51
 */
public class FileConvertUtils {

	public static MultipartFile getMulFileByFile(File file) {
		FileItem fileItem = createFileItem(file.getPath(),file.getName());
		MultipartFile mfile = new CommonsMultipartFile(fileItem);
		return mfile;
	}

	public static FileItem createFileItem(String filePath,String fileName){
		String fieldName = "file";
		FileItemFactory factory = new DiskFileItemFactory(16, null);
		FileItem item = factory.createItem(fieldName, "text/plain", false,fileName);
		File newfile = new File(filePath);
		int bytesRead = 0;
		byte[] buffer = new byte[8192];
		try
		{
			FileInputStream fis = new FileInputStream(newfile);
			OutputStream os = item.getOutputStream();
			while ((bytesRead = fis.read(buffer, 0, 8192))!= -1)
			{
				os.write(buffer, 0, bytesRead);
			}
			os.close();
			fis.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return item;
	}
}
