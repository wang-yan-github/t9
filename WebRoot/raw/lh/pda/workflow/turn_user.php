<?
include_once("../header.php");
include_once("run_role.php");
include_once("inc/utility_all.php");
include_once("general/workflow/list/turn/condition.php");
   ob_clean();
   $RUN_ID = intval($RUN_ID);
	$PRCS_ID = intval($PRCS_ID);
	$FLOW_ID = intval($FLOW_ID);
	$FLOW_PRCS = intval($FLOW_PRCS);
if(!$PRCS_ID_NEXT)
{
   echo 'NONEXTPRCS';
   exit;
}
//判断权限
$RUN_ROLE = run_role($RUN_ID,$PRCS_ID);
$query = "select TOP_FLAG FROM FLOW_RUN_PRCS WHERE RUN_ID='$RUN_ID' AND PRCS_ID='$PRCS_ID' AND FLOW_PRCS='$FLOW_PRCS' AND USER_ID='$LOGIN_USER_ID'";
$cursor = exequery($connection,$query);
if($ROW=mysql_fetch_array($cursor))
	$TOP_FLAG = $ROW["TOP_FLAG"];

if($TOP_FLAG!=2)
{
	if(!find_id($RUN_ROLE,2))
	{
		echo "NOEDITPRIV";
		exit;
	}
}
else
{
	//无主办会签
	if(!find_id($RUN_ROLE,4))
	{
		echo "NOSIGNFLOWPRIV";
		exit;
	}
}

$query = "SELECT * from FLOW_TYPE WHERE FLOW_ID='$FLOW_ID'";
$cursor1= exequery($connection,$query);
if($ROW=mysql_fetch_array($cursor1))
{
   $FLOW_NAME=$ROW["FLOW_NAME"];
   $FLOW_TYPE=$ROW["FLOW_TYPE"];
   $FORM_ID=$ROW["FORM_ID"];
}

$query = "SELECT RUN_NAME,USER_NAME,PARENT_RUN from FLOW_RUN LEFT JOIN USER ON(FLOW_RUN.BEGIN_USER=USER.USER_ID) WHERE RUN_ID='$RUN_ID'";
$cursor= exequery($connection,$query);
if($ROW=mysql_fetch_array($cursor))
{
   $RUN_NAME=$ROW["RUN_NAME"];
   $BEGIN_USER_NAME=$ROW["USER_NAME"];
   $PARENT_RUN=$ROW["PARENT_RUN"];
}

$query = "SELECT PRCS_NAME,PRCS_OUT,PRCS_OUT_SET,SYNC_DEAL,TURN_PRIV,PRCS_TO,USER_LOCK,TOP_DEFAULT,GATHER_NODE,CONDITION_DESC,REMIND_FLAG,VIEW_PRIV from FLOW_PROCESS WHERE FLOW_ID='$FLOW_ID' and PRCS_ID='$FLOW_PRCS'";
$cursor= exequery($connection,$query);
if($ROW=mysql_fetch_array($cursor))
{
   $PRCS_NAME=$ROW["PRCS_NAME"];

   $PRCS_OUT=$ROW["PRCS_OUT"];
   $PRCS_OUT_SET=$ROW["PRCS_OUT_SET"];
   $SYNC_DEAL=$ROW["SYNC_DEAL"]; //并发
   $TURN_PRIV=$ROW["TURN_PRIV"]; //强制转交
   $GATHER_NODE=$ROW["GATHER_NODE"]; //强制合并
   $PRCS_TO=$ROW["PRCS_TO"];
   $PRCS_TO=str_replace(",,",",",$PRCS_TO);
   $CONDITION_DESC=$ROW["CONDITION_DESC"];
   $CONDITION_ARR=explode("\n",$CONDITION_DESC);
   $CONDITION_DESC=$CONDITION_ARR[1];
   $REMIND_FLAG=$ROW["REMIND_FLAG"];
   $VIEW_PRIV_PRCS = $ROW["VIEW_PRIV"];
   $AUTO_TYPE=$ROW["AUTO_TYPE"];
}

//------------------------------------------- 转出条件检查 ----------------------------------
$FORM_DATA=get_form($FORM_ID,$RUN_ID);
$NOT_PASS=check_condition($FORM_DATA,$PRCS_OUT,$PRCS_OUT_SET,$RUN_ID,$PRCS_ID);
if(substr($NOT_PASS,0,5)=="SETOK")
   $NOT_PASS="";

//--- 监控人强制转交不限制转出条件 ---
if($OP == "MANAGE")
   $NOT_PASS="";
   
if($NOT_PASS!="")
{
   $NOT_PASS=str_replace("\n","<br>",$NOT_PASS);
      echo '<div class="no_msg">'.$NOT_PASS.'</div>';
   exit;
}
?>
<div class="container" id="workflow-turn-user">
   <div class="tform tformshow">
      <div class="read_detail"><em><?=_("工作名称/文号：")?></em><?=$RUN_NAME?></div>
      <div class="read_detail endline"><em><?=_("发起人：")?></em><?=$BEGIN_USER_NAME?></div>
   </div>
   
   <form action="turn_submit.php"  method="post" name="form1" onsubmit="return false;">
<?
include_once("inc/utility_cache.php");
$PRCS_ID_NEXT = td_trim($PRCS_ID_NEXT);
$PRCS_ID_NEXT_ARR = explode(",", $PRCS_ID_NEXT);
$COUNT = 0;
$query = "SELECT * from FLOW_PROCESS WHERE FLOW_ID='$FLOW_ID' and PRCS_ID in (".$PRCS_ID_NEXT.")";
$cursor= exequery($connection,$query);
while($ROW=mysql_fetch_array($cursor))
{
   $AUTO_TYPE = $ROW["AUTO_TYPE"];
   $AUTO_USER_OP=$ROW["AUTO_USER_OP"];
   $AUTO_USER=$ROW["AUTO_USER"];
   $PRCS_USER=$ROW["PRCS_USER"];
   $PRCS_DEPT=$ROW["PRCS_DEPT"];
   $PRCS_PRIV=$ROW["PRCS_PRIV"];
   $AUTO_BASE_USER=$ROW["AUTO_BASE_USER"];
      	
   $PRCS_USER = get_prcs_user($FLOW_ID,$PRCS_ID_NEXT_ARR[$COUNT]);
   $SELECT = user_select_default($AUTO_TYPE); //判断默认主办人和经办人
   $SELECT = explode(",",$SELECT);
     
   if(sizeof($PRCS_USER) == 0)
   {
      echo '<div class="no_msg">'._("错误：尚未设置步骤经办权限!").'</div>'; 
      exit;
   }
   
   //如果是自动选人
   $PRCS_USER_OP_ZB = '';
   if(count($PRCS_USER["OP_ZB"]) > 0)
   {
      foreach($PRCS_USER["OP_ZB"] as $v)
      $PRCS_USER_OP_ZB = "<em uid='".$v["UID"]."' userid='".$v["USER_ID"]."'>".$v["USER_NAME"]."<span>—</span></em>";
   }
   
   $PRCS_USER_OP_CB = '';
   if(count($PRCS_USER["OP_CB"]) > 0)
   {
      foreach($PRCS_USER["OP_CB"] as $v)
      $PRCS_USER_OP_CB .= "<em uid='".$v["UID"]."' userid='".$v["USER_ID"]."'>".$v["USER_NAME"]."<span>—</span></em>";
   }
     
   $PRCS_NAME = "";
   $query3 = "select PRCS_NAME from FLOW_PROCESS where FLOW_ID = '$FLOW_ID' and PRCS_ID='$PRCS_ID_NEXT_ARR[$COUNT]'";
   $cursor3 = exequery($connection, $query3);
   if($ROW3 = mysql_fetch_array($cursor3))
      $PRCS_NAME = $ROW3['PRCS_NAME'];
      
?>
   <div class="tform tformshow">
      <div class="read_detail read_detail_header"><?=sprintf("第%s步".$PRCS_NAME,$PRCS_ID_NEXT_ARR[$COUNT])?></div>
      <div class="read_detail read_detail_fem" id="USER_ZB_<?=$PRCS_ID_NEXT_ARR[$COUNT]?>"><?=_("主办人：")?><?=$PRCS_USER_OP_ZB?></div>
      <div class="read_detail read_detail_fem <?=$PRCS_USER["CFG"]["ALLOW_CHANGE"] == 0 ? "endline" : "";?>" id="USER_CB_<?=$PRCS_ID_NEXT_ARR[$COUNT]?>"><?=_("经办人：")?><?=$PRCS_USER_OP_CB?></div>
      
      <? 
      if($PRCS_USER["CFG"]["ALLOW_CHANGE"] == 1)
      { 
      ?>
      <div id="search_box">
         <div id="input_box">
            <input type="text" id="USER_NAME_<?=$PRCS_ID_NEXT_ARR[$COUNT]?>" did="USER_PLIST_<?=$PRCS_ID_NEXT_ARR[$COUNT]?>" name="USER_NAME" value="" autocapitalize="off" autocorrect="off"/>   
         </div>
      </div>
      <?
         $I = 0;
         $PLIST = $CHECKBOX = "";
         foreach($PRCS_USER["OP_USER"] as $UID => $USER_INFO)
         {
            $I++;
            $style = ($I > 10) ? " style='display:none;' " : "";

            $DEPT_NAME = str_replace("/"," - ",dept_long_name($USER_INFO["DEPT_ID"]));
            $PLIST .= '<li class="'.$fix_for_pad['list-li-style'].$CHECKED.'"'.$style.'q_id="'.$USER_INFO["UID"].'" q_name="'.$USER_INFO["USER_NAME"].'" q_user_id="'.$USER_INFO["USER_ID"].'" q_name_index="'.$USER_INFO["USER_NAME_INDEX"].'">
                     <h3>'.$USER_INFO["USER_NAME"].'</h3>
                     <p class="grapc">'._("部门：").($DEPT_NAME).'&nbsp;</p>
                     <span class="ui-li-text">
                        <a href="javascript:;" class="ui-li-text-a zb">'._("主办").'</a>
                     </span>
                  </li>';
         }
         
         $PLIST_BTN = "";
         //2012/6/18 1:00:42 lp 如果人数大于5人则显示更多按钮
         $PRCS_USER_OP_USER_COUNT = count($PRCS_USER["OP_USER"]);
         if($PRCS_USER_OP_USER_COUNT > 10)
            $PLIST_BTN = '<div id="USER_SHOW_'.$PRCS_ID_NEXT_ARR[$COUNT].'" class="appendList cp"><a href="javascript:;" class="bga" did="USER_PLIST_'.$PRCS_ID_NEXT_ARR[$COUNT].'" onclick="showList(this);">'._("点击显示全部").'（'.sprintf("共%s人",$PRCS_USER_OP_USER_COUNT).'）</a></div>';
            
         $PLIST_NODATE = '<div id="USER_NODATE_'.$PRCS_ID_NEXT_ARR[$COUNT].'" style="display:none" class="appendList cp">'._("没有查询到相关结果").'</div>';
      ?>
      
      <ul class="comm-list comm-pic-list" id="USER_PLIST_<?=$PRCS_ID_NEXT_ARR[$COUNT]?>"><?=$PLIST?></ul>
      <?=$PLIST_BTN?>
      <?=$PLIST_NODATE?>
      <script type="text/javascript">
         workFlowSearch_<?=$PRCS_ID_NEXT_ARR[$COUNT]?> = new $.workFlowSearch({input:"#USER_NAME_<?=$PRCS_ID_NEXT_ARR[$COUNT]?>",list:"#USER_PLIST_<?=$PRCS_ID_NEXT_ARR[$COUNT]?>",appendDom_zb:"#USER_ZB_<?=$PRCS_ID_NEXT_ARR[$COUNT]?>", appendDom_cb:"#USER_CB_<?=$PRCS_ID_NEXT_ARR[$COUNT]?>", showbtn:"#USER_SHOW_<?=$PRCS_ID_NEXT_ARR[$COUNT]?>", nodate:"#USER_NODATE_<?=$PRCS_ID_NEXT_ARR[$COUNT]?>", pageScroll:"oiScroll_6"});
         workFlowSearch_<?=$PRCS_ID_NEXT_ARR[$COUNT]?>.init();
         <? if((count($PRCS_USER["OP_ZB"]) > 0) && $PRCS_USER["CFG"]["ALLOW_CHANGE"] == 1){ ?>
         workFlowSearch_<?=$PRCS_ID_NEXT_ARR[$COUNT]?>.refresh();
         <? } ?>
         var allow_zb_isnull_<?=$PRCS_ID_NEXT_ARR[$COUNT]?> = "<?=$PRCS_USER['CFG']['ALLOW_ZB_ISNULL']?>";  
      </script>
      <? 
      }else{ 
      ?>
      <script type="text/javascript">
         $("#USER_ZB_<?=$PRCS_ID_NEXT_ARR[$COUNT]?>").find("em").die("click");
         $("#USER_CB_<?=$PRCS_ID_NEXT_ARR[$COUNT]?>").find("em").die("click");  
      </script>
      <? } ?>
   </div>   
<?
	$COUNT++;
}
?>
      <input type="hidden" name="PRCS_ID_NEXT" value="<?=$PRCS_ID_NEXT?>">
   </form>
</div>
<script type="text/javascript">
$(document).ready(function(){
   $("#page_6 ul.comm-list").each(function()
   {
      if($(this).find("li").length < 10)
      {
      	$(this).find("li:last").css("border-bottom","none");	
     	}
   });
})

function showList(obj)
{
   var did = $(obj).attr("did");
   $("#"+did).find("li:hidden").show();
   $("#"+did).find("li:last").css("border-bottom","none");
   $(obj).parent(".appendList").remove();
   oiScroll_6.refresh();
}
</script>