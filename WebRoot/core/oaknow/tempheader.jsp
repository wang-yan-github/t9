<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="org.apache.log4j.Logger" %>
<%@ page import="t9.core.util.T9Utility" %>
<%@ page import="t9.core.servlet.T9ServletUtility" %>
<%@ page import="t9.core.global.T9ActionKeys" %>
<%@ page import="t9.core.global.T9Const" %>

<%!
  private Logger log = Logger.getLogger("yzq." + this.getClass().getName());
%>
<%
	String contextPath = request.getContextPath();
  if (contextPath.equals("")) {
    contextPath = "/t9";
  }
	int styleIndex = 1;
	String stylePath = contextPath + "/core/styles/style" + styleIndex;
	String imgPath = stylePath + "/img";
	String cssPath = stylePath + "/css";
	String fullContextPath = T9ServletUtility.getWebAppDir(this.getServletConfig().getServletContext());
	
%>
<script type="text/javascript">
/** 常量定义 **/
var TDJSCONST = {
  YES: 1,
  NO: 0
};
/** 变量定义 **/
var contextPath = "<%=contextPath %>";
var imgPath = "<%=imgPath %>";

</script>