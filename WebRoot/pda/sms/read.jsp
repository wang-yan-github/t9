<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="t9.core.global.T9SysProps" %>
<%@ page import="t9.core.funcs.person.data.T9Person" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Date" %>
<%
String contextPath = request.getContextPath();
if (contextPath.equals("")) {
  contextPath = "/t9";
}
T9Person loginPerson = (T9Person)session.getAttribute("LOGIN_USER");
String seqId = (String) request.getParameter("seqId");
String fromId = (String) request.getParameter("fromId");
String sendTime = (String) request.getParameter("sendTime");
String smsType = (String) request.getParameter("smsType");
String content = (String) request.getParameter("content");
String fromName = (String) request.getParameter("fromName");
String prs = "&fromName="+fromName;
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
  <div class="list_top_left"><a class="ButtonBack" href="<%=contextPath %>/t9/pda/sms/act/T9PdaSmsAct/doint.act?P=<%= loginPerson.getSeqId()%>"></a></div>
  <span class="list_top_center">阅读短信</span>
  <div class="list_top_right"><a class="ButtonA" href="<%=contextPath %>/pda/sms/edit.jsp?P=<%=loginPerson.getSeqId() %><%=prs %>">回复</a></div>
</div>
<div id="list_main" class="list_main">
   <div class="read_time"><%=fromName %> <%=sendTime.substring(0,16) %></div>
   <div class="read_content"><%=content %></div>
</div>
<div id="list_bottom">
  <div class="list_bottom_right"><a class="ButtonHome" href="<%=contextPath %>/pda/main.jsp?P=<%=loginPerson.getSeqId() %>"></a></div>
</div>
</body>
<script type="text/javascript" src="<%=contextPath %>/pda/js/logic.js"></script>
</html>
