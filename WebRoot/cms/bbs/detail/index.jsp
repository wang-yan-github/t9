<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header.jsp" %>
<html>
<head>
<title>帖子页面</title>
<link rel="stylesheet" type="text/css" href="css/style.css" />
<link rel="stylesheet" type="text/css" href="css/style1.css" />
<script type="text/javascript" src="<%=contextPath %>/core/js/prototype.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/datastructs.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/sys.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/smartclient.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/Calendarfy.js"></script>
<script type="text/javascript" src="./js/jquery-1.8.3.js"></script>
<script src="./js/T9Bbsfenyebar.js" type="text/javascript"></script>
<script src="./js/T9Bbsreplaylist.js" type="text/javascript"></script>
<script src="./js/forum_moderate.js" type="text/javascript"></script>
<link rel="stylesheet" type="text/css" href="<%=contextPath %>/cms/js/easyui/themes/default/easyui.css">
	<link rel="stylesheet" type="text/css" href="<%=contextPath %>/cms/js/easyui/themes/icon.css">
	<script type="text/javascript" src="<%=contextPath %>/cms/js/easyui/jquery.easyui.min.js"></script>
<link rel="stylesheet" type="text/css" href="css/style_1_common.css" />
<link rel="stylesheet" type="text/css" href="css/style_1_forum_viewthread.css" />
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
		String did = request.getParameter("did");
		if(did == null || "".equals(did)){
			did = "1";
		}


%>


<script type="text/javascript">

jQuery.noConflict();
var currpage= <%=currpage%>;
var pagesize= <%=pagesize%>;
var allowmaxpage= <%=allowmaxpage%>;
var data1 ;
var did = <%=did%>;
var maxcount = 0;
function doInit(){
	  url="<%=contextPath%>/t9/cms/bbs/documentinfo/act/T9BbsDocumentInfoAct/getDocumentById.act?did="+did;
		var rtJson = getJsonRs(url);
	  if(rtJson.rtState == "0"){
	    var data = rtJson.rtData;
	  jQuery("#thread_subject").html(data[0].title);
	  jQuery("#postmessage_content").html(data[0].content);//
	  jQuery("#postmessage_creater_info").html("本帖最后由 "+data[0].creatername+"于 "+data[0].createtime+"创建");
	  
	  jQuery("#doccreater").html("<a href=\"#\" class=\"xw1\" >"+data[0].creatername+"</a>");//
	  jQuery("#lookcount").html(data[0].lookcount);
	  jQuery("#replaycount").html(data[0].replaycount);
	  }
	}
	
function getreplaylist(){
	 url="<%=contextPath%>/t9/cms/bbs/comment/act/T9BbsCommentAct/getCommentsByBid.act?did="+did+"&currpage="+currpage+"&pagesize="+pagesize;
		
	 var rtJson = getJsonRs(url);
	  if(rtJson.rtState == "0"){
		  data1 = rtJson.rtData;
	  }
	}
function getlistmaxcount(){
	 url="<%=contextPath%>/t9/cms/bbs/comment/act/T9BbsCommentAct/getCommentsMaxCount.act?did="+did;
		
	 var rtJson = getJsonRs(url);
	  if(rtJson.rtState == "0"){
		  maxcount = rtJson.rtData.maxcount;
		 
		
	  }
	}
	
/**
 * 避免jquery 与其他类库冲突
 */
jQuery(function(){
	doInit();
	getlistmaxcount();
jQuery("#fenye1").T9Bbsfenyebar({pagesize:pagesize,currpage:currpage,maxcount:maxcount,url:"index.jsp",basepara:"did="+did});
jQuery("#pgt").T9Bbsfenyebar({pagesize:pagesize,currpage:currpage,maxcount:maxcount,url:"index.jsp",basepara:"did="+did});
getreplaylist();
jQuery("#post_22767").T9Bbsreplaylist({data:data1});

});




function addComment(){
	$("did").value=<%=did%>;
	//$("form1").submit();
}

function addComment1(){
	$("did1").value=<%=did%>;
	$("form2").submit();
}

function updateComment1(){
	$("did2").value=<%=did%>;
	$("form3").submit();
}
function updateComment2(){
	$("did3").value=<%=did%>;
	$("form4").submit();
}

function fetchOffset(obj, mode) {
	var left_offset = 0, top_offset = 0, mode = !mode ? 0 : mode;

	if(obj.getBoundingClientRect && !mode) {
		var rect = obj.getBoundingClientRect();
		var scrollTop = Math.max(document.documentElement.scrollTop, document.body.scrollTop);
		var scrollLeft = Math.max(document.documentElement.scrollLeft, document.body.scrollLeft);
		if(document.documentElement.dir == 'rtl') {
			scrollLeft = scrollLeft + document.documentElement.clientWidth - document.documentElement.scrollWidth;
		}
		left_offset = rect.left + scrollLeft - document.documentElement.clientLeft;
		top_offset = rect.top + scrollTop - document.documentElement.clientTop;
	}
	if(left_offset <= 0 || top_offset <= 0) {
		left_offset = obj.offsetLeft;
		top_offset = obj.offsetTop;
		while((obj = obj.offsetParent) != null) {
			position = getCurrentStyle(obj, 'position', 'position');
			if(position == 'relative') {
				continue;
			}
			left_offset += obj.offsetLeft;
			top_offset += obj.offsetTop;
		}
	}
	return {'left' : left_offset, 'top' : top_offset};
}


function mouseCoords(ev) 
{ 
	if(ev.pageX || ev.pageY){ 
	return {x:ev.pageX, y:ev.pageY}; 
	} 
  return{ 
  x:ev.clientX + document.body.scrollLeft - document.body.clientLeft, 
  y:ev.clientY + document.body.scrollTop - document.body.clientTop 
 }
}


function showwindow(ev,seqId){
	Ev= ev || window.event; 
	var mousePos = mouseCoords(ev); 
	var fhtml = jQuery("#tdmessage_"+seqId).html();
//	jQuery("#form2text").html("回复“"+fhtml+"”")
	jQuery("#content1").html("回复“"+fhtml+"”<br />")
	var locX=(screen.width)/2;
	var locY=mousePos.y;
	jQuery('#w1231').window({
		title: '回复',
		width: 600,
		height:200,
	  top:locY,
	  left:mousePos.x,
		modal: true,
		shadow: false,
		closed: false
	});
}
function resize(){
	jQuery('#w1231').window("close");
}
function resize2(){
	jQuery('#w1232').window("close");
}
function resize3(){
	jQuery('#w1233').window("close");
}
function showupdatewindow(ev,seqId){
	Ev= ev || window.event; 
	var mousePos = mouseCoords(ev); 
	var fhtml = jQuery("#tdmessage_"+seqId).html();
	jQuery("#content2").html(fhtml);
	jQuery("#cid").val(seqId);
	var locX=(screen.width)/2;
	var locY=mousePos.y;
	jQuery('#w1232').window({
		title: '编辑',
		width: 600,
		height:200,
	  top: locY,
	  left:mousePos.x,
		modal: true,
		shadow: false,
		closed: false
	});
}

function showupdateContentwindow(ev,seqId){
	Ev= ev || window.event; 
	var mousePos = mouseCoords(ev); 
	var fhtml = jQuery("#postmessage_content").html();
	jQuery("#content3").html(fhtml);
	jQuery("#did4").val(seqId);
	var locX=(screen.width)/2;
	var locY=mousePos.y;
	jQuery('#w1233').window({
		title: '编辑',
		width: 600,
		height:200,
	  top: locY,
	  left:mousePos.x,
		modal: true,
		shadow: false,
		closed: false
	});
}

function showreplayContentwindow(ev){
	Ev= ev || window.event; 
	var mousePos = mouseCoords(ev); 
	var locX=(screen.width)/2;
	var locY=mousePos.y;
	jQuery('#w1231').window({
		title: '回复',
		width: 600,
		height:200,
	  top: locY,
	  left:mousePos.x,
		modal: true,
		shadow: false,
		closed: false
	});
}
</script>
</head>
<body id="nv_forum" class="pg_viewthread" onkeydown="if(event.keyCode==27) return false;" >
<div id="append_parent"></div>
<div id="ajaxwaitid"></div>
<div id="toptb" class="cl">
<div class="wp">
<div class="z">

</div>
</div>
</div>


<div id="qmenu_menu" class="p_pop blk" style="display: none;">
<div class="ptm pbw hm">
</div>
</div>
<div id="hd">
<div class="wp">
<div class="hdc cl"><h2><a href="./" title="<%=shortOrgName %>用户社区[OA论坛]"><img src="css/images/tongda.png" alt="" border="0" /></a></h2>
</div>
<!--  -->
<div id="nv">
<ul><li class="a" id="mn_forum" ><a href="../portal/index.jsp" hidefocus="true" title="BBS"  >论坛<span>BBS</span></a></li></ul>
</div>
<div id="wp" class="wp">
<!-- 这里注释掉；额 导航 只有可能会加上 所以这里注释掉了
<div id="pt" class="bm cl">

<div class="z">
<a href="./" class="nvhm" title="首页"><%=shortOrgName %>用户社区[OA论坛]</a> <em>&rsaquo;</em> <a href="forum.php">论坛</a> <em>&rsaquo;</em> <a href="forum.php?gid=39">OA2011版专区</a> <em>&rsaquo;</em> <a href="forum.php?mod=forumdisplay&fid=66&page=1">OA精灵PC版、MAC版、移动版</a> <em>&rsaquo;</em> <a href="forum.php?mod=viewthread&amp;tid=8096">苹果和安卓移动版只有管理员有工作流模块 ...</a>
</div>
</div>
-->
<div id="ct" class="wp cl">
<div id="pgt" class="bm bw0 pgs cl" >

<a href="../portal/newComment.jsp?style=2" title="发新帖1"><img src="./css/images/pn_post.png" alt="发新帖" /></a>
</div>



<div id="postlist" class="pl bm">
<table cellspacing="0" cellpadding="0">
	<tr>
		<td class="pls ptm pbm">
			<div class="hm">
				<span class="xg1">查看:</span> <span class="xi1" id="lookcount"></span><span class="pipe">|</span><span class="xg1" >回复:</span> <span class="xi1" id="replaycount"></span>
			</div>
		</td>
		<td class="plc ptm pbn">
			<div class="y">
			<a href="" title="打印" target="_blank"><img src="css/images/print.png" alt="打印" class="vm" /></a>
			<a href="" title="上一主题"><img src="css/images/thread-prev.png" alt="上一主题" class="vm" /></a>
			<a href="" title="下一主题"><img src="css/images/thread-next.png" alt="下一主题" class="vm" /></a>
			</div>
			<h1 class="ts">
			<a href="#" id="thread_subject">这里是标题</a>
				<span class="xw0 xs1 xg1">
				</span>
			</h1>
		</td>
	</tr>
</table>


<table cellspacing="0" cellpadding="0" class="ad"><tr><td class="pls"></td><td class="plc"></td></tr></table><div id="post_22759">
<table id="pid22759" summary="pid22759" cellspacing="0" cellpadding="0">
<tr>
	<td class="pls" rowspan="2">
		<a name="newpost"></a> 
		<div class="pi">
		<div class="authi" id="doccreater">
		</div>
		</div>
		<div>
		<div class="avatar" ><a href="#">
		<img src="css/images/noavatar_middle.gif"/></a></div>
		<p><em><a href="" target="_blank"><font color="#109dff"><b></b></font></a></em></p>
		</div>
	</td>
	<td class="plc">
		<div class="pi">
		<div class="pti">
		<div class="authi">
		<img class="authicn vm" id="authicon22759" src="css/images/online_member.gif" />
		<em id="authorposton22759">发表于 <span title="2013-1-22 17:37:29">6&nbsp;天前</span></em>
		</div>
		</div>
		</div>
		<div class="pct"><style type="text/css">.pcb{margin-right:0}</style><div class="pcb">
		<div class="t_fsz">
			<table cellspacing="0" cellpadding="0"><tr><td class="t_f" id="postmessage_22759">
			<i class="pstatus"><span id="postmessage_creater_info"> </span> </i><br />
			<br />
			<div id="postmessage_content"> 这里是内容！</div>
			<br />
			</td></tr></table>
		</div>
	</td>
</tr>
	<tr><td class="plc plm">
	<div id="p_btn" class="mtw mbm cl">
	</div>
	</td>
	</tr>
	<tr class="ad">
	<td class="pls"></td>
	<td class="plc">
		<div class="po">
			<div class="pob cl">
			<em>
			<a class="fastre" href="javascript:void('0')"  onclick="showreplayContentwindow(event)">回复</a>
			<a class="editp" href="javascript:void('0')" onclick="showupdateContentwindow(event,<%=did%>)">编辑</a></em>
			

			<ul id="mgc_post_23327_menu" class="p_pop mgcmn" style="display: none;">
			</ul>
			</div>
		</div>
	</td>
	</tr>
</table>
</div>
<!--111111111111111111111111111111111111111111111111111111111111111-->
<div id="post_22767">

	
<div id="mdly" class="fwinmask" style="display:none;">
<table cellspacing="0" cellpadding="0" class="fwin">
<tr>
<td class="t_l"></td>
<td class="t_c"></td>
<td class="t_r"></td>
</tr>
<tr>
<td class="m_l">&nbsp;&nbsp;</td>
<td class="m_c">
<div class="f_c">
<div class="c">
<h3>选中&nbsp;<strong id="mdct" class="xi1"></strong>&nbsp;篇: </h3>
<a href="javascript:;" onclick="modaction('warn')">警告</a><span class="pipe">|</span><a href="javascript:;" onclick="modaction('banpost')">屏蔽</a><span class="pipe">|</span><a href="javascript:;" onclick="modaction('delpost')">删除</a><span class="pipe">|</span><a href="javascript:;" onclick="modaction('stickreply')">置顶</a><span class="pipe">|</span></div>
</div>
</td>
<td class="m_r"></td>
</tr>
<tr>
<td class="b_l"></td>
<td class="b_c"></td>
<td class="b_r"></td>
</tr>
</table>
</div>
	
</div>

	<!-- 分页开始 -->
		<div class="pgs mtm mbm cl"  id="fenye1">
		<a  href='../portal/newComment.jsp?style=2' title ="发新帖"><img src="css/images/pn_post.png" alt="发新帖" /></a>
		</div>
	<!-- 分页结束 -->
	
	<div id="f_pst" class="pl bm bmw">
<form id="form1" name="form1" method="post" enctype="multipart/form-data"  action="<%=contextPath%>/t9/cms/bbs/comment/act/T9BbsCommentAct/addComment1.act">
<table cellspacing="0" cellpadding="0">
<tr>
<td class="pls">
<div class="avatar"><img src="css/images/noavatar_middle.gif" /></div></td>
<td class="plc">
<div class="cl">
<div id="fastsmiliesdiv" class="y"><div id="fastsmiliesdiv_data"><div id="fastsmilies"></div></div></div><div class="hasfsl" id="fastposteditor">
<div class="tedt mtn">

<div class="area">
<input id="did" name="did" type="hidden">
<textarea rows="6" cols="80" name="content" id="content"  tabindex="4" class="pt"></textarea>
</div>
</div>
</div>
</div>
<p class="ptm pnpost">
<button  name="replysubmit" id="fastpostsubmit" class="pn pnc vm" value="replysubmit" tabindex="5" onclick="addComment();"><strong>发表回复</strong></button>
</p>
</td>
</tr>
</table>
</form>
</div>

</div>

</div>
<div id="ft" class="wp cl">
<div id="flk" class="y">
<p><a href="archiver/" >Archiver</a><span class="pipe">|</span><a href="forum.php?mobile=yes" >手机版</a><span class="pipe">|</span><strong><a href="http://club.tongda2000.com/" target="_blank"><%=shortOrgName %>用户社区[OA论坛]</a></strong>
( <a href="http://www.miitbeian.gov.cn/" target="_blank">京ICP备05006333号</a> )</p>
<p class="xs0">
GMT+8, 2013-1-28 13:49<span id="debuginfo">
, Processed in 0.047233 second(s), 6 queries
, Apc On.
</span>
</p>
</div>
<div id="frt">
<p>Powered by <strong><a href="http://www.discuz.net" target="_blank">Discuz!</a></strong> <em>X2</em></p>
<p class="xs0">&copy; 2001-2011 <a href="http://www.comsenz.com" target="_blank">Comsenz Inc.</a></p>
</div></script>
</div>
<div id="w1231" class="easyui-window" title="回复" iconCls="icon-save" closed="true" style="width:500px;height:200px;padding:5px;background: #fafafa;">
		<div class="easyui-layout" fit="true">
			<div region="center" border="false" style="padding:10px;background:#fff;border:1px solid #ccc;">
			<div id="form2text"> </div>
				<div class="area">
				<form id="form2" name="form2" method="post" enctype="multipart/form-data"  action="<%=contextPath%>/t9/cms/bbs/comment/act/T9BbsCommentAct/addComment1.act">
				<input id="did1" name="did" type="hidden">
				<textarea rows="6" cols="80" name="content" id="content1"  tabindex="4" class="pt"></textarea>
				</form>
				</div>
			</div>
			<div region="south" border="false" style="text-align:right;height:30px;line-height:30px;">
				<a class="easyui-linkbutton" iconCls="icon-ok" href="javascript:void(0)" onclick="addComment1()">Ok</a>
				<a class="easyui-linkbutton" iconCls="icon-cancel" href="javascript:void(0)" onclick="resize()">Cancel</a>
			</div>
		</div>
</div>
<div id="w1232" class="easyui-window" title="编辑" iconCls="icon-save" closed="true" style="width:500px;height:200px;padding:5px;background: #fafafa;">
		<div class="easyui-layout" fit="true">
			<div region="center" border="false" style="padding:10px;background:#fff;border:1px solid #ccc;">
				<div class="area">
				<form id="form3" name="form3" method="post"   action="<%=contextPath%>/t9/cms/bbs/comment/act/T9BbsCommentAct/updateCommentById.act">
				<input id="cid" name="cid" type="hidden">
					<input id="did2" name="did" type="hidden">
				<textarea rows="6" cols="80" name="content" id="content2"  tabindex="4" class="pt"></textarea>
				</form>
				</div>
			</div>
			<div region="south" border="false" style="text-align:right;height:30px;line-height:30px;">
				<a class="easyui-linkbutton" iconCls="icon-ok" href="javascript:void(0)" onclick="updateComment1()">Ok</a>
				<a class="easyui-linkbutton" iconCls="icon-cancel" href="javascript:void(0)" onclick="resize2()">Cancel</a>
			</div>
		</div>
</div>
<div id="w1233" class="easyui-window" title="编辑" iconCls="icon-save" closed="true" style="width:500px;height:200px;padding:5px;background: #fafafa;">
		<div class="easyui-layout" fit="true">
			<div region="center" border="false" style="padding:10px;background:#fff;border:1px solid #ccc;">
				<div class="area">
				<form id="form4" name="form4" method="post"   action="<%=contextPath%>/t9/cms/bbs/comment/act/T9BbsCommentAct/updateComment1ById.act">
					<input id="did3" name="did" type="hidden">
				<textarea rows="6" cols="80" name="content" id="content3"  tabindex="4" class="pt"></textarea>
				</form>
				</div>
			</div>
			<div region="south" border="false" style="text-align:right;height:30px;line-height:30px;">
				<a class="easyui-linkbutton" iconCls="icon-ok" href="javascript:void(0)" onclick="updateComment2()">Ok</a>
				<a class="easyui-linkbutton" iconCls="icon-cancel" href="javascript:void(0)" onclick="resize3()">Cancel</a>
			</div>
		</div>
</div>

</body>
</html>
