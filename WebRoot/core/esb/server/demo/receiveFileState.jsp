<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<%@ include file="/core/inc/header.jsp" %>
<!DOCTYPE  html  PUBLIC  "-//W3C//DTD  HTML  4.01  Transitional//EN"  "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>下载进度</title>
<link rel="stylesheet" href="<%=cssPath%>/style.css">
<link rel="stylesheet" href="<%=cssPath %>/page.css"/>
<style>
</style>
<script type="text/javascript" src="<%=contextPath%>/core/js/prototype.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/datastructs.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/sys.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/smartclient.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/page.js"></script>
<script type="text/javascript">
function getState() {
  if (!$('guid').value) {
    return ;
  }
  var url = contextPath + "/t9/core/esb/server/demo/act/ESBDemoAct/getState.act?guid=" + $('guid').value;
  var json = getJsonRs(url);
  if (json.rtState == '0') {
    var txt = json.rtMsrg;
    $("resultDiv").update(txt);
  }
}
</script>
</head>

<body>

<table border="0" width="" cellspacing="0" cellpadding="3" class="small">
  <tr>
    <td class="Big"><img src="<%=imgPath %>/system.gif" align="absmiddle"><span class="big3"> 下载进度</span><br>
    </td>
  </tr>
</table>
<br>
<div id="listDiv">
<fieldset>
<form  name="queryForm" id="queryForm">
<table id="flowTable" border="0" width="100%"  class="TableList"  >
<tr class="TableLine2">
  <td align="left"> 
  GUID：<input value="" name="guid" id="guid" type="text"><input  onclick="getState()" type="button" value="进度">
  </td> 
  </tr>
  </table>
</fieldset>
<div id="resultDiv"></div>
</div>
</body>
</html>
