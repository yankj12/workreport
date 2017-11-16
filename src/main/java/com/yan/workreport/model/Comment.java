package com.yan.workreport.model;

import java.util.Date;

public class Comment {
	
	/**
	 * 这则评论的序号
	 */
	private Integer commontNo;
	
	/**
	 * 评论者
	 */
	private String userName;
	
	/**
	 * 评论内容
	 */
	private String comment;
	
	/**
	 * 评论日期
	 */
	private Date insertTime;

	public Integer getCommontNo() {
		return commontNo;
	}

	public void setCommontNo(Integer commontNo) {
		this.commontNo = commontNo;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Date getInsertTime() {
		return insertTime;
	}

	public void setInsertTime(Date insertTime) {
		this.insertTime = insertTime;
	}

}
