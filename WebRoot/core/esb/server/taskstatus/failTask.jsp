<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<%@ include file="/core/inc/header.jsp" %>
<%@ page import="t9.core.esb.server.task.T9EsbServerTasksMgr" %>
<%@ page import="t9.core.esb.common.data.T9TaskInfo" %>
<%@ page import="t9.core.esb.server.logic.T9EsbServerLogic" %>
<%@ page import="t9.core.esb.server.act.T9RangeDownloadAct" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="t9.core.data.T9RequestDbConn" %>
<%@ page import="t9.core.global.T9BeanKeys" %>

<%
Connection dbConn = null;
try {
  T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
  dbConn = requestDbConn.getSysDbConn();
  String guid = request.getParameter("guid");
  String toId = request.getParameter("toId");
  
  try{
    T9EsbServerTasksMgr.removeDownloadTask(guid, Integer.parseInt(toId));
  }catch(Exception ex) {
  }
  String msg = "文件" + guid + "的下载任务已被挂起";
  T9EsbServerLogic.setDownloadFailedMessage(dbConn, guid, msg, String.valueOf(toId) , 0 , false);
} catch (Exception ex) {
  request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
  request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
  throw ex;
}
%>
<!DOCTYPE  html  PUBLIC  "-//W3C//DTD  HTML  4.01  Transitional//EN"  "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>任务挂起</title>
<link rel="stylesheet" href="<%=cssPath%>/style.css">
<style>
</style>
<script type="text/javascript" src="<%=contextPath%>/core/js/prototype.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/datastructs.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/sys.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/smartclient.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/page.js"></script>
</head>

<body>
<div align=center>
<table align="center" width="230" class="MessageBox">
  <tbody><tr>
    <td class="msg info">
      <h4 class="title">提示</h4>
      <div style="font-size: 12pt; width:150px;" class="content">任务已被挂起</div>
    </td>
  </tr>
</tbody>
</table><br>
<a href="download.jsp">返回</a></div>
</body>
</html>
