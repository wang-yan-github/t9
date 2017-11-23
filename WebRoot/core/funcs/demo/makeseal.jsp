<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header.jsp" %>
<%
  String msg1 = (String)request.getAttribute("msg");

%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>签章制作</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href = "<%=cssPath%>/style.css">
<script type="text/javascript" src="<%=contextPath %>/core/js/prototype.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/datastructs.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/sys.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/smartclient.js"></script>
<script type="text/javascript">
var msg = '<%=msg1%>';
if(msg == "success"){
	alert(msg);
	window.location.href = "<%=contextPath%>/core/funcs/demo/makeseal.jsp";
}
function submitForm() {
  if (!$('SEAL_NAME').value) {
    alert("印章名称不能为空!");
    return false;
  }
  if (!$('SEAL_PWD').value) {
    alert("密码不能为空!");
    return false;
  }
   if ($('SEAL_PWD').value != $('reSealPwd').value) {
     alert("两次输入的密码不对!");
     return false;
   }
   if (!$('SealWidth').value&&isInteger($('SealWidth').value)) {
     alert("宽度只能是数字");
     return false;
   }
   if (!$('SealHeight').value&&isInteger($('SealHeight').value)) {
     alert("宽度只能是数字");
     return false;
   }
   $('form1').submit();
}

function GetPosition(obj){
	var left = 0;
	var top  = 0;
	while(obj != document.body){
		left += obj.offsetLeft;
		top  += obj.offsetTop;
		obj = obj.offsetParent;
	}
	return {x:left,y:top};
}

function setImgUploadPosition(obj,dataId){
  var dataObj = document.getElementById(dataId);
  dataObj.style.left =  (GetPosition(obj).x) + "px";
  dataObj.style.top = (GetPosition(obj).y )  + "px";
  dataObj.style.height = obj.height  + "px";
}
</script>
</head>
<body topmargin="5" onload="">
<table border="0" width="100%" cellspacing="0" cellpadding="3" class="small">
  <tr>
    <td class="Big"><img src="<%=imgPath%>/system.gif" WIDTH="22" HEIGHT="20" align="absmiddle"><b><span class="big1">&nbsp;移动印章</big></b>
    </td>
  </tr>
</table>
<form enctype="multipart/form-data" action="<%=contextPath %>/t9/core/funcs/demo/act/T9ImageUploadAct/setSeal.act"  method="post" name="form1" id="form1">
<table class="TableBlock" border=0 align="center" width=80%>
<tr>
	<td align="center" colspan=4 class="TableFooter"><b>印章信息</b></td>
</tr>
  
  <tr>
    <td  class="TableContent" width=80>印章名称</td>
    <td class="TableData">
    	<input type="text" name="SEAL_NAME" id="SEAL_NAME" class="BitInput" maxlength="32">	
    <br></td>
    
    <td align="center" class="TableData" width="250" rowspan="4">
        <div>
        <img src="<%=contextPath %>/core/funcs/demo/pic.png" name='SEAL_IMG' id='SEAL_IMG'  onmousemove="setImgUploadPosition(this,'SEAL_FILE');" style='cursor:pointer;width:200px;height:200px;' title='点击上传印章图片'>
        <input type='file' style="position:absolute;filter:alpha(opacity=0);opacity:0;" size='1'  hideFocus='' name='SEAL_FILE' id='SEAL_FILE' />
        <br><span>注：点击图片区域添加印章图片</span>
        </div>
      </td>
    
  </tr>
  <tr>
    <td  class="TableContent">印章密码</td>
    <td class="TableData">
    	<input type="password" name="SEAL_PWD" id="SEAL_PWD" class="BitInput" maxlength="32">	
    <br></td>
  </tr>
  <tr>
    <td  class="TableContent">确认密码</td>
    <td class="TableData">
    	<input type="password" name="reSealPwd" id="reSealPwd" class="BitInput" maxlength="32">	
    <br></td>
  </tr>
  <tr>
    <td  class="TableContent">图片宽度</td>
    <td class="TableData"><input name="SealWidth" id="SealWidth" maxlength="32"> mm</td>
  </tr>
  <tr>
    <td class="TableContent">图片高度</td>
    <td class="TableData"><input name="SealHeight" id="SealHeight" maxlength="32"> mm</td>
  </tr>
 
  <tr>
    <td align="center" colspan=4 class="TableFooter">
    	<input type="hidden" name="SEAL_DATA" id="SEAL_DATA">
    	<input type="hidden" name="KeyID" id="KeyID">
    	<input class="BigButton" type="button" value="保存" LANGUAGE=javascript onclick="submitForm()">&nbsp;
      </td>
  </tr>
</table>
<table style="display:none" id="seallisttable" class="TableList" border=0 align="center" width=80%>
	<tr class="TableHeader">
        <td>ID</td>
        <td>名称</td>
        <td>规格(mm)</td>
        <td>签名</td>
        <td>证书数量</td>
        <td>操作</td>
	</tr>
</table>
</form>
</body>
</html>