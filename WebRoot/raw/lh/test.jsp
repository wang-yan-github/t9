<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
 <%@ include file="/core/inc/header.jsp" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>公共信息</title>
<link rel="stylesheet" href = "<%=cssPath%>/style.css">
<script type="text/javascript" src="<%=contextPath %>/core/js/jquery/jquery-1.4.2.js"></script>
<script type="text/javascript">

function doInit() {
  $.getJSON(contextPath + "/t9/core/funcs/filefolder/act/T9FileSortAct/getFileList.act" ,{},function(result){
 // $.each(result.rtData , function (i , item) {
    alert(result.rtData.fileName);
  //}); 
  });
}
</script>
<body onload="doInit()">

</body>
</html>