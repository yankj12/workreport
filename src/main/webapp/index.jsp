<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="/main/taglibs.jsp"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>LayOut</title>
<script src="${ctx }/easyui/jquery.min.js"
	type="text/javascript"></script>
<script src="${ctx }/easyui/jquery.easyui.min.js"
	type="text/javascript"></script>
<link href="${ctx }/easyui/themes/default/easyui.css"
	rel="stylesheet" type="text/css" />
<link href="${ctx }/easyui/themes/icon.css"
	rel="stylesheet" type="text/css" />
<script language="JavaScript">
	function addTab(title, url) {
		if ($('#home').tabs('exists', title)) {
			$('#home').tabs('select', title);
		} else {
			var content = '<iframe scrolling="auto" frameborder="0" src="'
					+ url + '" style="width:100%;height:100%;"></iframe>';
			$('#home').tabs('add', {
				title : title,
				content : content,
				closable : true
			});
		}
	}

	$(document).ready(function() {

	});
	
	function quit(){
		
		$.messager.confirm('提示', '您确定要退出系统吗？', function(r){
			if (r){
				//可以使用js的方式获取host及ip，也可以使用java的方式获取
				//top.window.location.href="${ctx}/login/logout";
				var protocol = window.location.protocol;
				//window.location.host会把hostname和端口一起返回
				//window.location.hostname只会返回hostname
				var host = window.location.host;
				//var port = window.location.port;
				var newUrl = protocol + '//' + host + '${ctx}/login/logout.do';
				console.log(newUrl);
				window.location.href=newUrl;
			}
		});
		
	}
	
	//onload="init()"
	function init(){
		
		$.ajax({
	        type:"POST", 
	        url:contextRootPath + "/menu/showMenus.do",
	        dataType:"json", 
	        contentType: "text/html;charset=UTF-8", 
	        success:function(result){
	        	if (result.success){
	        		//$.messager.alert('提示', '加载菜单');
	        		var menus = result.rows;
	        		
	        		if(menus != null){
		        		for(var i=0;i<menus.length;i++){
		        			
		        			var firstLevelMenuName = menus[i].menuName;
		        			//一级菜单没有url
		        			var subMenus = menus[i].subMenus;
		        			
		        			var menuSelected = false;
		        			if(i==0){
		        				menuSelected = true;
		        			}
		        			
							var innerHtmlStr = '<div style="overflow: auto; padding: 10px;"><ul class="easyui-tree">';
		        			
		        			if(subMenus != null){
		        				for(var j=0;j<subMenus.length;j++){
		        					var subMenuName = subMenus[j].menuName;
		        					var subMenuUrl = subMenus[j].url;
		        					var innerHtmlStr = innerHtmlStr + '<li><a href="#" onclick="addTab(\'' + subMenuName +'\',\'' + contextRootPath + '/' + subMenuUrl + '\')">' + subMenuName + '</a></li>';
		        				}
		        			}
							
		        			var innerHtmlStr = innerHtmlStr + '</ul></div>';
		        			
		        			
		        			$('#menuDiv').accordion('add', {  
	                            title : firstLevelMenuName,  
	                            selected : menuSelected,  
	                            content : innerHtmlStr  
	                        }); 
		        			
		        		}
	        		}
	        	}
	        },
	       	failure:function (result) {  
	       		//(提示框标题，提示信息)
	    		$.messager.alert('提示',result.errorMsg);
	       	}
		});
	}
</script>
<style>
.footer {
	width: 100%;
	text-align: center;
	line-height: 35px;
}

.top-bg {
	background-color: #d8e4fe;
	height: 80px;
}
</style>
</head>

<body class="easyui-layout" onload="init()">

	<div region="north" border="true" split="true"
		style="overflow: hidden; height: 10%;">
		
			<table style="width:100%;height:90%;cellpadding:0;cellspacing:0">
				<tr>
					<td style="text-align: center;">
					简单数据系统
					</td>
				</tr>
				<tr>
					<td style="text-align:right">
						当前用户：${userMsgInfo.userCode }-${userMsgInfo.userCName }
						&nbsp;
						<a href="#" onclick="quit();">[退出]</a>
						&nbsp;
					</td>
				</tr>
				
			</table>
		
	</div>

<!-- 	<div region="south" border="true" split="true"
		style="overflow: hidden; height: 40px;">
		<div class="footer">
			版权所有：<a href="http://www.ewan.cn/">益玩平台</a>
		</div>
	</div> -->

	<div region="west" split="true" title="功能菜单" style="width: 10%;">

		<div id="menuDiv" class="easyui-accordion"
			style="position: absolute; top: 27px; left: 0px; right: 0px; bottom: 0px;">
			<!-- 菜单通过动态加载出来 -->
		</div>
		
		<%-- <div id="aa" class="easyui-accordion"
			style="position: absolute; top: 27px; left: 0px; right: 0px; bottom: 0px;">

			<div title="工作日志" selected="true"
				style="overflow: auto; padding: 10px;">
				<ul class="easyui-tree">
					<li><a href="#" onclick="addTab('工作日志','${ctx }/workreport/workreport.do')">工作日志</a></li>
				</ul>
			</div>
			
			<div title="菜单管理" selected="true"
				style="overflow: auto; padding: 10px;">
				<ul class="easyui-tree">
					<li><a href="#" onclick="addTab('菜单管理','${ctx }/menu/listMenu.do')">菜单管理</a></li>
				</ul>
			</div>

		</div> --%>
	</div>

	<div id="mainPanle" region="center" style="overflow: hidden;width: 90%;">

		<div id="home" class="easyui-tabs" style="width: 100%; height: 100%;">
			<div title="Home">Hello,welcome to use this system.</div>
		</div>
	</div>
</body>
</html>