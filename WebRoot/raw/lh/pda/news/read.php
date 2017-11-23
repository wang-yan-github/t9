<?
   include_once("../header.php");
   include_once("inc/utility_file.php");
   ob_clean();

	$query = "SELECT * from NEWS where NEWS_ID='$NEWS_ID' and PUBLISH='1' and (TO_ID='ALL_DEPT' or find_in_set('$LOGIN_DEPT_ID',TO_ID) or find_in_set('$LOGIN_USER_PRIV',PRIV_ID) or find_in_set('$LOGIN_USER_ID',USER_ID))";
	$cursor= exequery($connection,$query);
	if($ROW=mysql_fetch_array($cursor))
	{
      $NEWS_ID=$ROW["NEWS_ID"];
      $SUBJECT=$ROW["SUBJECT"];
      $CLICK_COUNT=$ROW["CLICK_COUNT"];
      $SUBJECT_COLOR=$ROW["SUBJECT_COLOR"];
      $ANONYMITY_YN = $ROW["ANONYMITY_YN"];
      $PROVIDER=$ROW["PROVIDER"];
      $NEWS_TIME=$ROW["NEWS_TIME"];
      $FORMAT=$ROW["FORMAT"];
      $READERS=$ROW["READERS"];
      $TYPE_ID = $ROW["TYPE_ID"];
      $CLICK_COUNT++;
      
      $SUBJECT=htmlspecialchars($SUBJECT);
      $TYPE_NAME=get_code_name($TYPE_ID,"NEWS");

      $SUBJECT="<font color='".$SUBJECT_COLOR."'>".$SUBJECT."</font>";
      
      $FORMAT=$ROW["FORMAT"];
      $COMPRESS_CONTENT=@gzuncompress($ROW["COMPRESS_CONTENT"]);
      
      if($COMPRESS_CONTENT!=""&&$FORMAT!="2")
         $CONTENT=$COMPRESS_CONTENT;
      else
         $CONTENT=$ROW["CONTENT"];
         
      $ATTACHMENT_ID=$ROW["ATTACHMENT_ID"];
      $ATTACHMENT_NAME=$ROW["ATTACHMENT_NAME"];
	   
	   //lp 2012/4/18 13:16:31 增加查阅情况以及点击次数
      if(!find_id($READERS,$LOGIN_USER_ID))
      {
         $READERS.=$LOGIN_USER_ID.",";
         $query = "update news set READERS='$READERS',CLICK_COUNT='$CLICK_COUNT' where NEWS_ID='$NEWS_ID'";
      }
      else
      {
         $query = "update NEWS set CLICK_COUNT='$CLICK_COUNT' where NEWS_ID='$NEWS_ID'";
      }
      exequery($connection,$query);
	
	  $query1 = "SELECT USER_NAME from USER where USER_ID='$PROVIDER'";
	  $cursor1= exequery($connection,$query1);
	  if($ROW=mysql_fetch_array($cursor1))
	     $FROM_NAME=$ROW["USER_NAME"];
	  else
	     $FROM_NAME=$FROM_ID;
	  
	  if($FORMAT=="2")
	    Header("location: $CONTENT");
	}
	else
	   exit;
?>


<div class="container">
   <h3 class="read_title fix_read_title"><?=$SUBJECT?></h3>
   <? if($TYPE_NAME!=""){ ?>
   <div class="read_detail fix_read_detail" style="text-align:left;"><span class="grapc"><?=_("类型：")?></span><?=$TYPE_NAME?></div>
   <? } ?>
   <div class="read_detail fix_read_detail" style="text-align:left;"><span class="grapc"><?=_("创建人：")?></span><?=$FROM_NAME?></div>
   <div class="read_detail fix_read_detail" style="text-align:left;"><span class="grapc"><?=_("发布时间：")?></span><?=$NEWS_TIME?></div>
   <div class="read_detail fix_read_detail" style="text-align:left;"><span class="grapc"><?=_("点击次数：")?></span><?=$CLICK_COUNT?></div>
   <div class="read_content"><?=$CONTENT?></div>
   <?
   if($ATTACHMENT_ID != "" && $ATTACHMENT_NAME != "")
   {
   ?>
      <div class="read_attach"><?=attach_link_pda($ATTACHMENT_ID,$ATTACHMENT_NAME,$P,'',1,1,1)?></div>
   <?
   }
   ?>
</div>
<script type="text/javascript">
<? if($ANONYMITY_YN!="2"){ ?>
   $(".readComment").show();
<? }else{ ?>
   $(".readComment").hide();
<? } ?>

$(document).ready(function(){
   oImg = $(".read_content img");
   oImg.each(function(){
      $(this).wrap("<div class='img_wrap'></div>");
      preLoadImage($(this).parent(".img_wrap") ,$(this).attr("src"));
   });
   
   function preLoadImage(obj, url)
   {
      //创建一个Image对象，实现图片的预下载
      var img = new Image();
      img.src = url;
      obj.html("<?=_('图片加载中...')?>");
      
      // 如果图片已经存在于浏览器缓存，直接调用回调函数
      if(img.complete)
      { 
         obj.empty().append(img);
         return; //直接返回，不用再处理onload事件
      }
      img.onload = function () 
      {
         obj.empty().append(img);
         oiScroll_2.refresh();
      };
      img.onerror = function()
      {
         obj.html('<?=_("图片加载失败！")?>');
      };
   }
});
</script>