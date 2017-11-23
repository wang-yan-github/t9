<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="t9.core.global.T9SysProps" %>
<%@ page import="t9.core.funcs.person.data.T9Person" %>
<%@ page import="t9.pda.workflow.data.T9PdaSign" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Date" %>
<%@ page import="t9.core.util.T9Utility" %>
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
List signs = (List) request.getAttribute("signs");

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
  <div class="list_top_left"><a class="ButtonBack" href="<%=contextPath %>/t9/pda/workflow/act/T9PdaWorkflowAct/search.act?P=<%= loginPerson.getSeqId()%>"></a></div>
  <span class="list_top_center">会签</span>
  <div class="list_top_right"><a class="ButtonA" href="javascript:document.form1.submit();">保存</a></div>
</div>
<div id="list_main" class="list_main">
   <form action="<%=contextPath %>/t9/pda/workflow/act/T9PdaWorkflowAct/signSubmit.act"  method="post" name="form1">
   会签意见：<br />
   <textarea style="width:100%;" name="content" rows="3" wrap="on"></textarea>
   <input type="hidden" name="flowId" value="<%=flowId %>">
   <input type="hidden" name="runId" value="<%=runId %>">
   <input type="hidden" name="prcsId" value="<%=prcsId %>">
   <input type="hidden" name="flowPrcs" value="<%=flowPrcs %>">
   </form>
   <br>
<% 
int count = 0;
if (signs != null) {
  count = signs.size();
}
for(int i = 0 ; i < count ; i++){
  T9PdaSign sign = (T9PdaSign)signs.get(i);
  if (sign == null) 
    continue;
  int seqId = sign.getSeqId();
  int prcsId1 = sign.getPrcsId();
  int userId = sign.getUserId();
  String userName = sign.getUserName();
  String deptName = sign.getDeptName();
  String content = sign.getContent();
  String attachmentId = sign.getAttachmentId();
  String attachmentName = sign.getAttachmentName();
  Date editTime = sign.getEditTime();
  String signData = sign.getSignData();
  int flowPrcs1 = sign.getFlowPrcs();
  String prcsName = sign.getPrcsName() == null ? "" : sign.getPrcsName();
  %>
     <b>第<%=prcsId1 %>步 <%if(flowPrcs1 > 0) out.println(prcsName);%></b>
   <u title="部门：<%=deptName %>" style="cursor:pointer"><%=userName %></u></b>
   <i><%=editTime.toString().substring(0,19) %></i>
   <div class="content">
      <%=content %>
   </div>
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