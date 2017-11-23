<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header.jsp" %>
<%@ page import="t9.core.global.T9RegistProps" %>
<%@ page import="t9.core.util.auth.T9RegistUtility" %>
<%@ page import="t9.core.util.SignProvider" %>
<html>
<head>
<title></title>
</head>
<%
response.setHeader("PRagma","No-cache"); 
response.setHeader("Cache-Control","no-cache"); 
response.setDateHeader("Expires", 0);
String statusRefStr = T9SysProps.getString("$STATUS_REF_SEC");
if (statusRefStr == null || "".equals(statusRefStr.trim())) {
  statusRefStr = "3600";
}
int remainDays = T9RegistUtility.remainDays();
String onlineRefStr = T9SysProps.getString("$ONLINE_REF_SEC");
if (onlineRefStr == null || "".equals(onlineRefStr.trim())) {
  onlineRefStr = "3600";
}

String machineCode = "";
try {
	machineCode = T9RegistUtility.getMchineCode();
}	catch (Exception e) {
  
}

%>
<body leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
<table cellspacing="0" cellpadding="0" border="0" width="100%" class="status-content">
  <tr>
    <td width="209" align="center" class="statusbar-usercount" id="usercount">
       <a onclick="selectTab(1);return false;" href="javascript:void(0)">
             共&nbsp;<span size="3" id="userCountInput" style="text-align:center;border:none;background-color:transparent;width:30px;"></span>&nbsp;人在线       </a>
    </td>
    <td>
	    <div id="status" style="">
			  <div id="statusContent" style="position:relative;top:0px;"></div>
			</div>
    </td>
    <td width="350">
<%if (SignProvider.type == null) { 
%>    
      <font color="red">序列号不正确，请联系厂家完成注册安装!</a>
<%  
}else if(SignProvider.type.equals("oem")){
%>
    <font color="red">正式版</a>
<%  
}else if(SignProvider.type.equals("trail")){
%>
      <font color="red"> 本软件尚未注册, 免费试用剩余&nbsp;<%=SignProvider.validDays() %>&nbsp;天</font>&nbsp;&nbsp;
      <a href="javascript:void(0)" style="color:blue;" onclick="dispParts('<%=contextPath %>/core/funcs/system/info/index.jsp');return false;">注册</a>
<%}else{%>
    <font color="red">免费版</a>
<%}%>
</td>
  </tr>
</table>
<script type="text/javascript">
	function initUserCound() {
	  var url = "<%=contextPath%>/t9/core/frame/act/T9ClassicInterfaceAct/queryUserCount.act";
	  $.ajax({
	    type: "GET",
	    dataType: "text",
	    url: url,
	    success: function(text){
	      var json = T9.parseJson(text);
	      if (json.rtState == '0') {
            document.getElementById("userCountInput").innerHTML = json.rtData;
	        //$('#userCountInput').val(json.rtData);
	      }
	    }
	  });
	}
	
	function initStatus() {
	  var url = "<%=contextPath%>/t9/core/frame/act/T9ClassicInterfaceAct/queryStatusText.act";
	  $.ajax({
	    type: "GET",
	    dataType: "text",
	    url: url,
	    success: function(text){
	      var json = T9.parseJson(text);
	      if (json.rtState == '0') {
	        $('#statusContent').html(json.rtData.TEXT || productName);
		      var lineHeight = $("#status").height();
		      var top = 0;
		      var height = $('#statusContent').height();
		      
		      function scroll() {
		        if (top < 2 * lineHeight - height) {
	            top = lineHeight;
	          }
		        $('#statusContent').animate({
		          'top': top -= lineHeight
			      }, 'slow');
		      }
		      
		      var marquee = 5000;
		      
		      if (!isNaN(json.rtData.MARQUEE)) {
		        maruqee = json.rtData.MARQUEE * 1000;
		      }
		      
		      var t = setInterval(scroll, marquee);
	      }
	    }
	  });
	}
	initStatus();
	initUserCound();
	 
	var statusRef = '<%=statusRefStr%>';
	if (isNaN(statusRef)) {
	  statusRef = 3600;
	}
	setInterval(initStatus, statusRef * 1000);
	
	 var onlineRef = '<%=onlineRefStr%>';
	if (isNaN(onlineRef)) {
	  onlineRef = 120;
	}
	setInterval(initUserCound, onlineRef * 1000);
</script>
</body>
</html>