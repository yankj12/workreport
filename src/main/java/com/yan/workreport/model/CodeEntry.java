package com.yan.workreport.model;

import java.util.Date;

public class CodeEntry {

	private String codeCode;
	
	private String codeValue;
	
	private String remark;
	
	private String validStatus;

	private Date insertTime;
	
	private Date updateTime;
	
	public CodeEntry() {
		
	}
	
	public CodeEntry(String codeCode, String codeValue) {
		super();
		this.codeCode = codeCode;
		this.codeValue = codeValue;
		this.validStatus = "1";
		this.insertTime = new Date();
		this.updateTime = new Date();
	}

	public String getCodeCode() {
		return codeCode;
	}

	public void setCodeCode(String codeCode) {
		this.codeCode = codeCode;
	}

	public String getCodeValue() {
		return codeValue;
	}

	public void setCodeValue(String codeValue) {
		this.codeValue = codeValue;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getValidStatus() {
		return validStatus;
	}

	public void setValidStatus(String validStatus) {
		this.validStatus = validStatus;
	}

	public Date getInsertTime() {
		return insertTime;
	}

	public void setInsertTime(Date insertTime) {
		this.insertTime = insertTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	
}
