<%@ page import = "java.util.*" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
		Map map = (Map)request.getAttribute("act.retdata");
		String seqId = (String)map.get("seqId");
		String SEAL_NAME = (String)map.get("SEAL_NAME");
		String DEVICE_NAME_STR = (String)map.get("DEVICE_NAME_STR");
		String contextPath = request.getContextPath();
		String sessionid = request.getParameter("sessionid");
%>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title></title>

</head>
<body>
			<div class="container">
		    <div class="tform tformshow">
		       <div class="read_detail read_detail_header">我的印章</div>
		       <div class="read_detail">
		          <em>印章名称:</em><%=SEAL_NAME %>     
		       </div>
		       <div class="read_detail">
		          <em>授权设备：</em>
		        	<%=DEVICE_NAME_STR %>
		       </div>
		       <div class="read_detail">
		          <em>印章预览：</em>
		          <div align="center">
		          <img src="<%=contextPath %>/t9/mobile/workflow/act/T9SealDataShowAct/show.act?id=<%=seqId %>&sessionid=<%=sessionid %>"  />
		          </div>
		       </div>
		    </div>
		</div>
	<script>
		seal_id = '<%=seqId%>'
		function set_width_seal(obj){
		      $(obj).contents().find("img").width(200);
		}	
	</script>
</body>
</html>
