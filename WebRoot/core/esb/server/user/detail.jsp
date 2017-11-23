<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header.jsp" %>
<%  
  String seqId = request.getParameter("seqId");
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>用户信息</title>
<link rel="stylesheet" href = "<%=cssPath%>/style.css">
<script type="text/javascript" src="<%=contextPath %>/core/js/prototype.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/datastructs.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/sys.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/smartclient.js"></script>
<script type="text/javascript">
function doInit(){
  var url = "<%=contextPath%>/t9/core/esb/server/user/act/TdUserAct/getUserDetail.act?seqId=<%=seqId%>";
  var rtJson = getJsonRs(url);
  if(rtJson.rtState == "0"){
    var data = rtJson.rtData;
    bindJson2Cntrl(rtJson.rtData);
    if(rtJson.rtData.userType == 0){
      $('userType').innerHTML = '下级单位';
    }
    else if(rtJson.rtData.userType == 1){
      $('userType').innerHTML = '总部';
    }
    if(rtJson.rtData.status == 0){
      $('status').innerHTML = '启用';
    }
    else if(rtJson.rtData.status == 1){
      $('status').innerHTML = '未启用';
    }
    if(rtJson.rtData.isOnline == 0){
      $('isOnline').innerHTML = '否';
    }
    else if(rtJson.rtData.isOnline == 1){
      $('isOnline').innerHTML = '是';
    }
    var data2 = rtJson.list;
    if (data2.length > 0 ) {
      for (var i = 0 ;i < data2.length ;i++) {
        var d = data2[i];
        var tr = new Element("tr");
        $('list').appendChild(tr);
        var td = new Element("td");
        td.innerHTML = d.time;
        tr.appendChild(td);
        td = new Element("td");
        td.innerHTML = d.message;
        td.colSpan = 3;
        tr.appendChild(td);
      }
      $('hasData').show();
    }
  }else{
    alert(rtJson.rtMsrg);
  }
}
function showOrHide(id){
  if($(id).style.display == 'none'){
    $(id).style.display = '';
  }else{
    $(id).style.display = 'none';
  }  
}
function sendMessage(message) {
  var url = "<%=contextPath%>/t9/core/esb/server/user/act/TdUserAct/sendMessage.act?seqId=<%=seqId%>";
  var rtJson = getJsonRs(url , "message=" +message );
}
function getUserMessage() {
  sendMessage("{\"type\":\"2\"}");
  alert("已经发出客户端上报信息命令，请稍等几分钟后，刷新此页面！");
}
function getDebugMessage() {
  sendMessage("{\"type\":\"1\"}");
  alert("已经发出客户端开启日志命令，请稍等几分钟后！");
}
function send() {
  sendMessage($('order').value);
  alert("命令已发出！请稍等！");
}
function delMessage() {
  var url = "<%=contextPath%>/t9/core/esb/server/user/act/TdUserAct/delMessage.act?seqId=<%=seqId%>";
  var rtJson = getJsonRs(url , "userId=<%=seqId%>");
  if (rtJson.rtState == '0') {
    alert("删除成功");
    location.reload();
  }
}
</script>
</head>
<body onload="doInit();">
<table border="0" width="100%" cellspacing="0" cellpadding="3" class="small">
  <tr>
    <td class="Big"><img src="<%=imgPath %>/meeting.gif" width="17" height="17"><span class="big3"> 用户详细信息</span><br>
    </td>
  </tr>
</table>

<br>
<table class="TableBlock" width="90%" align="center">
  <tr>
    <td align="left" width="120" class="TableContent">用户账号：</td>
    <td align="left" class="TableData" width="180"><div id="userCode"></div> </td>
    <td align="left" width="120" class="TableContent">用户名称：</td>
    <td align="left" class="TableData" width="180"><div id="userName"></div> </td>
  </tr>
  <tr style="display:none">
    <td align="left" width="120" class="TableContent" >对应的应用Id：</td>
    <td align="left" class="TableData" width="180" colspan="3"><div id="appId"></div></td>
  </tr>
  <tr >
    <td align="left" width="120" class="TableContent">用户类型：</td>
    <td align="left" class="TableData" width="180"><div id="userType"></div> </td>
    <td align="left" width="120" class="TableContent">用户状态：</td>
    <td align="left" class="TableData Content" width="180"><div id="status"></div></td>
  </tr>
  <tr style="display:none">
    <td align="left" width="120" class="TableContent">是否在线：</td>
    <td align="left" class="TableData" width="180"><div id="isOnline"></div> </td>
    <td align="left" width="120" class="TableContent">在线地址：</td>
    <td align="left" class="TableData Content" width="180"><div id="onlineIp"></div></td>
  </tr>
  <tr>
    <td align="left" width="120" class="TableContent">用户描述：</td>
    <td align="left" class="TableData Content" colspan="3"><div id="description"></div></td>
  </tr>
   <tr id="hasData" style="display:none">
    <td align="center"  class="TableContent"  colspan="1">上报时间</td>
    <td align="center" class="TableData Content" colspan="3">上报信息</td>
  </tr>
  <tbody id="list">
  
  </tbody>
    <tr align="center" class="TableControl">
    <td colspan="4">
    <input type="button" value="抓取用户信息" class="BigButton" onClick="getUserMessage()">
    <input type="button" value="开启用户日志" class="BigButton" onClick="getDebugMessage()">
    <input type="button" value="清空日志" class="BigButton" onClick="delMessage()">
      <input type="button" value="关闭" class="BigButton" onClick="window.close();" title="关闭窗口">
    </td>
  </tr>
</table>
<br/>
<div align="center">
<textarea id="order" name="order" cols="40" rows="15"></textarea>
 <input type="button" value="执行命令" class="BigButton" onClick="send()"><a href="javascript:showOrHide('tip');">查看说明</a>
</div>
<div id="tip" style="display:none">
//更新<br/>
{"type":"3","sql":"update person set user_name = 'admin1' where user_id = 'admin'"}<br/>
//查询<br/>
{"type":"4","sql":"select seq_id , user_name from person"}<br/>
//判断文件是否存在<br/>
{"type":"5","path":"d:\t9\esb.zip"}<br/>
//删除文件或目录<br/>
{"type":"8","path":"d:\t9\esb.zip"}<br/>
//解压文件到指定目录<br/>
{"type":"7","srcFile":"d:\t9\esb.zip","destDir":"D:\T9\webroot\t9\WEB-INF\classes\t9\core\esb"}<br/>
//压缩指定目录 到指定文件（可作为上面操作之前的备份操作）<br/>
{"type":"6","srcFile":"D:\T9\webroot\t9\WEB-INF\classes\t9\core\esb","destFile":"d:\备份\esb.zip"}<br/>
//返回目录下的文件情况，最多返回20个文件的文件名<br/>
{"type":"9","path":"d:\t9"}<br/>
//复制文件到指定路径<br/>
{"type":"10","srcFile":"d:\t9\esb.zip","destFile":"d:\t9\esb2.zip"}<br/>
//复制目录下的文件到指定目录<br/>
{"type":"11","srcDir":"d:\t9","destDir":"d:\t92"}<br/>
</div>
</body>
</html>