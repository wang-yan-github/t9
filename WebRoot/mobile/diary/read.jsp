<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@  page import="java.util.Map" %>
    <%@  page import="t9.mobile.util.T9MobileUtility" %>
<%@ include file="/mobile/header.jsp" %>
<%@ include file="/mobile/readheader.jsp" %>
<%

		Map map = (Map)request.getAttribute("n");
		
		
		String USER_ID=(String)map.get("USER_ID");
		String CONTENT= (String)map.get("CONTENT");
		String SUBJECT= (String)map.get("SUBJECT");
		String DIA_ID= (String)	map.get("DIA_ID");
		String DIA_DATE= (String)map.get("DIA_DATE");
		String DIA_TIME= (String)map.get("DIA_TIME");
		String DIA_TYPE_DESC= (String)map.get("DIA_TYPE_DESC");
		String ATTACHMENT_ID= (String)map.get("ATTACHMENT_ID");
		String ATTACHMENT_NAME= (String)map.get("ATTACHMENT_NAME");
%>
   <div class='container'>
  		<h3 class=‘read_title fix_read_title’><strong><%=SUBJECT %></strong></h3>
 		  <div class='read_detail fix_read_detail' style='text-align:left;'><span class='grapc'>类型：</span><%=DIA_TYPE_DESC %></div>
      <div class='read_detail fix_read_detail' style='text-align:left;'><span class='grapc'>日志时间</span><%=DIA_DATE %></div>
      <div class='read_detail fix_read_detail' style='text-align:left;'><span class='grapc'>最后修改时间：</span><%=DIA_TIME %></div>
      <div class='read_content'><%=CONTENT %></div>
		        <%  
		        if(ATTACHMENT_ID != null && !"".equals(ATTACHMENT_ID) && ATTACHMENT_NAME  != null && !"".equals(ATTACHMENT_NAME ) )
		         {
		         String sessionId = request.getSession().getId();
    	    	%>
		    	         <div class="read_attach"><%=T9MobileUtility.getAttachLinkPda(ATTACHMENT_ID, ATTACHMENT_NAME, sessionId, "diary", true, true, contextPath) %></div>
		    	   <%
                 }
             %>
		     
	</div>
					
