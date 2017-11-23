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
<title>发送公告通知</title>
<link rel="stylesheet" href ="<%=cssPath %>/workflow.css">
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/attach.js"></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/prototype.js" ></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/datastructs.js" ></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/sys.js" ></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/smartclient.js" ></script>
<script type="text/javascript" src="<%=contextPath %>/core/funcs/workflow/workflowUtility/utility.js"></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/orgselect.js" ></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/Calendarfy.js"></script>
<link rel="stylesheet" href="<%=cssPath%>/cmp/Calendar.css">
<script type="text/javascript">
var runId = "<%=runId %>";
var flowId = "<%=flowId %>";

function doInit() {
  var url =contextPath +  "/t9/core/funcs/workflow/act/T9FlowNotifyAct/getNotifyMsg.act?runId=" + runId;
  var json = getJsonRs(url);
  if (json.rtState == '0') {
    var userPriv = json.rtData.userPriv;
    if (userPriv) {
      var runName = json.rtData.runName;
      var isNeed = json.rtData.flag;
      var typeData = json.rtData.typeData;
      $('SUBJECT').value = runName;
      
      for(var i = 0; i < typeData.length; i++) {
        var optionStr = typeData[i];
        $("TYPE_ID").options.add(new Option(optionStr.typeDesc,optionStr.typeId)); 
      }
      if (isNeed == '1') {
        $('auditerTr').show();
        $('remindTip').update("提醒审批人");
        $('autButton').show();
        var auditers = json.rtData.auditer;
        for (var i = 0 ;i < auditers.length ;i++) {
          var aud = auditers[i];
          var op = new Element("option");
          op.value = aud.id; 
          op.update(aud.name);
          $('AUDITER').appendChild(op);
        }
      } else {
        $('publishButton').show();
      }
      
      var beginParameters = {
          inputId:'beginDate',
          property:{isHaveTime:false}
          ,bindToBtn:'beginDateImg'};
      new Calendar(beginParameters);
      
      var myDate=new Date();
      var month = '';
      var date = '';
      if ((parseInt(myDate.getMonth())+1) < 10) {
        month = '0' +  new String(parseInt(myDate.getMonth())+1);
      }else{
        month = myDate.getMonth() +1;
      } 
      if((parseInt(myDate.getDate())+1)<=10){
        date = '0' +  new String(parseInt(myDate.getDate()));
      }else {
        date = myDate.getDate();
      }
      var todadys= myDate.getFullYear()+'-'+month+'-'+date;
      document.getElementById('beginDate').value = todadys;
      var endParameters = {
        inputId:'endDate',
        property:{isHaveTime: false},
        bindToBtn:'endDateImg'
      };
      new Calendar(endParameters);
      
      getSysRemind();
      moblieSmsRemind('remidDiv','remind'); 
    } else {
      $('hasPriv').hide();
      $('noPriv').show();
    }
  }
}
//判断是否要显示短信提醒 
function getSysRemind(){ 
  var requestUrl =  contextPath + "/t9/core/funcs/mobilesms/act/T9MobileSelectAct/isShowSmsRmind.act?type=1"; 
  var rtJson = getJsonRs(requestUrl); 
  if(rtJson.rtState == "1"){ 
     var seqId = rtJson.rtData;
     alert(rtJson.rtMsrg); 
     return ; 
  } 
  var prc = rtJson.rtData; 
  allowRemind = prc.allowRemind; 
  defaultRemind = prc.defaultRemind; 

  if(allowRemind == '2') { 
    $("smsRemindDiv").style.display = 'none'; 
  }else { 
    if(defaultRemind == '1') { 
      $("mailRemind").checked = true; 
    } 
  }
}

/** 
*js代码 
*是否显示手机短信提醒 
*/ 
function moblieSmsRemind(remidDiv,remind){ 
  var requestUrl = contextPath + "/t9/core/funcs/mobilesms/act/T9MobileSelectAct/isShowSmsRmind.act?type=1"; 
  var rtJson = getJsonRs(requestUrl); 
  if(rtJson.rtState == "1"){ 
    alert(rtJson.rtMsrg); 
    return ; 
  } 
  var prc = rtJson.rtData; 
  moblieRemindFlag = prc.moblieRemindFlag;//手机默认选中  
  if(moblieRemindFlag == '2'){
    $(remidDiv).style.display = ''; 
    $(remind).checked = true; 
  }else if(moblieRemindFlag == '1'){ 
    $(remidDiv).style.display = ''; 
    $(remind).checked = false; 
  }else{ 
     $(remidDiv).style.display = 'none'; 
  }
}

function sendForm(flag) {
  document.form1.PUBLISH.value = flag;
  if(CheckForm())
  {
    var requestUrl = contextPath +   "/t9/core/funcs/workflow/act/T9FlowNotifyAct/saveNotify.act";
    var rtJson = getJsonRs(requestUrl , $('form1').serialize()); 
    if(rtJson.rtState == "0"){ 
      $('hasPriv').hide();
      $('noPriv').show();
      $("tip").update(rtJson.rtMsrg);
    } 
  }
}
function CheckForm() {
  if(document.form1.SUBJECT.value.trim()=="") { 
    alert("公告通知的标题不能为空！");
    document.form1.SUBJECT.value = "";
    document.form1.SUBJECT.focus();
    return (false);
  }
  if(document.form1.TO_ID.value==""　
      &&　document.form1.PRIV_ID.value==""　
      &&　document.form1.COPY_TO_ID.value=="") { 
    alert("请指定发布范围！");
    return (false);
  }
  var regex = /^((\d{2}(([02468][048])|([13579][26]))[\-\/\s]?((((0?[13578])|(1[02]))[\-\/\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\-\/\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\-\/\s]?((0?[1-9])|([1-2][0-9])))))|(\d{2}(([02468][1235679])|([13579][01345789]))[\-\/\s]?((((0?[13578])|(1[02]))[\-\/\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\-\/\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\-\/\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))/; //日期部分
  if(document.form1.beginDate.value != "") {
    if(!regex.test(document.form1.beginDate.value)){
      alert("输入的日期格式错误");
      document.form1.beginDate.value = "";
      document.form1.beginDate.focus();
      return false;
    }
  }else {
    var date = new Date();//当前日期
    var currDate = "";
    if(navigator.userAgent.indexOf("Firefox")>0){
		  currDate = date.getYear() + 1900 + "-" + appendBefore((date.getMonth()+1), 2, "0") + "-" + appendBefore(date.getDate(), 2, "0");
    }else{
	    currDate = date.getYear() + "-" + appendBefore((date.getMonth()+1), 2, "0") + "-" + appendBefore(date.getDate(), 2, "0");  
    }
    if(document.form1.endDate.value < currDate){
	    alert("终止日期不能小于当前日期");
	    return (false);
    }
  }
  if(document.form1.endDate.value != "") { 
       if(!regex.test(document.form1.endDate.value)){
       alert("输入的日期格式错误");
      document.form1.endDate.value = "";
       document.form1.endDate.focus();
         return false;
       }
  }
  if(document.form1.endDate.value){
    if(document.form1.endDate.value<document.form1.beginDate.value) {
      alert("生效日期不能晚于终止日期");
      return (false);
    }
  }
  if(document.form1.PRINT.value=="on" || document.form1.PRINT.value=="1") {
     document.form1.PRINT.value='1';
  }else {
     document.form1.PRINT.value='0';
  }
  if(document.form1.DOWNLOAD.value=="on" || document.form1.DOWNLOAD.value=="1") {
     document.form1.DOWNLOAD.value='1';
  }else {
     document.form1.DOWNLOAD.value='0';
  }
  if(document.form1.TOP.value == "on" || document.form1.TOP.value == "1") {
     document.form1.TOP.value = '1';
  }else {
     document.form1.TOP.value='0';
  }
  return true;
}
function ClearUser(TO_ID, TO_NAME) {
  if(!TO_ID){
    TO_ID = "TO_ID";
    TO_NAME = "TO_NAME";
  }
  document.getElementsByName(TO_ID)[0].value = "";
  document.getElementsByName(TO_NAME)[0].value = "";
}
</script>
</head>
<body onload="doInit()">
<div id="hasPriv">
<table border="0" width="100%" cellspacing="0" cellpadding="3" class="small">
  <tr>
    <td class="Big"><img src="<%=imgPath %>/notify.gif" WIDTH="22" HEIGHT="20" align="absmiddle"><span class="big3">公告通知发送工作流信息</span>
    </td>
  </tr>
</table>

<form id="form1"  method="post" name="form1">
<table border="0" width="100%" align="center" class="TableList">
    <tr>
      <td nowrap class="TableContent">发布范围（部门）：</td>
      <td class="TableData">
        <input type="hidden" id="TO_ID" name="TO_ID" value="">
        <textarea cols=40  id="TO_NAME"  name=TO_NAME rows=2 class="BigStatic" wrap="yes" readonly></textarea>
        <a href="javascript:;" class="orgAdd" onClick="selectDept(['TO_ID','TO_NAME'] , 5);">添加</a>
       <a href="javascript:;" class="orgClear" onClick="ClearUser('TO_ID','TO_NAME')">清空</a>
      </td>
    </tr>
    <tr>
      <td nowrap class="TableContent">发布范围（角色）：</td>
      <td class="TableData">
        <input type="hidden" name="PRIV_ID" ID="PRIV_ID" value="">
        <textarea cols=40 name="PRIV_NAME" ID="PRIV_NAME" rows=2 class="BigStatic" wrap="yes" readonly></textarea>
        <a href="javascript:;" class="orgAdd" onClick="selectRole(['PRIV_ID', 'PRIV_NAME'] , 5);">添加</a>
        <a href="javascript:;" class="orgClear" onClick="ClearUser('PRIV_ID', 'PRIV_NAME')">清空</a>
      </td>
   </tr>
   <tr>
      <td nowrap class="TableContent">发布范围（人员）：</td>
      <td class="TableData">
        <input type="hidden" name="COPY_TO_ID" id="COPY_TO_ID" value="">
        <textarea cols=40 name="COPY_TO_NAME" id="COPY_TO_NAME" rows=2 class="BigStatic" wrap="yes" readonly></textarea>
        <a href="javascript:;" class="orgAdd" onClick="selectUser(['COPY_TO_ID', 'COPY_TO_NAME'] , 5);">添加</a>
        <a href="javascript:;" class="orgClear" onClick="ClearUser('COPY_TO_ID', 'COPY_TO_NAME')">清空</a>
      </td>
   </tr>
   <tr>
      <td nowrap class="TableContent"> 标题：</td>
      <td class="TableData">
        <input type="text" id="SUBJECT" name="SUBJECT" size="55" maxlength="200" class="BigInput" value="">
      </td>
    </tr>
    <tr id="auditerTr" style="display:none">
      <td nowrap class="TableData"> 指定审批人：</td>
      <td class="TableData">
        <select name="AUDITER" id='AUDITER' class="BigSelect">
        </select>
      </td>
    </tr>
    <tr>
      <td nowrap class="TableContent"> 类型：</td>
      <td class="TableData"> 
        <select id="TYPE_ID" name="TYPE_ID" class="BigSelect">
          <option value=""></option>
        </select>&nbsp;
        公告通知类型可在“系统管理”->“系统代码设置”模块设置。
      </td>
    </tr>
    <tr>
      <td nowrap class="TableContent"> 有效期：</td>
      <td class="TableData">
        生效日期：<input type="text" id="beginDate" name="beginDate" size="10" maxlength="10" class="BigInput" value="" >
        <img id="beginDateImg" src="<%=imgPath%>/calendar.gif" align="absMiddle" border="0" style="cursor:pointer">
        终止日期：<input type="text"  id="endDate" name="endData" size="10" maxlength="10" class="BigInput" value="">
        <img id="endDateImg" src="<%=imgPath%>/calendar.gif" align="absMiddle" border="0"  style="cursor:pointer">
        为空为手动终止
      </td>
    </tr>
    <tr>
      <td nowrap class="TableContent"> 附件与权限：</td>
      <td class="TableData">
              <input type="checkbox" name="DOWNLOAD" id="DOWNLOAD" checked><label for="DOWNLOAD">允许下载Office附件</label>&nbsp;&nbsp;
              <input type="checkbox" name="PRINT" id="PRINT" checked><label for="PRINT">允许打印Office附件</label>&nbsp;&nbsp;&nbsp;<font color="gray">都不选中则只能阅读附件内容</font>   
      </td>
    </tr>
    <tr>
      <td nowrap class="TableContent" id="remindTip"> 提醒：</td>
      <td class="TableData">
<span id="smsRemindDiv" >
           <input type="checkbox" name="mailRemind" id="mailRemind" ><label for="mailRemind">使用内部短信提醒   </label>&nbsp;&nbsp;
           </span>
           <span id="remidDiv" >
           <input type="checkbox" name="remind" id="remind" ><label for="remind">使用手机短信提醒   </label>&nbsp;&nbsp;
           </span>
      </td>
    </tr>
    <tr>
      <td nowrap class="TableContent"> 置顶：</td>
      <td class="TableData">
        <input type="checkbox" name="TOP" id="TOP"><label for="TOP">使公告通知置顶，显示为重要</label>
      </td>
    </tr>
    <tr align="center" class="TableControl">
      <td colspan="2" nowrap>
      <input type="hidden" name="RUN_ID" value="<%=runId %>">
      <input type="hidden" name="FLOW_ID" value="<%=flowId %>">
      <input type="hidden" name="SEQ_ID" id="SEQ_ID" value="">
      <input type="hidden" name="PUBLISH" value="">
      <input type="hidden" name="OP" value="">
      <input id="publishButton" style="display:none" type="button" value="发布" class="BigButton" onclick="sendForm('1');" title="立即发布此公告通知">&nbsp;&nbsp;
      <input id="autButton" style="display:none"  type="button" value="提交审批" class="BigButton" onClick="sendForm('2');">&nbsp;&nbsp;
      <input type="button" value="保存" class="BigButton" onclick="sendForm('0');">&nbsp;&nbsp;
      <input type="button" onclick="window.close();" value="关闭" class="BigButton" title="关闭此窗口">&nbsp;&nbsp;
      </td>
</table>
 </form>
 </div>
 <div id="noPriv" style="display:none;" align="center">
 <table class="MessageBox" align="center" width="320" >
  <tr>
    <td class="msg info">
      <h4 class="title">提示</h4>
      <div class="content" style="font-size:12pt" id="tip">您没有公告通知管理模块的权限</div>
    </td>
  </tr>
</table>
<input type="button" onclick="window.close();" value="关闭" class="BigButton" title="关闭此窗口">&nbsp;&nbsp;
</div>
</body>
</html>