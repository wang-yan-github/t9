<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header.jsp" %>


<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<%
String gid="";
java.util.Enumeration params = request.getParameterNames(); 
while(params.hasMoreElements()) { 
String current_param = (String)params.nextElement(); 
String[] values=request.getParameterValues(current_param); 
for (int i=0;i<values.length;i++) 
  gid= values[i];
} 
%>


<style type="text/css">

body{
  background-color:#E8EBF2;
} 
</style>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href = "<%=cssPath%>/style.css">
<script type="text/Javascript" src="<%=contextPath%>/core/js/datastructs.js" ></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/sys.js" ></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/prototype.js" ></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/smartclient.js" ></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/cmp/Menu.js" ></script>
<script type="text/Javascript"> 
var gid=<%=gid%>;

function doInit(){
  getUserList(gid);
}
  //setInterval(reload, 12000);

  function getUserList(gId){
	  var url = "<%=contextPath%>/t9/core/funcs/system/ispirit/n12/org/act/T9IsPiritOrgAct/getGroupUserList.act";
	  var rtJson = getJsonRsAsyn(url,"groupId="+gId , asyn);
  }
function asyn(rtJson) {
	var text = "";
	if (rtJson.rtState == "0") {
		  var onLineUser=rtJson.rtData.onLine;
		  var offLineUser=rtJson.rtData.offLine;
		  //在线人员
		  for(var i=0;i<onLineUser.length;i++){
			  text+= "<div style='cursor:hand' onclick=\"window.external.OA_SMS('"+onLineUser[i].uId+"', '"+onLineUser[i].uName+"','SEND_MSG');\">"+onLineUser[i].uIcon+onLineUser[i].uName+"</div>";
		  }
		  //离线人员
		  for(var i=0;i<offLineUser.length;i++){
		       text+= "<div style='cursor:hand' onclick=\"window.external.OA_SMS('"+offLineUser[i].uId+"', '"+offLineUser[i].uName+"','SEND_MSG');\">"+offLineUser[i].uIcon+offLineUser[i].uName+"</div>";
		  }
		  $("userList").innerHTML=text;
	  }else{
		  
	  }
	setTimeout(doInit, 12000);
}

</script>
<body style=" background-color:#E8EBF2; " onload="doInit()" >
<div  id="userList"></div>
</body>
</html>