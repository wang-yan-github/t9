<%@ page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header.jsp"%>
<%
	String taskId=request.getParameter("taskId");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>项目任务日志详情</title>
<link rel="stylesheet" href="<%=cssPath%>/cmp/tab.css" type="text/css" />
<link rel="stylesheet" href="<%=cssPath%>/style.css">
<link rel="stylesheet" href="<%=cssPath%>/cmp/Calendar.css">
<script type="text/Javascript" src="<%=contextPath%>/core/js/datastructs.js"></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/sys.js"></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/prototype.js"></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/smartclient.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/Calendarfy.js"></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/cmp/select.js"></script>
<script type="text/javascript"	src="<%=contextPath%>/core/js/cmp/Menu.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/attach.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/attachMenu.js"></script>
<script type="text/javascript" src="<%=contextPath %>/project/js/util.js"></script>
<script type="text/javascript">
var taskId=<%=taskId%>;
function doInit(){
	 var url= contextPath + "/t9/project/task/act/T9ProjTaskLogAct/getTaskLogDetail.act?taskId="+taskId;
	 var rtJson = getJsonRs(url);
	 if(rtJson.rtState == "0"){
		  var data=rtJson.rtData;
		  if(data.size()>0){
			  var str="<table id=\"taskList\" class=\"TableList\" border=\"0\" width=\"100%\" align=\"center\">"
	   				+ "<tr class=\"TableHeader\">"
	    			+ "<td nowrap align=\"center\">任务负责人</td>"
	    			+ "<td nowrap align=\"center\">内容</td>"
	      		+ "<td nowrap align=\"center\">附件</td>"
	    			+ "<td nowrap align=\"center\">日志时间</td>"
	    			+ "<td nowrap align=\"center\">进度百分比</td>"
	   				+ "</tr>";
	   		var taskList="";
				for(var i=0;i<data.size();i++){
					var id="attachmentId"+i;
		      var name="attachmentName"+i;
					taskList +="<tr class=\"TableLine2\">"
	            + "<td nowrap align=\"center\">"+data[i].logUser+"</td>"
	            + "<td nowrap align=\"center\">"+data[i].logContent+"</td>"
	            + "<td nowrap align=\"center\">"
	            + "<div id=\"projAttach\">"
			    	  + "<input type=\"hidden\" id=\""+id+"\" name=\""+id+"\">"
			     		+ "<input type=\"hidden\" id=\""+name+"\" name=\""+name+"\">"
			     		+ "<input type=\"hidden\" id=\"moduel\" name=\"moduel\" value=\"project\">"
			   		  + "<span id=\"showAtt"+i+"\">无附件 </span></div>"
							+ "</td>"
	            + "<td nowrap align=\"center\">"+getTime(data[i].logTime)+"</td>"
	            + "<td nowrap align=\"center\">"+data[i].percent+"%</td>"
	          	+ "</tr>";
				}
	    $("taskLogDetail").innerHTML=str+taskList+"</table>";
	    for(var j=0;j<data.size();j++){
	    	var id="attachmentId"+j;
		    var name="attachmentName"+j;
	    	$(id).value=data[j].attachmentId ;
				$(name).value=data[j].attachmentName;
				var  selfdefMenu= {
	    		office:["downFile","dump","read"], 
	   	 		img:["downFile","dump","play"],
	    		music:["downFile","dump","play"],  
	    		video:["downFile","dump","play"], 
	   	  	others:["downFile","dump"]
	 		 }

			 if( $(id).value){
  			attachMenuSelfUtil("showAtt"+j,"project",$(name).value ,$(name).value, '','','',selfdefMenu);  
 			 }
	    }
		  }else{
			  WarningMsrg('无任务日志记录', 'msrg');
		  }
	    
	 }else{
			alert(rtJson.rtMsrg);
	 }  
}

function getTime(logTime){
	  if(logTime=="null"){
	    return "";
	  }
	  return logTime.substring(0,10);
	}
	
</script>
</head>

<body class="bodycolor" topmargin="5" onload="doInit();">
<table border="0" width="100%" cellspacing="0" cellpadding="3" class="small">
 <tr>
  <td class="Big"><img src="../images//project.gif" align="absmiddle"><span class="big3">项目进度日志详情</span>
  </td>
 </tr>
</table>
<div id="taskLogDetail" style="text-align:center;margin:0 auto;font-size:18px;"></div>

<div id="msrg">
</div>
</body>
</html>