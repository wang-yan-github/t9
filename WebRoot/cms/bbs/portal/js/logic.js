var uploadNum = 0;
var uploadTotalFlag = true;
//上传成功回调函数
function uploadSuccessOver(file, serverData){
  $('showAtt').innerHTML = '';
   var progress = new FileProgress(file, this.customSettings.progressTarget);
    progress.toggleCancel(false);
    var json = null;
    json = serverData.evalJSON();
    if(json.state=="1") {
       progress.setError();
       progress.setStatus("上传失败：" + serverData.substr(5));
       
       var stats=this.getStats();
       stats.successful_uploads--;
       stats.upload_errors++;
       this.setStats(stats);
    } else {
       $('attachmentId').value += json.data.attachmentId;
       $('attachmentName').value += json.data.attachmentName;
       var attachmentIds = $("attachmentId").value;
       var attachmentNames = $("attachmentName").value;
       var selfdefMenu = {
           office:["downFile","read","edit"], 
           img:["downFile","play"],  
           music:["downFile","play"],  
           video:["downFile","play"], 
           others:["downFile"]
       }
       attachMenuUtil("showAtt","cms",null,attachmentNames,attachmentIds,false);
    }
    $('fsUploadArea').style.display='none';
}

//浮动菜单文件的删除 
function deleteAttachBackHand(attachName,attachId,attrchIndex){ 
  var url= contextPath + "/t9/cms/bbs/comment/act/T9BbsCommentAct/delFloatFile.act?attachId=" + attachId +"&attachName=" + attachName ;
  if (attrchIndex) {
    url += "&seqId=" + attrchIndex;
  }
  var json=getJsonRs(encodeURI(url)); 
  if(json.rtState =='1'){ 
    alert(json.rtMsrg); 
    return false; 
  }else { 
    prcsJson=json.rtData; 
    var updateFlag=prcsJson.updateFlag; 
    if(updateFlag){ 
      var ids = $('attachmentId').value ;
      if (!ids) {
        ids = ""; 
      }
      var names =$('attachmentName').value;
      if (!names) {
        names = ""; 
      }
      var idss = ids.split(",");
      var namess = names.split("*");
     
      var newId = getStr(idss , attachId , ",");
      var newname = getStr(namess , attachName , "*");  
     
      $('attachmentId').value = newId;
      $('attachmentName').value = newname;
      return true; 
   }else{ 
     return false; 
   }  
  } 
}

function getStr(ids , id , split) {
	  var str = "";
	  for (var i = 0 ; i< ids.length ;i ++){
	    var tmp = ids[i];
	    if (tmp) {
	      if (tmp != id) {
	        str += tmp + split;
	      }
	    }
	  }
	  return str;
	}
function InsertImage(src){ 
	  var oEditor = FCKeditorAPI.GetInstance('fileFolder') ; //FCK实例 
	  if ( oEditor.EditMode == FCK_EDITMODE_WYSIWYG ) {     
	     oEditor.InsertHtml( "<img src='"+ src + "'/>") ; 
	  } 
	}
