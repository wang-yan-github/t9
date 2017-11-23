<?
   include_once("../header.php");
   include_once("inc/td_core.php");
?>
<body>
<script type="text/javascript">
var stype = "tel_no";
var p = "<?=$P?>";

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
}
</script>
<?
   $tHeadData = array(
      "1" => array(
         "l" => array("class" => "","event" => "gohome();", "title" => "首页"),
         "c" => array("title" => "区号邮编查询"),
         "r" => array("class" => "","event" => "search();", "title" => "查询"),
      ),
      "2" => array(
         "l" => array("class" => "","event" => "reback(2,1);", "title" => "返回"),
         "c" => array("title" => "区号邮编查询")
      )
   );
?>
<?=buildHead($tHeadData);?>
<?=buildMessage();?>
<?=buildProLoading();?>

   <!-- input of search -->
   <div id="page_1" class="pages tcontent">
      <div id="wrapper_1" class="wrapper tform_wrapper">
         <div id="scroller_1" class="scroller">
      		 <div class="container">
      		   <div class="tform">
                  <form action="/pda/tel_no/search.php" method="get" name="form1" onsubmit="return false;">
                     <div class="read_detail read_detail_header">
                        <?=_("市/区/县/街道的名称包含：")?>
                     </div>
                     <div class="read_detail">
                        <input type="text" id="AREA" name="AREA" value="" style="width:90%;"/>   
                     </div>
                     <div class="read_detail read_detail_header">
                        <?=_("区号：")?>
                     </div>
                     <div class="read_detail">
                        <input type="text" id="TEL_NO" name="TEL_NO" value=""/>
                     </div>
                     <div class="read_detail read_detail_header">
                        <?=_("邮编：")?>
                     </div>
                     <div class="read_detail endline">
                        <input type="text" id="POST_NO" name="POST_NO" value=""/>
                     </div>
                  </form>
               </div>
      		</div>
         </div>      
      </div>
   </div>


   <!-- page of read notify -->
   <div id="page_2" class="pages tcontent" style="display:none;">
      <div id="wrapper_2" class="wrapper">
         <div id="scroller_2" class="scroller">
            <ul class="comm-list"></ul>
         </div>      
      </div>
   </div>
   
<script type="text/javascript" src="/pda/js/udf-1.1.js"></script>
<script type="text/javascript">
$(document).ready(function(){
   pageInit(1);   
});
function search()
{
   var ob1 = $("#AREA");
   var ob2 = $("#TEL_NO");
   var ob3 = $("#POST_NO");
   var AREA = ob1.val();
   var TEL_NO = ob2.val();
   var POST_NO = ob3.val();
   if(AREA=="" && TEL_NO=="" && POST_NO=="")
   {
      showMessage("<?=_('请输入要查询的名称、区号或邮编')?>");
      return false;
   }
   getTelNOList(AREA,TEL_NO,POST_NO);     
}
function clearSearch()
{
   $("#AREA").val("");
   $("#TEL_NO").val("");
   $("#POST_NO").val("");
}
function getTelNOList(AREA,TEL_NO,POST_NO)
{
   $.ajax({
      type: 'GET',
      url: '/pda/inc/getdata.php',
      cache: false,
      data: {'A':"getTelNOList","STYPE": stype,'AREA': AREA,'TEL_NO': TEL_NO,'POST_NO': POST_NO},
      beforeSend: function()
      {
         $.ProLoading.show("<?=_('获取中...')?>");   
      },
      success: function(data)
      {
         $.ProLoading.hide();
         if(data == "NOFINDDATA")
         {
            showMessage("<?=_('无符合条件的记录')?>");
            return;
         }else{
            $("#page_2 > #wrapper_2 > #scroller_2 > ul").empty().append(data);
            $("#page_2").show('fast',function(){pageInit(2);});
            $("#header_1").hide();
            $("#header_2").show();
         }
      }
   });
}
</script>
</body>
</html>
