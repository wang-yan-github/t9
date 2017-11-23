<?
include_once("../header.php");
include_once("run_role.php");
include_once("inc/utility.php");
$RUN_ID = intval($RUN_ID);
$PRCS_ID = intval($PRCS_ID);
$FLOW_ID = intval($FLOW_ID);
$FLOW_PRCS = intval($FLOW_PRCS);
$run_role = run_role($RUN_ID,$PRCS_ID);
ob_clean();
//无主办人会签，先检查是否最后一个经办人
$query = "select TOP_FLAG FROM FLOW_RUN_PRCS WHERE RUN_ID='$RUN_ID' AND PRCS_ID='$PRCS_ID' AND FLOW_PRCS='$FLOW_PRCS' AND USER_ID='$LOGIN_USER_ID'";
$cursor = exequery($connection,$query);
if($ROW=mysql_fetch_array($cursor))
	$TOP_FLAG = $ROW["TOP_FLAG"];

if($TOP_FLAG==2)
{
	$query = "select 1 FROM FLOW_RUN_PRCS WHERE RUN_ID='$RUN_ID' AND PRCS_ID='$PRCS_ID' AND FLOW_PRCS='$FLOW_PRCS' AND USER_ID<>'$LOGIN_USER_ID' AND PRCS_FLAG IN(1,2)";
	$cursor = exequery($connection,$query);
	if(!$ROW=mysql_fetch_array($cursor))
	{
		$turn_next = true;
	}
	else
	{
		$turn_next = false;
	}
}

      if(!find_id($run_role,1) && !find_id($run_role,4))
      {
          echo 'NOSUBEDITPRIV';
          exit;
      }
      else if(!$turn_next)
      {
          $CUR_TIME=date("Y-m-d H:i:s",time());
          $query = "update FLOW_RUN_PRCS set PRCS_FLAG='4',DELIVER_TIME='$CUR_TIME' WHERE RUN_ID='$RUN_ID' and PRCS_ID='$PRCS_ID' and USER_ID='$LOGIN_USER_ID'";
          exequery($connection,$query);
          echo 'WORKDONECOMPLETE';
          exit;
      }else{
      	echo 'TURNNEXT';
      	exit;	
     	}
?>