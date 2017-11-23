<?

include_once("../header.php");
include_once("inc/utility_all.php");
include_once("inc/utility_file.php");
include_once("inc/utility_flow.php");
include_once("run_role.php");

function export_flow($RUN_ID,$WORD=0,$FLOW_VIEW=123)
{
  global $connection,$LOGIN_USER_ID,$ROOT_PATH,$ELEMENT_ARRAY;
  
  $OUTPUT = "";
  //----------- 文号 附件 -------------
  $query = "SELECT * from FLOW_RUN WHERE RUN_ID='$RUN_ID'";
  $cursor= exequery($connection,$query);
  if($ROW=mysql_fetch_array($cursor))
  {
     $FLOW_ID=$ROW["FLOW_ID"];
     $RUN_NAME=$ROW["RUN_NAME"];
     $ATTACHMENT_ID=$ROW["ATTACHMENT_ID"];
     $ATTACHMENT_NAME=$ROW["ATTACHMENT_NAME"];
  }
  
  $query = "SELECT * from FLOW_TYPE WHERE FLOW_ID='$FLOW_ID'";
  $cursor1= exequery($connection,$query);
  if($ROW=mysql_fetch_array($cursor1))
  {
     $FLOW_NAME=$ROW["FLOW_NAME"];
     $FLOW_TYPE=$ROW["FLOW_TYPE"];
     $FORM_ID=$ROW["FORM_ID"];
     $FORM_TYPE=$ROW["FORM_TYPE"];
     $FLOW_DOC=$ROW["FLOW_DOC"];
     $AUTO_NUM = $ROW["AUTO_NUM"];
  }
 
  if(strstr($FLOW_VIEW,"1"))
  {
    $query = "SELECT * from FLOW_FORM_TYPE WHERE FORM_ID='$FORM_ID'";
    $cursor1= exequery($connection,$query);
    if($ROW=mysql_fetch_array($cursor1))
    {
       $FORM_NAME=$ROW["FORM_NAME"];
       $PRINT_MODEL=$ROW["PRINT_MODEL_SHORT"];
    } 
  
    //----- 取表单数据 --------
  	$tbl_name = "flow_data_" . $FLOW_ID;
 	$query = "select * from $tbl_name  where RUN_ID='$RUN_ID'  limit 1";
 	$cursor= exequery($connection,$query);
 	if($ROW=mysql_fetch_array($cursor))
 	{
 		foreach($ROW as $key => $value)
		{
	    	if(strtolower(substr($key,0,5)) == 'data_')
	    	{
	        	$STR = strtoupper($key);
	        	$$STR = $value;
	    	}
		}
 	}
   
    //----------- 步骤1 -------------
    $query = "SELECT USER_ID,PRCS_TIME from FLOW_RUN_PRCS where RUN_ID='$RUN_ID' and PRCS_ID=1";
    $cursor= exequery($connection,$query);
    if($ROW=mysql_fetch_array($cursor))
    {
       $USER_ID=$ROW["USER_ID"];
       $PRCS_TIME1=$ROW["PRCS_TIME"];
       $PRCS_TIME1=format_date($PRCS_TIME1);
    }
  
  	//--------------------处理宏标记------------------------------------------  
  	$PRINT_MODEL=str_replace("#[MACRO_FORM]","<b>$FORM_NAME</b>",$PRINT_MODEL);
	$PRINT_MODEL=str_replace("#[MACRO_RUN_NAME]",$RUN_NAME,$PRINT_MODEL);
	$PRINT_MODEL=str_replace("#[MACRO_TIME]",_("日期：")."$PRCS_TIME1",$PRINT_MODEL);
	$PRINT_MODEL=str_replace("#[MACRO_RUN_ID]",$RUN_ID,$PRINT_MODEL);
	$PRINT_MODEL=str_replace("#[MACRO_COUNTER]",$AUTO_NUM,$PRINT_MODEL);
  
  	//---------附件链接宏标记------------------
  	
  	if($FLOW_DOC!=0)
  	{	
  		$PRINT_MODEL = getAttach($RUN_ID,$ATTACHMENT_ID,$ATTACHMENT_NAME,$PRINT_MODEL);
  	}
  	
  	
  	if(strstr($PRINT_MODEL,"#[MACRO_SIGN"))
  	   $PRINT_MODEL=getSignInfo($RUN_ID,$FLOW_ID,$PRINT_MODEL);
  	
  	$RUN_ID = intval($RUN_ID);
	$FLOW_ID = intval($FLOW_ID);
	$FLOW_PRCS = intval($FLOW_PRCS);
  	
  	//-----------判断字段对于当前用户是否为隐藏----------------
  	$query="select HIDDEN_ITEM from FLOW_PROCESS,FLOW_RUN_PRCS where FLOW_PROCESS.FLOW_ID='$FLOW_ID' and FLOW_RUN_PRCS.RUN_ID='$RUN_ID' and FLOW_RUN_PRCS.USER_ID='$LOGIN_USER_ID' and FLOW_PROCESS.PRCS_ID=FLOW_RUN_PRCS.FLOW_PRCS";
  	$cursor=exequery($connection,$query);
  	$HIDDEN_STR="";
  	while($ROW=mysql_fetch_array($cursor))
  	{
  	  if($ROW["HIDDEN_ITEM"])
  		  $HIDDEN_STR.=$ROW["HIDDEN_ITEM"];
  	}
  	
  	//若没生成缓存则生成	  
  	include_once("inc/workflow_form.php");
  	$ELEMENT_QUERY = $ELEMENT_ARRAY;
  	if($ELEMENT_ARRAY)
  	{
  		foreach($ELEMENT_ARRAY as $ENAME => $ELEMENT_ARR)
  		{
  		  //--- 默认值 ---
  		  $ECLASS = $ELEMENT_ARR["CLASS"];  
  		  $ITEM_ID = $ELEMENT_ARR["ITEM_ID"];
  		  $EVALUE = $ELEMENT_ARR["VALUE"];  
  		  $ETAG = $ELEMENT_ARR["TAG"];
  		  $ETITLE = $ELEMENT_ARR["TITLE"];
  		  $ELEMENT = $ELEMENT_ARR["CONTENT"];
  		  $ERICH = $ELEMENT_ARR["RICH"];
  		  $EHIDDEN = $ELEMENT_ARR['HIDDEN'];
  		
  		  $STR="DATA_".$ITEM_ID;
  		  $ITEM_VALUE=$$STR;
  		  $SIGN_VALUE=$ITEM_VALUE;
  		
  		  //--- 隐藏保密字段 ----
  		  if(find_id($HIDDEN_STR,$ETITLE) || $EHIDDEN == 1)
  		  {
  		     $ITEM_VALUE="";
  		     $SIGN_VALUE="";
  		  }
  		  
  		  //--- 用值替换控件 ----
  		  if($ECLASS=="DATE"||$ECLASS=="USER")
  		     $ITEM_VALUE="";
  		  elseif($ECLASS=="AUTO" && $ETAG=="SELECT" && $ITEM_VALUE!="") //--- 宏控件下拉菜单型 ---
  		  {
  		     $EDATAFLD=$ELEMENT_ARR["DATAFLD"];
  		     switch($EDATAFLD)
  		     {
  		        case "SYS_LIST_DEPT":
  		                      $query_auto="SELECT DEPT_NAME from DEPARTMENT where DEPT_ID='$ITEM_VALUE'";
  		                      $cursor_auto = exequery($connection,$query_auto);
  		                      if($ROW=mysql_fetch_array($cursor_auto))
  		                         $ITEM_VALUE=$ROW["DEPT_NAME"];
  		                      break;
  		        case "SYS_LIST_PRIV":
  		                      $query_auto="SELECT PRIV_NAME from USER_PRIV where USER_PRIV='$ITEM_VALUE'";
  		                      $cursor_auto = exequery($connection,$query_auto);
  		                      if($ROW=mysql_fetch_array($cursor_auto))
  		                         $ITEM_VALUE=$ROW["PRIV_NAME"];
  		                      break;
  		        case "SYS_LIST_USER":
  		        case "SYS_LIST_PRCSUSER1":
  		        case "SYS_LIST_PRCSUSER2":
  		                      $query_auto="SELECT USER_NAME from USER where USER_ID='$ITEM_VALUE'";
  		                      $cursor_auto = exequery($connection,$query_auto);
  		                      if($ROW=mysql_fetch_array($cursor_auto))
  		                         $ITEM_VALUE=$ROW["USER_NAME"];
  		                      break;
  		        case "SYS_LIST_SQL":
  		                      break;
  		     }
  		
  		  }
  		  elseif($ECLASS=="LIST_VIEW")  //列表控件
  		  {
  		    $LV_TITLE=$ELEMENT_ARR["LV_TITLE"];
  		    $LV_SIZE=$ELEMENT_ARR["LV_SIZE"];
  		    $MY_ARRAY_SIZE=explode("`",$LV_SIZE);
  		    $LV_VALUE= $ITEM_VALUE;
  		    $ITEM_VALUE="<TABLE class='small' style='border-collapse:collapse' border=1 cellspacing=0 cellpadding=2 bordercolor='#000000'><TR class=TableHeader>\n";
  		
  		    $MY_ARRAY=explode("`",$LV_TITLE);
  		    $ARRAY_COUNT_TITLE=sizeof($MY_ARRAY);
  		    if($MY_ARRAY[$ARRAY_COUNT_TITLE-1]=="")$ARRAY_COUNT_TITLE--;
  		    for($I=0;$I<$ARRAY_COUNT_TITLE;$I++)
  		       $ITEM_VALUE.="<TD nowrap>".$MY_ARRAY[$I]."</TD>\n";
  		    $ITEM_VALUE.="</TR>\n";
  		
  		    $MY_ARRAY=explode("\r\n",$LV_VALUE);
  		    $ARRAY_COUNT=sizeof($MY_ARRAY);
  		    if($MY_ARRAY[$ARRAY_COUNT-1]=="")$ARRAY_COUNT--;
  		    for($I=0;$I<$ARRAY_COUNT;$I++)
  		    {
  		        $ITEM_VALUE.="<tr>\n";
  		        $TR_DATA=$MY_ARRAY[$I];
  		
  		        $MY_ARRAY1=explode("`",$TR_DATA);
  		        for($J=0;$J<$ARRAY_COUNT_TITLE;$J++)
  		        {
  		        	  $TD_DATA=$MY_ARRAY1[$J];
  		        	  if($TD_DATA=="")
  		        	     $TD_DATA="&nbsp;";
  		            $ITEM_VALUE.="<td width=".($MY_ARRAY_SIZE[$J]*9).">".$TD_DATA."</td>\n";
  		        }
  		        $ITEM_VALUE.="</tr>\n";
  		    }
  		    $ITEM_VALUE.="</TABLE>\n";
  		  }
  		  elseif($ECLASS=="SIGN")  //签章控件
  		  {
  		    if($WORD)
  		       $ITEM_VALUE="";
  		    else
  		    {
  		      $SIGN_ID="DATA_".$ITEM_ID;
  		      $SIGN_CHECK=$ELEMENT_ARR["DATAFLD"];//印章锁定字段
  		      $ITEM_CHECK="";
  		      foreach($ELEMENT_QUERY as $ENAME1 => $ELEMENT_QUERY_ARR)
  		      {
  		         $ETITLE1=$ELEMENT_QUERY_ARR["TITLE"];
  		         $ECLASS1=$ELEMENT_QUERY_ARR["CLASS"];
  		         $ITEM_ID1=$ELEMENT_QUERY_ARR["ITEM_ID"];
  		         
  		         if($ECLASS1=="DATE" || $ECLASS1=="USER")
  		            continue;
  		          
  		         if(find_id($SIGN_CHECK,$ETITLE1))
  		           $ITEM_CHECK.="DATA_".$ITEM_ID1.",";
  		      }
  		      if(substr($ITEM_CHECK,-1,1)==",") 
  		         $ITEM_CHECK=substr($ITEM_CHECK,0,-1);
  		      $SIGN_CHECK_STR.='"'.$SIGN_ID.'":"'.$ITEM_CHECK.'",';
  		      
  		      $SIGN_OBJECT.=$SIGN_ID.","; //印章ID串
  		      $ITEM_VALUE="<div id=SIGN_POS_$SIGN_ID>&nbsp;</div>";
  		    }        
  		  }
  		  elseif($ETAG=="SELECT" && $ECLASS!="AUTO")
  		  {
  		  	$CHILD=$ELEMENT_ARR["CHILD"];
  		  	if($CHILD)
  		  	{
  		  		 foreach($ELEMENT_QUERY as $ENAME1 => $ELEMENT_QUERY_ARR)
  		       {
  		         $ETITLE1=$ELEMENT_QUERY_ARR["TITLE"];
  		         $ECLASS1=$ELEMENT_QUERY_ARR["CLASS"];
  		         $ITEM_ID1=$ELEMENT_QUERY_ARR["ITEM_ID"];
  		         $ETAG1=$ELEMENT_QUERY_ARR["TAG"];
  		         
  		         if($ECLASS1=="DATE" || $ECLASS1=="USER")
  		            continue;    
  		  
  		         if(find_id($CHILD,$ETITLE1) && $ETAG1=="SELECT")
  		         {
  		         	 $ITEM_CHILD = "DATA_".$ITEM_ID1;
  		           $$ITEM_CHILD = substr($$ITEM_CHILD,0,strpos($$ITEM_CHILD,"|"));
  		         }     
  		       }
  		  	}
  		  }
  		  else //--- 普通控件 ---
  		  {
  		     if($ECLASS=="AUTO" && $ITEM_VALUE=="{MACRO}")
  		        $ITEM_VALUE="";
  		
  		     if($ETAG != "TEXTAREA" && $ERICH != "1")
  		     {
      		     $ITEM_VALUE=str_replace("<","&lt",$ITEM_VALUE);
      		     $ITEM_VALUE=str_replace(">","&gt",$ITEM_VALUE);
      		     $ITEM_VALUE=stripslashes($ITEM_VALUE);
      		     $ITEM_VALUE=str_replace(chr(10),"<br>",$ITEM_VALUE);
      		     $ITEM_VALUE=str_replace(" ","&nbsp;",$ITEM_VALUE);
  		     }
  		
  		     if($ETAG=="INPUT" && stristr($ELEMENT,"type=checkbox"))
  		     {
  		       if($ITEM_VALUE=="on")
  		          $ITEM_VALUE="<input type=checkbox checked onclick='this.checked=1;'>";
  		       else
  		          $ITEM_VALUE="<input type=checkbox onclick='this.checked=0;'>";
  		     }
  		  }
  		
  		  if(!$WORD  && $ELASS != "DATE" && $ELASS != "USER" && $ELASS != "DATA" && $ELASS != "FETCH" && ($ETAG != "TEXTAREA" && $ERICH != "1"))
  		     $ITEM_VALUE.="<input type=hidden name=DATA_".$ITEM_ID." value='$SIGN_VALUE' title='$ETITLE'>\n";

  		  //-- 找到代换位置进行代换 --
  		  $PRINT_MODEL = str_replace('{'.$ENAME.'}',$ITEM_VALUE,$PRINT_MODEL);
  		}
  	}
    $OUTPUT.=$PRINT_MODEL."<br>";
  } //form_view1

  //----- 会签 -----
  if(strstr($FLOW_VIEW,"3"))
    $OUTPUT.=personal_sign($RUN_ID,$FLOW_ID,$FLOW_TYPE);
  
  //----- 流程图 -----
  if(strstr($FLOW_VIEW,"4"))
    $OUTPUT.=flow_view($RUN_ID,$WORD);

  return '<form name="form1" method="post" action="">'.$OUTPUT.'</form>';
}

function personal_sign($RUN_ID,$FLOW_ID,$FLOW_TYPE)
{
	global $connection;
	$SIGNLOOK_ARRAY=array();
	$PRCS_ID_ARRAY=array();
	$RUN_ID = intval($RUN_ID);
	$FLOW_ID = intval($FLOW_ID);
	$query = "SELECT PRCS_ID,FLOW_PRCS,USER_ID from FLOW_RUN_PRCS WHERE RUN_ID='$RUN_ID'";
	$cursor= exequery($connection,$query);
	while($ROW=mysql_fetch_array($cursor))
	{
		$PRCS_ID1=$ROW["PRCS_ID"];
		$FLOW_PRCS1=$ROW["FLOW_PRCS"];
		$USER_ID=$ROW["USER_ID"];

		$query1 = "SELECT PRCS_NAME,SIGNLOOK from FLOW_PROCESS WHERE FLOW_ID='$FLOW_ID' AND PRCS_ID='$FLOW_PRCS1'";
		$cursor1= exequery($connection,$query1);
		if($ROW=mysql_fetch_array($cursor1))
		{
			$PRCS_NAME=$ROW["PRCS_NAME"];
			$SIGNLOOK=$ROW["SIGNLOOK"];
		}

		//固定流程检查会签意可见性
		if($FLOW_TYPE==1)
			$SIGNLOOK_ARRAY[$PRCS_ID1]=$SIGNLOOK;

		if($PRCS_ID_ARRAY[$PRCS_ID1]=="")
			$PRCS_ID_ARRAY[$PRCS_ID1]=$PRCS_NAME;
		elseif($PRCS_ID_ARRAY[$PRCS_ID1]!=$PRCS_NAME) //并发
		$PRCS_ID_ARRAY[$PRCS_ID1].=",".$PRCS_NAME;
	}

	$query = "SELECT * from FLOW_RUN_FEEDBACK where RUN_ID='$RUN_ID' order by PRCS_ID,EDIT_TIME";
	$cursor= exequery($connection,$query);
	$FEEDBACK_COUNT=0;
	while($ROW=mysql_fetch_array($cursor))
	{
		$FEEDBACK_COUNT++;

		if($FEEDBACK_COUNT==1)
			$OUTPUT.="<table style='border-collapse:collapse' border=1 cellspacing=0 cellpadding=3 bordercolor='#000000' width='100%' class='small'><tr class='TableHeader'><td>"._("会签与点评")."</td></tr>";

		$USER_ID=$ROW["USER_ID"];
		$PRCS_ID1=$ROW["PRCS_ID"];
		$CONTENT=$ROW["CONTENT"];
		$ATTACHMENT_ID=$ROW["ATTACHMENT_ID"];
		$ATTACHMENT_NAME=$ROW["ATTACHMENT_NAME"];
		$EDIT_TIME=$ROW["EDIT_TIME"];
		$FEED_FLAG=$ROW["FEED_FLAG"];
		$CONTENT=UBB2XHTML($CONTENT);
		//$CONTENT=htmlspecialchars($CONTENT);

		$SEE_SIGN=1;
		if($FLOW_TYPE==1)
		{
			//判断当前用户是否为此步骤实际经办人
			$query1 = "SELECT * from FLOW_RUN_PRCS WHERE RUN_ID='$RUN_ID' AND PRCS_ID='$PRCS_ID1' AND USER_ID='$LOGIN_USER_ID'";
			$cursor1 = exequery($connection,$query1);
			if($ROW1=mysql_fetch_array($cursor1))
				$IS_PROC_USER=1;
			else
				$IS_PROC_USER=0;

			if($SIGNLOOK_ARRAY[$PRCS_ID1]==2)
			{
				if($IS_PROC_USER==0) $SEE_SIGN=0;
			}
			else if($SIGNLOOK_ARRAY[$PRCS_ID1]==1)
			{
				if($IS_PROC_USER==1 && $USER_ID!=$LOGIN_USER_ID) $SEE_SIGN=0;
			}
		}

		if($SEE_SIGN)
		{
			$query1 = "SELECT USER_NAME,DEPT_ID from USER where USER_ID='$USER_ID'";
			$cursor1= exequery($connection,$query1);
			if($ROW=mysql_fetch_array($cursor1))
			{
				$USER_NAME=$ROW["USER_NAME"];
				$DEPT_ID=$ROW["DEPT_ID"];
				$DEPT_NAME=dept_long_name($DEPT_ID);
			}
			else
				$USER_NAME=$USER_ID;
			 
			if($FEED_FLAG==1) $FEED_NAME="<font color=red>"._("点评")."</font>";
			else $FEED_NAME=_("会签");

			$OUTPUT.="<tr class='TableData'><td><b>";

			if($PRCS_ID1!=0)
				$OUTPUT.= sprintf(_("第%s步【%s】%s"),$PRCS_ID1,$FEED_NAME,$PRCS_ID_ARRAY[$PRCS_ID1]);
			$OUTPUT.= _(" <u title='部门：$DEPT_NAME' style='cursor:hand'>$USER_NAME</u>：</b><br>$CONTENT <i>$EDIT_TIME</i><br></td></tr>");
		}
	}//while

	if($FEEDBACK_COUNT>0)
		$OUTPUT.="</table><br> ";

	return $OUTPUT;
}//function

$RUN_ID = intval($RUN_ID);
$PRCS_ID = intval($PRCS_ID);
$FLOW_ID = intval($FLOW_ID);
$RUN_ROLE = run_role($RUN_ID,$PRCS_ID);
ob_clean();

if(!$RUN_ROLE)
{
	echo "NOREADFLOWPRIV";
	exit;
}

$CONTENT=export_flow($RUN_ID);
if(strstr($CONTENT,"#[MACRO_TIMEOUT"))
{
	$CONTENT=getTimeout($RUN_ID,$CONTENT);
}
//$CONTENT = stripslashes($CONTENT);
$CONTENT = str_replace("&lt", "<", $CONTENT);
$CONTENT = str_replace("&gt", ">", $CONTENT);
$CONTENT_STRIP = mysql_escape_string(strip_tags($CONTENT));
$COMPRESS_CONTENT = bin2hex(gzcompress($CONTENT));
?>
<div class="container">
<?=$CONTENT?>
</div>