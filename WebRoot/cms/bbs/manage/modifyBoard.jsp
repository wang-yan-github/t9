<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header.jsp" %>
<%
String seqId = request.getParameter("seqId");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>修改论坛版块</title>
<link rel="stylesheet" href = "<%=cssPath%>/style.css">
<link rel="stylesheet" href="<%=cssPath%>/cmp/Calendar.css" type="text/css" />
<style type="text/css"> 

.spanImage{
  width: 50px;
  height: 50px;
  display: inline-block;
}

</style>
<script type="text/javascript" src="<%=contextPath %>/core/js/prototype.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/datastructs.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/sys.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/smartclient.js"></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/cmp/select.js" ></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/orgselect.js" ></script>
<script type="text/javascript"  src="<%=contextPath%>/core/js/cmp/Menu.js"></script>
<script type="text/javascript"  src="<%=contextPath%>/cms/column/js/columnLogic.js"></script>
<script type="text/javascript">
function doInit(){
  getArea();
  
  var url = "<%=contextPath%>/t9/cms/bbs/board/act/T9BbsBoardAct/getBbsBoardById.act?seqId=<%=seqId%>";
  var rtJson = getJsonRs(url);
  if(rtJson.rtState == "0"){
    var data = rtJson.rtData;
    bindJson2Cntrl(data);
    var imageUrl = data.imageUrl;
    var value = imageUrl.substring(25, imageUrl.length-4);
    if(!$('spanImage'+value)){
      value = 1;
      $("imageUrl").value = "/t9/cms/bbs/image/common_"+value+".png";
    }
    $('spanImage'+value).style.border = "blue solid 2px";
  }
}

function getArea(){
  var url = "<%=contextPath%>/t9/cms/bbs/board/act/T9BbsBoardAct/getBbsArea.act";
  var rtJson = getJsonRs(url);
  if(rtJson.rtState == "0"){
    var data = rtJson.rtData;
    for(var i = 0; i < data.length; i++){
      $('areaId').options.add(new Option(data[i].areaName, data[i].seqId));
    }
  }
}

function clearArea(){
  $('areaId').options.length = 0;   
}

function checkForm(){
  if($("boardIndex").value == ""){
    alert("排序号不能为空！");
    $("boardIndex").focus();
    return (false);
  }
  
  if(!checkRate($("boardIndex").value)){
    alert("排序号必须为正整数！")
    $("boardIndex").value="";
    $("boardIndex").focus();
    return false;
  }
  
  if($("boardName").value == ""){
    alert("版块名称不能为空！");
    $("boardName").focus();
    return (false);
  }
  
  if($("boardManager").value == ""){
    alert("版主不能为空！");
    $("boardManager").focus();
    return (false);
  }
  return true;
}

function doSubmit(){
  if(checkForm()){
    var url = "<%=contextPath%>/t9/cms/bbs/board/act/T9BbsBoardAct/modifyBoard.act";
    var rtJson = getJsonRs(url,mergeQueryString($("form1")));
    if(rtJson.rtState == "0"){
      alert("修改版块成功！");
      location.href = contextPath + "/cms/bbs/manage/manage.jsp";
    }
  }
}

function addBbsArea(){
  var url=contextPath + "/cms/bbs/manage/addBbsArea.jsp";
  newWindow1(url,'560', '360');
}

function newWindow1(url,width,height){
  var locX=(screen.width-width)/2;
  var locY=(screen.height-height)/2;
  var info=window.open(url, "", 
      "height=" +height + ",width=" + width +",status=1,toolbar=no,menubar=no,location=no,scrollbars=yes, top=" 
      + locY + ", left=" + locX + ", resizable=yes");
}

function spanClick(e,k){
  var list = $('spanImageList').getElementsByTagName('span');
  for(var i = 0; i < list.length; i++){
    list[i].style.border = "white solid 0px";
  }
  $('imageUrl').value = "/t9/cms/bbs/image/common_"+k+".png";
  e.style.border = "blue solid 2px";
}

function checkRate(input){ 
  var re = /^[0-9]+[0-9]*]*$/;
  if(!re.test(input)) {  
    return false;  
  }  
  return true;
}   
</script>
</head>
<body onload="doInit();">
<table border="0" width="100%" cellspacing="0" cellpadding="3" class="small">
  <tr>
    <td><img src="<%=imgPath %>/notify_new.gif" align="middle"><span class="big3"> 编辑论坛版块</span>&nbsp;&nbsp;
    </td>
  </tr>
</table>
<br>
<form action="" method="post" name="form1" id="form1">
  <input type="hidden" id="seqId" name="seqId">
  <table class="TableBlock" width="80%" align="center">
    <tr>
      <td nowrap class="TableData">所属专区：<font color="red">*</font> </td>
      <td class="TableData">
        <select id="areaId" name="areaId" style="width:313px;">
        </select>&nbsp;&nbsp;&nbsp;&nbsp;
        <a href="javascript:void(0);" onClick="addBbsArea();">添加区域</a>
      </td>
    </tr>
    <tr>
      <td nowrap class="TableData">版块排序号：<font color="red">*</font> </td>
      <td class="TableData">
        <input type="text" name="boardIndex" id="boardIndex" class="BigInput" style="width:100px;" >&nbsp;&nbsp;请输入1~3位的正整数!
      </td>
    </tr> 
    <tr>
      <td nowrap class="TableData">版块名称：<font color="red">*</font> </td>
      <td class="TableData">
        <input type="text" name="boardName" id="boardName" class="BigInput" style="width:300px;" >
      </td>
    </tr>
    
    <tr>
      <td nowrap class="TableData">版块图标：<font color="red">*</font> </td>
      <td class="TableData" style="height:60px;" id="spanImageList">
        <input type="hidden" id="imageUrl" name="imageUrl" value="">
        <span id="spanImage1" class="spanImage" style="background: url('../image/common_1.png') no-repeat center;" onclick="spanClick(this,1)"></span>&nbsp;
        <span id="spanImage2" class="spanImage" style="background: url('../image/common_2.png') no-repeat center;" onclick="spanClick(this,2)"></span>&nbsp;
        <span id="spanImage3" class="spanImage" style="background: url('../image/common_3.png') no-repeat center;" onclick="spanClick(this,3)"></span>&nbsp;
        <span id="spanImage4" class="spanImage" style="background: url('../image/common_4.png') no-repeat center;" onclick="spanClick(this,4)"></span>&nbsp;
        <span id="spanImage5" class="spanImage" style="background: url('../image/common_5.png') no-repeat center;" onclick="spanClick(this,5)"></span>&nbsp;
        <span id="spanImage6" class="spanImage" style="background: url('../image/common_6.png') no-repeat center;" onclick="spanClick(this,6)"></span>&nbsp;
        <span id="spanImage7" class="spanImage" style="background: url('../image/common_7.png') no-repeat center;" onclick="spanClick(this,7)"></span>&nbsp;
        <span id="spanImage8" class="spanImage" style="background: url('../image/common_8.png') no-repeat center;" onclick="spanClick(this,8)"></span>&nbsp;
        <span id="spanImage9" class="spanImage" style="background: url('../image/common_9.png') no-repeat center;" onclick="spanClick(this,9)"></span>&nbsp;
      </td>
    </tr>
    
    <tr>
      <td nowrap class="TableData">版块简介： </td>
      <td class="TableData">
        <input type="text" name="boardAbstract" id="boardAbstract" class="BigInput" style="width:300px;" >
      </td>
    </tr> 
    <tr>
      <td nowrap class="TableData">锁帖期限： </td>
      <td class="TableData">
        锁定<input type="text" name="lockDay" id="lockDay" class="BigInput" style="width:10px;" value="0">天前的帖子，无法编辑删除 (说明：0或空表示不锁定)
      </td>
    </tr>
    <tr>
	    <td nowrap class="TableData">授权范围：（部门）</td>
	    <td class="TableData">
	      <input type="hidden" name="dept" id="dept" value="">
	      <textarea cols=40 name="deptDesc" id="deptDesc" rows=3 class="BigStatic" wrap="yes" readonly></textarea>
	      <a href="javascript:;" class="orgAdd" onClick="selectDept()">添加</a>
	      <a href="javascript:;" class="orgClear" onClick="$('dept').value='';$('deptDesc').value='';">清空</a>
	    </td>
	  </tr>
	  <tr>
	    <td nowrap class="TableData"">授权范围：（角色）</td>
	    <td class="TableData">
	      <input type="hidden" name="role" id="role" value="">
	      <textarea cols=40 name="roleDesc" id="roleDesc" rows=3 class="BigStatic" wrap="yes" readonly></textarea>
	      <a href="javascript:;" class="orgAdd" onClick="selectRole();">添加</a>
	      <a href="javascript:;" class="orgClear" onClick="$('role').value='';$('roleDesc').value='';">清空</a>
	    </td>
	  </tr>
	  <tr>
	    <td nowrap class="TableData">授权范围：（人员）</td>
	    <td class="TableData">
	      <input type="hidden" name="user" id="user" value="">
	      <textarea cols=40 name="userDesc" id="userDesc" rows=3 class="BigStatic" wrap="yes" readonly></textarea>
	      <a href="javascript:;" class="orgAdd" onClick="selectUser();">添加</a>
	      <a href="javascript:;" class="orgClear" onClick="$('user').value='';$('userDesc').value='';">清空</a>
	    </td>
	  </tr>
    
    <tr>
      <td nowrap class="TableData">版主： <font color="red">*</font></td>
      <td class="TableData">
        <input type="hidden" name="boardManager" id="boardManager" value="" >
        <input type="text" name="boardManagerDesc" id="boardManagerDesc" class="BigStatic" readonly style="width:300px;" >
        <a href="javascript:void(0);" class="orgAdd" onClick="selectUser(['boardManager', 'boardManagerDesc']);">添加</a>
      </td>
    </tr>
    <tr style="display:none;">
      <td nowrap class="TableData">允许匿名： </td>
      <td class="TableData">
        &nbsp;&nbsp;
        <input type="radio" name="anonymity" id="anonymityYes" size="15" value="1">是&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
        <input type="radio" name="anonymity" id="anonymityNo" size="15" value="0" checked>否
      </td>
    </tr>
    <tr style="display:none;">
      <td nowrap class="TableData">是否审核： </td>
      <td class="TableData">
        &nbsp;&nbsp;
        <input type="radio" name="isCheck" id="isCheckYes" size="15" value="1">是&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
        <input type="radio" name="isCheck" id="isCheckNo" size="15" value="0" checked>否
      </td>
    </tr>
    <tr align="center" class="TableControl">
      <td colspan=2 nowrap>
        <input type="hidden" name="dtoClass" id="dtoClass" value="t9.cms.bbs.board.data.T9BbsBoard">
        <input type="button" value="保存" onclick="doSubmit();" class="BigButton">
      </td>
    </tr>
  </table>
</form>
</body>
</html>