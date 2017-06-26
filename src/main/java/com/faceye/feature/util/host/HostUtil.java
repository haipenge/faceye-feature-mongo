package com.faceye.feature.util.host;

import org.apache.commons.lang3.StringUtils;

import com.faceye.feature.service.PropertyService;
import com.faceye.feature.util.bean.BeanContextUtil;

public class HostUtil {
	private static String HOST = "";
	
	private static String IMAGE_SERVER="";
	/**
	 * 取得服务器域名地址：http://wwww.faceye.net
	 * @todo
	 * @return
	 * @author:@haipenge
	 * haipenge@gmail.com
	 * 2015年3月30日
	 */
	public static String getHost() {
		if (StringUtils.isEmpty(HOST)) {
			HOST = BeanContextUtil.getBean(PropertyService.class).get("faceye.host");
		}
		return HOST;
	}
	
	public static String getProperty(String key){
		String res="";
		if(StringUtils.isNotEmpty(key)){
			res=BeanContextUtil.getBean(PropertyService.class).get(key);
		}
		return res;
	}
	/**
	 * 取得图片服务器地址
	 * @todo
	 * @return
	 * @author:@haipenge
	 * haipenge@gmail.com
	 * 2015年3月30日
	 */
	public static String getImageServer(){
	   if(StringUtils.isEmpty(IMAGE_SERVER)){
		   IMAGE_SERVER=BeanContextUtil.getBean(PropertyService.class).get("image.server");
	   }
	   return IMAGE_SERVER;
	}
}