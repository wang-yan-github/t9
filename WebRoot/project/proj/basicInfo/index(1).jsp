<%@ page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header.jsp"%>
<%
	String projId=request.getParameter("projId");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>项目信息</title>
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
  var jso = [
           {title:"基本信息", contentUrl:"<%=contextPath%>/project/proj/basicInfo/basicInfo.jsp?projId="+projId, imgUrl: "<%=imgPath%>/notify_new.gif", useIframe:true}
           ,{title:"项目文档", contentUrl:"<%=contextPath%>/project/proj/fileSort/index.jsp?projId="+projId, imgUrl: "<%=imgPath%>/notify_new.gif", useIframe:true}
           ,{title:"任务列表", contentUrl:"<%=contextPath%>/project/proj/basicInfo/projTask.jsp?projId="+projId, imgUrl: "<%=imgPath%>/notify_new.gif", useIframe:true}
           ,{title:"问题追踪", contentUrl:"<%=contextPath%>/project/proj/basicInfo/projBug.jsp?projId="+projId, imgUrl: "<%=imgPath%>/notify_new.gif", useIframe:true}
           ,{title:"项目批注", contentUrl:"<%=contextPath%>/project/proj/commentList.jsp?projId="+projId, imgUrl: "<%=imgPath%>/notify_new.gif", useIframe:true}
          ];
</script>
</head>
<body class="bodycolor" topmargin="5" onLoad="buildTab(jso, 'smsdiv', 800);">
</body>
</html>