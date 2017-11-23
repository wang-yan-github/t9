<?
include_once("../header.php");
include_once("inc/utility_all.php");
include_once("inc/utility_sms1.php");
ob_clean();

//ÒÑÔÄ¶Á
if($UNREAD==1){
   cancel_sms_remind($SMS_ID);   
}

$query = "SELECT SMS_ID,FROM_ID,FROM_UNIXTIME(SEND_TIME) as SEND_TIME,SMS_TYPE,CONTENT,USER_NAME from SMS,SMS_BODY,USER where SMS.BODY_ID=SMS_BODY.BODY_ID and USER.USER_ID=SMS_BODY.FROM_ID and TO_ID='$LOGIN_USER_ID' and SMS_ID='$SMS_ID'";
$cursor= exequery($connection,$query);
if($ROW=mysql_fetch_array($cursor))
{
   $SMS_ID=$ROW["SMS_ID"];
   $FROM_ID=$ROW["FROM_ID"];
   $SEND_TIME=$ROW["SEND_TIME"];
   $REMIND_FLAG=$ROW["REMIND_FLAG"];
   $SMS_TYPE=$ROW["SMS_TYPE"];
   $CONTENT=$ROW["CONTENT"];
   $FROM_NAME=$ROW["USER_NAME"];

   /*$CONTENT=str_replace("<","&lt",$CONTENT);
   $CONTENT=str_replace(">","&gt",$CONTENT);
   $CONTENT=stripslashes($CONTENT);*/
}
else
   exit;
?>
<div class="container">
   <h3><?=$FROM_NAME?> <?=substr($SEND_TIME, 0, 16)?></h3>
   <div class="read_content"><?=$CONTENT?></div>
</div>
