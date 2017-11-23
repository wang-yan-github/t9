<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page import="org.apache.log4j.Logger" %>
<%@ page import="t9.core.util.T9Utility" %>
<%@ page import="t9.core.servlet.T9ServletUtility" %>
<%@ page import="t9.core.global.T9ActionKeys" %>
<%@ page import="t9.core.global.T9Const" %>
<%@ page import="t9.core.global.T9SysProps" %>
<%@ page import="t9.core.global.T9SysPropKeys" %>
<%!
  private Logger log = Logger.getLogger("yzq." + this.getClass().getName());
%>
<%
  String contextPath = request.getContextPath();
  if (contextPath.equals("")) {
    contextPath = "/t9";
  }
  String limitUploadFiles  = T9SysProps.getProp("limitUploadFiles");
  String useSearchFunc = T9SysProps.getProp("useSearchFunc");
  String isDev = T9SysProps.getProp("isDevelopContext"); 
  if (T9Utility.isNullorEmpty(isDev)) { 
    isDev = "0"; 
  }
  //获取主题的索引号
  int styleIndex = 1;
  Integer styleInSession = (Integer)request.getSession().getAttribute("STYLE_INDEX");
  if (styleInSession != null) {
    styleIndex = styleInSession;
  }
  
  String stylePath = contextPath + "/uifrm/styles/style" + styleIndex;
  String imgPath = stylePath + "/img";
  String jsPath = contextPath + "/uifrm/js";
  String cssPath = stylePath + "/css";
  String fullContextPath = T9ServletUtility.getWebAppDir(this.getServletConfig().getServletContext());
  String ssoGPower = T9SysProps.getString("ssourl.gpower");
  String isOnlineEval = T9SysProps.getString("IS_ONLINE_EVAL");
  String signFileServiceUrl  = T9SysProps.getString("signFileServiceUrl");//主题标引服务地址
  int maxUploadSize = T9SysProps.getInt("maxUploadFileSize");
%>
<script type="text/javascript">
var contextPath = "<%=contextPath %>";
var imgPath = "<%=imgPath %>";
var jsPath = "<%=jsPath%>";
var ssoUrlGPower = "<%=ssoGPower%>";
var limitUploadFiles = "<%=limitUploadFiles%>"
var signFileServiceUrl = "<%=signFileServiceUrl%>";
var isOnlineEval = "<%=isOnlineEval%>";
var useSearchFunc = "<%=useSearchFunc%>";
var maxUploadSize = <%=maxUploadSize%>;
var isDev = "<%=isDev%>";
</script>