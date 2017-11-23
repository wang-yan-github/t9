<%@ page language="java" import="java.util.*,java.sql.*,t9.core.data.T9RequestDbConn,t9.core.global.T9BeanKeys" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
   Calendar cl =Calendar.getInstance();
   int curYear = cl.get(Calendar.YEAR);
   int month = cl.get(Calendar.MONTH);
   month = month + 1;
   int nextYear = curYear + 1;
   T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
   Connection dbConn = requestDbConn.getSysDbConn();
   T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
   T9PersonLogic pl = new T9PersonLogic();
   int userId = user.getSeqId();
   String deptId = request.getParameter("deptId");
   
   if(deptId==null||deptId.equals("")){
     deptId = user.getDeptId() + "";
   }
%>
<%@page import="t9.core.funcs.person.logic.T9PersonLogic"%>
<%@ include file="/core/inc/header.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>财务预算管理</title>
<link rel="stylesheet" href="<%=cssPath%>/style.css">
<link rel="stylesheet" href="<%=cssPath%>/cmp/Calendar.css">
<script type="text/javascript" src="<%=contextPath %>/core/js/prototype.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/tab.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/datastructs.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/sys.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/smartclient.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/Calendarfy.js"></script>
<script type="text/javascript"> 
var curYear = '<%=curYear%>';
var nextYear = '<%=nextYear%>';
var deptId = '<%=deptId%>';
var month = '<%=month%>';
function addBudget(year,deptId,type) {
  if(type==1&&month<8){
    alert("请在 八月一号以后再填报下年预算！故意放开——测试用");
    // return;
  }
  myleft = (screen.availWidth-800)/2;
  window.open("<%=contextPath%>/subsys/oa/finance/budget/addBudget.jsp?year="+year+"&deptId="+deptId,"","status=0,toolbar=no,menubar=no,location=no,scrollbars=yes,resizable=yes,width=800,height=600,left="+myleft+",top=50");
}
function set_budget(year,deptId,total){
  var requestUrl = "<%=contextPath%>/t9/subsys/oa/finance/act/T9BudgetDeptTotalAct/setBudget.act?year="+year+"&deptId="+deptId+"&total="+total;
  var rtJson = getJsonRs(requestUrl);
  if(rtJson.rtState == "1"){
    alert(rtJson.rtMsrg); 
    return ;
  }
  alert("操作成功！");
  window.location.reload();
}
 
function showBudget(year,deptId) {
  myleft = (screen.availWidth-800)/2;
  window.open("<%=contextPath%>/subsys/oa/finance/budget/budgetapply.jsp?year="+year+"&deptId="+deptId,"","status=0,toolbar=no,menubar=no,location=no,scrollbars=yes,resizable=yes,width=800,height=600,left="+myleft+",top=50");
}
function show_all(year,deptId) {
  myleft = (screen.availWidth-800)/2;
  window.open("<%=contextPath%>/subsys/oa/finance/budget/detail/index.jsp?year="+year+"&deptId="+deptId,"","status=0,toolbar=no,menubar=no,location=no,scrollbars=yes,resizable=yes,width=800,height=600,left="+myleft+",top=50");
}

function totalDetail(year,deptId) {
  myleft = (screen.availWidth-800)/2;
  window.open("<%=contextPath%>/subsys/oa/finance/budget/detail/charge.jsp?year="+year+"&deptId="+deptId,"","status=0,toolbar=no,menubar=no,location=no,scrollbars=yes,resizable=yes,width=800,height=600,left="+myleft+",top=50");
}
function doOnload(){
 
  var table = new Element('table',{"class":"TableList" ,"width":"75%"}).update("<tbody id='tbody'>"
      +"<tr class='TableHeader'>"
      +"<td nowrap align='center' width='10%'>年份</td>"
      +"<td nowrap align='center' width='23%'>实时预算金额</td>"
      +"<td nowrap align='center' width='22%'>实时使用金额</td>"
      +"<td nowrap align='center' width='45%'>操作</td>"
      +"</tr></tobdy>");
  $("bodyDiv").appendChild(table);

  //得到部门的所有年份
  var yearPrcs = selectAllYearBudgetByDept(deptId);
  for(var i =0 ;i<yearPrcs.length ; i++){
    var prc = yearPrcs[i];
    var year = prc.year;
    var prc = getUseTotal(year,deptId);
    var DeptTotal =  prc.deptTotal;
    var useMoney = prc.useMoney;
    var isDpetTotal = prc.isDpetTotal;
    var isCurOverStr = "";
    if(parseFloat(DeptTotal)<parseFloat(useMoney)){
      isCurOverStr = "<font color='red'>超支！</font>&nbsp;&nbsp;" 
    }
    var validateBudget = "";
    
    if(isDpetTotal=='1'){
      validateBudget = "<a href='javascript:;' onClick='set_budget("+year+","+deptId+",\""+DeptTotal+"\");'>确定额度</a>&nbsp;&nbsp;&nbsp; ";
    }
    var curTr = new Element('tr',{"class":"TableLine1"});
    $("tbody").appendChild(curTr);
    curTr.update("<td nowrap align='center'>"+year+"</td>"
        +"<td nowrap align='center'>"+insertKiloSplit(DeptTotal,2)+"</td>"
        +"<td nowrap align='right'><a href='javascript:totalDetail("+year+","+deptId+");'>"+insertKiloSplit(useMoney,2)+ "</a>" + isCurOverStr +"</td>"
        +"<td nowrap align='right'>"
        +validateBudget+""
        +"<a href='javascript:;' onClick='showBudget("+year+","+deptId+");'>预算登记详细</a>&nbsp;"
        +"<a href='javascript:;' onClick='show_all("+year+","+deptId+");'>預算使用详細</a>"
        +"</td>");  
  }
}
function getUseTotal(year,deptId){
  var requestUrl = "<%=contextPath%>/t9/subsys/oa/finance/act/T9BudgetDeptTotalAct/selectTotal.act?year="+year+"&deptId="+deptId;
  var rtJson = getJsonRs(requestUrl);
  if(rtJson.rtState == "1"){
    alert(rtJson.rtMsrg); 
    return ;
  }
  var prc = rtJson.rtData;
  return prc;
} 
//得到每个部门的所有年份的详细预算信息
function selectAllYearBudgetByDept(deptId){
  var requestUrl = "<%=contextPath%>/t9/subsys/oa/finance/act/T9BudgetApplyAct/selectAllYearBudgetByDept.act?deptId="+deptId;
  var rtJson = getJsonRs(requestUrl);
  if(rtJson.rtState == "1"){
    alert(rtJson.rtMsrg); 
    return ;
  }
  var prc = rtJson.rtData;
  return prc;
}
</script>
 
</head>
<body  topmargin="5" onload="doOnload();">
<table border="0" width="100%" cellspacing="0" cellpadding="3" class="small">
  <tr>
    <td class="Big"><img src="<%=contextPath %>/core/styles/imgs/menuIcon/@finance.gif" HEIGHT="20"><span class="big3"> 财务预算管理
</span>
    </td>
  </tr>
</table>
<br>
<table border="0" width="100%" cellspacing="0" cellpadding="3" class="small">
  <tr>
    <td class="Big" align='center'><span class="big3"> 当前年份<%=curYear %></span>
    </td>
  </tr>
</table>
<br>
<div align="center" id="bodyDiv">
</div>
<br></br>
<div align="center" >
<input type="button" value="关闭" class="" onclick="window.close();"></input>
</div>
<br>
</body>
</html>