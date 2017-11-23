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
		String style=request.getParameter("style");
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>论坛发帖</title>
<link rel="stylesheet" href="/t9/cms/bbs/portal/css/style.css">
<link rel="stylesheet" href = "<%=cssPath%>/style.css">
<link href="<%=cssPath%>/cmp/swfupload.css" rel="stylesheet"	type="text/css" />
<script type="text/javascript" src="<%=contextPath %>/core/js/prototype.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/datastructs.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/sys.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/smartclient.js"></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/cmp/select.js" ></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/Calendarfy.js"></script>
<script type="text/javascript"	src="<%=contextPath%>/core/js/cmp/Menu.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/attach.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/attachMenu.js"></script>
<script type="text/javascript"	src="<%=contextPath%>/cms/swf/js/swfupload/swfupload.js"></script>
<script type="text/javascript"	src="<%=contextPath%>/cms/swf/js/swfupload/swfupload.queue.js"></script>
<script type="text/javascript"	src="<%=contextPath%>/cms/swf/js/swfupload/fileprogress.js"></script>
<script type="text/javascript"	src="<%=contextPath%>/cms/swf/js/swfupload/handlers.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/cmp/fck/fckeditor/fckeditor.js"></script> 
<script type="text/javascript"	src="<%=contextPath%>/cms/bbs/portal/js/logic.js"></script>
<script type="text/javascript">

var upload_limit=1,limit_type=limitUploadFiles;
var oa_upload_limit=limitUploadFiles;
var swfupload;
var isUploadBackFun = false;
var style=<%=style %>;
function doInit(){
	/*
		初始化专区及板块
	 
	*/
	var requestURLStr = contextPath + "/t9/cms/bbs/board/act/T9BbsBoardAct/getBbsArea.act";
 	var rtJson = getJsonRs(requestURLStr);
 	if(rtJson.rtState == "1"){
 	  alert(rtJson.rtMsrg); 
 	  return ;
 	}
 	var prcs = rtJson.rtData;
 	var selects = document.getElementById("areaId");
 	getBorad(rtJson.rtData[0].seqId);
 	for(var i = 0; i< prcs.length; i++){
 	  var prc = prcs[i];
 	  var option = document.createElement("option"); 
 	  option.value = prc.seqId; 
 	  option.innerHTML = prc.areaName; 
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
	    upload_url: "<%=contextPath %>/t9/cms/bbs/comment/act/T9BbsCommentAct/fileLoad.act",
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

/**
 * 提交信息
 */
function doSubmit1(){
	var oEditor = FCKeditorAPI.GetInstance('fileFolder');
	$("commentContent").value = oEditor.GetXHTML();
	 	if(check()){
	 	  //alert($("form1").submit)
		 $("form1").submit();
	}
	else{
		alert("标题不能为空");
		$("title").focus;
	} 
 
}
// 获取标题字数
function getTitleNums(){
	var title=$("title").value;
	var nums=title.length;
	if(nums<=80){
	$("nums").innerHTML=79-nums;
	}
	else{
		alert("标题字数超过最大限制");
	}
}
function check(){
	if($("title").value==""||$("title").value==null){
		return false;
	}else{
		return true;
	}
}

function getBorad(areaId){
	  var url = "<%=contextPath%>/t9/cms/bbs/board/act/T9BbsBoardAct/getBbsBoard.act?areaId="+areaId;
		
	  var rtJson = getJsonRs(url);
		var selectObj = $("boardId");
		selectObj.length=0;
		if(rtJson.rtState == "0"){
			var prcs = rtJson.rtData;
		  for(var i = 0 ; i < prcs.length ; i++){
		    var prc = prcs[i];
		    var seqId = prc.seqId;
		    var boardName = prc.boardName;
		    var myOption = document.createElement("option");
		    myOption.value = seqId;
		    myOption.text = boardName;
		    selectObj.options.add(myOption, selectObj.options ? selectObj.options.length : 0);
		  }
		}
}

function changeArea(){
	var areaId=$("areaId").value;
	getBorad(areaId);
}
</script>
<style type="text/css">
.submit1{
	background-color:#06c;
	border:1px solid #06c;
	text-align:center;
}
</style>
</head>
<body onload="doInit();">
	<form enctype="multipart/form-data" action="<%=contextPath%>/t9/cms/bbs/comment/act/T9BbsCommentAct/addComment.act?style="+<%=style %> method="post" name="form1" id="form1" onsubmit="">
		<div style="height:60px;width:75%;margin-bottom:30px;margin:0 auto;">
			<div>
					<img src="images/tongda.png"/>
			</div>
			<div id="nv">
				<ul><li class="a" id="mn_forum" ><a href="index.jsp" hidefocus="true" title="BBS"  >论坛</a></li></ul>
			</div>
		</div>
		<div style="width:75%;height:auto;margin:0 auto;margin-top:60px" >
			<table class="TableBlock" width="100%" align="center">
				<tr>
					<td class="TableData" noWrap="nowrap" colspan="3">主题：</td>
					<td class="TableData" noWrap="nowrap" colspan="3">
							<div id="titleDiv" width="100%" margin-bottom="60px" >
								<input type="text" id="title" name="title" class="BigInput" size="30" onkeydown="getTitleNums();">
								<span id="count">您还可以输入：<span id="nums">80</span></span>
							</div>
					</td>
				</tr>
				<tr>
					<td  class="TableData" noWrap="nowrap" colspan="3">专区：</td>
					<td  class="TableData" noWrap="nowrap" colspan="3">
						<select id="areaId" name="areaId" onChange="changeArea();" style="width:130px;">
						</select>
					</td>
				</tr>
				<tr>
					<td  class="TableData" noWrap="nowrap" colspan="3">板块：</td>
					<td  class="TableData" noWrap="nowrap" colspan="3">
					<select id='boardId' name="boardId" style="width:130px;">
						</select>
					</td>
				</tr>
				<tr id="attr_tr">
      <td noWrap="nowrap" class="TableData" colspan="3">附件文档: </td>
      <td class="TableData" noWrap="nowrap" colspan="3">
        <input type="hidden" id="attachmentId" name="attachmentId">
        <input type="hidden" id="attachmentName" name="attachmentName">
        <input type="hidden" id="moduel" name="moduel" value="cms">
        <span id="showAtt">
        </span>
      </td>
    </tr>
    <tr height="25" id="attachment1">
      <td nowrap class="TableData" colspan="3"><span id="ATTACH_LABEL">附件上传：</span></td>
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
          <input type="hidden" name="ATTACHMENT_ID" id="ATTACHMENT_ID" value="">
          <input type="hidden" name="ATTACHMENT_NAME" id="ATTACHMENT_NAME" value="">
          <span id="spanButtonUpload" title="批量上传附件"> </span>
        </div>
      </td>
    	</tr>
			</table>
			<div id="content" style="margin:0 auto">
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
          oFCKeditor.Config['FullPage'] = true ;
          oFCKeditor.ToolbarSet = "fileFolder";
          oFCKeditor.Value = '' ;
          oFCKeditor.Create();
         </script>
        </div>
			</div>
			<div id="submitInfo">
				<input type="hidden" name="commentContent" id="commentContent" value="">
				<input type="button" name="submit1" id="submit1" class="submit1" onClick="doSubmit1();" value="发表帖子"> 
			</div>
		</div>
</form>
<div id="footer" style="width:100%;height:100px;">
	<div id="ft" class="wp cl" style="width:1020px;">
		<div id="flk" class="y">
		<strong><a href="<%=orgSecondSite %>" target="_blank" ><%=shortProductName %></a></strong>论坛
		( <a href="<%=orgFirstSite %>" target="_blank">京ICP备05006333号</a> )</p>
		<p class="xs0">
		<!-- foot 信息  --><%=fullOrgName %>
		</p>
		</div>
		<div id="frt">
		<p>Powered by <strong><a href="<%=orgFirstSite %>" target="_blank"><%=orgName %></a></strong> <em>BBS</em></p>
		<p class="xs0">&copy; 2012-2013 <a href="<%=orgFirstSite %>" target="_blank">Comsenz Inc.</a></p>
		</div>
	</div>
</div>
</body>
</html>