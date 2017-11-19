var url;

//项目代码和项目名称对照的map
var projectCodeToNameMap = {	'cif':'客户系统',
								'cqp':'车险定价',
								'ecif':'客户主数据'
							};



/**
 * 页面初始化后做些东西
 */
$(document).ready(function(){
	console.log('init');
	
}); 


//通过让数据表格url为调用js方法，这样可以让不管是分页插件还是查询条件点击下一页，都可以将查询条件和分页信息同时传到后台action
function exeQuery(){
	var data = $('#conditionForm').serializeArray();//先进行序列化数组操作
    var postData = {};  //创建一个对象  
    $.each(data, function(n,v) {
    	//postData[data[n].name]=data[n].value;  //循环数组，把数组的每一项都添加到对象中
    	//上面这种方式，在遇到多个同名input的时候，会进行覆盖操作，导致后台只能获取到最后一个元素
    	
        //如果有多个input的name重名，那么postData[data[n].name]存储的应该是一个数组
        if(postData[data[n].name]){
        	//已经存在的话
        	//有可能其中存储的是单个变量，也有可能存储的是个数组
        	var value = postData[data[n].name];
        	//$.isArray(value);  //jquery的方式判断是否为数组
        	if(value instanceof Array){
        		value.push(data[n].value);
        	}else{
        		var perviewElement = postData[data[n].name];
        		var ary = new Array();
        		//先应该将上一个元素添加到数组中
        		ary.push(perviewElement);
        		ary.push(data[n].value);
        		postData[data[n].name]=ary;
        	}
        }else{
        	//没有存在，那么我们当做单个变量来处理
        	postData[data[n].name]=data[n].value;  //循环数组，把数组的每一项都添加到对象中
        }
    });
	//通过设置datagrid的queryParams，可以将数据表格的参数带到后台
    //此处并不需要专门给url赋值，因为使用的是表单的url
    $('#dg').datagrid({
		//queryParams: {
		//	userNames: '张三'
		//}
		queryParams:postData
	}); 
}

function newRecord(title){
	//打开新的标签，在新的标签中进行添加操作
	//addTab(title,'leave/editLeaveApplication?editType=new');
	
	$('#dlg').dialog('open').dialog('setTitle','编写工作日志');
	$('#fm').form('clear');
	//设置修改类型，否则action中保存方法不知道是什么修改类型
	$('#editType_edit').val("new");
	//给日期赋默认值yyyyMMdd
	var date = new Date();
	var y = date.getFullYear();
	var m = date.getMonth()+1;
	var d = date.getDate();
	var day = ''+y+(m<10?('0'+m):m)+(d<10?('0'+d):d);
	$('#day_edit').textbox('setValue', day);
	
	//标题前缀，默认是日期yyyy-MM-dd
	var titlePreFix = ''+y+'-'+(m<10?('0'+m):m)+'-'+(d<10?('0'+d):d);
	//标题后缀
	var titleSuffix = '每日工作计划';
	var title =  titlePreFix + titleSuffix;
	$('#title_edit').textbox('setValue', title);
	
	var projectCode = 'cqp';
	$('#projectCode_edit').textbox('setValue', projectCode);
	$('#projectName_edit').textbox('setValue', projectCodeToNameMap[projectCode]);
	$('#writerName_edit').textbox('setValue', '严凯杰');
	$('#type_edit').textbox('setValue', 'team');
	
}


function editRecord(title){
	
	var row = $('#dg').datagrid('getSelected');    //这一步可以改造为从后台异步获取数据
	
	if(row != null){
		var id = row.id;
		
		//异步从action中加载数据
		$.ajax({
	        type:"GET", 
	        url:contextRootPath + "/workreport/findUniqueWorkReport.do?id=" + id,
	        //url:"leave/saveLeaveApplication?editType=新增",
	        dataType:"json", 
	        //data:postData,
	        contentType: "text/html;charset=UTF-8", 
	        success:function(result){
	        	if (result.success){
					
					//先打开界面
					$('#dlg').dialog('open').dialog('setTitle','编写工作日志');
					$('#fm').form('clear');

					//为一些属性赋默认值
	        		var workReport = result.object;
					
					$("#id_edit").val(workReport.id);
					
	        		//设置修改类型，否则action中保存方法不知道是什么修改类型
	        		$('#editType_edit').val("edit");
	        		
	        		//
	        		$('#day_edit').textbox('setValue', workReport.day);
	        		
	        		$('#writerName_edit').textbox('setValue', workReport.writerName);
					$('#title_edit').textbox('setValue', workReport.title);
					$('#type_edit').textbox('setValue', workReport.type);
	        		$('#projectCode_edit').textbox('setValue', workReport.projectCode);
	        		$('#projectName_edit').textbox('setValue', workReport.projectName);
	        		$('#workText_edit').textbox('setValue', workReport.workText);
	        		$('#comment_edit').textbox('setValue', workReport.comment);
	        		
	        	}else{
	        		$.messager.alert('提示',result.errorMsg);
	        	}
	        },
	       	failure:function (result) {  
	       		//(提示框标题，提示信息)
	    		$.messager.alert('提示','加载失败');
	       	}
		});
	}else{
		//alert("请选择一条记录进行修改");
		//(提示框标题，提示信息)
		$.messager.alert('提示','请选择一条记录进行修改');
	}
}


function destroyRecord(){
	var row = $('#dg').datagrid('getSelected');
	if (row){
		$.messager.confirm('Confirm','确定删除这条记录吗？',function(r){
			if (r){
				$.post(contextRootPath + '/workreport/deleteWorkReport.do',{id:row.id},function(result){
					if (result.success){
						$('#dg').datagrid('reload');	// reload the user data
					} else {
						$.messager.show({	// show error message
							title: 'Error',
							msg: result.errorMsg
						});
					}
				},'json');
			}
		});
	}
}


function saveRecord(){
	var editType = $('#editType_edit').val();
	var id = $('#id_edit').val();
	var day = $('#day_edit').textbox('getValue');
	var projectCode = $('#projectCode_edit').textbox('getValue');
	var projectName = $('#projectName_edit').textbox('getValue');
	var writerName = $('#writerName_edit').textbox('getValue');
	var title = $('#title_edit').textbox('getValue');
	var type = $('#type_edit').textbox('getValue');
	var workText = $('#workText_edit').textbox('getValue');
	var comment = $('#comment_edit').textbox('getValue');
	
	var requestVo = new Object();
	requestVo.editType = editType;
	
	requestVo.id = id;
	requestVo.day = day;
	requestVo.title = title;
	requestVo.type = type;
	requestVo.projectCode = projectCode;
	requestVo.projectName = projectName;
	requestVo.writerName = writerName;
	requestVo.workText = workText;
	requestVo.comment = comment;
	
	$.post(contextRootPath + '/workreport/saveWorkReport.do', requestVo, function(result){
		if (result.success){
			//$.messager.alert('提示',result.errorMsg);
			
			//此处会写不会写id关系不大了，因为下面会直接进行close
			// var workReport = result.object;
			// var id = workReport.id;
			// $('#id_edit').val(id);
			// $('#editType_edit').val('edit');
			
			$('#dlg').dialog('close');
			$('#dg').datagrid('reload');	// reload the user data
		} else {
			$.messager.show({	// show error message
				title: 'Error',
				msg: result.errorMsg
			});
		}
	},'json');

}


function setWorkReportTitle(){
	var day = $('#day_edit').textbox('getValue');
	if(day.length >= 8){
		var y = parseInt(day.substring(0,4),10);
		var m = parseInt(day.substring(4,6),10);
		var d = parseInt(day.substring(6,8),10);

		var type = $('#type_edit').textbox('getValue');
		
		//标题前缀，默认是日期yyyy-MM-dd
		var titlePreFix = ''+y+'-'+(m<10?('0'+m):m)+'-'+(d<10?('0'+d):d);
		//标题后缀
		var titleSuffix = '每日工作计划';
		if(type != null && type == 'person'){
			titleSuffix = '工作日志';
		}
		var title =  titlePreFix + titleSuffix;
		$('#title_edit').textbox('setValue', title);
	}
}

function setWorkReportProjectName(){
	var projectCode = $('#projectCode_edit').textbox('getValue');
	if(projectCode != null && projectCode.length > 0  && projectCodeToNameMap != null
		&& projectCodeToNameMap[projectCode] != null ){
		$('#projectName_edit').textbox('setValue', projectCodeToNameMap[projectCode]);
	}
}

/**
 * 去除工作日志中每行行首的空白符
 */
function indentRowText(){
	var workText = $('#workText_edit').textbox('getValue');
	var newWorkText = '';
	var rows = workText.split(/[\r\n]/g);
	for(var i=0;i<rows.length;i++){
		rows[i] = rows[i].replace(/^\s*/, '');
		newWorkText += rows[i];
		
		// 加上换行符
		if(i<rows.length-1){
			newWorkText += '\n';
		}
	}
	//alert(newWorkText);
	$('#workText_edit').textbox('setValue', newWorkText);
}

function addDate(date,dayAdd){
	var a = new Date(date)
	a = a.valueOf()
	a = a + dayAdd * 24 * 60 * 60 * 1000
	a = new Date(a)
	return a;
}

function changeWorkReportDay(num){
	var day = $('#day_edit').textbox('getValue');
	
	var y = null;
	var m = null;
	var d = null;
	var newDate = null;
	var nowDateStr = '';

	if(day.length >= 8){
		y = parseInt(day.substring(0,4),10);
		m = parseInt(day.substring(4,6),10);
		d = parseInt(day.substring(6,8),10);
	}else{
		var date = new Date();
		y = date.getFullYear();
		m = date.getMonth()+1;
		d = date.getDate();		
	}
	nowDateStr = day = ''+y+'-'+(m<10?('0'+m):m)+'-'+(d<10?('0'+d):d);
	
	if(num == -1 || num == '-1'){
		newDate = addDate(nowDateStr, -1);
	}else if(num == 1 || num == '1'){
		newDate = addDate(nowDateStr, 1);
	}
	y = newDate.getFullYear();
	m = newDate.getMonth()+1;
	d = newDate.getDate();
	
	day = ''+y+(m<10?('0'+m):m)+(d<10?('0'+d):d);
	
	$('#day_edit').textbox('setValue', day);
}

function workReportDayBack(){
	changeWorkReportDay(-1);
}

function workReportDayFoward(){
	changeWorkReportDay(1);
}

function resetConditionForm(){
	//JQuery中没有reset方法,需要通过JQuery来重置表单呢
	//或者使用dom的方法来做 document.getElementById
	$("#conditionForm")[0].reset();
}

/**
 * attendanceDay控件选择日期后，当点中某一天的时候会触发onSelect事件
 * @param date
 */
function setStatisticsMonthByDay(date){
	var y = date.getFullYear();
	var m = date.getMonth()+1;
	var d = date.getDate();
	
	//经过多次尝试，在js中给easyui的textbox赋值，需要向下面这样进行
	//尽量通过id的方式获取，可以选择到easyui的textbox，如果通过其他选择器，那么只能选中textbox组件的某一部分
	$("#statisticsMonth_edit").combobox('setValue',''+y+(m<10?('0'+m):m));
}

/**
 * 下拉列表控件选择option后，会触发onSelect事件
 * @param date
 */
function setUserNameByCode(s){
	var name = s.label;
	//如下方式set值
	$('#userName_edit').combobox('setValue', name);
}

/**
 * 下拉列表控件选择option后，会触发onSelect事件
 * @param date
 */
function selectUserCodeByName(s){
	var name = s.label;
	var list = $('#userCode_edit').combobox('getData');
		
	var userCode = '';
	if(list != null){
		for(var i=0;i<list.length;i++){
			if(list[i].label==name){
				userCode = list[i].value;
				break;
			}
		}
	}
	
	//select方法会选中某一项
	//$('#userCode_edit').combobox('select', userCode);
	$('#userCode_edit').combobox('setValue', userCode);
	
	//easyui 的textbox，但是我觉得combobox类似
	//setText	text	Set the displaying text value.
	//setValue	value	Set the component value.
}

function formatWorkReportType(val,row){
	if(val == 'team'){
		return '团队日志';
	}else if(val == 'person'){
		return '个人日志';
	}
}


/**
 * jquery easyui 在子tab页中打开新tab页
 * @param title
 * @param url
 */
function addTab(title, url) {
	//jquery easyui 在子tab页中打开新tab页(关于easyUI在子页面增加显示tabs的一个问题)
	//在子页面点个按钮也能触发增加子页面的情况。
	//改正的关键是用top.jQuery这个函数，这个函数具体出外我忘记了，用法看似是取得整个父页面对象，正确是写法：
	//http://blog.csdn.net/zhang527836447/article/details/44676581
	//其他写法尝试了下有问题，就采用了如下的写法	
	var jq = top.jQuery;
	if (jq('#home').tabs('exists', title)) {
		jq('#home').tabs('select', title);
	} else {
		var content = '<iframe scrolling="auto" frameborder="0" src="'
				+ url + '" style="width:100%;height:100%;"></iframe>';
		jq('#home').tabs('add', {
			title : title,
			content : content,
			closable : true
		});
	}
}	