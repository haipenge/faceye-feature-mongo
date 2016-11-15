package com.faceye.feature.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.faceye.feature.entity.UploadFile;
import com.faceye.feature.repository.UploadFileRepository;
import com.faceye.feature.service.UploadFileService;

/**
 * 文件上传
 * 
 * @author haipenge
 *
 */
@Service
public class UploadFileServiceImpl extends BaseMongoServiceImpl<UploadFile, Long, UploadFileRepository> implements UploadFileService {
	@Autowired
	public UploadFileServiceImpl(UploadFileRepository dao) {
		super(dao);
	}

	@Override
	public UploadFile save(String storePath, String url, String type, String key, Long targetEntityId, String targetEntityClassName) {
		UploadFile uploadFile = new UploadFile();
		uploadFile.setStorePath(storePath);
		uploadFile.setUrl(url);
		uploadFile.setType(type);
		uploadFile.setTargetEntityClassName(targetEntityClassName);
		uploadFile.setTargetEntityId(targetEntityId);
		this.save(uploadFile);
		return uploadFile;
	}

	@Override
	public UploadFile save(String storePath, String url, Long targetEntityId, String targetEntityClassName) {
		return this.save(storePath, url, "", "", targetEntityId, targetEntityClassName);
	}

	@Override
	public UploadFile getUploadFileByUrl(String url) {
		return this.dao.getUploadFileByUrl(url);
	}

	@Override
	public List<UploadFile> getUploadFilesByTargetEntityIdAndTargetEntityClass(Long targetEntityId, String targetEntityClassName) {
		Map searchParams=new HashMap();
		searchParams.put("EQ|targetEntityId", targetEntityId);
		searchParams.put("EQ|targetEntityClassName", targetEntityClassName);
		List<UploadFile> uploadFiles=this.getPage(searchParams, 1, 0).getContent();
		return uploadFiles;
	}

	
}
