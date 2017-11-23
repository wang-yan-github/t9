<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header.jsp" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>任务信息树</title>
<link rel="stylesheet" href="<%=cssPath%>/page.css">
<link rel="stylesheet" href="<%=cssPath%>/style.css">
<link rel="stylesheet" href="<%=cssPath%>/cmp/Calendar.css">
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

function doInit(){
	setDate();
  var url = "<%=contextPath%>/t9/project/project/act/T9ProjectAct/getProjLeftTree.act";
  var cfgs = {
    dataAction: url,
    container: "listContainer",
    sortIndex: 1,
    sortDirect: "desc",
    paramFunc: getParam,
    colums: [
       {type:"hidden", name:"seqId", text:"顺序号", dataType:"int"},
       {type:"data", name:"projName",  width: '50%', text:"项目名称" ,align: 'center'},
       {type:"data", name:"projStatus", text:"状态" ,align: 'center'}]
  };
  pageMgr = new T9JsPage(cfgs);
  pageMgr.show();
  $("pgPanel").style.display="none";
  var url1 = "<%=contextPath%>/t9/project/system/act/T9ProjSystemAct/getStyleList.act?classNo=PROJ_TYPE";
  var rtJson=getJsonRs(url1);
	if(rtJson.rtState==0){
    var selectObj = $("projType");
    for(var i=0;i<rtJson.rtData.size();i++){
    	  var opts = new Option(rtJson.rtData[i].classDesc);
    	  opts.value = rtJson.rtData[i].seqId;
        selectObj.add(opts);
        }
	}else{
		alert(rtJson.rtMsrg); 	
		}
}
function getParam() {
	  var queryParam = $("form1").serialize();
	  return queryParam;
	}
//设置日期控件
function setDate(){
	  var date1Parameters = {
	    inputId:'startDate',
	    property:{isHaveTime:false}
	    ,bindToBtn:'date1'
	  };
	  new Calendar(date1Parameters);

	  var date2Parameters = {
		  inputId:'endDate',
		  property:{isHaveTime:false}
		  ,bindToBtn:'date2'
	  };
	  new Calendar(date2Parameters);
	}
function checkForm(){
	  var sendDate1 = $("startDate").value;
	  if(sendDate1){
	    if(!isValidDateStr(sendDate1)){
	      alert("创建日期格式不对，应形如 2010-01-02");
	      $("startDate").focus();
	      $("startDate").select();
	      return false;
	    }
	  }
	  var sendDate2 = $("endDate").value;
	  if(sendDate2){
	    if(!isValidDateStr(sendDate2)){
	      alert("结束日期格式不对，应形如 2010-01-02");
	      $("endDate").focus();
	      $("endDate").select();
	      return false;
	    }
	  }
	  return true;
}
 function getGroup(){
	 if(checkForm()){
	  if(!pageMgr){
	    pageMgr = new T9JsPage(cfgs);
	    pageMgr.show();
	  }
	  else{
	    pageMgr.search();
	  }
	 }
	} 
</script>
</head>
<body onload="doInit()">
<form action="" id="form1" name="form1">
<table class="TableBlock" align="center" >
<tr>
          <td  class="TableContent">项目类型：
        <select id="projType" name="projType">
			<option value="" selected="selected">所有类型</option>
			</select>
      状态：
        <select id="projStatus" name="projStatus">
			<option value="" selected="selected">所有状态</option>
			<option value="1">立项中</option>
			<option value="2">审批中</option>
			<option value="3">进行中</option>
			<option value="4">已结束</option>
		</select>
 
      </td>
   
     </tr>
<tr>
    <td  class="TableData" >
        <input type="text" name="startDate" id="startDate" class="BigInput" size="17" maxlength="10" value="" >&nbsp;
        <img id="date1" src="<%=imgPath%>/calendar.gif" align="middle" border="0" style="cursor: hand"> 
       至
       <input type="text" name="endDate" id="endDate" class="BigInput" size="17" maxlength="10" value="" >&nbsp;
       <img id="date2" src="<%=imgPath%>/calendar.gif" align="middle" border="0" style="cursor: hand"> 
</td>
   </tr>
   <tr class="TableControl">
    <td colspan="6" align="center" >  &nbsp;   &nbsp;  &nbsp;
        <input type="button"  value="查询" class="BigButton" onClick="getGroup();"> &nbsp;&nbsp; &nbsp;
        <input type="button"  value="刷新" class="BigButton" onClick="location.reload();">
    </td>
   </tr>
 </table>
 </form>
 <div id="listContainer">
 </div>
<div id="msrg">
</div>
</body>
</html>