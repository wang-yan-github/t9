<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<%@ include file="/core/inc/header.jsp" %>
<!DOCTYPE  html  PUBLIC  "-//W3C//DTD  HTML  4.01  Transitional//EN"  "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>任务查询</title>
<link rel="stylesheet" href="<%=cssPath%>/style.css">
<link rel="stylesheet" href="<%=cssPath %>/page.css"/>
<link rel="stylesheet" href = "<%=cssPath %>/cmp/Calendar.css">
<style>
</style>
<script type="text/javascript" src="<%=contextPath %>/core/js/prototype.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/datastructs.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/sys.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/smartclient.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/page.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/esb/server/user/js/util.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/Calendarfy.js"></script>
<script type="text/javascript">
function doInit(){
  query();
}
function query() {
  var url = contextPath +"/t9/core/esb/server/taskstatus/act/T9TaskStatusAct/getTask.act";
  var json = getJsonRs(url  , "guid=" + $('guid').value + "&userCode=" +  $('userCode').value);
  if (json.rtState == '0') {
    var msrg = json.rtMsrg;
    $('uploadCount').update (msrg);
    $('listUploadData').update("");
	var data = json.rtData;
	if (data.length > 0 ) {
	  for (var i = 0 ;i < data.length ;i++){
	    var da = data[i];
	    addRow(da , i);  
	  }
	  $('uploadDivHas').show();
	  $('noData1').hide();
	}else {
	  $('uploadDivHas').hide();
	  $('noData1').show();
	}
  }
}
function addRow(data , i) {
  var tr = new Element("tr" , {"class":"TableTr"});
  var color = "";
  var fontColor = "";
  if (i % 2 == 0) {
    color = "#F5F5DC";
  }
  tr.style.backgroundColor = color;
  tr.style.color = fontColor;
  
  $('listUploadData').appendChild(tr);
  
  var userCode = data.userCode;
  var guid = data.guid;
  var filePath =data.filePath;
  var uploadScale =data.uploadScale;
  
  var td = new Element("td");
  td.update(guid);
  tr.appendChild(td);
  td = new Element("td");
  td.update(userCode);
  tr.appendChild(td);
  td = new Element("td");
  td.update(filePath);
  tr.appendChild(td);
  td = new Element("td");
  td.update(uploadScale);
  tr.appendChild(td);
  //td = new Element("td");
  //td.update("");
  //tr.appendChild(td);
}
</script>
</head>

<body onload="doInit()">

<table border="0" width="" cellspacing="0" cellpadding="3" class="small">
  <tr>
    <td class="Big"><img src="<%=imgPath %>/system.gif" align="absmiddle"><span class="big3"> 发送队列</span><br>
    正在发送中的任务数：<span id="uploadCount"></span>
    </td>
  </tr>
</table>
<br>
<fieldset>
<form  name="queryForm" id="queryForm">
<table id="flowTable" border="0" width="100%"  class="TableList"  >
<tr class="TableLine2">
  <td align="left"> 
&nbsp;GUID：<input type="text" value="" id="guid" name="guid">&nbsp;发送方账号：<input type="text" value="" id="userCode" name="userCode"> <input onclick="query()" value="查询" type="button" class="SmallButton">
  </td> 
  </tr>
  </table></form>
</fieldset>

<br>
<div id="uploadDiv">
<div id="uploadDivHas"  style="display:none">
<table class="TableList pgTable" width="80%">
<tr class="TableTr"><td class="TableHeader" width="20%">GUID</td><td  class="TableHeader"  width="20%">发送方</td><td  class="TableHeader"  width="20%">文件名</td><td  class="TableHeader"  width="20%">状态</td>
<!-- 
<td  class="TableHeader"  width="20%">操作</td>

--></tr>
<tbody id="listUploadData"></tbody></table>
</div>
<div id="noData1"  style="display:none">
<table id="pgMsrgPanel" class="MessageBox" width="300" align="center">
  <tbody><tr>
    <td class="msg info">
        没有查找到数据！
    </td>
  </tr>
</tbody></table>
</div>

</div>
</body>
</html>
