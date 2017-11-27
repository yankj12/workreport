<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="/main/taglibs.jsp"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<script src="${ctx }/easyui/jquery.min.js" type="text/javascript"></script>
<script src="${ctx }/easyui/jquery.easyui.min.js" type="text/javascript"></script>

<script src="${ctx }/main/js/mycommon.js" type="text/javascript"></script>
<script src="${ctx }/pages/menu/js/listMenu.js" type="text/javascript"></script>

<link href="${ctx }/easyui/themes/default/easyui.css" rel="stylesheet"type="text/css" />
<link href="${ctx }/easyui/themes/icon.css" rel="stylesheet" type="text/css" />
	<!-- 下拉列表可能需要的样式 -->
	<style type="text/css">
		.item-img{
			display:inline-block;
			vertical-align:middle;
			width:16px;
			height:16px;
		}
		.item-text{
			display:inline-block;
			vertical-align:middle;
			padding:3px 0 3px 3px;
		}
	</style>
<title>Insert title here</title>
</head>
<body>
<div class="easyui-panel" title="查询条件" style="width:100%;height:auto;">
    <form id="conditionForm" action="menu/queryMenu.do" method="post" enctype="multipart/form-data">
        <table>
            <tr>
	            <td>系统代码</td>
                <td>
                	<input name="syscode" class="easyui-combobox" 
	                	data-options="
							valueField: 'value',
							textField: 'label',
							data: [{
								label: 'manage',
								value: 'manage'
							},{
								label: '--',
								value: '',
								'selected':true
							}]"/>
                </td>
                <td>菜单级别</td>
                <td>
                	<input name="menuLevel" class="easyui-combobox" 
	                	data-options="
							valueField: 'value',
							textField: 'label',
							data: [{
								label: '1级菜单',
								value: '1'
							},{
								label: '2级菜单',
								value: '2'
							},{
								label: '--',
								value: '',
								'selected':true
							}]"/>
                </td>
                <td>菜单名称</td>
                <td>
                	<input name="menuName" class="easyui-textbox" style="width:100%"/>
                </td>
                <td>功能代码</td>
                <td>
                	<input name="funcCode" class="easyui-textbox" style="width:100%"/>
                </td>
            </tr>
            <tr>
                <td>上级菜单名称</td>
                <td>
                	<input name="upperMenuName" class="easyui-textbox" style="width:100%"/>
                </td>
                <td>上级菜单代码</td>
                <td>
                	<input name="upperFuncCode" class="easyui-textbox" style="width:100%"/>
                </td>
                <td>是否管理员菜单</td>
                <td><input name="adminMenuFlag" class="easyui-combobox" 
                	data-options="
						valueField: 'value',
						textField: 'label',
						data: [{
							label: '管理员菜单',
							value: '1'
						},{
							label: '非管理员菜单',
							value: '0'
						},{
							label: '--',
							value: '',
							'selected':true
						}]"/>
				</td>
                <td>有效状态</td>
                <td><input name="validStatus" class="easyui-combobox" 
                	data-options="
						valueField: 'value',
						textField: 'label',
						data: [{
							label: '有效',
							value: '1',
							'selected':true
						},{
							label: '无效',
							value: '0'
						},{
							label: '--',
							value: ''
						}]"/>
				</td>
            </tr>
            <tr>
                <td></td>
                <td></td>
                <td></td>
                <td><!-- 这里不能使用submit，因为submit会进行页面跳转，新跳转的页面会只显示json数据，应该用异步方式提交表单 -->
                	<input type="button" value="查询" onClick="exeQuery()"></input>
                </td>
                <td></td>
                <td></td>
                <td></td>
                <td></td>
            </tr>
        </table>
    </form>
</div>
<table id="dg" title="查询结果" class="easyui-datagrid" style="width:100%;height:auto;"
		url="${ctx }/menu/queryMenu.do"
		toolbar="#toolbar"
		rownumbers="true" pagination="true" fitColumns="true" singleSelect="true">
		<!-- table增加了pagination="true"属性，就增加了底部的分页工具栏 -->
	<thead>
		<tr>
			<th field="id" width="50">ID</th>
			<th field="upperId" width="50">上级ID</th>
			<th field="syscode" width="50">系统代码</th>
			<th field="menuLevel" width="50">菜单级别</th>
			<th field="menuName" width="50">菜单名称</th>
			<th field="funcCode" width="50">功能代码</th>
			<th field="url" width="50">URL</th>
			<th field="displayNo" width="50">展示顺序</th>
			<th field="adminMenuFlag" width="50" formatter="formatTrueOrFalse">是否管理员菜单</th>
			<th field="validStatus" width="50" formatter="formatValidStatus">有效状态</th>
			<th field="insertTime" width="50">创建时间</th>
			<th field="updateTime" width="50">修改时间</th>
			
		</tr>
	</thead>
</table>
<div id="toolbar">
	<a href="#" class="easyui-linkbutton" iconCls="icon-add" plain="true" onclick="newRecord('新增菜单')">新增菜单</a>
	<a href="#" class="easyui-linkbutton" iconCls="icon-edit" plain="true" onclick="editRecord('修改菜单')">修改菜单</a>
	<a href="#" class="easyui-linkbutton" iconCls="icon-remove" plain="true" onclick="destroyRecord()">删除菜单</a>
</div>

	<style type="text/css">
		#fm{
			margin:0;
			padding:10px 30px;
		}
		.ftitle{
			font-size:14px;
			font-weight:bold;
			padding:5px 0;
			margin-bottom:10px;
			border-bottom:1px solid #ccc;
		}
		.fitem{
			margin-bottom:5px;
		}
		.fitem label{
			display:inline-block;
			width:80px;
		}
		.fitem input{
			width:160px;
		}
	</style>
</body>
</html>