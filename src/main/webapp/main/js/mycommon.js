
/**
 * 将日期类型转换为字符串
 * 日期格式YYYYMMDD
 * @param date
 * @returns {String}
 */
function myDateFormatter(date){
	var y = date.getFullYear();
	var m = date.getMonth()+1;
	var d = date.getDate();
	return ''+y+(m<10?('0'+m):m)+(d<10?('0'+d):d);
}

/**
 * 将字符串转换为日期类型
 * 日期格式YYYYMMDD
 * @param s
 * @returns {Date}
 */
function myDateParser(s){
	if (!s) return new Date();
	
	var y = parseInt(s.substring(0,4),10);
	var m = parseInt(s.substring(4,6),10);
	var d = parseInt(s.substring(6,8),10);
	if (!isNaN(y) && !isNaN(m) && !isNaN(d)){
		return new Date(y,m-1,d);
	} else {
		return new Date();
	}
}




/**
 * 将日期类型转换为字符串
 * 日期格式yyyy-MM-dd HH:mm:ss
 * @param date
 * @returns {String}
 */
function myDateTimeFormatter(date){
	var y = date.getFullYear();
	var m = date.getMonth()+1;
	var d = date.getDate();
	var h = date.getHours();
	var mi = date.getMinutes();
	var s = date.getSeconds();
	return '' + y + '-' + (m<10?('0'+m):m) + '-' + (d<10?('0'+d):d) + ' ' + (h<10?('0'+h):h) + ':' + (mi<10?('0'+mi):mi) + ':' + (s<10?('0'+s):s);
}

/**
 * 将字符串转换为日期类型
 * 日期格式yyyy-MM-dd HH:mm:ss
 * @param s
 * @returns {Date}
 */
function myDateTimeParser(s){
	if (!s) return new Date();
	
	var y = parseInt(s.substring(0,4),10);
	var m = parseInt(s.substring(5,7),10);
	var d = parseInt(s.substring(8,10),10);
	
	var h = parseInt(s.substring(11,13),10);
	var mi = parseInt(s.substring(14,16),10);
	var se = parseInt(s.substring(17,19),10);
	
	if (!isNaN(y) && !isNaN(m) && !isNaN(d)){
		var date = new Date(y,m-1,d,h,mi,se);
//		date.setHours(h);
//		date.setMinutes(mi);
//		date.setSeconds(se);
		return date;
	} else {
		return new Date();
	}
}

/**
 * 将easyui的datagrid中的代码翻译为汉字
 * @param val
 * @param row
 * @returns
 */
function formatValidStatus(val,row){
	if(val == '1'){
		return '有效';
	}else if(val == '0'){
		return '无效';
	}
}



/**
 * 将easyui的datagrid中的代码翻译为汉字
 * @param val
 * @param row
 * @returns
 */
function formatTrueOrFalse(val,row){
	if(val == '1'){
		return '是';
	}else if(val == '0'){
		return '否';
	}
}