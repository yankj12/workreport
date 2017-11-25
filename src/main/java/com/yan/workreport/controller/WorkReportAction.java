package com.yan.workreport.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionSupport;
import com.yan.workreport.dao.WorkReportTextMongoDaoUtil;
import com.yan.workreport.model.WorkReportText;
import com.yan.workreport.vo.WorkReportTextVo;

public class WorkReportAction extends ActionSupport{

	private static final long serialVersionUID = 1L;

	/**
	 * 查询出来的总条数
	 * 分页插件需要
	 */
	private long total;
	
	/**
	 * 返回给页面的分页查询记录
	 * 分页插件需要
	 */
	private List rows;
	
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

	
	private Object object;

	private WorkReportTextMongoDaoUtil workReportTextMongoDaoUtil;
	
	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public List getRows() {
		return rows;
	}

	public void setRows(List rows) {
		this.rows = rows;
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

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}

	public WorkReportTextMongoDaoUtil getWorkReportTextMongoDaoUtil() {
		return workReportTextMongoDaoUtil;
	}

	public void setWorkReportTextMongoDaoUtil(WorkReportTextMongoDaoUtil workReportTextMongoDaoUtil) {
		this.workReportTextMongoDaoUtil = workReportTextMongoDaoUtil;
	}

	public String workreport(){
		
		return "success";
	}
	
	public String findWorkReports() {
    	
    	Map<String, Object> map = new HashMap<String, Object>();
		
    	HttpServletRequest request = ServletActionContext.getRequest();
		//当前台传过来的变量userNames是一个数组的时候，通过request.getParameterValues("userNames[]");这种方式才能获取到这个数组
		//String[] userNames = request.getParameterValues("userNames[]");
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
		
		//有效状态
		String validStatus = request.getParameter("validStatus");
		map.put("validStatus", validStatus);
		
		//起始日期
		String startDay = request.getParameter("startDay");
		map.put("startDay", startDay);
		
		//结束日期
		String endDay = request.getParameter("endDay");
		map.put("endDay", endDay);
		
		//项目名称
		String projectName = request.getParameter("projectName");
		map.put("projectName", projectName);
		
		//项目代码
		String projectCode = request.getParameter("projectCode");
		map.put("projectCode", projectCode);
		
		//日志编写人名称
		String writerName = request.getParameter("writerName");
		map.put("writerName", writerName);
		
		//数据类型
		String dataType = request.getParameter("dataType");
		map.put("dataType", dataType);		
		
		//日志类型
		String type = request.getParameter("type");
		map.put("type", type);		

		//日志正文
		String workText = request.getParameter("workText");
		map.put("workText", workText);	
		
    	//根据条件查询总条数
    	total = 0;
    	//查询结果
		List<WorkReportText> workReportTexts = workReportTextMongoDaoUtil.findWorkReportTextDocumentsByCondition(map);
		//返回给前台的rows不能是null，否则前台js会报rows is null异常
		List<WorkReportTextVo> workReportTextVos = new ArrayList<WorkReportTextVo>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if(workReportTexts != null && workReportTexts.size() > 0) {
			//workReportTextVos = new ArrayList<WorkReportTextVo>();
			for(WorkReportText workReportText:workReportTexts){
				WorkReportTextVo workReportTextVo = new WorkReportTextVo();
				workReportTextVo.setId(workReportText.getId());
				workReportTextVo.setDay(workReportText.getDay());
				workReportTextVo.setDataType(workReportText.getDataType());
				workReportTextVo.setTitle(workReportText.getTitle());
				workReportTextVo.setType(workReportText.getType());
				workReportTextVo.setProjectName(workReportText.getProjectName());
				workReportTextVo.setProjectCode(workReportText.getProjectCode());
				workReportTextVo.setWriterName(workReportText.getWriterName());
				workReportTextVo.setWorkText(workReportText.getWorkText());
				workReportTextVo.setComment(workReportText.getComment());
				workReportTextVo.setUpdateTime(sdf.format(workReportText.getUpdateTime()));
				
				workReportTextVos.add(workReportTextVo);
			}
		}
		rows = workReportTextVos;
    	total = workReportTextMongoDaoUtil.countWorkReportTextVoDocumentsByCondition(map);
		
    	//下面这两个变量在这个方法中并不是必须的
		success = true;
		errorMsg = null;
		return "json";
    }
    
    public String findUniqueWorkReport() {
    	
    	HttpServletRequest request = ServletActionContext.getRequest();
		//当前台传过来的变量userNames是一个数组的时候，通过request.getParameterValues("userNames[]");这种方式才能获取到这个数组
		//String[] userNames = request.getParameterValues("userNames[]");
		String id = request.getParameter("id");
    	
    	success = true;
    	errorMsg = null;
    	
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	WorkReportTextVo workReportTextVo = null;
    	if(id != null && !"".equals(id.trim())) {
    		WorkReportText workReportText = workReportTextMongoDaoUtil.findWorkReportById(id);
    		if(workReportText != null) {
    			workReportTextVo = new WorkReportTextVo();
    			workReportTextVo.setId(workReportText.getId());
    			workReportTextVo.setDay(workReportText.getDay());
    			workReportTextVo.setTitle(workReportText.getTitle());
    			workReportTextVo.setDataType(workReportText.getDataType());
    			workReportTextVo.setType(workReportText.getType());
    			workReportTextVo.setProjectName(workReportText.getProjectName());
    			workReportTextVo.setProjectCode(workReportText.getProjectCode());
    			workReportTextVo.setWriterName(workReportText.getWriterName());
    			workReportTextVo.setWorkText(workReportText.getWorkText());
    			workReportTextVo.setComment(workReportText.getComment());
    			workReportTextVo.setUpdateTime(sdf.format(workReportText.getUpdateTime()));
    		}
    	}
    	object = workReportTextVo;
    	
    	return "json";
    }
    
    public String saveWorkReport() {
    	Map<String, Object> map = new HashMap<String, Object>();
		
    	HttpServletRequest request = ServletActionContext.getRequest();
		//当前台传过来的变量userNames是一个数组的时候，通过request.getParameterValues("userNames[]");这种方式才能获取到这个数组
		//String[] userNames = request.getParameterValues("userNames[]");
    	
    	String id = request.getParameter("id");
		map.put("id", id);
		
    	
		String editType = request.getParameter("editType");
		
		//日志编写日期
		String day = request.getParameter("day");
		map.put("day", day);
		
		//日志标题
		String title = request.getParameter("title");
		map.put("title", title);
		
		//数据类型
		String dataType = request.getParameter("dataType");
		map.put("dataType", dataType);		
				
		//日志类型
		String type = request.getParameter("type");
		map.put("type", type);
		
		//项目代码
		String projectCode = request.getParameter("projectCode");
		map.put("projectCode", projectCode);
		
		//项目名称
		String projectName = request.getParameter("projectName");
		map.put("projectName", projectName);
		
		//日志编写人
		String writerName = request.getParameter("writerName");
		map.put("writerName", writerName);	
		
		//日志正文
		String workText = request.getParameter("workText");
		map.put("workText", workText);	
    	
		//日志评论
		String comment = request.getParameter("comment");
		map.put("comment", comment);	
    	
				
    	if(map != null){
    		WorkReportText reportText = new WorkReportText();
    		reportText.setId((String)map.get("id"));
    		reportText.setDay((String)map.get("day"));
    		reportText.setTitle((String)map.get("title"));
    		reportText.setDataType((String)map.get("dataType"));
    		reportText.setType((String)map.get("type"));
    		reportText.setProjectName((String)map.get("projectName"));
    		reportText.setProjectCode((String)map.get("projectCode"));
    		reportText.setWriterName((String)map.get("writerName"));
    		reportText.setWorkText((String)map.get("workText"));
    		reportText.setComment((String)map.get("comment"));
    		reportText.setValidStatus("1");
    		
    		if(editType != null && "new".equals(editType.trim())) {
    			reportText.setInsertTime(new Date());
    			reportText.setUpdateTime(new Date());
        		
    			id = workReportTextMongoDaoUtil.insertWorkReportText(reportText);
				reportText.setId(id);
				object = reportText;
    		}else if (editType != null && "edit".equals(editType.trim())) {
    			reportText.setUpdateTime(new Date());
        		
    			workReportTextMongoDaoUtil.updateWorkReportText(reportText);
    			object = reportText;
			}
    		
    		
    		//在这下面处理下工作日志物化视图
    		
    		if(dataType != null && "originalData".equals(dataType.trim())) {
    			//先查下day所在的这天是否已经有了工作日志无话视图
    			Map<String, Object> condition = new HashMap<String, Object>();
    			//日志编写日期
    			condition.put("day", day);
    			//数据类型
    			condition.put("dataType", "materializedView");		
//    		//日志类型
//    		condition.put("type", "team");
    			//项目代码
    			condition.put("projectCode", projectCode);
    			List<WorkReportText> workReportTexts = workReportTextMongoDaoUtil.findWorkReportTextDocumentsByCondition(map);
    			WorkReportText materializedViewWorkReportText = null;
    			if(workReportTexts != null && workReportTexts.size() > 0) {
    				materializedViewWorkReportText = workReportTexts.get(0);
    			}
    			
    			if(materializedViewWorkReportText == null) {
    				materializedViewWorkReportText = new WorkReportText();
    				materializedViewWorkReportText.setDay(day);
    				
    				String year = day.substring(0, 4);
    				String month = day.substring(4, 6);
    				String date = day.substring(6, 8);
    				String newTitle = year + "-" + month + "-" + date + "工作日志";
    				materializedViewWorkReportText.setTitle(newTitle);
    				
    				materializedViewWorkReportText.setDataType("materializedView");
    				materializedViewWorkReportText.setType("team");
    				materializedViewWorkReportText.setProjectCode(projectCode);
    				materializedViewWorkReportText.setProjectName(projectName);
    				materializedViewWorkReportText.setValidStatus("1");
    				
    				//查找day，data，dataType，projectCode
    				Map<String, Object> newMap = new HashMap<String, Object>();
    				//日志编写日期
    				newMap.put("day", day);
    				//数据类型
    				newMap.put("dataType", "originalData");		
    				//日志类型
    				newMap.put("type", "person");
    				//项目代码
    				newMap.put("projectCode", projectCode);
    				newMap.put("page", 1);
    				newMap.put("rows", 100);
    				
    				List<WorkReportText> reportTexts = workReportTextMongoDaoUtil.findWorkReportTextDocumentsByCondition(newMap);
    				if(reportTexts != null && reportTexts.size() > 0) {
    					
    					StringBuilder newText = new StringBuilder("今天工作完成情况：\n-----------------------------\n\n");
    					
    					for(WorkReportText report : reportTexts) {
    						String userName = report.getWriterName();
    						String text = report.getWorkText();
    						String comm = report.getComment();
    						
    						newText.append(userName).append("\n").append(text).append("\n评论：\n").append(comm).append("\n\n");
    					}
    					
    					materializedViewWorkReportText.setInsertTime(new Date());
    					materializedViewWorkReportText.setUpdateTime(new Date());
    					
    					workReportTextMongoDaoUtil.insertWorkReportText(materializedViewWorkReportText);
    				}
    				
    				
    			}else {
    				materializedViewWorkReportText.setDay(day);
    				
    				String year = day.substring(0, 4);
    				String month = day.substring(4, 6);
    				String date = day.substring(6, 8);
    				String newTitle = year + "-" + month + "-" + date + "工作日志";
    				materializedViewWorkReportText.setTitle(newTitle);
    				
    				materializedViewWorkReportText.setDataType("materializedView");
    				materializedViewWorkReportText.setType("team");
    				materializedViewWorkReportText.setProjectCode(projectCode);
    				materializedViewWorkReportText.setProjectName(projectName);
    				materializedViewWorkReportText.setValidStatus("1");
    				
    				//查找day，data，dataType，projectCode
    				Map<String, Object> newMap = new HashMap<String, Object>();
    				//日志编写日期
    				newMap.put("day", day);
    				//数据类型
    				newMap.put("dataType", "originalData");		
    				//日志类型
    				newMap.put("type", "person");
    				//项目代码
    				newMap.put("projectCode", projectCode);
    				newMap.put("page", 1);
    				newMap.put("rows", 100);
    				
    				List<WorkReportText> reportTexts = workReportTextMongoDaoUtil.findWorkReportTextDocumentsByCondition(newMap);
    				if(reportTexts != null && reportTexts.size() > 0) {
    					
    					StringBuilder newText = new StringBuilder("今天工作完成情况：\n-----------------------------\n\n");
    					
    					for(WorkReportText report : reportTexts) {
    						String userName = report.getWriterName();
    						String text = report.getWorkText();
    						String comm = report.getComment();
    						
    						newText.append(userName).append("\n").append(text).append("\n评论：\n").append(comm).append("\n\n");
    					}
    					
    					materializedViewWorkReportText.setUpdateTime(new Date());
    					
    					workReportTextMongoDaoUtil.updateWorkReportText(materializedViewWorkReportText);
    				}
    			}
    		}
    		
    		success = true;
    		errorMsg = null;
    	}else{
    		success = false;
    		errorMsg = "缺少参数或请求数据不全！";
    	}
    	
    	return "json";
    }
    
    public String deleteWorkReport() {
    	HttpServletRequest request = ServletActionContext.getRequest();
		//当前台传过来的变量userNames是一个数组的时候，通过request.getParameterValues("userNames[]");这种方式才能获取到这个数组
		//String[] userNames = request.getParameterValues("userNames[]");
		String id = request.getParameter("id");
    	
    	success = true;
    	errorMsg = null;
    	
    	if(id != null && !"".equals(id.trim())){
    		
			//目前采用修改有效无效标志位的方式来标志删除
			workReportTextMongoDaoUtil.updateWorkReportTextValidStatus(id, "0");
    	}else{
    		success = false;
    		errorMsg = "缺少参数或请求数据不全！";
    	}
    	
    	return "json";
    }
}