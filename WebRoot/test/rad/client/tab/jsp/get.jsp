<%@ page language="java" contentType="text/xml; charset=utf-8"
    pageEncoding="utf-8"%>
<%@ page import="raw.ljf.T9Category,java.util.*" %>
<?xml version="1.0" encoding="UTF-8"?>
<Categorys>
 <%
 	List<T9Category> catList = (ArrayList<T9Category>)request.getAttribute("catList");
	for(T9Category t : catList){	
 %>
<Category>
<title><%=t.getTitle()%></title>
<imgUrl><%=t.getImgUrl()%></imgUrl>
</Category>
<%
}
%>
</Categorys><br></br>