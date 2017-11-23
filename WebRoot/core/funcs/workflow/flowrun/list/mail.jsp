<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header.jsp" %>
<%
String runId = request.getParameter("runId");
String flowId = request.getParameter("flowId");
String prcsId = request.getParameter("prcsId");
String flowPrcs = request.getParameter("flowPrcs");

Cookie[] myCookie = request.getCookies();
String flowView = "";
for(int i = 0; i < myCookie.length ; i++){
  Cookie tmp= myCookie[i];
  if(tmp != null && tmp.getName().equals("FLOW_SEND_MAIL")){
    flowView = tmp.getValue();
    break;
  }
}
if(flowView == null || "".equals(flowView)){
  flowView = "1234";
}
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>发送邮件</title>
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
  var url =contextPath +  "/t9/core/funcs/workflow/act/T9FlowEmailAct/getEmailMsg.act?runId=" + runId;
  var json = getJsonRs(url);
  if (json.rtState == '0') {
    var userPriv = json.rtData.userPriv;
    if (!userPriv) {
      $('hasPriv').hide();
      $('noPriv').show();
    }
  }
}
function CheckForm()
{
  if(document.form1.TO_ID.value=="" && document.form1.COPY_TO_ID.value=="" && document.form1.SECRET_TO_ID.value=="")
  {
    alert("请选择收件人！");
    return false;
  }
  
  var flow_view_str="";
  if($("CHECK1").checked)
     flow_view_str+="1";
  if($("CHECK2").checked)
     flow_view_str+="2";
  if($("CHECK3").checked)
     flow_view_str+="3";
  if($("CHECK4").checked)
     flow_view_str+="4";
  if($("CHECK5").checked)
    flow_view_str+="5";
  if(flow_view_str=="")
  {
  	alert("发送内容不能为空！");
    return false;
  }
  document.form1.FLOW_VIEW.value=flow_view_str;
  return(true);
}
function flow_view()
{
  var flow_view_str="";
  if($("CHECK1").checked)
     flow_view_str+="1";
  if($("CHECK2").checked)
     flow_view_str+="2";
  if($("CHECK3").checked)
     flow_view_str+="3";
  if($("CHECK4").checked)
     flow_view_str+="4";
  if($("CHECK5").checked)
    flow_view_str+="5";
  var exp = new Date();
  exp.setTime(exp.getTime() + 24*60*60*1000);
  document.cookie = "FLOW_SEND_MAIL="+ flow_view_str + ";expires=" + exp.toGMTString()+";path=/";
}
function sendForm() {
  if(CheckForm())
  {
    var requestUrl = contextPath +   "/t9/core/funcs/workflow/act/T9FlowEmailAct/saveEmail.act";
    var rtJson = getJsonRs(requestUrl , $('form1').serialize()); 
    if(rtJson.rtState == "0"){ 
      $('hasPriv').hide();
      $('noPriv').show();
      $("tip").update(rtJson.rtMsrg);
    } 
  }
}
</script>
</head>
<body onload="doInit()">
<div id="hasPriv">
<table border="0" width="100%" cellspacing="0" cellpadding="3" class="small">
  <tr>
    <td class="Big"><img src="<%=imgPath %>/email.gif" align="absmiddle"><span class="big3"> 邮件发送工作流信息</span>
    </td>
  </tr>
</table>


<form  id="form1"  method="post" name="form1" >
<table border="0" width="100%" align="center" class="TableList">
    <tr>
      <td nowrap class="TableContent">收件人：</td>
      <td class="TableData">
        <input type="hidden" name="TO_ID"  id="TO_ID">
        <textarea cols="50" name="TO_NAME" id="TO_NAME" rows="2" style="overflow-y:auto;" class="SmallStatic" wrap="yes" readonly></textarea>
        <a href="javascript:void(0)" class="orgAdd" onClick="selectUser(['TO_ID', 'TO_NAME'],11);return false;" >添加</a>
        <a href="javascript:;" class="orgClear" onClick="ClearUser('TO_ID', 'TO_NAME');return false;">清空</a>
      </td>
    </tr>
    <tr>
      <td nowrap class="TableContent">抄送：</td>
      <td class="TableData">
        <input type="hidden" name="COPY_TO_ID"  id="COPY_TO_ID">
        <textarea cols="50" name="COPY_TO_NAME" id="COPY_TO_NAME" rows="1" style="overflow-y:auto;" class="SmallStatic" wrap="yes" readonly></textarea>
        <a href="javascript:;" class="orgAdd"  onClick="selectUser(['COPY_TO_ID', 'COPY_TO_NAME'],11);return false;" >添加</a>
        <a href="javascript:;" class="orgClear" onClick="ClearUser('COPY_TO_ID', 'COPY_TO_NAME')">清空</a>
      </td>
    </tr>
    <tr>
      <td nowrap class="TableContent">密送：</td>
      <td class="TableData">
        <input type="hidden" name="SECRET_TO_ID"  id="SECRET_TO_ID">
        <textarea cols="50" name="SECRET_TO_NAME" id="SECRET_TO_NAME" rows="1" style="overflow-y:auto;" class="SmallStatic" wrap="yes" readonly></textarea>
        <a href="javascript:;" class="orgAdd"  onClick="selectUser(['SECRET_TO_ID', 'SECRET_TO_NAME'],11);return false;">添加</a>
        <a href="javascript:;" class="orgClear"   onClick="ClearUser('SECRET_TO_ID', 'SECRET_TO_NAME')">清空</a>
      </td>
    </tr>
    <tr>
      <td nowrap class="TableContent">内容选择：</td>
      <td class="TableData">
         <input type="checkbox" name="CHECK1" id="CHECK1" onclick="flow_view();" <%=(flowView.indexOf("1") == -1 ? "" : "checked") %>> <label for="CHECK1" style="cursor:pointer">表单</label>
   <input type="checkbox" name="CHECK2" id="CHECK2" onclick="flow_view();" <%=(flowView.indexOf("2") == -1 ? "" : "checked") %>> <label for="CHECK2" style="cursor:pointer">公共附件</label>
   <input type="checkbox" name="CHECK3" id="CHECK3" onclick="flow_view();" <%=(flowView.indexOf("3") == -1 ? "" : "checked") %>> <label for="CHECK3" style="cursor:pointer">会签与点评</label>
   <input type="checkbox" name="CHECK4" id="CHECK4" onclick="flow_view();" <%=(flowView.indexOf("4") == -1 ? "" : "checked") %>> <label for="CHECK4" style="cursor:pointer">流程图</label></b>
      <input type="checkbox" name="CHECK5" id="CHECK5" onclick="flow_view()" <%=(flowView.indexOf("5") == -1 ? "" : "checked") %>> <label for="CHECK5" style="cursor:hand">会签区附件</label>
      </td>
    </tr>  
    <tr align="center" class="TableControl">
      <td colspan="2" nowrap>
      <input type="hidden" name="RUN_ID" value="<%=runId %>">
      <input type="hidden" name="FLOW_ID" value="<%=flowId %>">
      <input type="hidden" name="FLOW_VIEW">
      <input type="button" onclick="sendForm()" value="发送" class="BigButton" title="立即发送此邮件">&nbsp;&nbsp;
      <input type="button" onclick="window.close();" value="关闭" class="BigButton" title="关闭此窗口">&nbsp;&nbsp;
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
      <div class="content" style="font-size:12pt" id="tip">您没有邮件模块的权限</div>
    </td>
  </tr>
</table>
<input type="button" onclick="window.close();" value="关闭" class="BigButton" title="关闭此窗口">&nbsp;&nbsp;
</div>
</body>
</html>