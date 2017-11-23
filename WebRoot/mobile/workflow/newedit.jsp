<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@  page import="java.util.Map" %>
      <%@  page import="java.util.List" %>
      <%@  page import="java.util.HashMap" %>
    <%@  page import="t9.mobile.util.T9MobileUtility" %>
    <%@  page import="t9.mobile.util.T9MobileConfig" %>
<%
String contextPath = request.getContextPath();
Map r = (Map)request.getAttribute("r");
boolean isPriv =(Boolean) r.get("isPriv");
String runName =(String) r.get("runName");
int flowId =(Integer) r.get("flowId");
String flowName =(String) r.get("flowName");
String autoEdit =(String) r.get("autoEdit");
//String flowType =(String) r.get("flowType");

if(!isPriv)
{
	out.print("NOCREATEPRIV") ;
	return ;
}

%>
<div class="container">
   <div class="tform tformshow">
   		<form action="<%=contextPath %>/t9/mobile/workflow/act/T9PdaNewFlowAct/newSubmit.act" method="post" name="form1" id="new_from" onsubmit="return false;">
   		<%
   		if("2".equals(autoEdit)||"4".equals(autoEdit))
   		{
   		
   		%>

				<div class="read_detail read_detail_header">前缀：</div>
				<div class="read_detail">
					<input type="text" name="RUN_NAME_LEFT"><br />
				</div>
<%
   		}
   		%>
   			<div class="read_detail read_detail_header">填写该工作的名称或文号</div>
   			<div class="read_detail">
					<input type="text" value="<%=runName %>" name="RUN_NAME" style="width:80%" <%if (!"1".equals(autoEdit)) out.print("readOnly");  %>>
<%
if("1".equals(autoEdit))
{
%>
        			<span class="ccombtn" onclick="document.form1.RUN_NAME.value=''"><span>清空</span></span>
<%
}
%>
   			</div>
<%
if("3".equals(autoEdit) || "4".equals(autoEdit))
{
  %>
					<div class="read_detail read_detail_header">后缀：</div>
					<div class="read_detail">
        				<input type="text" name="RUN_NAME_RIGHT">
        			</div>
<%
}
%>
			<input type='hidden' value="<%=flowId %>" name="FLOW_ID">
			<input type='hidden' value="1" name="AUTO_NEW">
   		</form>
   	</div>
</div>
<script>
force_pre_set = '';
</script>