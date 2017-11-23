<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@  page import="java.util.Map" %>
      <%@  page import="java.util.List" %>
        <%@  page import="t9.core.util.T9Utility" %>
      <%@  page import="java.util.HashMap" %>
    <%@  page import="t9.mobile.util.T9MobileUtility" %>
    <%@  page import="t9.mobile.util.T9MobileConfig" %>
<%
String contextPath = request.getContextPath();
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
String formMsg = (String)r.get("formMsg");

List<Map> list = (List<Map>)r.get("feedbacks");
List<Map> list2 = (List<Map>)r.get("prcs");
String sessionId = request.getSession().getId();

%>
<div class="container">
   <div class="tform tformshow">
   <div class="read_detail">
      <em>名称/文号：</em><%=runName %>
   </div>
   <div class="read_detail">
      <em>流水号：</em><%=runId %>
   </div>
   <div class="read_detail">
      <em>流程开始：</em><%=beginTime %>
   </div>
  
      
   <div id="div_seal" class="read_detail endline" style="display:<%=("".equals(sealImg) ? "none" : "")%>">
      <em>手机审批：</em>
      <div>
      <%=sealImg %>
      </div>
   </div>
 </div>
  <form action="<%=contextPath %>/t9/mobile/workflow/act/T9PdaHandlerAct/editSubmit.act" method="post" name="form1" id="edit_from" onsubmit="return false;">
 <div class="tform">
   <%=formMsg %>
   </div>
<%
if(!T9Utility.isNullorEmpty(attachmentId)){
%>
<div class="tform">
      <div class="read_detail read_detail_header">附件</div>
      <div class="read_detail read_detail_p endline">
         <%=T9MobileUtility.getAttachLinkPda(attachmentId, attachmentName, sessionId, "workflow", true, true, contextPath) %>
      </div>
</div>
<% } %>
 
 <%
if(!"2".equals(feedbackFlag)){
%>
   <div class="tform">
      <div class="read_detail read_detail_header">会签意见</div>
      <div class="read_detail"><textarea name="CONTENT" id="CONTENT" rows="3" wrap="on"></textarea></div>
      <div id="editSignBox">
      <% 
      for (Map m : list) { 
        int prcsId2 =(Integer) m.get("prcsId");
        String prcsName =(String) m.get("prcsName");
        String userName =(String) m.get("userName");
        String deptName =(String) m.get("deptName");
        String time =(String) m.get("time");
        String content =(String) m.get("content");
      %>
   <div class="read_detail read_detail_header">第<%=prcsId2 %>步 <%=prcsName %></div>
   <div class="read_detail read_detail_p">
      <%=userName %>(<%=deptName %>) -  <%=time %><br />
     <%=content %>
   </div>
   <% } %>
	</div>
   </div>
   <% } %>
   <div class="tform">
      <div class="read_detail read_detail_header">流程图</div>
      <table width='100%' class="TableList">
<tr class=TableHeader><td colspan=3>流程图</td></tr>
<tbody id="listTbody">
  <% 
      for (Map m : list2) { 
        int prcsId3 =(Integer) m.get("prcsId");
        List<Map> prcs =(List<Map>) m.get("list");
      %>
      <tr>
      <td rowspan="<%=prcs.size() %>">第<font color=red><%=prcsId3 %></font>步</td>
      <% for (int i = 0 ;i  < prcs.size() ;i++) { 
      Map p = (Map)prcs.get(i);
      int flowPrcs2 =(Integer) p.get("flowPrcs");
      String prcsName =(String) p.get("prcsName");
      
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
          prcsUserName = "<span style=\"text-decoration:underline;font-weight:bold;color:red;cursor:pointer\"  title=\"部门："+deptName+"\">"+userName+" 主办</span>";
        }else{
          prcsUserName = "<span style=\"text-decoration:underline;font-weight:bold;cursor:pointer\" title=\"部门："+deptName+"\">"+userName+"</span>";
        }
        if (state ==1) {
          title += "<img src='"+ imgPath +"/email_close.gif'  align='absmiddle'/>&nbsp;" + prcsUserName + "&nbsp;[<font color=green>未接收办理</font>]";
        } else if(state == 2){
          title += "<img src='"+ imgPath +"/email_open.gif'  align='absmiddle'/>&nbsp;" + prcsUserName + "&nbsp;[<font color=green>办理中,已用时：" + timeUsed + "</font>]";
          
          if(!T9Utility.isNullorEmpty(timeOutFlag)){
            title += "<br><span style=\"color:red\">限时"+ timeOut +"小时," + timeUsed + "</span>";
          }
          title += "<br> 开始于：" + beginTime2;
        }else if(state == 3){
          title += "<img  src='"+ imgPath +"/flow_next.gif' align='absmiddle'>&nbsp;"+ prcsUserName +"&nbsp;[<font color=green>已转交下步,用时：" + timeUsed + "</font>]";
          title += "<br> 开始于：" + beginTime2;
          if(!T9Utility.isNullorEmpty(deliverTime)){
            title += "<br> 结束于：" + deliverTime;
          }
        }else if(state == 4){
          title += "<img  src='"+ imgPath +"/flow_next.gif' align='absmiddle'>&nbsp;"+ prcsUserName +"&nbsp;[<font color=green>已办结,用时：" + timeUsed + "</font>]";
          title += "<br>开始于：" + beginTime2;
          if(!T9Utility.isNullorEmpty(deliverTime)){
            title += "<br> 结束于：" +deliverTime;
          }
        }else if(state == 5){
          title += prcsUserName + "&nbsp;[预设经办人]";
        }
        title += "<br><br>";
      }
      if (i != 0) {%>
       <tr>
      <% }%>
      <td><img src='<%=imgPath %>/arrow_down.gif'/>&nbsp;序号<%=flowPrcs2 %>：<%=prcsName %></td>
      <td><%=title %></td>
      <% if (i != 0) {%>
       </tr>
      <% }%>
      <% } %>
      </tr>
       <%} %>
</tbody>
</table>
   </div>
   <input type="hidden" name="P" value="<%=sessionId %>">
   <input type="hidden" name="FLOW_ID" value="<%=flowId %>">
   <input type="hidden" name="RUN_ID" value="<%=runId %>">
   <input type="hidden" name="PRCS_ID" value="<%=prcsId %>">
   <input type="hidden" name="FLOW_PRCS" value="<%=flowPrcs %>">
   <input type="hidden" name="DO_ACTION" value="">
   <input type="hidden" name="FLOW_TYPE" value="<%=flowType %>">

   <div id="edit_opts" class="edit_opts" style="display:none;">
     <% if ("1".equals(opFlag)) { %>
      <span class="turn_flow">转交</span>
      <% } %>
      <span class="save_flow">保存</span>
     <% if ("0".equals(opFlag)) { %>
         <span class="stop_flow">办理完毕</span>
       <% } %>
        <% if (("1".equals(allowBack) || "2".equals(allowBack))
            && prcsId != 1) { %>
      <span class="sel_flow">回退</span>
       <% } %>
      <% if ("2".equals(flowType)) { %>
         <span class="stop_flow">结束流程</span>
       <% } %>
      <span class="show_original_form">原始表单查看</span>
   </div>
   </form>
   </div>
</div>

<script>
$('.tform').each(function(){
	$(this).find('.read_detail:last').addClass('endline');
	
	$(".mobile_seal_span").unbind().bind("click", function(){
		del_seal(this);
	});
});
</script>