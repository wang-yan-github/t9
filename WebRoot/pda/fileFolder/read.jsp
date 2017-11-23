<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="t9.core.global.T9SysProps" %>
<%@ page import="t9.core.funcs.person.data.T9Person" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Date" %>
<%@ page import="t9.core.util.T9Utility" %>
<%
String contextPath = request.getContextPath();
if (contextPath.equals("")) {
  contextPath = "/t9";
}
T9Person loginPerson = (T9Person)session.getAttribute("LOGIN_USER");
String seqId = (String) request.getParameter("seqId");
String subject = (String) request.getParameter("subject");
String sendTime = (String) request.getParameter("sendTime");
String attachmentId = (String) request.getParameter("attachmentId");
String attachmentName = (String) request.getParameter("attachmentName");
String content = (String) request.getParameter("content");

String attachmentIds[] = attachmentId.split(",");
String attachmentNames[] = attachmentName.replace("*",",").split(",");

%>
<!doctype html>
<html>
<head>
<title><%=  T9SysProps.getString("productName") %></title>
<meta name="viewport" content="width=device-width" />
<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
<link rel="stylesheet" type="text/css" href="<%=contextPath %>/pda/style/list.css" />
</head>
<body>
<div id="list_top">
  <div class="list_top_left"><a class="ButtonBack" href="<%=contextPath %>/t9/pda/fileFolder/act/T9PdaFileFolderAct/search.act?P=<%= loginPerson.getSeqId()%>"></a></div>
  <span class="list_top_center">阅读文件</span>
</div>
<div id="list_main" class="list_main">
   <div class="read_title"><%=subject %> </div>
   <div class="read_time"><%=sendTime.substring(0,19) %></div>
<%
if(!T9Utility.isNullorEmpty(attachmentId) && !T9Utility.isNullorEmpty(attachmentName)){
  %><div class="read_attachment">附件：<%
      String sessionid = request.getSession().getId();
  for(int i = 0; i < attachmentNames.length; i++){
%>
   <a  target="_blank" href="<%=contextPath %>/t9/core/funcs/office/ntko/act/T9NtkoAct/downFile.act?module=file_folder&sessionid=<%=sessionid %>&attachmentId=<%=attachmentIds[i] %>&attachmentName=<%=attachmentNames[i] %>"><%=attachmentNames[i] %></a>&nbsp;&nbsp;&nbsp;&nbsp;
<%
  }
  %></div><%
}
%>
   <div class="read_content"><%=content %></div>
</div>
<div id="list_bottom">
  <div class="list_bottom_right"><a class="ButtonHome" href="<%=contextPath %>/pda/main.jsp?P=<%=loginPerson.getSeqId() %>"></a></div>
</div>
</body>
<script type="text/javascript" src="<%=contextPath %>/pda/js/logic.js"></script>
</html>
