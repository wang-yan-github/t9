<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header.jsp" %>
<%@ page import="t9.core.funcs.person.data.T9Person,t9.core.global.T9SysProps" %>
<%
  String seqId = request.getParameter("seqId");
  if (seqId == null) {
      seqId = "";
  }
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="<%=cssPath%>/style.css">
<script type="text/Javascript" src="<%=contextPath%>/core/js/datastructs.js" ></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/sys.js" ></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/prototype.js" ></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/smartclient.js" ></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/orgselect.js" ></script>
<script type="text/javascript">
var seqId = "<%=seqId%>";
//清空
function ClearUser(TO_ID, TO_NAME) {
  if(!TO_ID){
    TO_ID = "TO_ID";
    TO_NAME = "TO_NAME";
  }
  document.getElementsByName(TO_ID)[0].value = "";
  document.getElementsByName(TO_NAME)[0].value = "";
}
//发送表单
function sendForm(){
  if(document.portForm.subject.value.trim() == "请输入模版名称...") {
      document.portForm.subject.value="";
  }
  if(document.portForm.subject.value.trim()=="") { 
    alert("模版名称不能为空！");
    document.portForm.subject.value = "";
    document.portForm.subject.focus();
    return false;
  }
  if(document.portForm.nickname.value.trim()=="") { 
	    alert("模版昵称不能为空！");
	    document.portForm.nickname.value = "";
	    document.portForm.nickname.focus();
	    return false;
	  }
  if(document.portForm.dept.value==""&&document.portForm.role.value==""&&document.portForm.user.value=="") { 
    alert("请指定发布范围！");
    return false;
  }
  savePory();
}
//保存模块
function savePory(){
    var url = contextPath + "/t9/core/funcs/portal/act/T9PortAct/addPort.act";
    $("portForm").action = url;
    $("portForm").submit();
}
//初期化
function doInit(){
  if(seqId != null && seqId !=""){
    $("protTitle").update("修改桌面模版");
    
    var url =contextPath+'/t9/core/funcs/portal/act/T9PortAct/getPort.act?seqId='+seqId;
    var json = getJsonRs(url);
    //[{roleDesc=OA 管理员, userDesc=cs2,cs,cs1, fileName=1111, role=1, deptDesc=分支二部,研发部, dept=9,3, user=1129,1126,1128}]
    if (json.rtState == "0") {
        var rtData = json.rtData;
        console.log(rtData)
        var fileName = rtData[0].fileName;
        var nickname = rtData[0].nickname;
        var userId = rtData[0].user;
        var toId = rtData[0].dept;
        var privId = rtData[0].role;
        if(fileName){
            $("subject").value = fileName;
        }
        if(nickname){
            $("nickname").value = nickname;
        }
        if(toId){
            $("dept").value = toId;
        }
        if(userId){
          if(privId){  //显示用户，显示角色

            $("user").value = userId;
            $("role").value = privId;
          }else{      //显示用户，不显示角色
            $("user").value = userId;
            $("role").value = "";
          }
        }else{//    不显示用户，显示角色
          if(privId){
            $("user").value = "";
            $("role").value = privId;
          }else{  //不显示用户，不显示角色
            $("user").value = "";$("role").value = "";
          }
        }
        if(toId != "" || userId != "" || privId != ""){
            if(toId && toId.trim() && toId!=0 && toId!='ALL_DEPT'){
              bindDesc([{cntrlId:"dept", dsDef:"DEPARTMENT,SEQ_ID,DEPT_NAME"}]);
            }else{
                if((toId == 0 || toId == 'ALL_DEPT') && toId != "") {
                  $('dept').value = 0;
                  $('deptDesc').value = "全体部门";
                }
            }
            bindDesc([{cntrlId:"user", dsDef:"PERSON,SEQ_ID,USER_NAME"},
              {cntrlId:"role", dsDef:"USER_PRIV,SEQ_ID,PRIV_NAME"}]);
        }else {
          document.getElementById("dept").value = "";
          document.getElementById("user").value = "";
          document.getElementById("role").value = "";
        }
    }
  }
}
//返回
function goBack() {
  window.location.href = "/t9/core/funcs/portal/portlet/list.jsp";
}
</script>
</head>
<body onload="doInit();">
<table border="0" width="90%" cellspacing="0" cellpadding="3" class="small">
  <tr>
    <td><img src="<%=contextPath %>/core/styles/imgs/green_plus.gif">
        <span class="big1" id="protTitle">新建桌面模版</span>&nbsp;&nbsp;
    </td>
  </tr>
</table>
<form enctype="multipart/form-data" action="<%=contextPath %>/t9/core/funcs/portal/act/T9PortAct/addPort.act"  method="post" name="portForm" id="portForm">
<input type="hidden" id="seqId" name="seqId" value="<%=seqId%>">
<table class="TableBlock" width="95%" align="center">
  <tr>
      <td nowrap class="TableData" width='15%'>模块名称：</td>
      <td class="TableData" >
        <input type="text" name="nickname" id="nickname" size="55" maxlength="200" class="BigInput" value="请输入模块名称..." style="color: #8896A0"
         onMouseOver="if($F('nickname')=='请输入模块名称...') document.getElementById('nickname').style.color='#000000';" 
         onMouseOut="if($F('nickname')=='请输入模块名称...') document.getElementById('nickname').style.color='#8896A0';" 
         onFocus="if($F('nickname')=='请输入模块名称...') {document.getElementById('nickname').value='';document.getElementById('nickname').style.color='#000000';}">
      </td>
    </tr>
    <tr>
      <td nowrap class="TableData" width='15%'>部门名称：</td>
      <td class="TableData" >
        <input type="text" name="subject" id="subject" size="55" maxlength="200" class="BigInput" value="请输入部门名称..." style="color: #8896A0"
         onMouseOver="if($F('subject')=='请输入部门名称...') document.getElementById('subject').style.color='#000000';" 
         onMouseOut="if($F('subject')=='请输入部门名称...') document.getElementById('subject').style.color='#8896A0';" 
         onFocus="if($F('subject')=='请输入部门名称...') {document.getElementById('subject').value='';document.getElementById('subject').style.color='#000000';}">
      </td>
    </tr>
    <tr>
      <td nowrap class="TableData">部门权限：</td>
      <td class="TableData">
        <input type="hidden" id="dept" name="toId" value="">
        <textarea cols=40 id="deptDesc" name="toName" rows=2 class="BigStatic" wrap="yes" readonly></textarea>
        <a href="javascript:;" class="orgAdd" onClick="javascript:selectDept(['dept','deptDesc'] , 5);">添加</a>
       <a href="javascript:;" class="orgClear" onClick="ClearUser('toId','toName')">清空</a>
       &nbsp;&nbsp;&nbsp;&nbsp;
      </td>
    </tr>
   <tr nowrap class="TableData" id="rang_user">
      <td nowrap class="TableData">人员权限：</td>
      <td class="TableData">
        <input type="hidden" id="user" name="userId" value="">
        <textarea cols=40 id="userDesc" name="userName" rows=2 class="BigStatic" wrap="yes" readonly></textarea>
        <a href="javascript:;" class="orgAdd" onClick="javascript:selectUser(['user', 'userDesc'] , 5);">添加</a>
        <a href="javascript:;" class="orgClear" onClick="ClearUser('userId','userName')">清空</a>
      </td>
   </tr>
   <tr nowrap class="TableData" id="rang_role">
      <td nowrap class="TableData">角色权限：</td>
      <td class="TableData">
        <input type="hidden" id="role" name="privId" value="">
        <textarea cols=40 id="roleDesc" name="privName" rows=2 class="BigStatic" wrap="yes" readonly></textarea>
        <a href="javascript:;" class="orgAdd" onClick="javascript:selectRole(['role', 'roleDesc'] , 5);">添加</a>
        <a href="javascript:;" class="orgClear" onClick="ClearUser('privId','privName')">清空</a><br>
        发布范围取部门、人员和角色的并集
      </td>
   </tr>
    <tr align="center" class="TableControl">
      <td colspan="2" nowrap>
         <input type="button" value="保存" class="BigButton" onClick="sendForm();">&nbsp;&nbsp;
         <input type="button" value="返回" class="BigButton" onClick="goBack();">&nbsp;&nbsp;
     </td>
  </tr>
 </table>
</form>
</body>
</html>