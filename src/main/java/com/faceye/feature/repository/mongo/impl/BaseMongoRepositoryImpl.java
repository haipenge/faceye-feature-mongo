package com.faceye.feature.repository.mongo.impl;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.QueryDslMongoRepository;

import com.faceye.feature.repository.mongo.BaseMongoRepository;
import com.faceye.feature.repository.mongo.DynamicSpecifications;
import com.querydsl.core.types.Predicate;

public class BaseMongoRepositoryImpl<T, ID extends Serializable> extends QueryDslMongoRepository<T, ID> implements BaseMongoRepository<T, ID> {

	protected Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	protected MongoOperations mongoOperations = null;

	private MongoEntityInformation entityInformation = null;

	private Class entityClass = null;

	public BaseMongoRepositoryImpl(MongoEntityInformation<T, ID> entityInformation, MongoOperations mongoOperations) {
		super(entityInformation, mongoOperations);
		this.mongoOperations = mongoOperations;
		this.entityInformation = entityInformation;
		entityClass = this.entityInformation.getJavaType();
	}

	@Override
	public Page<T> getPage(Map<String, Object> searchParams, int page, int size) {
		if (page != 0) {
			page = page - 1;
		}
		// SimpleEntityPathResolver resolver = SimpleEntityPathResolver.INSTANCE;
		// EntityPath<T> entityPath = resolver.createPath(entityClass);
		// PathBuilder<T> builder = new PathBuilder<T>(entityPath.getType(), entityPath.getMetadata());
		// Path path = entityPath.getRoot();
		// List<Predicate> predicates=DynamicSpecifications.buildPredicates(searchParams, entityClass);
		// Predicate predicate=DynamicSpecifications.builder(predicates);
		// NumberPath numberPath = new NumberPath(Number.class, path, "age");
		// predicates.add(numberPath.eq(15));
		Predicate predicate = DynamicSpecifications.builder(searchParams, entityClass);
		if (predicate != null) {
			logger.debug(">>FaceYe -->Query predicate is:" + predicate.toString());
		}
		Sort sort = new Sort(Direction.DESC, "id");
		Page<T> res = null;
		if (size != 0) {
			Pageable pageable = new PageRequest(page, size, sort);
			res = this.findAll(predicate, pageable);
		} else {
			// OrderSpecifier<Comparable> orderPOrderSpecifier=new OrderSpecifier<Comparable>(new Order(), new NumberExpression<T>("id") {
			// })
			List<T> items = (List) this.findAll(predicate, sort);
			res = new PageImpl<T>(items);
		}
		return res;
	}

	/**
	 * 构造sort对像,params 参数结构 :params.put("SORT|property","asc");
	 * 
	 * @param params
	 * @return
	 * @Desc:
	 * @Author:haipenge
	 * @Date:2017年2月27日 下午12:27:55
	 */
	protected Sort buildSort(Map<String, Object> params) {
		Sort sort = null;
		Iterator<String> it = params.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			if (StringUtils.startsWithIgnoreCase(key, "SORT")) {
				String property = StringUtils.split(key, "|")[1];
				String order = MapUtils.getString(params, key);
				if (isPropertyExist(property)) {
					Direction direction = Direction.ASC;
					if (StringUtils.equals(order, "asc")) {
						direction = Direction.ASC;
					} else {
						direction = Direction.DESC;
					}
					if (sort == null) {
						sort = new Sort(direction, property);
					} else {
						sort.and(new Sort(direction, property));
					}
				}
			}
		}
		if (sort == null) {
			sort = new Sort(Direction.DESC, "id");
		}
		return sort;
	}

	protected boolean isPropertyExist(String propertyName) {
		boolean isExist = false;
		Map properties = PropertyUtils.getMappedPropertyDescriptors(entityClass);
		if (properties != null && properties.containsKey(propertyName)) {
			isExist = true;
		}
		// PropertyUtils.getMappedPropertyDescriptors(beanClass)
		return isExist;
	}

}
