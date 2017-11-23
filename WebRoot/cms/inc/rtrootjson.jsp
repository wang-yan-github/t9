<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="t9.core.global.T9ActionKeys"%>
<%
String rtState = (String)request.getAttribute(T9ActionKeys.RET_STATE);
String rtMsrg = (String)request.getAttribute(T9ActionKeys.RET_MSRG);
String rtData = (String)request.getAttribute(T9ActionKeys.RET_DATA);
if (rtState == null) {
  rtState = "0";
}
if (rtMsrg == null) {
  rtMsrg = "";
}else {
  rtMsrg = rtMsrg.replace("\"", "\\\"").replace("\r", "").replace("\n", "");
}
if (rtData == null) {
  rtData = "\"\"";
}

%>
{root:<%=rtData %>}