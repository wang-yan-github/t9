<?
   include_once("../auth.php");
   include_once("inc/utility_all.php");
   ob_clean();

   $query="delete from CALENDAR where CAL_ID = '$CAL_ID'";
   exequery($connection,$query);
  
   echo "+OK";

?>