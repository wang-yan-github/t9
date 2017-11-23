function InsertImage(src){ 
  var oEditor = FCKeditorAPI.GetInstance('fileFolder') ; //FCK实例 
  if ( oEditor.EditMode == FCK_EDITMODE_WYSIWYG ) {     
     oEditor.InsertHtml( "<img src='"+ src + "'/>") ; 
  } 
}


/*
*获取项目类型
*/
function getProjectStyle(){
	var classNo="PROJ_TYPE";
	var url=contextPath +"/t9/project/system/act/T9ProjSystemAct/getStyleList.act?classNo="+classNo;
	var rtJson=getJsonRs(url);
	if(rtJson.rtState==0){
		var selectObj = $("projStyle");
		//selectObj.length=0;
		for(var i=0;i<rtJson.rtData.size();i++){
			var myOption = document.createElement("option");
		    myOption.value = rtJson.rtData[i].seqId;
		    myOption.text = rtJson.rtData[i].classDesc;
		    selectObj.options.add(myOption, selectObj.options ? selectObj.options.length : 0);
		}
	}
}


/*
*获取项目审批人
*/

function getProjectApprove(){
	var privCode="APPROVE";
	var url=contextPath +"/t9/project/project/act/T9ProjectAct/getApproveUser.act?privCode="+privCode;
	var rtJson=getJsonRs(url);
	if(rtJson.rtState==0){
		var selectObj = $("projManager");
		selectObj.length=0;
		for(var i=0;i<rtJson.rtData.size();i++){
			var myOption = document.createElement("option");
		    myOption.value = rtJson.rtData[i].seqId;
		    myOption.text = rtJson.rtData[i].projManager;
		    if(check(rtJson.rtData[i].seqId)){
		    	selectObj.options.add(myOption, selectObj.options ? selectObj.options.length : 0);
		    }
		}
	}
}

function check(seqId){
	var selectObj = $("projManager");
	for(var j=0;j<selectObj.length;j++){
    	if(selectObj.options[j].value==seqId){
    		 return false;
    		 continue;
    		}
    	}
	return true;
}