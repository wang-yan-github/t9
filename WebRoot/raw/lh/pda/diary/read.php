<?
include_once("../header.php");
include_once("inc/utility_all.php");
include_once("inc/utility_file.php");
ob_clean();
if($DIA_ID!="")
{
   $query = "SELECT * from DIARY where DIA_ID='$DIA_ID'";
   $cursor= exequery($connection,$query);
   if($ROW=mysql_fetch_array($cursor))
   {
      $USER_ID=$ROW["USER_ID"];
      $CONTENT=$ROW["CONTENT"];
      $SUBJECT=$ROW["SUBJECT"];
      $DIA_ID=$ROW["DIA_ID"];
      
      $DIA_DATE=$ROW["DIA_DATE"];
      $DIA_DATE = date("Y-m-d",strtotime($DIA_DATE));
      
      $DIA_TIME=$ROW["DIA_TIME"];
      $DIA_TYPE=$ROW["DIA_TYPE"];
      $CONTENT=$ROW["CONTENT"];
      $SUBJECT=$ROW["SUBJECT"];
      $DIA_TYPE_DESC=get_code_name($DIA_TYPE,"DIARY_TYPE");
      $CONTENT=$ROW["COMPRESS_CONTENT"] == "" ? $ROW["CONTENT"] : @gzuncompress($ROW["COMPRESS_CONTENT"]);
      
      $ATTACHMENT_ID = $ROW["ATTACHMENT_ID"];
      $ATTACHMENT_NAME = $ROW["ATTACHMENT_NAME"];
      
      if($SUBJECT=="")
         $SUBJECT = _("无标题");
   }
}
?>
<div class="container">
   <h3 class="read_title fix_read_title"><?=$SUBJECT?></h3>
   <div class="read_detail fix_read_detail" style="text-align:left;"><span class="grapc"><?=_("类型：")?></span><?=$DIA_TYPE_DESC?></div>
   <div class="read_detail fix_read_detail" style="text-align:left;"><span class="grapc"><?=_("日志时间：")?></span><?=$DIA_DATE?></div>
   <div class="read_detail fix_read_detail" style="text-align:left;"><span class="grapc"><?=_("最后修改时间：")?></span><?=$DIA_TIME?></div>
   <div class="read_content"><?=$CONTENT?></div>
      <?
      if($ATTACHMENT_ID != "" && $ATTACHMENT_NAME != "")
      {
      ?>
         <div class="read_attach"><?=attach_link_pda($ATTACHMENT_ID,$ATTACHMENT_NAME,$P,'',1,1,1)?></div>
      <?
      }
      ?>
   <input id="SHOW_DIA_ID" type="hidden" value="<?=$DIA_ID?>" />
</div>
<?
   if($ROW["CONTENT"] == @gzuncompress($ROW["COMPRESS_CONTENT"]))
   {
?>
   <script>$(".editDiary").show();</script>
<? } ?>