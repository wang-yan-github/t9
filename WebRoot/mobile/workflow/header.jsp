<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@  page import="java.util.Map" %>
<%@  page import="t9.core.servlet.T9SessionListener" %>
<%@  page import="t9.core.funcs.person.data.T9Person" %>
<%@ page import="t9.core.global.T9Const" %>
<%@  page import="java.util.List" %>
<%@  page import="java.util.HashMap" %>
<%@  page import="t9.mobile.util.T9MobileUtility" %>
<%@  page import="t9.mobile.util.T9MobileConfig" %>
<%@  page import="t9.core.global.T9SysPropKeys" %>
<%@  page import="t9.core.global.T9SysProps" %>
<%@  page import="java.io.File" %>
<% 
    String contextPath = request.getContextPath();
    T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
%>
<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1,maximum-scale=1,user-scalable=no">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-mobile-web-app-status-bar-style" content="black">
    <title></title>
	<link rel="stylesheet" href="<%=contextPath %>/mobile/workflow/mui/css/mui.css"/>
	<link rel="stylesheet" href="<%=contextPath %>/mobile/workflow/mui/css/mui.picker.min.css"/>
	<!---->
    <link rel="stylesheet" href="<%=contextPath %>/mobile/workflow/images/style.css"/>
    <script src="<%=contextPath %>/mobile/workflow/mui/js/mui.js" type="text/javascript"></script>
    <script src="<%=contextPath %>/mobile/workflow/mui/js/mui.picker.min.js" type="text/javascript"></script>
    <script src="<%=contextPath %>/mobile/workflow/mui/js/mui.pullToRefresh.js" type="text/javascript"></script>
    <script src="<%=contextPath %>/mobile/workflow/mui/js/mui.pullToRefresh.material.js" type="text/javascript"></script>
    <script type="text/javascript" src="<%=contextPath %>/mobile/workflow/js/jquery-1.9.1.min.js"></script>
    <script type="text/javascript" src="<%=contextPath %>/mobile/workflow/js/selectUserAndDept.js"></script>
    <script type="text/javascript" src="<%=contextPath %>/mobile/workflow/js/praserUtil_news.js"></script>
    <script type="text/javascript" src="<%=contextPath %>/mobile/workflow/js/workflow_new2.js"></script>
    <script type="text/javascript" src="<%=contextPath %>/mobile/workflow/layer/layer.js"></script>
    
	<script type="text/javascript">
        var contextPath = "<%=contextPath %>";
        var stype = "workflow";
        var pre_page = 0;
        var fileReadPage = 1;
        var nonewdata = "没有新工作";
        var newdata = "%s个新工作";
        var noeditpriv = "无办理权限";
        var nosubeditpriv = "无经办权限";
        var noreadflowpriv = "无查看表单权限";
        var nosignflowpriv = "无会签权限";
        var norightnextprcs = "没有符合条件的下一步骤";
        var nosetnewprcs = "错误：尚未设置下一步骤";
        var workcomplete = "工作已结束";
        var workdonecomplete = "工作办理完成";
        var workhasnotgoback = "不能退回此工作";
        var workhasgoback = "工作已经回退";
        var notselectedstep = "请选择回退步骤";
        var workhasturnnext = "工作已转交下一步";
        var signisnotempty = "会签意见不能为空";
        var signsuccess = "会签意见保存成功";
        var formsuccess = "表单保存成功";
        var getfature = "获取失败";
        var error = "数据不全未能转交";
        var errorzbisnotnull = "第%s步主办人不能为空";
        var errorblisnotnull = "第%s步办理人不能为空";
        var nocreatepriv = "没有该流程新建权限，请与OA管理员联系";
        var noflowlist = "此分类没有流程！";
        var norunname = "名称/文号不能为空！";
        var noprefix = "前缀不能为空！";
        var nosuffix = "后缀不能为空！";
        var namerepeat = "输入的工作名称/文号与之前的工作重复，请重新设置。";
        var nocreaterun = "新建工作失败，请重新创建！";
        var nocreaterunpriv = "无可办理流程权限！";
        var user_jb = "经办人";
        var user_over1 = "尚未办理完毕，确认要结束流程吗？";
        var user_over2 = "尚未办理完毕，不能结束流程！";
        var user_next1 = "尚未办理完毕，确认要转交下一步骤吗？";
        var user_next2 = "尚未办理完毕，不能转交流程！";
    </script>
</head>