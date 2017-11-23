<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header.jsp" %>
<%
String seqId = request.getParameter("seqId");
%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%><html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>编辑用户信息</title>
<link rel="stylesheet" href = "<%=cssPath%>/style.css">
<script type="text/javascript" src="<%=contextPath %>/core/js/prototype.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/datastructs.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/sys.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/smartclient.js"></script>
<script type="text/javascript">

function doSubmit() {
  if (!$('pwd').value ) {
    alert("密码不能为空");
    return ;
  }
  if ($('pwd').value != $('repwd').value) {
    alert("两次输入的密码不同");
    return ;
  } 
  /*
  alert(1);
   var url = "";
   var rtJson = getJsonRs(url , "pwd=" +pwd + "&seqId=" + $('seqId').value);
   if (rtJson.rtState == '0'){
     alert("修改成功");
   }*/
   document.form1.submit();
   //$('from1').submit();
}
</script>
</head>
<body>
<table border="0" width="100%" cellspacing="0" cellpadding="3" class="small">
  <tr>
    <td><img src="<%=imgPath %>/notify_new.gif" align="middle"><span class="big3"> 编辑用户信息</span>&nbsp;&nbsp;
    </td>
  </tr>
</table>
<br>
<form action="<%=contextPath%>/core/esb/server/user/updatePwdsubmit.jsp" name="from1" id="form1" method="post">
<table class="TableBlock" width="80%" align="center">
    <tr>
  	  <td  nowrap class="TableData">密码：</td>
  	  <td  nowrap class="TableData"><input type="text" id="pwd" name="pwd" ></td>
    </tr>
    <tr>
  	  <td  nowrap class="TableData">重新输入密码：</td>
  	  <td  nowrap class="TableData">
  	  <input type="text" id="repwd" name="repwd" >
  	  </td>
    </tr>
    <tr>
  	  <td  nowrap class="TableData" colspan="4" align="center">
  	    <input type="hidden" id="seqId" name="seqId" value="<%=seqId %>">
  	    <input type="button" onclick="doSubmit()" value="提交">
  	    <input type="button" onclick="window.close();" value="关闭">
  	  </td>
    </tr>
</table>
</form>
</body>
</html>