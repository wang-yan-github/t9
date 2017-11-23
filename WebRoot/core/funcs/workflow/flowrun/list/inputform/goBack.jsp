<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ include file="/core/inc/header.jsp" %> 
<%
String runId = T9Utility.null2Empty(request.getParameter("runId"));
String flowId = T9Utility.null2Empty(request.getParameter("flowId"));
String prcsId = T9Utility.null2Empty(request.getParameter("prcsId"));
String flowPrcs =T9Utility.null2Empty(request.getParameter("flowPrcs"));
String allowBack = T9Utility.null2Empty(request.getParameter("allowBack"));
String sortId = request.getParameter("sortId");
if (sortId == null) {
  sortId = "";
}
String skin = request.getParameter("skin");
if (skin == null) {
  skin = "";
}
%>
<html>
<head>
<title>回退</title>
<link rel="stylesheet" href ="<%=cssPath %>/style.css">
</head>

<body >
<iframe height="400" width="470" src="<%=contextPath %>/core/funcs/workflow/flowrun/list/inputform/goBackIndex.jsp?flowId=<%=flowId%>&runId=<%=runId%>&prcsId=<%=prcsId%>&flowPrcs=<%=flowPrcs%>&allowBack=<%=allowBack%>&sortId=<%=sortId%>&skin=<%=skin%>"></iframe>
</body>
</html>