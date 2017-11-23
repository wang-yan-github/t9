<?
include_once("header.php");
include_once("inc/utility.php");
include_once("inc/cache/cache.php");


$SYS_INTERFACE = $td_cache->get("SYS_INTERFACE");
if(!is_array($SYS_INTERFACE))
{
   include_once("inc/utility_all.php");
   cache_interface();
}

$WEATHER_CITY=$SYS_INTERFACE["WEATHER_CITY"];

if(strlen($WEATHER_CITY)==5)
{
   $query = "SELECT WEATHER_CITY from USER where UID='$LOGIN_UID'";
   $cursor= exequery($connection,$query);
   if($ROW=mysql_fetch_array($cursor))
   {
      if(strlen($ROW["WEATHER_CITY"]) == 5)
         $WEATHER_CITY = $ROW["WEATHER_CITY"];
      else
         $WEATHER_CITY = '';
   }
}

include_once("count.php");

$MODULE_ARRAY = array();
if(find_id($LOGIN_FUNC_STR, "3"))
   $MODULE_ARRAY[] = array('text' => _("��������"), 'href' => 'sms', 'module' => 'sms');
if(find_id($LOGIN_FUNC_STR, "1"))
   $MODULE_ARRAY[] = array('text' => _("�����ʼ�"), 'href' => 'email', 'module' => 'email');
if(find_id($LOGIN_FUNC_STR, "4"))
   $MODULE_ARRAY[] = array('text' => _("����֪ͨ"), 'href' => 'notify', 'module' => 'notify');
if(find_id($LOGIN_FUNC_STR, "5"))
   $MODULE_ARRAY[] = array('text' => _("������"), 'href' => 'workflow', 'module' => 'workflow');
if(find_id($LOGIN_FUNC_STR, "147"))
   $MODULE_ARRAY[] = array('text' => _("�ڲ�����"), 'href' => 'news', 'module' => 'news');
   
if(find_id($LOGIN_FUNC_STR, "8"))
 //  $MODULE_ARRAY[] = array('text' => _("�ճ̰���"), 'href' => 'calendar', 'module' => 'calendar');
if(find_id($LOGIN_FUNC_STR, "9"))
 //  $MODULE_ARRAY[] = array('text' => _("������־"), 'href' => 'diary', 'module' => 'diary');
if(find_id($LOGIN_FUNC_STR, "16"))
 //  $MODULE_ARRAY[] = array('text' => _("�����ļ���"), 'href' => 'file_folder', 'module' => 'folder');
   
$MODULE_ARRAY[] = array('text' => _("��Ա��ѯ"), 'href' => 'user_info', 'module' => 'query');

if(find_id($LOGIN_FUNC_STR, "10"))
  // $MODULE_ARRAY[] = array('text' => _("ͨѶ��"), 'href' => 'address', 'module' => 'address');
if(find_id($LOGIN_FUNC_STR, "21") || find_id($LOGIN_FUNC_STR, "22"))
   $MODULE_ARRAY[] = array('text' => _("�����ʱ�"), 'href' => 'tel_no', 'module' => 'zipcode');
if(strlen($WEATHER_CITY)==5)
   $MODULE_ARRAY[] = array('text' => _("����Ԥ��"), 'href' => 'weather', 'module' => 'weather');

$MODULE_ARRAY[] = array('text' => _("�������"), 'href' => 'settings', 'module' => 'settings');
?>
<script type="text/javascript">
var scrollNav;
function loaded() {
	//sideScrollContent = new iScroll('sideContentWrapper');
	//mainScrollContent = new iScroll('mainContentWrapper');
	scrollNav = new iScroll('navWrapper');
}
document.addEventListener('touchmove', function (e) { e.preventDefault(); }, false);
document.addEventListener('DOMContentLoaded', loaded, false);
</script>
<body>
<div id="page">
   
	<div id="sidebar">
	   <div id="loginInfo">
	      <div id="loginInfo-img"><img src="<?=showAvatar($LOGIN_AVATAR,1)?>" /></div>
	      <div id="loginInfo-info"><span><?=$LOGIN_USER_NAME?></span></div>
	   </div>
	   <div id="iconsBar">
	      <a href="javascript:void(0);" class="refresh"><?=_("ˢ��")?></a>
	      <a href="javascript:void(0);" class="message"><?=_("΢Ѷ")?> </a>
	      <a href="javascript:void(0);" class="skins"><?=_("����")?></a>  
	      <a href="javascript:void(0);" class="setting"><?=_("����")?></a>
	      <a href="javascript:void(0);" class="current relogin"><?=_("ע��")?></a> 
	   </div>
		<nav id="navWrapper">
			<ul id="navScroller">
			   <? 
			   foreach($MODULE_ARRAY as $v)
			   {
			      if(!$v) continue;
               $unread = $MODULE_ARRAY_COUNT[$v['module']] == 0 ? "" : "<i>".$MODULE_ARRAY_COUNT[$v['module']]."</i>";
		         echo '<li><a href="javascript:void(0);" _href="'.$v['href'].'" class="app app_'.$v['module'].'">'.$v['text'].$unread.'</a></li>';   
			   } 
			   ?>
			</ul>
		</nav>
	</div>
   
	<div id="contentArea"></div>
	<div id="g-overlay" class='overlay'></div>
	<div id="popPanelArea" style="display:none;">
		<div id="popPanelAreaWrapper" >
			<div id="popheader" class="header">
				<div id="popheader">
					<span class="t"></span>
				</div>
			</div>
			<div id="popContentPage" class="popContentPage">
				<div id="popContentWrapper" class="wrapper">
					<div id="popScroller" class="scroller" style="overflow: hidden; ">
		
					</div>
				</div>
			</div>
		</div>
	</div>
	
	
	
	
</div>
<script type="text/javascript" src="/pda/pad/js/udf-1.1.js"></script>
<script type="text/javascript" src="/pda/pad/js/tPad.js"></script>

<script type="text/javascript">
$(document).ready(function(){
	var lastData,lastAjaxTimer;						//��ʱ����������Ϣ��������¼�ϴ�json
	getCount_mon();
	window.unreadCount_mon = window.setInterval(getCount_mon, 60*1000);	//��ʱ����

	$("#navScroller li").live("click", function(){
	
		var url = $(this).find("a").attr("_href");
		var now = lastAjaxTimer = (new Date).getTime();
		'stopGetNewMsg' in window && stopGetNewMsg();				//ȡ��΢Ѷ���ڶ�ʱ����
		'stopGetNewDialog' in window && stopGetNewDialog();
		
		$.ajax({
         type: 'GET',
         url: url,
         cache: false,
         success: function(data){
				if(now != lastAjaxTimer){
				//alert(1);
					return;
				}
         	$("#contentArea").html(data);
				
         }
   	});	
		
		$("#navScroller li").removeClass('active');
		
		$(this).addClass('active');
		
	});
	
	$("#iconsBar").find(".refresh").live("click", function(){
		location.reload();	
	});
	var skinRange = [1,7],skin = 1;
	
	$("#iconsBar").find(".skins").live("click", function(){
		//todo ����	
		skin = skin == skinRange[1] ? skinRange[0] : ++skin;
		var url = "/pda/pad/style/Skin/Skin" + skin + "/bgImage.png";
		// document.location = 'background:'+skin;
		// document.body.style.background = 'transparent';
		
		var img = new Image();
		img.src = url;
		img.onload = function(){
			$('body').css('background-image' , "url("+url+")");
		}
	});
	
	$("#iconsBar").find(".message").live("click", function(){

			var now = lastAjaxTimer = (new Date).getTime();
			$.get('message/',{},function(data){
				if(now != lastAjaxTimer){
					return;
				}
				$('#contentArea').html(data);
			});
	
	});	
	$("#iconsBar").find(".setting").live("click", function(){

			var now = lastAjaxTimer = (new Date).getTime();
			
			$.get('settings/',{},function(data){
				if(now != lastAjaxTimer){
					return;
				}
				$('#contentArea').html(data);
			});
	});
	
	$("#iconsBar").find(".relogin").live("click", function(){
		if(confirm("<?=_('ȷ��Ҫע����')?>")){
			document.location = P_VER == 5 ? 'relogin:' : "index.php";
		}
	});
	
	$(".read_attach > a.pda_attach").live("tap click",function(){
			readAttach($(this));
	});

	// $(window)
		// .bind('gesturestart mousedown',tPad.gesturestart)
		// .bind('gesturechange mousemove',tPad.gesturechange)
		// .bind('gestureend mouseup',tPad.gestureend);
	
	/* 
	var gestures = ['gesturestart','gesturechange','gestureend'];
	for(var k in gestures){
		addEventListener(gestures[k], tPad[k]);	
	}
	 */
	 
	//var lastData;
	window.getCount_mon = getCount_mon;
	
	function getCount_mon()
	{
		$.get('count.php',{'now': new Date().getTime(), 'ACTION': "GetCount"},function(data){
			if(data!=lastData)
			{
				lastData = data;
				var json = eval('(' + data + ')');
				$.each(json || [], function(i, e){
					if('message' === i){
					
						var _tmp = $("#iconsBar a."+i).find("i");
						if($("#iconsBar a."+i).find("i").length > 0){
								e == 0 ? _tmp.remove() : (e == parseInt(_tmp.text()) ? "" : _tmp.text(e).animate({top: "+=5"}, 500).animate({top: "-=5"}, 500));    
							}else{
								if(e!=0)
								{
									$("#iconsBar a."+i).append("<i>" + e + "</i>");
									$("#iconsBar a."+i).find("i").animate({top: "+=5"}, 500).animate({top: "-=5"}, 500);
								}     
							}
							
					} else {
				
						var _tmp = $("a.app_"+i).find("i");
						if($("a.app_"+i).find("i").length > 0)
						{
							e == 0 ? _tmp.remove() : (e == parseInt(_tmp.text()) ? "" : _tmp.text(e).animate({top: "+=5"}, 500).animate({top: "-=5"}, 500));    
						}else{
							if(e!=0)
							{
								$("a.app_"+i).append("<i>" + e + "</i>");
								$("a.app_"+i).find("i").animate({top: "+=5"}, 500).animate({top: "-=5"}, 500);
							}     
						}
					
					}
				});  
			}
		});
	}


	 
	
});	
</script>
</body>
</html>