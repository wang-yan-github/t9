<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>论坛文列表</title>
<link rel="stylesheet" href = "<%=cssPath%>/style.css">
<link rel="stylesheet" href="<%=cssPath%>/page.css">
<link rel="stylesheet" href="<%=cssPath%>/cmp/Calendar.css">
<script type="text/javascript" src="<%=contextPath %>/core/js/prototype.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/datastructs.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/sys.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/smartclient.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/Calendarfy.js"></script>
<script> 
function doInit(){
  getArea();
}

function getArea(){
  var url = "<%=contextPath%>/t9/cms/bbs/board/act/T9BbsBoardAct/getBbsArea.act";
  var rtJson = getJsonRs(url);
  if(rtJson.rtState == "0"){
    var data = rtJson.rtData;
    var tbody = $('dataBody');
    for(var i = 0; i < data.length; i++){
      
      var tr = $C('tr');
      tr.id = "board_" + data[i].seqId;
      tr.name = "board_" + data[i].seqId;;
      tr.style.background = "rgb(247, 248, 189)";
      
      
      var td1 = $C('td');
      td1.align = 'left';
      td1.innerHTML = "("+data[i].areaIndex+") "+data[i].areaName;
      td1.style.textAlign = "left";
      td1.style.fontWeight = "bold";
      
      var td2 = $C('td');
      td2.align = 'left';
      td2.innerHTML = "<a href=javascript:editArea(" + data[i].seqId + ")>编辑</a> "
                    + "<a href=javascript:deleteArea(" + data[i].seqId + ")>删除</a>";
      td2.style.textAlign = "right";
      
      tr.appendChild(td1);
      tr.appendChild(td2);
      tbody.appendChild(tr);
      
      getBoard(data[i].seqId);
    }
  }
}

function getBoard(areaId){
  var tbody = $('dataBody');
  var url = "<%=contextPath%>/t9/cms/bbs/board/act/T9BbsBoardAct/getBbsBoard.act?areaId="+areaId;
  var rtJson = getJsonRs(url);
  if(rtJson.rtState == "0"){
    var data = rtJson.rtData;
    for(var i = 0; i < data.length; i++){
      
      
      var tr = $C('tr');
      tr.id = "area_" + data[i].seqId;
      tr.name = "area_" + data[i].seqId;;
      
      var td1 = $C('td');
      td1.align = 'left';
      td1.innerHTML = "<table><tr><td><img src=" + data[i].imageUrl + "></td><td><span>("+data[i].boardIndex+") "+data[i].boardName+"</span><br>版主："+data[i].boardManager+"</td></tr></table>";
      td1.style.paddingLeft = "20px";
      td1.style.textAlign = "left";
      td1.style.fontWeight = "bold";
      
      var td2 = $C('td');
      td2.align = 'left';
      td2.innerHTML = "<a href=javascript:editBoard(" + data[i].seqId + ")>编辑</a> "
                    + "<a href=javascript:deleteBoard(" + data[i].seqId + ")>删除</a>";
      td2.style.textAlign = "right";
      
      
      tr.appendChild(td1);
      tr.appendChild(td2);
      tbody.appendChild(tr);
    }
  }
}

function editArea(seqId){
  var url = contextPath + "/cms/bbs/manage/modifyBbsArea.jsp?seqId=" + seqId;
  newWindow1(url,'560', '360');
}

function newWindow1(url,width,height){
  var locX=(screen.width-width)/2;
  var locY=(screen.height-height)/2;
  var info=window.open(url, "", 
      "height=" +height + ",width=" + width +",status=1,toolbar=no,menubar=no,location=no,scrollbars=yes, top=" 
      + locY + ", left=" + locX + ", resizable=yes");
}

function editBoard(seqId){
  location.href = contextPath + "/cms/bbs/manage/modifyBoard.jsp?seqId=" + seqId;
}

function deleteArea(seqId){
  if(!window.confirm("确认要删除该专区吗？")){
    return ;
  }
  var url = "<%=contextPath%>/t9/cms/bbs/board/act/T9BbsBoardAct/deleteBbsArea.act?seqId=" + seqId;
  var rtJson = getJsonRs(url);
  if(rtJson.rtState == "0"){
    if(rtJson.rtData == 1){
      alert("删除专区成功！");
	    location.reload();
    }
    else{
      alert("该专区下还有未删除的版块，请先删除版块信息！")
    }
  }
  else{
    alert(rtJson.rtMsrg); 
  }
}

function deleteBoard(seqId){
  if(!window.confirm("确认要删除该版块吗，该板块下可能存在未删除的帖子？")){
    return ;
  }
  var url = "<%=contextPath%>/t9/cms/bbs/board/act/T9BbsBoardAct/deleteBbsBoard.act?seqId=" + seqId;
  var rtJson = getJsonRs(url);
  if(rtJson.rtState == "0"){
    alert("删除版块成功！");
    location.reload();
  }
  else{
    alert(rtJson.rtMsrg); 
  }
}

function $C(tag){
  return document.createElement(tag);
}
</script>
</head>
<body topmargin="5" onload="doInit()">
<table border="0" width="100%" cellspacing="0" cellpadding="3" class="small">
 <tr>
   <td class="Big"><img src="<%=imgPath%>/notify_open.gif" align="absMiddle"><span class="big3">&nbsp;论坛版块列表 </span>
   </td>
 </tr>
</table>
<br>

<table class="TableList" id="tableList" width="70%" align="center">
  <thead>
   <tr style="background: rgb(229, 229, 229); font-weight:bold;font-size: 14px;">
      <td noWrap align="left">(排序号) 名称</td>
      <td noWrap align="right" style="padding-right:15px;">操作</td>
    </tr>
  </thead>
  <tbody id="dataBody">
  </tbody>
</table>

</body>
</html>