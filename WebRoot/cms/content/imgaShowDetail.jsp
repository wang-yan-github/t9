<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ include file="/core/inc/header.jsp" %>
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>附件列表</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">

<link rel="stylesheet" href="css/jquery.ui.all.css">

<script type="text/javascript" src="<%=contextPath %>/core/js/sys.js"></script>
<script type="text/javascript" src="js/jquery-1.8.3.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.9.2.custom.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.9.2.custom.min.js"></script>
	<script src="js/jquery.ui.core.js"></script>
	<script src="js/jquery.ui.widget.js"></script>
	<script src="js/jquery.ui.mouse.js"></script>
	<script src="js/jquery.ui.draggable.js"></script>
	<script src="js/jquery.ui.droppable.js"></script>
	<script src="js/jquery.ui.resizable.js"></script>
	<script src="js/jquery.ui.sortable.js"></script>
<script type="text/javascript">
		
var rids = "";
var rnames = "";
var parentWindowObj ;
	/**
	 *
	 */
	$(function (){
		/**
		 *排序图片 实现拖拽
		 */
		 $( "#imgsContent" ).sortable({
			  revert: true,
			  //delay: 200,
			  //distance: 10,               //延迟拖拽事件(鼠标移动十像素),便于操作性
			  tolerance: 'pointer',       //通过鼠标的位置计算拖动的位置*重要属性*
			  connectWith: "ul",
			  scroll: false,
			  update:updateValue,
			  stop: function(e, ui) {
			    setTimeout(function() {
	          //$("li").remove();
	          //ui.item[0].onclick = click;
	        //  serializeSlide();
						//ui.item.removeAttr("clickdisabled");
			    }, 0);
			  },
			  start: function(e, ui) {
					//$("#trash").show();
					//ui.item.attr("clickdisabled", true);
	
			  }
		  });
		 $( "#imgsContent" ).disableSelection();
		
	});

/**
 * 更新图片顺序
 */
function updateValue(){
	rnames = "";
	rids = "";
	var size = $( "#imgsContent li" ).size();
	$( "#imgsContent li" ).each(function(index,docm){
		rids = rids +$(docm).attr("id")+",";
		rnames = rnames + $(docm).attr("name") + "*";
	});
	parentWindowObj.document.getElementById("attachmentId").value = rids;
	parentWindowObj.document.getElementById("attachmentName").value = rnames;
	window.dialogArguments.backFuJianRefresh();
}
/**
 * 打开新的窗口 查看图片
 */
function openDetailImg(obj){
	
	var src1 = $(obj).attr('src');
	openImgDialog(src1,screen.width,screen.height);
}
/**
* 打开新的窗口
*/
function openImgDialog(actionUrl, width, height) {
	  var locX = (screen.width - width) / 2;
	  var locY = (screen.height - height) / 2;
	  var attrs = null;
	  
	  attrs = "status:no;directories:no;scroll:yes;resizable:yes;location:no;";
	  attrs += "dialogWidth:" + width + "px;";
	  attrs += "dialogHeight:" + height + "px;";
	  attrs += "dialogLeft:" + locX + "px;";
	  attrs += "dialogTop:" + locY + "px;";
	  return window.showModalDialog(actionUrl, self, attrs);
	}
function doInit(){
   parentWindowObj = window.parent.dialogArguments;
  var pramIdValue = trim(parentWindowObj.document.getElementById("attachmentId").value);
  
  var pramNameValue = trim(parentWindowObj.document.getElementById("attachmentName").value);
  if(pramNameValue != "" && pramIdValue!= ""){
	  var names= new Array(); //定义数组 存放 附件的名字
	  var ids = new Array();//存放转换后的 id
	  pramNameValue = pramNameValue.substr(0,pramNameValue.length-1);
	  pramIdValue = pramIdValue.substr(0,pramIdValue.length-1);
	  names=pramNameValue.split("*"); //字符分割      
	 ids=pramIdValue.split(","); //字符分割   
	 var defImgUrl = "../image/otherfile.jpg";
	 var audioImgUrl ="../image/audiofile.jpg";
	 var vedioImgUrl = "../image/vediofile.jpg";
	 var baseACTUrl = contextPath+"/t9/core/funcs/office/ntko/act/T9NtkoAct/upload.act?attachmentName=";  
	 var content = "";
	 for (i=0;i<ids.length ;i++ ){    
		 var imgUrl = "";
	     if(isImg(names[i])){
	    	 imgUrl = baseACTUrl+names[i]+"&attachmentId="+ids[i]+"&module=cms&directView=1";
	    	 imgUrl =  encodeURI(imgUrl);
	    	 content = content +"<li id='"+ids[i]+"' name='"+names[i]+"' ><div style='width:175px;height:130px'  ><img src='"+imgUrl+"' width='175' height='130' ondblclick='return openDetailImg(this);' /></div> </li>";
		     }else if(isAudio(names[i])){
			      /**
			       *如果附件是音频 使用音频的图片
			       */
		    	 imgUrl = audioImgUrl;
		    	 content = content +"<li id='"+ids[i]+"' name='"+names[i]+"' ><div style='width:175px;height:130px'  ><img src='"+imgUrl+"' width='175' height='130' /></div> </li>";
		         
			   }else if(isVedio(names[i])){
				   /**
				    *如果附件是视频  使用视频的图片
				    */
				    imgUrl = vedioImgUrl;
			    	 content = content +"<li id='"+ids[i]+"' name='"+names[i]+"' ><div style='width:175px;height:130px'  ><img src='"+imgUrl+"' width='175' height='130' /></div> </li>";
			       
			   }else{
				   /**
				    *如果附件是其他  使用其他的图片
				    */
				    imgUrl = defImgUrl;
			    	 content = content +"<li id='"+ids[i]+"' name='"+names[i]+"' ><div style='width:175px;height:130px'  ><img src='"+imgUrl+"' width='175' height='130' /></div> </li>";
			       
				    
			   }
	  } 
     $("#imgsContent").html(content);
  }
}

/**
 * 正则 匹配 是否是 一张图片
 */
function isImg(name){///   /^.+\\.(jpg|bmp|gif)$/;  /.*\.(?:(?!(jpg|css|js|html|htm|png)).)+/
var reg = /.*\.(?:(?!(jpg|bmp|gif|jpeg|tiff|png|raw|pcx|dxf|wmf|emf|lic|eps|JPG|BMP|GIF|JPEG|TIFF|PNG|RAW|PCX|DXF|WMF|EMF|LIC|EPS)).)+/;
	if(!name.match(reg)){
		return true;
		}else{
			return false;
		}
}
/**
* 正则 匹配 是否是 一张图片
*/
function isAudio(name){///   /^.+\\.(jpg|bmp|gif)$/;  /.*\.(?:(?!(jpg|css|js|html|htm|png)).)+/
var reg = /.*\.(?:(?!(mp3|wmv|MP3|WMV)).)+/;
	if(!name.match(reg)){
		return true;
		}else{
			return false;
		}
}
/**
* 正则 匹配 是否是 一张图片
*/
function isVedio(name){///   /^.+\\.(jpg|bmp|gif)$/;  /.*\.(?:(?!(jpg|css|js|html|htm|png)).)+/
var reg = /.*\.(?:(?!(avi|mpg|AVI|MPG|mp4|MP4)).)+/;
	if(!name.match(reg)){
		return true;
		}else{
			return false;
		}
}

function colosWindow(){
	window.close();
}

window.onload = doInit;
</script>
<style type="text/css">

 .imgsContent li{
	BORDER-BOTTOM: #fff 1px solid;
	BORDER-LEFT: #fff 1px solid;
	PADDING-BOTTOM: 1px; MARGIN: 3px 6px 3px 0px;
	PADDING-LEFT: 1px;
	PADDING-RIGHT: 1px; 
	FLOAT: left;
	BORDER-TOP: #fff 1px solid; 
	BORDER-RIGHT: #fff 1px solid;
}
 .imgsContent li:hover{
   BORDER-BOTTOM: #005eac 1px solid;
   BORDER-LEFT: #005eac 1px solid;
   BACKGROUND-COLOR: #cee1ee;
   BORDER-TOP: #005eac 1px solid;
   BORDER-RIGHT: #005eac 1px solid;
   cursor: hand
}
.b1,.b2,.b3,.b4,.b1b,.b2b,.b3b,.b4b,.b{display:block;overflow:hidden;}
.b1,.b2,.b3,.b1b,.b2b,.b3b{height:1px;}
.b2,.b3,.b4,.b2b,.b3b,.b4b,.b{border-left:1px solid #999;border-right:1px solid #999;}
.b1,.b1b{margin:0 5px;background:#999;}
.b2,.b2b{margin:0 3px;border-width:2px;}
.b3,.b3b{margin:0 2px;}
.b4,.b4b{height:2px;margin:0 1px;}
.d1{background:#F7F8F9;}
.k {height:98%;}
</style>
</head>
<body>
   <div style="width:98%;margin-left:3px;margin-top:5px;">
   <div><strong class="b1"></strong><strong class="b2 d1"></strong><strong class="b3 d1"></strong><strong class="b4 d1"></strong>
    
   <div class="b d1 k"><font style="margin: 0px 10px; color: red; font-size: 26px;">附件列表</font> 
  <!-- 开始内容 -->
   <div class="imgsContent">
	<UL id="imgsContent"  style="list-style-type:none"></UL>
	</div>
	</div>
 
   <!-- 结束内容 -->
   </div>
	<strong class="b4b d1"></strong><strong class="b3b d1"></strong><strong class="b2b d1"></strong><strong class="b1b"></strong></div>
	 <br />
   <div style="clear: both;text-align: center;">
   <br>
   <br>
   <input type="button" value="关闭" onclick="colosWindow()"/>
   </div>
	
	
	

</body>
</html>