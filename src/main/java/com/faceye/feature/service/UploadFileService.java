package com.faceye.feature.service;

import java.util.List;

import com.faceye.feature.entity.UploadFile;

/**
 * 文件上传
 * @author haipenge
 *
 */
public interface UploadFileService extends BaseService<UploadFile, Long>{

	/**
	 * 
	 * @param storePath->存储路径
	 * @param url-->对外访问路径
	 * @param type-->文件类型
	 * @param key-->特别标识key，用于不同项目中targetEntityId+targetEntityClassName不唯一的情况
	 * @param targetEntityId->关联对像ID
	 * @param targetEntityClassName->关联对像名
	 * @return
	 * @Desc:
	 * @Author:haipenge
	 * @Date:2016年5月17日 上午10:37:13
	 */
	public UploadFile save(String storePath,String url,String type,String key,Long targetEntityId,String targetEntityClassName);
	
	/**
	 * 
	 * @param storePath
	 * @param url
	 * @param targetEntityId
	 * @param targetEntityClassName
	 * @return
	 * @Desc:
	 * @Author:haipenge
	 * @Date:2016年5月17日 上午10:39:22
	 */
	public UploadFile save(String storePath,String url,Long targetEntityId,String targetEntityClassName);
	
	public UploadFile getUploadFileByUrl(String url);

	public List<UploadFile> getUploadFilesByTargetEntityIdAndTargetEntityClass(Long targetEntityId,String targetEntityClassName);
}
