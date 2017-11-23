<?
   include_once("../header.php");
   include_once("inc/td_core.php");
?>
<body>
<script type="text/javascript">
var stype = "weather";
var p = "<?=$P?>";

function pageInit(page_id){
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
         "l" => array("class" => "","event" => "gohome();", "title" => "��ҳ"),
         "c" => array("title" => "����Ԥ��"),
         "r" => array("class" => "","event" => "search();", "title" => "��ѯ"),
      ),
      "2" => array(
         "l" => array("class" => "","event" => "clearSearch();reback(2,1);", "title" => "����"),
         "c" => array("title" => "�鿴����")
      )
   );
?>
<?=buildHead($tHeadData);?>
<?=buildMessage();?>
<?=buildProLoading();?>

   <!-- all of notify -->
   <div id="page_1" class="pages tcontent">
      <div id="wrapper_1" class="wrapper tform_wrapper" >
         <div id="scroller_1" class="scroller">
         <div class="container">
      		<div class="tform tformshow">
      		   <form action="/pda/weather/index.php" method="get" name="form1" onsubmit="return false;">
         		   <div class="read_detail">
                     <em><?=_("��ѯ�������ƣ�")?></em>
                     <input name="WEATHER_CITY" id="WEATHER_CITY" type="text" value="<?=$WEATHER_CITY?>" />
                  </div>
                  <div class="read_detail read_detail_header"><?=_("��ʷ��ѯ")?></div>
                  <div class="read_detail city_list" id='city_cookie'></div>
                  <div class="read_detail read_detail_header"><?=_("���ų���")?></div>
              <?
                  $city_list = array("����","�Ϻ�","����","����","���","����","�Ͼ�");
                  foreach($city_list as $city){
              ?>
                  <div class="read_detail city_list"><?=$city?></div>
              <?
                  }
              ?>
         		</form>
            </div>
         </div>
         </div>      
      </div>
   </div>


   <!-- page of read weather -->
   <div id="page_2" class="pages tcontent" style="display:none;">
      <div id="wrapper_2" class="wrapper">
         <div id="scroller_2" class="scroller"></div>      
      </div>
   </div>

<script type="text/javascript" src="/pda/js/udf-1.1.js"></script>
<script type="text/javascript">
$(document).ready(function(){
   $("#city_cookie").html(echoCookie());
   pageInit(1);
   $("#page_1 .container .read_detail:last").addClass("endline");
});

function search()
{
   var ob = $("#WEATHER_CITY");
   var WEATHER_CITY = ob.val();
   if(WEATHER_CITY=="")
   {
      showMessage("<?=_("������Ҫ��ѯ�ĳ�������")?>");
      return false;
   }
   getWeatherContent(WEATHER_CITY);     
}

function clearSearch(){
   $("#WEATHER_CITY").val("");
}
function getWeatherContent(WEATHER_CITY)
{
   if(WEATHER_CITY == '') return ;
   setCookie("city_cookie",WEATHER_CITY);
   $("#city_cookie").html(getCookie("city_cookie"));

   $.ajax({
      type: 'GET',
      url: 'search.php',
      cache: true,
      data: {'P': '<?=$P?>','WEATHER_CITY': WEATHER_CITY},
      beforeSend: function()
      {
         $.ProLoading.show("<?=_('��ȡ��...')?>");   
      },
      success: function(data){
         $.ProLoading.hide();  
         $("#page_2 > #wrapper_2 > #scroller_2").empty();
         $("#page_2 > #wrapper_2 > #scroller_2").append(data);
         $("#page_2").show('fast',function(){pageInit(2);});
         $("#header_1").hide();
         $("#header_2").show();
      },
      error: function(data){
         $.ProLoading.hide();  
         showMessage("<?=_('��ȡʧ��')?>");
      }
   });
}

$(".city_list").live("click tap",function(){
   $$a = $(this);
   getWeatherContent($$a.text());
});
</script>   
</body>
</html>
