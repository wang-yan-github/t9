<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header.jsp" %> 
<%String projId= (String)request.getParameter("projId")==null?"0":(String)request.getParameter("projId");
String seqId= (String)request.getParameter("seqId")==null?"0":(String)request.getParameter("seqId");
%>
<html>
<head>
<title>指定可访问人员</title>
<link rel="stylesheet" href = "<%=cssPath%>/style.css">
<link rel="stylesheet" href = "<%=cssPath%>/cmp/ExchangeSelect.css">
<script type="text/Javascript" src="<%=contextPath%>/core/js/datastructs.js" ></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/sys.js" ></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/prototype.js" ></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/smartclient.js" ></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/cmp/select.js" ></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/cmp/DTree1.0.js"></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/orgselect.js" ></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/jquery.js" ></script>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<script>
var projId='<%=projId%>';
var seqId='<%=seqId%>';
jQuery.noConflict();
function doInit(){
	
	var url="<%=contextPath%>/t9/project/file/act/T9ProjFileSortAct/getPriAndUser.act?seqId="+seqId+"&projId="+projId;
	var rtJson=getJsonRs(url);
	if(rtJson.rtState==0){
		var typeList= "<table class=\"TableBlock\" align=\"center\" width=\"450\" id=\"priv_tab\">"
					+ "<tr class=\"TableHeader\">"
					+ "<td  align=\"center\"  width=\"120\">人员名称</td>"
					+ "<td  align=\"center\" >新建</td>"
					+ "<td  align=\"center\" >删除</td>"
					+ "<td  align=\"center\" >修改</td>"
					+ "<td  align=\"center\" >查看</td>"
					+ "<td  align=\"center\" ><input type=\"checkbox\" name=\"checkAll\" onclick=\"check_all(this);\">全选</td>"
					+ "</tr>";
		var str="";
		var users = rtJson.rtData[0].userName.split(",");
		var userIds=rtJson.rtData[0].userId.split(",");
		for(var i=0;i<users.length;i++){
			if(users[i]!=""&&users[i]!=null){
			str += "<tr class=\"TableLine1\" id=\""+userIds[i]+"\">"
				+ "<td align=\"center\"  width=\"120\">"+users[i]+"</td>"
				+ "<td align=\"center\"><input type=\"checkbox\" id=\""+userIds[i]+"_new"+"\" ></td>"
				+ "<td align=\"center\"><input type=\"checkbox\" id=\""+userIds[i]+"_del"+"\" ></td>"
				+ "<td align=\"center\"><input type=\"checkbox\" id=\""+userIds[i]+"_edit"+"\" ></td>"
				+ "<td align=\"center\"><input type=\"checkbox\" id=\""+userIds[i]+"_view"+"\" ></td>"
				+ "<td align=\"center\"><input type=\"checkbox\" name=\"checkAll\" onclick=\"check_one(this);\"></td>"
			    + "</tr>";
			}
		}
		var subtr ="";
		subtr+="<tr>"
	    +"<td class=\"TableControl\" colspan=\"6\" align=\"center\">"
	    	+"<input type=\"hidden\" name=\"projId\" id=\"projId\" value="+projId+">"
	      +"<input type=\"hidden\" name=\"seqId\" id=\"seqId\" value="+seqId+">"
	      +"<input type=\"hidden\" name=\"new_user\" id=\"new_user\" value=\"\">"
	      +"<input type=\"hidden\" name=\"manage_user\" id=\"manage_user\" value=\"\">"
	      +"<input type=\"hidden\" name=\"modify_user\" id=\"modify_user\" value=\"\">"
	      +"<input type=\"hidden\" name=\"view_user\" id=\"view_user\" value=\"\">"
	        +"<input type=\"button\" value=\"确定\" class=\"BigButton\" onclick=\"priv_submit();\">&nbsp;&nbsp;"
	        +"<input type=\"button\" value=\"返回\" class=\"BigButton\" onclick=\"javascript:history.go(-1);\">"
	    +"</td>"
	  +"</tr>"
		$("styleList").innerHTML= typeList + str + subtr + "</table>";
	priv_set(rtJson.rtData[1].newUser.split(","),"_new");
	priv_set(rtJson.rtData[1].manageUser.split(","),"_del");
	priv_set(rtJson.rtData[1].modifyUser.split(","),"_edit");
	priv_set(rtJson.rtData[1].viewUser.split(","),"_view");
	}else{
		alert(rtJson.rtMsrg); 	
	}
}
function priv_set(users,flag){
	for(var i=0;i<users.length;i++){
		if(users[i]!=""&&users[i]!="null"){
		$(users[i]+flag).checked=true;
			}
		}
}
var $ = function(id) {
	return document.getElementById(id);
	};
function priv_submit(){
	var tab=$("priv_tab");
	projId = $("projId").value;
	seqId = $("seqId").value;
	for(var i=1;i<tab.rows.length-1;i++)
	{
		var user_id=tab.rows[i].id;
		if(tab.rows[i].cells[1].firstChild.checked==true)
		  $("new_user").value += user_id+",";
		if(tab.rows[i].cells[2].firstChild.checked==true)
		  $("manage_user").value += user_id+",";
		if(tab.rows[i].cells[3].firstChild.checked==true)
		  $("modify_user").value += user_id+",";
		if(tab.rows[i].cells[4].firstChild.checked==true)
		  $("view_user").value += user_id+",";
	}
	  var url = "<%=contextPath%>/t9/project/file/act/T9ProjFileSortAct/updatefileSortPriv.act";
	  var rtJson = getJsonRs(url, jQuery("#form1").serialize());
    if (rtJson.rtState == "0") {
      alert(rtJson.rtMsrg);
      history.go(-1);
    }else {
      alert(rtJson.rtMsrg); 
    }
}


function check_one(obj)
{
	var tr=obj.parentNode.parentNode;
	if(obj.checked==true)
	{
	  for(var i=1;i<tr.cells.length-1;i++)
	    tr.cells[i].firstChild.checked=true;
	}
	else
	{
		for(var i=1;i<tr.cells.length-1;i++)
	    tr.cells[i].firstChild.checked=false;
	}
}
function check_all(obj)
{
	var tab=$("priv_tab");
	if(obj.checked==true)
	{
  	for(var i=1;i<tab.rows.length-1;i++)
  	{
  		tab.rows[i].cells[5].firstChild.checked=true;
  		check_one(tab.rows[i].cells[5].firstChild); 
  	}
  }
  else
  {
  	for(var i=1;i<tab.rows.length-1;i++)
  	{
  		tab.rows[i].cells[5].firstChild.checked=false;
  		check_one(tab.rows[i].cells[5].firstChild); 
  	}  	
  }	
  		
}
</script>
</head>

<body class="bodycolor" topmargin="5" onload="doInit()">


<table border="0" width="100%" cellspacing="0" cellpadding="3" class="small">
  <tr>
    <td class="small"><img src="<%=contextPath %>/core/funcs/system/filefolder/images/edit.gif" WIDTH="22" HEIGHT="20" align="absmiddle"><span class="big3"> 指定权限</span>
    </td>
  </tr>
</table>
<form action="" method="post" id="form1" name="form1">
<div id="styleList" style="min-height:200px;"></div>
</form>
</body>
</html>
