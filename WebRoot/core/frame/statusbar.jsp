<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header.jsp" %>
<%@ page import="t9.core.global.T9RegistProps" %>
<%@ page import="t9.core.util.auth.T9RegistUtility" %>
<html>
<head>
<title></title>
</head>
<%
String statusRefStr = T9SysProps.getString("$STATUS_REF_SEC");
if (statusRefStr == null || "".equals(statusRefStr.trim())) {
  statusRefStr = "3600";
}
int remainDays = T9RegistUtility.remainDays();
String onlineRefStr = T9SysProps.getString("$ONLINE_REF_SEC");
if (onlineRefStr == null || "".equals(onlineRefStr.trim())) {
  onlineRefStr = "3600";
}
%>
<body leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
<table cellspacing="0" cellpadding="0" border="0" width="100%" class="status-content">
  <tr>
    <td width="209" align="center" class="statusbar-usercount" id="usercount">
       <a onclick="selectTab(1);" href="javascript:void(0)">
             共<input type="text" size="3" id="userCountInput" style="text-align:center;border:none;background-color:transparent;width:30px;" onfocus="this.blur();return false;">人在线       </a>
    </td>
    <td>
	    <div id="status" style="text-align:center;line-height:21px;height:21px;overflow:hidden;">
			  <div id="statusContent" style="position:relative;top:0px;"></div>
			</div>
    </td>
<%if (!T9RegistUtility.hasRegisted()) { %>
    <td width="350">
      <font color="red"> 本软件尚未注册, 免费试用剩余&nbsp;<%=remainDays %>&nbsp;天</font>&nbsp;&nbsp;
	    <a href="javascript:void(0)" style="color:blue;" onclick="dispParts('<%=contextPath %>/core/funcs/system/info/index.jsp');return false;">注册</a>
    </td>
<%} %>
  </tr>
</table>
<script type="text/javascript">

function initUserCound() {
  var url = "<%=contextPath%>/t9/core/funcs/setdescktop/syspara/act/T9SysparaAct/queryUserCount.act";
  alert(url);
  try {
    var json = getJsonRs(url);
    if (json.rtState == '0') {
      alert( json.rtData);
      $('userCountInput').value = json.rtData;
    }
  } catch (e) {
 
  }
}

function initStatus() {
  var url = "<%=contextPath%>/t9/core/funcs/setdescktop/syspara/act/T9SysparaAct/queryStatusText.act";
  try {
    var json = getJsonRs(url);
    if (json.rtState == '0') {
      $('statusContent').innerHTML = json.rtData.TEXT || productName;
    }

    var lineHeight = 21;
    var height = $('statusContent').getHeight();
    
    function scroll() {
      if ($('statusContent').offsetTop + height - lineHeight <= 0) {
        $('statusContent').setStyle({'top':'0px'});
      }
      else {
	      $('statusContent').setStyle({'top':$('statusContent').offsetTop - lineHeight + 'px'});
      }
    }
    
    var marquee = 5000;
    
    if (!isNaN(json.rtData.MARQUEE)) {
      maruqee = json.rtData.MARQUEE * 1000;
    }
    
    var t = setInterval(scroll, marquee);
    
  } catch (e) {
    $('statusContent').innerHTML = json.rtData.TEXT || productName + '&nbsp;&nbsp;直面挑战&nbsp;&nbsp;抓住机遇';
  }
}

function doInit() {
  
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
}
doInit();
</script>
</body>
</html>