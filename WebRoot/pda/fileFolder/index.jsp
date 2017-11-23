<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="t9.core.global.T9SysProps" %>
<%@ page import="t9.core.funcs.person.data.T9Person" %>
<%@ page import="t9.pda.fileFolder.data.T9PdaFileFolder" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Date" %>
<%@ page import="t9.core.util.T9Utility" %>
<%
String contextPath = request.getContextPath();
if (contextPath.equals("")) {
  contextPath = "/t9";
}
T9Person loginPerson = (T9Person)session.getAttribute("LOGIN_USER");
List fileFolders = (List) request.getAttribute("fileFolders");
%>
<!doctype html>
<html>
<head>
<title><%=  T9SysProps.getString("productName") %></title>
<meta name="viewport" content="width=device-width" />
<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
<link rel="stylesheet" type="text/css" href="<%=contextPath %>/pda/style/list.css" />
</head>
<body>
<div id="list_top">
  <div class="list_top_left"><a class="ButtonBack" href="<%=contextPath %>/pda/main.jsp?P=<%=loginPerson.getSeqId() %>"></a></div>
  <span class="list_top_center">我的文件</span>
</div>
<div id="list_main" class="list_main">
<%
int pageSize = (Integer)request.getAttribute("pageSize");
int thisPage = (Integer)request.getAttribute("thisPage");
int totalPage = (Integer)request.getAttribute("totalPage");
int count = fileFolders.size();
for(int i = 0 ; i < count ; i++){
  if(i >= pageSize)
    break;
  T9PdaFileFolder fileFolder = (T9PdaFileFolder)fileFolders.get(i);
  int seqId = fileFolder.getSeqId();
  String subject = fileFolder.getSubject();
  Date sendTime = fileFolder.getSendTime();
  String attachmentId = fileFolder.getAttachmentId() == null ? "" : fileFolder.getAttachmentId();
  String attachmentName = fileFolder.getAttachmentName() == null ? "" : fileFolder.getAttachmentName();
  String content = fileFolder.getContent();
  String prs = "&seqId="+seqId
              +"&subject="+T9Utility.encodeURL(subject)
              +"&sendTime="+sendTime.toString()
              +"&attachmentId="+attachmentId
              +"&attachmentName="+attachmentName
              +"&content="+content;
  %>
<a class="list_item" href="<%=contextPath %>/pda/fileFolder/read.jsp?P=<%=loginPerson.getSeqId() %><%=prs %>" hidefocus="hidefocus" >
   <div class="list_item_subject"><%=i+1+(thisPage-1)*pageSize %>.<%=subject %></div>
   <div class="list_item_time"><%=sendTime.toString().substring(0,19) %></div>
   <div class="list_item_arrow"></div>
</a>
<% 
}
if(count==0)
  out.println("<div id=\"message\" class=\"message\">个人文件柜根目录无文件</div>");
%>
</div>
<div id="list_bottom">
  <div class="list_bottom_left">
  <div id="pageArea" class="pageArea">
           第<span id="pageNumber" class="pageNumber"><%=thisPage %>/<%=totalPage %></span>页
    <%if(thisPage == 1 || thisPage == 0){ %>
      <a href="javascript:void(0);" id="pageFirst" class="pageFirstDisable" title="首页"></a>
      <a href="javascript:void(0);" id="pagePrevious" class="pagePreviousDisable" title="上一页"></a>
    <%} else{%>
      <a href="<%=contextPath %>/t9/pda/fileFolder/act/T9PdaFileFolderAct/search.act?P=<%=loginPerson.getSeqId() %>&thisPage=1" id="pageFirst" class="pageFirst" title="首页"></a>
      <a href="<%=contextPath %>/t9/pda/fileFolder/act/T9PdaFileFolderAct/search.act?P=<%=loginPerson.getSeqId() %>&thisPage=<%=thisPage-1 %>" id="pagePrevious" class="pagePrevious" title="上一页"></a>
    <%}
      if(thisPage == totalPage ){%>
      <a href="javascript:void(0);" id="pageNext" class="pageNextDisable" title="下一页"></a>
      <a href="javascript:void(0);" id="pageLast" class="pageLastDisable" title="末页"></a>
    <%} else{%>
    <a href="<%=contextPath %>/t9/pda/fileFolder/act/T9PdaFileFolderAct/search.act?P=<%=loginPerson.getSeqId() %>&thisPage=<%=thisPage+1 %>" id="pageNext" class="pageNext" title="下一页"></a>
    <a href="<%=contextPath %>/t9/pda/fileFolder/act/T9PdaFileFolderAct/search.act?P=<%=loginPerson.getSeqId() %>&thisPage=<%=totalPage %>" id="pageLast" class="pageLast" title="末页"></a>
    <%} %>
  </div>
  </div>
  <div class="list_bottom_right"><a class="ButtonHome" href="<%=contextPath %>/pda/main.jsp?P=<%=loginPerson.getSeqId() %>"></a></div>
</div>
</body>
<script type="text/javascript" src="<%=contextPath %>/pda/js/logic.js"></script>
</html>
