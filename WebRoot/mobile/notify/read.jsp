<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@  page import="java.util.Map" %>
    <%@  page import="t9.mobile.util.T9MobileUtility" %>
<%@ include file="/mobile/header.jsp" %>
<%@ include file="/mobile/readheader.jsp" %>
<%
Map n = (Map)request.getAttribute("m");
String TYPE_NAME = (String)n.get("TYPE_NAME");
String aId = (String)n.get("ATTACHMENT_ID");
String aName = (String)n.get("ATTACHMENT_NAME");
%>
<div class="container">
   <h3 class="read_title fix_read_title"><strong><%=(String)n.get("SUBJECT") %></strong></h3>
   <% if (!"".equals(TYPE_NAME)) { %> 
   <div class="read_detail fix_read_detail" style="text-align:left;"><span class="grapc">类型：</span><%=TYPE_NAME %></div>
  <% } %> 
   <div class="read_detail fix_read_detail" style="text-align:left;"><span class="grapc">创建人：</span><%=(String)n.get("FROM_NAME") %></div>
   <div class="read_detail fix_read_detail" style="text-align:left;"><span class="grapc">创建时间：</span<%=(String)n.get("SEND_TIME") %></div>
   <div class="read_detail fix_read_detail" style="text-align:left;"><span class="grapc">生效日期：</span><%=(String)n.get("BEGIN_DATE") %></div>
   <div class="read_content"><%=(String)n.get("CONTENT") %></div>
  <%
   if(!"".equals(aName) && !"".equals(aId))
   {
     String sessionId = request.getSession().getId();
     %>
      <div class="read_attach"><%=T9MobileUtility.getAttachLinkPda(aId, aName, sessionId, "notify", true, true, contextPath) %></div>
   <%
   }
   %>
</div>
