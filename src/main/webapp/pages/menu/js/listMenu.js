var url;

		
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
    $('#dg').datagrid({
    	//queryParams: {
		//	userNames: '严凯杰'
		//}
		queryParams:postData
	}); 
}

function newRecord(title){
	//在新的标签页中打开新增清单页面
	addTab(title, contextRootPath + '/menu/editMenu.do?editType=new');
}

function editRecord(title){
	
	var row = $('#dg').datagrid('getSelected');    //这一步可以改造为从后台异步获取数据
	
	if(row != null){
		var id = row.id;
		
		addTab(title, contextRootPath + '/menu/editMenu.do?editType=edit&id=' + id);
	}else{
		//alert("请选择一条记录进行修改");
		//(提示框标题，提示信息)
		$.messager.alert('提示','请选择一条记录进行修改');
	}
}


function destroyRecord(){
	var row = $('#dg').datagrid('getSelected');
	if (row){
		$.messager.confirm('Confirm','Are you sure you want to destroy this user?',function(r){
			if (r){
				$.post(contextRootPath + '/menu/deleteMenu.do',{id:row.id},function(result){
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

function closeTab(title){
	//根据tab的标题关闭
	//content_tabs是生成tab的容器id，close表示关闭，tab_name是tab的标题
	//$("#content_tabs").tabs('close','tab_name');
	var jq = top.jQuery;
	if (jq('#home').tabs('exists', title)) {
		jq('#home').tabs('close', title);
	}
}