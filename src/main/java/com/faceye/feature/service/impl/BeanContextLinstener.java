package com.faceye.feature.service.impl;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import com.faceye.feature.service.PropertyService;
import com.faceye.feature.service.SequenceService;
import com.faceye.feature.util.bean.BeanContextUtil;

@Component
public class BeanContextLinstener implements ApplicationListener<ContextRefreshedEvent> {
	private Logger logger = LoggerFactory.getLogger(getClass());
	private ApplicationContext applicationContext = null;
	// 是否重置序列
	private final static Boolean DEFAULT_RESET_SEQUENCE = false;
	private static Boolean IS_RESET_SEQUENCE = DEFAULT_RESET_SEQUENCE;

	public void onApplicationEvent(ContextRefreshedEvent event) {
		// logger.debug(">>Faceye --> Envent is" + event.getClass().getName());
		if (event instanceof ContextRefreshedEvent) {
			if (null == applicationContext) {
				applicationContext = event.getApplicationContext();
			}
			reset();
		}
	}

	// @Override
	public void afterPropertiesSet() throws Exception {
		// reset();
	}

	private void reset() {
		SequenceService sequenceService = this.applicationContext.getBean(SequenceService.class);
		String resetValue = BeanContextUtil.getBean(PropertyService.class).get("default.reset.sequence");
		if (StringUtils.isNotEmpty(resetValue) && !StringUtils.contains(resetValue, "$")) {
			IS_RESET_SEQUENCE = new Boolean(resetValue);
		}
		Map<String, BaseMongoServiceImpl> beans = this.applicationContext.getBeansOfType(BaseMongoServiceImpl.class);
		if (MapUtils.isNotEmpty(beans)) {
			Iterator<String> it = beans.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				logger.debug(">>FaceYe --> key of bean is:" + key);
				BaseMongoServiceImpl bean = beans.get(key);
				if (bean != null) {
					if (bean.sequenceService != null) {
						Method method = ReflectionUtils.findMethod(bean.getClass(), "resetEntitySequenceID");
						if (method == null) {
							logger.debug(">>FaceYe -->没有找到方法:resetEntitySequenceID");
						} else {
							try {
								if (IS_RESET_SEQUENCE) {
									ReflectionUtils.invokeMethod(method, bean);
								}
							} catch (Exception e) {
								logger.debug(">>FaceYe throws Exception when invoke method resetEntitySequenceID(),exception is :" + e.toString());
							}
						}
					}
				}

			}
		} else {
			logger.debug(">>FaceYe Have not got any bean in application context listener.");
		}
	}

}
