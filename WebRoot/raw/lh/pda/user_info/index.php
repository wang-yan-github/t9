<?
   include_once("../header.php");
   include_once("inc/td_core.php");
   $query = "SELECT count(*) from USER,USER_PRIV,DEPARTMENT where DEPARTMENT.DEPT_ID!=0 and USER.USER_PRIV=USER_PRIV.USER_PRIV and USER.DEPT_ID=DEPARTMENT.DEPT_ID order by PRIV_NO,USER_NO,USER_NAME ";
   $TOTAL_ITEMS = resultCount($query);
?>
<body>
<script type="text/javascript">
var stype = "user_info";
var p = "<?=$P?>";
var nonewdata = "<?=_('没有新的人员信息')?>";
var newdata = "<?=_('%s个新的人员信息')?>";

/* --- 自定义参数 ---*/
var nomoredata_2 = false;
var noshowPullUp_2 = <? echo ($PAGE_SIZE >= $TOTAL_ITEMS) ? "true" : "false"; ?>;

function pageInit(page_id)
{
   if(page_id == 1)
   {
      tiScroll_1 = new $.tiScroll({"listType": "readonly"});
      tiScroll_1.init();
   }
   if(page_id == 2)
   {
      tiScroll_2 = new $.tiScroll({"page_id": 2, "nomoredata": nomoredata_2, "noshowPullUp":noshowPullUp_2});
      tiScroll_2.init();
   }
   if(page_id == 3)
   {
      tiScroll_3 = new $.tiScroll({"page_id": 3, "listType": "readonly"});
      tiScroll_3.init();
   }
}
var from_page = 1;
</script>
<?
   $tHeadData = array(
      "1" => array(
         "l" => array("class" => "","event" => "gohome();", "title" => "首页"),
         "c" => array("title" => "人员查询"),
         "r" => array("class" => "","event" => "Gotolist();", "title" => "所有")
      ),
      "2" => array(
         "l" => array("class" => "","event" => "reback(2,1);", "title" => "返回"),
         "c" => array("title" => "人员列表"),
      ),
      "3" => array(
         "l" => array("class" => "","event" => "goback();", "title" => "返回"),
         "c" => array("title" => "人员详情")
      )
   );
?>
<?=buildHead($tHeadData);?>
<?=buildMessage();?>
<?=buildProLoading();?>

   <!-- page of search input -->
   <div id="page_1" class="pages tlist">
      <div id="wrapper_1" class="wrapper">
         <div id="scroller_1" class="scroller">
            <div id="search_box">
               <div id="input_box">
                  <input type="text" id="USER_NAME" name="USER_NAME" value="" autocapitalize="off" autocorrect="off"/>   
               </div>
            </div>
            <ul class="comm-list" id="contactlist_1"></ul>       
         </div>      
      </div>
   </div>

   <!-- all of search result -->
   <div id="page_2" class="pages tlist"  style="display:none;">
      <div id="wrapper_2" class="wrapper">
         <div id="scroller_2" class="scroller">
      <?
         echo buildPullDown();
         echo '<ul class="comm-list comm-pic-list" id="all_user">';
         if($TOTAL_ITEMS > 0)
         {
      		$query = "SELECT UID,SEX,AVATAR,USER_ID,USER_NAME,USER.DEPT_ID,PRIV_NAME from USER,USER_PRIV,DEPARTMENT where DEPARTMENT.DEPT_ID!=0 and USER.USER_PRIV=USER_PRIV.USER_PRIV and USER.DEPT_ID=DEPARTMENT.DEPT_ID order by PRIV_NO,USER_NO,USER_NAME limit 0,$PAGE_SIZE";
      		$cursor= exequery($connection,$query);
      		while($ROW=mysql_fetch_array($cursor))
      		{
      		   $UID=$ROW["UID"];
      		   $USER_NAME=$ROW["USER_NAME"];
      		   $PRIV_NAME=$ROW["PRIV_NAME"];
      		   $DEPT_ID=$ROW["DEPT_ID"];
      		   $SEX=$ROW["SEX"];
               $AVATAR=$ROW["AVATAR"];
      
      		   $DEPT_LONG_NAME=dept_long_name($DEPT_ID);
      		
      		   if($SEX==0)
      		      $SEX=_("男");
      		   else
      		      $SEX=_("女");
   		?>
            <li class="<?=$fix_for_pad['list-li-style']?>" q_id="<?=$UID?>">
               <img src="<?=showAvatar($AVATAR,$SEX)?>" class="ui-li-thumb"/>
               <h3><?=$USER_NAME?><?=_("(")?><?=$SEX?><?=_(")")?></h3>
               <p class="w100 grapc"><?=_("部门：")?><?=$DEPT_LONG_NAME?> <?=_("角色：")?><?=$PRIV_NAME?></p>
               <span class="ui-icon-rarrow"></span>
            </li>
   		<?
   		   }//while
   		   echo "</ul>";
         }else{
   		   echo "</ul>";
   		   echo '<div class="no_msg">'._("暂无人员！").'</div>';
   		}
      ?>
         <?=buildPullUp();?> 
         </div>      
      </div>
   </div>

   <!-- page of result detail -->
   <div id="page_3" class="pages tcontent"  style="display:none;">
      <div id="wrapper_3" class="wrapper tform_wrapper">
         <div id="scroller_3" class="scroller"></div>      
      </div>
   </div>

<script type="text/javascript" src="/pda/js/udf-1.1.js"></script>
<script>
$(document).ready(function(){
   pageInit(1);   
});

function getUserDetailContent(UID,FROM){
   $.ajax({
      type: 'GET',
      url: 'detail.php',
      cache: true,
      data: {'P': '<?=$P?>','UID': UID},
      beforeSend: function()
      {
         $.ProLoading.show("<?=_('获取中...')?>");   
      },
      success: function(data)
      {
         $.ProLoading.hide(); 
         if(FROM == 'all_user'){
            $("#header_2").hide();
            from_page = 2;
         }else{
            $("#header_1").hide();
            $("#page_1").hide();
            from_page = 1;
         }
         $("#header_3").show();
         $("#page_3 > #wrapper_3 > #scroller_3").empty().append(data);
         $("#page_3").show('fast',function(){pageInit(3);});
      }
   });
}
$("ul.comm-list li").live("click tap",function(){
   $$a = $(this);
   getUserDetailContent($$a.attr("q_id"),$$a.parent("ul").attr("id"));
   $$a.removeClass("active");
});

function Gotolist(){
   //reback(from_page,1);
   $("#header_1").hide();
   $("#header_2").show();
   $("#page_2").show('fast',function(){pageInit(2);});
}

function goback(){
   reback(3,from_page);
   if(from_page == 2){
      Gotolist();
   }
}

tSearch2 = new $.tSearch2({page_id:1, input:"#USER_NAME", list:"#contactlist_1"});
tSearch2.init();
</script>
</body>
</html>
