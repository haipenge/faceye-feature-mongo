package com.faceye.feature.service;

import java.util.Map;

import org.springframework.data.domain.Page;

import com.faceye.feature.doc.Sequence;

/**
 * 序列服务，当前为mogo提供序列服务
 * @author @haipenge 
 * haipenge@gmail.com
*  Create Date:2014年8月10日
 */
public interface SequenceService {
	public Long getNextSequence(String name);

	public Sequence getSequence(Long id);

	public Sequence getSequenceByName(String name);

	public void save(Sequence sequence);
	
	public Page<Sequence> getSequenes(Map params);
	
	public void remove(Long id);
}
