package com.faceye.feature.service.impl;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.util.ClassUtils;

import com.faceye.feature.doc.Sequence;
import com.faceye.feature.repository.mongo.BaseMongoRepository;
import com.faceye.feature.service.BaseService;
import com.faceye.feature.service.Reporter;
import com.faceye.feature.service.SequenceService;
import com.faceye.feature.util.ServiceException;

/**
 * 基于Mongo的底层服务实现
 * 
 * @author @haipenge haipenge@gmail.com Create Date:2014年8月10日
 * @param <T>
 * @param <ID>
 * @param <D>
 */
public abstract class BaseMongoServiceImpl<T, ID extends Serializable, D extends BaseMongoRepository<T, ID>> implements BaseService<T, ID> {

	protected Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	public SequenceService sequenceService = null;

	@Autowired
	protected Reporter reporter = null;
	protected D dao = null;
	protected Class<T> entityClass = null;

	public BaseMongoServiceImpl(D dao) {
		this.dao = dao;
		Type genType = getClass().getGenericSuperclass();
		Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
		entityClass = (Class<T>) params[0];
	}

	@Override
	public void save(T entity) throws ServiceException {
		try {
			ID id = (ID) PropertyUtils.getProperty(entity, "id");
			if (id == null) {
				PropertyUtils.setProperty(entity, "id", this.sequenceService.getNextSequence(entityClass.getName()));
			}
		} catch (IllegalAccessException e) {
			logger.error(">>FaceYe throws Exception: --->" + e.toString(), e);
		} catch (InvocationTargetException e) {
			logger.error(">>FaceYe throws Exception: --->" + e.toString(), e);
		} catch (NoSuchMethodException e) {
			logger.error(">>FaceYe throws Exception: --->" + e.toString(), e);
		}
		dao.save(entity);
	}

	public void save(Iterable<T> entities) throws ServiceException {
		if (entities != null) {
			Iterator<T> iterator = entities.iterator();
			while (iterator.hasNext()) {
				T entity = iterator.next();
				ID id;
				try {
					id = (ID) PropertyUtils.getProperty(entity, "id");
					if (id == null) {
						PropertyUtils.setProperty(entity, "id", this.sequenceService.getNextSequence(entityClass.getName()));
					}
				} catch (IllegalAccessException e) {
					logger.error(">>FaceYe throws Exception: --->" + e.toString());
				} catch (InvocationTargetException e) {
					logger.error(">>FaceYe throws Exception: --->" + e.toString());
				} catch (NoSuchMethodException e) {
					logger.error(">>FaceYe throws Exception: --->" + e.toString());
				}
			}
		}
		dao.save(entities);
	}

	@Override
	public void saveAndFlush(T entity) throws ServiceException {
		save(entity);
	}

	@Override
	public void remove(ID id) throws ServiceException {
		dao.delete(id);
	}

	@Override
	public void remove(T entity) throws ServiceException {
		dao.delete(entity);
	}

	@Override
	public void removeAll() throws ServiceException {
		dao.deleteAll();
	}

	@Override
	public void removeAllInBatch() throws ServiceException {
		this.removeAll();
	}

	@Override
	public void removeInBatch(Iterable<T> entities) throws ServiceException {
		this.dao.delete(entities);
	}

	@Override
	public T get(ID id) throws ServiceException {
		return dao.findOne(id);
	}

	@Override
	public List<T> getAll() throws ServiceException {
		return dao.findAll();
	}

	@Override
	public List<T> getAll(Iterable<ID> ids) throws ServiceException {
		List<T> res = null;
		Iterable<T> its = dao.findAll(ids);
		res = (List) its;
		return res;
	}

	@Override
	public Page<T> getPage(Map<String, Object> searchParams, int page, int size) throws ServiceException {
		// if (page != 0) {
		// page = page - 1;
		// }
		// SimpleEntityPathResolver resolver = SimpleEntityPathResolver.INSTANCE;
		// EntityPath<T> entityPath = resolver.createPath(entityClass);
		// PathBuilder<T> builder = new PathBuilder<T>(entityPath.getType(), entityPath.getMetadata());
		// Path path = entityPath.getRoot();
		// List<Predicate> predicates=DynamicSpecifications.buildPredicates(searchParams, entityClass);
		// Predicate predicate=DynamicSpecifications.builder(predicates);
		// NumberPath numberPath = new NumberPath(Number.class, path, "age");
		// predicates.add(numberPath.eq(15));
		// Predicate predicate = DynamicSpecifications.builder(searchParams, entityClass);
		// if (predicate != null) {
		// logger.debug(">>FaceYe -->Query predicate is:" + predicate.toString());
		// }
		// Sort sort = new Sort(Direction.DESC, "id");
		// Page<T> res = null;
		// if (size != 0) {
		// Pageable pageable = new PageRequest(page, size, sort);
		// res = this.dao.findAll(predicate, pageable);
		// } else {
		// // OrderSpecifier<Comparable> orderPOrderSpecifier=new OrderSpecifier<Comparable>(new Order(), new NumberExpression<T>("id") {
		// // })
		// List<T> items = (List) this.dao.findAll(predicate,sort);
		// res = new PageImpl<T>(items);
		// }
		return this.dao.getPage(searchParams, page, size);
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
				 boolean isPropertyExist=isPropertyExist(property);
				 if (isPropertyExist) {
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
				 }else{
				 logger.debug(">>FaceYe --> proeprty :"+property+" not exist in bean: "+entityClass.getName());
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
        Field [] fields=entityClass.getDeclaredFields();
        if(fields!=null){
        	for(Field field:fields){
        		String name=field.getName();
        		if(StringUtils.equalsIgnoreCase(name, propertyName)){
        			isExist=true;
        			break;
        		}
        	}
        }
//		Map properties = PropertyUtils.getMappedPropertyDescriptors(entityClass);
//		if (properties != null && properties.containsKey(propertyName)) {
//			isExist = true;
//		}
		// PropertyUtils.getMappedPropertyDescriptors(beanClass)
		return isExist;
	}

	/**
	 * 取得实体当前的最大ID
	 * 
	 * @todo
	 * @return
	 * @author:@haipenge haipenge@gmail.com 2015年2月10日
	 */
	public void resetEntitySequenceID() {
		Long maxId = 0L;
		Sort sort = new Sort(Direction.DESC, "id");
		Pageable pageable = new PageRequest(0, 1, sort);
		Page<T> page = this.dao.findAll(pageable);
		if (page != null && CollectionUtils.isNotEmpty(page.getContent())) {
			T entity = page.getContent().get(0);
			if (ClassUtils.hasAtLeastOneMethodWithName(entityClass, "getId")) {
				try {
					Object id = PropertyUtils.getProperty(entity, "id");
					if (null != id) {
						maxId = (Long) id;
					}
				} catch (IllegalAccessException e) {
					logger.error(">>FaceYe throws Exception: --->" + e.toString());
				} catch (InvocationTargetException e) {
					logger.error(">>FaceYe throws Exception: --->" + e.toString());
				} catch (NoSuchMethodException e) {
					logger.error(">>FaceYe throws Exception: --->" + e.toString());
				}
			}
		}
		if (entityClass != null) {
			Sequence sequence = this.sequenceService.getSequenceByName(entityClass.getName());
			sequence.setSeq(maxId);
			logger.debug(">>FaceYe --Reset entity class " + entityClass + " sequence id to :" + maxId);
			this.sequenceService.save(sequence);
		} else {
			logger.debug(">>FaceYe -- reset entity max id in " + getClass().getName() + ",entity is null.");
		}
	}

}