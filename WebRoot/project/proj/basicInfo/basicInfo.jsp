<%@ page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header.jsp"%>
<%
	String projId=request.getParameter("projId");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>基本信息</title>
<link rel="stylesheet" href="<%=cssPath%>/cmp/tab.css" type="text/css" />
<link rel="stylesheet" href="<%=cssPath%>/style.css">
<link rel="stylesheet" href="<%=cssPath%>/cmp/Calendar.css">
<script type="text/Javascript" src="<%=contextPath%>/core/js/datastructs.js"></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/sys.js"></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/prototype.js"></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/smartclient.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/Calendarfy.js"></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/cmp/select.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/cmp/tab.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/attach.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/swfupload/swfupload.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/swfupload/swfupload.queue.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/swfupload/fileprogress.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/swfupload/handlers.js"></script>
<script type="text/javascript"	src="<%=contextPath%>/core/js/cmp/Menu.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/attachMenu.js"></script>
<script type="text/javascript">
var projId=<%=projId%>;
function doInit(){
		var url= contextPath + "/t9/project/project/act/T9ProjectAct/getBasicInfo.act?projId="+projId;
	 	var rtJson = getJsonRs(url);
	  if(rtJson.rtState == "0"){
		  var data=rtJson.rtData;
		  $("projNum").innerHTML=data.projNum;
		  $("projName").innerHTML=data.projName;
		  $("projOwner").innerHTML=data.projOwner;
		  $("projManager").innerHTML=data.projManager;
		  $("projType").innerHTML=data.projType;
		  $("projDept").innerHTML=data.projDept;
		  $("projCycle").innerHTML=data.projStartTime+"至"+data.projEndTime;
		  if(data.projTime){
			  $("projTime").innerHTML=data.projTime+"天";
		  }
		  $("attachmentId").value=data.attachmentId;
		  $("attachmentName").value=data.attachmentName;
		  $("projDescription").innerHTML=data.projDescription;
		  if(data.approveLog!="null" && data.approveLog!=null){
		  	$("approveLog").innerHTML=showResult(data.approveLog);
		  }
		  var str="<table class=\"TableList\" width=\"100%\" align=\"left\" topmargin=\"5\">"
		 	 				+"<tr class=\"TableHeader\" >"
		    			+"<td nowrap colspan=\"4\"><div style=\"float:left;font-weight:bold;\">&nbsp;"
		    			+"<img src='"+imgPath+"/green_arrow.gif' align=\"absmiddle\"> 项目经费成本</div></td>"
		  				+"</tr>";
		  if(data.projCostMoney){
			  var types=(data.projCostType).split(",");
			  var moneys=(data.projCostMoney).split(",");
			  var costStr="";
			  for(var i=0;i<moneys.size();i++){
				  if(moneys[i]!="" && moneys[i]!="null"){
					  costStr +="<tr>"
					      + "<td class=\"TableContent\" width=100>"+types[i]+":</td>"
					      + "<td class=\"TableData\" nowrap colspan=\"3\">"+moneys[i]+"</td>"  	
					      + "</tr>";
				  }
			  }
		  }
		  var userStr= "<tr class=\"TableHeader\" >"
				 + "<td nowrap  colspan=4><div style=\"float:left;font-weight:bold;\">&nbsp;"
				 + "<img src=\""+imgPath+"/green_arrow.gif\" align=\"absmiddle\"> 项目成员</div></td>"
				 + "</tr>";
		  if(data.projUser){
			  var users=(data.projUser).split("|");
			  var userPrivs=(data.projUserPriv).split("|");
			  for(var j=0;j<userPrivs.size();j++){
				  if(userPrivs[j]!=""){
					  userStr	+="<tr>"
					      + "<td class=\"TableContent\" width=100>"+userPrivs[j]+":</td>"
					      + "<td class=\"TableData\" nowrap colspan=\"3\">"+users[j]+"</td>"  	
					      + "</tr>";
				  }
			  }
		  } 
		  $("roleAndCost").innerHTML=str+costStr+userStr+"</table>";
		  
		  
		  var  selfdefMenu = {
				    office:["downFile","dump","read"], 
				    img:["downFile","dump","play"],
				    music:["downFile","dump","play"],  
				    video:["downFile","dump","play"], 
				    others:["downFile","dump"]
				  }
		  if( $("attachmentId").value){
			  attachMenuSelfUtil("showAtt","project",$('attachmentName').value ,$('attachmentId').value, '','','',selfdefMenu);  
			  }
	  }else{
		 alert(rtJson.rtMsrg);
	 }
}

function showResult(result){
	var results=result.split("|*|");
	var str="";
	for(var i=0;i<results.size();i++){
		str+="<span>"+results[i]+"</span><br/>";
	}
	return str;
}
</script>
</head>
<body class="bodycolor" topmargin="5" onload="doInit();">
<table border="0" width="100%" cellspacing="0" cellpadding="3" class="small">
	<tr><td>
	<img src="../../images/project.gif" align="absmiddle"/>
	<span class="big3"> 项目基本信息</span>
	<td></tr>
</table>
<table class="TableList" width="100%" align="left" topmargin="5">
	<tr>
  	<td nowrap class="TableContent">项目编号：</td>
  	<td class="TableData"><div id="projNum">xmbh001</div></td>
  </tr>
	<tr>
  	<td nowrap class="TableContent" width=100>项目名称：</td>
  	<td class="TableData" colspan=3><div id="projName">项目名称</div></td>  	  	
  </tr>
	<tr>
  	<td nowrap class="TableContent" width=100>创建者：</td>
  	<td class="TableData" colspan=3><div id="projOwner">系统管理员</div></td>  	  	
  </tr>
	<tr>
  	<td nowrap class="TableContent" width=100>审批者：</td>
  	<td class="TableData" colspan=3><div id="projManager">系统管理员</div> </td>  	  	
  </tr>
  <tr>
    <td nowrap class="TableContent">项目类别：</td>
  	<td class="TableData"><div id="projType">工程项目</div></td>
  </tr>
  <tr>
  	<td nowrap class="TableContent">参与部门：</td>
  	<td class="TableData"><div id="projDept">管理层,业务层,测试层,</div></td>
  </tr>
  <tr>
  	<td nowrap class="TableContent">项目计划周期：</td>
  	<td class="TableData" colspan=3><div id="projCycle">2013-03-05&nbsp;至&nbsp;2013-03-06</div></td>
  </tr>
  <tr>
  	<td nowrap class="TableContent">项目工期：</td>
  	<td class="TableData" colspan=3><div id="projTime">2 工作日</div></td>
  </tr>
  <tr>
    <td nowrap class="TableContent">项目描述：</td>
  	<td class="TableData Content" style="word-break:break-all;" colspan=3><div id="projDescription">测试</div></td>
  </tr>
    <tr>
      <td nowrap class="TableContent">附件文档：</td>
      <td nowrap class="TableData" colspan="3">
      	<div id="projAttach">
      		<input type="hidden" id="attachmentId" name="attachmentId">
      		<input type="hidden" id="attachmentName" name="attachmentName">
      		<input type="hidden" id="moduel" name="moduel" value="project">
        <span id="showAtt">
        	无附件 
        </span>
      	</div>
    </td>
    </tr>
    <tr class="TableHeader">
      <td nowrap colspan=4><div style="float:left;font-weight:bold;">&nbsp;<img src="<%=imgPath %>/green_arrow.gif" align="absmiddle"> 审批记录</div></td>
    </tr>
    <tr class="TableLine2">
      <td nowrap colspan=4><div id="approveLog"><b>无审批记录</b></div></td>
    </tr>
    </table>
 <div id="roleAndCost">
 </div>
</body>
</html>