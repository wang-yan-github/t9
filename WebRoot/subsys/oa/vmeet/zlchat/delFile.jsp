<%@ page language="java" import="t9.subsys.oa.vmeet.act.*,t9.core.global.T9SysProps" contentType="text/html;charset=utf-8"%>
<%@ include file="inc.jsp" %>
<% /*文件作用: 处理删除"共享文档"功能中的单个文件*/ %> 
<%
	
  String uploadDir = T9SysProps.getAttachPath() + "\\zlchat\\doc\\";
	//zlchat 客户端传过来的文件名
	String fileName = request.getParameter("fileName");
	if (StringHelper.isNotEmpty(fileName))
	{
		File file = new File(uploadDir + fileName);
		if (file.exists())
		{
			file.delete();
			PPTUtil.delFile(uploadDir + fileName);
		}
		String sql = "delete from zl_file where file_Name='" + fileName+ "'";

		int affectedRows=JdbcUtils.update(sql,conn);		
		JdbcUtils.closeConnection(conn);
	}
%>