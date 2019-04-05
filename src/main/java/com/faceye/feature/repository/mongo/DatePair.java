package com.faceye.feature.repository.mongo;

import java.util.Date;

/**
 * QueryDSL 做日期查询时的时间对
 * @author haipenge
 *
 */
public class DatePair {

	private Date start=null;
	private Date end=null;
	public DatePair(Date start,Date end){
		this.start=start;
		this.end=end;
	}
	public Date getStart() {
		return start;
	}
	public void setStart(Date start) {
		this.start = start;
	}
	public Date getEnd() {
		return end;
	}
	public void setEnd(Date end) {
		this.end = end;
	}
	
}
