<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@  page import="java.util.Map" %>
    <%@  page import="t9.mobile.util.T9MobileUtility" %>
<%@ include file="/mobile/header.jsp" %>
<%@ include file="/mobile/readheader.jsp" %>
<%

Map n = (Map)request.getAttribute("n");

String typeName = (String)n.get("typeName");
String aId = (String)n.get("aId");
String aName = (String)n.get("aName");
String anonymityYn =  (String)n.get("anonymityYn");
%>
<div class="container">
   <h3 class="read_title fix_read_title"><strong><%=(String)n.get("subject") %></strong></h3>
   <% if (!"".equals(typeName)) { %> 
   <div class="read_detail fix_read_detail" style="text-align:left;"><span class="grapc">类型：</span><%=typeName %></div>
  <% } %> 
   <div class="read_detail fix_read_detail" style="text-align:left;"><span class="grapc">创建人：</span><%=(String)n.get("fromName") %></div>
   <div class="read_detail fix_read_detail" style="text-align:left;"><span class="grapc">发布时间：</span<%=(String)n.get("newsTime") %></div>
   <div class="read_detail fix_read_detail" style="text-align:left;"><span class="grapc">点击次数：</span><%=(Integer)n.get("clickCount") %></div>
   <div class="read_content"><%=(String)n.get("content") %></div>
  <%
   if(!"".equals(aName) && !"".equals(aId))
   {
     String sessionId = request.getSession().getId();
     %>
      <div class="read_attach"><%=T9MobileUtility.getAttachLinkPda(aId, aName, sessionId, "news", true, true, contextPath) %></div>
   <%
   }
   %>
</div>
<script type="text/javascript">
<% if(!"2".equals(anonymityYn)){%>
   $(".readComment").show();
   <% }else{ %>
   $(".readComment").hide();
   <% } %>

$(document).ready(function(){
   oImg = $(".read_content img");
   oImg.each(function(){
      $(this).wrap("<div class='img_wrap'></div>");
      preLoadImage($(this).parent(".img_wrap") ,$(this).attr("src"));
   });
   
   function preLoadImage(obj, url)
   {
      //创建一个Image对象，实现图片的预下载
      var img = new Image();
      img.src = url;
      obj.html("图片加载中...");
      
      // 如果图片已经存在于浏览器缓存，直接调用回调函数
      if(img.complete)
      { 
         obj.empty().append(img);
         return; //直接返回，不用再处理onload事件
      }
      img.onload = function () 
      {
         obj.empty().append(img);
         oiScroll_2.refresh();
      };
      img.onerror = function()
      {
         obj.html('图片加载失败！');
      };
   }
});
</script>