package com.faceye.feature.repository.mongo;

import java.io.Serializable;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
 public interface BaseMongoRepository<T,ID extends Serializable> extends MongoRepository<T,ID>,QuerydslPredicateExecutor<T>{
	
	
}
