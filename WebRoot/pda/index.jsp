<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="t9.core.global.T9SysProps" %>
<%@ page import="t9.core.util.T9Utility" %>
<%
String contextPath = request.getContextPath();
if (contextPath.equals("")) {
  contextPath = "/t9";
}
String username = (String) request.getAttribute("username") == null ? "" : (String) request.getAttribute("username");
String errorNo = (String) request.getAttribute("errorNo");
String errorMsg = (String) request.getAttribute("errorMsg");
%>
<!doctype html>
<html>
<head>
<title><%=  T9SysProps.getString("productName") %></title>
<meta name="viewport" content="width=device-width" />
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="stylesheet" type="text/css" href="<%=contextPath %>/pda/style/index.css" />
</head>
<body>
<div id="logo">
   <div id="product"></div>
   <div id="form">
      <form name="form" method="post" action="<%=contextPath %>/t9/pda/login/act/T9PdaLoginAct/doLogin.act">
      <div id="form_input">
         <div class="user"><input type="text" class="text" name="username" maxlength="20" value="<%=username %>" /></div>
         <div class="pwd"><input type="password" class="text" name="pwd" value="" /></div>
      </div>
      <div id="form_submit">
         <input type="image" src="<%=contextPath %>/pda/style/images/submit.png" class="submit" title="登录" value=" " />
      </div>
      </form>
   </div>
   <div id="msg">
<% 
    if(!T9Utility.isNullorEmpty(errorNo)){
      out.println(errorMsg);
    }
%>
   </div>
</div>
</body>
</html>
