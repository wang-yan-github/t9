<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header.jsp" %>
<%

	String staffDept = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("staffDept")));
	String staffName = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("staffName")));
	String staffEName = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("staffEName")));
	String workStatus = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("workStatus")));
	String staffNo = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("staffNo")));
	String workNo = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("workNo")));
	String staffSex = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("staffSex")));
	String staffCardNo = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("staffCardNo")));
	String birthdayMin = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("birthdayMin")));
	String birthdayMax = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("birthdayMax")));
	
	String ageMin = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("ageMin")));
	String ageMax = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("ageMax")));
	String staffNationality = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("staffNationality")));
	String staffNativePlace = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("staffNativePlace")));
	String workType = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("workType")));
	String staffDomicilePlace = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("staffDomicilePlace")));
	String staffMaritalStatus = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("staffMaritalStatus")));
	String staffHealth = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("staffHealth")));
	String staffPoliticalStatus = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("staffPoliticalStatus")));
	String administrationLevel = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("administrationLevel")));
	
	String staffOccupation = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("staffOccupation")));
	String computerLevel = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("computerLevel")));
	String staffHighestSchool = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("staffHighestSchool")));
	String staffHighestDegree = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("staffHighestDegree")));
	String staffMajor = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("staffMajor")));
	String graduationSchool = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("graduationSchool")));
	String jobPosition = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("jobPosition")));
	String presentPosition = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("presentPosition")));
	String graduationMin = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("graduationMin")));
	String graduationMax = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("graduationMax")));
	
	String joinPartyMin = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("joinPartyMin")));
	String joinPartyMax = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("joinPartyMax")));
	String beginningMin = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("beginningMin")));
	String beginningMax = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("beginningMax")));
	String employedMin = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("employedMin")));
	String employedMax = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("employedMax")));
	String workAgeMin = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("workAgeMin")));
	String workAgeMax = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("workAgeMax")));
	String jobAgeMin = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("jobAgeMin")));
	String jobAgeMax = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("jobAgeMax")));
	
	
	String leaveTypeMin = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("leaveTypeMin")));
	String leaveTypeMax = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("leaveTypeMax")));
	String foreignLanguage1 = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("foreignLanguage1")));
	String foreignLanguage2 = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("foreignLanguage2")));
	String foreignLanguage3 = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("foreignLanguage3")));
	String foreignLevel1 = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("foreignLevel1")));
	String foreignLevel2 = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("foreignLevel2")));
	String foreignLevel3 = T9Utility.encodeSpecial(T9Utility.null2Empty(request.getParameter("foreignLevel3")));


%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>导出内容</title>
<link  rel="stylesheet"  href  =  "<%=cssPath%>/cmp/ExchangeSelect.css">
<link  rel="stylesheet"  href  ="<%=cssPath%>/style.css">
<script  type="text/Javascript"  src="<%=contextPath%>/core/js/datastructs.js"  ></script>
<script  type="text/Javascript"  src="<%=contextPath%>/core/js/sys.js"  ></script>
<script  type="text/Javascript"  src="<%=contextPath%>/core/js/prototype.js"  ></script>
<script  type="text/Javascript"  src="<%=contextPath%>/core/js/smartclient.js"  ></script>
<script  type="text/Javascript"  src="<%=contextPath%>/core/js/cmp/ExchangeSelect1.0.js"  ></script>
<script type="text/javascript">
var selectbox;

var param = "";
param = "staffDept=" + encodeURIComponent("<%=staffDept%>");
param += "&staffName=" + encodeURIComponent("<%=staffName%>");
param += "&staffEName=" + encodeURIComponent("<%=staffEName%>");
param += "&workStatus=" + encodeURIComponent("<%=workStatus%>");
param += "&staffNo=" + encodeURIComponent("<%=staffNo%>");
param += "&workNo=" + encodeURIComponent("<%=workNo%>");
param += "&staffSex=" + encodeURIComponent("<%=staffSex%>");
param += "&staffCardNo=" + encodeURIComponent("<%=staffCardNo%>");
param += "&birthdayMin=" + encodeURIComponent("<%=birthdayMin%>");
param += "&birthdayMax=" + encodeURIComponent("<%=birthdayMax%>");

param += "&ageMin=" + encodeURIComponent("<%=ageMin%>");
param += "&ageMax=" + encodeURIComponent("<%=ageMax%>");
param += "&staffNationality=" + encodeURIComponent("<%=staffNationality%>");
param += "&staffNativePlace=" + encodeURIComponent("<%=staffNativePlace%>");
param += "&workType=" + encodeURIComponent("<%=workType%>");
param += "&staffDomicilePlace=" + encodeURIComponent("<%=staffDomicilePlace%>");
param += "&staffMaritalStatus=" + encodeURIComponent("<%=staffMaritalStatus%>");
param += "&staffHealth=" + encodeURIComponent("<%=staffHealth%>");
param += "&staffPoliticalStatus=" + encodeURIComponent("<%=staffPoliticalStatus%>");
param += "&administrationLevel=" + encodeURIComponent("<%=administrationLevel%>");

param += "&staffOccupation=" + encodeURIComponent("<%=staffOccupation%>");
param += "&computerLevel=" + encodeURIComponent("<%=computerLevel%>");
param += "&staffHighestSchool=" + encodeURIComponent("<%=staffHighestSchool%>");
param += "&staffHighestDegree=" + encodeURIComponent("<%=staffHighestDegree%>");
param += "&staffMajor=" + encodeURIComponent("<%=staffMajor%>");
param += "&graduationSchool=" + encodeURIComponent("<%=graduationSchool%>");
param += "&jobPosition=" + encodeURIComponent("<%=jobPosition%>");
param += "&presentPosition=" + encodeURIComponent("<%=presentPosition%>");
param += "&graduationMin=" + encodeURIComponent("<%=graduationMin%>");
param += "&graduationMax=" + encodeURIComponent("<%=graduationMax%>");

param += "&joinPartyMin=" + encodeURIComponent("<%=joinPartyMin%>");
param += "&joinPartyMax=" + encodeURIComponent("<%=joinPartyMax%>");
param += "&beginningMin=" + encodeURIComponent("<%=beginningMin%>");
param += "&beginningMax=" + encodeURIComponent("<%=beginningMax%>");
param += "&employedMin=" + encodeURIComponent("<%=employedMin%>");
param += "&employedMax=" + encodeURIComponent("<%=employedMax%>");
param += "&workAgeMin=" + encodeURIComponent("<%=workAgeMin%>");
param += "&workAgeMax=" + encodeURIComponent("<%=workAgeMax%>");
param += "&jobAgeMin=" + encodeURIComponent("<%=jobAgeMin%>");
param += "&jobAgeMax=" + encodeURIComponent("<%=jobAgeMax%>");

param += "&leaveTypeMin=" + encodeURIComponent("<%=leaveTypeMin%>");
param += "&leaveTypeMax=" + encodeURIComponent("<%=leaveTypeMax%>");
param += "&foreignLanguage1=" + encodeURIComponent("<%=foreignLanguage1%>");
param += "&foreignLanguage2=" + encodeURIComponent("<%=foreignLanguage2%>");
param += "&foreignLanguage3=" + encodeURIComponent("<%=foreignLanguage3%>");
param += "&foreignLevel1=" + encodeURIComponent("<%=foreignLevel1%>");
param += "&foreignLevel2=" + encodeURIComponent("<%=foreignLevel2%>");
param += "&foreignLevel3=" + encodeURIComponent("<%=foreignLevel3%>");



function doInit(){
	selected = [];
	disSelected = [{value:'staffName',text:'姓名'},
	 	          	{value:'staffEName',text:'英文名'},
	 	          	{value:'deptId',text:'部门'},
	 	          	{value:'staffSex',text:'性别'},
	 	          	{value:'staffNo',text:'编号'},
	 	          	{value:'workNo',text:'工号'},
	 	          	{value:'staffCardNo',text:'身份证号码'},
	 	          	{value:'staffBirth',text:'出生日期'},
	 	          	{value:'staffAge',text:'年龄'},
	 	          	{value:'staffNativePlace',text:'籍贯'},
	 	          	{value:'staffNationality',text:'民族'},
	 	          	{value:'staffMaritalStatus',text:'婚姻状况'},
	 	          	{value:'staffPoliticalStatus',text:'政治面貌'},
	 	          	{value:'workStatus',text:'在职状态'},
	 	          	{value:'joinPartyTime',text:'入党时间'},
	 	          	{value:'staffPhone',text:'联系电话'},
	 	          	{value:'staffMobile',text:'手机号码'},
	 	          	{value:'staffLittleSmart',text:'小灵通'},
	 	          	{value:'staffMsn',text:'MSN'},
	 	          	{value:'staffQq',text:'QQ'},
	 	          	{value:'staffEmail',text:'电子邮件'},
	 	          	{value:'homeAddress',text:'家庭地址'},
	 	          	{value:'jobBeginning',text:'参加工作时间'},
	 	          	{value:'otherContact',text:'其他联系方式'},
	 	          	{value:'workAge',text:'总工龄'},
	 	          	{value:'staffHealth',text:'健康状况'},
	 	          	{value:'staffDomicilePlace',text:'户口所在地'},
	 	          	{value:'staffType',text:'户口类别'},
	 	          	{value:'datesEmployed',text:'入职时间'},
	 	          	{value:'staffHighestSchool',text:'学历'},
	 	          	{value:'staffHighestDegree',text:'学位'},
	 	          	{value:'graduationDate',text:'毕业时间'},
	 	          	{value:'staffMajor',text:'专业'},
	 	          	{value:'graduationSchool',text:'毕业院校'},
	 	          	{value:'computerLevel',text:'计算机水平'},
	 	          	{value:'foreignLanguage1',text:'外语语种1'},
	 	          	{value:'foreignLanguage2',text:'外语语种2'},
	 	          	{value:'foreignLanguage3',text:'外语语种3'},
	 	          	{value:'foreignLevel1',text:'外语水平1'},
	 	          	{value:'foreignLevel2',text:'外语水平2'},
	 	          	{value:'foreignLevel3',text:'外语水平3'},
	 	          	{value:'staffSkills',text:'特长'},
	 	          	{value:'workType',text:'工种'},
	 	          	{value:'administrationLevel',text:'行政级别'},
	 	          	{value:'staffOccupation',text:'员工类型'},
	 	          	{value:'jobPosition',text:'职务'},
	 	          	{value:'presentPosition',text:'职称'},
	 	          	{value:'jobAge',text:'本单位工龄'},
	 	          	{value:'beginSalsryTime',text:'起薪时间'},
	 	          	{value:'leaveType',text:'年休假'},
	 	          	{value:'resume',text:'简历'},
	 	          	{value:'surety',text:'担保记录'},
	 	          	{value:'certificate',text:'职务情况'},
	 	          	{value:'insure',text:'社保缴纳情况'},
	 	          	{value:'bodyExamim',text:'体检记录'},
	 	          	{value:'remark',text:'备 注'}
	 	          
 	];
	new ExchangeSelectbox({containerId:'selectItemDiv' 
		,selectedArray:selected 
		,disSelectedArray:disSelected 
		,isOneLevel:false 
		 ,isSort:true
		,selectedChange:exchangeHandler 
		});

	
}
function exchangeHandler(ids){ 
	//alert(ids);
	if(ids){
		$("fieldName").value = ids;
	}
	
}
function exreport(){
	//var query = $("form1").serialize();
	//alert($("fieldName").value);
	var selectValue = $("fieldName").value;
	location.href = "<%=contextPath%>/t9/subsys/oa/hr/manage/staffInfo/act/T9HrStaffInfoQueryAct/exportToCSV.act?" + param + "&selectValue=" + selectValue;
	//var url = "<%=contextPath%>/t9/subsys/oa/hr/manage/staffInfo/act/T9HrStaffInfoQueryAct.act?" + param + "&selectValue=" + selectValue;
	//alert(url);
	
}


</script>
</head>
<body onload="doInit();">

<table border="0" width="100%" cellspacing="0" cellpadding="3" class="small">
  <tr>
    <td class="Big"><img src="<%=imgPath %>/send.gif" width="18" HEIGHT="18"><span class="big3"> 导出EXCEL人员档案</span>
    </td>
  </tr>
</table>
<br>


<form action="" method="post" name="form1" id="form1">
 <table align="center" width="95%" class="TableBlock">

     <tr>
      <td nowrap class="TableHeader" colspan="4">
         <img src="<%=imgPath %>/green_arrow.gif"> &nbsp;导出内容
      </td>
    </tr>
  <tr>
  <td nowrap class="TableData" colspan="4" align="center"><br>
  	<div id="selectItemDiv"></div>
</td>
  </tr>
    <tfoot align="center" class="TableFooter">
    <td nowrap colspan="4" align="center">
        <input type="reset" value="导出" class="BigButton" onClick="exreport()">&nbsp;&nbsp;
        <input type="button" value="返回" class="BigButton" onClick="window.location.href='<%=contextPath %>/subsys/oa/hr/manage/query/query.jsp';">
        <input type="hidden" name="fieldName" id="fieldName" value="">
      </td>
    </tfoot>
  </table>
</form>





</body>
</html>