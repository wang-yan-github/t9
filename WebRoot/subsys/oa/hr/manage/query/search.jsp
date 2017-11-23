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
<title>查询结果</title>
<link rel="stylesheet" href = "<%=cssPath%>/style.css">
<link rel="stylesheet" href="<%=cssPath%>/page.css">
<script type="text/javascript" src="<%=contextPath %>/core/js/prototype.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/datastructs.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/sys.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/smartclient.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/cmp/page.js"></script>
<script type="text/javascript" src="<%=contextPath%>/subsys/oa/hr/manage/staffInfo/js/util.js"></script>
<script type="text/javascript" src="<%=contextPath%>/subsys/oa/hr/manage/staffInfo/js/staffInfoListLogic.js"></script>
<script type="text/javascript">
function doInit(){
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
	
	
	var url = "<%=contextPath%>/t9/subsys/oa/hr/manage/staffInfo/act/T9HrStaffInfoQueryAct/queryStaffInfoListJson.act?" + param;
	var cfgs = {
	    dataAction: url,
	    container: "listContainer",
	    sortIndex: 1,
	    sortDirect: "desc",
	    colums: [
	       {type:"selfdef", text:"选择", width: '5%', render:checkBoxRender},
	       {type:"hidden", name:"seqId", text:"顺序号", dataType:"int"},
	       {type:"data", name:"deptName",  width: '10%', text:"部门", render:infoCenterFunc},
	       {type:"data", name:"userId",  width: '10%', text:"OA用户名", render:infoCenterFunc},
	       {type:"data", name:"userName",  width: '10%', text:"姓名", render:infoCenterFunc},
	       {type:"data", name:"staffNo",  width: '10%', text:"编号", render:infoCenterFunc},
	       {type:"data", name:"workNo",  width: '10%', text:"工号", render:infoCenterFunc},
	       {type:"data", name:"staffBirth",  width: '10%', text:"年龄", render:staffAgeFunc},
	       {type:"data", name:"staffSex",  width: '10%', text:"性别", render:staffSexFunc},
	       {type:"selfdef", text:"操作", width: '10%',render:detailOpts}]
	  };
	  pageMgr = new T9JsPage(cfgs);
	  pageMgr.show();
	  var total = pageMgr.pageInfo.totalRecord;
	  if(total){
	    showCntrl('listContainer');
	    var mrs = " 共 " + total + " 条记录 ！";
	    showCntrl('delOpt');
	  }else{
	    WarningMsrg('无符合条件的记录！', 'msrg');
	  }
}



</script>
</head>
<body onload="doInit();">
	<table border="0" width="100%" cellspacing="0" cellpadding="3" class="small">
  <tr>
    <td class="Big"><img src="<%=imgPath %>/infofind.gif" align="middle">
    <span class="big3"> 人事档案查询结果</span><br>
   </td>
	</tr>
</table>
<br>
<div id="listContainer" style="display:none;width:100;"></div>
<div id="delOpt" style="display:none">
<table class="TableList" width="100%">
<tr class="TableControl">
      <td colspan="19">
         <input type="checkbox" name="checkAlls" id="checkAlls" onClick="checkAll(this);"><label for="checkAlls">全选</label> &nbsp;
         <a href="javascript:deleteInfo();" title="删除所选人事"><img src="<%=imgPath%>/delete.gif" align="middle">删除所选人事  </a>&nbsp;
      </td>
 </tr>
</table>
</div>

<div id="msrg"></div>
<br>
<div id="backDiv" style="display: " align="center">
<br>
  <input type="button" value="返回" class="BigButton" onclick="window.location.href='<%=contextPath %>/subsys/oa/hr/manage/query/query.jsp';">&nbsp;&nbsp;
</div>


</body>
</html>