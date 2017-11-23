<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header.jsp" %>
<%
String stationId = request.getParameter("stationId");
String columnId = request.getParameter("columnId");
String treeId = request.getParameter("treeId");
%>
<%@ page import="t9.cms.setting.logic.T9JhSysParaLogic" %>
<%
    T9JhSysParaLogic logic=new T9JhSysParaLogic();
		String isCtrlAttachCms=logic.getAttachCtrl();
		double imageSize=0;
		double audioSize=0;
		double videoSize=0;
		double otherSize=0;
    if(isCtrlAttachCms.equals("1")){
    	double maxSize[]=logic.getSizeCtrl();
    	imageSize=maxSize[0];
    	audioSize=maxSize[1];
    	videoSize=maxSize[2];
    	otherSize=maxSize[3];
    }else{
    	imageSize=0;
    	audioSize=0;
    	videoSize=0;
    	otherSize=0;
    }

%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>新建文章</title>
<link rel="stylesheet" href = "<%=cssPath%>/style.css">
<link rel="stylesheet" href="<%=cssPath%>/cmp/Calendar.css" type="text/css" />
<link rel="stylesheet" href="<%=cssPath %>/cmp/swfupload.css" type="text/css" />
<script type="text/javascript" src="<%=contextPath %>/core/js/prototype.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/datastructs.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/sys.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/smartclient.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/Calendarfy.js"></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/cmp/select.js" ></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/orgselect.js" ></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/cmp/fck/fckeditor/fckeditor.js"></script> 
<script type="text/javascript" src="<%=contextPath %>/cms/js/attach/attach.js"></script>
<script type="text/javascript" src="<%=contextPath %>/cms/swf/js/swfupload/swfupload.js"></script>
<script type="text/javascript" src="<%=contextPath %>/cms/swf/js/swfupload/swfupload.queue.js"></script>
<script type="text/javascript" src="<%=contextPath %>/cms/swf/js/swfupload/fileprogress.js"></script>
<script type="text/javascript" src="<%=contextPath %>/cms/swf/js/swfupload/handlers.js"></script>
<script type="text/javascript"	src="<%=contextPath%>/core/js/cmp/Menu.js"></script>
<script type="text/javascript" src="<%=contextPath %>/cms/js/attach/attachMenu.js"></script>
<script type="text/javascript"	src="<%=contextPath%>/cms/station/js/contentLogic.js"></script>
<script type="text/javascript" src="js/jquery-1.8.3.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.9.2.custom.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.9.2.custom.min.js"></script>
<script type="text/javascript">
<!----------------------------------------------------------------------------------------------------------------->
jQuery.noConflict();


/**
*查看附件后 刷新附件列表
*子窗口调用
*/
function backFuJianRefresh(){
	var attachmentIds = $("attachmentId").value;
	  var attachmentName = $("attachmentName").value;
	  if(attachmentIds){
	    attachMenuUtil("showAtt","cms",null,attachmentName,attachmentIds,false);
	  }
}

/**
* 窗口函数 用于弹出图片窗口
*  定制一个窗口
*/
function openImgDialog(actionUrl, width, height) {
	  var locX = (screen.width - width) / 2;
	  var locY = (screen.height - height) / 2;
	  var attrs = null;
	  
	  attrs = "status:no;directories:no;scroll:yes;resizable:yes;";
	  attrs += "dialogWidth:" + width + "px;";
	  attrs += "dialogHeight:" + height + "px;";
	  attrs += "dialogLeft:" + locX + "px;";
	  attrs += "dialogTop:" + locY + "px;";
	  return window.showModalDialog(actionUrl, self, attrs);
	}
function openImgDialog1(url,width,height){
	var locX=(screen.width-width)/2;
	var locY=(screen.height-height)/2;
	window.open(url, "meeting", 
	"height=" +height + ",width=" + width +",status=1,toolbar=no,menubar=no,location=no,scrollbars=yes, top=" 
	+ locY + ", left=" + locX + ", resizable=yes");
	}
function opeanImages(){
	 URL = "/t9/cms/content/imgaShowDetail.jsp?ids=" + $('attachmentId').value + "&names=" + $('attachmentName').value;
	 openImgDialog(URL,'800', '600');
}

<!----------------------------------------------------------------------------------------------------------------->


var fckContentStr = "";
var upload_limit=1,limit_type=limitUploadFiles;
var oa_upload_limit=limitUploadFiles;
var swfupload;
var isUploadBackFun = false;

var stationId = '<%=stationId %>';
var columnId = '<%=columnId %>';
function doInit(){
  if(stationId == 'null' && parentId == 'null'){
    $('showInfo2').style.display = "";
  }
  else{
    $('showInfo').style.display = "";
    var date1Parameters = {
        inputId:'contentDate',
        property:{isHaveTime:true}
        ,bindToBtn:'date1'
     };
     new Calendar(date1Parameters);
     
     var now = new Date();
     var year = now.getFullYear();       //年
     var month = now.getMonth() + 1;     //月
     var day = now.getDate();            //日
    
     var clock = year + "-";
     if(month < 10){
         clock += "0";
     }
     clock += month + "-";
     if(day < 10){
         clock += "0";
     }
     clock += day + " ";
     clock += now.toLocaleTimeString();
     $('contentDate').value = clock;
  }
	
  var url = "<%=contextPath%>/t9/cms/column/act/T9ColumnAct/getInfomation.act?stationId="+stationId+"&parentId="+columnId;
	var rtJson = getJsonRs(url);
	if(rtJson.rtState == "0"){
		var prcs = rtJson.rtData;
	  $('columnName').value = prcs.columnName;
	  $('stationId').value = prcs.stationId;
	}
	initSwfUpload();
}

function doSubmit(){
	var oEditor = FCKeditorAPI.GetInstance('fileFolder');
	$("content").value = oEditor.GetXHTML();
  if(checkForm()){
    var url = "<%=contextPath%>/t9/cms/content/act/T9ContentAct/addContent.act";
  	var rtJson = getJsonRs(url,mergeQueryString($("form1")));
  	if(rtJson.rtState == "0"){
  	  alert("文章新建成功！");
  	  location.href = contextPath + "/cms/content/manage.jsp?stationId="+$('stationId').value+"&columnId="+$('columnId').value;
  	}
  }
}

function checkForm(){
  if($("contentName").value == ""){
    alert("文章标题不能为空！");
    $("contentName").focus();
    return (false);
  }
  return true;
}

function clearnOnBor(){
  window.onbeforeunload = "";
}

function upload_attach() {
  $("btnFormFile").click();
}
function initSwfUpload() {
  var linkColor = document.linkColor;
  var image_size_limit_seq = <%=imageSize%>*1024;
  var audio_size_limit_seq = <%=audioSize%>*1024;
  var video_size_limit_seq = <%=videoSize%>*1024;
  var other_size_limit_seq = <%=otherSize%>*1024;
  var settings = {
    flash_url : "<%=contextPath %>/cms/ctrls/swfupload.swf",
    upload_url: "<%=contextPath %>/t9/cms/content/act/T9ContentAct/fileLoad.act?stationId="+stationId,
    post_params: {"PHPSESSID" : "<%=session.getId()%>"},
    file_size_limit : (maxUploadSize + " MB"),
    image_size_limit : image_size_limit_seq + " KB",
    audio_size_limit : audio_size_limit_seq + " KB",
    video_size_limit : video_size_limit_seq + " KB",
    other_size_limit : other_size_limit_seq + " KB",
    file_types : "*.*",
    file_types_description : "All Files",
    file_upload_limit : 100,
    file_queue_limit : 0,
    custom_settings : {
      uploadRow : "fsUploadRow",
      uploadArea : "fsUploadArea",
      progressTarget : "fsUploadProgress",
      startButtonId : "btnStart",
      cancelButtonId : "btnCancel"
    },
    debug: false,
    button_image_url: "<%=imgPath %>/uploadx4.gif",
    button_width: "65",
    button_height: "29",
    button_placeholder_id: "spanButtonPlaceHolder",
    button_text: '<span class=\"textUpload\">批量上传</span>',
    button_text_style: ".textUpload{color:" + linkColor + ";}",
    button_text_top_padding : 1,
    button_text_left_padding : 18,
    button_placeholder_id : "spanButtonUpload",
    button_width: 70,
    button_height: 18,
    button_window_mode: SWFUpload.WINDOW_MODE.TRANSPARENT,
    button_cursor: SWFUpload.CURSOR.HAND,
    
    file_queued_handler : fileQueued,
    file_queue_error_handler : fileQueueError,
    file_dialog_complete_handler : fileDialogComplete,
    upload_start_handler : uploadStart,
    upload_progress_handler : uploadProgress,
    upload_error_handler : uploadError,
    upload_success_handler : uploadSuccessOver,
    upload_complete_handler : uploadComplete,
    queue_complete_handler : queueComplete
  };

  swfupload = new SWFUpload(settings);
  var attachmentIds = $("attachmentId").value;
  var attachmentName = $("attachmentName").value;
  var attachUrls = $("attachUrl").value;
  
  if(attachmentIds){
    attachMenuUtil("showAtt","cms",null,attachmentName,attachmentIds,attachUrls,false);
  }
}
function InsertImage(src){ 
  var oEditor = FCKeditorAPI.GetInstance('fileFolder') ; //FCK实例 
  if ( oEditor.EditMode == FCK_EDITMODE_WYSIWYG ) {     
     oEditor.InsertHtml( "<img src='"+ src + "'/>") ; 
  } 
}
function uploadSuccessOver(file, serverData){
  try {
    var progress = new FileProgress(file, this.customSettings.progressTarget);
    progress.toggleCancel(false);
    var json = null;
    json = serverData.evalJSON();
    if (json.state == "1") {
      progress.setError();
      progress.setStatus("上传失败：" + serverData.substr(5));
      var stats=this.getStats();
      stats.successful_uploads--;
      stats.upload_errors++;
      this.setStats(stats);
    }else {
      $('attachmentId').value += json.data.attachmentId;
      $('attachUrl').value += json.data.attachUrl;
      $('attachmentName').value += json.data.attachmentName;
      var attachmentIds = $("attachmentId").value;
      var attachmentName = $("attachmentName").value;
      var ensize =  $('ensize').value;
      if(ensize){
        $('ensize').value =(json.data.size + parseInt(ensize));
      }else {
        $('ensize').value =json.data.size ;
      }//附件大小
      attachMenuUtil("showAtt","cms",null,$('attachmentName').value ,$('attachmentId').value,$('attachUrl').value,false);
    }
  }catch(ex) {
    this.debug(ex);
  }
}

/**
 * 处理文件上传
 */
function handleSingleUpload(rtState, rtMsrg, rtData) {
  if (rtState != 0) {
    alert(rtMsrg);
    return;
  }
  var data = rtData.evalJSON(); 
  $('attachmentId').value +=  data.attrId;
  $('attachmentName').value +=  data.attrName; 
  $('attachUrl').value+= data.attachUrl;
  var  selfdefMenu = {
    office:["downFile","dump","read","edit","deleteFile"], 
    img:["downFile","dump","play","deleteFile","insertImg"],
    music:["downFile","dump","play","deleteFile"],  
    video:["downFile","dump","play","deleteFile"], 
    others:["downFile","dump","deleteFile"]
  }
  attachMenuSelfUtil("showAtt","cms",$('attachmentName').value ,$('attachmentId').value ,$('attachUrl').value , '','','',selfdefMenu);
  removeAllFile();
  if (isUploadBackFun == true) {
    sendForm(savePar);
    isUploadBackFun = false;
  }
}

//浮动菜单文件的删除 
function deleteAttachBackHand(attachName,attachId,attachUrl){ 
  var url= contextPath + "/t9/cms/content/act/T9ContentAct/delFloatFile.act?attachId=" + attachId +"&attachName=" + attachName+"&stationId="+stationId ; 
  var json=getJsonRs(encodeURI(url)); 
  if(json.rtState =='1'){ 
    alert(json.rtMsrg); 
    return false; 
  }else { 
    prcsJson=json.rtData; 
    var updateFlag=prcsJson.updateFlag; 
    if(updateFlag){ 
      var ids = $('attachmentId').value ;
      if (!ids) {
        ids = ""; 
      }
      var names =$('attachmentName').value;
      if (!names) {
        names = ""; 
      }
      var urls =$('attachUrl').value;
      if (!urls) {
    	  urls = ""; 
      }
      var idss = ids.split(",");
      var namess = names.split("*");
      var urlss = urls.split(",");
     
      var newId = getStr(idss , attachId , ",");
      var newname = getStr(namess , attachName , "*");  
      var newurl = getStr(urlss , attachUrl , ",");  
      $('attachmentId').value = newId;
      $('attachmentName').value = newname;
      $('attachUrl').value = newurl;
      return true; 
   }else{ 
     return false; 
   }  
  } 
}

function getStr(ids , id , split) {
  var str = "";
  for (var i = 0 ; i< ids.length ;i ++){
    var tmp = ids[i];
    if (tmp) {
      if (tmp != id) {
        str += tmp + split;
      }
    }
  }
  return str;
}

//隐藏/显示左边栏目树框架
function shleft(){
	 if (parent.frame1.cols.indexOf("200,*")!=-1){
		    parent.frame1.cols="0,*"
	      //document.getElementById("shLeft").value="显示";
		    document.getElementById("showOrHide").innerHTML="<img src=\"<%=contextPath %>/cms/image/show.png\" align=\"middle\" >";
	 }
	 else{
		 parent.frame1.cols="200,*"
	   //document.getElementById("shLeft").value="隐藏";
		 document.getElementById("showOrHide").innerHTML="<img src=\"<%=contextPath %>/cms/image/hide.png\" align=\"middle\" >";
	 }
}

function checkLocation()
{
    object="floatButton";
    yy=eval(y)+250;
    eval(dS+object+sD+v+yy);
    setTimeout("checkLocation()",10);
}
function setVariables()
{
    if (navigator.appName == "Netscape")
    {
        v=".top=";
        dS="document.";
        sD="";
        y="window.pageYOffset";
    }
    else 
    {
        v=".pixelTop=";
        dS="";
        sD=".style";
        y="document.documentElement.scrollTop";
    }
}
</script>
</head>
<body onload="doInit();setVariables();checkLocation();">
<div id="showInfo" name="showInfo" style="display:none;">
<table border="0" width="100%" cellspacing="0" cellpadding="3" class="small">
  <tr>
    <td><img src="<%=imgPath %>/notify_new.gif" align="middle"><span class="big3"> 新建文章</span>&nbsp;&nbsp;
    </td>
  </tr>
</table>
<br>
<form action="" method="post" name="form1" id="form1">
  <input type="hidden" name="stationId" id="stationId" value="<%=stationId %>">
  <input type="hidden" name="columnId" id="columnId" value="<%=columnId %>">
	<table class="TableBlock" width="80%" align="center">
	  <tr>
      <td nowrap class="TableData">文章标题：<font color="red">*</font> </td>
      <td class="TableData" colspan="3">
        <input type="text" name="contentName" id="contentName" class="BigInput" size="65">
      </td>
    </tr>
    <tr>
      <td nowrap class="TableData">文章副标题：</td>
      <td class="TableData" colspan="3">
        <input type="text" name="contentTitle" id="contentTitle" class="BigInput" size="65">
      </td>
	  </tr>
    <tr>
      <td nowrap class="TableData">摘要：</td>
      <td class="TableData" colspan="3">
      	<textarea id="contentAbstract" name="contentAbstract" cols="50" rows="3"></textarea>
      </td>
    </tr> 
    <tr>
    	<td nowrap class="TableData">关键字： </td>
      <td class="TableData">
      	<input type="text" name="keyword" id="keyword" class="BigInput" size="15" >
      </td>
 	    <td nowrap class="TableData">来源： </td>
      <td class="TableData">
      	<input type="text" name="contentSource" id="contentSource" class="BigInput" size="15" >
      </td>
    </tr>
    <tr>
    	<td nowrap class="TableData">文件名： </td>
      <td class="TableData">
      	<input type="text" name="contentFileName" id="contentFileName" class="BigInput" size="15" >
      </td>
 	    <td nowrap class="TableData">作者： </td>
      <td class="TableData">
      	<input type="text" name="contentAuthor" id="contentAuthor" class="BigInput" size="15" >
      </td>
    </tr>
 	  <tr>
      <td nowrap class="TableData">发布日期： </td>
      <td class="TableData">
 	      <input type="text" name="contentDate" id="contentDate" size="20" maxlength="20"  class="BigInput" value="" readonly>
	      <img id="date1" align="middle" src="<%=imgPath %>/calendar.gif" align="middle" border="0" style="cursor:pointer" >
      </td>
      <td nowrap class="TableData">所属栏目：</td>
      <td class="TableData">
        <input type="text" name="columnName" id="columnName" class="BigInput" size="15" disabled>
      </td>
	  </tr>
    <tr id="attr_tr">
      <td nowrap class="TableData"><a href="javascript:void(0)"   onclick="opeanImages()" >附件列表:</a></td>
      <td class="TableData" colspan="3" id="showAttachment">
        <input type="hidden" id="attachmentId" name="attachmentId">
        <input type="hidden" id="attachmentName" name="attachmentName">
        <input type="hidden" id="attachUrl" name="attachUrl">
        <input type="hidden" id="ensize" name="ensize">
        <input type="hidden" id="moduel" name="moduel" value="cms">
        <span id="showAtt">
        </span>
      </td>
    </tr>
    <tr id="fileShowId">
      <td nowrap class="TableData">附件选择：</td>
      <td class="TableData" colspan="3" id="fsUploadRow">
         <div id="fsUploadArea" class="flash" style="width:380px;">
			     <div id="fsUploadProgress"></div>
			     <div id="totalStatics" class="totalStatics"></div>
			     <div>
			       <input type="button" id="btnStart" class="SmallButtonW" value="开始上传" onclick="swfupload.startUpload();" disabled="disabled">&nbsp;&nbsp;
			       <input type="button" id="btnCancel" class="SmallButtonW" value="全部取消" onclick="swfupload.cancelQueue();" disabled="disabled">&nbsp;&nbsp;
			    </div>
	      </div>
	      <div id="attachment1">
          <script>ShowAddFile();ShowAddImage();</script>
          <script>$("ATTACHMENT_upload_div").innerHTML='<a href="javascript:upload_attach();">上传附件</a>'</script>
          <input type="hidden" name="ATTACHMENT_ID_OLD" id="ATTACHMENT_ID_OLD" value="">
          <input type="hidden" name="ATTACHMENT_NAME_OLD" id="ATTACHMENT_NAME_OLD" value="">
          <input type="hidden" name="ATTACH_URL_OLD" id="ATTACH_URL_OLD" value="">
          <span id="spanButtonUpload" title="批量上传附件"> </span>
        </div>
      </td>
    </tr>
    <tr id="EDITOR">
      <td class="TableData" colspan="4"> 文章内容：
        <div>
         <script language=JavaScript>    
          var sBasePath = contextPath+'/core/js/cmp/fck/fckeditor/';
          var oFCKeditor = new FCKeditor( 'fileFolder' ) ;
          oFCKeditor.BasePath = sBasePath ;
          oFCKeditor.Height = 300;
          var sSkinPath = sBasePath + 'editor/skins/office2003/';
          oFCKeditor.Config['SkinPath'] = sSkinPath ;
          oFCKeditor.Config['PreloadImages'] =
                          sSkinPath + 'images/toolbar.start.gif' + ';' +
                          sSkinPath + 'images/toolbar.end.gif' + ';' +
                          sSkinPath + 'images/toolbar.buttonbg.gif' + ';' +
                          sSkinPath + 'images/toolbar.buttonarrow.gif' ;
          //oFCKeditor.Config['FullPage'] = true ;
          oFCKeditor.ToolbarSet = "fileFolder";
          oFCKeditor.Value = '' ;
          oFCKeditor.Create();
         </script>
        </div>
      </td>
    </tr>
    <tr align="center" class="TableControl">
      <td colspan=4 nowrap>
      	<input type="hidden" name="content" id="content" value="">
        <input type="hidden" name="dtoClass" id="dtoClass" value="t9.cms.content.data.T9CmsContent">
        <input type="button" value="保存" onclick="doSubmit();" class="BigButton">
      </td>
    </tr>
  </table>
</form>
<form id="formFile" action="<%=contextPath %>/t9/cms/content/act/T9ContentAct/uploadFile.act?stationId=<%=stationId%>" method="post" enctype="multipart/form-data" target="commintFrame">
  <input id="btnFormFile" name="btnFormFile" type="submit" style="display:none;"></input>
</form>
<iframe widht="0" height="0" name="commintFrame" id="commintFrame"></iframe>
</div>
<div id="showInfo2" name="showInfo2" style="display:none;">
</div>
<div id="floatButton" style="position: absolute; visibility: visible; left: 0px; top: 0px;z-index: 1000; background-color: White;cursor:pointer;" onClik="shleft();">
<span id="showOrHide" onClick="shleft()"><img src="<%=contextPath %>/cms/image/hide.png" align="middle"></span>
</div>
</body>
</html>