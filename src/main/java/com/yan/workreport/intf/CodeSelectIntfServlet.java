package com.yan.workreport.intf;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.alibaba.fastjson.JSON;
import com.yan.workreport.dao.TransCodeMongoDaoUtil;
import com.yan.workreport.intf.vo.SelectOptionVo;
import com.yan.workreport.model.CodeEntry;
import com.yan.workreport.model.TransCode;

public class CodeSelectIntfServlet  extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		//获取ServletContext 再获取 WebApplicationContextUtils  
        ServletContext servletContext = this.getServletContext();  
        WebApplicationContext context =   
                WebApplicationContextUtils.getWebApplicationContext(servletContext);  
        TransCodeMongoDaoUtil transCodeMongoDaoUtil = (TransCodeMongoDaoUtil) context.getBean("transCodeMongoDaoUtil"); 
		
		//获取入参
		String codeType = request.getParameter("codeType");
		String codeCode = request.getParameter("codeCode");
		String required = request.getParameter("required");
		
		//从数据库查询
		List<SelectOptionVo> list = new ArrayList<SelectOptionVo>();
		
		if(codeType != null && !"".equals(codeType.trim())){
			
			//TransCodeMongoDaoUtil transCodeMongoDaoUtil = new TransCodeMongoDaoUtil();
			List<TransCode> transCodes = null;
			transCodes = transCodeMongoDaoUtil.findTransCodeDocumentsByCodeTypeAndValidStatus(codeType, "1");
			
			TransCode transCode = null;
			List<CodeEntry> codeEntries = null;
			if(transCodes != null && transCodes.size() > 0) {
				transCode = transCodes.get(0);
				
				if(codeCode != null && !"".equals(codeCode.trim())){
					//如果codecode不为空，说明是代码翻译接口，需要找到对应codecode
					if(transCode.getTrans() != null) {
						for(CodeEntry code:transCode.getTrans()){
							if(code != null && code.getCodeCode() != null 
									&& codeCode.trim().equals(code.getCodeCode().trim())) {
								codeEntries = new ArrayList<>(1);
								codeEntries.add(code);
								break;
							}
						}
					}
				}else{
					//codecode为空，说明是代码选择接口
					codeEntries = transCode.getTrans();
				}
			}
			
			
			if(codeEntries != null && codeEntries.size() > 0){
				for(CodeEntry code:codeEntries){
					SelectOptionVo optionVo = new SelectOptionVo();
					optionVo.setValue(code.getCodeCode());
					optionVo.setLabel(code.getCodeValue());
					
					list.add(optionVo);
				}
			}
			
			if(required != null && "true".equalsIgnoreCase(required.trim())){
				//required为true，表示必录，肯定不需要'--'
			}else{
				//添加一个"请选择"
				SelectOptionVo optionVo = new SelectOptionVo();
				optionVo.setLabel("--");
				optionVo.setValue("");
				optionVo.setSelected(true);
				list.add(optionVo);
			}
				
			
		}
		
		//fastjson转换为json
		//返回json数据
		String json = JSON.toJSONString(list);
		
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");
		PrintWriter out = response.getWriter();
		out.println(json);
	}
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		//如果在dopost中不调用doget，那么通过post方式请求过来的连接将不会被做任何处理
		doGet(request, response);
	}
}
