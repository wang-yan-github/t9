<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page import="org.apache.log4j.Logger" %>
<%@ page import="t9.core.util.T9Utility" %>
<%@ page import="t9.core.servlet.T9ServletUtility" %>
<%@ page import="t9.core.global.T9ActionKeys" %>
<%@ page import="t9.core.global.T9Const" %>
<%@ page import="t9.core.global.T9SysProps" %>
<%@ page import="t9.core.global.T9SysPropKeys" %>
<%@ page import="t9.core.funcs.person.data.T9Person" %>
<%@ page import="t9.core.util.SignProvider" %>

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
  
  
  String stylePath = contextPath + "/core/styles/style" + styleIndex;
  String imgPath = stylePath + "/img";
  String cssPath = stylePath + "/css";
  
  String oaStyle = (String)request.getSession().getAttribute("OA_STYLE");
  if(T9Utility.isNullorEmpty(oaStyle)){
    oaStyle = "1";
  }
  String cssPathOA = contextPath + "/core/frame/5/styles/style"+oaStyle+"/css";
  String fullContextPath = T9ServletUtility.getWebAppDir(this.getServletConfig().getServletContext());
  String ssoGPower = T9SysProps.getString("ssourl.gpower");
  String isOnlineEval = T9SysProps.getString("IS_ONLINE_EVAL");
  String signFileServiceUrl  = T9SysProps.getString("signFileServiceUrl");//主题标引服务地址
  int maxUploadSize = T9SysProps.getInt("maxUploadFileSize");
  //系统信息
  String shortOrgName = T9SysProps.getString("shortOrgName");
  String orgName = T9SysProps.getString("orgName");
  String productName = T9SysProps.getString("productName");
  String fullOrgName = T9SysProps.getString("fullOrgName");
  String t9SysInfo = T9SysProps.getString("t9SysInfo");
  String shortProductName = T9SysProps.getString("shortProductName");
  String orgFirstSite = T9SysProps.getString("orgFirstSite");
  String orgSecondSite = T9SysProps.getString("orgSecondSite");
  String workflowZipDown = T9SysProps.getString("workflowZipDown");
  
  String softType = SignProvider.type;
  String userCount =  SignProvider.userCount;
  String registerDate =  SignProvider.registerDate;
  String expiresDate =  SignProvider.expiresDate;
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
var ssoUrlGPower = "<%=ssoGPower%>";
var limitUploadFiles = "<%=limitUploadFiles%>"
var signFileServiceUrl = "<%=signFileServiceUrl%>";
var isOnlineEval = "<%=isOnlineEval%>";
var useSearchFunc = "<%=useSearchFunc%>";
var maxUploadSize = <%=maxUploadSize%>;
var isDev = "<%=isDev%>";
var ostheme = "<%=oaStyle %>";
//系统信息
var shortOrgName = "<%=shortOrgName%>";
var orgName = "<%=orgName%>";
var productName = "<%=productName%>";
var fullOrgName = "<%=fullOrgName%>";
var t9SysInfo = "<%=t9SysInfo%>";
var shortProductName = "<%=shortProductName%>";
var orgFirstSite = "<%=orgFirstSite%>";
var orgSecondSite = "<%=orgSecondSite%>";
var workflowZipDown = "<%=workflowZipDown%>";

var softType = "<%=softType%>";
var userCount = "<%=userCount%>";
var registerDate = "<%=registerDate%>";
var expiresDate = "<%=expiresDate%>";
</script>