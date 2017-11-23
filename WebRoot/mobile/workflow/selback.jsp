<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@  page import="java.util.Map" %>
<%@  page import="java.util.List" %>
<%@  page import="t9.core.util.T9Utility" %>
<%@  page import="java.util.HashMap" %>
<%@  page import="t9.mobile.util.T9MobileUtility" %>
<%@  page import="t9.mobile.util.T9MobileConfig" %>
<%
String contextPath = request.getContextPath();
List<Map> r = (List<Map>)request.getAttribute("r");
%>
<div class="mui-content-padded" style="margin: 5px;">
	<h5 class="mui-content-padded">请选择下一步骤：</h5>
	<div class="mui-card">
	<%
    for (Map m : r) {
	    String flowPrcs = (String)m.get("FLOW_PRCS");
	    String prcsName = (String)m.get("PRCS_NAME");
	    out.println("<div class=\"mui-input-row mui-radio\"><label for='"+flowPrcs+"'>"+prcsName+"</label><input type=\"radio\" name='PRCS' id='"+flowPrcs+"' value='"+flowPrcs+"'/></div>");
	}
	%>
	</div>
	<h5 class="mui-content-padded">请输入回退意见</h5>
	<div class="mui-input-row" style="margin:5px;">
        <textarea name="CONTENT_BACK" id="CONTENT_BACK" rows="3"></textarea>
    </div>
    <div class="mui-button-row">
        <button type="button" class="mui-btn mui-btn-warning" onclick="goOnSelBackWorkFlow();">回退</button>
    </div>
    <div class="mui-input-row"</div>
</div>