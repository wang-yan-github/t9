 /**
  *自己写的回复列表树
  *t9 BBS
  *author:zp
  */
 (function($) {
 
 $.fn.T9Bbsreplaylist = function(options) {
		var settings = $.extend({
		}, options || {});
	//初始化變數
	var data = settings.data || "";//
	var thisid = $(this).attr("id");
	//var data111 = "[{name:'1',id:'99'}]";
	//var jsondata = "{root:[{name:'这是测试你懂吗',id:'99',children:[{name:'',id:''},{name:'',id:''}],title:'ss'},{name:'这是测试你懂吗',id:'88',children:[{name:'',id:''},{name:'',id:''}],title:'ss'}]}";
	//var htmlobj =jQuery.ajax({url:surl,async:false});
	//var data = htmlobj.responseText	;
	//var dataObj = eval("("+data111+")");//转换为json对象 \
	//alert(data.length);
	$.each(data,function(i){
		var indexlou = i +1;
		var seqId= data[i].seqId;
		
		$("<table id=\"pid22767\" summary=\"pid22767\" cellspacing=\"0\" cellpadding=\"0\">"
		+"<tr>"
		+"<td class=\"pls\" rowspan=\"2\">"
		+" <div class=\"pi\">"
		+"<div class=\"authi\"><a href=\"javascript:void(0)\" target=\"_blank\" class=\"xw1\">"+data[i].uid+"</a> #"+seqId+"</div>"
		+"</div>"
		+"<div>"
		+"<div class=\"avatar\" ><a href=\"#\" ><img src=\"css/images/noavatar_middle.gif\" /></a></div>"
		+"<p><em><a href=\"javascript:void(0)\" ><font color=\"#FF0000\"><b></b></font></a></em></p>"
		+"</div>"
		+"</td>"
		+"<td class=\"plc\">"
		+"<div class=\"pi\">"
		+"<div class=\"pti\">"
		+"<div class=\"pdbt\">"
		+"</div>"
		+"<div class=\"authi\">"
		+"<img class=\"authicn vm\" id=\"authicon22767\" src=\"css/images/online_member.gif\" />"
		+"<em id=\"authorposton22767\">发表于 <span >"+data[i].createtime+"&nbsp;</span></em>"
		+"</div>"
		+"</div>"
		+"</div>"
		+"<div class=\"pct\">"
		+"<div class=\"pcb\">"
		+"<div class=\"t_fsz\">"
		+"<table cellspacing=\"0\" cellpadding=\"0\"><tr><td class=\"t_f\" id=\"tdmessage_"+seqId+"\">"
		+data[i].content
		+"</td></tr>"
		+"</table>"
		+"</div>"
		+"<div id=\"comment_22767\" class=\"cm\">"
		+"</div>"
		+"<div id=\"post_rate_div_22767\"></div>"
		+"</div></div>"
		+"</td>"
		+"</tr>"
		+"<tr>"
		+"<td class=\"plc plm\">"
		+"</td>"
		+"</tr>"
		+"<tr>"
		+"<td class=\"pls\"></td>"
		+"<td class=\"plc\">"
		+"<div class=\"po\">"
		+"<div class=\"pob cl\">"
		+"</div>"
		+"</div>"
		+"</td>"
		+"</tr>"
		+"<tr class=\"ad\">"
		+"<td class=\"pls\"></td>"
		+"<td class=\"plc\">"
		+"<div class=\"po\">"
		+"<div class=\"pob cl\">"
		+"<em>"
		//+"<a class=\"cmmnt\" href=\"\" onclick=\"showWindow('comment', this.href, 'get', 0)\">点评</a>"
		+"<a class=\"fastre\" href=\"javascript:void(0)\" onclick=\"showwindow(event,"+seqId+")\">回复</a>"
		+"<a class=\"editp\" href=\"javascript:void(0)\" onclick=\"showupdatewindow(event,"+seqId+")\">编辑</a></em>"
		+"<p>"
		//+"<a href=\"javascript:;\" id=\"mgc_post_23327\" onmouseover=\"showMenu(this.id)\" class=\"showmenu\">使用道具</a>"
		//+"<a href=\"javascript:;\" >评分</a>"
		//+"<a href=\"javascript:;\">举报</a>"
		+"</p>"
		+"<ul id=\"mgc_post_23327_menu\" class=\"p_pop mgcmn\" style=\"display: none;\">"
		+"</ul>"
		+"</div>"
		+"</div>"
		+"</td>"
		+"</tr>"
		+"</table>").appendTo("#"+thisid);
	
		
	});
  }
//自訂function 結束
})(jQuery)