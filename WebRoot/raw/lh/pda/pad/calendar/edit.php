<?
include_once("../header.php");
include_once("inc/utility_all.php");
ob_clean();

$query="SELECT * from CALENDAR where USER_ID='$LOGIN_USER_ID' and CAL_ID='$CAL_ID'";
$cursor= exequery($connection,$query);
if($ROW=mysql_fetch_array($cursor))
 {
    
    $CAL_ID=$ROW["CAL_ID"];
    $CAL_TIME=$ROW["CAL_TIME"];
    $CAL_TIME=date("Y-m-d H:i:s",$CAL_TIME);
    $END_TIME=$ROW["END_TIME"];
    $END_TIME=date("Y-m-d H:i:s",$END_TIME);
    $CAL_TIME=strtok($CAL_TIME," ");
    $CAL_TIME=strtok(" ");
    $CAL_TIME=substr($CAL_TIME,0,5);
    $CAL_TYPE=$ROW["CAL_TYPE"];
    $END_TIME=strtok($END_TIME," ");
    $END_TIME=strtok(" ");
    $END_TIME=substr($END_TIME,0,5);
    $CAL_LEVEL=$ROW["CAL_LEVEL"];
    $CONTENT=$ROW["CONTENT"];
    $CONTENT=str_replace("<","&lt",$CONTENT);
    $CONTENT=str_replace(">","&gt",$CONTENT);
    $CONTENT=stripslashes($CONTENT);
}
?>
<? if($TYPE == "read"){ ?>
<div class="container">
   <div class="tform tformshow">
		<form action="#"  method="post" name="form1" onsubmit="return false;">
			<div class="read_detail">
			   <em><?=_("日程类型：")?></em><?=get_code_name($CAL_TYPE,"CAL_TYPE")?>
         </div>
         <div class="read_detail">
			   <em><?=_("优先级：")?></em><?=cal_level_desc_fix($CAL_LEVEL)?>
         </div>
         <div class="read_detail">
            <em><?=_("起始时间：")?></em><?=$CAL_TIME?>
         </div>
         <div class="read_detail">
            <em><?=_("结束时间：")?></em><?=$END_TIME?>
         </div>
         <div class="read_detail endline">
		      <em><?=_("日程内容：")?></em><?=$CONTENT?>
		   </div>
		   <input id="SHOW_CAL_ID" type="hidden" value="<?=$CAL_ID?>" />
		</form>
	</div>
</div>
<? }else{ ?>
<div class="container">
   <div class="tform">
			<div class="read_detail">
			   <?=_("日程类型：")?>
			   <select id="CAL_TYPE_EDIT" name="CAL_TYPE">
               <?=code_list("CAL_TYPE",$CAL_TYPE)?>
            </select>
         </div>
         
         <div class="read_detail">
			   <?=_("优先级：")?>
			   <select id="CAL_LEVEL_EDIT" name="CAL_LEVEL">
               <option selected value="" <? if($CAL_LEVEL =="") echo "selected"; ?>><?=_("未指定")?></option>
               <?
                  foreach($CAL_LEVEL_ARRAY as $k => $v)
                  {
               ?>
                  <option value="<?=$k?>" <? if($CAL_LEVEL == $k) echo "selected"; ?>><?=$v?></option>
               <? } ?>
            </select>
         </div>
         
         <div class="read_detail">
            <?=_("起始时间：")?>
            <input id="CAL_TIME_EDIT" type="text" name="CAL_TIME" value="<?=$CAL_TIME?>" onfocus="if(this.value=='格式如 09:35') this.value='';" onblur="if(this.value=='') this.value='格式如 09:35';"/>
         </div>
         
         <div class="read_detail">
            <?=_("结束时间：")?>
            <input id="END_TIME_EDIT" type="text" name="END_TIME" value="<?=$END_TIME?>" onfocus="if(this.value=='格式如 19:23') this.value='';" onblur="if(this.value=='') this.value='格式如 19:23';"/>
         </div>
         
         <div class="read_detail endline">
		      <?=_("日程内容：")?>
		      <textarea id="CONTENT_EDIT" name="CONTENT" rows="5" wrap="on"><?=$CONTENT?></textarea>
		   </div>
		   
		   <input id="SAVE_TYPE_EDIT" type="hidden" name="SAVE_TYPE" value="edit" />
		   <input id="CAL_ID_EDIT" type="hidden" name="CAL_ID" value="<?=$CAL_ID?>" />
		</form>
	</div>
</div>
<? } ?>
