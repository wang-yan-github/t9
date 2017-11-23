<%@ page import = "java.util.*" %>
<%@  page import="java.util.Map" %>
<%@  page import="java.util.HashMap" %>
<%@  page import="t9.mobile.util.T9MobileUtility" %>
<%@  page import="t9.mobile.mobileseal.data.*" %>
<%@  page import="t9.mobile.util.T9MobileConfig" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/mobile/workflow/header.jsp" %>
<%
	Map map1 = (Map)request.getAttribute("act.retdata");
/**
 *这是 获取 列表 印章列表 和 设备列表
 */
	List dList = (ArrayList)map1.get("dList");
	List sList = (ArrayList)map1.get("sList");
%>
<script type="text/javascript">
var p = "<%=request.getSession().getId()%>";
var seal_id = "";

function pageInit(page_id)
{
   if(page_id == 1)
   {
      tiScroll_1 = new $.tiScroll({"page_id": 1,"listType": "readonly"});
      tiScroll_1.init();
   }
   if(page_id == 2)
   {
      tiScroll_2 = new $.tiScroll({"page_id": 2,"listType": "readonly"});
      tiScroll_2.init();
   }
   if(page_id == 3)
   {
      tiScroll_3 = new $.tiScroll({"page_id": 3,"listType": "readonly"});
      tiScroll_3.init();
   }
}


function mobile_app_home()
{   
	<% 
	
	if(request.getHeader("User-Agent").indexOf("Android")!=-1) {
	%>
		window.Android.backtomain();
		return;
	<%}else if(request.getHeader("User-Agent").indexOf("Mac")!=-1){ %>
		document.location = "backtomain:";
		return;
	<% } %>
}
</script>

<%
	Map map = new HashMap();
	map.put("1" , T9MobileUtility.getHeadMap("" , "goSettings();" , "返回","手机签章" , "" , "createApply();" , "申请"));
	map.put("2" , T9MobileUtility.getHeadMap("" , "reback(2,1);" , "返回","申请记录"));
	map.put("3" , T9MobileUtility.getHeadMap("" , "reback(3,1);" , "返回","我的印章" , "" , "editPass();" , "修改"));
	map.put("4" , T9MobileUtility.getHeadMap("" , "reback(4,3);" , "返回","修改印章" , "" , "savePass();" , "确定"));
	
%>

<%=T9MobileUtility.buildHead(map)%>
<%=T9MobileUtility.buildMessage()%>
<%=T9MobileUtility.buildProLoading()%>

<!-- page of settings -->

    <div id="page_1" class="pages tcontent" >
        <div id="wrapper_1" class="wrapper tform_wrapper">
            <div id="scroller_1" class="scroller">
      		    <div class="container">
                    <div id="deviceForm" class="tform tformshow">
                        <div class="read_detail read_detail_header">
                            <em>设备申请记录</em>    
                        </div>
                        <%
                        if(dList != null){
                        	for(int i=0;i<dList.size();i++){
                        		T9MobileDevice md = (T9MobileDevice)dList.get(i);
                        %>
                        <div class="read_detail read_detail_hasarrow" style="cursor:pointer;" onclick="goToDeviceInfo(<%=md.getSeqId() %>)">
                            <div class="read_detail_hl_t">
                                <em><%=md.getDeviceName() %>&nbsp;&nbsp;<%=md.getSubmitTime() %></em>
                            </div>
                            <span class="ui-icon-rarrow"></span>
                            <div class="clear"></div>
                        </div>
                        <%
                        }//while
                        if(dList.size() ==0)
                             out.print("<div class=\"no_msg\">暂无申请记录！</div>");
                        
                        }
                        %>
                    </div>
                    <div id="sealForm" class="tform tformshow">
                        <div class="read_detail read_detail_header">
                            <em>我的签章</em>    
                        </div>
                        <% 
                        String SEAL_ID_TMP = "";
                        if(sList != null){
                       for(int j=0;j<sList.size();j++){
                    	   
                    	   T9MobileSeal ms = (T9MobileSeal)sList.get(j);
                    	 %>
      		            <div class="read_detail read_detail_hasarrow" onclick="goToMySeal(<%=ms.getSeqId() %>)" style="cursor:pointer;">
                            <div class="read_detail_hl_t">
                                <em><%=ms.getSealName() %></em>
                            </div>
                            <span class="ui-icon-rarrow"></span>
                            <div class="clear"></div>
                        </div>
                        <%
                        	SEAL_ID_TMP += ms.getSeqId()+",";
                        }
                       if(sList.size() ==0)
                    	   out.print("<div class=\"no_msg\">暂无授权的印章！</div>");
                       
                        }
                       %>
                    </div>
      		    </div>
            </div>      
        </div>
    </div>
    <div id="page_2" class="pages tcontent" style="display:none;">
        <div id="wrapper_2" class="wrapper tform_wrapper">
            <div id="scroller_2" class="scroller">
            </div>      
        </div>
   </div>
   <div id="page_3" class="pages tcontent" style="display:none;">
        <div id="wrapper_3" class="wrapper tform_wrapper">
            <div id="scroller_3" class="scroller">
            </div>      
        </div>
   </div>
   <div id="page_4" class="pages tcontent" style="display:none;">
        <div id="wrapper_4" class="wrapper tform_wrapper">
            <div id="scroller_4" class="scroller">
            </div>
        </div>
   </div>
   
<script type="text/javascript" src="<%=contextPath %>/mobile/js/udf-1.1.js"></script>
<script type="text/javascript">

$(document).ready(function(){
   pageInit(1);
});

function goSettings()
{
  if(typeof(mobile_app_home) == "function")
    mobile_app_home();
  else
    window.location='../';
}
function goToMySeal(ID){
   if(!ID) return;
   jQuery.ajax({
     type: 'POST',
      url:contextPath + '/t9/mobile/mobileseal/act/T9PdaMobileSealAct/getSealDeviceById.act',
      async:false,
      data: {'sessionid': p,'ID': ID},
     beforeSend: function()
     {
         $.ProLoading.show();
      },
      error:function(){  
          alert("error occured!!!");  
          },
      success: function(data){
         $.ProLoading.hide();
         $("#page_3 > #wrapper_3 > #scroller_3").empty().append(data);
         $("#page_3").show('fast',function(){pageInit(3);});
         $("#header_1").hide();
         $("#header_3").show();
      }
   });
}

function goToDeviceInfo(SEQ_ID){
   if(!SEQ_ID) return;
   $.ajax({
      type: 'GET',
      url: contextPath + '/t9/mobile/mobileseal/act/T9PdaMobileSealAct/getDeviceById.act',
      cache: false,
      data: {'sessionid': p, 'SEQ_ID': SEQ_ID},
      beforeSend: function()
      {
         $.ProLoading.show();
      },
      success: function(data){
         $.ProLoading.hide();
         $("#page_2 > #wrapper_2 > #scroller_2").empty().append(data);
         $("#page_2").show('fast',function(){pageInit(2);});
         $("#header_1").hide();
         $("#header_2").show();
      }
   });
}

function createApply()
{
    if(typeof window.Android != 'object')
    {
        showMessage("此操作仅支持客户端执行");
        return;
    }
    //"{'deviceId':'1111','phoneNumber':'phoneNumber','imsi':'imsi','imei':'imei','model':'model'}";
    var authData = window.Android.GetAuthCode();

    $.ajax({
      type: 'GET',
      url:contextPath + '/t9/mobile/mobileseal/act/T9PdaMobileSealAct/submit.act',
      cache: false,
      data: {'sessionid': p,'authData':authData},// encodeURIComponent(authData)},
      beforeSend: function()
      {
         $.ProLoading.show("提交验证中...");   
      },
      success: function(data){
    	  var dataObj = eval("("+data+")");//转换为json对象 
         $.ProLoading.hide(); 
         if(dataObj.state == 0) 
         {
            showMessage("提交申请成功");
            var newDiv = document.createElement("div");
            $(newDiv).addClass("read_detail read_detail_hasarrow");
            $(newDiv).click(function(){goToDeviceInfo(dataObj.data.SEQ_ID)});
            $(newDiv).html("<div class=\"read_detail_hl_t\"><em>"+dataObj.model+"&nbsp;&nbsp;"+dataObj.time+"</em></div><span class=\"ui-icon-rarrow\"></span><div class=\"clear\"></div>");
            $("#deviceForm").append(newDiv);
            $("#deviceForm .no_msg").hide();
         }
         else
         {			
       		 var dataObj = eval("("+data+")");//转换为json对象 
            showMessage(dataObj.msg);
         }
      },
      error: function(data){
         $.ProLoading.hide();  
         showMessage("提交申请失败");
      }
   });
}

function editPass(){
// 	alert("III"+seal_id);
	if(!seal_id)
		return;
   	$.ajax({
     // type: 'GET',
      url: contextPath + '/t9/mobile/mobileseal/act/T9PdaMobileSealAct/getSealById.act',
      //cache: false,
      data: {'sessionid': p,'ID': seal_id},
      beforeSend: function()
      {
         $.ProLoading.show();
      },
      success: function(data){
         $.ProLoading.hide();
         $("#page_4 > #wrapper_4 > #scroller_4").empty().append(data);
         $("#page_4").show('fast',function(){pageInit(4);});
         $("#header_3").hide();
         $("#header_4").show();
      }
   });
}

function savePass(){
	if(!seal_id)
		return;
	var seal_pass = $("input[name='SEAL_PASS']").val();
	var seal_new_pass = $("input[name='SEAL_NEW_PASS']").val();
   	$.ajax({
      type: 'GET',
      url: contextPath + '/t9/mobile/mobileseal/act/T9PdaMobileSealAct/resetPass.act',
      cache: false,
      data: {'sessionid': p,'ID': seal_id,'SEAL_PASS': seal_pass,'SEAL_NEW_PASS': seal_new_pass},
      beforeSend: function()
      {
         $.ProLoading.show();
      },
      success: function(data){
         $.ProLoading.hide();
         if(data == "+OK")
         {
         	showMessage("密码修改成功");
         }
         else {
         	showMessage(data);
         }
      }
   });
}
     
</script>
</body>
</html>