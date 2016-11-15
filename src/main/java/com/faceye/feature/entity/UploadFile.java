package com.faceye.feature.entity;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.faceye.feature.service.PropertyService;
import com.faceye.feature.util.bean.BeanContextUtil;

@Document(collection = "global_upload_file")
public class UploadFile {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8926119711730830203L;
	@Id
	private Long id = null;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * 说明:存储路径-》相对路径 属性名: storePath 类型: String 数据库字段:store_path
	 * 
	 * @author haipenge
	 */

	private String storePath;

	public String getStorePath() {
		return storePath;
	}

	public void setStorePath(String storePath) {
		this.storePath = storePath;
	}

	/**
	 * 文件对外访问路径
	 */
	private String url = "";

	public String getUrl() {
		if (StringUtils.isEmpty(url)) {
			String imgHost = BeanContextUtil.getInstance().getBean(PropertyService.class).get("image.server");
			if (StringUtils.isNotEmpty(imgHost)) {
				url = imgHost + storePath;
			}
		}
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * 创建时间
	 */
	private Date createDate = new Date();

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	/**
	 * 图片所属实体ID
	 */
	private Long targetEntityId = null;

	public Long getTargetEntityId() {
		return targetEntityId;
	}

	public void setTargetEntityId(Long targetEntityId) {
		this.targetEntityId = targetEntityId;
	}

	/*
	 * 图片所属实体名称 -class.getName();
	 */
	private String targetEntityClassName = "";

	public String getTargetEntityClassName() {
		return targetEntityClassName;
	}

	public void setTargetEntityClassName(String targetEntityClassName) {
		this.targetEntityClassName = targetEntityClassName;
	}

	/**
	 * 特殊标识KEY，唯一
	 */
	private String key = "";

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * 文件类型
	 */
	private String type = "";

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
