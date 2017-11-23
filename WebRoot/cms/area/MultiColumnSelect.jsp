<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header.jsp"%>
<%
	String stationId = request.getParameter("stationId");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>设置栏目</title>
<link rel="stylesheet" href ="<%=cssPath %>/style.css">
<link rel="stylesheet" href ="<%=cssPath %>/cmp/orgselect.css">
<script type="text/Javascript" src="<%=contextPath%>/core/js/datastructs.js" ></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/sys.js" ></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/prototype.js" ></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/smartclient.js" ></script>
<script type="text/javascript"><!--
var selectedColor = "rgb(0, 51, 255)";
var ColumnId,ColumnName;
var stationId = "<%=stationId%>";
function doInit(){
  var parentWindowObj = window.dialogArguments;

  var columnRetNameArray = parentWindowObj["columnRetNameArray"];
  if (columnRetNameArray && columnRetNameArray.length == 2) {
    var columnCntrl = columnRetNameArray[0];
    var columnDescCntrl = columnRetNameArray[1];
    ColumnId = parentWindowObj.$(columnCntrl);
    ColumnName = parentWindowObj.$(columnDescCntrl);
  }else {
	  ColumnId = parentWindowObj.$("columnId");
    ColumnName = parentWindowObj.$("columnDesc");
  }
  url = contextPath + "/t9/cms/area/act/T9AreaAct/getColumns.act?stationId=" + stationId;
  var json = getJsonRs(url);
  if(json.rtState == "0"){
	  columnList = json.rtData;
	  addDiv(columnList,0);
  }
  setSelected(ColumnId.value.split(","));
}

function setSelected(selectedColumn){
  for(var i = 0 ;i < selectedColumn.length ;i++){
    var selectedDiv = $("Div-" + selectedColumn[i]);
    if(selectedDiv){
      selectedDiv.isChecked = true;
      selectedDiv.className = "item select";
	  }
  }
}

function addDiv(columns,space){
  var divs = $("columnsDiv");
  if(columns.length > 0 ){
    for(var i = 0 ; i < columns.length ; i++){
      var column = columns[i];
      	if(column.hasChild==false){
        	var div = createDiv(column);
        	divs.appendChild(div);
      	}else{
      		space++;
      	    url = contextPath + "/t9/cms/area/act/T9AreaAct/getChildColumn.act?columnId=" + column.columnId+"&space="+space;
      	    var json = getJsonRs(url);
      	    if(json.rtState == "0"){
            	var div = createDiv(column);
            	divs.appendChild(div);
      	  	 	childColumnList = json.rtData;
      	  	 	addDiv(childColumnList,space);
            	//document.getElementById("Div-"+column.columnId).style.display="none";
      	    }
      	    space=0;
      	}

    }
  }else{
  	$('hasColumn').hide();
	$('noColumn').show();
  }
}
function createDiv(column){
  var div = new Element('div',{'class':'item'}).update(column.columnName)
  div.id = "Div-" + column.columnId ;
  div.onmouseout = function(){
    if(!this.isChecked){
      this.className = "item";
	  }else {
      this.className = "item select";
    }
  }
  div.onmouseover = function(){
    if(!this.isChecked){
      this.className = "item";
	  }else {
		  this.className = "item select";
	  }
  }
  div.onclick = function(){
    var columnStr = ColumnId.value;
	var columnNameStr = ColumnName.value;
    if (columnNameStr && columnNameStr.trim) {
    	columnNameStr = columnNameStr.trim();
    }

    var columnId = this.id.substr(4);
    var columnName = this.innerHTML.trim().split("├")[1];
    if(this.isChecked){
      ColumnId.value = getOutofStr(columnStr , columnId);
      ColumnName.value = getOutofStr(columnNameStr , columnName);
	    this.isChecked = false;
	    this.className = "item";
    }else{
      if (ColumnId.value.length > 0) {
        ColumnId.value += "," + columnId;
      }else {
        ColumnId.value = columnId;
      }
      if(ColumnName.value){
        ColumnName.value += "," + columnName;
      }else{
        ColumnName.value = columnName;
      }
      this.isChecked = true;
      this.className = "item select";
    }
    
/* 	    url = contextPath + "/t9/cms/area/act/T9AreaAct/getChildColumn.act?&columnId=" + column.columnId+"&space="+0;
  	    var json = getJsonRs(url);
  	    if(json.rtState == "0"){
        	//var div = createDiv(column);
        	//divs.appendChild(div);
  	  	 	childColumnList = json.rtData;
  	  	 	addDiv(childColumnList,0);
  	    }*/
  }
  return div;
}

function selectedAll(){
  var divs = $('columnsDiv').getElementsByTagName('div');
  var columnIdStr = ColumnId.value;
  if(ColumnName.value){
    var columnNameStr = ColumnName.value;
    if (columnNameStr && columnNameStr.trim) {
      columnNameStr = columnNameStr.trim();
    }
  }else{
    var columnNameStr = "";
  }
  if (columnNameStr) {
    columnNameStr += ",";
  }
  if (columnIdStr) {
    columnIdStr += ",";
  }
  for(var i = 0 ;i < divs.length ;i++){
		var div = divs[i];
		if(!div.isChecked){
		  var columnId = div.id.substr(4);
	 	  var columnName = div.innerHTML.trim().split("├")[1];
	 	  div.isChecked = true;
      div.className = "item select";
      columnIdStr +=  columnId + ',';
      columnNameStr += columnName + ',';
		}
  }
  if (columnIdStr && columnIdStr.lastIndexOf(",") == columnIdStr.length - 1) {
	  columnIdStr = columnIdStr.substring(0, columnIdStr.length - 1);
  }
  if (columnNameStr && columnNameStr.lastIndexOf(",") == columnNameStr.length - 1) {
	  columnNameStr = columnNameStr.substring(0, columnNameStr.length - 1);
  }
  ColumnId.value = columnIdStr;
  ColumnName.value = columnNameStr;
}
function disSelectedAll(){
  var divs = $('columnsDiv').getElementsByTagName('div');
  var columnIdStr = ColumnId.value;
  
  if(!ColumnName.value){
    return ;
  }
  var columnNameStr = ColumnName.value;
  if (columnNameStr && columnNameStr.trim) {
	  columnNameStr = columnNameStr.trim();
  }
  for(var i = 0 ; i< divs.length ;i++){
    var div = divs[i];
  	if(div.isChecked){
  	  var columnId = div.id.substr(4);
   	  var columnName = div.innerHTML.trim().split("├")[1];
   	   columnIdStr = getOutofStr(columnIdStr , columnId);
      
   	  columnNameStr = getOutofStr(columnNameStr , columnName);
   	  div.isChecked = false;
    	div.className = "item";
	  }
  }
  ColumnId.value = columnIdStr;
  ColumnName.value = columnNameStr;
}
function getOutofStr(str, s){
  var aStr = str.split(',');
  var strTmp = "";
  var j = 0 ;//控制重名
  for(var i = 0 ;i < aStr.length ; i++){
    if(aStr[i] && (aStr[i] != s || j != 0)){
      strTmp += aStr[i] + ',';
    }else{
      if(aStr[i] == s){
    	  j = 1;
      }  
    }
  }
  if (strTmp && strTmp.lastIndexOf(",") == strTmp.length - 1) {
    strTmp = strTmp.substring(0, strTmp.length - 1);
  }
  return strTmp;
}
-->
</script>
</head>
<body onload="doInit()">
<div id="rightRole">
<div id="hasColumn" class="list">
<table class="TableTop" width="100%">
  <tr>
    <td class="left">
    </td>
    <td class="center" id="title">
      <div id="title" class="header" >选择栏目</div>
    </td>
    <td class="right">
    </td>
  </tr>
</table>
<div class="op">
  <input type="button" class="SmallButtonW" value="全部添加" onclick="selectedAll()">
  <input type="button" class="SmallButtonW" value="全部删除" onclick="disSelectedAll()">
  <input type="button" class="SmallButtonW" value="确定" onclick="window.close()"/>
</div>
<div id="columnsDiv">
</div>

</div>
<div id="noColumn" align="center" class="item" style="display:none;color:red">未定义栏目</div>
</div>
<div style="text-align: center;padding-top: 10px;">
  <input type=button class="SmallButtonW" value="确定" onclick="window.close()"/>
</div>
</body>
</html>