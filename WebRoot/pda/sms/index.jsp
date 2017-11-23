<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="t9.core.global.T9SysProps" %>
<%@ page import="t9.core.funcs.person.data.T9Person" %>
<%@ page import="t9.pda.sms.data.T9PdaSms" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Date" %>
<%
String contextPath = request.getContextPath();
if (contextPath.equals("")) {
  contextPath = "/t9";
}
T9Person loginPerson = (T9Person)session.getAttribute("LOGIN_USER");
List smses = (List) request.getAttribute("smses");
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
  <div class="list_top_left"><a class="ButtonBack" href="<%=contextPath %>/pda/main.jsp?P=<%=loginPerson.getSeqId() %>"></a></div>
  <span class="list_top_center">未确认短信</span>
  <div class="list_top_right"><a class="ButtonB" href="<%=contextPath %>/pda/sms/edit.jsp?P=<%=loginPerson.getSeqId() %>">写短信</a></div>
</div>
<div id="list_main" class="list_main">
<%
int count = smses.size();
for(int i = 0 ; i < count ; i++){
  if(i >= 20)
    break;
  T9PdaSms sms = (T9PdaSms)smses.get(i);
  int seqId = sms.getSeqId();
  int fromId = sms.getFromId();
  Date sendTime = sms.getSendTime();
  String smsType = sms.getSmsType();
  String content = sms.getContent();
  String fromName = sms.getUserName();
  String prs = "&seqId="+seqId+"&fromId="+fromId+"&sendTime="+sendTime.toString()+"&smsType="+smsType+"&content="+content+"&fromName="+fromName;
  String prs2 = "&fromName="+fromName;
%>
   <div class="list_item" onclick="window.location='<%=contextPath %>/pda/sms/read.jsp?P=<%=loginPerson.getSeqId() %><%=prs %>'">
      <div class="list_item_subject"><%=i+1 %>.<%=content %></div>
      <div class="list_item_time"><%=fromName %> <%=sendTime.toString().substring(0,16) %></div>
      <div class="list_item_arrow"></div>
      <div class="list_item_op">
         <a href="<%=contextPath %>/pda/sms/edit.jsp?P=<%=loginPerson.getSeqId() %><%=prs2 %>">回复</a>&nbsp;
         <a href="<%=contextPath %>/t9/pda/sms/act/T9PdaSmsAct/cancelSms.act?P=<%= loginPerson.getSeqId()%>&seqId=<%=seqId %>">已读</a>
      </div>
   </div>
<%
}
if(count==0)
  out.print("<div id=\"message\" class=\"message\">无未确认短信</div>");
%>
</div>
<div id="list_bottom">
  <div class="list_bottom_right"><a class="ButtonHome" href="<%=contextPath %>/pda/main.jsp?P=<%=loginPerson.getSeqId() %>"></a></div>
</div>
</body>
<script type="text/javascript" src="<%=contextPath %>/pda/js/logic.js"></script>
</html>
