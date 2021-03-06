<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="/main/taglibs.jsp"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta http-equiv="Access-Control-Allow-Origin" content="*">

<script src="${ctx }/main/js/mycommon.js" type="text/javascript"></script>
<script src="${ctx }/workreport.js" type="text/javascript"></script>

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
<title>工作日志管理</title>
</head>
<body>
<div class="easyui-panel" title="查询条件" style="width:100%;height:auto;">
    <form id="conditionForm" action="workreport/findWorkReports.do" method="post" enctype="multipart/form-data">
        <table>
            <tr>
				<td>起始日期:</td>
				<td>
					<input name="startDay" class="easyui-textbox" data-options="prompt:'yyyyMMdd'">
				</td>
				<td>结束日期:</td>
				<td>
					<input name="endDay" class="easyui-textbox" data-options="prompt:'yyyyMMdd'">
				</td>
				<td>项目名称:</td>
				<td>
					<input name="projectName" class="easyui-textbox">
				</td>
				<td>项目代码:</td>
				<td>
					<input name="projectCode" class="easyui-combobox"
						data-options="
					        valueField: 'value',
					        textField: 'label',
					        url: '${ctx }/CodeSelectIntfServlet?codeType=projectCode'"/>
				</td>
				<td>日志编写人名称:</td>
				<td>
					<input name="writerName" class="easyui-textbox">
				</td>
				<td>日志类型:</td>
				<td>
					<input name="type" class="easyui-combobox" 
						data-options="
							valueField: 'value',
					        textField: 'label',
					        url: '${ctx }/CodeSelectIntfServlet?codeType=workReportType'"/>
				</td>
			</tr>
			
			<tr>
				<td>日志正文:</td>
				<td colspan="5">
					<input name="workText" class="easyui-textbox"style="width:100%;"/>
				</td>
				<td>数据类型:</td>
				<td>
					<input name="dataType" class="easyui-combobox" 
						data-options="
							valueField: 'value',
					        textField: 'label',
					        url: '${ctx }/CodeSelectIntfServlet?codeType=workReportDataType&selected=originalData'"/>
				</td>
				<td></td>
				<td></td>
				<td></td>
				<td></td>
			</tr>
						
            <tr>
                <td></td>
                <td></td>
                <td></td>
                <td colspan="2"><!-- 这里不能使用submit，因为submit会进行页面跳转，新跳转的页面会只显示json数据，应该用异步方式提交表单 -->
					<input type="button" value="查询" onClick="exeQuery()"></input>
					&nbsp;&nbsp;
					<input type="button" value="重置" onClick="resetConditionForm()"></input>
				</td>
                <td></td>
                <td></td>
                <td></td>
            </tr>
        </table>
    </form>
</div>
<table id="dg" title="查询结果" class="easyui-datagrid" style="width:100%;height:auto;"
		url="${ctx }/workreport/findWorkReports.do?validStatus=1"
		toolbar="#toolbar"
		rownumbers="true" pagination="true" fitColumns="true" singleSelect="false">
		<!-- table增加了pagination="true"属性，就增加了底部的分页工具栏 -->
	<thead>
		<tr>
			<!-- field必须不能重复，否则页面展示上比例调整起来很没有规律 -->
			<th data-options="field:'id',hidden:true">主键</th>
			<th field="day" width="30">日期</th>
			<th field="title" width="30">标题</th>
			<th field="type" width="30" formatter="formatWorkReportType">日志类型</th>
			<th field="dataType" width="30" formatter="formatWorkReportDataType">数据类型</th>
			<th data-options="field:'workText',hidden:true">日志内容</th>
			<th field="projectCode" width="10">项目代码</th>
			<th field="projectName" width="30">项目名称</th>
			<th field="writerName" width="30">日志编写人</th>
			<th field="members" width="60">团队成员</th>
			<th field="updateTime" width="30">修改时间</th>
			
		</tr>
	</thead>
</table>
<div id="toolbar">
	<a href="#" class="easyui-linkbutton" iconCls="icon-add" plain="true" onclick="newRecord('编写工作日志')">编写工作日志</a>
	<a href="#" class="easyui-linkbutton" iconCls="icon-edit" plain="true" onclick="editRecord('修改工作日志')">修改工作日志</a>
	<a href="#" class="easyui-linkbutton" iconCls="icon-remove" plain="true" onclick="destroyRecord()">删除工作日志</a>
	<a href="#" class="easyui-linkbutton" plain="true" onclick="viewSelectedRecords('查看选中日志')">查看选中日志</a>
</div>
	<!-- 下面dlg是为了有新增用户界面 -->
	<div id="dlg" class="easyui-dialog" style="width:800px;height:auto;padding:0px 0px"
			closed="true" buttons="#dlg-buttons">
		<form id="fm" method="post" novalidate>
		<table cellpadding="5" style="width:100%;">
			<tr>
				<td><label>日志日期</label></td>
				<td>
					<input type="hidden" id="userCode_edit" name="userCode" value="${userMsgInfo.userCode }"/>
					<input type="hidden" id="editType_edit" name="editType" value="new"/>
					<input type="hidden" id="id_edit" name="id" value=""/>
					<a id="btn" href="#" class="easyui-linkbutton" data-options="iconCls:'icon-user-back',onClick:workReportDayBack"></a>
					<input id="day_edit" name="day" class="easyui-textbox" value="" data-options="prompt:'yyyyMMdd',onChange:setWorkReportTitle"/>
					<a id="btn" href="#" class="easyui-linkbutton" data-options="iconCls:'icon-user-foward',onClick:workReportDayFoward"></a>
				</td>
				<td><label>日志编写人</label></td>
				<td>
					<input id="writerName_edit" name="writerName" class="easyui-textbox" value=""/>
				</td>
				
			</tr>

			<tr>
				<td><label>日志类型:</label></td>
				<td>
					<!-- <input id="type_edit" name="type" class="easyui-textbox" value="" data-options="onChange:setWorkReportTitle"/> -->
					<input id="type_edit" name="type" class="easyui-combobox" 
						data-options="
							valueField: 'value',
					        textField: 'label',
					        url: '${ctx }/CodeSelectIntfServlet?codeType=workReportType&required=true',
					        onChange:setWorkReportTitle"/>
				</td>
				<td><label>数据类型</label></td>
				<td>
					<input id="dataType_edit" name="dataType" class="easyui-combobox" 
						data-options="
							valueField: 'value',
					        textField: 'label',
					        url: '${ctx }/CodeSelectIntfServlet?codeType=workReportDataType&required=true'"/>
				</td>
				
			</tr>

			<tr>
				<td><label>日志标题</label></td>
				<td colspan="3">
					<input id="title_edit" name="title" class="easyui-textbox" value="" style="width:100%"/>
				</td>
				
			</tr>
			
			<tr>
				<td><label>项目代码:</label></td>
				<td>
					<!-- <input id="projectCode_edit" name="projectCode" class="easyui-textbox" value="" data-options="onChange:setWorkReportProjectName"/> -->
					<input id="projectCode_edit" name="projectCode" class="easyui-combobox"
						data-options="
					        valueField: 'value',
					        textField: 'label',
					        url: '${ctx }/CodeSelectIntfServlet?codeType=projectCode&required=true',
					        onChange:setWorkReportProjectName"/>
				</td>
				<td><label>项目名称:</label></td>
				<td>
					<input id="projectName_edit" name="projectName" class="easyui-textbox" value="" readonly/>
				</td>
				
			</tr>
			
			<tr>
				<td colspan="4"><label>工作日志:</label></td>
			</tr>
		</table>
			<div id="tabs" class="easyui-tabs" style="width:100%;height:410px;">
				<!-- 在出现滚动条的元素（如div）处，添加样式overflow:hidden;即可去掉滚动条 -->
				<div title="日志内容" style="padding:0px;overflow:hidden;">
					<table style="width:98%;height:auto;">
						<tr>
							<td colspan="4">
								<a href="#" class="easyui-linkbutton"   data-options="iconCls:'icon-indent',onClick:indentRowText">去除行首空格</a>
								<a href="#" class="easyui-linkbutton"   data-options="onClick:workTextSplitToTask">日志拆分成任务</a>
							</td>
						</tr>
						<tr>
							<!-- 待审批工单内容概要 -->
							<td colspan="4">
								<input id="workText_edit" name="workText" class="easyui-textbox" data-options="multiline:true" value="" style="width:100%;height:200px">
							</td>
						</tr>
			
						<tr>
							<td colspan="4"><label>评论:</label></td>
						</tr>
						<tr>
							<td colspan="4">
								<input id="comment_edit" name="comment" class="easyui-textbox" data-options="multiline:true" value="" style="width:100%;height:100px">
							</td>
						</tr>
					</table>
				</div>
				<div title="日志任务" style="padding:0px">
					<table id="dg2" class="easyui-datagrid" style="width:100%;height:auto;"
							url=""
							toolbar="#toolbar2"
							rownumbers="true" pagination="false" fitColumns="true" singleSelect="false">
							<!-- table增加了pagination="true"属性，就增加了底部的分页工具栏 -->
						<thead>
							<tr>
								<!-- field必须不能重复，否则页面展示上比例调整起来很没有规律 -->
								<th data-options="field:'id',hidden:true">主键</th>
								<th field="title" width="50">标题</th>
								<th data-options="field:'projectCode',hidden:true">项目代码</th>
								<th field="projectName" width="20">项目名称</th>
								<th data-options="field:'userrName',hidden:true">指派给</th>
								<th field="progressStatus" width="30">任务进度</th>
								<th field="keys" width="50">关键词</th>
								<th data-options="field:'comment',hidden:true">备注</th>
							</tr>
						</thead>
					</table>
					<div id="toolbar2">
						<a href="javascript:void(0)" class="easyui-linkbutton" data-options="iconCls:'icon-add',plain:true" onclick="append()">添加</a>
						<a href="javascript:void(0)" class="easyui-linkbutton" data-options="iconCls:'icon-remove',plain:true" onclick="removeit()">移除</a>
						<!-- <a href="javascript:void(0)" class="easyui-linkbutton" data-options="iconCls:'icon-undo',plain:true" onclick="reject()">撤销</a> -->
						<a href="javascript:void(0)" class="easyui-linkbutton" data-options="iconCls:'icon-edit',plain:true" onclick="editTask()">编辑任务信息</a>
						<a href="javascript:void(0)" class="easyui-linkbutton" data-options="iconCls:'icon-edit',plain:true" onclick="mergeToTask()">归并为一个任务</a>
					</div>
				</div>
			</div>
		</form>
	</div>
	<!-- 下面dlg-buttons是为了让新增用户页面有保存和取消按钮 -->
	<div id="dlg-buttons">
		<a href="javascript:void(0)" class="easyui-linkbutton c6" iconCls="icon-ok" onclick="saveRecord()" style="width:90px">保存</a>
		<a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-cancel" onclick="javascript:$('#dlg').dialog('close')" style="width:90px">取消</a>
	</div>
	
	<!-- 下面dlg是为了任务编辑界面 -->
	<div id="dlg2" class="easyui-dialog" style="width:800px;height:auto;padding:0px 0px;overflow:hidden;"
			closed="true" buttons="#dlg2-buttons">
		<form id="fm2" method="post" novalidate>
		<table cellpadding="5px" style="width:100%;">
		
			<tr>
				<td><label>日志标题</label></td>
				<td colspan="3">
					<input id="task_title_edit" name="title" class="easyui-textbox" value="" style="width:100%"/>
				</td>
				
			</tr>
			
			<tr>
				<td><label>项目代码:</label></td>
				<td>
					<!-- <input id="projectCode_edit" name="projectCode" class="easyui-textbox" value="" data-options="onChange:setWorkReportProjectName"/> -->
					<input id="task_projectCode_edit" name="projectCode" class="easyui-combobox"
						data-options="
					        valueField: 'value',
					        textField: 'label',
					        url: '${ctx }/CodeSelectIntfServlet?codeType=projectCode&required=true',
					        onChange:setTaskProjectName"/>
				</td>
				<td><label>项目名称:</label></td>
				<td>
					<input id="task_projectName_edit" name="projectName" class="easyui-textbox" value="" readonly/>
				</td>
			</tr>
			
			<tr>
				<td><label>指派给</label></td>
				<td>
					<input id="task_userrName_edit" name="userName" class="easyui-textbox" value=""/>
				</td>
				<td><label>任务进度</label></td>
				<td>
					<input id="task_progressStatus_edit" name="progressStatus" class="easyui-textbox" value=""/>
				</td>
			</tr>
			<tr>
				<td><label>关键词</label></td>
				<td colspan="3">
					<input id="task_keys_edit" name="keys" class="easyui-textbox" value="" style="width:98%">
				</td>
			</tr>
			<tr>
				<td><label>备注</label></td>
				<td colspan="3">
					<input id="task_comment_edit" name="comment" class="easyui-textbox" data-options="multiline:true" value="" style="width:98%;height:100px">
				</td>
			</tr>
		</table>
		</form>
	</div>
	<!-- 下面dlg-buttons是为了让新增用户页面有保存和取消按钮 -->
	<div id="dlg2-buttons">
		<a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-cancel" onclick="closeTask()" style="width:90px">关闭</a>
	</div>
	
	
	<!-- 下面dlg是为了任务编辑界面 -->
	<div id="dlg3" class="easyui-dialog" style="width:800px;height:auto;padding:0px 0px;overflow:hidden;"
			closed="true" buttons="#dlg3-buttons">
		<table cellpadding="5px" style="width:100%;">
			<tr>
				<td colspan="4">
					<input id="workreport_view" class="easyui-textbox" data-options="multiline:true" value="" style="width:98%;height:600px">
				</td>
			</tr>
		</table>
	</div>
	<!-- 下面dlg-buttons是为了让新增用户页面有保存和取消按钮 -->
	<div id="dlg3-buttons">
		<a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-cancel" onclick="closeWorkReportView()" style="width:90px">关闭</a>
	</div>
	<script type="text/javascript">

	
	</script>
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