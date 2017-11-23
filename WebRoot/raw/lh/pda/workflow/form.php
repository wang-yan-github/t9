<?
include_once("../header.php");
include_once("inc/utility_all.php");
include_once("inc/utility_file.php");
include_once("inc/utility_flow.php");
include_once("run_role.php");
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
?>
<div class="container">
   <div class="tform tformshow">
<?    
   $query = "SELECT FORM_ID from FLOW_TYPE WHERE FLOW_ID='$FLOW_ID'";
   $cursor1= exequery($connection,$query);
   if($ROW=mysql_fetch_array($cursor1))
      $FORM_ID=$ROW["FORM_ID"];
   
   //--- 文号 开始时间 ---
   $query1 = "SELECT * from FLOW_RUN where RUN_ID='$RUN_ID'";
   $cursor1 = exequery($connection,$query1);
   if($ROW=mysql_fetch_array($cursor1))
   {
      $RUN_NAME=$ROW["RUN_NAME"];
      $ATTACHMENT_ID = $ROW["ATTACHMENT_ID"];
      $ATTACHMENT_NAME = $ROW["ATTACHMENT_NAME"];
      $BEGIN_TIME = $ROW["BEGIN_TIME"];
   }
?>
   <div class="read_detail"><em><?=_("名称/文号：")?></em><?=$RUN_NAME?></div>
   <div class="read_detail"><em><?=_("流水号：")?></em><?=$RUN_ID?></div>
   <div class="read_detail"><em><?=_("流程开始：")?></em><?=$BEGIN_TIME?></div>
   <? if($ATTACHMENT_ID!=""){?>
   <div class="read_detail read_detail_header"><?=_("附件")?></div>
   <div class="read_detail read_detail_p">
      <?=attach_link_pda($ATTACHMENT_ID,$ATTACHMENT_NAME,$P,'',1,1,1)?>
   </div>
   <? } ?>
<?
   //-----------判断字段对于当前用户是否为隐藏----------------
   $FLOW_ID = intval($FLOW_ID);
   $RUN_ID = intval($RUN_ID);
   $query="select HIDDEN_ITEM from FLOW_PROCESS,FLOW_RUN_PRCS where FLOW_PROCESS.FLOW_ID='$FLOW_ID' and FLOW_RUN_PRCS.RUN_ID='$RUN_ID' and FLOW_RUN_PRCS.USER_ID='$LOGIN_USER_ID' and FLOW_PROCESS.PRCS_ID=FLOW_RUN_PRCS.PRCS_ID";
   $cursor=exequery($connection,$query);
   $HIDDEN_STR="";
   while($ROW=mysql_fetch_array($cursor))
   {
   	$HIDDEN_STR.=$ROW["HIDDEN_ITEM"];
   }
   
   //--- 表单数据 ---
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

   foreach($ELEMENT_ARRAY as $ENAME => $ELEMENT_ARR)
   {
     //--- 默认值 ---
     $ECLASS = $ELEMENT_ARR["CLASS"];  
     $ITEM_ID = $ELEMENT_ARR["ITEM_ID"];
     $EVALUE = $ELEMENT_ARR["VALUE"]; 
     $ETITLE = $ELEMENT_ARR["TITLE"]; 
     $ETAG = $ELEMENT_ARR["TAG"]; 
   
     $ETITLE=str_replace("<","&lt",$ETITLE);
     $ETITLE=str_replace(">","&gt",$ETITLE);
     $ETITLE=stripslashes($ETITLE);
     
   
     $STR="DATA_".$ITEM_ID;
     $ITEM_VALUE=$$STR;
   
     if(find_id($HIDDEN_STR,$ETITLE))
        $ITEM_VALUE="";
        
     if($ECLASS!="LIST_VIEW")
        $ITEM_VALUE=str_replace("\r\n"," ",$ITEM_VALUE);
   
     if($ECLASS=="AUTO" && $ETAG=="SELECT" && $ITEM_VALUE!="") //--- 列表型宏控件 ---
     {
        $EDATAFLD=$ELEMENT_ARR["DATAFLD"];
        switch($EDATAFLD)
        {
           case "SYS_LIST_DEPT":
                         $query_auto="SELECT * from DEPARTMENT where DEPT_ID='$ITEM_VALUE'";
                         $cursor_auto = exequery($connection,$query_auto);
                         if($ROW=mysql_fetch_array($cursor_auto))
                            $ITEM_VALUE=$ROW["DEPT_NAME"];
                         break;
           case "SYS_LIST_PRIV":
                         $query_auto="SELECT * from USER_PRIV where USER_PRIV='$ITEM_VALUE'";
                         $cursor_auto = exequery($connection,$query_auto);
                         if($ROW=mysql_fetch_array($cursor_auto))
                            $ITEM_VALUE=$ROW["PRIV_NAME"];
                         break;
           case "SYS_LIST_USER":
           case "SYS_LIST_PRCSUSER1":
           case "SYS_LIST_PRCSUSER2":
                         $query_auto="SELECT * from USER where USER_ID='$ITEM_VALUE'";
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
       $ITEM_VALUE=str_replace("\r\n","|",$ITEM_VALUE);
       $ITEM_VALUE=str_replace("`",",",$ITEM_VALUE);
     }
     elseif($ECLASS=="SIGN")  //签章控件
     {
     	 $ITEM_VALUE="";
     }
     else //--- 普通控件 ---
     {
        if($ECLASS=="AUTO" && $ITEM_VALUE=="{MACRO}")
           $ITEM_VALUE="";
   
        $ITEM_VALUE=str_replace("<","&lt",$ITEM_VALUE);
        $ITEM_VALUE=str_replace(">","&gt",$ITEM_VALUE);
        $ITEM_VALUE=stripslashes($ITEM_VALUE);
   
        if($ENAME=="INPUT" && strstr($ELEMENT,"type=checkbox"))
        {
          if($ITEM_VALUE=="on")
             $ITEM_VALUE=_("是");
          else
             $ITEM_VALUE=_("否");
        }
     }
     echo "<div class='read_detail'><em>".$ETITLE."：</em>".$ITEM_VALUE."</div>";
   }//for

   $query = "SELECT FORM_NAME,PRINT_MODEL_SHORT from FLOW_FORM_TYPE WHERE FORM_ID='$FORM_ID'";
   $cursor1= exequery($connection,$query);
   if($ROW=mysql_fetch_array($cursor1))
   {
      $FORM_NAME=$ROW["FORM_NAME"];
      $PRINT_MODEL=$ROW["PRINT_MODEL_SHORT"];
   }
   
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
   
        $CONTENT=str_replace("<","&lt",$CONTENT);
        $CONTENT=str_replace(">","&gt",$CONTENT);
        $CONTENT=stripslashes($CONTENT);
        $CONTENT=str_replace("\n","<br />",$CONTENT);
   
        $query1 = "SELECT USER_NAME,DEPT_ID from USER where USER_ID='$USER_ID'";
        $cursor1= exequery($connection,$query1);
        if($ROW=mysql_fetch_array($cursor1))
        {
           $USER_NAME=$ROW["USER_NAME"];
           $DEPT_ID=$ROW["DEPT_ID"];
           $DEPT_NAME=dept_long_name($DEPT_ID);
        }
   
        if($PRCS_ID1!=0)
          $SIGN_CONTENT.="<div class=\"read_detail read_detail_header\">".sprintf(_("第%s步"), $PRCS_ID1)."</b> ".$PRCS_ID_ARRAY[$PRCS_ID1]."</div>";
   
         $SIGN_CONTENT.="<p class=\"read_detail read_detail_p\"><em>$USER_NAME($DEPT_NAME)：</em><br />$CONTENT $EDIT_TIME</p>";
         if($ATTACHMENT_ID1!="")
         {
          $SIGN_CONTENT .= "<div class='read_detail read_detail_p'>".attach_link_pda($ATTACHMENT_ID1,$ATTACHMENT_NAME1,$P,'',1,1,1)."</div>";
         }
     }
   
     echo $SIGN_CONTENT;
   }
?>
   <div id="form_opts" class="form_opts" style="display:none;">
      <? if(find_id($RUN_ROLE,2)){  ?>
      <span class="turn_flow"><?=_("转交")?></span>
      <? } if(find_id($RUN_ROLE,4)){  ?>
      <span class="sign_flow"><?=_("会签")?></span>
      <? } ?>
   </div>
   </div>
</div>
