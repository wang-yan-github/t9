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
List<Map> r = (List<Map>)request.getAttribute("list");
String SEAL_CUR_ITEM =(String)request.getAttribute("SEAL_CUR_ITEM");
String SEAL_ITEM_CHECK =(String)request.getAttribute("SEAL_ITEM_CHECK");

String sessionId = request.getSession().getId();

%>
<div class="container">
	<div id="deviceForm" class="tform tformshow">
		<div class="read_detail">
		      <em>请选择印章:</em>
		      <select name="SEAL_ID" onchange="select_seal(this)">
		      <%  for (Map m : r) {
		        int id =(Integer) m.get("id");
		        String name = (String)m.get("name");
		        %>
			        <option value="<%=id %>"><%=name %></option>
			     <%} %>
		      </select>
		</div>
		<div class="read_detail">
		    <em>请输入印章密码:</em>
		    <input type="password" name="SEAL_PASSWORD">
		    <input type="hidden" name="SEAL_ITEM_CHECK" value="<%=SEAL_ITEM_CHECK %>">
            <input type="hidden" name="SEAL_CUR_ITEM" value="<%=SEAL_CUR_ITEM %>">
		</div>
		<div class="read_detail">
		    <em>预览:</em>
		    <div id="seal_show" align="center">
		    <%
		    if (r != null && r.size() > 0) {
		      Map m = r.get(0);
		      int id =(Integer) m.get("id");
		    %>
		    <div id="seal_show" align="center">
		    <img   class="mobile_seal" src="<%=contextPath %>/t9/mobile/workflow/act/T9SealDataShowAct/show.act?id=<%=id %>"/>
		    </div>
		    <% } %>
		    </div>
		</div>
	</div>
</div>