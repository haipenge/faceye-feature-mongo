package com.faceye.feature.upload;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.faceye.feature.service.PropertyService;
import com.faceye.feature.util.Json;
import com.faceye.feature.util.bean.BeanContextUtil;

public class Upload {
	private static Logger logger = LoggerFactory.getLogger(Upload.class);

	enum FILE_TYPE {
		word, pdf, excel, ppt, txt
	}

	public static List<UploadResult> upoad(HttpServletRequest request) {
		List<UploadResult> result = new ArrayList();
		if (!ServletFileUpload.isMultipartContent(request)) {
			throw new IllegalArgumentException("Request is not multipart, please 'multipart/form-data' enctype for your form.");
		}
		ServletFileUpload uploadHandler = new ServletFileUpload(new DiskFileItemFactory());
		String path = Path.getPath();
		try {
			if(!StringUtils.endsWith(path, "/")){
				path=path+"/";
			}
			logger.debug(">>Faceye --> upload dir is :"+path);
			List<FileItem> items = uploadHandler.parseRequest(request);
			if (CollectionUtils.isNotEmpty(items)) {
				logger.debug(">>FaceYe items size is:" + items.size());
				for (FileItem item : items) {
					logger.debug(">>FaceYe item name is:" + item.getName());
					if (StringUtils.isNotEmpty(item.getName()) && StringUtils.indexOf(item.getName(), ".") != -1) {
						String generateFileName = Path.generateFileName(item.getName());
						logger.debug(">>FaceYe generate file name is:" + generateFileName);
						File file = new File(path, generateFileName);
						item.write(file);
						String name = item.getName();
						String size = "" + item.getSize();
						UploadResult uploadResult = new UploadResult();
						uploadResult.setGenerateFileName(generateFileName);
						uploadResult.setName(name);
						uploadResult.setPath(path);
						uploadResult.setSize(size);
						result.add(uploadResult);
					}
				}
			}else{
				logger.debug(">>FaceYe upload file is empty.");
			}
		} catch (FileUploadException e) {
			logger.error(">>--->" + e.getMessage());
		} catch (Exception e) {
			logger.error(">>--->" + e.getMessage());
		}
		return result;
	}

	/**
	 * 将upload 的结果，转化为json 结构，以支持jQuery-Upload-File识别
	 * @todo
	 * @param uploadResult
	 * @return
	 * @author:@haipenge
	 * haipenge@gmail.com
	 * 2014年8月18日
	 */
	public static String uploadResults2Json(List<UploadResult> uploadResults) {
		String res = "";
		List items = new ArrayList();
		// JSONArray json = new JSONArray();
		if (CollectionUtils.isNotEmpty(uploadResults)) {
			for (UploadResult uploadResult : uploadResults) {
//				JSONObject jsono = new JSONObject();
				Map jsono = new HashMap();
				jsono.put("name", uploadResult.getName());
				jsono.put("key", uploadResult.getGenerateFileName().substring(0, uploadResult.getGenerateFileName().lastIndexOf(".")));
				jsono.put("size", uploadResult.getSize());
				jsono.put("url", "/UploadServlet?getfile=" + uploadResult.getGenerateFileName());
				jsono.put("filename", uploadResult.getGenerateFileName());
				// jsono.put("thumbnailUrl", "/UploadServlet?getthumb=" + uploadResult.getGenerateFileName());
				// // 如果是word,ppt,pdf文档，则生成不同的预览图
				// if (StringUtils.isNotEmpty(uploadResult.getGenerateFileName())) {
				// if (uploadResult.getGenerateFileName().toLowerCase().endsWith("doc")
				// || uploadResult.getGenerateFileName().toLowerCase().endsWith("ppt")
				// || uploadResult.getGenerateFileName().toLowerCase().endsWith("docx")) {
				// jsono.put("thumbnailUrl", "/UploadServlet?getthumb=" + this.getWordPreview());
				// }
				// }
				String preview = getPreview(uploadResult.getGenerateFileName());
				jsono.put("thumbnailUrl", "/UploadServlet?getthumb=" + preview);
				jsono.put("deleteUrl", "/UploadServlet?delfile=" + uploadResult.getGenerateFileName());
				jsono.put("deleteType", "GET");
				items.add(jsono);
			}
		}else{
			logger.debug(">>FaceYe uploadResult is empty.");
		}

		res = "{\"files\":";
		res += Json.toJson(items);
		res += "}";
		return res;
	}

	/**
	 * 将DocFile转化为jQuery-Upload-File可识别的结构
	 * @todo
	 * @param docFiles
	 * @return
	 * @author:@haipenge
	 * haipenge@gmail.com
	 * 2014年8月18日
	 */
	// public static String docFiles2Json(List<DocFile> docFiles) {
	// String res = "";
	// List items = new ArrayList();
	// // JSONArray json = new JSONArray();
	// if (CollectionUtils.isNotEmpty(docFiles)) {
	// for (DocFile docFile : docFiles) {
	// // JSONObject jsono = new JSONObject();
	// Map jsono = new HashMap();
	// jsono.put("name", docFile.getNativeName());
	// jsono.put("key", docFile.getName().substring(0, docFile.getName().lastIndexOf(".") - 1));
	// jsono.put("size", docFile.getSize());
	// jsono.put("url", "/UploadServlet?getfile=" + docFile.getName());
	// // jsono.put("thumbnailUrl", "/UploadServlet?getthumb=" + docFile.getName());
	// String preview=getPreview(docFile.getName());
	// jsono.put("thumbnailUrl", "/UploadServlet?getthumb=" + preview);
	// // 如果是word,ppt,pdf文档，则生成不同的预览图
	// jsono.put("deleteUrl", "/UploadServlet?delfile=" + docFile.getName());
	// jsono.put("deleteType", "GET");
	// items.add(jsono);
	// }
	// }
	//
	// res = "{\"files\":";
	// res += Json.toJson(items);
	// res += "}";
	// return res;
	// }

	/**
	 * 取得word预览图
	 * @todo
	 * @return
	 * @author:@haipenge
	 * haipenge@gmail.com
	 * 2014年8月18日
	 */
	// private String getWordPreview() {
	// return this.getPreview(FILE_TYPE.word.toString());
	// }

	private static String getPreview(String filename) {
		String res = "";
		String type = "";
		if (StringUtils.isNotEmpty(filename)) {
			String xfilename = filename.toLowerCase();
			if (xfilename.endsWith("doc") || xfilename.endsWith("docx")) {
				type = FILE_TYPE.word.toString();
			} else if (xfilename.endsWith("ppt") || xfilename.endsWith("pptx")) {
				type = FILE_TYPE.ppt.toString();
			} else if (xfilename.endsWith("pdf")) {
				type = FILE_TYPE.pdf.toString();
			} else if (xfilename.endsWith("xls") || xfilename.endsWith("xlsx")) {
				type = FILE_TYPE.excel.toString();
			}
			PropertyService propertyService = BeanContextUtil.getInstance().getBean(PropertyService.class);
			if (StringUtils.equals(type, FILE_TYPE.word.toString())) {
				res = propertyService.get("preview.img.word");
			}
		}
		if (StringUtils.isEmpty(res)) {
			res = filename;
		}
		return res;
	}

	public static boolean delete(String filename) {
		boolean res = Boolean.FALSE;
		String path = Path.getPathByFilename(filename);
		logger.debug(">>FaceYe->Delete file:" + filename);
		File file = new File(path + filename);
		if (file.exists()) {
			file.delete();
			res = Boolean.TRUE;
		}
		return res;
	}
}
