<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/core/inc/t6.jsp"%>
<title>列出所有的标记 </title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="<%=jsPath%>/jquery-1.7.1.min.js"></script>
<script type="text/javascript" src="<%=jsPath%>/ui/jquery-ui-1.8.17.custom.min.js"></script>
<script type="text/javascript" src="<%=jsPath%>/ui/jquery.ux.borderlayout.js"></script>
<script type="text/javascript" src="<%=jsPath%>/ui/jquery-ui-patch.js"></script>
<script type="text/javascript" src="<%=jsPath%>/ui/jqGrid/grid.locale-cn.js"></script>
<script type="text/javascript" src="<%=jsPath%>/ui/jqGrid/jquery.jqGrid.src.js"></script>
<link rel="stylesheet" type="text/css" href="<%=cssPath%>/ui/jqGrid/ui.jqgrid.css"/>
<link rel="stylesheet" type="text/css" href="<%=cssPath%>/ui/zTree/zTreeStyle.css"/>
<link rel="stylesheet" type="text/css" href="<%=cssPath%>/ui/jqueryUI/base/jquery.ui.all.css"/>
<link rel="stylesheet" type="text/css" href="<%=cssPath%>/style.css"/>
<script type="text/javascript" src="<%=jsPath%>/ui/zTree/jquery.ztree.all-3.1.js"></script>
<script type="text/javascript">
function doInit(){
  $("#list").jqGrid({
    url:"<%=contextPath %>/test/cy/code/act/T9City1Act/getList.act?dtoClass=t9.core.data.T9PageQueryParamNew&nameStr=act,seqId,cityNo,cityName",
    datatype: "json",
    height: "230px",
    colNames:["操作","自增字段","城市编码","城市名称"],
    colModel:[
    		{name: "act", 	index: "act", 	hidden: false, width: 80, sortable: false}
			 ,{name: "seqId", index: "自增字段", hidden: false, width: 100}
			 ,{name: "cityNo", index: "城市编码", hidden: false, width: 100}
			 ,{name: "cityName", index: "城市名称", hidden: false, width: 100}
			     ],
    rowNum:10,
    rowList:[10,20,30],
    pager: '#pager',
    gridComplete: opts,
    viewrecords: true,
    sortorder: "desc",
    toolbar: [true, "top"],
    multiselect: true
  });
  
$("#list").jqGrid('navGrid','#pager',{add:false, edit:false, del:false, search:true, refresh:true});
  var toolbar = $("<div></div>").toolbar({
		btns: [{
      text: "新增",
      icon:'',
      handler: function() {
        $('<iframe class="selector" src="<%=contextPath %>/test/code/city1/input.jsp"></iframe>').dialog({
          title: "新建科目",
          height: 450,
          width: 500,
          modal: true
        }).css({
          width: "100%"
        });
      }
		}, {
	    text: "批量删除",
	    icon:'',
	    handler: function(e, t, a) {
	      var selectStr = jQuery("#list").jqGrid('getGridParam','selarrrow');
	      if(selectStr == ""){
	        alert("请至少选择一条记录！");
	        return;
	      }
	      var seqIdStr = "";
	      for(var selectId in selectStr){
	     	  var rowData = $("#list").jqGrid('getRowData',selectId);
	     	  seqIdStr = seqIdStr + rowData.seqId + ",";
	      }
	      deleteRecord(seqIdStr);
	    }
		}]
  });
  $("#t_list").append(toolbar);
}

function opts(){
	var ids = $("#list").jqGrid('getDataIDs');
	for(var i = 0; i < ids.length; i++){
		var rowId = ids[i];
		var str = "<center>"
				 		+ "<a href=javascript:editorRecord(" + rowId + ");><font color='blue'>修改</font></a> "
						+ "<a href=javascript:deleteRecordByRowId(" + rowId + ");><font color='blue'>删除</font></a>"
						+ "</center>";
		$("#list").jqGrid('setRowData',ids[i],{act:str});
	}	
}

function editorRecord(rowId) {
  var s = $("#list").jqGrid('getRowData',rowId);
  var url = "<%=contextPath %>/test/code/city1/input.jsp?seqId=" + s.seqId;
  $('<iframe class="selector" src="'+url+'"></iframe>').dialog({
    title: "修改科目",
    height: 450,
    width: 500,
    modal: true
  }).css({
    width: "100%"
  });
}

function deleteRecordByRowId(rowId){
  var s = $("#list").jqGrid('getRowData',rowId);
  deleteRecord(s.seqId);
}

function deleteRecord(seqId) {
  if(!confirmDel()) {
    return ;
  }
  var url = "<%=contextPath %>/test/cy/code/act/T9City1Act/deleteField.act";
  jQuery.ajax({
    url: url,
    data: "seqId=" + seqId,
    dataType: "json",
    success: function(json) {
      if (json.rtState == "0") {
        alert(json.rtMsrg);
        $("#list").trigger("reloadGrid");
      }
    },
    error: function(json) {
      alert(json.rtMsrg);
      $("#list").trigger("reloadGrid");
    }
  });
}

function confirmDel() {
  if(confirm("确认删除！ ")) {
    return true;
  }else {
    return false;
  }
}
</script>
</head>
<body onload="doInit()">
  <div class="ui-layout-center">
	  <table id="list"></table>
	  <div id="pager"></div>
	</div>
</body>
</html>