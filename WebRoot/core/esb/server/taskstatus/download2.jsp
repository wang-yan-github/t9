<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<%@ include file="/core/inc/header.jsp" %>
<!DOCTYPE  html  PUBLIC  "-//W3C//DTD  HTML  4.01  Transitional//EN"  "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>接收任务统计</title>
<link rel="stylesheet" href="<%=cssPath%>/style.css">
<link rel="stylesheet" href="<%=cssPath %>/page.css"/>
<link rel="stylesheet" href = "<%=cssPath %>/cmp/Calendar.css">
<style>
</style>
<script type="text/javascript" src="<%=contextPath%>/core/js/prototype.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/datastructs.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/sys.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/smartclient.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/page.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/esb/server/user/js/util.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/Calendarfy.js"></script>
<script type="text/javascript">
var pageMgr;
function doInit(){
  var beginParameters = {
      inputId:'startTime',
      property:{isHaveTime:true}
      ,bindToBtn:'beginDateImg'
  };
  new Calendar(beginParameters);
  var endParameters = {
      inputId:'endTime',
      property:{isHaveTime:true}
      ,bindToBtn:'endDateImg'
  };
  new Calendar(endParameters);
  
}
function getParam(){
  queryParam = $("queryForm").serialize();
  return queryParam;
}

function ipAddress() {
  return '';
}

//清空时间组件
function empty_date(){
  $("startTime").value="";
  $("endTime").value="";
}

function queryTypeChange(value) {
  if  (value == '1') {
    $('dateRange').show();
  } else {
    $('dateRange').hide();
  }
}
function statistics() {
  var pars = Form.serialize($('queryForm'));
  var url = contextPath + "/t9/core/esb/server/taskstatus/act/T9TaskStatusAct/statisticsDown.act";
  var json = getJsonRs(url , pars);
  if (json.rtState == '0') {
    var datas = json.rtData;
    if (datas.length> 0) {
      $('listData').update("");
      var count = 0 ;
      var count2 = 0 ;
      for (var i = 0 ;i < datas.length ;i++) {
      	var data = datas[i];
      	if ($('type').value == '2') {
      	  addData2(data , i );
      	} else {
      	  addData(data , i );
      	}
      	count += data.count;
      	count2 += data.count2;
      }
      $('countTd').update(count);
      $('countTd2').update(getSize(count2));
      $('listDiv').show();
      $('noData').hide();
    } else {
      $('listDiv').hide();
      $('noData').show();
    }
  }
}
function getSize(size) {
  var s = Math.floor((size / 1024 ) / 1024  ) + "M";
  return s;
}
function addData(data , i) {
  var tr = new Element("tr" , {"class":"TableTr"});
  var color = "";
  var fontColor = "";
  if (i % 2 == 0) {
	color = "#F5F5DC";
	//fontColor = "#fff";
  }
  tr.style.backgroundColor = color;
  tr.style.color = fontColor;
  
  $('listData').appendChild(tr);
  var status = $('downType').value;
  
  var rowspan = 4;
  
  if (status) {
    rowspan = 1;
  } 
  var userCode = data.userCode;
  var userName = data.userName;
  var td = new Element("td");
  td.update(userCode);
  td.rowSpan = rowspan;
  tr.appendChild(td);
  td = new Element("td");
  td.update(userName);
  td.rowSpan = rowspan;
  tr.appendChild(td);
  
  var statusValue = data.STATUS0;
  var statusValue2 = data.STATUS20;
  var statusTitle = "等侍接收";
  if (status) {
	if (status == 1) {
	  statusValue = data.STATUS1;
	  statusValue2 = data.STATUS21;
	  
	  statusTitle = "接收中";
	} else if (status == 2) {
	  statusValue = data.STATUS2;
	  statusValue2 = data.STATUS22;
	  
	  statusTitle = "接收完毕";
	} else if (status == 4) {
	  statusValue = data.STATUS4;
	  statusValue2 = data.STATUS24;
	  
	  statusTitle = "接收失败";
	}   
  } 
  td = new Element("td");
  td.update(statusTitle);
  tr.appendChild(td);
  td = new Element("td");
  td.update(statusValue);
  tr.appendChild(td);
  td = new Element("td");
  td.update(data.count);
  td.rowSpan = rowspan;
  tr.appendChild(td);
  
  td = new Element("td");
  td.update(getSize(statusValue2));
  tr.appendChild(td);
  td = new Element("td");
  td.update(getSize(data.count2));
  td.rowSpan = rowspan;
  tr.appendChild(td);
  
  if (!status)  {
    var status2 = data.STATUS2;
    var status1 = data.STATUS1;
    var status4 = data.STATUS4;
    
    var status22 = data.STATUS22;
    var status21 = data.STATUS21;
    var status24 = data.STATUS24;
    
    getTr("接收中" , status1  , status21, color , fontColor);
    getTr("接收完毕" , status2  , status22, color , fontColor);
    getTr("接收失败" , status4 , status24, color , fontColor);
  }
}
function addData2(data , i) {
  var tr = new Element("tr" , {"class":"TableTr"});
  var color = "";
  var fontColor = "";
  if (i % 2 == 0) {
	color = "#F5F5DC";
  }
  tr.style.backgroundColor = color;
  tr.style.color = fontColor;
  
  $('listData').appendChild(tr);
  var status = $('sizeType').value;
  
  var rowspan = 2;
  
  if (status) {
    rowspan = 1;
  } 
  var userCode = data.userCode;
  var userName = data.userName;
  var td = new Element("td");
  td.update(userCode);
  td.rowSpan = rowspan;
  tr.appendChild(td);
  td = new Element("td");
  td.update(userName);
  td.rowSpan = rowspan;
  tr.appendChild(td);
  
  var statusValue = data.STATUS1;
  var statusValue2 = data.STATUS21;
  
  var statusTitle = "小于2M";
  if (status) {
	if (status == 2) {
	  statusValue = data.STATUS2;
	  statusValue2 = data.STATUS22;
	  statusTitle = "大于2M";
	} 
  } 
  td = new Element("td");
  td.update(statusTitle);
  tr.appendChild(td);
  td = new Element("td");
  td.update(statusValue);
  tr.appendChild(td);
  td = new Element("td");
  td.update(data.count);
  td.rowSpan = rowspan;
  tr.appendChild(td);
  
  td = new Element("td");
  td.update(getSize(statusValue2));
  tr.appendChild(td);
  
  td = new Element("td");
  td.update(getSize(data.count2));
  td.rowSpan = rowspan;
  tr.appendChild(td);
  
  if (!status)  {
    var status2 = data.STATUS2;
    var status3 = data.STATUS3;
    var status4 = data.STATUS4;
    var status5 = data.STATUS5;
    
    var status22 = data.STATUS22;
    var status23 = data.STATUS23;
    var status24 = data.STATUS24;
    var status25 = data.STATUS25;
    
    getTr("大于2M" , status2 , status22, color , fontColor);
  }
}
function addData3(data , i) {
  var tr = new Element("tr" , {"class":"TableTr"});
  var color = "";
  var fontColor = "";
  if (i % 2 == 0) {
	color = "#F5F5DC";
  }
  tr.style.backgroundColor = color;
  tr.style.color = fontColor;
  
  $('listData').appendChild(tr);
  var status = $('sizeType').value;
  
  var rowspan = 5;
  
  if (status) {
    rowspan = 1;
  } 
  var userCode = data.userCode;
  var userName = data.userName;
  var td = new Element("td");
  td.update(userCode);
  td.rowSpan = rowspan;
  tr.appendChild(td);
  td = new Element("td");
  td.update(userName);
  td.rowSpan = rowspan;
  tr.appendChild(td);
  
  var statusValue = data.STATUS1;
  var statusValue2 = data.STATUS21;
  
  var statusTitle = "0M到2M";
  if (status) {
	if (status == 2) {
	  statusValue = data.STATUS2;
	  statusValue2 = data.STATUS22;
	  statusTitle = "2M到3M";
	} else if (status == 3) {
	  statusValue = data.STATUS3;
	  statusValue2 = data.STATUS23;
	  statusTitle = "3M到4M";
	} else if (status == 4) {
	  statusValue = data.STATUS4;
	  statusValue2 = data.STATUS24;
	  statusTitle = "4M到5M";
	} else if (status == 5) {
	  statusValue = data.STATUS5;
	  statusValue2 = data.STATUS25;
	  statusTitle = "大于5M";
	}   
  } 
  td = new Element("td");
  td.update(statusTitle);
  tr.appendChild(td);
  td = new Element("td");
  td.update(statusValue);
  tr.appendChild(td);
  td = new Element("td");
  td.update(data.count);
  td.rowSpan = rowspan;
  tr.appendChild(td);
  
  td = new Element("td");
  td.update(getSize(statusValue2));
  tr.appendChild(td);
  
  td = new Element("td");
  td.update(getSize(data.count2));
  td.rowSpan = rowspan;
  tr.appendChild(td);
  
  if (!status)  {
    var status2 = data.STATUS2;
    var status3 = data.STATUS3;
    var status4 = data.STATUS4;
    var status5 = data.STATUS5;
    
    var status22 = data.STATUS22;
    var status23 = data.STATUS23;
    var status24 = data.STATUS24;
    var status25 = data.STATUS25;
    
    getTr("2M到3M" , status2 , status22, color , fontColor);
    getTr("3M到4M" , status3 , status23, color , fontColor);
    getTr("4M到5M" , status4 , status24, color , fontColor);
    getTr("大于5M" , status5 , status25, color , fontColor);
  }
}
function getTr(title , value ,value2, color , fontColor) {
  var tr = new Element("tr" , {"class":"TableTr"});
  tr.style.backgroundColor = color;
  tr.style.color = fontColor;
  
  $('listData').appendChild(tr);
  var td = new Element("td");
  td.update(title);
  tr.appendChild(td);
  td = new Element("td");
  td.update(value);
  tr.appendChild(td);
  td = new Element("td");
  td.update(getSize(value2));
  tr.appendChild(td);
}
function ext(){
  var pars = Form.serialize($('queryForm'));
  location =  "<%=contextPath%>/t9/core/esb/server/taskstatus/act/T9TaskStatusAct/expUpload.act?"+pars;
}
function changeSpan() {
  if ($('typeSpan').style.display == 'none') {
    $('typeSpan').show();
    $('typeSpan2').hide();
    $('type').value = "1";
  } else {
    $('typeSpan2').show();
    $('typeSpan').hide();
    $('type').value = "2";
  }
}
</script>
</head>

<body onload="doInit()">

<table border="0" width="" cellspacing="0" cellpadding="3" class="small">
  <tr>
    <td class="Big"><img src="<%=imgPath %>/system.gif" align="absmiddle"><span class="big3"> 接收任务统计</span><br>
    </td>
  </tr>
</table>
<br>
<fieldset>
<form  name="queryForm" id="queryForm">
<table id="flowTable" border="0" width="100%"  class="TableList"  >
<tr class="TableLine2">
  <td align="left"> 
   <span id="typeSpan" style="display:none">
&nbsp;
接收状态：
  <select  id="downType" name="downType" >
  <option value="">所有</option>
  <option value="0">等侍接收</option>
  <option value="1">接收中</option>
  <option value="2">接收完毕</option>
   <option value="4">接收失败</option>
  </select>
  </span>
  <span id="typeSpan2" >
  &nbsp;
接收大小：
  <select  id="sizeType" name="sizeType" >
  <option value="">所有</option>
  <option value="1">小于2M</option>
  <option value="2">大于2M</option>
  
  <!-- 
  <option value="1">0到2M</option>
  <option value="2">2到3M</option>
  <option value="3">3到4M</option>
  <option value="4">4到5M</option>
  <option value="5">大于5M</option> -->
  </select></span> <input type="button" value="切换查询类型" onclick="changeSpan()"/>
  <input type="hidden" id="type"  name="type" value="2"/>
  
  
   &nbsp;接收方账号：<input type="text" value="" id="recUserCode" name="recUserCode"> 
  </td> 
  </tr>
  <tr class="TableLine1">
  <td align="left">&nbsp;类型：
  <select  id="queryType" name="queryType" onchange="queryTypeChange(this.value)">
  <option value="0">当天</option>
  <option value="1">时间范围</option>
  <option value="2">三天内</option>
  <option value="3">本周</option>
  <option value="4">本月</option>
  </select>
  <span id="dateRange" style="display:none">
 发送时间： 从
  <input type="text" id="startTime" name="startTime" size="20" maxlength="19" readonly class="BigStatic" value="">
  <img id="beginDateImg" src="<%=imgPath %>/calendar.gif" align="absMiddle" border="0" style="cursor:pointer">
      到
  <input type="text" id="endTime" name="endTime" size="20" maxlength="19"  readonly class="BigStatic">
  <img id="endDateImg" src="<%=imgPath %>/calendar.gif" align="absMiddle" border="0" style="cursor:pointer">
  <a href="javascript:empty_date()">清空</a></span>
   &nbsp; &nbsp; &nbsp; &nbsp;<input onclick="statistics()" value="统计" type="button" class="SmallButton">
  <!-- 
  <input type="button" value="导出" class="BigButton" onClick="ext();" title="导出" name="button">
   -->
    </td> 
  </tr>
  </table>
</form>
</fieldset>

<br>
<div align="center" id="listDiv" style="display:none">
<table class="TableList pgTable" width="80%">
<tr class="TableTr"><td class="TableHeader" width="20%">用户账号</td><td  class="TableHeader"  width="20%">用户名</td><td  class="TableHeader"  width="20%">状态</td><td  class="TableHeader"  width="10%">数量</td><td  class="TableHeader"  width="10%">合计</td><td  class="TableHeader"  width="10%">文件大小</td><td  class="TableHeader"  width="10%">大小合计</td></tr>
<tbody id="listData">
<!-- 
<tr class="TableTr"><td rowspan="4">client</td><td rowspan="4">client2</td><td >3</td><td >3333</td><td rowspan="4">55</td></tr>
<tr class="TableTr"><td>2</td><td>222</td></tr>
<tr class="TableTr"><td>2</td><td>222</td></tr>
<tr class="TableTr"><td>2</td><td>222</td></tr>
 -->
</tbody>
<tr class="TableTr"><td colspan="4" align="right">总 计：</td><td id="countTd"></td><td align="right">大小总计：</td><td id="countTd2"></td></tr>
</table>
</div>
<div id="noData"  style="display:none">
<table id="pgMsrgPanel" class="MessageBox" width="300" align="center">
  <tbody><tr>
    <td class="msg info">
           没有查找到数据！
    </td>
  </tr>
</tbody></table>
</div>
</body>
</html>
