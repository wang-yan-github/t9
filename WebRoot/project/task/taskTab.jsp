<%@ page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header.jsp"%>
<%
	String taskId=request.getParameter("taskId");
	String projId=request.getParameter("projId");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>任务办理</title>
<link rel="stylesheet" href="<%=cssPath%>/cmp/tab.css" type="text/css" />
<link rel="stylesheet" href="<%=cssPath%>/style.css">
<script type="text/Javascript" src="<%=contextPath%>/core/js/datastructs.js"></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/sys.js"></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/prototype.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/Calendarfy.js"></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/smartclient.js"></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/cmp/select.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/cmp/tab.js"></script>
<script type="text/javascript">
var projId=<%=projId%>;
var taskId=<%=taskId%>;
  var jso = [
           {title:"任务日志", contentUrl:"<%=contextPath%>/project/task/taskManage.jsp?projId="+projId+"&taskId="+taskId, imgUrl: "<%=imgPath%>/notify_new.gif", useIframe:true}
           ,{title:"项目问题", contentUrl:"<%=contextPath%>/project/task/projBug.jsp?projId="+projId+"&taskId="+taskId, imgUrl: "<%=imgPath%>/notify_new.gif", useIframe:true}
           ,{title:"项目流程", contentUrl:"<%=contextPath%>/project/task/flow.jsp?projId="+projId+"&taskId="+taskId, imgUrl: "<%=imgPath%>/notify_new.gif", useIframe:true}
          ];
</script>
</head>
<body class="bodycolor" topmargin="5" onLoad="buildTab(jso, 'smsdiv', 800);">
</body>
</html>