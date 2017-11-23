<? 
	include_once("../inc_header.php");
	include_once("inc/utility_all.php");   
	$query = "SELECT count(*) from EMAIL,EMAIL_BODY where EMAIL_BODY.BODY_ID=EMAIL.BODY_ID and BOX_ID=0 and TO_ID='$LOGIN_USER_ID' and SEND_FLAG='1' and (DELETE_FLAG='' or DELETE_FLAG='0' or DELETE_FLAG='2')";
   $TOTAL_ITEMS['inbox'] = resultCount($query);
	$query = "SELECT count(*) from EMAIL_BODY where FROM_ID='$LOGIN_USER_ID' and SEND_FLAG='0'";	
	$TOTAL_ITEMS['draftbox'] = resultCount($query);
	$query = "SELECT count(*) from EMAIL_BODY,EMAIL LEFT JOIN USER ON USER.USER_ID = EMAIL.TO_ID where EMAIL.BODY_ID=EMAIL_BODY.BODY_ID and FROM_ID='$LOGIN_USER_ID' and SEND_FLAG='1' and DELETE_FLAG!='2' and DELETE_FLAG!='4'";	
	$TOTAL_ITEMS['sendbox'] = resultCount($query);
	$query = "SELECT count(*) from EMAIL,EMAIL_BODY LEFT JOIN USER ON USER.USER_ID = EMAIL_BODY.FROM_ID where EMAIL.BODY_ID=EMAIL_BODY.BODY_ID and TO_ID='$LOGIN_USER_ID' and (DELETE_FLAG='3' or DELETE_FLAG='4')";
	$TOTAL_ITEMS['delbox'] = resultCount($query);
			
?>
<script>
var stype = "email";
var nonewdata = "<?=_('没有新邮件')?>";
var newdata = "<?=_('%s封新邮件')?>";
var email_PadHD = "\r\n\r\n"+"<?=_('发自Pad HD版.')?>";
var Scroll_plist;
var nowriteReceiver = "<?=_('收件人不能为空')?>";
var errorWebAddress = "<?=_('外部邮箱格式不对')?>";
var side_nomoredata_2 = false;
var side_noshowPullUp_2 = <? echo ($PAGE_SIZE >= $TOTAL_ITEMS['inbox']) ? "true" : "false"; ?>;
var side_nomoredata_3 = false;
var side_noshowPullUp_3 = <? echo ($PAGE_SIZE >= $TOTAL_ITEMS['draftbox']) ? "true" : "false"; ?>;
var side_nomoredata_4 = false;
var side_noshowPullUp_4 = <? echo ($PAGE_SIZE >= $TOTAL_ITEMS['sendbox']) ? "true" : "false"; ?>;
var side_nomoredata_5 = false;
var side_noshowPullUp_5 = <? echo ($PAGE_SIZE >= $TOTAL_ITEMS['delbox']) ? "true" : "false"; ?>;

function pageInit(type, page_id)
{
	if(type == "side")
	{
	   if(page_id == 1)				//收件箱、发件箱、草稿..
	   {
	      tiScroll_1 = new $.tiScroll({"page_type": "side", "listType": "readonly"});
	      tiScroll_1.init();
	   }
		if(page_id == 2){
	      tiScroll_2 = new $.tiScroll({
				"page_id": 2, 
				"page_type": "side", 
				"listType": "listview",
				"nomoredata": side_nomoredata_2, 
				"noshowPullUp":side_noshowPullUp_2,
				refreshCallback:function(){
					if($('ul.multi',this.scroller).size()){			//多选状态不更新时间
						return;
					}
					var now = new Date(),									
					d = [ now.getFullYear(), now.getMonth()+1, now.getDay() ].join('-'),
					t = [ now.getHours(), now.getMinutes() ].join(':'),
					msg = $("<div class='updateTime'>").css({
						fontSize: '10pt'
					}).html('更新于 ' + d + ' ' + t);
					$('#siderfooter_2').html(msg).show();				//最后更新时间
				}
			});
	      tiScroll_2.init();
	   }
		if(page_id == 3){
	      tiScroll_3 = new $.tiScroll({
				"page_id": 3, 
				"page_type": "side", 
				"listType": "listview",
				"nomoredata": side_nomoredata_3, 
				"noshowPullUp":side_noshowPullUp_3,
				onPullUp: emailPullUpEvent.draftbox,
				onPullDown: emailPullDownEvent.draftbox,
				refreshCallback:function(){
					if($('ul.multi',this.scroller).size()){			//多选状态不更新时间
						return;
					}
					var now = new Date(),									
					d = [ now.getFullYear(), now.getMonth()+1, now.getDay() ].join('-'),
					t = [ now.getHours(), now.getMinutes() ].join(':'),
					msg = $("<div class='updateTime'>").css({
						fontSize: '10pt'
					}).html('更新于 ' + d + ' ' + t);
					$('#siderfooter_2').html(msg).show();				//最后更新时间
				}
			});
	      tiScroll_3.init();
	   }
		if(page_id == 4){
	      tiScroll_4 = new $.tiScroll({
				"page_id": 4, 
				"page_type": "side", 
				"listType": "listview",
				"nomoredata": side_nomoredata_4, 
				"noshowPullUp":side_noshowPullUp_4,
				onPullUp: emailPullUpEvent.sendbox,
				onPullDown: emailPullDownEvent.sendbox,
				refreshCallback:function(){
					if($('ul.multi',this.scroller).size()){			//多选状态不更新时间
						return;
					}
					var now = new Date(),									
					d = [ now.getFullYear(), now.getMonth()+1, now.getDay() ].join('-'),
					t = [ now.getHours(), now.getMinutes() ].join(':'),
					msg = $("<div class='updateTime'>").css({
						fontSize: '10pt'
					}).html('更新于 ' + d + ' ' + t);
					$('#siderfooter_2').html(msg).show();				//最后更新时间
				}
			});
	      tiScroll_4.init();
	   }
		if(page_id == 5){
	      tiScroll_5 = new $.tiScroll({
				"page_id": 5, 
				"page_type": "side", 
				"listType": "listview",
				"nomoredata": side_nomoredata_5, 
				"noshowPullUp":side_noshowPullUp_5,
				onPullUp: emailPullUpEvent.delbox,
				onPullDown: emailPullDownEvent.delbox,
				refreshCallback:function(){
					if($('ul.multi',this.scroller).size()){			//多选状态不更新时间
						return;
					}
					var now = new Date(),									
					d = [ now.getFullYear(), now.getMonth()+1, now.getDay() ].join('-'),
					t = [ now.getHours(), now.getMinutes() ].join(':'),
					msg = $("<div class='updateTime'>").css({
						fontSize: '10pt'
					}).html('更新于 ' + d + ' ' + t);
					$('#siderfooter_2').html(msg).show();				//最后更新时间
				}
			});
	      tiScroll_5.init();
	   }
	}else{								//邮件内容
		if(page_id == 1)
	   {
	      tiScroll_1_main = new $.tiScroll({"page_type": "main", "listType": "readonly"});
	      tiScroll_1_main.init();
	   }		
		if(page_id == 'multi')		//多选状态，内容预览区域
	   {
	      tiScroll_multi_main = new $.tiScroll({"page_id": "multi","page_type": "main", "listType": "readonly"});
	      tiScroll_multi_main.init();
	   }
	}
}
</script>
<div id="sideContentArea">
	<?
		//导航数据
	   $tSideBarHeadData = array(
	      "1" => array(
	      	"display" => "none",
	         "c" => array("title" => "邮箱")
	      ),
	      "2" => array(
	      	"display" => "block",
	         "l" => array("class" => "","event" => "sidereback(2,1);", "title" => "邮箱"),
	         "c" => array("title" => "收件箱"),
	         "r" => array("class" => "", "event" => "multiModeToggle();", "title" => "编辑")
	      ),
	      "3" => array(
	      	"display" => "none",
	         "l" => array("class" => "","event" => "sidereback(3,1);", "title" => "邮箱"),
	         "c" => array("title" => "草稿箱"),
	         "r" => array("class" => "", "event" => "multiModeToggle();", "title" => "编辑")
	      ),
	      "4" => array(
	      	"display" => "none",
	         "l" => array("class" => "","event" => "sidereback(4,1);", "title" => "邮箱"),
	         "c" => array("title" => "已发送"),
	         "r" => array("class" => "", "event" => "multiModeToggle();", "title" => "编辑")
	      ),
	      "5" => array(
	      	"display" => "none",
	         "l" => array("class" => "","event" => "sidereback(5,1);", "title" => "邮箱"),
	         "c" => array("title" => "废纸篓"),
	         "r" => array("class" => "", "event" => "multiModeToggle();", "title" => "编辑")
	      )
	   ); 
	?>
	
	<?=buildSiderHead($tSideBarHeadData)?>
	<?=buildMessage();?>

	<!-- type of email -->
	<div id="sideContentPage_1" class="sideContentPage" style="display:none;">
		<div id="sideContentWrapper_1" class="wrapper">
			<div id="contentScroller_1" class="scroller">
				<ul id="sidebar_email" class="sideBarSubList sideBarCateList">
					<li><a href="javascript:void(0);" class="inbox" onclick="sidereback(1,2);"><?=_("收件箱")?></a><span class="ui-icon-num">12</span><span class="ui-icon-rarrow"></span></li>
					<li><a href="javascript:void(0);" class="outbox" onclick="sidereback(1,3);"><?=_("草稿箱")?></a><span class="ui-icon-rarrow"></span></li>
					<li><a href="javascript:void(0);" class="sentbox" onclick="sidereback(1,4);"><?=_("已发送")?></a><span class="ui-icon-rarrow"></span></li>
					<li><a href="javascript:void(0);" class="delbox" onclick="sidereback(1,5);"><?=_("废纸篓")?></a><span class="ui-icon-rarrow"></span></li>
				</ul>	
			</div>
		</div>
	</div>
	
	<!-- list of email 收件箱 -->
	<div id="sideContentPage_2" class="sideContentPage">
		<div id="sideContentWrapper_2" class="wrapper">
			<div id="contentScroller_2" class="scroller">
				<?=buildPullDown();
				 if($TOTAL_ITEMS > 0){
						?>
					<ul id="email-inbox-list" class="comm-list sideBarSubList preViewList">
						<?
						$query = "SELECT EMAIL_ID,FROM_ID,SUBJECT,READ_FLAG,from_unixtime(SEND_TIME) as SEND_TIME,CONTENT,IMPORTANT,ATTACHMENT_ID,ATTACHMENT_NAME,USER.USER_NAME from EMAIL,EMAIL_BODY left join USER on EMAIL_BODY.FROM_ID=USER.USER_ID where EMAIL_BODY.BODY_ID=EMAIL.BODY_ID and BOX_ID=0 and TO_ID='$LOGIN_USER_ID' and SEND_FLAG='1' and (DELETE_FLAG='' or DELETE_FLAG='0' or DELETE_FLAG='2') order by SEND_TIME desc,EMAIL_ID desc limit 0 ,$PAGE_SIZE";
						$cursor= exequery($connection,$query);
						while($ROW=mysql_fetch_array($cursor))
						{
							$COUNT++;
							$EMAIL_ID=$ROW["EMAIL_ID"];
							$FROM_ID=$ROW["FROM_ID"];
							$SUBJECT=$ROW["SUBJECT"];
							$SEND_TIME=$ROW["SEND_TIME"];
							$IMPORTANT=$ROW["IMPORTANT"];
							$ATTACHMENT_ID=$ROW["ATTACHMENT_ID"];
							$ATTACHMENT_NAME=$ROW["ATTACHMENT_NAME"];
							$FROM_NAME=$ROW["USER_NAME"];
							$CONTENT = $ROW["CONTENT"];
							$READ_FLAG = $ROW["READ_FLAG"];
							
							$SUBJECT=htmlspecialchars($SUBJECT);
							if($FROM_NAME == "")
								$FROM_NAME=$FROM_ID;
						
							if($IMPORTANT=='0' || $IMPORTANT=="")
								$IMPORTANT_DESC="";
							else if($IMPORTANT=='1')
								$IMPORTANT_DESC="<font color=red>"._("重要")."</font>";
							else if($IMPORTANT=='2')
								$IMPORTANT_DESC="<font color=red>"._("非常重要")."</font>";
								
							if($SUBJECT=="")
								$SUBJECT = _("无标题");
								
							if($READ_FLAG!=1)
							{
								$Class = "unread";
							}else{
								$Class = "";
							}
						?>
						<li class="<?=$Class?>" q_id="<?=$EMAIL_ID?>">
							<h3><span class="time"><?=timeintval(strtotime($SEND_TIME))?></span><?=$FROM_NAME?></h3>
							 <?=$IMPORTANT_DESC?><p class="title"><?=$SUBJECT?></p>
							<p class="content"><?=strip_tags($CONTENT)?></p>	
						</li>
						<? } ?>
					</ul>
					<?}else{
							echo '</ul>';
                     echo '<div class="no_msg">'._("暂无邮件！").'</div>';
					}?>
					
					
					
				<?=buildPullUp();?>		
			</div>
		</div>
	</div>
	
	

		<!-- list of email 草稿箱 -->
	<div id="sideContentPage_3" class="sideContentPage hide">
		<div id="sideContentWrapper_3" class="wrapper">
			<div id="contentScroller_3" class="scroller">
				<?=buildPullDown();
				 if($TOTAL_ITEMS > 0){
						?>
					<ul id="email-draftbox-list" class="comm-list sideBarSubList preViewList">
						<?
						$query = "SELECT BODY_ID as EMAIL_ID,COPY_TO_ID,SECRET_TO_ID,SUBJECT,IMPORTANT,ATTACHMENT_ID,ATTACHMENT_NAME,CONTENT,SIZE,from_unixtime(SEND_TIME) as SEND_TIME from EMAIL_BODY where FROM_ID='$LOGIN_USER_ID' and SEND_FLAG='0' order by SEND_TIME desc limit 0 ,$PAGE_SIZE";
						$cursor= exequery($connection,$query);
						while($ROW=mysql_fetch_array($cursor))
						{
							$COUNT++;
							$EMAIL_ID=$ROW["EMAIL_ID"];
							$FROM_ID=$ROW["FROM_ID"];
							$SUBJECT=$ROW["SUBJECT"];
							$SEND_TIME=$ROW["SEND_TIME"];
							$IMPORTANT=$ROW["IMPORTANT"];
							$ATTACHMENT_ID=$ROW["ATTACHMENT_ID"];
							$ATTACHMENT_NAME=$ROW["ATTACHMENT_NAME"];
							$FROM_NAME=$ROW["USER_NAME"];
							$CONTENT = $ROW["CONTENT"];
							$READ_FLAG = $ROW["READ_FLAG"];
							
							$SUBJECT=htmlspecialchars($SUBJECT);
							if($FROM_NAME == "")
								$FROM_NAME=$FROM_ID;
							$FROM_NAME = _('草稿');
							if($IMPORTANT=='0' || $IMPORTANT=="")
								$IMPORTANT_DESC="";
							else if($IMPORTANT=='1')
								$IMPORTANT_DESC="<font color=red>"._("重要")."</font>";
							else if($IMPORTANT=='2')
								$IMPORTANT_DESC="<font color=red>"._("非常重要")."</font>";
								
							if($SUBJECT=="")
								$SUBJECT = _("无标题");
								
					
							$Class = "";
						?>
						<li class="<?=$Class?>" q_id="<?=$EMAIL_ID?>">
							<h3><span class="time"><?=timeintval(strtotime($SEND_TIME))?></span><?=$FROM_NAME?></h3>
							 <?=$IMPORTANT_DESC?><p class="title"><?=$SUBJECT?></p>
							<p class="content"><?=strip_tags($CONTENT)?></p>	
						</li>
						<? } ?>
					</ul>
					<?}else{
							echo '</ul>';
                     echo '<div class="no_msg">'._("暂无邮件！").'</div>';
					}?>
					
					
					
				<?=buildPullUp();?>		
			</div>
		</div>
	</div>
		<!-- list of email 已发送 -->
	<div id="sideContentPage_4" class="sideContentPage hide">
		<div id="sideContentWrapper_4" class="wrapper">
			<div id="contentScroller_4" class="scroller">
				<?=buildPullDown();
				 if($TOTAL_ITEMS > 0){
						?>
					<ul id="email-sendbox-list" class="comm-list sideBarSubList preViewList">
						<?
						$query = "SELECT EMAIL_ID,TO_ID,READ_FLAG,DELETE_FLAG,EMAIL_BODY.BODY_ID,TO_ID2,COPY_TO_ID,TO_WEBMAIL,SUBJECT,from_unixtime(SEND_TIME) as SEND_TIME,ATTACHMENT_ID,ATTACHMENT_NAME,IMPORTANT,SIZE,IS_WEBMAIL,WEBMAIL_FLAG,USER_NAME,DEPT_ID from EMAIL_BODY,EMAIL LEFT JOIN USER ON USER.USER_ID = EMAIL.TO_ID where EMAIL.BODY_ID=EMAIL_BODY.BODY_ID and FROM_ID='$LOGIN_USER_ID' and SEND_FLAG='1' and DELETE_FLAG!='2' and DELETE_FLAG!='4' group by EMAIL.BODY_ID order by SEND_TIME desc limit 0 ,$PAGE_SIZE;";

						$cursor= exequery($connection,$query);
						while($ROW=mysql_fetch_array($cursor))
						{
							$COUNT++;
							$EMAIL_ID=$ROW["EMAIL_ID"];
							$FROM_ID=$ROW["FROM_ID"];
							$SUBJECT=$ROW["SUBJECT"];
							$SEND_TIME=$ROW["SEND_TIME"];
							$IMPORTANT=$ROW["IMPORTANT"];
							$ATTACHMENT_ID=$ROW["ATTACHMENT_ID"];
							$ATTACHMENT_NAME=$ROW["ATTACHMENT_NAME"];
							$FROM_NAME=$ROW["USER_NAME"];
							$CONTENT = $ROW["CONTENT"];
							$READ_FLAG = $ROW["READ_FLAG"];
							
							$SUBJECT=htmlspecialchars($SUBJECT);
							if($FROM_NAME == "")
								$FROM_NAME=$FROM_ID;
						
							if($IMPORTANT=='0' || $IMPORTANT=="")
								$IMPORTANT_DESC="";
							else if($IMPORTANT=='1')
								$IMPORTANT_DESC="<font color=red>"._("重要")."</font>";
							else if($IMPORTANT=='2')
								$IMPORTANT_DESC="<font color=red>"._("非常重要")."</font>";
								
							if($SUBJECT=="")
								$SUBJECT = _("无标题");
						
							$Class = "";
						?>
						<li class="<?=$Class?>" q_id="<?=$EMAIL_ID?>">
							<h3><span class="time"><?=timeintval(strtotime($SEND_TIME))?></span><?=$FROM_NAME?></h3>
							 <?=$IMPORTANT_DESC?><p class="title"><?=$SUBJECT?></p>
							<p class="content"><?=strip_tags($CONTENT)?></p>	
						</li>
						<? } ?>
					</ul>
					<?}else{
							echo '</ul>';
                     echo '<div class="no_msg">'._("暂无邮件！").'</div>';
					}?>
					
					
					
				<?=buildPullUp();?>		
			</div>
		</div>
	</div>
		<!-- list of email 废纸篓 -->
	<div id="sideContentPage_5" class="sideContentPage hide">
		<div id="sideContentWrapper_5" class="wrapper">
			<div id="contentScroller_5" class="scroller">
				<?=buildPullDown();
				 if($TOTAL_ITEMS > 0){
						?>
					<ul id="email-delbox-list" class="comm-list sideBarSubList preViewList">
						<?
						$query = "SELECT EMAIL_BODY.BODY_ID,EMAIL_ID,TO_ID,READ_FLAG,FROM_ID,TO_ID2,COPY_TO_ID,SUBJECT,from_unixtime(SEND_TIME) as SEND_TIME,ATTACHMENT_ID,ATTACHMENT_NAME,IMPORTANT,SIZE,USER_NAME,AVATAR,DEPT_ID,IS_WEBMAIL,RECV_TO,RECV_TO_ID,RECV_FROM,RECV_FROM_NAME,IS_WEBMAIL from EMAIL,EMAIL_BODY LEFT JOIN USER ON USER.USER_ID = EMAIL_BODY.FROM_ID where EMAIL.BODY_ID=EMAIL_BODY.BODY_ID and TO_ID='$LOGIN_USER_ID' and (DELETE_FLAG='3' or DELETE_FLAG='4') order by SEND_TIME desc limit 0 ,$PAGE_SIZE;";
						$cursor= exequery($connection,$query);
						while($ROW=mysql_fetch_array($cursor))
						{
							$COUNT++;
							$EMAIL_ID=$ROW["EMAIL_ID"];
							$FROM_ID=$ROW["FROM_ID"];
							$SUBJECT=$ROW["SUBJECT"];
							$SEND_TIME=$ROW["SEND_TIME"];
							$IMPORTANT=$ROW["IMPORTANT"];
							$ATTACHMENT_ID=$ROW["ATTACHMENT_ID"];
							$ATTACHMENT_NAME=$ROW["ATTACHMENT_NAME"];
							$FROM_NAME=$ROW["USER_NAME"];
							$CONTENT = $ROW["CONTENT"];
							$READ_FLAG = $ROW["READ_FLAG"];
							
							$SUBJECT=htmlspecialchars($SUBJECT);
							if($FROM_NAME == "")
								$FROM_NAME=$FROM_ID;
						
							if($IMPORTANT=='0' || $IMPORTANT=="")
								$IMPORTANT_DESC="";
							else if($IMPORTANT=='1')
								$IMPORTANT_DESC="<font color=red>"._("重要")."</font>";
							else if($IMPORTANT=='2')
								$IMPORTANT_DESC="<font color=red>"._("非常重要")."</font>";
								
							if($SUBJECT=="")
								$SUBJECT = _("无标题");
								
						
							$Class = "";
						?>
						<li class="<?=$Class?>" q_id="<?=$EMAIL_ID?>">
							<h3><span class="time"><?=timeintval(strtotime($SEND_TIME))?></span><?=$FROM_NAME?></h3>
							 <?=$IMPORTANT_DESC?><p class="title"><?=$SUBJECT?></p>
							<p class="content"><?=strip_tags($CONTENT)?></p>	
						</li>
						<? } ?>
					</ul>
					<?}else{
							echo '</ul>';
                     echo '<div class="no_msg">'._("暂无邮件！").'</div>';
					}?>
					
					
					
				<?=buildPullUp();?>		
			</div>
		</div>
	</div>
	-->
	<?
		//导航数据
	   $tSideBarFootData = array(
	      "2" => array(
	         "c" => array("title" => "")
	      )
	   ); 
	?>
	<?=buildSiderFoot($tSideBarFootData)?>
</div>
		
<div id="mainContentArea">
	<?=buildMainProLoading()?>
	<?
		//导航数据
	   $tMainBarHeadData = array(
	   	"1" => array(
	         "c" => array("title" => "收件箱"),
				"r" => array("class" => "icon-group", "title" => '<b class="icon-item recycle"></b><b class="icon-item newEmail"></b>')
	      ),
			"multi" => array(
	         "c" => array("title" => "选择要删除或移动的邮件")
			)
	   ); 
	?>
	<?=buildMainHead($tMainBarHeadData)?>
	<div id="mainContentPage_1" class="mainContentPage tzoom">
		<div id="mainContentWrapper_1" class="wrapper">
			<div id="contentScroller_1" class="scroller shadowscroller">
			</div>
		</div>
	</div>
	<div id="mainContentPage_multi" class="mainContentPage tzoom hide">
		<div id="mainContentWrapper_multi" class="wrapper">
			<div class="preview-box-wrapper">
			</div>
		</div>
	</div>
</div>

<script type="text/javascript">
tPad.changeLayout();
var multi = tPad.multi;
pageInit('main',1);
function multiModeToggle(){								//多选模式 打开/关闭
	var $header = $('#siderheader>div:visible'),
	$btn = $('.combtn.rbtn',$header),
	$lbtn = $('.lcbtn',$header),
	$list = $(".preViewList"),
	isMulti = $list.hasClass('multi'),
	activeId = $list.find('.activebg').attr('q_id'),
	lastActive,
	$footer = $('#siderfooter_2'),
	$btnGroup = '<div class=".btn-group"><span class="combtn active" style="position:relative;margin: 0 10px;top:0;" onclick="multiDelete()"><span>删除</span></span></div>';
	if(isMulti){					//关闭复选
		lastActive = $list.data('last_active');
		$list
			.removeClass('multi')
			.find('.activebg').removeClass('activebg');
		lastActive && $list.find('[q_id='+lastActive+']').addClass('activebg');	
		multi.destory();
		$btn.removeClass('active').find('span').html('编辑');
		$footer.find('.btn-group').remove();
		$footer.find('div').show();
		$lbtn.show();
	}else{							//开启复选
		$list
			.addClass('multi')
			.find('.activebg').removeClass('activebg');
		
		$lbtn.hide();
		activeId &&	$list.data('last_active',activeId);
		
		$list.find('li').each(function(){
			var $this = $(this);
			if(!$this.find('.multi-check').size()){
				$this.append('<div class="multi-check"></div>');
			}
		});	
		multi.init();
		$btn.addClass('active').find('span').html('返回');
		$footer.find('div').hide();
		$footer.append($btnGroup);
	}
}

function multiDelete(){
	$('.sideContentPage:visible ul>li.activebg').each(function(){
		deleteEmail(this.getAttribute('q_id'), true);
	});
	multi.removeAll();
}
	
function deleteEmail(email_id, multi){
	$.post('email/delete.php',{EMAIL_ID: email_id},function(data){
		if(multi) {
			$('.sideContentPage:visible ul>li[q_id='+email_id+']').fadeOut('slow',function(){
				$(this).remove();
				tiScroll_2.refresh();
			});
			showMessage('删除成功！');
			return;
		};
		if(data == "+OK"){
			$(tiScroll_1_main.getOIScroll().scroller).toggleClass('scroller-fadein-top');
			var delete_target = $('.sideContentPage:visible ul>li[q_id='+email_id+']');
			delete_target.next().click();
			delete_target.fadeOut('slow', function(){
				$(this).remove();
				tiScroll_2.refresh();
			});
			showMessage('删除成功！');
		}		
	});
}
var emailPullDownEvent = {
	draftbox: function(){
		var lastedId =  $("li:first", tiScroll_3.getElement()).attr("q_id");
		var oiScroll = tiScroll_3.getOIScroll();
		var $$page_dom = tiScroll_3.getElement();
		var oUl = $('ul', tiScroll_3.getElement());
		$.get(
			"inc/getdata.php", 
			{'A':"GetNewDraft", 'STYPE':stype, "LASTEDID": lastedId},
			function(data)	{
				if(data == "NONEWDATA"){
					showMessage(nonewdata);
				}else{
					var size = $("<ul>"+data+"</ul>").find("li").size();
					var osize = oUl.find("li").size();
					
					if(osize == 0)
						$$page_dom.find(".no_msg").hide();
							
					oUl.prepend(data);
					showMessage(sprintf(newdata,size));
				}
				oiScroll.refresh();
			}
		);
		return false;	
	},
	sendbox: function(){
		var lastedId =  $("li:first", tiScroll_4.getElement()).attr("q_id");
		var oiScroll = tiScroll_4.getOIScroll();
		var $$page_dom = tiScroll_4.getElement();
		var oUl = $('ul', tiScroll_4.getElement());
		$.get(
			"inc/getdata.php", 
			{'A':"GetNewSend", 'STYPE':stype, "LASTEDID": lastedId},
			function(data)	{
				if(data == "NONEWDATA"){
					showMessage(nonewdata);
				}else{
					var size = $("<ul>"+data+"</ul>").find("li").size();
					var osize = oUl.find("li").size();
					
					if(osize == 0)
						$$page_dom.find(".no_msg").hide();
							
					oUl.prepend(data);
					showMessage(sprintf(newdata,size));
				}
				oiScroll.refresh();
			}
		);
		return false;	
	},
	delbox: function(){
		var lastedId =  $("li:first", tiScroll_5.getElement()).attr("q_id");
		var oiScroll = tiScroll_5.getOIScroll();
		var $$page_dom = tiScroll_5.getElement();
		var oUl = $('ul', tiScroll_5.getElement());
		
		$.get(
			"inc/getdata.php", 
			{'A':"GetNewDel", 'STYPE':stype, "LASTEDID": lastedId},
			function(data)	{
				if(data == "NONEWDATA"){
					showMessage(nonewdata);
				}else{
					var size = $("<ul>"+data+"</ul>").find("li").size();
					var osize = oUl.find("li").size();
					
					if(osize == 0)
						$$page_dom.find(".no_msg").hide();
							
					oUl.prepend(data);
					showMessage(sprintf(newdata,size));
				}
				oiScroll.refresh();
			}
		);
		return false;	
	}
};


var emailPullUpEvent = {
	draftbox: function(){
		var currIterms = $("li", tiScroll_3.getElement()).size();
		if(currIterms > 0){
			var lastGetId = $("li:last", tiScroll_3.getElement()).attr("q_id");    
		}
		$.get(
			"inc/getdata.php", 
			{'A':"GetMoreDraft", 'STYPE':stype, "P":p, "CURRITERMS": currIterms, "LASTGETID": lastGetId},
			function(data){
				if(data == "NOMOREDATA")
				{
					$(".pullUp", tiScroll_3.getElement()).remove();
					nomoredata = true;
					window[ page_type + "_nomoredata_" + page_id ] = true;
					noshowPullUp = true;
					window[ page_type + "_noshowPullUp_" + page_id ] = true;
					
					$(".scroller", tiScroll_3.getElement()).append('<div class="loadingComplete">' + td_lang.pda.msg_8 + '</div>');
				}else{
					$('ul', tiScroll_3.getElement()).append(data);
					tiScroll_3.getOIScroll().refresh();
				}
			}
		);  
		return false;	
	},
	sendbox: function(){
		var currIterms = $("li", tiScroll_4.getElement()).size();
		if(currIterms > 0){
			var lastGetId = $("li:last", tiScroll_4.getElement()).attr("q_id");    
		}
		$.get(
			"inc/getdata.php", 
			{'A':"GetMoreSend", 'STYPE':stype, "P":p, "CURRITERMS": currIterms, "LASTGETID": lastGetId},
			function(data){
				if(data == "NOMOREDATA")
				{
					$(".pullUp", tiScroll_4.getElement()).remove();
					nomoredata = true;
					window[ page_type + "_nomoredata_" + page_id ] = true;
					noshowPullUp = true;
					window[ page_type + "_noshowPullUp_" + page_id ] = true;
					
					$(".scroller", tiScroll_4.getElement()).append('<div class="loadingComplete">' + td_lang.pda.msg_8 + '</div>');
				}else{
					$('ul', tiScroll_4.getElement()).append(data);
					tiScroll_4.getOIScroll().refresh();
				}
			}
		);  
		return false;	
	},
	delbox: function(){
		var currIterms = $("li", tiScroll_5.getElement()).size();
		if(currIterms > 0){
			var lastGetId = $("li:last", tiScroll_5.getElement()).attr("q_id");    
		}
		$.get(
			"inc/getdata.php", 
			{'A':"GetMoreDel", 'STYPE':stype, "P":p, "CURRITERMS": currIterms, "LASTGETID": lastGetId},
			function(data){
				if(data == "NOMOREDATA")
				{
					$(".pullUp", tiScroll_5.getElement()).remove();
					nomoredata = true;
					window[ page_type + "_nomoredata_" + page_id ] = true;
					noshowPullUp = true;
					window[ page_type + "_noshowPullUp_" + page_id ] = true;
					
					$(".scroller", tiScroll_5.getElement()).append('<div class="loadingComplete">' + td_lang.pda.msg_8 + '</div>');
				}else{
					$('ul', tiScroll_5.getElement()).append(data);
					tiScroll_5.getOIScroll().refresh();
				}
			}
		);
		return false;		
	}
};
	
$(document).ready(function(){
	
	pageInit("side", 2);
	
	$(".preViewList li").die().live("click tap",function(){
		var $this = $(this),
		isMulti = $this.parents('ul').hasClass('multi');
		if(isMulti){
			return;
		}else{
			$(".preViewList li").removeClass("activebg");
			$(this).addClass("activebg");
			if($(this).hasClass("unread"))
				$(this).removeClass("unread");
		}		
	});
	
	$("#email-inbox-list li").die().live("click tap",function(){
	   var $this = $(this),
		isMulti = $('#email-inbox-list').hasClass('multi');	
		if(isMulti){
			multi[ $this.hasClass('activebg') ? 'remove' : 'add' ]($this.attr("q_id"));
			$this.toggleClass('activebg');
		}else{
			getEmailContent($this.attr("q_id"));
		}
	});	
	$("#email-draftbox-list li").die().live("click tap",function(){
	   var $this = $(this),
		isMulti = $('#email-inbox-list').hasClass('multi');	
		if(isMulti){
			multi[ $this.hasClass('activebg') ? 'remove' : 'add' ]($this.attr("q_id"));
			$this.toggleClass('activebg');
		}else{
			getEmailContent($this.attr("q_id"),'draft');
		}
	});	
	$("#email-sendbox-list li").die().live("click tap",function(){
	   var $this = $(this),
		isMulti = $('#email-inbox-list').hasClass('multi');	
		if(isMulti){
			multi[ $this.hasClass('activebg') ? 'remove' : 'add' ]($this.attr("q_id"));
			$this.toggleClass('activebg');
		}else{
			getEmailContent($this.attr("q_id"),'send');
		}
	});	
	$("#email-delbox-list li").die().live("click tap",function(){
	   var $this = $(this),
		isMulti = $('#email-inbox-list').hasClass('multi');	
		if(isMulti){
			multi[ $this.hasClass('activebg') ? 'remove' : 'add' ]($this.attr("q_id"));
			$this.toggleClass('activebg');
		}else{
			getEmailContent($this.attr("q_id"),'del');
		}
	});

	$('.icon-group .icon-item').click(function(){
		var $this = $(this),
		email_id = $('#email-inbox-list .activebg').attr('q_id');
		
		if($this.hasClass('newEmail')){
			editEmail.open();
			return false;
		}	
		
		if($this.hasClass('recycle')){
			deleteEmail(email_id);
			return false;
		}
		if($this.hasClass('newEmail')){
			editEmail.open();
			return false;
		}	
	});
	
	

	function getEmailContent(email_id, act)
	{
		act = act ? '_'+act : '';
		pageInit("main", 1);
		tiScroll_1_main.getMainData({
			url: 'email/read'+ act +'.php',
			data: {'EMAIL_ID': email_id},
			showCallback: function(){
				tiScroll_1_main.refresh();
				$('img', tiScroll_1_main.getElement()).bind('load',function(){
					tiScroll_1_main.refresh();				
				})
			}
		});
	}
	
//	$("#email-inbox-list li").eq(0).click();
	
});

var editEmail = {
	init: function(){
		this.p = new tPad.PopPanel( $('#popPanelArea') );
		this.p.setHeader(this.getTmpl());
		return this;
	},
	open: function(){
		this.p.open();	
		return this;
	},
	close: function(){
		this.p.close();
		return this;
	},
	getTmpl: function(){
		return 	[	
					'<span class="lcbtn" onclick="editEmail.close();"> <span>返回</span> </span>',
					'<span class="t">新邮件</span>',
					'<span class="combtn rbtn active" onclick="editEmail.post();"> <span>发送</span> </span>'
					].join('');		
	},
	post: function(){
		
		var TO_ID = CS_ID = '';
		var WEBMAIL = $("#TO_NAME2").val();
		var TO_NAME3 = $("#TO_NAME3").val();
		var SUBJECT = $("#SUBJECT").val();
		var CONTENT = $("#CONTENT").val();
		
		//收件人user_id
		$("#contactlist_1_result em").each(function(){
			TO_ID += $(this).attr("userid") + ",";   
		});
		
		//抄送user_id 
		$("#contactlist_3_result em").each(function(){
			CS_ID += $(this).attr("userid") + ",";   
		});
		
		//检验收件人
		if(TO_ID=="")
		{
			showMessage(nowriteReceiver);
			$("#TO_NAME1").focus();
			return;      
		}
		
		//检验外部收件人
		if(WEBMAIL!="" && !isEmail(WEBMAIL))
		{
			showMessage(errorWebAddress);
			$("#TO_NAME2").focus();
			return;
		}

		$.ajax({
			type: 'POST',
			url: 'email/submit.php',
			cache: false,
			data: {"P": P, "TO_ID": TO_ID, "CS_ID": CS_ID, "WEBMAIL": WEBMAIL, "SUBJECT": SUBJECT, "CONTENT": CONTENT},
			beforeSend: function()
			{
				editEmail.p.showLoading("<?=_('发送中...')?>");   
			},
			success: function(data)
			{
				if(data=="<?=_('邮件发送成功')?>" || data == "<?=_('外部邮件发送成功')?>")
				{
					editEmail.p.showLoading("<?=_('发送成功')?>");
					setTimeout(function(){
						editEmail.p.hideLoading();
						editEmail.resetEmail().close();
					},1000)
				}
			}
		});
		return this;
	},
	resetEmail: function(){
		$("#contactlist_1_result").empty();
		$("#contactlist_3_result").empty();
		$("#TO_NAME1").val("");
		$("#TO_NAME3").val("");
		$("#SUBJECT").val("");
		$("#CONTENT")[0].value = email_PadHD;

		return this; 
	}
};

editEmail.init().p.ajax('email/new.php', {}, $.noop,{ 
	complete: function(){
		editEmail.oiScroll = new iScroll( $('.wrapper', editEmail.p.el)[0], { useTransform: false } );

		tSearch1 = new $.tSearch({input:"#TO_NAME1",list:"#contactlist_1",appendDom:"#contactlist_1_result",posFix:function(o){	o.top = 50;	return o;} });
		tSearch1.init();

		tSearch2 = new $.tSearch({input:"#TO_NAME3",list:"#contactlist_3",appendDom:"#contactlist_3_result",posFix:function(o){	o.top = 100;	return o;} });
		tSearch2.init();

	}
});







</script>