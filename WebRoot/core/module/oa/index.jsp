<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header.jsp" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>创建表</title>
<link rel="stylesheet" href = "<%=cssPath%>/style.css">
<script type="text/javascript" src="<%=contextPath %>/core/js/prototype.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/datastructs.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/sys.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/smartclient.js"></script>
<script type="text/javascript">
function createReportUnit(){
  var url = "<%=contextPath%>/t9/core/module/report/act/T9UnitAct/createUnit.act";
  var rtJson = getJsonRs(url);
  if(rtJson.rtState == "0"){
    alert("创建报表组织机构成功");
  }else{
    alert(rtJson.rtMsrg);
  }
}
function syncReportData(){
  var url = "<%=contextPath%>/t9/core/module/report/act/T9UnitAct/syncUnit.act";
  var rtJson = getJsonRs(url);
  if(rtJson.rtState == "0"){
    alert("同步报表组织机构成功");
  }else{
    alert(rtJson.rtMsrg);
  }
}
function createOaUnit(){
  var url = "<%=contextPath%>/t9/core/module/oa/act/T9UnitAct/createUnit.act";
  var rtJson = getJsonRs(url);
  if(rtJson.rtState == "0"){
    alert("创建oa组织机构成功");
  }else{
    alert(rtJson.rtMsrg);
  }
}
function syncOaData(){
  var url = "<%=contextPath%>/t9/core/module/oa/act/T9UnitAct/syncUnit.act";
  var rtJson = getJsonRs(url);
  if(rtJson.rtState == "0"){
    alert("同步oa组织机构成功");
  }else{
    alert(rtJson.rtMsrg);
  }
}
function createOaMenu(){
  var url = "<%=contextPath%>/t9/core/module/oa/act/T9UnitAct/createOaMenu.act";
  var rtJson = getJsonRs(url);
  if(rtJson.rtState == "0"){
    alert("创建oa菜单成功");
  }else{
    alert(rtJson.rtMsrg);
  }
}
function createReportMenu(){
  var url = "<%=contextPath%>/t9/core/module/report/act/T9UnitAct/createReportMenu.act";
  var rtJson = getJsonRs(url);
  if(rtJson.rtState == "0"){
    alert("创建报表菜单成功");
  }else{
    alert(rtJson.rtMsrg);
  }
}


</script>
</head>
<body>
<table border="0" width="100%" cellspacing="0" cellpadding="3" class="small">
  <tr>
    <td class="Big"><img src="<%=imgPath %>/meeting.gif" width="17" height="17"><span class="big3">同步数据</span><br>
    </td>
  </tr>
</table>

<br>
 <table class="TableBlock" width="500" align="center">
  <tr>
      <td align="left" colspan=2 class="TableData">第一步：创建表</td>
      </td>
    </tr>
    <tr>
      <td nowrap class="TableControl" colspan="2" align="center">
        <input type="button" value="创建报表组织机构" class="BigButton" onclick="createReportUnit();">&nbsp;&nbsp;
          <input type="button" value="创建oa组织机构" class="BigButton" onclick="createOaUnit();">
      </td>
    </tr>
  </table>
  <table class="TableBlock" width="500" align="center">
  <tr>
      <td align="left" colspan=2 class="TableData">第二步：同步数据</td>
      </td>
    </tr>
    <tr>
      <td nowrap class="TableControl" colspan="2" align="center">
        <input type="button" value="同步报表组织机构" class="BigButton" onclick="syncReportData();">&nbsp;&nbsp;
        <input type="button" value="同步oa组织机构" class="BigButton" onclick="syncOaData();">
      </td>
    </tr>
  </table>
  <table class="TableBlock" width="500" align="center">
  <tr>
      <td align="left" colspan=2 class="TableData">第三步：创建菜单</td>
      </td>
    </tr>
    <tr>
      <td nowrap class="TableControl" colspan="2" align="center">
        <input type="button" value="创建报表菜单" class="BigButton" onclick="createReportMenu();">&nbsp;&nbsp;
        <input type="button" value="创建crm和pmp菜单" class="BigButton" onclick="createOaMenu();">
      </td>
    </tr>
  </table>
</body>
</html>