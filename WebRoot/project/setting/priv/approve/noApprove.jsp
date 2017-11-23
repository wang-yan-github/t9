<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header.jsp" %> 
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>项目申请权限设置</title>
<link rel="stylesheet" href = "<%=cssPath%>/style.css">
<link rel="stylesheet" href = "<%=cssPath%>/cmp/ExchangeSelect.css">
<script type="text/Javascript" src="<%=contextPath%>/core/js/datastructs.js" ></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/sys.js" ></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/prototype.js" ></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/smartclient.js" ></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/cmp/select.js" ></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/cmp/DTree1.0.js"></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/orgselect.js" ></script>
</head>
<script type="text/javascript">
var requestURL="<%=contextPath%>/t9/project/system/act/T9ProjSystemAct";
var privCode="NOAPPROVE";
function sendForm(){
	  var user = document.getElementById("user").value;
	  var dept = document.getElementById("dept").value;
	  var role = document.getElementById("role").value;
	  var url =requestURL + "/setNewPriv.act?privCode=" + privCode + "&user="+user+"&dept="+dept+"&role="+role;
	  var rtJson = getJsonRs(url);
	  if(rtJson.rtState == "0"){
	    alert('设置权限成功！');
	    location.reload();
	  }else{
			alert(rtJson.rtMsrg);
	  }  
	}
	
function doInit(){
	var prcsJson; 
	var url =requestURL+"/getNewPriv.act?privCode="+privCode;		
	var json = getJsonRs(url);
  if(json.rtState == '1'){ 
    alert(json.rtMsrg); 
    return ; 
  }
  prcsJson = json.rtData;
  document.getElementById("dept").value = prcsJson.deptPer;
  if(prcsJson.deptPer == "0"){
    document.getElementById("deptDesc").value = "全体部门";
  }
  else{
  	document.getElementById("deptDesc").value = prcsJson.deptPerDesc;
  }
  document.getElementById("role").value = prcsJson.privPer;
  document.getElementById("roleDesc").value = prcsJson.privPerDesc;
  document.getElementById("user").value = prcsJson.userPer;
  document.getElementById("userDesc").value = prcsJson.userPerDesc;
}
</script>
<body topmargin="5" onload="doInit();">
<div id="mianqianrange" >
<div>
	<table border="0" width="100%" cellspacing="0" cellpadding="3" class="small">
  <tr>
    <td class="small"><img src="<%=contextPath %>/core/funcs/system/filefolder/images/edit.gif" WIDTH="22" HEIGHT="20" align="absmiddle">
    	<span class="big3" id="modelName">设置免审批范围</span>
    	<span id="modelNameInfo"></span>
    </td>
  </tr>
 </table>
</div>
<div>
		<form action=""  method="post" name="form2">
<table class="TableBlock" width="60%" height="100%" align="center">
  <tr>
    <td nowrap class="TableContent" align="center">免审批范围：<br>（部门）</td>
    <td class="TableData">
      <input type="hidden" name="dept" id="dept" value="">
      <textarea cols=40 name="deptDesc" id="deptDesc" rows=5 class="BigStatic" wrap="yes" readonly></textarea>
      <a href="javascript:;" class="orgAdd" onClick="selectDept()">添加</a>
      <a href="javascript:;" class="orgClear" onClick="$('dept').value='';$('deptDesc').value='';">清空</a>
    </td>
  </tr>
  <tr>
    <td nowrap class="TableContent" align="center">免审批范围：<br>（角色）</td>
    <td class="TableData">
      <input type="hidden" name="role" id="role" value="">
      <textarea cols=40 name="roleDesc" id="roleDesc" rows=5 class="BigStatic" wrap="yes" readonly></textarea>
      <a href="javascript:;" class="orgAdd" onClick="selectRole();">添加</a>
      <a href="javascript:;" class="orgClear" onClick="$('role').value='';$('roleDesc').value='';">清空</a>
    </td>
  </tr>
  <tr>
    <td nowrap class="TableContent"" align="center">免审批范围：<br>（人员）</td>
    <td class="TableData">
      <input type="hidden" name="user" id="user" value="">
      <textarea cols=40 name="userDesc" id="userDesc" rows=5 class="BigStatic" wrap="yes" readonly></textarea>
      <a href="javascript:;" class="orgAdd" onClick="selectUser();">添加</a>
      <a href="javascript:;" class="orgClear" onClick="$('user').value='';$('userDesc').value='';">清空</a>
    </td>
  </tr>
  <tr>
    <td nowrap  class="TableControl" colspan="2" align="center">
      <input type="button" value="确定" onclick="sendForm()" class="BigButton">&nbsp;&nbsp;
    </td>
</table>
</form>
</div>
</div>
</body>
</html>