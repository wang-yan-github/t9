<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
      <%@  page import="java.util.Map" %>
       <%@  page import="java.util.Map" %>
      <%@  page import="t9.core.servlet.T9SessionListener" %>
       <%@  page import="t9.core.funcs.person.data.T9Person" %>
       <%@ page import="t9.core.global.T9Const" %>
    <% 
    String contextPath = request.getContextPath();
    T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
    
    %>
<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<meta name="viewport" content="width=device-width, minimum-scale=1, maximum-scale=1">
	<meta name="apple-mobile-web-app-status-bar-style" content="black" />
	<meta name="format-detection" content="telephone=no" /> 
	<title></title>
	<script type="text/javascript" src="<%=contextPath %>/mobile/js/jquery-1.6.4.min.js"></script>
	<script type="text/javascript" src="<%=contextPath %>/mobile/js/iscroll.js"></script>
	<link rel="stylesheet"  href="<%=contextPath %>/mobile/style/pda.css" />
	<script type="text/javascript">
var td_lang = {};
         td_lang.pda = {
            msg_1:'暂无更多信息',
            msg_2:'加载中...',
            msg_3:'页面加载错误',
            msg_4:'下拉刷新...',
            msg_5:'释放立即刷新...',
            msg_6:'上拉加载更多...',
            msg_7:'释放加载更多...',
            msg_8:'已全部加载完毕',
            msg_9:'读取附件中...',
         };
   var C_VER = "";
   var  contextPath = "<%=contextPath %>";
   var P_VER = "<%=T9SessionListener.getUserStateMap().get(person.getSeqId()) %>";
   var isIDevice = (/iphone|ipad/gi).test(navigator.appVersion);
</script>
</head>