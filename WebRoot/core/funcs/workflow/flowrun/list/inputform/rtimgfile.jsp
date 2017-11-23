<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header.jsp" %>
<%@page import="t9.core.global.T9ActionKeys"%>
<%
  String returnState = (String)request.getAttribute(
      T9ActionKeys.RET_STATE);
  String rtMsrg = (String)request.getAttribute(
    T9ActionKeys.RET_MSRG);  
  String retData = (String)request.getAttribute(
      T9ActionKeys.RET_DATA);
  if (retData == null) {
    retData = "''";
  }
  //retData = retData.replace("\\", "/").replaceAll("\"", "\\\\\"");
%>
<script>
var returnState = "<%=returnState%>";
/**
 * 处理页面加载
 */
function doInit() {
  parent.handleImgUpload(returnState, "<%=rtMsrg %>", <%=retData%>);
}
</script>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title></title>
</head>
<body onload="doInit()">
</body>
</html>