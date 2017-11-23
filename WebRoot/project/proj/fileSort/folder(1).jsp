<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header.jsp" %> 
<%
	String sortId=request.getParameter("seqId");
	String sortName=request.getParameter("sortName");
	String parentId=request.getParameter("parentId");
	T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
	String userId=person.getSeqId()+"";
	String flag = (String)request.getParameter("flag")==null?"0":(String)request.getParameter("flag");
	String projId=request.getParameter("projId");
	if(sortId==null){
	  sortId="";
	}	
	//获取复制、剪切的session
	String actionFlag = (String) request.getSession().getAttribute("folderActionStr");
	String folderSeqId = (String) request.getSession().getAttribute("folderSeqId");
	if(actionFlag==null){
	  actionFlag="";
	}
	if(folderSeqId==null){
		folderSeqId="";
	}
  String pageIndex = request.getParameter("pageNo");
  if(pageIndex == null){
    pageIndex = "0";
  }
  String pageSize = request.getParameter("pageSize");
  if(pageSize == null){
    pageSize = "20";
  }
  String pageAscDesc=request.getParameter("ascDescFlag");
  String pageField=request.getParameter("field");
	if(pageAscDesc==null || "".equals(pageAscDesc.trim())){
		pageAscDesc="1";
	}
	if(pageField==null || "".equals(pageField.trim())){
		pageField="NAME";
	}
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@page import="t9.core.funcs.person.data.T9Person"%><html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>文件柜</title>
<link rel="stylesheet"	href="<%=cssPath%>/style.css">
<script type="text/Javascript"	src="<%=contextPath%>/core/js/datastructs.js"></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/sys.js"></script>
<script type="text/Javascript"	src="<%=contextPath%>/core/js/prototype.js"></script>
<script type="text/Javascript"	src="<%=contextPath%>/core/js/smartclient.js"></script>
<script type="text/Javascript"	src="<%=contextPath%>/core/js/cmp/select.js"></script>
<script type="text/Javascript"	src="<%=contextPath%>/core/js/orgselect.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/cmp/page.js"></script>
<!-- 文件上传 -->
<link href="<%=cssPath %>/cmp/swfupload.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/swfupload/swfupload.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/swfupload/swfupload.queue.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/swfupload/fileprogress.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/swfupload/handlers.js"></script>
<!-- <script type="text/javascript" src="<%=contextPath %>/core/js/cmp/attach.js"></script> -->
<script type="text/javascript" src="<%=contextPath%>/core/js/cmp/Menu.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/attachMenu.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/pagePilot.js"></script>
<link rel="stylesheet" href="<%=cssPath%>/page.css">
<script type="text/javascript">
var upload_limit=1,limit_type=limitUploadFiles;
var oa_upload_limit=limitUploadFiles;
var swfupload;
var field = '<%=pageField%>'; 
var ascDesc = '<%=pageAscDesc%>';
var pageSize = "<%=pageSize%>";
var totalRecord = 0;
var pageIndex = "<%=pageIndex%>";
var cfgs;
var actionFlag='<%=actionFlag%>';
var requestURL="<%=contextPath%>/t9/project/file/act/T9ProjFileAct";
var requestURL1="<%=contextPath%>/t9/project/file/act/T9ProjFileSortAct";
var sortId='<%=sortId%>';
var  pageMgr  =  null;
var newWin = 0;
var newPriv = 0;
var viewPriv = 0;
var editPriv = 0;
var managePriv = 0;
var flag=<%=flag%>;

/*
 * 页面初始化
 */
function doInit(){
	  if(flag==1){
		  $('fileManage').style.display="none";
		  }
	getPriv();
	if(viewPriv==0){
		$('noPrivDiv').style.display="";
		$('selectList').style.display="none";
		$('fileManage').style.display="none";
		}
	$('sortName').innerHTML='<%=sortName%>';
  getPages();
  checkPriv();
  if(actionFlag=="copyFile"||actionFlag=="cutFile"){
			$("paste_file").style.display="";	
	}
	var  selfdefMenu = {
        	office:["downFile", "dump", "read", "readNoPrint", "edit", "setSign", "deleteFile"], 
	        img:["downFile", "dump", "play"],  
	        music:["downFile", "dump", "play"],  
			    video:["downFile", "dump", "play"],
			    pdf: ["readpdf", "downFile", "dump"], 
			    others:["downFile", "dump"]
				};
  var  returnAttId = document.getElementsByName("returnAttId");
  var  returnAttName = document.getElementsByName("returnAttName");
  var  returnAttSeqId = document.getElementsByName("returnAttSeqId");
  for(var i=0; i<returnAttId.length; i++){
	  attachMenuSelfUtil("attr_"+returnAttSeqId[i].value,"proj_file",returnAttName[i].value ,returnAttId[i].value, returnAttSeqId[i].value,'',returnAttSeqId[i].value,selfdefMenu);
  }
}
function getPriv(){
	var url1=requestURL1 + "/getSortPrivById.act?seqId=<%=sortId%>";
	var json=getJsonRs(url1);
	
	if(json.rtState == '0'){
		if((","+json.rtData[0].newUsers).indexOf(","+<%=userId%>+",")!=-1){
			newPriv = 1;
			}
		if((","+json.rtData[0].viewUsers).indexOf(","+<%=userId%>+",")!=-1){
			viewPriv = 1;
			}
		if((","+json.rtData[0].editUsers).indexOf(","+<%=userId%>+",")!=-1){
			editPriv = 1;
			}
		if((","+json.rtData[0].delUsers).indexOf(","+<%=userId%>+",")!=-1){
			managePriv = 1;
			
			}
		
	}else{
		alert(json.rtMsrg);
		return ;				
		}
}
function checkPriv(){
	if(newPriv==0){
		$('newFile').innerHTML="无新建权限";
		}
	if(managePriv==1){
		$('manageRow').style.display="";
		}
}
/*
 * 分页显示功能
 */
function getPages(){
    var url="<%=contextPath%>/t9/project/file/act/T9ProjFileAct/getPages.act?sortId="+sortId;
    var cfgs=null;
	if(flag==1){
    cfgs ={
        dataAction:url,
        container:"selectList",
        afterShow: getTotal,
        colums:[
                {type:"selfdef",name:"selects", text:"选择",align:"center", width:"5%",render:toCheck},
                {type:"hidden",name:"seqId",text:"seqId",dataType:"int",align:"center"},
                {type:"data",name:"subject",text:"文件标题",align:"center",width:'25%',render:getInfo},
                {type:"data",name:"userName",text:"上传者",width:'25%',align:"center"},
                {type:"hidden",name:"attachmentId",text:"附件id",align:"center"},
                {type:"data",name:"attachmentName",text:"附件",align:"center",width:'25%',render:getAttr},
                {type:"data",name:"updateTime",text:"发布时间",align:"center",width:'20%'}
                ]
                }
	}else{
    cfgs ={
        dataAction:url,
        container:"selectList",
        afterShow: getTotal,
        colums:[
                {type:"selfdef",name:"selects", text:"选择",align:"center", width:"5%",render:toCheck},
                {type:"hidden",name:"seqId",text:"seqId",dataType:"int",align:"center"},
                {type:"data",name:"subject",text:"文件标题",align:"center",width:'20%',render:getInfo},
                {type:"data",name:"userName",text:"上传者",width:'20%',align:"center"},
                {type:"hidden",name:"attachmentId",text:"附件id",align:"center"},
                {type:"data",name:"attachmentName",text:"附件",align:"center",width:'20%',render:getAttr},
                {type:"data",name:"updateTime",text:"发布时间",align:"center",width:'20%'},
                {type:"selfdef", text:"操作", align:"center",width: '15%',render:opts}]
                }
		}
    pageMgr = new T9JsPage(cfgs);
    pageMgr.show();
}
function getInfo(cellData, recordIndex, columInde){
	var seqId = this.getCellData(recordIndex,"seqId");
	return "<a href='javascript: selectInfo("+seqId+ ")' >" + cellData+  "</a>";
}
/*
 * 增加操作列
 */
function opts(cellData, recordIndex, columInde){
    if(editPriv==1){
    var seqId = this.getCellData(recordIndex,"seqId");
    var edit="<a href=\"javascript:editOne("+seqId+");\" >编辑</a>&nbsp&nbsp";
    }else{
    	var edit="无编辑权限"
        }
    
    return edit;
}
function getAttr(cellData, recordIndex, columInde){
	if(cellData!=""&&cellData!="null"){
	var seqId = this.getCellData(recordIndex,"seqId");
	var attachmentId = this.getCellData(recordIndex,"attachmentId");
	return "<input type = 'hidden' id='returnAttId_" + seqId + "' value='"+attachmentId+"' name='returnAttId'></input>"
	+ "<input type = 'hidden' id='returnAttName_" + seqId + "' value='"+cellData+"' name='returnAttName'></input>"
	+ "<input type = 'hidden' id='returnAttSeqId' name='returnAttSeqId' value='"+seqId+"'></input>"
	+ "<div id='attr_" + seqId + "'></div>";
	}else{
		return "";
		}
}
function editOne(seqId){
	 var url = contextPath + "/project/proj/fileSort/edit.jsp?seqId=" + sortId + "&contentId=" + seqId +"&sortName=<%=sortName%>";
	  window.location.href(url);
}
/**
 * 查看详情
 * @param paraName
 * @return
 */
function selectInfo(seqId){
	var url=contextPath + "/project/proj/fileSort/read.jsp?seqId=" + sortId + "&contentId=" + seqId +"&sortName=<%=sortName%>&editPriv="+editPriv;
	window.location.href(url);
    }
/*
* 单项或多项删除
*/
function delete_arrang(){
	var delStr=get_checked();
	if(delStr==""){
		alert("请至少选择一个文件");
		return;
	}
	msg="确定要删除选择文件吗？这将不可恢复！"
	if(window.confirm(msg)){
		var url=requestURL + "/delCheckedFile.act?seqIdStr="+delStr;
		var json=getJsonRs(url);
		if(json.rtState == '1'){
			alert(json.rtMsrg);
			return ;				
		}
		window.location.reload();
	}
}
/*
* 通用分页插入全选tr
*/
function insertTr(table) {
 var currRowIndex = table.rows.length;
 var mynewrow = table.insertRow(currRowIndex);// 新建一行

 mynewrow.className = "TableControl";
 var cellnum = mynewrow.cells.length;
 var mynewcell = mynewrow.insertCell(cellnum);// 新建一列

 mynewcell.colSpan = "6";
 if(flag==1){
 mynewcell.innerHTML ="<div style='float:left;' >&nbsp;&nbsp;&nbsp;<input type='checkbox' name='allbox' id='allbox' onClick='check_all();'><label for='allbox' style='cursor:pointer'>全选</label>&nbsp;&nbsp;</div>"
	  + "<div style='float:left;'>"
		+ "<a href='javascript:do_action(\"downFile\");' id='downFile' ><img src='<%=imgPath%>/download.gif' align='center' border='0' title='批量压缩后下载'><span id='label_down'>下载</span>&nbsp;&nbsp;</a>"
		+ "</div>"
	  +"</td>"
	 }else{
 mynewcell.innerHTML ="<div style='float:left;' >&nbsp;&nbsp;&nbsp;<input type='checkbox' name='allbox' id='allbox' onClick='check_all();'><label for='allbox' style='cursor:pointer'>全选</label>&nbsp;&nbsp;</div>"
	  + "<div id='manageRow' style='float:left;display:none;'>"
		+ "<a href='javascript:do_action(\"copyFile\")' id='copyFile'><img src='<%=contextPath %>/core/funcs/filefolder/images/copy.gif' align='center' border='0' title='复制所选文件'>复制&nbsp;&nbsp;</a>"
		+ "<a href='javascript:do_action(\"cutFile\")' id='cutFile'><img src='<%=contextPath %>/core/funcs/filefolder/images/cut.gif' align='center' border='0' title='剪切所选文件'>剪切&nbsp;&nbsp;</a>"
		+ "<a href='javascript:delete_arrang();' id='delFile'><img src='<%=contextPath %>/core/funcs/filefolder/images/delete.gif' align='center' border='0' title='删除所选文件'>删除&nbsp;&nbsp;</a>"
		+ "</div>"
	  + "<div style='float:left;'>"
		+ "<a href='javascript:do_action(\"downFile\");' id='downFile' ><img src='<%=imgPath%>/download.gif' align='center' border='0' title='批量压缩后下载'><span id='label_down'>下载</span>&nbsp;&nbsp;</a>"
		+ "</div>"
	  +"</td>"
		 }
}
/*
* 获取已选项
*/
function get_checked() {
 checked_str = "";
 for (i = 0; i < document.getElementsByName("selects").length; i++) {
   el = document.getElementsByName("selects").item(i);
   if (el.checked) {
     val = el.value;
     checked_str += val + ",";
   }
 }
 if (i == 0) {
   el = document.getElementsByName("selects");
   if (el.checked) {
     val = el.value;
     checked_str += val + ",";
   }
 }
 return checked_str;
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
function toCheck(cellData, recordIndex, columInde) {
	  var seqId = this.getCellData(recordIndex, "seqId");
	  return "<input type='checkbox'  id='selects' name='selects' value='"
	      + seqId + "' onClick='check_one(self);'>";

	}
/**
 * 全选
 */
function check_all() {

	  var t = document.getElementsByName("selects");
	  for (i = 0; i < document.getElementsByName("selects").length; i++) {
	    if (document.getElementsByName("allbox")[0].checked) {
	      document.getElementsByName("selects").item(i).checked = true;
	    } else {
	      document.getElementsByName("selects").item(i).checked = false;
	    }
	  }
	  if (i == 0) {
	    if (document.getElementsByName("allbox")[0].checked) {
	      document.getElementsByName("selects").checked = true;
	    } else {
	      document.getElementsByName("selects").checked = false;
	    }
	  }
}
function check_one(el) {
	  if (!el.checked)
	    document.getElementsByName("allbox")[0].checked = false;

}
function getTotal() {
	  var table = pageMgr.getDataTableDom();
	  insertTr(table);
}
	//对文件操作
function do_action(action){
		var selects=get_checked();
		var idStr=selects.split(",");
		var count=idStr.length-1;
		if(count <= 0){
			alert("请至少选择一个文件");
			return ;
		}
			if(action == "copyFile"){
				var url=requestURL+"/copyFileByIds.act?folderSeqId=<%=sortId%>&seqIdStrs="+selects+"&action="+action;
				var json=getJsonRs(url);
		    if(json.rtState == '0'){
		    	alert("选择的文件已“复制”\n请到目标目录中进行“粘贴”操作");
		    	$("paste_file").style.display="";	
		    }else{
					alert(json.rtMsrg);
		    }
			}else if(action=="cutFile"){			
				var url=requestURL+"/copyFileByIds.act?seqIdStrs="+selects+"&action="+action;
				var json=getJsonRs(url);
		    if(json.rtState == '0'){
		    	alert("选择的文件已“剪切”\n请到目标目录中进行“粘贴”操作");
		    	$("paste_file").style.display="";	
		    }else{
					alert(json.rtMsrg);
		    }
			}else{
			if(count>1 && window.confirm("一次下载多个文件需要在服务器上做压缩处理，会占用较多服务器CPU资源，确定继续下载吗？\n该操作请不要下载超过128MB的大文件")){
				location.href = requestURL + "/batchDownload.act?sortId=<%=sortId%>&contentIdStr=" + selects + "&name=";
			}else if(count == 1){
				location.href = requestURL + "/batchDownload.act?sortId=<%=sortId%>&contentIdStr=" + selects + "&name=";
			}
				}
}
/**
 * 粘贴文件
 */
function paste_File(){
	var url=requestURL + "/pasteFile.act?sortId=<%=sortId%>";
	var rtJson=getJsonRs(url);
  if(rtJson.rtState == '0'){
 		window.location.reload();
  }
}
/*
 * 新建文件
 */
function newFile(){
	  var folderPath= $("sortName").innerHTML.trim();
	  var url="<%=contextPath%>/project/proj/fileSort/newfile.jsp?seqId=<%=sortId %>&folderPath=" + folderPath+"&projId=<%=projId%>";
	 	location.href = encodeURI(url);
	}
</script>
</head>
<body onload="doInit()">
<table id="headTableStr" border="0" width="100%" cellspacing="0" cellpadding="3" class="small">
  <tr>
    <td class="Big3"><img src="<%=contextPath %>/core/funcs/filefolder/images/notify_open.gif" align="middle"><b>&nbsp;<span class="Big1" id="sortName"> </span></b><br>
   </td>
   </tr>
   <tr>
    <td id="pageDiv" align="right" valign="bottom" class="small1">
	   <div id="pageInfo" style="float:right;"></div>
  	</td>
  </tr>
</table>
<div id="listDiv" align="center"></div>
<div id="nothingDiv" align="center"></div>

<div id="noPrivDiv" style="display: none">
<table class="MessageBox" align="center" width="340">
  <tr>
    <td class="msg error">
      <h4 class="title">错误</h4>
      <div class="content" style="font-size:12pt">您没有权限访问该目录</div>
    </td>
  </tr>
</table>

</div>

<br>
<div id="selectList">
</div>


<div id="fileManage">
<br>
<table id="fileTable" class="TableBlock" width="100%" align="center">
  <tr>
    <td class="TableContent"  align="center" width="65"><b>文件操作：</b></td>
    <td class="TableControl">&nbsp;
   		<span id="paste_file" style="display: none;" title="粘贴文件"><a href="javascript:paste_File()" style="height:20px;"><img src="<%=contextPath %>/core/funcs/filefolder/images/paste.gif" align="middle" border="0">&nbsp;粘贴</a>&nbsp;&nbsp;</span>
   		<span id="newFile" ><a href="javascript:newFile();" title="创建新的文件" style="height:20px;"><img src="<%=imgPath%>/notify_new.gif" align="middle" border="0">&nbsp;新建文件&nbsp;&nbsp;</a></span>
   		<span id="battUpload" ><span id="spanButtonUpload" title="批量上传">&nbsp;&nbsp;</span></span>
 		</td>
  </tr>
</table>
</div>
</body>
</html>