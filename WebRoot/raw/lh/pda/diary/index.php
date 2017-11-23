<?
   include_once("../header.php");
   include_once("inc/utility_all.php");

   $query = "SELECT count(*) from DIARY where USER_ID='$LOGIN_USER_ID'";
   $TOTAL_ITEMS = resultCount($query);
?>
<body>
<script type="text/javascript">
var stype = "diary";
var p = "<?=$P?>";
var nonewdata = "<?=_('没有新的工作日志')?>";
var newdata = "<?=_('%s个新工作日志')?>";


/* --- 自定义参数 ---*/
var nomoredata_1 = false;
var noshowPullUp_1 = <? echo ($PAGE_SIZE >= $TOTAL_ITEMS) ? "true" : "false"; ?>;

function pageInit(page_id)
{
   if(page_id == 1)
   {
      tiScroll_1 = new $.tiScroll({"nomoredata": nomoredata_1, "noshowPullUp":noshowPullUp_1});
      tiScroll_1.init();
   }
   if(page_id == 2)
   {
      tiScroll_2 = new $.tiScroll({"page_id": 2, "listType": "readonly"});
      tiScroll_2.init();
   }
   if(page_id == 3)
   {
      tiScroll_3 = new $.tiScroll({"page_id": 3, "listType": "readonly"});
      tiScroll_3.init();
   }
   if(page_id == 4)
   {
      tiScroll_4 = new $.tiScroll({"page_id": 4, "listType": "readonly"});
      tiScroll_4.init();
   }   
   if(page_id == "attach_read")
   {
      tiScroll_attach_read = new $.tiScroll({"page_id": "attach_read", "listType": "attach_show"});
      tiScroll_attach_read.init();      
   }
}
</script>
<?
   $tHeadData = array(
      "1" => array(
         "l" => array("class" => "","event" => "gohome();", "title" => "首页"),
         "c" => array("title" => "工作日志"),
         "r" => array("class" => "","event" => "newDiary();", "title" => "写日志")
      ),
      "2" => array(
         "l" => array("class" => "","event" => "reback(2,1);$(\".editDiary\").hide();", "title" => "返回"),
         "c" => array("title" => "日志详情"),
         "r" => array("display" => "none","class" => "editDiary","event" => "editDiary();", "title" => "编辑")
      ),
      "3" => array(
         "l" => array("class" => "","event" => "reback(3,1);", "title" => "返回"),
         "c" => array("title" => "新建日志"),
         "r" => array("class" => "","event" => "saveDiary(\"new\");", "title" => "保存")
      ),
      "4" => array(
         "l" => array("class" => "","event" => "reback(4,2);", "title" => "返回"),
         "c" => array("title" => "编辑日志"),
         "r" => array("class" => "","event" => "saveDiary(\"edit\");", "title" => "保存")
      ),
      "attach_read" => array(
         "l" => array("class" => "","event" => "reback(\"attach_read\",2);", "title" => "返回"),
         "c" => array("title" => "查看附件")
      )
   );
?>

   <?=buildHead($tHeadData);?>
   <?=buildMessage();?>
   <?=buildProLoading();?>
   
   <!-- all of diary -->
   <div id="page_1" class="pages tlist">
      <div id="wrapper_1" class="wrapper">
         <div id="scroller_1" class="scroller">
            <?=buildPullDown();?>
            <ul class="comm-list" id="diary-list">
            <?
               if($TOTAL_ITEMS > 0)
               {
                  $query = "SELECT * from DIARY where USER_ID='$LOGIN_USER_ID' order by DIA_ID desc limit 0,$PAGE_SIZE";
                  $cursor= exequery($connection,$query);
                  while($ROW=mysql_fetch_array($cursor))
                  {
                     $DIA_ID=$ROW["DIA_ID"];
                     $DIA_DATE=$ROW["DIA_DATE"];
                     $DIA_DATE=strtok($DIA_DATE," ");
                     $DIA_TYPE=$ROW["DIA_TYPE"];
                     $CONTENT=$ROW["CONTENT"];
                     $SUBJECT=$ROW["SUBJECT"];
                     //$DIA_TYPE_DESC=get_code_name($DIA_TYPE,"DIARY_TYPE");
                     
                     $CONTENT=str_replace("<","&lt",$CONTENT);
                     $CONTENT=str_replace(">","&gt",$CONTENT);
                     $CONTENT=stripslashes($CONTENT);
                     
                     $ATTACHMENT_ID = $ROW["ATTACHMENT_ID"];
         		      $ATTACHMENT_NAME = $ROW["ATTACHMENT_NAME"];
         		      
         		      if($SUBJECT=="")
         		         $SUBJECT = _("无标题");
                   
            ?>
                     <li class="<?=$fix_for_pad['list-li-style']?><?=$Class?>" q_id="<?=$DIA_ID?>">
                        <h3><?=$SUBJECT?></h3>
                        <p class="grapc"><?=strip_tags($CONTENT)?>&nbsp;</p>
                        <? 
                           if($ATTACHMENT_ID != "" && $ATTACHMENT_NAME != "")
                           {
                        ?>
                           <span class="iconbtn attach_icon"></span>
                        <?
                           }
                        ?>
                        <span class="ui-icon-rarrow"></span>
                     </li>
            <?
                  }//while
                  echo "</ul>";
               }else{
                  echo '</ul>';
                  echo '<div class="no_msg">'._("暂无日志！").'</div>';
               }
            ?>
            <?=buildPullUp();?>    
         </div>
      </div>
   </div>
   
   <!-- page of read diary -->
   <div id="page_2" class="pages tcontent" style="display:none;">
      <div id="wrapper_2" class="wrapper">
         <div id="scroller_2" class="scroller"></div>      
      </div>
   </div>
   
   <!-- page of new diary -->
   <div id="page_3" class="pages tcontent" style="display:none;">
      <div id="wrapper_3" class="wrapper tform_wrapper">
         <div id="scroller_3" class="scroller">
            <div class="container">
               <div class="tform">
                  <form action="#"  method="post" name="form1" onsubmit="return false;">
                     <div class="read_detail">
            			   <em><?=_("标题：")?></em>
            			   <?
               			   $query1 = "SELECT PRIV_NAME from USER_PRIV where USER_PRIV='$LOGIN_USER_PRIV'";
                           $cursor1= exequery($connection,$query1);
                           if($ROW1=mysql_fetch_array($cursor1))
                              $LOGIN_PRIV_NAME = $ROW1["PRIV_NAME"];
                           $CAL_DATE=date("Y-m-d",time());   
                           $weeknames=Array(_("星期日"),_("星期一"),_("星期二"),_("星期三"),_("星期四"),_("星期五"),_("星期六"));
                           $dateArr = explode("-", $CAL_DATE);
                           $week=date("w",mktime(0,0,0,$dateArr[1],$dateArr[2],$dateArr[0]));
                           $weekname=$weeknames[$week];
            			      $SUBJECT = $LOGIN_USER_NAME."(".$LOGIN_PRIV_NAME.")".$CAL_DATE." ".$weekname._(" 日志");
            			   ?>
                        <input id="SUBJECT" type="text" name="SUBJECT" value="<?=$SUBJECT?>" style="width:70%;"/>
                     </div>
            			<div class="read_detail">
            			   <em><?=_("类型：")?></em>
                        <select id="DIA_TYPE" name="DIA_TYPE">
                           <?=code_list("DIARY_TYPE")?>
                        </select>
                     </div>
                     <div class="read_detail">
            			   <em><?=_("日期：")?></em>
                        <input id="DATE" type="text" name="DATE" value="<?=date("Y-m-d",time())?>" size="10">   
                     </div>
                     <div class="read_detail endline">
            		      <em><?=_("日志内容：")?></em>
            		      <textarea id="CONTENT" name="CONTENT" rows="10" wrap="on"></textarea>
            		   </div>
         		   </form>
               </div>
            </div>   
         </div>      
      </div>
   </div>
   
   <!-- page of edit diary -->
   <div id="page_4" class="pages tcontent" style="display:none;">
      <div id="wrapper_4" class="wrapper tform_wrapper">
         <div id="scroller_4" class="scroller"></div>      
      </div>
   </div>
   
   <!-- page of attach_file -->
   <div id="page_attach_read" class="pages tcontent" style="display:none;">
      <div id="wrapper_attach_read" class="wrapper">
         <div id="scroller_attach_read" class="scroller" style="position:relative;width:100%;height:100%;">
            <div id="layer" style="position: absolute;left: 0; top: 0;height: 100%; width:100%;"></div>
            <iframe id="file_iframe" name="file_iframe" class="attach_iframe" src="" ></iframe> 
         </div>      
      </div>
   </div>

<script type="text/javascript" src="/pda/js/mobiscroll-2.0.full.min.js<?=$GZIP_POSTFIX?>"></script>
<link href="/pda/style/mobiscroll.sense-ui.css" rel="stylesheet" type="text/css" />

<script type="text/javascript" src="/pda/js/udf-1.1.js"></script>
<script type="text/javascript">
$(document).ready(function(){
   pageInit(1);
   initMobiScrollDate('date',$("#DATE"));
});

function getDiaryContent(dia_id)
{
   $.ajax({
         type: 'GET',
         url: 'read.php',
         cache: false,
         data: {'P': '<?=$P?>','DIA_ID': dia_id},
         beforeSend: function(){
            $.ProLoading.show();   
         },
         success: function(data){
            $.ProLoading.hide();
            $("#page_2 > #wrapper_2 > #scroller_2").empty().append(data);
            $("#page_2").show('fast',function(){pageInit(2);});
            $("#header_1").hide();
            $("#header_2").show();
         },
         error: function(data){
            $.ProLoading.hide();  
            showMessage("<?=_('获取失败')?>");
         }
   });
}

function getDiaryContentEdit(dia_id)
{
   if(!dia_id) return;
   $.ajax({
      type: 'GET',
      url: 'edit.php',
      cache: false,
      data: {'P': '<?=$P?>','DIA_ID': dia_id},
      beforeSend: function()
      {
         $.ProLoading.show();
      },
      success: function(data){
         $.ProLoading.hide();
         $("#page_4 > #wrapper_4 > #scroller_4").empty().append(data);
         $("#page_4").show('fast',function(){pageInit(4);});
         $("#header_2").hide();
         $("#header_4").show();
      }
   });
}

function newDiary()
{
   $("#header_1").hide();
   $("#header_3").show();
   $("#page_3").show('fast',function(){pageInit(3);});   
}

function editDiary(){
   var dia_id = $("#SHOW_DIA_ID").val();
   getDiaryContentEdit(dia_id); 
}

function saveDiary(stype)
{
   
   if(stype == "new")
   {
      var SAVE_TYPE = $("#SAVE_TYPE").val();
      var SUBJECT = $("#SUBJECT").val();
      var DIA_TYPE = $("#DIA_TYPE").val();
      var CONTENT = $("#CONTENT").val();
      var DATE = $("#DATE").val();
      var DIA_ID = '';
   }else{
      var SAVE_TYPE = $("#SAVE_TYPE_EDIT").val();
      var SUBJECT = $("#SUBJECT_EDIT").val();
      var DIA_TYPE = $("#DIA_TYPE_EDIT").val();
      var CONTENT = $("#CONTENT_EDIT").val();
      var DATE = $("#DATE_EDIT").val();
      var DIA_ID = $("#DIA_ID_EDIT").val();
   }

   $.ajax({
      type: 'POST',
      url: 'send.php',
      cache: false,
      data: {'P': '<?=$P?>','DIA_ID': DIA_ID, 'SUBJECT': SUBJECT, 'CONTENT': CONTENT, 'DIA_TYPE': DIA_TYPE, 'DATE': DATE},
      beforeSend: function()
      {
         $.ProLoading.show("<?=_('保存中...')?>");   
      },
      success: function(data)
      {
         if(data =='OK')
         {
            $.ProLoading.hide();
            showMessage("日志保存成功");
            if(stype == 'edit')
            {
               reback(4,2);
               getDiaryContent(DIA_ID);
            }
            if(stype == 'new')
            {
               reback(3,1);
            }
         }else{
            $.ProLoading.hide();
            showMessage(data);
         }
      }
   });
}

$("ul.comm-list li").live("click tap",function(){
   $$a = $(this);
   getDiaryContent($$a.attr("q_id"));
});

// 删除动作
$("ul.ui-listview li .delete_icon").live("tap click",function(e)
{
   e.stopPropagation();
   var oP = $(this).parents("li");
   var dia_id = oP.attr("dia_id");
   $.get("delete.php?P=<?=$P?>",{DIA_ID: dia_id},function(data){
      if(data=="+OK")
         oP.fadeOut(200,function(){oP.remove()});   
   }); 
   return false;       
});

$(".read_attach > a.pda_attach").live("tap click",function(){
   readAttach($(this));
});

</script>  
</body>
</html>
