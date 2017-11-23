<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header.jsp"%>

<%@ page import="t9.core.funcs.system.act.T9SystemAct" %>
<%
String useUsbKeyStr = (String)request.getAttribute("useUsbKey");
String secKeyUserStr = (String)request.getAttribute("secKeyUser");
String verificationCodeStr = (String)request.getAttribute("verificationCode");
String ieTitle = (String)request.getAttribute("ieTitle");
String useT9ErpStr = T9SysProps.getString("useT9Erp");

if (T9Utility.isNullorEmpty(ieTitle)) {
  ieTitle = productName;
}

if (T9Utility.isNullorEmpty(useUsbKeyStr) || T9Utility.isNullorEmpty(secKeyUserStr)) {
  request.getRequestDispatcher("/t9/core/funcs/system/act/T9SystemAct/prepareLoginIn.act").forward(request, response);
  return;
}

boolean useUsbKey = "1".equals(useUsbKeyStr);
boolean secKeyUser = "1".equals(secKeyUserStr);
boolean useT9Erp = "1".equals(useT9ErpStr);
boolean verificationCode = "1".equals(verificationCodeStr);



Integer randomInt = (Integer)request.getSession().getAttribute("RANDOM_NUMBER");

int randomNum = 123456;
if (randomInt != null) {
  randomNum = randomInt;
}
String template = T9SystemAct.queryTemplate(request);
template = contextPath + "/core/templates/" + template;
%>
<html>
<head>
<title><%=ieTitle %></title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta name="author" content="<%=productName %>,<%=shortProductName %>" />
<meta name="keywords" content="<%=productName %>,<%=shortProductName %>" />
<meta name="description" content="<%=productName %>,<%=shortProductName %>" />
<link rel="stylesheet" href = "<%=template %>/style.css">
<script type="text/javascript" src="<%=contextPath%>/core/js/datastructs.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/sys.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/prototype.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/smartclient.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/tdPass.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/rsa.js"></script>
<script type="text/javascript">
var useUsbKey = <%=useUsbKey%>;
var secKeyUser = <%=secKeyUser%>;
var useT9Erp = <%=useT9Erp%>;
var randomNum = "<%=randomNum%>";
function checkUserName(inputStr) {
  var tmpValue = inputStr;
  //以下搜索字符串中的特殊字符，如果存在，则替换成""
  if (tmpValue.indexOf('|') > -1) {return false; }
  if (tmpValue.indexOf('&') > -1) {return false; }
  if (tmpValue.indexOf(';') > -1) {return false; }
  if (tmpValue.indexOf('$') > -1) {return false; }
  if (tmpValue.indexOf('%') > -1) {return false; }
  if (tmpValue.indexOf('@') > -1) {return false; }
  if (tmpValue.indexOf("'") > -1) {return false; }
  if (tmpValue.indexOf('"') > -1) {return false; }
  if (tmpValue.indexOf('(') > -1) {return false; }
  if (tmpValue.indexOf(')') > -1) {return false; }
   if (tmpValue.indexOf('+') > -1) {return false; }
  if (tmpValue.indexOf('<') > -1) {return false; }
  if (tmpValue.indexOf('>') > -1) {return false; }
  if (tmpValue.indexOf('--') > -1) {return false; }
   if (tmpValue.indexOf(",") > -1) {return false; }
   if (tmpValue.indexOf("?") > -1) {return false; }
   if (tmpValue.indexOf("=") > -1) {return false; }
  if (tmpValue.indexOf("\\") > -1) {return false; }
    if (tmpValue.indexOf("\n") > -1) {return false; }
	if (tmpValue.indexOf("\r") > -1) {return false; }
 return true;
}
function doLogin(){
  if (useUsbKey) {
     if(!CheckForm()){
       return;
     }
     if (!$("KEY_SN").value || !$("KEY_DIGEST").value || !$("KEY_USER").value) {
       if(!$('userName').present()){
         alert("请输入用户名");
         $('userName').focus();
         return;
       }
       if (!checkUserName($('userName').value)) {
         alert("输入的用户名含有特殊字符！请修改");
         $('userName').focus();
         return;
       }
     }
     if (secKeyUser) {
       if(!$('userName').present()){
         alert("请输入用户名");
         $('userName').focus();
         return;
       }
       if (!checkUserName($('userName').value)) {
         alert("输入的用户名含有特殊字符！请修改");
         $('userName').focus();
         return;
       }
     }
     loginUsbKey();
  }else {
    if(!$('userName').present()){
      alert("请输入用户名");
      $('userName').focus();
      return ;
    }
    if (!checkUserName($('userName').value)) {
      alert("输入的用户名含有特殊字符！请修改");
      $('userName').focus();
      return;
    }
    loginNamePass();
  }
}

function CheckForm(){
  $("KEY_SN").value = "";
  $("KEY_DIGEST").value = "";
  $("KEY_USER").value = ""
  try{
		var theDevice = document.getElementById("tdPass");
		var KeySN = READ_SN(theDevice);
		if (KeySN < 0) {
		  return true;
		}
		var Digest = COMPUTE_DIGEST(theDevice, randomNum);
		if (Digest < 0) {
		  return true;
		}
		var Key_UserID = READ_KEYUSER(theDevice);
		if (Key_UserID < 0) {
		  return true;
		}
		$("KEY_SN").value = KeySN;
		$("KEY_DIGEST").value = Digest;
		$("KEY_USER").value = Key_UserID
		//var rsa = new RSAKey();
		//rsa.setPublic("aebc3bedeeff7587f512736b8ffba63ac033c1bbf1a51a821af920a49bc7786f", "10001");
		 //$("pwd").value = rsa.encrypt($("pwd").value);
  } catch(ex){
    return false;
  }
  return true;
}

<%
if (useUsbKey) {
%>
/**
 * 用户名密码登录 */
function loginUsbKey() {
  var pars = $('loginForm').serialize() ;
  var url = contextPath + "/t9/core/funcs/system/act/T9SystemAct/doLoginIn.act";
  $('layout').hide();
  $('tip').show();
  //$('displayDiv').show();
  var json = getJsonRs(url,pars);
  $('tip').hide();
  loginComplete(json);
}

function showTdPassObject(){
  //document.getElementById("tdPassObject").innerHTML='<object id="tdPass" name="tdPass" CLASSID="clsid:0272DA76-96FB-449E-8298-178876E0EA89" CODEBASE="/t9/core/cntrls/tdPass.cab#Version=1,00,0000" BORDER="0" VSPACE="0" HSPACE="0" ALIGN="TOP" HEIGHT="0" WIDTH="0"></object>';
  //document.getElementById("installTdPass").style.display="none";
}
<%
}
%>

/**
 * 用户名密码登录 */
function loginNamePass() {

  var pars = $('loginForm').serialize() ;
  var url = contextPath + "/t9/core/funcs/system/act/T9SystemAct/doLoginIn.act";
  $('layout').hide();
  //$('displayDiv').show();
  
  try {
      //$('tip').show();
      
	  var json = getJsonRs(url,pars);
	  //$('tip').hide();
	  loginComplete(json);
  } catch (e){
    alert(e);
    //alert('服务器连接中断');
  }
}
function loginComplete(json) {
  if (json.rtState == "0"){
      if (useT9Erp) {
         t9erpSso(json.rtData);
      }
      //记录上次成功登陆的用户名
      if (json.rtData.saveUserName == "1") {
        setCookie('userName',$('userName').value , 30);
      }else {
        setCookie('userName','' , 30);
      }
      if (json.rtData.sessionToken) {
        setCookie('SID_'  + json.rtData.seqId ,json.rtData.sessionToken , 1);
      }
      var url = contextPath + (json.rtData.homeAddress || "/core/frame/webos/index.jsp");
      //alert(url)
      switch (json.rtData.menuType) {
          case '1':
        	  window.location.href = url;
        	  break;
        case '2':
        	  window.open(url, "_blank","top=0,left=0,toolbar=yes," +
           "location=yes, directories=no, status=no, scrollbars=yes," +
           "resizable=yes, copyhistory=no, width=" + window.screen.width + "," + 
           "height=" + window.screen.height);
        	window.opener=null;
            window.open("","_self");
            window.close();
            return;
        case '3': 
            window.open(url, "_blank","top=0,left=0,toolbar=no," +
           "location=no, directories=no, status=no, menubar=no, scrollbars=yes," +
           "resizable=yes, copyhistory=no, width=" + window.screen.width + "," +
           "height=" + window.screen.height);
           window.opener=null;
           window.open("", "_self");
           window.close();
           return;
        default:
           window.location.href = url;
       }
    } else{
        switch(json.rtData.code){
          case 0:{
	        alert(json.rtMsrg);
	        $('loginForm').reset();
	        $('layout').show();
	        $('userName').focus();
	        break;
	      }
	      case 1:{
	      }
	      case 2:{
	      }
	      case 3:{
	      }
	      case 9:{
	      }
	      case 10:{
	      }
	      case 11:{
	      }
	      case 12:{
	        alert(json.rtMsrg);
	        $('pwd').value = '';
	        $('layout').show();
	        $('userName').focus();
	        break;
	      }
	      case '13':{
	    	alert(json.rtMsrg);
	        window.location.href = contextPath + "/core/funcs/system/info/index.jsp";
	        break;
	      }
	      case 4:{
	        alert(json.rtMsrg);
	        $('layout').show();
	        break;
	      }
	      case 5:{
	        alert(json.rtMsrg);
	        $('pwd').value = '';
	        $('layout').show();
	        $('pwd').focus();
	        break;
	      }
	      case 6:{
	        if (json.rtData.sessionToken) {
	          setCookie('SID_'  + json.rtData.seqId ,json.rtData.sessionToken , 1);
	        }
	        window.location = contextPath + "/core/frame/pass.jsp";
	        break;
	      }
	      case 7:{
	        if (json.rtData.sessionToken) {
	          setCookie('SID_'  + json.rtData.seqId ,json.rtData.sessionToken , 1);
	        }
	        window.location = contextPath + "/core/frame/pass.jsp";
	        break;
	      }
	      case 8:{
	        alert('用户名或密码错误超过 ' + json.rtData.msg.times + ' 次，请等待' + json.rtData.msg.minutes + '分钟后重试!');
	        $('pwd').value = '';
	        $('layout').show();
	        $('pwd').focus();
	        break;
	      }
	      case 14: {
	        alert('验证码错误');
	        $('pwd').value = '';
	        $('layout').show();
	        $('pwd').focus();
	        break;
	      }
	      default:{
	        alert("登录失败!");
	        $('layout').show();
	      }
       }
    }
}

/**
 * 处理用户获取
 */
function doFocus(obj) {
  obj.select();
}

var KEY_ENTER = 0X000D;

/**
 * 处理键盘按键press事件
 */
function documentKeypress(e){
  var id = document.activeElement.id;
  if (id != 'userName' && id != 'pwd' && id != 'verificationCode'){
    return;
  }
  
  var currKey = 0;
  var e = e || event;
  currKey = e.keyCode || e.which || e.charCode;

  if (currKey == KEY_ENTER){
    doLogin();
  }
}

document.onkeypress = documentKeypress;

function doInit(){
  getLoginBg();
  if (getCookie('userName')){
	  $('userName').value = getCookie('userName');
    $('pwd').focus();
  }
  else{
    $('userName').focus();
  }
}

function getLoginBg() {
  
  $('loginBg').setStyle({'backgroundImage':'url(<%=contextPath%>/t9/core/funcs/system/act/T9SystemAct/getLoginBg.act)'});
}

/**
 * 设置cookie
 */
function setCookie(name,value , Days){
  var exp  = new Date();
  exp.setTime(exp.getTime() + Days*24*60*60*1000);
  document.cookie = name + "="+ escape (value) + ";path=/;expires=" + exp.toGMTString();
}

/**
 * 读取cookie
 */
function getCookie(name){
  
  var arr = document.cookie.match(new RegExp("(^| )"+name+"=([^;]*)(;|$)"));
  if (arr != null){
    return unescape(arr[2]);
  }
  else{
    return null;
  }
  return null;
}

/**
 * 进销存单点登录
 */
function t9erpSso(userinfo) {
  var srcs = [
              "/t9erp/login.do?userName=",
              userinfo.userName,
              "&action_flag=login_check_depend_on_oa_ea&seqId=",
              userinfo.seqId,
              "&deptId=",
              userinfo.deptId];
  var url = srcs.join("");
  try {
    var json = getJsonRsAsyn(url);
  } catch (e){
    
  }
}
</script>
</head>
<body onload="doInit()">
<%
if (isOnlineEval.equals("1")) {
%>
<div style="color:#000000;position:absolute;left:10px;top:10px;border:1px solid #0000FF;padding:3px;">
<%=shortProductName %>在线试用服务器，24小时均可以访问 <br>
用户名 oa 或  ceo，没有密码，请勿在系统内发表不文明信息
</div>
<%
}
%>
<div id="tip" style="display:none">
</div>
<div id="layout" align="center">
	<form method="post" id='loginForm'>
	  <div class="login-bg" id="loginBg">
	    <div class="login-username">
	      <div class="login-input-bg">
	        <input type="text" onfocus="doFocus(this)" name="userName" id="userName" tabindex="1">
	      </div>
	    </div>
	    <div class="login-password">
	      <div class="login-input-bg">
	        <input type="password" onfocus="doFocus(this)" name="pwd" id="pwd" value="" tabindex="2">
	      </div>
	    </div>
	    <%if (verificationCode) { %>
	    <div class="verification-code">
	      <span>验证码:</span><input type="text" name="verificationCode" id="verificationCode"/>
	      <a href="javascript: void(0)" onclick="changeVerificationCode();"><span>换一张</span></a>
	      <img id="verificationImg" src="<%=contextPath %>/Kaptcha.jpg?t=<%=System.currentTimeMillis()%>"/>
	    </div>
	    <script type="text/javascript">
	      function changeVerificationCode() {
	        $("verificationImg").src = "";
	        setTimeout(function() {
	        $("verificationImg").src = contextPath + "/Kaptcha.jpg?t=" + new Date().getTime();
	        }, 100);
	      }
	    </script>
	    <%} %>
	    <div class="login-btn" id="btnLogin" onclick="doLogin();"></div>
	  </div>
	<%
	if (useUsbKey) {
	%>
	  <div id="tdPassObject" style="display:none;"><object id="tdPass" name="tdPass" CLASSID="clsid:0272DA76-96FB-449E-8298-178876E0EA89" CODEBASE="/t9/core/cntrls/tdPass.cab#Version=1,00,0000" BORDER="0" VSPACE="0" HSPACE="0" ALIGN="TOP" HEIGHT="0" WIDTH="0"></object></div>
	  <input type="hidden" name="KEY_SN" id="KEY_SN" value="">
	  <input type="hidden" name="KEY_USER" id="KEY_USER" value="">
	  <input type="hidden" name="KEY_DIGEST" id="KEY_DIGEST" value="">
	<%
	}
	%>
	</form>
</div>
</body>
</html>
