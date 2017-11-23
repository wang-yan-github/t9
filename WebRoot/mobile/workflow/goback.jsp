<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
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
<div class="container">
	<div class="tform">
		<div class="read_detail read_detail_header">请选择回退步骤</div>
		<div class="read_detail">
<%
for (Map m : r) {
  String flowPrcs = (String)m.get("FLOW_PRCS");
  String prcsName = (String)m.get("PRCS_NAME");
  out.println("<input type='radio' name='PRCS' id='"+flowPrcs+"' value='"+flowPrcs+"'><label for='"+flowPrcs+"'>"+prcsName+"</label>");
}
%>

		</div>
	</div>
	<div class="tform">
		<div class="read_detail read_detail_header">请输入会签意见</div>
		<div class="read_detail">
			<textarea name="CONTENT_BACK" id="CONTENT_BACK" rows="3" wrap="on"></textarea>
		</div>
	</div>
</div>