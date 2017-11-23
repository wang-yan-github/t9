<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header.jsp" %>
<%String seqId= (String)request.getParameter("seqId")==null?"0":(String)request.getParameter("seqId");
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>项目批注</title>
<link rel="stylesheet" href="<%=cssPath%>/page.css">
<link rel="stylesheet" href="<%=cssPath%>/style.css">
<link rel="stylesheet" href="<%=cssPath%>/cmp/Calendar.css">
<script type="text/javascript" src="<%=contextPath%>/core/js/prototype.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/cmp/tab.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/datastructs.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/sys.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/smartclient.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/cmp/Calendarfy.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/cmp/Menu.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/cmp/page.js"></script>
<script type="text/javascript">
var seqId='<%=seqId%>';
function doInit(){
	var url = "<%=contextPath%>/t9/project/comment/act/T9ProjCommentAct/getById.act?seqId="+seqId;
	 
	  var rtJson = getJsonRs(url);
	    if (rtJson.rtState == "0") {
		    $("WRITE_TIME").value=rtJson.rtData[0].writeTime.substr(0,rtJson.rtData[0].writeTime.length-2);
		    $("content").value=rtJson.rtData[0].content;
		    $("seqId").value=rtJson.rtData[0].seqId;
	    }else {
	      alert(rtJson.rtMsrg); 
	    }
}
function checkForm(){
	if($("content").value==""){
		alert("内容不能为空");
		return false;
		}
	return true;
}
function updateOne(){
	if(checkForm()){
		var url = "<%=contextPath%>/t9/project/comment/act/T9ProjCommentAct/updateComment.act";
	  var rtJson = getJsonRs(url, mergeQueryString($("form1")));
	    if (rtJson.rtState == "0") {
	      alert(rtJson.rtMsrg);
	      history.go(-1);
	    }else {
	      alert(rtJson.rtMsrg); 
	    }
		}
}

</script>
</head>
<body onload="doInit()">
<table border="0" width="100%" cellspacing="0" cellpadding="3" class="small">
  <tr>
    <td class="small"><img src="<%=contextPath %>/core/styles/style1/img/edit.gif" WIDTH="22" HEIGHT="20" align="absmiddle"><span class="big3"> 修改项目批注</span>
    </td>
  </tr>
</table>
<form action=""  method="post" name="form1" id="form1"> 

<table class="TableList" width="95%"  align="center" > 
   <tr>
    <td  class="TableContent" width="90">批注时间：</td>
    <td class="TableData">
      <input type="text" name="WRITE_TIME" id="WRITE_TIME" size="19" readonly="readonly" maxlength="100" class="BigStatic" value="">       
    </td>
   </tr>
   <tr>
     <td  class="TableContent"> 批注内容：</td>
     <td class="TableData" colspan="1">
       <textarea name="content" id="content" class="BigInput" cols="80" rows="6"></textarea>
     </td>
   </tr>
   <tr>
    <td   class="TableControl" colspan="2" align="center">
    <input type="hidden" id="seqId" name="seqId"> 
      <input type="button" value="确定" onclick="updateOne()" class="BigButton">&nbsp;&nbsp;
      <input type="button" class="BigButton" value="返回" onClick="history.back();">
    </td>
    </tr>
</table>
</form>
</body>
</html>