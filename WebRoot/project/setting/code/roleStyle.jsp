<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header.jsp" %> 
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>项目角色类型</title>
<link rel="stylesheet" href = "<%=cssPath%>/style.css">
<link rel="stylesheet" href = "<%=cssPath%>/cmp/ExchangeSelect.css">
<script type="text/Javascript" src="<%=contextPath%>/core/js/datastructs.js" ></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/sys.js" ></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/prototype.js" ></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/smartclient.js" ></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/cmp/select.js" ></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/cmp/DTree1.0.js"></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/orgselect.js" ></script>
<script type="text/Javascript" src="<%=contextPath%>/project/setting/code/js/logic.js" ></script>
</head>
<script type="text/javascript">
var classNo="PROJ_ROLE";
function doInit(){
	var url="<%=contextPath%>/t9/project/system/act/T9ProjSystemAct/getStyleList.act?classNo="+classNo;
	var rtJson=getJsonRs(url);
	if(rtJson.rtState==0){
    var typeList = "";
    if(rtJson.rtData.size() == 0){
      typeList = "<table class=\"MessageBox\" width=\"360\" align=\"center\"> "
               + "<tr><td class=\"msg info\"><div style=\"font-size: 12pt;\" class=\"content\">无相关信息！</div></td></tr></table>";
    }
    else{
		  typeList= "<table class=\"TableBlock\" align=\"center\" width=\"450\">"
							+ "<tr class=\"TableHeader\">"
							+ "<th colspan=\"3\" width=\"60%\">项目角色类型</th>"
							+ "<th colspan=\"1\">操作</th>"
							+ "</tr>";
    }
		var str="";
		for(var i=0;i<rtJson.rtData.size();i++){
			str += "<tr class=\"TableData\">"
				+ "<td nowrap class=\"TableData\" colspan=\"3\">"+rtJson.rtData[i].classDesc+"</td>"
				+ "<td nowrap class=\"TableData\" colspan=\"1\" align=\"center\"><span style=\"margin-right:50px;\">"
				+"<a href=\"javascript:getItemById("+rtJson.rtData[i].seqId+")\" >编辑</a></span><span><a href=\"javascript:del("+rtJson.rtData[i].seqId+");\">删除</a></span></td>"
			    + "</tr>";
		} 
		$("styleList").innerHTML= typeList + str + "</table>";
	}else{
		alert(rtJson.rtMsrg); 	
	}
}
function commitItem() {
	  if(!check()){
	    return;
	  }
	    classNo = document.getElementById("classNo").value;
	    var url = "<%=contextPath%>/t9/core/codeclass/act/T9CodeClassAct/addCodeItem.act";
	 
	    var rtJson = getJsonRs(url, mergeQueryString($("form1")));
	   
	    if (rtJson.rtState == "0") {
	      alert(rtJson.rtMsrg);
	      $("form1").reset();
	      window.location.reload();
	      document.getElementById("classNo").value = classNo;
	      document.getElementById("classCode").focus();
	    }else {
	      alert(rtJson.rtMsrg); 
	    }
	  
	}
	
function del(seqId){
	    var url = "<%=contextPath%>/t9/core/codeclass/act/T9CodeClassAct/deleteCodeItem.act?sqlId="+seqId;
	 
	    var rtJson = getJsonRs(url);
	   
	    if (rtJson.rtState == "0") {
	      alert(rtJson.rtMsrg);
	      window.location.reload();
	    }else {
	      alert(rtJson.rtMsrg); 
	    }
}
function getItemById(seqId){
    var url = "<%=contextPath%>/t9/core/codeclass/act/T9CodeClassAct/getCodeItem.act?sqlId="+seqId;
    var rtJson = getJsonRs(url);
    if (rtJson.rtState == "0") {
      $("form1").reset();
      document.getElementById("classCode").value = rtJson.rtData.classCode;
      document.getElementById("classCodeOld").value = rtJson.rtData.classCode;
      document.getElementById("sortNo").value = rtJson.rtData.sortNo;
      document.getElementById("classDesc").value = rtJson.rtData.classDesc;
      document.getElementById("sqlId").value = rtJson.rtData.sqlId;
      document.getElementById("classNo").value = rtJson.rtData.classNo;
      $('commitButton').style.display="none";
      $('updateButton').style.display="";
    }else {
      alert(rtJson.rtMsrg); 
    }
}
function updateCodeItem(){
	 if(!check()){
		    return;
		  }
		    classNo = document.getElementById("classNo").value;
		    var url = "<%=contextPath%>/t9/core/codeclass/act/T9CodeClassAct/updateCodeItem.act";
		    var rtJson = getJsonRs(url, mergeQueryString($("form1")));
		   
		    if (rtJson.rtState == "0") {
		      alert(rtJson.rtMsrg);
		      $("form1").reset();
		      window.location.reload();
		      document.getElementById("classNo").value = classNo;
		      document.getElementById("classCode").focus();
		    }else {
		      alert(rtJson.rtMsrg); 
		    }
}
</script>
<body onload="doInit()">
<table border="0" width="100%" cellspacing="0" cellpadding="3" class="small">
  <tr>
    <td class="small"><img src="<%=contextPath %>/core/funcs/system/filefolder/images/edit.gif" WIDTH="22" HEIGHT="20" align="absmiddle">
    	<span class="big3" id="modelName">项目角色类型添加</span>
    	<span id="modelNameInfo"></span>
    </td>
  </tr>
</table>
<div id="addStyle" style="min-height:200px;">
  <form action=""  method="post" name="form1" id="form1" >
	<table class="TableBlock" width="450" align="center">
   <tr height="30">
    <td  class="TableData" width="120">代码类别：</td>
    <td  class="TableData"> 项目角色类型   </td>
   </tr>
   <tr>
    <td  class="TableData" width="120">代码编号：</td>
    <td  class="TableData">
        <input type="text" name="classCode" id="classCode" class="BigInput" size="20" maxlength="100" value="">&nbsp;
    </td>
   </tr>
   <tr>
    <td  class="TableData" width="120">排序号：</td>
    <td  class="TableData">
        <input type="text" id="sortNo" name="sortNo" class="BigInput" size="20" maxlength="100" value="">
    </td>
   </tr>
   <tr>
    <td  class="TableData">代码名称：</td>
    <td  class="TableData">
        <input type="text" id="classDesc" name="classDesc" class="BigInput" size="20" maxlength="100" value="">&nbsp;
    </td>
   </tr>
   <tr>
    <td   class="TableControl" colspan="2" align="center">
        <input type="hidden" value="PROJ_ROLE" name="classNo" id="classNo">
        <input type="hidden" name="sqlId" id="sqlId">
        <input type="hidden" name="classCodeOld" id="classCodeOld">
        <div id="commitButton"><input type="button" value="添加" class="BigButton" onclick="commitItem()">&nbsp;&nbsp;</div>
        <div id="updateButton" style="display:none"><input type="button" value="更新" class="BigButton" onclick="updateCodeItem()">&nbsp;&nbsp;
        <input type="button" value="返回" class="BigButton" onclick="window.location.reload();">&nbsp;&nbsp;
        </div>
    </td>
    </tr>
</table>
  </form>
</div>
<table border="0" width="100%" cellspacing="0" cellpadding="3" class="small">
  <tr>
    <td class="small"><img src="<%=contextPath %>/core/styles/style1/img/notify_open.gif" WIDTH="22" HEIGHT="20" align="absmiddle">
    	<span class="big3" id="modelName">项目角色类型管理</span>
    	<span id="modelNameInfo"></span>
    </td>
  </tr>
</table>
<div id="styleList" style="min-height:200px;">
	
</div>
</body>
</html>