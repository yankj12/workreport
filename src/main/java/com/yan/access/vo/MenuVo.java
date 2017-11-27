package com.yan.access.vo;

import java.io.Serializable;
import java.util.List;

public class MenuVo implements Serializable, Comparable<MenuVo>{

	private static final long serialVersionUID = 1L;

	private Integer menuLevel;
    
    private String menuName;
    
    private String url;
    
    /**
     * 菜单排列顺序
     */
    private Integer displayNo;
    
    private List<MenuVo> subMenus;

	public Integer getMenuLevel() {
		return menuLevel;
	}

	public void setMenuLevel(Integer menuLevel) {
		this.menuLevel = menuLevel;
	}

	public String getMenuName() {
		return menuName;
	}

	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<MenuVo> getSubMenus() {
		return subMenus;
	}

	public void setSubMenus(List<MenuVo> subMenus) {
		this.subMenus = subMenus;
	}

	public Integer getDisplayNo() {
		return displayNo;
	}

	public void setDisplayNo(Integer displayNo) {
		this.displayNo = displayNo;
	}

	public int compareTo(MenuVo o) {
		return this.displayNo.compareTo(o.getDisplayNo());
	}
    
}
