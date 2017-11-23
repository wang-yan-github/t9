<?
include_once("../header.php");
include_once("run_role.php");
ob_clean();
$RUN_ID = intval($RUN_ID);
$PRCS_ID = intval($PRCS_ID);
$FLOW_ID = intval($FLOW_ID);
$FLOW_PRCS = intval($FLOW_PRCS);
$query = "SELECT OP_FLAG from FLOW_RUN_PRCS where USER_ID='$LOGIN_USER_ID' AND RUN_ID='$RUN_ID' AND PRCS_ID='$PRCS_ID' AND FLOW_PRCS='$FLOW_PRCS'";
$cursor= exequery($connection,$query);
if($ROW=mysql_fetch_array($cursor))
{
    $OP_FLAG=$ROW["OP_FLAG"];
}


if(!run_role($RUN_ID,$PRCS_ID))
{
   echo "NOSIGNFLOWPRIV";
   exit;
}

if(trim($CONTENT)=="")
{
    echo "SIGNISNOTEMPTY";
    exit;  
}
else
{
   $CONTENT = td_iconv(htmlspecialchars($CONTENT), "utf-8", $MYOA_CHARSET);
   $EDIT_TIME=date("Y-m-d H:i:s",time());
   $query="INSERT INTO FLOW_RUN_FEEDBACK (RUN_ID,PRCS_ID,USER_ID,CONTENT,ATTACHMENT_ID,ATTACHMENT_NAME,EDIT_TIME) VALUES ($RUN_ID,$PRCS_ID,'$LOGIN_USER_ID','$CONTENT','','','$EDIT_TIME')";
   exequery($connection,$query);
   echo "SIGNSUCCESS";
   exit;
}

?>