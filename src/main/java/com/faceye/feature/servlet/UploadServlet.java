package com.faceye.feature.servlet;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.faceye.feature.upload.Path;
import com.faceye.feature.upload.Upload;
import com.faceye.feature.upload.UploadResult;

@MultipartConfig(maxFileSize=-1,maxRequestSize=-1)
public class UploadServlet extends HttpServlet {

	/**
	 * word 文件预览图
	 * /work/server/document/pic/20140817
	 */
	private static final long serialVersionUID = -598317371461082707L;
	private Logger logger = LoggerFactory.getLogger(UploadServlet.class);

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 * 
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		if (request.getParameter("getfile") != null && !request.getParameter("getfile").isEmpty()) {
			// File file = new File(request.getRealPath("/")+"imgs/"+request.getParameter("getfile"));
			// File file = new File(this.getUploadPath() + "/" + request.getParameter("getfile"));
			String getfile = request.getParameter("getfile");
			File file = new File(Path.getPathByFilename(getfile));
			if (file.exists()) {
				int bytes = 0;
				ServletOutputStream op = response.getOutputStream();
				response.setContentType(getMimeType(file));
				response.setContentLength((int) file.length());
				response.setHeader("Content-Disposition", "inline; filename=\"" + file.getName() + "\"");
				byte[] bbuf = new byte[1024];
				DataInputStream in = new DataInputStream(new FileInputStream(file));
				while ((in != null) && ((bytes = in.read(bbuf)) != -1)) {
					op.write(bbuf, 0, bytes);
				}
				in.close();
				op.flush();
				op.close();
			}
		} else if (request.getParameter("delfile") != null && !request.getParameter("delfile").isEmpty()) {
			// File file = new File(request.getRealPath("/") + "imgs/" + request.getParameter("delfile"));
			String delfile = request.getParameter("delfile");
			File file = new File(Path.getPathByFilename(delfile));
			if (file.exists()) {
				file.delete(); // TODO:check and report success
			}
			PrintWriter writer = response.getWriter();
			response.setContentType("text/json");
			writer.write("{\"url\":\"success\",\"filename\":\"" + delfile + "\"}");
			// 从库中删除某一文件
			// DocFileService docFileService=BeanContextUtil.getInstance().getBean(DocFileService.class);
			// DocFile docFile=docFileService.getDocFileByName(delfile);
			// docFileService.remove(docFile);
		} else if (request.getParameter("getthumb") != null && !request.getParameter("getthumb").isEmpty()) {
			// File file = new File(request.getRealPath("/")+"imgs/"+request.getParameter("getthumb"));
			// File file = new File(this.getUploadPath() + "/" + request.getParameter("getthumb"));
			String getthumb = request.getParameter("getthumb");
			String size = request.getParameter("size");
			File file = new File(Path.getPathByFilename(getthumb));
			if (file.exists()) {
				logger.debug(">>FaceYe --> File absolute path is :" + file.getAbsolutePath());
				String mimetype = getMimeType(file);
				if (mimetype.endsWith("png") || mimetype.endsWith("jpeg") || mimetype.endsWith("jpg") || mimetype.endsWith("gif")) {
					Integer[] widthAndHeight = this.targetImgWidthAndHeight(size);
					BufferedImage im = ImageIO.read(file);
					// BufferedImage im = this.buildBufferedImage(im, widthAndHeight);
					if (im != null) {
						// BufferedImage thumb = Scalr.resize(im, 75);
						BufferedImage thumb = this.buildBufferedImage(im, widthAndHeight);
						ByteArrayOutputStream os = new ByteArrayOutputStream();
						if (mimetype.endsWith("png")) {
							ImageIO.write(thumb, "PNG", os);
							response.setContentType("image/png");
						} else if (mimetype.endsWith("jpeg")) {
							ImageIO.write(thumb, "jpg", os);
							response.setContentType("image/jpeg");
						} else if (mimetype.endsWith("jpg")) {
							ImageIO.write(thumb, "jpg", os);
							response.setContentType("image/jpeg");
						} else {
							ImageIO.write(thumb, "GIF", os);
							response.setContentType("image/gif");
						}
						ServletOutputStream srvos = response.getOutputStream();
						response.setContentLength(os.size());
						response.setHeader("Content-Disposition", "inline; filename=\"" + file.getName() + "\"");
						os.writeTo(srvos);
						srvos.flush();
						srvos.close();
					}
				}
			} // TODO: check and report success
		} else {
			PrintWriter writer = response.getWriter();
			writer.write("call POST with multipart form data");
		}
	}

	private BufferedImage buildBufferedImage(BufferedImage bi, Integer[] size) {
		BufferedImage img = null;
		Integer originalWidth = bi.getWidth();
		Integer originalHeight = bi.getHeight();
		Double widthAndHeightRate = new Double(originalWidth) / new Double(originalHeight);
		if (bi != null) {
			Integer width = size[0];
			Integer height = size[1];
			if (width == 0 && height != 0) {
				width = new Double((new Double(height) * widthAndHeightRate)).intValue();
			} else if (width != 0 && height == 0) {
				height = new Double((new Double(width) / widthAndHeightRate)).intValue();
			}
			if (height.intValue() != 0 && width != 0) {
				// img=Scalr.resize(bi, width, height);
				// img = Scalr.resize(bi, Scalr.Method.SPEED, Scalr.Mode.FIT_TO_WIDTH, width, height, Scalr.OP_ANTIALIAS);
//				img = Scalr.resize(bi, Scalr.Method.SPEED, Scalr.Mode.FIT_EXACT, width, height, Scalr.OP_ANTIALIAS);
				img = Scalr.resize(bi, Scalr.Method.ULTRA_QUALITY, Scalr.Mode.FIT_EXACT, width, height, Scalr.OP_ANTIALIAS,Scalr.OP_BRIGHTER);
				//
				// img = Scalr.resize(bi, Scalr.Method.SPEED, Scalr.Mode.FIT_EXACT, height, Scalr.OP_ANTIALIAS);

			} else {
				// img = Scalr.resize(bi, width);
				img = Scalr.resize(bi, Scalr.Method.SPEED, Scalr.Mode.FIT_EXACT, width, Scalr.OP_ANTIALIAS,Scalr.OP_BRIGHTER);
				      
			}
		}
		return img;
	}

	/**
	 * 输出图片的目标宽高
	 * 
	 * @todo
	 * @param size
	 * @return
	 * @author:@haipenge haipenge@gmail.com 2014年3月3日
	 */
	private Integer[] targetImgWidthAndHeight(String size) {
		Integer[] res = new Integer[2];
		if (StringUtils.isNotEmpty(size)) {
			size = size.toUpperCase();
			if (StringUtils.indexOf(size, "X") != -1) {
				String[] sizes = size.split("X");
				res[0] = Integer.parseInt(sizes[0]);
				res[1] = Integer.parseInt(sizes[1]);
			} else {
				res[0] = Integer.parseInt(size);
				res[1] = 0;
			}
		} else {
			res[0] = 75;
			res[1] = 0;
		}
		return res;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		List<UploadResult> result = Upload.upoad(request);
		PrintWriter writer = response.getWriter();
		response.setContentType("text/json");
		// List items = new ArrayList();
		// // JSONArray json = new JSONArray();
		// if (CollectionUtils.isNotEmpty(result)) {
		// for (UploadResult uploadResult : result) {
		// // JSONObject jsono = new JSONObject();
		// Map jsono = new HashMap();
		// jsono.put("name", uploadResult.getName());
		// jsono.put("key", uploadResult.getGenerateFileName().substring(0, uploadResult.getGenerateFileName().lastIndexOf(".") - 1));
		// jsono.put("size", uploadResult.getSize());
		// jsono.put("url", "/UploadServlet?getfile=" + uploadResult.getGenerateFileName());
		// jsono.put("thumbnailUrl", "/UploadServlet?getthumb=" + uploadResult.getGenerateFileName());
		// // 如果是word,ppt,pdf文档，则生成不同的预览图
		// if (StringUtils.isNotEmpty(uploadResult.getGenerateFileName())) {
		// if (uploadResult.getGenerateFileName().toLowerCase().endsWith("doc")
		// || uploadResult.getGenerateFileName().toLowerCase().endsWith("ppt")
		// || uploadResult.getGenerateFileName().toLowerCase().endsWith("docx")) {
		// jsono.put("thumbnailUrl", "/UploadServlet?getthumb=" + this.WORD_PREVIEW_FILE);
		// }
		// }
		// jsono.put("deleteUrl", "/UploadServlet?delfile=" + uploadResult.getGenerateFileName());
		// jsono.put("deleteType", "GET");
		// items.add(jsono);
		// }
		// }
		//
		// String res = "{\"files\":";
		// res += Json.toJson(items);
		// res += "}";
		String res = Upload.uploadResults2Json(result);
		writer.write(res);
		writer.close();

	}

	private String getMimeType(File file) {
		String mimetype = "";
		if (file.exists()) {
			String suffix = this.getSuffix(file.getName());
			if (getSuffix(file.getName()).equalsIgnoreCase("png")) {
				mimetype = "image/png";
			} else if (getSuffix(file.getName()).equalsIgnoreCase("jpg")) {
				mimetype = "image/jpg";
			} else if (getSuffix(file.getName()).equalsIgnoreCase("jpeg")) {
				mimetype = "image/jpeg";
			} else if (getSuffix(file.getName()).equalsIgnoreCase("gif")) {
				mimetype = "image/gif";
			} else if (suffix.equalsIgnoreCase("doc") || suffix.equalsIgnoreCase("docx")) {
				mimetype = "application/x-msword";
				// application/msword
				// application/vnd.ms-word
			} else if (suffix.equalsIgnoreCase("xls") || suffix.equalsIgnoreCase("xlsx")) {
				mimetype = "application/vnd.ms-excel";
			} else if (suffix.equalsIgnoreCase("pdf")) {
				mimetype = "application/pdf";
			} else {
				javax.activation.MimetypesFileTypeMap mtMap = new javax.activation.MimetypesFileTypeMap();
				mimetype = mtMap.getContentType(file);
			}
		}
		return mimetype;
	}

	private String getSuffix(String filename) {
		String suffix = "";
		int pos = filename.lastIndexOf('.');
		if (pos > 0 && pos < filename.length() - 1) {
			suffix = filename.substring(pos + 1);
		}
		return suffix;
	}

	/**
	 * 取得今天的文件存储目录
	 * 
	 * @todo
	 * @return
	 * @author:@haipenge haipenge@gmail.com 2014年2月19日
	 */
	// private String getTodayDir() {
	// String dir = "";
	// dir = DateUtil.formatDate(new Date(), "yyyyMMdd");
	// return dir;
	// }

	// private String getUploadPath() {
	// PropertyService propertyService=BeanContextUtil.getInstance().getBean(PropertyService.class);
	// String path=propertyService.get("upload.dir");
	// String result = "";
	// String dir = this.getTodayDir();
	// result = path + "/" + dir;
	// this.initPath(result);
	// return result;
	// }

	/**
	 * 根据文件名反推存储目录
	 * @todo
	 * @param filename
	 * @return
	 * @author:@haipenge
	 * haipenge@gmail.com
	 * 2014年8月19日
	 */
	// private String getUploadPathByFileName(String filename){
	// String res="";
	//
	// return res;
	// }

	// private void initPath(String path) {
	// File file = new File(path);
	// if (!file.exists()) {
	// file.mkdir();
	// }
	// }
}
