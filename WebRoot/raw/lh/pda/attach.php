<?
   include_once("header.php");
   include_once("inc/utility_all.php");
   ob_clean();
?>
   <iframe class="attach_iframe" src="attach_show.php?P=<?=$P?>&AID=<?=$AID?>&MODULE=<?=$MODULE?>&YM=<?=$YM?>&ATTACHMENT_ID=<?=$ATTACHMENT_ID?>&ATTACHMENT_NAME=<?=$ATTACHMENT_NAME?>" frameborder="0" style="height:1000px;"></iframe>