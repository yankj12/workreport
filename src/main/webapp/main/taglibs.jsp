<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<!-- set cdn here -->
<!-- property cdn: ${ctx} or "//www.yankj12.info/Public/js" -->
<!-- 自适应http和https协议 -->
<c:set var="cdn" value="//www.yankj12.info/Public/js"/>

<script type="text/javascript">
  var contextRootPath = "${ctx}";
</script>
<script src="${cdn}/easyui/jquery.min.js" type="text/javascript"></script>
<script src="${cdn}/easyui/jquery.easyui.min.js" type="text/javascript"></script>
<link href="${cdn}/easyui/themes/default/easyui.css" rel="stylesheet" type="text/css" />
<link href="${cdn}/easyui/themes/icon.css" rel="stylesheet" type="text/css" />