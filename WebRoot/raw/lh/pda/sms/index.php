<?
   include_once("../header.php");
   include_once("inc/utility_all.php"); 
   
   $CUR_TIME = time();
   //2012/3/12 16:08:12 lp 查询总条数
   $query = "SELECT count(*) from SMS,SMS_BODY,USER where SMS.BODY_ID=SMS_BODY.BODY_ID and USER.USER_ID=SMS_BODY.FROM_ID and TO_ID='$LOGIN_USER_ID' and DELETE_FLAG!='1' and SEND_TIME<='$CUR_TIME'";
   $TOTAL_ITEMS = resultCount($query);
?>
<body>
<script type="text/javascript">
var stype = "sms";
var p = "<?=$P?>";
var nonewdata = "<?=_('没有新的事务提醒')?>";
var newdata = "<?=_('%s个新提醒')?>";

/* --- 自定义参数 ---*/
var nomoredata_1 = false;
var noshowPullUp_1 = <? echo ($PAGE_SIZE >= $TOTAL_ITEMS) ? "true" : "false"; ?>;

var nomoredata_2 = false;
var noshowPullUp_2 = <? echo ($PAGE_SIZE >= $TOTAL_ITEMS2) ? "true" : "false"; ?>;

function pageInit(page_id){
   if(page_id == 1)
   {
      tiScroll_1 = new $.tiScroll({"nomoredata": nomoredata_1, "noshowPullUp":noshowPullUp_1});
      tiScroll_1.init();
   }
   
   if(page_id == 2)
   {
      tiScroll_2 = new $.tiScroll({"page_id": 2, "listType": "readonly"});
      tiScroll_2.init();
   }      
}
</script>
<?
   //导航数据
   //$tHeadData_1_c_title = '<a class="comtab comtab_l comtab_active" href="javascript:;" page="page_1"><span>'._("未确认").'</span></a><a class="comtab comtab_r" href="javascript:;" page="page_2"><span>'._("全部").'</span></a>';
  
   $tHeadData = array(
      "1" => array(
         "l" => array("class" => "","event" => "gohome();", "title" => "首页"),
         "c" => array("title" => "任务提醒")
      ),
      "2" => array(
         "l" => array("class" => "","event" => "reback(2,1);", "title" => "返回"),
         "c" => array("title" => "阅读提醒")
      )
   );
?>

<?=buildHead($tHeadData);?>
<?=buildMessage();?>
<?=buildProLoading();?>

   <!-- all of sms -->
   <div id="page_1" class="pages tlist">
      <div id="wrapper_1" class="wrapper">
         <div id="scroller_1" class="scroller">
            <?=buildPullDown();?>
            <ul class="comm-list comm-pic-list">
            <?
               if($TOTAL_ITEMS > 0)
               {
                  $query = "SELECT SMS_ID,FROM_ID,SEND_TIME,SMS_TYPE,CONTENT,USER_NAME,AVATAR,UID,REMIND_FLAG from SMS,SMS_BODY,USER where SMS.BODY_ID=SMS_BODY.BODY_ID and USER.USER_ID=SMS_BODY.FROM_ID and TO_ID='$LOGIN_USER_ID' and DELETE_FLAG!='1' and SEND_TIME<='$CUR_TIME' order by SEND_TIME desc limit 0 ,$PAGE_SIZE";
                  $cursor= exequery($connection,$query);
               
                  while($ROW=mysql_fetch_array($cursor))
                  {
                     $SMS_ID=$ROW["SMS_ID"];
                     $FROM_ID=$ROW["FROM_ID"];
                     $SEND_TIME=$ROW["SEND_TIME"];
                     $REMIND_FLAG=$ROW["REMIND_FLAG"];
                     $SMS_TYPE=$ROW["SMS_TYPE"];
                     $CONTENT=$ROW["CONTENT"];
                     $FROM_NAME=$ROW["USER_NAME"];
                     $AVATAR=$ROW["AVATAR"];
                     $SEX=$ROW["SEX"];
                  
                     $CONTENT=str_replace("<","&lt",$CONTENT);
                     $CONTENT=str_replace(">","&gt",$CONTENT);
                     $CONTENT=stripslashes($CONTENT);
                     
                     if($REMIND_FLAG!=0)
                     {
                        $Class = " active";$unread = ' unread="1"';
                     }else{
                        $Class = "";$unread = "";
                     }
            ?>
                     <li class="<?=$fix_for_pad['list-li-style']?><?=$Class?>" q_id="<?=$SMS_ID?>"<?=$unread?>>
                        <img src="<?=showAvatar($AVATAR,$SEX)?>" class="ui-li-thumb"/>
                        <h3><span class="time"><?=timeintval($SEND_TIME)?></span><?=$FROM_NAME?></h3>
                        <p class="grapc"><?=strip_tags($CONTENT)?>&nbsp;</p>
                        <span class="ui-icon-rarrow"></span>
                     </li>
            <?
                  }//while
                  echo "</ul>";
               }else{
                  echo '</ul>';
                  echo '<div class="no_msg">'._("暂无提醒！").'</div>';
               }
            ?>
            <?=buildPullUp();?>  
         </div>      
      </div>
   </div>
   
   <!-- page of read sms -->
   <div id="page_2" class="pages tcontent" style="display:none;">
      <div id="wrapper_2" class="wrapper">
         <div id="scroller_2" class="scroller"></div>      
      </div>
   </div>
   
<script type="text/javascript" src="/pda/js/udf-1.1.js"></script>
<script type="text/javascript">
$(document).ready(function(){
   pageInit(1);   
});

function getSmsContent(sms_id,unread){
   $.ajax({
         type: 'GET',
         url: 'read.php',
         cache: true,
         data: {'P': '<?=$P?>','SMS_ID': sms_id, 'UNREAD': unread},
         beforeSend: function(){
            $.ProLoading.show();   
         },
      success: function(data){
            $.ProLoading.hide();
         $("#page_2 > #wrapper_2 > #scroller_2").empty().append(data);
         $("#page_2").show('fast',function(){pageInit(2);});
         $("#header_1").hide();
         $("#header_2").show();
         },
      error: function(data){
         $.ProLoading.hide();  
         showMessage("<?=_('获取失败')?>");
      }
   });
}

$("ul.comm-list li").live("click tap",function(){
   $$a = $(this);
   getSmsContent($$a.attr("q_id"),$$a.attr("unread"));
   $$a.removeClass("active");
});   
</script>
</body>
</html>
