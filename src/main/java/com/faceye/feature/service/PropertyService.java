package com.faceye.feature.service;

import com.faceye.feature.util.ServiceException;

public interface PropertyService {

	/**
	 * 取得property 中的值
	 * @param key
	 * @return
	 * @
	 * @Date:Create Date:2014年1月2日
	 * @Author @haipenge
	 */
	public String get(String key) ;
	
	
	public String [] getKeys() ;
	
	
}
