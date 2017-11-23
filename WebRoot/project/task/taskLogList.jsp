<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header.jsp" %>
<%
	String taskId=request.getParameter("taskId");
	if(taskId==null){
	  taskId="";
	}	%>

<%@page import="java.util.Date"%><html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>任务办理</title>
<link rel="stylesheet" href="<%=cssPath%>/page.css">
<link rel="stylesheet" href="<%=cssPath%>/style.css">
<link rel="stylesheet" href="<%=cssPath%>/cmp/Calendar.css">
<script type="text/javascript" src="<%=contextPath%>/core/js/prototype.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/cmp/tab.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/datastructs.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/sys.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/smartclient.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/cmp/Calendarfy.js"></script>
<script type="text/javascript" src="/t9/core/js/cmp/swfupload/handlers.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/cmp/Menu.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/cmp/page.js"></script>
<script type="text/javascript"
	src="<%=contextPath%>/core/js/cmp/attachMenu.js"></script>
<script type="text/javascript"
	src="<%=contextPath%>/core/js/cmp/attach.js"></script>
<script type="text/javascript">
var pageMgr=null;
var taskId='<%=taskId%>';

function doInit(){
	$('taskId').value=taskId;
	var url = "<%=contextPath%>/t9/project/task/act/T9TaskAct/getTaskInfo.act?taskId="+taskId;
    var rtJson = getJsonRs(url);
    if (rtJson.rtState == "0") {
        $('modelName').innerHTML="进度日志详情（"
        	+rtJson.rtData.taskName
        +"["
        +rtJson.rtData.taskStartTime.substring(0,10)
        +"至"
        +rtJson.rtData.taskEndTime.substring(0,10)
        +"]"
        ;
    }
  var url = "<%=contextPath%>/t9/project/task/act/T9ProjTaskLogAct/getTaskLogTree.act?taskId="+taskId;
  var cfgs = {
    dataAction: url,
    container: "listContainer",
    sortIndex: 1,
    sortDirect: "desc",
    colums: [
             {type:"hidden", name:"seqId", text:"顺序号", dataType:"int"},
             {type:"data", name:"logUser",  width: '15%', text:"任务负责人" ,align: 'center'},
             {type:"data", name:"logContent",  width: '30%', text:"内容" ,align: 'center'},
             {type:"hidden", name:"attachmentId",align: 'center'},
             {type:"data", name:"attachmentName",  width: '20%', text:"附件" ,align: 'center',render:getAttr},
             {type:"data", name:"logTime",  width: '15%', text:"日志时间" ,align: 'center'},
             {type:"data", name:"percent",  width: '10%', text:"进度百分比" ,align: 'center',render:getPercent}
            // {type:"selfdef",width: '10%', text:"操作" ,align: 'center',render:opts}
             ]
        };
  pageMgr = new T9JsPage(cfgs);
  pageMgr.show();
	var  selfdefMenu = {
        	office:["downFile", "dump", "read", "readNoPrint", "edit", "setSign", "deleteFile"], 
	        img:["downFile", "dump", "play"],  
	        music:["downFile", "dump", "play"],  
			    video:["downFile", "dump", "play"],
			    pdf: ["readpdf", "downFile", "dump"], 
			    others:["downFile", "dump"]
				};
  var  returnAttId = document.getElementsByName("returnAttId");
  var  returnAttName = document.getElementsByName("returnAttName");
  var  returnAttSeqId = document.getElementsByName("returnAttSeqId");
  for(var i=0; i<returnAttId.length; i++){
	  attachMenuSelfUtil("attr_"+returnAttSeqId[i].value,"proj_file",returnAttName[i].value ,returnAttId[i].value, returnAttSeqId[i].value,'',returnAttSeqId[i].value,selfdefMenu);
  }
}

function getPercent(cellData, recordIndex, columInde){
	return cellData+"%";
}
function getAttr(cellData, recordIndex, columInde){
	if(cellData!=""&&cellData!="null"){
	var seqId = this.getCellData(recordIndex,"seqId");
	var attachmentId = this.getCellData(recordIndex,"attachmentId");
	return "<input type = 'hidden' id='returnAttId_" + seqId + "' value='"+attachmentId+"' name='returnAttId'></input>"
	+ "<input type = 'hidden' id='returnAttName_" + seqId + "' value='"+cellData+"' name='returnAttName'></input>"
	+ "<input type = 'hidden' id='returnAttSeqId' name='returnAttSeqId' value='"+seqId+"'></input>"
	+ "<div id='attr_" + seqId + "'></div>";
	}else{
		return "";
		}
}
/*
* 增加操作列
*/
/* function opts(cellData, recordIndex, columInde){
   var seqId = this.getCellData(recordIndex,"seqId");
   var edit="<a href=\"javascript:getLog("+seqId+");\" >修改</a>&nbsp&nbsp";
   var finishTask="<a href=\"javascript:selectInfo("+seqId+");\" >删除</a>&nbsp&nbsp";
   return edit+finishTask;
} */

function  jugeFile(){
	  var formDom  = document.getElementById("formFile");
	  var inputDoms  = formDom.getElementsByTagName("input"); 
	  for(var i=0; i<inputDoms.length; i++){
	    var idval = inputDoms[i].id;
	    if(idval.indexOf("ATTACHMENT")!=-1){
	      return true;
	    }
	  } 
	  return false; 
}
</script>
</head>
<body onload="doInit()">
<table border="0" width="100%" cellspacing="0" cellpadding="3" class="small">
  <tr>
    <td class="small"><img src="<%=contextPath %>/core/funcs/system/filefolder/images/edit.gif" WIDTH="22" HEIGHT="20" align="absmiddle">
    	<span class="big3" id="modelName"></span>
    </td>
  </tr>
</table>
 <div id="listContainer">
 </div>
<div id="msrg">
</div>
<input type="hidden" id="taskId">
</body>	
</html>