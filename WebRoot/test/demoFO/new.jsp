<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>新建员工关怀信息</title>
<link rel="stylesheet" href = "<%=cssPath%>/style.css">
<link rel="stylesheet" href="<%=cssPath%>/cmp/Calendar.css" type="text/css" />
<script type="text/javascript" src="<%=contextPath %>/core/js/prototype.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/datastructs.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/sys.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/smartclient.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/Calendarfy.js"></script>
<link rel="stylesheet" href="<%=cssPath%>/cmp/Calendar.css" type="text/css" />
<script type="text/Javascript" src="<%=contextPath%>/core/js/cmp/select.js" ></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/orgselect.js" ></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/cmp/fck/fckeditor/fckeditor.js"></script> 
<script type="text/javascript"  src="<%=contextPath%>/core/js/cmp/Menu.js"></script>
<script type="text/javascript"  src="<%=contextPath%>/core/js/cmp/attachMenu.js"></script>
<script type="text/javascript"  src="<%=contextPath%>/subsys/oa/hr/manage/staffCare/js/staffCareLogic.js"></script>
<script type="text/javascript">
var fckContentStr = "";

function doInit(){
  getSecretFlag("HR_STAFF_CARE1","careType");
  setDate();
}


//日期
function setDate(){
  var date1Parameters = {
     inputId:'careDate',
     property:{isHaveTime:false}
     ,bindToBtn:'date1'
  };
  new Calendar(date1Parameters);
}

function doSubmit(){
  var oEditor = FCKeditorAPI.GetInstance('fileFolder');
  $("careContent").value = oEditor.GetXHTML();
  if(checkForm()){
    var url = "<%=contextPath%>/t9/test/demo/act/T9DemoAct/addFO.act";
    var rtJson = getJsonRs(url, $('form1').serialize());
    if(rtJson.rtState == "0"){
      alert("保存成功")
    }
  }
}

function checkForm(){
  if($("byCareStaffsDesc").value == ""){
    alert("被关怀员工不能为空！");
    $("byCareStaffsDesc").focus();
    return (false);
  }

  if($("careDate").value == ""){
    alert("关怀日期不能为空！");
    $("careDate").focus();
    return (false);
  }

  if($("participantsDesc").value == ""){
    alert("参与人不能为空！");
    $("participantsDesc").focus();
    return (false);
  }

  if($("careContent").value == ""){
    alert("关怀内容不能为空！");
    return (false);
  }

  var careFees = $("careFees").value;
  if(careFees){
    if(!checkRate(careFees)){
        alert("您填写的关怀开支费用格式错误，请输入正整数");
        $("careFees").focus();
        $("careFees").select();
      return (false);
    }
  }
  return true;
}

//判断正整数  
function checkRate(input){ 
  var re = /^[1-9]+[0-9]*]*$/;
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
    <td><img src="<%=imgPath %>/notify_new.gif" align="middle"><span class="big3"> 新建员工关怀信息</span>&nbsp;&nbsp;
    </td>
  </tr>
</table>
<br>
<form method="post" name="form1" id="form1">
  <table class="TableBlock" width="80%" align="center">
    <tr>
      <td nowrap class="TableData">关怀类型： </td>
      <td class="TableData" >
        <select name="careType" id="careType"  title="关怀类型可在“人力资源设置”->“HRMS代码设置”模块设置。">
          <option value="">关怀类型&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</option>
        </select>
      </td>
      <td nowrap class="TableData">关怀开支费用：</td>
      <td class="TableData">
        <input type="text" name="careFees" id="careFees" class="BigInput" size="15">(元)
      </td>
    </tr>
    <tr>
      <td nowrap class="TableData">被关怀员工：<font color="red">*</font> </td>
      <td class="TableData">
        <input type="hidden" name="byCareStaffs" id="byCareStaffs" value="" >
        <input type="text" name="byCareStaffsDesc" id="byCareStaffsDesc" class="BigStatic" readonly size="15" >
        <a href="javascript:;" class="orgAdd" onClick="selectUser(['byCareStaffs', 'byCareStaffsDesc'],null,null,1);">添加</a>
      </td>
      <td nowrap class="TableData">关怀日期：<font color="red">*</font> </td>
      <td class="TableData">
        <input type="text" name="careDate" id="careDate" size="11" maxlength="10"  class="BigInput" value="" readonly>
        <img id="date1" align="middle" src="<%=imgPath %>/calendar.gif" align="middle" border="0" style="cursor:pointer" >
      </td>
    </tr>
    <tr>
      <td nowrap class="TableData">关怀效果： </td>
      <td class="TableData" colspan=3>
        <textarea name="careEffects" id="careEffects" cols="78" rows="2" class="BigInput" value=""></textarea>
      </td>
    </tr> 
    <tr>
      <td nowrap class="TableData">参与人：<font color="red">*</font> </td>
      <td class="TableData" colspan=3>
        <input type="hidden" name="participants" id="participants" value="">
        <textarea cols="40" name="participantsDesc" id="participantsDesc" rows="2" style="overflow-y:auto;" class="BigStatic" wrap="yes" readonly></textarea>
        <a href="javascript:;" class="orgAdd" onClick="selectUser(['participants', 'participantsDesc'],null,null,1);">添加</a>
        <a href="javascript:;" class="orgClear" onClick="$('participants').value='';$('participantsDesc').value='';">清空</a>
      </td>
    </tr> 
    <tr id="EDITOR">
      <td class="TableData" colspan="4"> 关怀内容：<font color="red">*</font>
        <div>
         <script language=JavaScript>    
          var sBasePath = contextPath+'/core/js/cmp/fck/fckeditor/';
          var oFCKeditor = new FCKeditor( 'fileFolder' ) ;
          oFCKeditor.BasePath = sBasePath ;
          oFCKeditor.Height = 200;
          var sSkinPath = sBasePath + 'editor/skins/office2003/';
          oFCKeditor.Config['SkinPath'] = sSkinPath ;
          oFCKeditor.Config['PreloadImages'] =
                          sSkinPath + 'images/toolbar.start.gif' + ';' +
                          sSkinPath + 'images/toolbar.end.gif' + ';' +
                          sSkinPath + 'images/toolbar.buttonbg.gif' + ';' +
                          sSkinPath + 'images/toolbar.buttonarrow.gif' ;
          //oFCKeditor.Config['FullPage'] = true ;
          oFCKeditor.ToolbarSet = "fileFolder";
          oFCKeditor.Value = '' ;
          oFCKeditor.Create();
         </script>
        </div>
      </td>
    </tr>
    <tr align="center" class="TableControl">
      <td colspan=4 nowrap>
        <input type="hidden" name="careContent" id="careContent" value="">
        <input type="hidden" id="dtoClass" name="dtoClass" value="t9.test.demo.data.T9HrStaffCare">
        <input type="button" value="保存" onclick="doSubmit();" class="BigButton">
      </td>
    </tr>
  </table>
</form>

</body>
</html>