package com.yan.workreport.intf.vo;

import java.io.Serializable;

public class SelectOptionVo implements Serializable{

	private String label;
	
	private String value;
	
	private boolean selected;

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
}
