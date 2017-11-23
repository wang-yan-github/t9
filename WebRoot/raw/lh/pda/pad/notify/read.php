<?
   include_once("../header.php");
   include_once("inc/utility_file.php");
   ob_clean();

   $CUR_DATE=date("Y-m-d",time());
   $CUR_TIME=date("Y-m-d H:i:s",time());
   $query = "SELECT * from NOTIFY where NOTIFY_ID='$NOTIFY_ID' and (TO_ID='ALL_DEPT' or find_in_set('$LOGIN_DEPT_ID',TO_ID) or find_in_set('$LOGIN_USER_PRIV',PRIV_ID) or find_in_set('$LOGIN_USER_ID',USER_ID)) and begin_date<='$CUR_DATE' and (end_date>='$CUR_DATE' or end_date is null) and PUBLISH='1'";

   $cursor= exequery($connection,$query);
   if($ROW=mysql_fetch_array($cursor))
   {
      $FROM_ID=$ROW["FROM_ID"];
      $SUBJECT_COLOR=$ROW["SUBJECT_COLOR"];
      $SUBJECT=$ROW["SUBJECT"];
      $FORMAT=$ROW["FORMAT"];
      //$CONTENT=$ROW["CONTENT"];
      $COMPRESS_CONTENT=@gzuncompress($ROW["COMPRESS_CONTENT"]);
      if($COMPRESS_CONTENT!=""&&$FORMAT!="2")
        $CONTENT=$COMPRESS_CONTENT;
      else
        $CONTENT=$ROW["CONTENT"];
      $ATTACHMENT_ID=$ROW["ATTACHMENT_ID"];
      $ATTACHMENT_NAME=$ROW["ATTACHMENT_NAME"];
      $FORMAT=$ROW["FORMAT"];
      $READERS=$ROW["READERS"];
      $SUBJECT=htmlspecialchars($SUBJECT);
      $TYPE_ID=$ROW["TYPE_ID"];
      $TYPE_NAME=get_code_name($TYPE_ID,"NOTIFY");

      $SUBJECT="<font color='".$SUBJECT_COLOR."'>".$SUBJECT."</font>";
        
      $BEGIN_DATE=$ROW["BEGIN_DATE"];
      $SEND_TIME=$ROW["SEND_TIME"];
      $BEGIN_DATE=strtok($BEGIN_DATE," ");

      $query1 = "SELECT USER_NAME from USER where USER_ID='$FROM_ID'";
      $cursor1= exequery($connection,$query1);
      if($ROW=mysql_fetch_array($cursor1))
         $FROM_NAME=$ROW["USER_NAME"];
      else
         $FROM_NAME=$FROM_ID;

      if($FORMAT=="2")
         $CONTENT="<a href='$CONTENT'>$CONTENT</a>";
         
      if(!find_id($READERS,$LOGIN_USER_ID))
      {
         $READERS.=$LOGIN_USER_ID.",";
         $query = "update NOTIFY set READERS='$READERS' where NOTIFY_ID='$NOTIFY_ID'";
         exequery($connection,$query);
         
         $query = "insert into APP_LOG(USER_ID,TIME,MODULE,OPP_ID,TYPE) values ('$LOGIN_USER_ID','$CUR_TIME','4','$NOTIFY_ID','1')";
         exequery($connection,$query);
      }

   }
   else
      exit;
?>
<div class="container">
	<div class="read_detail fix_read_detail">
		<h3 class="read_title fix_read_title"><span class="grapc"><?=_("标题：")?></span><?=$SUBJECT?></h3>
	</div>
   <? if($TYPE_NAME!=""){ ?>
   <div class="read_detail fix_read_detail" style="text-align:left;"><span class="grapc"><?=_("类型：")?></span><?=$TYPE_NAME?></div>
   <? } ?>
   <div class="read_detail fix_read_detail" style="text-align:left;"><span class="grapc"><?=_("创建人：")?></span><?=$FROM_NAME?></div>
   <div class="read_detail fix_read_detail" style="text-align:left;"><span class="grapc"><?=_("创建时间：")?></span><?=$SEND_TIME?></div>
   <div class="read_detail fix_read_detail" style="text-align:left;"><span class="grapc"><?=_("生效日期：")?></span><?=$BEGIN_DATE?></div>
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