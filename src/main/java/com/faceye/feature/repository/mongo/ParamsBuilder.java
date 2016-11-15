package com.faceye.feature.repository.mongo;

import java.util.HashMap;
import java.util.Map;

/**
 * 参数构造器
 * 
 * @author haipenge
 *
 */
public class ParamsBuilder {
	private Map params = null;
	private static ParamsBuilder paramsBuilder = null;

	private ParamsBuilder() {
		params = new HashMap();
	}
	public ParamsBuilder put(Object key, Object value) {
		if (paramsBuilder == null) {
			paramsBuilder = new ParamsBuilder();
		}
		params.put(key, value);
		return paramsBuilder;
	}

	public Map getParams() {
		return params;
	}

}
