<? 
	include_once("../inc_header.php");
	include_once("inc/utility_all.php");
	
	$CUR_TIME = time();
   //2012/3/12 16:08:12 lp 查询总条数
   $query = "SELECT count(*) from SMS,SMS_BODY,USER where SMS.BODY_ID=SMS_BODY.BODY_ID and USER.USER_ID=SMS_BODY.FROM_ID and TO_ID='$LOGIN_USER_ID' and DELETE_FLAG!='1' and SEND_TIME<='$CUR_TIME'";
   $TOTAL_ITEMS = resultCount($query);
?>
<script>
var stype = "sms";
var nonewdata = "<?=_('没有新的事务提醒')?>";
var newdata = "<?=_('%s个新提醒')?>";

/* --- 自定义参数 ---*/
var nomoredata_1 = false;
var noshowPullUp_1 = true;
function pageInit(type, page_id)
{
	if(type == "side")
	{
	   if(page_id == 1)
	   {
	      tiScroll_1 = new $.tiScroll({"page_type": "side", "listType": "readonly"});
	      tiScroll_1.init();
	   }
	}else{
		if(page_id == 1)
	   {
	      tiScroll_3 = new $.tiScroll({"page_type": "main", "listType": "readonly"});
	      tiScroll_3.init();
	   }	
	}
}
</script>
<div id="sideContentArea">
	<?
		//导航数据
	   $tSideBarHeadData = array(
	      "1" => array(
	         "c" => array("title" => "提醒类型")
	      )
	   ); 
	?>
	
	<?=buildSiderHead($tSideBarHeadData)?>
	<?=buildMessage();?>

	<!-- type of email -->
	<div id="sideContentPage_1" class="sideContentPage">
		<div id="sideContentWrapper_1" class="wrapper">
			<div id="contentScroller_1" class="scroller">
				<ul id="sidebar_sms" class="sideBarSubList sideBarCateList">
					<?
						$SMS_TYPE = $SMS_REMIND_ARR = array();
						$query = "SELECT SMS_TYPE,REMIND_FLAG from SMS,SMS_BODY,USER where SMS.BODY_ID=SMS_BODY.BODY_ID and USER.USER_ID=SMS_BODY.FROM_ID and (TO_ID='$LOGIN_USER_ID' and DELETE_FLAG!='1') and SEND_TIME<='$CUR_TIME' ORDER BY SEND_TIME desc";
	               $cursor= exequery($connection,$query);
	               while($ROW=mysql_fetch_array($cursor))
	               {
	               	//REMIND_FLAG!='0'
	               	if(!in_array($ROW["SMS_TYPE"], $SMS_TYPE)){
	               		$SMS_TYPE[] = $ROW["SMS_TYPE"];
	               		$SMS_REMIND_ARR[$ROW["SMS_TYPE"]]["unread"] = 0;
	               	}
	               	if($ROW["REMIND_FLAG"]!='0')
               			$SMS_REMIND_ARR[$ROW["SMS_TYPE"]]["unread"]++;	
	              	}
	              	if(count($SMS_REMIND_ARR) > 0)
	              	{
	              		foreach($SMS_REMIND_ARR as $k => $v)
	              		{
	              			$style = $num = "";
	              			if($v["unread"] > 0)
	              			{
	              				$style = "unread";
	              				$num = "<span class=\"ui-icon-num\">".$v["unread"]."</span>";	
	              			}
	              			echo '<li q_id="'.$k.'" class="'.$style.'"><a href="javascript:void(0);">'.get_code_name($k,"SMS_REMIND").'</a>'.$num.'<span class="ui-icon-rarrow"></span></li>';
               ?>
               <? 	}
               	} 
               ?>
				</ul>	
			</div>
		</div>
	</div>
</div>
		
<div id="mainContentArea">
	<?=buildMainProLoading()?>
	<?
		//导航数据
	   $tMainBarHeadData = array(
	   	"1" => array(
	         "c" => array("title" => "提醒详情")
	      )   
	   ); 
	?>
	<?=buildMainHead($tMainBarHeadData)?>
	<div id="mainContentPage_1" class="mainContentPage">
		<div id="mainContentWrapper_1" class="wrapper">
			<div id="contentScroller_1" class="scroller shadowscroller">
			</div>
		</div>
	</div>
</div>

<script type="text/javascript">
tPad.changeLayout();
pageInit("side", 1);	
pageInit("main", 1);


$("#sidebar_sms li").die().live("click tap",function(){
	if($(this).hasClass("unread"))
		$(this).removeClass("unread");
	
	$("#sidebar_sms li").removeClass("activebg");
		$(this).addClass("activebg");
	
	if($(this).find(".ui-icon-num").length > 0)
		$(this).find(".ui-icon-num").remove();
	
	getSmsList($(this).attr("q_id"));
});

function getSmsList(sms_type_id)
{
	tiScroll_3.getMainData({
			url: 'sms/list.php',
			data: {'SMS_TYPE_ID': sms_type_id},
			showCallback: function(){
				//tiScroll_3.refresh();
				pageInit("main", 1);
			}
	});
}

$("#sidebar_sms li").eq(0).click();

</script>