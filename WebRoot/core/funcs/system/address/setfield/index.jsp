<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header.jsp" %>
<%@ page import="java.text.*" %>
<%@ page import="java.util.*,t9.core.funcs.system.censorwords.data.T9CensorWords" %>
<%@ page import="t9.core.data.T9RequestDbConn, t9.core.global.T9BeanKeys" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>分组管理</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="<%=cssPath %>/cmp/tab.css" type="text/css" />
<link rel="stylesheet" href = "<%=cssPath%>/cmp/Calendar.css">
<link rel="stylesheet" href = "<%=cssPath%>/style.css">
<link rel="stylesheet" href = "<%=cssPath%>/cmp/tree.css">
<script type="text/javascript" src="<%=contextPath %>/core/js/prototype.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/tab.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/datastructs.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/sys.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/smartclient.js"></script>
<script type="text/javascript">

function doInit(){
//  var url = "<%=contextPath%>/t9/core/funcs/dept/act/T9DeptPositionAct/getSelestData.act";
 // var rtJson = getJsonRs(url, null);
 // if (rtJson.rtState == "0") {
 //   var  selected = rtJson.rtData;
 //   var disselected = [];
 //   new ExchangeSelectbox('parent',selected,disselected,true,false);
 // }
}

</script>
</head>
<body topmargin="5" onload="doInit()">
 <div id="deplist"></div>
 <form name="form1" id="form1">
 <div id="parent"></div>
 <br></br>
 
 </form>
</body>
</html>