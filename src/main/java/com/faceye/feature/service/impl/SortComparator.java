package com.faceye.feature.service.impl;

import java.util.Comparator;
import java.util.Map;

import org.springframework.data.domain.Sort;

/**
 * 对排序key的排序
 * 
 * @author haipenge
 *
 */
public class SortComparator implements Comparator<Map<Sort, Integer>> {
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
