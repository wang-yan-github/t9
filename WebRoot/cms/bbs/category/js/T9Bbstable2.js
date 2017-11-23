  /**
  *title:自己写的table插件
  *t9 BBS
  *author:zp
  */
 (function($) { 
 
 $.fn.T9Bbstable2 = function(options) {
		var settings = $.extend({
			speed: 300,
			border: "black"
		}, options || {});

	//初始化變數
	var coloumUrl = settings.coloumUrl;
	var surl = settings.url;
	var turl = settings.turl;
	var basepara = settings.basepara;
	basepara = basepara || "";
	//var colums = settings.colums;	
	//var data=" { root: [ {seqId:'1',boardId:'0',docTitle:'这是测试呵呵呵',docCreatetime:'0',docCreaterid:'0',docCreatername:'shenhua',docStatues:'0',docTopstatues:'0',docSelect:'0',docLight:'0',docNice:'0',docOriginal:'0',docImgAttch:'0',docAudioAttch:'0',docOtherAttch:'0',docVedioAttch:'0',docLookCount:'0',docReplayCount:'0',docLastReplayUser:'0',docLastReplayUserid:'0',docLastReplayTime:'0'}]}"; 

	
	var htmlobj =jQuery.ajax({url:surl,async:false});
		var data = htmlobj.responseText	;

	var htmlobj1 =jQuery.ajax({url:turl,async:false});
	var data1 = htmlobj1.responseText	;
	var dataObj = eval("("+htmlobj.responseText+")");//转换为json对象 
	var dataObj1 = eval("("+htmlobj1.responseText+")");//转换为json对象 

	var baseJson = dataObj.root;
	var baseJson1 = dataObj1.root;
	var ids = jQuery(this)[0].id;
		
	jQuery.each(baseJson1,function(i){
		var seqId = baseJson1[i].seqId;
		var docTopstatues = baseJson1[i].docTopstatues;
		var title = _titleChang(baseJson1[i].docStatues);
		var docurl = coloumUrl.docTitle+baseJson1[i].seqId;
		var docCreatername = baseJson1[i].docCreatername;
		var docReplayCount = baseJson1[i].docReplayCount;
		var docLastReplayUser = baseJson1[i].docLastReplayUser;
		var docLastreplayTime = baseJson1[i].docLastreplayTime;
		var docLookCount = baseJson1[i].docLookCount;
		var imgeurl = _imgChang1(baseJson1[i].docStatues,docTopstatues);
		var statHtml = "";
		var titleHtml  = "<a href=\""+docurl+"\" onclick=\"updateLookNums("+seqId+")\" class=\"xst\" >"+dataObj1.root[i].docTitle+"</a>";
		if(baseJson1[i].docSelect == 1){
			statHtml = "&nbsp;<img title=\"精华\" align=\"absmiddle\" alt=\"attach_img\" src=\"./css/images/digest_1.gif\"/>"
		}
		if(baseJson1[i].docLight == 1){
			
			titleHtml = "<a href=\""+docurl+"\" onclick=\"updateLookNums("+seqId+")\" class=\"xst\" style=\"color:#ee1b2e;\" >"+dataObj1.root[i].docTitle+"</a>";
		}
		var docurl = coloumUrl.docTitle+baseJson1[i].seqId;
	var shtml = "<tbody id=\"normalthread_"+seqId+"\">"
				+"<tr>"
				+"<td class=\"icn\">"
				+"<a href=\"\" title=\""+title+"\" target=\"_blank\">"
				+"<img style=\"border:0\" src=\""+imgeurl+"\" />"
				+"</a>"
				+"</td>"
				+"<th class=\"new\">"
				+titleHtml
				+statHtml
				+"</th>"
				+"<td class=\"by\">"
				+"<cite>"
				+"<a href=\"javascript:void(0)\">"+docCreatername+"</a></cite>"
				+"<em><span>"+dataObj1.root[i].docCreatetime+"</span></em>"
				+"</td>"
				+"<td class=\"num\"><a href=\"javascript:void(0)\">"+docReplayCount+"</a><em>"+docLookCount+"</em></td>"
				+"<td class=\"by\">"
				+"<cite><a >"+docLastReplayUser+"</a></cite>"
				+"<em><a ><span >"+docLastreplayTime+"</span></a></em>"
				+"</td>"
				+"</tr>"
				+"</tbody>"
				 jQuery(shtml).appendTo(jQuery("#"+ids));
	});
		
	//////////////////////////////
	
	var separatorline = "<tbody id=\"separatorline\">"
						+"<tr class=\"ts\">"
						+"<td class=\"icn\">&nbsp;</td>"
						+"<th><a href=\"javascript:;\" onclick=\"refreshData()\" title=\"查看更新\" class=\"forumrefresh\">版块主题</a></th><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td>"
						+"</tr>"
						+"</tbody>";
	 jQuery(separatorline).appendTo(jQuery("#"+ids));
	////////////////////////////// 
		jQuery.each(baseJson,function(i){
			var seqId = baseJson[i].seqId;
			var title = _titleChang(baseJson[i].docStatues);
			var imgeurl = _imgChang(baseJson[i].docStatues);
			var docurl = coloumUrl.docTitle+baseJson[i].seqId;
			var docCreatername = baseJson[i].docCreatername;
			var docReplayCount = baseJson[i].docReplayCount;
			var docLookCount = baseJson[i].docLookCount;
			var docLastreplayTime = baseJson[i].docLastreplayTime;
			var docLastReplayUser = baseJson[i].docLastReplayUser;
			var statHtml = "";
			var titleHtml  = "<a href=\""+docurl+"\" onclick=\"updateLookNums("+seqId+");\" class=\"xst\" >"+dataObj.root[i].docTitle+"</a>";
			if(baseJson[i].docSelect == 1){
				statHtml = "&nbsp;<img title=\"图片附件\" align=\"absmiddle\" alt=\"attach_img\" src=\"./css/images/digest_1.gif\"/>"
			}
			if(baseJson[i].docLight == 1){
				
				titleHtml = "<a href=\""+docurl+"\" onclick=\"updateLookNums("+seqId+");\" class=\"xst\" style=\"color:#ee1b2e;\" >"+dataObj.root[i].docTitle+"</a>";
			}
		var shtml = "<tbody id=\"normalthread_"+seqId+"\">"
					+"<tr>"
					+"<td class=\"icn\">"
					+"<a href=\"\" title=\""+title+"\" target=\"_blank\">"
					+"<img style=\"border:0\" src=\""+imgeurl+"\" />"
					+"</a>"
					+"</td>"
					+"<th class=\"new\">"
					+titleHtml
					+statHtml
					+"</th>"
					+"<td class=\"by\">"
					+"<cite>"
					+"<a href=\"javascript:void(0)\">"+docCreatername+"</a></cite>"
					+"<em><span>"+dataObj.root[i].docCreatetime+"</span></em>"
					+"</td>"
					+"<td class=\"num\"><a href=\"javascript:void(0)\">"+docReplayCount+"</a><em>"+docLookCount+"</em></td>"
					+"<td class=\"by\">"
					+"<cite><a >"+docLastReplayUser+"</a></cite>"
					+"<em><a ><span >"+docLastreplayTime+"</span></a></em>"
					+"</td>"
					+"</tr>"
					+"</tbody>"
					 jQuery(shtml).appendTo(jQuery("#"+ids));
		});


function  backData(d){
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

function _imgChang1(docStatues,docTopstatues){
	if(docStatues == 0 ){//无新帖 未锁定
		if(docTopstatues == 1){
			return "./css/images/pin_1.gif";
		}
		if(docTopstatues == 2){
			return "./css/images/pin_2.gif";
		}
		if(docTopstatues == 3){
			return "./css/images/pin_3.gif";
		}
	}else{
			return "./css/images/folder_lock.gif";
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


