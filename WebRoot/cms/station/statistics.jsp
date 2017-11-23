<%@ page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false"%>
<%@ include file="/core/inc/header.jsp"%>
<%
  String data = request.getParameter("data") == null ? "" :  request.getParameter("data");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>统计分析</title>
<link rel="stylesheet" href="<%=cssPath%>/style.css">
<script type="text/javascript" src="<%=contextPath %>/core/js/prototype.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/datastructs.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/sys.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/smartclient.js"></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/orgselect.js" ></script>
<script type="text/Javascript" src="<%=contextPath %>/core/js/cmp/select.js"></script>
<script type="text/javascript" src="<%=contextPath %>/subsys/oa/hr/score/js/util.js"></script>
<script type="text/javascript" src="<%=contextPath%>/subsys/oa/examManage/examOnline/FusionCharts/FusionCharts.js"></script>
<script type="text/javascript">
var strXML = "111";
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
	query();
}
function getColumn(stationId){
	  var url = "<%=contextPath%>/t9/cms/column/act/T9ColumnAct/getAreaTemplate.act?stationId="+stationId;
		
	  var rtJson = getJsonRs(url);
		var selectObj = $("columnId");
		selectObj.length=0;
		if(rtJson.rtState == "0"){
			var prcs = rtJson.rtData;
		  for(var i = 0 ; i < prcs.length ; i++){
		    var prc = prcs[i];
		    var seqId = prc.columnId;
		    var columnName = prc.columnName;
		    var myOption = document.createElement("option");
		    myOption.value = seqId;
		    myOption.text = columnName;
		    selectObj.options.add(myOption, selectObj.options ? selectObj.options.length : 0);
		  }
		}
}
function changeStation(){
	var stationId=$("stationId").value;
	$("columnId").value="";
	$("columnDesc").value="";
}
function selectColumn(){
	var stationId=$("stationId").value;
	if(stationId=="0")
		alert("您选择的是所有站点，请选择相应站点！！");
	else{
	var url=contextPath+"/cms/area/MultiColumnSelect.jsp?stationId="+stationId;
	openDialogResize(url,360,400)
	}
	}
</script>
</head>
<body onload="doInit()">
 <form  method="post" name="form1" id="form1" >
 <div id="condition">
 <table border="0" width="100%" cellspacing="0" cellpadding="8" class="small">
  <tr>
    <td class="Big"><img src="/t9/core/styles/style1/img/edit.gif" align="absmiddle"><span class="big3"> 统计条件</span><br>
    </td>
  </tr>
</table>
	  <table class="TableBlock" width="600" align="center">
    <tr>
	    <td nowrap class="TableData">站点名称： </td>
        <td class="TableData">
          <select name="stationId" id="stationId" style="width: 130px;"onChange="changeStation()">
          <option value="0">所有站点</option>
          </select>
        </td>
    </tr>
	     <tr>
    <td nowrap class="TableData" align="left">栏目：</td>
    <td class="TableData" colspan="3">
      <input type="hidden" name="columnId" id="columnId" value="">
      <textarea cols=40 name="columnDesc" id="columnDesc" rows=2 class="BigStatic" wrap="yes" readonly></textarea>
        <a href="javascript:;" class="orgAdd" onClick="selectColumn();">添加</a>
      	<a href="javascript:;" class="orgClear" onClick="$('columnId').value='';$('columnDesc').value='';">清空</a></br>
      	<span style="color:red">（栏目为空，默认统计当前站点一级栏目的文章数）</span>
    </td>
  </tr>
    <tr align="center" class="TableControl">
      <td colspan="2" nowrap>
        <input type="button" value="确定" onclick="conditonQuery();" class="BigButton">&nbsp;&nbsp;
        <input type="reset" value="重填" class="BigButton">&nbsp;&nbsp;
      </td>
    </tr>
 </table>
</div>
</form>
  <center>
<!-- START Script Block for Chart FactorySum -->
<div id="FactorySumDiv" align="center">
 <br>
 <div id="chart1div" align="center">
  <div align="center" style="display:none" id="NoData">
  <table class="MessageBox" align="center" width="240">
  <tr>
  <td class="msg info"><div class='content' style='font-size:12pt'>没有统计数据</div></td>

  </tr>
  </table>
  </div>
</div>
<script type="text/javascript"> 
function query(){
 	var requestURLStr = contextPath + "/t9/cms/content/act/T9ContentAct/statistics.act";
 	var rtJson = getJsonRs(requestURLStr);
 	if(rtJson.rtState == 0){
	  var choseStr = "Column3D.swf";
	  var chart1 = new FusionCharts("<%=contextPath%>/subsys/oa/examManage/examOnline/FusionCharts/"+choseStr+"", "chart1Id", "800","400", "0", "0");
	  chart1.setTransparent("false");
	  chart1.setDataXML(rtJson.rtData.data);
	  chart1.render("chart1div");
 	} else {
 		 alert(rtJson.Mrsg);
		 $("NoData").style.display = 'block';
	 }
}

function conditonQuery(){
	 var url="<%=contextPath%>/t9/cms/content/act/T9ContentAct/getStatistics.act";
	 var rtJson = getJsonRs(url,mergeQueryString($("form1")));
	 	if(rtJson.rtState == 0){
	 		  var choseStr = "Column3D.swf";
	 		  var chart1 = new FusionCharts("<%=contextPath%>/subsys/oa/examManage/examOnline/FusionCharts/"+choseStr+"", "chart1Id", "800","400", "0", "0");
	 		  chart1.setTransparent("false");
	 		  chart1.setDataXML(rtJson.rtData.data);
	 		  chart1.render("chart1div");
	 	 	} else {
	 	 		 alert(rtJson.Mrsg);
	 			 $("NoData").style.display = 'block';
	 		 }
}
</script>
</center>
</body>
</html>
