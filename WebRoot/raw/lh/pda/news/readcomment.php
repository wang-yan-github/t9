<?
include_once("../header.php");
include_once("inc/utility_cache.php");
include_once("inc/utility_file.php");
ob_clean();

$query = "SELECT 1 from NEWS where NEWS_ID='$NEWS_ID' and PUBLISH='1' and (TO_ID='ALL_DEPT' or find_in_set('$LOGIN_DEPT_ID',TO_ID) or find_in_set('$LOGIN_USER_PRIV',PRIV_ID) or find_in_set('$LOGIN_USER_ID',USER_ID))";
$cursor= exequery($connection,$query);
if(!(mysql_num_rows($cursor) > 0))
{
   echo "NOCOMMENT";
   exit;
}
?>

<div class="container">
   <div class="tform tformshow tlistformshow">
   <form action="sign_submit.php"  method="post" name="form1" onsubmit="return false;">
      <div class="read_detail read_detail_header"><?=_("评论：")?></div>
      <div class="read_detail">
         <div class="replyTo"><span class="contactlist_result replyToResult"></span></div>
         <textarea name="CONTENT" id="CONTENT" rows="3" wrap="on" autocapitalize="off" autocorrect="off"></textarea>
      </div>
   </form>
<?
//先查ID
$ids = '';
$query = "SELECT USER_ID from NEWS_COMMENT where NEWS_ID='$NEWS_ID'";
$cursor = exequery($connection,$query);
while($ROW=mysql_fetch_array($cursor))
{
   $ids.= $ROW['USER_ID'].",";
}

//2012/4/18 16:47:10 lp 查询总数
$query = "SELECT count(*) from NEWS_COMMENT where NEWS_ID='$NEWS_ID'";
$TOTAL_ITEMS = resultCount($query);

$USER_NAME_ARR = array();
$UID_ARR = UserId2Uid($ids,TRUE);
$USER_INFO_ARR = GetUserInfoByUID($UID_ARR,"USER_NAME,DEPT_ID");

// lp 2012/4/19 15:29:32 循环存储评论ID
$COUNT1 = 0;
$COMMENT_ID_ARR = array();
$query = "SELECT * from NEWS_COMMENT where NEWS_ID='$NEWS_ID' order by COMMENT_ID ASC";
$cursor= exequery($connection,$query);
while($ROW=mysql_fetch_array($cursor)){
   $COUNT1++;
   $COMMENT_ID = $ROW['COMMENT_ID'];
   $COMMENT_ID_ARR['L'.$COMMENT_ID] = $COUNT1;
}   

$COUNT = 0;
$query = "SELECT * from NEWS_COMMENT where NEWS_ID='$NEWS_ID' order by COMMENT_ID DESC";
$cursor= exequery($connection,$query);
while($ROW=mysql_fetch_array($cursor))
{
   $COUNT++;
   $COMMENT_ID = $ROW['COMMENT_ID'];
   $PARENT_ID = $ROW['PARENT_ID'];
   $USER_ID = $ROW['USER_ID'];
   $NICK_NAME = $ROW['NICK_NAME'];
   $CONTENT = $ROW['CONTENT'];
   $RE_TIME = $ROW['RE_TIME'];
   $USER_NAME = "";
   
   if($NICK_NAME=="")
      $USER_NAME = $USER_INFO_ARR[$UID_ARR[$USER_ID]]["USER_NAME"];
   else
      $USER_NAME = $NICK_NAME;
   
   if($USER_NAME == "")
   {
      $USER_NAME = _("该用户已删除");
      $DEPT_NAME = '';
   }else{
      $DEPT_ID = $USER_INFO_ARR[$UID_ARR[$USER_ID]]["DEPT_ID"];
      if($DEPT_ID == 0 or $DEPT_ID == "")
      {
         $DEPT_NAME =  _("离职/外部人员");    
      }else{
         $DEPT_NAME =  dept_long_name($DEPT_ID);    
      }
      
      $DEPT_NAME = "(".$DEPT_NAME.")";
   }
   
   //2012/4/18 18:34:10 lp 回复楼层单独处理
   if($PARENT_ID!=0)
   {
      $CONTENT = sprintf( _("回复%s楼："),$COMMENT_ID_ARR['L'.$PARENT_ID]).$CONTENT;
   }
?>
<div class="data_line" p_id="<?=$COMMENT_ID?>" o_num = "<?=$TOTAL_ITEMS?>">
   <div class="read_detail read_detail_header read_detail_sheader">#<?=$TOTAL_ITEMS?> <?=$USER_NAME?> <?=$DEPT_NAME?> - <?=timeintval(strtotime($RE_TIME))?></div>
   <p class="read_detail read_detail_p data_line"><?=$CONTENT?></p>
</div>
<?
   $TOTAL_ITEMS--;
}
?>
   </div>
</div>