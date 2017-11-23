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
Map r = (Map)request.getAttribute("r");
int runId =(Integer) r.get("runId");
int flowId =(Integer) r.get("flowId");
int prcsId =(Integer) r.get("prcsId");
int flowPrcs =(Integer) r.get("flowPrcs");

String opFlag =(String) r.get("opFlag");
String feedbackFlag =(String) r.get("feedbackFlag");


List<Map> list = (List<Map>)r.get("feedbacks");
String sessionId = request.getSession().getId();

%>
 
 <%
if(!"2".equals(feedbackFlag)){
%>
      <% 
      for (Map m : list) { 
        int prcsId2 =(Integer) m.get("prcsId");
        String prcsName =(String) m.get("prcsName");
        String userName =(String) m.get("userName");
        String deptName =(String) m.get("deptName");
        String time =(String) m.get("time");
        String content =(String) m.get("content");
      %>
   <div class="read_detail read_detail_header">第<%=prcsId2 %>步 <%=prcsName %></div>
   <div class="read_detail read_detail_p">
      <%=userName %>(<%=deptName %>) -  <%=time %><br />
     <%=content %>
   </div>
   <% } %>
   <% } %>
   
<% if ("0".equals(opFlag)) { %>
<div id="sign_opts" class="sign_opts" style="display:none;">
	   <span class="sign_save_flow">保存</span>
	   <span class="stop_flow">办理完毕</span>   
   </div>
   <script>
   	$(".saveSign span").text("操作");
   	$(".saveSign").unbind('click').bind("click",function(){
   		showMenu("sign_opts");
   	});
  	</script>
<% } else { %>
	<script>
   	$(".saveSign span").text("保存");
   	$(".saveSign").unbind('click').bind("click",function(){
   		saveSignWorkFlow();
   	});
  	</script>
  	<% }  %>