<%@ page import = "java.util.*" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
	Map map = (Map)request.getAttribute("act.retdata");
	String SEAL_NAME = (String)map.get("SEAL_NAME");
	String SEAL_DATA = (String) map.get("SEAL_DATA");
%>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title></title>

</head>
<body>
		<div class="container">
		    <div class="tform tformshow">
		       <div class="read_detail read_detail_header">修改印章</div>
		       <div class="read_detail">
		          <em>印章名称：</em> <%=SEAL_NAME %>
		       </div>
		       <div class="read_detail">
		          <em>印章原始密码</em>
		          <em><input type="password" name="SEAL_PASS" /></em>
		       </div>
		       <div class="read_detail">
		          <em>印章新密码：</em>
		          <em><input type="password" name="SEAL_NEW_PASS" /></em>
		       </div>
		    </div>
		</div>
</body>
</html>
