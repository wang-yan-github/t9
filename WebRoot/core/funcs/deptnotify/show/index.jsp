<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
 <%@ include file="/core/inc/header.jsp" %>
 <%
  String portName = request.getParameter("portName");
  if (portName == null) {
      portName = "";
  }else{
      portName = portName.substring(0,portName.indexOf("."));
  }
%>
<html>
<head>
<title>公告列表</title>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<link rel="stylesheet" href="<%=cssPath %>/page.css">
<link rel="stylesheet" href ="<%=cssPath %>/style.css">
<link rel="stylesheet" href ="<%=cssPath %>/cmp/tab.css">
<script type="text/javascript" src="<%=contextPath %>/core/js/prototype.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/tab.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/datastructs.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/sys.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/smartclient.js"></script>
<script type="text/javascript">
/**
 * 页面加载初始化

 */
function doInit() {
  var notifyNoReadUrl = "<%=contextPath%>/core/funcs/deptnotify/show/notifyNoRead.jsp?portName=<%=portName%>";
  var notifyAllUrl = "<%=contextPath%>/core/funcs/deptnotify/show/notifyAll.jsp?portName=<%=portName%>";
  var notifyQueryUrl = "<%=contextPath%>/core/funcs/deptnotify/show/notifyQuery.jsp?portName=<%=portName%>";
  var tabArray = [{title:"未读公告", content:"", contentUrl:notifyNoReadUrl, imgUrl: "<%=imgPath%>/show_reader.gif", useIframe:true},
                  {title:"公告通知", content:"", contentUrl:notifyAllUrl, imgUrl:  "<%=imgPath%>/show_reader.gif", useIframe:true},
                  {title:"公告查询", content:"", contentUrl:notifyQueryUrl, imgUrl: "<%=imgPath%>/search.gif", useIframe:true}
                 ];
  buildTab(tabArray,'contentDiv',800);
}
</script>
</head>
<body onload="doInit();" topmargin="3">
<div id="contentDiv"></div>
</body>
</html>