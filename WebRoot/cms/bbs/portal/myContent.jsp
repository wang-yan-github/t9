<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header.jsp"%>
<%@ page import="t9.core.util.T9Utility" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="t9.core.data.T9RequestDbConn" %>
<%@ page import="t9.core.global.T9BeanKeys" %>
<%@ page import="t9.core.funcs.person.data.T9Person" %>
<%@ page import="t9.cms.bbs.comment.logic.*" %>
<%@ page import="java.net.*" %>
<%

	Connection conn = null;
	T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
	conn = requestDbConn.getSysDbConn();
	T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
	T9BbsCommentLogic logic=new T9BbsCommentLogic();
	int num=logic.getMyComments(conn, person);
	int pageSize=8;
	int totalDataSize=0;
	int curPage=1;
	int totalPageSize=0;
	String curpage=request.getParameter("curPage");
  if(curpage!=null && !curpage.equals("")){
	  curPage=Integer.parseInt(curpage);
	  
  }
  totalPageSize=(num%pageSize==0)?(num/pageSize):(num/pageSize+1);
%>
<html>
<head>
<title><%=shortOrgName %>用户社区[OA论坛]</title>
<link rel="stylesheet" type="text/css" href="css/common_css.css" />
<link rel="stylesheet" type="text/css" href="css/space_css.css" />
<script src="js/common.js" type="text/javascript"></script>
<script src="js/home.js" type="text/javascript"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/prototype.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/datastructs.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/sys.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/smartclient.js"></script>

<script type="text/javascript">
function doInit(){
	var url="<%=contextPath%>/t9/cms/bbs/comment/act/T9BbsCommentAct/getMyComment.act";
	var rtJson=getJsonRs(url);
	if(rtJson.rtState == "0"){
		 var data = rtJson.rtData;
		 var currentpage=<%=curPage%>;
		 var pagesize=<%=pageSize%>;
		 var prcStr="<table cellspacing=\"0\" cellpadding=\"0\"><tr class=\"th\">"
														+"<td class=\"icn\">&nbsp;</td><th>主题</th><td class=\"frm\">版块</td>"
														+"<td class=\"num\">回复/查看</td><td class=\"by\"><cite>最后回帖</cite></td></tr>";
		 for(var i=(currentpage-1)*pagesize;i<data.size()&&i<currentpage*pagesize;i++)
		 {
			 var title=data[i].commentTitle;
			 if(data[i].commentTitle.length>20){
				 title=data[i].commentTitle.substring(0,25)+"...";
			 }
		 	var str="<tr><td class=\"icn\"><a href=\"../detail/index.jsp?did="+data[i].infoId+"\" title=\"新窗口打开\">"
				     +"<img src=\"images/folder_common.gif\" /></a></td>"
		         +"<th><a href=\"../detail/index.jsp?did="+data[i].infoId+"\" onclick=\"updateLookNums("+data[i].infoId+")\">"+title+"</a></th>"
						 +"<td>"+data[i].boardName+"</td>"
						 +"<td class=\"num\">"+data[i].replyNums+"<em>"+data[i].lookNums+"</em></td>"
						 +"<td class=\"by\"><cite>"+data[i].lastCommentUser+"</cite> <em>"
						 +data[i].lastCommentTime+"</em></td></tr>";
						 prcStr+=str;
		 }
		 prcStr+="</table>";
		 $("myComment").innerHTML=prcStr;
	}else{
		alert(rtJson.rtMrsg);
	}

}
 function doSubmit(){
	 form1.submit();
 }
 
 function updateLookNums(seqId){
	 var url="<%=contextPath%>/t9/cms/bbs/comment/act/T9BbsCommentAct/updateLookNums.act?seqId="+seqId;
	 var rtJson=getJsonRs(url);
}
</script>
</head>

<body id="nv_home" class="pg_space" onload="doInit();">
	<div style="height:80px;width:75%;margin-bottom:10px;margin:0 auto;">
		<div>
				<img src="images/tongda.png"/>
		</div>
		<div id="nv">
				<ul><li class="a" id="mn_forum" ><a href="index.jsp" hidefocus="true" title="BBS"  >论坛<span>BBS</span></a></li></ul>
			</div>
	</div>
	<div style="width:100%;height:30px"></div>
	<div id="wp" class="wp"> 
		<style id="diy_style" type="text/css"></style>
		<div id="ct" class="ct2_a wp cl">
			<div class="mn">
				<div id="diycontenttop" class="area"></div>
				<div class="bm bw0">
					<h1 class="mt">
						<img alt="thread" src="images/thread.gif" class="vm" />全部
					</h1>
					<ul class="tb cl">
						<li class="a"><a href="">我的帖子</a></li>
						<li class="o"><a href="newComment.jsp?style=0">发帖</a></li>
					</ul>
					<div class="tl">
						<form method="post" name="form1" id="form1" action="">
							<input type="hidden" name="formhash" value="e83173d5" /> 
							<input type="hidden" name="delthread" value="true" />
							<div id="myComment">
							</div>
							<div id="fengye" style="text-align:right;">
								<a href="myContent.jsp?curPage=<%if(curPage==1)out.print(1);else out.print(curPage-1);%>"><span style="border:1px solid gray;background-color:#999;text-align:center;">上一页</span></a>
								<a href="myContent.jsp?curPage=<%if(curPage==totalPageSize)out.print(totalPageSize);else out.print(curPage+1);%>" ><span style="border:1px solid gray;background-color:#999;text-align:center;margin-left:25px;margin-right:20px;">下一页</span></a>
						</div>	
						</form>
					</div>
				</div>
			</div>
		</div>
<div id="footer" style="width:100%;height:100px;">
	<div id="ft" class="wp cl">
			<div id="flk" class="y">
			<strong><a href="<%=orgSecondSite %>" target="_blank" ><%=shortProductName %></a></strong>论坛
			( <a href="<%=orgFirstSite %>" target="_blank">京ICP备05006333号</a> )</p>
			<p class="xs0">
			<!-- foot 信息  --><%=fullOrgName %>
			</p>
			</div>
			<div id="frt">
			<p>Powered by <strong><a href="<%=orgFirstSite %>" target="_blank"><%=orgName %></a></strong> <em>BBS</em></p>
			<p class="xs0">&copy; 2012-2013 <a href="<%=orgFirstSite %>" target="_blank">Comsenz Inc.</a></p>
			</div>
	</div>
</div>
	</div>
</body>
</html>
