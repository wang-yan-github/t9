<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="t9.core.global.T9SysProps" %>
<%@ page import="t9.core.funcs.person.data.T9Person" %>
<%@ page import="t9.core.util.T9Utility" %>
<%
String contextPath = request.getContextPath();
if (contextPath.equals("")) {
  contextPath = "/t9";
}
T9Person loginPerson = (T9Person)session.getAttribute("LOGIN_USER");
String subject = (String) request.getParameter("subject") == null ? "" : (String) request.getParameter("subject");
String fromName = (String) request.getParameter("fromName") == null ? "" : (String) request.getParameter("fromName");
if(!T9Utility.isNullorEmpty(fromName)){
  subject = "RE:" + subject;
}
%>
<!doctype html>
<html>
<head>
<title><%=  T9SysProps.getString("productName") %></title>
<meta name="viewport" content="width=device-width" />
<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
<link rel="stylesheet" type="text/css" href="<%=contextPath %>/pda/style/list.css" />
</head>
<body onLoad="document.form1.toName1.focus();">
<div id="list_top">
  <div class="list_top_left"><a class="ButtonBack" href="<%=contextPath %>/t9/pda/email/act/T9PdaEmailAct/search.act?P=<%= loginPerson.getSeqId()%>"></a></div>
  <span class="list_top_center">写新邮件</span>
  <div class="list_top_right"><a class="ButtonA" href="javascript:document.form1.submit();">发送</a></div>
</div>
<div id="list_main" class="list_main">
<form action="<%=contextPath %>/t9/pda/email/act/T9PdaEmailAct/sendMail.act?P=<%= loginPerson.getSeqId()%>"  method="post" name="form1">
   内部收信人姓名:<br>
   <input type="text" style="width:100%;" name="toName1" value="<%=fromName %>" /><br><br>
   外部收信人地址:<br>
   <input type="text" style="width:100%;" name="toName2" value="" /><br><br>
   邮件主题:<br>
   <input type="text" style="width:100%;" name="subject" value="<%=subject %>" /><br><br>
   邮件内容:<br>
   <textarea style="width:100%;" name="content" rows="5" wrap="on"></textarea>
   <input type="hidden" name="P" value="<%= loginPerson.getSeqId()%>" />
</form>
</div>
<div id="list_bottom">
  <div class="list_bottom_right"><a class="ButtonHome" href="<%=contextPath %>/pda/main.jsp?P=<%=loginPerson.getSeqId() %>"></a></div>
</div>
</body>
<script type="text/javascript" src="<%=contextPath %>/pda/js/logic.js"></script>
</html>