<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header.jsp" %> 
<%
	String seqId=request.getParameter("projId");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>项目基本信息</title>
<link rel="stylesheet" href = "<%=cssPath%>/style.css">
<link rel="stylesheet" href="<%=cssPath%>/cmp/Calendar.css">
<link rel="stylesheet" href="<%=cssPath %>/cmp/swfupload.css" type="text/css" />
<link rel="stylesheet" href = "<%=cssPath%>/cmp/ExchangeSelect.css">
<link rel="stylesheet" href="<%=contextPath%>/project/css/dialog.css">
<script type="text/javascript" src="<%=contextPath %>/core/js/prototype.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/datastructs.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/sys.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/smartclient.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/Calendarfy.js"></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/cmp/select.js" ></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/orgselect.js" ></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/cmp/fck/fckeditor/fckeditor.js"></script> 
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/attach.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/swfupload/swfupload.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/swfupload/swfupload.queue.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/swfupload/fileprogress.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/swfupload/handlers.js"></script>
<script type="text/javascript"	src="<%=contextPath%>/core/js/cmp/Menu.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/attachMenu.js"></script>
<script type="text/javascript" src="<%=contextPath %>/project/proj/basicInfo/js/logic.js"></script>
<script type="text/javascript" src="<%=contextPath %>/project/js/jquery.min.js"></script>
<script type="text/javascript" src="<%=contextPath %>/project/js/dialog.js"></script>
<script type="text/javascript">
jQuery.noConflict();
var fckContentStr = "";
var upload_limit=1,limit_type=limitUploadFiles;
var oa_upload_limit=limitUploadFiles;
var swfupload;
var isUploadBackFun = false;
var seqId=<%=seqId%>;

function isApprove(){
	var requestURLStr = contextPath + "/t9/project/project/act/T9ProjectAct";
	var url = requestURLStr + "/isApprove.act";
	var rtJson = getJsonRs(url);
	if (rtJson.rtState == "0") {
		if(rtJson.rtData.flag=="0"){
			//$("approveSubmit").style.display="";
			//$("directSubmit").style.display="none";
			return false;
		}else{
			//$("approveSubmit").style.display="none";
			//$("directSubmit").style.display="";
			//$("projManager").style.display="none";
			$("manager").innerHTML="(免审核)";
			return true;
		}
	}else {
	 alert(rtJson.rtMsrg); 
	 return false;
	}
}


function doInit(){
	$("approveSubmit").style.display="none";
	$("directSubmit").style.display="none";
	$("saveTemplate").style.display="none";
	var date1Parameters = {
		      inputId:'beginDate',
		      property:{isHaveTime:false}
		      ,bindToBtn:'beginImg'
		  };
    new Calendar(date1Parameters);
    var date2Parameters = {
		       inputId:'endDate',
		       property:{isHaveTime:false}
		      ,bindToBtn:'endImg'
		  };
    new Calendar(date2Parameters);
    getProjectStyle();
    if(!isApprove()){
    	getProjectApprove();
    }
    initSwfUpload();
    if(seqId!=null && seqId!="" && seqId !="0"){
    	getBasicInfo(seqId);
    }
    
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
/*
 * 检查数据
 */
function check_form(){
	if($("projNum").value=="" || $("projName").value=="" || $("beginDate").value=="" || $("endDate").value=="")
	{
	   alert("必填项目不能为空！");
	   return(false);
	}
   var starttime=new Date((document.form1.beginDate.value).replace(/-/g,"/"));
   var endtime=new Date((document.form1.endDate.value).replace(/-/g,"/"));
   if(endtime<starttime)
   {
      alert("项目计划周期的结束时间不能小于开始时间！");
      return(false);
   }
   return true;
}
/**
 * 保存数据
 */
function saveData(){
	var oEditor = FCKeditorAPI.GetInstance('fileFolder');
	$("description").value = oEditor.GetXHTML();
	if(check_form()){
		 var url= contextPath + "/t9/project/project/act/T9ProjectAct/saveData.act";
		var rtJson = getJsonRs(url,mergeQueryString($("form1")));
		  if(rtJson.rtState == "0"){
		    alert('数据保存成功！');
		    var curId=rtJson.rtData.maxId;
		    $("projId").value=curId;
		    window.parent.projId=curId;
		    parent.location="../index.jsp?projId="+curId;
		    //parent.opener.location.reload();
		  }else{
				alert(rtJson.rtMsrg);
		  }  
	}
}


/*
 * 提交审批
 */
function submitApprove(flag){
	var projId=$("projId").value;
	if(ableSubmit(projId)){
		var url= contextPath + "/t9/project/project/act/T9ProjectAct/submitApprove.act?projId="+projId+"&flag="+flag;
		var rtJson=getJsonRs(url);
		if(rtJson.rtState == "0"){
		    alert('项目已提交审批');
		    parent.location="../projectList.jsp"
		  }else{
				alert(rtJson.rtMsrg);
		  } 
	}
}
/*
 * 马上立项
 */
function directSub(flag){
	var projId=$("projId").value;
	if(ableSubmit(projId)){
		if(window.confirm("您是免审批人员，确定直接立项吗？")){
			var url= contextPath + "/t9/project/project/act/T9ProjectAct/submitApprove.act?projId="+projId+"&flag="+flag;
			var rtJson=getJsonRs(url);
			if(rtJson.rtState == "0"){
			    parent.location="../projectList.jsp"
			  }else{
					alert(rtJson.rtMsrg);
			  } 
		}
	}
}

/*
 * 导出为项目模板
 */
function exportProj(){
	var projId=$("projId").value;
	//var fileName=$("modelName").value;
	var fileName=$("modelName").value;
	var param="?projId="+projId+"&modelName="+fileName;
	if(window.confirm("确定导出为模板？")){
		var url= contextPath + "/t9/project/project/act/T9ProjectAct/exportProj.act";
		var rtJson=getJsonRs(url,param);
		if(rtJson.rtState == "0"){
		    HideDialog('comment');
		  }else{
				alert(rtJson.rtMsrg);
		  } 
	}
}


function proj_import(modelName){
	var param="?modelName="+modelName;
	var url= contextPath + "/t9/project/project/act/T9ProjectAct/importProj.act";
	var rtJson=getJsonRs(url,param);
	if(rtJson.rtState == "0"){
			//alert(rtJson.rtData.projId);
			var curId=rtJson.rtData.projId;
			parent.location="../index.jsp?projId="+curId; 
	  }else{
			alert(rtJson.rtMsrg);
	  }
}
/**
 * 根据seqID 获取项目基本详情
 */
function getBasicInfo(projId){
	 var url= contextPath + "/t9/project/project/act/T9ProjectAct/getBasicInfo2.act?projId="+projId;
	 var rtJson=getJsonRs(url);
	 if(rtJson.rtState == "0"){
		 var data=rtJson.rtData;
		 $("projId").value=projId;
	   $("projNum").value=data.projNum;
	   $("projName").value=data.projName;
	   $("beginDate").value=data.projStartTime.substring(0,10);
	   $("endDate").value=data.projEndTime.substring(0,10);
	   fckContentStr=data.projDescription;
	   $("projOwner").value=data.projOwner;
	   $("saveTemplate").style.display="";
	   $("importTemplate").style.display="none";
	   if(data.projOwner){
		      bindDesc([{cntrlId:"projOwner", dsDef:"PERSON,SEQ_ID,USER_NAME"}]);
		    }
	    $("projLeader").value=data.projLeader;
	   if(data.projLeader){
		      bindDesc([{cntrlId:"projLeader", dsDef:"PERSON,SEQ_ID,USER_NAME"}]);
		    }
	   if(data.projManager && !isApprove()){
	  	 $("projManager").value=data.projManager;
	   }
	   if(data.projStatus!='0'){
			$("approveSubmit").style.display="none";
			$("directSubmit").style.display="none";
	   }else{
		   if(isApprove()){
			   $("directSubmit").style.display="";
			  
		   }else{
			   $("approveSubmit").style.display="";
		   }
	   }
	   $("attachmentId").value=data.attachmentId;
	   $("attachmentName").value=data.attachmentName;
	   $("user").value=data.projViwer;
	   if(data.projViwer){
		      bindDesc([{cntrlId:"user", dsDef:"PERSON,SEQ_ID,USER_NAME"}]);
		    }
	   $("dept").value=data.projDept;
	   if(data.projDept){
		      bindDesc([{cntrlId:"dept", dsDef:"DEPARTMENT,SEQ_ID,DEPT_NAME"}]);
		    }
	   if(data.projDept=='0'){
		   $("deptDesc").value="全体部门";
	   }
	   $("projStyle").value=data.projType;  
	   if( $("attachmentId").value){
		    attachMenuUtil("showAtt","project",null,$("attachmentName").value,$("attachmentId").value,false);
		  }
		 }else{
				alert(rtJson.rtMsrg);
		  } 
}

function FCKeditor_OnComplete( editorInstance ) {
	  editorInstance.SetData( fckContentStr ) ;
	}
	
	
	/**
	*判断是否能够提交审批
	*/
	function ableSubmit(projId){
		var url= contextPath + "/t9/project/project/act/T9ProjectAct/ableSubmit.act?projId="+projId;
		var rtJson=getJsonRs(url);
		if(rtJson.rtState == "0"){
			var data=rtJson.rtData;
		   	if(data.flag=="1"){
		   		alert("您还没有分配项目任务");
		   		return false;
		   	}
		    else if(data.flag=="2"){
		    	alert("您还没有设置项目成员");
		   		return false;
		   	}else{
		   		return true;
		   	}
		  }else{
				alert(rtJson.rtMsrg);
				return false;
		  }
	}
	
	function goBack(){
		parent.location="../projectList.jsp";
	}
	
	
	function exportShow(){
		ShowDialog('comment');
	}
	function importShow(){
		ShowDialog('detail');
		getModelList();
	}
	
	
	function getModelList(){
		var url= contextPath + "/t9/project/project/act/T9ProjectAct/getModelList.act";
		var rtJson=getJsonRs(url);
		if(rtJson.rtState == "0"){
			var data=rtJson.rtData;
			if(data.size()>0){
				var str="<ul style=\"margin:0px;padding:0px;list-style-type:none;\">"
				for(var i=0;i<data.size();i++){
						str+="<li style=\"margin:0px;padding:0px;height:30px;\"><a style=\"height:30px;\" href=\"javascript:proj_import('"+data[i].fileName+"')\"><div class=\"template\">"+data[i].fileName+"</div></a></li>"
					}
					str+="</ul>"
				$("detailInfo").innerHTML=str;
			}else{
				$("detailInfo").innerHTML="<div style=\"height:30px;line-height:30px;font-size:16px;color:red\">无项目模板</div>";
			}
			
		}
	}
</script>
</head>
<body onload="doInit();">
<form method="post" id="form1" name="form1" enctype="multipart/form-data" action="<%=contextPath%>/t9/project/project/act/T9ProjectAct/saveData.act">
 <table class="TableBlock" border="0" width="90%" align="center">
  	<tr>
  		<td nowrap class="TableContent" width="90">项目编号：<span style="color:red">(*)</span></td>
  	  <td class="TableData">
  	  	<input type="text" class="BigInput" name="projNum" id="projNum" value="" size=20><span id="check_msg"></span>
  	  </td>  	  	
  		<td nowrap class="TableContent" width="90">项目名称：<span style="color:red">(*)</span></td>
  	  <td class="TableData"><input type="text" class="BigInput" name="projName" id="projName" value="" size=20></td>  	  	
  	</tr>
    <tr>
  		<td nowrap class="TableContent">项目负责人：</td>
  	  <td class="TableData">
		<input type="hidden" name="projLeader" id="projLeader" value="">
	    <input name="projLeaderDesc" id="projLeaderDesc"  class="BigStatic" readonly>
	    <a href="javascript:;" class="orgAdd" onClick="selectSingleUser(['projLeader','projLeaderDesc']);">选择</a>
  	  </td>
  		<td nowrap class="TableContent">项目计划周期：<span style="color:red">(*)</span></td>
  	  <td class="TableData"> 
  	     	    <input type="text" id="beginDate" name="beginDate" class=BigInput size="10">
                <img id="beginImg" src="<%=imgPath%>/calendar.gif" align="absMiddle" border="0" style="cursor:pointer" >
       至      	    <input type="text" id="endDate" name="endDate" class=BigInput size="10" value="">
                <img id="endImg" src="<%=imgPath%>/calendar.gif" align="absMiddle" border="0" style="cursor:pointer" >
     
      </td>
  	</tr>
  	<tr>
  		<td nowrap class="TableContent">项目类别：</td>
  	  <td class="TableData">
		<select name="projStyle" class="SmallSelect" id="projStyle" ></select> <span style="color:red">(*)</span>  	  </td>	  	
     <td nowrap class="TableContent">项目审批人：</td>
     <td nowrap class="TableData">
     	 <div id="manager"><select name="projManager" id="projManager" class="SmallSelect" >
       </select></div>
     </td>
  	</tr>
  	<tr>
  		<td nowrap class="TableContent">参与部门：</td>
  	  <td class="TableData">
  	  	 <input type="hidden" name="dept" id="dept" value="">
        <textarea cols=40 name="deptDesc" id="deptDesc" rows=3 class="BigStatic" wrap="yes" readonly></textarea>
       	<a href="javascript:;" class="orgAdd" onClick="selectDept()">添加</a>
      	<a href="javascript:;" class="orgClear" onClick="$('dept').value='';$('deptDesc').value='';">清空</a>
     </td>
  		<td nowrap class="TableContent">项目创建人：</td>
  	    <td class="TableData">
	      	<input type="hidden" name="projOwner" id="projOwner"  class="BigStatic">
	      	<input name="projOwnerDesc" id="projOwnerDesc"  class="BigStatic" readonly>
	      	<a href="javascript:;" class="orgAdd" onClick="selectSingleUser(['projOwner','projOwnerDesc']);">选择</a>
  	  </td>
  	</tr>
	<!--此处存放全局变量-->
	<tr>
		<td class="TableData" colspan="4" id="DEFINE_SYSCODE_CONTENT_G"></td>
	</tr>
	<!--此处存放自定义变量-->
	<tr>
		<td class="TableData" colspan="4" id="DEFINE_SYSCODE_CONTENT" style="display:none"></td>
	</tr>
  	<tr>
     <td nowrap class="TableContent">项目查看者：</td>
     <td nowrap class="TableData" colspan="3">
     	    <input type="hidden" name="user" id="user" value="">
	      	<textarea cols=40 name="userDesc" id="userDesc" rows=3 class="BigStatic" wrap="yes" readonly></textarea>
	      	<a href="javascript:;" class="orgAdd" onClick="selectUser();">添加</a>
	      	<a href="javascript:;" class="orgClear" onClick="$('user').value='';$('userDesc').value='';">清空</a>
     </td>
   </tr>
    <tr>
  		<td nowrap class="TableContent">项目描述：</td>
  	  <td class="TableData" colspan="3">
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
          <script>ShowAddFile();ShowAddImage();</script>
          <script>$("ATTACHMENT_upload_div").innerHTML='<a href="javascript:upload_attach();">上传附件</a>'</script>
          <input type="hidden" name="ATTACHMENT_ID_OLD" id="ATTACHMENT_ID_OLD" value="">
          <input type="hidden" name="ATTACHMENT_NAME_OLD" id="ATTACHMENT_NAME_OLD" value="">
          <span id="spanButtonUpload" title="批量上传附件"> </span>
        </div>
      </td>
    </tr>
    <tr align="center" class="TableControl">
    	<td colspan="4" nowrap>
    	<input type="hidden" name="projId" id="projId" value="">
    	<input type="hidden" name="description" id="description" value="">
    	<input type="button" value="保存" class="BigButton" onClick="saveData();">&nbsp;
    	<input type="button" value="返回" class="BigButton" onClick="goBack();">&nbsp;
	    <input type="button" value="提交审批" id='approveSubmit' name="approveSubmit" class="BigButton" onClick="submitApprove('0');">&nbsp;
	    <input type="button" value="马上立项" id='directSubmit' name="directSubmit" class="BigButton" onClick="directSub('1');">&nbsp;
      <input type="button" value="从模板导入" id="importTemplate" name="importTemplate" class="BigButton" onClick="importShow();">
      <input type="button" value="另存为模板" id="saveTemplate" name="saveTemplate" class="BigButton" onClick="exportShow();">
	    </td>
  </tr>
 </table>
</form>
<form id="formFile" action="<%=contextPath %>/t9/project/project/act/T9ProjectAct/uploadFile.act" method="post" enctype="multipart/form-data" target="commintFrame">
  <input id="btnFormFile" name="btnFormFile" type="submit" style="display:none;"></input>
</form>
<iframe widht="0" height="0" name="commintFrame" id="commintFrame"></iframe>


<div id="overlay"></div>
<div id="comment" class="ModalDialog" style="width:550px;">
  <div class="header"><span id="title" class="title">另存为模板</span><a class="operation" href="javascript:HideDialog('comment');"><img src="../../images/close.png"/></a></div>
  <form name="form2" id="form2" method="post">
  <div id="detail_body" class="body">
 			<div align="center">模板名称：	<input type="text" id="modelName" name="modelName">(为空，模板名称为当前项目名称)</div>
  </div>
   <div id="footer" class="footer">
    <input class="BigButton" type="button" value="确定" onclick="exportProj()"/>
    <input class="BigButton" onclick="HideDialog('comment')" type="button" value="关闭"/>
  </div>
  </form>
</div>

<div id="detail" class="ModalDialog" style="width:550px;">
  <div class="header"><span id="title" class="title">项目模板列表</span><a class="operation" href="javascript:HideDialog('detail');"><img src="../../images/close.png"/></a></div>
  <div id="detailInfo" style="text-align:center;margin-top:0px;">
  </div>
  <div id="footer" class="footer" style="margin-top:15px;">
    <input class="BigButton" onclick="HideDialog('detail')" type="button" value="关闭"/>
  </div>
</div>
</body>
</html>
