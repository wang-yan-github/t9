<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header.jsp" %>
<%
	String stationId = request.getParameter("stationId");
	if(stationId == null){
	  stationId = "0";
	}
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>管理区域</title>
<link rel="stylesheet" href = "<%=cssPath%>/style.css">
<link rel="stylesheet" href="<%=cssPath%>/page.css">
<script type="text/javascript" src="<%=contextPath %>/core/js/prototype.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/datastructs.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/sys.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/smartclient.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/cmp/page.js"></script>
<script type="text/javascript" src="<%=contextPath %>/cms/area/js/util.js"></script>
<script type="text/javascript" src="<%=contextPath %>/cms/area/js/areaLogic.js"></script>
<script> 
var pageMgr = null;
var stationId = <%=stationId %>;
function doInit(){
 	var requestURLStr = contextPath + "/t9/cms/template/act/T9TemplateAct/selectStationName.act";
 	var rtJson = getJsonRs(requestURLStr);
 	if(rtJson.rtState == "1"){
 	  alert(rtJson.rtMsrg); 
 	  return ;
 	}
 	var prcs = rtJson.rtData;
 	var selects = document.getElementById("stationId");
 	for(var i = 0; i< prcs.length; i++){
 	  var prc = prcs[i];
 	  var option = document.createElement("option"); 
 	  option.value = prc.seqId; 
 	  option.innerHTML = prc.stationName; 
 	  selects.appendChild(option);
 	}
 	$('stationId').value = <%=stationId %>;
  var url = "<%=contextPath%>/t9/cms/area/act/T9AreaAct/getAreaList.act?stationId=<%=stationId %>";
  var cfgs = {
    dataAction: url,
    container: "listContainer",
    sortIndex: 1,
    sortDirect: "desc",
    colums: [
       {type:"selfdef", text:"选择", width: '5%', render:checkBoxRender},
       {type:"hidden", name:"seqId", text:"顺序号", dataType:"int"},
       {type:"data", name:"areaName",  width: '20%', text:"区域名称" ,align: 'center'},
       {type:"data", name:"areaFileName",  width: '20%', text:"区域文件名" ,align: 'center'},
      // {type:"data", name:"columnNameStr",  width: '20%', text:"所属栏目" ,align: 'center' ,render: templateTypeName},
      // {type:"hidden", name:"colunmIdStr", text:"栏目Id"},
       {type:"hidden",name:"stationId",text:"站点ID",dataType:"int"},
       {type:"selfdef", text:"操作", width: '15%',render:opts}]
  };
  pageMgr = new T9JsPage(cfgs);
  pageMgr.show();
  var total = pageMgr.pageInfo.totalRecord;
  if(total){
    showCntrl('listContainer');
    var mrs = " 共 " + total + " 条记录 ！";
    showCntrl('delOpt');
  }else{
    WarningMsrg('无区域信息', 'msrg');
  }
}

function stationChange(){
  location.href = "<%=contextPath%>/cms/area/manage.jsp?stationId="+$('stationId').value;
}
</script>
</head>
<body topmargin="5" onload="doInit()">
<table border="0" width="100%" cellspacing="0" cellpadding="3" class="small">
 <tr>
   <td class="Big"><img src="<%=imgPath%>/notify_open.gif" align="absMiddle"><span class="big3">&nbsp;管理区域&nbsp; </span>
     <select name="stationId" id="stationId" style="width: 150px;" onchange="stationChange()"><option value="0">所有站点</option></select>
   </td>
 </tr>
</table>
<br>
<div id="listContainer" style="display:none;width:100;">
</div>
<div id="delOpt" style="display:none">
<table class="TableList" width="100%">
<tr class="TableControl">
      <td colspan="19">
         <input type="checkbox" name="checkAlls" id="checkAlls" onClick="checkAll(this);"><label for="checkAlls">全选</label> &nbsp;
         <a href="javascript:deleteAll();" title="删除所选记录"><img src="<%=imgPath%>/delete.gif" align="absMiddle">删除所选记录</a>&nbsp;
      </td>
 </tr>
</table>
</div>

<div id="msrg">
</div>
</body>
</html>