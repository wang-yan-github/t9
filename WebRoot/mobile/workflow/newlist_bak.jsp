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
List<Map> list = (List)r.get("list");
String sortNameTmp = (String)r.get("sortNameTmp");
String sortId = (String)r.get("sortId");
int parentId = (Integer)r.get("parentId");

if(list.size() == 0)
{
	out.print("NOCREATERUNPRIV") ;
	return ;
}
	

for (Map m : list) {
  String title = (String)m.get("title");
  String json =(String) m.get("json");
  Integer key = (Integer)m.get("key");
  boolean isFolder = (Boolean)m.get("isFolder");
  boolean isLazy = (Boolean)m.get("isLazy");
  if (isFolder) {
%>
<li class="folder"
	q_id="<%=key %>" q_name="<%=title %>"><img
	src="<%=contextPath %>/mobile/workflow/style/images/folder.png" class="ui-li-thumb" />
	<h3><%=title %></h3>
	<p class="w100 grapc">&nbsp;</p> <span class="ui-icon-rarrow"></span></li>
<% } else{%>
<li class="files"
	q_id="<%=key %>"><img src="<%=contextPath %>/mobile/workflow/style/images/file.png"
	class="ui-li-thumb" />
	<h3><%=title %></h3>
	<p class="w100 grapc">&nbsp;</p>
    <span class="ui-icon-rarrow"></span>
</li>
<% } }
%>
<script>
now_sort = '<%=sortId %>';
parent = '<%=parentId %>';
$("#header_12").find(".t").text('<%=sortNameTmp %>');
</script>