<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>待审批项目</title>
<link rel="stylesheet" href = "<%=cssPath%>/style.css">
<link rel="stylesheet" href="<%=cssPath%>/page.css">
<link rel="stylesheet" href="<%=cssPath%>/cmp/Calendar.css">
<link rel="stylesheet" href="<%=contextPath%>/project/css/dialog.css">
<script type="text/javascript" src="<%=contextPath %>/core/js/prototype.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/datastructs.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/sys.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/smartclient.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/cmp/page.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/Calendarfy.js"></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/cmp/select.js" ></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/orgselect.js" ></script>
<script type="text/javascript" src="<%=contextPath %>/project/js/jquery.min.js"></script>
<script type="text/javascript" src="<%=contextPath %>/project/js/dialog.js"></script>
<script type="text/javascript" src="<%=contextPath %>/project/js/util.js"></script>
<script> 
jQuery.noConflict();
var pageMgr = null;

function doInit(){
  var url = "<%=contextPath%>/t9/project/project/act/T9ProjectAct/getNoApproveList.act";
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
       {type:"data", name:"projStatus", width:'8%',text:"状态", dataType:"int",align:'center',render:showStatus},
       {type:"selfdef", text:"操作", width: '10%',align:'center',render:opts}]
  };
  pageMgr = new T9JsPage(cfgs);
  pageMgr.show();
  var total = pageMgr.pageInfo.totalRecord;
  if(total){
    $('listContainer').style.display="";
    var mrs = " 共 " + total + " 条记录 ！";
    showCntrl('delOpt');
  }else{
    WarningMsrg('无待批项目', 'msrg');
  }
}


function approve(projId,pass)
{
	if(pass==1)
	  	var msg="确认要审批通过此项目申请吗？请填写审批意见：";
	else
		var msg="确认要驳回此项目申请吗？请填写驳回理由：";
	$("confirm").innerHTML="<font color=red>"+msg+"</font>";
	$("projId").value=projId;
	$("pass").value=pass;
  	ShowDialog('comment');
}


function subApprove()
{
	if(check_form()){
		var url= contextPath + "/t9/project/project/act/T9ProjectAct/subApprove.act";
		var rtJson = getJsonRs(url,mergeQueryString($("form1")));
		if(rtJson.rtState == "0"){
	    window.location.href="noApproveList.jsp";
	  }else{
			alert(rtJson.rtMsrg);
	  } 
	}
}

function check_form()
{
   if(document.form1.content.value.trim()=="")
   { alert("请填写审批意见！");
     return (false);
   }

   return (true);
}
function opts(cellData, recordIndex, columIndex){
	var status=this.getCellData(recordIndex,"projStatus");
	var projId=this.getCellData(recordIndex,"seqId");
  var str=""
	  str="<span><a href='javascript:approve("+projId+",1)'>通过</a></span><span style='margin-left:20px;'><a href='javascript:approve("+projId+",0)'>拒绝</a></span>";
  return str;
}

function showInfo(cellData, recordIndex, columIndex){
	var projId=this.getCellData(recordIndex,"seqId");
//  var str="<a href='../proj/basicInfo/index.jsp?projId="+projId+"'>"+cellData+"</a>"
		 var str="<a href='#this' onClick=showOpen("+projId+")>"+cellData+"</a>"
  return str;
}
function showOpen(seqId) {    
	var url = "../proj/basicInfo/index.jsp?projId="+seqId;
	var iWidth=800; //窗口宽度       
	var iHeight=800;//窗口高度          
	var iTop=(window.screen.height-iHeight)/2;          
	var iLeft=(window.screen.width-iWidth)/2;          
	window.open(url,"Detail","Scrollbars=no,Toolbar=no,Location=no,Direction=no,Resizeable=no,     Width="+iWidth+" ,Height="+iHeight+",top="+iTop+",left="+iLeft);        
	}
function showStatus(cellData, recordIndex, columIndex){
 	if(cellData=='0'){
 		return "立项中";
 	}else if(cellData=='1'){
 		return "待审批";
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

/* function deleteProj(projId){
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
	
} */

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

<div id="overlay"></div>
<div id="comment" class="ModalDialog" style="width:550px;">
  <div class="header"><span id="title" class="title">审批意见</span><a class="operation" href="javascript:HideDialog('comment');"><img src="../images/close.png"/></a></div>
  <form name="form1" id="form1" method="post">
  <div id="detail_body" class="body">
  <span id="confirm"></span>
  <textarea id="content" name="content" cols="60" rows="5" style="overflow-y:auto;" class="BigInput" wrap="yes"></textarea>
  </div>
  <input type="hidden" name="projId" id="projId">
  <input type="hidden" name="pass" id="pass">
  <div id="footer" class="footer">
    <input class="BigButton" type="button" value="确定" onclick="subApprove()"/>
    <input class="BigButton" onclick="HideDialog('comment')" type="button" value="关闭"/>
  </div>
  </form>
</div>
</body>
</html>