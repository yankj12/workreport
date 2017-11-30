var url;

function saveMenu(){
	//将表单中数据序列化，再传到后台
	var data = $('#menuForm').serializeArray();//先进行序列化数组操作
	var paramStr = $.param(data);
    
	var editTypeOld = $('#menu_editType_hidden').val();
	var title = '';
	if(editTypeOld != null && editTypeOld == 'new'){
		title = '新增菜单';
	}else if(editTypeOld != null && editTypeOld == 'edit'){
		title = '修改菜单';
	}
	
	//通过异步的方式与后台进行交互
    $.ajax({
        type:"POST", 
        url: contextRootPath + "/menu/saveMenu.do?" + paramStr,
        dataType:"json", 
        contentType: "text/html;charset=UTF-8", 
        success:function(result){
        	if (result.success){
        		//(提示框标题，提示信息)
        		//alert(result.errorMsg);
        		
        		//easyui所有的$.messager都是异步的，如果想要在消息框关闭后做什么事情，需要些回调方法
        		$.messager.alert('提示',result.errorMsg,'info',function(){
        			//如果成功，关闭tab页，重新加载数据集
            		closeTab(title);
            		$('#dg').datagrid('reload');	// reload the user data
        		});
        		
//        		var menu = result.menu;
//        		
//        		//将url改为修改
//        		$('#menu_editType_hidden').val('edit');
//        		
//        		var menuId = menu.id;
//        		//jquery中.是选择器中的一种语法，所以不要在id中放入.
//        		$('#menu_id_label').text(menuId);
//        		$('#menu_id_hidden').val(menuId);
        		
        	}else{
        		//(提示框标题，提示信息)
        		$.messager.alert('提示',result.errorMsg);
        	}
        },
       	failure:function (result) {  
       		//(提示框标题，提示信息)
    		$.messager.alert('提示',result.errorMsg);
       	}
	});
	
}

function init(){
	//当editType为edit的时候，设置一些默认值
	var editType = $('#menu_editType_hidden').val();
	if(editType != null && editType == 'edit'){
		//menuLevel下拉列表设置默认值
		var menuLevel = $('#menu_menuLevel_hidden').val();
		$('#menu_menuLevel_edit').combobox('setValue', menuLevel);
		
		//menu_adminMenuFlag_hidden
		var adminMenuFlag = $('#menu_adminMenuFlag_hidden').val();
		$('#menu_adminMenuFlag_edit').combobox('setValue', adminMenuFlag);
		
		//有效无效下拉列表
		var validStatus = $('#menu_validStatus_hidden').val();
		$('#menu_validStatus_edit').combobox('setValue', validStatus);
		
		//插入时间
		var insertTime = $('#menu_insertTime_hidden').val();
		$('#menu_insertTime_edit').datetimebox('setValue', insertTime);
		
		//修改时间
		var updateTime = $('#menu_updateTime_hidden').val();
		$('#menu_updateTime_edit').datetimebox('setValue', updateTime);
		
		
	}
	
	
	//把上级菜单流水号作为下拉列表展示
	//先加载下拉列表内容
	//异步从action中加载数据
	$.ajax({
        type:"GET", 
        url: contextRootPath + "/menu/queryMenu.do?menuLevel=1&validStatus=1&pageing=0",
        //url:"leave/saveLeaveApplication?editType=新增",
        dataType:"json", 
        //data:postData,
        contentType: "text/html;charset=UTF-8", 
        success:function(result){
        	if (result.success){
        		var rows = result.rows;
        		
        		var menuAry = [];
        		
        		//再给下拉列表赋默认值
        		//从页面上加载上级菜单序列号
        		var upperId = $('#menu_upperId_hidden').val();
        		
        		for(var i=0;i<rows.length;i++){
        			var code = rows[i].id;
        			var name = rows[i].syscode + '-' + rows[i].id + '-' + rows[i].menuName;
        			var selec = false;
        			
        			if(upperId != null && upperId != '' && upperId == code){
        				selec = true;
        			}
        			menuAry.push({value:code,label:name,selected:selec});
        		}
        		//添加一个空白项
        		menuAry.push({value:'',label:'--'});
        		
        		$('#menu_upperId_edit').combobox('loadData', menuAry);
        		
        	}else{
        		$.messager.alert('提示', '加载上级菜单下拉列表失败');
        	}
        },
       	failure:function (result) {
       		//(提示框标题，提示信息)
    		$.messager.alert('提示', '加载上级菜单下拉列表失败');
       	}
	});
	
}


function closeTab(title){
	//根据tab的标题关闭
	//content_tabs是生成tab的容器id，close表示关闭，tab_name是tab的标题
	//$("#content_tabs").tabs('close','tab_name');
	var jq = top.jQuery;
	if (jq('#home').tabs('exists', title)) {
		jq('#home').tabs('close', title);
	}
}
