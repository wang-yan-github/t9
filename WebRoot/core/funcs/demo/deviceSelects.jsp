<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*" %>
<%@ page import="t9.core.data.T9RequestDbConn" %>
<%@ page import="t9.core.global.T9BeanKeys" %>
<%@ page import="t9.core.util.db.T9DBUtility" %>
<%@ include file="/core/inc/header.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>印章管理</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="<%=cssPath %>/cmp/tab.css" type="text/css" />
<link rel="stylesheet" href = "<%=cssPath%>/cmp/Calendar.css">
<link rel="stylesheet" href = "<%=cssPath%>/style.css">
<link rel="stylesheet" href = "<%=cssPath%>/cmp/tree.css">
<link rel="stylesheet" href="<%=cssPath%>/page.css">
<script type="text/javascript" src="<%=contextPath %>/core/js/prototype.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/tab.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/datastructs.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/sys.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/cmp/page.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/cmp/Menu.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/smartclient.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/cmp/Calendarfy.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/funcs/demo/js/sealManageUtil.js"></script>
<script type="text/javascript">
function check_all()
{
	if(document.all("email_select") == "null" || document.all("email_select") == null){
		return;
		}
 for (i=0;i<document.all("email_select").length;i++)
 {
   if(document.all("allbox").checked)
      document.all("email_select").item(i).checked=true;
   else
      document.all("email_select").item(i).checked=false;
 }

 if(i==0)
 {
   if(document.all("allbox").checked)
      document.all("email_select").checked=true;
   else
      document.all("email_select").checked=false;
 }
 
 
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
  return strTmp;
}
function check_one(el)
{ 
   if(!el.checked) {
     var s = window.dialogArguments.document.getElementById("user").value;
     var s1 = window.dialogArguments.document.getElementById("userDesc").value;
     window.dialogArguments.document.getElementById("user").value = getOutofStr(s, el.value) ;
     window.dialogArguments.document.getElementById("userDesc").value = getOutofStr(s1, el.title) ; 
   } else {
     window.dialogArguments.document.getElementById("user").value += el.value + ",";
     window.dialogArguments.document.getElementById("userDesc").value += el.title + ",";
   }
}

function doInit(){
  var str = window.dialogArguments.document.getElementById("user").value;
  var url = "<%=contextPath%>/t9/core/funcs/demo/act/T9SealAct/getDevice.act";
  var rtJson = getJsonRs(url, "seqId="+seqId);
  if (rtJson.rtState == "0") {
    for (var i = 0 ;i<rtJson.rtData.length;i++) {
      var d = rtJson.rtData[i];
      var seqId = d.SEQ_ID;
      var device = d.DEVICE_NAME;
      var userName = d.USER_NAME;
      var t = "";
      
      if (findId (str , seqId)) {
        t = "checked";
      }
      var tr = new Element("tr");
      var td1 = new Element("td");
      td1.update("&nbsp;<input type=\"checkbox\" "+t+" name=\"email_select\" value=\""+seqId+"\" title=\""+device+"\" onClick=\"check_one(this);\">");
      tr.appendChild(td1);
      var td2 = new Element("td");
      td2.update(device);
      tr.appendChild(td2);
      var td3 = new Element("td");
      td3.update(userName);
      tr.appendChild(td3);
      $('list').appendChild(tr);
    }
  }else {
    alert(rtJson.rtMsrg); 
  }
}


function findId (str , s) {
  if (!str) {
    return false; 
  } else {
    var ss = str.split(",");
    for (var i = 0 ;i < ss.length ; i++) {
      var tmp = ss[i] ;
      if (tmp && tmp == s) {
        return true;
      }
    }
    return false;
  }
}
</script>
</head>

<body topmargin="5"  onload="doInit()">

<table class="TableList" width="95%" align="center">
  <tr class="TableHeader">
	<td nowrap align="center" width=20%>选择</td>
      <td nowrap align="center">设备名称</td>
      <td nowrap align="center">所有人</td> 
  </tr>
<tbody id="list">

</tbody><!-- 
<tr class="TableControl">
<td colspan="3">
<div style="float:left;">
   <input type="checkbox" name="allbox" id="allbox_for" onClick="check_all();">
   <label for="allbox_for">全选</label>&nbsp;
</div>
</td></tr> -->
</table><br>
 <div id="footer" class="footer">
    <input class="BigButton" onclick="window.close()" type="button" value="关闭"/>
  </div>

</body>
</html>
 
