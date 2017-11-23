<%@ page language="java" contentType="text/html; charset=UTF-8"pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header.jsp" %>
<%
  String date = T9Utility.getDateTimeStr(new Date());
	String taskId=request.getParameter("taskId");
	if(taskId==null){
	  taskId="";
	}	%>

<%@page import="java.util.Date"%><html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>任务办理</title>
<link rel="stylesheet" href="<%=cssPath%>/page.css">
<link rel="stylesheet" href="<%=cssPath%>/style.css">
<link rel="stylesheet" href="<%=cssPath%>/cmp/Calendar.css">
<script type="text/javascript" src="<%=contextPath%>/core/js/prototype.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/cmp/tab.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/datastructs.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/sys.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/smartclient.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/cmp/Calendarfy.js"></script>
<script type="text/javascript" src="/t9/core/js/cmp/swfupload/handlers.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/cmp/Menu.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/cmp/page.js"></script>
<script type="text/javascript"
	src="<%=contextPath%>/core/js/cmp/attachMenu.js"></script>
<script type="text/javascript"
	src="<%=contextPath%>/core/js/cmp/attach.js"></script>
<script type="text/javascript">
var pageMgr=null;
var taskId='<%=taskId%>';
var percentValue=0;

function doInit(){
	$('taskId').value=taskId;
	getPercentFormDb();
	var url = "<%=contextPath%>/t9/project/task/act/T9TaskAct/getTaskInfo.act?taskId="+taskId;
    var rtJson = getJsonRs(url);
    if (rtJson.rtState == "0") {
        $('modelName').innerHTML="进度日志详情（"
        	+rtJson.rtData.taskName
        +"["
        +rtJson.rtData.taskStartTime.substring(0,10)
        +"至"
        +rtJson.rtData.taskEndTime.substring(0,10)
        +"]"
        ;
    }
	var date = new Date();  
	$("LOG_TIME").value= '<%=date%>';
  var url = "<%=contextPath%>/t9/project/task/act/T9ProjTaskLogAct/getTaskLogTree.act?taskId="+taskId;
  var cfgs = {
    dataAction: url,
    container: "listContainer",
    sortIndex: 1,
    sortDirect: "desc",
    colums: [
             {type:"hidden", name:"seqId", text:"顺序号", dataType:"int"},
             {type:"data", name:"logUser",  width: '15%', text:"任务负责人" ,align: 'center'},
             {type:"data", name:"logContent",  width: '30%', text:"内容" ,align: 'center'},
             {type:"hidden", name:"attachmentId",align: 'center'},
             {type:"data", name:"attachmentName",  width: '20%', text:"附件" ,align: 'center',render:getAttr},
             {type:"data", name:"logTime",  width: '15%', text:"日志时间" ,align: 'center'},
             {type:"data", name:"percent",  width: '10%', text:"进度百分比" ,align: 'center',render:getPercent},
             {type:"selfdef",width: '10%', text:"操作" ,align: 'center',render:opts}]
        };
  pageMgr = new T9JsPage(cfgs);
  pageMgr.show();
	var  selfdefMenu = {
        	office:["downFile", "dump", "read", "readNoPrint", "edit", "setSign", "deleteFile"], 
	        img:["downFile", "dump", "play"],  
	        music:["downFile", "dump", "play"],  
			    video:["downFile", "dump", "play"],
			    pdf: ["readpdf", "downFile", "dump"], 
			    others:["downFile", "dump"]
				};
  var  returnAttId = document.getElementsByName("returnAttId");
  var  returnAttName = document.getElementsByName("returnAttName");
  var  returnAttSeqId = document.getElementsByName("returnAttSeqId");
  for(var i=0; i<returnAttId.length; i++){
	  attachMenuSelfUtil("attr_"+returnAttSeqId[i].value,"proj_file",returnAttName[i].value ,returnAttId[i].value, returnAttSeqId[i].value,'',returnAttSeqId[i].value,selfdefMenu);
  }
}
function getPercentFormDb(){
	var url = "<%=contextPath%>/t9/project/task/act/T9ProjTaskLogAct/getPercent.act?taskId="+taskId;
    var rtJson = getJsonRs(url);
    if (rtJson.rtState == "0") {
        $('percent').value=rtJson.rtData[0].percent;
        percentValue=rtJson.rtData[0].percent;
        
    }
}
function getPercent(cellData, recordIndex, columInde){
	return cellData+"%";
}
function getAttr(cellData, recordIndex, columInde){
	if(cellData!=""&&cellData!="null"){
	var seqId = this.getCellData(recordIndex,"seqId");
	var attachmentId = this.getCellData(recordIndex,"attachmentId");
	return "<input type = 'hidden' id='returnAttId_" + seqId + "' value='"+attachmentId+"' name='returnAttId'></input>"
	+ "<input type = 'hidden' id='returnAttName_" + seqId + "' value='"+cellData+"' name='returnAttName'></input>"
	+ "<input type = 'hidden' id='returnAttSeqId' name='returnAttSeqId' value='"+seqId+"'></input>"
	+ "<div id='attr_" + seqId + "'></div>";
	}else{
		return "";
		}
}
/*
* 增加操作列
*/
function opts(cellData, recordIndex, columInde){
   var seqId = this.getCellData(recordIndex,"seqId");
   var edit="<a href=\"javascript:getLog("+seqId+");\" >修改</a>&nbsp&nbsp";
   var finishTask="<a href=\"javascript:deleteTaskLog("+seqId+");\" >删除</a>&nbsp&nbsp";
   return edit+finishTask;
}
function getLog(seqId){
	var url = "<%=contextPath%>/t9/project/task/act/T9ProjTaskLogAct/getLogBySeqId.act?seqId="+seqId;
    var rtJson = getJsonRs(url);
    if (rtJson.rtState == "0") {
        $('seqId').value=rtJson.rtData[0].seqId;
        $('logContent').value=rtJson.rtData[0].logContent;
        $('percent').value=rtJson.rtData[0].percent;
        $('attachmentIdOld').value=rtJson.rtData[0].attachmentId;
        $('attachmentNameOld').value=rtJson.rtData[0].attachmentName;
		var  selfdefMenu = {
		      	office:["downFile","dump","read","edit","deleteFile","rename"], 
		        img:["downFile","dump","play","deleteFile","rename","insertImg"],  
		        music:["downFile","dump","play","deleteFile"],  
				    video:["downFile","dump","play","deleteFile"], 
				    others:["downFile","dump","deleteFile","rename"]
					}

		attachMenuSelfUtil("attr1","proj_file",$('attachmentNameOld').value ,$('attachmentIdOld').value, '','',seqId,selfdefMenu);
		$('updateButton').style.display="";
		$('submitButton').style.display="none";
		$('logContent').focus();
    }else {
      alert(rtJson.rtMsrg); 
	}
}


function deleteTaskLog(taskId){
	if(!window.confirm("确认要删除该任务日志吗 ？")){
		return ;
	}
	var requestURLStr = contextPath + "/t9/project/task/act/T9ProjTaskLogAct";
	var url = requestURLStr + "/delTaskLog.act";
	var rtJson = getJsonRs(url, "taskId=" + taskId );
	if (rtJson.rtState == "0") {
		window.location.reload();
	}else {
	 alert(rtJson.rtMsrg); 
	}
	
}
function checkForm(flag){
if($('percent').value==""){
	alert("请填写完成百分比。")
	$('percent').focus();
	return false;
}if($('logContent').value==""){
	alert("请填写进度详情。")
	$('logContent').focus();
	return false;
}
if(flag==0){
   if($('percent').value>100||$('percent').value<percentValue){
	alert("进度不能小于已完成度("+percentValue+"%)且不能大于100%");
	$('percent').value=percentValue;
	return false;
	}
   }
else{
	if($('percent').value>100||$('percent').value<0){
		alert("进度不能小于0%且不能大于100%");
		$('percent').value=0;
		return false;
	 }
 	}
return true;
}

function updateLog(seqId){
	if(checkForm(1)){
		if(jugeFile()){//如果有没有上传的文件，则进行上传
	          $("formFile").submit();
	         isUploadBackFun = true;
	         isUpdate=true;
	         return;
	    }
		 $('attachmentId1').value  +=  $('attachmentIdOld').value
		 $('attachmentName1').value  += $('attachmentNameOld').value
		 var url = "<%=contextPath%>/t9/project/task/act/T9ProjTaskLogAct/updateLog.act";
		  var rtJson=getJsonRs(url, mergeQueryString($("form1")));
			if(rtJson.rtState==0){
				alert(rtJson.rtMsrg); 	
				window.location.href=window.location.href;
			}else{
				alert(rtJson.rtMsrg); 	
			}
	  }
}
//附件上传
var upload_limit=1,limit_type=limitUploadFiles;
var oa_upload_limit=limitUploadFiles;
var isUploadBackFun = false;
var isUpdate = false;

function  handleSingleUpload(rtState,  rtMsrg,  rtData)  {
    var  data  =  rtData.evalJSON();  
    $('attachmentId1').value  =  data.attrId;
    $('attachmentName1').value  =  data.attrName;
    removeAllFile();
    if  (isUploadBackFun)  {
        if(isUpdate){
    	updateLog();
            }else{
            	upload_attach();
                }
        isUploadBackFun  =  false;
    }
}
function  jugeFile(){
	  var formDom  = document.getElementById("formFile");
	  var inputDoms  = formDom.getElementsByTagName("input"); 
	  for(var i=0; i<inputDoms.length; i++){
	    var idval = inputDoms[i].id;
	    if(idval.indexOf("ATTACHMENT")!=-1){
	      return true;
	    }
	  } 
	  return false; 
	}
function upload_attach(){
	if(checkForm(0)){
		if(jugeFile()){//如果有没有上传的文件，则进行上传
	          $("formFile").submit();
	         isUploadBackFun = true;
	         return;
	    }
		if($('SMS_REMIND').checked){
			$('sendSms').value=1;
		}
		var url = "<%=contextPath%>/t9/project/task/act/T9ProjTaskLogAct/addLog.act";
		  var rtJson=getJsonRs(url, mergeQueryString($("form1")));
			if(rtJson.rtState==0){
				alert(rtJson.rtMsrg); 	
				window.location.href=window.location.href;
			}else{
				alert(rtJson.rtMsrg); 	
			}
	  }
}
function ShowAddFile(postfix, show_sel_attach) { 
	//代替attach.js 里的ShowAddFile
	if (isUndefined(postfix)) {
		postfix = ""
	}
	$('attrShow').innerHTML+='<span id="ATTACHMENT'
		+ postfix
		+ '_div"></span>';
	document
			.write('<span id="ATTACHMENT'
					+ postfix
					+ '_upload_div" style="display:none;"></span><div id="SelFileDiv'
					+ postfix
					+ '"></div><a id="linkAddAttach" class="addfile" href="javascript:;">添加附件<input class="addfile" type="file" name="ATTACHMENT'
					+ postfix
					+ '_0" id="ATTACHMENT'
					+ postfix
					+ '_0" size="1" hideFocus="true" onchange="AddFile();" /></a>');
	if (show_sel_attach != "0") {
		document
				.write('&nbsp;|&nbsp;<a href="#" onclick="sel_attach(\'SelFileDiv'
						+ postfix
						+ "','ATTACH_DIR"
						+ postfix
						+ "','ATTACH_NAME"
						+ postfix
						+ "','DISK_ID"
						+ postfix
						+ '\');" class="selfile">从文件柜和网络硬盘选择附件</a><input type="hidden" value="" id="ATTACH_NAME'
						+ postfix
						+ '" name="ATTACH_NAME'
						+ postfix
						+ '"><input type="hidden" value="" id="ATTACH_DIR'
						+ postfix
						+ '" name="ATTACH_DIR'
						+ postfix
						+ '"><input type="hidden" id="DISK_ID'
						+ postfix
						+ '" value="" name="DISK_ID' + postfix + '">')
	}
}

</script>
</head>
<body onload="doInit()">
<table border="0" width="100%" cellspacing="0" cellpadding="3" class="small">
  <tr>
    <td class="small"><img src="<%=contextPath %>/core/funcs/system/filefolder/images/edit.gif" WIDTH="22" HEIGHT="20" align="absmiddle">
    	<span class="big3" id="modelName"></span>
    </td>
  </tr>
</table>
 <div id="listContainer">
 </div>
<div id="msrg">
</div>

<table border="0" width="100%" cellspacing="0" cellpadding="3" class="small">
  <tr>
    <td class="small"><img src="<%=contextPath %>/core/funcs/system/filefolder/images/edit.gif" WIDTH="22" HEIGHT="20" align="absmiddle">
    	<span class="big3" id="modelName">添加进度日志</span>
    </td>
  </tr>
</table>
<form action=""  method="post" name="form1" id="form1" enctype="multipart/form-data" >
<table class="TableList"  width="95%" align="center" >
   <tr>
    <td  class="TableContent" width="90">当前时间：</td>
    <td class="TableData">
      <input type="text" name="LOG_TIME" id="LOG_TIME" size="19" readonly="readonly" maxlength="100" class="BigStatic" value="">
    </td>
   </tr>
   <tr>
     <td  class="TableContent"> 完成百分比：</td>
     <td class="TableData" colspan="1">
       <input type="text" id="percent" name="percent" size="2" maxlength="3" class="BigInput" value="" onkeyup="value=value.replace(/[^\d]/g,'')" ><font size="3"> %</font><input type="hidden" name="PERCENT_MAX" size="2" class="BigInput" value="12"> （注：估计完成量与总工作量的百分比）     </td>
   </tr>
   <tr>
     <td  class="TableContent"> 进度详情描述：</td>
     <td class="TableData" colspan="1">
       <textarea name="logContent" id="logContent" class="BigInput" cols="55" rows="5"></textarea>
     </td>
   </tr>
    <tr>
      <td  class="TableContent">附件文档：</td>
      <td  class="TableData" id="attrShow">
      <span id="attr1"></span>
 </td>
    </tr>
    <tr height="25">
      <td  class="TableContent">附件选择：</td>
      <td class="TableData">
        <script>ShowAddFile();</script>
        <input type="hidden" id="attachmentName1"
			name="attachmentName1"></input> <input type="hidden" id="attachmentId1"
			name="attachmentId1"></input>      
			
      </td>
    </tr>
    <tr>
      <td  class="TableContent"> 提醒：</td>
      <td class="TableData">
      <input type="hidden" name="sendSms" id="sendSms" value="0">
<input type="checkbox" name="SMS_REMIND" id="SMS_REMIND"><label for="SMS_REMIND">发送事务提醒消息</label>&nbsp;&nbsp;      </td>
    </tr>
   <tr>
    <td   class="TableControl" colspan="2" align="center">
      <input type="hidden" value="" name="seqId" id="seqId">
      <input type="hidden" value="" name="taskId" id="taskId">
      <input type="hidden" name="attachmentIdOld" id="attachmentIdOld" value="">
      <input type="hidden" name="attachmentNameOld" id="attachmentNameOld" value="">
      <input type="button" value="确定" class="BigButton"  onclick="upload_attach()" id="submitButton">&nbsp;&nbsp;
      <input type="button" value="更新" class="BigButton"  onclick="updateLog()" id="updateButton" style="display:none">&nbsp;&nbsp;
      <input type="button" value="关闭" class="BigButton" onclick="javascript:parent.close();">&nbsp;&nbsp;
    </td>
    </tr>
</table>
</form>
<form name="formFile" id="formFile"
	action="<%=contextPath%>/t9/project/task/act/T9ProjTaskLogAct/uploadFile.act"
	method="post" enctype="multipart/form-data" target="commintFrame">
</form>
<iframe  name="commintFrame" id="commintFrame" style="display:none"></iframe>
</body>	
</html>