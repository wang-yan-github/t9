<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@  page import="java.util.Map" %>
      <%@  page import="java.util.List" %>
        <%@  page import="t9.core.util.T9Utility" %>
      <%@  page import="java.util.HashMap" %>
    <%@  page import="t9.mobile.util.T9MobileUtility" %>
    <%@  page import="t9.mobile.util.T9MobileConfig" %>
   
   <%
   
   Map r = (Map)request.getAttribute("r");
   List<Map> list = (List)r.get("list");
   int TOTAL_ITEMS =(Integer)r.get("totalItems") ;
   int CURRITERMS =(Integer)r.get("curriterms");
   
   for (Map m : list) {
	   String Class = (String)m.get("CLASS");
	   String createTime = (String)m.get("createTime");
	   String opFlag = (String)m.get("opFlag");
	   String flowName = (String)m.get("flowName");
	   String runName = (String)m.get("runName");
	   String prcsName = (String)m.get("prcsName");
	   String status = (String)m.get("status");
	   String opFlagDesc = (String)m.get("opFlagDesc");
	   int flowId = (Integer)m.get("flowId");
	   int runId = (Integer)m.get("runId");
	   int prcsId = (Integer)m.get("prcsId");
	   int flowPrcs = (Integer)m.get("flowPrcs");
   
   %> 
     <li class="<%=Class %>"
	q_id="<%=createTime %>" q_run_id="<%=runId %>"
	q_flow_id="<%=flowId %>" q_prcs_id="<%=prcsId %>"
	q_flow_prcs="<%=flowPrcs %>" q_op_flag="<%=opFlag %>">
	<h3>[<%=runId %>] - <%=flowName %> - <%=runName %></h3>
	<p class="grapc"><%=status %> <%=prcsName %> <%=opFlagDesc %></p> <span
	class="ui-icon-rarrow"></span>
</li>
   <% }%> 

<script>
nomoredata_15 = <%= (T9MobileConfig.PAGE_SIZE >= (TOTAL_ITEMS - CURRITERMS) ? "true" : "false")%>;
noshowPullUp_15 = <%= (T9MobileConfig.PAGE_SIZE >= (TOTAL_ITEMS - CURRITERMS) ? "true" : "false") %>;
</script>