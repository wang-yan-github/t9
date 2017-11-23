 /**
  *自己写的列表树
  *t9 BBS
  *author:zp
  */
 (function($) {
 
 $.fn.T9Bbslefttree = function(options) {
		var settings = $.extend({
			
		}, options || {});
	//初始化變數
	var surl = settings.url || "";//每页显示条数
	var tarurl = settings.tarurl || "";//每页显示条数
	
	//var jsondata = "{root:[{name:'这是测试你懂吗',id:'99',children:[{name:'',id:''},{name:'',id:''}],title:'ss'},{name:'这是测试你懂吗',id:'88',children:[{name:'',id:''},{name:'',id:''}],title:'ss'}]}";
	var htmlobj =jQuery.ajax({url:surl,async:false});
	var data = htmlobj.responseText	;
	var dataObj = eval("("+data+")");//转换为json对象 
	var thisid = $(this).attr("id");
	$.each(dataObj.root,function(i){
		var firstdl = $("<dl>").attr("id","lf_"+dataObj.root[i].id).addClass("");
		var children = dataObj.root[i].children;
		$(firstdl).append(
				$("<dt>").append(
					$("<a href=\"javascript:;\" hidefocus=\"true\" onclick=\"leftside('lf_"+dataObj.root[i].id+"')\" title=\""+dataObj.root[i].name+"\">"+dataObj.root[i].name+"</a>")
				)
		);
		$.each(children,function(j){
			$(firstdl).append(
			$("<dd>").append(
				$("<a>").attr({title:"",hidefocus:"true",href:tarurl+"?bid="+children[j].id}).text(children[j].name)
			));
		});
		$("#"+thisid).append($(firstdl));
	});
  }
//自訂function 結束
})(jQuery)