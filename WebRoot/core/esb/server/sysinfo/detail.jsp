<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header.jsp" %> 
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><%=t9SysInfo %></title>
<link rel="stylesheet"  href="<%=cssPath%>/style.css">
<script type="text/Javascript"  src="<%=contextPath%>/core/js/datastructs.js"></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/sys.js"></script>
<script type="text/Javascript"  src="<%=contextPath%>/core/js/prototype.js"></script>
<script type="text/Javascript"  src="<%=contextPath%>/core/js/smartclient.js"></script>
<script type="text/javascript">
function doInit() {
  var url = '<%=contextPath%>/t9/core/funcs/system/info/act/T9InfoAct/detailRegInfo.act';
  var rtJson = getJsonRs(url);
  if (rtJson.rtState == "0") {
    var data = rtJson.rtData;
    $('name').innerHTML = data['sysname.t9'];
    $('time').innerHTML = data['install.time.t9'];
    $('unit').innerHTML = data['regist.org.t9'];
    $('machineCode').innerHTML = data['machineCode.t9'];
    $('serialId').innerHTML = data['serial.id.t9'];
    $('userCount').innerHTML = data['user.cnt.t9'];
  }
}
</script>
</head>

<body onload="doInit()">
<table border="0" width="100%" cellspacing="0" cellpadding="3" class="small">
  <tr>

    <td class="Big"><img src="<%=imgPath %>/sys_config.gif" WIDTH="22" HEIGHT="20" align="absmiddle"><span class="big3">查看注册文件</span>
    </td>
  </tr>
</table>

<table border="0" width="600" align="center" cellspacing="0" cellpadding="1" class="TableBlock">
  <tr class="TableLine2">
    <td nowrap width="150"><b>&nbsp;软件名称：</b></td>
    <td nowrap id="name"></td>
  </tr>
  <tr class="TableLine2">
    <td nowrap width="150"><b>&nbsp;安装时间：</b></td>
    <td nowrap id="time"></td>
  </tr>
  <tr class="TableLine2">
    <td nowrap width="150"><b>&nbsp;机器码：</b></td>
    <td nowrap id="machineCode"></td>
  </tr>
  <tr class="TableLine2">
    <td nowrap width="150"><b>&nbsp;用户单位：</b></td>
    <td nowrap id="unit"></td>
  </tr>
  <tr class="TableLine2">
    <td nowrap width="150"><b>&nbsp;系统标识码：</b></td>
    <td nowrap id="serialId"></td>
  </tr>

  <tr class="TableLine2">
    <td nowrap width="150"><b>&nbsp;用户数：</b></td>
    <td nowrap id="userCount"></td>
  </tr>
</table>

<br>
<center><input type="button" class="BigButton" value="返回" onclick="location.href='<%=contextPath %>/core/esb/server/sysinfo/index.jsp'"></center>
</body>
</html>