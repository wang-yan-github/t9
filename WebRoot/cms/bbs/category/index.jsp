<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header.jsp" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>板块页面</title>
<link rel="stylesheet" type="text/css" href="css/1.css" />
<link rel="stylesheet" type="text/css" href="css/2.css" />
<script src="./js/common.js" type="text/javascript"></script>
<script src="./js/forum.js" type="text/javascript"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/prototype.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/datastructs.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/sys.js"></script>
<script type="text/javascript" src="<%=contextPath%>/core/js/cmp/page.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/smartclient.js"></script>
<script type="text/javascript" src="./js/jquery-1.8.3.js"></script>
<script src="./js/forum_moderate.js" type="text/javascript"></script>
<link rel="stylesheet" type="text/css" href="css/style_1_forum_moderator.css" />

<script src="./js/T9Bbstable1.js" type="text/javascript"></script>
<script src="./js/T9Bbstable2.js" type="text/javascript"></script>
<script src="./js/T9Bbsfenyebar.js" type="text/javascript"></script>
<script src="./js/T9Bbslefttree.js" type="text/javascript"></script>
</head>


<%
		String currpage = request.getParameter("currpage");
		if(currpage == null || "".equals(currpage)){
			currpage = "1";
		}
		String pagesize = request.getParameter("pagesize");
		if(pagesize == null || "".equals(pagesize)){
			pagesize = "10";
		}
		String allowmaxpage = request.getParameter("allowmaxpage");
		if(allowmaxpage == null || "".equals(allowmaxpage)){
			allowmaxpage = "10";
		}
		String bid = request.getParameter("bid");
		if(bid == null || "".equals(bid)){
			bid = "1";
		}


%>

<script type="text/javascript">

var currpage= <%=currpage%>;
var pagesize= <%=pagesize%>;
var allowmaxpage= <%=allowmaxpage%>;
var data1 ;
var bid = <%=bid%>;
var maxcount = 0;
/**
 * 避免jquery 与其他类库冲突
 */
jQuery.noConflict();
function doInit(){
	 var url="<%=contextPath%>/t9/cms/bbs/board/act/T9BbsBoardAct/isBoardManager.act?bid="+bid;
	  var rtJson=getJsonRs(url);
	  if(rtJson.rtState==0){
		  var flag=rtJson.rtData.flag;
	  }
	  if(flag=="0"){
		  jQuery("#doctablelist").T9Bbstable2({
				coloumUrl:{docTitle:"<%=contextPath%>/cms/bbs/detail/index.jsp?did=",docCreatername:"<%=contextPath%>/cms/bbs/detail/index.jsp?did="},
				url:"<%=contextPath%>/t9/cms/bbs/documentinfo/act/T9BbsDocumentInfoAct/getDocumentsByBoardId.act?bid="+bid+"&pagesize="+pagesize+"&currpage="+currpage
				,turl:"<%=contextPath%>/t9/cms/bbs/documentinfo/act/T9BbsDocumentInfoAct/getDocumentsTopsByBoardId.act?bid="+bid
			});
	  }else{
		  jQuery("#doctablelist").T9Bbstable1({
				coloumUrl:{docTitle:"<%=contextPath%>/cms/bbs/detail/index.jsp?did=",docCreatername:"<%=contextPath%>/cms/bbs/detail/index.jsp?did="},
				url:"<%=contextPath%>/t9/cms/bbs/documentinfo/act/T9BbsDocumentInfoAct/getDocumentsByBoardId.act?bid="+bid+"&pagesize="+pagesize+"&currpage="+currpage
				,turl:"<%=contextPath%>/t9/cms/bbs/documentinfo/act/T9BbsDocumentInfoAct/getDocumentsTopsByBoardId.act?bid="+bid
			});
	  }
}

function refreshData(){
	jQuery("#doctablelist").html("");
	doInit();
}
function getBoardInfo(){
	  var url = "<%=contextPath%>/t9/cms/bbs/board/act/T9BbsBoardAct/getBbsBoardInfoById.act?seqId="+bid;
	  var rtJson = getJsonRs(url);
	  if(rtJson.rtState == "0"){
	     data = rtJson.rtData;///boardManagerDesc,boardName,todayNum,topicNum
	     jQuery("#boardname").html(data.boardName);
	     jQuery("#bbsboardname").html(data.boardName);
	     jQuery("#bbsareaname").html(data.areaName);
	     jQuery("#todayNum").html(data.todayNum);
	     jQuery("#topicNum").html(data.topicNum);
		   jQuery("#boardManagerDesc").html(data.boardManagerDesc);
	  }
	}
jQuery(function(){
		var data = " { root: [ {title:'1',value:'0'},{title:'1',value:'0'},{title:'1',value:'0'},{title:'1',value:'0'} ]}";
	 

		doInit();
getlistmaxcount();
getBoardInfo();
jQuery("#fenye1").T9Bbsfenyebar({pagesize:pagesize,currpage:currpage,maxcount:maxcount,url:"index.jsp"});
	jQuery("#pgt").T9Bbsfenyebar({pagesize:pagesize,currpage:currpage,maxcount:maxcount,url:"index.jsp"});
	jQuery("#forumleftside").T9Bbslefttree({
		url:"<%=contextPath%>/t9/cms/bbs/board/act/T9BbsBoardAct/getLeftTree.act?bid="+bid,
		tarurl:"<%=contextPath%>/cms/bbs/category/index.jsp"
		
	});
	

 
function getlistmaxcount(){
	 url="<%=contextPath%>/t9/cms/bbs/documentinfo/act/T9BbsDocumentInfoAct/getDocumentsMaxCount.act?bid="+bid;
	 var rtJson = getJsonRs(url);
	  if(rtJson.rtState == "0"){
		  maxcount = rtJson.rtData.maxcount;
	  }
	}
  });
  

function updateLookNums(seqId){
	 var url="<%=contextPath%>/t9/cms/bbs/comment/act/T9BbsCommentAct/updateLookNums.act?seqId="+seqId;
	 var rtJson=getJsonRs(url);
}
  
function  docoption(stat,method){
	var spara = "";
	var size = jQuery("#doctablelist input[type='checkbox']:checked").each(
			function(i){
				var sValue = jQuery(this).val()
				spara += sValue + ",";
	});
	
	var url = "<%=contextPath%>/t9/cms/bbs/documentinfo/act/T9BbsDocumentInfoAct/manageDocumentInfo.act?ids="+spara+"&method="+method+"&stat="+stat;
	  var rtJson = getJsonRs(url);
	  if(rtJson.rtState == "0"){
	     jQuery('#mdly').hide();
	     jQuery("#doctablelist").html("")
	     doInit();
	   
	  }
	
}
</script>
<body id="nv_forum" class="pg_forumdisplay" onkeydown="if(event.keyCode==27) return false;">
<div id="append_parent"></div><div id="ajaxwaitid"></div>

<div id="toptb" class="cl">
<div class="wp">
</div>
</div>
<div id="hd">
<div class="wp">
<div class="hdc cl"><h2><a href="./" title="<%=shortOrgName %>用户社区[OA论坛]"><img src="./css/images/tongda.png" alt="<%=shortOrgName %>用户社区[OA论坛]" border="0" /></a></h2>
</div>

<div id="nv">
<a href="javascript:;" id="qmenu" ></a>
<ul><li class="a" id="mn_forum" ><a href="../portal/index.jsp" hidefocus="true" title="BBS"  >论坛<span>BBS</span></a></li>
</ul>
</div>
</div>
</div>


<div id="wp" class="wp"><style id="diy_style" type="text/css"></style>
<!--[diy=diynavtop]--><div id="diynavtop" class="area"></div><!--[/diy]-->
<div id="pt" class="bm cl">
<div class="z">
<a href="./" class="nvhm" title="首页"><%=shortOrgName %>用户社区[OA论坛]</a> <em>&rsaquo;</em> <a href="#">论坛</a> <em>&rsaquo;</em>
 <a href="javascript:void(0)" id="bbsareaname">OA2013版专区</a><em>&rsaquo;</em>
  <a href="javascript:void(0)" id="bbsboardname">OA精灵PC版、MAC版、移动版</a>
</div>
</div><div class="wp">
<!--[diy=diy1]--><div id="diy1" class="area"></div><!--[/diy]-->
</div>
<div class="boardnav">
<div id="ct" class="wp cl" style="margin-left:145px">
<!-- 这是导航菜单 有问题 在iei下 111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111-->
<div id="sd_bdl" class="bdl"style="width:130px;margin-left:-145px">
<!--[diy=diyleftsidetop]--><div id="diyleftsidetop" class="area"></div><!--[/diy]-->

<div class="tbn" id="forumleftside"><h2 class="bdl_h">版块导航</h2>
</div>

<!--[diy=diyleftsidebottom]--><div id="diyleftsidebottom" class="area"></div><!--[/diy]-->
</div>
<!-- 这是导航菜单 有问题 在iei下 1111111111111111111111111111111111111111111111111111111111111111111111111111111111111111-->
<div class="mn" >
<div class="bm bml pbn">
<div class="bm_h cl">
<h1 class="xs2">
<a href="#" id="boardname"></a>
<span class="xs1 xw0 i">今日: <strong class="xi1"><span id="todayNum"></span></strong><span class="pipe">|</span>主题: <strong class="xi1"><span id="topicNum"></span></strong></span></h1>
</div>
<div class="bm_c cl pbn">
<div>版主: <span class="xi2" id="boardManagerDesc">
</span></div></div>
</div>



<div class="drag">
<!--[diy=diy4]--><div id="diy4" class="area"></div><!--[/diy]-->
</div>




<div id="pgt" class="bm bw0 pgs cl" >

<a href="../portal/newComment.jsp?style=1"  title="发新帖"><img src="./css/images/pn_post.png" alt="发新帖" /></a>
</div>
<div id="threadlist" class="tl bm bmw">
<div class="th">
<table cellspacing="0" cellpadding="0">
<tr>
<th colspan="2">
    <div style="width:100%;margin-left:100px;text-align:left;">主题</div>
</th>
<td class="by">作者</td>
<td class="num">回复/查看</td>
<td class="by">最后发表</td>
</tr>
</table>
</div>
<div class="">
<script type="text/javascript">var lasttime = 1358305691;</script>
<div id="forumnew" style="display:none"></div>

<form method="post" autocomplete="off" name="moderate" id="moderate" action="forum.php?mod=topicadmin&amp;action=moderate&amp;fid=58&amp;infloat=yes&amp;nopost=yes">
<input type="hidden" name="formhash" value="4525bd0c" />
<input type="hidden" name="listextra" value="page%3D1" />
<div id="threadlist" class="tl bm bmw" style="position: relative;">
<table summary="forum_58"  cellspacing="0" cellpadding="0" id="doctablelist">

</table><!-- end of table "forum_G[fid]" branch 1/3 -->
<div id="mdly" style="display: none;" >
<input type="hidden" name="optgroup" />
<input type="hidden" name="operation" />
<a class="cp" href="javascript:;" title="最小化" onclick="$('mdly').className='cpd'">最小化</a>
<label><input type="checkbox" name="chkall" class="pc" onclick="if(!($('mdct').innerHTML = modclickcount = checkall(this.form, 'moderate'))) {$('mdly').style.display = 'none';}" />全选</label>
<h6><span>选中</span><strong onclick="$('mdly').className='';" onmouseover="this.title='最大化'" id="mdct"></strong><span>篇: </span></h6>
<p>
<strong><a href="javascript:;" onclick="docoption(0,'del');">删除</a></strong>
<span class="pipe">|</span>
<strong><a href="javascript:;" onclick="docoption(1, 'jinghua');">精华</a><a style="color:red" href="javascript:;" onclick="docoption(0, 'jinghua');">反</a></strong>
<span class="pipe">|</span>
<strong><a href="javascript:;" onclick="docoption(1, 'gaoliang');">高亮</a><a style="color:red" href="javascript:;" onclick="docoption(0, 'gaoliang');">反</a></strong>
<span class="pipe">|</span>
<strong><a href="javascript:;" onclick="docoption(1,'gbdk');">关闭</a></strong>
<strong><a href="javascript:;" onclick="docoption(0,'gbdk');">打开</a></strong>
</p>

<p>
<a href="javascript:;" onclick="docoption(3, 'zhiding');">全局置顶</a>
<a href="javascript:;" onclick="docoption(1, 'zhiding');">本版置顶</a>
<a href="javascript:;" onclick="docoption(0, 'zhiding');">撤销置顶</a>


</p>
</div>
</div>
</form>

</div>
</div>

<!-- 隐藏下拉菜单 开始 -->
<div id="filter_special_menu" class="p_pop" style="display:none" change="location.href='forum.php?mod=forumdisplay&fid=58&filter='+$('filter_special').value">
<ul>
<li><a href="forum.php?mod=forumdisplay&amp;fid=58">全部主题</a></li>
<li><a href="forum.php?mod=forumdisplay&amp;fid=58&amp;filter=specialtype&amp;specialtype=poll">投票</a></li></ul>
</div>
<!-- 隐藏下拉菜单 结束 -->

<!-- 隐藏下拉菜单 开始 -->
<div id="filter_dateline_menu" class="p_pop" style="display:none">
<ul>
<li><a href="forum.php?mod=forumdisplay&amp;fid=58&amp;orderby=lastpost&amp;filter=dateline">全部时间</a></li>
<li><a href="forum.php?mod=forumdisplay&amp;fid=58&amp;orderby=lastpost&amp;filter=dateline&amp;dateline=86400">一天</a></li>
<li><a href="forum.php?mod=forumdisplay&amp;fid=58&amp;orderby=lastpost&amp;filter=dateline&amp;dateline=172800">两天</a></li>
<li><a href="forum.php?mod=forumdisplay&amp;fid=58&amp;orderby=lastpost&amp;filter=dateline&amp;dateline=604800">一周</a></li>
<li><a href="forum.php?mod=forumdisplay&amp;fid=58&amp;orderby=lastpost&amp;filter=dateline&amp;dateline=2592000">一个月</a></li>
<li><a href="forum.php?mod=forumdisplay&amp;fid=58&amp;orderby=lastpost&amp;filter=dateline&amp;dateline=7948800">三个月</a></li>
</ul>
</div>
<!-- 隐藏下拉菜单 结束 -->

<!-- 隐藏下拉菜单 开始 -->
<div id="filter_orderby_menu" class="p_pop" style="display:none">
<ul>
<li><a href="forum.php?mod=forumdisplay&amp;fid=58">默认排序</a></li>
<li><a href="forum.php?mod=forumdisplay&amp;fid=58&amp;filter=author&amp;orderby=dateline">发帖时间</a></li>
<li><a href="forum.php?mod=forumdisplay&amp;fid=58&amp;filter=reply&amp;orderby=replies">回复/查看</a></li>
<li><a href="forum.php?mod=forumdisplay&amp;fid=58&amp;filter=reply&amp;orderby=views">查看</a></li>
<li><a href="forum.php?mod=forumdisplay&amp;fid=58&amp;filter=lastpost&amp;orderby=lastpost">最后发表</a></li>
<li><a href="forum.php?mod=forumdisplay&amp;fid=58&amp;filter=heat&amp;orderby=heats">热门</a></li>
<ul>
</div>
<!-- 隐藏下拉菜单 结束 -->

<!-- 分页开始 -->
<div class="bm bw0 pgs cl"  id="fenye1">

<a href="../portal/newComment.jsp?style=1" title="发新帖"><img src="./css/images/pn_post.png" alt="发新帖" /></a>
<!--[diy=diyfastposttop]--><div id="diyfastposttop" class="area"></div><!--[/diy]-->
<!-- 分页结束 -->
<!--[diy=diyforumdisplaybottom]--><div id="diyforumdisplaybottom" class="area"></div><!--[/diy]-->



</div>

</div>
</div>

<div class="wp mtn">
<!--[diy=diy3]--><div id="diy3" class="area"></div><!--[/diy]-->
</div>	</div>
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

<div id="g_upmine_menu" class="tip tip_3" style="display:none;">
<div class="tip_c">
积分 0, 距离下一级还需  积分
</div>
<div class="tip_horn"></div>
</div>
<span id="scrolltop" onclick="window.scrollTo('0','0')">回顶部</span>

</body>
</html>
