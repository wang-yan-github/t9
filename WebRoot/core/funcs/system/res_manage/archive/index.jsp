<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="java.util.Date"%>
    <%@ page import="t9.core.funcs.person.data.T9Person" %>
<%@ include file="/core/inc/header.jsp" %>  
<%
  T9Person loginUser = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);   
  String userId = loginUser.getUserId();  
    %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>数据归档</title>
<link rel="stylesheet" href ="<%=cssPath %>/style.css">
<link rel="stylesheet" href ="<%=cssPath %>/cmp/Calendar.css">
<script type="text/javascript" src="<%=contextPath %>/core/js/prototype.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/datastructs.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/sys.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/Calendarfy.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/smartclient.js"></script>
<script type="text/javascript">
function doInit() {
  var date1Parameters = {
      inputId:'BEGIN_DATE',
      property:{isHaveTime:true}
  };
  new Calendar(date1Parameters);
  var url = contextPath + "/t9/core/funcs/system/resManage/act/ArchiveAct/getArchive.act";
  var json = getJsonRs(url , $('form1').serialize());
  if (json.rtState == "0") {
    for (var i = 0 ;i < json.rtData.length ; i++) {
      var module = json.rtData[i].module;
      add(module , json.rtData[i].data);
    }
  }
}

function CheckForm()
{
   if(!window.confirm('确定要归档数据吗？'))
      return false;
   var inputs = form1.getElementsByTagName('input');
   for(var i=0; i<inputs.length; i++){
      if(inputs[i].type == 'checkbox' && inputs[i].checked)
         return true;
   }
   alert('请选择要归档的数据');
   return false;
}
function CheckForm2()
{
   if(!window.confirm('确定要删除归档数据吗？'))
      return false;
   var inputs = form2.getElementsByTagName('input');
   for(var i=0; i<inputs.length; i++){
      if(inputs[i].type == 'checkbox' && inputs[i].checked)
         return true;
   }
   alert('请选择要删除的归档数据');
   return false;
}
var requestUrl = contextPath + "/t9/core/funcs/system/resManage/act/ArchiveAct/archive.act";
function archive() {
  if ("<%=userId %>" == 'admin') {
    if (!CheckForm()) {
      return ;
    }
    var json = getJsonRs(requestUrl , $('form1').serialize());
    if (json.rtState == "0") {
      alert(json.rtData);
      location.reload();
    }
  } else {
    alert("不是管理员，不能执行此操作！");
  }
}
var requestUrl2 = contextPath + "/t9/core/funcs/system/resManage/act/ArchiveAct/dropArchive.act";
function drop() {
  if ("<%=userId %>" == 'admin') {
    if (!CheckForm2()) {
      return ;
    }
    var json = getJsonRs(requestUrl2 , $('form2').serialize());
    if (json.rtState == "0") {
      alert("删除成功");
      location.reload();
    }
  } else {
    alert("不是管理员，不能执行此操作！");
  }
}



function add(module , data) {
  for (var i = 0 ;i < data.length ; i++) {
    var div = new Element("div");
    var input = new Element("input");
    input.type = 'checkbox';
    input.name = 'delete';
    input.id = module + '_' + data[i];
    input.value = module + '_' + data[i];
    div.appendChild(input);
    var label = new Element("label");
    label.htmlFor = module + '_' + data[i];
    label.innerHTML = data[i];
    div.appendChild(label);
    $(module + '_Td').appendChild(div);
  }
}
</script>
</head>
<body class="bodycolor" topmargin="5" onload="doInit()">
<table border="0" width="100%" cellspacing="0" cellpadding="3" class="small">
  <tr>
    <td class="Big"><img src="<%=imgPath %>/system.gif" WIDTH="22" HEIGHT="20" align="absmiddle"><span class="big3">数据归档</span>
    </td>
  </tr>
</table>
<form id="form1" name="form1">
<table width="500" class="TableBlock" align="center">
   <tr class="TableHeader">
      <td colspan=2>请选择要归档的数据</td>
   </tr>
   <tr class="TableData" align="center">
      <td>请选择时间：</td>
      <td class="TableData"  align="left">
      <input type="text" name="BEGIN_DATE" id="BEGIN_DATE" size="20" maxlength="20" class="BigInput" value="">
      (将该日期之前的数据归档,为空为当前日期)</td>
   </tr>
   <tr class="TableData" align="center">
   	<td >请选择数据：</td>
      <td colspan=2 align="left" >
         <input type="checkbox" name="TABLE_NAME" value="EMAIL_BODY" id="EMAIL_BODY"><label for="EMAIL_BODY">邮件</label>&nbsp;&nbsp;
         <input type="checkbox" name="TABLE_NAME" value="MESSAGE" id="MESSAGE"><label for="MESSAGE">微讯</label>&nbsp;&nbsp;
         <input type="checkbox" name="TABLE_NAME" value="DIARY_COMMENT_REPLY" id="DIARY_COMMENT_REPLY"><label for="DIARY_COMMENT_REPLY">工作日志</label>&nbsp;&nbsp;
         <input type="checkbox" name="TABLE_NAME" value="SYS_LOG" id="SYS_LOG"><label for="SYS_LOG">系统日志</label>&nbsp;&nbsp;
      </td>
   </tr>
   <tr class="TableFooter" align="center">
      <td colspan=2><input type="button" value="归档" class="BigButton" onclick="archive()"></td>
   </tr>
</table>
</form>

<div  align=center >
   <table class="MessageBox" width="300">
    <tbody>
        <tr>
            <td id="msgInfo" class="msg info">
            <br>
归档时先复制备份数据，并以选择的日期命名，然后清空当前数据表，以提高系统效率。
<br>
<br>归档的数据仍然可以查询。
            </td>
        </tr>
    </tbody>
</table>
</div>

<table border="0" width="100%" cellspacing="0" cellpadding="3" class="small">
  <tr>
    <td class="Big"><img src="<%=imgPath %>/system.gif" WIDTH="22" HEIGHT="20" align="absmiddle"><span class="big3">删除归档数据</span>
    </td>
  </tr>
</table>
<form name="form2" id="form2" method="post">
<table width="500" class="TableBlock" align="center">
   <tr class="TableHeader">
      <td colspan="2">请选择要删除的归档数据</td>
   </tr>

   <tr class="TableData">
      <td width="110" align="center">邮件归档：</td>
      <td width="390" id="EMAIL_Td">


      </td>
   </tr>
  
    <tr class="TableData">
      <td width="110" align="center">微讯归档：</td>
      <td width="390"  id="MESSAGE_Td">


      </td>
   </tr>
   
     <tr class="TableData">
      <td width="110" align="center">工作日志归档：</td>
      <td width="390"  id="DIARY_Td">
      </td>
   </tr>
   
     <tr class="TableData">
      <td width="110" align="center">系统日志归档：</td>
      <td width="390"  id="SYS_LOG_Td">

      </td>
   </tr>
  
  
   <tr class="TableFooter" align="center">
      <td colspan="2"><input type="button" value="删除" onclick="drop()" class="BigButton"></td>
   </tr>
</table>
</form>
<br>
<div  align=center >
   <table class="MessageBox" width="300">
    <tbody>
        <tr>
            <td id="msgInfo" class="msg info">
            <br>
删除归档数据可减少磁盘空间占用，删除的数据将不能被查询。
<br>
<br>删除后数据将不可恢复。
            </td>
        </tr>
    </tbody>
</table>
</div>
</body>
</html>