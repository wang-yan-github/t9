<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>项目列表</title>
<link rel="stylesheet" href = "<%=cssPath%>/style.css">
<link rel="stylesheet" href="<%=cssPath%>/page.css">
<link rel="stylesheet" href="<%=cssPath%>/cmp/Calendar.css">
<script type="text/javascript" src="<%=contextPath %>/core/js/prototype.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/datastructs.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/sys.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/smartclient.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/cmp/page.js"></script>
<script type="text/javascript" src="<%=contextPath %>/project/js/util.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/Calendarfy.js"></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/cmp/select.js" ></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/orgselect.js" ></script>
<script type="text/javascript" src="<%=contextPath %>/project/proj/basicInfo/js/logic.js"></script>
<script> 
var pageMgr = null;
var range=null;
var TOTAL=null;
function doInit(){
	  var date1Parameters = {
		      inputId:'beginDate',
		      property:{isHaveTime:false}
		      ,bindToBtn:'beginImg'
		  };
	new Calendar(date1Parameters);
	var date2Parameters = {
		       inputId:'endDate',
		       property:{isHaveTime:false}
		      ,bindToBtn:'endImg'
		  };
	new Calendar(date2Parameters);
	range=$('range').value;
	getProjectStyle();
	getProjectList();
	if(!TOTAL){
		WarningMsrg('无项目信息', 'msrg');
	} 
}
function getProjectList(){
  var url = "<%=contextPath%>/t9/project/project/act/T9ProjectAct/getProjectList.act?range="+range;
  var cfgs = {
    dataAction: url,
    container: "listContainer",
    sortIndex: 1,
    sortDirect: "desc",
    paramFunc: getParam,
    colums: [
       //{type:"selfdef", text:"选择", width: '5%', render:checkBoxRender},
       {type:"hidden", name:"seqId", text:"顺序号", dataType:"int"},
       {type:"data", name:"projNum",  width: '15%', text:"项目编号" ,align: 'center'},
       {type:"data", name:"projName",  width: '15%', text:"项目名称" ,align: 'center',render:showInfo},
       {type:"data", name:"userName",  width: '12%', text:"项目创建人" ,align: 'center'},
       {type:"data", name:"projStartTime",  width: '12%', text:"开始时间" ,align: 'center',render:getTime},
       {type:"data", name:"projEndTime",  width: '12%', text:"结束时间" ,align: 'center',render:getTime},
       {type:"data", name:"projActEndTime",  width: '12%', text:"实际结束时间" ,align: 'center',render:getTime},
       {type:"data", name:"projStatus", width:'8%',text:"状态", dataType:"int",align:'center',render:showStatus},
       {type:"selfdef", text:"操作", width: '10%',align:'left',render:opts}]
  };
  pageMgr = new T9JsPage(cfgs);
  pageMgr.show();
  var total = pageMgr.pageInfo.totalRecord;
  TOTAL=total;
  if(total){
    $('listContainer').style.display="";
    var mrs = " 共 " + total + " 条记录 ！";
    //showCntrl('delOpt');
  }else{
    //WarningMsrg('无项目信息', 'msrg');
  }
}


function getParam(){
	  var queryParam = $("form1").serialize();
	  return queryParam;
	}

function dosubmit(){
	if(check_form()){
		pageMgr.refreshAll();
		getProjectList();
	}
}


function opts(cellData, recordIndex, columIndex){
	var status=this.getCellData(recordIndex,"projStatus");
	var projId=this.getCellData(recordIndex,"seqId");
  var str=""
  if(status=="0"){
	  str="<span><a href='index.jsp?projId="+projId+"'>编辑</a></span><span style='margin-left:20px;'><a href='javascript:deleteProj("+projId+")'>删除</a></span>";
  }else if(status=="1"){
	  str="<a href='index.jsp?projId="+projId+"'>项目变更</a></span><span style='margin-left:20px;'><a href='javascript:endProj("+projId+")'>结束</a>";
  }else if(status=="2"){
	  str="<a href='index.jsp?projId="+projId+"'>项目变更</a></span><span style='margin-left:20px;'><a href='javascript:endProj("+projId+")'>结束</a>";
  }else if(status=="3"){
	  str="<a href='javascript:recoveryProj("+projId+")'>恢复执行</a>";
  }else{
	  str="<a href='index.jsp?projId="+projId+"'>项目变更</a></span><span style='margin-left:20px;'><a href='javascript:endProj("+projId+")'>结束</a>";
  }
  return str;
}
function showInfo(cellData, recordIndex, columIndex){
	var projId=this.getCellData(recordIndex,"seqId");
  var str="<a href='basicInfo/index.jsp?projId="+projId+"' target='_blank'>"+cellData+"</a>"
  return str;
}
function showStatus(cellData, recordIndex, columIndex){
 	if(cellData=='0'){
 		return "立项中";
 	}else if(cellData=='1'){
 		return "审批中";
 	}else if(cellData=='2'){
 		return "进行中";
 	}else if(cellData=='3'){
 		return "已结束";
 	}else{
 		return "已超时";
 	}
  
}
function getTime(cellData, recordIndex, columIndex){
  if(!cellData){
    return "";
  }
  return cellData.substring(0,10);
}

function check_form(){
   var starttime=new Date((document.form1.beginDate.value).replace(/-/g,"/"));
   var endtime=new Date((document.form1.endDate.value).replace(/-/g,"/"));
   if(endtime<starttime)
   {
      alert("项目计划周期的结束时间不能小于开始时间！");
      return(false);
   }
   return true;
}

function deleteProj(projId){
	if(!window.confirm("确认要删除该项目吗 ？")){
		return ;
	}
	var requestURLStr = contextPath + "/t9/project/project/act/T9ProjectAct";
	var url = requestURLStr + "/deleteProj.act";
	var rtJson = getJsonRs(url, "projId=" + projId );
	if (rtJson.rtState == "0") {
		window.location.reload();
	}else {
	 alert(rtJson.rtMsrg); 
	}
	
}

function endProj(projId){
	if(!window.confirm("确认要结束该项目吗？")){
		return ;
	}
	var requestURLStr = contextPath + "/t9/project/project/act/T9ProjectAct";
	var url = requestURLStr + "/endProj.act";
	var rtJson = getJsonRs(url, "projId=" + projId );
	if (rtJson.rtState == "0") {
		window.location.reload();
	}else {
	 alert(rtJson.rtMsrg); 
	}
}

function recoveryProj(projId){
	if(!window.confirm("确认要恢复该项目吗？")){
		return ;
	}
	var requestURLStr = contextPath + "/t9/project/project/act/T9ProjectAct";
	var url = requestURLStr + "/recoveryProj.act";
	var rtJson = getJsonRs(url, "projId=" + projId );
	if (rtJson.rtState == "0") {
		window.location.reload();
	}else {
	 alert(rtJson.rtMsrg); 
	}
}

function optionChange(){
	range=$('range').value;
	pageMgr.refreshAll();
	getProjectList();
	pageMgr.refreshAll();
}


function createProject(){
	var requestURLStr = contextPath + "/t9/project/project/act/T9ProjectAct";
	var url = requestURLStr + "/isHasNewPriv.act";
	var rtJson = getJsonRs(url);
	if (rtJson.rtState == "0") {
		if(rtJson.rtData.flag=="0"){
			alert("您没有立项权限，如需项目立项权限请与管理员联系开通！");
		}else{
			window.location.href=contextPath +"/project/proj/index.jsp";
		}
	}else {
	 alert(rtJson.rtMsrg); 
	}
}
</script>
</head>
<body topmargin="5" onload="doInit()">
<table border="0" width="100%" cellspacing="0" cellpadding="3" class="small">
 <tr>
   <td class="Big"><img src="<%=imgPath%>/search.gif" align="absMiddle"><span class="big3">&nbsp;查询条件</span>
   </td>
 </tr>
</table>
<br>
<div id="condition">
<form method="post" id="form1" name="form1" enctype="multipart/form-data" action="">
	<table class="TableBlock" border="0" width="90%" align="center">
		<tr>
		<td nowrap class="TableContent">项目类型：</td>
  	  <td class="TableData"> 
  	     	    <select name="projStyle" class="SmallSelect" id="projStyle" >
  	     	    	<option value="-1">全部类型</option>
  	     	    </select> 
      </td>
      <td nowrap class="TableContent">项目状态：</td>
  	    <td class="TableData">
  	    <select name="projStatus" class="SmallSelect" id="projStatus" >
  	    <option value="-1">所有状态</option>
  	    <option value="0">立项中</option>
  	    <option value="1">审批中</option>
  	    <option value="2">进行中</option>
  	    <option value="3">已结束</option>
  	    </select> 
  	  </td>
	</tr>
	<tr>
		<td nowrap class="TableContent">项目计划周期：</td>
  	  <td class="TableData"> 
  	     	    <input type="text" id="beginDate" name="beginDate" class=BigInput size="10">
                <img id="beginImg" src="<%=imgPath%>/calendar.gif" align="absMiddle" border="0" style="cursor:pointer" >
       至      	    <input type="text" id="endDate" name="endDate" class=BigInput size="10" value="">
                <img id="endImg" src="<%=imgPath%>/calendar.gif" align="absMiddle" border="0" style="cursor:pointer" >
     
      </td>
      <td nowrap class="TableContent">项目创建人：</td>
  	    <td class="TableData">
	      	<input type="hidden" name="projOwner" id="projOwner"  class="BigStatic">
	      	<input name="projOwnerDesc" id="projOwnerDesc"  class="BigStatic" readonly>
	      	<a href="javascript:;" class="orgAdd" onClick="selectSingleUser(['projOwner','projOwnerDesc']);">选择</a>
  	  </td>
	</tr>
	<tr>
  		<td nowrap class="TableContent" width="90">项目编号：</td>
  	  <td class="TableData">
  	  	<input type="text" class="BigInput" name="projNum" id="projNum" value="" size=20><span id="check_msg"></span>
  	  </td>  	  	
  		<td nowrap class="TableContent" width="90">项目名称：</td>
  	  <td class="TableData"><input type="text" class="BigInput" name="projName" id="projName" value="" size=20></td>  	  	
  	</tr>
  	<tr>
  		<td colspan="4" nowrap align="center">
  		<input type="button" value="查询" class="BigButton" onClick="dosubmit()">
  		<input type="button" value="新建项目" class="BigButton" onClick="createProject()">
  		</td>
  	</tr>
	</table>
</form>
</div>
<br/>
<table border="0" width="100%" cellspacing="0" cellpadding="3" class="small">
 <tr>
   <td class="Big"><img src="<%=imgPath%>/notify_open.gif" align="absMiddle"><span class="big3">&nbsp;管理项目</span>
   <input type="hidden" id="rangeValue" name="rangeValue">
   <select name="range" id="range" style="width: 150px;" onchange="optionChange();">
   <option value="0">所有范围</option>
   <option value="1">我管理的</option>
   <option value="2">我参与的</option>
   </select>
   </td>
 </tr>
</table>
<br/>
<div id="listContainer" style="display:none;width:100;">
没有查到相关信息
</div>
<div id="delOpt" style="display:none">
<table class="TableList" width="100%">
<%-- <tr class="TableControl">
      <td colspan="19">
         <input type="checkbox" name="checkAlls" id="checkAlls" onClick="checkAll(this);"><label for="checkAlls">全选</label> &nbsp;
         <a href="javascript:deleteAll();" title="删除所选记录"><img src="<%=imgPath%>/delete.gif" align="absMiddle">删除所选记录</a>&nbsp;
      </td>
 </tr> --%>
</table>
</div>

<div id="msrg">
</div>
</body>
</html>