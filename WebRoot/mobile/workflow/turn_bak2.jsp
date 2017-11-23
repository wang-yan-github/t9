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
Map r1 = (Map)request.getAttribute("r");
Map r = (Map)r1.get("flowRun");
String runName =(String) r.get("runName");
String beginUser =(String) r.get("beginUser");
String flowType =(String) r.get("flowType");
String syncDeal = (String) r1.get("syncDeal");
String topFlag = (String) r1.get("topFlag");
String isAllowTurn = (String) r.get("isAllowTurn");
String gatherNode = (String) r.get("gatherNode");
String notAllFinish =  (String) r.get("notAllFinish");
List<Map> nextPrcs = (List<Map>) r1.get("nextPrcs");
Map m2 = nextPrcs.get(0);
int firstPrcsId =(Integer) m2.get("prcsId");
String sessionId = request.getSession().getId();
%>
<div class="container">
    <div class="tform tformshow">
        <div class="read_detail"><em>工作名称/文号：</em><%=runName %></div>
        <div class="read_detail"><em>发起人：</em><%=beginUser %></div>
        <%if ("1".equals(flowType)) {
            if (T9Utility.isNullorEmpty(PRCS_ID_NEXT)) {
        %>
                <div class="read_detail read_detail_header">请选择下一步骤：</div>
                <%
                if (!T9Utility.isNullorEmpty(gatherNode)) {
                %>
                    <div class="no_msg">此步骤为强制合并步骤，尚有步骤未转交至此步骤，您不能继续转交下一步！</div>
                <%
                    return ;
                } %>
                <%
                if (notAllFinish == null) {
                %>
                    <div class="no_msg">此步骤禁止转交！</div>
                <%
                    return ;
                } %>
                <% 
                int count = 0;
                int COUNT_PRCS_OK = 0;
                if (nextPrcs.size() ==1 && firstPrcsId == 0) {
                    COUNT_PRCS_OK++;
                %>
                    <form action="#" method="post" name="form1" onsubmit="return false">
                        <div class="read_detail">
                            <input type="checkbox" name="PRCS_ID_NEXT" 
                                onclick="select_turn(this)" 
                                id="prcs_0" value="0" checked/>
                            <label for="prcs_0">结束流程</label>
                        </div>
                <% } else {
                %>
                    <form action="#" method="post" name="form1" onsubmit="return false">
                <%
                    COUNT_PRCS_OK = 0 ;
                    for (Map m : nextPrcs) {
                        count++;
                        String NOT_PASS =(String) m.get("notInPass");
                        int prcsIdNext = (Integer)m.get("prcsId");
                        String prcsName2 =(String) m.get("prcsName");
                        if (NOT_PASS != null) {
                    %>
                            <label for="prcs_<%=prcsIdNext %>"><%=prcsName2 %> <%=NOT_PASS %></label>
                    <%  } else {
                            if (COUNT_PRCS_OK == 0) {%>
                                <div class="read_detail">
                            <% } %>
                            <input type="checkbox" 
                                name="PRCS_ID_NEXT" 
                                id="prcs_<%=prcsIdNext %>" 
                                onclick="select_turn(this)" 
                                value="<%=prcsIdNext %>" <%=((count == 1 || "2".equals(syncDeal))? " checked" : "") %>> 
                            <label for="prcs_<%=prcsIdNext %>"><%=prcsName2 %></label>
                            <% 
                            COUNT_PRCS_OK++;
                        }
                    }
                    if (COUNT_PRCS_OK > 0) { %>
                        </div>
                    <% }%>
                <% } %>
                <input type="hidden" name="TOP_FLAG" value="<%=topFlag %>">
            <% } %>
        <% 
        }
        %>
        <input type="hidden" name="turn_action" value="">
        <input type="hidden" name="NOT_ALL_FINISH" value="<%=notAllFinish %>">
        <input type="hidden" name="NOT_ALL_FINISH_NEXT" value="<%=notAllFinish %>">
        <input type="hidden" name="TURN_PRIV" value="<%=isAllowTurn %>">
        <input type="hidden" name="NEW_PRCS_ID_NEXT" value="">
        </form>
        <div id="turn_opts" class="turn_opts">
            <a onclick="goOnWorkFlow();">继续</a>
        </div>
        <script>
        var SYNC_DEAL = '<%=syncDeal %>';
        function select_turn(obj) {
            if(obj.id == "prcs_0" && obj.checked == true){
                $("input[id^='prcs_']").attr("checked", false);
                $("#prcs_0").attr("checked", true);
            }
            //强制并发
            else if(obj.id != "prcs_0" && SYNC_DEAL == 2){
                $("input[id^='prcs_']").attr("checked", true);
                $("#prcs_0").attr("checked", false);
            }
            //禁止并发
            else if(obj.id != "prcs_0" && obj.checked == true && SYNC_DEAL == 0){
                $("input[id^='prcs_']").attr("checked", false);
                obj.checked = true;
                $("#prcs_0").attr("checked", false);
            }
            //允许并发
            else if(obj.id != "prcs_0" && obj.checked == true){
                $("#prcs_0").attr("checked", false);
            }
            var prcs_str = "";
            $("input[name='PRCS_ID_NEXT']").each(function(i){
                if(this.checked == true){
                    if(this.id == "prcs_0"){
                        $("input[name='turn_action']").val(contextPath + '/t9/mobile/workflow/act/T9PdaTurnAct/turnSubmit.act');
                        prcs_str = 0;
                    }else{
                        prcs_str += this.value + ",";
                        $("input[name='turn_action']").val(contextPath + '/t9/mobile/workflow/act/T9PdaTurnAct/turnUser.act');
                    }
                }
            });
            $("input[name='NEW_PRCS_ID_NEXT']").val(prcs_str);
        }
        $(document).ready(function(){
            select_turn("");
        });
        </script>
    </div>
</div>