package com.faceye.test.feature.repository;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.faceye.feature.doc.Sequence;
import com.faceye.feature.repository.mongo.SequenceRepository;

public class SequenceRepositoryTestCase extends BaseRepositoryTestCase {
	@Autowired
	private SequenceRepository sequenceRepository;
	
	@Test
	public void testGetSequenceByName() throws Exception{
		String name="com.faceye.component.book.doc.Book";
		Sequence sequence=this.sequenceRepository.getSequenceByName(name);
		Assert.isTrue(sequence!=null);
	}

}
