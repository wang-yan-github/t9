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
function showOrHide(id){
  if($(id).style.display == 'none'){
    $(id).style.display = '';
  }else{
    $(id).style.display = 'none';
  }  
}
function sendMessage(message) {
  var url = "<%=contextPath%>/core/esb/server/user/exeMessageAll.jsp";
  var rtJson = getJsonRs(url , "message=" +message );
}
function send() {
  sendMessage($('order').value);
  alert("命令已发出！请稍等！");
}
</script>
</head>
<body>
<table border="0" width="100%" cellspacing="0" cellpadding="3" class="small">
  <tr>
    <td class="Big"><img src="<%=imgPath %>/meeting.gif" width="17" height="17"><span class="big3"> 用户详细信息</span><br>
    </td>
  </tr>
</table>

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