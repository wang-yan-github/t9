<?
include_once("../header.php");
include_once("run_role.php");
include_once("inc/utility_ubb.php");
include_once("inc/utility_file.php");
ob_clean();
$RUN_ID = intval($RUN_ID);
$PRCS_ID = intval($PRCS_ID);
$FLOW_ID = intval($FLOW_ID);
$FLOW_PRCS = intval($FLOW_PRCS);
if(!run_role($RUN_ID,$PRCS_ID))
{
   echo "NOSIGNFLOWPRIV";
   exit;
}
$query = "SELECT * from FLOW_TYPE WHERE FLOW_ID='$FLOW_ID'";
$cursor1= exequery($connection,$query);
if($ROW=mysql_fetch_array($cursor1))
{
    $FLOW_NAME=$ROW["FLOW_NAME"];
    $FLOW_TYPE=$ROW["FLOW_TYPE"];
}

$CUR_TIME=date("Y-m-d H:i:s",time());
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


<?
	if($OP_FLAG == 0){
?>
	<div id="sign_opts" class="sign_opts" style="display:none;">
	   <span class="sign_save_flow"><?=_('保存')?></span>
	   <span class="stop_flow"><?=_('办理完毕')?></span>   
   </div>
   <script>
   	$(".saveSign span").text("<?=_('操作')?>");
   	$(".saveSign").unbind('click').bind("click",function(){
   		showMenu("sign_opts");
   	});
  	</script>
<? }else{ ?>
	<script>
   	$(".saveSign span").text("<?=_('保存')?>");
   	$(".saveSign").unbind('click').bind("click",function(){
   		saveSignWorkFlow();
   	});
  	</script>
<? } ?>