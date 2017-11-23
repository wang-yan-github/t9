<%@ page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>我的项目</title>
<link rel="stylesheet" href="<%=cssPath%>/cmp/tab.css" type="text/css" />
<link rel="stylesheet" href="<%=cssPath%>/style.css">
<link rel="stylesheet" href="<%=contextPath %>/project/css/menu_top.css">
<script type="text/Javascript" src="<%=contextPath%>/core/js/datastructs.js"></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/sys.js"></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/prototype.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/Calendarfy.js"></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/smartclient.js"></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/cmp/select.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/cmp/tab.js"></script>
<script type="text/javascript" src="<%=contextPath%>/project/js/menu_top.js"></script>
<script type="text/javascript">
var projId=window.parent.projId;
var $ = function(id) {return document.getElementById(id);};
var userAgent = navigator.userAgent.toLowerCase();
var isSafari = userAgent.indexOf("Safari")>=0;
var is_opera = userAgent.indexOf("opera") != -1 && opera.version();
var is_moz = (navigator.product == "Gecko") && userAgent.substr(userAgent.indexOf("firefox") + 8, 3);
var is_ie = (userAgent.indexOf("msie") != -1 && !is_opera) && userAgent.substr(userAgent.indexOf("msie") + 5, 3);;
if(is_ie){
	window.attachEvent("onload", forbiden);
}
else
{   
	window.addEventListener("load", forbiden,false);
}
   
   

  function forbiden()
{
	  if(!projId){
		var menu=document.getElementById("navMenu");
		   var menu_id=0;
		   if(!menu) return;  
		   for(var i=0; i<menu.childNodes.length;i++)
		   {
		      if(menu.childNodes[i].tagName!="A")
		         continue;
		      if(menu_id!=0)
		      {
		        menu.childNodes[i].href="#";
		        menu.childNodes[i].target="_self";     
		        menu.childNodes[i].onclick=function(){alert("请先保存项目信息！");};
		      }
		      menu_id++; 
		}
	  }else{return;}
}  
</script>
</head>
<body class="bodycolor" topmargin="5">
<div id="navPanel" >
  <div id="navMenu" style="width:auto;">
  	<script>
 		 	document.write("<a href=\"basicInfo/new.jsp?projId="+projId+"\" target=\"menu_main\" title=\"基本信息\" hidefocus=\"hidefocus\"><span><img style=\"height:16px;\" src=\"../images/menu/project.gif\" align=\"absmiddle\" /> 基本信息</span></a>");
  	  document.write("<a href=\"projMemberManage.jsp?projId="+projId+"\" target=\"menu_main\" title=\"项目成员\" hidefocus=\"hidefocus\"><span><img style=\"height:16px;\" src=\"../images/menu/user_group.gif\" align=\"absmiddle\" /> 项目成员</span></a>");
  	  document.write("<a href=\"task/index.jsp?projId="+projId+"\" target=\"menu_main\" title=\"项目任务\" hidefocus=\"hidefocus\"><span><img style=\"height:16px;\" src=\"../images/menu/diary.gif\" align=\"absmiddle\" />项目任务</span></a>");
  	  document.write("<a href=\"projDoc.jsp?projId="+projId+"\" target=\"menu_main\" title=\"项目文档\" hidefocus=\"hidefocus\"><span><img style=\"height:16px;\" src=\"../images/menu/file_folder.gif\" align=\"absmiddle\" /> 项目文档</span></a>");
  	  document.write("<a href=\"setCost.jsp?projId="+projId+"\" target=\"menu_main\" title=\"项目经费\" hidefocus=\"hidefocus\"><span><img style=\"height:16px;\" src=\"../images/menu/cost.gif\" align=\"absmiddle\" />项目经费</span></a>");
  	</script>
  </div>
  <div id="navRight" style="float:left;">
    <img id="navScroll" src="/images/nav_r1.gif" style="display:none;cursor:pointer;" align="absMiddle" title="显示下一行" />
      </div>
</div>
</body>
</html>