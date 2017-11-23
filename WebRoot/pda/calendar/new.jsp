<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="t9.core.global.T9SysProps" %>
<%@ page import="t9.core.funcs.person.data.T9Person" %>
<%
String contextPath = request.getContextPath();
if (contextPath.equals("")) {
  contextPath = "/t9";
}
T9Person loginPerson = (T9Person)session.getAttribute("LOGIN_USER");
%>
<!doctype html>
<html>
<head>
<title><%=  T9SysProps.getString("productName") %></title>
<meta name="viewport" content="width=device-width" />
<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
<link rel="stylesheet" type="text/css" href="<%=contextPath %>/pda/style/list.css" />
</head>
<body onLoad="document.form1.calTime.focus();">
<div id="list_top">
  <div class="list_top_left"><a class="ButtonBack" href="<%=contextPath %>/t9/pda/calendar/act/T9PdaCalendarAct/doint.act?P=<%= loginPerson.getSeqId()%>"></a></div>
  <span class="list_top_center">新建今日日程</span>
  <div class="list_top_right"><a class="ButtonA" href="javascript:document.form1.submit();">提交</a></div>
</div>
<div id="list_main" class="list_main">
<form action="<%=contextPath %>/t9/pda/calendar/act/T9PdaCalendarAct/newCalendar.act"  method="post" name="form1">
  日程类型:<br>
 <select name="calType" class="BigSelect" style="width:100%;">
  <option value="1" selected>工作事务</option>
  <option value="2">个人事务</option>
 </select>
 <br>
 <br>
   起始时间:（样式如 09:35 ）<br>
   <input type="text" style="width:100%;" name="calTime" value="" /><br><br>  
   结束时间:（样式如 19:23 ）<br>
   <input type="text" style="width:100%;" name="endTime" value="" /><br><br>    
   日程内容:<br>
   <textarea style="width:100%;" name="content" rows="5" wrap="on"></textarea>
   <input type="hidden" name="P" value="<%=loginPerson.getSeqId() %>" />
</form>
</div>
<div id="list_bottom">
  <div class="list_bottom_right"><a class="ButtonHome" href="<%=contextPath %>/pda/main.jsp?P=<%=loginPerson.getSeqId() %>"></a></div>
</div>
</body>
<script type="text/javascript" src="<%=contextPath %>/pda/js/logic.js"></script>
</html>