<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header.jsp" %><html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>系统归档</title>
<link rel="stylesheet" href = "<%=cssPath%>/style.css">
<link rel="stylesheet" href="<%=cssPath%>/cmp/Calendar.css">
<script type="text/javascript" src="<%=contextPath %>/core/js/prototype.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/datastructs.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/sys.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/smartclient.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/cmp/Calendarfy.js"></script>
<script type="text/javascript">
function doInit(){
	var date2Parameters = {
		      inputId:'time',
		      property:{isHaveTime:true}
		      ,bindToBtn:'date'
		  };
		  new Calendar(date2Parameters);
		  var date2Parameters1 = {
			      inputId:'time1',
			      property:{isHaveTime:true}
			      ,bindToBtn:'date1'
			  };
			  new Calendar(date2Parameters1);
		  
		  var url = contextPath + "/t9/core/esb/server/task/T9DataRollAct/getDataCount.act";
		  var json = getJsonRs(url);
		  if(json.rtState == '0'){
			  var t1 = json.rtData.ESB_TRANSFER;
			  var t2 = json.rtData.ESB_TRANSFER_STATUS;
			  var t3 = json.rtData.ESB_SYS_MSG;
			 
			  var str = "ESB_TRANSFER："　+　t1 + "<br>";
			  str +=  "ESB_TRANSFER_STATUS："　+　t2 + "<br>";
			  str +=  "ESB_SYS_MSG："　+　t3 + "<br>";
			  
			  $('dbNameSelf2').update(str);
		  }
		  
}
function backUp(){
  var url = contextPath + "/t9/core/funcs/mysqldb/act/T9MySqldbAct/backUp.act";
  var rtJson = getJsonRs(url,$('backForm').serialize());
  if(rtJson.rtState == '0'){
	alert("备份成功");
   　}
}
function roll(){
　　var url = contextPath + "/t9/core/esb/server/task/T9DataRollAct/roll.act";
	  
　　var json = getJsonRs(url,$('rollForm').serialize());
 　　if(json.rtState == '0'){
	 alert("归档成功");
  　　}
}
function rollFile() {
	var url = contextPath + "/t9/core/esb/server/task/T9DataRollAct/deleteMsg.act";
　        var json = getJsonRs(url);
	if(json.rtState == '0'){
 	  alert("清理成功");
	}
}
</script>
</head>
<body onload="doInit();">
<table border="0" width="100%" cellspacing="0" cellpadding="3" class="small">
  <tr>
    <td><img src="<%=imgPath %>/notify_new.gif" align="middle"><span class="big3"> 系统归档---建议操作前备份数据库</span>&nbsp;&nbsp;
    </td>
  </tr>
</table>

<br>
<div align="center" class="Big1">
<form method="post" id="backForm" name="backForm">
  <table class="TableBlock" width="500" align="center">
  <tr>
      <td align="left" colspan=2 class="TableData">第一步：备份</td>
      </td>
    </tr>
    <tr>
      <td nowrap class="TableData">选择要备份的数据库：</td>
      <td class="TableData" id="dbNameSelf" align="left">
      <input type="checkbox" name="dbName" value="t9">t9<BR>
      </td>
    </tr>
    <tr>
      <td nowrap class="TableData">数据库热备份的保存路径：</td>
      <td class="TableData">
        <input type="text" name="backUpDir" size="50" class="BigInput" value="D:/t9/bak">
      </td>
    </tr>
    <tr>
      <td nowrap class="TableControl" colspan="2" align="center">
        <input type="button" value="立即备份" class="BigButton" onclick="backUp();">
      </td>
    </tr>
  </table>
</form>
</div>

<div align="center" class="Big1">
<form method="post" id="rollForm" name="rollForm">
  <table class="TableBlock" width="500" align="center">
  <tr>
      <td align="left" colspan=2 class="TableData">第二步：归档</td>
      </td>
    </tr>
    <tr>
      <td nowrap class="TableData">目前数据量：</td>
      <td class="TableData" id="dbNameSelf2" align="left">
      </td>
    </tr>
    <tr>
      <td nowrap class="TableData">归档时间：</td>
      <td class="TableData" align="left">
        <input type="text" name="time" id="time" class="BigInput" >
      <img id="date"
			src="<%=imgPath%>/calendar.gif" align="absMiddle"
			border="0" style="cursor: hand"> 
      </td>
    </tr>
    <tr>
      <td nowrap class="TableControl" colspan="2" align="center">
        <input type="button" value="立即归档" class="BigButton" onclick="roll();">
      </td>
    </tr>
  </table>
</form>
</div>
<div align="center" class="Big1">
<form method="post" id="rollFileForm" name="rollFileForm">
  <table class="TableBlock" width="500" align="center">
  <tr>
      <td align="left" colspan=2 class="TableData">第三步：清理ESB_SYS_MSG</td>
      </td>
    </tr>
    <tr>
      <td nowrap class="TableControl" colspan="2" align="center">
        <input type="button" value="立即清理" class="BigButton" onclick="rollFile();">
      </td>
    </tr>
  </table>
</form>
</div>
</body>
</html>