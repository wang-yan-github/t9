<?
   include_once("../inc_header.php");
   include_once("inc/utility_all.php");

   $query = "SELECT count(*) from NEWS where PUBLISH='1' and (TO_ID='ALL_DEPT' or find_in_set('$LOGIN_DEPT_ID',TO_ID) or find_in_set('$LOGIN_USER_PRIV',PRIV_ID) or find_in_set('$LOGIN_USER_ID',USER_ID))";
   $TOTAL_ITEMS = resultCount($query);
?>
<body>
<script type="text/javascript">
var stype = "news";
var nonewdata = "<?=_('没有新的新闻')?>";
var newdata = "<?=_('%s个新的新闻')?>";
var noemptycomment = "<?=_('评论内容不能为空')?>";
var savecomment = "<?=_('评论保存成功')?>";
var g_news_id = 0;

/* --- 自定义参数 ---*/
var nomoredata_1 = false;
var noshowPullUp_1 = <? echo ($PAGE_SIZE >= $TOTAL_ITEMS) ? "true" : "false"; ?>;

function pageInit(type, page_id){
	if(type == 'side'){
		if(page_id == 1)
		{
			tiScroll_1 = new $.tiScroll({"page_type":"side","nomoredata": nomoredata_1, "noshowPullUp":noshowPullUp_1});
			tiScroll_1.init();
		}
   } else {
		if(page_id == 1)
		{
			tiScroll_1_main = new $.tiScroll({"page_id": 1, "listType": "readonly"});
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
         "c" => array("title" => "最新新闻")
      )
   );
?>
<?=buildSiderHead($tHeadData);?>
<?=buildMessage();?>
<?=buildSideProLoading();?>

	<div id="sideContentPage_1" class="sideContentPage">
		<div id="sideContentWrapper_1" class="wrapper">
			<div id="contentScroller_1" class="scroller">
               
   <?
         echo buildPullDown();
         echo '<ul id="news-list" class="comm-list sideBarSubList preViewList">';
         if($TOTAL_ITEMS > 0)
         {
      		//============================ 显示新闻 =======================================
      		$CUR_DATE=date("Y-m-d",time());
      		$query = "SELECT READERS,CLICK_COUNT,SUBJECT_COLOR,NEWS_ID,PROVIDER,SUBJECT,NEWS_TIME,LAST_EDIT_TIME,FORMAT,TYPE_ID,ATTACHMENT_ID,ATTACHMENT_NAME from NEWS where PUBLISH='1' and (TO_ID='ALL_DEPT' or find_in_set('$LOGIN_DEPT_ID',TO_ID) or find_in_set('$LOGIN_USER_PRIV',PRIV_ID) or find_in_set('$LOGIN_USER_ID',USER_ID)) order by NEWS_ID desc limit 0,$PAGE_SIZE";
      		$cursor= exequery($connection,$query);
      		while($ROW=mysql_fetch_array($cursor))
      		{
      		   $NEWS_ID=$ROW["NEWS_ID"];
      		   $PROVIDER=$ROW["PROVIDER"];
      		   $SUBJECT=$ROW["SUBJECT"];
      		   $NEWS_TIME=$ROW["NEWS_TIME"];
      		   $LAST_EDIT_TIME=$ROW["LAST_EDIT_TIME"];
      		   $FORMAT=$ROW["FORMAT"];
      		   $TYPE_ID=$ROW["TYPE_ID"];
      		   $ATTACHMENT_ID=$ROW["ATTACHMENT_ID"];
      		   $ATTACHMENT_NAME=$ROW["ATTACHMENT_NAME"];
      		   $READERS=$ROW["READERS"];
      		   $SUBJECT_COLOR=$ROW["SUBJECT_COLOR"];
               $CLICK_COUNT=$ROW["CLICK_COUNT"];
        
      		   $SUBJECT=htmlspecialchars($SUBJECT);
      		   
      		   $TYPE_NAME=get_code_name($TYPE_ID,"NEWS");
      		   if($TYPE_NAME!="")
      		      $SUBJECT="[".$TYPE_NAME."]".$SUBJECT;
               $SUBJECT="<font color='".$SUBJECT_COLOR."'>".$SUBJECT."</font>";
               
      		   $query1 = "SELECT USER_NAME from USER where USER_ID='$PROVIDER'";
      		   $cursor1= exequery($connection,$query1);
      		   if($ROW=mysql_fetch_array($cursor1))
      		      $FROM_NAME=$ROW["USER_NAME"];
      		   else
      		      $FROM_NAME=$FROM_ID;
      		      
               if(!find_id($READERS,$LOGIN_USER_ID))
               {
                  $Class = " active";$unread = ' unread="1"';
               }else{
                  $Class = "";$unread = "";
               }
               
               if($LAST_EDIT_TIME == "0000-00-00 00:00:00")
               {
                  if(strtotime($NEWS_TIME) > time()){
                     $TIME = substr($NEWS_TIME,0,10);      
                  }else{
                     $TIME = timeintval(strtotime($NEWS_TIME));          
                  }         
               }else{
                  $TIME = timeintval(strtotime($LAST_EDIT_TIME));      
               }
   ?>
   
         		<li class="<?=$fix_for_pad['list-li-style']?><?=$Class?>" q_id="<?=$NEWS_ID?>"<?=$unread?>>
                     <h3><?=$SUBJECT?></h3>
                     <p class="content"><?=$FROM_NAME?> - <?=$TIME?></p>
                     <?
                        if($ATTACHMENT_ID != "" && $ATTACHMENT_NAME != "")
                        {
                     ?>
                        <span class="iconbtn attach_icon"></span>
                     <?
                        }
                     ?>
                     <span class="ui-icon-rarrow"></span>
               </li>
         		<?
            }//while
            echo "</ul>";
		   }else{
		      echo '</ul>';
		      echo '<div class="no_msg">'._("无最新新闻").'</div>';
		   }
            echo buildPullUp(); 
   ?>
         </div>      
      </div>
   </div>
</div> 
<div id="mainContentArea">	
<?
   $tHeadData = array(
      "1" => array(
        // "l" => array("class" => "","event" => "reback(2,1);", "title" => "返回"),
         "c" => array("title" => "阅读新闻"),
        // "r" => array("class" => "readComment","event" => "readComment();", "title" => "评论")
      ),
      "2" => array(
         "l" => array("class" => "","event" => "reback(3,2);", "title" => "返回"),
         "c" => array("title" => "阅读评论"),
         "r" => array("class" => "","event" => "saveComment();", "title" => "保存")
      ),
      "attach_read" => array(
      //   "l" => array("class" => "","event" => "reback(\"attach_read\",2);", "title" => "返回"),
         "c" => array("title" => "查看附件")
      )
   );
	
	echo buildMainHead($tHeadData);
?>
   <!-- page of read news -->
	<div id="mainContentPage_1" class="mainContentPage tzoom">
		<div id="mainContentWrapper_1" class="wrapper">
			<div id="contentScroller_1" class="scroller shadowscroller">
			</div>
		</div>
	</div>
 
   <!-- page of attach_file -->
   <div id="mainContentPage_attach_read" class="mainContentPage" style="display:none;">
      <div id="mainContentWrapper_attach_read" class="wrapper">
         <div id="contentScroller_attach_read" class="scroller shadowscroller" style="position:relative;width:100%;height:100%;">
            <div id="layer" style="position: absolute;left: 0; top: 0;height: 100%; width:100%;"></div>
            <iframe id="file_iframe" name="file_iframe" class="attach_iframe" src="" ></iframe> 
         </div>      
      </div>
   </div>
</div>
<script>
$(document).ready(function(){
	tPad.changeLayout('side');
   pageInit('side',1);   
});
$(".preViewList li").die().live("click tap",function(){
		$(".preViewList li").removeClass("activebg");
		$(this).addClass("activebg");
});
function getNewsContent(jqobj,news_id)
{
	tPad.changeLayout('both');
	pageInit('main',1);
	tiScroll_1_main.getMainData({
      url: 'news/read.php',
      data: {'NEWS_ID': news_id,},
		showCallback: function(){
			$(this.getOIScroll().scroller).append('<div id="newsComment"></div>');
			readComment();
			tiScroll_1_main.refresh();
		}
	});
	return;
   $.ajax({
      type: 'GET',
      url: 'read.php',
      cache: true,
      data: {'NEWS_ID': news_id,},
      beforeSend: function(){
         $.ProLoading.show();   
      },
      success: function(data){
         jqobj.removeClass("active");
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
function readComment()
{
   $.ajax({
      type: 'GET',
      url: 'news/readcomment.php',
      cache: true,
      data: {'NEWS_ID': g_news_id},
      beforeSend: function(){
         //$.ProLoading.show();   
      },
      success: function(data){
        // $.ProLoading.hide();
         if(data == "NOCOMMENT")
         {
            showMessage("<?=_('暂无评论')?>");
            return;
         }
      /*    $("#page_3 > #wrapper_3 > #scroller_3").empty().append(data);
         $("#page_3").show('fast',function(){pageInit(3);});
         $("#header_2").hide();
         $("#header_3").show(); */
			$("#newsComment").html(data);
         $(".container .read_detail:last", tiScroll_1_main.getElement()).addClass("endline");
			pageInit('main',1);
      },
      error: function(data){
         $.ProLoading.hide();  
         showMessage("<?=_('获取失败')?>");
      }
   });
}

function saveComment()
{
   CONTENT = $("#CONTENT").val();
   if(CONTENT == "")
   {  
      showMessage(noemptycomment);
      $("#CONTENT").focus();
      return;   
   }
   
   //检查回复
   var $$pDom = $(".replyToResult em");
   if($$pDom.size() == 1)
   {
      var p_id = $$pDom.attr("p_id");  
   }else{
      var p_id = 0;   
   }
      
   $.ajax({
      type: 'POST',
      url: 'save.php',
      cache: false,
      data: {'NEWS_ID': g_news_id, 'CONTENT': CONTENT, 'PARENT_ID': p_id},
      beforeSend: function()
      {
         $.ProLoading.show();   
      },
      success: function(data)
      {
         $.ProLoading.hide();
         if(data == "SUCCESS")
         {
            showMessage(savecomment);
            readComment();
            return;
         }
      },
      error: function(data){
         $.ProLoading.hide();  
         showMessage("<?=_('获取失败')?>");
      }
   });
}

$("#page_3 .container .data_line").live("click",function()
{
   $(".data_line .read_detail_sheader").removeClass("activebg");
   $(this).find(".read_detail_sheader").addClass("activebg");
   var p_id = $(this).attr("p_id");
   var o_num = $(this).attr("o_num");
   if(p_id!="")
   {
      var str = '<em p_id = "' + p_id +'" o_num = "' + o_num +'" class="active"><?=_("回复：")?>'+ o_num + '#<span></span></em></span>'; 
      $(".replyToResult").empty().append(str);
   }
});

$("#page_3 .container .replyToResult em span").live("click",function(e)
{
   e.stopPropagation();
   $(".data_line .read_detail_sheader").removeClass("activebg");
   $(this).parents(".replyToResult").empty();
   return;
});

$("#news-list li").die().live("click tap",function(){
   var $this = $(this);
   getNewsContent($this, g_news_id = $this.attr("q_id"));
});

</script>
</body>
</html>
