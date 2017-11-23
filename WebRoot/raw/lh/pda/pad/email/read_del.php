<?
   include_once("../inc_header.php");
   include_once("inc/utility_file.php");
   ob_clean();

   $query = "SELECT * from EMAIL,EMAIL_BODY where EMAIL_BODY.BODY_ID=EMAIL.BODY_ID and EMAIL_ID='$EMAIL_ID' and TO_ID='$LOGIN_USER_ID' and (DELETE_FLAG='3' or DELETE_FLAG='4')";
   $cursor= exequery($connection,$query);
   if($ROW=mysql_fetch_array($cursor))
   {
      $FROM_ID=$ROW["FROM_ID"];
      $SUBJECT=$ROW["SUBJECT"];
      $CONTENT=$ROW["COMPRESS_CONTENT"] == "" ? $ROW["CONTENT"] : @gzuncompress($ROW["COMPRESS_CONTENT"]);
      $SEND_TIME=$ROW["SEND_TIME"];
      $IMPORTANT=$ROW["IMPORTANT"];
      $ATTACHMENT_ID=$ROW["ATTACHMENT_ID"];
      $ATTACHMENT_NAME=$ROW["ATTACHMENT_NAME"];
      $READ_FLAG=$ROW["READ_FLAG"];
   
      $SUBJECT=htmlspecialchars($SUBJECT);
      $CONTENT=stripslashes($CONTENT);
   
      $IS_WEBMAIL=$ROW["IS_WEBMAIL"];
   
      if($IMPORTANT=="0" || $IMPORTANT=="")
      $IMPORTANT_DESC="";
      else if($IMPORTANT=="1")
      $IMPORTANT_DESC="<font color='red'>"._("重要")."</font>";
      else if($IMPORTANT=="2")
      $IMPORTANT_DESC="<font color='red'>"._("非常重要")."</font>";
   
      $query1 = "SELECT UID,USER_NAME from USER where USER_ID='$FROM_ID'";
      $cursor1= exequery($connection,$query1);
      if($ROW=mysql_fetch_array($cursor1))
      {
         $UID=$ROW["UID"];
         $FROM_NAME=$ROW["USER_NAME"];
      }
      else
         $FROM_NAME=$FROM_ID;
         
      //2012/4/12 2:30:45 lp 增加已阅状态更新
      if($READ_FLAG == 0)
      {
         $query = "update EMAIL set READ_FLAG = 1 where EMAIL_ID='$EMAIL_ID'";
         exequery($connection,$query);    
      }
   }
   else
   {
      exit;
   }
?>
      <div class="container">
         <div id="replyTo" style="display:none;">
            <em userid="<?=$FROM_ID?>" uid="<?=$UID?>"><?=$FROM_NAME?></em>   
         </div>
         <div class="read_detail"><span class="grapc"><?=_("发件人：")?></span><?=$FROM_NAME?></div>
         <?
            if($IS_WEBMAIL!="0")
            {
         ?>
         <div class="read_detail"><span class="grapc"><?=_("收件人：")?></span><?=$FROM_NAME?></div>
         <?
            }
         ?>
         <div class="read_detail read_detail_muti">
            <em><?=$SUBJECT?> <?=$IMPORTANT_DESC?></em>
            <span class="fix_detail grapc"><?=date("Y"._("年")."n"._("月")."j"._("日")." H:i",$SEND_TIME)?></span>
         </div>
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
		
		
