package com.yan.workreport.model;

import java.util.Date;
import java.util.List;

public class TransCode {

	private String id;
	
	//private String appCode;
	
	private String codeType;
	
	private String codeTypeName;
	
	private List<CodeEntry> trans;
	
	private String remark;
	
	private String validStatus;

	private Date insertTime;
	
	private Date updateTime;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCodeType() {
		return codeType;
	}

	public void setCodeType(String codeType) {
		this.codeType = codeType;
	}

	public List<CodeEntry> getTrans() {
		return trans;
	}

	public void setTrans(List<CodeEntry> trans) {
		this.trans = trans;
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

	public String getCodeTypeName() {
		return codeTypeName;
	}

	public void setCodeTypeName(String codeTypeName) {
		this.codeTypeName = codeTypeName;
	}
	
}
