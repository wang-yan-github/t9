<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header.jsp" %>
<%
String runId = request.getParameter("runId");
String flowId = request.getParameter("flowId");
String prcsId = request.getParameter("prcsId");
String flowPrcs = request.getParameter("flowPrcs");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>归档文件</title>
<link rel="stylesheet" href ="<%=cssPath %>/workflow.css">
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/attach.js"></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/prototype.js" ></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/datastructs.js" ></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/sys.js" ></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/smartclient.js" ></script>
<script type="text/javascript" src="<%=contextPath %>/core/funcs/workflow/workflowUtility/utility.js"></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/orgselect.js" ></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/Calendarfy.js"></script>
<script type="text/javascript" src="<%=contextPath %>/subsys/oa/rollmanage/js/rollfilelogic.js"></script>
<link rel="stylesheet" href="<%=cssPath%>/cmp/Calendar.css">
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/attachMenu.js"></script>
<script type="text/javascript"  src="<%=contextPath%>/core/js/cmp/attach.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/cmp/Menu.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/funcs/workflow/flowrun/list/js/pickup.js"></script>
<script type="text/javascript">
var runId = "<%=runId %>";
var flowId = "<%=flowId %>";
var prcsId = "<%=prcsId %>";
var requestUrl = contextPath + "/t9/core/funcs/workflow/act/T9FlowRollAct";
function doInit() {
  var url = requestUrl +  "/loadRollData.act?runId=" + runId;
  var json = getJsonRs(url);
  if (json.rtState == '0') {
    var userPriv = json.rtData.userPriv;
    if (!userPriv) {
      $('hasPriv').hide();
      $('noPriv').show();
      return ;
    } else {
      var runData = json.rtData.runData;
      if (runData) {
        setRunData(runData);
      }
      var ids = json.rtData.attachmentId;
      var names = json.rtData.attachmentName;
      setAttachment({attachmentId:ids , attachmentName:names});
    }
  }
    setDate();
	getSecretFlag("RMS_SECRET","SECRET");
	getSecretFlag("RMS_URGENCY","URGENCY");
	getSecretFlag("RMS_FILE_TYPE","FILE_TYPE");
	getSecretFlag("RMS_FILE_KIND","FILE_KIND");
	checkSelectBox2();
}

function setAttachment(attachment) {
  var  selfdefMenu = {
     office:["downFile","read"], 
      img:["downFile","play"],  
      music:["downFile","play"],  
	    video:["downFile","play"], 
	    others:["downFile"]
		}
     $('ATTACHMENT_ID_OLD').value = attachment.attachmentId;
     $('ATTACHMENT_NAME_OLD').value = attachment.attachmentName;
	attachMenuSelfUtil("attr","roll_manage",attachment.attachmentName ,attachment.attachmentId, 'bb','',"",selfdefMenu);
}
function setRunData(runData) {
  if(runData && runData.length > 0){
   for(i=0;i < runData.length;i++) { 
     var name = runData[i][0];
     var value = runData[i][1];
     if (!name) {
       continue;
     }
     for (j = 0 ;j < field.length ;j ++) {
       var f = field[j];
       if (f[0] == name) {
         var fi = f[1];
         setValue(fi , value);
       }
     }
    }
  }
}
  function setSelectedValue(fi , value) {
    var option = fi.getElementsByTagName("option");
    for (var i = 0 ;i < option.length ;i ++) {
      var op = option[i];
      var text = op.innerHTML;
      if (text == value) {
        op.selected = true;
      }
    }
  }
function setValue(fi , value) {
  var f = $(fi);
    if (f) {
      if (f.tagName == "SELECT") {
        setSelectedValue(f , value);
      } else {
        f.value = value;
      }
    }
}

function setDate() {
  var date1Parameters = {
      inputId:'SEND_DATE',
      property:{isHaveTime:false}
      ,bindToBtn:'date1'
   };
   new Calendar(date1Parameters);
}
function checkDate(){
	var leaveDate1 = document.getElementById("SEND_DATE"); 
	var leaveDate1Array = leaveDate1.value.trim().split(" "); 
	if(!leaveDate1.value){
		return true;
	}
	if(!isValidDateStr(leaveDate1Array[0])){
		alert("日期格式不对，应形如 1999-01-01"); 
		leaveDate1.focus(); 
		leaveDate1.select(); 
		return false; 
	}
	return true;
}
function CheckForm()
{
   if(document.form1.FILE_CODE.value=="")
   { alert("文件号不能为空！");
     return (false);
   }
   if(document.form1.FILE_TITLE.value=="")
   { alert("文件名称不能为空！");
     return (false);
   }
   if(checkDate() == false ){
 		return false;
 	}
   document.form1.ATTACHMENT_COUNT.value = ROW_COUNT; 
   return (true);
}

function sendForm() {
  if(CheckForm()){
   // $('button').disable = false;
    document.form1.submit();
  }
}
function refreshClienArea()
{
 var tempStr=HIDDEN_ROW_ID.innerHTML.replace("style=\"display:","");

 tempStr=tempStr.replace("none\"","");

 var repStr2="id=ROW_ID_"+ROW_COUNT;
 tempStr=tempStr.replace("id=ROW_ID_",repStr2);
 tempStr=tempStr.replace("id=\"ROW_ID_\"",repStr2);

 var repStr2="id=UPFILE_"+ROW_COUNT;
 tempStr=tempStr.replace("id=UPFILE_",repStr2);
 tempStr=tempStr.replace("id=\"UPFILE_\"",repStr2);
 
 var repStr3="ATTACHMENT_"+ROW_COUNT;
 tempStr=tempStr.replace("ATTACHMENT_",repStr3); 
 
 if (tmpStr=="")
 {
  tmpStr = tempStr;   	
 }
 else
 {
  tmpStr += tempStr;
 }
 clientArea.innerHTML += tempStr;

}

function deleteClienArea()
{    
  ROW_ID=eval("ROW_ID_"+ROW_COUNT);
  ROW_ID.innerHTML='';
  ROW_ID.id="none"; 
  ROW_ID.style.display="none"; 
}
var tmpStr="";
var ROW_COUNT=1;
var dataTemStr="";
var fieldNum2="";
function clickInsertRow()
{
 ROW_COUNT++;
 tmpStr = clientArea.innerHTML;
 refreshClienArea();
}

function clickDeleteRow()
{
  if(tmpStr=="" || ROW_COUNT==1)
  {
     alert("没有附件，无法删除！");   	
     return;
  }	
	
	if(ROW_COUNT!=1)
	{
     deleteClienArea();
     ROW_COUNT--;
  }
}
</script>
</head>
<body onload="doInit()">
<div id="hasPriv">

<table border="0" width="90%" cellspacing="0" cellpadding="3" class="small">
  <tr>
    <td class="Big"><img src="<%=imgPath %>/notify_new.gif" align="absmiddle"><span class="big3"> 新建文件</span>
    </td>
  </tr>
</table>

<table class="TableBlock" width="90%"  align="center">
  <form enctype="multipart/form-data"  action="<%=contextPath %>/t9/core/funcs/workflow/act/T9FlowRollAct/saveRoll.act" id="form1" method="post" name="form1">
<input type="hidden" value="0" name="FILE_ID" id="FILE_ID">
  <TR>
      <TD class="TableData">文件号：</TD>
      <TD class="TableData">
       <INPUT name="FILE_CODE" id="FILE_CODE" size=20 maxlength="100" dataType="Require" require="true" msg="文件号不能为空！" class="BigInput"value="">
      </TD>
      <TD class="TableData">文件主题词：</TD>
      <TD class="TableData">
       <INPUT name="FILE_SUBJECT" id="FILE_SUBJECT" size=30 maxlength="100" class="BigInput" value="">
      </TD>
  </TR>
  <TR>
      <TD class="TableData">文件标题：</TD>
      <TD class="TableData">
       <INPUT name="FILE_TITLE" id="FILE_TITLE" size=30 maxlength="100" dataType="Require" require="true" msg="文件标题不能为空！" class="BigInput" value="">
      </TD>
      <TD class="TableData">文件辅标题：</TD>
      <TD class="TableData">
       <INPUT name="FILE_TITLE0" id="FILE_TITLE0" size=30 maxlength="100" class="BigInput" value="">
      </TD>
  </TR>
  <TR>
      <TD class="TableData">发文单位：</TD>
      <TD class="TableData">
       <INPUT name="SEND_UNIT" id="SEND_UNIT" size=30 dataType="Require" require="true" msg="发文单位不能为空！" class="BigInput" value="">
      </TD>
      <TD class="TableData">发文日期：</TD>
      <TD class="TableData">
      
        <input type="text" name="SEND_DATE" id="SEND_DATE" size="10" maxlength="10" class="BigInput" value="" >
      <img id="date1" align="middle" src="<%=imgPath %>/calendar.gif" align="middle" border="0" style="cursor:pointer" >&nbsp;
      </TD>
  </TR>
  <TR>
      <TD nowrap class="TableData">密级：</TD>
      <TD class="TableData">
	<select name="SECRET" ID="SECRET" class="BigSelect">
	<option value="" ></option>
	</select>
      </TD>
      <TD class="TableData">紧急等级：</TD>
      <TD class="TableData">
	<select name="URGENCY" id="URGENCY" class="BigSelect">
	<option value="" ></option>
	</select>
     </TD>
  </TR>
  <TR>
      <TD nowrap class="TableData">文件分类：</TD>
      <TD class="TableData">
	<select name="FILE_TYPE" id="FILE_TYPE" class="BigSelect">
	  <option value="" ></option>
	</select>
      </TD>
      <TD class="TableData">公文类别：</TD>
      <TD class="TableData">
	<select name="FILE_KIND" id="FILE_KIND" class="BigSelect">
	<option value="" ></option>
	</select>
     </TD>
  </TR>
  <TR>
      <TD nowrap class="TableData">文件页数：</TD>
      <TD class="TableData">
        <input type="text" name="FILE_PAGE" id="FILE_PAGE" value="" size="10" maxlength="50" class="BigInput" dataType="Number" require="false" msg="文件页数必须是数字！" value="<?=$FILE_PAGE?>">
      </TD>
      <TD class="TableData">打印页数：</TD>
      <TD class="TableData">
        <input type="text" name="PRINT_PAGE"  id="PRINT_PAGE" value="" size="10" maxlength="50" class="BigInput" dataType="Number" require="false" msg="打印页数必须是数字！" value="<?=$PRINT_PAGE?>">
      </TD>
  </TR>
  <TR>
      <TD nowrap class="TableData">备注：</TD>
      <TD class="TableData"><input type="text" name="REMARK" value="" size="30" maxlength="100" class="BigInput" value="<?=$REMARK?>"></TD>
      <TD class="TableData">所属案卷：</TD>
      <TD class="TableData">
 <select name=rollId id="rollId" class="SmallSelect">
	</select>
      </TD>
   </TR>
   <tr class="TableData">
      <td nowrap>附件文档：</td>
      <td nowrap colSpan=3 id="attr"> 
      </td>
    </tr>
   <TR>
      <TD colSpan=4 nowrap class="TableData">
        <IMG style="CURSOR: hand" onclick="clickInsertRow('');" src="<%=contextPath %>/core/funcs/workflow/flowrun/list/inputform/img/topplus.gif">
        <IMG style="CURSOR: hand" onclick="clickDeleteRow('');" src="<%=contextPath %>/core/funcs/workflow/flowrun/list/inputform/img/buttomplus.gif">
	添加附件：
      </TD>
   </TR>
  <TR>
      <TD colSpan=4 class="TableData">
	<div id="ROW_ID_1" width=100%>
	  <TABLE width=100% border=0>
	  <TR>
	    <TD nowrap align="left" class="TableData"><input type='file' class=BigInput name='ATTACHMENT_1' id=UPFILE_1 size='50'>
	    </TD>
	 </TR>
	 </TABLE>
	</div>
    <!-- start 附件显示区域 -->
    <DIV id="clientArea" name="clientArea"></DIV>
    <!-- end 附件显示区域 -->
    </TD>
  </TR>
  <TR style="display:none">
      <TD colSpan=4>
	<div id="HIDDEN_ROW_ID">
	<div id="ROW_ID_" width=100%>
	  <TABLE width=100% border=0>
	  <TR>
	    <TD nowrap align="left" class="TableData"><input type='file' class=BigInput name='ATTACHMENT_' id=UPFILE_ size='50'>
	    </TD>
	 </TR>
	 </TABLE>
	</div>
	</div>
      </TD>
   </TR>
    <tr align="center" class="TableControl">
      <td colspan="4" nowrap>
        <input type="hidden" name="OP" id="OP" value="">
        <input type="hidden" value="" name="ATTACHMENT_ID_OLD" id="ATTACHMENT_ID_OLD">
        <input type="hidden" value="" name="ATTACHMENT_NAME_OLD"  id="ATTACHMENT_NAME_OLD">
        <input type="hidden" name="ATTACHMENT_COUNT" value="">        
        <input type="button" id="button" value="保存" class="BigButton" onclick="sendForm();">&nbsp;&nbsp;
        <input type="reset" value="重置" class="BigButton">&nbsp;&nbsp;
      </td>
    </tr>
  </table>
</form>
 </div>
 <div id="noPriv" style="display:none;" align="center">
 <table class="MessageBox" align="center" width="320" >
  <tr>
    <td class="msg info">
      <h4 class="title">提示</h4>
      <div class="content" style="font-size:12pt" id="tip">您没有档案管理模块的权限</div>
    </td>
  </tr>
</table>
<input type="button" onclick="window.close();" value="关闭" class="BigButton" title="关闭此窗口">&nbsp;&nbsp;
</div>
</body>
</html>