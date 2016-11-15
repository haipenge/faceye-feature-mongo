package com.faceye.feature.util.host;

import org.apache.commons.lang3.StringUtils;

import com.faceye.feature.service.PropertyService;
import com.faceye.feature.util.bean.BeanContextUtil;

/**
 * 资源文件工具类
 * 
 * @author haipenge
 *
 */
public class PropertyUtil {
	public static String get(String key) {
		String res = "";
		if (StringUtils.isNotEmpty(key)) {
			res = BeanContextUtil.getInstance().getBean(PropertyService.class).get(key);
		}
		return res;
	}
}
