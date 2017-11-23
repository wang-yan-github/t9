<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header.jsp"%>
<html>
<head>
<title>选择来文单位</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="<%=cssPath %>/page.css">
<link rel="stylesheet" href ="<%=cssPath %>/style.css">
<script type="text/javascript" src="<%=contextPath %>/core/js/prototype.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/datastructs.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/sys.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/smartclient.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/page.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/funcs/doc/receive/js/fromDept.js"></script>
</head>
<body onload="doInit()">
<div id="form" style="display:;padding:4px" align=center>
<fieldset >
  <legend class="small">
      <b>快速查询</b>
  </legend>
<form id="form2" name="form2">
<table border="0" width="100%" align="center">
         <tr id="fiTr"><td align=left>
         来文单位：<input name="fromDeptName" id="fromDeptName" type=text value="">&nbsp;<input type=button class="SmallButton" value="查询" onclick="doQuery()">&nbsp;<a href="javascript:addDept()">添加到常用来文单位</a>
         </td></tr>
     </table>
</form>
</fieldset>
</div>
<div id="container" style="overflow:auto;padding:4px;height:550px;width:790px"></div>
<div id="msrg" style="display:none"></div>
</body>
</html>
