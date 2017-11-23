<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@  page import="java.util.Map" %>
    <%@  page import="t9.mobile.util.T9MobileUtility" %>
<%@ include file="/mobile/header.jsp" %>
<%@ include file="/mobile/readheader.jsp" %>
<%
String aName = request.getParameter("ATTACHMENT_NAME");
String aId = request.getParameter("ATTACHMENT_ID");
String module = request.getParameter("module");
String host = request.getParameter("host") + contextPath;
%>
<!DOCTYPE html>
<html>
<head>
	<meta name="viewport" content="width=device-width, minimum-scale=1, maximum-scale=1">
	<meta name="apple-mobile-web-app-status-bar-style" content="black" />
	<meta name="format-detection" content="telephone=no" /> 
	<link rel="stylesheet"  href="<%=host %>/mobile/style/pda.css?v=20120822" />
	<title>附件查阅</title>
	
</head>
<body style="padding:5px 10px;background-color:transparent;">

  <%
   if(!"".equals(aName) && !"".equals(aId))
   {
     String sessionId = request.getSession().getId();
     %>
      <div class="read_attach"><%=T9MobileUtility.getAttachLinkPda(aId, aName, sessionId, module, true, true, host) %></div>
   <%
   }
   %>
</body>
</html>