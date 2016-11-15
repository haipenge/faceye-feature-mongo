package com.faceye.feature.repository.mongo;

import java.util.Map;

import org.springframework.data.domain.Page;

import com.faceye.feature.doc.Sequence;

public interface SequenceRepository {

	public Sequence getSequenceByName(String name);
	
	/**
	 * 取得带有下一个序列值的sequence
	 * @todo
	 * @param name
	 * @return
	 * @author:@haipenge
	 * haipenge@gmail.com
	 * 2015年2月10日
	 */
	public Sequence getSequenceWithNext(String name);

	public Sequence getSequence(Long id);

	public void save(Sequence sequence);
	
	public Page<Sequence> getSequenes(Map params);
	
	
	public void remove(Long id);
	
}
