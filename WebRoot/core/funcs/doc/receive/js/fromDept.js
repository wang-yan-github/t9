var config = null;
var pageMgr = null;
var parentWindowObj = null;
var fromDeptNameNode = null;
var fromDeptIdNode = null;

function doInit() {
  parentWindowObj = window.dialogArguments;
  fromDeptNameNode =  parentWindowObj.$("fromDeptName");
  fromDeptIdNode =  parentWindowObj.$("fromDeptId");
  config = this.getConfig();
  doQuery();
}
function doQuery() {
  if(!pageMgr){
    pageMgr = new T9JsPage(config);
    pageMgr.show();
  }else{
    pageMgr.search();
  }
  var total = pageMgr.pageInfo.totalRecord;
  if(total){
    $('container').style.display = "";
    $('msrg').style.display = "none";
  }else{
    WarningMsrg('没有检索到数据!', 'msrg','info');
    $('msrg').style.display = "";
    $('container').style.display = "none";
  }
}
function addDept(){
  if ($('fromDeptName').value) {
    var url = contextPath + "/t9/core/funcs/doc/act/T9DocFromDeptAct/addFromDept.act";
    var json = getJsonRs(url , $('form2').serialize()); 
    if(json.rtState == "0"){
      $('fromDeptName').value = '';
      doQuery();
    }
  } else {
    alert("来文单位为空!");
  }
}
/**
 * 消息提示
 * @param msrg
 * @param cntrlId 绑定消息的控件

 * @param type  消息类型[info|error||warning|forbidden|stop|blank] 默认为info
 * @return
 */
function WarningMsrg(msrg, cntrlId,type ) {
  var msrgDom = "<table class=\"MessageBox\" align=\"center\" width=\"260\">";
  if(!type){
    type = "info";
  }
  msrgDom += " <tr> <td class=\"msg " + type + "\">"
  msrgDom +=  "<div class=\"content\" style=\"font-size:12pt\">" + msrg + "</div>"
      + " </td> </tr> </table>";
  $(cntrlId).innerHTML = msrgDom;
}


function getConfig() {
  var url =  contextPath + "/t9/core/funcs/doc/act/T9DocFromDeptAct/getAllDept.act";
  var cfgs = {
    dataAction: url,
    container: "container",
    paramFunc: getParameter,
    pageSize:10
  };
  var colums = new Array();
  colums.push({type:"hidden", name:'seqId'});
  colums.push({type:"data", name:'deptName'  ,width:400, text:"来文单位"});
  colums.push({type:"hidden", name:'deptId'});
  var operate =  {type:"selfdef", text:"操作",width:200, render:opRender};
  colums.push(operate);
  cfgs.colums = colums;
  return cfgs;
}

function getParameter(){
  var queryParam = $("form2").serialize() ;
  return queryParam;
}
/**
 * 操作描画器 * @param cellData
 * @param recordIndex
 * @param columIndex
 * @return
 */
function opRender(cellData, recordIndex, columIndex){
  var value = this.getCellData(recordIndex,"seqId");
  var deptName = this.getCellData(recordIndex,"deptName");
  var deptId = this.getCellData(recordIndex,"deptId");
  var input = "<div align=center><input value='删除' class='SmallButton' onclick='del(\""+ value +"\")' type='button'/>&nbsp;"
    +"<input value='选择' class='SmallButton' onclick='setVal(\""+ deptName +"\" , \""+deptId+"\")' type='button'/></div>";
  return input;
}

function setVal(str , deptId) {
  var node = fromDeptNameNode;
  node.value = str;
  if (deptId && deptId != 0) {
    node = fromDeptIdNode;
    node.value = deptId;
  }
  window.close();
}
function del(id){
    var url = contextPath + "/t9/core/funcs/doc/act/T9DocFromDeptAct/delFromDept.act?id=" + id;
    var json = getJsonRs(url); 
    if(json.rtState == "0"){
      doQuery();
    }
}
function selectFromDept(){
  var URL=contextPath + "/core/funcs/workflow/flowrun/list/inputform/flow_selectData/index.jsp?ctrl=" + id + "&itemStr=" + item_str;
  var openWidth = 800;
  var openHeight = 500;
  openDialog(URL,  openWidth, openHeight);
}