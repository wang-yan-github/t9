<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header.jsp" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>添加专区</title>
<link rel="stylesheet" href = "<%=cssPath%>/style.css">
<link rel="stylesheet" href="<%=cssPath%>/page.css">
<script type="text/javascript" src="<%=contextPath %>/core/js/prototype.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/datastructs.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/sys.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/cmp/page.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/smartclient.js"></script>
<script type="text/javascript">
function doSubmit(){
	if(checkForm()){
	  var queryParam = $("form1").serialize();
	  var url = contextPath + "/t9/cms/bbs/board/act/T9BbsBoardAct/addBbsArea.act";
	  var json = getJsonRs(url , queryParam);
	  if (json.rtState == '0') {
		  window.close();
		  opener.clearArea();
		  opener.getArea();
	  }
	  else{
		  alert(json.rtMsrg);
	  }
	}
}
function checkForm(){
  if($("areaIndex").value==""){
	  alert("排序号不能为空")
	  $("areaIndex").focus();
    return (false);
  }
  if($("areaName").value == ""){
	  alert("专区名称不能为空");
	  $("areaName").focus();
    return (false);
  }
  if(!checkRate($("areaIndex").value)){
		alert("排序号必须为正整数！")
		$("areaIndex").value="";
		$("areaIndex").focus();
		return false;
	}
  if($("areaName").value.length>20){
	  alert("专区名称太长，请重新输入！");
	  $("areaName").value="";
	  $("areaName").focus();
	  return false;
  }
  return true;
}

function checkRate(input){ 
  var re = /^[0-9]+[0-9]*]*$/;
  if(!re.test(input)) {  
    return false;  
  }  
  return true;
} 	
</script>
</head>
<body onload="">
<table border="0" width="100%" cellspacing="0" cellpadding="3" class="small">
  <tr>
    <td class="Big"><img src="<%=imgPath %>/meeting.gif" width="17" height="17"><span class="big3">添加专区信息</span><br>
    </td>
  </tr>
</table>
<br>
<form id="form1" name="form1" action="<%=contextPath%>/t9/cms/bbs/board/act/T9BbsBoardAct/addBbsArea.act" method="post" onsubmit="">
<table class="TableBlock" width="90%" align="center">
  <tr>
	<td align="left" width="20%" class="TableContent" nowrap>专区排序号：</td>
    <td align="left" class="TableData" width="80%" nowrap>
      <input id="areaIndex" name="areaIndex" type="text" style="width: 50%;">
      <label>请输入1~3位的正整数!</label>
    </td>
  </tr> 
    <tr>
	<td align="left" width="20%" class="TableContent" nowrap>专区名称：</td>
    <td align="left" class="TableData" width="80%" nowrap>
      <input id="areaName" name="areaName" type="text" style="width: 50%;">
      <input id="reciveDeptArray" name="reciveDeptArray" type="hidden">
      <label>请输入小于20个文字的名称!</label>
    </td>
  </tr> 
  <tr>
  	<td colspan="2" align="center">
  	  <input type="hidden" name="dtoClass" id="dtoClass" value="t9.cms.bbs.board.data.T9BbsArea">
  	  <input type="button" value="确定" onclick="doSubmit();" class="BigButton">
  	</td>
  </tr>
</table>
</form>
</body>
</html>