<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="t9.core.global.T9SysProps" %>
<%@ page import="t9.core.funcs.person.data.T9Person" %>
<%@ page import="java.util.Date" %>
<%
String contextPath = request.getContextPath();
if (contextPath.equals("")) {
  contextPath = "/t9";
}
T9Person loginPerson = (T9Person)session.getAttribute("LOGIN_USER");
int flowId = Integer.parseInt(request.getParameter("flowId"));
int runId = Integer.parseInt(request.getParameter("runId"));
int prcsId = Integer.parseInt(request.getParameter("prcsId"));
int flowPrcs = Integer.parseInt(request.getParameter("flowPrcs"));
String js = (String) request.getAttribute("js");
String css = (String) request.getAttribute("css");
String formMsg = (String) request.getAttribute("formMsg");
String opFlag = (String) request.getAttribute("opFlag");
String feedback = (String) request.getAttribute("feedback");

String runName = (String) request.getAttribute("runName");
String attachmentId = (String) request.getAttribute("attachmentId") == null ? "" : (String) request.getAttribute("attachmentId");
String attachmentName = (String) request.getAttribute("attachmentName") == null ? "" : (String) request.getAttribute("attachmentName");
Date beginTime = (Date) request.getAttribute("beginTime");

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
<style>
table,td {
  border-collapse : collapse;
  border : 1px solid black;
  
}
<%= css%>
</style>
</head>
<body>
<div id="list_top">
  <div class="list_top_left"><a class="ButtonBack" href="<%=contextPath %>/t9/pda/workflow/act/T9PdaWorkflowAct/search.act?P=<%= loginPerson.getSeqId()%>"></a></div>
  <span class="list_top_center">工作流 </span>
</div>
<div id="list_main" class="list_main">
<b>名称/文号</b>:<%=runName %><br>
<b>流水号</b>:<%=runId %><br>
<b>流程开始</b>:<%=beginTime.toString().substring(0,19) %><br>
<b>附件</b>:
<%
String sessionid = request.getSession().getId();
for(int i = 0; i < attachmentNames.length; i++){
%>
  <a  target="_blank" href="<%=contextPath %>/t9/core/funcs/office/ntko/act/T9NtkoAct/downFile.act?module=workflow&sessionid=<%=sessionid %>&attachmentId=<%=attachmentIds[i] %>&attachmentName=<%=attachmentNames[i] %>"><%=attachmentNames[i] %></a>&nbsp;&nbsp;&nbsp;&nbsp;
<%
}
%>
<br>
<hr>
<form action="<%=contextPath %>/t9/pda/workflow/act/T9PdaWorkflowAct/saveFormData.act" method="post" name="form1">
<div id="form" style="margin-top:5px;margic-bottom:5px;padding-bottom:5px"><%=formMsg %></div>
<input type="hidden" name="flowId" value="<%=flowId %>">
<input type="hidden" name="runId" value="<%=runId %>">
<input type="hidden" name="prcsId" value="<%=prcsId %>">
<input type="hidden" name="flowPrcs" value="<%=flowPrcs %>">
<input type="hidden" name="isTurn" value="0">
</form>
<hr>
<%
if(!"1".equals(feedback)){
%>
<a class="ButtonA" href="<%=contextPath %>/t9/pda/workflow/act/T9PdaWorkflowAct/sign.act?P=<%=loginPerson.getSeqId() %>&flowId=<%=flowId %>&runId=<%=runId %>&prcsId=<%=prcsId %>&flowPrcs=<%=flowPrcs %>">会签</a>&nbsp;
<%
}
%>
<a class="ButtonA" href="javascript:document.form1.submit();">保存</a>&nbsp;
<%
if("1".equals(opFlag)){
%>
<a class="ButtonA" href="javascript:document.form1.isTurn.value ='1';document.form1.submit();">转交</a>&nbsp;
<%
}
%>
</div>
<div id="list_bottom">
    <div class="list_bottom_right"><a class="ButtonHome" href="<%=contextPath %>/pda/main.jsp?P=<%=loginPerson.getSeqId() %>"></a></div>
</div>
</body>
<script type="text/javascript" src="<%=contextPath %>/pda/js/logic.js"></script>
</html>