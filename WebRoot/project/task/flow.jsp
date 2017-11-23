<%@ page language="java" contentType="text/html; charset=UTF-8"pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header.jsp"%>
<%
String taskId=request.getParameter("taskId");
String projId=request.getParameter("projId");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="<%=cssPath%>/cmp/tab.css" type="text/css" />
<link rel="stylesheet" href="<%=cssPath%>/style.css">
<link rel="stylesheet" href="<%=cssPath%>/cmp/Calendar.css">
<script type="text/javascript" src="<%=contextPath %>/core/js/prototype.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/datastructs.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/sys.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/smartclient.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/cmp/page.js"></script>
<script type="text/javascript" src="<%=contextPath %>/project/js/util.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/funcs/workflow/workflowUtility/utility.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/Calendarfy.js"></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/cmp/select.js" ></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/orgselect.js" ></script>
<script type="text/Javascript" src="<%=contextPath%>/project/proj/task/js/task.js" ></script>
<script type="text/Javascript" src="<%=contextPath%>/core/funcs/workflow/workflowUtility/utility.js" ></script>
<title>流程设计图</title>
<script>
var projId = '<%=projId%>';
var taskId= '<%=taskId%>';
var pageMgr = null;
function doInit(){
	//alert(0);
  var url = "<%=contextPath%>/t9/project/task/act/T9TaskAct/getflowName.act?projId="+projId+"&taskId="+taskId;
  var cfgs = {
      dataAction: url,
      container: "listContainer",
      sortIndex: 1,
      sortDirect: "desc",
      colums: [
         {type:"hidden", name:"seqId", text:"顺序号",align: 'center', dataType:"int"},
         {type:"data", name:"flowType",  width: '15%', text:"流程类型",align: 'center',render:flowType},       
         {type:"data", name:"flowName",  width: '15%', align: 'center',text:"流程名称",render:getFlowJpg},
         {type:"selfdef", text:"操作", align: 'center',width: '15%',render:opts}
  ] };
    pageMgr = new T9JsPage(cfgs);
    pageMgr.show();
    var total = pageMgr.pageInfo.totalRecord;
    if(total){
      showCntrl('listContainer');
      var mrs = " 共 " + total + " 条记录 ！";
      showCntrl('delOpt');
    }else{
      WarningMsrg('无符合条件的流程记录', 'msrg');
    }
    getFlowNowName();
    getFinshFlow();
}
function opts(cellData, recordIndex, columInde){
	
	 var flowType = this.getCellData(recordIndex,"seqId");
	 var flowName = this.getCellData(recordIndex,"flowName");
	// alert(flowName);
	 return "<a href=\"javascript:newWork('"+flowName+"');\" >新建工作</a>&nbsp&nbsp";
}
function newWork(flowName){
	createNewWork(flowName,"","1",projId,taskId);
}
function flowType(cellData, recordIndex, columInde){
	  var flowType = this.getCellData(recordIndex,"flowType");
	  if(flowType==1){
		  return "固定流程";
	  }else{
		  return "自由流程";
	  }
}
function getFlowJpg(cellData, recordIndex, columInde){
	 var flowType = this.getCellData(recordIndex,"seqId");
	 var flowName = this.getCellData(recordIndex,"flowName");
	 return "<a href=\"javascript:showOpen("+flowType+");\" >"+flowName+"</a>&nbsp&nbsp";
}
function showOpen(seqId) {    
	var url = "index3.jsp?flowId="+seqId;
	var iWidth=800; //窗口宽度       
	var iHeight=800;//窗口高度          
	var iTop=(window.screen.height-iHeight)/2;          
	var iLeft=(window.screen.width-iWidth)/2;          
	window.open(url,"Detail","Scrollbars=no,Toolbar=no,Location=no,Direction=no,Resizeable=no,     Width="+iWidth+" ,Height="+iHeight+",top="+iTop+",left="+iLeft);        
}
function getFlowNowName(){
	//alert(0);
	 var url = "<%=contextPath%>/t9/project/task/act/T9TaskAct/getflowNowName.act?projId="+projId+"&taskId="+taskId;
	 var rtJson = getJsonRs(url);
	 if(rtJson.rtState == "0"){
		 var data=rtJson.rtData;
		//alert(data.size());
		 if(data.size()>0){
			  var str="<table class=\"TableList\" width=\"100%\">"
				   	 + "<tr class=\"TableHeader\">"
				     + "<td nowrap align=\"center\">流水号</td>"
				     + "<td nowrap align=\"center\">流程名称</td>"
				     + "<td nowrap align=\"center\" width=\"120px\">操作</td>"
				     + "</tr>";
	    var bugList="";
	    for(var i=0;i<data.size();i++){
	    	//alert(data[i].flowId);
			bugList +="<tr class=\"TableLine1\">"
						 // + "<td nowrap align=\"center\"><a href=\"../proj/basicInfo/index.jsp?projId="+data[i].projId+"\">"+data[i].projName+"</a></td>"
						  + "<td nowrap align=\"center\">"+data[i].runId+"</td>"
						  + "<td nowrap align=\"center\"><a href=\"#\" onclick=\"getFlow("+data[i].runId+","+data[i].flowId+")\">"+data[i].runName+"</a></td>"
						  + "<td nowrap align=\"center\">"+opts1(data[i].runId+","+data[i].flowId)+" </td></tr>";
		}
	    $("bugList").innerHTML=str+bugList+"</table>";
		 } else{
			 WarningMsrg('无办理中流程', 'msrg1');
		 }
	 }else{
		 alert(rtJson.rtMrsg);
	 }
}
function opts1(runId,flowId){
    var str="<a href=\"javascript:getDetailInfo("+runId+","+flowId+")\">办理</a>";
	return str;
}
function getDetailInfo(runId,flowId){
//	createNewWork1(runName,"","");
	var url =  "<%=contextPath%>/core/funcs/workflow/flowrun/list/inputform/index.jsp?runId="+runId+"&flowId="+flowId+"&prcsId=1&flowPrcs=1&isNewStr=1&isWriteLog=1";
	location.href = url;
}
function getFlow(runId,flowId){
	//alert(1);
	formView(runId , flowId) ;
	
}
function getFinshFlow(){
	 var url = "<%=contextPath%>/t9/project/task/act/T9TaskAct/getFinshflow.act?projId="+projId+"&taskId="+taskId;
	 var rtJson = getJsonRs(url);
	 if(rtJson.rtState == "0"){
		 var data=rtJson.rtData;
		//alert(data.size());
		 if(data.size()>0){
			  var str="<table class=\"TableList\" width=\"100%\">"
				   	 + "<tr class=\"TableHeader\">"
				     + "<td nowrap align=\"center\">流水号</td>"
				     + "<td nowrap align=\"center\">流程名称</td>"
				  //   + "<td nowrap align=\"center\" width=\"120px\">操作</td>"
				     + "</tr>";
	    var bugList="";
	    for(var i=0;i<data.size();i++){
	    	//alert(data[i].flowId);
			bugList +="<tr class=\"TableLine1\">"
						 // + "<td nowrap align=\"center\"><a href=\"../proj/basicInfo/index.jsp?projId="+data[i].projId+"\">"+data[i].projName+"</a></td>"
						  + "<td nowrap align=\"center\">"+data[i].runId+"</td>"
						  + "<td nowrap align=\"center\"><a href=\"#\" onclick=\"getFlow("+data[i].runId+","+data[i].flowId+")\">"+data[i].runName+"</a></td>"
					//	  + "<td nowrap align=\"center\">"+opts1(data[i].runId+","+data[i].flowId)+" </td>
					      +"</tr>";
		}
	    $("bugList1").innerHTML=str+bugList+"</table>";
		 } else{
			 WarningMsrg('无结束流程', 'msrg2');
		 }
	 }else{
		 alert(rtJson.rtMrsg);
	 }
	
	
	
}
</script>
</head>
<body onload="doInit()">
<div id="listContainer" style="display:none;width:100;">
</div>
<div id="delOpt" style="display:none">
<table class="TableList" width="100%">
</table>
</div>
<div id="msrg">
</div>
</br>
<img src="../images/project.gif" align="absmiddle"><span class="big3"> 项目流程-待办</span>
</br>
</br>
<div id="bugList">
			<div style="margin:0 auto;text-align:center;font-size:18px;font-color:blue"></div>
</div>
<div id="msrg1">
</div>
</br>
</br>
<img src="../images/project.gif" align="absmiddle"><span class="big3"> 项目流程-结束</span>
<div id="bugList1">
			<div style="margin:0 auto;text-align:center;font-size:18px;font-color:blue"></div>
</div>
<div id="msrg2">
</div>
</body>
</html>