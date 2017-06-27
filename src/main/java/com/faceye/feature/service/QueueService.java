package com.faceye.feature.service;

import java.util.Collection;
import java.util.Queue;


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
<<<<<<< HEAD
	public T get();
=======
	public T get()  ;
>>>>>>> 3ac107a295ed78669752937b3f6349fd05c73157

	/**
	 * 往队列中加入对像
	 * @todo
	 * @throws Exception
	 * @author:@haipenge
	 * haipenge@gmail.com
	 * 2014年1月2日
	 */
<<<<<<< HEAD
	public void add(T o);
=======
	public void add(T o)  ;
>>>>>>> 3ac107a295ed78669752937b3f6349fd05c73157

	/**
	 * 添加整个集合
	 * @todo
	 * @param collections
	 * @throws 
	 * @author:@haipenge
	 * haipenge@gmail.com
	 * 2014年1月2日
	 */
<<<<<<< HEAD
	public void addAll(Collection<T> collections);
=======
	public void addAll(Collection<T> collections) ;
>>>>>>> 3ac107a295ed78669752937b3f6349fd05c73157

	/**
	 * 
	 * @todo
	 * @return
	 * @throws 
	 * @author:@haipenge
	 * haipenge@gmail.com
	 * 2014年7月8日
	 */
<<<<<<< HEAD
	public Boolean isEmpty();
=======
	public Boolean isEmpty() ;
>>>>>>> 3ac107a295ed78669752937b3f6349fd05c73157

	/**
	 * 取得队列大小
	 * @todo
	 * @return
	 * @throws ServiceExcepotion
	 * @author:@haipenge
	 * haipenge@gmail.com
	 * 2014年7月8日
	 */
<<<<<<< HEAD
	public int getSize();
=======
	public int getSize()  ;
>>>>>>> 3ac107a295ed78669752937b3f6349fd05c73157

	public Queue<T> getQueue();
}
