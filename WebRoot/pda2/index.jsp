<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="t9.core.global.T9SysProps" %>
<%@ page import="t9.core.util.T9Utility" %>
<%
String contextPath = request.getContextPath();
if (contextPath.equals("")) {
  contextPath = "/t9";
}
String ieTitle  = T9SysProps.getString("productName");

Cookie[] myCookie = request.getCookies();
String userName = "";
if (myCookie != null){
  for(int i = 0; i < myCookie.length ; i++){
    Cookie tmp= myCookie[i];
    if(tmp != null && tmp.getName().equals("USER_NAME_COOKIE")){
      userName = tmp.getValue();
      break;
    }
  }
}
if (T9Utility.isNullorEmpty(userName)) {
  userName = "";
}
String focus = "USERNAME";
if (!"".equals(userName)) {
  focus = "PASSWORD";
}
String errorNo = request.getParameter("errorNo");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title><%=ieTitle %></title>
<meta name="viewport" content="width=device-width, minimum-scale=1, maximum-scale=1" />
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link rel="stylesheet" type="text/css" href="<%=contextPath %>/pda2/pad/style/index.css?56465" />
</head>
<body>
<div id="logo">
   <div id="form">
		<div id='cloud'></div>
		   <div id="msg">
			<%
			   if("1".equals(errorNo))
			      out.print("<div>用户名或密码错误或禁止该用户登录请重新登录<br /></div>");
					 %>
				</div>
		
      <form name="form1" method="post" action="<%=contextPath %>/pda2/login/act/T9PdaLoginAct/doLogin.act">
      <div id="form_input">
         <div class="user"><input type="text" class="text" name="username" maxlength="20" value="<%=userName %>" /></div>
         <div class="pwd"><input type="password" class="text" name="pwd" value="" /></div>
      </div>
      <div id="form_submit">
         <input type="submit" class="submit" title="登录" value=" "/>
      </div>
		<input type="hidden" name="SaveDevType" value="" />
		<input type="hidden" name="DevType" value="pad" />
      </form>
   </div>

</div>
</body>
</html>
