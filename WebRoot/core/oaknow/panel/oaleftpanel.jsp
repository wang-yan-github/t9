<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header.jsp" %>
<%@ page  import="t9.core.funcs.person.data.T9Person"%>
<html>
<head>
<title>OA知道</title>
<script type="text/javascript" src="<%=contextPath %>/core/js/datastructs.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/sys.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/prototype.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/smartclient.js"></script>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href = "<%=contextPath%>/core/styles/oaknow/css/wiki.css">
<link rel="stylesheet" href = "<%=cssPath%>/style.css">
<%
  T9Person user = (T9Person)request.getAttribute("user");
%>
<script type="text/javascript">
	function goHome(){
		window. parent.location.href = "<%=contextPath%>/t9/core/oaknow/act/T9OAKnowAct/OAKnowIndex.act";
	}
</script>
</head>
<body class="mbodycolor" topmargin="5">	

<div class="mb">
   <div class="menubg">
   	 <a href="<%=contextPath%>/t9/core/oaknow/act/T9OAKnowPanelAct/leftPanel.act" target="mainpanel">我的问题</a>
   </div> 
   <div class="menubg">
   	 <a href="<%=contextPath%>/t9/core/oaknow/act/T9OAKnowPanelAct/findMyReferenceAsks.act" target="mainpanel">我的参与</a>
   </div> 
   <%
   	if(user.isAdminRole() || "admin".equals(user.getUserId())){  //oa管理员
   	 %>
   	 	 <div class="menubg">
   	      <a href="<%=contextPath%>/t9/core/oaknow/act/T9OAKnowManageAct/gotoManage.act?ask=&startTime=&endTime=&status" target="mainpanel">知道管理</a>
       </div>   
       <div class="menubg">
   	      <a href="<%=contextPath%>/t9/core/oaknow/act/T9OAKnowInputAct/oaInput.act" target="mainpanel">知道录入</a>
        </div> 
   	 <%    	  
   	}
   %>      
  
   <%
   	  if("admin".equals(user.getUserId())){
   	    %>
   	    <div class="menubg">
   	      <a href="<%=contextPath%>/t9/core/oaknow/act/T9CategoriesAct/goToCategoty.act?seqId=" target="mainpanel">知道分类</a>
        </div>
		   <div class="menubg">
		   	 <a href='<%=contextPath%>/t9/core/oaknow/act/T9OAKnowPanelAct/userManage.act?userKey=' target="mainpanel">用户管理</a>
		   </div>
		   <div class="menubg">
		   	 <a href="<%=contextPath%>/t9/core/oaknow/act/T9OAKnowPanelAct/findOAName.act" target="mainpanel">系统设置</a>
		   </div>   
   	    <%
   	  }
   %>
   <div class="menubg">
   	 <a href="javascript:goHome();">知道首页</a>
   </div>  
</div>	
</body>
</html>

