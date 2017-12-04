<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="/main/taglibs.jsp"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

<script src="${ctx }/main/js/mycommon.js" type="text/javascript"></script>
<script src="${ctx }/pages/menu/js/editMenu.js" type="text/javascript"></script>

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
<body onload="init()">
<div class="easyui-panel" title="菜单信息" style="width:100%;height:auto;">
    <form id="menuForm" action="menu/saveMenu.do" method="post" enctype="multipart/form-data">
        <table>
            <tr>
            	<input type="hidden" id="menu_editType_hidden" name="editType" value="${editType }"/>
            	
            	<td><label>序列号</label></td>
				<td colspan="3">
					<label id="menu_id_label">${menu.id }</label>
					<input type="hidden" id="menu_id_hidden" name="menu.id" value="${menu.id }" />
				</td>
				<td><label>菜单级别</label></td>
				<td colspan="3">
					<input type="hidden" id="menu_menuLevel_hidden" value="${menu.menuLevel }"/>
					<input id="menu_menuLevel_edit" name="menu.menuLevel" class="easyui-combobox" 
	                	data-options="
							valueField: 'value',
							textField: 'label',
							data: [{
								label: '1级',
								value: '1'
							},{
								label: '2级',
								value: '2'
							},{
								label: '--',
								value: '',
								'selected':true
							}]"/>
				</td>
				<td><label>上级菜单流水号</label></td>
				<td colspan="7">
					<input type="hidden" id="menu_upperId_hidden" value="${menu.upperId }">
					<input id="menu_upperId_edit" name="menu.upperId" class="easyui-combobox" data-options="
							valueField: 'value',
							textField: 'label'"  style="width:100%"/>
					
				</td>
            </tr>
            <tr>
            	<td><label>菜单名称</label></td>
				<td colspan="7">
					<input id="menu_menuName_edit" name="menu.menuName" class="easyui-textbox" value="${menu.menuName }" style="width:100%"/>
				</td>
				<td><label>菜单功能代码</label></td>
				<td colspan="3">
					<input name="menu.funcCode" class="easyui-textbox" value="${menu.funcCode }" style="width:100%"/>
				</td>
				<td><label>菜单展示顺序</label></td>
				<td colspan="3">
					<input id="menu_displayNo_edit" name="menu.displayNo" class="easyui-textbox" value="${menu.displayNo }" style="width:100%"/>
				</td>
            </tr>
            
            <tr>
            	<td><label>菜单url</label></td>
				<td colspan="7">
					<input name="menu.url" class="easyui-textbox" value="${menu.url }" style="width:100%"/>
				</td>
				<td><label>是否管理员菜单</label></td>
				<td colspan="3">
					<input type="hidden" id="menu_adminMenuFlag_hidden" value="${menu.adminMenuFlag }">
					<input id="menu_adminMenuFlag_edit" name="menu.adminMenuFlag" class="easyui-combobox" 
	                	data-options="
							valueField: 'value',
							textField: 'label',
							data: [{
								label: '管理员菜单',
								value: '1'
							},{
								label: '非管理员菜单',
								value: '0',
								'selected':true
							}]"/>
				</td>
				<td><label>有效状态</label></td>
				<td colspan="3">
					<input type="hidden" id="menu_validStatus_hidden" value="${menu.validStatus }">
					<input id="menu_validStatus_edit" name="menu.validStatus" class="easyui-combobox" 
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
				<td><label>备注</label></td>
				<td colspan="15"><input name="menu.remark" class="easyui-textbox" value="${menu.remark }" style="width:100%"></td>
            </tr>
            
            <tr>
				<td><label>插入时间</label></td>
				<td colspan="3">
					<input type="hidden" id="menu_insertTime_hidden" value="<fmt:formatDate value="${menu.insertTime }"   pattern="yyyy-MM-dd HH:mm:ss" type="date" dateStyle="long" />">
					<input id="menu_insertTime_edit" name="menu.insertTime" class="easyui-datetimebox" data-options="formatter:myDateTimeFormatter,parser:myDateTimeParser" value="${menu.insertTime }"/>
				</td>
				<td><label>修改时间</label></td>
				<td colspan="3">
					<input type="hidden" id="menu_updateTime_hidden" value="<fmt:formatDate value="${menu.updateTime }"   pattern="yyyy-MM-dd HH:mm:ss" type="date" dateStyle="long" />">
					<input id="menu_updateTime_edit" name="menu.updateTime" class="easyui-datetimebox" data-options="formatter:myDateTimeFormatter,parser:myDateTimeParser" value="${menu.updateTime }"/>
				</td>
				<td><label>系统代码</label></td>
				<td colspan="3">
					<input name="menu.syscode" class="easyui-textbox" value="${menu.syscode }" style="width:100%"/>
				</td>
				<td><label></label></td>
				<td colspan="3"></td>
            </tr>
            
            <tr>
                <td></td>
                <td colspan="7"></td>
                <td></td>
                <td><!-- 这里不能使用submit，因为submit会进行页面跳转，新跳转的页面会只显示json数据，应该用异步方式提交表单 -->
                	<input type="button" value="保存" onClick="saveMenu()"></input>
                </td>
                <td></td>
                <td colspan="6"></td>
            </tr>
        </table>
    </form>
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