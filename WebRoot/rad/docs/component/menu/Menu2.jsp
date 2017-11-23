<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<title></title>
<link href="<%=cssPath %>/style.css" rel="stylesheet" type="text/css" />
<link href="<%=cssPath %>/cmp/tab.css" rel="stylesheet" type="text/css" />
<script type="text/Javascript" src="<%=contextPath%>/core/js/datastructs.js" ></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/sys.js" ></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/prototype.js" ></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/smartclient.js" ></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/cmp/Menu.js" ></script>
<script type="text/javascript">
function showMenu1(event){
  //新建菜单
  var url = contextPath + "/t9/rad/docs/menu/T9MenuAct/getData.act";
  var menu = new Menu({requestUrl:url,attachCtrl:true,bindTo:"menu1"});
  //显示
  menu.show(event);
}

function add(event ,bindTo, extData,item) {
  alert("新增" + extData);
}
function del(event ,bindTo, extData,item) {
  alert("删除" + extData);
}
function update(event ,bindTo, extData,item) {
  alert("修改" + extData);
}
</script>
</head>

<body>
<span id="menu1" onclick="showMenu1(event)">菜单一</span>
</body>
</html>