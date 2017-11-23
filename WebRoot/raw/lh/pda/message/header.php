<?
   include_once("pda/header.php");
	include_once("pda/config.php");
?>
<script>
var monInterval = {MSG_LIST_REF_SEC:<?=$C['MSG_LIST_REF_SEC']?>, MSG_DIOG_REF_SEC:<?=$C['MSG_DIOG_REF_SEC']?>};
var loginUser = {uid:<?=$LOGIN_UID?>, user_id:"<?=str_replace("\"", "\\\"", $LOGIN_USER_ID)?>", user_name:"<?=str_replace("\"", "\\\"", $LOGIN_USER_NAME)?>"};
</script>