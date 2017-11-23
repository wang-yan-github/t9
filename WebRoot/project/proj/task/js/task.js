function createNewWork(flowName , par , isNotOpenWindow,projId,taskId){
  
  var url = contextPath + "/t9/core/funcs/workflow/act/T9FlowRunAct/createWork.act";
  if (par) {
    par = "flowName=" + flowName + "&" + par;
  } else {
    par = 'flowName=' + flowName;
  }
  var json = getJsonRs(url ,  par);
  if(json.rtState == "0"){
    var flowId = json.rtData.flowId;
    var runId = json.rtData.runId;
//    alert(runId);
//    return ;
    updateRunId(projId,taskId,runId);
    
    var url2 =  contextPath + "/core/funcs/workflow/flowrun/list/inputform/index.jsp?runId=" + runId + "&flowId=" + flowId + "&prcsId=1&flowPrcs=1&isNew=1";
    if (isNotOpenWindow) {
      location.href = url2;
    } else {
      window.open(url2);
    }
  
  }else{
    alert(json.rtMsrg);
  }
}

function createNewWork1(flowName , par , isNotOpenWindow){
	  
	  var url = contextPath + "/t9/core/funcs/workflow/act/T9FlowRunAct/createWork.act";
	  if (par) {
	    par = "flowName=" + flowName + "&" + par;
	  } else {
	    par = 'flowName=' + flowName;
	  }
	  var json = getJsonRs(url ,  par);
	  if(json.rtState == "0"){
	    var flowId = json.rtData.flowId;
	    var runId = json.rtData.runId;
	    //updateRunId(projId,taskId,runId);
	    var url2 =  contextPath + "/core/funcs/workflow/flowrun/list/inputform/index.jsp?runId=" + runId + "&flowId=" + flowId + "&prcsId=1&flowPrcs=1&isNew=1";
	    if (isNotOpenWindow) {
	      location.href = url2;
	    } else {
	      window.open(url2);
	    }
	  
	  }else{
	    alert(json.rtMsrg);
	  }
	}
function updateRunId (projId,taskId,runId){
	//alert(runId);
	 var url = contextPath+"/t9/project/task/act/T9TaskAct/updateRunId.act?projId="+projId+"&taskId="+taskId+"&runId="+runId;
	 var json = getJsonRs(url);
	 if(json.rtState == "0"){
		 
	 }else{
		 alert(json.rtMsrg);
	 }
}