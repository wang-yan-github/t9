<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header.jsp" %> 
<%
String projId=(String)request.getParameter("projId")==null?"":(String)request.getParameter("projId");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>项目文档</title>
<link rel="stylesheet" href = "<%=cssPath%>/style.css">
<link rel="stylesheet" href = "<%=cssPath%>/cmp/ExchangeSelect.css">
<script type="text/Javascript" src="<%=contextPath%>/core/js/datastructs.js" ></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/sys.js" ></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/prototype.js" ></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/smartclient.js" ></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/cmp/select.js" ></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/cmp/DTree1.0.js"></script>
<script type="text/javascript" src="<%=contextPath %>/project/js/util.js"></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/orgselect.js" ></script>
</head>
<script type="text/javascript">
var projId='<%=projId%>';
function check() {
	  var cntrl = document.getElementById("sortNo");
	  if (!cntrl.value) {   
	  	alert("排序号不能为空！");
	  	cntrl.focus();
	  	return false;
	  }
	  if(!isNumber(cntrl.value)){
		alert("必须填入数字！");
		cntrl.focus();
	  	return false;
	  }
	  cntrl = document.getElementById("sortName");
	  if(!cntrl.value) {
	  	alert("目录名称不能为空！");
	  	cntrl.focus();
	  	return false;
	  }
	  return true;
	}
function doInit(){
	document.getElementById("projId").value=projId; 
	var url="<%=contextPath%>/t9/project/file/act/T9ProjFileSortAct/getSortList.act?projId="+projId;
	var rtJson=getJsonRs(url);
	if(rtJson.rtState==0){
		if(rtJson.rtData.size()>0){
				var typeList= "<table class=\"TableBlock\" align=\"center\" width=\"450\">"
					+ "<tr class=\"TableHeader\">"
					+ "<th colspan=\"3\" width=\"60%\">目录名称</th>"
					+ "<th colspan=\"1\">操作</th>"
					+ "</tr>";
				var str="";
				for(var i=0;i<rtJson.rtData.size();i++){
						str += "<tr class=\"TableData\">"
								+ "<td nowrap  align=\"center\" class=\"TableData\"  colspan=\"3\">"+rtJson.rtData[i].sortName+"</td>"
								+ "<td nowrap class=\"TableData\" colspan=\"1\" align=\"center\"><span style=\"margin-right:20px;\">"
								+"<a href=\"javascript:getItemById("+rtJson.rtData[i].seqId+");\">编辑</a></span><span style=\"margin-right:20px;\">"
								+"<a href=\"javascript:del("+rtJson.rtData[i].seqId+");\" >删除</a></span><span style=\"margin-right:20px;\">"
								+"<a href=\"javascript:window.location.href='<%=contextPath%>/project/proj/setPriv.jsp?projId="+projId+"&seqId="+rtJson.rtData[i].seqId+"';\" >权限设置</a></span></td>"
							  + "</tr>";
			} 
			$("styleList").innerHTML= typeList + str + "</table>";
		}else{
			WarningMsrg('无目录信息', 'msrg');
		}
		
	}else{
		alert(rtJson.rtMsrg); 	
	}
}
function commitItem() {
	  if(!check()){
	    return;
	  }
	  projId = document.getElementById("projId").value;
	    var url = "<%=contextPath%>/t9/project/file/act/T9ProjFileSortAct/addProjFileSort.act";
	 
	    var rtJson = getJsonRs(url, mergeQueryString($("form1")));
	   
	    if (rtJson.rtState == "0") {
	      alert(rtJson.rtMsrg);
	      $("form1").reset();
	      window.location.reload();
	      document.getElementById("projId").value = projId;
	      document.getElementById("sortNo").focus();
	    }else {
	      alert(rtJson.rtMsrg); 
	    }
	  
	}
	
function del(seqId){
	if(confirm("提醒：删除该文件夹将会删除掉该文件夹下的所有文件，确认删除？")){
	    var url = "<%=contextPath%>/t9/project/file/act/T9ProjFileSortAct/deleteProjFileSort.act?seqId="+seqId;
	 
	    var rtJson = getJsonRs(url);
	   
	    if (rtJson.rtState == "0") {
	      alert(rtJson.rtMsrg);
	      window.location.reload();
	    }else {
	      alert(rtJson.rtMsrg); 
	}
	    }
}
function getItemById(seqId){
    var url = "<%=contextPath%>/t9/project/file/act/T9ProjFileSortAct/getSingleById.act?seqId="+seqId;
    var rtJson = getJsonRs(url);
    if (rtJson.rtState == "0") {
      $("form1").reset();
      document.getElementById("sortNo").value = rtJson.rtData.sortNo;
      document.getElementById("sortName").value = rtJson.rtData.sortName;
      document.getElementById("seqId").value = rtJson.rtData.seqId;
      document.getElementById("projId").value = rtJson.rtData.projId;
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
	 projId = document.getElementById("projId").value;
		    var url = "<%=contextPath%>/t9/project/file/act/T9ProjFileSortAct/updatefileSort.act";
		    var rtJson = getJsonRs(url, mergeQueryString($("form1")));
		   
		    if (rtJson.rtState == "0") {
		      alert(rtJson.rtMsrg);
		      $("form1").reset();
		      window.location.reload();
		      document.getElementById("projId").value = projId;
		      document.getElementById("sortNo").focus();
		    }else {
		      alert(rtJson.rtMsrg); 
		    }
}
</script>
<body onload="doInit()">
<table border="0" width="100%" cellspacing="0" cellpadding="3" class="small">
  <tr>
    <td class="small"><img src="<%=contextPath %>/core/funcs/system/filefolder/images/edit.gif" WIDTH="22" HEIGHT="20" align="absmiddle">
    	<span class="big3" id="modelName">新建文档目录</span>
    	<span id="modelNameInfo"></span>
    </td>
  </tr>
</table>
<div id="addStyle" style="min-height:200px;">
  <form action=""  method="post" name="form1" id="form1" >
	<table class="TableBlock" width="450" align="center">
   <tr>
    <td  class="TableData" width="120">排序号：</td>
    <td  class="TableData">
        <input type="text" id="sortNo" name="sortNo" class="BigInput" size="20" maxlength="100" value="">
    </td>
   </tr>
   <tr>
    <td  class="TableData">目录名称：</td>
    <td  class="TableData">
        <input type="text" id="sortName" name="sortName" class="BigInput" size="20" maxlength="100" value="">&nbsp;
    </td>
   </tr>
   <tr>
    <td   class="TableControl" colspan="2" align="center">
        <input type="hidden" name="seqId" id="seqId">
        <input type="hidden" name="projId" id="projId">
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
    <td class="small"><img src="<%=contextPath %>/core/funcs/system/filefolder/images/edit.gif" WIDTH="22" HEIGHT="20" align="absmiddle">
    	<span class="big3" id="modelName">文档目录列表</span>
    	<span id="modelNameInfo"></span>
    </td>
  </tr>
</table>
<div id="styleList">
	
</div>
<div id="msrg">
</div>
</body>
</html>