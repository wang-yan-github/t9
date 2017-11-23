<?
   include_once("../header.php");
?>
<body>
<script type="text/javascript">
var stype = "address";
var p = "<?=$P?>";
var nonewdata = "<?=_('没有新的通讯薄记录')?>";
var newdata = "<?=_('%s个新通讯薄记录')?>";


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
}
</script>
<?
   $tHeadData = array(
      "1" => array(
         "l" => array("class" => "","event" => "gohome();", "title" => "首页"),
         "c" => array("title" => "通讯簿查询"),
         "r" => array("class" => "","event" => "search();", "title" => "查询"),
      ),
      "2" => array(
         "l" => array("class" => "","event" => "reback(2,1);", "title" => "返回"),
         "c" => array("title" => "查询结果")
      ),
      "3" => array(
         "l" => array("class" => "","event" => "reback(3,2);", "title" => "返回"),
         "c" => array("title" => "查看详情")
      )
   );
?>
<?=buildHead($tHeadData);?>
<?=buildMessage();?>
<?=buildProLoading();?>

   <!-- page of search input -->
   <div id="page_1" class="pages tcontent">
      <div id="wrapper_1" class="wrapper tform_wrapper">
         <div id="scroller_1" class="scroller">
            <div class="container">
      		   <div class="tform">
                  <form action="#"  method="get" name="form1" onsubmit="return false;">
                     <div class="read_detail">
                        <?=_("姓名：")?>
                        <input type="text" id="PSN_NAME" name="PSN_NAME" value=""/>
                     </div>
                     <div class="read_detail endline">
                        <?=_("单位：")?>
                        <input type="text" id="DEPT_NAME" name="DEPT_NAME" />
                     </div>
                  </form>
               </div>
            </div>
         </div>      
      </div>
   </div>
   
   <!-- page of search result -->
   <div id="page_2" class="pages tlist"  style="display:none;">
      <div id="wrapper_2" class="wrapper">
         <div id="scroller_2" class="scroller">
            <ul class="comm-list"></ul>
         </div>      
      </div>
   </div>
   
   <!-- page of result detail -->
   <div id="page_3" class="pages tcontent" style="display:none;">
      <div id="wrapper_3" class="wrapper tform_wrapper">
         <div id="scroller_3" class="scroller"></div>      
      </div>
   </div>
   
<script type="text/javascript" src="/pda/js/udf-1.1.js"></script>
<script type="text/javascript">
$(document).ready(function(){
   pageInit(1);   
});

function search()
{
   var ob1 = $("#PSN_NAME");
   var ob2 = $("#DEPT_NAME");
   var PSN_NAME = ob1.val();
   var DEPT_NAME = ob2.val();
   if(PSN_NAME=="" && DEPT_NAME=="" )
   {
      showMessage("<?=_('请输入要查询人员的姓名或单位')?>");
      return false;
   }
   getPsnList(PSN_NAME,DEPT_NAME);     
}

function clearSearch(){
   $("#PSN_NAME").val("");
   $("#DEPT_NAME").val("");
}

function getPsnList(PSN_NAME,DEPT_NAME)
{
   $.ajax({
      type: 'GET',
      url: '/pda/inc/getdata.php',
      cache: true,
      data: {"A":"getPsnList","STYPE": stype,'PSN_NAME': PSN_NAME,'DEPT_NAME': DEPT_NAME},
      beforeSend: function()
      {
         $.ProLoading.show("<?=_('查询中...')?>");   
      },
      success: function(data){
         $.ProLoading.hide();
         if(data == "NOFINDDATA")
         {
            showMessage("<?=_('无符合条件的记录')?>");
            return;
         }else{
            $("#header_1").hide();
            $("#header_2").show();
            $("#page_2 > #wrapper_2 > #scroller_2 ul").empty().append(data);
            $("#page_2").show('fast',function(){pageInit(2);});
         }
      },
      error: function(data){
         $.ProLoading.hide();  
         showMessage("<?=_('获取失败')?>");
      }
   });
}

function getPSNDetailContent(ADD_ID){
   $.ajax({
      type: 'GET',
      url: 'detail.php',
      cache: true,
      data: {'ADD_ID': ADD_ID},
      beforeSend: function()
      {
         $.ProLoading.show("<?=_('获取中...')?>");   
      },
      success: function(data){
         $.ProLoading.hide();   
         $("#page_3 > #wrapper_3 > #scroller_3").empty().append(data);
         $("#page_3").show('fast',function(){pageInit(3);});
         $("#header_2").hide();
         $("#header_3").show();
      },
      error: function(data){
         $.ProLoading.hide();  
         showMessage("<?=_('获取失败')?>");
      }
   });
}
$("ul.comm-list li").live("click tap",function(){
   $$a = $(this);
   getPSNDetailContent($$a.attr("q_id"));
});

</script>
</body>
</html>
