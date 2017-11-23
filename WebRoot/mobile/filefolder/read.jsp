<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@  page import="java.util.Map" %>
    <%@  page import="t9.mobile.util.T9MobileUtility" %>
<%@ include file="/mobile/header.jsp" %>
<%@ include file="/mobile/readheader.jsp" %>
<%

		Map m = (Map)request.getAttribute("act.retdata");
		String SUBJECT = (String) m.get("SUBJECT");
		String SEND_TIME = (String)m.get("SEND_TIME");
		String CONTENT = (String)m.get("CONTENT");
		String id = (String)m.get("ATTACHMENT_ID");
		String name = (String)m.get("ATTACHMENT_NAME");
		 String sessionId = request.getSession().getId();
	
%>


<div class="container">
   <h3 class="read_title fix_read_title"><strong><%=SUBJECT%></strong></h3>
   <p class="read_detail fix_read_detail"><%=SEND_TIME %></p>
   <div class="read_content"><%=CONTENT %></div>
   <%
   if( !"".equals(id) && !"".equals(name)  && id != null && name != null)
   {
	   %>
      <div class="read_attach"><%= T9MobileUtility.getAttachLinkPda(id,name,sessionId,"file_folder",true,true,contextPath)%></div>
   <%
   }
   %>
</div>