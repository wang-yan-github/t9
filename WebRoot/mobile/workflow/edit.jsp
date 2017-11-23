<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@  page import="java.util.Map" %>
<%@  page import="java.util.List" %>
<%@  page import="t9.core.util.T9Utility" %>
<%@  page import="java.util.HashMap" %>
<%@  page import="t9.mobile.util.T9MobileUtility" %>
<%@  page import="t9.mobile.util.T9MobileConfig" %>
<%
String contextPath = request.getContextPath();
Map r = (Map)request.getAttribute("r");
String runName =(String) r.get("runName");
int runId =(Integer) r.get("runId");
int flowId =(Integer) r.get("flowId");
int prcsId =(Integer) r.get("prcsId");
int flowPrcs =(Integer) r.get("flowPrcs");

String flowType =(String) r.get("flowType");
String sealImg =(String) r.get("sealImg");

String opFlag =(String) r.get("opFlag");
String beginTime =(String) r.get("beginTime");
String feedbackFlag =(String) r.get("feedbackFlag");
String allowBack =(String) r.get("allowBack");

String attachmentName =(String) r.get("attachmentName");
String attachmentId =(String) r.get("attachmentId");
String imgPath = (String)request.getAttribute("imgPath");
String formMsg = (String)r.get("formMsg");

List<Map> list = (List<Map>)r.get("feedbacks");
List<Map> list2 = (List<Map>)r.get("prcs");
String sessionId = request.getSession().getId();
%>
<form action="<%=contextPath %>/t9/mobile/workflow/act/T9PdaHandlerAct/editSubmit.act" method="post" name="form1" id="edit_from" onsubmit="return false;">
<input type="hidden" name="P" value="<%=sessionId %>">
<input type="hidden" name="FLOW_ID" value="<%=flowId %>">
<input type="hidden" name="RUN_ID" value="<%=runId %>">
<input type="hidden" name="PRCS_ID" value="<%=prcsId %>">
<input type="hidden" name="FLOW_PRCS" value="<%=flowPrcs %>">
<input type="hidden" name="DO_ACTION" value="">
<input type="hidden" name="FLOW_TYPE" value="<%=flowType %>">
<div class="hk-trave-con">
    <!--top-title-->
    <div class="hk-top-title">
      <span></span>
    </div>
    <!--信息列表-->
    <div class="hk-info-list">
        <div class="hk-info-list-con">
            <%=formMsg%>
        </div>
    </div>
</div>
<!--bottom-button-group-->
<div class="hk-bottom-buttom-group">
    <% if ("1".equals(opFlag)) { %>
        <div class="button-group-item greey"><span id="turn_flow">转交</span></div>
    <% } %> 
    <div class="button-group-item back"><span id="save_flow">保存</span></div>
    <% if ("0".equals(opFlag)) { %>
         <div class="button-group-item greey"><span>办理完毕</span></div>
    <% } %>
    <% if (("1".equals(allowBack) || "2".equals(allowBack)) && prcsId != 1) { %>
        <div class="button-group-item back"><span>回退</span></div>
    <% } %>
    <% if ("2".equals(flowType)) { %>
        <div class="button-group-item back"><span>结束流程</span></div>
    <% } %>
    <div class="button-group-item vote"><span>原始表单查看</span></div>
</div>
</form>