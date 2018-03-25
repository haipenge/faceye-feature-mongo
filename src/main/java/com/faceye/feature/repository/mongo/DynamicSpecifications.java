package com.faceye.feature.repository.mongo;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.querydsl.SimpleEntityPathResolver;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import com.faceye.feature.repository.SearchFilter;
import com.faceye.feature.repository.SearchFilter.Operator;
import com.google.common.collect.Lists;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.ArrayPath;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.BooleanPath;
import com.querydsl.core.types.dsl.DatePath;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.core.types.dsl.SimplePath;
import com.querydsl.core.types.dsl.StringPath;

/**
 * Mongo DB Query DSL 动态参数解析
 * 
 * @author @haipenge haipenge@gmail.com Create Date:2015年2月12日
 */
public class DynamicSpecifications {
	private static Logger logger = LoggerFactory.getLogger(DynamicSpecifications.class);

	public static <T> Predicate builder(Map searchParams, Class<T> clazz) {
		List<Predicate> predicates = buildPredicates(searchParams, clazz);
		Predicate predicate = null;
		predicate = builder(predicates);
		if (null != predicate) {
			logger.debug(">>FaceYe --> Predicate is :" + predicate.toString());
		}
		return predicate;
	}

	/**
	 * 拼装查询条件
	 * 
	 * @todo
	 * @param predicates
	 * @return
	 * @author:@haipenge 联系:haipenge@gmail.com 创建时间:2015年5月6日
	 */
	public static <T> Predicate builder(List<Predicate> predicates) {
		Predicate predicate = null;
		BooleanBuilder booleanBuilder = new BooleanBuilder();
		if (CollectionUtils.isNotEmpty(predicates)) {
			for (Predicate p : predicates) {
				booleanBuilder.and(p);
			}
		}
		predicate = booleanBuilder.getValue();
		return predicate;
	}

	/**
	 * 构建查询条件
	 * 
	 * @todo
	 * @param searchParams
	 * @param clazz
	 * @return
	 * @author:@haipenge 联系:haipenge@gmail.com 创建时间:2015年5月6日
	 */
	public static <T> List<Predicate> buildPredicates(Map searchParams, Class<T> clazz) {
		List<Predicate> predicates = Lists.newArrayList();
		Predicate predicate = null;
		Map<String, SearchFilter> searchFilters = SearchFilter.parse(searchParams);
		SimpleEntityPathResolver resolver = SimpleEntityPathResolver.INSTANCE;
		EntityPath<T> entityPath = resolver.createPath(clazz);
		PathBuilder<T> builder = new PathBuilder<T>(entityPath.getType(), entityPath.getMetadata());
		Path<?> path = entityPath.getRoot();

		if (MapUtils.isNotEmpty(searchFilters)) {
			for (SearchFilter searchFilter : searchFilters.values()) {
				Operator operator = searchFilter.operator;
				String fieldName = searchFilter.fieldName;
				Object fieldValue = searchFilter.value;
				boolean isNumber = false;
				if (fieldValue == null || StringUtils.isEmpty(fieldValue.toString())) {
					continue;
				}
				isNumber = NumberUtils.isNumber(fieldValue == null ? null : fieldValue.toString());
				// logger.debug(">>FaceYe trace query predicate,isNumber ? " + fieldValue + "," + isNumber + ",operator is:" + operator);
				switch (operator) {
				case EQ:
					if (fieldValue != null) {
						if (isNumber || fieldValue instanceof Number) {
							if (StringUtils.contains(fieldName, "$id")) {
								Object instance=getObjectReferenceInstance(clazz,fieldName,fieldValue.toString());
								String property=StringUtils.replace(fieldName, ".$id", "");
//								SimplePath simplePath =createPath(builder, clazz, fieldName, fieldValue.toString());
								Class propertyClass = ReflectionUtils.findField(clazz, property).getType();
								SimplePath simplePath = builder.getSimple(property, propertyClass);
								predicates.add(simplePath.eq(instance));
							} else {
								NumberPath numberPath = (NumberPath) createPath(builder, clazz, fieldName, fieldValue.toString());
								predicates.add(numberPath.eq(NumberUtils.createNumber(fieldValue.toString())));
							}
						} else if (fieldValue instanceof String) {
							// StringPath stringPath = new StringPath(path, fieldName);
							StringPath stringPath = builder.getString(fieldName);
							predicates.add(stringPath.eq(fieldValue.toString()));
						} else {
							SimplePath simplePath = builder.getSimple(fieldName, fieldValue.getClass());
							predicates.add(simplePath.eq(fieldValue));
						}
					} else {
						logger.debug(">>FaceYe --> field value is empty of field" + fieldName);
					}
					break;
				case NE:
					if (fieldValue != null) {
						if (isNumber || fieldValue instanceof Number) {
							// NumberPath numberPath = new NumberPath(Number.class, path, fieldName);
							NumberPath numberPath = (NumberPath) createPath(builder, clazz, fieldName, fieldValue.toString());
							predicates.add(numberPath.ne(NumberUtils.createNumber(fieldValue.toString())));
						} else if (fieldValue instanceof String) {
							// StringPath stringPath = new StringPath(path, fieldName);
							StringPath stringPath = builder.getString(fieldName);
							predicates.add(stringPath.ne(fieldValue.toString()));
						} else {
							SimplePath simplePath = builder.getSimple(fieldName, fieldValue.getClass());
							predicates.add(simplePath.ne(fieldValue));
						}
					} else {
						logger.debug(">>FaceYe --> field value is empty of field" + fieldName);
					}
					break;
				case GT:
					if (fieldValue != null) {
						if (isNumber || fieldValue instanceof Number) {
							// NumberPath numberPath = new NumberPath(Number.class, path, fieldName);
							NumberPath numberPath = (NumberPath) createPath(builder, clazz, fieldName, fieldValue.toString());
							predicates.add(numberPath.gt(NumberUtils.createNumber(fieldValue.toString())));
						} else if (fieldValue instanceof String) {
							StringPath stringPath = builder.getString(fieldName);
							predicates.add(stringPath.gt(fieldValue.toString()));
						} else if (fieldValue instanceof Date) {
							DatePath datePath = builder.getDate(fieldName, Date.class);
							Date date = (Date) fieldValue;
							predicates.add(datePath.gt(date));
						} else {

						}
					} else {
						logger.debug(">>FaceYe --> field value is empty of field" + fieldName);
					}
					break;
				case GTE:
					if (fieldValue != null) {
						if (isNumber || fieldValue instanceof Number) {
							// NumberPath numberPath = new NumberPath(Number.class, path, fieldName);
							NumberPath numberPath = (NumberPath) createPath(builder, clazz, fieldName, fieldValue.toString());
							predicates.add(numberPath.goe(NumberUtils.createNumber(fieldValue.toString())));
						} else if (fieldValue instanceof String) {
							StringPath stringPath = builder.getString(fieldName);
							predicates.add(stringPath.goe(fieldValue.toString()));
						} else if (fieldValue instanceof Date) {
							DatePath datePath = builder.getDate(fieldName, Date.class);
							Date date = (Date) fieldValue;
							predicates.add(datePath.goe(date));
						} else {

						}
					} else {
						logger.debug(">>FaceYe --> field value is empty of field" + fieldName);
					}
					break;
				case LT:
					if (fieldValue != null) {
						if (isNumber || fieldValue instanceof Number) {
							// NumberPath numberPath = new NumberPath(Number.class, path, fieldName);
							NumberPath numberPath = (NumberPath) createPath(builder, clazz, fieldName, fieldValue.toString());
							predicates.add(numberPath.lt(NumberUtils.createNumber(fieldValue.toString())));
						} else if (fieldValue instanceof String) {
							StringPath stringPath = builder.getString(fieldName);
							predicates.add(stringPath.lt(fieldValue.toString()));
						} else if (fieldValue instanceof Date) {
							DatePath datePath = builder.getDate(fieldName, Date.class);
							Date date = (Date) fieldValue;
							predicates.add(datePath.lt(date));
						} else {

						}
					} else {
						logger.debug(">>FaceYe --> field value is empty of field" + fieldName);
					}
					break;
				case LTE:
					if (fieldValue != null) {
						if (isNumber || fieldValue instanceof Number) {
							// NumberPath numberPath = new NumberPath(Number.class, path, fieldName);
							NumberPath numberPath = (NumberPath) createPath(builder, clazz, fieldName, fieldValue.toString());
							predicates.add(numberPath.loe(NumberUtils.createNumber(fieldValue.toString())));
						} else if (fieldValue instanceof String) {
							StringPath stringPath = builder.getString(fieldName);
							predicates.add(stringPath.loe(fieldValue.toString()));
						} else if (fieldValue instanceof Date) {
							DatePath datePath = builder.getDate(fieldName, Date.class);
							Date date = (Date) fieldValue;
							predicates.add(datePath.loe(date));
						} else {

						}
					} else {
						logger.debug(">>FaceYe --> field value is empty of field" + fieldName);
					}
					break;
				case LIKE:
					if (fieldValue instanceof String) {
						// StringPath stringPath = new StringPath(path, fieldName);
						StringPath stringPath = builder.getString(fieldName);
						predicates.add(stringPath.like("%" + fieldValue.toString() + "%"));
					} else {
						logger.debug(">>FaceYe --> field value is empty of field" + fieldName);
					}
					break;
				case ISTRUE:
					// BooleanPath isTrueBooleanPath = new BooleanPath(path, fieldName);
					BooleanPath isTrueBooleanPath = builder.getBoolean(fieldName);
					predicates.add(isTrueBooleanPath.isTrue());
					break;
				case ISFALSE:
					// BooleanPath isFalseBooleanPath = new BooleanPath(path, fieldName);
					BooleanPath isFalseBooleanPath = builder.getBoolean(fieldName);
					predicates.add(isFalseBooleanPath.isFalse());
					break;
				case BOOLEAN:
					// BooleanPath booleanPath = new BooleanPath(path, fieldName);
					BooleanPath booleanPath = builder.getBoolean(fieldName);
					BooleanExpression be = null;
					Boolean bool = null;
					if (fieldValue instanceof String) {
						if (StringUtils.equalsIgnoreCase(fieldValue.toString(), "true") || StringUtils.equalsIgnoreCase(fieldValue.toString(), "yes")) {
							bool = true;
						} else if (StringUtils.equalsIgnoreCase(fieldValue.toString(), "false") || StringUtils.equalsIgnoreCase(fieldValue.toString(), "no")) {
							bool = false;
						}
					} else if (fieldValue instanceof Boolean) {
						bool = (Boolean) fieldValue;
					}
					if (bool != null) {
						be = bool ? booleanPath.isTrue() : booleanPath.isFalse();
						predicates.add(be);
					}
					break;
				case ISEMPTY:
					if (isNumber || fieldValue instanceof Number) {
						// NumberPath numberPath = new NumberPath(Number.class, path, fieldName);
						NumberPath numberPath = (NumberPath) createPath(builder, clazz, fieldName, fieldValue.toString());
						predicates.add(numberPath.isNull());
					} else if (fieldValue instanceof String) {
						StringPath stringPath = builder.getString(fieldName);
						predicates.add(stringPath.isEmpty());
					} else {
						SimplePath simplePath = builder.getSimple(fieldName, fieldValue.getClass());
						predicates.add(simplePath.isNull());
					}
					break;
				case ISNULL:
					if (isNumber || fieldValue instanceof Number) {
						// NumberPath numberPath = new NumberPath(fieldValue.getClass(), path, fieldName);
						NumberPath numberPath = (NumberPath) createPath(builder, clazz, fieldName, fieldValue.toString());
						predicates.add(numberPath.isNull());
					} else if (fieldValue instanceof String) {
						// StringPath stringPath = new StringPath(path, fieldName);
						StringPath stringPath = builder.getString(fieldName);
						predicates.add(stringPath.isNull());
					} else {
						SimplePath simplePath = builder.getSimple(fieldName, fieldValue.getClass());
						predicates.add(simplePath.isNull());
					}
					break;
				case IN:
					if (isNumber || fieldValue instanceof Number) {
						// NumberPath numberPath = new NumberPath(Number.class, path, fieldName);
						NumberPath numberPath = (NumberPath) createPath(builder, clazz, fieldName, fieldValue.toString());
						predicates.add(numberPath.in(new Number[] { -1, NumberUtils.createNumber(fieldValue.toString()) }));
					} else if (fieldValue instanceof Number[]) {
						// NumberPath numberPath = new NumberPath(Number.class, path, fieldName);
						// NumberPath numberPath =(NumberPath) createPath(builder,clazz,fieldName,fieldValue.toString());
						ArrayPath arrayPath = builder.getArray(fieldName, Number[].class);
						predicates.add(arrayPath.in((Number[]) fieldValue));
					} else if (fieldValue instanceof String) {
						// StringPath stringPath = new StringPath(path, fieldName);
						StringPath stringPath = builder.getString(fieldName);
						predicates.add(stringPath.in("", fieldValue.toString()));

					} else if (fieldValue instanceof String[]) {
						// StringPath stringPath = new StringPath(path, fieldName);
						// ArrayPath arrayPath = new ArrayPath(String[].class, path, fieldName);
						ArrayPath arrayPath = builder.getArray(fieldName, String[].class);
						predicates.add(arrayPath.in("", (String[]) fieldValue));
					} else if (fieldValue instanceof List) {
						List c = (List) fieldValue;
						Class queryType = c.get(0).getClass();
						// logger.debug(">>FaceYe -->fieldName :" + fieldName + ",queryType is:" + queryType);
						// CollectionPath collectionPath=builder.getCollection(fieldName, fieldValue.getClass(), queryType);
						SimplePath simplePath = builder.getSimple(fieldName, queryType);
						// predicates.add(collectionPath.contains(fieldValue));
						predicates.add(simplePath.in(c));
					} else {
						SimplePath simplePath = builder.getSimple(fieldName, fieldValue.getClass());
						predicates.add(simplePath.in(fieldValue));
					}
					break;
				case NOTIN:
					if (isNumber || fieldValue instanceof Number) {
						// NumberPath numberPath = new NumberPath(Number.class, path, fieldName);
						NumberPath numberPath = (NumberPath) createPath(builder, clazz, fieldName, fieldValue.toString());
						predicates.add(numberPath.notIn(new Number[] { -1, NumberUtils.createNumber(fieldValue.toString()) }));
					} else if (fieldValue instanceof String) {
						// StringPath stringPath = new StringPath(path, fieldName);
						StringPath stringPath = builder.getString(fieldName);
						predicates.add(stringPath.notIn("", fieldValue.toString()));
					} else {
						SimplePath simplePath = builder.getSimple(fieldName, fieldValue.getClass());
						predicates.add(simplePath.notIn(fieldValue));
					}
					break;
				default:
				}
			}
		}
		if (CollectionUtils.isEmpty(predicates)) {
			NumberPath numberPath = (NumberPath) createPath(builder, clazz, "id", "0");
			predicates.add(numberPath.gt(NumberUtils.createNumber("0")));
		}
		return predicates;
	}

	private static SimpleExpression<?> createPath(PathBuilder builder, Class clazz, String propertyName, String propertyValue) {
		boolean isNumber = NumberUtils.isNumber(propertyValue);
		if (isNumber) {
			if (StringUtils.contains(propertyName, "$id")) {
				// builder.as(clazz).
				String property = StringUtils.replace(propertyName, ".$id", "");
				Class propertyClass = ReflectionUtils.findField(clazz, property).getDeclaringClass();
				SimplePath simplePath = builder.getSimple(property, propertyClass);
				// return builder.getNumber(propertyName, Long.class);
				// return simplePath.eq(instance);
				// Path p=null;
				return simplePath;
			} else {
				return createPath(builder, clazz, propertyName);
			}
		} else {
			return createPath(builder, clazz, propertyName);
		}
	}

	private static Object getObjectReferenceInstance(Class clazz, String propertyName, String propertyValue) {
		Object instance = null;
		String property = StringUtils.replace(propertyName, ".$id", "");
		Class propertyClass = ReflectionUtils.findField(clazz, property).getType();
		// return builder.getNumber(propertyName, Long.class);
		Field id = null;
		try {
			instance = propertyClass.newInstance();
//			ReflectionUtils.find
			id=ReflectionUtils.findField(propertyClass, "id");
			ReflectionUtils.makeAccessible(id);
//			id = propertyClass.getField("id");
//			id.setAccessible(true);
			ReflectionUtils.setField(id, instance, NumberUtils.createLong(propertyValue));
		} catch (InstantiationException e) {
			logger.error(">>FaceYe Throws Exception:", e);
		} catch (IllegalAccessException e) {
			logger.error(">>FaceYe Throws Exception:", e);
		}  catch (SecurityException e) {
			logger.error(">>FaceYe Throws Exception:", e);
		}
		return instance;
	}

	private static SimpleExpression<?> createPath(PathBuilder builder, Class clazz, String propertyName) {
		Field field = ReflectionUtils.findField(clazz, propertyName);
		if (field != null) {
			String typeName = field.getType().getSimpleName();
			switch (typeName) {
			case "Integer":
				return builder.getNumber(propertyName, Integer.class);
			case "Long":
				return builder.getNumber(propertyName, Long.class);
			case "Double":
				return builder.getNumber(propertyName, Double.class);
			case "Date":
				return builder.getDate(propertyName, Date.class);
			case "String":
				return builder.getString(propertyName);
			case "Boolean":
				return builder.getBoolean(propertyName);
			default:
				logger.debug(">>FaceYe --> Default simple expression creator:class is:" + clazz.getName() + ",propery is :" + propertyName + ",type is:" + typeName);
			}
			// FieldTypeEnum type=field.getType().get
		}
		return null;
	}
}
