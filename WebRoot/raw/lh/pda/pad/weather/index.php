<?
   include_once("../inc_header.php");
   include_once("inc/td_core.php");
?>
<body>
<script type="text/javascript">
var stype = "weather";

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
         "c" => array("title" => "天气预报"),
         "r" => array("class" => "","event" => "search();", "title" => "查询"),
      ),
      "2" => array(
         "c" => array("title" => "查看天气")
      )
   );
?>
<?=buildSiderHead($tHeadData);?>
<?=buildMessage();?>
<?=buildSideProLoading();?>

   <!-- all of notify -->
	<div id="sideContentPage_1" class="sideContentPage" style="">
		<div id="sideContentWrapper_1" class="wrapper">
			<div id="contentScroller_1" class="scroller">
         <div class="container">
      		<div class="tform tformshow">
      		   <form action="/pda/weather/index.php" method="get" name="form1" onsubmit="return false;">
         		   <div class="read_detail">
                     <em><?=_("查询城市名称：")?></em>
                     <input name="WEATHER_CITY" id="WEATHER_CITY" type="text" value="<?=$WEATHER_CITY?>" />
                  </div>
                  <div class="read_detail read_detail_header"><?=_("历史查询")?></div>
                  <div class="read_detail city_list" id='city_cookie'></div>
                  <div class="read_detail read_detail_header"><?=_("热门城市")?></div>
              <?
                  $city_list = array("北京","上海","广州","深圳","香港","重庆","南京");
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
</div>

<div id="mainContentArea">	
	<?=buildMainProLoading()?>
	<?
	   $tHeadData = array(
			"1" => array(
				//"l" => array("class" => "","event" => "clearSearch();reback(2,1);", "title" => "返回"),
				"c" => array("title" => "查看天气")
			)
		);
	?>
	<?=buildMainHead($tHeadData)?>
   <!-- page of read weather -->
	<div id="mainContentPage_1" class="mainContentPage tzoom">
		<div id="mainContentWrapper_1" class="wrapper">
			<div id="contentScroller_1" class="scroller shadowscroller">
			</div>
		</div>
	</div>
</div>
<script type="text/javascript">
$(document).ready(function(){
	tPad.changeLayout('side');
   $("#city_cookie").html(echoCookie());
	getWeatherContent(echoCookie());
   pageInit('side',1);
   $("#page_1 .container .read_detail:last").addClass("endline");
});

function search()
{
   var ob = $("#WEATHER_CITY");
   var WEATHER_CITY = ob.val();
   if(WEATHER_CITY=="")
   {
      showMessage("<?=_("请输入要查询的城市名称")?>");
      return false;
   }
   getWeatherContent(WEATHER_CITY);     
}

function clearSearch(){
   $("#WEATHER_CITY").val("");
}
function getWeatherContent(WEATHER_CITY)
{
   if(WEATHER_CITY.trim() == '') return ;
	tPad.changeLayout('both');
   setCookie("city_cookie",WEATHER_CITY);
   $("#city_cookie").html(getCookie("city_cookie"));

	pageInit('main',1);
	tiScroll_1_main.getMainData({
      url: 'weather/search.php',
      data: {'P': '<?=$P?>','WEATHER_CITY': WEATHER_CITY},
		onSuccess: function(data){
			var html = [];
			tiScroll_1_main.getHeader().html(WEATHER_CITY);
			if(data == '无该城市的天气数据'){
				html.push('<div class="no_msg">无该城市的天气数据</div>');
			}else{
				var weather = $.parseJSON(data);
				html = ['<div class="weather-result">'];
				$.each(weather, function(i,n){
					if(i>1){
						return false;
					}
					html.push('<div class="day">');
					html.push('<h3>' + this.date + '</h3>');
					this.img1 < 32 && html.push('<img src="/images/weather/a' + this.img1 +'.gif"/>');
					this.img2 < 32 && html.push('<img src="/images/weather/a' + this.img2 +'.gif"/>');
					html.push('<br>');
					html.push('<p>' + this.temperature + '</p>');
					html.push('<p>' + this.weather + '</p>');
					html.push('<p>' + this.wind + '</p>');
					html.push('</div>');
				
				});
				html.push('</div>');
			}
			tiScroll_1_main.getOIScroll().scroller.innerHTML = html.join('');
			return false; 
		}
	});
	
	/* 
   $.ajax({
      type: 'GET',
      url: 'search.php',
      cache: true,
      data: {'P': '<?=$P?>','WEATHER_CITY': WEATHER_CITY},
      beforeSend: function()
      {
         $.ProLoading.show("<?=_('获取中...')?>");   
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
         showMessage("<?=_('获取失败')?>");
      }
   }); */
}

$(".city_list").die().live("click tap",function(){
   getWeatherContent($(this).text());
});
</script>   
</body>
</html>
