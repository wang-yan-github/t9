<%@ page import="java.sql.*"%>
<%@ page import="java.util.*"%>
<%@ page import="java.io.*"%>
<%@ page import="java.text.*"%>
<%@ page import="t9.core.data.T9RequestDbConn"%>
<%@ page import="t9.core.global.T9BeanKeys"%>
<%
T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);

Connection conn=requestDbConn.getSysDbConn();
 conn.setAutoCommit(true);
%>