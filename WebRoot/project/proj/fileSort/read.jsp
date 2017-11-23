<%@ page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header.jsp"%>
<%
	String seqId=request.getParameter("seqId");
String sortName=request.getParameter("sortName");
String contentId=request.getParameter("contentId");
String editPriv=request.getParameter("editPriv");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>基本信息</title>
<link rel="stylesheet" href="<%=cssPath%>/cmp/tab.css" type="text/css" />
<link rel="stylesheet" href="<%=cssPath%>/style.css">
<link rel="stylesheet" href="<%=cssPath%>/cmp/Calendar.css">
<script type="text/Javascript" src="<%=contextPath%>/core/js/datastructs.js"></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/sys.js"></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/prototype.js"></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/smartclient.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/Calendarfy.js"></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/cmp/select.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/cmp/tab.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/attach.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/swfupload/swfupload.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/swfupload/swfupload.queue.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/swfupload/fileprogress.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/swfupload/handlers.js"></script>
<script type="text/javascript"	src="<%=contextPath%>/core/js/cmp/Menu.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/attachMenu.js"></script>
<script type="text/javascript">
var editPriv='<%=editPriv%>';

function doInit(){
	var url="<%=contextPath %>/t9/project/file/act/T9ProjFileAct/getFileInfoById.act?seqId=<%=contentId%>";
	var json=getJsonRs(url);
	if(json.rtState == '0'){
		$('subject').innerHTML=json.rtData[0].subject;
		$('fileDesc').innerHTML=json.rtData[0].content;
		$('userName').innerHTML=json.rtData[0].userName;
		if(editPriv==1){
			$('editButton').style.display="";
			}
		if(json.rtData[0].attachmentName==""||json.rtData[0].attachmentName==null){
				$('attr').innerHTML="无";
			}else{
		var  selfdefMenu = {
		      	office:["downFile","dump","read","edit","deleteFile","rename"], 
		        img:["downFile","dump","play","deleteFile","rename"],  
		        music:["downFile","dump","play","deleteFile"],  
				    video:["downFile","dump","play","deleteFile"], 
				    others:["downFile","dump","deleteFile","rename"]
					}

				attachMenuSelfUtil("attr","proj_file",json.rtData[0].attachmentName,json.rtData[0].attachmentId, '','','<%=contentId%>',selfdefMenu);
				}
		getHistory();
	}else{
		alert(json.rtMsrg);
		}
}
function getHistory(){
	var url="<%=contextPath %>/t9/project/file/act/T9ProjFileLogAct/getTree.act?seqId=<%=contentId%>";
	var json=getJsonRs(url);
	if(json.rtState == '0'){
		for(var i=0;i<json.rtData.size();i++){
			$('history').innerHTML+=json.rtData[i].log+"<br>";
		}
	}
}
function toEdit(){
	var url = "<%=contextPath %>/project/proj/fileSort/edit.jsp?seqId=<%=seqId%>&contentId=<%=contentId%>&sortName=<%=sortName%>";
	window.location.href(url);
}
/*
* 打开新窗口
* 
*/
function newWindow(url, width, height, nameStr) {
 var defaultName = "newWindos";
 if (nameStr) {
   defaultName = nameStr;
 }
 var locX = (screen.width - width) / 2;
 var locY = (screen.height - height) / 2;
 if (newWin) {
   if (!newWin.closed)
     newWin.close();
 }
 newWin = window
     .open(
         url,
         defaultName,
         "height="
             + height
             + ",width="
             + width
             + ",status=1,toolbar=no,menubar=no,location=no,scrollbars=yes, top="
             + locY + ", left=" + locX + ", resizable=yes");
}
</script>
</head>
<body class="bodycolor" topmargin="0" onload="doInit()">

<table border="0" width="100%" cellspacing="0" cellpadding="3" class="small">
  <tr>
    <td class="Big"><img src="<%=contextPath %>/core/styles/style1/img/folder_file.gif" width="22" align="absmiddle"><b><span class="Big1">查看文件</span></b>
    </td>
  </tr>
</table>

  <form action=""  method="post" name="form1">
<table class="TableBlock" width="100%" align="center">
    <tr>
      <td class="TableHeader" align="center" colspan="2"><b><span class="big" id="subject"></span></b></td>
    </tr>
    <tr class="small">
      <td class="TableData" width="150">内容:
                </td>
      <td class="TableData" width="400" id="fileDesc">
                </td>
    </tr>
     <tr class="small">
      <td class="TableData" width="150">文件:
                </td>
      <td class="TableData" width="400" id="AttachmentName">
      <div id="attr"></div>
                </td>
    </tr>
      <tr class="small">
         <td class="TableData" width="150">创建人：</td>
         <td class="TableData" width="400" id="userName"></td>
       </tr>
       <tr class="small">
         <td class="TableData" width="150">操作记录：</td>
         <td class="TableData" width="400" id="history"></td>
       </tr>
      
    <tr align="center" class="TableControl">
      <td colspan="2" align="center">
      <div style="width: 40%;" align="center">
      <div id="editButton" style="float: left;display: none;width: 50%">
        <input type="button" value="编辑" class="BigButton" onClick="toEdit()">&nbsp;&nbsp;
        </div>
        <div style="float: left;width: 50%">
        <input type="button" value="返回" class="BigButton" onClick="location='<%=contextPath%>/project/proj/fileSort/folder.jsp?seqId=<%=seqId %>&sortName=<%=sortName%>'">
        </div>
        </div>
      </td>
    </tr>
  </table>
</form>
</body>
</html>