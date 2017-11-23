<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>印章日志</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="<%=cssPath %>/cmp/tab.css" type="text/css" />
<link rel="stylesheet" href = "<%=cssPath%>/cmp/Calendar.css">
<link rel="stylesheet" href="<%=cssPath%>/page.css">
<link rel="stylesheet" href = "<%=cssPath%>/style.css">
<link rel="stylesheet" href = "<%=cssPath%>/cmp/tree.css">
<script type="text/javascript" src="<%=contextPath %>/core/funcs/demo/js/sealManageUtil.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/prototype.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/tab.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/datastructs.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/sys.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/cmp/page.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/cmp/Menu.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/smartclient.js"></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/orgselect.js" ></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/cmp/Calendarfy.js"></script>
<script type="text/javascript">
var pageMgr  =null;
function doInit(){
  var url =  contextPath + "/t9/core/funcs/demo/act/T9DeviceAct/getDeviceLogList.act";
  var cfgs = {
    dataAction: url,
    container: "listContainer",
    paramFunc: getParam,
    colums: [
       {type:"selfdef", text:"选择", width: "10%", render:checkBoxRender},
       {type:"hidden", name:"seqId", text:"顺序号", dataType:"int"},
       {type:"data",width: "10%", name:"deviceName", text:"设备名称"},   
       {type:"data", width: "10%", name:"uid", text:"申请人"},    
       {type:"data",width: "10%", name:"deviceDesc", text:"设备描述"},
       {type:"data", width: "10%", name:"deviceType", text:"状态",render:changType1}, 
       {type:"data", width: "20%", name:"submitTime", text:"创建时间"},
       {type:"selfdef", text:"操作", width: "10%",render:opts}]
  };
  pageMgr = new T9JsPage(cfgs);
  pageMgr.show();
  var total = pageMgr.pageInfo.totalRecord;
  if(total){
    showCntrl('listContainer');
    showCntrl('delOpt');
  }else{
    WarningMsrg('无设备！', 'msrg');
  }
  
  var beginParameters = {
      inputId:'beginTime',
      property:{isHaveTime:true}
      ,bindToBtn:'beginTimeImg'
  };
  new Calendar(beginParameters);
  var endParameters = {
      inputId:'endTime',
      property:{isHaveTime:true}
      ,bindToBtn:'endTimeImg'
  };
  new Calendar(endParameters);

  var date = new Date();
  var y = date.getFullYear();
  var m = date.getMonth() + 1;
  m = (m > 9) ? m : '0' + m;
  var d = date.getDate();
  d = (d > 9) ? d : '0' + d;
  var time = date.toLocaleTimeString();
  $('endTime').value = y + '-' + m + '-' + d + ' ' + time;
}

function getName(cellData, recordIndex, columIndex){
	 var seqId = this.getCellData(recordIndex,"seqId");
	 var url =  contextPath + "/t9/core/funcs/demo/act/T9DeviceAct/getDeviceInfo.act";
	  var rtJson = getJsonRs(url,"seqId="+seqId+"&para=model");
	  if(rtJson.rtState == "0"){
		  return "<center>" + rtJson.rtData.data + "</center>"
	  }else{
		  return "<center>" + cellData + "</center>"
	  }
	}

//////////
function getImsi(cellData, recordIndex, columIndex){
	 var seqId = this.getCellData(recordIndex,"seqId");
	 var url =  contextPath + "/t9/core/funcs/demo/act/T9DeviceAct/getDeviceInfo.act";
	  var rtJson = getJsonRs(url,"seqId="+seqId+"&para=imsi");
	  if(rtJson.rtState == "0"){
		  return "<center>" + rtJson.rtData.data + "</center>"
	  }else{
		  return "<center>" + cellData + "</center>"
	  }
	}
///////////////
function getImei(cellData, recordIndex, columIndex){
	 var seqId = this.getCellData(recordIndex,"seqId");
	 var url =  contextPath + "/t9/core/funcs/demo/act/T9DeviceAct/getDeviceInfo.act";
	  var rtJson = getJsonRs(url,"seqId="+seqId+"&para=imei");
	  if(rtJson.rtState == "0"){
		  return "<center>" + rtJson.rtData.data + "</center>"
	  }else{
		  return "<center>" + cellData + "</center>"
	  }
	}
//////////////
function getPhoneNumber(cellData, recordIndex, columIndex){
	 var seqId = this.getCellData(recordIndex,"seqId");
	 var url =  contextPath + "/t9/core/funcs/demo/act/T9DeviceAct/getDeviceInfo.act";
	  var rtJson = getJsonRs(url,"seqId="+seqId+"&para=phoneNumber");
	  if(rtJson.rtState == "0"){
		  return "<center>" + rtJson.rtData.data + "</center>"
	  }else{
		  return "<center>" + cellData + "</center>"
	  }
	}
function changType1(cellData, recordIndex, columIndex){
	if(cellData == "0"){
		 return "<center>待批准</center>"
		}
	if(cellData == "1"){
		return "<center>已批准</center>"
	}
	if(cellData == "2"){
		return "<center>未批准</center>"
	}
}
function opts(cellData, recordIndex, columIndex){
	  var seqId = this.getCellData(recordIndex,"seqId");
	  var dtype = this.getCellData(recordIndex,"deviceType");
	  if(dtype != 0){
		  return "<center><a href=\"javascript:chexiao('"+seqId+"');\">撤销</a></center>";
		  }else{
			  return "<center><a href=\"javascript:pizhun('"+seqId+"');\">批准</a>&nbsp;&nbsp;<a href=\"javascript:bupizhun("+seqId+");\">不批准 </a></center>";
	      }
	  
		 
	 
	
	  
	  
	  //<a href=\"javascript:doSeal("+seqId+");\">恢复印章</a>
	}

function checkBoxRender(cellData, recordIndex, columIndex){
  var diaId = this.getCellData(recordIndex,"seqId");
  return "<center><input type=\"checkbox\" name=\"deleteFlag\" value=\"" + diaId + "\" onclick=\"checkSelf()\" ></center>";
}

function getSealName(cellData, recordIndex, columIndex){
  
  var sId = this.getCellData(recordIndex,"sId");
  var urls = "<%=contextPath%>/t9/core/funcs/system/sealmanage/act/T9SealLogAct/getSealName.act";
  var rtJsons = getJsonRs(urls , "sId=" + sId);
  if(rtJsons.rtState == '0'){
    return "<center>" + rtJsons.rtData + "</center>";
  }
}

function chexiao(seqId){
	  var url = "<%=contextPath%>/t9/core/funcs/demo/act/T9DeviceAct/changType.act";
	  var rtJson = getJsonRs(url, "seqId=" + seqId+"&opt=0");
	  if (rtJson.rtState == "0") {
		  window.location.reload();
	  }else {
	    alert("操作失败!");
	  }
}
function pizhun(seqId){
	var url = "<%=contextPath%>/t9/core/funcs/demo/act/T9DeviceAct/changType.act";
	  var rtJson = getJsonRs(url, "seqId=" + seqId+"&opt=1");
	  if (rtJson.rtState == "0") {
		  window.location.reload();
	  }else {
	    alert("无印章信息!");
	  }
}
function bupizhun(seqId){
	var url = "<%=contextPath%>/t9/core/funcs/demo/act/T9DeviceAct/changType.act";
	  var rtJson = getJsonRs(url, "seqId=" + seqId+"&opt=2");
	  if (rtJson.rtState == "0") {
		  window.location.reload();
	  }else {
	    alert("无印章信息!");
	  }
}



function getUserOpName(cellData, recordIndex, columIndex){
  var urls = "<%=contextPath%>/t9/core/funcs/system/sealmanage/act/T9SealLogAct/getUserOpName.act";
  var rtJsons = getJsonRs(urls , "userId=" +  cellData);
  if(rtJsons.rtState == '0'){
    return "<center>" + rtJsons.rtData + "</center>";
  }
}

function getLogType(cellData, recordIndex, columIndex){
  if(cellData == "addseal"){
    cellData = "加盖印章";
    return "<center>" + cellData + "</center>";
  }else if(cellData == "delseal"){
    cellData = "删除印章";
    return "<center>" + cellData + "</center>";
  }
  else if(cellData == "verify"){
    cellData = "验证印章";
    return "<center>" + cellData + "</center>";
  }else if(cellData == "makeseal"){
    cellData = "制作印章";
    return "<center>" + cellData + "</center>";
  }else if(cellData == "setseal"){
    cellData = "印章授权";
    return "<center>" + cellData + "</center>";
  }else if(cellData == "writeseal"){
    cellData = "恢复印章";
    return "<center>" + cellData + "</center>";
  }
}

/**
 * 全选
 */
function checkAll(field) {
  var deleteFlags = document.getElementsByName("deleteFlag");
  for(var i = 0; i < deleteFlags.length; i++) {
    deleteFlags[i].checked = field.checked;
  }
}

function checkSelf(){
  var allCheck = $('checkAlls');
  if(allCheck.checked){
    allCheck.checked = false;
  }
}

function deleteAllUser() {
  var idStrs = checkMags('deleteFlag');
  if(!idStrs) {
    alert("请至少选择一条日志。");
    return;
  }
  if(!confirmDel()) {
   return ;
  }  
  var url = "<%=contextPath%>/t9/core/funcs/demo/act/T9DeviceAct/deleteDevice.act";
  var rtJson = getJsonRs(url, "sumStrs=" + idStrs);
  if (rtJson.rtState == "0") {
    window.location.reload();
  }else {
    alert(rtJson.rtMsrg); 
  }
}

function confirmDel() {
  if(confirm("确认要删除所选用户吗？")) {
    return true;
  }else {
    return false;
  }
}

function doSearch(){
  pageMgr.search();
}
function getParam(){
  queryParam = $("form1").serialize();
  return queryParam;
}
</script>
</head>
<body topmargin="5" onload="doInit()">
<table border="0" width="100%" cellspacing="0" cellpadding="3" class="small">
  <tr>
    <td class="Big"><img src="<%=imgPath%>/infofind.gif" align="absmiddle"><span class="big3">&nbsp;移动设备查询  </span>
    </td>
  </tr>
</table>

<form method="post" name="form1" id="form1">
<table width="500px" class="TableList" align="center" >
  <tr>
   
    <td nowrap class="TableContent"> 移动设备状态：</td>
    <td nowrap class="TableData">
      <select name="DEVICE_TYPE" id="DEVICE_TYPE" class="SmallSelect">
      	<option value="">全部</option>
      	<option value="0">待批准</option>
      	<option value="1">已批准</option>
      	<option value="2">未批准</option>
      </select>
    </td>
  </tr>
  <tr>
    <td nowrap class="TableContent"> 申请人：</td>
    <td nowrap class="TableData" colspan=3>
    <input type="hidden" class="SmallInput" size=20 readOnly name="user" id="user">
    <textarea name="userDesc" id="userDesc" readOnly rows="1" class="SmallStatic" cols="20"></textarea>
    <a href="javascript:;" class="orgAdd" onClick="selectSingleUser(['user','userDesc']);">选择</a>
    <a href="javascript:;" class="orgClear" onClick="$('user').value='';$('userDesc').value='';">清空</a>
    </td>
  </tr>
  <tr>
      <td nowrap class="TableContent"> 发生时间范围：</td>
      <td nowrap class="TableData" colspan=3>
        从 <input type="text" name="beginTime" id="beginTime" size="20" maxlength="20" class="BigInput" value="">
        <img id="beginTimeImg" src="<%=imgPath%>/calendar.gif" align="absMiddle" border="0" style="cursor:pointer">
        至 <input type="text" name="endTime" id="endTime" size="20" maxlength="20" class="BigInput" value="">
        <img id="endTimeImg" src="<%=imgPath%>/calendar.gif" align="absMiddle" border="0" style="cursor:pointer">
      </td>
  </tr>
  <tr class="TableFooter" >
      <td nowrap align="center" colspan=4>
      	<input type="button" onclick="doSearch();" class="BigButton" value="查询">&nbsp;
      </td>
  </tr>
</form>
</table>
<br>
<table width="95%" border="0" cellspacing="0" cellpadding="0" height="3">
 <tr>
   <td background="/images/dian1.gif" width="100%"></td>
 </tr>
</table>


<table border="0" width="100%" cellspacing="0" cellpadding="3" class="small">
  <tr>
    <td class="Big"><img src="<%=imgPath%>/system.gif" align="absmiddle"><span class="big3">&nbsp;移动设备管理</span>&nbsp;
    </td>
</table>
<br/>
<div id="listContainer" style="display:none">
</div>
<div id="delOpt" style="display:none">
<table id="beSortTable" class="TableList" width="100%">
   <tr class='TableControl'>
     <td colspan='10'>
       &nbsp;<input type="checkbox" name="checkAlls" id="checkAlls" onClick="checkAll(this)"><label for='checkAlls'>全选</label> &nbsp;
       <a href="javascript:deleteAllUser();" title="删除所选印章"><img src="<%=imgPath%>/delete.gif" align="absMiddle">删除</a>&nbsp;
     </td>
   </tr>
</table>
</div>
<div id="msrg">
</div>


</table>
</body>
</html>