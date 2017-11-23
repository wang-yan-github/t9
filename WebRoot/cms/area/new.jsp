<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header.jsp" %>
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
<title>新建区域</title>
<link rel="stylesheet" href = "<%=cssPath%>/style.css">
<link href="<%=cssPath%>/cmp/swfupload.css" rel="stylesheet"	type="text/css" />
<link rel="stylesheet" href="<%=cssPath%>/cmp/Calendar.css" type="text/css" />
<script type="text/javascript" src="<%=contextPath %>/core/js/prototype.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/datastructs.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/sys.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/attach.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/smartclient.js"></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/cmp/select.js" ></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/orgselect.js" ></script>
<script type="text/javascript"	src="<%=contextPath%>/core/js/cmp/Menu.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/attachMenu.js"></script>
<script type="text/javascript"	src="<%=contextPath%>/cms/swf/js/swfupload/swfupload.js"></script>
<script type="text/javascript"	src="<%=contextPath%>/cms/swf/js/swfupload/swfupload.queue.js"></script>
<script type="text/javascript"	src="<%=contextPath%>/cms/swf/js/swfupload/fileprogress.js"></script>
<script type="text/javascript"	src="<%=contextPath%>/cms/swf/js/swfupload/handlers.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/cmp/fck/fckeditor/fckeditor.js"></script> 
<script type="text/javascript"	src="<%=contextPath%>/cms/area/js/logic.js"></script>
<script type="text/javascript">
var upload_limit=1,limit_type=limitUploadFiles;
var oa_upload_limit=limitUploadFiles;
var swfupload;
var isUploadBackFun = false;
function doInit(){
 	var requestURLStr = contextPath + "/t9/cms/template/act/T9TemplateAct/selectStationName.act";
 	var rtJson = getJsonRs(requestURLStr);
 	if(rtJson.rtState == "1"){
 	  alert(rtJson.rtMsrg); 
 	  return ;
 	}
 	var prcs = rtJson.rtData;
 	var selects = document.getElementById("stationId");
 	getTemplate(rtJson.rtData[0].seqId);
 	for(var i = 0; i< prcs.length; i++){
 	  var prc = prcs[i];
 	  var option = document.createElement("option"); 
 	  option.value = prc.seqId; 
 	  option.innerHTML = prc.stationName; 
 	  selects.appendChild(option);
 	}
 	//初始化附件
 	  var linkColor = document.linkColor;
 	  var file_size_limit_seq = 1000;
 	  var image_size_limit_seq = <%=imageSize%>*1024;
 	  var audio_size_limit_seq = <%=audioSize%>*1024;
 	  var video_size_limit_seq = <%=videoSize%>*1024;
 	  var other_size_limit_seq = <%=otherSize%>*1024;
 	  var settings = {
 	    flash_url : "<%=contextPath %>/cms/ctrls/swfupload.swf",
 	    upload_url: "<%=contextPath %>/t9/cms/area/act/T9AreaAct/fileLoad.act",
 	    post_params: {"PHPSESSID" : "<%=session.getId()%>"},
 	    file_size_limit : file_size_limit_seq +  " MB",
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
 	  var selfdefMenu = {
 	      office:["downFile","read","edit"], 
 	      img:["downFile","play"],  
 	      music:["downFile","play"],  
 	      video:["downFile","play"], 
 	      others:["downFile"]
 	  }
 	  var attachmentIds = $("attachmentId").value;
 	  var attachmentNames = $("attachmentName").value;
 	  if(attachmentIds){
 	      $('attr_tr').style.display = "";
 	      attachMenuUtil("showAtt","cms",null,attachmentNames,attachmentIds,false);
 	  }
}

function doSubmit(){
	var oEditor = FCKeditorAPI.GetInstance('fileFolder');
	$("areaContent").value = oEditor.GetXHTML();
 	 if(checkForm()){
   	 $("form1").submit();
  }
}

function checkForm(){
  if($("areaName").value == ""){
    alert("区域名称不能为空！");
    $("areaName").focus();
    return (false);
  }
  if($("areaFileName").value == ""){
	    alert("区域文件名不能为空！");
	    $("areaFileName").focus();
	    return (false);
  }
  return true;
}
function upload_attach() {
	  $("btnFormFile").click();
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
  $('attachmentId').value +=  data.attachmentId;
  $('attachmentName').value +=  data.attachmentName;   
  var  selfdefMenu = {
    office:["downFile","dump","read","edit","deleteFile"], 
    img:["downFile","dump","play","deleteFile","insertImg"],
    music:["downFile","dump","play","deleteFile"],  
    video:["downFile","dump","play","deleteFile"], 
    others:["downFile","dump","deleteFile"]
  }
  attachMenuSelfUtil("showAtt","cms",$('attachmentName').value ,$('attachmentId').value, '','','',selfdefMenu);
  removeAllFile();
  if (isUploadBackFun == true) {
    sendForm(savePar);
    isUploadBackFun = false;
  }
}
function getTemplate(stationId){
	  var url = "<%=contextPath%>/t9/cms/area/act/T9AreaAct/getAreaTemplate.act?stationId="+stationId;
		
	  var rtJson = getJsonRs(url);
		var selectObj = $("templateId");
		selectObj.length=0;
		if(rtJson.rtState == "0"){
			var prcs = rtJson.rtData;
		  for(var i = 0 ; i < prcs.length ; i++){
		    var prc = prcs[i];
		    var seqId = prc.seqId;
		    var templateName = prc.templateName;
		    var myOption = document.createElement("option");
		    myOption.value = seqId;
		    myOption.text = templateName;
		    selectObj.options.add(myOption, selectObj.options ? selectObj.options.length : 0);
		  }
		}
}

function changeStation(){
	var stationId=$("stationId").value;
	getTemplate(stationId);
}
</script>
</head>
<body onload="doInit();">
<table border="0" width="100%" cellspacing="0" cellpadding="3" class="small">
  <tr>
    <td><img src="<%=imgPath %>/notify_new.gif" align="middle"><span class="big3"> 新建区域</span>&nbsp;&nbsp;
    </td>
  </tr>
</table>
<br>
<form action="<%=contextPath%>/t9/cms/area/act/T9AreaAct/addArea.act" method="post" name="form1" id="form1" enctype="multipart/form-data">
	<table class="TableBlock" width="80%" align="center">
	  <tr>
        <td nowrap class="TableData">区域名称：<font color="red">*</font></td>
        <td class="TableData">
          <input type="text" name="areaName" id="areaName" class="BigInput" size="15">
        </td>
        <td nowrap class="TableData">文件名：<font color="red">*</font></td>
        <td class="TableData">
          <input type="text" name="areaFileName" id="areaFileName" class="BigInput" size="15" value="index.jsp"><font color="red">   (需要填写后缀名)</font>
        </td>
	  </tr>
	  <tr>
        <td nowrap class="TableData">摘要：</td>
        <td class="TableData" colspan="3">
       	  <textarea id="areaAbstract" name="areaAbstract" cols="50" rows="3"></textarea>
        </td>
	  </tr>
	  <tr>
	    <td nowrap class="TableData">所属站点：<font color="red">*</font> </td>
        <td class="TableData">
          <select name="stationId" id="stationId" style="width: 130px;" onChange="changeStation()">
          </select>
        </td>
	    <td nowrap class="TableData">选择模板：<font color="red">*</font> </td>
        <td class="TableData">
        <select name="templateId" id="templateId" style="width:130px;">
        </select>
        </td>
	  </tr>
	     <tr>
    <td nowrap class="TableData" align="left">所属栏目：</td>
    <td class="TableData" colspan="3">
      <input type="hidden" name="columnId" id="columnId" value="">
      <textarea cols=40 name="columnDesc" id="columnDesc" rows=2 class="BigStatic" wrap="yes" readonly></textarea>
        <a href="javascript:;" class="orgAdd" onClick="selectColumn();">添加</a>
      	<a href="javascript:;" class="orgClear" onClick="$('columnId').value='';$('columnDesc').value='';">清空</a>
    </td>
  </tr>
	  <tr id="attr_tr">
      <td noWrap="nowrap" class="TableData">附件文档: </td>
      <td class="TableData" noWrap="nowrap" colspan="3">
        <input type="hidden" id="attachmentId" name="attachmentId">
        <input type="hidden" id="attachmentName" name="attachmentName">
        <input type="hidden" id="moduel" name="moduel" value="cms">
        <span id="showAtt">
        </span>
      </td>
    </tr>
    <tr height="25" id="attachment1">
      <td nowrap class="TableData"><span id="ATTACH_LABEL">附件上传：</span></td>
      <td class="TableData" id="fsUploadRow" colspan="3">
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
          <input type="hidden" name="ATTACHMENT_ID" id="ATTACHMENT_ID" value="">
          <input type="hidden" name="ATTACHMENT_NAME" id="ATTACHMENT_NAME" value="">
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
        <input type="hidden" name="areaContent" id="areaContent" value="">
        <input type="hidden" name="dtoClass" id="dtoClass" value="t9.cms.area.data.T9CmsArea">
        <input type="button" value="保存" onclick="doSubmit();" class="BigButton">
      </td>
    </tr>
  </table>
</form>
<form id="formFile" action="<%=contextPath %>/t9/cms/area/act/T9AreaAct/uploadFile.act" method="post" enctype="multipart/form-data" target="commintFrame">
  <input id="btnFormFile" name="btnFormFile" type="submit" style="display:none;"></input>
</form>
<iframe widht="0" height="0" name="commintFrame" id="commintFrame"></iframe>
</body>
</html>