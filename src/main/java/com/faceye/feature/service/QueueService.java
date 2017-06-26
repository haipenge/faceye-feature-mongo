package com.faceye.feature.service;

import java.util.Collection;
import java.util.Queue;

import com.faceye.feature.util.ServiceException;

/**
 * 队列服务接口
 * @author @haipenge 
 * haipenge@gmail.com
*  Create Date:2014年1月2日
 */
public interface QueueService<T> {

	/*
	 * 从队列中取出对像
	 */
	public T get();

	/**
	 * 往队列中加入对像
	 * @todo
	 * @throws Exception
	 * @author:@haipenge
	 * haipenge@gmail.com
	 * 2014年1月2日
	 */
	public void add(T o);

	/**
	 * 添加整个集合
	 * @todo
	 * @param collections
	 * @throws ServiceException
	 * @author:@haipenge
	 * haipenge@gmail.com
	 * 2014年1月2日
	 */
	public void addAll(Collection<T> collections);

	/**
	 * 
	 * @todo
	 * @return
	 * @throws ServiceException
	 * @author:@haipenge
	 * haipenge@gmail.com
	 * 2014年7月8日
	 */
	public Boolean isEmpty();

	/**
	 * 取得队列大小
	 * @todo
	 * @return
	 * @throws ServiceExcepotion
	 * @author:@haipenge
	 * haipenge@gmail.com
	 * 2014年7月8日
	 */
	public int getSize();

	public Queue<T> getQueue();
}
