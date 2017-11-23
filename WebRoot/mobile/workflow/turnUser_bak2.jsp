<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@  page import="java.util.Map" %>
<%@  page import="java.util.List" %>
<%@  page import="t9.core.util.T9Utility" %>
<%@  page import="java.util.HashMap" %>
<%@  page import="t9.mobile.util.T9MobileUtility" %>
<%@  page import="t9.mobile.util.T9MobileConfig" %>
<%
String contextPath = request.getContextPath();
String PRCS_ID_NEXT = request.getParameter("PRCS_ID_NEXT");
String flowType = (String)request.getAttribute("flowType");
int prcsId = (Integer)request.getAttribute("prcsId");

Map r1 = (Map)request.getAttribute("r");
String runName =(String) r1.get("runName");
String beginUser =(String) r1.get("beginUser");
int prcsIdNext = 0 ;
if (r1.containsKey("nextPrcs")) 
    prcsIdNext = (Integer) r1.get("nextPrcs");
String preSet = "";
if (r1.containsKey("nextPrcs")) 
    preSet =(String) r1.get("preSet");
String notAllFinish = "";
if (r1.containsKey("notAllFinish")) 
    notAllFinish = T9Utility.null2Empty((String) r1.get("notAllFinish"));
String turnPriv = "";
if (r1.containsKey("isAllowTurn")) 
    turnPriv = T9Utility.null2Empty((String) r1.get("isAllowTurn"));
%>
<div class="container">
    <div class="tform tformshow">
        <div class="read_detail"><em>工作名称/文号：</em><%=runName %></div>
        <div class="read_detail"><em>发起人：</em><%=beginUser %></div>
        <form action="<%=contextPath %>/t9/mobile/workflow/act/T9PdaTurnAct/turnSubmit.act"  method="post" name="form1" onsubmit="return false;">
        <% if ("2".equals(flowType)) {
            String PRCS_USER_OP_CB =(String) r1.get("PRCS_USER_OP_CB");
            String PRCS_USER_OP_ZB =(String) r1.get("PRCS_USER_OP_ZB");
            String prcsName = "";
            if (!T9Utility.isNullorEmpty(preSet)) {
                prcsName = "本步骤为预设步骤";
            }
        %>
            <div class="tform tformshow">
                <div class="read_detail read_detail_header">第<%=prcsIdNext %>步<%=prcsName %></div>
                <div class="read_detail read_detail_fem" id="USER_ZB_<%=prcsIdNext %>">主办人：<%=PRCS_USER_OP_ZB %></div>
                <div class="read_detail read_detail_fem endline" id="USER_CB_<%=prcsIdNext %>">经办人：<%=PRCS_USER_OP_CB %></div>
                <% 
                if (T9Utility.isNullorEmpty(preSet) || prcsId ==1) {
                %>
                    <div id="search_box">
                       <div id="input_box">
                           <input type="text" id="USER_NAME_<%=prcsIdNext %>" did="USER_PLIST_<%=prcsIdNext %>" name="USER_NAME" value="" autocapitalize="off" autocorrect="off"/>   
                       </div>
                    </div>
                <%
                String PLIST_BTN = "";
                String PLIST_NODATE = "";
                String PLIST = "";
                String CHECKBOX=  "";
                int i = 0 ;
                List<Map> users = (List<Map>)r1.get("users");
                for (Map m : users) {
                    i++;
                    String style = (i > 10) ? " style='display:none' " : "";
                    String deptName = (String)m.get("deptName");
                    String userName = (String)m.get("userName");
                    String userId = (String)m.get("userId");
                    int seqId = (Integer)m.get("seqId");
                    PLIST += "<li class=\"\" "+style+"  q_id=\""+seqId+"\"  q_user_id=\""+userId+"\" q_name=\""+userName+"\">"
                           + "<h3>"+userName+"</h3>"
                           + "<p class=\"grapc\">部门："+deptName+"&nbsp;</p>"
                           + "<span class=\"ui-li-text\">"
                           + "<a href=\"javascript:;\" class=\"ui-li-text-a zb\">主办</a>"
                           + "</span>"
                           + "</li>";
                }
                if (users.size() > 10) {
                    PLIST_BTN = "<div id=\"USER_SHOW_"+prcsIdNext+"\" class=\"appendList cp\"><a href=\"javascript:;\" class=\"bga\" did=\"USER_PLIST_"+prcsIdNext+"\" onclick=\"showList(this);\">点击显示全部（共"+users.size() +"人）</a></div>";
                }
                PLIST_NODATE = "<div id=\"USER_NODATE_"+prcsIdNext+"\"  style=\"display:none\" class=\"appendList cp\">没有查询到相关结果</div>";
                %>
                <ul class="comm-list comm-pic-list" id="USER_PLIST_<%=prcsIdNext %>"><%=PLIST %></ul>
                <%=PLIST_BTN %>
                <%=PLIST_NODATE %>
                <script type="text/javascript">
                    workFlowSearch_<%=prcsIdNext %> = new $.workFlowSearch({input:"#USER_NAME_<%=prcsIdNext %>",list:"#USER_PLIST_<%=prcsIdNext %>",appendDom_top:"#TOP_DEFAULT_<%=prcsIdNext %>",appendDom_zb:"#USER_ZB_<%=prcsIdNext %>", appendDom_cb:"#USER_CB_<%=prcsIdNext %>", showbtn:"#USER_SHOW_<%=prcsIdNext %>", nodate:"#USER_NODATE_<%=prcsIdNext %>", pageScroll:"oiScroll_6"});
                    workFlowSearch_<%=prcsIdNext %>.init();
                    workFlowSearch_<%=prcsIdNext %>.refresh();
                    var allow_zb_isnull_<%=prcsIdNext %> = false;
                </script>
                <%}else{  %> 
                <script type="text/javascript">
                    $("#USER_ZB_<%=prcsIdNext %>").find("em").off("click");
                    $("#USER_CB_<%=prcsIdNext %>").find("em").off("click");
                </script>
                <% } %> 
            </div>
        <% 
        } else {
            List<Map> prcss = (List<Map>)r1.get("list");
            for (Map p : prcss) {
                String prcsName =(String) p.get("prcsName");
                String userLock =(String) p.get("userLock");
                String topDefault =(String) p.get("topDefault");
                String prcsNext = (String)p.get("prcsNext");
                List<String[]> zb = null;
                List<String[]> jb = null;
                String PRCS_USER_OP_ZB = "";
                if(zb != null && zb.size() > 0){
                    for (String[] z : zb) {
                        PRCS_USER_OP_ZB = "<em uid='"+z[0]+"' userid='"+z[1]+"'>"+z[2]+"<span>—</span></em>";
                    }
                }
                String PRCS_USER_OP_CB = "";
                if(jb != null && jb.size() > 0){
                    for (String[] z : jb) 
                        PRCS_USER_OP_CB += "<em uid='"+z[0]+"' userid='"+z[1]+"'>"+z[2]+"<span>—</span></em>";
                }
                %>
                <div class="tform tformshow">
                    <div class="read_detail read_detail_header">第<%=prcsNext %>步<%=prcsName %></div>
                    <div class="read_detail read_detail_fem">主办人相关：
                        <select name="TOP_DEFAULT_<%=prcsNext %>" id="TOP_DEFAULT_<%=prcsNext %>"  <%=("0".equals(userLock) ? "onchange=this.value=" + topDefault : "")%>>
                        <option value=0 <%=("0".equals(topDefault) ? "selected" : "")%>>明确指定主办人</option>
                        <option value=2  <%=("2".equals(topDefault) ? "selected" : "")%>>无主办人会签</option>
                        <option value=1  <%=("1".equals(topDefault) ? "selected" : "")%>>先接收者为主办</option>
                        </select>
                    </div>
                    <div class="read_detail read_detail_fem" id="USER_ZB_<%=prcsNext %>">主办人：<%=PRCS_USER_OP_ZB %></div>
                    <div class="read_detail read_detail_fem <%=("0".equals(userLock) ? "endline" : "")%>" id="USER_CB_<%=prcsNext %>">经办人：<%=PRCS_USER_OP_CB %></div>
                    <% 
                    if ("1".equals(userLock) || jb.size() == 0 ) {%>
                        <div id="search_box">
                         <div id="input_box">
                             <input type="text" id="USER_NAME_<%=prcsNext %>" did="USER_PLIST_<%=prcsNext %>" name="USER_NAME" value="" autocapitalize="off" autocorrect="off"/>   
                         </div>
                        </div>
                    <%
                    List<String[]> users = (List<String[]>)p.get("users");
                    String PLIST_BTN = "";
                    String PLIST_NODATE = "";
                    String PLIST = "";
                    String CHECKBOX=  "";
                    int b = 0 ;
                    for (String[] m : users) {
                        b++;
                        String style = (b > 10) ? " style='display:none' " : "";
                        String deptName =  m[3];
                        String userName =  m[2];
                        String userId =  m[1];
                        String seqId = m[0];
                        PLIST += "<li class=\"\" "+style+"  q_id=\""+seqId+"\"  q_user_id=\""+userId+"\" q_name=\""+userName+"\">"
                                + "<h3>"+userName+"</h3>"
                                + "<p class=\"grapc\">部门："+deptName+"&nbsp;</p>"
                                + "<span class=\"ui-li-text\">"
                                + "<a href=\"javascript:;\" class=\"ui-li-text-a zb\">主办</a>"
                                + "</span>"
                                + "</li>";
                    }
                    if (b > 10) {
                        PLIST_BTN = "<div id=\"USER_SHOW_"+prcsNext+"\" class=\"appendList cp\"><a href=\"javascript:;\" class=\"bga\" did=\"USER_PLIST_"+prcsNext+"\" onclick=\"showList(this);\">点击显示全部（共"+b +"人）</a></div>";
                    }
                    PLIST_NODATE = "<div id=\"USER_NODATE_"+prcsNext+"\"  style=\"display:none\" class=\"appendList cp\">没有查询到相关结果</div>";
                    %>
                    <ul class="comm-list comm-pic-list" id="USER_PLIST_<%=prcsNext %>"><%=PLIST %></ul>
                    <%=PLIST_BTN %>
                    <%=PLIST_NODATE %>
                    <script type="text/javascript">
                        workFlowSearch_<%=prcsNext %> = new $.workFlowSearch({input:"#USER_NAME_<%=prcsNext %>",list:"#USER_PLIST_<%=prcsNext %>",appendDom_top:"#TOP_DEFAULT_<%=prcsNext %>",appendDom_zb:"#USER_ZB_<%=prcsNext %>", appendDom_cb:"#USER_CB_<%=prcsNext %>", showbtn:"#USER_SHOW_<%=prcsNext %>", nodate:"#USER_NODATE_<%=prcsNext %>", pageScroll:"oiScroll_6"});
                        workFlowSearch_<%=prcsNext %>.init();
                        <% if(zb != null && zb.size() > 0 && "1".equals(userLock)){ %>
                            workFlowSearch_<%=prcsNext %>.refresh();
                        <% } %>
                        var allow_zb_isnull_<%=prcsNext %> = "<%=topDefault %>";
                    </script>
                <% } else {%>
                    <script type="text/javascript">
                        $("#USER_ZB_<%=prcsNext %>").find("em").off("click");
                        $("#USER_CB_<%=prcsNext %>").find("em").off("click");  
                    </script>
                <% } %>
            </div>
            <% }%>
        <%}%>
        <input type="hidden" name="PRCS_ID_NEXT" value="<%=prcsIdNext %>">
        <input type="hidden" name="NEW_PRCS_ID_NEXT" value="<%=prcsIdNext %>">
        <input type="hidden" name="PRESET" value="<%=preSet %>">
        <input type="hidden" name="NOT_ALL_FINISH_NEXT" value="<%=notAllFinish %>">
        <input type="hidden" name="TURN_PRIV" value="<%=turnPriv %>">
        <input type="hidden" name="FLOW_TYPE" value="<%=flowType %>">
        </form>
        <div id="turn_opts" class="turn_opts">
            <a onclick="turnUserWorkFlow();">提交</a>
        </div>
    </div>
</div>
<script type="text/javascript">
$(document).ready(function(){
    $("#page_6 ul.comm-list").each(function() {
        if($(this).find("li").length < 10) {
            $(this).find("li:last").css("border-bottom","none");
        }
    });
})
function showList(obj){
    var did = $(obj).attr("did");
    $("#"+did).find("li:hidden").show();
    $("#"+did).find("li:last").css("border-bottom","none");
    $(obj).parent(".appendList").remove();
    oiScroll_6.refresh();
}
</script>