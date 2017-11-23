<%@ page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>员工关怀列表页面</title>

<link rel="stylesheet" href = "<%=cssPath%>/style.css">
<link rel="stylesheet" href="<%=cssPath%>/page.css">

<script type="text/Javascript" src="<%=contextPath%>/core/js/datastructs.js"></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/sys.js"></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/prototype.js"></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/smartclient.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/cmp/page.js"></script>
<script type="text/javascript" src="<%=contextPath%>/test/demo/js/util.js"></script>
<script type="text/javascript" src="<%=contextPath%>/test/demo/js/staffCareLogic.js"></script>
<script type="text/javascript">

var pageMgr = null;
function doInit(){
  var url = "<%=contextPath%>/t9/test/demo/act/T9DemoAct/getStaffCareListJson.act";
  var cfgs = {
    dataAction: url,
    container: "listContainer",
    sortIndex: 1,
    sortDirect: "desc",
    colums: [
       {type:"hidden", name:"seqId", text:"顺序号", dataType:"int"},
       {type:"data", name:"careType",  width: '20%', text:"关怀类型" ,align: 'center' },
       {type:"data", name:"byCareStaffs",  width: '20%', text:"被关怀员工" ,align: 'center' ,render:getUserName},
       {type:"data", name:"careDate",  width: '10%', text:"关怀日期" ,align: 'center' ,render:splitDate},
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
    WarningMsrg('无员工关怀信息', 'msrg');
  }
}

function splitDate(cellData, recordIndex, columIndex){
  return cellData.substr(0, 10);
}

function getUserName(cellData, recordIndex, columIndex){
  var url = "<%=contextPath%>/t9/test/demo/act/T9DemoAct/getUserName.act?seqId="+cellData;
  var rtJsons = getJsonRs(url);
  if(rtJsons.rtState == '0'){
    var prc = rtJsons.rtData;
    return prc.userName;
  }
  else{
    return "";
  }
}

</script>
</head>
<body onLoad="doInit();">

<div id="listContainer" style="display:none;width:100;"></div>

<div id="msrg"></div>
</body>
</html>
