package com.faceye.feature.repository.mongo.impl;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
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

public class BaseMongoRepositoryImpl<T, ID extends Serializable> extends QueryDslMongoRepository<T, ID>
		implements BaseMongoRepository<T, ID> {

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
		// SimpleEntityPathResolver resolver =
		// SimpleEntityPathResolver.INSTANCE;
		// EntityPath<T> entityPath = resolver.createPath(entityClass);
		// PathBuilder<T> builder = new PathBuilder<T>(entityPath.getType(),
		// entityPath.getMetadata());
		// Path path = entityPath.getRoot();
		// List<Predicate>
		// predicates=DynamicSpecifications.buildPredicates(searchParams,
		// entityClass);
		// Predicate predicate=DynamicSpecifications.builder(predicates);
		// NumberPath numberPath = new NumberPath(Number.class, path, "age");
		// predicates.add(numberPath.eq(15));
		Predicate predicate = DynamicSpecifications.builder(searchParams, entityClass);
		if (predicate != null) {
			logger.debug(">>FaceYe -->Query predicate is:" + predicate.toString());
		}else{
			logger.debug(">>FaceYe --> predicate is null.");
		}
		Sort sort = this.buildSort(searchParams);
		Page<T> res = null;
		if (size != 0) {
			Pageable pageable = new PageRequest(page, size, sort);
			res = this.findAll(predicate, pageable);
		} else {
			// OrderSpecifier<Comparable> orderPOrderSpecifier=new
			// OrderSpecifier<Comparable>(new Order(), new
			// NumberExpression<T>("id") {
			// })
			List<T> items = (List) this.findAll(predicate, sort);
			res = new PageImpl<T>(items);
		}
		return res;
	}

	/**
	 * 构造sort对像,params 参数结构 :params.put("SORT|property","asc"); 如果有多个排序key
	 * params.put("SORT|property:0","asc"); params.put("SORT|property:1","asc");
	 * ... 排序将以0...n的方式进行
	 * 
	 * @param params
	 * @return
	 * @Desc:
	 * @Author:haipenge
	 * @Date:2017年2月27日 下午12:27:55
	 */
	protected Sort buildSort(Map<String, Object> params) {
		Sort sort = null;
		List<Map<Sort, Integer>> sorts = new ArrayList<Map<Sort, Integer>>(0);
		params=this.filterDuplicateKey(params);
		if (MapUtils.isNotEmpty(params)) {
			Iterator<String> it = params.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				if (StringUtils.startsWithIgnoreCase(key, "SORT")) {
					String property = StringUtils.split(key, "|")[1];
					String realProperty = "";
					Integer index = 0;
					if (StringUtils.contains(property, ":")) {
						String[] splits = StringUtils.split(property, ":");
						realProperty = StringUtils.trim(splits[0]);
						index = Integer.parseInt(StringUtils.trim(splits[1]));
					} else {
						realProperty = property;
					}
					String order = MapUtils.getString(params, key);
					boolean isPropertyExist = isPropertyExist(realProperty);
					if (isPropertyExist) {
						Direction direction = Direction.ASC;
						if (StringUtils.equalsIgnoreCase(order, "asc")) {
							direction = Direction.ASC;
						} else {
							direction = Direction.DESC;
						}
						Map<Sort, Integer> map = new HashMap<Sort, Integer>();
						Sort subSort = new Sort(direction, realProperty);
						map.put(subSort, index);
						sorts.add(map);
					} else {
						logger.debug(
								">>FaceYe --> proeprty :" + property + " not exist in bean: " + entityClass.getName());
					}
				}
			}
		}
		if (CollectionUtils.isNotEmpty(sorts)) {
			Collections.sort(sorts, new SortComparator());
			for (Map<Sort, Integer> map : sorts) {
				Sort inSort = map.keySet().iterator().next();
				if (sort == null) {
					sort = inSort;
				} else {
					sort.and(inSort);
				}
			}
		}
		if (sort == null) {
			sort = new Sort(Direction.DESC, "id");
		}
		return sort;
	}

	/**
	 * 对排序key的排序
	 * 
	 * @author haipenge
	 *
	 */
	class SortComparator implements Comparator<Map<Sort, Integer>> {
		@Override
		public int compare(Map<Sort, Integer> o1, Map<Sort, Integer> o2) {
			int res = 0;
			if (o1 != null && o2 != null) {
				Integer v1 = o1.values().iterator().next();
				Integer v2 = o2.values().iterator().next();
				res = v1.compareTo(v2);
			}
			return res;
		}
	}

	protected boolean isPropertyExist(String propertyName) {
		boolean isExist = false;
		Field[] fields = entityClass.getDeclaredFields();
		if (fields != null) {
			for (Field field : fields) {
				String name = field.getName();
				if (StringUtils.equalsIgnoreCase(name, propertyName)) {
					isExist = true;
					break;
				}
			}
		}
		// todo ,多层对像，需逐层判断
		if (!isExist && StringUtils.contains(propertyName, ".")) {
			isExist = true;
		}
		// Map properties =
		// PropertyUtils.getMappedPropertyDescriptors(entityClass);
		// if (properties != null && properties.containsKey(propertyName)) {
		// isExist = true;
		// }
		// PropertyUtils.getMappedPropertyDescriptors(beanClass)
		return isExist;
	}

	/**
	 * 对KEY进行去重
	 * @return
	 * @Desc:
	 * @Author:haipenge
	 * @Date:2017年7月15日 下午7:57:01
	 */
	private Map filterDuplicateKey(Map<String,Object> params){
		Map map=new HashMap<String,Object>();
		if(MapUtils.isNotEmpty(params)){
			Iterator<String> it=params.keySet().iterator();
			while(it.hasNext()){
				String key=it.next();
				Object value=MapUtils.getObject(params, key);
				if(!this.isMapContainsIgnoreCaseKey(map, key)){
					map.put(key, value);
				}
			}
		}
		return map;
	}
	
	private boolean isMapContainsIgnoreCaseKey(Map<String,Object> map,String key){
		boolean res=false;
	    Iterator<String> it=map.keySet().iterator();
	    while(it.hasNext()){
	    	String mapKey=it.next();
	    	res=StringUtils.equalsIgnoreCase(mapKey, key);
	    }
		return res;
	}
}
