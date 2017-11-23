<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header.jsp" %>
<%String projId= (String)request.getParameter("projId")==null?"0":(String)request.getParameter("projId");
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>项目批注</title>
<link rel="stylesheet" href="<%=cssPath%>/page.css">
<link rel="stylesheet" href="<%=cssPath%>/style.css">
<link rel="stylesheet" href="<%=cssPath%>/cmp/Calendar.css">
<script type="text/javascript" src="<%=contextPath%>/core/js/prototype.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/cmp/tab.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/datastructs.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/sys.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/smartclient.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/cmp/Calendarfy.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/cmp/Menu.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/cmp/page.js"></script>
<script type="text/javascript">
var pageMgr=null;
var projId='<%=projId%>';
var newWin = 0;
function doInit(){
	$('projId').value= projId;
  var url = "<%=contextPath%>/t9/project/comment/act/T9ProjCommentAct/getPages.act?projId="+projId;
  var date = new Date();  
  $("WRITE_TIME").value= date.pattern("yyyy-MM-dd hh:mm:ss");
  var cfgs = {
    dataAction: url,
    container: "listContainer",
    sortIndex: 1,
    sortDirect: "desc",
    colums: [
       {type:"hidden", name:"seqId", text:"顺序号", dataType:"int"},
       {type:"data", name:"writer",  width: '20%', text:"批注领导" ,align: 'center'},
       {type:"data", name:"content",  width: '40%', text:"批注内容" ,align: 'center'},
       {type:"data", name:"writerTime",  width: '20%', text:"批注时间" ,align: 'center'},
       {type:"selfdef", width: '20%',text:"操作" ,align: 'center',render:opts}]
  };
  pageMgr = new T9JsPage(cfgs);
  pageMgr.show();
}
/**
 * 操作
 * @param cellData
 * @param recordIndex
 * @param columIndex
 * @return
 */
function opts(cellData, recordIndex, columIndex){
	var seqId = this.getCellData(recordIndex,"seqId");
	var attachId=this.getCellData(recordIndex,"attachmentId");
	var attachName=this.getCellData(recordIndex,"attachmentName");
	return "<center>"
				+ "<a href=javascript:updateComment(" + seqId +")>修改</a>&nbsp;"
				+ "<a href=javascript:deleteOne(" + seqId+")>删除</a>"
				+ "</center>";
}
function deleteOne(seqId){
	if(confirm("确认要删除此条批注？")){
		var url = "<%=contextPath%>/t9/project/comment/act/T9ProjCommentAct/deleteComment.act?seqId="+seqId;
		 
	    var rtJson = getJsonRs(url);
	   
	    if (rtJson.rtState == "0") {
	      alert(rtJson.rtMsrg);
	      window.location.reload();
	      $("projId").value=projId;
	    }else {
	      alert(rtJson.rtMsrg); 
	    }
		}
}
/**
 * 修改页面
 * @param seqId
 * @return
 */
function updateComment(seqId){
  var URL = contextPath + "/project/proj/editComment.jsp?seqId=" + seqId;
  window.navigate(URL); 
}
/**
* 打开新窗口  newWindow(URL,'740', '540');
* @param url
* @param width
* @param height
* @return
*/
function newWindow(url,width,height){
	var locX=(screen.width-width)/2;
	var locY=(screen.height-height)/2;
	window.open(url, "meeting", 
			"height=" +height + ",width=" + width +",status=1,toolbar=no,menubar=no,location=no,scrollbars=yes, top=" 
			+ locY + ", left=" + locX + ", resizable=yes");
}

 function getGroup(){
	 if(checkForm()){
	  if(!pageMgr){
	    pageMgr = new T9JsPage(cfgs);
	    pageMgr.show();
	  }
	  else{
	    pageMgr.search();
	  }
	 }
	}
function checkForm(){
	if($("content").value==""){
		alert("请输入批注内容");
		return false;
		}
	return true;
	
}
function submitButton(){
	if(checkForm()){
		if($('SMS_REMIND').checked){
			$('sendSms').value=1;
		}
	 var url = "<%=contextPath%>/t9/project/comment/act/T9ProjCommentAct/addComment.act";
	    var rtJson = getJsonRs(url, mergeQueryString($("form1")));
	    if (rtJson.rtState == "0") {
	      alert(rtJson.rtMsrg);
	      window.location.reload();
	      document.getElementById("projId").value = projId;
	    }else {
	      alert(rtJson.rtMsrg); 
		}
	}
}     
 Date.prototype.pattern=function(fmt) {        
     var o = {        
     "M+" : this.getMonth()+1, //月份        
     "d+" : this.getDate(), //日        
     "h+" : this.getHours()%12 == 0 ? 12 : this.getHours()%12, //小时        
     "H+" : this.getHours(), //小时        
     "m+" : this.getMinutes(), //分        
     "s+" : this.getSeconds(), //秒        
     "q+" : Math.floor((this.getMonth()+3)/3), //季度        
     "S" : this.getMilliseconds() //毫秒        
     };        
     if(/(y+)/.test(fmt)){        
         fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length));        
     }        
     if(/(E+)/.test(fmt)){        
         fmt=fmt.replace(RegExp.$1, ((RegExp.$1.length>1) ? (RegExp.$1.length>2 ? "\u661f\u671f" : "\u5468") : "")+week[this.getDay()+""]);        
     }        
     for(var k in o){        
         if(new RegExp("("+ k +")").test(fmt)){        
             fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));        
         }        
     }        
     return fmt;        
 } 

</script>
</head>
<body onload="doInit()">
<table border="0" width="100%" cellspacing="0" cellpadding="3" class="small">
  <tr>
    <td class="small"><img src="<%=contextPath %>/core/styles/style1/img/edit.gif" WIDTH="22" HEIGHT="20" align="absmiddle"><span class="big3"> 项目批注记录</span>
    </td>
  </tr>
</table>
<div  style="text-align:center;padding-top:20px;" >
 <div id="listContainer" style="width:95%;MARGIN-RIGHT: auto;MARGIN-LEFT: auto;">
 </div></div>
<div id="msrg">
</div>
<table border="0" width="100%" cellspacing="0" cellpadding="3" class="small">
  <tr>
    <td class="small"><img src="<%=contextPath %>/core/styles/style1/img/edit.gif" WIDTH="22" HEIGHT="20" align="absmiddle"><span class="big3"> 项目批注</span>
    </td>
  </tr>
</table>
<form action=""  method="post" name="form1" id="form1"> 

<table class="TableList" width="95%"  align="center" > 
   <tr>
    <td  class="TableContent" width="90">批注时间：</td>
    <td class="TableData">
      <input type="text" name="WRITE_TIME" id="WRITE_TIME" size="19" readonly="readonly" maxlength="100" class="BigStatic" value="">       
    </td>
   </tr>
   <tr>
     <td  class="TableContent"> 批注内容：</td>
     <td class="TableData" colspan="1">
       <textarea name="content" id="content" class="BigInput" cols="80" rows="6"></textarea>
     </td>
   </tr>
    <tr>
      <td  class="TableContent"> 提醒项目成员：</td>
      <td class="TableData">
      <input type="hidden" id="sendSms" name="sendSms" value="0">
<input type="checkbox" name="SMS_REMIND" id="SMS_REMIND"><label for="SMS_REMIND">发送事务提醒消息</label>&nbsp;&nbsp;      </td>
    </tr>       
   <tr>
    <td   class="TableControl" colspan="2" align="center">
    <input type="hidden" id="projId" name="projId"> 
      <input type="button" value="提交" onclick="submitButton()" class="BigButton">&nbsp;&nbsp;
    </td>
    </tr>
</table>
</form>
</body>
</html>