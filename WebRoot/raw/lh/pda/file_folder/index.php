<?
   include_once("../header.php");
   include_once("inc/utility_all.php");
   
   //lp 2012/5/24 0:08:47 根目录文件判断
   $query = "SELECT count(*) from FILE_CONTENT where SORT_ID=0 and  USER_ID='$LOGIN_USER_ID'";
   $TOTAL_ITEMS_ROOT = resultCount($query);
   
   //lp 2012/5/24 0:09:01 含有子目录的判断
   $query = "SELECT count(*) from file_sort where SORT_TYPE = 4 and USER_ID='$LOGIN_USER_ID' and SORT_PARENT=0";
   $TOTAL_ITEMS_SUB_FOLDER = resultCount($query);
   
   $TOTAL_ITEMS = $TOTAL_ITEMS_ROOT + $TOTAL_ITEMS_SUB_FOLDER;
?>
<body>
<script type="text/javascript">
var stype = "file_folder";
var p = "<?=$P?>";
var nonewdata = "<?=_('没有新的文件')?>";
var newdata = "<?=_('%s个新文件')?>";
var noaccesspriv = "<?=_('您没有权限访问该目录')?>";
var now_sort = 0;
var last_sort = 0;
/* --- 自定义参数 ---*/
var nomoredata_1 = true;
var noshowPullUp_1 = true;

function pageInit(page_id)
{
   if(page_id == 1)
   {
      tiScroll_1 = new $.tiScroll({"listType": "readonly"});
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
   
   if(page_id == "attach_read")
   {
      tiScroll_attach_read = new $.tiScroll({"page_id": "attach_read", "listType": "attach_show"});
      tiScroll_attach_read.init();      
   }
}
function new_reback()
{
   if(now_sort == last_sort)
      return ;
   if(last_sort == 0){
      reback(2,1);
      now_sort = 0;
      return ;
   }

   getfileContentlist(last_sort);
}
function new_reback2()
{
   if(now_sort == 0)
      reback(3,1);
   else
      reback(3,2);
}
</script>
<?
   $tHeadData = array(
      "1" => array(
         "l" => array("class" => "","event" => "gohome();", "title" => "首页"),
         "c" => array("title" => "个人文件柜目录")
      ),
      "2" => array(
         "l" => array("class" => "","event" => "new_reback();", "title" => "返回"),
         "c" => array("title" => "")
      ),
      "3" => array(
         "l" => array("class" => "","event" => "new_reback2();", "title" => "返回"),
         "c" => array("title" => "阅读文件")
      ),
      "attach_read" => array(
         "l" => array("class" => "","event" => "reback(\"attach_read\",3);", "title" => "返回"),
         "c" => array("title" => "查看附件")
      )
   );
?>
<?=buildHead($tHeadData);?>
<?=buildMessage();?>
<?=buildProLoading();?>
   
   <!-- all of root folders -->
   <div id="page_1" class="pages tlist">
      <div id="wrapper_1" class="wrapper">
         <div id="scroller_1" class="scroller">
         <ul class="comm-list comm-pic-list">

   <? 
      if($TOTAL_ITEMS > 0)
      {
         if($TOTAL_ITEMS_SUB_FOLDER > 0)
         {
      		 //============================ 显示根目录下目录 =======================================
            $query = "SELECT SORT_NAME,SORT_ID from file_sort where SORT_TYPE = 4 and USER_ID='$LOGIN_USER_ID' and SORT_PARENT=0 order by SORT_ID ASC";
            $cursor= exequery($connection,$query);
            while($ROW=mysql_fetch_array($cursor))
            {
               $SORT_NAME = $ROW["SORT_NAME"];
               $SORT_ID = $ROW["SORT_ID"];
   ?>
   		<li class="folder <?=$fix_for_pad['list-li-style']?><?=$Class?>" q_id="<?=$SORT_ID?>" q_name="<?=$SORT_NAME?>">
               <img src="../style/images/folder.png" class="ui-li-thumb"/>
               <h3><?=$SORT_NAME?></h3>
               <p class="w100 grapc"><?=_("一级目录")?></p>
               <span class="ui-icon-rarrow"></span>
         </li>
   <?
            }//while
         }
   
         if($TOTAL_ITEMS_ROOT > 0)
         {
      		 //============================ 显示根目录下文件 =======================================
            $query = "SELECT READERS,CONTENT_ID,SUBJECT,SEND_TIME,ATTACHMENT_ID,ATTACHMENT_NAME from FILE_CONTENT where SORT_ID=0 and USER_ID='$LOGIN_USER_ID' order by CONTENT_ID desc";// limit 0,$PAGE_SIZE
            $cursor= exequery($connection,$query);
            while($ROW=mysql_fetch_array($cursor))
            {
               $READERS = $ROW["READERS"];
               $CONTENT_ID = $ROW["CONTENT_ID"];
               $SUBJECT = $ROW["SUBJECT"];
               $SEND_TIME = $ROW["SEND_TIME"];
               $ATTACHMENT_ID = $ROW["ATTACHMENT_ID"];
               $ATTACHMENT_NAME = $ROW["ATTACHMENT_NAME"];
               $SUBJECT=htmlspecialchars($SUBJECT);
   ?>
   		<li class="files <?=$fix_for_pad['list-li-style']?><?=$Class?>" q_id="<?=$CONTENT_ID?>">
               <img src="<?=file_icon($ATTACHMENT_NAME)?>" class="ui-li-thumb"/>
               <h3><?=$SUBJECT?></h3>
               <p class="w100 grapc"><?=timeintval(strtotime($SEND_TIME))?></p>
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
         }
            echo "</ul>";
      }else
      {
            echo "</ul>";    
            echo "<div class='no_msg'>"._("个人文件柜根目录无文件")."</div>";
      } 
    ?>  
         </div>      
      </div>
   </div>

   <!-- all of files -->
   <div id="page_2" class="pages tlist" style="display:none;">
      <div id="wrapper_2" class="wrapper">
         <div id="scroller_2" class="scroller">
            <ul class="comm-list comm-pic-list">
            </ul>
         </div>      
      </div>
   </div>


   <!-- page of read file -->
   <div id="page_3" class="pages tcontent" style="display:none;">
      <div id="wrapper_3" class="wrapper">
         <div id="scroller_3" class="scroller"></div>      
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

<script type="text/javascript" src="/pda/js/udf-1.1.js"></script>
<script>
$(document).ready(function(){
   pageInit(1);   
});
function getfileContent(CONTENT_ID)
{
   $.ajax({
      type: 'GET',
      url: 'read.php',
      cache: true,
      data: {'P': '<?=$P?>','CONTENT_ID': CONTENT_ID},
      beforeSend: function(){
            $.ProLoading.show();   
         },
      success: function(data){
         $.ProLoading.hide();  
         $("#page_3 > #wrapper_3 > #scroller_3").empty().append(data);
         $("#page_3").show('fast',function(){pageInit(3);});
         $("#header_1").hide();
         $("#header_2").hide();
         $("#header_3").show();
      },
      error: function(data){
         $.ProLoading.hide();  
         showMessage("<?=_('获取失败')?>");
      }
   });
}
function getfileContentlist(PARENT_SORT,SORT_NAME)
{
   $.ajax({
      type: 'GET',
      url: 'file_folder.php',
      cache: true,
      data: {'P': '<?=$P?>','SORT_ID': PARENT_SORT},
      beforeSend: function(){
            $.ProLoading.show();   
         },
      success: function(data){
         $.ProLoading.hide();  
         if(data == 'NOMOREDATA'){
            showMessage("<?=_('此目录没有文件！')?>");
            return;
         }else if(data == 'NOACCESSPRIV'){
            showMessage(noaccesspriv);
            return;
         }else{
            $("#page_2 > #wrapper_2 > #scroller_2 ul").empty().append(data);
            $("#page_2").show('fast',function(){pageInit(2);});
            $("#header_1").hide();
            $("#header_2").show();  
         }
      },
      error: function(data){
         $.ProLoading.hide();  
         showMessage("<?=_('获取失败')?>");
      }
   });
   $("#header_2").find(".t").text(SORT_NAME);
}

$("ul.comm-list li.files").live("click tap",function(){
   $$a = $(this);
   getfileContent($$a.attr("q_id"));
});
$("ul.comm-list li.folder").live("click tap",function(){
   $$a = $(this);
   getfileContentlist($$a.attr("q_id"),$$a.attr("q_name"));
});

$(".read_attach > a.pda_attach").live("tap click",function(){
   readAttach($(this));   
});

</script>
</body>
</html>
