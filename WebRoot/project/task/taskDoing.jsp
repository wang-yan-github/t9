<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header.jsp" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>进行中任务</title>
<link rel="stylesheet" href="<%=cssPath%>/page.css">
<link rel="stylesheet" href="<%=cssPath%>/style.css">
<link rel="stylesheet" href="<%=cssPath%>/cmp/Calendar.css">
<link rel="stylesheet" href="<%=contextPath%>/project/css/dialog.css">
<script type="text/javascript" src="<%=contextPath %>/project/js/dialog.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/prototype.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/cmp/tab.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/datastructs.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/sys.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/smartclient.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/cmp/Calendarfy.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/cmp/Menu.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/cmp/page.js"></script>
<script type="text/javascript">
var pageMgr=null;
var newWin = 0;

function doInit(){
  var url = "<%=contextPath%>/t9/project/task/act/T9TaskAct/getTaskTree.act";
  var cfgs = {
    dataAction: url,
    container: "listContainer",
    sortIndex: 1,
    sortDirect: "desc",
    paramFunc: getParam,
    colums: [
             {type:"hidden", name:"seqId", text:"顺序号", dataType:"int"},
             {type:"hidden", name:"projId", text:"顺序号", dataType:"int"},
             {type:"data", name:"taskNo",  width: '10%', text:"任务编号" ,align: 'center'},
             {type:"data", name:"projName",  width: '15%', text:"项目名称" ,align: 'center',render:getProjInfo},
             {type:"data", name:"taskName",  width: '15%', text:"任务名称" ,align: 'center',render:getInfo},
             {type:"data", name:"taskLevel",  width: '10%', text:"任务等级" ,align: 'center'},
             {type:"data", name:"taskStartTime",  width: '10%', text:"开始日期" ,align: 'center',render:getDate},
             {type:"data", name:"taskTime",  width: '10%', text:"工期" ,align: 'center',render:getTime},
             {type:"data", name:"taskEndTime",  width: '10%', text:"结束日期" ,align: 'center',render:getDate},
             {type:"hidden", name:"taskStatus"},
             {type:"selfdef",width: '10%', text:"操作" ,align: 'center',render:opts}]
        };
  pageMgr = new T9JsPage(cfgs);
  pageMgr.show();
  var url1 = "<%=contextPath%>/t9/project/task/act/T9TaskAct/getTaskListByUser.act";
  var rtJson=getJsonRs(url1);
	if(rtJson.rtState==0){
    var selectObj = $("projlist");
    for(var i=0;i<rtJson.rtData.size();i++){
    	  var opt = new Option(rtJson.rtData[i].projName);
    	  opt.value = rtJson.rtData[i].seqId;
        selectObj.add(opt);
        }
	}else{
		alert(rtJson.rtMsrg); 	
		}
}
/*
* 增加操作列
*/
function opts(cellData, recordIndex, columInde){
   var seqId = this.getCellData(recordIndex,"seqId");
   var projId = this.getCellData(recordIndex,"projId");
   var edit="<a href=\"javascript:editTask("+seqId+","+projId+");\" >办理任务</a>&nbsp&nbsp";
   var finishTask="<a href=\"javascript:updateStatus("+seqId+");\" >结束任务</a>&nbsp&nbsp";
   return edit+finishTask;
}
function updateStatus(seqId){
	if(confirm("确认结束任务？")){
	var url = "<%=contextPath%>/t9/project/task/act/T9TaskAct/updateStatus.act?status=1&seqId="+seqId;
	  var rtJson=getJsonRs(url);
		if(rtJson.rtState==0){
			window.location.reload();
		}else{
			alert(rtJson.rtMsrg); 	
		}
	}
}
function getProjInfo(cellData, recordIndex, columIndex){
	var projId=this.getCellData(recordIndex,"projId");
  var str="<a href=\"javascript:selectProjInfo("+projId+");\" >"+cellData+"</a>&nbsp&nbsp";
  return str;
}
function selectProjInfo(projId){
	var url="<%=contextPath%>/project/proj/basicInfo/basicInfo.jsp?projId="+projId;
	newWindow(url,1100,600,"info");
}
function editTask(seqId,projId){
	var url="<%=contextPath%>/project/task/taskTab.jsp?taskId="+seqId+"&projId="+projId;
    newWindow(url,1100,600,"info");
}
function getParam() {
	  var queryParam = $("form1").serialize();
	  return queryParam;
	}
function getDate(cellData, recordIndex, columInde){
	   return cellData.substring(0,10);
	}
	function getTime(cellData, recordIndex, columInde){
	   return cellData+"工作日";
	}
 function getGroup(){
	  if(!pageMgr){
	    pageMgr = new T9JsPage(cfgs);
	    pageMgr.show();
	  }
	  else{
	    pageMgr.search();
	  }
	} 
 /*
 * 打开新窗口
 * 
 */
 function newWindow(url, width, height, nameStr) {
  var defaultName = "newWindos";
  if (nameStr) {
    defaultName = nameStr;
  }
  var locX = (screen.width - width) / 2;
  var locY = (screen.height - height) / 2;
  if (newWin) {
    if (!newWin.closed)
      newWin.close();
  }
  newWin = window
      .open(
          url,
          defaultName,
          "height="
              + height
              + ",width="
              + width
              + ",status=1,toolbar=no,menubar=no,location=no,scrollbars=yes, top="
              + locY + ", left=" + locX + ", resizable=yes");
 }
 function getInfo(cellData, recordIndex, columInde){
	 var taskId = this.getCellData(recordIndex,"seqId");
	 return "<a href=\"javascript:getDetailInfo("+taskId+");\" >"+cellData+"</a>"
 }
 function getDetailInfo(taskId){
	 var str="<table width=\"80%\" align=\"center\" class=\"TableList\" border=\"0\">";
	 var url= "<%=contextPath%>/t9/project/task/act/T9TaskAct/getTaskInfo.act?taskId="+taskId;
	 var rtJson = getJsonRs(url);
	 if(rtJson.rtState == "0"){
		 var data=rtJson.rtData;
			str +="<tr><td  nowrap class=\"TableContent\">任务序号：</td><td class=\"TableData\" >"+data.taskNo+"</td></tr>"
			 		 + "<tr><td nowrap class=\"TableContent\">任务名称：</td><td class=\"TableData\" >"+data.taskName+"</td></tr>"
			 		 + "<tr><td nowrap class=\"TableContent\">上级任务：</td><td class=\"TableData\" >"+data.parentTask+"</td></tr>"
			 		 + "<tr><td nowrap class=\"TableContent\">前置任务：</td><td class=\"TableData\" >"+data.preTask+"</td></tr>"
			 		 + "<tr><td nowrap class=\"TableContent\">任务计划周期：</td><td class=\"TableData\" >"+data.taskStartTime.substring(0,10)+"至"+data.taskEndTime.substring(0,10)+"</td></tr>"
			 		 + "<tr><td nowrap class=\"TableContent\">任务工时：</td><td class=\"TableData\" >"+data.taskTime+"</td></tr>"
			 		 + "<tr><td nowrap class=\"TableContent\">任务描述：</td><td class=\"TableData\" >"+data.taskDescription+"</td></tr>"
		}
		$("detail_body").innerHTML=str+"</table>";
		ShowDialog('detail')
} 
</script>
</head>
<body onload="doInit()">
<form action="" id="form1" name="form1">
<table border="0" width="100%" cellspacing="0" cellpadding="3" class="small">
  <tr>
    <td class="small"><img src="<%=contextPath %>/core/funcs/system/filefolder/images/edit.gif" WIDTH="22" HEIGHT="20" align="absmiddle">
    	<span class="big3" id="modelName">我的任务</span>
    	<span id="modelNameInfo"></span>
    	<select id="range" name="range">
			<option value="0" selected="selected">--时间范围--</option>
			<option value="1">本周任务</option>
			<option value="2">本月任务</option>
			<option value="3">未来任务</option>
			</select>
      <select id="projlist" name="projlist">
			<option value="" selected="selected">--参与项目列表--</option>
					</select>
					<input type="hidden" value="0" id="flag" name="flag"> 
					<input type="button"  value="查询" class="BigButton" onClick="getGroup();"> &nbsp;&nbsp; &nbsp;
    </td>
  </tr>
</table>
 </form>
 <div id="listContainer">
 </div>
<div id="msrg">
</div>
<div id="overlay"></div>
<div id="detail" class="ModalDialog" style="width:550px;">
  <div class="header"><span id="title" class="title">项目问题详情</span><a class="operation" href="javascript:HideDialog('detail');"><img src="../images/close.png"/></a></div>
  <div id="detail_body" class="body">
  </div>
  <div id="footer" class="footer">
    <input class="BigButton" onclick="HideDialog('detail')" type="button" value="关闭"/>
  </div>
</div>
</body>
</html>