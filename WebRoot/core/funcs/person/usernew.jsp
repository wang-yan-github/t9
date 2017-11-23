<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header.jsp" %>
<%@ page import="t9.core.funcs.person.data.T9Person" %>
<%@ page import="t9.core.global.T9RegistProps" %>
<%@ page import="t9.core.util.auth.T9RegistUtility" %>
<%@ page import="t9.core.data.T9AuthKeys" %>
<%
  String deptId = request.getParameter("deptId");
  if (deptId == null) {
    deptId = "";
  }
  T9Person loginUser = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
  String maxSum = String.valueOf(T9RegistUtility.getUserCnt());
  if (maxSum == null) {
    maxSum = "";
  }
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>离职人员</title>
<base target="_self">
<link rel="stylesheet" href="<%=cssPath %>/views.css" type="text/css" />
<link rel="stylesheet" href="<%=cssPath%>/page.css">
<link rel="stylesheet" href ="<%=cssPath%>/style.css">
<script type="text/Javascript" src="<%=contextPath%>/core/js/datastructs.js"></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/sys.js"></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/prototype.js"></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/smartclient.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/cmp/page.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/cmp/Menu.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/cmp/grid.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/funcs/person/js/personUtil.js"></script>
<script type="text/javascript">
loginUserId = <%=loginUser.getSeqId()%>;
var isAdmin = <%=loginUser.isAdmin()%>;
var deptId = "<%=deptId%>";
var maxSum = "<%=maxSum%>";
var total = 0;
function doInit() {
  var url =  contextPath + "/t9/core/funcs/person/act/T9PersonAct/getManagePersonList.act?deptId=" + deptId;
  var cfgs = {
    dataAction: url,
    container: "listContainer",
    colums: [
       {type:"selfdef", text:"选择", width: '40', selfStyle: "padding-left: 8px", render:checkBoxRender},
       {type:"hidden", name:"seqId", text:"顺序号", dataType:"int", render:subjectRender},
       {type:"data", name:"userId",  width: '10%', text:"用户名", render:subjectRender},       
       {type:"data", name:"userName",  width: '15%', text:"真实姓名", render:subjectRender},
       {type:"data", name:"deptId",  width: '15%', text:"部门", render:getDeptName},
       {type:"data", name:"dutyType",  width: '10%', text:"排班", render:getDutyType}, 
       {type:"data", name:"userPriv",  width: '15%', text:"角色", render:getRoleName},
       {type:"hidden", name:"notLogin", text:"是否登录", dataType:"int"},
       {type:"hidden", name:"password", text:"密码", dataType:"int"},
       {type:"data", name:"postPriv",  width: '10%', text:"管理范围", styleFunc:getStyle, render:getPostPriv}, 
       {type:"selfdef", text:"操作", width: '20%',render:opts}]
  };
  window.pageMgr = new T9JsPage(cfgs);
  pageMgr.show();
  total = pageMgr.pageInfo.totalRecord;
  $('numUser').innerHTML = total;
  if(total){
    showCntrl('listContainer');
    var mrs = " 共 "+ total + " 条记录 ！";
    //WarningMsrg(mrs, 'msrg');
    showCntrl('delOpt');
  }else{
    WarningMsrg('暂无可管理的用户！', 'msrg');
  }
  var urls = "<%=contextPath%>/t9/core/funcs/person/act/T9PersonAct/getDeptName.act";
  var rtJsons = getJsonRs(urls , "deptId=" +  deptId);
  if(deptId == "0"){
    document.getElementById("newUser").innerHTML = "离职人员/外部人员";
    document.getElementById("manageUser").innerHTML = "离职人员/外部人员";
  }else{
    if(rtJsons.rtState == '0'){
      document.getElementById("newUser").innerHTML = rtJsons.rtData;
      document.getElementById("manageUser").innerHTML = rtJsons.rtData;
    }
  }
}  

function reloadFunc(){
  parent.navigateFrame.location.reload();
  if(total == 0){
    location = "<%=contextPath%>/core/funcs/person/usernew.jsp?deptId="+deptId;
  }else{
    pageMgr.refreshAll();
  }
}

function getStyle(cellData, recordIndex, columIndex) {
  //var record = this.getRecord(recordIndex)["userName"];
  var password = this.getCellData(recordIndex,"isEmptyPass");
  if (password == "1") {
    return {color: "#FF0000"};
  }else {
    return {color: "#00FF00"};
  }
}

function opts(cellData, recordIndex, columIndex){
  var seqId = this.getCellData(recordIndex,"seqId");
  var userName = this.getCellData(recordIndex,"userName");
  var urls = "<%=contextPath%>/t9/core/funcs/person/act/T9PersonAct/getUserKey.act";
  var rtJsons = getJsonRs(urls , "seqId=" + seqId);
  if(rtJsons.rtData == 1){
    return "<center><a href=\"javascript:doEdit("+seqId+",'"+userName+"');\">编辑</a>&nbsp;&nbsp;<a href=\"javascript:CREAT_KEY("+seqId+");\">初始化USB KEY </a></center>";
  }else{
    return "<center><a href=\"javascript:doEdit("+seqId+",'"+userName+"');\">编辑</a></center>";
  }
}

function confirmUsbKey() {
  if(confirm("确认已插入用户KEY?")) {
    return true;
  }else {
    return false;
  }
}

function CREAT_KEY(seqId){
  if(!confirmUsbKey()) {
    return ;
  } 
  location="<%=contextPath%>/core/funcs/person/createkey.jsp?deptId="+deptId+"&USER_ID="+seqId;
}
/**
 * @recordIndex         当前页面记录索引，从0开始计数

 */
function reader(recordIndex) {
  var diaId = this.getCellData(recordIndex,"seqId");
  showReader(diaId);
}
/**
 * @recordIndex         当前页面记录索引，从0开始计数

 */
function doEdit(seqId, userName) {
  location = "<%=contextPath%>/core/funcs/person/indutypersoninput.jsp?seqId="+ seqId +"&deptId="+deptId+"&userName="+userName;
}

/**
 * subject 描画
 */
function subjectRender(cellData, recordIndex, columIndex) {
  //alert(cellData);
  var deptId = this.getCellData(recordIndex,"seqId");
  var userName = this.getCellData(recordIndex,"userName");
  var notLogin = this.getCellData(recordIndex,"notLogin");
  var password = this.getCellData(recordIndex,"isEmptyPass");
  //var url = "<%=contextPath%>/t9/core/funcs/person/act/T9PersonAct/getMenuPara.act?deptId="+deptId;
  //var rtJson = getJsonRs(url);
  //if(rtJson.rtState == '0'){
    if(notLogin == "1"){
      return "<div style=color:#C0C0C0 align=\"center\" title=\""+userName+" 禁止登录OA\">" + cellData + "</div >";
    }else if(password == "1"){
      return "<div align=\"center\" title=\""+userName+" 登录密码为空\"><font color=red>" + cellData + "</font ></div >";
      }else{
      return "<div align=\"center\" title=\""+userName+"\"><font color=black>" + cellData + "</font ></div >";
    }
  //}
}
/**
 * 获得部门名称及相对应的颜色：密码为空用户显示为红色，禁止登录用户显示为灰色 
 */
function getDeptName(cellData, recordIndex, columIndex){
  var deptId = this.getCellData(recordIndex,"seqId");
  var userName = this.getCellData(recordIndex,"userName");
  var notLogin = this.getCellData(recordIndex,"notLogin");
  var password = this.getCellData(recordIndex,"isEmptyPass");
 // var url = "<%=contextPath%>/t9/core/funcs/person/act/T9PersonAct/getMenuPara.act?deptId="+deptId;
 // var rtJson = getJsonRs(url);
  //if(rtJson.rtState == '0'){
    var urls = "<%=contextPath%>/t9/core/funcs/person/act/T9PersonAct/getDeptName.act";
    var rtJsons = getJsonRs(urls , "deptId=" +  cellData);
    if(rtJsons.rtState == '0'){
      if(notLogin == "1"){
         //document.getElementById("newUser").innerHTML = rtJsons.rtData;
        // document.getElementById("manageUser").innerHTML = rtJsons.rtData;
        return "<center><div style=color:#C0C0C0 align=\"center\" title=\""+userName+" 禁止登录OA\">" +rtJsons.rtData+ "</div >";
      }else if(password == "1"){
        return "<div align=\"center\" title=\""+userName+" 登录密码为空\"><font color=red>" +rtJsons.rtData+ "</font ></div >";
      }else{
        return "<div align=\"center\" title=\""+userName+"\"><font color=black>" +rtJsons.rtData+ "</font ></div >";
      }
    }
  //}
}
/**
 * 获得角色名称及相对应的颜色：密码为空用户显示为红色，禁止登录用户显示为灰色 
 */
function getRoleName(cellData, recordIndex, columIndex){
  var deptId = this.getCellData(recordIndex,"seqId");
  var userName = this.getCellData(recordIndex,"userName");
  var notLogin = this.getCellData(recordIndex,"notLogin");
  var password = this.getCellData(recordIndex,"isEmptyPass");
  //var url = "<%=contextPath%>/t9/core/funcs/person/act/T9PersonAct/getMenuPara.act?deptId="+deptId;
  //var rtJson = getJsonRs(url);
  //if(rtJson.rtState == '0'){
    var urls = "<%=contextPath%>/t9/core/funcs/person/act/T9PersonAct/getRoleName.act";
    var rtJsons = getJsonRs(urls , "roleId=" +  cellData);
    if(rtJsons.rtState == '0'){
      if(notLogin == "1"){
        return "<center><div style=color:#C0C0C0 align=\"center\" title=\""+userName+" 禁止登录OA\">" +rtJsons.rtData+ "</div ></center>";
      }else if(password == "1"){
        return "<div align=\"center\" title=\""+userName+" 登录密码为空\"><font color=red>" +rtJsons.rtData+ "</font ></div >";
      }else{
        return "<div align=\"center\" title=\""+userName+"\"><font color=black>" +rtJsons.rtData+ "</font ></div >";
      }
//     }else if(rtJson.rtData.password == ""){
//       var urls = "<%=contextPath%>/t9/core/funcs/person/act/T9PersonAct/getRoleName.act";
//       var rtJsons = getJsonRs(urls , "roleId=" +  cellData);
//       if(rtJsons.rtState == '0'){
//         return "<font color=red>" +rtJsons.rtData+ "</font >";
//      }
//     }else{
//       var urls = "<%=contextPath%>/t9/core/funcs/person/act/T9PersonAct/getRoleName.act";
//       var rtJsons = getJsonRs(urls , "roleId=" +  cellData);
//       if(rtJsons.rtState == '0'){
//         return "<font color=black>" +rtJsons.rtData+ "</font >";
//       }
//     }
    }
  //}
}
/**
 * 获得管理范围名称及相对应的颜色：密码为空用户显示为红色，禁止登录用户显示为灰色 
 */
function getPostPriv(cellData, recordIndex, columIndex){
  var deptId = this.getCellData(recordIndex,"seqId");
  var userName = this.getCellData(recordIndex,"userName");
  var notLogin = this.getCellData(recordIndex,"notLogin");
  var password = this.getCellData(recordIndex,"isEmptyPass");
 // var url = "<%=contextPath%>/t9/core/funcs/person/act/T9PersonAct/getMenuPara.act?deptId="+deptId;
 // var rtJson = getJsonRs(url);
 // if(rtJson.rtState == '0'){
    if(notLogin == "1"){
      if(cellData == "0"){
        return "<div style=color:#C0C0C0 align=\"center\" title=\""+userName+" 禁止登录OA\">本部门</div >";
      }else if(cellData == "1"){
        return "<div style=color:#C0C0C0 align=\"center\" title=\""+userName+" 禁止登录OA\">全体</div >";
      }else if(cellData == "2"){
        return "<div style=color:#C0C0C0 align=\"center\" title=\""+userName+" 禁止登录OA\">指定部门</div >";
      }
    }else if(password == "1"){
      if(cellData == "0"){
        return "<div align=\"center\" title=\""+userName+" 登录密码为空\"><font color=red>本部门</font ></div >";
      }else if(cellData == "1"){
        return "<div align=\"center\" title=\""+userName+" 登录密码为空\"><font color=red>全体</font ></div >";
      }else if(cellData == "2"){
        return "<div align=\"center\" title=\""+userName+" 登录密码为空\"><font color=red>指定部门</font ></div >";
      }
    }else{
      if(cellData == "0"){
        return "<div align=\"center\" title=\""+userName+"\"><font color=black>本部门</font ></div >";
      }else if(cellData == "1"){
        return "<div align=\"center\" title=\""+userName+"\"><font color=black>全体</font ></div >";
      }else if(cellData == "2"){
        return "<div align=\"center\" title=\""+userName+"\"><font color=black>指定部门</font ></div >";
      }
    }
 // }
}
/**
 * 获得排班名称及相对应的颜色：密码为空用户显示为红色，禁止登录用户显示为灰色 
 */
function getDutyType(cellData, recordIndex, columIndex){
  var deptId = this.getCellData(recordIndex,"seqId");
  var userName = this.getCellData(recordIndex,"userName");
  var notLogin = this.getCellData(recordIndex,"notLogin");
  var password = this.getCellData(recordIndex,"isEmptyPass");
  //var url = "<%=contextPath%>/t9/core/funcs/person/act/T9PersonAct/getMenuPara.act?deptId="+deptId;
  //var rtJson = getJsonRs(url);
  //if(rtJson.rtState == '0'){
    var urls = "<%=contextPath%>/t9/core/funcs/person/act/T9PersonAct/getDutyType.act";
    var rtJsons = getJsonRs(urls , "dutyId=" +  cellData);
    if(rtJsons.rtState == '0'){
      if(notLogin == "1"){
        return "<div style=color:#C0C0C0 align=\"center\" title=\""+userName+" 禁止登录OA\">" +rtJsons.rtData+ "</div >";
      }else if(password == "1"){
        return "<div align=\"center\" title=\""+userName+" 登录密码为空\"><font color=red>" +rtJsons.rtData+ "</font ></div >";
      }else{
        return "<div align=\"center\" title=\""+userName+"\"><font color=black>" +rtJsons.rtData+ "</font ></div >";
      }
    }
  //}
}

function getRegistNumFunc(){
  var urls = "<%=contextPath%>/t9/core/funcs/person/act/T9PersonAct/getRegistNum.act";
  var rtJsons = getJsonRs(urls);
  if (rtJsons.rtState == "0") {
    if(rtJsons.rtData == "1"){
      var url = "/t9/core/funcs/person/supportIframe.jsp?deptId="+deptId;
      //window.showModalDialog(url, window, "dialogWidth:700px;dialogHeight:500px");
      //IE浏览器
      /* if(!!window.ActiveXObject || "ActiveXObject" in window){
          window.showModalDialog(url, self, "dialogWidth:700px;dialogHeight:500px");
      }{ //其他浏览器
          openWindow(url,'',700,500);
      } */
      openWindow(url,'',700,500);
     }else{
       alert("已经达到系统的最大授权用户数("+maxSum+")，不能再增加允许登录OA用户");
     }
  }else{
    alert(rtJson.rtMsrg); 
  }
}

function getNoRegistNumFunc(){
  var urls = "<%=contextPath%>/t9/core/funcs/person/act/T9PersonAct/getNoRegistNum.act";
  var rtJsons = getJsonRs(urls);
  if (rtJsons.rtState == "0") {
    if(rtJsons.rtData == "1"){
      var url = "/t9/core/funcs/person/supportIframe.jsp?deptId="+deptId;
      window.showModalDialog(url, window, "dialogWidth:700px;dialogHeight:450px");
     }else{
       alert("已经达到系统的最大授权用户数(30)，不能再增加允许登录OA用户");
     }
  }else{
    alert(rtJson.rtMsrg); 
  }
}

function support(groupNames, seqId){
  //var URL = "/t9/core/funcs/person/supportIframe.jsp?deptId="+deptId;
  //openDialog(URL,'670', '450');
  var urls = "<%=contextPath%>/t9/core/funcs/person/act/T9PersonAct/getRegistOrg.act";
  var rtJsons = getJsonRs(urls);
  if (rtJsons.rtState == "0") {
    if(rtJsons.rtData == "1"){
      getRegistNumFunc();
    }else{
      getNoRegistNumFunc();
    }
  } else {
    alert(rtJson.rtMsrg); 
  }
}

function checkBoxRender(cellData, recordIndex, columIndex){
  var diaId = this.getCellData(recordIndex,"seqId");
  var userId = this.getCellData(recordIndex,"userId");
  if(userId != "admin"){
    return "<input type=\"checkbox\" name=\"deleteFlag\" value=\"" + diaId + "\" onclick=\"checkSelf()\" >";
  }else{
    return "";
  }
    
}
/**
 * 全选

 */
function checkAll(field) {
  var deleteFlags = document.getElementsByName("deleteFlag");
  for(var i = 0; i < deleteFlags.length; i++) {
    deleteFlags[i].checked = field.checked;
  }
}

function checkSelf(){
  var allCheck = $('checkAlls');
  if(allCheck.checked){
    allCheck.checked = false;
  }
}

function deleteAllUser() {
  var idStrs = checkMags('deleteFlag');
  if(!idStrs) {
    alert("要删除用户，请至少选择其中一个。");
    return;
  }
  if(!confirmDel()) {
    return ;
  }  
  var url = "<%=contextPath%>/t9/core/funcs/person/act/T9PersonAct/deleteUser.act";
  var rtJson = getJsonRs(url, "sumStrs=" + idStrs);
  if (rtJson.rtState == "0") {
    window.location.reload();
    window.parent.navigateFrame.location.reload();
  }else {
    alert(rtJson.rtMsrg); 
  }
}

/**
 * 清空密码
 */
 
function clearPassword(){
  var idStrs = checkMags('deleteFlag');
  if(!idStrs) {
    alert("要清空用户密码，请至少选择其中一个。");
    return;
  }
  if(!confirmClear()) {
     return ;
  }  
  var url = "<%=contextPath%>/t9/core/funcs/person/act/T9PersonAct/clesrUserPassword.act";
  var rtJson = getJsonRs(url, "seqIdStrs=" + idStrs);
  if (rtJson.rtState == "0") {
    window.location.reload();
    window.parent.navigateFrame.location.reload();
  }else {
    alert(rtJson.rtMsrg); 
  }
}

/**
 * 清空在线时长
 */

function clearVisitTime(){
  var idStrs = checkMags('deleteFlag');
  if(!idStrs) {
    alert("要清空在线时长，请至少选择其中一个用户。");
    return;
  }
  if(!confirmVisitTime()) {
     return ;
  }  
  var url = "<%=contextPath%>/t9/core/funcs/person/act/T9PersonAct/clearVisitTime.act";
  var rtJson = getJsonRs(url, "seqIdStrs=" + idStrs);
  if (rtJson.rtState == "0") {
    window.location.reload();
    window.parent.navigateFrame.location.reload();
  }else {
    alert(rtJson.rtMsrg); 
  }
}

function refreshBoth() {
  parent.document.getElementById("navigateFrame").src = "<%=contextPath %>/core/funcs/person/utilitylist.jsp";
  window.location.reload(); 
   
}
</script>
</head>
<body onload="doInit()">
<table cellscpacing="1" cellpadding="3" width="100%">
  <tr>
    <td class="Big">
      <img src="<%=imgPath%>/notify_new.gif"></img><span class="big3">&nbsp;新建用户 （<span id="newUser"></span>）</span>
    </td>
  </tr>
</table>
<div align="center">
  <a href="javascript: void(0)" class="ToolBtn" title="新建用户" onclick="support()"><span>新建用户</span></a>
  &nbsp;
<%if (!T9RegistUtility.hasRegisted()) { %>
<a class="ToolBtn" href="<%=orgFirstSite %>" target="_blank"><span>试用登记</span></a>
			&nbsp;&nbsp;&nbsp;&nbsp;<span class="TextColor3">参加试用登记的用户，均可获得售前技术支持服务，还可获赠试用期延长和用户数限制增加的注册文件。</span>
<%} %>
</div>
<br>
<table width="95%" border="0" cellspacing="0" cellpadding="0" height="3">
 <tr>
   <td background="<%=imgPath%>/dian1.gif" width="100%"></td>
 </tr>
</table>

<table border="0" width="100%" cellspacing="0" cellpadding="3" class="small">
  <tr>
    <td class="Big"><img src="<%=imgPath%>/notify_open.gif" align="absmiddle"><span class="big3">&nbsp;管理用户（<span id="manageUser"></span>）－ <span id="numUser"></span>人</span>
      &nbsp;<span class="TextColor3" style="FONT-SIZE:9pt;">说明：密码为空用户显示为红色，禁止登录用户显示为灰色</span>
    </td>
  </tr>
</table>
<br/>
<div id="listContainer" style="display:none;">
</div>
<div id="delOpt" style="display:none">
<table id="beSortTable" class="TableBlock no-top-border" width="100%">
   <tr class='TableContent'>
     <td style="padding-left: 8px;">
       <input type="checkbox" name="checkAlls" id="checkAlls" onClick="checkAll(this)">
       <label for='heckAlls'>全选</label>&nbsp;&nbsp;&nbsp;&nbsp;
       <input type='button' value='删除' class='SmallButton' onClick='deleteAllUser();' title='删除'>&nbsp;&nbsp;&nbsp;&nbsp;
       <input type='button' value='清空在线时长' class="SmallButtonC" onClick='clearVisitTime();' title='清空在线时长'>&nbsp;&nbsp;&nbsp;&nbsp;
       <input type='button' value='清空密码' class='SmallButton' onClick='clearPassword();' title='清空密码'>
     </td>
   </tr>
</table>
</div>
<div id="msrg">
</div>
</body>
</html>