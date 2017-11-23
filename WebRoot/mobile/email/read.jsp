<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@  page import="java.util.Map" %>
    <%@  page import="t9.mobile.util.T9MobileUtility" %>
<%@ include file="/mobile/header.jsp" %>
<%@ include file="/mobile/readheader.jsp" %>
<%

		Map map = (Map)request.getAttribute("n");
		
		
		String FROM_ID=(String)map.get("FROM_ID");
		String UID= (String)map.get("UID");
		String SUBJECT= (String)map.get("SUBJECT");
		String IMPORTANT_DESC= (String)	map.get("IMPORTANT_DESC");
		String FROM_NAME= (String)map.get("FROM_NAME");
		String DEPT_NAME= (String)map.get("DEPT_NAME");
		String SEND_TIME= (String)map.get("SEND_TIME");
		String CONTENT= (String)map.get("CONTENT");
		String ATTACHMENT_ID= (String)map.get("ATTACHMENT_ID");
		String ATTACHMENT_NAME= (String)map.get("ATTACHMENT_NAME");
		String IS_WEBMAIL= 	(String)map.get("IS_WEBMAIL");
		String TO_MAIL= 	(String)map.get("TO_MAIL");
%>
    <div class="container">
         <h3 class="read_title fix_read_title"><strong><%=SUBJECT%></strong> <%=IMPORTANT_DESC%></h3>            
         <div id="replyTo" style="display:none;">
            <em userid="<%=FROM_ID %>" uid="<%=UID%>"><%=FROM_NAME%></em>   
         </div>
         
         <div class="read_detail email_from fix_read_detail"  style="text-align:left;"><span class="grapc">发件人：</span><%=FROM_NAME%>&nbsp;&nbsp;部门：<%=DEPT_NAME%></div>
         <% 
            if(!"0".equals(IS_WEBMAIL))
            {
          %>
         <div class="read_detail fix_read_detail" style="text-align:left" ><span class="grapc">收件人：</span><%=TO_MAIL%></div>
         <%
            }
         %>
         <div class="read_detail fix_read_detail"  style="text-align:left;">
            <span class="grapc">发送时间：</span><%=SEND_TIME%>
         </div>
         <div class="read_content"><%=CONTENT%></div>
         <% 
         if(ATTACHMENT_ID != null && !"".equals(ATTACHMENT_ID) && ATTACHMENT_NAME  != null && !"".equals(ATTACHMENT_NAME ) )
         {
         String sessionId = request.getSession().getId();
     %>
      <div class="read_attach"><%=T9MobileUtility.getAttachLinkPda(ATTACHMENT_ID, ATTACHMENT_NAME, sessionId, "email", true, true, contextPath) %></div>
   <%
         }
         %>
      </div>
<script type="text/javascript">
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
         obj.html('"图片加载失败！');
      };
   }
});
<%
if(!"0".equals(IS_WEBMAIL)){
%>
$("#header_2 .emailReply").hide();
<%
}else{
%>
$("#header_2 .emailReply").show();
<%
}
%>
</script>