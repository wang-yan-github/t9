<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header.jsp" %>
<html>
<head>
<meta  http-equiv="Content-Type"  content="text/html;  charset=UTF-8">
<title>Insert  title  here</title>
<link  rel="stylesheet"  href  ="<%=cssPath  %>/style.css">
<script  type="text/Javascript"  src="<%=contextPath%>/core/js/prototype.js"  ></script>
<script  type="text/Javascript"  src="<%=contextPath%>/core/js/datastructs.js"  ></script>
<script  type="text/Javascript"  src="<%=contextPath%>/core/js/sys.js"  ></script>
<script  type="text/Javascript"  src="<%=contextPath%>/core/js/smartclient.js"  ></script>
<script  type="text/javascript">

function doInit() {
  
}

function downloadT9Log() {
  download("T9");
}

function downloadIMLog() {
  download("IM");
}

function downloadWSLog() {
  download("webserver");
}
function downloadAllLog() {
  download("all");
}

function download(type) {
  var url = contextPath + "/t9/core/funcs/system/resManage/act/T9LogAct/download.act";
  $("form1").action = url;
  $("type").value = type;
  $("form1").submit();
}
</script>
</head>
<body onLoad="doInit()">
<table border="0" width="90%" cellspacing="0" cellpadding="3" class="small">
  <tr>
    <td class="Big"><img src="<%=imgPath%>/system.gif" align="abstop"/><span class="big3">日志文件下载</span><br>
    </td>
  </tr>
</table>
<table class="TableTop" width="400" align="center">
  <tr>
    <td class="left">
    </td>
    <td class="center">
    日志文件下载
    </td>
    <td class="right">
    </td>
  </tr>
</table>
<table class="TableBlock no-top-border" width="400" align="center">
  <tr>
    <td nowrap class="TableData" width=60%>T9日志</td>
    <td class="TableData" align="center">
    <input onclick="downloadT9Log()" type="button" class="BigButtonA" value="下载"/>
    </td>
  </tr>
  <tr>
    <td nowrap class="TableData">Web服务日志&nbsp;(目前只支持Tomcat)</td>
    <td class="TableData" align="center">
    <input onclick="downloadWSLog()" type="button" class="BigButtonA" value="下载"/>
    </td>
  </tr>
  <tr>
    <td nowrap class="TableData">IM服务日志</td>
    <td class="TableData" align="center">
    <input onclick="downloadIMLog()" type="button" class="BigButtonA" value="下载"/>
    </td>
  </tr>
  <tr>
    <td nowrap class="TableData">所有日志</td>
    <td class="TableData" align="center">
    <input onclick="downloadAllLog()" type="button" class="BigButtonA" value="下载"/>
    </td>
  </tr>
</table>
<form id="form1">
<input type="hidden" id="type" name="type"/>
</form>
</body>
</html>