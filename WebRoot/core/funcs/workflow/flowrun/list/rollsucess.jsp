<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>归档成功</title>
<link rel="stylesheet" href ="<%=cssPath %>/style.css">
</head>
<body>
 <div id="noPriv"  align="center">
 <table class="MessageBox" align="center" width="320" >
  <tr>
    <td class="msg info">
      <h4 class="title">提示</h4>
      <div class="content" style="font-size:12pt" id="tip">归档操作成功！</div>
    </td>
  </tr>
</table>
<input type="button" onclick="window.close();" value="关闭" class="BigButton" title="关闭此窗口">&nbsp;&nbsp;
</div>
</body>
</html>