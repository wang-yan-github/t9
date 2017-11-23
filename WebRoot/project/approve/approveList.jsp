<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>项目审批记录</title>
<link rel="stylesheet" href = "<%=cssPath%>/style.css">
<link rel="stylesheet" href="<%=cssPath%>/page.css">
<link rel="stylesheet" href="<%=cssPath%>/cmp/Calendar.css">
<script type="text/javascript" src="<%=contextPath %>/core/js/prototype.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/datastructs.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/sys.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/smartclient.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/cmp/page.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/Calendarfy.js"></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/cmp/select.js" ></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/orgselect.js" ></script>
<script type="text/javascript" src="<%=contextPath %>/project/js/util.js"></script>
<script> 
var pageMgr = null;

function doInit(){
  var url = "<%=contextPath%>/t9/project/project/act/T9ProjectAct/getApproveList.act";
  var cfgs = {
    dataAction: url,
    container: "listContainer",
    sortIndex: 1,
    sortDirect: "desc",
    colums: [
       {type:"hidden", name:"seqId", text:"顺序号", dataType:"int"},
       {type:"data", name:"projNum",  width: '15%', text:"项目编号" ,align: 'center'},
       {type:"data", name:"projName",  width: '15%', text:"项目名称" ,align: 'center',render:showInfo},
       {type:"data", name:"userName",  width: '12%', text:"申请人" ,align: 'center'},
       {type:"data", name:"projStartTime",  width: '12%', text:"开始时间" ,align: 'center',render:getTime},
       {type:"data", name:"projEndTime",  width: '12%', text:"结束时间" ,align: 'center',render:getTime},
       {type:"data", name:"projActEndTime",  width: '12%', text:"实际结束时间" ,align: 'center',render:getActTime}]
  };
  pageMgr = new T9JsPage(cfgs);
  pageMgr.show();
  var total = pageMgr.pageInfo.totalRecord;
  if(total){
    $('listContainer').style.display="";
    var mrs = " 共 " + total + " 条记录 ！";
    showCntrl('delOpt');
  }else{
    WarningMsrg('无审批记录', 'msrg');
  }
}

/* 
function getParam(){
	  var queryParam = $("form1").serialize();
	  return queryParam;
	} */

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
	  str="<a href='javascript:recoveryProj("+projId+")'>回复执行</a>";
  }else{
	  str="<a href='index.jsp?projId="+projId+"'>项目变更</a></span><span style='margin-left:20px;'><a href='javascript:endProj("+projId+")'>结束</a>";
  }
  return str;
}

function showInfo(cellData, recordIndex, columIndex){
	var projId=this.getCellData(recordIndex,"seqId");
  var str="<a href='../proj/basicInfo/index.jsp?projId="+projId+"'>"+cellData+"</a>"
  return str;
}


function getTime(cellData, recordIndex, columIndex){
  if(!cellData){
    return "";
  }
  return cellData.substring(0,10);
}
function getActTime(cellData, recordIndex, columIndex){
  if(!cellData){
    return "<font color=green>尚未结束</font>";
  }
  return cellData.substring(0,10);
}

</script>
</head>
<body topmargin="5" onload="doInit()">
<br/>
<table border="0" width="100%" cellspacing="0" cellpadding="3" class="small">
 <tr>
   <td class="Big"><img src="../images/project.gif" align="absMiddle"><span class="big3">&nbsp;项目审批</span>
   </td>
 </tr>
</table>
<br/>
<div id="listContainer" style="display:none;width:100;">
</div>
<div id="delOpt" style="display:none">
<table class="TableList" width="100%">
</table>
</div>

<div id="msrg">
</div>
</body>
</html>