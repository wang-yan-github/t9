<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header.jsp"%>
<%@ page import="t9.core.funcs.person.data.T9Person"%>
<!DOCTYPE  html  PUBLIC  "-//W3C//DTD  HTML  4.01  Transitional//EN"  "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html;  charset=UTF-8">
<title>模块管理</title>
<link rel="stylesheet" href="<%=cssPath%>/style.css">
<link rel="stylesheet" href="<%=cssPath%>/page.css" />
<script type="text/javascript" src="<%=contextPath%>/core/js/prototype.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/datastructs.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/sys.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/smartclient.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/cmp/page.js"></script>
<script type="text/javascript">
  //seq:
  //[T9DbRecord [fieldCnt=8, fieldIndexMap={3=modulePos, 2=viewType, 1=fileName, 0=seqId, 7=priv_id, 6=dept_id, 5=user_id, 4=type}, 
  //queryFields={dept_id=0, priv_id=null, fileName=OA知道.js, modulePos=r, user_id=null, type=0, viewType=1, seqId=1}], 
  //T9DbRecord [fieldCnt=8, fieldIndexMap={3=modulePos, 2=viewType, 1=fileName, 0=seqId, 7=priv_id, 6=dept_id, 5=user_id, 4=type}, 
  //queryFields={dept_id=0, priv_id=null, fileName=便签.js, modulePos=null, user_id=null, type=0, viewType=1, seqId=2}], 
  //T9DbRecord [fieldCnt=8, fieldIndexMap={3=modulePos, 2=viewType, 1=fileName, 0=seqId, 7=priv_id, 6=dept_id, 5=user_id, 4=type}, 
  //queryFields={dept_id=0, priv_id=null, fileName=公告通知.js, modulePos=null, user_id=null, type=0, viewType=1, seqId=3}], 
  //T9DbRecord [fieldCnt=8, fieldIndexMap={3=modulePos, 2=viewType, 1=fileName, 0=seqId, 7=priv_id, 6=dept_id, 5=user_id, 4=type},
   //queryFields={dept_id=0, priv_id=null, fileName=内部邮件.js, modulePos=null, user_id=null, type=0, viewType=1, seqId=4}], 
   //T9DbRecord [fieldCnt=8, fieldIndexMap={3=modulePos, 2=viewType, 1=fileName, 0=seqId, 7=priv_id, 6=dept_id, 5=user_id, 4=type}, queryFields={dept_id=0, priv_id=null, fileName=外出人员.js, modulePos=null, user_id=null, type=0, viewType=1, seqId=8}], T9DbRecord [fieldCnt=8, fieldIndexMap={3=modulePos, 2=viewType, 1=fileName, 0=seqId, 7=priv_id, 6=dept_id, 5=user_id, 4=type}, queryFields={dept_id=0, priv_id=null, fileName=待办工作.js, modulePos=null, user_id=null, type=0, viewType=1, seqId=10}], T9DbRecord [fieldCnt=8, fieldIndexMap={3=modulePos, 2=viewType, 1=fileName, 0=seqId, 7=priv_id, 6=dept_id, 5=user_id, 4=type}, queryFields={dept_id=0, priv_id=null, fileName=搜索.js, modulePos=null, user_id=null, type=0, viewType=3, seqId=11}], T9DbRecord [fieldCnt=8, fieldIndexMap={3=modulePos, 2=viewType, 1=fileName, 0=seqId, 7=priv_id, 6=dept_id, 5=user_id, 4=type}, queryFields={dept_id=0, priv_id=null, fileName=文件柜.js, modulePos=null, user_id=null, type=0, viewType=1, seqId=12}], T9DbRecord [fieldCnt=8, fieldIndexMap={3=modulePos, 2=viewType, 1=fileName, 0=seqId, 7=priv_id, 6=dept_id, 5=user_id, 4=type}, queryFields={dept_id=0, priv_id=null, fileName=新闻.js, modulePos=null, user_id=null, type=0, viewType=1, seqId=13}], T9DbRecord [fieldCnt=8, fieldIndexMap={3=modulePos, 2=viewType, 1=fileName, 0=seqId, 7=priv_id, 6=dept_id, 5=user_id, 4=type}, queryFields={dept_id=0, priv_id=null, fileName=日历.js, modulePos=null, user_id=null, type=0, viewType=1, seqId=14}], T9DbRecord [fieldCnt=8, fieldIndexMap={3=modulePos, 2=viewType, 1=fileName, 0=seqId, 7=priv_id, 6=dept_id, 5=user_id, 4=type}, queryFields={dept_id=0, priv_id=null, fileName=日程安排.js, modulePos=null, user_id=null, type=0, viewType=1, seqId=15}], T9DbRecord [fieldCnt=8, fieldIndexMap={3=modulePos, 2=viewType, 1=fileName, 0=seqId, 7=priv_id, 6=dept_id, 5=user_id, 4=type}, queryFields={dept_id=0, priv_id=null, fileName=时钟.js, modulePos=null, user_id=null, type=0, viewType=3, seqId=16}], T9DbRecord [fieldCnt=8, fieldIndexMap={3=modulePos, 2=viewType, 1=fileName, 0=seqId, 7=priv_id, 6=dept_id, 5=user_id, 4=type}, queryFields={dept_id=0, priv_id=null, fileName=网络硬盘.js, modulePos=null, user_id=null, type=0, viewType=1, seqId=17}], T9DbRecord [fieldCnt=8, fieldIndexMap={3=modulePos, 2=viewType, 1=fileName, 0=seqId, 7=priv_id, 6=dept_id, 5=user_id, 4=type}, queryFields={dept_id=0, priv_id=null, fileName=天气.js, modulePos=null, user_id=null, type=0, viewType=3, seqId=216}], T9DbRecord [fieldCnt=8, fieldIndexMap={3=modulePos, 2=viewType, 1=fileName, 0=seqId, 7=priv_id, 6=dept_id, 5=user_id, 4=type}, queryFields={dept_id=0, priv_id=null, fileName=图片新闻.js, modulePos=null, user_id=null, type=0, viewType=1, seqId=217}], T9DbRecord [fieldCnt=8, fieldIndexMap={3=modulePos, 2=viewType, 1=fileName, 0=seqId, 7=priv_id, 6=dept_id, 5=user_id, 4=type}, queryFields={dept_id=0, priv_id=null, fileName=在办发文.js, modulePos=null, user_id=null, type=0, viewType=1, seqId=218}], T9DbRecord [fieldCnt=8, fieldIndexMap={3=modulePos, 2=viewType, 1=fileName, 0=seqId, 7=priv_id, 6=dept_id, 5=user_id, 4=type}, queryFields={dept_id=0, priv_id=null, fileName=待办发文.js, modulePos=null, user_id=null, type=0, viewType=1, seqId=219}], T9DbRecord [fieldCnt=8, fieldIndexMap={3=modulePos, 2=viewType, 1=fileName, 0=seqId, 7=priv_id, 6=dept_id, 5=user_id, 4=type}, queryFields={dept_id=0, priv_id=null, fileName=待办公文.js, modulePos=null, user_id=null, type=0, viewType=1, seqId=220}], T9DbRecord [fieldCnt=8, fieldIndexMap={3=modulePos, 2=viewType, 1=fileName, 0=seqId, 7=priv_id, 6=dept_id, 5=user_id, 4=type}, queryFields={dept_id=0, priv_id=null, fileName=工作日志.js, modulePos=null, user_id=null, type=0, viewType=1, seqId=221}], T9DbRecord [fieldCnt=8, fieldIndexMap={3=modulePos, 2=viewType, 1=fileName, 0=seqId, 7=priv_id, 6=dept_id, 5=user_id, 4=type}, queryFields={dept_id=0, priv_id=null, fileName=周活动安排.js, modulePos=null, user_id=null, type=0, viewType=1, seqId=222}]]
var pageMgr = "";
function doInit(){
  
  var url = contextPath + "/t9/core/funcs/portal/act/T9PortAct/listAllPorts.act";
  var cfgs = {
    dataAction: url,
    container: "pageData",
    colums: [
       {type:"text", width: "10%", align: "center", name: "seqId", text:'<input type="checkbox" onclick="checkAll(this)">', render: checkboxRender},
       {type:"text", width: "20%", name: "fileName", text: "模块名称", align:'center', render: nameRender},
       {type:"text", width: "20%", name: "viewType", text: "显示状态(经典界面)", align:'center', render: stateRender, sortDef:{type:0, direct:"asc"}},
       {type:"text", width: "20%", name: "modulePos", text: "默认位置(经典界面)", align:'center', render: posRender, sortDef:{type:0, direct:"asc"}},
       {type:"hidden", name: "type", render: posRender},
       {type:"hidden", name: "user_id", render: optRender},
       {type:"hidden", name: "dept_id", render: optRender},
       {type:"hidden", name: "priv_id", render: optRender},
       {type:"selfdef", width: "420", text: "经典界面相关操作", align:'left', render: optRender}
    ]
  };
  pageMgr = new T9JsPage(cfgs);
  pageMgr.show();

  if (pageMgr.getRecordCnt() <= 0) {
    $("opt").hide();
    $("noData").show();
  }
}

function nameRender(cellData, recordIndex, columInde) {
  var type = this.getCellData(recordIndex,"type");
  var seqId = this.getCellData(recordIndex,"seqId");
  var temp = "";
  if(type && type=="1"){
      temp += '<a href="/t9/core/funcs/portal/portlet/Add.jsp?seqId=';
      temp += seqId;
      temp += '"><span>';
      temp += cellData && cellData.replace('.js', '');
      temp += '</span></a>';
  }else{
      temp = cellData && cellData.replace(".js", "");
  }
  return temp;
}

function stateRender(cellData, recordIndex, columInde) {
  var i = cellData * 1;
  if (!i || i < 1 || i > 4) {
    i = 3;
  }
  return [
    '<span>用户可选</span>',
    '<span style="color: red">用户必选</span>',
    '<span style="color: gray">暂停显示</span>'
  ][i - 1];
}

function posRender(cellData, recordIndex, columInde) {
  return cellData == "r" ? "右侧" : "左侧";
}

function optRender(cellData, recordIndex, columInde) {
  var seqId = this.getCellData(recordIndex,"seqId");
  var userId = this.getCellData(recordIndex,"user_id");
  var type = this.getCellData(recordIndex,"type");
  if(type && type == 1){
  return [
          "&nbsp;&nbsp;&nbsp;",
          '<a class="" href="javascript:void(0)" onclick="setViewType(1, ' + seqId + ')"><span>可选</span></a>',
          '<a class="" href="javascript:void(0)" onclick="setViewType(2, ' + seqId + ')"><span>必选</span></a>',
          '<a class="" href="javascript:void(0)" onclick="setViewType(3, ' + seqId + ')"><span>暂停显示</span></a>',
          '<a class="" href="javascript:void(0)" onclick="setPosL(' + seqId + ')"><span>设置为左侧</span></a>',
          '<a class="" href="javascript:void(0)" onclick="setPosR(' + seqId + ')"><span>设置为右侧</span></a>'
        ].join("&nbsp;&nbsp;");
  }else{
  return [
          "&nbsp;&nbsp;&nbsp;",
          '<a class="" href="javascript:void(0)" onclick="setViewType(1, ' + seqId + ')"><span>可选</span></a>',
          '<a class="" href="javascript:void(0)" onclick="setViewType(2, ' + seqId + ')"><span>必选</span></a>',
          '<a class="" href="javascript:void(0)" onclick="setViewType(3, ' + seqId + ')"><span>暂停显示</span></a>',
          '<a class="" href="javascript:void(0)" onclick="add4All(' + seqId + ')"><span>为所有用户添加此模块</span></a>',
          '<a class="" href="javascript:void(0)" onclick="setPosL(' + seqId + ')"><span>设置为左侧</span></a>',
          '<a class="" href="javascript:void(0)" onclick="setPosR(' + seqId + ')"><span>设置为右侧</span></a>'
        ].join("&nbsp;&nbsp;");
  }
}

function checkboxRender(cellData, recordIndex, columInde) {
  return "<input type=\"checkbox\" value=\"" + cellData + "\">"
}

function importPorts() {
  $('importForm').action = contextPath + "/t9/core/funcs/portal/act/T9PortAct/importPorts.act";
  $('importForm').submit();
}

function showImport(id) {
  $('importForm').show();
  if (id) {
    $(id).insert($('importForm'));
  }
}

function del() {
  if (!confirm("确定要删除选定模块吗?")) {
    return;
  }
  var c = $$("#pageData input['type'='checkbox']") || [];
  if (c.length < 1) {
    alert("请选择至少一个模块进行删除操作!");
    return;
  }
  var s = "";
  c.each(function(e, i) {
    if (e.checked) {
      s += e.value + ",";
    }
  });

  var rtJson = getJsonRs(contextPath + "/t9/core/funcs/portal/act/T9PortAct/delete.act", {
    ports: s
  });
  window.location.reload();
}

function exportPorts() {
  var ports = $$('#pageData .TableLine1 input').concat($$('#pageData .TableLine2 input'));
  var portsStr = '';
  ports.each(function(e, i) {
    if (e.checked) {
      portsStr += e.value + ',';
    }
  });
  if (!portsStr) {
    alert("请选择模块导出");
    return;
  }
  $('portsStr').value = portsStr;
  $('form1').action = contextPath + "/t9/core/funcs/portal/act/T9PortAct/exportPorts.act";
  $('form1').submit();
}


function checkAll(o) {
  var checked = o.checked
  $$('#pageData .TableLine1 input').each(function(e, i) {
    e.checked = checked;
  });
  $$('#pageData .TableLine2 input').each(function(e, i) {
    e.checked = checked;
  });
}

function add4All(id) {
  var r = getJsonRs(contextPath + "/t9/core/funcs/setdescktop/setports/act/T9MytableAct/add4All.act",{
    seqId: id
	});
  if (r.rtState == "0") {
    alert("添加成功");
  }
  else{
    alert("设置未成功");
  }
}

function setViewType(type, id) {
  var setType = getJsonRs(contextPath + "/t9/core/funcs/setdescktop/setports/act/T9MytableAct/setViewType.act",{
    seqId: id,
    type: type
	});
  if (setType.rtState == "0") {
    pageMgr.refreshAll();
  }
  else{
    alert("设置未成功");
  }
}

function setPosL(id) {
  setPos("l", id);
}

function setPosR(id) {
  setPos("r", id);
}

function setPos(pos, id) {
  var setType = getJsonRs(contextPath + "/t9/core/funcs/setdescktop/setports/act/T9MytableAct/setPos.act",{
    seqId: id,
    pos: pos
	});
  if (setType.rtState == "0") {
    pageMgr.refreshAll();
  }
  else{
    alert("设置未成功");
  }
}
</script>
</head>

<body onLoad="doInit()">
    <table border="0" width="90%" cellspacing="0" cellpadding="3" class="small"
        style="margin:5px 0px;">
        <tr>
            <td class="Big"><img src="<%=imgPath%>/control_theme.gif" align="abstop" /><span
                class="big3"> 模块管理</span><br></td>
        </tr>
    </table>
    <br />
    <br />
    <div id="pageData" style="margin: 0 auto; width: 100%;">
        <input type="hidden" name="TYPE"> <input type="hidden" name="USER_ID"> <input
            type="hidden" name="DEPT_ID"> <input type="hidden" name="PRIV_ID">
    </div>
    <table id="opt" class="TableBlock no-top-border" style="margin: 0 auto; width: 100%;">
        <tr class="TableData">
            <td>&nbsp;&nbsp; <a class="ToolBtn" href="javascript:void(0)"
                onclick="exportPorts()"><span>导出选中模块</span></a> <a class="ToolBtn"
                href="javascript:void(0)" onclick="showImport()"><span>导入桌面模块</span></a> <a
                class="ToolBtn" href="javascript:void(0)" onclick="del()"><span>删除选中模块</span></a> <a
                class="ToolBtn" href="/t9/core/funcs/portal/portlet/Add.jsp"><span>添加模块</span></a>
                <form style="display:none;" method="post" id="importForm" name="importForm"
                    enctype="multipart/form-data">
                    &nbsp;<input type="file" id="file" name="file" class="" value="" /> &nbsp;<input
                        type="button" class="BigButton" value="导入" onclick="importPorts()" />
                </form>
            </td>
        </tr>
    </table>
    <div id="noData" align="center" style="display:none">
        <input type="button" class="BigButtonC" value="导入桌面模块" onclick="showImport('noData')" /> <br>
        <br>
    </div>
    <form action="" id="form1" name="form1">
        <input type="hidden" id="portsStr" name="portsStr">
    </form>

</body>
</html>