<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String seqId = request.getParameter("seqId");
if(seqId == null) {
  seqId = "";
}
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/core/inc/t6.jsp" %>
<title>增加或修改页面</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="<%=jsPath %>/jquery-1.7.1.min.js"></script>
<script type="text/javascript" src="<%=jsPath%>/ui/jquery-ui-1.8.17.custom.min.js"></script>
<script type="text/javascript" src="<%=jsPath%>/ui/uni-form/uni-form-validation.jquery.js"></script>
<script type="text/Javascript" src="<%=contextPath%>/rad/codeSel/codeSel.js" ></script>
<script type="text/Javascript" src="<%=contextPath%>/rad/grid/grid.js" ></script>
<link rel="stylesheet" type="text/css" href="<%=cssPath%>/ui/jqueryUI/base/jquery.ui.all.css"/>
<link rel="stylesheet" type="text/css" href="<%=cssPath%>/ui/uni-form/css/uni-form.css" media="screen" rel="stylesheet"/>
<link rel="stylesheet" type="text/css" href="<%=cssPath%>/ui/uni-form/css/default.uni-form.css" title="Default Style" media="screen" rel="stylesheet"/>
<link rel="stylesheet" type="text/css" href="<%=cssPath%>/style.css"/>
<script type="text/javascript">
var seqId = "<%=seqId%>";
function doInit() {
	$(".top-toolbar a").button();
  var url = "<%=contextPath%>/test/cy/code/act/T9City1Act/show.act"; 
  if(seqId){
	  jQuery.ajax({
	    url: url,
	    data: "seqId=" + seqId,
	    dataType: "json",
	    success: function(json) {
	      if (json.rtState == "0") {
	        bindJson2Cntrl(json.rtData);
	      } else{
	        alert(json.rtMsrg);
	      }
	    },
	    error: function(json) {
	      alert(json.rtMsrg);
	    }
	  });
  }
}

function check(el) {
  var flag = true;
  var cntrl = document.getElementById(el);
  if(!cntrl.value) {
	  alert("标记编号不能为空！");
	  cntrl.focus();
	  flag = false;
  }
  if(!isNumber(cntrl.value)){
	  alert("标记编号必须填入数字！");
	  cntrl.focus();
  	flag = false;
  }
  cntrl = document.getElementById("flagDesc");
  if(!cntrl.value) {
  	alert("标记描述不能为空！");
  	cntrl.focus();
  	flag = false;
  }
  return flag;
}

function commitItem() {
  var url = "";
  if(seqId) {
    url = "<%=contextPath%>/test/cy/code/act/T9City1Act/updateField.act";
  }else {
    url = "<%=contextPath%>/test/cy/code/act/T9City1Act/addField.act";
  }
  jQuery.ajax({
    url: url,
    data: $("#form1").serialize(),
    dataType: "json",
    success: function(json) {
      if (json.rtState == "0") {
        alert(json.rtMsrg);
        parent.$("#list").trigger("reloadGrid");
        parent.$('.selector').dialog("close");
      }
      else{
        alert(json.rtMsrg);
      }
    },
    error: function(json) {
      alert(json.rtMsrg);
    }
  });
}

function bindJson2Cntrl(json, filters) {
  for (var property in json) {
    if (filters) {
      if (Object.isString(filters) && filters.indexOf(",") > 0) {
        var filterArray = filters.split(",");
        if (!filterArray.contains(property)) {
          continue;
        }
      }else if (Object.isString(filters)) {
        var ancestor = $(filters);
        var elem = $('#'+property);
        if (ancestor && elem && !Element.descendantOf(elem, ancestor)) {
          continue;
        }
      }else if (Object.isArray(filters)) {
        if (!filters.contains(property)) {
          continue;
        }
      }else if (Object.isElement(filters)) {
        var elem = $('#'+property);
        if (elem && !Element.descendantOf(elem, ancestor)) {
          continue;
        }
      }
    }
    var value = json[property];  
    var cntrlArray = document.getElementsByName(property);    
    var cntrlCnt = cntrlArray.length;
    if (!cntrlArray || cntrlCnt < 1) {
      if (document.getElementById(property)) {
        cntrlArray = [document.getElementById(property)];
        cntrlCnt = 1;
      }else {
        continue;
      }
    }
    if (cntrlCnt == 1) {
      var cntrl = cntrlArray[0];
      if (cntrl.tagName.toLowerCase() == "input" && cntrl.type.toLowerCase() == "checkbox") {
        if (cntrl.value == value) {
          cntrl.checked = true;
        }else {
          cntrl.checked = false;
        }
      }else if (cntrl.tagName.toLowerCase() == "td"
          || cntrl.tagName.toLowerCase() == "div"
          || cntrl.tagName.toLowerCase() == "span") {
        cntrl.innerHTML = value;
      } else if (cntrl.tagName.toLowerCase() == 'select') {
        for (var i = 0; i < cntrl.childNodes.length; i++) {
          if (cntrl.childNodes[i].value == value) {
            cntrl.childNodes[i].setAttribute("selected", "selected");
            break;
          }
        }
      }else {
        cntrl.value = value;
      }
    }else {
      for (var i = 0; i < cntrlCnt; i++) {
        var cntrl = cntrlArray[i];
        if (cntrl.value == value) {
          cntrl.checked = true;
        }else {
          cntrl.checked = false;
        }
      }
    }
  }
}

function onSelectTable(tableNo, nameField, nameFieldNo, e) {
  this.e = e;
  selectCode( {
    sort : "1",
    tableNo : tableNo,
    codeField : "编号",
    nameField : nameField,
    codeFieldNo : "代码",
    nameFieldNo : nameFieldNo,
    filterField : "",
    filterValue : "",
    currValue : "",
    orderBy : ""
  });
}

function setValue(valueId, value){
  $('#'+e.id.substr(0, e.id.length - 4)).val(valueId);
  e.value = value;
}

function getSecretFlag(fkTableName2, fkFilterName, codeClass, fkNameFieldName2, optDiv, extValue){
	var requestURLStr = "<%=contextPath%>/test/cy/code/act/T9City1Act/getCodeSelect.act";
	  jQuery.ajax({
    url: requestURLStr,
    data: "fkTableName2=" + fkTableName2 + "&fkFilterName=" + fkFilterName + "&codeClass=" + codeClass + "&fkNameFieldName2=" + fkNameFieldName2,
    dataType: "json",
    success: function(json) {
      if (json.rtState == "0") {
				var prcs = json.rtData;
				var selects = document.getElementById(optDiv);
				for(var i=0;i<prcs.length;i++){
				  var prc = prcs[i];
				  var option = document.createElement("option"); 
				  option.value = prc.seqId; 
				  option.innerHTML = prc.codeName; 
				  selects.appendChild(option);
				  if(extValue && (extValue == prc.value)){
			      option.selected = true;
			    }
				}
      }
      else{
        alert(json.rtMsrg);
      }
    },
    error: function(json) {
      alert(json.rtMsrg);
    }
  });
}
</script>
</head>
<body onload="doInit()">
<form name="form1" id="form1" method="post" class="uniForm">
  <%
    if(seqId.equals("")) {
  %>   
    <h2><img src="<%=contextPath %>/core/styles/imgs/green_plus.gif"></img>添加</h2> 
  <%
    }else {
  %>    
    <h2><img src="<%=contextPath %>/core/styles/imgs/edit.gif"></img>修改</h2>
  <%
    }
  %>
  <input type="hidden" id="dtoClass" name="dtoClass" value="test.cy.code.T9City1"/>
  <fieldset class="inlineLabels">
  	<div class="ctrlHolder">
			<label for="seqId"> 自增字段:</label>
			<input type="text" id="seqId" name="seqId" />
		</div>
  	<div class="ctrlHolder">
			<label for="cityNo"> 城市编码:</label>
			<input type="text" id="cityNo" name="cityNo" />
		</div>
  	<div class="ctrlHolder">
			<label for="cityName"> 城市名称:</label>
			<input type="text" id="cityName" name="cityName" />
		</div>
  </fieldset>
  <fieldset class="action top-toolbar">
    	<a href="javascript:void(0)" onclick="commitItem()"><span>提交</span></a>
  </fieldset>
</form>
</body>
</html>