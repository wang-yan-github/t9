<%@ page language="java"  import="t9.subsys.oa.vmeet.act.*,t9.core.global.T9SysProps" contentType="text/html;charset=utf-8"%>
<%@ include file="inc.jsp" %> 
<% /*文件作用: 处理"私聊"功能中的单个文件的上传,保存到一个临时的目录中*/ %> 
<%
String uploadDir = T9SysProps.getAttachPath()+"\\zlchat\\wb\\";
	
	//zlchat 客户端传过来的文件名
	String fileName = request.getParameter("fileName");
	
	HashMap uploadFileInfo = UploadUtil.uploadFile(request, uploadDir,
			fileName);
	if (uploadFileInfo == null)
	{
		out.print("upload failed");
	}
	
%>