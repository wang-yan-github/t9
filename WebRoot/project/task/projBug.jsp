<%@ page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header.jsp"%>
<%
	String taskId=request.getParameter("taskId");
	String projId=request.getParameter("projId");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>项目问题</title>
<link rel="stylesheet" href="<%=cssPath%>/cmp/tab.css" type="text/css" />
<link rel="stylesheet" href="<%=cssPath%>/style.css">
<link rel="stylesheet" href="<%=cssPath %>/cmp/swfupload.css" type="text/css" />
<link rel="stylesheet" href="<%=cssPath%>/cmp/Calendar.css">
<link rel="stylesheet" href="<%=contextPath%>/project/css/dialog.css">
<script type="text/Javascript" src="<%=contextPath%>/core/js/datastructs.js"></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/sys.js"></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/prototype.js"></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/smartclient.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/Calendarfy.js"></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/cmp/select.js"></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/orgselect.js" ></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/cmp/fck/fckeditor/fckeditor.js"></script> 
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/attach.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/swfupload/swfupload.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/swfupload/swfupload.queue.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/swfupload/fileprogress.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/swfupload/handlers.js"></script>
<script type="text/javascript"	src="<%=contextPath%>/core/js/cmp/Menu.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/attachMenu.js"></script>
<script type="text/javascript" src="<%=contextPath %>/project/js/jquery.min.js"></script>
<script type="text/javascript" src="<%=contextPath %>/project/js/dialog.js"></script>
<script type="text/javascript" src="<%=contextPath %>/project/js/util.js"></script>
<script type="text/javascript">


jQuery.noConflict();
var projId=<%=projId%>;
var taskId=<%=taskId%>;
var fckContentStr = "";
var upload_limit=1,limit_type=limitUploadFiles;
var oa_upload_limit=limitUploadFiles;
var swfupload;
var isUploadBackFun = false;
/**
 * 获取任务列表
 */
function doInit(){
	var date1Parameters = {
		      inputId:'deadLine',
		      property:{isHaveTime:false}
		      ,bindToBtn:'deadLineImg'
		  };
  new Calendar(date1Parameters);
  $("projId").value=projId;  //需要传递进来的数据 projId，taskId
  $("taskId").value=taskId;
  initSwfUpload();
  getBugInfoList(taskId);
}

function addProjBug(){
	$("bugList").style.display="none";
	 $("msrg").style.display="none";
	$("bugInfo").style.display="";
}

function returnBugList(){
	location.reload();
}

function showDetail(bugId)
{
	var str="<table width=\"80%\" align=\"center\" class=\"TableList\" border=\"0\">";
	var url= contextPath + "/t9/project/bug/act/T9ProjBugAct/getBugInfo.act?bugId="+bugId;
	var rtJson = getJsonRs(url);
	if(rtJson.rtState == "0"){
		var data=rtJson.rtData;
		str +="<tr><td  nowrap class=\"TableContent\">问题名称：</td><td class=\"TableData\" >"+data.bugName+"</td></tr>"
		 		 + "<tr><td nowrap class=\"TableContent\">提交人：</td><td class=\"TableData\" >"+data.beginUser+"</td></tr>"
		 		 + "<tr><td nowrap class=\"TableContent\">问题描述：</td><td class=\"TableData\" >"+data.bugDesc+"</td></tr>"
		 		 + "<tr><td nowrap class=\"TableContent\">处理期限：</td><td class=\"TableData\" >"+data.deadLine+"</td></tr>"
		 		 + "<tr><td nowrap class=\"TableContent\">附件文档：</td><td class=\"TableData\" >"
		 		 + "<div id=\"projAttach\">"
		     + "<input type=\"hidden\" id=\"attachmentId1\" name=\"attachmentId\">"
		     + "<input type=\"hidden\" id=\"attachmentName1\" name=\"attachmentName\">"
		     + "<input type=\"hidden\" id=\"moduel\" name=\"moduel\" value=\"project\">"
		     + "<span id=\"showAtt1\">无附件 </span></div></td></tr>"
		 		 + "<tr><td nowrap class=\"TableContent\">处理记录：</td><td class=\"TableData\" >"+showResult(data.result)+"</td></tr>"
	}
	$("detail_body").innerHTML=str+"</table>";
	$('attachmentId1').value=data.attachmentId ;
	$('attachmentName1').value=data.attachmentName;
	var  selfdefMenu2 = {
		    office:["downFile","dump","read"], 
		    img:["downFile","dump","play"],
		    music:["downFile","dump","play"],  
		    video:["downFile","dump","play"], 
		    others:["downFile","dump"]
		  }

if( $("attachmentId1").value){
	  attachMenuSelfUtil("showAtt1","project",$('attachmentName1').value ,$('attachmentId1').value, '','','',selfdefMenu2);  
	  }
	ShowDialog('detail')
	//jQuery.get("detail.jsp?bugId="+bugId,function(data){jQuery("#detail_body").html(data);;});
}
function showResult(result){
	var results=result.split("|*|");
	var str="";
	for(var i=0;i<results.size();i++){
		str+="<span>"+results[i]+"</span><br/>";
	}
	return str;
}

function upload_attach() {
	  $("btnFormFile").click();
	}
function initSwfUpload() {
	  var linkColor = document.linkColor;
	  var settings = {
	    flash_url : "<%=contextPath %>/core/cntrls/swfupload.swf",
	    upload_url: "<%=contextPath %>/t9/project/project/act/T9ProjectAct/fileLoad.act",
	    post_params: {"PHPSESSID" : "<%=session.getId()%>"},
	    file_size_limit : (maxUploadSize + " MB"),
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
	  
	  if(attachmentIds){
	    attachMenuUtil("showAtt","project",null,attachmentName,attachmentIds,false);
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
	      $('attachmentName').value += json.data.attachmentName;
	      var attachmentIds = $("attachmentId").value;
	      var attachmentName = $("attachmentName").value;
	      var ensize =  $('ensize').value;
	      if(ensize){
	        $('ensize').value =(json.data.size + parseInt(ensize));
	      }else {
	        $('ensize').value =json.data.size ;
	      }//附件大小
	      attachMenuUtil("showAtt","project",null,$('attachmentName').value ,$('attachmentId').value,false);
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
	  var  selfdefMenu = {
	    office:["downFile","dump","read","edit","deleteFile"], 
	    img:["downFile","dump","play","deleteFile","insertImg"],
	    music:["downFile","dump","play","deleteFile"],  
	    video:["downFile","dump","play","deleteFile"], 
	    others:["downFile","dump","deleteFile"]
	  }
	  attachMenuSelfUtil("showAtt","project",$('attachmentName').value ,$('attachmentId').value, '','','',selfdefMenu);
	  removeAllFile();
	  if (isUploadBackFun == true) {
	    sendForm(savePar);
	    isUploadBackFun = false;
	  }
	}

	//浮动菜单文件的删除 
function deleteAttachBackHand(attachName,attachId,attrchIndex){ 
var url= contextPath + "/t9/project/project/act/T9ProjectAct/delFloatFile.act?attachId=" + attachId +"&attachName=" + attachName ; 
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
    var idss = ids.split(",");
    var namess = names.split("*");
   
    var newId = getStr(idss , attachId , ",");
    var newname = getStr(namess , attachName , "*");  
   
    $('attachmentId').value = newId;
    $('attachmentName').value = newname;
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


function saveBugInfo(flag)
{
	if(CheckForm()){
		var url= contextPath + "/t9/project/bug/act/T9ProjBugAct/addBugInfo.act?flag="+flag;
		var rtJson = getJsonRs(url,mergeQueryString($("form1")));
		if(rtJson.rtState == "0"){
	    alert('问题保存成功！');
	    location.reload();
	  }else{
			alert(rtJson.rtMsrg);
	  } 
	}
}

function subResult()
{
	if(check_form()){
		var url= contextPath + "/t9/project/bug/act/T9ProjBugAct/subResult.act";
		var rtJson = getJsonRs(url,mergeQueryString($("form3")));
		if(rtJson.rtState == "0"){
	    alert('回退成功！');
	    location.reload();
	  }else{
			alert(rtJson.rtMsrg);
	  } 
	}
}

/*
 * 检查数据
 */
 function CheckForm()
 {
    if(document.form1.bugName.value.trim()==""||document.form1.bugDesc.value==""||document.form1.dealUser.value==""||document.form1.deadLine.value=="")
    { alert("请填写必填字段！");
      return (false);
    }

    return (true);
 }
 function check_form()
 {
    if(document.form3.result.value.trim()=="")
    { alert("请填写回退意见！");
      return (false);
    }

    return (true);
 }



function getBugInfoList(taskId){
	 var url= contextPath + "/t9/project/bug/act/T9ProjBugAct/getBugList.act?taskId="+taskId;
	 var rtJson = getJsonRs(url);
	 if(rtJson.rtState == "0"){
		  var data=rtJson.rtData;
		  if(data.size()>0){
			  var str="<table id=\"bugList\" class=\"TableList\" border=\"0\" width=\"100%\" align=\"center\">"
	   				+ "<tr class=\"TableHeader\">"
	    			+ "<td nowrap align=\"center\">问题名称</td>"
	      		+ "<td nowrap align=\"center\">提交人</td>"
	    			+ "<td nowrap align=\"center\">处理底线</td>"
	    			+ "<td nowrap align=\"center\">优先级</td>"
	    			+ "<td nowrap align=\"center\">状态</td>"
	    			+ "<td nowrap align=\"center\">操作</td>" 	
	   				+ "</tr>";
	   		var bugList="";
				for(var i=0;i<data.size();i++){
					bugList +="<tr class=\"TableLine2\">"
	            + "<td nowrap align=\"center\"><a href=\"javascript:showDetail("+data[i].seqId+")\">"+data[i].bugName+"</a></td>"
	            + "<td nowrap align=\"center\">"+data[i].beginUser+"</td>"
	            + "<td nowrap align=\"center\">"+data[i].deadLine.substring(0,10)+"</td>"
	            + "<td nowrap align=\"center\">"+getLevel(data[i].level)+"</td>"
	            + "<td nowrap align=\"center\">"+getStatus(data[i].status)+"</td>"
	            + "<td nowrap align=\"center\">";
	            if(data[i].status=='1'){
	            	bugList +="<span style=\"color:green;\">已提交</span>"
		          					+ "</td></tr>";
	            }else if(data[i].status=='2'){
	            	bugList +="<span><a href=\"javascript:void(0)\" onclick=\"goBack("+data[i].seqId+");\">退回</a></span>"
    					+ "</td></tr>";
 					  }else{
	            	bugList +="<span><a href=\"javascript:void(0)\" onclick=\"editBug("+data[i].seqId+");\">修改</a></span>"
   									  + "<span style=\" margin-left:20px;\"><a href=\"javascript:void(0)\" onclick=\"subBug("+data[i].seqId+");\">提交</a></span>"
   				  					+ "<span style=\" margin-left:20px;\"><a href=\"javascript:void(0)\" onclick=\"delBug("+data[i].seqId+");\">删除</a></span></td>"  	
  										+ "</tr>";
	            }
				}
	    $("bugInfoList").innerHTML=str+bugList+"</table>";
		  }else{
			  WarningMsrg('无项目问题', 'msrg');
		  }
	 }else{
			alert(rtJson.rtMsrg);
	 }  
}
function getStatus(status){
	if(status=="0"){
		return "未提交";
	}else if(status=="1"){
		return "处理中";
	}else if(status=="2"){
		return "已反馈";
	}else{
		return "已超时";
	}
}
function getLevel(level){
	if(level=="0"){
		return "低"
	}else if(level=="1"){
		return "普通"
	}else if(level=="2"){
		return "高"
	}else{
		return "非常高"
	}
}
function goBack(bugId){
	$("returnBugId").value=bugId;
	ShowDialog('back');
}
function subBug(bugId){
	var sysRemind=$("sysRemind").value;
	if(confirm("确认提交?提交以后不能再修改")){
		var url= contextPath + "/t9/project/bug/act/T9ProjBugAct/subBug.act?bugId="+bugId+"&sysRemind="+sysRemind;
		 var rtJson = getJsonRs(url);
		 if(rtJson.rtState == "0"){
			 alert("提交成功！！");
			 location.reload();
		 }else{
			 alert(rtJson.rtMsrg);
		 }
	}
}
function editBug(bugId){
	var url= contextPath + "/t9/project/bug/act/T9ProjBugAct/getBugInfo.act?bugId="+bugId;
	 var rtJson = getJsonRs(url);
	 if(rtJson.rtState == "0"){
		  $("bugList").style.display="none";
			$("bugInfo").style.display="";
		  var data=rtJson.rtData;
		  $("bugId").value=data.seqId;
		  bindJson2Cntrl(rtJson.rtData);
		  if( $("attachmentId").value){
			    attachMenuUtil("showAtt","project",null,$("attachmentName").value,$("attachmentId").value,false);
			  }
		  if(data.dealUser){
		      bindDesc([{cntrlId:"dealUser", dsDef:"PERSON,SEQ_ID,USER_NAME"}]);
		    }
		  if(data.deadLine){
		      $("deadLine").value=data.deadLine.substring(0,10);
		    }
	 }else{
		 alert(rtJson.rtMsrg);
	 }
}

function delBug(bugId){
	if(confirm("确认删除！")){
		var url= contextPath + "/t9/project/bug/act/T9ProjBugAct/delBugInfo.act?bugId="+bugId;
		 var rtJson = getJsonRs(url);
		 if(rtJson.rtState == "0"){
			 alert("删除成功");
			 location.reload();
		 }else{
			 alert(rtJson.rtMsrg);
		 }
	}
}

</script>
</head>
<body onload="doInit();">
 <div id="bugList" >
 <table border="0" width="100%" cellspacing="0" cellpadding="3" class="small">
	<tr><td>
	<img src="<%=imgPath %>/notify_new.gif" align="absmiddle"/>
	<span class="big3">添加新问题</span><td></tr>
</table>

<div align="center">
   <input type="submit" value="新建问题" class="BigButton" title="新建问题" onclick="addProjBug()">
</div>

<br>

<table width="100%" border="0" cellspacing="0" cellpadding="0" height="3">
 <tr>
   <td width="100%"></td>
 </tr>
</table>

<table border="0" width="100%" cellspacing="0" cellpadding="3" class="small">
	<tr><td>
	<img src="../images/bug2.gif" align="absmiddle"/>
	<span class="big3">项目问题列表</span>
	<td></tr>
</table>
<div id="bugInfoList" style="width:80%;text-align:center;margin:0 auto" >
</div>
 </div>
 <div id="bugInfo" style="display:none">
<table border="0" width="100%" cellspacing="0" cellpadding="3" class="small">
	<tr><td>
	<img src="../images/bug2.gif" align="absmiddle"/>
	<span class="big3">创建新问题</span><td></tr>
</table>
<form name="form1" id="form1" method="post" action="">
 <table class="TableList" border="0" width="80%" align="center">
   <tr>
  		<td nowrap class="TableContent">问题名称：<span style="color:red">(*)</span></td>
  	  <td class="TableData">
  	  	<input type="text" class="BigInput" name="bugName" id="bugName" value="" size=20>
  	  </td>  	  	
  	</tr>
  	<tr>
  		<td nowrap class="TableContent">优先级：</td>
  	  <td class="TableData">
       <select name="level" id="level" style="width: 50px;" >
   				<option value="0">低</option>
   				<option value="1" selected="selected">普通</option>
   				<option value="2">高</option>
   				<option value="3">非常高</option>
   </select>
  	  </td>  	  	
  	</tr>
  	<tr>
  		<td nowrap class="TableContent">问题描述：<span style="color:red">(*)</span></td>
  	  <td class="TableData">
  	  	<textarea cols="60" name="bugDesc" id="bugDesc" rows="5" style="overflow-y:auto;" class="BigInput" wrap="yes"></textarea>
  	  </td>  	  	
  	</tr>
  	<tr>
  		<td nowrap class="TableContent">处理人：<span style="color:red">(*)</span></td>
  	  <td class="TableData">
     			<input type="hidden" name="dealUser" id="dealUser"  class="BigStatic">
	      	<input name="dealUserDesc" id="dealUserDesc"  class="BigStatic" readonly>
	      	<a href="javascript:;" class="orgAdd" onClick="selectSingleUser(['dealUser','dealUserDesc']);">选择</a>
  	  </td>  	  	
  	</tr>
  	<tr>
  		<td nowrap class="TableContent">最后处理期限：<span style="color:red">(*)</span></td>
  	  <td class="TableData"> 
  	   <input type="text" id="deadLine" name="deadLine" class=BigInput size="10">
        <img id="deadLineImg" src="<%=imgPath%>/calendar.gif" align="absMiddle" border="0" style="cursor:pointer" >
      </td>
    </tr>
    <tr>
      <td nowrap class="TableContent">附件文档：</td>
      <td nowrap class="TableData" colspan="3">
       <input type="hidden" id="attachmentId" name="attachmentId">
        <input type="hidden" id="attachmentName" name="attachmentName">
        <input type="hidden" id="ensize" name="ensize">
        <input type="hidden" id="moduel" name="moduel" value="project">
        <span id="showAtt">
        </span>
    </td>
    </tr>
    <tr height="25">
       <td nowrap class="TableContent">附件选择：</td>
      <td class="TableData" colspan="3">
         <div id="fsUploadArea" class="flash" style="width:380px;">
			     <div id="fsUploadProgress"></div>
			     <div id="totalStatics" class="totalStatics"></div>
			     <div>
			       <input type="button" id="btnStart" class="SmallButtonW" value="开始上传" onclick="swfupload.startUpload();" disabled="disabled">&nbsp;&nbsp;
			       <input type="button" id="btnCancel" class="SmallButtonW" value="全部取消" onclick="swfupload.cancelQueue();" disabled="disabled">&nbsp;&nbsp;
			    </div>
	      </div>
	     	 <div id="attachment1">
          <script>ShowAddFile();</script>
          <script>$("ATTACHMENT_upload_div").innerHTML='<a href="javascript:upload_attach();">上传附件</a>'</script>
          <input type="hidden" name="ATTACHMENT_ID_OLD" id="ATTACHMENT_ID_OLD" value="">
          <input type="hidden" name="ATTACHMENT_NAME_OLD" id="ATTACHMENT_NAME_OLD" value="">
          <span id="spanButtonUpload" title="批量上传附件"> </span>
        </div>
      </td>
    </tr>
    <tr>
      <td nowrap class="TableContent">提醒处理人员：</td>
      <td class="TableData">
						<input type="checkbox" name="sysRemind" id="sysRemind" checked><label for="sysRemind">发送事务提醒消息</label>&nbsp;&nbsp;    </td>
    </tr>
    <tr align="center" class="TableControl">
    	<td colspan="2" nowrap>
      <input type="hidden" name="projId" id="projId" value="">
      <input type="hidden" name="bugId" id="bugId" value="">
      <input type="hidden" name="taskId" id="taskId" value="">
      <input type="hidden" name="SAVE_FLAG" value="">
    	<input type="button" value="保存" class="BigButton" onclick="saveBugInfo('0');">
    	<input type="button" value="提交" class="BigButton" onclick="saveBugInfo('1');">
	    <input type="button" value="返回" class="BigButton" onclick="returnBugList();">
	    </td>
  </tr>
 </table>
</form>
 </div>
 <form id="formFile" action="<%=contextPath %>/t9/project/project/act/T9ProjectAct/uploadFile.act" method="post" enctype="multipart/form-data" target="commintFrame">
  <input id="btnFormFile" name="btnFormFile" type="submit" style="display:none;"></input>
</form>
<iframe widht="0" height="0" name="commintFrame" id="commintFrame"></iframe>
<div id="msrg">
</div>
<div id="overlay"></div>
<div id="detail" class="ModalDialog" style="width:550px;">
  <div class="header"><span id="title" class="title">项目问题详情</span><a class="operation" href="javascript:HideDialog('detail');"><img src="../images/close.png"/></a></div>
  <div id="detail_body" class="body">
  </div>
  <div id="footer" class="footer">
    <input class="BigButton" onclick="HideDialog('detail')" type="button" value="关闭"/>
  </div>
</div>

<div id="back" class="ModalDialog" style="width:550px;">
  <div class="header"><span id="title" class="title">项目问题回退</span><a class="operation" href="javascript:HideDialog('back');"><img src="../images/close.png"/></a></div>
  <form action="" method="post" name="form3" id="form3">
  <div id="back_body" class="body">
  		<table class="TableList" border=0 align="center">
  		  <tr>
  		  	<td class="TableContent">回退意见：</td>
  		    <td class="TableData"><textarea class="BigInput" rows=5 cols=50 name="result" id="result"></textarea></td>
  		  </tr>
  		</table>
  </div>
  <div id="footer" class="footer">
    <input type="hidden" name="returnBugId" id="returnBugId"/>
    <input class="BigButton" type="button" value="确定" onclick="subResult();"/>
    <input class="BigButton" onclick="HideDialog('back')" type="button" value="关闭"/>
  </div>
  </form>
</div>
</body>
</html>