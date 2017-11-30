package com.yan.access.action;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.yan.access.dao.MenuMongoDaoUtil;
import com.yan.access.model.Menu;
import com.yan.access.vo.MenuVo;
import com.yan.access.vo.UserMsgInfo;

public class MenuAction extends ActionSupport{

	private static final long serialVersionUID = 1L;

	/**
	 * 应用代码
	 * 适用于，代码转换等模块
	 */
	public static final String APP_CODE = "manage";
	
	/**
	 * 成功失败标志位
	 * 前台和后台之间异步交互
	 */
	private boolean success;
	
	/**
	 * 错误信息
	 * 前台和后台之间异步交互
	 */
	private String errorMsg;
	
	private String editType;
	
	private MenuMongoDaoUtil menuMongoDaoUtil;

	/**
	 * 查询出来的总条数
	 * 分页插件需要
	 */
	private long total;
	
	private List rows;
	
	private Menu menu;
	
	public String listMenu(){
		return "success";
	}

	/**
	 * 这个方法用户进入系统后加载菜单
	 * @return
	 */
	public String showMenus(){
		
		try {
			//根据用户代码查询出来这个用户有哪些功能代码，查出对应的菜单，这样可以在下面进行展示
			
			//获取用户代码
			UserMsgInfo userMsgInfo = null;
			String userCode = null;
			
			HttpServletRequest request = ServletActionContext.getRequest();
			String sessID = request.getSession().getId();
			Map<String, Object> session = ActionContext.getContext().getSession();
			//从session中获取userCode
			if(session != null){
				if(session.get(sessID) != null){
					userMsgInfo = (UserMsgInfo)session.get(sessID);
					if(userMsgInfo != null && userMsgInfo.getUserCode() != null){
						userCode = userMsgInfo.getUserCode().trim();
					}
				}
			}
			
			String adminMenuFlag = "0";    //为0表示只能查询非管理员的菜单，为1表示所有的菜单都可以查询
			if(userCode != null && "admin".equals(userCode.trim())){
				adminMenuFlag = "1";
			}
		
			//根据条件查出来对应的菜单
			Map<String, Object> map = new HashMap<>();
			map.put("syscode", APP_CODE);
			map.put("validStatus", '1');
			
			//为0表示只能查询非管理员的菜单，为1表示所有的菜单都可以查询
			if(adminMenuFlag != null && "0".equals(adminMenuFlag.trim())){
				map.put("adminMenuFlag", '0');
			}
			
			map.put("orderBy", "displayNo");
			
			List<Menu> menus = menuMongoDaoUtil.findMenuDocumentsByCondition(map);
			
			
			Map<String, MenuVo> firstLevelMenuMap = new HashMap<>();    //保存一级菜单
			List<Menu> secondLevelMenus = new ArrayList<>();    //初步保存二级菜单
			
			List<MenuVo> firstLevelMenuVos = new ArrayList<>();    //最终返回给前台的一级菜单
			
			if(menus != null && menus.size() > 0){
				
				for(Menu menu:menus){
					
					if(menu.getMenuLevel() != null && menu.getMenuLevel().equals(1)){
						MenuVo menuVo = new MenuVo();
						menuVo.setMenuLevel(1);
						menuVo.setMenuName(menu.getMenuName());
						menuVo.setUrl(null);
						menuVo.setDisplayNo(menu.getDisplayNo());
						
						firstLevelMenuMap.put(menu.getId().trim(), menuVo);
						
					}else if(menu.getMenuLevel() != null && menu.getMenuLevel().equals(2)){
						
						//因为此时这个二级菜单的父级可能还没有遍历到，所以不宜做过多操作
						secondLevelMenus.add(menu);
						
					}
				}
			}
			
			//将二次菜单添加到一级菜单上
			if(secondLevelMenus != null && secondLevelMenus.size() > 0){
				for(Menu menu:secondLevelMenus){
					
					String upperId = menu.getUpperId().trim();
					
					if(firstLevelMenuMap.containsKey(upperId) && firstLevelMenuMap.get(upperId) != null){
						MenuVo firstLevelMenu = firstLevelMenuMap.get(upperId);
						
						List<MenuVo> subMenus = firstLevelMenu.getSubMenus();
						if(subMenus == null){
							subMenus = new ArrayList<>();
							firstLevelMenu.setSubMenus(subMenus);
						}
						
						MenuVo menuVo = new MenuVo();
						menuVo.setMenuLevel(2);
						menuVo.setMenuName(menu.getMenuName());
						menuVo.setUrl(menu.getUrl());
						menuVo.setDisplayNo(menu.getDisplayNo());
						
						subMenus.add(menuVo);
						
					}else{
						//当二级菜单没有正确配置父级菜单的时候，不进行报错，这样正确配置的菜单还可以正确显示
						//当二级菜单没有配置父级菜单的时候
						//当一级菜单是管理岗，二级菜单是非管理岗，也会出现这样的情况
						
						//throw new RuntimeException("二级菜单[" + menu.getMenuName() + "]没有正确配置父级菜单");
					}
					
					
				}
			}
			
			if(firstLevelMenuMap != null && firstLevelMenuMap.size() > 0){
				for(Entry<String, MenuVo> entry:firstLevelMenuMap.entrySet()){
					firstLevelMenuVos.add(entry.getValue());
				}
			}
			
			//按照displayNo排序
			//升序排列
			Collections.sort(firstLevelMenuVos);
			//降序排列
			//Collections.reverse(firstLevelMenuVos);
			
			rows = firstLevelMenuVos;
			
			success = true;
			errorMsg = null;
		} catch (Exception e) {
			e.printStackTrace();
			
			rows = null;
			success = false;
			errorMsg = e.getMessage();
			
		}
		
		
		return "json";
	}
	
	/**
	 * 这个方法用户进入菜单管理后查询菜单并展示
	 * @return
	 */
	public String queryMenu(){
		
		try {
			//根据用户代码查询出来这个用户有哪些功能代码，查出对应的菜单，这样可以在下面进行展示
			
			//获取用户代码
			UserMsgInfo userMsgInfo = null;
			String userCode = null;
			
			HttpServletRequest request = ServletActionContext.getRequest();
			String sessID = request.getSession().getId();
			Map<String, Object> session = ActionContext.getContext().getSession();
			//从session中获取userCode
			if(session != null){
				if(session.get(sessID) != null){
					userMsgInfo = (UserMsgInfo)session.get(sessID);
					if(userMsgInfo != null && userMsgInfo.getUserCode() != null){
						userCode = userMsgInfo.getUserCode().trim();
					}
				}
			}
			
			String adminMenuFlag = "0";    //为0表示只能查询非管理员的菜单，为1表示所有的菜单都可以查询
			if(userCode != null && "admin".equals(userCode.trim())){
				adminMenuFlag = "1";
				String flag = request.getParameter("adminMenuFlag");
				
				//有管理员权限，但是页面上选择了查询非管理员菜单
				if(flag != null && "0".equals(flag.trim())){
					adminMenuFlag = "0";
				}
				
			}
			
			//根据条件查出来对应的菜单
			Map<String, Object> map = new HashMap<>();
			
			//加这个参数是因为这个可以用在查询之外的其他地方，但是查询的时候需要进行分页，其他地方未必需要分页
			//为1表示进行分页，为0表示不进行分页
			String pagination = request.getParameter("pageing");
			
			if(pagination == null || !"0".equals(pagination.trim())){
				//获取分页信息
				String page = request.getParameter("page");
				String r = request.getParameter("rows");
				
				if(page == null || "".equals(page.trim())){
					page = "1";
				}
				map.put("page", page);
				if(r == null || "".equals(r.trim())){
					r = "10";
				}
				map.put("rows", r);
				
			}
			
			String menuLevel = request.getParameter("menuLevel");
			map.put("menuLevel", menuLevel);
			
			String menuName = request.getParameter("menuName");
			map.put("menuName", menuName);
			
			String funcCode = request.getParameter("funcCode");
			map.put("funcCode", funcCode);
			
			String upperMenuName = request.getParameter("upperMenuName");
			map.put("upperMenuName", upperMenuName);
			
			String upperFuncCode = request.getParameter("upperFuncCode");
			map.put("upperFuncCode", upperFuncCode);
			
			String syscode = request.getParameter("syscode");
			map.put("syscode", syscode);
			
			String validStatus = request.getParameter("validStatus");
			map.put("validStatus", validStatus);
			
			//为0表示只能查询非管理员的菜单，为1表示所有的菜单都可以查询
			if(adminMenuFlag != null && "0".equals(adminMenuFlag.trim())){
				map.put("adminMenuFlag", '0');
			}
			
			map.put("orderBy", "displayNo");
			
			//加这个参数是因为这个可以用在查询之外的其他地方，但是查询的时候需要进行分页，其他地方未必需要分页
			//为1表示进行分页，为0表示不进行分页
			if(pagination == null || !"0".equals(pagination.trim())){
				total = menuMongoDaoUtil.countMenuVoDocumentsByCondition(map);
			}
			
			List<Menu> menus = menuMongoDaoUtil.findMenuDocumentsByCondition(map);
			
			rows = menus;
			
			success = true;
			errorMsg = null;
		} catch (Exception e) {
			e.printStackTrace();
			
			rows = null;
			success = false;
			errorMsg = e.getMessage();
			
		}
		
		return "json";
	}

	public String editMenu(){
		HttpServletRequest request = ServletActionContext.getRequest();
		//当前台传过来的变量userNames是一个数组的时候，通过request.getParameterValues("userNames[]");这种方式才能获取到这个数组
		//String[] userNames = request.getParameterValues("userNames[]");
		String editType = request.getParameter("editType");
		
		if(editType == null){
			editType = "";
		}
		
		if("new".equals(editType.trim())){
			menu = new Menu();
			menu.setValidStatus("1");
			menu.setInsertTime(new Date());
			menu.setUpdateTime(new Date());
			
		}else if("edit".equals(editType.trim())){
			String id = request.getParameter("id");
			menu = new Menu();
			
			if(id != null && !"".equals(id.trim())){
				
				menu = menuMongoDaoUtil.findMenuById(id);
				
				success = true;
				errorMsg = null;
			}else{
				success = false;
				errorMsg = "缺少参数id";
				
			}
			
		}else{
			success = false;
			errorMsg = "缺少参数editType";
		}
		
		return "success";
	}

	public String deleteMenu(){
		try {
			HttpServletRequest request = ServletActionContext.getRequest();
			//当前台传过来的变量userNames是一个数组的时候，通过request.getParameterValues("userNames[]");这种方式才能获取到这个数组
			//String[] userNames = request.getParameterValues("userNames[]");
			
			String id = request.getParameter("id");
			menu = new Menu();
			
			if(id != null && !"".equals(id.trim())){
				
				//TODO 设置审批人代码、名称
				//TODO 设置审批时间
				//TODO 设置修改人代码
				
				//非物理删除，只是置为无效
				menuMongoDaoUtil.updateMenuValidStatus(id, "0");
				
				success = true;
				errorMsg = null;
			}else{
				success = false;
				errorMsg = "缺少参数id";
				
			}
			
		} catch (Exception e) {
			success = false;
			errorMsg = e.getLocalizedMessage();
		}
		
		return "json";
	}
	
	public String saveMenu(){
		try {
			HttpServletRequest request = ServletActionContext.getRequest();
			//当前台传过来的变量userNames是一个数组的时候，通过request.getParameterValues("userNames[]");这种方式才能获取到这个数组
			//String[] userNames = request.getParameterValues("userNames[]");
			
			editType = request.getParameter("editType");
			String id = request.getParameter("id");
			
			if(editType == null){
				editType = "";
			}
			
			if("new".equals(editType.trim())){
				Menu menuTmp = menuMongoDaoUtil.findMenuById(id);
				if(menuTmp == null){
					//新增时设置有效状态为有效
					menu.setValidStatus("1");
					id = menuMongoDaoUtil.insertMenu(menu);
					success = true;
					errorMsg = "菜单已经生成！流水号[" + id + "]";
				}else{
					id = menuTmp.getId();
					success = false;
					errorMsg = "菜单已经存在！流水号[" + id + "]，不允许重复新增！";
				}
			}else if("edit".equals(editType.trim())){
				
				//TODO 设置审批人代码、名称
				//TODO 设置审批时间
				//TODO 设置修改人代码
				
				id = menu.getId();
				menu.setUpdateTime(new Date());
				
				menuMongoDaoUtil.updateMenu(menu);
				success = true;
				errorMsg = "菜单修改成功！流水号[" + id + "]";
			}else{
				success = false;
				errorMsg = "缺少参数editType";
			}
		} catch (Exception e) {
			success = false;
			errorMsg = e.getLocalizedMessage();
		}
		return "json";
	}
	
	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public List getRows() {
		return rows;
	}

	public void setRows(List rows) {
		this.rows = rows;
	}

	public Menu getMenu() {
		return menu;
	}

	public void setMenu(Menu menu) {
		this.menu = menu;
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public String getEditType() {
		return editType;
	}

	public void setEditType(String editType) {
		this.editType = editType;
	}

	public MenuMongoDaoUtil getMenuMongoDaoUtil() {
		return menuMongoDaoUtil;
	}

	public void setMenuMongoDaoUtil(MenuMongoDaoUtil menuMongoDaoUtil) {
		this.menuMongoDaoUtil = menuMongoDaoUtil;
	}
	
}
