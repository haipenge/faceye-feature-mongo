package com.faceye.feature.repository.mongo.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.faceye.feature.doc.Sequence;
import com.faceye.feature.repository.mongo.SequenceRepository;

@Repository
public class SequenceRepositoryImpl implements SequenceRepository {
	@Autowired
	private MongoOperations mongoOps = null;

	private Logger logger = LoggerFactory.getLogger(SequenceRepository.class);

	@Override
synchronized	public Sequence getSequenceByName(String name) {
		Sequence sequence = null;
		if (StringUtils.isNotEmpty(name)) {
			logger.debug(">>FaceYe get sequence name is :" + name);
			Query query = new Query();
			query.addCriteria(Criteria.where("name").is(name));
			sequence = this.mongoOps.findOne(query, Sequence.class);
		}
		return sequence;
	}

	@Override
	public Sequence getSequence(Long id) {
		Query query = new Query();
		query.addCriteria(Criteria.where("id").is(id));
		Sequence sequence = this.mongoOps.findById(id, Sequence.class);
		return sequence;
	}

	@Override
	public void save(Sequence sequence) {
		if (sequence != null) {
			this.mongoOps.save(sequence);
		}
	}

	@Override
synchronized	public Sequence getSequenceWithNext(String name) {
		Sequence sequence = null;
		if (StringUtils.isNotEmpty(name)) {
			logger.debug(">>FaceYe getSequenceWithNext,name is:" + name);
			Query query = new Query();
			query.addCriteria(Criteria.where("name").is(name));
			Update update = new Update().inc("seq", 1);
			sequence = this.mongoOps.findAndModify(query, update, new FindAndModifyOptions().returnNew(true), Sequence.class);
			if (null == sequence) {
				sequence = new Sequence();
				sequence.setId(System.currentTimeMillis());
				sequence.setName(name);
				sequence.setSeq(1L);
				sequence.setStep(new Long(1));
				this.mongoOps.save(sequence);
			}
		} else {
			logger.debug(">>FaceYe --> get sequence ,name is empty.");
		}
		return sequence;
	}

	public Page<Sequence> getSequenes(Map params) {
		Page<Sequence> page = null;
		Query query = new Query();
		String name = MapUtils.getString(params, "name");
		if (StringUtils.isNotEmpty(name)) {
			query.addCriteria(Criteria.where("name").is(name));
		}
		query.with(new Sort(new Order(Direction.DESC, "name")));
		List<Sequence> sequences = this.mongoOps.find(query, Sequence.class);
		return new PageImpl<Sequence>(sequences);
	}

	@Override
	public void remove(Long id) {
		logger.debug(">>FaceYe --> Remove sequence id is:" + id);
		// Sequence seq = this.mongoOps.findById(id, Sequence.class);
		Query query = new Query();
		query.addCriteria(Criteria.where("id").is(id));

		// if (seq != null) {
		// logger.debug(">>FaceYe sequence will be remove,id is:" + id + ",name is:" + seq.getName());
		try {
			// this.mongoOps.remove(seq,"global_sequence");
			this.mongoOps.remove(query, Sequence.class);
		} catch (Exception e) {
			logger.error(">>FaceYe --> throws Exception sequence:", e);
		}
	}
	// }
}
