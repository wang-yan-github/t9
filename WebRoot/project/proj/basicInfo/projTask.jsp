<%@ page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header.jsp"%>
<%
	String projId=request.getParameter("projId");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>项目任务列表</title>
<link rel="stylesheet" href="<%=cssPath%>/cmp/tab.css" type="text/css" />
<link rel="stylesheet" href="<%=cssPath%>/style.css">
<link rel="stylesheet" href="<%=cssPath%>/cmp/Calendar.css">
<link rel="stylesheet" href="<%=contextPath%>/project/css/dialog.css">
<script type="text/Javascript" src="<%=contextPath%>/core/js/datastructs.js"></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/sys.js"></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/prototype.js"></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/smartclient.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/Calendarfy.js"></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/cmp/select.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/cmp/page.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/cmp/tab.js"></script>
<script type="text/javascript" src="<%=contextPath %>/project/js/dialog.js"></script>
<script type="text/javascript" src="<%=contextPath %>/project/js/util.js"></script>
<script type="text/javascript">
var projId=<%=projId%>;
function doInit(){
		if(projId==null || projId==""){
			projId="0";
		}
	 var url= contextPath + "/t9/project/task/act/T9TaskAct/getTaskList.act?projId="+projId;
	 var rtJson = getJsonRs(url);
	 if(rtJson.rtState == "0"){
		  var data=rtJson.rtData;
		  if(data.size()>0){
			  var str="<table id=\"taskList\" class=\"TableList\" border=\"0\" width=\"100%\" align=\"center\">"
	   				+ "<tr class=\"TableHeader\">"
	    			+ "<td nowrap align=\"center\">标识</td>"
	    			+ "<td nowrap align=\"center\">任务名称</td>"
	      		+ "<td nowrap align=\"center\">负责人</td>"
	    			+ "<td nowrap align=\"center\">开始</td>"
	    			+ "<td nowrap align=\"center\">工期</td>"
	    			+ "<td nowrap align=\"center\">结束</td>"
	    			+ "<td nowrap align=\"center\">完成度</td>" 	
	   				+ "</tr>";
	   		var taskList="";
				for(var i=0;i<data.size();i++){
					taskList +="<tr class=\"TableLine2\">"
	            + "<td nowrap align=\"center\">"+showStone(data[i].taskMilestone)+"</td>"
	            + "<td nowrap align=\"center\"><a href=\"#\" onclick=\"getDetailInfo("+data[i].taskId+")\">"+data[i].taskName+"</a></td>"
	            + "<td nowrap align=\"center\">"+data[i].taskUser+"</td>"
	            + "<td nowrap align=\"center\">"+data[i].beginDate+"</td>"
	            + "<td nowrap align=\"center\">"+data[i].taskTime+"</td>"
	            + "<td nowrap align=\"center\">"+data[i].endDate+"</td>"
	            + "<td nowrap align=\"center\">"
	            + "<span>"+data[i].taskPercentComplete+"%</span>"
	            + "<span style=\" margin-left:50px;\"><a href=\"javascript:void(0)\" onclick=\"showTaskInfo("+data[i].taskId+");\">详情</a></span></td>"  	
	          	+ "</tr>";
				}
	   	 $("taskInfoList").innerHTML=str+taskList+"</table>";
		  }else{
			  WarningMsrg('无项目任务', 'msrg');
		  }
	 }else{
		 WarningMsrg('无项目任务', 'msrg');
	 }  
}


function showStone(flag){
	if(flag==1){
		return "<img src=\"../../images/milestone.gif\"/>";
	}else{
		return "-";
	}
}

function showTaskInfo(taskId){
	  var url= contextPath + "/project/task/taskLogDetail.jsp?taskId="+taskId;
	  myleft=(screen.availWidth-500)/2;
	  window.open(url,"taskLogDetail","height=500,width=550,status=1,toolbar=no,menubar=no,Location=no,scrollbars=yes,top=100,left="+myleft+",resizable=yes");
}

function getDetailInfo(taskId){
	 var str="<table width=\"80%\" align=\"center\" class=\"TableList\" border=\"0\">";
	 var url= contextPath + "/t9/project/task/act/T9TaskAct/getTaskInfo.act?taskId="+taskId;
	 var rtJson = getJsonRs(url);
	 if(rtJson.rtState == "0"){
		 var data=rtJson.rtData;
			str +="<tr><td  nowrap class=\"TableContent\">任务序号：</td><td class=\"TableData\" >"+data.taskNo+"</td></tr>"
			 		 + "<tr><td nowrap class=\"TableContent\">任务名称：</td><td class=\"TableData\" >"+data.taskName+"</td></tr>"
			 		 + "<tr><td nowrap class=\"TableContent\">上级任务：</td><td class=\"TableData\" >"+data.parentTask+"</td></tr>"
			 		 + "<tr><td nowrap class=\"TableContent\">前置任务：</td><td class=\"TableData\" >"+data.preTask+"</td></tr>"
			 		 + "<tr><td nowrap class=\"TableContent\">任务计划周期：</td><td class=\"TableData\" >"+data.taskStartTime.substring(0,10)+"至"+data.taskEndTime.substring(0,10)+"</td></tr>"
			 		 + "<tr><td nowrap class=\"TableContent\">任务工时：</td><td class=\"TableData\" >"+data.taskTime+"</td></tr>"
			 		 + "<tr><td nowrap class=\"TableContent\">任务描述：</td><td class=\"TableData\" >"+data.taskDescription+"</td></tr>"
		}
		$("detail_body").innerHTML=str+"</table>";
		ShowDialog('detail')
} 

</script>
</head>

<body class="bodycolor" topmargin="5" onload="doInit();">
<table border="0" width="100%" cellspacing="0" cellpadding="3" class="small">
	<tr>
	   <td>
	<img src="<%=imgPath %>/green_arrow.gif" align="absmiddle" />
	<span class="big3">项目任务列表</span>
	</td>
	</tr>
</table>
<div id="taskInfoList" align="center">
	<span></span>
</div>

<div id="msrg">
</div>
<div id="overlay"></div>
<div id="detail" class="ModalDialog" style="width:550px;">
  <div class="header"><span id="title" class="title">项目问题详情</span><a class="operation" href="javascript:HideDialog('detail');"><img src="../../images/close.png"/></a></div>
  <div id="detail_body" class="body">
  </div>
  <div id="footer" class="footer">
    <input class="BigButton" onclick="HideDialog('detail')" type="button" value="关闭"/>
  </div>
</div>
</body>
</html>
