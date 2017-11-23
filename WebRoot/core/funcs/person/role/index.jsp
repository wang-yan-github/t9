<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header.jsp" %>
<%@ page import="t9.core.funcs.person.data.T9Person" %>
<%
  T9Person loginUser = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>分栏显示</title>
<script type="text/Javascript" src="<%=contextPath%>/core/js/datastructs.js"></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/sys.js"></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/prototype.js"></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/smartclient.js"></script>
<script type="text/javascript">
var loginUserPriv = <%=loginUser.getUserPriv()%>;
var loginUserId = <%=loginUser.getSeqId()%>;
var isAdminRole = <%=loginUser.isAdminRole()%>;
function doInit(){
	isSecPriv();
  var url = "<%=contextPath%>/t9/core/funcs/person/act/T9PersonAct/getMaxPrivNo.act";
  var rtJson = getJsonRs(url);
  if (rtJson.rtState == "0") {
    if(rtJson.rtData == loginUserPriv && loginUserId != "1"){
      location = "<%=contextPath%>/core/funcs/person/indexPrivNo.jsp";
    }
  }
}

function isSecPriv(){
	
	  var url = "<%=contextPath%>/t9/core/funcs/person/act/T9PersonAct/isSecPriv.act";
	  var rtJson = getJsonRs(url);
	  if (rtJson.rtState == "0") {
	    if(rtJson.rtData!="1"){
	      location = "<%=contextPath%>/core/funcs/person/role/noPriv.jsp";
	    }
	  }
	
}

</script>
</head>
<frameset onload="doInit()" rows="40,*"  cols="*" frameborder="no" border="0" framespacing="0" id="frame1">
  <frame name="user_title" scrolling="no" noresize src="<%=contextPath %>/core/funcs/person/role/title.jsp" frameborder="NO">
  <frameset rows="*"  cols="210,*" frameborder="0" border="0" framespacing="0">	
    <frame name="navigateFrame" id="navigateFrame" src="<%=contextPath %>/core/funcs/person/role/utilitylist.jsp" frameborder="NO" noresize scrolling="auto"/>
    <frame name="contentFrame" id="contentFrame" src="<%=contextPath %>/core/funcs/person/role/query.jsp" frameborder="NO" scrolling="auto">	
  </frameset>
</frameset>
</html>