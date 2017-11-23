<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>周活动安排设置</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href = "<%=cssPath%>/style.css">
<script type="text/javascript" src="<%=contextPath %>/core/js/prototype.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/datastructs.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/sys.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/smartclient.js"></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/orgselect.js" ></script>
<script type="text/javascript">
function doInit(){
   var url = "<%=contextPath%>/t9/core/funcs/system/active/act/T9ActiveSetAct/getActiveSet.act";
   var rtJson = getJsonRs(url);
   if (rtJson.rtState == "0") {
     var seqId = rtJson.rtData.seqId;
     var userId = rtJson.rtData.userId;
     var userName = rtJson.rtData.userName;
     
     $('seqId').value = seqId;
     $('userId').value = userId;
     $('userName').value = userName;
   }
}

function commit(){
  var url = "<%=contextPath%>/t9/core/funcs/system/active/act/T9ActiveSetAct/setActive.act";
  var rtJson = getJsonRs(url, mergeQueryString($("form1")));
  if (rtJson.rtState == "0") {
    alert("设置成功！");
  }
}
function ClearUser(){ 
  var args = $A(arguments); 
  for(var i = 0; i < args.length; i++ ){ 
    var cntrl = $(args[i]); 
    if(cntrl){ 
      if (cntrl.tagName.toLowerCase() == "td" 
        || cntrl.tagName.toLowerCase() == "div" 
        || cntrl.tagName.toLowerCase() == "span") { 
        cntrl.innerHTML = ''; 
      } else{ 
        cntrl.value =''; 
      } 
    } 
  } 
}
</script>
</head>
<body onload="doInit()" topmargin="5">
<table border="0" width="100%" cellspacing="0" cellpadding="3" class="small">
  <tr>
    <td class="Big"><img src="<%=imgPath%>/attendance.gif" WIDTH="22" HEIGHT="20" align="absmiddle"><span class="big3"> 周活动安排设置</span><br>
    </td>
  </tr>
</table>
<form name="form1" id="form1"  enctype="multipart/form-data" method="post">
<table class="TableBlock" width="60%" align="center">
<tr>
   <td nowrap class="TableContent">指定周活动安排人员</td>
   <td class="TableData">
   <input type="hidden" name="seqId" id="seqId" value="">
   <input type="hidden" name="userId" id="userId" value="">
   <textarea cols=40 name="userName" id="userName" rows="6" class="BigStatic" wrap="yes" readonly></textarea><a href="javascript:;" class="orgAdd" onClick="selectUser(['userId', 'userName'])">添加</a>
   <a href="javascript:;" class="orgClear" onClick="ClearUser('userId', 'userName')">清空</a></td>
</tr>
<tr>
   <td nowrap  class="TableControl" colspan="2" align="center">
      <input type="button" value="确定" class="BigButton" onclick="commit()">&nbsp;&nbsp;
   </td>
  </tr>
</table>
</form>

</body>
</html>