package com.yan.access.filter;


import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.yan.access.service.facade.UserAccessService;
import com.yan.access.vo.ResponseVo;

public class LoginFilter implements Filter{

	
    private String ignoreKeys[];
    private int ignoreKeyCount;
    
    // spring上下文
    private WebApplicationContext context;
    
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * 判断是否登陆
	 * 判断是否有访问这个菜单的权限，可能判断权限这个放在拦截器更合适，因为拦截器拦截的一定是要经过action的，而菜单基本都会通过action。
	 * 但是过滤器过滤到的可能还有一些资源的url
	 * 
	 */
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		
		
		// 获得在下面代码中要用的request,response,session对象
        HttpServletRequest servletRequest = (HttpServletRequest) request;
        HttpServletResponse servletResponse = (HttpServletResponse) response;
        HttpSession httpSession = servletRequest.getSession();
        
        UserAccessService userAccessService = (UserAccessService) context.getBean("userAccessService");
        
        // 获得用户请求的URI
        String uri = servletRequest.getRequestURI();
        
        
        if(uri == null){
        	uri = "";
        }
        
        boolean isInIgnoreList = false;    //是否在忽略列表，在忽略列表中可以不检查是否登录
        // 登陆页面无需过滤
        if(uri.indexOf("/login/") > -1) {
        	isInIgnoreList = true;
        }
        //判断一下是否是无需过滤的页面
        for(int i = 0; i < ignoreKeyCount; i++){
        	//
            if(ignoreKeys[i].endsWith("/")){
            	//以/结尾，认为是一个文件夹或者路径前缀，所以要判断是否包含
            	if(!uri.toUpperCase().startsWith(ignoreKeys[i].toUpperCase())){
            		continue;
            	}
            }else{
            	//不是以/结尾，认为是特定的一个url，所以要判断url是否相等
            	if(!ignoreKeys[i].equalsIgnoreCase(uri)){
            		continue;
            	}
            }
        	
            //走到这一步说明uri是在忽略的列表中的
            isInIgnoreList = true;
            break;
        }
		
		if(isInIgnoreList){
			chain.doFilter(servletRequest, servletResponse);
			
            return;
		}
        
        
        // 从session里取员工工号信息
        String contextPath = servletRequest.getContextPath();
        
		boolean isUserLogin = false;
		String sessID = httpSession.getId();
		
		// find tickets from cookies
		String ticket = null;
		Cookie[] cookies = servletRequest.getCookies();
		if (cookies != null) {
            for (Cookie cookie : cookies) {
            	String name = cookie.getName();
            	if("ticket".equals(name)) {
            		ticket = cookie.getValue();
            		break;
            	}
            }
        }
		if(ticket == null || "".equals(ticket)) {
			ticket = sessID;
		}
		
		try {
			ResponseVo responseVo = userAccessService.getSession(ticket);
			if(responseVo != null && responseVo.isSuccess()) {
				isUserLogin = true;
			}else {
				isUserLogin = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(isUserLogin){
			// 已经登陆,继续此次请求
            chain.doFilter(servletRequest, servletResponse);
		}else{
			// 跳转到登陆页面
            servletResponse.sendRedirect(contextPath + "/login/prepareLogin.do");
		}
        /*创建类Constants.java，里面写的是无需过滤的页面
        for (int i = 0; i < Constants.NoFilter_Pages.length; i++) {

            if (path.indexOf(Constants.NoFilter_Pages[i]) > -1) {
                chain.doFilter(servletRequest, servletResponse);
                return;
            }
        }*/
        
	}

	public void init(FilterConfig filterConfig) throws ServletException {
		
		String ignoreKey = filterConfig.getInitParameter("ignoreKey");
        if(ignoreKey == null){
            ignoreKeys = new String[0];
        }else{
            ignoreKeys = ignoreKey.split(",");
            
            //经过下面的处理，在web.xml中配置ignoreKey的时候就可以换行了
            if(ignoreKeys != null && ignoreKeys.length > 0){
            	for(int i=0;i<ignoreKeys.length;i++){
            		if(ignoreKeys[i] != null && !"".equals(ignoreKeys[i])){
            			ignoreKeys[i] = ignoreKeys[i].replaceAll("\n", "");
            			ignoreKeys[i] = ignoreKeys[i].trim();
            		}
            	}
            }
        }
        ignoreKeyCount = ignoreKeys.length;
        
        // 获取spring上下文
        ServletContext servletContext = filterConfig.getServletContext();  
        context = WebApplicationContextUtils.getWebApplicationContext(servletContext);  
        
	}

}
