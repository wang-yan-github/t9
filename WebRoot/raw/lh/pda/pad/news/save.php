<?
   include_once("../header.php");
   include_once("inc/utility_all.php");
   ob_clean();
   
   $CONTENT = td_iconv($CONTENT, "utf-8", $MYOA_CHARSET);
   
   $CUR_TIME=date("Y-m-d H:i:s",time());
   $query="insert into NEWS_COMMENT(NEWS_ID,PARENT_ID,USER_ID,CONTENT,RE_TIME) values ($NEWS_ID,$PARENT_ID,'$LOGIN_USER_ID','$CONTENT','$CUR_TIME')";
   exequery($connection,$query);
   
   echo "SUCCESS";
   exit;

?>