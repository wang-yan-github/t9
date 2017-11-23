<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@  include file="/core/inc/header.jsp"%>
<%
  String projId = (String)request.getParameter("seqId")==null?"":(String)request.getParameter("seqId");
  String flag=request.getParameter("flag");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>左侧文件树</title>
<link href="<%=cssPath%>/style.css" rel="stylesheet" type="text/css" />
<link href="<%=cssPath%>/cmp/tab.css" rel="stylesheet" type="text/css" />
<script type="text/Javascript"
	src="<%=contextPath%>/core/js/datastructs.js"></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/sys.js"></script>
<script type="text/Javascript"
	src="<%=contextPath%>/core/js/prototype.js"></script>
<script type="text/Javascript"
	src="<%=contextPath%>/core/js/smartclient.js"></script>
<script type="text/Javascript"
	src="<%=contextPath%>/core/js/cmp/DTree1.0.js"></script>
<script type="text/javascript">
var projId='<%=projId%>'
var tree = null;

function  doInit(){
     tree  =  new  DTree({bindToContainerId:'content'
        ,requestUrl:'<%=contextPath%>/t9/project/file/act/T9ProjFileSortAct/getTree.act?projId='+projId+"&id="
        ,isOnceLoad:false
        ,checkboxPara:{isHaveCheckbox:false}
      	,linkPara:{clickFunc:getUrl}
      	,treeStructure:{isNoTree:false}
        , isWrodWrap:true	
    });
  tree.show();  
  if($('content').innerHTML==""){
	  $('content').innerHTML="暂无文件夹";
  }
  
}
function getUrl(id){  
		if(id){
			var url="folder.jsp?flag=<%=flag%>&seqId=" + id +"&sortName="+$('-a-'+id).innerHTML+"&projId="+projId;
			//var url = "privacyFlag.jsp?seqId=" + id 
			parent.file_main.location.href=url;
		}
	}
</script>
</head>
<body onload="doInit()">
<div id="content"></div>
</body>
</html>