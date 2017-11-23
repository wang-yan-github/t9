<?
   include_once("../auth.php");
   include_once("inc/utility_all.php");
   ob_clean();

   $query="update EMAIL set DELETE_FLAG='3' where TO_ID='$LOGIN_USER_ID' and (DELETE_FLAG='' or (DELETE_FLAG='' or DELETE_FLAG='0')) and EMAIL_ID='$EMAIL_ID'";
   exequery($connection,$query);
   
   $query="update EMAIL set DELETE_FLAG='4' where TO_ID='$LOGIN_USER_ID' and DELETE_FLAG='2' and EMAIL_ID='$EMAIL_ID'";
   exequery($connection,$query);
  
   echo "+OK";

?>