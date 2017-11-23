function doInit(){
  if (sortName) {
    sortId = getSortIdsByName(sortName);
  }
  parseObj(secrtGrade, "secretsLevel");
  parseObj(docType, "recType");
  var beginParameters = {
      inputId:'startTime',
      property:{isHaveTime:false}
      ,bindToBtn:'beginDateImg'
  };
  new Calendar(beginParameters);
  var endParameters = {
      inputId:'endTime',
      property:{isHaveTime:false}
      ,bindToBtn:'endDateImg'
  };
  new Calendar(endParameters);
  getFlowType();
  var url = contextPath + "/t9/core/funcs/doc/receive/act/T9DocRegisterAct/getRegList.act?type=" +type ;
  cfgs = {
      dataAction: url,
      container: "container",
      moduleName:"doc",
      paramFunc: getParam,
      colums:[
              {type:"data", name:"title", text:"标题", width:"20%" ,render: titleRender },
              {type:"data", name:"sendDocNo", text:"来文文号", width:"10%"},
              {type:"data", name:"attachName", text:"来文正文", width: "15%",render: attachReader},
              {type:"hidden",name:"attachId"},
              {type:"data", name:"fromDeptName", text:"来文单位", width:"5%"},
              {type:"data", name:"secretsLevel", text:"密级", width:"5%"},
              {type:"data", name:"recType", text:"类型", width:"5%"},  
              {type:"data", name:"registerTime", text:"登记时间", width:"10%", dataType:"dateTime",format:'yyyy-mm-dd HH:MM:ss'},
              {type:"data", name:"status", text:"步骤", width:"8%" , render:prcsRender},
              {type:"hidden", name:"seqId"},
              {type:"hidden", name:"runId"},
              {type:"hidden", name:"runEnd"},
              {type:"hidden", name:"recNo"},
              {type:"hidden", name:"flowId"},
              {type:"hidden", name:"pages"},
              {type:"selfdef",text:"操作", width:"15%",render:opRender}
              ]
    };
  pageMgr = new T9JsPage(cfgs);
  pageMgr.show();
}
function attachReader(cellData, recordIndex, columIndex) {
  var attachId = this.getCellData(recordIndex,"attachId");
  if (!attachId) {
    return "无";
  }
  var imgSrc = imgPath + "/doc.gif";;
  var str = "<a href='javascript:void(0)' onclick='return false;' onmouseover='createDiv(event  ,this , \""+ attachId +"\" , \""+ cellData +"\")'><img src='" + imgSrc + "'/>&nbsp;" + cellData + "</a>";
  return str;
}
/**
 * 创建右建菜单
 * 
 */
function createDiv(event , node , attachId , attachName){
  var down = { attachmentId:attachId , attachmentName: attachName ,name:'<div  style="padding-top:5px;margin-left:10px">下载<div>',action:downAction ,extData: ""};
  var save = { attachmentId:attachId , attachmentName: attachName ,name:'<div style="padding-top:5px;margin-left:10px">转存<div>',action:saveAction,extData: ""};
  var read = { attachmentId:attachId , attachmentName: attachName ,name:'<div  style="padding-top:5px;margin-left:10px">阅读<div>',action:readAction,extData: ""};
  
  var menuD = [];
  menuD.push( read );
  menuD.push( down );
  menuD.push( save );
  var divStyle = {border:'1px solid #69F',width:'100px',position :'absolute',backgroundColor:'#FFFFFF',fontSize:'10pt',display:"block"};
  var menu = new Menu({bindTo:node , menuData:menuD , attachCtrl:true},divStyle);
  menu.show(event);
}
/**
 * 下载
 * @return
 */
function downAction(){
  var down = arguments[3];
  var attachmentId = down.attachmentId;
  var attachmentName = down.attachmentName;
  downLoadFile(attachmentName ,  attachmentId, "doc");
}

/**
 * 转存
 * @return
 */
function saveAction(){
  var down = arguments[3];
  var attachmentId = down.attachmentId;
  var attachmentName = down.attachmentName;
  archived(attachmentName,attachmentId,"doc");
}
/**
 * 转存处理函数
 * @param attachName
 * @param attachId
 * @param moudle
 * @return
 */
function archived(attachName,attachId,moudle){
  var URL = contextPath + "/core/funcs/savefile/index.jsp?attachId=" + attachId + "&attachName=" + encodeURIComponent(attachName) +"&module=" + moudle;
  var loc_x = screen.availWidth/2-200;
  var loc_y = screen.availHeight/2-90;
  window.open(URL,null,"height=180,width=400,status=1,toolbar=no,menubar=no,location=no,scrollbars=yes,top="+loc_y+",left="+loc_x+",resizable=yes");
}
/**
 * 阅读
 * @return
 */
function readAction(){
  var down = arguments[3];
  var attachmentId = down.attachmentId;
  var attachmentName = down.attachmentName;
  office(attachmentName,attachmentId,"doc",7);
}
function getParam(){
  queryParam = $("queryForm").serialize();
  return queryParam;
}
function titleRender (cellData, recordIndex, columIndex) {
  var sub = cellData;
  if (sub.length > 20) {
    sub = sub.substring(0 , 20) + "...";
  }
  cellData = "<span title='" + cellData +"'>" + sub + "</span>";
  return cellData;
}
function parseObj(docType, id , value){
  if(docType){
    var tt = docType.split(",");
    var sel = document.getElementById(id);
    if(tt && sel){
      for(var i=0; i<tt.length; i++ ){  
        var op = new Option(tt[i], tt[i]);
        if (tt[i] == value) {
          op.selected = true;
        }       
        sel.options.add(op);
      }
    }
  }
}
function prcsRender (cellData, recordIndex, columIndex) {
  var runId = this.getCellData(recordIndex,"runId");
  var flowId = this.getCellData(recordIndex,"flowId");
  var status = this.getCellData(recordIndex,"status");
  if (runId != '0') {
    return "<a href=\"javascript:flowView("+ runId +"," + flowId +",'','" + sortId +"','" + skin +"' , 1);\">" + status + "</a>";
  } else {
    return status;
  }
}
function opRender(cellData, recordIndex, columIndex){
  var runEnd = this.getCellData(recordIndex,"runEnd");
  var seqId = this.getCellData(recordIndex,"seqId");
  var runId = this.getCellData(recordIndex,"runId");
  
  var fromDeptName = this.getCellData(recordIndex,"fromDeptName");
  var secretsLevel = this.getCellData(recordIndex,"secretsLevel");
  var sendDocNo = this.getCellData(recordIndex,"sendDocNo");
  var title = this.getCellData(recordIndex,"title");
  var recType = this.getCellData(recordIndex,"recType");
  var registerTime = this.getCellData(recordIndex,"registerTime");
  var recNo = this.getCellData(recordIndex,"recNo");
  var pages = this.getCellData(recordIndex,"pages");
  if (!pages) {
    pages = "0";
  }
  
  var result = "";
  if (runId == '0' &&  runEnd != '1') {
    var param = encodeURIComponent(secretsLevel) + "&" + encodeURIComponent(recType) +'&'+ registerTime +
      '&'+encodeURIComponent(fromDeptName)+'&'+encodeURIComponent(sendDocNo)+'&'+ encodeURIComponent(title) +
      '&'+seqId +'&'+ recNo+ '&'+encodeURIComponent(userName)+ "&" + pages;
    result += "&nbsp;<input id='param-"+seqId+"' type='hidden' value=\"" + param + "\"/>"
    result += "&nbsp;<a href='javascript:void(0)'  onclick='handler("+seqId+")'>办理</a>";
  } else {
    result += "&nbsp;<a  href='javascript:void(0)' onclick='formViewByRunId("+runId+")' />办理详情</a>";
  } 
  result += "&nbsp;<a  href='javascript:void(0)' onclick='edit("+seqId+")'>修改</a>";
  if (type == '2') {
    result += "&nbsp;<a  href='javascript:void(0)' onclick='endWorkFlow("+runId+")'>结束</a>";
  } 
  if (type == '3') {
    result += "&nbsp;<a  href='javascript:void(0)' onclick='restore("+runId+")'>恢复执行</a>";
  } 
  result += "&nbsp;<a  href='javascript:void(0)' onclick='del("+seqId+")'>删除</a>";
  return result;
}
function endWorkFlow(runId) {
  if(!confirm("确认结束工作！")) {
    return ;
  }
 var url = contextPath + "/t9/core/funcs/doc/receive/act/T9DocRegisterAct/endWorkFlow.act";
 var json = getJsonRs(url, "runIdStr=" + runId) ;
 if (json.rtState == '0') {
   alert(json.rtMsrg); 
   pageMgr.search();
 }
}
function del(seqId) {
  if(!confirm("确认删除工作！")) {
    return ;
  }
 var url = contextPath + "/t9/core/funcs/doc/receive/act/T9DocRegisterAct/delRegister.act";
 var json = getJsonRs(url, "seqId=" + seqId) ;
 if (json.rtState == '0') {
   alert(json.rtMsrg); 
   pageMgr.search();
 }
}
function restore(runId) {
  var url = contextPath + "/t9/core/funcs/doc/receive/act/T9DocRegisterAct/restore.act?runId=" + runId ;
  var json = getJsonRs(url);
  if (json.rtState == '0') {
    alert(json.rtMsrg);
    pageMgr.search();
  }
}
function createNewWork(flowId , par , isNotOpenWindow , seqId ,flag , sortId){
  var url = contextPath +   moduleSrcPath + "/receive/act/T9DocReceiveHandlerAct/createWorkNew.act?seqId=" + seqId;
  if (par) {
    par = "flowId=" + flowId + "&" + par;
  } else {
    par = 'flowId=' + flowId;
  }
  if (flag) {
    url += "&attid=" + flag; 
  }
  var json = getJsonRs(url ,  par);
  if(json.rtState == "0"){
    var runId = json.rtData.runId;
    var url2 =   contextPath +   moduleContextPath +"/flowrunRec/list/inputform/index.jsp?skin="+ skin +"&sortId="+ sortId +"&runId=" + runId + "&flowId=" + flowId + "&prcsId=1&flowPrcs=1&isNew=1";
    if (isNotOpenWindow) {
      parent.location.href = url2;
    } else {
      window.open(url2);
    }
  }else{
    alert(json.rtMsrg);
  }
}
function handler(seqId) {
  var param = $('param-' + seqId).value;
  var url = contextPath + "/core/funcs/doc/receive/register/handler.jsp?seqId=" + seqId ;
  var resultValue = window.showModalDialog(url,null,'dialogWidth=500px;dialogHeight=500px;help:no;status:no; ');  
  if (resultValue != undefined) {
    var attid = resultValue.attid;
    var flowId = resultValue.flowId;
    var sortId = resultValue.sort;
    var flowName = resultValue.flowName;
    var values = getParamValues(flowName , param);
    createNewWork(flowId , values , true , seqId , attid , sortId);
  }
  return false;
}
//清空时间组件
function empty_date(){
  $("startTime").value="";
  $("endTime").value="";
}
function query() {
  pageMgr.search();
}
function getParamValues(flowName , values) {
  var vals = values.split("&");
  return getParamValuesImp(flowName , vals[0],vals[1],vals[2],vals[3],vals[4],vals[5],vals[6],vals[7],vals[8],vals[9]);
}

function getParamValuesImp(flowName , secretsLevel,recType,registerTime,fromDeptName,sendDocNo,title,seqId,recNo, userName , pages) {
  var params = paramsAll[flowName];
  var values = "";
  if (params["密级"]) {
    values += params["密级"] + "=" + secretsLevel;
  }
  if (params["类型"]) {
    values += "&" + params["类型"] + "=" + recType;
  }
  if (params["收文日期"]) {
    values += "&" + params["收文日期"] + "=" + registerTime;
  }
  if (params["来文单位"]) {
    values += "&" + params["来文单位"] + "=" + fromDeptName;
  }
  if (params["原文编号"]) {
    values += "&" + params["原文编号"] + "=" + sendDocNo;
  }
  if (params["标题"]) {
    values += "&" + params["标题"] + "=" + title;
  }
  if (params["收文ID"]) {
    values += "&" + params["收文ID"] + "=" + seqId;
  }
  if (params["文号"]) {
    values += "&" + params["文号"] + "=" + recNo;
  }
  if (params["联系人"]) {
    values += "&" + params["联系人"] + "=" + userName;
  }
  if (params["份数"]) {
    values += "&" + params["份数"] + "=" + pages;
  }
  return values;
}