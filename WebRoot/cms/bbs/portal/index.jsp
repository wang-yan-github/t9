<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>论坛专区</title>
<link rel="stylesheet" href="/t9/cms/bbs/portal/css/style.css">
<script type="text/javascript" src="<%=contextPath %>/core/js/prototype.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/datastructs.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/sys.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/smartclient.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/Calendarfy.js"></script>
<script type="text/javascript" src="<%=contextPath %>/cms/bbs/portal/js/jquery-1.8.3.js"></script>
<script type="text/javascript">
  jQuery.noConflict();
  
	function doInt(){
		//getComments();
		url="<%=contextPath%>/t9/cms/bbs/board/act/T9BbsBoardAct/getBbsAreaAndBoard.act";
		var rtJson=getJsonRs(url);
		  if(rtJson.rtState == "0"){
			    var data = rtJson.rtData;
			    var container=document.getElementById("container");
			    for(var i=0;i<data.size();i++){
			    	var str = "";
			    	str = "<div class='area'><div class='areaStyle'><div style='float:left;width:98%'>"+data[i].areaName+"</div><div class=\"hide\" onclick='toggleDiv1("+data[i].areaId+",this)'></div></div><div id="+data[i].areaId+" class='board'>";
 			    	for(var j=0;j<data[i].board.size();j++){
 			    		str += "<div class='boardStyle'>"
										+"<div class='child1'><a href=\"../category/index.jsp?bid="+data[i].board[j].boardId+"\"><img class='images' src='"+data[i].board[j].imageUrl+"'/></a></div>"
										+"<div class='child2'><div class='child21'><a href=\"../category/index.jsp?bid="+data[i].board[j].boardId+"\"><h2>"+this.showNums(data[i].board[j].boardName,data[i].board[j].todayNum)+"</h2></a></div><div class='child22'>"
										+data[i].board[j].boardAbstract+"</div><div class='child23'>版主： "+data[i].board[j].boardManager+"</div>"
										+"</div><div class='child3'>"+data[i].board[j].totalReply+"/"+data[i].board[j].totalComments+"</div>"
										+"<div class='child4'>"
										+"<div class='child41'>"+data[i].board[j].lastCommentTitle.substring(0,16)+"</div><div class='chilid42'>"+data[i].board[j].lastCommentTime+"&nbsp;&nbsp;"+data[i].board[j].lastCommentAuther+"</div></div></div>";
			    	}  
			    	str += "</div></div>";
			    	$("container").innerHTML += str; 
			    }
		  }
		  else{
			  alert(rtJson.rtMrsg);
		  }
	}
	
	 function hideOrShow(id){
		 var disp = document.getElementById(id).style.display ;
		 if(disp == null || disp == ""){
			 document.getElementById(id).style.display = "none";
		 }else{
			 document.getElementById(id).style.display = "";
		 }
	}
	 /**
	  *隐藏显示专区
	  */
	 function toggleDiv1(id,obj){
		  jQuery("#"+id).toggle(600);
		  jQuery(obj).toggleClass(function(){ return "show";});
		}
	 
	 
	 function getComments(){
		 var url="<%=contextPath%>/t9/cms/bbs/board/act/T9BbsBoardAct/getBbsComments.act";
		 var rtJson=getJsonRs(url);
		 if(rtJson.rtState == "0"){
			 var data=rtJson.rtData;
			 $("todayNum").innerHTML=data.todayComments;
			 $("yesterdayNum").innerHTML=data.yesterdayComments;
			 $("totalNum").innerHTML=data.totalComments;
		 }else{
			 alert(rtJson.rtMrsg);
		 }
	 }
	 
	 function showNums(boardName,nums){
		 if(nums>0){
			 return boardName+"<span style=\"color:red;font-weight:400;\">("+nums+")</span>";
		 }
		 else{
			 return boardName;
		 }
	 }
</script>
</head>
<body onload="doInt();getComments();">
<div id="all">
	<div style="height:120px;width:960px;margin-bottom:30px;margin:0 auto;">
		<div>
				<img src="images/tongda.png"/>
		</div>
			<div id="nv">
				<ul><li class="a" id="mn_forum" ><a href="index.jsp" hidefocus="true" title="BBS"  >论坛</a></li></ul>
			</div>
	</div>
	<div id="chart" style="width:960px;height:31px;">
		<div class="chart z">今日: <em id="todayNum">0</em><span class="pipe">|</span>昨日: <em id="yesterdayNum">0</em><span class="pipe">|</span>帖子: <em id="totalNum">0</em></div>
		<div style="float:right;margin-right:18px;"><a href='myContent.jsp'>我的帖子</a></div>
	</div>
	<div id="container">
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