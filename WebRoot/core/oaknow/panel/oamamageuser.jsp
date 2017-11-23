<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<%@ include file="/core/inc/header.jsp" %>
<%@ page  import="java.util.List"%>
<%@ page  import="java.lang.*"%>
<%@ page  import="t9.core.oaknow.data.*"%>
<%@ page  import="t9.core.oaknow.util.*"%>
<%@ page  import="t9.core.funcs.person.data.T9Person"%>
<html> 
<head> 
<title>用户管理</title> 
<script type="text/javascript" src="<%=contextPath %>/core/js/datastructs.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/sys.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/prototype.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/smartclient.js"></script>
<link rel="stylesheet" href = "<%=contextPath%>/core/styles/oaknow/css/wiki.css">
<link rel="stylesheet" href = "<%=cssPath%>/style.css">
<script type="text/javascript">
	function  pager(currNo){
		var userKey = $('userKey').value;
		var param ="currNo="+currNo+"&userKey="+userKey;		
		window.location.href = "<%=contextPath%>/t9/core/oaknow/act/T9OAKnowPanelAct/userManage.act?" + param;
	}
	function openWin(userId){
		var url = "<%=contextPath%>/t9/core/oaknow/act/T9OAKnowPanelAct/findPerson.act?userId=" +userId;
	  window.open(url,'','height=400,width=550,status=1,toolbar=no,menubar=no,location=no,scrollbars=yes,left=220,top=180,resizable=yes');
  }
  function deleteUser(userId){
		if(window.confirm("确实要删除这个用户么！")){
			window.location.href = "<%=contextPath%>/t9/core/oaknow/act/T9OAKnowPanelAct/deleteUserByUserId.act?userId="+userId;
    }else{
			return false;
    }
  }
</script>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<%
   List<T9Person> users = (List<T9Person>)request.getAttribute("users");
   T9Person person = (T9Person)request.getAttribute("user");
   T9PageUtil pu =  (T9PageUtil)request.getAttribute("page");
%>

</head> 
<body class="mbodycolor" topmargin="5"> 
<div class="gt">
	<form name="form1" id="form1" action='<%=contextPath%>/t9/core/oaknow/act/T9OAKnowPanelAct/userManage.act' method='post'> 
		用户管理 &nbsp;&nbsp;&nbsp;&nbsp;
		<input class="BigInput" type="text" onmouseover="this.focus()" onfocus="this.select()" id="userKey" name="userKey" value="${userKey}"/>&nbsp;
		<input type="submit" value="搜索" class="BigButton"/> 
	</form>
</div> 
<br/>
<div style="text-align: center;"> 
   <%if(users !=null && users.size() !=0 ){ %>
   <TABLE class="tlists"> 
       <TR class="header"> 
         <TD nowrap>用户名</TD> 
         <TD nowrap>真实姓名</TD> 
         <TD>积分</TD>         
         <TD nowrap>用户类别</TD> 
         <TD>操作</TD> 
       </TR> 
      <%
      	
      	  for(int i=0; i<users.size(); i++){
      	  %>
      <TR> 
       <TD class="tctd"><%= users.get(i).getUserId()%></TD> 
       <TD class="tctd"><%= users.get(i).getUserName() %></TD> 
       <TD class="tctd"><%= users.get(i).getScore() %></TD> 
       <TD class="tctd">
       		<%
       			if(users.get(i).isAdminRole() || users.get(i).isAdmin()){
       			 %>
       			 	<span style="color:#2CBB17">管理员</span>
       			 <% 
       			}else{
       			 %>
      			 	<span style="color:#3AB1B1">普通用户</span>
      			 <% 
       			}
       		%>       		
       		</TD> 
       <TD class="tctd"> 
          <%
          	if(person.getSeqId() == users.get(i).getSeqId()){
          	  %>
          	  <a href="javascript:void(0)" onclick="openWin(<%=users.get(i).getSeqId()%>); return false;">修改</a>
          	  <%
          	}else{
          	  %>
          	  <a href="javascript:void(0)" onclick="openWin(<%=users.get(i).getSeqId()%>); return false;">修改</a> 
       	      <a onclick="deleteUser(<%=users.get(i).getSeqId()%>); return false;">删除</a>  
          	  <%
          	}
          %>
       	         	  
       </TD> 
     </TR>     
      	  <%
      	  }
         
         %></TABLE> <%
     }else{
      	  %>
      	 <table align="center" width="340" class="MessageBox">        
          <tr>
				    <td class="msg info">
				      <h4 class="title" align="left">提示</h4>
				      <div style="font-size: 10pt;" class="content">没有搜到相关用户！</div>
				    </td>
          </tr>        
      </table>     	  
      	  <%
      	}
      %> 
   
</div> 
<div class="pagebar"><!-- 分页 -->
   <%
   if(users.size() !=0){
   	 if(pu.getCurrentPage()>1){
   %>
   	<a href=javascript:pager(1)>首页</a>&nbsp;&nbsp;
   	<a href=javascript:pager(<%=pu.getCurrentPage()-1%>)>上一页</a>&nbsp;&nbsp;
   <%
   	 }
     if(pu.getCurrentPage() -4 >0){
       for(int no = pu.getCurrentPage()-4; no<pu.getCurrentPage(); no++){
         %>
         	<a href="javascript:pager(<%=no%>)"><%=no%></a>&nbsp;&nbsp;
         <%
       }       
     }else{
       for(int no=1; no<pu.getCurrentPage(); no++){
         %>
        	<a href=javascript:pager(<%=no%>)><%=no%></a>&nbsp;&nbsp;
        <%
       }
     } 
     if(pu.getPagesCount()>1){
     %>
     		<a href=javascript:pager(<%=pu.getCurrentPage()%>)>[<%=pu.getCurrentPage()%>]</a>
     <%   
     }
     if(pu.getCurrentPage()+5 < pu.getPagesCount()){
       for(int no2= pu.getCurrentPage()+1; no2<pu.getCurrentPage()+5; no2++){
         %>
         	<a href=javascript:pager(<%=no2%>)><%=no2%></a>&nbsp;&nbsp;
         <%
       }
     }else{
       for(int no2=pu.getCurrentPage()+1; no2<=pu.getPagesCount(); no2++){
         %>
        	<a href=javascript:pager(<%=no2%>)><%=no2%></a>&nbsp;&nbsp;
        <%
       }
    }
     if(pu.getCurrentPage() < pu.getPagesCount()){
       %>
       <a href=javascript:pager(<%=pu.getCurrentPage()+1 %>)>下一页</a>&nbsp;
       <a href=javascript:pager(<%=pu.getPagesCount() %>)>末页</a>
       <%
     }}
   %>
  </div> 
</body> 
</html>