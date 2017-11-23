<?
include_once("../header.php");
include_once("inc/utility_all.php");
include_once("inc/utility_flow.php");
include_once("inc/utility_file.php");
include_once("run_role.php");
ob_clean();
$RUN_ID = intval($RUN_ID);
$PRCS_ID = intval($PRCS_ID);
$FLOW_ID = intval($FLOW_ID);
$FLOW_PRCS = intval($FLOW_PRCS);


$RUN_ROLE = run_role($RUN_ID,$PRCS_ID);

function flow_other_sql($PRCS_PRIV)
{
  global $connection;

  if($PRCS_PRIV=="")
     return;

  $MY_ARRAY=explode(",",$PRCS_PRIV);
  $ARRAY_COUNT=sizeof($MY_ARRAY);
  if($MY_ARRAY[$ARRAY_COUNT-1]=="")$ARRAY_COUNT--;
  for($I=0;$I<$ARRAY_COUNT;$I++)
  {
  	$USER_PRIV=$MY_ARRAY[$I];
  	$QUERY.=" or USER_PRIV_OTHER like '$USER_PRIV,%' or USER_PRIV_OTHER like '%,$USER_PRIV,%'";
  }
  return $QUERY;
}

function dept_parent($DEPT_ID,$FLAG)
{
  global $connection;
  $query = "SELECT DEPT_PARENT from DEPARTMENT where DEPT_ID='$DEPT_ID'";
  $cursor= exequery($connection,$query);
  if($ROW=mysql_fetch_array($cursor))
  {
     $DEPT_PARENT=$ROW["DEPT_PARENT"];

     if($DEPT_PARENT=="0"||$DEPT_PARENT=="")
        return $DEPT_ID;
     else
     {
        if($FLAG)
           return $DEPT_PARENT;
        else
           return dept_parent($DEPT_PARENT,$FLAG);
     }
  }
}

function sys_manager($TMP_DEPT_ID)
{
  global $connection,$LOGIN_USER_ID;
  $query = "SELECT MANAGER FROM DEPARTMENT WHERE DEPT_ID='$TMP_DEPT_ID'";
  $cursor= exequery($connection,$query);
  if($ROW=mysql_fetch_array($cursor))
    $MANAGER=$ROW["MANAGER"];
  if($MANAGER!="")
  {
    $MANAGER_ARRAY=explode(",",$MANAGER);
    $query = "SELECT USER_NAME FROM USER WHERE USER_ID='$MANAGER_ARRAY[0]'";
    $cursor= exequery($connection,$query);
    if($ROW=mysql_fetch_array($cursor))
      $AUTO_VALUE=$ROW["USER_NAME"];
  }
  else
  {
    $query = "SELECT USER_ID,USER_NAME,USER_PRIV.USER_PRIV from USER,USER_PRIV where USER.USER_PRIV=USER_PRIV.USER_PRIV and DEPT_ID='$TMP_DEPT_ID' and USER_ID!='$LOGIN_USER_ID' order by PRIV_NO,USER_NO,USER_NAME LIMIT 1";
    $cursor= exequery($connection,$query);
    if($ROW=mysql_fetch_array($cursor))
      $AUTO_VALUE=$ROW["USER_NAME"];
  }
  return $AUTO_VALUE;
}
if(!find_id($RUN_ROLE,2) && !find_id($RUN_ROLE,4))
{
   echo "NOEDITPRIV";
   exit;
}
?>
<div class="container">
   <div class="tform tformshow">
<?
   $CUR_TIME1=date("H:i:s",time());
   $CUR_DATE=date("Y-m-d");
   $CUR_TIME=$CUR_DATE." ".$CUR_TIME1;
   $query = "SELECT FORM_ID,FLOW_TYPE,FLOW_NAME,FLOW_DOC from FLOW_TYPE WHERE FLOW_ID='$FLOW_ID'";
   $cursor1= exequery($connection,$query);
   if($ROW=mysql_fetch_array($cursor1))
   {
       $FORM_ID=$ROW["FORM_ID"];
       $FLOW_TYPE=$ROW["FLOW_TYPE"];
       $FLOW_NAME=$ROW["FLOW_NAME"];
       $FLOW_DOC=$ROW["FLOW_DOC"];
      
       $query = "SELECT PARENT,TOP_FLAG,FREE_ITEM,PRCS_FLAG,OP_FLAG from FLOW_RUN_PRCS WHERE RUN_ID='$RUN_ID' AND PRCS_ID='$PRCS_ID' AND USER_ID='$LOGIN_USER_ID' AND FLOW_PRCS='$FLOW_PRCS' ORDER BY PRCS_FLAG LIMIT 1";
       $cursor= exequery($connection,$query);
       if($ROW=mysql_fetch_array($cursor))
       {
          $PRCS_FLAG=$ROW["PRCS_FLAG"];
          $TOP_FLAG=$ROW["TOP_FLAG"];
          $PARENT=$ROW["PARENT"];
          $FREE_ITEM=$ROW["FREE_ITEM"];
          $OP_FLAG=$ROW["OP_FLAG"];
       }
   }
   if($PRCS_FLAG == 1)
   {
       $query = "update FLOW_RUN_PRCS set PRCS_FLAG='2',PRCS_TIME='$CUR_TIME' WHERE USER_ID='$LOGIN_USER_ID' AND RUN_ID='$RUN_ID' AND PRCS_ID='$PRCS_ID' AND FLOW_PRCS='$FLOW_PRCS' AND PRCS_FLAG='$PRCS_FLAG'";
   	   exequery($connection,$query);
   	   
   	   //处理后接收者为从斑
   	   if($TOP_FLAG==1 && $OP_FLAG==1)
   	   {
   	   	$query = "update FLOW_RUN_PRCS set OP_FLAG=0 WHERE USER_ID<>'$LOGIN_USER_ID' AND RUN_ID='$RUN_ID' AND PRCS_ID='$PRCS_ID' AND FLOW_PRCS='$FLOW_PRCS'";
   	   	exequery($connection,$query);
   	   }
   }
   $PRCS_ID1=$PRCS_ID-1;
   $query = "update FLOW_RUN_PRCS set PRCS_FLAG='4' WHERE RUN_ID='$RUN_ID' AND PRCS_ID='$PRCS_ID1'";
   if($PARENT!="0" && $PARENT!="")
      $query.=" AND FLOW_PRCS IN ($PARENT)";
   exequery($connection,$query);
   
   //--- 文号 开始时间 ---
   $query1 = "SELECT * from FLOW_RUN where RUN_ID='$RUN_ID'";
   $cursor1 = exequery($connection,$query1);
   if($ROW=mysql_fetch_array($cursor1))
   {
      $RUN_NAME=$ROW["RUN_NAME"];
      $ATTACHMENT_ID = $ROW["ATTACHMENT_ID"];
      $ATTACHMENT_NAME = $ROW["ATTACHMENT_NAME"];
      $BEGIN_TIME = $ROW["BEGIN_TIME"];
      $DEL_FLAG=$ROW["DEL_FLAG"];
   }
?>
   <div class="read_detail">
      <em><?=_("名称/文号：")?></em><?=$RUN_NAME?>
   </div>
   <div class="read_detail">
      <em><?=_("流水号：")?></em><?=$RUN_ID?>
   </div>
   <div class="read_detail">
      <em><?=_("流程开始：")?></em><?=$BEGIN_TIME?>
   </div>
   <div class="read_detail endline">
      <em><?=_("流程开始：")?></em><?=$BEGIN_TIME?>
   </div>
 </div>
 <div class="tform">
   <form action="edit_submit.php" method="post" name="form1" id="edit_from" onsubmit="return false;">
   <?
   
   //-----------判断字段对于当前用户是否为隐藏----------------
   $query="select HIDDEN_ITEM from FLOW_PROCESS,FLOW_RUN_PRCS where FLOW_PROCESS.FLOW_ID='$FLOW_ID' and FLOW_RUN_PRCS.RUN_ID='$RUN_ID' and FLOW_RUN_PRCS.USER_ID='$LOGIN_USER_ID' and FLOW_PROCESS.PRCS_ID=FLOW_RUN_PRCS.PRCS_ID";
   $cursor=exequery($connection,$query);
   $HIDDEN_STR="";
   while($ROW=mysql_fetch_array($cursor))
   {
   	$HIDDEN_STR.=$ROW["HIDDEN_ITEM"];
   }
   $query = "SELECT * from FLOW_PROCESS WHERE FLOW_ID='$FLOW_ID' AND PRCS_ID='$FLOW_PRCS'";
   $cursor1= exequery($connection,$query);
   if($ROW=mysql_fetch_array($cursor1))
   {
   	$PRCS_NAME              = $ROW["PRCS_NAME"];
       $PRCS_ITEM              = $ROW["PRCS_ITEM"];
       $PRCS_ITEM_AUTO         = $ROW["PRCS_ITEM_AUTO"];
       $FEEDBACK               = $ROW["FEEDBACK"];
       $ALLOW_BACK             = $ROW["ALLOW_BACK"];
   }
   //判断是否允许回退
   if($FLOW_TYPE == 1)
   {
      if($ALLOW_BACK)
      {
         $BACK_FLAG=1;
	   	 $query = "SELECT 1 FROM FLOW_RUN_PRCS WHERE RUN_ID='$RUN_ID' AND PRCS_ID='$PRCS_ID' AND OP_FLAG='1' AND FLOW_PRCS<>'$FLOW_PRCS' AND PARENT='$PARENT'";
	   	 $cursor = exequery($connection,$query);
	   	 if(mysql_fetch_array($cursor))
	   	    $BACK_FLAG=0;
	  }
   }
   
   $table_name = 'flow_data_'.$FLOW_ID;
   $query = " select * from $table_name where run_id='$RUN_ID' limit 1";
   $cursor = exequery($connection,$query);
   if($ROW=mysql_fetch_assoc($cursor))
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
   
   include_once("inc/workflow_form.php");
   //print_r($ELEMENT_ARRAY);exit;
   $CUR_DATE=date("Y-m-d");
   $I = 0;
   $ITEM_COUNT = count($ELEMENT_ARRAY);
   foreach($ELEMENT_ARRAY as $ENAME => $ELEMENT_ARR)
   {
       //--- 默认值 ---
       $ECLASS = $ELEMENT_ARR["CLASS"];  
       $ITEM_ID = $ELEMENT_ARR["ITEM_ID"];
       $EVALUE = $ELEMENT_ARR["VALUE"]; 
       $ETITLE = $ELEMENT_ARR["TITLE"]; 
       //$ETAG = $ELEMENT_ARR["TAG"]; 
       $ETAG = strtoupper($ELEMENT_ARR["TAG"]);
       $ITEM_ID = $ELEMENT_ARR["ITEM_ID"];
       $ELEMENT_OUT = $ELEMENT_ARR["CONTENT"];
       $ETYPE = $ELEMENT_ARR["TYPE"];
       $ETITLE=str_ireplace("<","&lt",$ETITLE);
       $ETITLE=str_ireplace(">","&gt",$ETITLE);
       $ETITLE=stripslashes($ETITLE);
       //-------判断不可写字段------------
       if(!$OP_FLAG || (($FLOW_TYPE==2 && $FREE_ITEM!="" && !find_id($FREE_ITEM,$ETITLE)) || ($FLOW_TYPE==1 && !find_id($PRCS_ITEM,$ETITLE))) && $ECLASS!="DATE" && $ECLASS!="USER" )
       	$READ_ONLY=1;
       else
       	$READ_ONLY=0;
       
       if(($FLOW_TYPE==2 && $FREE_ITEM!="" && !find_id($FREE_ITEM,$ETITLE)) || ($ECLASS=="DATE" || $ECLASS=="USER"))
          continue;
       
       $STR="DATA_".$ITEM_ID;
       $ITEM_VALUE=$$STR;
       
       if(find_id($HIDDEN_STR,$ETITLE))
          $ITEM_VALUE="";
       
       if($ITEM_VALUE=="{MACRO}")
          $ITEM_VALUE="";
   
       if($ETAG=="INPUT")
       {
           //复选框
       	  if($ETYPE == "checkbox")
          {
               //去掉value属性
            	$ELEMENT_OUT=str_ireplace(' value="on"',"",$ELEMENT_OUT);
            	$ELEMENT_OUT=str_ireplace(' value=""',"",$ELEMENT_OUT);
            	if(!$DEBUG_MODE)
            	{
            	    $ELEMENT_OUT=str_ireplace(" CHECKED","",$ELEMENT_OUT);
                }
            	$ELEMENT_OUT=str_ireplace(' checked="checked"',"",$ELEMENT_OUT);
            
            	if($ITEM_VALUE=="on")
            	{
                    $ELEMENT_OUT=str_ireplace("<$ETAG","<$ETAG CHECKED",$ELEMENT_OUT);
                }
           }
           else
           {
               //隐藏属性           
                $HIDDEN = $ELEMENT_ARR["HIDDEN"];
                $HIDDEN_PROP = "";
                if($HIDDEN=="1")
                    $HIDDEN_PROP = "type=hidden";
                $ELEMENT_OUT=str_ireplace("value=$EVALUE","",$ELEMENT_OUT);
                $ELEMENT_OUT=str_ireplace("<$ETAG","<$ETAG value=\"$ITEM_VALUE\" $HIDDEN_PROP",$ELEMENT_OUT);             
           }
       }
       else if($ETAG=="TEXTAREA")
       {
           $ELEMENT_OUT=str_ireplace(">$EVALUE<",">\n$ITEM_VALUE<",$ELEMENT_OUT);
       }
       else if($ETAG=="SELECT" && $ECLASS!="AUTO")
       {
       	if($ITEM_VALUE!="")
       	{
          	    $ELEMENT_OUT=str_ireplace(" selected","",$ELEMENT_OUT);
          	    $ELEMENT_OUT=str_ireplace("<OPTION value=$ITEM_VALUE>","<OPTION selected value=\"$ITEM_VALUE\">",$ELEMENT_OUT);
          	    $ELEMENT_OUT=str_ireplace("<OPTION value=\"$ITEM_VALUE\">","<OPTION selected value=\"$ITEM_VALUE\">",$ELEMENT_OUT);
           }    
       }
       if($ECLASS=="RADIO" && $ETAG=="IMG") //textfield
       {
       	   $RADIO_FIELD=$ELEMENT_ARR["RADIO_FIELD"];
           $RADIO_CHECK=$ELEMENT_ARR["RADIO_CHECK"];
           $RADIO_ARRAY = explode("`",rtrim($RADIO_FIELD,"`"));
           
       		$ELEMENT_OUT = "";
            if($ITEM_VALUE!="")
                $RADIO_CHECK = $ITEM_VALUE;
            
            $DISABLED = ($READ_ONLY == 1) ? "disabled" : "";
            foreach($RADIO_ARRAY as $RADIO)
            {
                $CHECKED = "";
                if($RADIO == $RADIO_CHECK)
                    $CHECKED = "checked";
                $ELEMENT_OUT .= '<input type="radio" title="'.$ETITLE.'" name="'.$ENAME.'" value="'.$RADIO.'" '.$CHECKED.' '.$DISABLED .'><label>'.$RADIO.'</label>&nbsp;'; 
            }
       }
       //进度条
       if($ECLASS=="PROGRESSBAR")
       {
       	$ELEMENT_OUT = "";
       }
       if($ECLASS=="IMGUPLOAD")
       {
       	$ELEMENT_OUT = "";
       }
       if($ECLASS=="QRCODE")
       {
       	$ELEMENT_OUT = "";
       }
       //------------------------------------ 特殊控件：日期、计算、宏、列表控件 -----------------------------
       if($ECLASS=="DATE")  //日历控件
       {
       	$ELEMENT_OUT = "";
       }
       if($ECLASS=="USER")  //部门人员控件
       {
       	$ELEMENT_OUT = "";
       }
       elseif($ECLASS=="CALC")  //计算控件
       {
       	$ELEMENT_OUT = "";
       }
       elseif($ECLASS=="AUTO")  // 宏控件
       {
           $EDATAFLD=$ELEMENT_ARR["DATAFLD"];
           $AUTO_VALUE="";
           if($ETAG=="INPUT") // 宏控件单行输入框
           {
               switch($EDATAFLD)
               {
                  case "SYS_DATE":
                                $AUTO_VALUE=$CUR_DATE;
                                break;
                  case "SYS_DATE_CN":
                                $AUTO_VALUE=format_date($CUR_DATE);
                                break;
                  case "SYS_DATE_CN_SHORT1":
                                $AUTO_VALUE=format_date_short1($CUR_DATE);
                                break;
                  case "SYS_DATE_CN_SHORT2":
                                $AUTO_VALUE=format_date_short2($CUR_DATE);
                                break;
                  case "SYS_DATE_CN_SHORT3":
                                $AUTO_VALUE=format_date_short3($CUR_DATE);
                                break;
                  case "SYS_DATE_CN_SHORT4":
                                $AUTO_VALUE=date("Y",time());
                                break;
                  case "SYS_TIME":
                                $AUTO_VALUE=$CUR_TIME1;
                                break;
                  case "SYS_DATETIME":
                                $AUTO_VALUE=$CUR_TIME;
                                break;
                  case "SYS_WEEK":
                                $AUTO_VALUE=get_week($CUR_TIME);
                                break;
                  case "SYS_USERID":
                                $AUTO_VALUE=$LOGIN_USER_ID;
                                break;
                  case "SYS_USERNAME":
                                $query_auto="SELECT USER_NAME from USER where USER_ID='$LOGIN_USER_ID'";
                                $cursor_auto = exequery($connection,$query_auto);
                                if($ROW=mysql_fetch_array($cursor_auto))
                                   $AUTO_VALUE=$ROW["USER_NAME"];
                                break;
                  case "SYS_USERPRIV":
                                $query_auto="SELECT PRIV_NAME from USER_PRIV where USER_PRIV='$LOGIN_USER_PRIV'";
                                $cursor_auto = exequery($connection,$query_auto);
                                if($ROW=mysql_fetch_array($cursor_auto))
                                   $AUTO_VALUE=$ROW["PRIV_NAME"];
                                break;
                  case "SYS_USERNAME_DATE":
                                $query_auto="SELECT USER_NAME from USER where USER_ID='$LOGIN_USER_ID'";
                                $cursor_auto = exequery($connection,$query_auto);
                                if($ROW=mysql_fetch_array($cursor_auto))
                                   $AUTO_VALUE=$ROW["USER_NAME"]." ".$CUR_DATE;
                                break;
                  case "SYS_USERNAME_DATETIME":
                                $query_auto="SELECT USER_NAME from USER where USER_ID='$LOGIN_USER_ID'";
                                $cursor_auto = exequery($connection,$query_auto);
                                if($ROW=mysql_fetch_array($cursor_auto))
                                   $AUTO_VALUE=$ROW["USER_NAME"]." ".$CUR_TIME;
                                break;
                  case "SYS_DEPTNAME":
                                $AUTO_VALUE=dept_long_name($LOGIN_DEPT_ID);
                                break;
                  case "SYS_DEPTNAME_SHORT":
                                $query_auto="SELECT DEPT_NAME from DEPARTMENT where DEPT_ID='$LOGIN_DEPT_ID'";
                                $cursor_auto = exequery($connection,$query_auto);
                                if($ROW=mysql_fetch_array($cursor_auto))
                                   $AUTO_VALUE=$ROW["DEPT_NAME"];
                                break;
                  case "SYS_FORMNAME":
                                $AUTO_VALUE=$FORM_NAME;
                                break;
                  case "SYS_RUNNAME":
                                $AUTO_VALUE=$RUN_NAME;
                                break;
                  case "SYS_RUNDATE":
                                $AUTO_VALUE=$PRCS_DATE;
                                break;
                  case "SYS_RUNDATETIME":
                                $AUTO_VALUE=$BEGIN_TIME;
                                break;
                  case "SYS_RUNID":
                                $AUTO_VALUE=$RUN_ID;
                                break;
                  case "SYS_AUTONUM":
                                $AUTO_VALUE=$AUTO_NUM;
                                break;
                  case "SYS_IP":
                                $AUTO_VALUE=get_client_ip();
                                break;
                  case "SYS_SQL":
                                $query_auto="select PRIV_NO from USER_PRIV where USER_PRIV='$LOGIN_USER_PRIV'";
                                $cursor_auto=exequery($connection,$query_auto);
                                if($ROW=mysql_fetch_array($cursor_auto))
                                   $LOGIN_USER_PRIV_NO=$ROW["PRIV_NO"];
               
                                $EDATASRC=$ELEMENT_ARR["DATASRC"];
                                $EDATASRC=str_ireplace("`","'",$EDATASRC);
                                $EDATASRC=str_ireplace("&#13;&#10;"," ",$EDATASRC);
                                $EDATASRC=str_ireplace("[SYS_USER_ID]",$LOGIN_USER_ID,$EDATASRC);
                                $EDATASRC=str_ireplace("[SYS_DEPT_ID]",$LOGIN_DEPT_ID,$EDATASRC);
                                $EDATASRC=str_ireplace("[SYS_PRIV_ID]",$LOGIN_USER_PRIV,$EDATASRC);
                                $EDATASRC=str_ireplace("[SYS_PRIV_NO]",$LOGIN_USER_PRIV_NO,$EDATASRC);
                                $EDATASRC=str_ireplace("[SYS_RUN_ID]",$RUN_ID,$EDATASRC);
                                
                                $cursor_SYS_SQL = exequery($connection,$EDATASRC);
                                if($ROW=mysql_fetch_array($cursor_SYS_SQL))
                                   $AUTO_VALUE=$ROW[0];
                                break;
                   case "SYS_MANAGER1":
                                $TMP_DEPT_ID=$LOGIN_DEPT_ID;
                                $AUTO_VALUE=sys_manager($TMP_DEPT_ID);
                                break;
                   case "SYS_MANAGER2":
                                $TMP_DEPT_ID=dept_parent($LOGIN_DEPT_ID,1);
                                $AUTO_VALUE=sys_manager($TMP_DEPT_ID);
                                break;
                   case "SYS_MANAGER3":
                                $TMP_DEPT_ID=dept_parent($LOGIN_DEPT_ID,0);
                                $AUTO_VALUE=sys_manager($TMP_DEPT_ID);
                                break;
               }
           //--- 宏控件单行输入框的自动赋值，数据库为空值且为可写字段时将自动取值，或者是设定为允许在非可写状态下赋值的宏控件(不管是否为空，都自动赋值) ---
                if(($ITEM_VALUE=="" && !$READ_ONLY)||($READ_ONLY && find_id($PRCS_ITEM_AUTO,$ETITLE) && $OP_FLAG))
                {
                    $ELEMENT_OUT = preg_replace("/value\s?=\s?\"?$EVALUE\"?/i","",$ELEMENT_OUT);
                    $ELEMENT_OUT=str_ireplace("<$ETAG","<$ETAG value=\"$AUTO_VALUE\"",$ELEMENT_OUT);
//                    $ELEMENT_OUT=str_ireplace("value=$EVALUE","",$ELEMENT_OUT);
//                    $ELEMENT_OUT=str_ireplace("value='$EVALUE'","",$ELEMENT_OUT);
//                    $ELEMENT_OUT=str_ireplace("value=''","",$ELEMENT_OUT);
//                    $ELEMENT_OUT=str_ireplace("<$ETAG","<$ETAG value='$AUTO_VALUE'",$ELEMENT_OUT);
                }
               if(find_id($PRCS_ITEM_AUTO,$ETITLE))
                    $ELEMENT_OUT=str_ireplace("<$ETAG","<$ETAG readOnly value='$AUTO_VALUE'",$ELEMENT_OUT);
           }    
           elseif($ETAG=="SELECT") // 宏控件下拉菜单
           {
               $AUTO_VALUE="<option value=\"\"";
               if($ITEM_VALUE=="")
                   $AUTO_VALUE.=" selected";
               $AUTO_VALUE.="></option>\n";
               $POS=strpos($ELEMENT_OUT,">")+1;
               $POS1=strpos($ELEMENT_OUT,"</SELECT>",$POS);
               $EVALUE=substr($ELEMENT_OUT,$POS,$POS1-$POS);  
               $ITEM_VALUE_TEXT="";
               switch($EDATAFLD)
               {
                   case "SYS_LIST_DEPT":
                       $AUTO_VALUE.=my_dept_tree(0,$ITEM_VALUE,0);
                       if($ITEM_VALUE!="")
                       {
                          $query_auto="SELECT DEPT_NAME from DEPARTMENT where DEPT_ID='$ITEM_VALUE'";
                          $cursor_auto = exequery($connection,$query_auto);
                          if($ROW=mysql_fetch_array($cursor_auto))
                             $ITEM_VALUE_TEXT=$ROW["DEPT_NAME"];
                       }
                       break;
                   case "SYS_LIST_USER":
                       $query_auto="SELECT USER_ID,USER_NAME from USER,USER_PRIV where USER.USER_PRIV=USER_PRIV.USER_PRIV order by PRIV_NO,USER_NO,USER_NAME";
                       $cursor_auto = exequery($connection,$query_auto);
                       while($ROW=mysql_fetch_array($cursor_auto))
                       {
                          $USER_ID=$ROW["USER_ID"];
                          $USER_NAME=$ROW["USER_NAME"];
                          $AUTO_VALUE.="<option value=\"$USER_ID\"";
                          if($ITEM_VALUE==$USER_ID)
                          {
                             $AUTO_VALUE.=" selected";
                             $ITEM_VALUE_TEXT=$USER_NAME;
                          }
                          $AUTO_VALUE.=">$USER_NAME</option>\n";
                       }
                       break;
                  case "SYS_LIST_PRIV":
                       $query_auto="SELECT USER_PRIV,PRIV_NAME from USER_PRIV order by PRIV_NO";
                       $cursor_auto = exequery($connection,$query_auto);
                       while($ROW=mysql_fetch_array($cursor_auto))
                       {
                          $USER_PRIV=$ROW["USER_PRIV"];
                          $PRIV_NAME=$ROW["PRIV_NAME"];
                          $AUTO_VALUE.="<option value=\"$USER_PRIV\"";
                          if($ITEM_VALUE==$USER_PRIV)
                          {
                             $AUTO_VALUE.=" selected";
                             $ITEM_VALUE_TEXT=$PRIV_NAME;
                          }
                          $AUTO_VALUE.=">$PRIV_NAME</option>\n";
                       }
                       break;
               	case "SYS_LIST_PRIV_ONLY":
               	     $query_auto="SELECT USER_PRIV,PRIV_NAME from USER_PRIV order by PRIV_NO";
               	     $cursor_auto = exequery($connection,$query_auto);
               	     while($ROW=mysql_fetch_array($cursor_auto))
               	     {
               	     	$USER_PRIV=$ROW["USER_PRIV"];
               	     	$PRIV_NAME=$ROW["PRIV_NAME"];
               	     		 
               	     	$USER_COUNT = 0;
               	     	$query1 = "SELECT count(*) from USER where USER_PRIV='$USER_PRIV'";
               	     	$cursor1= exequery($connection,$query1);
               	     	if($ROW1=mysql_fetch_array($cursor1))
               	     		$USER_COUNT=$ROW1[0];
               	     	if($USER_COUNT == 0)
               	     		continue;
               	     		 
               	     	$AUTO_VALUE.="<option value=\"$PRIV_NAME\"";
               	     	if($ITEM_VALUE==$PRIV_NAME)
               	     	{
               	     		$AUTO_VALUE.=" selected";
               	     		$ITEM_VALUE_TEXT=$PRIV_NAME;
               	     	}
               	     	$AUTO_VALUE.=">$PRIV_NAME</option>\n";
               	     }
               	     break;
                	case "SYS_LIST_PRIV_OTHER":
                	    $query_auto="SELECT USER_PRIV,PRIV_NAME from USER_PRIV order by PRIV_NO";
                	    $cursor_auto = exequery($connection,$query_auto);
                	    while($ROW=mysql_fetch_array($cursor_auto))
                	    {
                	    	$USER_PRIV=$ROW["USER_PRIV"];
                	    	$PRIV_NAME=$ROW["PRIV_NAME"];
                	    	$query1 = "SELECT count(*) from USER where FIND_IN_SET('$USER_PRIV',USER_PRIV_OTHER)";
                	    	$cursor1= exequery($connection,$query1);
                	    	if($ROW1=mysql_fetch_array($cursor1))
                	    		$USER_COUNT=$ROW1[0];
                	    	if($USER_COUNT == 0)
                	    		continue;
                	    	$AUTO_VALUE.="<option value=\"$PRIV_NAME\"";
                	    	if($ITEM_VALUE==$PRIV_NAME)
                	    	{
                	    		$AUTO_VALUE.=" selected";
                	    		$ITEM_VALUE_TEXT=$PRIV_NAME;
                	    	}
                	    	$AUTO_VALUE.=">$PRIV_NAME</option>\n";
                	    }
                	    break;
                  case "SYS_LIST_PRCSUSER1":
                       $query_auto = "select PRCS_USER,PRCS_DEPT,PRCS_PRIV from FLOW_PROCESS where FLOW_ID='$FLOW_ID' order by PRCS_ID";
                       $cursor_auto=exequery($connection,$query_auto);
                       $PRCS_USER="";
                       $PRCS_DEPT="";
                       $PRCS_PRIV="";
                       $PRCS_DEPT_ALL="";
                       while($ROW=mysql_fetch_array($cursor_auto))
                       {
                       	  if($ROW["PRCS_USER"]!="")
                             $PRCS_USER.=$ROW["PRCS_USER"];
                          if($ROW["PRCS_DEPT"]!="" && $ROW["PRCS_DEPT"]!="ALL_DEPT")
                             $PRCS_DEPT.=$ROW["PRCS_DEPT"];
                          elseif($ROW["PRCS_DEPT"]=="ALL_DEPT")
                             $PRCS_DEPT_ALL="ALL_DEPT";
                          if($ROW["PRCS_PRIV"]!="")
                             $PRCS_PRIV.=$ROW["PRCS_PRIV"];
                       }
               
                       $query_auto = "SELECT USER_ID,USER_NAME from USER,USER_PRIV where USER.USER_PRIV=USER_PRIV.USER_PRIV AND NOT_LOGIN=0 AND (";
                       if($PRCS_DEPT && $PRCS_DEPT_ALL!="ALL_DEPT")
                       	  $query_auto .= "FIND_IN_SET(USER.DEPT_ID,'$PRCS_DEPT')";
                       elseif($PRCS_DEPT_ALL=="ALL_DEPT")
                       	  $query_auto .= "1=1";
                       else
                         	$query_auto .= "1=0";
                       if($PRCS_USER)
                       	  $query_auto .= " or FIND_IN_SET(USER.USER_ID,'$PRCS_USER')";
                       if($PRCS_PRIV)
                         	$query_auto .= " or FIND_IN_SET(USER.USER_PRIV,'$PRCS_PRIV')".flow_other_sql($PRCS_PRIV);
               
                       $query_auto .= ") order by PRIV_NO,USER_NO,USER_NAME";
                       $cursor_auto=exequery($connection,$query_auto);
                       while($ROW=mysql_fetch_array($cursor_auto))
                       {
                         $USER_ID=$ROW["USER_ID"];
                         $USER_NAME=$ROW["USER_NAME"];
                         $AUTO_VALUE.="<option value=\"$USER_ID\"";
                         if($ITEM_VALUE==$USER_ID)
                         {
                            $AUTO_VALUE.=" selected";
                            $ITEM_VALUE_TEXT=$USER_NAME;
                         }
                         $AUTO_VALUE.=">$USER_NAME</option>\n";
                       }
                       break;
                  case "SYS_LIST_PRCSUSER2":
                                if(!$EDIT_MODE)
                                {
                                  $query_auto = "select PRCS_USER,PRCS_DEPT,PRCS_PRIV from FLOW_PROCESS where FLOW_ID='$FLOW_ID' and PRCS_ID='$FLOW_PRCS'";
                                  $cursor_auto=exequery($connection,$query_auto);
                                  $PRCS_USER="";
                                  $PRCS_DEPT="";
                                  $PRCS_PRIV="";
                                  if($ROW=mysql_fetch_array($cursor_auto))
       	 	 										 {
                                  	  if($ROW["PRCS_USER"]!="")
                                     	 $PRCS_USER=$ROW["PRCS_USER"];
               
                                     if($ROW["PRCS_DEPT"]!="" && $ROW["PRCS_DEPT"]!="ALL_DEPT")
                                     	 $PRCS_DEPT=$ROW["PRCS_DEPT"];
                                     elseif($ROW["PRCS_DEPT"]=="ALL_DEPT")
                                     	 $PRCS_DEPT="ALL_DEPT";
               
                                     if($ROW["PRCS_PRIV"]!="")
                                     	 $PRCS_PRIV=$ROW["PRCS_PRIV"];
                                  }
               
                                  if($ITEM_VALUE!="")
                                     $PRCS_USER.=$ITEM_VALUE;
               
                                  $query_auto = "SELECT USER_ID,USER_NAME from USER,USER_PRIV where USER.USER_PRIV=USER_PRIV.USER_PRIV AND NOT_LOGIN=0 AND (";
                                  if($PRCS_DEPT && $PRCS_DEPT!="ALL_DEPT")
                                  	  $query_auto .= "FIND_IN_SET(USER.DEPT_ID,'$PRCS_DEPT')";
                                  elseif($PRCS_DEPT=="ALL_DEPT")
                                  	  $query_auto .= "1=1";
                                  else
                                  	  $query_auto .= "1=0";
                                  if($PRCS_USER)
                                  	  $query_auto .= " or FIND_IN_SET(USER.USER_ID,'$PRCS_USER')";
                                  if($PRCS_PRIV)
                                  	  $query_auto .= " or FIND_IN_SET(USER.USER_PRIV,'$PRCS_PRIV')".flow_other_sql($PRCS_PRIV);
               
                                  $query_auto .= ") order by PRIV_NO,USER_NO,USER_NAME";
                                  $cursor_auto=exequery($connection,$query_auto);
                                  while($ROW=mysql_fetch_array($cursor_auto))
                                  {
                                    $USER_ID=$ROW["USER_ID"];
                                    $USER_NAME=$ROW["USER_NAME"];
                                    $AUTO_VALUE.="<option value=\"$USER_ID\"";
                                    if($ITEM_VALUE==$USER_ID)
                                    {
                                       $AUTO_VALUE.=" selected";
                                       $ITEM_VALUE_TEXT=$USER_NAME;
                                    }
                                    $AUTO_VALUE.=">$USER_NAME</option>\n";
                                  }
                                }
                                break;
                  case "SYS_LIST_SQL":
                                $EDATASRC=$ELEMENT_ARR["DATASRC"];
                                $ELEMENT_OUT=str_ireplace($EDATASRC,"",$ELEMENT_OUT);
                                
                                $EDATASRC=str_ireplace("`","'",$EDATASRC);
                                $EDATASRC=str_ireplace("&#13;&#10;"," ",$EDATASRC);
                                $EDATASRC=str_ireplace("[SYS_USER_ID]",$LOGIN_USER_ID,$EDATASRC);
                                $EDATASRC=str_ireplace("[SYS_DEPT_ID]",$LOGIN_DEPT_ID,$EDATASRC);
                                $EDATASRC=str_ireplace("[SYS_PRIV_ID]",$LOGIN_USER_PRIV,$EDATASRC);
                                $EDATASRC=str_ireplace("[SYS_PRIV_NO]",$LOGIN_USER_PRIV_NO,$EDATASRC);
                                $EDATASRC=str_ireplace("[SYS_RUN_ID]",$RUN_ID,$EDATASRC);
               
                                $cursor_SYS_SQL = exequery($connection,$EDATASRC);
                                $ITEM_VALUE_TEXT=$ITEM_VALUE;
                                while($ROW=mysql_fetch_array($cursor_SYS_SQL))
                                {
                                   $AUTO_VALUE_SQL=$ROW[0];
                                   $AUTO_VALUE.="<option value=\"$AUTO_VALUE_SQL\"";
                                   if($ITEM_VALUE==$AUTO_VALUE_SQL)
                                      $AUTO_VALUE.=" selected";
                                   $AUTO_VALUE.=">$AUTO_VALUE_SQL</option>\n";
                                }
                                break;
                     case "SYS_LIST_MANAGER1":
                           $MANAGER_ARRAY = get_dept_manager($LOGIN_DEPT_ID);
                           $AUTO_VALUE = array2list(&$MANAGER_ARRAY, $ITEM_VALUE);
                           break;
                     case "SYS_LIST_MANAGER2":
                           $PARENT_DEPT_ID = get_dept_parent($LOGIN_DEPT_ID);
                           $MANAGER_ARRAY = get_dept_manager($PARENT_DEPT_ID);
                           $AUTO_VALUE = array2list(&$MANAGER_ARRAY, $ITEM_VALUE);
                           break;
                     case "SYS_LIST_MANAGER3":
                           $TOP_DEPT_ID=get_dept_parent($LOGIN_DEPT_ID,1);
                           $MANAGER_ARRAY = get_dept_manager($TOP_DEPT_ID);
                           $AUTO_VALUE = array2list(&$MANAGER_ARRAY, $ITEM_VALUE);
                           break;
               }
               $ELEMENT_OUT=substr($ELEMENT_OUT,0,strpos($ELEMENT_OUT,">")+1).$AUTO_VALUE."</SELECT>";
           }
       }
       elseif($ECLASS=="LIST_VIEW")  //列表控件
       {
       	$ELEMENT_OUT = "";
       }
       elseif($ECLASS=="SIGN")  //签章控件
       {
       	$ELEMENT_OUT = "";
       }
       elseif($ECLASS=="DATA")  //数据选择控件
       {
       	$ELEMENT_OUT = "";
       }
       elseif($ECLASS=="FETCH")  //数据获取控件
       {
       	$ELEMENT_OUT = "";
       }
       
       if($READ_ONLY)
       {
       	//3.3之前版本所有签章存在同一个字段,故均可保存
       	//if(!find_id($PRCS_ITEM_AUTO,$ETITLE)&&$ECLASS!="CALC"&&$ECLASS!="SIGN")
       	//修改签章控件不可写的不保存 modify by lx 20090416
       	if(!find_id($PRCS_ITEM_AUTO,$ETITLE) && $ECLASS!="CALC")
       	{
       		$READ_ONLY_STR.=$ITEM_ID.",";
       	}
       
       	//改变颜色和设置只读标记
       	if($ETYPE == "checkbox"){
       		if(strstr($ELEMENT_OUT," CHECKED"))
       			$ELEMENT_OUT=str_ireplace("<$ETAG","<$ETAG readonly onclick='this.checked=1;' class=BigStatic1",$ELEMENT_OUT);
       		else
       			$ELEMENT_OUT=str_ireplace("<$ETAG","<$ETAG readonly onclick='this.checked=0;' class=BigStatic1",$ELEMENT_OUT);
       	}else if($ECLASS!="LIST_VIEW"&&$ECLASS!="SIGN"){
       		$ELEMENT_OUT=str_ireplace("<$ETAG","<$ETAG readOnly class='read_only'",$ELEMENT_OUT);
       	}
       
       	//设置下拉菜单的数据为只读
       	if($ETAG=="SELECT")
       	{
       		if($ECLASS!="AUTO") //非宏控件
       		{
       			$ELEMENT_OUT=substr($ELEMENT_OUT,0,stripos($ELEMENT_OUT,">")+1)."<OPTION value=$ITEM_VALUE>$ITEM_VALUE</OPTION></SELECT>";
       			if($CHILD)
       				$ELEMENT_OUT .= $ELEMENT_OUT_JS;
       		}
       		else //宏控件如为空时，项目也显示为空
       		{
       			//单独要显示的
       			if($DATAFLD == "SYS_LIST_SQL")
       			{
       				$ELEMENT_OUT=substr($ELEMENT_OUT,0,stripos($ELEMENT_OUT,">")+1)."<OPTION value=$ITEM_VALUE> $ITEM_VALUE </OPTION></SELECT>";
       			}
       			elseif($DATAFLD == "SYS_LIST_PRIV")
       			{
       				if(is_numeric($ITEM_VALUE))
       				{
       					$query_priv = "select PRIV_NAME from USER_PRIV where USER_PRIV='$ITEM_VALUE'";
       					$cursor_priv = exequery($connection, $query_priv);
       					if($ROW_PRIV = mysql_fetch_array($cursor_priv))
       					{
       						if($ROW_PRIV['PRIV_NAME'])
       						{
       							$ITEM_VALUE_TEXT = $ROW_PRIV['PRIV_NAME'];
       							$ELEMENT_OUT=substr($ELEMENT_OUT,0,stripos($ELEMENT_OUT,">")+1)."<OPTION value=$ITEM_VALUE> $ITEM_VALUE_TEXT </OPTION></SELECT>";
       							$ITEM_VALUE_TEXT = "";
       						}
       					}
       				}
       			}
       			else
       			{
       				if($ITEM_VALUE_TEXT)
       				{
       					$ITEM_VALUE_TEMP = $ITEM_VALUE_TEXT;
       				}
       				else
       				{
       					$ITEM_VALUE_TEMP = $ITEM_VALUE;
       				}
       				$ELEMENT_OUT=substr($ELEMENT_OUT,0,stripos($ELEMENT_OUT,">")+1)."<OPTION value=$ITEM_VALUE> $ITEM_VALUE_TEMP </OPTION></SELECT>";
       			}
       			$ITEM_VALUE_TEXT = "";
       		}
       	}
       	$WriteDiv = "";
       }
       else //可输入项，突出行颜色
       {
       	$WriteDiv = "WriteDiv";
       }
       //去除控件宽度以自适应PDA宽度
       //$ELEMENT_OUT=preg_replace('/size="(.*)"/i', '', $ELEMENT_OUT);
       $ELEMENT_OUT=preg_replace('/width\s*:\s*\d+px;?\s*/i', '', $ELEMENT_OUT);
       //处理非IE浏览器有hidden则不显示的问题
       $ELEMENT_OUT=str_ireplace('hidden="0"',"",$ELEMENT_OUT);
       $I++;
       if($I == $ITEM_COUNT)
       	  $END_LINE = "endline";
       else
       	$END_LINE = "";
       if($ELEMENT_OUT)
       {
       	echo "<div class='read_detail ".$WriteDiv . " " . $END_LINE ."'><em>".$ETITLE."：</em>".$ELEMENT_OUT."</div>";
       }
   }//for
   $query = "SELECT FORM_NAME,PRINT_MODEL_SHORT from FLOW_FORM_TYPE WHERE FORM_ID='$FORM_ID'";
   $cursor1= exequery($connection,$query);
   if($ROW=mysql_fetch_array($cursor1))
   {
      $FORM_NAME=$ROW["FORM_NAME"];
      $PRINT_MODEL=$ROW["PRINT_MODEL_SHORT"];
   }
   //会签宏
   if(strstr($PRINT_MODEL,"#[MACRO_SIGN"))
   {
   	
     $query = "SELECT PRCS_ID,FLOW_PRCS from FLOW_RUN_PRCS WHERE RUN_ID='$RUN_ID'";
     $cursor= exequery($connection,$query);
     while($ROW=mysql_fetch_array($cursor))
     {
        $PRCS_ID1=$ROW["PRCS_ID"];
        $FLOW_PRCS1=$ROW["FLOW_PRCS"];
   	  $FLOW_PRCS1 = intval($FLOW_PRCS1);
        $query = "SELECT PRCS_NAME from FLOW_PROCESS WHERE FLOW_ID='$FLOW_ID' AND PRCS_ID='$FLOW_PRCS1'";
        $cursor1= exequery($connection,$query);
        if($ROW=mysql_fetch_array($cursor1))
           $PRCS_NAME=$ROW["PRCS_NAME"];
   
        if($PRCS_ID_ARRAY[$PRCS_ID1]=="")
           $PRCS_ID_ARRAY[$PRCS_ID1]=$PRCS_NAME;
        elseif($PRCS_ID_ARRAY[$PRCS_ID1]!=$PRCS_NAME) //并发
           $PRCS_ID_ARRAY[$PRCS_ID1].=",".$PRCS_NAME;
     }
     
     $label = "MACRO_SIGN";
     $exp = "/#\[".$label."(\d*)(\*?)\]\[([\S\s]*?)\]/i";
     preg_match_all($exp, $PRINT_MODEL, $matches);
   	
      $query = "SELECT * from FLOW_RUN_FEEDBACK where RUN_ID='$RUN_ID' order by PRCS_ID,EDIT_TIME";
      $cursor= exequery($connection,$query);
      $FEEDBACK_COUNT=0;
      while($ROW=mysql_fetch_array($cursor))
      {
         $FEEDBACK_COUNT++;
         $USER_ID=$ROW["USER_ID"];
         $PRCS_ID1=$ROW["PRCS_ID"];
         $CONTENT=$ROW["CONTENT"];
         $EDIT_TIME=$ROW["EDIT_TIME"];
      
         $ATTACHMENT_ID1=$ROW["ATTACHMENT_ID"];
         $ATTACHMENT_NAME1=$ROW["ATTACHMENT_NAME"];
         $CONTENT=str_ireplace("<","&lt",$CONTENT);
         $CONTENT=str_ireplace(">","&gt",$CONTENT);
         $CONTENT=stripslashes($CONTENT);
         $CONTENT=str_ireplace("\n","<br />",$CONTENT);
         $query1 = "SELECT USER_NAME,DEPT_ID from USER where USER_ID='$USER_ID'";
         $cursor1= exequery($connection,$query1);
         if($ROW=mysql_fetch_array($cursor1))
         {
           $USER_NAME=$ROW["USER_NAME"];
           $DEPT_ID=$ROW["DEPT_ID"];
           $DEPT_NAME=dept_long_name($DEPT_ID);
         }
         if(in_array($PRCS_ID1, $matches[1]))
         {
	         if($PRCS_ID1!=0)
	          $SIGN_CONTENT.="<div class=\"read_detail read_detail_header\">".sprintf(_("第%s步"), $PRCS_ID1)."</b> ".$PRCS_ID_ARRAY[$PRCS_ID1]."</div>";
	   
	         $SIGN_CONTENT.="<p class=\"read_detail read_detail_p\"><em>$USER_NAME($DEPT_NAME)：</em><br />$CONTENT $EDIT_TIME</p>";
	         if($ATTACHMENT_ID1!="")
	         {
	          $SIGN_CONTENT .= "<div class='read_detail read_detail_p'>".attach_link_pda($ATTACHMENT_ID1,$ATTACHMENT_NAME1,$P,'',1,1,1)."</div>";
	         }
         }
     }
     
     //$SIGN_CONTENT = getSignInfo($RUN_ID,$FLOW_ID,$PRINT_MODEL);
     echo $SIGN_CONTENT;
   }

//宏附件
if($FLOW_DOC!=0 && strstr($PRINT_MODEL,"#[MACRO_ATTACH"))
{
	$ATTACHMENT_ID_ARRAY=explode(",",$ATTACHMENT_ID);
	$ATTACHMENT_NAME_ARRAY=explode("*",$ATTACHMENT_NAME);
	$ARRAY_COUNT=sizeof($ATTACHMENT_ID_ARRAY);
	$label = "MACRO_ATTACH";
	$exp = "/#\[".$label."(\d*)(\*?)\]/i";
	preg_match_all($exp, $PRINT_MODEL, $matches);
	
	//$PRINT_MODEL=getAttach($RUN_ID,$ATTACHMENT_ID,$ATTACHMENT_NAME,$PRINT_MODEL);
	$ATTACH_CONTENT = "";
	foreach($matches[1] as $ATTACH_KEY)
	{
		if($ATTACH_KEY)
		{
			if($ATTACHMENT_ID)
			{
				$ATTACH_CONTENT .= attach_link_pda($ATTACHMENT_ID,$ATTACHMENT_NAME,$P,'',1,1,1);
			}
		}
		else
		{
			if($ATTACHMENT_ID_ARRAY[$ATTACH_KEY - 1])
			{
				$ATTACH_CONTENT .= attach_link_pda($ATTACHMENT_ID_ARRAY[$ATTACH_KEY - 1],$ATTACHMENT_NAME_ARRAY[$ATTACH_KEY - 1],$P,'',1,1,1);
			}
		}
	}
	if($ATTACH_CONTENT)
	{
		echo "<div class='read_detail read_detail_p'>".$ATTACH_CONTENT."</div>";
	}
	
	//$ATTACH_CONTENT .= "<div class='read_detail read_detail_p'>".attach_link_pda($ATTACHMENT_ID1,$ATTACHMENT_NAME1,$P,'',1,1,1)."</div>";
}
?>
   </div>
<?
   if($ATTACHMENT_ID!=""){
?>
<div class="tform">
      <div class="read_detail read_detail_header"><?=_("附件")?></div>
      <div class="read_detail read_detail_p endline">
         <?=attach_link_pda($ATTACHMENT_ID,$ATTACHMENT_NAME,$P,'',1,1,1)?>
      </div>
</div>
<?
   }
?>
   <div class="tform">
      <div class="read_detail read_detail_header"><?=_("会签意见")?></div>
      <div class="read_detail"><textarea name="CONTENT" id="CONTENT" rows="3" wrap="on"></textarea></div>
      <div id="editSignBox">
<?
$query = "SELECT * from FLOW_RUN_PRCS where USER_ID='$LOGIN_USER_ID' AND RUN_ID='$RUN_ID' AND PRCS_ID='$PRCS_ID'";
$cursor= exequery($connection,$query);
if($ROW=mysql_fetch_array($cursor))
{
	$PRCS_FLAG=$ROW["PRCS_FLAG"];
	$TOP_FLAG=$ROW["TOP_FLAG"];
	if($PRCS_FLAG==1)
	{
		$query = "update FLOW_RUN_PRCS set PRCS_FLAG='2',PRCS_TIME='$CUR_TIME' WHERE USER_ID='$LOGIN_USER_ID' AND RUN_ID='$RUN_ID' AND PRCS_ID='$PRCS_ID'";
		exequery($connection,$query);
	}
}

$query = "SELECT PRCS_ID,FLOW_PRCS from FLOW_RUN_PRCS WHERE RUN_ID='$RUN_ID'";
$cursor= exequery($connection,$query);
while($ROW=mysql_fetch_array($cursor))
{
	$PRCS_ID1=$ROW["PRCS_ID"];
	$FLOW_PRCS1=$ROW["FLOW_PRCS"];
	$FLOW_PRCS1 = intval($FLOW_PRCS1);
	$query = "SELECT PRCS_NAME from FLOW_PROCESS WHERE FLOW_ID='$FLOW_ID' AND PRCS_ID='$FLOW_PRCS1'";
	$cursor1= exequery($connection,$query);
	if($ROW=mysql_fetch_array($cursor1))
		$PRCS_NAME=$ROW["PRCS_NAME"];

	$FLOW_PRCS_ARRAY[$FLOW_PRCS1] = $PRCS_NAME;  //按设计步骤存放步骤名称的数组 add by lx 20100222

	if($PRCS_ID_ARRAY[$PRCS_ID1]=="")
		$PRCS_ID_ARRAY[$PRCS_ID1]=$PRCS_NAME;
	elseif(!find_id($PRCS_ID_ARRAY[$PRCS_ID1],$PRCS_NAME)) //并发
	$PRCS_ID_ARRAY[$PRCS_ID1].=",".$PRCS_NAME;
}

//固定流程检查会签意可见性
$SIGNLOOK_ARR=array();
if($FLOW_TYPE==1)
{
	$query1 = "select PRCS_ID,SIGNLOOK FROM FLOW_PROCESS WHERE FLOW_ID='$FLOW_ID'";
	$cursor1= exequery($connection,$query1);
	while($ROW=mysql_fetch_array($cursor1))
		$SIGNLOOK_ARR[$ROW["PRCS_ID"]] = $ROW["SIGNLOOK"];
}

$query = "SELECT * from FLOW_RUN_FEEDBACK where RUN_ID='$RUN_ID' order by PRCS_ID,EDIT_TIME";
$cursor= exequery($connection,$query);
$FEEDBACK_COUNT=0;
while($ROW=mysql_fetch_array($cursor))
{
	$FEEDBACK_COUNT++;

	$FEED_ID=$ROW["FEED_ID"];
	$PRCS_ID1=$ROW["PRCS_ID"];
	$FLOW_PRCS1=$ROW["FLOW_PRCS"];
	$USER_ID=$ROW["USER_ID"];
	$CONTENT=$ROW["CONTENT"];
	$ATTACHMENT_ID1=$ROW["ATTACHMENT_ID"];
	$ATTACHMENT_NAME1=$ROW["ATTACHMENT_NAME"];
	$EDIT_TIME=$ROW["EDIT_TIME"];
	$FEED_SIGN_DATA=$ROW["SIGN_DATA"];
	 
	//固定流程检查会签意可见性
	if($FLOW_TYPE==1)
	{
		$SIGNLOOK1 = $SIGNLOOK_ARR["$FLOW_PRCS1"];
		//无权查看会签意见
		if(($SIGNLOOK1==2 && $PRCS_ID1!=$PRCS_ID && $USER_ID!=$LOGIN_USER_ID) || ($SIGNLOOK1==1 && $FLOW_PRCS1==$FLOW_PRCS && $USER_ID!=$LOGIN_USER_ID))
			continue;
	}
	 
	$CONTENT_VIEW=htmlspecialchars($CONTENT);
	$CONTENT_VIEW=UBB2XHTML($CONTENT_VIEW);
	$CONTENT_VIEW=nl2br($CONTENT_VIEW);
	if($ATTACHMENT_ID1!=""){
		$CONTENT_VIEW .= attach_link_pda($ATTACHMENT_ID1,$ATTACHMENT_NAME1,$P,'',1,1,1);
	}
	 
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
?>
   <div class="read_detail read_detail_header"><?=sprintf(_("第%s步"), $PRCS_ID1)?> <?=$FLOW_PRCS1>0 ? $FLOW_PRCS_ARRAY[$FLOW_PRCS1] : $PRCS_ID_ARRAY[$PRCS_ID1]?></div>
   <div class="read_detail read_detail_p">
      <?=$USER_NAME?>(<?=$DEPT_NAME?>) - <?=$EDIT_TIME?><br />
      <?=$CONTENT_VIEW?>
   </div>
<?
}
?>
	</div>
   </div>
   
   <div class="tform">
      <div class="read_detail read_detail_header"><?=_("流程图")?></div>
<?
$query = "SELECT MAX(PRCS_ID) from FLOW_RUN_PRCS where RUN_ID='$RUN_ID'";
$cursor= exequery($connection,$query);
if($ROW=mysql_fetch_array($cursor))
	$PRCS_ID=$ROW[0];

$query = "SELECT MAX(PRCS_ID) from FLOW_RUN_PRCS where RUN_ID='$RUN_ID' and PRCS_FLAG<>'5'";
$cursor= exequery($connection,$query);
if($ROW=mysql_fetch_array($cursor))
	$PRCS_ID_NOW=$ROW[0];
$attend_cfg = array();
$PRCS_ARR = array();
for($PRCS_ID_I=1;$PRCS_ID_I<=$PRCS_ID;$PRCS_ID_I++)
{
	$query = "SELECT * from FLOW_RUN_PRCS where RUN_ID='$RUN_ID' and PRCS_ID='$PRCS_ID_I' group by FLOW_PRCS";
	$cursor= exequery($connection,$query);
	$NUM_ROWS = mysql_num_rows($cursor);

	//while1 获取步骤信息
	$FLOW_PRCS_COUNT=0;
	while($ROW=mysql_fetch_array($cursor))
	{
		$FLOW_PRCS = $ROW["FLOW_PRCS"];
		$PARENT = $ROW["PARENT"];
		$FLOW_PRCS_COUNT++;
		$TIME_OUT = $ROW["TIME_OUT"];

		//判断当前用户是否为此步骤实际经办人
		$query1 = "SELECT 1 from FLOW_RUN_PRCS WHERE RUN_ID='$RUN_ID' AND PRCS_ID='$PRCS_ID_I' AND FLOW_PRCS='$FLOW_PRCS' AND USER_ID='$LOGIN_USER_ID'";
		$cursor1 = exequery($connection,$query1);
		if($ROW1=mysql_fetch_array($cursor1))
			$IS_PROC_USER=1;
		else
			$IS_PROC_USER=0;
	  
		//取步骤主办人信息

		$RUN_ID = intval($RUN_ID);
		$PRCS_ID_I = intval($PRCS_ID_I);
		$query1 = "SELECT USER_ID,PRCS_FLAG from FLOW_RUN_PRCS where RUN_ID='$RUN_ID' and PRCS_ID='$PRCS_ID_I' and FLOW_PRCS='$FLOW_PRCS' and OP_FLAG=1";
		$cursor1= exequery($connection,$query1);
		if($ROW=mysql_fetch_array($cursor1))
		{
			$OP_USER = $ROW["USER_ID"];
			$OP_PRCS_FLAG = $ROW["PRCS_FLAG"];
		}

		if($FLOW_TYPE==1)
		{
			include_once("inc/workflow_flow.php");
			 
			if(is_array($PRCS_ARRAY) && array_key_exists($FLOW_PRCS,$PRCS_ARRAY))
			{
				$PRCS_NAME=$PRCS_ARRAY["$FLOW_PRCS"]["PRCS_NAME"];
				if($TIME_OUT == "")
					$TIME_OUT=$PRCS_ARRAY["$FLOW_PRCS"]["TIME_OUT"];
				$SIGNLOOK=$PRCS_ARRAY["$FLOW_PRCS"]["SIGNLOOK"];
				$TIME_OUT_TYPE=$PRCS_ARRAY["$FLOW_PRCS"]["TIME_OUT_TYPE"];
				$TIME_OUT_ATTEND = $PRCS_ARRAY["$FLOW_PRCS"]["TIME_OUT_ATTEND"];
			}
			else
				$PRCS_NAME="<font color=red>"._("流程步骤已删除")."</font>";
		}

		//---------- while2 获得此步骤、此序号的办理信息 ---------------
		$query1 = "SELECT * from FLOW_RUN_PRCS where RUN_ID='$RUN_ID' and PRCS_ID='$PRCS_ID_I' and FLOW_PRCS='$FLOW_PRCS' order by OP_FLAG DESC,PRCS_FLAG desc,PRCS_TIME";
		$cursor1= exequery($connection,$query1);
		$PRCS_FLAG_DESC="";
		while($ROW=mysql_fetch_array($cursor1))
		{
			$PRCS_USER=$ROW["USER_ID"];
			$OTHER_USER=$ROW["OTHER_USER"];
			$FROM_USER=$ROW["FROM_USER"];
			$PRCS_TIME=$ROW["PRCS_TIME"];
			$DELIVER_TIME=$ROW["DELIVER_TIME"];
			$PRCS_FLAG = $ROW["PRCS_FLAG"];
			$OP_FLAG_PRCS = $ROW["OP_FLAG"];
			$CREATE_TIME = $ROW["CREATE_TIME"];
			$TIME_OUT_FLAG = $ROW["TIME_OUT_FLAG"];

			//---- 获取该用户信息 ----
			$PRCS_USER_NAME="";
			$query2 = "SELECT USER_NAME,DEPT_ID from USER where USER_ID='$PRCS_USER'";
			$cursor2= exequery($connection,$query2);
			if($ROW=mysql_fetch_array($cursor2))
			{
				$PRCS_USER_NAME=$ROW["USER_NAME"];
				$PRCS_USER_NAME1=$PRCS_USER_NAME;
				$DEPT_ID=$ROW["DEPT_ID"];
				$DEPT_NAME=dept_long_name($DEPT_ID);
			}
			else
				$PRCS_USER_NAME=$PRCS_USER;

			//获取委托人信息
			if($OTHER_USER != "")
			{
				$OTHER_USER_ARR = explode(",", $OTHER_USER);
				$OTHER_USER_ID = end($OTHER_USER_ARR);
				$OTHER_USER_NAME = rtrim(GetUserNameById($OTHER_USER_ID),",");

				$OTHER_CONTENT = "<b>".sprintf(_("接受 %s委托%s"),$OTHER_USER_NAME,"</b><br />");
			}


			//---- 获取该用户会签意见 ----
			$SEE_SIGN=1;
			$FEED_CONTENT="";
			if($FLOW_TYPE==1)
			{
				if($SIGNLOOK==2)
				{
					if($IS_PROC_USER==0) $SEE_SIGN=0;
				}
				elseif($SIGNLOOK==1)
				{
					if($IS_PROC_USER==1 && $PRCS_USER!=$LOGIN_USER_ID) $SEE_SIGN=0;
				}
			}

			if($SEE_SIGN==1)
			{
				$RUN_ID = intval($RUN_ID);
				$PRCS_ID_I = intval($PRCS_ID_I);
				$query2 = "SELECT CONTENT,EDIT_TIME from FLOW_RUN_FEEDBACK where RUN_ID='$RUN_ID' and PRCS_ID='$PRCS_ID_I' and USER_ID='$PRCS_USER' order by EDIT_TIME";
				$cursor2= exequery($connection,$query2);
				$FEED_CONTENT="";
				while($ROW=mysql_fetch_array($cursor2))
				{
					if($FEED_CONTENT=="")
						$FEED_CONTENT= "\n\n"._("会签与点评：")."\n";
					$FEED_CONTENT.=$ROW["CONTENT"]." ".$ROW["EDIT_TIME"]."\n";
				}
			}
			//echo $PRCS_USER;
			//---- 形成用户信息title ----
			if($OP_FLAG_PRCS==1) //主办人
			{
				$PRCS_USER_NAME="<span class='big4'><b>".$PRCS_USER_NAME._(" 主办")."</b></span>";
			}
			else
				$PRCS_USER_NAME=_("<u title='$OTHER_CONTENT 部门：$DEPT_NAME' style='cursor:pointer'><b>$PRCS_USER_NAME</b></u>");

			//获取工作移交原办理人信息
			if($FROM_USER!="")
			{
				$query2 = "SELECT USER_NAME from USER where USER_ID='$FROM_USER'";
				$cursor2= exequery($connection,$query2);
				if($ROW=mysql_fetch_array($cursor2))
					$FROM_USER_NAME=$ROW["USER_NAME"];
				$FROM_USER_TITLE = _("原办理人:").$FROM_USER_NAME;
				$PRCS_USER_NAME.="<span title=".$FROM_USER_TITLE.">(".$FROM_USER_NAME.")</span>";
			}

			if(!$PRCS_TIME){
				$PRCS_BEGIN_TIME=strtotime($CREATE_TIME);
			}else {
				$PRCS_BEGIN_TIME=strtotime($PRCS_TIME);
			}

			if(!$PRCS_BEGIN_TIME)
				$PRCS_BEGIN_TIME=strtotime($CUR_TIME);
			if($TIME_OUT_ATTEND){
				//获取排班信息
				$attend_cfg = get_attend_cfg($PRCS_USER);
			}else {
				$attend_cfg = "";
			}
				
			//计算已用时
				
			$PRCS_END_TIME = !empty($DELIVER_TIME) ? strtotime($DELIVER_TIME) : time();
			$TIME_USED=get_time_out($PRCS_BEGIN_TIME,$PRCS_END_TIME,$attend_cfg);
			if($TIME_USED > 0){
				$TIME_STR = get_time_out_format($TIME_USED,'dhms',$attend_cfg);
			}else {
				$TIME_STR = _("0秒");
			}
			if($PRCS_FLAG==1){
				$TIME_STR=sprintf(_("接收延迟：%s"),$TIME_STR);
			}else{
				$TIME_STR=sprintf(_("已用时：%s"),$TIME_STR);
			}
			$TIME_STR .= "&#10;".get_attend_cfg_desc($attend_cfg);
			$TIME_USED_TOTAL = get_time_out($PRCS_BEGIN_TIME,$PRCS_END_TIME);
			$TIME_STR_TOTAL = get_time_out_format($TIME_USED_TOTAL,'dhms');

			//-- 超时信息 --
			if($TIME_OUT_FLAG == 1 && $TIME_OUT)
			{
				if($TIME_OUT_TYPE == 0)
				{
					$PRCS_BEGIN_TIME = $PRCS_TIME;
					if(!$PRCS_TIME)
						$PRCS_BEGIN_TIME = $CREATE_TIME;
				}
				else
					$PRCS_BEGIN_TIME = $CREATE_TIME;

				$PRCS_END_TIME = $DELIVER_TIME;
				$PRCS_BEGIN_TIME = strtotime($PRCS_BEGIN_TIME);
				$PRCS_END_TIME = strtotime($PRCS_END_TIME);
				if(!$PRCS_BEGIN_TIME)
					$PRCS_BEGIN_TIME=$CUR_TIME;
				if(!$PRCS_END_TIME)
					$PRCS_END_TIME = $CUR_TIME;
				$TIME_USED_DESC = getTimeOutDesc($TIME_OUT,$PRCS_BEGIN_TIME,$PRCS_END_TIME,$attend_cfg);
			}

			//记录未接收或者超时用户
			if($PRCS_FLAG==1 || $TIME_OUT_FLAG==1)
			{
				$TIME_TO_ID.=$PRCS_USER.",";
				$TIME_TO_NAME.=$PRCS_USER_NAME1.",";
			}
			//-- 形成该用户办理状态 ---
			if($PRCS_FLAG==1)
				$PRCS_FLAG_DESC.='<img src="/images/email_close.gif" align="absmiddle"> '.$PRCS_USER_NAME."[<font color=green>"._("未接收办理")."</font>]";
			elseif($PRCS_FLAG==2)
			{
				$PRCS_FLAG_DESC.='<img src="/images/email_open.gif" align="absmiddle"> '.$PRCS_USER_NAME."[<font color=green>".sprintf(_("办理中,已用时：%s"),$TIME_STR_TOTAL."</font>]");
			}
			elseif($PRCS_FLAG==3)
			{
				$PRCS_FLAG_DESC.='<img src="/images/flow_next.gif" align="absmiddle"> '.$PRCS_USER_NAME."[<font color=green>".sprintf(_("已转交下步,用时：%s"),$TIME_STR_TOTAL."</font>]");
			}
			elseif($PRCS_FLAG==4)
			{
				$PRCS_FLAG_DESC.='<img src="/images/flow_next.gif" align="absmiddle"> '.$PRCS_USER_NAME."[<font color=green>".sprintf(_("已办结,用时：%s"),$TIME_STR_TOTAL."</font>]");
			}
			elseif($PRCS_FLAG==5)
			{
				$PRCS_FLAG_DESC.='<img src="/images/flow_prev.gif" align="absmiddle"> '.$PRCS_USER_NAME."[<font color=gray>"._("预设经办人")."</font>]";
			}
			elseif($PRCS_FLAG==6)
			{
				$PRCS_FLAG_DESC.='<img src="/images/sms_type4.gif" align="absmiddle"> '.$PRCS_USER_NAME."[<font color=green>".sprintf(_("挂起中,用时：%s"),$TIME_STR_TOTAL."</font>]");
			}

			 
			if($TIME_OUT_FLAG && $TIME_OUT)
				$PRCS_FLAG_DESC.='<br> <span style="color:red">'.sprintf(_("限时%s小时,超时%s"),$TIME_OUT,$TIME_USED_DESC.'</span>');

			if($PRCS_TIME!=NULL)
				$PRCS_FLAG_DESC.=_("<br> 开始于：$PRCS_TIME");
			if($DELIVER_TIME!=NULL && $DELIVER_TIME!="0000-00-00 00:00:00" )
				$PRCS_FLAG_DESC.=_("<br> 结束于：$DELIVER_TIME");

			$PRCS_FLAG_DESC.="<br>";
		}//while2

		if($PRCS_FLAG_DESC=="")
			$PRCS_FLAG_DESC="&nbsp;";

		if($PRCS_ID_I==$PRCS_ID_NOW)
			$CLASS="TableContent";
		else
			$CLASS="TableLine".($PRCS_ID_I%2+1);
		 
		if($WORD || $HTML)
		{
			$PRCS_FLAG_DESC=str_ireplace("<img ","<span ",$PRCS_FLAG_DESC);
		}
   		$PRCS_ARR["$PRCS_ID_I"]["$FLOW_PRCS"] = array("PRCS_NAME" => $PRCS_NAME, "PRCS_FLAG"=>$PRCS_FLAG, "FLOW_TYPE"=>$FLOW_TYPE, "PARENT"=>$PARENT, "WORD"=>$WORD, "HTML"=>$HTML, "PRCS_FLAG_DESC"=>$PRCS_FLAG_DESC);
  }//while1
}//for

if(count($PRCS_ARR) > 0)
{
	foreach($PRCS_ARR as $PRCS_ID_TMP=>$FLOW_PRCS_TMP)
	{
?>
		<div class="read_detail">
			<em><?=sprintf(_("第%s步 "),"<b><span class='Big4'>".$PRCS_ID_TMP."</span></b>");?></em>
		</div>
<?
		foreach ($FLOW_PRCS_TMP as $FLOW_PRCS_KEY => $FLOW_PRCS_VAL)
		{
			echo "<div class='read_detail read_detail_p'>\n";
			//输出序号
			$SPAN_HTML = "<span ";
			if($FLOW_PRCS_VAL['PARENT']!="0" && $FLOW_PRCS_VAL['PARENT']!="")
				$SPAN_HTML .= "title='"._("上一步骤序号：").$FLOW_PRCS_VAL['PARENT']."'";
			$SPAN_HTML .= ">\n";
			if(!$FLOW_PRCS_VAL['WORD'] && !$FLOW_PRCS_VAL['HTML'])
			{
				$SPAN_HTML .= "<img border=0 src='/images/arrow_down.gif'>";
			}
			$SPAN_HTML .= _("序号") . $FLOW_PRCS_KEY . _("：") . $FLOW_PRCS_VAL['PRCS_NAME'];
			if($PRCS_FLAG==5)
				$SPAN_HTML .= "&nbsp;" . _("预设步骤");
			$SPAN_HTML .= "\n</span><br />\n";
			echo $SPAN_HTML;
			
			//输出内容
			echo $FLOW_PRCS_VAL['PRCS_FLAG_DESC'];
			echo "</div>";
		}
	}
}
?>
   </div>
   <input type="hidden" name="P" value="<?=$P?>">
   <input type="hidden" name="FLOW_ID" value="<?=$FLOW_ID?>">
   <input type="hidden" name="RUN_ID" value="<?=$RUN_ID?>">
   <input type="hidden" name="PRCS_ID" value="<?=$PRCS_ID?>">
   <input type="hidden" name="FLOW_PRCS" value="<?=$FLOW_PRCS?>">
   <input type="hidden" name="DO_ACTION" value="">

   <div id="edit_opts" class="edit_opts" style="display:none;">
      <? if($OP_FLAG == 1){ ?>
      <span class="turn_flow"><?=_("转交")?></span>
      <? } ?>
      <span class="save_flow"><?=_("保存")?></span>
      <? if($OP_FLAG == 0){ ?>
         <span class="stop_flow"><?=_("办理完毕")?></span>
      <? } ?>
      <? if($BACK_FLAG && $PRCS_ID!=1){ ?>
      <span class="sel_flow"><?=_("回退")?></span>
      <? } ?>
      <? if($FLOW_TYPE == 2){ ?>
         <span class="stop_flow"><?=_("结束流程")?></span>
      <? } ?>
      <span class="show_original_form"><?=_("原始表单查看")?></span>
   </div>
   </form>
   </div>
</div>

<script>
$('.tform').each(function(){
	$(this).find('.read_detail:last').addClass('endline');
});
</script>