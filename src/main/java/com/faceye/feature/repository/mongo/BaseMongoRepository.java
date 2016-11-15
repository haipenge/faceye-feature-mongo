package com.faceye.feature.repository.mongo;

import java.io.Serializable;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.NoRepositoryBean;
@NoRepositoryBean
abstract public interface BaseMongoRepository<T,ID extends Serializable> extends MongoRepository<T, ID>,QueryDslPredicateExecutor<T>{
	public Page<T> getPage(Map<String, Object> searchParams, int page, int size);
}
