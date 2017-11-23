<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header.jsp" %> 
<%
	String seqId=request.getParameter("seqId");
%>
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
var privCode="APPROVE";
var seqId=<%=seqId%>;
function doInit(){
	var url =requestURL + "/getApproveList.act?privCode="+privCode;
	var rtJson=getJsonRs(url);
	if(rtJson.rtState==0){
    var typeList = "";
    if(rtJson.rtData.size() == 0){
      typeList = "<table class=\"MessageBox\" width=\"360\" align=\"center\"> "
               + "<tr><td class=\"msg info\"><div style=\"font-size: 12pt;\" class=\"content\">无相关信息！</div></td></tr></table>";
    }
    else{
		  typeList= "<table class=\"TableBlock\" align=\"center\" width=\"800\">"
							+ "<tr class=\"TableHeader\">"
							+ "<th colspan=\"3\" width=\"40%\">审批人员</th>"
							+ "<th colspan=\"3\" width=\"40%\">管理部门</th>"
							+ "<th colspan=\"1\">操作</th>"
							+ "</tr>";
    }
		var str="";
		for(var i=0;i<rtJson.rtData.size();i++){
			str += "<tr class=\"TableData\">"
				+ "<td nowrap class=\"TableData\" colspan=\"3\">"+rtJson.rtData[i].approveUser+"</td>"
				+ "<td nowrap class=\"TableData\" colspan=\"3\">"+rtJson.rtData[i].managerDept+"</td>"
				+ "<td nowrap class=\"TableData\" colspan=\"1\" align=\"center\"><span style=\"margin-right:50px;\">"
				+"<a href=\"javascript:void(0)\" onclick=\"modify("+rtJson.rtData[i].seqId+")\">编辑</a></span><span><a href=\"javascript:void(0)\" onclick=\"del("+rtJson.rtData[i].seqId+");\">删除</a></span></td>"
			    + "</tr>";
		} 
		$("ruleList").innerHTML= typeList + str + "</table>";
	}else{
		alert(rtJson.rtMsrg); 	
	}
}
function sendForm(){
	if(check()){
		  var user = document.getElementById("user").value;
		  var dept = document.getElementById("dept").value;
		  var seqId = document.getElementById("seqId").value;
		  var url =requestURL + "/setApprovePriv.act?privCode=" + privCode +"&seqId="+seqId+"&user="+user+"&dept="+dept;
		  var rtJson = getJsonRs(url);
		  if(rtJson.rtState == "0" && $("doSubmit").value=="确定"){
		    alert('设置权限成功！');
		    location.reload();
		  }
		  else if(rtJson.rtState == "0" && $("doSubmit").value=="修改"){
			    alert('权限修改成功！');
			    location.reload();
			  }else{
				alert(rtJson.rtMsrg);
		  }
	}
	  
}
function check(){
	if($('user').value==""||$('user').value==null){
		alert("审批人员不能为空");
		return false;
	}
	if($('dept').value==""||$('user').value==null){
		alert("请选择管理部门");
		return false;
	}
	return true;
}

function del(seqId){
	if(confirm("确认删除！")){
		var url =requestURL + "/delApprovePriv.act?seqId=" + seqId;
		var rtJson = getJsonRs(url);
		if(rtJson.rtState == "0" ){
		    alert('删除成功!!!');
		    location.reload();
		  }else{
			  alert(rtJson.rtMsrg);
		  }
	}
}

function modify(seqId){
	var url =requestURL + "/getApprovePriv.act?seqId=" + seqId;
	$("seqId").value=seqId;
	$("doSubmit").value="修改";
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
	  document.getElementById("user").value = prcsJson.userPer;
	  document.getElementById("userDesc").value = prcsJson.userPerDesc;
}
</script>
<body topmargin="5" onload="doInit();">
<div id="approvePerson">
<table border="0" width="100%" cellspacing="0" cellpadding="3" class="small">
  <tr>
    <td class="small"><img src="<%=contextPath %>/core/funcs/system/filefolder/images/edit.gif" WIDTH="22" HEIGHT="20" align="absmiddle">
    	<span class="big3" id="modelName">设置审批人员</span>
    	<span id="modelNameInfo"></span>
    </td>
  </tr>
</table>
<div>
<form action=""  method="post" name="form1">
<table class="TableBlock" width="60%" height="100%" align="center">
  <tr>
    <td nowrap class="TableContent"" align="center">审批人员：</td>
    <td class="TableData">
      <input type="hidden" name="user" id="user" value="">
      <input type="hidden" name="seqId" id="seqId" value="">
      <textarea cols=40 name="userDesc" id="userDesc" rows=5 class="BigStatic" wrap="yes" readonly></textarea>
      <a href="javascript:;" class="orgAdd" onClick="selectUser();">添加</a>
      <a href="javascript:;" class="orgClear" onClick="$('user').value='';$('userDesc').value='';">清空</a>
    </td>
  </tr>
    <tr>
    <td nowrap class="TableContent" align="center">所管部门：</td>
    <td class="TableData">
      <input type="hidden" name="dept" id="dept" value="">
      <textarea cols=40 name="deptDesc" id="deptDesc" rows=5 class="BigStatic" wrap="yes" readonly></textarea>
      <a href="javascript:;" class="orgAdd" onClick="selectDept()">添加</a>
      <a href="javascript:;" class="orgClear" onClick="$('dept').value='';$('deptDesc').value='';">清空</a>
    </td>
  </tr>
  <tr>
    <td nowrap  class="TableControl" colspan="2" align="center">
      <input id="doSubmit" type="button" value="确定" onclick="sendForm()" class="BigButton">&nbsp;&nbsp;
    </td>
</table>
</form>
</div>
</div>
<div id="approveList">
 <table border="0" width="100%" cellspacing="0" cellpadding="3" class="small">
  <tr>
    <td class="small"><img src="<%=contextPath %>/core/funcs/system/filefolder/images/edit.gif" WIDTH="22" HEIGHT="20" align="absmiddle">
    	<span class="big3" id="modelName">审批规则管理</span>
    	<span id="modelNameInfo"></span>
    </td>
  </tr>
 </table>
 <div id="ruleList">
 </div>
 </div>
</body>
</html>