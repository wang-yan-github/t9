<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@  page import="java.util.Map" %>
      <%@  page import="java.util.List" %>
      <%@  page import="java.util.HashMap" %>
    <%@  page import="t9.mobile.util.T9MobileUtility" %>
    <%@  page import="t9.mobile.util.T9MobileConfig" %>
<%
String contextPath = request.getContextPath();
Map r = (Map)request.getAttribute("r");
boolean isPriv =(Boolean) r.get("isPriv");
String runName =(String) r.get("runName");
int flowId =(Integer) r.get("flowId");
String flowName =(String) r.get("flowName");
String autoEdit =(String) r.get("autoEdit");
String flowType =(String) r.get("flowType");



%>
<script>
q_run_id      = '<?=$RUN_ID?>';
q_flow_id     = '<?=$FLOW_ID?>';
q_prcs_id     = '1';
q_flow_prcs   = '1';
q_op_flag     = '1';
</script>