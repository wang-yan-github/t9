function check() {
  var cntrl = document.getElementById("classCode")
  if(!cntrl.value) {
	alert("代码编号不能为空！");
	cntrl.focus();
	return false;
  }


  cntrl = document.getElementById("sortNo");
  if (!cntrl.value) {   
  	alert("排序号不能为空！");
  	cntrl.focus();
  	return false;
  }
  
  if(!isNumber(cntrl.value)){
	alert("必须填入数字！");
	cntrl.focus();
  	return false;
  }
 

  cntrl = document.getElementById("classDesc");
  if(!cntrl.value) {
  	alert("代码描述不能为空！");
  	cntrl.focus();
  	return false;
  }
  return true;
}

function commitItem() {
  if(!check()){
    return;
  }
    classNo = document.getElementById("classNo").value;
    alert(classNo);
    var url = "<%=contextPath%>/t9/core/codeclass/act/T9CodeClassAct/addCodeItem.act";
 
    var rtJson = getJsonRs(url, mergeQueryString($("form1")));
   
    if (rtJson.rtState == "0") {
      alert(rtJson.rtMsrg);
      $("form1").reset();
      document.getElementById("classNo").value = classNo;
      document.getElementById("classCode").focus();
    }else {
      alert(rtJson.rtMsrg); 
    }
  
}