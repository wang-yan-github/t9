<%@ page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header.jsp"%>
<%
	String projId=request.getParameter("projId");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>项目任务</title>
<link rel="stylesheet" href="<%=cssPath%>/cmp/tab.css" type="text/css" />
<link rel="stylesheet" href="<%=cssPath%>/style.css">
<link rel="stylesheet" href="<%=cssPath%>/cmp/Calendar.css">
<script type="text/Javascript" src="<%=contextPath%>/core/js/datastructs.js"></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/sys.js"></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/prototype.js"></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/smartclient.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/Calendarfy.js"></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/cmp/select.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/cmp/tab.js"></script>
<script type="text/javascript" src="<%=contextPath %>/project/js/util.js"></script>
<script type="text/javascript">
var projId=<%=projId%>;
/**
 * 获取任务列表
 */
function doInit(){
	if($("taskMilestone").checked==true){
		$("taskMilestone").value="1";
	}else{
		$("taskMilestone").value="0";
	}
	if($("taskConstrain").checked==true){
		$("taskConstrain").value="1";
	}else{
		$("taskConstrain").value="0";
	}
	var date1Parameters = {
		      inputId:'taskStartTime',
		      property:{isHaveTime:false}
		      ,bindToBtn:'beginImg'
		  };
  new Calendar(date1Parameters);
  var date2Parameters = {
		       inputId:'taskEndTime',
		       property:{isHaveTime:false}
		      ,bindToBtn:'endImg'
		  };
  new Calendar(date2Parameters);
  if(projId!=null){
	  $("projId").value=projId;
  }
  getTaskInfoList(projId);
  getTaskUser(projId);
  getTaskName(projId);
  getFlowName();
}

function addTask(){
	if(isHasProjPerson()){
		$("taskList").style.display="none";
		$("msrg").style.display="none";
		$("taskInfo").style.display="";
	}else{
		alert("还没有分配项目人员！！");
	}
}

function returnTaskList(){
	location.reload();
}


function isHasProjPerson(){
	 var url= contextPath + "/t9/project/project/act/T9ProjectAct/isHasProjUser.act?projId="+projId;
	 var rtJson = getJsonRs(url);
	 if(rtJson.rtState == "0"){
		 var data=rtJson.rtData;
		 if(data.flag=='1'){
				return false;
			}else{
				return true;
			}
	 }else{
		 return false;
	 }
}

function saveTaskInfo(){
	if($("taskMilestone").checked==true){
		$("taskMilestone").value="1";
	}else{
		$("taskMilestone").value="0";
	}
	if($("taskConstrain").checked==true){
		$("taskConstrain").value="1";
	}else{
		$("taskConstrain").value="0";
	}
	var flag=$("taskMilestone").value;
	var constrain=$("taskConstrain").value;
	if(check_form()){
		var url= contextPath + "/t9/project/task/act/T9TaskAct/addTaskInfo.act?flag="+flag+"&constrain="+constrain;
		var rtJson = getJsonRs(url,mergeQueryString($("form1")));
		if(rtJson.rtState == "0"){
	    alert('任务分配成功！');
	    location.reload();
	  }else{
			alert(rtJson.rtMsrg);
	  } 
	}
}

/*
 * 检查数据
 */
function check_form(){
	if($("taskNo").value=="" || $("taskName").value=="" || $("taskStartTime").value=="" || $("taskEndTime").value=="")
	{
	   alert("必填项目不能为空！");
	   return(false);
	}
   var starttime=new Date((document.form1.taskStartTime.value).replace(/-/g,"/"));
   var endtime=new Date((document.form1.taskEndTime.value).replace(/-/g,"/"));
   if(endtime<starttime)
   {
      alert("项目计划周期的结束时间不能小于开始时间！");
      return(false);
   }
   return true;
}



function getTaskInfoList(projId){
	 var url= contextPath + "/t9/project/task/act/T9TaskAct/getTaskList.act?projId="+projId;
	 var rtJson = getJsonRs(url);
	 if(rtJson.rtState == "0"){
		  var data=rtJson.rtData;
		  if(data.size()>0){
			  var str="<table id=\"taskList\" class=\"TableList\" border=\"0\" width=\"100%\" align=\"center\">"
	   				+ "<tr class=\"TableHeader\">"
	    			+ "<td nowrap align=\"center\">任务名称</td>"
	      		+ "<td nowrap align=\"center\">执行人</td>"
	    			+ "<td nowrap align=\"center\">开始</td>"
	    			+ "<td nowrap align=\"center\">工期</td>"
	    			+ "<td nowrap align=\"center\">结束</td>"
	    			+ "<td nowrap align=\"center\">操作</td>" 	
	   				+ "</tr>";
	   		var taskList="";
				for(var i=0;i<data.size();i++){
					taskList +="<tr class=\"TableLine2\">"
	            + "<td nowrap align=\"center\">"+data[i].taskName+"</td>"
	            + "<td nowrap align=\"center\">"+data[i].taskUser+"</td>"
	            + "<td nowrap align=\"center\">"+data[i].beginDate+"</td>"
	            + "<td nowrap align=\"center\">"+data[i].taskTime+"&nbsp;天</td>"
	            + "<td nowrap align=\"center\">"+data[i].endDate+"</td>"
	            + "<td nowrap align=\"center\">"
	            + "<span><a href=\"javascript:void(0)\" onclick=\"editTask("+data[i].taskId+");\">编辑</a></span>"
	            + "<span style=\" margin-left:50px;\"><a href=\"javascript:void(0)\" onclick=\"delTask("+data[i].taskId+");\">删除</a></span></td>"  	
	          	+ "</tr>";
				}
	    $("taskInfoList").innerHTML=str+taskList+"</table>";
		  }else{
			  WarningMsrg('无项目任务', 'msrg');
		  }
	    
	 }else{
			alert(rtJson.rtMsrg);
	 }  
}

function editTask(taskId){
	var url= contextPath + "/t9/project/task/act/T9TaskAct/getTaskInfo.act?taskId="+taskId;
	 var rtJson = getJsonRs(url);
	 if(rtJson.rtState == "0"){
		  $("taskList").style.display="none";
			$("taskInfo").style.display="";
		  var data=rtJson.rtData;
		  bindJson2Cntrl(rtJson.rtData);
		  $("taskId").value=rtJson.rtData.seqId;
		  $("taskStartTime").value=data.taskStartTime.substring(0,10);
		  $("taskEndTime").value=data.taskEndTime.substring(0,10);
		  $("preTask").value=data.preTask;
		  $("taskTime").innerHTML=data.taskTime+"天";
		  $("FLOW_ID_STR").value=data.flowIdStr;
		//  alert($("FLOW_ID_STR").value);
		  getName($("FLOW_ID_STR").value);
		  
		 // $("FLOW_ID_STR").value=
		  if($("preTask").value!='0') {
				document.getElementById("SHOW_CONSTRAIN").style.display="";
			}else{
				document.getElementById("SHOW_CONSTRAIN").style.display="none";
			}
		  if(data.taskMilestone=="1"){
			  $("taskMilestone").checked=true;
		  }else{
			  $("taskMilestone").checked=false;
		  }
		  if(data.taskConstrain=="1"){
			  $("taskConstrain").checked=true;
		  }else{
			  $("taskConstrain").checked=false;
		  }
	 }else{
		 alert(rtJson.rtMsrg);
	 }
}

function delTask(taskId){
	if(confirm("确认删除！")){
		var url= contextPath + "/t9/project/task/act/T9TaskAct/delTaskInfo.act?taskId="+taskId;
		 var rtJson = getJsonRs(url);
		 if(rtJson.rtState == "0"){
			 alert("删除成功");
			 location.reload();
		 }else{
			 alert(rtJson.rtMsrg);
		 }
	}
}



function changeFlag(){
	if($("taskMilestone").checked==true){
		$("taskMilestone").value="1"
	}else{
		$("taskMilestone").value="0";
	}
}

function getTaskUser(projId){
	var url= contextPath + "/t9/project/task/act/T9TaskAct/getTaskUser.act?projId="+projId;
	var rtJson=getJsonRs(url);
	if(rtJson.rtState == "0"){
		var selectObj=$("taskUser");
		selectObj.length=0;
		for(var i=0;i<rtJson.rtData.size();i++){
				if(rtJson.rtData[i].userId!=null && rtJson.rtData[i].userId!=""){
					var myOption = document.createElement("option");
			    myOption.value = rtJson.rtData[i].userId;
			    myOption.text = rtJson.rtData[i].userName;
			    if(check(rtJson.rtData[i].userId)){
			   	 selectObj.options.add(myOption, selectObj.options ? selectObj.options.length : 0);
			    }
				}
		}
  }else{
		alert(rtJson.rtMsrg);
  }
}

function check(userId){
	var selectObj = $("taskUser");
	for(var j=0;j<selectObj.length;j++){
    	if(selectObj.options[j].value==userId){
    		 return false;
    		 continue;
    		}
    	}
	return true;
}


function getTaskName(projId){
	var url= contextPath + "/t9/project/task/act/T9TaskAct/getTaskName.act?projId="+projId;
	var rtJson=getJsonRs(url);
	if(rtJson.rtState == "0"){
		var selectObj1=$("parentTask");
		var selectObj2=$("preTask");
		//selectObj1.length=0;
		//selectObj2.length=0;
		for(var i=0;i<rtJson.rtData.size();i++){
				if(rtJson.rtData[i].taskId!=null && rtJson.rtData[i].taskId!=""){
					var myOption1 = document.createElement("option");
					var myOption2 = document.createElement("option");
			    myOption1.value = rtJson.rtData[i].taskId;
			    myOption2.value = rtJson.rtData[i].taskId;
			    myOption1.text = rtJson.rtData[i].taskName;
			    myOption2.text = rtJson.rtData[i].taskName;
			   	selectObj1.options.add(myOption1, selectObj1.options ? selectObj1.options.length : 0);
			    selectObj2.options.add(myOption2, selectObj2.options ? selectObj2.options.length : 0);
				}
		}
  }else{
		alert(rtJson.rtMsrg);
  }
}

function showIt(str) {
	if(str!="0") {
		document.getElementById("SHOW_CONSTRAIN").style.display="";
	}else{
		document.getElementById("SHOW_CONSTRAIN").style.display="none";
		document.form1.taskConstrain.checked=false;
	}
}
function getFlowName(){
	// alert(0);
	var url= contextPath + "/t9/project/task/act/T9TaskAct/getFlowFromName.act";
	var rtJson=getJsonRs(url);
	if(rtJson.rtState == "0"){
		var selectObj=$("FLOW_ID");
		for(var i=0;i<rtJson.rtData.size();i++){
		//	 alert(0);
				if(rtJson.rtData[i].flowId!=null && rtJson.rtData[i].flowId!=""){
					var myOption = document.createElement("option");
			    myOption.value = rtJson.rtData[i].flowId;
			    myOption.text = rtJson.rtData[i].flowName;
			    if(check(rtJson.rtData[i].userId)){
			    	//	alert(selectObj.options.length)
			   	 selectObj.options.add(myOption, selectObj.options ? selectObj.options.length : 0);
			    }
			   // break;
				}
		}
  }else{
		alert(rtJson.rtMsrg);
  }
}
function addFlow()
{
	var obj=$("FLOW_ID");
	//alert(obj.value);
	//alert($("FLOW_ID_STR").value);
	if(document.getElementById("FLOW_ID_STR").value.indexOf(obj.value)!=-1){
		alert("流程为空或该流程已选择");
		return;
	}
	if(obj.value=="")
	{
		alert("请选择流程！");
		return;
	}
	var flow=document.createElement("span");
	flow.id=obj.value;
	//alert(flow.id);
	flow.innerHTML=obj.options[obj.selectedIndex].text+'<img src="images/delete.jpg" align="absmiddle" onclick=delFlow(this) />';
	document.getElementById('FLOW_STR').appendChild(flow);
	//document.form1.FLOW_ID_STR.value+=obj.value+",";	
	document.getElementById("FLOW_ID_STR").value+=obj.value+",";	
}
function delFlow(obj){
	var flow_id=obj.parentNode.id;
	obj.parentNode.removeNode(true);
	var val=$("FLOW_ID_STR").value;
	
	if(val.indexOf(flow_id+",")==0){
	   val = val.replace(flow_id+",","");
	}else if(val.indexOf(","+flow_id+",")>0){
	   val = val.replace(flow_id+",","");
	 //  alert(val);
	}
	document.getElementById("FLOW_ID_STR").value=val;
	//alert(document.getElementById("FLOW_ID_STR1").value);
	
}
function getName(flowId){
	var url= contextPath + "/t9/project/task/act/T9TaskAct/getflowNameByflowId.act?flowId="+flowId;
	var rtJson=getJsonRs(url);
	if(rtJson.rtState == "0"){
		for(var i=0;i<rtJson.rtData.size();i++){
		//	alert(rtJson.rtData[i].flowName);	
			var flow=document.createElement("span");
			flow.innerHTML=rtJson.rtData[i].flowName+'<img src="images/delete.jpg" align="absmiddle" onclick=delFlow(this) />';
			document.getElementById('FLOW_STR').appendChild(flow);
		}
		
	}
	
}
</script>
</head>
<body onload="doInit();">
 <div id="taskList" >
 <table border="0" width="100%" cellspacing="0" cellpadding="3" class="small">
	<tr><td>
	<img src="<%=imgPath %>/notify_new.gif" align="absmiddle"/>
	<span class="big3">添加新任务</span><td></tr>
</table>

<div align="center">
   <input type="submit" value="新建任务" class="BigButton" title="添加项目成员" onclick="addTask()">
</div>

<br>

<table width="100%" border="0" cellspacing="0" cellpadding="0" height="3">
 <tr>
   <td width="100%"></td>
 </tr>
</table>

<table border="0" width="100%" cellspacing="0" cellpadding="3" class="small">
	<tr><td>
	<img src="<%=imgPath %>/user_group.gif" align="absmiddle"/>
	<span class="big3">项目任务列表</span>
	<td></tr>
</table>
<div id="taskInfoList" style="width:80%;text-align:center;margin:0 auto" >
</div>
 </div>
 <div id="taskInfo" style="display:none">
<table border="0" width="100%" cellspacing="0" cellpadding="3" class="small">
	<tr><td>
	<img src="<%=imgPath %>/notify_new.gif" align="absmiddle"/>
	<span class="big3">添加新任务</span><td></tr>
</table>

 <table class="TableList" border="0" width="80%" align="center">
  <form name="form1" id="form1" method="post" action="">
   <tr>
  		<td nowrap class="TableContent">任务序号：</td>
  	  <td class="TableData">
  	  	<input type="text" class="BigInput" name="taskNo" id="taskNo" value="" size=20>
  	  </td>  	  	
  	</tr>
  	<tr>
  		<td nowrap class="TableContent">任务名称：<span style="color:red">(*)</span></td>
  	  <td class="TableData">
  	  	<input type="text" class="BigInput" name="taskName" id="taskName" value="" size=20>
  	  </td>  	  	
  	</tr>
  	<tr>
  		<td nowrap class="TableContent">执行人：<span style="color:red">(*)</span></td>
  	  <td class="TableData">
  	   <select name="taskUser" id="taskUser" class="SmallSelect">
    	</select> </td>  	  	
  	</tr>
  	<tr>
  		<td nowrap class="TableContent">上级任务：</td>
  	  <td class="TableData">
  	  	<select name="parentTask" id="parentTask" class="SmallSelect" >
  	  	   <option value="0">无</option>
  	  	</select>
  	  </td>	  	
  	</tr>
  	<tr>
  		<td nowrap class="TableContent">前置任务：</td>
  	  <td class="TableData">
  	  	<select name="preTask" id= "preTask" class="SmallSelect" onchange="showIt(this.options[this.selectedIndex].value);">
  	  	   <option value="0">无</option>
  	  	</select>
  	  </td>	  	
  	</tr>
  	<tr id="SHOW_CONSTRAIN" style="display:none;">
  		<td nowrap class="TableContent">依赖性：</td>
  	  <td class="TableData"> 
         <input type="checkbox" name="taskConstrain" id="taskConstrain" ><label for="CONSTRAIN">通过前置任务设定任务开始时间</label>
      </td>
    </tr>
  	<tr>
  		<td nowrap class="TableContent">任务计划周期：<span style="color:red">(*)</span></td>
  	  <td class="TableData"> 
  	    <input type="text" id="taskStartTime" name="taskStartTime" class=BigInput size="10">
                <img id="beginImg" src="<%=imgPath%>/calendar.gif" align="absMiddle" border="0" style="cursor:pointer" >
       至      	    <input type="text" id="taskEndTime" name="taskEndTime" class=BigInput size="10" value="">
                <img id="endImg" src="<%=imgPath%>/calendar.gif" align="absMiddle" border="0" style="cursor:pointer" >

      </td>
    </tr>
    <tr>
  		<td nowrap class="TableContent">任务工时：</td>
  	  <td class="TableData">
  	  			<div id="taskTime" ></div>
  	        </td>
  	</tr>
    <tr>
  		<td nowrap class="TableContent">任务描述：</td>
  	  <td class="TableData">
  	  	<textarea cols="50" name="taskDescription"  id="taskDescription" rows="2" style="overflow-y:auto;" class="BigInput" wrap="yes"></textarea>
  	  </td>
  	</tr>
  	<tr>
     <td nowrap class="TableContent">任务级别：</td>
     <td nowrap class="TableData">
     <select name="taskLevel"class="SmallSelect">
      <option value="0">次要</option>
      <option value="1" selected>一般</option>
      <option value="2">重要</option>
      <option value="3">非常重要</option>
     </select>
     </td>
   </tr>
    <tr>
  		<td nowrap class="TableContent">里程碑：</td>
  	  <td class="TableData">
      <input type="checkbox" id="taskMilestone" name="taskMilestone" onclick="changeFlag()"><label for="TASK_MILESTONE">标记为里程碑</label>
  	  </td>
  	</tr>
    <tr>
  		<td nowrap class="TableContent">备注：</td>
  	  <td class="TableData">
  	  	<textarea cols="50" name="remark" id="remark" rows="2" style="overflow-y:auto;" class="BigInput" wrap="yes"></textarea>
  	  </td>
  	</tr>
     <tr>
  		<td nowrap class="TableContent">项目流程：</td>
  	  <td class="TableData"><select name="FLOW_ID" id="FLOW_ID" >
  	  <option value="">------请选择------</option>
  	  </select>
  	     <a href="javascript:;" class="orgAdd" onClick="addFlow()">添加流程</a>
  	       <div id="FLOW_STR" style="margin-top:5px;"></div>
      </td>
  	</tr> 
    <tr align="center" class="TableControl">
    	<td colspan="2" nowrap>
      <input type="hidden" name="projId"  id="projId" value="">
      <input type="hidden" name="taskId" id="taskId" value="">
      <input type="hidden" name="FLOW_ID_STR" id="FLOW_ID_STR" value="">
    	<input type="button" value="保存" class="BigButton" onclick="saveTaskInfo();">
	    <input type="button" value="返回" class="BigButton" onClick="returnTaskList()">
	    </td>
  </tr>
  </form>
 </table>
 </div>
 
<div id="msrg">
</div>
</body>
</html>