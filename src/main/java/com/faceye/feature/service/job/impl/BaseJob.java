package com.faceye.feature.service.job.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.faceye.feature.service.job.Job;
 

abstract public class BaseJob implements Job {
    
	protected Logger logger =LoggerFactory.getLogger(getClass());
	/**
	 * JOb任务通用实现。
	 */
	public void doJob() {
		logger.debug(">>FaceYe come to run job now,job class is:"+getClass().getName());
		this.run();
		logger.debug(">>FaceYe run Job over.");
	}
	
	/**
	 * 每一个具体任务需要实现在抽像方法
	 * @throws BusinessException
	 */
	public abstract void run();

}
