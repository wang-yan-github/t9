<?php
include_once ("../header.php");
include_once("run_role.php");
include_once("inc/utility_all.php");
include_once("inc/utility_flow.php");
ob_clean();
$PRCS_ROLE = prcs_role($FLOW_ID,1);

if(!find_id($PRCS_ROLE,2))
{
	echo "NOCREATEPRIV";
	exit;
}

$RUN_NAME_LEFT = td_iconv($RUN_NAME_LEFT, "utf-8", $MYOA_CHARSET);
$RUN_NAME = td_iconv($RUN_NAME, "utf-8", $MYOA_CHARSET);
$RUN_NAME_RIGHT = td_iconv($RUN_NAME_RIGHT, "utf-8", $MYOA_CHARSET);

$RUN_NAME=$RUN_NAME_LEFT.$RUN_NAME.$RUN_NAME_RIGHT;
$RUN_NAME=htmlspecialchars($RUN_NAME);

if(trim($RUN_NAME) == ""){
	echo "NORUNNAME";
	exit;
}

$query = "SELECT MAX(RUN_ID) from FLOW_RUN";
$cursor = exequery($connection,$query);
if($ROW=mysql_fetch_array($cursor))
	$RUN_ID=$ROW[0]+1;
if(strstr($RUN_NAME,'{RUN}'))
	$RUN_NAME = str_replace('{RUN}',$RUN_ID,$RUN_NAME);

$query = "SELECT 1 from FLOW_RUN WHERE RUN_NAME='$RUN_NAME' and FLOW_ID='$FLOW_ID'";
$cursor1= exequery($connection,$query);
if($ROW=mysql_fetch_array($cursor1))
{
	echo "NAMEREPEAT";
   exit;
}

$RUN_NAME = str_replace("\n","",$RUN_NAME);
$RUN_NAME = str_replace("\r","",$RUN_NAME);
$CUR_TIME=date("Y-m-d H:i:s",time());
$query="insert into FLOW_RUN(RUN_ID,RUN_NAME,FLOW_ID,BEGIN_USER,BEGIN_TIME) values ($RUN_ID,'$RUN_NAME','$FLOW_ID','$LOGIN_USER_ID','$CUR_TIME')";
if(!exequery($connection,$query))
{
	$query = "SELECT MAX(RUN_ID) from FLOW_RUN";
	$cursor = exequery($connection,$query);
	if($ROW=mysql_fetch_array($cursor))
		$RUN_ID=$ROW[0]+1;
	$query="insert into FLOW_RUN(RUN_ID,RUN_NAME,FLOW_ID,BEGIN_USER,BEGIN_TIME) values ($RUN_ID,'$RUN_NAME','$FLOW_ID','$LOGIN_USER_ID','$CUR_TIME')";
	if(!exequery($connection,$query))
	{
		echo "NOCREATERUN";
		exit;
	}
}

$query="insert into FLOW_RUN_PRCS(RUN_ID,PRCS_ID,USER_ID,PRCS_FLAG,FLOW_PRCS,CREATE_TIME) values ($RUN_ID,1,'$LOGIN_USER_ID','1','1','$CUR_TIME')";
exequery($connection,$query);

$FLOW_ID = intval($FLOW_ID);
$query = "SELECT * from FLOW_TYPE WHERE FLOW_ID='$FLOW_ID'";
$cursor1= exequery($connection,$query);
if($ROW=mysql_fetch_array($cursor1))
{
	$FORM_ID=$ROW["FORM_ID"];
	$AUTO_NAME = $ROW["AUTO_NAME"];
	include_once("inc/workflow_form.php");
}

if(strstr($AUTO_NAME,"{N}"))
{
	$query="update FLOW_TYPE set AUTO_NUM=AUTO_NUM+1 where FLOW_ID='$FLOW_ID'";
	exequery($connection,$query);
}
$query = "SELECT * from FLOW_FORM_TYPE WHERE FORM_ID='$FORM_ID'";
$cursor1= exequery($connection,$query);
if($ROW=mysql_fetch_array($cursor1))
	$PRINT_MODEL=$ROW["PRINT_MODEL_SHORT"];

//-------- Html ÖÇÄÜ·ÖÎö -----------
$run_data = array(
		"run_id"        => $RUN_ID,
		"run_name"      => $RUN_NAME,
		"begin_time"    => $CUR_TIME,
		"begin_user"    => $LOGIN_USER_ID
);
foreach($ELEMENT_ARRAY as $ENAME => $ELEMENT_ARR)
{
	//$ETAG = $ELEMENT_ARR["TAG"];
	$ETAG = strtoupper($ELEMENT_ARR["TAG"]);
	$ELEMENT = $ELEMENT_ARR["CONTENT"];
	$EVALUE = $ELEMENT_ARR["VALUE"];
	$ETITLE = $ELEMENT_ARR["TITLE"];
	$ECLASS = $ELEMENT_ARR["CLASS"];
	$ITEM_ID = $ELEMENT_ARR["ITEM_ID"];

	if($ECLASS=="DATE" || $ECLASS=="USER")
		continue;

	if($ETAG=="INPUT" && stristr($ELEMENT,"checkbox"))
	{
		if(stristr($ELEMENT,"CHECKED") || stristr($ELEMENT,' checked="checked"'))
			$ITEM_DATA="on";
		else
			$ITEM_DATA="";
	}
	elseif($ETAG!="SELECT" && $ECLASS!="LIST_VIEW")
	{
		$ITEM_DATA=$EVALUE;
		$ITEM_DATA=str_replace("\"","",$ITEM_DATA);
		if($ITEM_DATA=="{MACRO}") //add by sogo
			$ITEM_DATA="";
	}
	else
		$ITEM_DATA="";

	$run_data[strtolower($ENAME)] = $ITEM_DATA;
}

update_table($FLOW_ID,$ELEMENT_ARRAY);
insert_table_data($FLOW_ID,$run_data);
?>
<script>
q_run_id      = '<?=$RUN_ID?>';
q_flow_id     = '<?=$FLOW_ID?>';
q_prcs_id     = '1';
q_flow_prcs   = '1';
q_op_flag     = '1';
</script>