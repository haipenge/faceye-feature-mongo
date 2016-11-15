package com.faceye.feature.util.storage;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.faceye.feature.service.PropertyService;
import com.faceye.feature.util.bean.BeanContextUtil;

/**
 * 存储工具类
 * 
 * @author haipenge
 *
 */
public class StorageHandler {
	/**
	 * 默认临时存储路径
	 * 
	 * @todo,考虑windows环境
	 */
	private static final String DEFAULT_STORAGE_PATH = "/tmp/faceye";
	/**
	 * 默认配置存储路径 key
	 */
	private static final String DEFAULT_STORAGE_CONFIG_KEY = "default.storage.path";
	/**
	 * 设置默认存储路径
	 */
	private static String STORAGE_PATH = DEFAULT_STORAGE_PATH;
	private Logger logger = LoggerFactory.getLogger(StorageHandler.class);

	private StorageHandler() {
		String configStoragePath = BeanContextUtil.getInstance().getBean(PropertyService.class).get(DEFAULT_STORAGE_CONFIG_KEY);
		if (StringUtils.isNotEmpty(configStoragePath)) {
			STORAGE_PATH = configStoragePath;
		}
	}

	private static class StorageHandlerHolder {
		private static StorageHandler INSTANCE = new StorageHandler();
	}

	public static StorageHandler getInstance() {
		return StorageHandlerHolder.INSTANCE;
	}

	/**
	 * 文件写入(单线程写入，防止线程争用）
	 * 
	 * @param bytes
	 * @param writeImgUrl
	 * @return
	 * @Desc:
	 * @Author:haipenge
	 * @Date:2016年5月10日 上午10:16:26
	 */
	synchronized public boolean write(byte[] bytes, String path) {
		String writePath = "";
		logger.debug(">>FaceYe --> Storage path is:"+STORAGE_PATH+",customer path is:"+path);
		if (!StringUtils.endsWith(STORAGE_PATH, "/") && !StringUtils.startsWith(path, "/")) {
			writePath = STORAGE_PATH + "/" + path;
		}
		if (StringUtils.endsWith(STORAGE_PATH, "/") && StringUtils.startsWith(path, "/")) {
			writePath = STORAGE_PATH + StringUtils.substring(path, 1);
		}
		if(StringUtils.isEmpty(writePath)){
			writePath = STORAGE_PATH  + path;
		}
		logger.debug(">>FaceYe --> Write path is:" + writePath);
		boolean res = false;
		if (null != bytes && bytes.length > 0) {
			BufferedOutputStream out = null;
			try {
				String dir = writePath.substring(0, writePath.lastIndexOf("/"));
				File file = new File(dir);
				if (!file.exists()) {
					file.mkdirs();
				}
				File writeFile = new File(writePath);
				if (!writeFile.exists()) {
					writeFile.createNewFile();
				}
				out = new BufferedOutputStream(new FileOutputStream(writeFile));
				out.write(bytes);
				out.flush();
				res = true;
			} catch (Exception e) {
				res = false;
				logger.error(">>FaceYe throws Exception when wirte img,path is:" + writePath + ",exception:" + e.toString());
			} finally {
				if (out != null) {
					try {
						out.close();
					} catch (IOException e) {
						logger.error(">>FaceYe throws Exception: --->" + e.toString());
					}
				}
			}
		}
		return res;
	}

}
