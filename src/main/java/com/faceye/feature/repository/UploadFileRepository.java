package com.faceye.feature.repository;

import com.faceye.feature.entity.UploadFile;
import com.faceye.feature.repository.mongo.BaseMongoRepository;

/**
 * 文件上传
 * 
 * @author haipenge
 *
 */
public interface UploadFileRepository extends BaseMongoRepository<UploadFile, Long> {

	public UploadFile getUploadFileByUrl(String url);
}
