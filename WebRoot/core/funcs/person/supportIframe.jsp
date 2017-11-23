<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header.jsp" %>
<%@ page import="java.text.*" %>
<%@ page import="java.util.*,t9.core.funcs.system.censorwords.data.T9CensorWords" %>
<%@ page import="t9.core.data.T9RequestDbConn, t9.core.global.T9BeanKeys" %>
<%
  String deptId = request.getParameter("deptId");
  if (deptId == null){
    deptId = "";
  }
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>新建用户</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<base target="_self">
<link rel="stylesheet" href="<%=cssPath %>/cmp/tab.css" type="text/css" />
<link rel="stylesheet" href = "<%=cssPath%>/cmp/Calendar.css">
<link rel="stylesheet" href = "<%=cssPath%>/style.css">
<link rel="stylesheet" href = "<%=cssPath%>/cmp/tree.css">
<script type="text/javascript" src="<%=contextPath %>/core/js/prototype.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/tab.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/datastructs.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/sys.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/smartclient.js"></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/orgselect.js" ></script>
<script type="text/javascript">
var deptId = "<%=deptId%>";
</script>
</head>
<body topmargin="5">
<iframe id="iframe" name="iframe" src="<%=contextPath %>/core/funcs/person/indutypersoninput.jsp?deptId=<%=deptId%>" align="center" width="680" height="470" scrolling="no" frameborder="0" onclick="window.close();"></iframe>
</body>
</html>