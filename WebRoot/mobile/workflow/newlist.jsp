<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@  page import="java.util.Map" %>
<%@  page import="java.util.List" %>
<%@  page import="java.util.HashMap" %>
<%@  page import="t9.mobile.util.T9MobileUtility" %>
<%@  page import="t9.mobile.util.T9MobileConfig" %>
<%
    String contextPath = request.getContextPath();
    Map r = (Map)request.getAttribute("r");
    List<Map> list_sort = (List)r.get("list_sort");
    String sortNameTmp = (String)r.get("sortNameTmp");
    String sortId = (String)r.get("sortId");
    int parentId = (Integer)r.get("parentId");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=no">
    <link rel="stylesheet" href="<%=contextPath %>/mobile/workflow/mui/css/mui.min.css"/>
    <link rel="stylesheet" href="<%=contextPath %>/mobile/workflow/images/style.css"/>
    <script type="text/javascript" src="<%=contextPath %>/mobile/workflow/js/jquery-1.9.1.min.js"></script>
    <script type="text/javascript" src="<%=contextPath %>/mobile/workflow/js/iscroll.js"></script>
    <script type="text/javascript" src="<%=contextPath %>/mobile/workflow/js/workflow_new.js"></script>
    <link href="<%=contextPath %>/mobile/workflow/js/mobiscroll/css/mobiscroll.custom-2.6.2.min.css" rel="stylesheet" type="text/css">
    <script src="<%=contextPath %>/mobile/workflow/js/mobiscroll/js/mobiscroll.custom-2.6.2.min.js" type="text/javascript"></script>
    <script src="<%=contextPath %>/mobile/workflow/js/selectUserAndDept.js" type="text/javascript"></script>
    <script src="<%=contextPath %>/mobile/workflow/js/praserUtil.js" type="text/javascript"></script>
    <script src="<%=contextPath %>/mobile/workflow/mui/js/mui.min.js" type="text/javascript"></script>
    <title></title>
</head>
<script type="text/javascript">
//初始化流程数据
var q_run_id = 0;
var q_flow_id = 0; 
var q_prcs_id = 0;
var q_flow_prcs = 0;
var q_op_flag = 1;

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

mui.init();  
var contextPath = '<%=contextPath%>';
var p = "<%=request.getSession().getId()%>";

function tdCalendar(id, format) {
    if(format == 'yyyy-mm-dd'){
        $("#"+id).mobiscroll().date({
            preset : 'date', 
            theme: "android-ics light",
            lang: "zh",  
            mode: "clickpick",
            display: 'modal',
            dateOrder: 'yyyymmdd', //面板中日期排列格式
            dateFormat: 'yyyy-mm-dd', //返回结果格式化为年月格式  
            onSelect: function (valueText, inst) {  
                $(this).find(".mbase-menu-txt").html(valueText);  
            }
        });
    }else{
        var opt = {};
        opt.datetime = { 
            preset : 'datetime', 
            minDate: new Date(2012,3,10,9,22),
            dateOrder:"yyyymmdd" , 
            maxDate: new Date(2020,7,30,15,44) 
            ,stepSecond: 1 
        };
        $("#" + id).scroller('destroy').scroller(
           $.extend(opt.datetime, { 
                theme: "android-ics light", 
                mode: "clickpick", 
                display: "modal",
                dateFormat: 'yy-mm-dd ', 
                timeFormat: 'HH:ii:ss', // 日期输出格式
                lang: "zh" 
          }));
    }
}

$(document).ready(function(){
    //选择流程后创建新流程
    $(document).on("click tap", ".hk-process-item",function(){
        getFlowNew($(this).attr("q_id"));
    });

    //保存流程
    $(document).on("click tap", "#save_flow",function(){
        gotoWork('save');
    });
    
    //转交
    $(document).on("click tap", "#turn_flow",function(){
        gotoWork('turn');
    });
});

function gotoWork(WORK_TYPE){
    if(WORK_TYPE == "turn"){//转交
        turnWorkFlow();
    }else if(WORK_TYPE == "save"){//保存
        saveWorkFlow();
    }else if(WORK_TYPE == "stop"){
        stopWorkFlow();
    }else if(WORK_TYPE == "new_save"){
        saveNewWorkFlow();
    }
}

//新建流程-创建流水号
function getFlowNew(FLOW_ID){
    $.ajax({
        type: 'GET',
        url : contextPath + '/t9/mobile/workflow/act/T9PdaNewFlowAct/newEdit.act',
        cache: true,
        data: {'sessionid': p,'FLOW_ID': FLOW_ID},
        //beforeSend: function(){
        //      $.ProLoading.show();
        //},
        success: function(data){
            $(".vux-header-left a").attr("href", "#");//头部返回按钮
            $(".vux-header-title").html("新建工作");//头部中间内容
            $(".vux-header-right").hide(); //头部操作按钮
            $(".hk-menu").hide();//中间正文内容
            $(".hk-line").hide();
            $(".hk-process").hide();
            var json = eval(data);
            var runName = json[0].runName;
            var flowId = json[0].flowId;
            var AUTO_NEW = 1;
            saveNewWorkFlow(runName, flowId, AUTO_NEW)
        },
        error: function(data){
           //$.ProLoading.hide();  
           //showMessage(getfature);
        }
    });
}

/**保存工作流**/
function saveNewWorkFlow(runName, flowId, AUTO_NEW){
    $.ajax({
        type: 'GET',
        url : contextPath + '/t9/mobile/workflow/act/T9PdaNewFlowAct/newSubmit.act',
        cache: false,
        async: false,
        data: {'sessionid': p,'FLOW_ID': flowId, 'RUN_NAME':runName},
        //beforeSend: function(){
        //   $.ProLoading.show();
        //},
        success: function(data){
            //$.ProLoading.hide();
            if(data == "NORUNNAME"){
                showMessage(norunname);
                return;
            }else if(data == "NAMEREPEAT"){
                showMessage(namerepeat);
                return;
           }else if(data == "NOCREATERUN"){
                showMessage(nocreaterun);
                return;
           }else{
                $(".vux-header-left a").attr("href", "#");//头部返回按钮
                $(".vux-header-title").html("新建工作");//头部中间内容
                $(".vux-header-right").show(); //头部操作按钮
                $(".vux-header-right").show(); //头部操作按钮
                $(".hk-menu").hide();//中间正文内容
                $(".hk-line").hide();
                $(".hk-process").hide();
                //$("#page_2 > #wrapper_2 > #scroller_2").empty().append(data);
                //$("div[id^='page_']").hide();
                var json = eval(data);
                q_run_id = json[0].q_run_id; 
                q_flow_id = json[0].q_flow_id;
                q_prcs_id = json[0].q_prcs_id;
                q_flow_prcs = json[0].q_flow_prcs;
                editWorkFlow(q_run_id, q_flow_id, q_prcs_id, q_flow_prcs);
                //g_pre_page = 1;
                //g_now_page = 2;
                //refreshList();
            }
        },
        error: function(data){
            //$.ProLoading.hide();  
            //showMessage(getfature);
        }
    });
}

//主办工作
function editWorkFlow(q_run_id, q_flow_id, q_prcs_id, q_flow_prcs){
   $.ajax({
      type: 'GET',
      url : contextPath + '/t9/mobile/workflow/act/T9PdaHandlerAct/edit.act',
      cache: false,
      data: {'sessionid': p, 'RUN_ID': q_run_id,'FLOW_ID': q_flow_id,'PRCS_ID': q_prcs_id,'FLOW_PRCS': q_flow_prcs},
      //beforeSend: function(){
      //   $.ProLoading.show();
      //},
      success: function(data){
         //fileReadPage = 2;
         //pre_page = 2;
         //$.ProLoading.hide();
         if(data == "NOEDITPRIV"){
            //showMessage(noeditpriv);
            //return;
            showPageMessage(noeditpriv);
            delete_flow();
            return;
         }else{
                $(".vux-header-left a").attr("href", "#");//头部返回按钮
                $(".vux-header-title").html("新建工作");//头部中间内容
                $(".vux-header-right").show(); //头部操作按钮
                $(".vux-header-right").show(); //头部操作按钮
                $(".hk-menu").hide();//中间正文内容
                $(".hk-line").hide();
                $(".hk-process").hide();
                //表单页面
                $(".hk-trave-apply").empty().append(data);
                $(".hk-trave-apply").show();
                
            //$("div[id^='page_']").hide();
            //$("#page_2 > #wrapper_2 > #scroller_2").empty().append(data);
            //$("#page_2").show('fast',function(){
            //    pageInit(2);
            //   $("#page_2 .container .read_detail:last").addClass("endline");
            //});
            //$("#header div[id^='header_']").hide();
            //$("#header_2").show();
         }
      },
      error: function(data){
         //$.ProLoading.hide();  
         //showMessage(getfature);
      }
   });
}
</script>
<body>
<div class="hk-workapproval">
    <!--页面头部-->
    <div class="hk-header">
        <div class="vux-header">
            <div class="vux-header-left">
                <a href="javaScript:void(0);"><img src="<%=contextPath %>/mobile/workflow/images/comm/btn_topbar_back@2x.png" alt=""/></a>
            </div>
            <h1 class="vux-header-title">工作审批</h1> 
            <div class="vux-header-right" style="display:none;">
                <!--<a onclick="goOnWorkFlow();">继续</a>-->
            </div>
        </div>
    </div>
    <!--顶部MENU-->
    <div class="hk-menu">
        <div class="vux-flexbox">
            <div class="vux-flexbox-item">
                <div class="hk-menu-item">
                    <img src="<%=contextPath %>/mobile/workflow/images/comm/icon_work_top_01@2x.png" alt="">
                    <p>工作查询</p>
                </div>
            </div>
            <div class="vux-flexbox-item">
                <div class="hk-menu-item">
                    <img src="<%=contextPath %>/mobile/workflow/images/comm/icon_work_top_02@2x.png" alt="">
                    <p>待办流程</p>
                </div>
            </div>
            <div class="vux-flexbox-item">
                <div class="hk-menu-item">
                    <img src="<%=contextPath %>/mobile/workflow/images/comm/icon_work_top_03@2x.png" alt="">
                    <p>已办流程</p>
                </div>
            </div>
            <div class="vux-flexbox-item">
                <div class="hk-menu-item">
                    <img src="<%=contextPath %>/mobile/workflow/images/comm/icon_work_top_04@2x.png" alt="">
                    <p>关注流程</p>
                </div>
            </div>
        </div>
    </div>
    <%
    for (Map m : list_sort) {
        String title = (String)m.get("title");
        String json =(String) m.get("json");
        Integer key = (Integer)m.get("key");
        boolean isFolder = (Boolean)m.get("isFolder");
        boolean isLazy = (Boolean)m.get("isLazy");
        List<Map> list_type = (List)m.get("list_type");
    %>
        <!--横线8px-->
        <div class="hk-line"></div>
        <!--流程-->
        <div class="hk-process">
            <div class="cell-title"><%=title%></div>
            <div class="hk-process-cell">
            <div class="hk-process-cell-row">
            <%
                for(int i = 0; i < list_type.size(); i++){
                    Map map_ = list_type.get(i);
                    String title_ = (String)map_.get("title");
                    Integer key_ = (Integer)map_.get("key");
            %>
                    <div class="hk-process-cell-row-item">
                        <div class="hk-process-item" q_id="<%=key_%>">
                            <img src="<%=contextPath %>/mobile/workflow/images/comm/icon_work_xz_01@2x.png" alt="">
                            <span><%=title_%></span>
                        </div>
                    </div>
            <%
                }
            %>
            </div>
            </div>
        </div>
    <%
    }
    %>
</div>
<!--审批表单页面-->
<div class="hk-trave-apply" style="display:none;"></div>

<!-- 转交第一步，选择下一步 -->
<div id="flow_chooseNext" style="display: none;"></div>

<!-- 转交第二步，选人 -->
<div id="flow_chooseUser" style="display: none;"></div>

<!-- 操作结束 -->
<div id="flow_end" style="display: none;"></div>

<!--日期弹出框-->
<div class="dw-hidden" role="alert"></div>

<!--选择部门用户控件-->
<div id="treeDiv" style="display: none;">
<center>
    <input onclick="jQuery('#treeDiv').hide();jQuery('.hk-trave-apply').show();" type="button" value="确定">
</center>
<table style="width: 100%;">
    <tbody>
    <tr>
        <td id="left" style="width: 50%; vertical-align: top;"></td>
        <td id="center" style="width: 25%; vertical-align: top;"></td>
        <td id="right" style="width: 25%; vertical-align: top;"></td>
    </tr>
    </tbody>
</table>
</div>
</body>
</html>
