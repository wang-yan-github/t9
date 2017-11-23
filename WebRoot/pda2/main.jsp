<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="t9.core.global.T9SysProps" %>
<%@ page import="t9.core.funcs.person.data.T9Person" %>
<%
String contextPath = request.getContextPath();
if (contextPath.equals("")) {
  contextPath = "/t9";
}
T9Person loginPerson = (T9Person)session.getAttribute("LOGIN_USER");
String P_VER = (String)session.getAttribute("P_VER");
%>
<!doctype html>
<html>
<head>
<title><%=  T9SysProps.getString("productName") %></title>
<meta name="viewport" content="width=device-width" />
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="stylesheet" type="text/css" href="<%=contextPath %>/pda/style/main.css"/>
<style type="text/css">
html, body {
	height: 100%;
}
</style>
<script type="text/javascript">
function doInit() {
  doHeight();
}
window.onresize = doHeight;
function doHeight() {
  var height = (document.body || document.documentELement).scrollHeight;
	var ctx = document.getElementById("main");
	var borderHeight = document.getElementById("main_top").scrollHeight + 11;
	var bottom = document.getElementById("main_bottom");
	borderHeight += (bottom ? bottom.scrollHeight : 0);
	if (ctx.scrollHeight < (height - borderHeight)) {
	  ctx.style.height = height - borderHeight + "px";
	}
}
function backTo(){
  var url="message:backtomain";   
  document.location = url;   
}
</script>
</head>
<body onload="doInit();">
<div id="main_top" class="product" style="font-size:24px;"></div>
<div id="main">
<table>
<tr>
  <td><a href="<%=contextPath %>/t9/pda/sms/act/T9PdaSmsAct/doint.act?P=<%= loginPerson.getSeqId()%>"><img src="<%=contextPath %>/pda/style/images/icon/sms.jpg" /><div>内部短信</div></a></td>
  <td><a href="<%=contextPath %>/t9/pda/email/act/T9PdaEmailAct/search.act?P=<%= loginPerson.getSeqId()%>"><img src="<%=contextPath %>/pda/style/images/icon/email.jpg" /><div>内部邮件</div></a></td>
  <td><a href="<%=contextPath %>/t9/pda/notify/act/T9PdaNotifyAct/search.act?P=<%= loginPerson.getSeqId()%>"><img src="<%=contextPath %>/pda/style/images/icon/notify.jpg" /><div>公告通知</div></a></td>
  <td><a href="<%=contextPath %>/t9/pda/news/act/T9PdaNewsAct/search.act?P=<%= loginPerson.getSeqId()%>"><img src="<%=contextPath %>/pda/style/images/icon/news.jpg" /><div>内部新闻</div></a></td>
</tr>
<tr>
  <td><a href="<%=contextPath %>/t9/pda/calendar/act/T9PdaCalendarAct/doint.act?P=<%= loginPerson.getSeqId()%>"><img src="<%=contextPath %>/pda/style/images/icon/calendar.jpg" /><div>今日日程</div></a></td>
  <td><a href="<%=contextPath %>/t9/pda/diary/act/T9PdaDiaryAct/doint.act?P=<%= loginPerson.getSeqId()%>"><img src="<%=contextPath %>/pda/style/images/icon/diary.jpg" /><div>工作日志</div></a></td>
  <td><a href="<%=contextPath %>/t9/pda/fileFolder/act/T9PdaFileFolderAct/search.act?P=<%= loginPerson.getSeqId()%>"><img src="<%=contextPath %>/pda/style/images/icon/folder.jpg" /><div>我的文件</div></a></td>
  <td><a href="<%=contextPath %>/t9/pda/workflow/act/T9PdaWorkflowAct/search.act?P=<%= loginPerson.getSeqId()%>"><img src="<%=contextPath %>/pda/style/images/icon/workflow.jpg" /><div>工作流</div></a></td>
</tr>
<tr>
  <td><a href="<%=contextPath %>/pda/userInfo?P=<%= loginPerson.getSeqId()%>"><img src="<%=contextPath %>/pda/style/images/icon/query.jpg" /><div>人员查询</div></a></td>
  <td><a href="<%=contextPath %>/pda/address?P=<%= loginPerson.getSeqId()%>"><img src="<%=contextPath %>/pda/style/images/icon/address.jpg" /><div>通讯簿</div></a></td>
  <td><a href="<%=contextPath %>/pda/telNo?P=<%= loginPerson.getSeqId()%>"><img src="<%=contextPath %>/pda/style/images/icon/zipcode.jpg" /><div>区号邮编</div></a></td>
  <td><a href="<%=contextPath %>/pda/weather?P=<%= loginPerson.getSeqId()%>"><img src="<%=contextPath %>/pda/style/images/icon/weather.jpg" /><div>天气预报</div></a></td>
</tr>

</table>
</div>
<div id="main_bottom">
	<div class="user_name">用户：<%=loginPerson.getUserName() %></div>
   <%if("5".equals(P_VER)){ %>
   	 <a class="relogin" href="javascript:void(0);" onclick="backTo();"><img src="<%=contextPath %>/pda/style/images/relogin.jpg" alt="重新登录" /></a>
   <%} 
     else if(!"6".equals(P_VER)){ %>
   	 <a class="relogin" href="<%=contextPath %>/pda/index.jsp"><img src="<%=contextPath %>/pda/style/images/relogin.jpg" alt="重新登录" /></a>
   <%} %>
</div>
</body>
</html>
