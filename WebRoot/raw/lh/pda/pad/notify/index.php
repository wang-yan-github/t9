<?
	include_once("../inc_header.php");
   include_once("inc/utility_all.php");

   $CUR_DATE=date("Y-m-d",time());
   $query = "SELECT count(*) from NOTIFY where (TO_ID='ALL_DEPT' or find_in_set('$LOGIN_DEPT_ID',TO_ID)".dept_other_sql("TO_ID")." or find_in_set('$LOGIN_USER_PRIV',PRIV_ID)".priv_other_sql("PRIV_ID")." or find_in_set('$LOGIN_USER_ID',USER_ID)) and BEGIN_DATE<='$CUR_DATE' and (END_DATE>='$CUR_DATE' or END_DATE='0000-00-00') and PUBLISH='1'";
   $TOTAL_ITEMS = resultCount($query);
   
   //2012/6/25 10:49:12 lp 将所有通知的ID组成一个数组
   $NOTIFY_ID_ARRAY = "";
   $CUR_DATE=date("Y-m-d",time()); 
   $query = "SELECT NOTIFY_ID from NOTIFY where (TO_ID='ALL_DEPT' or find_in_set('$LOGIN_DEPT_ID',TO_ID)".dept_other_sql("TO_ID")." or find_in_set('$LOGIN_USER_PRIV',PRIV_ID)".priv_other_sql("PRIV_ID")." or find_in_set('$LOGIN_USER_ID',USER_ID)) and begin_date<='$CUR_DATE' and (end_date>='$CUR_DATE' or end_date is null) and PUBLISH='1' order by TOP desc,BEGIN_DATE desc,SEND_TIME desc";
   $cursor= exequery($connection,$query);
   while($ROW=mysql_fetch_array($cursor))
   {
     $NOTIFY_IDS.=$ROW["NOTIFY_ID"].",";
   }
   $NOTIFY_ID_ARRAY = "[".$NOTIFY_IDS."]";

?>

<script type="text/javascript">
var stype = "notify";
var nonewdata = "<?=_('没有新的公告通知')?>";
var newdata = "<?=_('%s个新公告通知')?>";
var now_notify_id = 0;
var notify_arr = <?=$NOTIFY_ID_ARRAY?>;
/* --- 自定义参数 ---*/
var nomoredata_1 = false;
var noshowPullUp_1 = <? echo ($PAGE_SIZE >= $TOTAL_ITEMS) ? "true" : "false"; ?>;


function pageInit(type, page_id)
{
	if(type == "side")
	{
	   if(page_id == 1)
	   {
	      tiScroll_1 = new $.tiScroll({"page_type": "side", "nomoredata": nomoredata_1, "noshowPullUp":noshowPullUp_1});
	      tiScroll_1.init();
	   }
	}else{
		if(page_id == 1)
	   { 
			fixZoomPageAttachSize(1, type);
	      tiScroll_1_main = new $.tiScroll({"page_type": "main", "listType": "readonly"});
	      tiScroll_1_main.init();
	   }
		if(page_id == "attach_read")
		{
			tiScroll_attach_read = new $.tiScroll({"page_id": "attach_read", "listType": "attach_show"});
			tiScroll_attach_read.init();      
		}  
	}
}
</script>
<div id="sideContentArea">
<?	
   $tHeadData = array(
      "1" => array(
         "c" => array("title" => "公告通知")
      )
   );
?>
<?=buildSiderHead($tHeadData);?>
<?=buildMessage();?>
<?=buildSideProLoading();?>
   <!-- all of notify -->
	<div id="sideContentPage_1" class="sideContentPage">
		<div id="sideContentWrapper_1" class="wrapper">
			<div id="contentScroller_1" class="scroller">
   <?

         echo buildPullDown();
         echo '<ul id="sidebar_notify" class="sideBarSubList preViewList">';
         if($TOTAL_ITEMS > 0)
         {
      		 //============================ 显示公告通知 =======================================
            $query = "SELECT NOTIFY_ID,FROM_ID,SUBJECT_COLOR,SUBJECT,TOP,TYPE_ID,READERS,BEGIN_DATE,ATTACHMENT_ID,ATTACHMENT_NAME,FORMAT,CONTENT from NOTIFY where (TO_ID='ALL_DEPT' or find_in_set('$LOGIN_DEPT_ID',TO_ID)".dept_other_sql("TO_ID")." or find_in_set('$LOGIN_USER_PRIV',PRIV_ID)".priv_other_sql("PRIV_ID")." or find_in_set('$LOGIN_USER_ID',USER_ID)) and begin_date<='$CUR_DATE' and (end_date>='$CUR_DATE' or end_date is null) and PUBLISH='1' order by TOP desc,BEGIN_DATE desc,SEND_TIME desc limit 0,$PAGE_SIZE";
            $cursor= exequery($connection,$query);
            while($ROW=mysql_fetch_array($cursor))
            {
              $NOTIFY_ID=$ROW["NOTIFY_ID"];
              $SUBJECT_COLOR=$ROW["SUBJECT_COLOR"];
              $FROM_ID=$ROW["FROM_ID"];
              $READERS = $ROW["READERS"];
              $SUBJECT=$ROW["SUBJECT"];
              $TOP=$ROW["TOP"];
              $TYPE_ID=$ROW["TYPE_ID"];
              $ATTACHMENT_ID=$ROW["ATTACHMENT_ID"];
              $ATTACHMENT_NAME=$ROW["ATTACHMENT_NAME"];
				  
				  $FORMAT=$ROW["FORMAT"];
              $COMPRESS_CONTENT=@gzuncompress($ROW["COMPRESS_CONTENT"]);	  
				  $CONTENT= $COMPRESS_CONTENT!=""&&$FORMAT!="2" ? $COMPRESS_CONTENT : $ROW["CONTENT"];
					
				  $SUBJECT=str_replace("<","&lt",$SUBJECT);
              $SUBJECT=str_replace(">","&gt",$SUBJECT);
              $SUBJECT=stripslashes($SUBJECT);
				  
              $SEND_TIME = $ROW["SEND_TIME"];
              $BEGIN_DATE=$ROW["BEGIN_DATE"];
              $BEGIN_DATE=strtok($BEGIN_DATE," ");
          
              $TYPE_NAME=get_code_name($TYPE_ID,"NOTIFY");
              if($TYPE_NAME!="")
                 $SUBJECT="[".$TYPE_NAME."]".$SUBJECT;
              $SUBJECT="<font color='".$SUBJECT_COLOR."'>".$SUBJECT."</font>";
          
              $query1 = "SELECT USER_NAME from USER where USER_ID='$FROM_ID'";
              $cursor1= exequery($connection,$query1);
              if($ROW=mysql_fetch_array($cursor1))
                 $FROM_NAME=$ROW["USER_NAME"];
              else
                 $FROM_NAME=$FROM_ID;
    
              $IMPORTANT_DESC= $TOP == '1' ? "<img src='/pda/style/images/top.png' />" : "";

                 
					if(!find_id($READERS,$LOGIN_USER_ID))
					{
						$Class = " unread";
						$unread = ' unread="1"';
					}else{
						$Class = "";
						$unread = "";
					}
   
						echo '<li class="'.$fix_for_pad['list-li-style'].$Class.'" q_id="'.$NOTIFY_ID.'" '.$unread.'>';
                  echo '<h3>'.$FROM_NAME.'<span class="time">'.$BEGIN_DATE.'</span></h3>';
                  echo '<p class="title">'.$SUBJECT.$IMPORTANT_DESC.'</p>';
						echo '<p class="content">'.$CONTENT.'</p>';
						
                  if($ATTACHMENT_ID != "" && $ATTACHMENT_NAME != "")
                  {
                     echo '<span class="iconbtn attach_icon"></span>';
                  }
                  
						echo '</li>';
            }//while
                  echo "</ul>";
         }else
         {
            echo "</ul>";
    ?>
            <div class="no_msg"><?=_("暂无新公告！")?></div>
    <? 
         } 
         echo buildPullUp(); 
    ?>  
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
				"c" => array("title" => "阅读公告"),
				"r" => array("class" => "","event" => "getnextNotify();", "title" => "上一篇"),
			),
			"attach_read" => array(
				"l" => array("class" => "","event" => "reback(\"attach_read\",2);", "title" => "返回"),
				"c" => array("title" => "查看附件")
			)
	   ); 
	?>
	<?=buildMainHead($tMainBarHeadData)?>

   <!-- page of read notify -->	
	<div id="mainContentPage_1" class="mainContentPage">
		<div id="mainContentWrapper_1" class="wrapper">
			<div id="contentScroller_1" class="scroller shadowscroller">
			</div>
		</div>
	</div>
<!--
   <div id="page_2" class="pages tcontent tzoom" style="display:none;">
      <div id="wrapper_2" class="wrapper">
         <div id="scroller_2" class="scroller"></div>      
      </div>
   </div> 
  --> 
   <!-- page of attach_file -->	
	<div id="mainContentPage_attach_read" class="mainContentPage" style="display:none;">
		<div id="mainContentWrapper_attach_read" class="wrapper">
			<div id="contentScroller_attach_read" class="scroller shadowscroller" style="position:relative;width:100%;height:100%;">
				<div id="layer" style="position: absolute;left: 0; top: 0;height: 100%; width:100%;"></div>
            <iframe id="file_iframe" name="file_iframe" class="attach_iframe" src=""></iframe> 
			</div>
		</div>
	</div>
</div>
<script>
tPad.changeLayout();
pageInit("side", 1);  


function getNotifyContent(obj)
{
   var notify_id = typeof(obj) == "object" ? obj.attr("q_id") : obj;
   now_notify_id = notify_id;

	pageInit('main', 1);
	
	tiScroll_1_main.getMainData({
		url: '/pda/pad/'+stype+'/read.php',
      data: {'P': '<?=$P?>','NOTIFY_ID': notify_id},
		onSuccess: function(data){
			if(typeof(obj) == "object"){
            obj.removeClass("unread");
         }else{
            $("#sidebar_notify li").each(function()
            {
					$("#sidebar_notify li").removeClass("activebg");
					$(this).addClass("activebg");
               if($(this).attr("q_id") == obj)
               {
                  $(this).removeClass("unread");
                  return false;
               }
            });            
         }
		},
		showCallback: function(){
			tiScroll_1_main.refresh();
		}
	});
	
}

function getnextNotify()
{
   if(now_notify_id!=0)
   {
      for(var i in notify_arr){
         if(notify_arr[i] == now_notify_id)
         {
            var next_notify_id = notify_arr[(++i)];
            if(typeof(next_notify_id)!="undefined" && next_notify_id!="")
            {
               getNotifyContent(next_notify_id);
					$("#sidebar_notify li").each(function(){
						if($(this).attr("q_id") == next_notify_id){
							$("#sidebar_notify li").removeClass("activebg");
							$(this).addClass("activebg");
							tiScroll_1.getOIScroll().scrollToElement(this,500);
							return false;
						}
					});            
               return;
            }else{
               showMessage("<?=('已经是最后一篇了')?>");
               return;      
            }
         }    
      }         
   }      
}

$("#sidebar_notify li").die().live("click tap",function(){
   getNotifyContent($(this).attr('q_id'));
});

</script>
