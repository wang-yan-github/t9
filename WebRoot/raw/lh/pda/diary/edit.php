<?
   include_once("../header.php");
   include_once("inc/utility_all.php");
   ob_clean();
   
   if($DIA_ID!="")
   {
      $query = "SELECT * from DIARY where DIA_ID='$DIA_ID'";
      $cursor= exequery($connection,$query);
      if($ROW=mysql_fetch_array($cursor))
      {
         $USER_ID=$ROW["USER_ID"];
         if($USER_ID==$LOGIN_USER_ID)
         {
            $SUBJECT=$ROW["SUBJECT"];   
            $DIA_TYPE=$ROW["DIA_TYPE"];
            $CONTENT=$ROW["CONTENT"];
            $DIA_DATE=$ROW["DIA_DATE"];
            $DIA_DATE = date("Y-m-d",strtotime($DIA_DATE));
         }
     }
   }

?>
<div class="container">
   <div class="tform">
      <form action="#"  method="post" name="form1" onsubmit="return false;">
         <div class="read_detail">
            <em><?=_("标题：")?></em>
            <input id="SUBJECT_EDIT" type="text" name="SUBJECT" value="<?=$SUBJECT?>" style="width:70%;"/>
         </div>
			<div class="read_detail">
			   <em><?=_("类型：")?></em>
			   <select id="DIA_TYPE_EDIT" name="DIA_TYPE">
               <?=code_list("DIARY_TYPE",$DIA_TYPE)?>
            </select>
         </div>
         <div class="read_detail">
			   <em><?=_("日期：")?></em>
            <input id="DATE_EDIT" type="text" name="DATE" value="<?=$DIA_DATE?>" size="10">   
         </div>
         <div class="read_detail endline">
		      <em><?=_("日程内容：")?></em>
		      <textarea id="CONTENT_EDIT" name="CONTENT" rows="5" wrap="on"><?=$CONTENT?></textarea>
		   </div>
		   <input id="SAVE_TYPE_EDIT" type="hidden" name="SAVE_TYPE" value="edit" />
		   <input id="DIA_ID_EDIT" type="hidden" name="DIA_ID" value="<?=$DIA_ID?>" />
		</form>
	</div>
</div>
<script type="text/javascript">
$(document).ready(function(){
   initMobiScrollDate('date',$("#DATE_EDIT"));
});
</script>