<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="/core/inc/header.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title></title>
    <link rel="stylesheet" href = "<%=cssPath%>/cmp/AssistInput.css">
     <link rel="stylesheet" href ="<%=cssPath%>/style.css">
	<script type="text/javascript" src="<%=contextPath %>/raw/lh/fckeditor/fckeditor.js"></script>
	<script type="text/Javascript" src="<%=contextPath%>/core/js/datastructs.js" ></script>
	<script type="text/Javascript" src="<%=contextPath%>/core/js/sys.js" ></script>
	<script type="text/Javascript" src="<%=contextPath%>/core/js/prototype.js" ></script>
	<script type="text/Javascript" src="<%=contextPath%>/core/js/smartclient.js" ></script>
   <script type="text/javascript" src="<%=contextPath%>/core/js/cmp/AssistInput1.0.js"></script>
    <script type="text/javascript">
	  var url = contextPath +'/raw/lh/assistinput/T9AssistAct/assistInput.act?str=';
	  //http://localhost/t9/raw/lh/assistinput/T9AssistAct/assistInput.act?str=d&length=10
    function doInit(){
        new AssistInuput({bindToId:'test1'
             ,requestUrl:url
			 ,func:test
		 });
       }
      function test(id){
		alert(id);
      }
      var scriptText3 = getTextRs(contextPath + "/core/js/merc_form.js ");
       if(window.execScript){  
          window.execScript(scriptText3);  
       }else{
         window.eval(scriptText3);   
        }
       var scriptText4 = getTextRs(contextPath + "/core/js/tab-view.js "); 
       if(window.execScript){   
         window.execScript(scriptText4);  
         }else{
           window.eval(scriptText4);    
          }
       initTabs('dhtmlgoodies_tabView2',Array('基本信息','经营信息','结算信息','风控信息','终端信息','业务信息','网站展示信息'),0,500,900,Array(false,true,true,true,true,true,true));

    </script>
  </head>
  
  <body onload="doInit()">
  <div id="di" style="width:800px">

   please input:<input type="text" id="test1" value="" >
  <br>

  </div>
  <div id="di2">
  
  </div>
  </body>
</html>
