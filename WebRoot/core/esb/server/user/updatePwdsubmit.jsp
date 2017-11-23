<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<%@ include file="/core/inc/header.jsp" %>
<%@ page import="t9.core.esb.server.task.T9EsbServerTasksMgr" %>
<%@ page import="t9.core.esb.common.data.T9TaskInfo" %>
<%@ page import="t9.core.esb.server.logic.T9EsbServerLogic" %>
<%@ page import="t9.core.esb.server.act.T9RangeDownloadAct" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="t9.core.data.T9RequestDbConn" %>
<%@ page import="t9.core.global.T9BeanKeys" %>
<%@ page import="t9.core.util.db.T9DBUtility" %>
<%@ page import="t9.core.util.auth.T9DigestUtility" %>
<%
Connection dbConn = null;
try {
  T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
  dbConn = requestDbConn.getSysDbConn();
  
  T9EsbServerLogic logic = new T9EsbServerLogic();
  String seqId = request.getParameter("seqId");
  String pwd = request.getParameter("pwd");
  
  pwd = T9DigestUtility.md5Hex(pwd.getBytes());
  String sql = "update  td_user set password=? where SEQ_ID =" + seqId;
  PreparedStatement ps = null;
  try {
    ps = dbConn.prepareStatement(sql);
    ps.setString(1, pwd);
    ps.executeUpdate();
  } catch (Exception e) {
    e.printStackTrace();
  } finally {
    T9DBUtility.close(ps, null, null);
  }
} catch (Exception ex) {
  request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
  request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
  throw ex;
}
%>
{"rtState":"0", "rtMsrg":"执行成功"}
