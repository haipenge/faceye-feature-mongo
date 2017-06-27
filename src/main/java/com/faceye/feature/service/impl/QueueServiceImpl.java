package com.faceye.feature.service.impl;

import java.util.Collection;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.faceye.feature.service.QueueService;

public abstract class QueueServiceImpl<T> implements QueueService<T> {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	@SuppressWarnings("unchecked")
	@Override
<<<<<<< HEAD
	synchronized public T get()  {
=======
	synchronized public T get() {
>>>>>>> 3ac107a295ed78669752937b3f6349fd05c73157
		T o = (T) this.getQueue().poll();
		return o;
	}

	@SuppressWarnings("unchecked")
	@Override
<<<<<<< HEAD
	public void add(T o)  {
=======
	public void add(T o) {
>>>>>>> 3ac107a295ed78669752937b3f6349fd05c73157
		this.getQueue().add(o);
	}

	/**
	 * 添加整个集合
	 * @todo
	 * @param collections
	 * @
	 * @author:@haipenge
	 * haipenge@gmail.com
	 * 2014年1月2日
	 */
	@SuppressWarnings("unchecked")
<<<<<<< HEAD
	public void addAll(Collection<T> collections)  {
=======
	public void addAll(Collection<T> collections) {
>>>>>>> 3ac107a295ed78669752937b3f6349fd05c73157
		this.getQueue().addAll(collections);
	}

	/**
	 * 是否为空
	 */
<<<<<<< HEAD
	public Boolean isEmpty()  {
=======
	public Boolean isEmpty() {
>>>>>>> 3ac107a295ed78669752937b3f6349fd05c73157
		Boolean res = Boolean.TRUE;
		if (this.getQueue() != null && this.getQueue().size() > 0) {
			res = Boolean.FALSE;
		}
		return res;
	}

<<<<<<< HEAD
	public int getSize()  {
=======
	public int getSize() {
>>>>>>> 3ac107a295ed78669752937b3f6349fd05c73157
		int size = 0;
		if (!isEmpty()) {
			size = this.getQueue().size();
		}
		return size;
	}
}
