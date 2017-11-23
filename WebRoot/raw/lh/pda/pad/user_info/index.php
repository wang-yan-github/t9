<?
   include_once("../inc_header.php");
   include_once("inc/td_core.php");
   $query = "SELECT count(*) from USER,USER_PRIV,DEPARTMENT where DEPARTMENT.DEPT_ID!=0 and USER.USER_PRIV=USER_PRIV.USER_PRIV and USER.DEPT_ID=DEPARTMENT.DEPT_ID order by PRIV_NO,USER_NO,USER_NAME ";
   $TOTAL_ITEMS = resultCount($query);
?>
<body>
<script type="text/javascript">
var stype = "user_info";
var nonewdata = "<?=_('没有新的人员信息')?>";
var newdata = "<?=_('%s个新的人员信息')?>";

/* --- 自定义参数 ---*/
var nomoredata_2 = false;
var noshowPullUp_2 = <? echo ($PAGE_SIZE >= $TOTAL_ITEMS) ? "true" : "false"; ?>;

function pageInit(type, page_id)
{
	if(type=='side')
	{
		if(page_id == 1)
		{
			tiScroll_1 = new $.tiScroll({"page_type": "side","listType": "readonly"});
			tiScroll_1.init();
		}
		if(page_id == 2)
		{
			tiScroll_2 = new $.tiScroll({"page_id": 2,"page_type": "side", "nomoredata": nomoredata_2, "noshowPullUp":noshowPullUp_2});
			tiScroll_2.init();
		}
	}
	else
	{
		if(page_id == 1)
		{
			tiScroll_1_main = new $.tiScroll({"page_type": 'main', "listType": "readonly"});
			tiScroll_1_main.init();
		}
	}
}
var from_page = 1;
</script>
<div id="sideContentArea">
<?
   $tHeadData = array(
      "1" => array(
         "c" => array("title" => "人员查询"),
         "r" => array("class" => "","event" => "userSideGo(1,2);", "title" => "所有")
      ),
      "2" => array(
         "l" => array("class" => "","event" => "userSideGo(2,1);", "title" => "返回"),
         "c" => array("title" => "人员列表"),
      )
   );
?>
<?=buildSiderHead($tHeadData);?>
<?=buildMessage();?>
<?=buildSideProLoading();?>

   <!-- page of search input -->
	<div id="sideContentPage_1" class="sideContentPage">
		<div id="sideContentWrapper_1" class="wrapper">
			<div id="contentScroller_1" class="scroller">
            <div id="search_box">
               <div id="input_box">
                  <input type="text" id="USER_NAME" name="USER_NAME" value="" autocapitalize="off" autocorrect="off"/>   
               </div>
            </div>
            <ul class="comm-list sideBarSubList preViewList" id="contactlist_1"></ul>       
         </div>      
      </div>
   </div>

   <!-- all of search result -->
	<div id="sideContentPage_2" class="sideContentPage" style="display:none;">
		<div id="sideContentWrapper_2" class="wrapper">
			<div id="contentScroller_2" class="scroller">
      <?
         echo buildPullDown();
         echo '<ul class="main-comm-list comm-pic-list" id="all_user">';
         if($TOTAL_ITEMS > 0)
         {
      		$query = "SELECT UID,SEX,AVATAR,USER_ID,USER_NAME,USER.DEPT_ID,PRIV_NAME from USER,USER_PRIV,DEPARTMENT where DEPARTMENT.DEPT_ID!=0 and USER.USER_PRIV=USER_PRIV.USER_PRIV and USER.DEPT_ID=DEPARTMENT.DEPT_ID order by PRIV_NO,USER_NO,USER_NAME limit 0,$PAGE_SIZE";
      		$cursor= exequery($connection,$query);
      		while($ROW=mysql_fetch_array($cursor))
      		{
      		   $UID=$ROW["UID"];
      		   $USER_NAME=$ROW["USER_NAME"];
      		   $PRIV_NAME=$ROW["PRIV_NAME"];
      		   $DEPT_ID=$ROW["DEPT_ID"];
      		   $SEX=$ROW["SEX"];
               $AVATAR=$ROW["AVATAR"];
      
      		   $DEPT_LONG_NAME=dept_long_name($DEPT_ID);
      		
      		   if($SEX==0)
      		      $SEX=_("男");
      		   else
      		      $SEX=_("女");
   		?>
            <li class="<?=$fix_for_pad['list-li-style']?>" q_id="<?=$UID?>">
               <img src="<?=showAvatar($AVATAR,$SEX)?>" class="ui-li-thumb"/>
               <h3><?=$USER_NAME?><?=_("(")?><?=$SEX?><?=_(")")?></h3>
               <p class="w100 grapc"><?=_("部门：")?><?=$DEPT_LONG_NAME?> <?=_("角色：")?><?=$PRIV_NAME?></p>
               <span class="ui-icon-rarrow"></span>
            </li>
   		<?
   		   }//while
   		   echo "</ul>";
         }else{
   		   echo "</ul>";
   		   echo '<div class="no_msg">'._("暂无人员！").'</div>';
   		}
      ?>
         <?=buildPullUp();?> 
         </div>      
      </div>
   </div>
</div>
<div id="mainContentArea">	
<?
   $tHeadData = array(
      "1" => array(
         "c" => array("title" => "人员详情")
      )
   );
	
	echo buildMainHead($tHeadData);
?>
   <!-- page of result detail -->
	<div id="mainContentPage_1" class="mainContentPage">
		<div id="mainContentWrapper_1" class="wrapper">
			<div id="contentScroller_1" class="scroller shadowscroller">
			</div>
		</div>
	</div>
</div>

<script>
$(document).ready(function(){
	tPad.changeLayout('side');
   pageInit('side',1);   
});

function getUserDetailContent(UID,FROM){
 /*   $.ajax({
      type: 'GET',
      url: 'detail.php',
      cache: true,
      data: {'P': '<?=$P?>','UID': UID},
      beforeSend: function()
      {
         $.ProLoading.show("<?=_('获取中...')?>");   
      },
      success: function(data)
      {
         $.ProLoading.hide(); 
         if(FROM == 'all_user'){
            $("#header_2").hide();
            from_page = 2;
         }else{
            $("#header_1").hide();
            $("#page_1").hide();
            from_page = 1;
         }
         $("#header_3").show();
         $("#page_3 > #wrapper_3 > #scroller_3").empty().append(data);
         $("#page_3").show('fast',function(){pageInit(3);});
      }
   }); */
	
	pageInit('main',1);
	tiScroll_1_main.getMainData({
      url: 'user_info/detail.php',
      data: {'P': '<?=$P?>','UID': UID},
		showCallback: function(){
			tiScroll_1_main.refresh();
		}
	});
}
$("#all_user>li,#contactlist_1>li").die().live("click tap",function(){
   var $this = $(this);
   getUserDetailContent($this.attr("q_id"),$this.parent("ul").attr("id"));
   $this.removeClass("active");
	tPad.changeLayout('both');
});

function userSideGo(from,to){
   tPad.changeLayout('side');
	sidereback(from,to);
}

function goback(){
   reback(3,from_page);
   if(from_page == 2){
      Gotolist();
   }
}

tSearch2 = new $.tSearch2({page_id:1, input:"#USER_NAME", list:"#contactlist_1"});
tSearch2.init();
</script>
</body>
</html>
