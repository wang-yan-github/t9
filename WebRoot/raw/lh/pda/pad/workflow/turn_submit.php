<?
include_once("../header.php");
include_once("run_role.php");
include_once("general/workflow/list/turn/condition.php");
ob_clean();
$RUN_ID = intval($RUN_ID);
$PRCS_ID = intval($PRCS_ID);
$FLOW_ID = intval($FLOW_ID);
$FLOW_PRCS = intval($FLOW_PRCS);
$RUN_ROLE = run_role($RUN_ID,$PRCS_ID);
$PRCS_ID_NEXT_ARR = explode(",",td_trim($PRCS_ID_NEXT));
$query = "select TOP_FLAG FROM FLOW_RUN_PRCS WHERE RUN_ID='$RUN_ID' AND PRCS_ID='$PRCS_ID' AND FLOW_PRCS='$FLOW_PRCS' AND USER_ID='$LOGIN_USER_ID'";
$cursor = exequery($connection,$query);
if($ROW=mysql_fetch_array($cursor))
	$TOP_FLAG = $ROW["TOP_FLAG"];
if($TOP_FLAG!=2)
{
	if(!find_id($RUN_ROLE,2))
	{
		//echo '<div class="no_msg">'._("无权限！").'</div>';
		echo "NOEDITPRIV";
	   exit;
	}
}
else
{
	//无主办会签
	if(!find_id($RUN_ROLE,4))
	{
		//echo '<div class="no_msg">'._("无权限！").'</div>';
		echo "NOSIGNFLOWPRIV";
	   exit;
	}
}

$CUR_TIME=date("Y-m-d H:i:s",time());
$PRCS_ID_NEW=$PRCS_ID+1;//下一步骤运行编号
if(td_trim($PRCS_ID_NEXT)==0)
{
    $query = "update FLOW_RUN_PRCS set PRCS_FLAG='4' WHERE RUN_ID='$RUN_ID' and PRCS_ID='$PRCS_ID' and FLOW_PRCS='$FLOW_PRCS'";
    exequery($connection,$query);
    
    $query = "update FLOW_RUN_PRCS set DELIVER_TIME='$CUR_TIME' WHERE RUN_ID='$RUN_ID' and PRCS_ID='$PRCS_ID' and FLOW_PRCS='$FLOW_PRCS' and USER_ID='$LOGIN_USER_ID'";
    exequery($connection,$query);
    
    //判断是否唯一执行中步骤
    $query = "select 1 FROM FLOW_RUN_PRCS WHERE RUN_ID='$RUN_ID' AND PRCS_FLAG IN (1,2) AND ((TOP_FLAG IN (0,1) AND OP_FLAG=1) OR TOP_FLAG=2)";
    $cursor = exequery($connection,$query);
    if(!mysql_fetch_array($cursor))
    {
        $query = "update FLOW_RUN set END_TIME='$CUR_TIME' WHERE RUN_ID='$RUN_ID'";
        exequery($connection,$query);
    }
    //工作流日志
    $CONTENT=_("结束流程");
    $USER_IP = get_client_ip();
    $query = "insert into FLOW_RUN_LOG (LOG_ID,RUN_ID,RUN_NAME,FLOW_ID,PRCS_ID,FLOW_PRCS,USER_ID,TIME,TYPE,IP,CONTENT) VALUES ('','$RUN_ID','$RUN_NAME','$FLOW_ID','$PRCS_ID','$FLOW_PRCS','$LOGIN_USER_ID','$CUR_TIME','1','$USER_IP','$CONTENT')";
    exequery($connection,$query);
    //echo '<div class="no_msg">'._("工作已结束！").'</div>';
    echo "WORKCOMPLETE";
    exit;
}
else
{
	foreach ($PRCS_ID_NEXT_ARR as $PRCS_ID_NEXT_VAL)
	{
		$PRCS_USER_OP_STR = "PRCS_USER_OP_" . $PRCS_ID_NEXT_VAL;
		$PRCS_USER_OP = $$PRCS_USER_OP_STR;
		$PRCS_USER_STR = "PRCS_USER_" . $PRCS_ID_NEXT_VAL;
		$PRCS_USER = $$PRCS_USER_STR;

	   //lp 2012/4/23 14:44:11 增加中文用户名判断
	   $PRCS_USER_OP = td_iconv($PRCS_USER_OP, "utf-8", $MYOA_CHARSET);
	   $PRCS_USER = td_iconv($PRCS_USER, "utf-8", $MYOA_CHARSET);
	   
	   //强制合并节点与子流程节点
	   $query = "select GATHER_NODE,CHILD_FLOW,PRCS_TYPE FROM FLOW_PROCESS WHERE FLOW_ID='$FLOW_ID' AND PRCS_ID='$PRCS_ID_NEXT_VAL'";
	   $cursor= exequery($connection,$query);
	   if($ROW=mysql_fetch_array($cursor))
	   {
	   	 $GATHER_NODE=$ROW["GATHER_NODE"];
	   	 $CHILD_FLOW=$ROW["CHILD_FLOW"];
	   	 $PRCS_TYPE=$ROW["PRCS_TYPE"];
	   }
	   
	   //------------是否允许按转交规则转交--------------
	   $query="select FREE_OTHER from FLOW_TYPE where FLOW_ID='$FLOW_ID';";
	   $cursor=exequery($connection,$query);
	   if($ROW=mysql_fetch_array($cursor))
	     $FREE_OTHER=$ROW["FREE_OTHER"];
	     
	   $OTHER_ARRAY = array();
	   if($FREE_OTHER==2)
	   {
	     $PRCS_USER_OP_OLD=$PRCS_USER_OP;
	     $PRCS_USER_OP=turn_other($PRCS_USER_OP);
	   }
	   if($PRCS_USER_OP)
	   {
		   //处理并发
		   $query = "SELECT PRCS_ID from FLOW_RUN_PRCS where RUN_ID='$RUN_ID' and FLOW_PRCS='$PRCS_ID_NEXT_VAL' and USER_ID='$PRCS_USER_OP' and PRCS_FLAG in('1','2')";
		   if(!$GATHER_NODE)
		   {
		   	 $query .= " and PRCS_ID='$PRCS_ID_NEW'";
		   }
		   $cursor= exequery($connection,$query);
		   if(!$ROW=mysql_fetch_array($cursor))
		   {
		   	 //主办人
		   	 $query="insert into FLOW_RUN_PRCS(RUN_ID,PRCS_ID,USER_ID,PRCS_FLAG,FLOW_PRCS,TIME_OUT,OP_FLAG,TOP_FLAG,PARENT,CREATE_TIME,OTHER_USER) values ('$RUN_ID','$PRCS_ID_NEW','$PRCS_USER_OP','1',$PRCS_ID_NEXT_VAL,'','1','0','$FLOW_PRCS','$CUR_TIME','')";
		   	 exequery($connection, $query);
		   }
		   else
		   {
		   	 //合并时判断当前步骤号与下一步骤号大小
		   	 if($ROW["PRCS_ID"] > $PRCS_ID_NEW)
		   	 {
		   		$PRCS_ID_NEW = $ROW["PRCS_ID"];
		   	 }
		   	 $LAST_PRCS_ID = $ROW["PRCS_ID"];
		   	 $query = "update FLOW_RUN_PRCS set PARENT=CONCAT(PARENT,',$FLOW_PRCS'),PRCS_ID='$PRCS_ID_NEW' WHERE RUN_ID='$RUN_ID' AND PRCS_ID='$LAST_PRCS_ID' AND FLOW_PRCS='$PRCS_ID_NEXT_VAL' and USER_ID='$PRCS_USER_OP' and PRCS_FLAG in ('1','2')";
		   	 exequery($connection,$query);
		   }
	   }
	   
	    
	   if($FREE_OTHER==2)
	     $PRCS_USER=turn_other($PRCS_USER,$PRCS_USER_OP_OLD);
	   $PRCS_USER_ARRAY = explode(",",rtrim($PRCS_USER,","));
	   foreach($PRCS_USER_ARRAY as $v)
	   {
	   	if($v == "" || $v == $PRCS_USER_OP)
	   		continue;
	     $query="insert into FLOW_RUN_PRCS(RUN_ID,PRCS_ID,USER_ID,PRCS_FLAG,FLOW_PRCS,TIME_OUT,OP_FLAG,TOP_FLAG,PARENT,CREATE_TIME,OTHER_USER) values ('$RUN_ID','$PRCS_ID_NEW','$v','1',$PRCS_ID_NEXT_VAL,'','0','0','$FLOW_PRCS','$CUR_TIME','$OTHER_ARRAY[$TOK]')";
	     exequery($connection, $query);
	   }
	    
	   //直接更新状态为办结
	   $query = "update FLOW_RUN_PRCS set DELIVER_TIME='$CUR_TIME',PRCS_FLAG='3' WHERE RUN_ID='$RUN_ID' and PRCS_ID='$PRCS_ID' and FLOW_PRCS='$FLOW_PRCS' and USER_ID='$LOGIN_USER_ID' and PRCS_FLAG in ('1','2')";
	   exequery($connection, $query);
	   
	   //工作流日志
	   $USER_NAME_STR="";
	   $query = "SELECT USER_NAME FROM USER WHERE FIND_IN_SET(USER_ID,'$PRCS_USER_OP,$PRCS_USER')";
	   $cursor= exequery($connection,$query);
	   while($ROW=mysql_fetch_array($cursor))
	    $USER_NAME_STR.=$ROW["USER_NAME"].",";
	    
	   $USER_IP = get_client_ip();
	   $CONTENT=_("转交至步骤：").$PRCS_ID_NEW.","._("办理人：").$USER_NAME_STR;
	   $query = "select RUN_NAME,FLOW_ID FROM FLOW_RUN WHERE RUN_ID='$RUN_ID'";
	   $cursor = exequery($connection,$query);
	   if($ROW=mysql_fetch_array($cursor))
	   {
	      $RUN_NAME=$ROW["RUN_NAME"];
	      $FLOW_ID=$ROW["FLOW_ID"];
	   }
	   $query = "insert into FLOW_RUN_LOG (LOG_ID,RUN_ID,RUN_NAME,FLOW_ID,PRCS_ID,FLOW_PRCS,USER_ID,TIME,TYPE,IP,CONTENT) VALUES ('','$RUN_ID','$RUN_NAME','$FLOW_ID','$PRCS_ID','$FLOW_PRCS','$LOGIN_USER_ID','$CUR_TIME','1','$USER_IP','$CONTENT')";
	   exequery($connection,$query);
   }
   echo "WORKHASTURNNEXT";
   exit;
}

?>