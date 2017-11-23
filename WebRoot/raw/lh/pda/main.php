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

$BANNER_TEXT=$SYS_INTERFACE["BANNER_TEXT"];
$WEATHER_CITY=$SYS_INTERFACE["WEATHER_CITY"];

$BANNER_TEXT = trim($BANNER_TEXT);
$BANNER_CLASS = ($BANNER_TEXT != "") ? '' : 'product';
$FONT_SIZE = 16+ceil((36-strlen($BANNER_TEXT))/2);
$FONT_SIZE = max(16, $FONT_SIZE);
$FONT_SIZE = min(24, $FONT_SIZE);
$BANNER_STYLE = 'font-size:'.$FONT_SIZE.'px;';

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
   $MODULE_ARRAY[] = array('text' => _("任务提醒"), 'href' => 'sms?P='.$P, 'module' => 'sms');
if(find_id($LOGIN_FUNC_STR, "1"))
   $MODULE_ARRAY[] = array('text' => _("电子邮件"), 'href' => 'email?P='.$P, 'module' => 'email');
if(find_id($LOGIN_FUNC_STR, "4"))
   $MODULE_ARRAY[] = array('text' => _("公告通知"), 'href' => 'notify?P='.$P, 'module' => 'notify');
if(find_id($LOGIN_FUNC_STR, "5"))
   $MODULE_ARRAY[] = array('text' => _("工作流"), 'href' => 'workflow?P='.$P, 'module' => 'workflow');
if(find_id($LOGIN_FUNC_STR, "147"))
   $MODULE_ARRAY[] = array('text' => _("内部新闻"), 'href' => 'news?P='.$P, 'module' => 'news');
   
if(find_id($LOGIN_FUNC_STR, "8"))
   $MODULE_ARRAY[] = array('text' => _("日程安排"), 'href' => 'calendar?P='.$P, 'module' => 'calendar');
if(find_id($LOGIN_FUNC_STR, "9"))
   $MODULE_ARRAY[] = array('text' => _("工作日志"), 'href' => 'diary?P='.$P, 'module' => 'diary');
//if(find_id($LOGIN_FUNC_STR, "9"))
   //$MODULE_ARRAY[] = array('text' => _("日志查看"), 'href' => 'diary?P='.$P, 'module' => 'diary');
if(find_id($LOGIN_FUNC_STR, "16"))
   $MODULE_ARRAY[] = array('text' => _("个人文件柜"), 'href' => 'file_folder?P='.$P, 'module' => 'folder');
//if(find_id($LOGIN_FUNC_STR, "15"))
   //$MODULE_ARRAY[] = array('text' => _("公共文件柜"), 'href' => 'file_folder?P='.$P, 'module' => 'folder');
   
$MODULE_ARRAY[] = array('text' => _("人员查询"), 'href' => 'user_info?P='.$P, 'module' => 'query');
if(find_id($LOGIN_FUNC_STR, "10"))
   $MODULE_ARRAY[] = array('text' => _("通讯簿"), 'href' => 'address?P='.$P, 'module' => 'address');
if(find_id($LOGIN_FUNC_STR, "21") || find_id($LOGIN_FUNC_STR, "22"))
   $MODULE_ARRAY[] = array('text' => _("区号邮编"), 'href' => 'tel_no?P='.$P, 'module' => 'zipcode');
if(strlen($WEATHER_CITY)==5)
   $MODULE_ARRAY[] = array('text' => _("天气预报"), 'href' => 'weather?P='.$P, 'module' => 'weather');

$MODULE_ARRAY[] = array('text' => _("控制面板"), 'href' => 'settings?P='.$P, 'module' => 'settings');
/*$MODULE_ARRAY[] = array('text' => _("微讯"), 'href' => 'message?P='.$P, 'module' => 'message');*/
   
?>
<body>
<style>
#m_wrapper {top:43px;width:100%;height:100%;float:left;position:relative;z-index:1;overflow:hidden;}
#m_scroller{width:100%;height:100%;float:left;padding:0;}
#m_scroller ul {list-style:none;display:block;float:left;height:100%;padding:0;margin:0;text-align:left;}
#m_scroller li {-webkit-box-sizing:border-box;-moz-box-sizing:border-box;-o-box-sizing:border-box;box-sizing:border-box;display:block; float:left;height:100%;text-align:center;font-size:14px;}
.m_index{font-size:16px !important;overflow: hidden;text-overflow: ellipsis;white-space: nowrap;}
.m_index .t{padding-left:10px;padding-right:10px;}
#m_scroller li .app_table{margin:0px auto;width:95%;}
#m_scroller li .app_table td{height:70px;padding:10px 0px 0px 0px;}
#m_scroller li .app_table td a{position:relative;text-decoration:none;}
#m_scroller li .app_table td i{min-width:20px;height:20px;line-height:20px;position:absolute;font-style:normal;top:-6px;right:-6px;color:#FFFFFF;background-color:#F35618;border:2px solid #FFFFFF;box-shadow: 0px 1px 1px 0px #999999;-moz-box-shadow:0px 1px 0px 0px #000000;-webkit-border-shadow:0px 1px 0px 0px #000000;border-radius: 11px;font-size:16px;text-align:center;padding:0px 2px;}
#m_scroller li .app_table td em{display:block;font-size:10pt;font-style: normal;font-weight: normal;padding:5px 0px 0px 0px;text-shadow:1px 1px 0px #ffffff;}
#m_scroller li .app_table td .app{display:block;width:60px;height:60px;background-image:url("/pda/style/images/pda_icons_new.jpg");background-repeat:none;margin:0px auto;border-radius: 0.5em 0.5em 0.5em 0.5em;background-size:240px 240px;-o-background-size:240px 240px;-webkit-background-size:240px 240px;}
#m_scroller li .app_sms{background-position:0px 0px;}
#m_scroller li .app_email{background-position:-60px 0px;}
#m_scroller li .app_notify{background-position:-120px 0px;}
#m_scroller li .app_news{background-position:-180px 0px;}
#m_scroller li .app_calendar{background-position:0px -60px;}
#m_scroller li .app_diary{background-position:-60px -60px;}
#m_scroller li .app_folder{background-position:-120px -60px;}
#m_scroller li .app_workflow{background-position:-180px -60px;}
#m_scroller li .app_query{background-position:0px -120px;}
#m_scroller li .app_address{background-position:-60px -120px;}
#m_scroller li .app_zipcode{background-position:-120px -120px;}
#m_scroller li .app_weather{background-position:-180px -120px;}
/*#m_scroller li .app_message{background-position:0px -180px;}*/
#m_scroller li .app_settings{background-position:-60px -180px;}
#m_nav{position:absolute; z-index:2;left:50%;margin-left:-10px;width:20px;height:8px;padding:0;text-align:center;display:none;}
#m_nav #pagebar{display:block;height:8px;margin:0px auto;padding:0;}
#m_nav #pagebar li {display:block;float:left;list-style:none;padding:0; margin:0;text-indent:-9999em;width:8px; height:8px;-webkit-border-radius:4px;-moz-border-radius:4px;-o-border-radius:4px;border-radius:4px;background:#ddd;overflow:hidden;margin-right:4px;cursor:pointer;}
#m_nav #pagebar li.active {background:#0B7CC4;}
#m_nav #pagebar li:last-child {margin:0;}

</style>
<div id="header" class="m_index"><span class="t"><?=$LOGIN_IE_TITLE?></span></div>
   <?=buildProLoading();?>
   <div id="page">
      <div id="m_wrapper" style="background-color:transparent;">
         	<div id="m_scroller">
							<ul id="thelist">
							  <?
							  
							  $SCREEN_COUNT = ceil(count($MODULE_ARRAY)/$PAGE_APP_COUNT);
							  for($I = 0; $I<($SCREEN_COUNT * $PAGE_APP_COUNT); $I++)
							  {
							    
							     if($I % 9 == 0)
							        echo "<li><table class='app_table'>";
							     
							     if($I % 3 == 0)
							        echo "<tr>\n";
							     
							     if($MODULE_ARRAY[$I])
							     {
							        //2012/5/28 1:29:25 lp 新增模块未读条数统计
							        $unread = $MODULE_ARRAY_COUNT[$MODULE_ARRAY[$I]['module']] == 0 ? "" : "<i>".$MODULE_ARRAY_COUNT[$MODULE_ARRAY[$I]['module']]."</i>";
							        echo '<td><a href="javascript:void(0);" _href="'.$MODULE_ARRAY[$I]['href'].'" class="app app_'.$MODULE_ARRAY[$I]['module'].'">'.$unread.'</a><em>'.$MODULE_ARRAY[$I]['text'].'</em></td>'."\n";
							     }else{
							        echo '<td>&nbsp;</td>'."\n";
							     }
							     if($I % 3 == 2)
							        echo "</tr>\n";
							        
							     if($I!=0 && $I % 9 == 8 )
							        echo "</table></li>\n";
							  }
							
							  ?>
							</ul>
          </div>
      </div>
   </div>
   
   <div id="m_nav">
			<ul id="pagebar">
				<? for($I=0; $I< $SCREEN_COUNT;$I++){ ?>
					<li class="<?=$I == 0 ? "active" : ""; ?>"><?=($I+1)?></li>
				<? } ?>
			</ul>
	 </div>
<script type="text/javascript" src="/pda/js/udf-1.1.js"></script>
<script type="text/javascript">
var myScroll;
$(window).resize(function(){
   setbody();
});

$(document).ready(function(){
   //2012/5/25 10:46:56 lp 增加点击事件
   $(".app_table a").live("click",function(){
      var _href = $(this).attr("_href");
      if(_href!="")
      {
         $.ProLoading.show();
         location.href = _href;           
      }     
   });
   setpagebar();
});

setbody();

var lastData = '';
var unreadCount_mon = null;
unreadCount_mon = window.setInterval(getCount_mon, <?=$C['MAIN_PAGE_REF_SEC']?>*1000);

function getCount_mon()
{
   $.get('count.php',{'now': new Date().getTime(), 'ACTION': "GetCount"},function(data){
      if(data!=lastData)
      {
         lastData = data;
         var json = eval('(' + data + ')');
         $.each(json || [], function(i, e)
         {
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
         });  
      }
   });
}

function loaded()
{
	myScroll = new iScroll('m_wrapper', {
		snap: true,
		momentum: false,
		hScrollbar: false,
		onScrollEnd: function () {
				$("#pagebar > li.active").removeClass("active");
				$("#pagebar > li:nth-child(" + (this.currPageX+1) + ")").addClass('active');
				setCookie("currPage", this.currPageX);
		}
	 });
	 
	 currPageX = getCookie("currPage");
	 if(currPageX!= 0)
	 {
	   myScroll.scrollToPage(currPageX, 0, 0);
	   $("#pagebar > li:eq("+currPageX+")").addClass('active');
	 }
}

function setbody()
{
   $("#m_scroller").width($(document).width()*<?=$SCREEN_COUNT?>);
   $("#thelist li").width($(document).width());
   $("#thelist li").height($(document).height() - 43);
}

function setpagebar()
{
	var lih = tah = 0;
	var lih = $("#thelist li").height();
	var tah = $("#thelist li .app_table").height();
	if((lih - tah) < 30 )
	{
		$("#m_nav").css("top",tah + 10).fadeIn();		
	}else{
		$("#m_nav").css("bottom",(lih - tah)/2).fadeIn();		
	}
	
	$("#pagebar li").bind("click", function()
	{
		if($(this).hasClass("active")){
			return;
		}else{
			myScroll.scrollToPage($(this).index(), myScroll.currPageX, 200);
			setCookie("currPage", myScroll.currPageX);
		}
	});
}

document.addEventListener('DOMContentLoaded', loaded, false);
window.addEventListener('load', function(){
   setTimeout(function(){window.scrollTo(0, 1);}, 100);
});
</script>
</body>
</html>
