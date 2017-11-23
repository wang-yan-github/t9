<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header.jsp" %>
<%@ page import="t9.core.funcs.person.data.T9Person,t9.core.funcs.workflow.util.T9WorkFlowUtility" %>
<%
String sFlowId = request.getParameter("flowId");

T9Person loginUser = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
String userPrivOther  = loginUser.getUserPrivOther();
boolean isAdmin = false;
if (loginUser.isAdminRole() 
    || T9WorkFlowUtility.findId(userPrivOther , "1")){
  isAdmin = true;
}
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>设置流程属性</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href = "<%=cssPath %>/style.css">
<link rel="stylesheet" href="<%=cssPath %>/cmp/tab.css" type="text/css" />
<link rel="stylesheet" href="<%=cssPath %>/cmp/swfupload.css" type="text/css" />
<link rel="stylesheet" href="<%=cssPath %>/cmp/ExchangeSelect.css" type="text/css" />
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/attach.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/prototype.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/tab.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/datastructs.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/sys.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/smartclient.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/swfupload/swfupload.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/swfupload/swfupload.queue.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/swfupload/fileprogress.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/swfupload/handlers.js"></script>
<script type="text/javascript"	src="<%=contextPath%>/core/js/cmp/Menu.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/attachMenu.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/orgselect.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/ExchangeSelect1.0.js"></script>
<script type="text/javascript" src="edit.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/funcs/workflow/workflowUtility/utility.js"></script>
<script type="text/javascript">
var flowId = "<%=(sFlowId != null ? sFlowId : "")%>";
var isIE=!!window.ActiveXObject;
var isIE6=isIE&&!window.XMLHttpRequest;
var isAdmin = <%=isAdmin%>;
var uDeptId = <%=loginUser.getDeptId()%>;
var upload_limit=1,limit_type=limitUploadFiles;
var oa_upload_limit=limitUploadFiles;
var swfupload;

function ShowAddImg(postfix,attachPre)
{  
  //alert("aaa");
  // if(isUndefined(postfix)) postfix="";
  // document.write('&nbsp;&nbsp;<a id="add_image" href="javascript:;" onclick="ShowImage(this.id, \''+postfix+'\')" onfocus="this.blur();">插入正文'+(is_ie ? '<span style="font-family:Webdings">6</span>' : '<img src="/images/menu_arrow_down.gif" align="absMiddle">')+'</a><div id="add_image_menu" class="attach_div"></div>');
  if(isUndefined(postfix)) postfix="";
  if(isUndefined(attachPre)) attachPre = "";
  document.write('&nbsp;&nbsp;<a id="add_image" class="addimage" href="javascript:;"  >插入图片<input class="addfile" type="file" name="ATTACHMENT'+postfix+'_1000" id="ATTACHMENT'+postfix+'_1000" size="1" hideFocus="true" onchange="AddFile(\'image\');" onclick="closeImage()"/></a>');
  var cntrl = $('add_image');
  cntrl.observe("mouseover", this.addImagMenu.bindAsEventListener(this, 'add_image',attachPre));
  
}
function closeImage(){
	document.getElementById('add_image').style.visibility="hidden";
}
function initSwfUpload() {
    var linkColor = document.linkColor;
    var settings = {
      flash_url : "<%=contextPath %>/core/cntrls/swfupload.swf",
      upload_url: "<%=contextPath %>/t9/core/funcs/news/act/T9NewsHandleAct/fileLoad.act",
      post_params: {"PHPSESSID" : "<%=session.getId()%>"},
      file_size_limit : (maxUploadSize + " MB"),
      file_types : "*.*",
      file_types_description : "All Files",
      file_upload_limit : 100,
      file_queue_limit : 0,
      custom_settings : {
        uploadRow : "fsUploadRow",
        uploadArea : "fsUploadArea",
        progressTarget : "fsUploadProgress",
        startButtonId : "btnStart",
        cancelButtonId : "btnCancel"
      },
      debug: false,
      button_image_url: "<%=imgPath %>/uploadx4.gif",
      button_width: "65",
      button_height: "29",
      button_placeholder_id: "spanButtonPlaceHolder",
      button_text: '<span class=\"textUpload\">批量上传</span>',
      button_text_style: ".textUpload{color:" + linkColor + ";}",
      button_text_top_padding : 1,
      button_text_left_padding : 18,
      button_placeholder_id : "spanButtonUpload",
      button_width: 70,
      button_height: 18,
      button_window_mode: SWFUpload.WINDOW_MODE.TRANSPARENT,
      button_cursor: SWFUpload.CURSOR.HAND,
      
      file_queued_handler : fileQueued,
      file_queue_error_handler : fileQueueError,
      file_dialog_complete_handler : fileDialogComplete,
      upload_start_handler : uploadStart,
      upload_progress_handler : uploadProgress,
      upload_error_handler : uploadError,
      upload_success_handler : uploadSuccessOver,
      upload_complete_handler : uploadComplete,
      queue_complete_handler : queueComplete
    };

    swfupload = new SWFUpload(settings);
    var attachmentIds = $("attachmentId").value;
    var attachmentName = $("attachmentName").value;
    
    if(attachmentIds){
        attachMenuUtil("showAtt","news",null,attachmentName,attachmentIds,false);
    }
      
  }

function InsertImage(src){ 
  var oEditor = FCKeditorAPI.GetInstance('Econtent') ; //FCK实例 
  if ( oEditor.EditMode == FCK_EDITMODE_WYSIWYG ) {     
     oEditor.InsertHtml( "<img id='_temp_img' src='"+ src + "'/>") ; 
     var img = oEditor.EditorDocument.getElementById("_temp_img");
 		if (img && img.src) {
 			src  =  src.replace("&amp;" , "&");
 			src  =  src.replace("&amp;" , "&");
 			src  =  src.replace("&amp;" , "&");
 			src  =  src.replace("&amp;" , "&");
 			img.setAttribute("src" , src);
 			img.id = "";
 		}
  } 
  }

function openForm(){
  var formId = $F('formId');
  viewForm(formId);
}

function openDesign(){ 
  openFlowDesign(flowId);
}
function menuCode(type, id){
  var title = "菜单定义指南";
  var URL = contextPath + "/core/module/menucode/index.jsp?type=" + type + "&id=" + id;
  showModalWindow(URL , title , "menuWindow" ,600,350);
}
function explortFlow() {
  window.open(contextPath + "/t9/core/funcs/workflow/act/T9FlowExportAct/exportFlow.act?flowId=" + flowId);
}
function resizeDiv() {
  var oldHeight = 38;
  if (isIE6) {
    oldHeight =   50 ;
  }
  var height = (document.viewport.getDimensions().height -  oldHeight) + 'px';
  $('bodyDiv').style.height = height;
}
function addExpress(ex) {
  var txt2 = document.getElementById("autoName"); 
  var i = getCaret(txt2);       
  txt2.value = txt2.value.substr(0, i) + 
       ex + txt2.value.substring(i);
}
//返回光标所在位置function getCaret(textbox) {
  var control = document.activeElement;
  textbox.focus();
  var rang = document.selection.createRange();
  rang.setEndPoint("StartToStart",textbox.createTextRange())
  control.focus();
  return rang.text.length;
}
function clearUser(input, hidden){
  if($(input).tagName == 'INPUT'){
    $(input).value = "";  
  }else if($(input).tagName == 'TEXTAREA'){
    $(input).innerHTML = '';  
  }
  $(hidden).value = "";
}

function upload_atta() {
	/* alert(document.getElementById("add_image"));
	if(document.getElementById("attachLink_0")){
		document.getElementById('add_image').remove();
	} */
	$("btnFormFile").click();
	document.getElementById('add_image').style.visibility="hidden";
	//document.getElementById('add_image').remove();
	/* var attachLink = document.getElementById("attachLink_0");
	if(!attachLink){
	 	$("btnFormFile").click();
	}else{
		$("btnFormFile").click();
		alert(1);
		$("#fsUploadRow").empty();
		alert(2);
	} */
	
}
</script>
<script type="text/javascript" src="<%=contextPath %>/core/funcs/news/manage/js/newsAdd.js"></script>
</head>
<body onload="doInit()" style="margin:0px;">
 <div id="bodyDiv">
<table  border="0" cellspacing="0" cellpadding="3">
  <tr>
    <td><img src="<%=imgPath %>/edit.gif" WIDTH="22" HEIGHT="20" align="absmiddle"><span class="big3"> <span id="optionTitle"></span></span>&nbsp;<br>
    </td>
  </tr>
</table>
 <form action="" method="post" id="editForm" name="editForm">
<table width="90%" align="center" class="TableList">
    <tr class="TableHeader">
      <td colspan=4><img src="<%=imgPath %>/green_arrow.gif" align="absmiddle"> 流程基本属性<span id="flowIdSpan"></span> </td>
    </tr>
    <tr  class="TableLine2">
      <td nowrap class="TableContent" width=120>流程分类：</td>
      <td class="TableData">
        <select name="flowSort" id="flowSort" >
        </select>
      </td>
      <td nowrap class="TableContent">流程排序号：</td>
      <td class="TableData">
        <input type="text"  onkeyup="value=value.replace(/[^\d]/g,'')"  name="flowNo" id="flowNo" size="4" maxlength="100" class="BigInput" value=""> 控制同一分类下流程的排序
      </td>
    </tr>
    <tr  class="TableLine2"> 
      <td nowrap class="TableContent">流程名称：</td>
      <td class="TableData"  colspan=3>
        <input type="text" name="flowName" id="flowName" size="30" maxlength="100" class="BigInput" value="">
      </td>
    </tr>
    
    
    <tr class="TableLine2">
      <td nowrap class="TableContent">流程类型：</td>
      <td class="TableData"  <% if (!isAdmin) { out.print(" colspan=3"); }%>>
        <select name="flowType" id="flowType" onchange="type_change()">
          <option value="1" >固定流程</option>
          <option value="2" >自由流程</option>
        </select>
      </td>
      <% 
if (isAdmin) { 
%>
      <td nowrap class="TableContent">所属部门：</td>
      <td class="TableData">
      <select id='deptId' name="deptId">
      <option value="0">系统管理员</option>
      </select>
      </td>
       <%} %>
    </tr>
    <tr  class="TableLine1">
      <td nowrap class="TableContent">表单：</td>
      <td class="TableData"  colspan=3>
        <select name="formId" id="formId" >
      
        </select>
      </td>
    </tr>
    <tr  class="TableLine2" id="FREE_SET" style="display:none">
      <td nowrap class="TableContent">允许预设步骤：</td>
      <td class="TableData"  colspan=3>
      <select name="freePreset" id="freePreset">
      <option value="1">是</option>
      <option value="0"  selected>否</option>
      </select>
      </td>
   </tr>
   <tr  class="TableLine1">
    <td nowrap class="TableContent">委托类型：</td>
      <td class="TableData">
        <select name="freeOther" id="freeOther">
        	<option value="2" >自由委托</option>
          <option value="3" >按步骤设置的经办权限委托</option>
          <option value="1" >仅允许委托当前步骤经办人</option>
          <option value="0" >禁止委托</option>
        </select>
        <a href="javascript:showOrHide('tip1');">查看说明</a>
        <div id='tip1' style="display:none">
        委托类型说明：<br>
        <b>自由委托：</b>用户可以在工作委托模块中设置委托规则,可以为委托给任何人。<br>
        <b>按步骤设置的经办权限委托：</b>仅能委托给流程步骤设置中经办权限范围内的人员<br>
        <b>按实际经办人委托：</b>仅能委托给步骤实际经办人员。<br>
        <b>禁止委托：</b>办理过程中不能使用委托功能。<br>
        <b>注意：</b>只有自由委托才允许定义委托规则，委托后更新自己步骤为办理完毕，主办人变为经办人
        </div>
      </td>
      <td nowrap class="TableContent">允许附件：</td>
      <td class="TableData">
        <select name="flowDoc" id="flowDoc" >
          <option value="1" >是</option>
          <option value="0" >否</option>
        </select>
      </td>
    </tr>
    </tr>
      <tr id="fileShowId" class="TableLine1">
      <td nowrap class="TableContent">附件选择：</td>
      <td class="TableData" id="fsUploadRow" colspan="3">
	        	<input type="hidden" id="attachmentId" name="attachmentId">
		        <input type="hidden" id="attachmentName" name="attachmentName">
		        <span id="showAtt" style="float:left;">
		        </span>
		      <div id="attachment1">
		          <script>ShowAddFile();ShowAddImg();</script>
		          <script>$("ATTACHMENT_upload_div").innerHTML='<a href="javascript:upload_atta();">上传附件</a>'</script>
	        	</div>
      </td>
    </tr>
    <tr class="TableLine1">
      <td nowrap class="TableContent">流程说明：</td>
      <td class="TableData">
        <textarea cols=40   name="flowDesc" id="flowDesc" rows="3" class="BigInput" wrap="yes"></textarea>
      </td>
      <td nowrap class="TableContent">传阅人：</td>
      <td class="TableData">
      <input type="checkbox" name="VIEW_PRIV" id="VIEW_PRIV" value="1"  style="position:relative;top:2px;"/><label for="VIEW_PRIV">允许传阅</label><br />
            <input type="hidden" name="VIEW_USER_ID"  id="VIEW_USER_ID"  value="">
            <textarea cols=30 name="VIEW_USER_NAME" id="VIEW_USER_NAME" rows=2 class="BigStatic" wrap="yes" readonly></textarea>
            <a href="javascript:;" class="orgAdd" onClick="selectUser(['VIEW_USER_ID', 'VIEW_USER_NAME'])">选择</a>
            <a href="javascript:;" class="orgClear" onClick="clearUser( 'VIEW_USER_NAME','VIEW_USER_ID')">清空</a>
      </td>
    </tr>
    <tr  class="TableHeader">
      <td colspan=4><img src="<%=imgPath %>/green_arrow.gif" align="absmiddle"> 工作名称/文号的设定  <a href="javascript:showOrHide('num');">显示/隐藏 </a></td>
    </tr>
    <tbody style="display:none;" id="num">
    <tr class="TableLine1">
      <td nowrap class="TableContent">自动文号表达式：</td>
      <td class="TableData"  colspan=3>
        <input type="text"  name="autoName" id="autoName" size="30" class="BigInput" value="">
        &nbsp;&nbsp;<a href="javascript:showOrHide('tip');">查看说明</a>
      </td>
    </tr>
    <tr  class="TableLine2" >
      <td nowrap class="TableContent">自动编号计数器：</td>
      <td class="TableData">
        <input type="text"   onkeyup="value=value.replace(/[^\d]/g,'')"  name="autoNum" id="autoNum" size="11" class="BigInput" value="0"><br>用于表达式编号标记      </td>
       <td nowrap class="TableContent">自动编号显示长度：</td>
      <td class="TableData">
        <input type="text"   onkeyup="value=value.replace(/[^\d]/g,'')"  name="autoLen" id="autoLen" size="2" class="BigInput" value="0"><br>为0表示按实际编号位数显示
      </td>
    </tr>
    <tr id="tip"  class="TableLine2" style="display:none">
      <td nowrap class="TableContent">自动文号表达式说明：</td>
      <td class="TableData"  colspan=3>表达式中可以使用以下特殊标记：<br>
      <table>
        <tr>
          <td>
          {Y}：表示年
          </td>
          <td>
          <input type=button class="SmallButtonC" value="插入年" onclick="addExpress('{Y}')"/>
          </td>
        </tr>
        <tr>
          <td>
          {M}：表示月
          </td>
          <td>
          <input type=button class="SmallButtonC" value="插入月" onclick="addExpress('{M}')"/>
          </td>
        </tr>
        <tr>
          <td>
          {D}：表示日
          </td>
          <td>
          <input type=button class="SmallButtonC" value="插入日" onclick="addExpress('{D}')"/>
          </td>
        </tr>
        <tr>
          <td>
          {H}：表示时
          </td>
          <td>
          <input type=button class="SmallButtonC" value="插入时" onclick="addExpress('{H}')"/>
          </td>
        </tr>
        <tr>
          <td>
         {I}：表示分
          </td>
          <td>
          <input type=button class="SmallButtonC" value="插入分" onclick="addExpress('{I}')"/>
          </td>
        </tr>
        <tr>
          <td>
          {S}：表示秒
          </td>
          <td>
          <input type=button class="SmallButtonC" value="插入秒" onclick="addExpress('{S}')"/>
          </td>
        </tr>
        <tr>
          <td>
          {F}：表示流程名
          </td>
          <td>
          <input type=button class="SmallButtonC" value="插入流程名" onclick="addExpress('{F}')"/>
          </td>
        </tr>
        <tr>
          <td>
          {FS}：表示流程分类名称
          </td>
          <td>
         <input type=button class="SmallButtonC" value="插入分类名称" onclick="addExpress('{FS}')"/>
          </td>
        </tr>
        <tr>
          <td>
          {U}：表示用户姓名
          </td>
          <td>
          <input type=button class="SmallButtonC" value="插入姓名" onclick="addExpress('{U}')"/>
          </td>
        </tr>
        <tr>
          <td>
          {SD}：表示短部门
          </td>
          <td>
         <input type=button class="SmallButtonC" value="插入部门" onclick="addExpress('{SD}')"/>
          </td>
        </tr>
        <tr>
          <td>
          {LD}：表示长部门
          </td>
          <td>
         <input type=button class="SmallButtonC" value="插入部门" onclick="addExpress('{LD}')"/>
          </td>
        </tr>
        <tr>
          <td>
          {R}：表示角色
          </td>
          <td>
          <input type=button class="SmallButtonC" value="插入角色" onclick="addExpress('{R}')"/>
          </td>
        </tr>
        <tr>
          <td>
          {RUN}：表示流水号
          </td>
          <td>
          <input type=button class="SmallButtonC" value="插入流水号" onclick="addExpress('{RUN}')"/>
          </td>
        </tr>
        <tr>
          <td>
          {N}：表示编号，通过 <u>编号计数器</u> 取值并自动增加计数值
          </td>
          <td>
          <input type=button class="SmallButtonC" value="插入编号" onclick="addExpress('{N}')"/>
          </td>
        </tr>
      </table>
        <br>
        例如，表达式为：成建委发[{Y}]{N}号<br>
        同时，设置自动编号显示长度为4<br>
        则自动生成的文号如下：成建委发[2006]0001号<br>
        <br>
        例如，表达式为：BH{N}<br>
        同时，设置自动编号显示长度为3<br>
        则自动生成的文号如下：BH001<br>
        <br>
        例如，表达式为：{F}流程（{Y}年{M}月{D}日{H}:{I}）{U}<br>
        自动生成文号如：请假流程（2006年01月01日10:30）张三<br>
        <br>
        可以不填写自动文号表达式，则系统默认按以下格式，如：<br>
        请假流程(2006-01-01 10:30:30)
      </td>
    </tr>
    <tr  class="TableLine1" >
      <td width=120 class="TableContent">新建工作时是否允许手工修改文号：</td>
      <td class="TableData" colspan="3">
        <select name="autoEdit" id='autoEdit'>
          <option value="1">允许手工修改文号</option>
          <option value="0">不允许手工修改文号</option>
          <option value="2">允许在自动文号前输入前缀</option>
          <option value="3">允许在自动文号后输入后缀</option>
          <option value="4">允许在自动文号前后输入前缀和后缀</option>
        </select>
      </td>
    </tr>
    </tbody>
 <tr  class="TableHeader"  id="formItemTr">
      <td nowrap height=25 colspan="4">
        <div style="font-weight:bold">
        	<img src="<%=imgPath %>/green_arrow.gif" align="absmiddle"> 设置列表扩展字段
        	<a href="javascript:showOrHide('advSet');">显示/隐藏 </a>
        </div>
      </td>
    </tr>
    <tr  class="TableLine1" id="advSet" style="display:none;">
      <td colspan=4 align="center">
      	<div id="exchangeDiv"></div>
      </td>
    </tr>
    <tr align="center" class=TableControl>
      <td colspan="4" nowrap>
        <input type='hidden' value="" id="flowId" name="flowId">
        <input type='hidden' value="" name="DEPT_ID_OLD">
        <!-- added 090216  -->
        <input type='hidden' value="" id="listFldsStr" name="listFldsStr">
        <!-- /added 090216  -->
        <% if (!isAdmin) { %>
      <input type='hidden' value="" name="deptId" id="deptId">
      <%} %>
        <input type="button"  value="保存" class="BigButton" name="save" onClick="commit();">&nbsp;&nbsp;
        <input type="button"  value="返回" class="BigButton" name="back" onClick="history.back();">
      </td>
    </tr>
</table>
</form>
<form id="formFile" action="<%=contextPath %>/t9/core/funcs/news/act/T9NewsHandleAct/uploadFile.act" method="post" enctype="multipart/form-data" target="commintFrame">
  <input id="btnFormFile" name="btnFormFile" type="submit" style="display:none;"></input>
  </form>
<iframe widht="0" height="0" name="commintFrame" id="commintFrame"></iframe>
</div>
<div align="center" id="wpiroot" style="height: 20px;display:none">
<TABLE class=TableList width="98%" align=center>
<TBODY>
<TR>
<TD class=TableContent height=25 colSpan=2 noWrap>
<DIV style="FONT-WEIGHT: bold">
<IMG align=absMiddle src="<%=imgPath %>/green_arrow.gif"> 相关操作</DIV></TD>
<TD class=TableData align="left" noWrap>
<span id="isOperDesign">
<A href="javascript:openDesign()"><IMG border=0 align=absMiddle src="<%=imgPath %>/arrow_down.gif" width=11 height=13> 流程设计器 </A></span>
&nbsp;<A title="" href="javascript:openForm()"><IMG border=0 align=absMiddle src="<%=imgPath %>/sys_config.gif"> 预览表单</A>
 <a href="javascript:menuCode('FLOW' , '<%=sFlowId %>')"><IMG border=0 align=absMiddle src="<%=imgPath %>/system.gif" width=19 height=17> 菜单定义指南</A>
 <a href="javascript:clone()">克隆</A> 
 <a href="javascript:clear()">清空</A>
  <a id="delA" href="javascript:delFlowType()">删除</A>
   <a href="javascript:explortFlow()">导出</A> 
   <a href="javascript:location='import.jsp?flowId=<%=sFlowId %>'">导入</A> 
   </TD></TR></TBODY></TABLE></div>
</body>
<script type="text/javascript">
//隐藏添加附近|从文件柜和网络硬盘选择附件  
document.getElementById("linkAddAttach").style.display="none";//隐藏  
var classobj= new Array();//定义数组 

var classint=0;//定义数组的下标 
   
var tags=document.getElementsByTagName("*");//获取HTML的所有标签 
   
for(var i in tags){//对标签进行遍历 
	if(tags[i].nodeType==1){//判断节点类型 
		if(tags[i].getAttribute("class") == 'selfile')//判断和需要CLASS名字相同的，并组成一个数组 
		{ 
		tags[i].style.display="none"; 
		classint++; 
		} 
	} 
}
//浮动菜单文件的删除 
function deleteAttachBackHand(attachName,attachId,attrchIndex){ 
  var url= contextPath + "/t9/core/funcs/workflow/act/T9FlowTypeAct/delFloatFile.act?attachId=" + attachId +"&attachName=" + attachName ; 
  document.getElementById('add_image').style.visibility="visible";
  if (flowId) {
    url += "&seqId=" + flowId;
  }
  var json=getJsonRs(encodeURI(url)); 
  if(json.rtState =='1'){ 
    alert(json.rtMsrg); 
    return false; 
  }else { 
    prcsJson=json.rtData; 
    var updateFlag=prcsJson.updateFlag; 
    if(updateFlag){ 
      var ids = $('attachmentId').value ;
      if (!ids) {
        ids = ""; 
      }
      var names =$('attachmentName').value;
      if (!names) {
        names = ""; 
      }
      var idss = ids.split(",");
      var namess = names.split("*");
     
      var newId = getStr(idss , attachId , ",");
      var newname = getStr(namess , attachName , "*");  
     
      $('attachmentId').value = newId;
      $('attachmentName').value = newname;
      return true; 
   }else{ 
     return false; 
   }  
  } 
}
</script>
</html>