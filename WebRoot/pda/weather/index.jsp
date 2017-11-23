<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="t9.core.global.T9SysProps" %>
<%@ page import="t9.core.util.T9Utility" %>
<%@ page import="t9.core.funcs.person.data.T9Person" %>
<%
String contextPath = request.getContextPath();
if (contextPath.equals("")) {
  contextPath = "/t9";
}
T9Person loginPerson = (T9Person)session.getAttribute("LOGIN_USER");
String weatherCity = (String) request.getAttribute("weatherCity") == null ? "" : (String) request.getAttribute("weatherCity");
String city = (String) request.getAttribute("city");
String weather = (String) request.getAttribute("weather");
String temperature = (String) request.getAttribute("temperature");
String wind = (String) request.getAttribute("wind");
String errorMsg = (String) request.getAttribute("errorMsg") == null ? "" : (String) request.getAttribute("errorMsg");
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
  <div class="list_top_left"><a class="ButtonBack" href="<%=contextPath %>/pda/main.jsp?P=<%= loginPerson.getSeqId()%>"></a></div>
  <span class="list_top_center">天气预报</span>
  <div class="list_top_right"><a class="ButtonB" href="javascript:document.form1.submit();">查询</a></div>
</div>
<div id="list_main" class="list_main">
   <form method="get" action="<%=contextPath %>/t9/pda/weather/act/T9PdaWeatherAct/queryWeather.act" name="form1">
      请输入查询城市名称：<br>
      <input name="weatherCity" size="20" value="<%=weatherCity %>">
      <input type="hidden" name="P" value="<%= loginPerson.getSeqId()%>">
   </form>
   <%
    if(!T9Utility.isNullorEmpty(weather)){
      out.println("<b>"+city+"</b><br>");
      out.println(weather+"<br>");
      out.println(temperature+"<br>");
      out.println(wind);
    }
   if(!T9Utility.isNullorEmpty(errorMsg)){
     out.println(errorMsg+"<br>");
   }
   %>
</div>
<div id="list_bottom">
  <div class="list_bottom_right"><a class="ButtonHome" href="<%=contextPath %>/pda/main.jsp?P=<%= loginPerson.getSeqId()%>"></a></div>
</div>
</body>
<script type="text/javascript" src="<%=contextPath %>/pda/js/logic.js"></script>
</html>