package com.yan.access.action;


import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionSupport;
import com.yan.access.dao.UserMongoDaoUtil;
import com.yan.access.model.User;
import com.yan.access.service.facade.UserAccessService;
import com.yan.access.vo.ResponseVo;
import com.yan.access.vo.UserMsgInfo;

public class LoginAction extends ActionSupport{

	private static final long serialVersionUID = 1L;
	
	private User user;
	
	private String userCode;
	private String password;
	
	private boolean success;
	
	private String errorMsg;
	
	private UserMongoDaoUtil userMongoDaoUtil;
	
	private UserAccessService userAccessService;
	
	private UserMsgInfo userMsgInfo;
	
	public String prepareLogin(){

		return "success";
	}
	
	public String login(){
		errorMsg = "";
		//System.out.println(authen);
		
		//从session中获取usercode
		//获取Session对象
		//获取sessionid
		HttpServletRequest request = ServletActionContext.getRequest();
		//String ip = request.getRemoteAddr();
		
		//HttpSession httpSession = request.getSession();
		String sessID = request.getSession().getId();
		
		// find tickets from cookies
		String ticket = null;
		Cookie[] cookies = request.getCookies();
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
			// 先检查下session中是否存在tickets
			ResponseVo responseVo = userAccessService.getSession(ticket);
			
			if(responseVo != null && responseVo.isSuccess()){
				userMsgInfo = responseVo.getUserMsgInfo();
				
				String userCode = userMsgInfo.getUserCode();
				errorMsg = null;
				return "success";
			}else{
				// 如果ticket不存在登陆记录，那么我们应该讲ticket更新成新的，否则就会出现一个ticket一直用的情况
				ticket = sessID;
				//根据用户名密码进行登录
				if(userCode == null || password == null 
						|| "".equals(userCode.trim()) || "".equals(password.trim())){
					errorMsg = "用户名和密码不能为空！";
					
					
					return "login";
				}
				String passwordMD5 = DigestUtils.md5Hex(password);
				
				try {
					ResponseVo responseVo2 = userAccessService.checkUserAuth(userCode, passwordMD5, ticket);
					if(responseVo2 != null && responseVo2.isSuccess()) {
						success = true;
						//errorMsg = "用户名或密码不正确！";
						errorMsg = responseVo2.getErrorMsg();
						userMsgInfo = responseVo2.getUserMsgInfo();
						
						// 将tickets写入到父级域名的cookie中
						// put tickets in cookie
						Cookie ticketsCookie = new Cookie("ticket", ticket);
						ticketsCookie.setPath("/");
				        ServletActionContext.getResponse().addCookie(ticketsCookie);
				        
						return "success";
					}else {
						success = false;
						//errorMsg = "用户名或密码不正确！";
						errorMsg = responseVo.getErrorMsg();
						return "login";
					}
				} catch (Exception e) {
					e.printStackTrace();
					errorMsg = e.getMessage();
				}
				success = false;
				//errorMsg = "用户名或密码不正确！";
				
				return "login";
			}
		} catch (Exception e) {
			e.printStackTrace();
			errorMsg = e.getMessage();
		}
		// 缺省的是进入登录界面
		return "login";
	}
	
	public String logout(){
		HttpServletRequest request = ServletActionContext.getRequest();
		String sessID = request.getSession().getId();
		
		// find tickets from cookies
		String ticket = null;
		Cookie[] cookies = request.getCookies();
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
			ResponseVo responseVo = userAccessService.invalidateSession(ticket);
			if(responseVo.isSuccess()) {
				
			}else {
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "login";
	}
	
	/**
	 * 用户注册界面校验用户编码和邮箱是否唯一
	 * 
	 * 这个方法放在这里，是因为namespace为login的才能免登陆，其他的都要经过单点过滤器
	 * @return
	 */
	public String checkUserUnique(){
		
		if(user != null){
			String userCode = user.getUserCode();
			String email = user.getEmail();
			
			if(userCode != null && !"".equals(userCode.trim())){
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("userCode", userCode.trim());
				
				long userCodeCount = userMongoDaoUtil.countUserVoDocumentsByCondition(map);
				
				if(userCodeCount > 0){
					success = false;
					errorMsg = "用户编码[" + userCode + "]已经被其他用户使用，请重新出入！";
					
					return "json";
				}else{
					success = true;
					errorMsg = null;
				}
				
			}else{
				success = false;
				errorMsg = "缺少参数userCode";
				return "json";
			}
			
			if(email != null && !"".equals(email.trim())){
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("email", email.trim());
				
				long emailCount = userMongoDaoUtil.countUserVoDocumentsByCondition(map);
				
				if(emailCount > 0){
					success = false;
					errorMsg = "邮箱[" + email + "]已经被其他用户使用，请重新出入！";
					
					return "json";
				}else{
					success = true;
					errorMsg = null;
				}
				
			}else{
				success = false;
				errorMsg = "缺少参数email";
			}
			
		}else{
			success = false;
			errorMsg = "录入参数不足，请进行检查！";
		}
		
		return "json";
	}
	
//	/**
//	 * 注册用户的方法
//	 * 这个方法放在这里，是因为namespace为login的才能免登陆，其他的都要经过单点过滤器
//	 * @return
//	 */
//	public String registeUser(){
//		try {
//			
//			UserDaoService userDaoService = new UserDaoServiceImpl();
//				
//			if(user != null){
//				if(user.getUserCode() != null && !"".equals(user.getUserCode().trim())
//						&& user.getPswd() != null && !"".equals(user.getPswd().trim())){
//					User userTmp = userDaoService.getUserByPK(user);
//					if(userTmp == null){
//						//将用户的密码进行MD5再进行保存
//						String passwordMD5 = DigestUtils.md5Hex(user.getPswd().trim());
//						user.setPswd(passwordMD5);
//						
//						//新增时，设置审批状态
//						//如果新增的是admin，那么自动审批通过。
//						//但是只有这么一个用户在注册的时候可以自动审批通过，其他的用户在注册的时候必须经过审批
//						if("admin".equals(user.getUserCode().trim())){
//							//如果是管理岗，自动审批通过
//							user.setAuditStatus("2");    //自动审批通过
//							user.setAuditOpinion("注册admin用户，自动审批通过");
//						}else{
//							//如果是非管理岗，则处于待审批状态
//							user.setAuditStatus("1");    //待审批
//							user.setAuditOpinion("注册用户，进入待审批状态");
//						}
//						
//						//新增时设置有效状态为有效
//						user.setValidStatus("1");
//						String userCode = userDaoService.insertUser(user);
//						success = true;
//						errorMsg = "用户已经生成！用户编码[" + userCode + "]";
//						
//						if("1".equals(user.getAuditStatus().trim())){
//							errorMsg += "，用户处于待审批状态，请等待管理员审批，才能正常使用。";
//						}else if("2".equals(user.getAuditStatus().trim())){
//							errorMsg += "，用户审批通过，可以正常使用。";
//						}
//						
//					}else{
//						String userCode = userTmp.getUserCode();
//						success = false;
//						errorMsg = "用户已经存在！用户编码[" + userCode + "]，不允许重复新增！";
//					}
//				}else{
//					success = false;
//					errorMsg = "用户名或面不能为空，请进行检查！";
//				}
//				
//			}else{
//				success = false;
//				errorMsg = "录入参数不足，请进行检查！";
//			}
//		} catch (Exception e) {
//			success = false;
//			errorMsg = e.getLocalizedMessage();
//		}
//		success = true;
//		return "json";
//	}
	
	public String getUserCode() {
		return userCode;
	}

	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public UserMsgInfo getUserMsgInfo() {
		return userMsgInfo;
	}

	public void setUserMsgInfo(UserMsgInfo userMsgInfo) {
		this.userMsgInfo = userMsgInfo;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public UserMongoDaoUtil getUserMongoDaoUtil() {
		return userMongoDaoUtil;
	}

	public void setUserMongoDaoUtil(UserMongoDaoUtil userMongoDaoUtil) {
		this.userMongoDaoUtil = userMongoDaoUtil;
	}

	public UserAccessService getUserAccessService() {
		return userAccessService;
	}

	public void setUserAccessService(UserAccessService userAccessService) {
		this.userAccessService = userAccessService;
	}
	
}
