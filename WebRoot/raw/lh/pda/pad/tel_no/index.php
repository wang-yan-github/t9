<?
   include_once("../inc_header.php");
   include_once("inc/td_core.php");
?>
<body>
<script type="text/javascript">
var stype = "tel_no";

function pageInit(type, page_id){
	if(type=='side'){
		if(page_id == 1)
		{
			tiScroll_1 = new $.tiScroll({"listType": "readonly","page_type": 'side'});
			tiScroll_1.init();
		}
   }
	else{
		if(page_id == 1)
		{
			tiScroll_1_main = new $.tiScroll({"page_type": 'main', "listType": "readonly"});
			tiScroll_1_main.init();
		}    
	}	
}
</script>
<div id="sideContentArea">
<?
   $tHeadData = array(
      "1" => array(
         "c" => array("title" => "区号邮编查询"),
         "r" => array("class" => "","event" => "search();", "title" => "查询"),
      )
   );
?>
<?=buildSiderHead($tHeadData);?>
<?=buildMessage();?>
<?=buildSideProLoading();?>

   <!-- input of search -->

	<div id="sideContentPage_1" class="sideContentPage">
		<div id="sideContentWrapper_1" class="wrapper">
			<div id="contentScroller_1" class="scroller">
      		 <div class="container">
      		   <div class="tform">
                  <form action="/pda/tel_no/search.php" method="get" name="form1" onsubmit="search();return false;">
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
</div>

<div id="mainContentArea">	
<?
   $tHeadData = array(
      "1" => array(
         "c" => array("title" => "区号邮编查询")
      )
   );
	
	echo buildMainHead($tHeadData);
?>
   <!-- page of read notify -->
	<div id="mainContentPage_1" class="mainContentPage">
		<div id="mainContentWrapper_1" class="wrapper">
			<div id="contentScroller_1" class="scroller shadowscroller">
			</div>
		</div>
	</div>
</div>
	
<script type="text/javascript">
$(document).ready(function(){
	tPad.changeLayout();
   pageInit('side',1);   
	pageInit('main',1);
});
$(".preViewList li").die();

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
	pageInit('main',1);
	tiScroll_1_main.getMainData({
      url: '/pda/pad/inc/getdata.php',
		data: {'A':"getTelNOList","STYPE": stype,'AREA': AREA,'TEL_NO': TEL_NO,'POST_NO': POST_NO},
		onSuccess: function(data){
			if(data == "NOFINDDATA"){
				showMessage("<?=_('无符合条件的记录')?>");
            return false;
			}
		},
		showCallback: function(){
			tiScroll_1_main.refresh();
		}
	});

/*    $.ajax({
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
   }); */
}
</script>
</body>
</html>
