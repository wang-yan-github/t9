<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header.jsp" %>
<%
String seqId =(String)request.getParameter("projId")==null?"0":(String)request.getParameter("projId");
%> 
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Insert title here</title><link rel="stylesheet" href = "<%=cssPath%>/style.css">
<link rel="stylesheet" href = "<%=cssPath%>/cmp/ExchangeSelect.css">
<script type="text/Javascript" src="<%=contextPath%>/core/js/datastructs.js" ></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/sys.js" ></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/prototype.js" ></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/smartclient.js" ></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/cmp/select.js" ></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/cmp/DTree1.0.js"></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/orgselect.js" ></script>
<script type="text/javascript">
var seqId='<%=seqId%>';
function doInit(){
	document.getElementById("seqId").value = seqId;
	var url="<%=contextPath%>/t9/project/project/act/T9ProjectAct/getCostTypeAndValue.act?seqId="+seqId;
	var rtJson=getJsonRs(url);
	if(rtJson.rtState==0){
		var typeList= "<table class=\"TableBlock\" align=\"center\" width=\"450\" id=\"tab\">"
					+ "<tr class=\"TableHeader\">"
					+ "<td width=\"60%\">费用类别</td>"
					+ "<td >金额（人民币）</td>"
					+ "</tr>";
		var str="";
		var matchs="onkeypress=\"if(!this.value.match(/^[\+\-]?\d*?\.?\d*?$/))this.value=this.t_value;else this.t_value=this.value;if(this.value.match(/^(?:[\+\-]?\d+(?:\.\d+)?)?$/))this.o_value=this.value\" onkeyup=\"if(!this.value.match(/^[\+\-]?\d*?\.?\d*?$/))this.value=this.t_value;else this.t_value=this.value;if(this.value.match(/^(?:[\+\-]?\d+(?:\.\d+)?)?$/))this.o_value=this.value\"";
		for(var i=1;i<rtJson.rtData.size();i++){
			str += "<tr class=\"TableData\">"
				+ "<td align=\"center\" class=\"TableData\">"+rtJson.rtData[i].codeName+"</td>"
				+ "<td class=\"TableData\" align=\"center\"><input type=\"text\" id=\""+rtJson.rtData[i].codeSeqId+"\"  class=\"BigInput\" style=\"text-align:right\"" 
				+ "onkeypress=\"if(!this.value.match(/^[\\\+\\\-]?\d*?\\\.?\\\d*?$/))this.value=this.t_value;else this.t_value=this.value;if(this.value.match(/^(?:[\\\+\\\-]?\d+(?:\\\.\\\d+)?)?$/))this.o_value=this.value\"" 
				+ "onkeyup=\"if(!this.value.match(/^[\\\+\-]?\\\d*?\\\.?\\\d*?$/))this.value=this.t_value;else this.t_value=this.value;if(this.value.match(/^(?:[\\\+\\\-]?\d+(?:\\\.\\\d+)?)?$/))this.o_value=this.value\">"
				+"</td></tr>";
		}
		var subtr="<tr>"
		    +"<td class=\"TableControl\" colspan=\"6\" align=\"center\">"
		        +"<input type=\"button\" value=\"保存\" class=\"BigButton\" onclick=\"commitItem();\">&nbsp;&nbsp;"
		    +"</td>"
		  +"</tr>" 
		$("styleList").innerHTML= typeList + str + subtr + "</table>";
		var costTypes = rtJson.rtData[0].costType.split(",");
		var costValues = rtJson.rtData[0].codeValue.split(",");
		for(var i=0;i<costTypes.length;i++){
			if(!checkIsExist(costTypes[i])){
				continue;
			}
		if(costTypes[i]!="" && costTypes[i]!="null"){
		$(costTypes[i]).value=costValues[i];
			}
			}
	}else{
		alert(rtJson.rtMsrg); 	
	}
}
/* function checkText(){
	alert(this.t_value);
	if(!this.value.match(/^[\+\-]?\d*?\.?\d*?$/))this.value=this.t_value;
	else this.t_value=this.value;
	if(this.value.match(/^(?:[\+\-]?\d+(?:\.\d+)?)?$/))this.o_value=this.value;
}
 */
 
function checkIsExist(costId){
	 var url="<%=contextPath%>/t9/project/project/act/T9ProjectAct/checkIsExist.act?costId="+costId;
	 var rtJson=getJsonRs(url);
	 if (rtJson.rtState == "0") {
		 if(rtJson.rtData.flag=="1"){
			 return true;
		 }else{
			 return false;
		 }
	 }else{
		 return false;
	 }
	 
 }
function commitItem() {
	var tab=$("tab");
	for(i=1;i<tab.rows.length-1;i++)
	{
		if(tab.rows[i].cells[1].firstChild.value!="")
 		  $("costType").value += tab.rows[i].cells[1].firstChild.id+",";
 		  $("costMoney").value += tab.rows[i].cells[1].firstChild.value+",";
	}
	    var url = "<%=contextPath%>/t9/project/project/act/T9ProjectAct/updateCost.act";
	    var rtJson = getJsonRs(url, mergeQueryString($("form1")));
	    if (rtJson.rtState == "0") {
	      alert(rtJson.rtMsrg);
	      window.location.reload();
	      document.getElementById("seqId").value = seqId;
	    }else {
	      alert(rtJson.rtMsrg); 
	}
}
</script>
</head>
<body onload="doInit()">

<table border="0" width="100%" cellspacing="0" cellpadding="3" class="small">
  <tr>
    <td class="Big"><img src="<%=contextPath %>/core/funcs/system/filefolder/images/edit.gif" width="24" height="24" align="absmiddle"><span class="big3"> 项目经费设定</span><br>
    </td>
  </tr>
</table>
<br>
<br>
<div id="styleList"></div>
<form name="form1" id="form1" method="post" action="">
<input type="hidden" value="" id="costType" name="costType"></input>
<input type="hidden" value="" id="costMoney" name="costMoney"></input>
<input type="hidden" value="" id="seqId" name="seqId"></input>
</form>
</body>
</html>