<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header.jsp" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>参数设置</title>
<link rel="stylesheet" href = "<%=cssPath%>/style.css">
<script type="text/javascript" src="<%=contextPath %>/core/js/prototype.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/datastructs.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/sys.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/smartclient.js"></script>
<script type="text/javascript">
var paraName = "IS_RELEASE_CMS";//webservice业务接口直接发布
var paraName1 = "IS_ISSUED_CMS";//签发权限是否生效
var paraName2="IS_CTRLATTACH_CMS";
var paraName3="IMAGE_FILE_SIZE";
var paraName4="AUDIO_FILE_SIZE";
var paraName5="VIDEO_FILE_SIZE";
var paraName6="OTHER_FILE_SIZE";
function doInit() {
  //webservice业务接口直接发布
  var url = "<%=contextPath%>/t9/cms/setting/act/T9JhSysParaAct/selectObj.act?paraName=" + paraName;
  var json = getJsonRs(url);
  if(json.rtState == '1'){ 
    alert(json.rtMsrg); 
    return ; 
  } 
  var prc = json.rtData;
  if (prc.seqId) {
    if($("IS_RELEASE_CMS" + prc.paraValue)){
      $("IS_RELEASE_CMS" + prc.paraValue).checked = true;
    }
  }
  
  //签发权限是否生效
  var url = "<%=contextPath%>/t9/cms/setting/act/T9JhSysParaAct/selectObj.act?paraName=" + paraName1;
  var json = getJsonRs(url);
  if(json.rtState == '1'){ 
    alert(json.rtMsrg); 
    return ; 
  } 
  var prc = json.rtData;
  if (prc.seqId) {
    if($("IS_ISSUED_CMS" + prc.paraValue)){
      $("IS_ISSUED_CMS" + prc.paraValue).checked = true;
    }
  }
  //附件大小控制
  var url = "<%=contextPath%>/t9/cms/setting/act/T9JhSysParaAct/selectObj.act?paraName=" + paraName2;
  var json = getJsonRs(url);
  if(json.rtState == '1'){ 
    alert(json.rtMsrg); 
    return ; 
  } 
  var prc = json.rtData;
  if (prc.seqId) {
    if($("IS_CTRLATTACH_CMS" + prc.paraValue)){
      $("IS_CTRLATTACH_CMS" + prc.paraValue).checked = true;
    }
    if($("IS_CTRLATTACH_CMS1").checked){
    	$("attachSize").style.display="";
    	  var url1 = "<%=contextPath%>/t9/cms/setting/act/T9JhSysParaAct/selectObj.act?paraName=" + paraName3;
    	  var json = getJsonRs(url1);
    	  if(json.rtState == '1'){ 
    	    alert(json.rtMsrg); 
    	    return ; 
    	  } 
    	  var prc = json.rtData;
    	  if (prc.seqId) {
    		  $("IMAGE_FILE_SIZE").value=prc.paraValue;
    	  }
    	  var url2 = "<%=contextPath%>/t9/cms/setting/act/T9JhSysParaAct/selectObj.act?paraName=" + paraName4;
    	  var json = getJsonRs(url2);
    	  if(json.rtState == '1'){ 
    	    alert(json.rtMsrg); 
    	    return ; 
    	  } 
    	  var prc = json.rtData;
    	  if (prc.seqId) {
    		  $("AUDIO_FILE_SIZE").value=prc.paraValue;
    	  }
    	  var url3 = "<%=contextPath%>/t9/cms/setting/act/T9JhSysParaAct/selectObj.act?paraName=" + paraName5;
    	  var json = getJsonRs(url3);
    	  if(json.rtState == '1'){ 
    	    alert(json.rtMsrg); 
    	    return ; 
    	  } 
    	  var prc = json.rtData;
    	  if (prc.seqId) {
    		  $("VIDEO_FILE_SIZE").value=prc.paraValue;
    	  }
    	  var url4 = "<%=contextPath%>/t9/cms/setting/act/T9JhSysParaAct/selectObj.act?paraName=" + paraName6;
    	  var json = getJsonRs(url4);
    	  if(json.rtState == '1'){ 
    	    alert(json.rtMsrg); 
    	    return ; 
    	  } 
    	  var prc = json.rtData;
    	  if (prc.seqId) {
    		  $("OTHER_FILE_SIZE").value=prc.paraValue;
    	  }
    }
    else{
    	$("attachSize").style.display="none";
    }
  }
  
}

function checkForm() {
  var paraValue = 0;
  var getAipTextType = document.getElementsByName("IS_RELEASE_CMS");
  for(var i = 0; i < getAipTextType.length; i++) {
    if( getAipTextType[i].checked ){
      paraValue = getAipTextType[i].value;
    }
  }
  var requestURL = "<%=contextPath%>/t9/cms/setting/act/T9JhSysParaAct/addAndUpdate.act?paraName=IS_RELEASE_CMS&paraValue=" + paraValue;
  var json = getJsonRs(requestURL);
  if (json.rtState == '1') { 
    alert(json.rtMsrg); 
    return ; 
  } else {
    alert("保存成功！");
    window.location.reload();
  }
}

function checkForm1() {
  var paraValue = 0;
  var getAipTextType = document.getElementsByName("IS_ISSUED_CMS");
  for(var i = 0; i < getAipTextType.length; i++) {
    if( getAipTextType[i].checked ){
      paraValue = getAipTextType[i].value;
    }
  }
  var requestURL = "<%=contextPath%>/t9/cms/setting/act/T9JhSysParaAct/addAndUpdate.act?paraName=IS_ISSUED_CMS&paraValue=" + paraValue;
  var json = getJsonRs(requestURL);
  if (json.rtState == '1') { 
    alert(json.rtMsrg); 
    return ; 
  } else {
    alert("保存成功！");
    window.location.reload();
  }
}


function checkForm2() {
	  var paraValue = 0;
	  var getAipTextType = document.getElementsByName("IS_CTRLATTACH_CMS");
	  for(var i = 0; i < getAipTextType.length; i++) {
	    if( getAipTextType[i].checked ){
	      paraValue = getAipTextType[i].value;
	    }
	  }
	  var requestURL="";
	  var json="";
	  if($("IS_CTRLATTACH_CMS1").checked){
		  requestURL = "<%=contextPath%>/t9/cms/setting/act/T9JhSysParaAct/addAndUpdateAttach.act?paraName=IS_CTRLATTACH_CMS&paraValue=" + paraValue;
		  json = getJsonRs(requestURL,mergeQueryString($("form1")));
	  }else{
		  requestURL = "<%=contextPath%>/t9/cms/setting/act/T9JhSysParaAct/addAndUpdate.act?paraName=IS_CTRLATTACH_CMS&paraValue=" + paraValue;
		  json = getJsonRs(requestURL);
	  }
	  if (json.rtState == '1') { 
	    alert(json.rtMsrg); 
	    return ; 
	  } else {
	    alert("设置成功！");
	    window.location.reload();
	  }
	}
function hideDiv(){
	$("attachSize").style.display="none";
}
function showDiv(){
	$("attachSize").style.display="";
}
</script>
</head>
<body class="bodycolor" topmargin="5" onload="doInit();">
<table border="0" width="100%" cellspacing="0" cellpadding="3" class="small">
  <tr>
    <td class="Big"><img src="<%=imgPath %>/theme.gif" align="absmiddle"><span class="big3"> 参数设置</span>
    </td>
  </tr>
</table>
<br>
<table class="TableList" width="60%" align="center" >
  <tr class="TableData" align="center" height="30">
    <td width="250"><b>webservice业务接口直接发布</b></td>
    <td align="left">
       <input type="radio" name="IS_RELEASE_CMS" id="IS_RELEASE_CMS0" value="0" checked><label for="IS_RELEASE_CMS0">否</label>&nbsp;&nbsp;&nbsp;&nbsp;
       <input type="radio" name="IS_RELEASE_CMS" id="IS_RELEASE_CMS1" value="1" ><label for="IS_RELEASE_CMS1">是</label>
    </td>
    <td width="250" align="left">
      <Input type="button" name="submit" class="BigButton" value="保存" onclick="checkForm()">
    </td>
  </tr>
</table>
<p>
<table class="TableList" width="60%" align="center" >
  <tr class="TableData" align="center" height="30">
    <td width="250"><b>签发权限是否生效</b></td>
    <td align="left">
       <input type="radio" name="IS_ISSUED_CMS" id="IS_ISSUED_CMS0" value="0" checked><label for="IS_ISSUED_CMS0">否</label>&nbsp;&nbsp;&nbsp;&nbsp;
       <input type="radio" name="IS_ISSUED_CMS" id="IS_ISSUED_CMS1" value="1" ><label for="IS_ISSUED_CMS1">是</label>
    </td>
    <td width="250" align="left">
      <Input type="button" name="submit" class="BigButton" value="保存" onclick="checkForm1()">
    </td>
  </tr>
</table>
<p>

<table class="TableList" width="60%" align="center" >
  <tr class="TableData" align="center" height="30">
    <td width="250"><b>控制附件大小</b></td>
    <td align="left">
       <input type="radio" name="IS_CTRLATTACH_CMS" id="IS_CTRLATTACH_CMS0" value="0" checked onClick="hideDiv()"><label for="IS_CTRLATTACH_CMS0">否</label>&nbsp;&nbsp;&nbsp;&nbsp;
       <input type="radio" name="IS_CTRLATTACH_CMS" id="IS_CTRLATTACH_CMS1" value="1" onClick="showDiv()" ><label for="IS_CTRLATTACH_CMS1">是</label>
    </td>
    <td width="250" align="left">
      <Input type="button" name="submit" class="BigButton" value="保存" onclick="checkForm2()">
    </td>
  </tr>
</table>
<div id="attachSize">
	<table class="TableList" width="60%" align="center" style="border-top:1px solid #ffffff">
<form method="post" name="form1" id="form1">
  <tr class="TableData" align="center" height="30">
    <td width="250">图片文件大小:</td>
    <td width="250" align="left">
      <input type="text" name="IMAGE_FILE_SIZE" id="IMAGE_FILE_SIZE" class="BigInput" size="15">(MB)
    </td>
  </tr>
    <tr class="TableData" align="center" height="30">
    <td width="250">音频文件大小:</td>
    <td width="250" align="left">
     <input type="text" name="AUDIO_FILE_SIZE" id="AUDIO_FILE_SIZE" class="BigInput" size="15">(MB)
    </td>
  </tr>
    <tr class="TableData" align="center" height="30">
    <td width="250">视频文件大小:</td>
    <td width="250" align="left">
      <input type="text" name="VIDEO_FILE_SIZE" id="VIDEO_FILE_SIZE" class="BigInput" size="15">(MB)
    </td>
  </tr>
    <tr class="TableData" align="center" height="30">
    <td width="250">其他文件大小:</td>
    <td width="250" align="left">
     <input type="text" name="OTHER_FILE_SIZE" id="OTHER_FILE_SIZE" class="BigInput" size="15">(MB)
    </td>
  </tr>
</form>
</table>
</div>
</body>
</html>