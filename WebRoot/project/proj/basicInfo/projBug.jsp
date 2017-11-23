<%@ page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header.jsp"%>
<%
	String projId=request.getParameter("projId");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>项目问题列表</title>
<link rel="stylesheet" href="<%=cssPath%>/cmp/tab.css" type="text/css" />
<link rel="stylesheet" href="<%=cssPath%>/style.css">
<link rel="stylesheet" href="<%=cssPath%>/cmp/Calendar.css">
<link rel="stylesheet" href="<%=contextPath%>/project/css/dialog.css">
<script type="text/Javascript" src="<%=contextPath%>/core/js/datastructs.js"></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/sys.js"></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/prototype.js"></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/smartclient.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/Calendarfy.js"></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/cmp/select.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/cmp/tab.js"></script>
<script type="text/javascript"	src="<%=contextPath%>/core/js/cmp/Menu.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/attach.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/attachMenu.js"></script>
<script type="text/javascript" src="<%=contextPath %>/project/js/jquery.min.js"></script>
<script type="text/javascript" src="<%=contextPath %>/project/js/dialog.js"></script>
<script type="text/javascript" src="<%=contextPath %>/project/js/util.js"></script>
<script type="text/javascript">
jQuery.noConflict();
var projId=<%=projId%>;
function doInit(){
	 var url= contextPath + "/t9/project/bug/act/T9ProjBugAct/getBugInfoList.act?projId="+projId;
	 var rtJson = getJsonRs(url);
	 if(rtJson.rtState == "0"){
		  var data=rtJson.rtData;
		  if(data.size()>0){
	    var str="<table class=\"TableList\" width=\"100%\">"
				   	 + "<tr class=\"TableHeader\">"
				     + "<td nowrap align=\"center\">问题名称</td>"
				     + "<td nowrap align=\"center\">提交人</td>"
				     + "<td nowrap align=\"center\">处理人</td>"
				     + "<td nowrap align=\"center\">处理结果</td>"
				     + "<td nowrap align=\"center\" width=\"120px\">处理底线</td>"
				     + "<td nowrap align=\"center\" width=\"120px\">优先级</td>"
				     + "<td nowrap align=\"center\" width=\"120px\">状态</td>"
				     + "</tr>";
	    var bugList="";
				for(var i=0;i<data.size();i++){
					bugList +="<tr class=\"TableLine1\">"
								  + "<td nowrap align=\"center\"><a href=\"#\" onclick=\"getDetailInfo("+data[i].seqId+")\">"+data[i].bugName+"</a></td>"
								  + "<td nowrap align=\"center\">"+data[i].beginUser+"</td>"
								  + "<td nowrap align=\"center\">"+data[i].dealUser+"</td>"
								  + "<td nowrap align=\"center\" title=\""+data[i].result+"\">"+showResult(data[i].result)+"</td>"
								  + "<td nowrap align=\"center\">"+data[i].deadLine+"</td>"
								  + "<td nowrap align=\"center\"><span>"+getLevel(data[i].level)+"</span></td>"
								  + "<td nowrap align=\"center\">"+getStatus(data[i].status)+"</td></tr>";
				}
	    $("bugList").innerHTML=str+bugList+"</table>";
		  }else{
			  WarningMsrg('无相关项目问题', 'msrg');
		  }
	 }else{
		 alert(rtJson.rtMrsg);
	 }  
}

function getStatus(status){
	if(status=="0"){
		return "未提交";
	}else if(status=="1"){
		return "处理中";
	}else if(status=="2"){
		return "已反馈";
	}else{
		return "已超时";
	}
}

function getLevel(level){
	if(level=="0"){
		return "<font color=gray>低</font>"
	}else if(level=="1"){
		return "<font color=green>普通</font>"
	}else if(level=="2"){
		return "<font color=#ff9933>高</font>"
	}else{
		return "<font color=red>非常高</font>"
	}
}

function getDetailInfo(bugId){
	 var str="<table width=\"80%\" align=\"center\" class=\"TableList\" border=\"0\">";
	 var url= contextPath + "/t9/project/bug/act/T9ProjBugAct/getBugInfo.act?bugId="+bugId;
	 var rtJson = getJsonRs(url);
	 if(rtJson.rtState == "0"){
		 var data=rtJson.rtData;
			str +="<tr><td  nowrap class=\"TableContent\">问题名称：</td><td class=\"TableData\" >"+data.bugName+"</td></tr>"
			 		 + "<tr><td nowrap class=\"TableContent\">提交人：</td><td class=\"TableData\" >"+data.beginUser+"</td></tr>"
			 		 + "<tr><td nowrap class=\"TableContent\">问题描述：</td><td class=\"TableData\" >"+data.bugDesc+"</td></tr>"
			 		 + "<tr><td nowrap class=\"TableContent\">处理期限：</td><td class=\"TableData\" >"+data.deadLine+"</td></tr>"
			 		 + "<tr><td nowrap class=\"TableContent\">附件文档：</td><td class=\"TableData\" >"
			 		 + "<div id=\"projAttach\">"
			     + "<input type=\"hidden\" id=\"attachmentId1\" name=\"attachmentId\">"
			     + "<input type=\"hidden\" id=\"attachmentName1\" name=\"attachmentName\">"
			     + "<input type=\"hidden\" id=\"moduel\" name=\"moduel\" value=\"project\">"
			     + "<span id=\"showAtt1\">无附件 </span></div></td></tr>"
			 		 + "<tr><td nowrap class=\"TableContent\">处理记录：</td><td class=\"TableData\" >"+showResult(data.result)+"</td></tr>"
		}
		$("detail_body").innerHTML=str+"</table>";
		$('attachmentId1').value=data.attachmentId ;
		$('attachmentName1').value=data.attachmentName;
		var  selfdefMenu2 = {
			    office:["downFile","dump","read"], 
			    img:["downFile","dump","play"],
			    music:["downFile","dump","play"],  
			    video:["downFile","dump","play"], 
			    others:["downFile","dump"]
			  }

	if( $("attachmentId1").value){
		  attachMenuSelfUtil("showAtt1","project",$('attachmentName1').value ,$('attachmentId1').value, '','','',selfdefMenu2);  
		  }
		ShowDialog('detail')
} 

function showResult(result){
	if(result=="null"){
		return "";
	}else{
		var results=result.split("|*|");
		var str="";
		for(var i=0;i<results.size();i++){
			str+="<span>"+results[i]+"</span><br/>";
		}
		return str;
	}
}
</script>
</head>
<body class="bodycolor" topmargin="5" onload="doInit();">
<table border="0" width="100%" cellspacing="0" cellpadding="3" class="small">
	<tr>
	   <td>
    <img src="../../images/bug.gif" align="absmiddle"/>
    <span class="big3"> 项目问题记录</span>
	<td></tr>
</table>
<div id="bugList">
			<div style="margin:0 auto;text-align:center;font-size:18px;font-color:blue"></div>
</div>
<div id="msrg">
</div>
<div id="overlay"></div>
<div id="detail" class="ModalDialog" style="width:550px;">
  <div class="header"><span id="title" class="title">项目问题详情</span><a class="operation" href="javascript:HideDialog('detail');"><img src="../../images/close.png"/></a></div>
  <div id="detail_body" class="body">
  </div>
  <div id="footer" class="footer">
    <input class="BigButton" onclick="HideDialog('detail')" type="button" value="关闭"/>
  </div>
</div>
</body>
</html>