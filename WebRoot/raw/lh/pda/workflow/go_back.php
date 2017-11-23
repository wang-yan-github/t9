<?
include_once ("../header.php");
include_once ("run_role.php");
include_once ("inc/utility_all.php");
ob_clean ();

$RUN_ID = intval($RUN_ID);
$FLOW_ID = intval($FLOW_ID);
$FLOW_PRCS = intval($FLOW_PRCS);

$RUN_ROLE=run_role($RUN_ID,$PRCS_ID);
if(!find_id($RUN_ROLE,1) && !find_id($RUN_ROLE,2) && !find_id($RUN_ROLE,3))
	exit;

$EDIT_TIME=date("Y-m-d H:i:s",time());
if ($CONTENT) {
	$CONTENT = td_iconv(htmlspecialchars($CONTENT), "utf-8", $MYOA_CHARSET);
	$query = "INSERT INTO FLOW_RUN_FEEDBACK (RUN_ID,PRCS_ID,FLOW_PRCS,USER_ID,CONTENT,ATTACHMENT_ID,ATTACHMENT_NAME,EDIT_TIME,SIGN_DATA) VALUES ('$RUN_ID','$PRCS_ID','$FLOW_PRCS','$LOGIN_USER_ID','$CONTENT','','','$EDIT_TIME','$SIGN_DATA')";
	exequery ( $connection, $query );
}

$query = "SELECT ALLOW_BACK from FLOW_PROCESS WHERE FLOW_ID='$FLOW_ID' AND PRCS_ID='$FLOW_PRCS'";
$cursor= exequery($connection,$query);
if($ROW=mysql_fetch_array($cursor))
	$ALLOW_BACK=$ROW["ALLOW_BACK"];

$query = "SELECT PARENT from FLOW_RUN_PRCS WHERE RUN_ID='$RUN_ID' AND PRCS_ID='$PRCS_ID' AND USER_ID='$LOGIN_USER_ID' AND OP_FLAG=1";
$cursor= exequery($connection,$query);
if($ROW=mysql_fetch_array($cursor))
	$PARENT=$ROW["PARENT"];
 
if($ALLOW_BACK && $PRCS_ID!=1)
{
	$CUR_TIME=date("Y-m-d H:i:s",time());
	$PRCS_ID_NEW=$PRCS_ID+1;
	 
	//------------直接返回上一步骤-----------------
	if($ALLOW_BACK==1 && !strstr($PARENT,","))
	{
		$PRCS_ID_LAST=$PRCS_ID-1;
		$query = "select USER_ID,FLOW_PRCS,TOP_FLAG from FLOW_RUN_PRCS WHERE RUN_ID='$RUN_ID' AND PRCS_ID='$PRCS_ID_LAST' ORDER BY OP_FLAG DESC";
		$cursor= exequery($connection,$query);
		while($ROW=mysql_fetch_array($cursor))
		{
			$FLOW_PRCS_NEW = $ROW["FLOW_PRCS"];
			$TOP_FLAG = $ROW["TOP_FLAG"];
			$USER_ID_LAST .= $ROW["USER_ID"].",";
		}
		$LOG_CONTENT=_("回退至上一步骤");
	}
	else //返回之前指定步骤
	{
		$FLOW_PRCS_NEW=$FLOW_PRCS_LAST;
		$PRCS_ID_LAST = "";
		$query = "select PRCS_ID,USER_ID,FLOW_PRCS,TOP_FLAG from FLOW_RUN_PRCS WHERE RUN_ID='$RUN_ID' AND FLOW_PRCS='$FLOW_PRCS_LAST' ORDER BY PRCS_ID DESC,OP_FLAG DESC";
		$cursor= exequery($connection,$query);
		while($ROW=mysql_fetch_array($cursor))
		{
			$PRCS_ID_TMP = $ROW["PRCS_ID"];
			if(!$PRCS_ID_LAST)
				$PRCS_ID_LAST=$PRCS_ID_TMP;
			if($PRCS_ID_TMP!=$PRCS_ID_LAST)
				break;
			$TOP_FLAG = $ROW["TOP_FLAG"];
			$USER_ID_LAST .= $ROW["USER_ID"].",";
			$PRCS_ID_LAST=$PRCS_ID_TMP;
		}
		$LOG_CONTENT= sprintf(_("回退至步骤%s"),$FLOW_PRCS_LAST);
	}
	 
	//新建下一步
	$USER_ARRAY = explode(",",$USER_ID_LAST);
	foreach($USER_ARRAY as $K=>$USER_ID)
	{
		if($USER_ID == "")
			continue;
		if($TOP_FLAG==0 && $K==0)
			$OP_FLAG = 1;
		elseif($TOP_FLAG==1)
		$OP_FLAG = 1;
		else
			$OP_FLAG = 0;
		
		$query="insert into FLOW_RUN_PRCS(RUN_ID,PRCS_ID,USER_ID,PRCS_FLAG,FLOW_PRCS,OP_FLAG,TOP_FLAG,PARENT,CREATE_TIME) values ($RUN_ID,$PRCS_ID_NEW,'$USER_ID','1','$FLOW_PRCS_NEW','$OP_FLAG','$TOP_FLAG','$FLOW_PRCS','$CUR_TIME')";
		exequery($connection, $query);
	}

	//更新本步骤状态
	$PRCS_ID = intval($PRCS_ID);
	$query = "update FLOW_RUN_PRCS set DELIVER_TIME='$CUR_TIME',PRCS_FLAG='3' WHERE RUN_ID='$RUN_ID' and PRCS_ID='$PRCS_ID' and FLOW_PRCS='$FLOW_PRCS' and PRCS_FLAG in ('1','2')";
	exequery($connection, $query);

	run_log($RUN_ID,$PRCS_ID,$FLOW_PRCS,$LOGIN_USER_ID,1,$LOG_CONTENT);
	
	$USER_IP = get_client_ip();
	$query = "select RUN_NAME FROM FLOW_RUN WHERE RUN_ID='$RUN_ID'";
	$cursor = exequery($connection,$query);
	if($ROW=mysql_fetch_array($cursor))
	{
		$RUN_NAME=$ROW["RUN_NAME"];
	}
	$query = "insert into FLOW_RUN_LOG (LOG_ID,RUN_ID,RUN_NAME,FLOW_ID,PRCS_ID,FLOW_PRCS,USER_ID,TIME,TYPE,IP,CONTENT) VALUES ('','$RUN_ID','$RUN_NAME','$FLOW_ID','$PRCS_ID','$FLOW_PRCS','$LOGIN_USER_ID','$CUR_TIME','1','$USER_IP','$CONTENT')";
	exequery($connection,$query);
	echo "WORKHASGOBACK";
	exit;
}
else
{
	echo "WORKHASNOTGOBACK";
	exit;
}
?>