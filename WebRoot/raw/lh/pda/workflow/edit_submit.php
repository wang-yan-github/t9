<?
   include_once("../header.php");
   include_once("run_role.php");
   include_once("inc/utility_flow.php");
   ob_clean();
   $RUN_ID = intval($RUN_ID);
	$PRCS_ID = intval($PRCS_ID);
	$FLOW_ID = intval($FLOW_ID);
	$FLOW_PRCS = intval($FLOW_PRCS);
   $RUN_ROLE = run_role($RUN_ID,$PRCS_ID);
   if(!find_id($RUN_ROLE,2))
   {
      echo "NOEDITPRIV";
      exit;
   }
   
   //$_POST = td_iconv($_POST, "utf-8", $MYOA_CHARSET);
   foreach($_POST as $k => $v)
   {
   	$_POST[$k] = iconv("utf-8", $MYOA_CHARSET."//IGNORE",$v);
   }

   $query = "SELECT * from FLOW_TYPE WHERE FLOW_ID='$FLOW_ID'";
   $cursor1= exequery($connection,$query);
   if($ROW=mysql_fetch_array($cursor1))
   {
       $FORM_ID = $ROW["FORM_ID"];
       $FLOW_TYPE=$ROW["FLOW_TYPE"];
       //若没生成缓存则生成	  
       include_once("inc/workflow_form.php");
   }
   
   
   //--写入单独数据表BEGIN--
   $table_name = 'flow_data_'.$FLOW_ID;
   update_table($FLOW_ID,$ELEMENT_ARRAY);
  
   //不存在则写入
   $sql = " select 1 from $table_name where run_id='$RUN_ID' limit 1";
   $cursor = exequery($connection,$sql);
   if(!mysql_fetch_array($cursor))
   {
   	//不存在则写入
   	$run_data = array("run_id" => $RUN_ID);
   	$sql = "select BEGIN_USER,BEGIN_TIME,RUN_NAME from FLOW_RUN where RUN_ID='$RUN_ID'";
   	$cursor = exequery($connection,$sql);
   	if($ROW = mysql_fetch_array($cursor))
   	{
   		$run_data["begin_user"] = $ROW["BEGIN_USER"];
   		$run_data["begin_time"] = $ROW["BEGIN_TIME"];
   		$run_data["run_name"] = $ROW["RUN_NAME"];
   	}
   	foreach ($_POST as $key=>$value)
   	{
   		//普通字段
   		if (strtolower(substr($key,0,5)) == 'data_')
   		{
   			$run_data["$key"] = $value;
   		}
   	}
   	insert_table_data($FLOW_ID,$run_data);
   }
   else 
   {
		
		//存在则更新
		$run_data = array();
		if($SAVE_FLAG=="N")
        {
            $run_data["run_name"] = $RUN_NAME;
        }
        
        //$ELEMENT_ARR = $ELEMENT_ARRAY;
		foreach ($_POST as $key=>$value)
		{
			if(find_id($HIDDEN_STR,substr($key,5))||find_id($READ_ONLY_STR,substr($key,5)))
				continue;
			if (strtolower(substr($key,0,5)) == 'data_')
			{
				$run_data[$key] = $_POST[$key];
			}
		}
		
		update_table_data($FLOW_ID,$RUN_ID,$run_data);
   }
   
   //--写入单独数据表END--
   foreach ($_POST as $key=>$value)
   {
   	if (strtolower(substr($key,0,4)) == 'data')
   	{
   	    $ITEM_ID = substr($key,5);
   	    $query="update FLOW_RUN_DATA set ITEM_DATA='$value' where RUN_ID='$RUN_ID' and ITEM_ID='$ITEM_ID'";
   	    exequery($connection, $query);
   	}
   }
   
?>
   <div class="no_msg"><?=_("保存成功")?></div>
   
   <div id="save_opts" class="save_opts" style="display:none;">
      <span class="continueEdit_flow"><?=_("继续编辑")?></span>
      <span class="turn_flow"><?=_('转交')?></span> 
   </div>