<?
   include_once("../inc_header.php");
   include_once("inc/utility_all.php");
	include_once("inc/utility_sms1.php");
	ob_clean();
?>
<ul id='sms-pic-list' class="main-comm-list comm-pic-list">
<?
   $query = "SELECT SMS_ID,FROM_ID,FROM_UNIXTIME(SEND_TIME) as SEND_TIME,SMS_TYPE,CONTENT,USER_NAME,REMIND_FLAG from SMS,SMS_BODY,USER where SMS.BODY_ID=SMS_BODY.BODY_ID and USER.USER_ID=SMS_BODY.FROM_ID and TO_ID='$LOGIN_USER_ID' and SMS_TYPE ='$SMS_TYPE_ID' ORDER BY SEND_TIME desc limit 100";
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
         $Class = " active";
         cancel_sms_remind($SMS_ID);
      }else{
         $Class = "";$unread = "";
      }
?>
	<li class="<?=$Class?>" q_id="<?=$SMS_ID?>">
	   <img src="<?=showAvatar($AVATAR,$SEX)?>" class="ui-li-thumb" />
	   <h3><span class="time"><?=timeintval($SEND_TIME)?></span><?=$FROM_NAME?></h3>
	   <p class="grapc"><?=strip_tags($CONTENT)?>&nbsp;</p>
	</li>
<?
	}
?>
</ul>
<div class='clearfix'></div>