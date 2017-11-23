<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@  page import="java.util.Map" %>
<%@  page import="java.util.List" %>
<%@  page import="t9.core.util.T9Utility" %>
<%@  page import="java.util.HashMap" %>
<%@  page import="t9.mobile.util.T9MobileUtility" %>
<%@  page import="t9.mobile.util.T9MobileConfig" %>
<%@ include file="/mobile/workflow/header.jsp" %>
<%
Map r = (Map)request.getAttribute("r");
String runName =(String) r.get("runName");
int runId =(Integer) r.get("runId");
int flowId =(Integer) r.get("flowId");
int prcsId =(Integer) r.get("prcsId");
int flowPrcs =(Integer) r.get("flowPrcs");

String flowType =(String) r.get("flowType");
String sealImg =(String) r.get("sealImg");

String opFlag =(String) r.get("opFlag");
String beginTime =(String) r.get("beginTime");
String feedbackFlag =(String) r.get("feedbackFlag");
String allowBack =(String) r.get("allowBack");

String attachmentName =(String) r.get("attachmentName");
String attachmentId =(String) r.get("attachmentId");
String imgPath = (String)request.getAttribute("imgPath");
//String formMsg = (String)r.get("formMsg");
List<Map> list = (List<Map>)r.get("feedbacks");
List<Map> list2 = (List<Map>)r.get("prcs");
String sessionId = request.getSession().getId();
Map<String, Map<String, String>> formMaps = (Map<String, Map<String, String>>)r.get("formMaps");

String refreshFlag = request.getParameter("refreshFlag");
%>
<style>
    .mui-card .mui-control-content {
        padding: 10px;
    }
    .mui-control-content {
        height:500px;
    }
</style>
<script type="text/javascript">
    var refreshFlag = <%=refreshFlag%>;
    mui.init({keyEventBind: { backbutton: false }});
    
    mui.back = function(){
        var display = jQuery("#flowFormEdit").css("display");
        if(display == "none"){//编辑被隐藏
            jQuery("#flowChooseStep").hide();
            jQuery("#flowChooseUser").hide();
            jQuery("#flowBack").hide();
            jQuery("#flowFormEdit").show();
        }else{
            closeWin();
        }
    }
    //初始化流程数据
    var q_run_id = <%=runId%>;
    var q_flow_id = <%=flowId%>; 
    var q_prcs_id = <%=prcsId%>;
    var q_flow_prcs = <%=flowPrcs%>;
    var q_op_flag = 1;
    var p = "<%=request.getSession().getId()%>";
    jQuery(function(){
        var dates = jQuery('input[flowformdate="flowformdate"]');
        dates.each(function(i, btn) {
            btn.addEventListener('tap', function() {
                var optionsJson = this.getAttribute('data-options') || '{}';
                var options = JSON.parse(optionsJson);
                var id = this.getAttribute('id');
                var value = this.getAttribute('value');
                var picker = new mui.DtPicker(options);
                if(value){
                    picker.setSelectedValue(value); 
                }
                picker.show(function(rs) {
                    jQuery("#"+id).val(rs.text);
                    picker.dispose();
                });
            }, false);
        });
    });
    var isEdit = true;
    
    function closeWin(){
        if(refreshFlag && (refreshFlag == 1 || refreshFlag == 3)){
            if(refreshFlag == 3){
                window.parent.reSearch();
            }else{
                window.parent.location.reload();
            }
        }
        var index = parent.layer.getFrameIndex(window.name); //获取窗口索引
        parent.layer.close(index);
    }
    
    function backFlowFun(module){
        if(module){
            //jQuery(".hk-workapproval").hide();
            jQuery("#flowFormEdit").hide();
            jQuery("#flowChooseStep").hide();
            jQuery("#flowChooseUser").hide();
            jQuery("#flowBack").hide();
            if(module == 'flowFormEdit'){//流程编辑
                //jQuery(".hk-workapproval").show();
                closeWin();
            }else if(module == 'flowChooseStep'){//选择下一步
                jQuery("#flowFormEdit").show();
            }else if(module == 'flowChooseUser'){//选人
                jQuery("#flowFormEdit").show();
            }else if(module == 'flowBack'){//回退
                jQuery("#flowFormEdit").show();
            }
        }
        return false;
    }
</script>
<body>
<div id="flowFormEdit">
    <header id="header" class="mui-bar mui-bar-nav">
        <div class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left" onclick="backFlowFun('flowFormEdit')"></div>
        <h1 class="mui-title">编辑流程</h1>
    </header>
    <div class="mui-content">
        <div style="padding: 10px 10px;">
             <div id="segmentedControl" class="mui-segmented-control">
                 <a class="mui-control-item mui-active" href="#item1">单据详情</a>
                 <a class="mui-control-item" href="#item2">审批记录</a>
             </div>
        </div>
        <form  action="<%=contextPath %>/t9/mobile/workflow/act/T9PdaHandlerAct/editSubmit.act" method="post" name="form1" id="edit_from" onsubmit="return false;">
        <input type="hidden" name="P" value="<%=sessionId %>">
        <input type="hidden" name="FLOW_ID" value="<%=flowId %>">
        <input type="hidden" name="RUN_ID" value="<%=runId %>">
        <input type="hidden" name="PRCS_ID" value="<%=prcsId %>">
        <input type="hidden" name="FLOW_PRCS" value="<%=flowPrcs %>">
        <input type="hidden" name="DO_ACTION" value="">
        <input type="hidden" name="FLOW_TYPE" value="<%=flowType %>">
        <%=formMaps.get("others").get("content")%>
        <div>
        <div id="item1" class="mui-slider-item mui-control-content mui-scroll-wrapper mui-active">
        <div id="forms" class="mui-content-padded" style="margin: 5px;">
            <div class="mui-input-group">
            <%
                if(formMaps != null){
                    for (Map.Entry<String, Map<String, String>> items : formMaps.entrySet()) {
                        String title = items.getKey();
                        String name = items.getValue().get("name");
                        String clazz = items.getValue().get("clazz");
                        String type = items.getValue().get("type");
                        String tag = items.getValue().get("tag");
                        String content = items.getValue().get("content");
                        if(!title.equals("others") && tag != null && !tag.equals("textarea") && (clazz == null || (clazz != null && !clazz.equals("list_view") && !clazz.equals("radio")))){
                            if(tag.equals("select")){
                        %>
                                <div class="mui-input-row"><label><%=title%>：</label><%=content%></div>
                        <%  }else if(type != null && type.equals("checkbox")){
                        %>
                                <div class="mui-input-row mui-checkbox"><label><%=title%>：</label><%=content%></div>
                        <%  }else{  
                                if(content != null && (content.trim().startsWith("<input") || content.trim().startsWith("<INPUT")) && !content.contains("type=")){
                                    content = "<input type=\"text\" " + content.trim().substring(7, content.trim().length());
                        %>
                                    <div class="mui-input-row"><label><%=title%>：</label><%=content%></div>
                        <%
                                }else{
                        %>
                                    <div class="mui-input-row"><label><%=title%>：</label><%=content%></div>
                        <%      }
                            }
                        }
                    }
                }
            %>
            </div>
            <%
                if(formMaps != null){
                    for (Map.Entry<String, Map<String, String>> items : formMaps.entrySet()) {
                        String title = items.getKey();
                        String name = items.getValue().get("name");
                        String clazz = items.getValue().get("clazz");
                        String tag = items.getValue().get("tag");
                        String content = items.getValue().get("content");
                        if(!title.equals("others") && ((tag != null && tag.equals("textarea")) || (clazz != null && (clazz.equals("list_view") || clazz.equals("radio"))))){
                            if(clazz != null && clazz.equals("radio")){
                       %>
                                <h5 class="mui-content-padded"><%=title%>：</h5>
                                <div class="mui-card">
                                    <div class="mui-input-group"><%=content%></div>
                                </div>
                       <%   }else if(tag != null && tag.equals("textarea")){%>
                                <h5 class="mui-content-padded"><%=title%>：</h5>
                                <div class="mui-input-row" style="margin:10px;"><%=content%></div>
                       <%   }else{
                       %>
                                <h5 class="mui-content-padded"><%=title%>：</h5>
                                <div class="mui-card">
                                    <div class="mui-input-row" style="margin: 10px 5px;"><%=content%></div>
                                </div>
                       <%  }
                       }
                    }
                }
            %>
            <div class="mui-button-row">
            <% if ("1".equals(opFlag)) { %>
                <button type="button" class="mui-btn mui-btn-primary" id="turn_flow" onclick="LVsubmit();gotoWork('turn')">转交</button>
            <% } %> 
            <button type="button" class="mui-btn mui-btn-primary" id="save_flow" onclick="LVsubmit();gotoWork('save');">保存</button>
            <% if ("0".equals(opFlag)) { %>
                <button type="button" class="mui-btn mui-btn-warning" onclick="stopWorkFlow();">办理完毕</button>
            <% } %>
            <% if (("1".equals(allowBack) || "2".equals(allowBack)) && flowPrcs != 1) { %>
                <button type="button" class="mui-btn mui-btn-warning" onclick="selWorkFlow();">回退</button>
            <% } %>
            <% if ("2".equals(flowType)) { %>
                <button type="button" class="mui-btn mui-btn-warning" onclick="stopWorkFlow();">结束流程</button>
            <% } %>
            </div>
            <div class="mui-button-row"></div>
        </div>
        </div>
        <div id="item2" class=" mui-control-content mui-scroll-wrapper">
            <div id="forms" class="mui-content-padded" style="margin: 5px;">
                <div class="mui-input-group">
                    <%
                        for (Map m : list2) {
                            int prcsId3 = (Integer) m.get("prcsId");
                            List<Map> prcs = (List<Map>) m.get("list");
                            for (int i = 0; i < prcs.size(); i++){
                                Map p = (Map)prcs.get(i);
                                int flowPrcs2 = (Integer) p.get("flowPrcs");
                                String prcsName = (String) p.get("prcsName");
                                String title = "";
                                List<Map> us = (List<Map>)p.get("user");
                                for (Map u : us) {
                                    boolean isOp = (Boolean)u.get("isOp");
                                    String deptName = (String)u.get("deptName");
                                    String userName = (String)u.get("userName");
                                    String stateStr = (String)u.get("state");
                                    String timeOutFlag = (String)u.get("timeOutFlag");
                                    String timeOut = (String)u.get("timeOut");
                                    String timeUsed = (String)u.get("timeUsed");
                                    String beginTime2 = (String)u.get("beginTime");
                                    String deliverTime = (String)u.get("deliverTime");
                                    int state = Integer.parseInt(stateStr);
                                    String prcsUserName = "";
                                    if(isOp){
                                        prcsUserName = userName + " 主办";
                                    }else{
                                        prcsUserName = userName;
                                    }
                                    if(!title.trim().equals("")){
                                        title += "<br>";
                                    }
                                    if (state == 1) {
                                        title += "&nbsp;" + prcsUserName + "&nbsp;[<font color=green>未接收办理</font>]";
                                    } else if(state == 2){
                                        title += "&nbsp;" + prcsUserName + "&nbsp;[<font color=green>办理中,已用时：" + timeUsed + "</font>]";
                                        if(!T9Utility.isNullorEmpty(timeOutFlag)){
                                            title += "<span style=\"color:red\">限时"+ timeOut +"小时," + timeUsed + "</span>";
                                        }
                                        title += "<br>&nbsp;开始于：" + beginTime2;
                                    }else if(state == 3){
                                        title += "&nbsp;"+ prcsUserName +"&nbsp;[<font color=green>已转交下步,用时：" + timeUsed + "</font>]";
                                        title += "<br>&nbsp;开始于：" + beginTime2;
                                        if(!T9Utility.isNullorEmpty(deliverTime)){
                                            title += "<br>&nbsp;结束于：" + deliverTime;
                                        }
                                    }else if(state == 4){
                                        title += "&nbsp;"+ prcsUserName +"&nbsp;[<font color=green>已办结,用时：" + timeUsed + "</font>]";
                                        title += "<br>&nbsp;开始于：" + beginTime2;
                                        if(!T9Utility.isNullorEmpty(deliverTime)){
                                            title += "<br>&nbsp;结束于：" +deliverTime;
                                        }
                                    }else if(state == 5){
                                        title += "&nbsp;" + prcsUserName + "&nbsp;[预设经办人]";
                                    }
                                }
                                %>
                                <div class="mui-input-row" style="padding:10px 0px 10px 10px;">
                                    &nbsp;序号<%=flowPrcs2 %>：<%=prcsName %><br/>
                                    <%=title %>
                                </div>
                                <%
                            }
                        }
                    %>
                </div>
            </div>
        </div>
        </div>
        </form>
        <!--选择部门用户控件-->
        <div id="treeDiv" style="display:none;">
        <center>
            <input onclick="jQuery('#treeDiv').hide();jQuery('#forms').show();" type="button" value="确定">
        </center>
        <table style="width: 100%;height:100%;">
            <tbody>
            <tr>
                <td id="left" style="width: 50%; vertical-align: top;"></td>
                <td id="center" style="width: 25%; vertical-align: top;"></td>
                <td id="right" style="width: 25%; vertical-align: top;"></td>
            </tr>
            </tbody>
        </table>
        </div>
    </div>
</div>
<div id="flowChooseStep" style="display:none;">
    <header id="header" class="mui-bar mui-bar-nav">
        <a class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left" onclick="backFlowFun('flowChooseStep')"></a>
        <h1 class="mui-title">转交下一步</h1>
    </header>
    <div class="mui-content"></div>
</div>
<div id="flowChooseUser" style="display:none;">
    <header id="header" class="mui-bar mui-bar-nav">
        <a class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left" onclick="backFlowFun('flowChooseUser')"></a>
        <h1 class="mui-title">转交选人</h1>
    </header>
    <div class="mui-content"></div>
</div>
<div id="flowBack" style="display:none;">
    <header id="header" class="mui-bar mui-bar-nav">
        <a class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left" onclick="backFlowFun('flowBack')"></a>
        <h1 class="mui-title">流程回退</h1>
    </header>
    <div class="mui-content"></div>
</div>
</body>
</html>
<script>
    (function($) {
        $('#item1').scroll({
            indicators: true //是否显示滚动条
        });
        $('#item2').scroll({
            indicators: true //是否显示滚动条
        });
        jQuery(".mui-control-content").css("height", (document.documentElement.clientHeight-100) + "px")
    })(mui);
</script>