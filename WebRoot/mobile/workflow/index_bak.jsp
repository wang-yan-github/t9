<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@  page import="java.util.Map" %>
      <%@  page import="java.util.List" %>
      <%@  page import="java.util.HashMap" %>
    <%@  page import="t9.mobile.util.T9MobileUtility" %>
    <%@  page import="t9.mobile.util.T9MobileConfig" %>
    <%@ include file="/mobile/workflow/header_bak.jsp" %>
<%
Map n = (Map)request.getAttribute("n");
int total_items = (Integer)n.get("total_items");

%>
<script type="text/javascript">
var stype = "workflow";
var pre_page = 0;
var fileReadPage = 1;
var nonewdata = "没有新工作";
var newdata = "%s个新工作";
var noeditpriv = "无办理权限";
var nosubeditpriv = "无经办权限";
var noreadflowpriv = "无查看表单权限";
var nosignflowpriv = "无会签权限";
var norightnextprcs = "没有符合条件的下一步骤";
var nosetnewprcs = "错误：尚未设置下一步骤";
var workcomplete = "工作已结束";
var workdonecomplete = "工作办理完成";
var workhasnotgoback = "不能退回此工作";
var workhasgoback = "工作已经回退";
var notselectedstep = "请选择回退步骤";
var workhasturnnext = "工作已转交下一步";
var signisnotempty = "会签意见不能为空";
var signsuccess = "会签意见保存成功";
var formsuccess = "表单保存成功";
var getfature = "获取失败";
var error = "数据不全未能转交";
var errorzbisnotnull = "第%s步主办人不能为空";
var errorblisnotnull = "第%s步办理人不能为空";
var nocreatepriv = "没有该流程新建权限，请与OA管理员联系";
var noflowlist = "此分类没有流程！";
var norunname = "名称/文号不能为空！";
var noprefix = "前缀不能为空！";
var nosuffix = "后缀不能为空！";
var namerepeat = "输入的工作名称/文号与之前的工作重复，请重新设置。";
var nocreaterun = "新建工作失败，请重新创建！";
var nocreaterunpriv = "无可办理流程权限！";
var user_jb = "经办人";
var user_over1 = "尚未办理完毕，确认要结束流程吗？";
var user_over2 = "尚未办理完毕，不能结束流程！";
var user_next1 = "尚未办理完毕，确认要转交下一步骤吗？";
var user_next2 = "尚未办理完毕，不能转交流程！";
var g_pre_page = 1;
var g_now_page = 1;

var q_run_id = 0;
var q_flow_id = 0; 
var q_prcs_id = 0;
var q_flow_prcs = 0;
var q_op_flag = 1;

var now_sort = 0;
var parent = 0;
var force_pre_set = 0;			//是否强制前后缀

/* --- 自定义参数 ---*/
var nomoredata_1 = false;
var noshowPullUp_1 = <% out.print(T9MobileConfig.PAGE_SIZE >= total_items ? "true" : "false"); %>;
var nomoredata_15 = false;
var noshowPullUp_15 = true;

function pageInit(page_id)
{
		if(page_id == 1)//初始页
		{
			tiScroll_1 = new $.tiScroll({"page_type":'side',"nomoredata": nomoredata_1, "noshowPullUp":noshowPullUp_1});
			tiScroll_1.init();
		}
		if(page_id == 2)//主办
		{
			tiScroll_2 = new $.tiScroll({"page_id": 2, "listType": "readonly"});
			tiScroll_2.init();
		}
		if(page_id == 3)//表单
		{
			tiScroll_3 = new $.tiScroll({"page_id": 3, "listType": "readonly"});
			tiScroll_3.init();
		}
		if(page_id == 4)//会签
		{
			tiScroll_4 = new $.tiScroll({"page_id": 4, "listType": "readonly"});
			tiScroll_4.init();
		}
		if(page_id == 5)//转交步骤
		{
		try{
			}catch(ex){
			}
			tiScroll_5 = new $.tiScroll({"page_id": 5, "listType": "readonly"});
			tiScroll_5.init();
		}
		if(page_id == 6)//转交选人
		{
			tiScroll_6 = new $.tiScroll({"page_id": 6, "listType": "readonly"});
			tiScroll_6.init();
		}
		if(page_id == 7)//返回列表
		{
			tiScroll_7 = new $.tiScroll({"page_id": 7, "listType": "readonly"});
			tiScroll_7.init();
		}
		if(page_id == 8)//保存表单
		{
			tiScroll_8 = new $.tiScroll({"page_id": 8, "listType": "readonly"});
			tiScroll_8.init();
		}
		if(page_id == 9)//查看原始表单
		{
			tiScroll_9 = new $.tiScroll({"page_id": 9, "listType": "readonly"});
			tiScroll_9.init();
		}
		if(page_id == 10)//查看原始表单
		{
			tiScroll_10 = new $.tiScroll({"page_id": 10, "listType": "readonly"});
			tiScroll_10.init();
		}
		if(page_id == 11)//新建工作
		{
			tiScroll_11 = new $.tiScroll({"page_id": 11, "listType": "readonly"});
			tiScroll_11.init();
		}
		if(page_id == 12)//选择子分类
		{
			tiScroll_12 = new $.tiScroll({"page_id": 12, "listType": "readonly"});
			tiScroll_12.init();
		}
		if(page_id == 13)//新建工作
		{
			tiScroll_13 = new $.tiScroll({"page_id": 13, "listType": "readonly"});
			tiScroll_13.init();
		}
		if(page_id == 14)
	    {
	      	tiScroll_14 = new $.tiScroll({"page_type":'side',"page_id": 14, "listType": "readonly"});
	      	tiScroll_14.init();
	    }
	    if(page_id == 15)
	    {
	      	tiScroll_15 = new $.tiScroll({"page_type":'side',"page_id": 15,"nomoredata": nomoredata_15, "noshowPullUp": noshowPullUp_15,"onPullUp":pullUp_search_list, "onPullDown":pullDown_search_list});
	      	tiScroll_15.init();
	    }
		if(page_id == "attach_read")
		{
			tiScroll_attach_read = new $.tiScroll({"page_id": "attach_read", "listType": "attach_show"});
			tiScroll_attach_read.init();      
		}
}
</script>    
<%
	Map map = new HashMap();
	map.put("1" , T9MobileUtility.getHeadMap("" , "gohome();" , "首页","工作流" , "" , "showMenu(\"list_opts\");" , "操作"));
	map.put("2" , T9MobileUtility.getHeadMap("" , "reback(g_now_page,g_pre_page);" , "返回","工作办理" , "" , "showMenu(\"edit_opts\");" , "操作"));
	map.put("3" ,  T9MobileUtility.getHeadMap("" , "reback(g_now_page,g_pre_page);" , "返回","查看表单" , "" , "showMenu(\"form_opts\");" , "操作"));
	map.put("4" , T9MobileUtility.getHeadMap("" , "reback(4,pre_page);" , "返回","会签" , "saveSign" , "" , ""));
	map.put("5" , T9MobileUtility.getHeadMap("" , "reback(5,pre_page);" , "返回","转交下一步" , "" , "goOnWorkFlow();" , "继续"));
	map.put("6" ,  T9MobileUtility.getHeadMap("" , "reback(6,turn_back_page);" , "返回","转交选人" , "" , "turnUserWorkFlow();" , "提交"));
	map.put("7" ,  T9MobileUtility.getHeadMap("" , "reback(7,1);" , "返回","办理结束"));
	map.put("8" ,  T9MobileUtility.getHeadMap("" , "reback(8,1);" , "返回","保存表单" , "" , "showMenu(\"save_opts\");" , "操作"));
	map.put("9" ,  T9MobileUtility.getHeadMap("" , "reback(9,2);" , "返回","原始表单查看"));
	map.put("10" ,  T9MobileUtility.getHeadMap("" , "reback(10,2);" , "返回","回退", "" , "goOnSelBackWorkFlow();" , "确认"));
	map.put("11" ,  T9MobileUtility.getHeadMap("" , "reback(11,1);" , "返回","新建工作"));	
	map.put("12" ,  T9MobileUtility.getHeadMap("" , "new_reback();" , "返回","新建工作"));	
	map.put("13" ,  T9MobileUtility.getHeadMap("" , "reback(13, 12);" , "返回","新建工作", "" , "gotoWork(\"new_save\");" , "确认"));
	map.put("14" ,  T9MobileUtility.getHeadMap("" , "reback(14, 1);" , "返回","工作查询", "" , "gotoWork(\"search_list\");" , "确认"));
	map.put("15" ,  T9MobileUtility.getHeadMap("" , "reback(15,14);" , "返回","查询结果"));	
	map.put("attach_read" ,  T9MobileUtility.getHeadMap("" , "reback(\"attach_read\",g_pre_page);" , "返回","查看附件"));	
	map.put("seal" ,  T9MobileUtility.getHeadMap("" , "reback(\"seal\",pre_page);" , "返回","移动设备盖章","",  "goOnSeal();",  "确认"));	
%>
   
   <%=T9MobileUtility.buildHead(map) %>
   <%=T9MobileUtility.buildMessage()%>
   <%=T9MobileUtility.buildProLoading()%>
   
   <span class="mutiMenuLayer" style="display: none;">
		<div class="mutiMenu">
			<em></em>
			<div class="opts"></div>
		</div>
	</span>

	<!-- list of workflow -->
	<div id="page_1" class="pages tlist">
		<div id="wrapper_1" class="wrapper">
			<div id="scroller_1" class="scroller">
         <%=T9MobileUtility.buildPullDown()%>
         <ul class="comm-list" id="workflow_list">
      <%
      if (total_items > 0) {
        List<Map> list = (List)n.get("list");
        for (Map m : list) {
      %>
            <li class="<%=(String)m.get("CLASS") %>"
						q_id="<%=m.get("CREATE_TIME") %>" q_run_id="<%=(Integer)m.get("RUN_ID") %>"
						q_flow_id="<%=(Integer)m.get("FLOW_ID") %>" q_prcs_id="<%=(Integer)m.get("PRCS_ID") %>"
						q_flow_prcs="<%=(Integer)m.get("FLOW_PRCS") %>" q_op_flag="<%=(Integer)m.get("OP_FLAG") %>">
						<h3>[<%=(Integer)m.get("RUN_ID") %>] - <%=(String)m.get("FLOW_NAME") %> - <%=(String)m.get("RUN_NAME") %></h3>
						<p class="grapc"><%=(String)m.get("PRCS_NAME") %> <%=(String)m.get("OP_FLAG_DESC") %></p> <span
						class="ui-icon-rarrow"></span>
					</li>
         <% } %>
            </ul>
         <%
      }else {
         %>
        </ul>
            <div class="no_msg">暂无待处理工作流！</div>;
         <% } %>
           <%=T9MobileUtility.buildPullUp()%>
			
			
			</div>
		</div>
	</div>


<!-- page of edit workflow -->
	<div id="page_2" class="pages tcontent" style="display: none;">
		<div id="wrapper_2" class="wrapper tform_wrapper">
			<div id="scroller_2" class="scroller"></div>
		</div>
	</div>

	<!-- page of read form -->
	<div id="page_3" class="pages tcontent" style="display: none;">
		<div id="wrapper_3" class="wrapper">
			<div id="scroller_3" class="scroller"></div>
		</div>
	</div>

	<!-- page of sign -->
	<div id="page_4" class="pages tcontent" style="display: none;">
		<div id="wrapper_4" class="wrapper">
			<div id="scroller_4" class="scroller"></div>
		</div>
	</div>

	<!-- page of turn1 -->
	<div id="page_5" class="pages tcontent" style="display: none;">
		<div id="wrapper_5" class="wrapper tform_wrapper">
			<div id="scroller_5" class="scroller"></div>
		</div>
	</div>

	<!-- page of turn2 -->
	<div id="page_6" class="pages tcontent" style="display: none;">
		<div id="wrapper_6" class="wrapper tform_wrapper">
			<div id="scroller_6" class="scroller"></div>
		</div>
	</div>

	<!-- page of end -->
	<div id="page_7" class="pages tlist" style="display: none;">
		<div id="wrapper_7" class="wrapper">
			<div id="scroller_7" class="scroller"></div>
		</div>
	</div>

	<!-- page of save form -->
	<div id="page_8" class="pages tlist" style="display: none;">
		<div id="wrapper_8" class="wrapper">
			<div id="scroller_8" class="scroller"></div>
		</div>
	</div>

	<!-- page of form -->
	<div id="page_9" class="pages tcontent tzoom" style="display: none;">
		<div id="wrapper_9" class="wrapper">
			<div id="scroller_9" class="scroller"></div>
		</div>
	</div>

	<!-- page of sel_back -->
	<div id="page_10" class="pages tcontent" style="display: none;">
		<div id="wrapper_10" class="wrapper tform_wrapper">
			<div id="scroller_10" class="scroller"></div>
		</div>
	</div>

	<!-- page of new_flow -->
	<div id="page_11" class="pages tlist" style="display: none;">
		<div id="wrapper_11" class="wrapper">
			<div id="scroller_11" class="scroller">
				<ul class="comm-list comm-pic-list">

				</ul>
			</div>
		</div>
	</div>

	<!-- page of new_flow -->
	<div id="page_12" class="pages tlist" style="display: none;">
		<div id="wrapper_12" class="wrapper">
			<div id="scroller_12" class="scroller">
				<ul class="comm-list comm-pic-list">

				</ul>
			</div>
		</div>
	</div>
	
	<div id="page_13" class="pages tcontent" style="display: none;">
		<div id="wrapper_13" class="wrapper tform_wrapper">
			<div id="scroller_13" class="scroller"></div>
		</div>
	</div>
	<div id="page_14" class="pages tcontent" style="display: none;">
		<div id="wrapper_14" class="wrapper tform_wrapper">
			<div id="scroller_14" class="scroller"></div>
		</div>
	</div>
	<div id="page_15" class="pages tlist" style="display: none;">
		<div id="wrapper_15" class="wrapper">
			<div id="scroller_15" class="scroller">
				<%=T9MobileUtility.buildPullDown()%>
         	<ul class="comm-list" id="search_list">
         	
         	</ul>
         	<%=T9MobileUtility.buildPullUp()%>
			</div>
		</div>
	</div>

	<!-- page of attach_file -->
	<div id="page_attach_read" class="pages tcontent"
		style="display: none;">
		<div id="wrapper_attach_read" class="wrapper">
			<div id="scroller_attach_read" class="scroller"
				style="position: relative; width: 100%; height: 100%;">
				<div id="layer"
					style="position: absolute; left: 0; top: 0; height: 100%; width: 100%;"></div>
				<iframe id="file_iframe" name="file_iframe" class="attach_iframe"
					src=""></iframe>
			</div>
		</div>
	</div>
	
	<div id="list_opts" class="list_opts" style="display:none;">
	   <span class="new_flow">新建工作</span>
	   <span class="search_flow">工作查询</span>   
   </div>

   <div id="page_seal" class="pages tcontent" style="display:none;">
      <div id="wrapper_seal" class="wrapper">
         <div id="scroller_seal" class="scroller">
         </div>      
      </div>
   </div>
	<script type="text/javascript" src="<%=contextPath %>/mobile/workflow/js/udf-1.1.js"></script>
	<script type="text/javascript" src="<%=contextPath %>/mobile/workflow/js/workflow.js"></script>
	<script type="text/javascript">
function triggerFlowEvt(){
   $(".read_flow").bind("tap click",function(){gotoWork('form');});
   $(".stop_flow").bind("tap click",function(e){gotoWork('stop'); });
   $(".save_flow").bind("tap click",function(e){gotoWork('save'); });
   $(".sign_flow").bind("tap click",function(e){gotoWork('sign'); });
   $(".turn_flow").bind("tap click",function(e){gotoWork('turn'); });
   $(".new_flow").bind("tap click",function(e){gotoWork('new'); });
   $(".search_flow").bind("tap click",function(e){gotoWork('search'); });
   $(".sign_save_flow").bind("tap click",function(e){gotoWork('sign_save_flow'); });
   $(".continueEdit_flow").bind("tap click",function(e){gotoWork('continueEdit'); });
   $(".show_original_form").bind("tap click",function(e){gotoWork('show_original'); });
   $(".sel_flow").bind("tap click",function(e){gotoWork('sel'); });
   $("#overlay").bind("click",function(e){
      e.stopPropagation();
      if($(this).hasClass("overlayGray"))
         $.mutiMenu.hide();
   })   
}

function gotoWork(WORK_TYPE)
{   
   if(WORK_TYPE == "form")
   {
      getflowContent(); 
   }else if(WORK_TYPE == "turn")
   {
      turnWorkFlow();
   }else if(WORK_TYPE == "sign")
   {
      signWorkFlow();
   }else if(WORK_TYPE == "save")
   {
      saveWorkFlow();   
   }else if(WORK_TYPE == "continueEdit")
   {
      continueEditFlow();   
   }else if(WORK_TYPE == "stop")
   {
      stopWorkFlow();   
   }else if(WORK_TYPE == "sign_save_flow"){
   	saveSignWorkFlow();		
   }else if(WORK_TYPE == "show_original"){
   	showOriginalForm();
   }else if(WORK_TYPE == "sel"){
      selWorkFlow();
   } else if(WORK_TYPE == "new"){
	   newFlow();
   }else if(WORK_TYPE == "search"){
	   searchFlow();
	}else if(WORK_TYPE == "search_list"){
	   searchFlowList();
	}else if(WORK_TYPE == "new_save"){
	 saveNewWorkFlow();
   }
   $.mutiMenu.hide();
}

function goToSeal(item_check,cur_item)
{
	if(typeof window.Android != 'object')
    {
        showMessage("此操作仅支持客户端执行");
        return 'clientOnly';
    }
	var authData = window.Android.GetAuthCode();
	$.ajax({
	      type: 'POST',
	      url:contextPath+ '/t9/mobile/workflow/act/T9SealSelectAct/data.act',
	      cache: false,
	      data: {sessionid: p, SEAL_ITEM_CHECK: item_check,SEAL_CUR_ITEM: cur_item, "authData":authData},
	      beforeSend: function(){
	         $.ProLoading.show();
	      },
	      success: function(data)
	      {
		      $("#scroller_seal").html(data);
	    	    $("#page_seal").show('fast');
	    	    $("#header div[id^='header_']").hide();
	    	    $("#header_seal").show();
	    	    $.ProLoading.hide();
	      },
	      error: function(data){
	         $.ProLoading.hide();  
	         showMessage(getfature);
	      }
	   });
}

function checkMobileAuth()
{
	if(typeof window.Android != 'object')
    {
        showMessage("此操作仅支持客户端执行");
        return 'clientOnly';
    }
    
    var authData = window.Android.GetAuthCode();
    var checkAuth = "";
    $.ajax({
      type: 'POST',
      url: contextPath + '/t9/mobile/workflow/act/T9SealCheckAuthAct/data.act',
      cache: false,
      async:false,
      data: {sessionid: p,'authData': authData},
      success: function(data){
      	checkAuth = data;
      },
      error: function(data){
         showMessage("提交申请失败");
      }
   });
   return checkAuth;
}

function goOnSeal()
{
    var sealId = $("#page_seal select[name='SEAL_ID']").val();
    var sealPassword = $("#page_seal input[name='SEAL_PASSWORD']").val();
    var sealItemCheck = $("#page_seal input[name='SEAL_ITEM_CHECK']").val();
    var sealCurItem = $("#page_seal input[name='SEAL_CUR_ITEM']").val();    //NAME
    var checkResult = checkMobileAuth();
    
    if(checkResult == 'clientOnly')
    {
        return;    
    }
    
    if(checkResult != "+OK")
    {
    	showMessage("没有盖章权限");
    	return;
    }
    if($("#page_seal input[name='SEAL_PASSWORD']").val() == "")
    {
        showMessage("请输入印章密码！");
        return;
    }
	$.ajax({
	  type: 'GET',
	  url: contextPath + '/t9/mobile/workflow/act/T9SealSubmitAct/data.act',
	  cache: false, 
	  data: {"sessionid": p,'RUN_ID': q_run_id,'FLOW_ID': q_flow_id,'PRCS_ID': q_prcs_id,'FLOW_PRCS': q_flow_prcs,'sealId': sealId, 'sealPassword':sealPassword,'sealItemCheck':sealItemCheck,'sealCurItem':sealCurItem},
	  beforeSend: function()
	  {
	     $.ProLoading.show("提交验证中...");   
	  },
	  success: function(data){
	     $.ProLoading.hide(); 
	     if(data == "+OK") 
	     {
	        showMessage("盖章成功");
	        $("span[name='"+sealCurItem+"_START']").hide();
	        $("span[name='"+sealCurItem+"_END']").show();
	        $("#div_seal").append('<div class="seal_wrap"><img data_id="'+sealCurItem+'" class="mobile_seal" src="'+contextPath +'/t9/mobile/workflow/act/T9SealDataShowAct/data.act?FLOW_ID='+q_flow_id+'&RUN_ID='+q_run_id+'&ITEM_ID='+sealCurItem+'&CHECK_FIELD='+sealItemCheck+'"/><span onclick="del_seal(this)" class="mobile_seal_span" style="width: 16px;display:inline;">—</span></div>').show();
	        reback("seal",pre_page);
	     }
	     else
	     {
	        showMessage(data);
	     }
	  },
	  error: function(data){
	     $.ProLoading.hide();  
	     showMessage("盖章失败");
	  }
	});
}

function select_seal(obj) {
   if($(obj).val() != "") {
      $("#seal_show").html("<img class=\"mobile_seal\" src=\""+contextPath+"/t9/mobile/workflow/act/T9SealDataShowAct/show.act?id="+$(obj).val()+"\"/>");
   }
}

function set_width_seal(obj){
      $(obj).contents().find("img").width(120);
}
function del_seal(obj){
	var data_id = $(obj).parent().find("iframe").attr("data_id");
	var data_field = $("input[name='"+data_id+"']").attr("data_field");
	var seal_obj = $(obj);
	$.ajax({
    	type: 'POST',
    	url: contextPath + '/t9/mobile/workflow/act/T9SealDelAct/data.act',
    	cache: false,
    	data: "RUN_ID=" + q_run_id + "&FLOW_ID=" + q_flow_id + "&FLOW_PRCS=" + q_flow_prcs + "&PRCS_ID=" + q_prcs_id + "&DATA_ID=" + data_id + "&sessionid=" + p,
    	success: function(data)
    	{
    		seal_obj.parent().remove();
    		$("span[name='"+data_id+"_START']").show();
            $("span[name='"+data_id+"_END']").hide();
    	},
	    error: function(data){
	    	showMessage("删除失败");
	      	//alert(data);
	    }
   	});
	//$(this).parent().find("iframe").remove();
}

function showMenu(opts_type)
{
   if($("#"+opts_type).length > 0)
   {
      var menu = $("#"+opts_type).html();
      $.mutiMenu.init(menu);
      $.mutiMenu.show();
      triggerFlowEvt();
   }   
}

//处理返回操作
function new_reback()
{
	if(now_sort == parent)
		return ;
	if(parent == 0){
		reback(12,11);
		now_sort = 0;
		return ;
	}
	getFlowNewlist(parent);
}

//2012/4/25 16:26:41 lp 自定义下拉刷新功能，截断插件自带刷新

$("a.pda_attach").live("tap click",function(){
   readAttach($(this));
});
</script>
</body>
</html>


<script type="text/javascript">
var p = "<%=request.getSession().getId()%>";

/* --- 区别于pda的工作流地址 ---*/
var mobile_dataurl = "<%=contextPath%>/t9/mobile/workflow/act/T9PdaWorkflowIndexAct/data.act";
var mobile_contactlisturl = "<%=contextPath%>/t9/mobile/inc/act/T9PdaUserSelectAct/select.act";

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