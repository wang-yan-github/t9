<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header.jsp" %> 
<%
String seqId=(String)request.getParameter("projId")==null?"":(String)request.getParameter("projId");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>项目成员</title>
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
var seqId='<%=seqId%>';
var privCode="NEW";
var classNo="PROJ_ROLE";
function doInit(){
	$("seqId").value=seqId;
	var url="<%=contextPath%>/t9/project/system/act/T9ProjSystemAct/getStyleList.act?classNo="+classNo;
	var rtJson=getJsonRs(url);
	if(rtJson.rtState==0){
		var selectObj = $("projPriv");
	    for(var i=0;i<rtJson.rtData.size();i++){
	    	  var opts = new Option(rtJson.rtData[i].classDesc);
	    	  opts.value = rtJson.rtData[i].seqId;
	        selectObj.add(opts);
	        }
	}else{
		alert(rtJson.rtMsrg); 	
	}
	getMemberList();

}
function getMemberList(){
	var url="<%=contextPath%>/t9/project/project/act/T9ProjectAct/getUserList.act?seqId="+seqId;
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
							+ "<th width=\"40%\">项目角色</th>"
							+ "<th width=\"40%\">姓名</th>"
							+ "<th >操作</th>"
							+ "</tr>";
    }
		var str="";
		for(var i=0;i<rtJson.rtData.size();i++){
			str += "<tr class=\"TableData\">"
				+ "<td nowrap class=\"TableData\" align=\"center\">"+rtJson.rtData[i].codeName+"</td>"
				+ "<td nowrap class=\"TableData\" >"+rtJson.rtData[i].user.substr(0,rtJson.rtData[i].user.length-1)+"</td>"
				+ "<td nowrap class=\"TableData\"  align=\"center\"><span style=\"margin-right:50px;\">"
				+"<a href=\"javascript:del("+rtJson.rtData[i].privId+");\">删除</a></span></td>"
			    + "</tr>";
		} 
		$("styleList").innerHTML= typeList + str + "</table>";
	}else{
		alert(rtJson.rtMsrg); 	
	}
}

function commitItem() {
	if($('userDesc').value==""){
		alert("请选择人员后添加");
		return ;
		}
	    var url = "<%=contextPath%>/t9/project/project/act/T9ProjectAct/addProjMember.act";
	 
	    var rtJson = getJsonRs(url, mergeQueryString($("form1")));
	   
	    if (rtJson.rtState == "0") {
	      alert(rtJson.rtMsrg);
	      window.location.reload();
	      $("seqId").value=seqId;
	    }else {
	      alert(rtJson.rtMsrg); 
	    }
	  
	}
	
function del(privId){
	var projId=$("seqId").value;
	    var url = "<%=contextPath%>/t9/project/project/act/T9ProjectAct/delProjPrivUser.act?privId="+privId+"&projId="+projId;
	    var rtJson = getJsonRs(url);
	    if (rtJson.rtState == "0") {
	      alert("删除成功!");
	      window.location.reload();
	    }else {
	      alert(rtJson.rtMsrg); 
	    }
}
</script>
<body onload="doInit()">
<table border="0" width="100%" cellspacing="0" cellpadding="3" class="small">
  <tr>
    <td class="small"><img src="<%=contextPath %>/core/funcs/system/filefolder/images/edit.gif" WIDTH="22" HEIGHT="20" align="absmiddle">
    	<span class="big3" id="modelName">添加项目成员</span>
    	<span id="modelNameInfo"></span>
    </td>
  </tr>
</table>
<div id="addStyle" style="min-height:200px;">
  <form action=""  method="post" name="form1" id="form1" >
	<table class="TableBlock" width="450" align="center">
   <tr height="30">
    <td  class="TableData" width="120">项目角色：</td>
    <td  class="TableData"><select id="projPriv" name="projPriv"></select></td>
   </tr>
   <tr>
    <td  class="TableData" width="120">选择人员：</td>
    <td  class="TableData">
    <input type="hidden" name="role" id="role" value="">
      <input type="hidden" name="user" id="user" value="">
      <textarea cols="40" name="userDesc" id="userDesc" rows="5" class="BigStatic" wrap="yes" readonly="readonly"></textarea>
      <a href="javascript:;" class="orgAdd" onClick="selectUser();">添加</a>
      <a href="javascript:;" class="orgClear" onClick="$('user').value='';$('userDesc').value='';">清空</a>
    </td>
   </tr>
   <tr>
    <td   class="TableControl" colspan="2" align="center">
        <input type="hidden" value="projUser" name="classNo" id="classNo">
        <input type="hidden" name="seqId" id="seqId">
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
    	<span class="big3" id="modelName">项目成员管理</span>
    	<span id="modelNameInfo"></span>
    </td>
  </tr>
</table>
<div id="styleList" style="min-height:200px;">
	
</div>
</body>
</html>