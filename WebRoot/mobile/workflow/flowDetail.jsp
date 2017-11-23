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
    .html-disabled {
        border: 0 !important;
        -webkit-appearance: none;
    }
</style>
<script type="text/javascript">
    mui.init({keyEventBind: { backbutton: true }});
    
    mui.back = function(){
        closeWin();
    }
    var p = "<%=request.getSession().getId()%>";
    var isEdit = true;
    
    function closeWin(){
        var index = parent.layer.getFrameIndex(window.name); //获取窗口索引
        parent.layer.close(index);
    }
    jQuery(function(){
        jQuery('input,select,textarea',jQuery('form[name="my_form"]')).attr('disabled',"disabled");
        jQuery('input,select,textarea',jQuery('form[name="my_form"]')).attr('onclick',"");
        jQuery('input,select,textarea',jQuery('form[name="my_form"]')).addClass("html-disabled");
    });
    
</script>
<body>
<header id="header" class="mui-bar mui-bar-nav">
    <div class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left"></div>
    <h1 class="mui-title">审批详情</h1>
</header>
<div class="mui-content">
    <div style="padding: 10px 10px;">
         <div id="segmentedControl" class="mui-segmented-control ">
             <a class="mui-control-item mui-active" href="#item1">单据详情</a>
             <a class="mui-control-item" href="#item2">审批记录</a>
         </div>
    </div>
    <form id="my_form" name="my_form">
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
</div>
</body>
</html>
<script>
    (function($) {
        $('#item1').scroll({
            indicators: true //是否显示滚动条
        });
        jQuery(".mui-control-content").css("height", (document.documentElement.clientHeight-100) + "px")
    })(mui);
</script>
