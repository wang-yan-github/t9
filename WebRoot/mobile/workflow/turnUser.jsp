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
if (r1.containsKey("nextPrcs")) {
    prcsIdNext = (Integer) r1.get("nextPrcs");
}
String preSet = "";
if (r1.containsKey("nextPrcs"))  {
    preSet =(String) r1.get("preSet");
}
String notAllFinish = "";
if (r1.containsKey("notAllFinish"))  {
    notAllFinish = T9Utility.null2Empty((String) r1.get("notAllFinish"));
}
String turnPriv = "";
if (r1.containsKey("isAllowTurn"))  {
    turnPriv = T9Utility.null2Empty((String) r1.get("isAllowTurn"));
}
%>
<style>
    .textCss{
        background-color:transparent;
        box-shadow:none;
        margin-bottom:0;
        border:0;
        border-radius:0;
        padding:10px 15px;
        line-height:21px;
        height : 40px;
        width:100%;
    }
</style>
<h5 class="mui-content-padded">工作名称/文号：</h5>
<div class="mui-input-group mui-content-padded">
    <div class="mui-input-row" ><input type="text" readonly value="<%=runName %>"/></div>
</div>
<h5 class="mui-content-padded">发起人：</h5>
<div class="mui-input-group mui-content-padded">
    <div class="mui-input-row" ><input type="text" readonly value="<%=beginUser %>"/></div>
</div>
<form action="<%=contextPath %>/t9/mobile/workflow/act/T9PdaTurnAct/turnSubmit.act"  method="post" name="form1" onsubmit="return false;">
<input type="hidden" name="PRCS_ID_NEXT" value="<%=prcsIdNext %>">
<input type="hidden" name="NEW_PRCS_ID_NEXT" value="<%=prcsIdNext %>">
<input type="hidden" name="PRESET" value="<%=preSet %>">
<input type="hidden" name="NOT_ALL_FINISH_NEXT" value="<%=notAllFinish %>">
<input type="hidden" name="TURN_PRIV" value="<%=turnPriv %>">
<input type="hidden" name="FLOW_TYPE" value="<%=flowType %>">
<div id="forms" class="mui-content-padded" style="margin: 5px;">
<% if ("2".equals(flowType)) {
    String PRCS_USER_OP_CB =(String) r1.get("PRCS_USER_OP_CB");
    String PRCS_USER_OP_ZB =(String) r1.get("PRCS_USER_OP_ZB");
    String prcsName = "";
    if (!T9Utility.isNullorEmpty(preSet)) {
        prcsName = "本步骤为预设步骤";
    }
%>
    <h5 class="mui-content-padded">第<%=prcsIdNext %>步<%=prcsName %></h5>
    <h5 class="mui-content-padded">主办人：</h5>
    <div class="mui-input-group mui-content-padded">
        <div class="mui-input-row">
            <div class="textCss" id="USER_ZB_<%=prcsIdNext %>"><%=PRCS_USER_OP_ZB %></div>
        </div>
    </div>
    <h5 class="mui-content-padded">经办人：</h5>
    <div class="mui-input-group mui-content-padded">
        <div class="mui-input-row" >
            <div class="textCss" id="USER_CB_<%=prcsIdNext %>"><%=PRCS_USER_OP_CB %></div>
        </div>
    </div>
    <% 
    if (T9Utility.isNullorEmpty(preSet) || prcsId ==1) {
        String PLIST_BTN = "";
        String PLIST_NODATE = "";
        String PLIST = "";
        String CHECKBOX=  "";
        int i = 0 ;
        List<Map> users = (List<Map>)r1.get("users");
        for (Map m : users) {
            i++;
            String style = (i > 10) ? "" : "";
            String deptName = (String)m.get("deptName");
            String userName = (String)m.get("userName");
            String userId = (String)m.get("userId");
            int seqId = (Integer)m.get("seqId");
            PLIST += "<div class=\"mui-input-row mui-checkbox\" "+style+">";
            PLIST += "<label>"+userName+"("+deptName+")</label>";
            PLIST += "<input type='checkbox' q_id=\""+seqId+"\" onclick=\"chooseUser(this, '"+prcsIdNext+"')\" q_user_id=\""+userId+"\" q_name=\""+userName+"\" />";
            PLIST += "</div>";
        }
        %>
        <h5 class="mui-content-padded">选择主办人：</h5>
        <div class="mui-input-group" ><%=PLIST %></div>
        <%=PLIST_BTN %>
        <%=PLIST_NODATE %>
        <script type="text/javascript">
            var allow_zb_isnull_<%=prcsIdNext %> = '0';
            </script>
    <%}else{  %> 
    
    <% } %> 
<% 
    } else {
        List<Map> prcss = (List<Map>)r1.get("list");
        for (Map p : prcss) {
            String prcsName =(String) p.get("prcsName");
            String userLock =(String) p.get("userLock");
            String topDefault =(String) p.get("topDefault");
            String prcsNext = (String)p.get("prcsNext");
            List<String[]> zb = (List<String[]>)p.get("prcsOpUser");
            List<String[]> jb = (List<String[]>)p.get("prcsUser");
            String PRCS_USER_OP_ZB = "";
            
            if(zb != null && zb.size() > 0){
                for (String[] z : zb) {
                    PRCS_USER_OP_ZB = "<em uid='"+z[0]+"' userid='"+z[1]+"'>"+z[2]+"<span>&nbsp;</span></em>";
                }
            }
            String PRCS_USER_OP_CB = "";
            if(jb != null && jb.size() > 0){
                for (String[] z : jb) 
                    PRCS_USER_OP_CB += "<em uid='"+z[0]+"' userid='"+z[1]+"'>"+z[2]+"<span>&nbsp;</span></em>";
            }
            %>
            <h5 class="mui-content-padded">第<%=prcsNext %>步<%=prcsName %></h5>
            <h5 class="mui-content-padded">主办人相关：</h5>
            <select style="padding:10px 15px;" name="TOP_DEFAULT_<%=prcsNext %>" readonly id="TOP_DEFAULT_<%=prcsNext %>" <%=("0".equals(userLock) ? "onchange=this.value=" + topDefault : "onchange=\"allow_zb_isnull_"+prcsNext+"=this.value;\"")%>>
                <option value=0 <%=("0".equals(topDefault) ? "selected" : "")%>>明确指定主办人</option>
                <option value=2 <%=("2".equals(topDefault) ? "selected" : "")%>>无主办人会签</option>
                <option value=1 <%=("1".equals(topDefault) ? "selected" : "")%>>先接收者为主办</option>
            </select>
            <h5 class="mui-content-padded">主办人：</h5>
            <div class="mui-input-group mui-content-padded">
                <div class="mui-input-row">
                    <div class="textCss" id="USER_ZB_<%=prcsNext %>"><%=PRCS_USER_OP_ZB %></div>
                </div>
            </div>
            <h5 class="mui-content-padded">经办人：</h5>
            <div class="mui-input-group mui-content-padded">
                <div class="mui-input-row" >
                    <div class="textCss" id="USER_CB_<%=prcsNext %>"><%=PRCS_USER_OP_CB %></div>
                </div>
            </div>
            <% //是否允许修改主办人或者已有主办人为空
            if ("1".equals(userLock) || jb == null || jb.size() == 0 ) {%>
	            <%
	            List<String[]> users = (List<String[]>)p.get("users");
	            String PLIST_BTN = "";
	            String PLIST_NODATE = "";
	            String PLIST = "";
	            String CHECKBOX=  "";
	            int b = 0 ;
	            for (String[] m : users) {
	                b++;
	                String style = (b > 10) ? "" : "";
	                String deptName =  m[3];
	                String userName =  m[2];
	                String userId =  m[1];
	                String seqId = m[0];
	                PLIST += "<div class=\"mui-input-row mui-checkbox\" "+style+">";
	                PLIST += "<label>"+userName+"("+deptName+")</label>";
	                PLIST += "<input type='checkbox' q_id=\""+seqId+"\"  onclick=\"chooseUser(this, '"+prcsNext+"')\" q_user_id=\""+userId+"\" q_name=\""+userName+"\" />";
	                PLIST += "</div>";
	            }
	            %>
	            <h5 class="mui-content-padded">选择主办人：</h5>
	            <div class="mui-input-group">
	                <%=PLIST %>
	            </div>
	            <%=PLIST_BTN %>
	            <%=PLIST_NODATE %>
	            <script type="text/javascript">
	                var allow_zb_isnull_<%=prcsNext %> = "<%=topDefault %>";
	            </script>
	        <% } else {%>
	            <script type="text/javascript">
	                jQuery("#USER_ZB_<%=prcsNext %>").find("em").off("click");
	                jQuery("#USER_CB_<%=prcsNext %>").find("em").off("click");  
	            </script>
	        <% } %>
    <%   }   %>
    <%}%>
</div>
<div class="mui-button-row">
    <button type="button" class="mui-btn mui-btn-primary" onclick="turnUserWorkFlow();">提交</button>
</div>
<div class="mui-button-row"></div>
<script type="text/javascript">
function chooseUser(obj, prcsNext){
	var _uid = obj.getAttribute("q_id");
    if(!obj.checked) {
        remove_user("zb", obj, prcsNext);
        remove_user("cb", obj, prcsNext);
    }else{
        var haszb = hascb = false;
        if(jQuery("#USER_ZB_"+prcsNext).find("em").length > 0) {
        	jQuery("#USER_ZB_"+prcsNext).find("em").each(function(){
                if($(this).attr("uid") == _uid) {
                    haszb = true;
                    return false;
                }
            });
        	jQuery("#USER_CB_"+prcsNext).find("em").each(function(){
                if($(this).attr("uid") == _uid) {
                	hascb = true;
                    return false;
                }
            });
	        //主办和从办都没有选择该人的时候，加入该人
            if(!hascb && !hascb) {
                add_user("cb", obj, prcsNext);
            }
            return;
        }else{
            add_user("zb", obj, prcsNext);
            return;  
       }   
   }
}
function add_user(t, o, prcsNext){
    var str = "";
    var _oSelect_uid = o.getAttribute("q_id");
    var _oSelect_name = o.getAttribute("q_name");
    var _oSelect_user_id = o.getAttribute("q_user_id");
    str = "<em uid='"+_oSelect_uid+"' userid='"+_oSelect_user_id+"'>" + _oSelect_name +"<span>&nbsp;</span></em>";
    if(t == "zb") {
        if(jQuery("#TOP_DEFAULT_"+prcsNext).val() == 0 || jQuery("#TOP_DEFAULT_"+prcsNext).val() == undefined) {
            jQuery("#USER_ZB_"+prcsNext).append(str);
        }
        //判断有无从办
        var cb_has = false;
        if(jQuery("#USER_CB_"+prcsNext).find("em").length > 0) {
            jQuery("#USER_CB_"+prcsNext).find("em").each(function(){
                if($(this).attr("uid") == _oSelect_uid){
                    cb_has = true;
                    return false;
                }
            });
        }
        if(!cb_has){
        	jQuery("#USER_CB_"+prcsNext).append(str);
        }
    }else{
	    jQuery("#USER_CB_"+prcsNext).append(str);
    }
}

function remove_user(t, o, prcsNext){
    var _oSelect_uid = typeof(o) == "object" ? o.getAttribute("q_id") : o;
    if(t == "zb"){
    	jQuery("#USER_ZB_"+prcsNext).find("em").each(function(){
            if($(this).attr("uid") == _oSelect_uid){
                $(this).remove();
            }else{
                return true;
            }
        });
        jQuery("#USER_CB_"+prcsNext).find("em").each(function(){
            if($(this).attr("uid") == _oSelect_uid){
                $(this).remove();
            }else{
                return true;
            }
        });
    }else if(t == "cb"){
    	jQuery("#USER_CB_"+prcsNext).find("em").each(function(){
            if($(this).attr("uid") == _oSelect_uid){
                $(this).remove();
            }else{
                return true;
            }
        });
    }else{
    	jQuery("#USER_ZB_"+prcsNext).find("em").each(function(){
            if($(this).attr("uid") == _oSelect_uid){
                $(this).remove();
            }else{
                return true;
            }
        });
    }
}
</script>