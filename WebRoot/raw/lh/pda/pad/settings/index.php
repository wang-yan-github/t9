<?
   include_once("../inc_header.php");
   include_once("inc/td_core.php");
   include_once("inc/utility_all.php");
?>
<body>
<script type="text/javascript">
var stype = "settings";

function pageInit(page_id)
{
   if(page_id == 1)
   {
      tiScroll_1 = new $.tiScroll({"listType": "readonly","page_type":"side"});
      tiScroll_1.init();
   } 
	if(page_id == 2)
   {
      tiScroll_2 = new $.tiScroll({"page_id":"2", "listType": "readonly","page_type":"side"});
      tiScroll_2.init();
   }
	if(page_id == 3)
   {
      tiScroll_3 = new $.tiScroll({"page_id":"3", "listType": "readonly","page_type":"side"});
      tiScroll_3.init();
   }
}
</script>
<div id="sideContentArea">
<?
   $tHeadData = array(
      "1" => array(
         "c" => array("title" => "控制面板")
      ),
      "2" => array(
         "l" => array("class" => "","event" => "tiScroll_1.show()", "title" => "返回"),
         "c" => array("title" => "个人资料")
      ),
      "3" => array(
         "l" => array("class" => "","event" => "tiScroll_1.show()", "title" => "返回"),
         "c" => array("title" => "帮助")
      )
   );
?>
<?=buildSiderHead($tHeadData);?>
<?=buildMessage();?>
<?=buildSideProLoading();?>
   <!-- page of settings -->
	 <?
	include_once("inc/utility_cache.php");
	$USER_INFO = GetUserInfoByUID($LOGIN_UID);

	//2012/6/1 2:20:48 lp 查询辅助角色
	if($USER_INFO["USER_PRIV_OTHER"] == "")
	{
		$USER_PRIV_OTHER = _("无"); 
	}else{
		$PRIV_INFO = GetPrivInfoByUserPriv($USER_INFO["USER_PRIV_OTHER"],"PRIV_NAME");
		if(substr($PRIV_INFO,-1)==",")
			$USER_PRIV_OTHER = substr($PRIV_INFO,0,-1);
	}

	//2012/6/1 2:22:40 lp 管理范围
	$POST_PRIV = $USER_INFO["POST_PRIV"];
	$POST_DEPT = $USER_INFO["POST_DEPT"];
	if($POST_PRIV=="1")
		$POST_PRIV=_("全体");
	elseif($POST_PRIV=="2")
		$POST_PRIV=_("指定部门");
	elseif($POST_PRIV=="0")
		$POST_PRIV=_("本部门");

	if($POST_PRIV==_("指定部门"))
	{
		$TOK=strtok($POST_DEPT,",");
		while($TOK!="")
		{
			$query1 = "SELECT * from DEPARTMENT where DEPT_ID='$TOK'";
			$cursor1= exequery($connection,$query1);
			if($ROW=mysql_fetch_array($cursor1))
			$POST_DEPT_NAME.=$ROW["DEPT_NAME"].",";
			$TOK=strtok(",");
		}

		if(substr($POST_DEPT_NAME,-1)==",")
			$POST_DEPT_NAME = substr($POST_DEPT_NAME,0,-1);
	}
	?>
	<div id="sideContentPage_1" class="sideContentPage">
		<div id="sideContentWrapper_1" class="wrapper tform_wrapper">
			<div id="contentScroller_1" class="scroller">
      		 <div class="container">
      
               <div class="tform tformshow">
      		      <div class="read_detail read_detail_hasarrow endline" onclick="goToMyProfile()">
							<div class="avatar_box" _href="<?=showAvatar($LOGIN_AVATAR,$USER_INFO["SEX"])?>">loading</div>
                     <div class="read_detail_hl_t">
                        <em><?=$LOGIN_USER_NAME?></em>
                        <p class="read_detail_info"><?=("查看我的账户及个人信息")?></p>
                     </div>
                     <span class="ui-icon-rarrow"></span>
                     <div class="clear"></div>
						</div>	
               </div>
               
            

               <div class="tform tformshow">
                  
                  <div class="read_detail">
                     <em><?=_("桌面提醒")?></em>
                     <span style="float:right"><?=_("已开启")?></span>
                  </div>
                  
                  <? if($_SESSION["C_TYPE"]){ ?>
                  <div class="read_detail">
                     <em><?=_("当前网络环境")?></em>
                     <span style="float:right"><?=showNetType($C_TYPE)?></span>
                  </div>
                  <? } ?>

                  <? if($_SESSION["C_VER"]){ ?>
                  <div class="read_detail">
                     <em><?=_("客户端版本")?></em>
                     <span style="float:right"><?=$C_VER?></span>
                  </div>
                  <? } ?>
                  
                  <div class="read_detail endline">
                     <em><?=_("当前OA版本")?></em>
                     <span style="float:right"><?=$TD_MYOA_VERSION?></span>
                  </div>
               </div>
					
					<div class="tform tformshow">
                  
                  <div class="read_detail">
                     <em><?=_("登录时记住终端类型")?></em>
                     <span style="float:right"><b id='login_dev_cookie'><?=_("已开启")?></b></span>
                  </div>
                  
               </div>
               
					
					<? if($_SESSION["C_VER"]){ ?>
               <div class="tform tformshow">
                  <div class="read_detail endline" style="text-align:center;" onclick="goToHelp()">
                     <em><?=_("帮助")?></em>
                  </div>
               </div>   
               <? } ?>

  
				</div>
			</div>    
		</div>  
	</div>
	
	<div id="sideContentPage_2" class="sideContentPage" style="display:none">
		<div id="sideContentWrapper_2" class="wrapper tform_wrapper">
			<div id="contentScroller_2" class="scroller">
				 <div class="container">
					<div class="tform tformshow">
      		      <div class="read_detail">
                     <em><?=_("主角色：")?></em> <?=$USER_INFO["PRIV_NAME"]?>   
                  </div>
                  <div class="read_detail">
                     <em><?=_("辅助角色：")?></em> <?=$USER_PRIV_OTHER?>
                  </div>
                  <div class="read_detail endline">
                     <em><?=_("管理范围：")?></em> <?=$POST_PRIV?> <? if($POST_DEPT_NAME!="") echo $POST_DEPT_NAME;?>
  
                  </div>
               </div>
				 
				    <div class="tform tformshow">
      		      <div class="read_detail">
                     <em><?=_("工作电话：")?></em> <?=$USER_INFO["TEL_NO_DEPT"]?>     
                  </div>
                  <div class="read_detail">
                     <em><?=_("工作传真：")?></em> <?=$USER_INFO["FAX_NO_DEPT"]?> 
                  </div>
                  <div class="read_detail">
                     <em><?=_("工作电话2：")?></em> <?=$USER_INFO["BP_NO"]?>
                  </div>
                  <div class="read_detail">
                     <em><?=_("电子邮件：")?></em> <?=$USER_INFO["EMAIL"]?>
                  </div>
                  <div class="read_detail">
                     <em><?=_("QQ号码：")?></em> <?=$USER_INFO["OICQ_NO"]?>
                  </div>
                  <div class="read_detail endline">
                     <em><?=_("MSN：")?></em> <?=$USER_INFO["MSN"]?>
                  </div>
               </div>
				 </div>    
			 </div>    
		</div>  
	</div>
	
	<div id="sideContentPage_3" class="sideContentPage" style="display:none">
		<div id="sideContentWrapper_3" class="wrapper tform_wrapper">
			<div id="contentScroller_3" class="scroller">
				 <div class="container">
					<div class="tform tformshow">
						<div class="read_content">
							<div class="read_detail_indheader"><?=_("通达OA精灵2013移动版使用说明：")?></div> 
							<div class="p_content">
								<p>1.<?=_("打开通达OA精灵2013移动版，进入登录界面。")?> </p>
								<p>2.<?=_("请在登录界面输入您的用户名和密码，以及本公司OA服务器地址。选中“保存密码”选项，则系统会帮您保存您的登录信息，下次启动程序时您将不用再次输入登录信息。选中“自动登录”选项，则系统会帮您保存您的登录信息，并在您下次启动程序时为您跳过登录界面自动登录，直接进入OA界面。")?></p> 
								<p>3.<?=_("登录成功之后您就能够使用通达OA移动版进行协同办公。")?></p>
								<p>4.<?=_("点击下方菜单栏的“OA导航”按钮进入OA主页面。点击“微讯”按钮进入微讯功能页面，与您的同事进行交流。")?></p>
								<p>5.<?=_("您可以点击手机的菜单功能键来选择重新登录或退出。")?></p>
								<p>6.<?=_("OA详细功能使用方法请您参照通达OA使用说明。")?></p>
							</div>  
						</div>
					</div>
				 </div>    
			 </div>    
		</div>  
	</div>
	
</div>
   
<script type="text/javascript">
tPad.changeLayout('side');
$('#settings-help').hide();
pageInit(1);	
//2012/6/1 0:34:51 lp 1秒钟后自动加载
 setTimeout(function(){
	$(".avatar_box").each(
	function(){
		if($(this).attr("_href") != "")
		{
			var oImg = $("<img />");
			oImg.hide();
			$(this).html(oImg.attr("src",$(this).attr("_href")).fadeIn("1000",function(){
				pageInit(1);
			}));
		}   
	});},1000); 

function goToMyProfile()
{
	pageInit(2);
	tiScroll_2.show();
} 

function goToHelp()
{
	pageInit(3);
	tiScroll_3.show();
}
var login_dev_cookie = {
	cookie_value: getCookie('TD_MOBILE_DEVICE'),
	clear: function(){
		setCookie('TD_MOBILE_DEVICE','', { path: '/' });
		$('#login_dev_cookie').removeClass('active').html('已关闭');
	},
	open: function(){
		setCookie('TD_MOBILE_DEVICE', this.cookie_value, { path: '/' });
		$('#login_dev_cookie').addClass('active').html('已开启');
	},
	init: function(){
		this[ this.cookie_value && this.cookie_value.trim() ? 'open' : 'clear']();
		if(this.cookie_value && this.cookie_value.trim()){    
			$('#login_dev_cookie').click(function(){
				var isActive = $(this).hasClass('active');
				login_dev_cookie[ isActive ? 'clear' : 'open']();
			});
		}
	}
};   

	login_dev_cookie.init(); 
</script>
</body>
</html>
