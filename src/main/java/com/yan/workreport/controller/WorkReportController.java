package com.yan.workreport.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yan.workreport.dao.WorkReportTextMongoDaoUtil;
import com.yan.workreport.model.WorkReportText;
import com.yan.workreport.vo.ResultVo;
import com.yan.workreport.vo.WorkReportTextVo;

@CrossOrigin(allowCredentials="true", allowedHeaders="*", methods={RequestMethod.GET,  
        RequestMethod.POST, RequestMethod.DELETE, RequestMethod.OPTIONS,  
        RequestMethod.HEAD, RequestMethod.PUT, RequestMethod.PATCH}, origins="*")  
@RestController
public class WorkReportController {

    @RequestMapping(value="/findWorkReports", method=RequestMethod.POST)
    public ResultVo findWorkReports(@RequestParam Map<String, Object> map) {
    	ResultVo queryResultVo = new ResultVo();
    	
    	//根据条件查询总条数
    	long total = 0;
    	//查询结果
    	WorkReportTextMongoDaoUtil workReportTextMongoDaoUtil = new WorkReportTextMongoDaoUtil();
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
				workReportTextVo.setTitle(workReportText.getTitle());
				workReportTextVo.setType(workReportText.getType());
				workReportTextVo.setProjectName(workReportText.getProjectName());
				workReportTextVo.setProjectCode(workReportText.getProjectCode());
				workReportTextVo.setWriterName(workReportText.getWriterName());
				workReportTextVo.setWorkText(workReportText.getWorkText());
				workReportTextVo.setUpdateTime(sdf.format(workReportText.getUpdateTime()));
				
				workReportTextVos.add(workReportTextVo);
			}
		}
    	total = workReportTextMongoDaoUtil.countWorkReportTextVoDocumentsByCondition(map);
		
    	queryResultVo.setTotal(total);
    	//返回给前台的rows不能是null，否则前台js会报rows is null异常
    	queryResultVo.setRows(workReportTextVos);
        return queryResultVo;
    }
    
    @RequestMapping("/findUniqueWorkReport")
    public ResultVo findUniqueWorkReport(@RequestParam(value="id") String id) {
    	boolean success = true;
    	String errorMsg = null;
    	ResultVo resultVo = new ResultVo();
    	
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	WorkReportTextVo workReportTextVo = null;
    	if(id != null && !"".equals(id.trim())) {
    		WorkReportTextMongoDaoUtil workReportTextMongoDaoUtil = new WorkReportTextMongoDaoUtil();
    		WorkReportText workReportText = workReportTextMongoDaoUtil.findWorkReportById(id);
    		if(workReportText != null) {
    			workReportTextVo = new WorkReportTextVo();
    			workReportTextVo.setId(workReportText.getId());
    			workReportTextVo.setDay(workReportText.getDay());
    			workReportTextVo.setTitle(workReportText.getTitle());
    			workReportTextVo.setType(workReportText.getType());
    			workReportTextVo.setProjectName(workReportText.getProjectName());
    			workReportTextVo.setProjectCode(workReportText.getProjectCode());
    			workReportTextVo.setWriterName(workReportText.getWriterName());
    			workReportTextVo.setWorkText(workReportText.getWorkText());
    			workReportTextVo.setUpdateTime(sdf.format(workReportText.getUpdateTime()));
    		}
    	}
    	
    	resultVo.setObject(workReportTextVo);
    	resultVo.setErrorMsg(errorMsg);
    	resultVo.setSuccess(success);
    	return resultVo;
    }
    
    @RequestMapping("/saveWorkReport")
    public ResultVo saveWorkReport(@RequestParam Map<String, Object> map) {

    	boolean success = true;
    	String errorMsg = null;
    	ResultVo resultVo = new ResultVo();
    	
    	if(map != null){
    		WorkReportText reportText = new WorkReportText();
    		reportText.setId((String)map.get("id"));
    		reportText.setDay((String)map.get("day"));
    		reportText.setTitle((String)map.get("title"));
    		reportText.setType((String)map.get("type"));
    		reportText.setProjectName((String)map.get("projectName"));
    		reportText.setProjectCode((String)map.get("projectCode"));
    		reportText.setWriterName((String)map.get("writerName"));
    		reportText.setWorkText((String)map.get("workText"));
			
    		reportText.setValidStatus("1");
    		
    		String editType = (String)map.get("editType");
    		
			WorkReportTextMongoDaoUtil workReportTextMongoDaoUtil = new WorkReportTextMongoDaoUtil();
    		if(editType != null && "new".equals(editType.trim())) {
    			reportText.setInsertTime(new Date());
    			reportText.setUpdateTime(new Date());
        		
    			String id = workReportTextMongoDaoUtil.insertWorkReportText(reportText);
				reportText.setId(id);
				resultVo.setObject(reportText);
    		}else if (editType != null && "edit".equals(editType.trim())) {
    			reportText.setUpdateTime(new Date());
        		
    			workReportTextMongoDaoUtil.updateWorkReportText(reportText);
				resultVo.setObject(reportText);
			}
    	}else{
    		success = false;
    		errorMsg = "缺少参数或请求数据不全！";
    	}
    	
    	resultVo.setErrorMsg(errorMsg);
    	resultVo.setSuccess(success);
    	return resultVo;
    }
    
    @RequestMapping("/deleteWorkReport")
    public ResultVo deleteWorkReport(@RequestParam(value="id") String id) {
    	boolean success = true;
    	String errorMsg = null;
    	ResultVo resultVo = new ResultVo();
    	
    	if(id != null && !"".equals(id.trim())){
    		
			WorkReportTextMongoDaoUtil workReportTextMongoDaoUtil = new WorkReportTextMongoDaoUtil();
			//目前采用修改有效无效标志位的方式来标志删除
			workReportTextMongoDaoUtil.updateWorkReportTextValidStatus(id, "0");
    	}else{
    		success = false;
    		errorMsg = "缺少参数或请求数据不全！";
    	}
    	
    	resultVo.setErrorMsg(errorMsg);
    	resultVo.setSuccess(success);
    	return resultVo;
    }
}