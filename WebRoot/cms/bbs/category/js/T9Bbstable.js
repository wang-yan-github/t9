  /**
  *title:自己写的table插件
  *t9 BBS
  *author:zp
  */
 (function($) {
 
 $.fn.T9Bbstable = function(options) {
		var settings = $.extend({
			speed: 300,
			border: "black"
		}, options || {});

	//初始化變數
	var coloumUrl = settings.coloumUrl;
	var surl = settings.url;
	var basepara = settings.basepara;
	basepara = basepara || "";
	//var colums = settings.colums;	
	//var data=" { root: [ {seqId:'1',boardId:'0',docTitle:'这是测试呵呵呵',docCreatetime:'0',docCreaterid:'0',docCreatername:'shenhua',docStatues:'0',docTopstatues:'0',docSelect:'0',docLight:'0',docNice:'0',docOriginal:'0',docImgAttch:'0',docAudioAttch:'0',docOtherAttch:'0',docVedioAttch:'0',docLookCount:'0',docReplayCount:'0',docLastReplayUser:'0',docLastReplayUserid:'0',docLastReplayTime:'0'}]}"; 

	
	var htmlobj =jQuery.ajax({url:surl,async:false});
		var data = htmlobj.responseText	;

	var dataObj = eval("("+htmlobj.responseText+")");//转换为json对象 

	var baseJson = dataObj.root;
	var ids = jQuery(this)[0].id;
		jQuery.each(baseJson,function(i){
			var html1 = "11111111111111111111111";
			jQuery("<tbody>").attr("id","normalthread_"+baseJson[i].seqId).append(
		 jQuery("<tr>").append(
				jQuery("<td>").addClass("icn").append(
					jQuery("<a>").attr({target:"_blank"}).attr("title",_titleChang(baseJson[i].docStatues)).attr("href",coloumUrl.docTitle+baseJson[i].seqId)
					.append(jQuery("<img>").css("border","0").attr("src",_imgChang(baseJson[i].docStatues))
					))
					.add(
							"<td class=\"o\">" +
							"<input onclick=\"tmodclick(this)\" type=\"checkbox\" name=\"moderate[]\" value=\"8163\" />" +
							"</td>"
					 )
					.add(
							jQuery("<th>").addClass("new").append(
								jQuery("<a>").addClass("xst").bind("click",function(){}).attr("href",coloumUrl.docTitle+baseJson[i].seqId).html(dataObj.root[i].docTitle)
								.add(
								       jQuery("<a>").addClass("xi1").attr("href","").html("new")
									)
							    )
					 ).add(jQuery("<td>").addClass("by").append(
							jQuery("<cite>").append(
								jQuery("<a>").attr({c:"1",href:coloumUrl.docCreatername+dataObj.root[i].docCreaterid,uid:"22"}).html(dataObj.root[i].docCreatername)
							).add(jQuery("<em>").append(jQuery("<span>").html(dataObj.root[i].docCreatetime)))
							)
					 
					 ).add(
							jQuery("<td>").addClass("num")
										  .append(jQuery("<a>").addClass("xi2").attr("href",coloumUrl.docTitle+baseJson[i].seqId).html(dataObj.root[i].docReplayCount))
										  .append(jQuery("<em>").html(dataObj.root[i].docLookCount))
					 
					 ).add(
							jQuery("<td>").addClass("by").append(
								jQuery("<cite>").append(jQuery("<a>").attr("href","").attr("c","1").html(dataObj.root[i].docLastReplayUser))
								.add(
										jQuery("<em>").append(jQuery("<a>").attr("href","").append(jQuery("<span>").attr("title","").html(dataObj.root[i].docLastReplayTime)))
								)
							)
					 )
			    )).appendTo(jQuery("#"+ids));
					
			
		});


function  backData(d){
	alert();
	data = d;
}
function _imgChang(docStatues){
	if(docStatues == 0 ){//无新帖 未锁定
		return "./css/images/folder_common.gif";
	}else{
		if(docStatues == 1 ){ //被锁定
			return "./css/images/folder_lock.gif";
		}else{//有新帖 未锁定
			return "./css/images/folder_new.gif";
		}
	}
}

function _titleChang(docStatues){
	if(docStatues == 0 ){//无新帖 未锁定
		return "无新的回复 - 新窗口打开";
	}else{
		if(docStatues == 1 ){ //被锁定
			return "关闭主题-新窗口打开";
		}else{//有新帖 未锁定
			return "有新的回复-新窗口打开";
		}
	}
}




	
  }
//自訂function 結束
})(jQuery)


