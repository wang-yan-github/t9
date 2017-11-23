<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>管理模板</title>
<link rel="stylesheet" href = "<%=cssPath%>/style.css">
<link rel="stylesheet" href="<%=cssPath%>/page.css">
<script type="text/javascript" src="<%=contextPath %>/core/js/prototype.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/datastructs.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/sys.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/smartclient.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/cmp/page.js"></script>
<script type="text/javascript" src="<%=contextPath %>/project/js/util.js"></script>
<script> 
var pageMgr = null;
function doInit(){
	var url= contextPath + "/t9/project/project/act/T9ProjectAct/getModelList.act";
	var rtJson=getJsonRs(url);
	if(rtJson.rtState == "0"){
	    var data=rtJson.rtData;
	    if(data.size()>0){
	    	var str="<table class=\"TableList\" width=\"600\" align=\"center\" border=0>"
				+"<tr class=\"TableHeader\">"
				+"<td width=80>选择</td>"
				+"<td>模板名称</td>"
						+"</tr>";
			for(var i=0;i<data.size();i++){
				str+="<tr class=\"TableLine2\">"
				   +"<td><input type=\"checkbox\" name=\"email_select\" value=\""+data[i].fileName+"\" onClick=\"check_one(self);\"></td>"
				   +"<td style=\"cursor:hand\">"+data[i].fileName+"</td></tr>";
			}
			str+="<tr class=\"TableFooter\">"
			   +"<td colspan=2>"
				   +"<div align=\"left\">"
			   +"<input type=\"checkbox\" name=\"allbox\" id=\"allbox_for\" onClick=\"check_all();\">"
			   +"<label for=\"allbox_for\">全选</label>&nbsp;"
			   +"<a href=\"javascript:delete_tpl();\" title=_(\"删除所选记录\")><img src=\"../../images/delete.gif\" align=\"absMiddle\">删除</a>&nbsp;"
			   +"</div></td></tr>"
					 +"</table>";
			$("modelList").innerHTML=str;
	    }else{
	    	WarningMsrg('无项目模板', 'msrg');
	    }
	  }else{
			alert(rtJson.rtMsrg);
	  } 
 
}
</script>
</head>
<body class="bodycolor" topmargin="5" onload="doInit();">  

<script Language="JavaScript">
function check_all()
{
 for (i=0;i<document.all("email_select").length;i++)
 {
   if(document.all("allbox").checked)
      document.all("email_select").item(i).checked=true;
   else
      document.all("email_select").item(i).checked=false;
 }

 if(i==0)
 {
   if(document.all("allbox").checked)
      document.all("email_select").checked=true;
   else
      document.all("email_select").checked=false;
 }
}

function check_one(el)
{
   if(!el.checked)
      document.all("allbox").checked=false;
}

function get_checked()
{
  checked_str="";
  for(i=0;i<document.all("email_select").length;i++)
  {

      el=document.all("email_select").item(i);
      if(el.checked)
      {  val=el.value;
         checked_str+=val + "|";
      }
  }

  if(i==0)
  {
      el=document.all("email_select");
      if(el.checked)
      {  val=el.value;
         checked_str+=val + "|";
      }
  }
  return checked_str;
}
function delete_tpl()
{
  delete_str=get_checked();
  if(delete_str=="")
  {
     alert("请至少选择一个模板。");
     return;
  }

  msg='确认要删除所选模板吗？';
  if(window.confirm(msg))
  {
	  
	  var url= contextPath + "/t9/project/project/act/T9ProjectAct/deleteModel.act";
		var param="?fileNames="+delete_str;
	  var rtJson=getJsonRs(url,param);
		if(rtJson.rtState == "0"){
			alert("删除成功！");
			location.reload();
		}else{
			alert(rtJson.rtMsrg);
		}
  }
}
</script>

<table border="0" width="100%" cellspacing="0" cellpadding="3" class="small">
  <tr>
    <td class="Big"><img src="../../images/project.gif" align="absmiddle"><span class="big3"> 项目模板管理</span>
    </td>
  </tr>
</table>
<br>
<div id="modelList"></div>
<div id="msrg">
</div>
</body>
</html>