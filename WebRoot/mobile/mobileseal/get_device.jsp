<%@ page import = "java.util.*" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
		Map map = (Map)request.getAttribute("act.retdata");

		
		int seqId = (Integer)map.get("seqId");
		String deviceType = (String)map.get("deviceType");
		String deviceTypeDesc = (String)map.get("deviceTypeDesc");
		String deviceName = (String)map.get("deviceName");
		String md5cheeck = (String)map.get("md5cheeck");
		String submitTime = (String)map.get("submitTime");
		

%>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title></title>

</head>
<body>
		<div class="container">
		   <div class="tform tformshow">
		      <div class="read_detail read_detail_header">申请记录</div>
		      <div class="read_detail">
		         <em>手机型号：</em> <%=deviceName %>
		      </div>
		      <div class="read_detail">
		         <em>申请时间：</em> <%=submitTime %>
		      </div>
		      <div class="read_detail">
		         <em>状态：</em> <%=deviceTypeDesc %>
		      </div>
		      <div class="read_detail">
		         <em>MD5摘要：</em>  <%=md5cheeck %>
		      </div>
   </div>
</div>
</body>
</html>
