<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="/struts-tags" prefix="s"%>

<%@ page import="org.springframework.web.context.support.WebApplicationContextUtils"%>
<%@ page import="org.springframework.context.ApplicationContext"%>
<%@ page import="com.yan.common.schema.ApplicationConfig"%>
<%
	ServletContext sc = this.getServletConfig().getServletContext();
	ApplicationContext ac2 = WebApplicationContextUtils.getWebApplicationContext(sc);
	// 要获取的对象 serv = (要获取的对象) ac2.getBean("spring配置文件中的id");
	ApplicationConfig applicationConfig = (ApplicationConfig)ac2.getBean("applicationConfig");
	String cdn = applicationConfig.getCdn();
%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<c:set var="cdn" value="<%=cdn %>"/>

<script type="text/javascript">
  var contextRootPath = "${ctx}";
</script>
<script src="${cdn}/easyui/jquery.min.js" type="text/javascript"></script>
<script src="${cdn}/easyui/jquery.easyui.min.js" type="text/javascript"></script>
<link href="${cdn}/easyui/themes/default/easyui.css" rel="stylesheet" type="text/css" />
<link href="${cdn}/easyui/themes/icon.css" rel="stylesheet" type="text/css" />