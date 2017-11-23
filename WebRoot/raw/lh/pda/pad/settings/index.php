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
         "c" => array("title" => "�������")
      ),
      "2" => array(
         "l" => array("class" => "","event" => "tiScroll_1.show()", "title" => "����"),
         "c" => array("title" => "��������")
      ),
      "3" => array(
         "l" => array("class" => "","event" => "tiScroll_1.show()", "title" => "����"),
         "c" => array("title" => "����")
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

	//2012/6/1 2:20:48 lp ��ѯ������ɫ
	if($USER_INFO["USER_PRIV_OTHER"] == "")
	{
		$USER_PRIV_OTHER = _("��"); 
	}else{
		$PRIV_INFO = GetPrivInfoByUserPriv($USER_INFO["USER_PRIV_OTHER"],"PRIV_NAME");
		if(substr($PRIV_INFO,-1)==",")
			$USER_PRIV_OTHER = substr($PRIV_INFO,0,-1);
	}

	//2012/6/1 2:22:40 lp ����Χ
	$POST_PRIV = $USER_INFO["POST_PRIV"];
	$POST_DEPT = $USER_INFO["POST_DEPT"];
	if($POST_PRIV=="1")
		$POST_PRIV=_("ȫ��");
	elseif($POST_PRIV=="2")
		$POST_PRIV=_("ָ������");
	elseif($POST_PRIV=="0")
		$POST_PRIV=_("������");

	if($POST_PRIV==_("ָ������"))
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
                        <p class="read_detail_info"><?=("�鿴�ҵ��˻���������Ϣ")?></p>
                     </div>
                     <span class="ui-icon-rarrow"></span>
                     <div class="clear"></div>
						</div>	
               </div>
               
            

               <div class="tform tformshow">
                  
                  <div class="read_detail">
                     <em><?=_("��������")?></em>
                     <span style="float:right"><?=_("�ѿ���")?></span>
                  </div>
                  
                  <? if($_SESSION["C_TYPE"]){ ?>
                  <div class="read_detail">
                     <em><?=_("��ǰ���绷��")?></em>
                     <span style="float:right"><?=showNetType($C_TYPE)?></span>
                  </div>
                  <? } ?>

                  <? if($_SESSION["C_VER"]){ ?>
                  <div class="read_detail">
                     <em><?=_("�ͻ��˰汾")?></em>
                     <span style="float:right"><?=$C_VER?></span>
                  </div>
                  <? } ?>
                  
                  <div class="read_detail endline">
                     <em><?=_("��ǰOA�汾")?></em>
                     <span style="float:right"><?=$TD_MYOA_VERSION?></span>
                  </div>
               </div>
					
					<div class="tform tformshow">
                  
                  <div class="read_detail">
                     <em><?=_("��¼ʱ��ס�ն�����")?></em>
                     <span style="float:right"><b id='login_dev_cookie'><?=_("�ѿ���")?></b></span>
                  </div>
                  
               </div>
               
					
					<? if($_SESSION["C_VER"]){ ?>
               <div class="tform tformshow">
                  <div class="read_detail endline" style="text-align:center;" onclick="goToHelp()">
                     <em><?=_("����")?></em>
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
                     <em><?=_("����ɫ��")?></em> <?=$USER_INFO["PRIV_NAME"]?>   
                  </div>
                  <div class="read_detail">
                     <em><?=_("������ɫ��")?></em> <?=$USER_PRIV_OTHER?>
                  </div>
                  <div class="read_detail endline">
                     <em><?=_("����Χ��")?></em> <?=$POST_PRIV?> <? if($POST_DEPT_NAME!="") echo $POST_DEPT_NAME;?>
  
                  </div>
               </div>
				 
				    <div class="tform tformshow">
      		      <div class="read_detail">
                     <em><?=_("�����绰��")?></em> <?=$USER_INFO["TEL_NO_DEPT"]?>     
                  </div>
                  <div class="read_detail">
                     <em><?=_("�������棺")?></em> <?=$USER_INFO["FAX_NO_DEPT"]?> 
                  </div>
                  <div class="read_detail">
                     <em><?=_("�����绰2��")?></em> <?=$USER_INFO["BP_NO"]?>
                  </div>
                  <div class="read_detail">
                     <em><?=_("�����ʼ���")?></em> <?=$USER_INFO["EMAIL"]?>
                  </div>
                  <div class="read_detail">
                     <em><?=_("QQ���룺")?></em> <?=$USER_INFO["OICQ_NO"]?>
                  </div>
                  <div class="read_detail endline">
                     <em><?=_("MSN��")?></em> <?=$USER_INFO["MSN"]?>
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
							<div class="read_detail_indheader"><?=_("ͨ��OA����2013�ƶ���ʹ��˵����")?></div> 
							<div class="p_content">
								<p>1.<?=_("��ͨ��OA����2013�ƶ��棬�����¼���档")?> </p>
								<p>2.<?=_("���ڵ�¼�������������û��������룬�Լ�����˾OA��������ַ��ѡ�С��������롱ѡ���ϵͳ������������ĵ�¼��Ϣ���´���������ʱ���������ٴ������¼��Ϣ��ѡ�С��Զ���¼��ѡ���ϵͳ������������ĵ�¼��Ϣ���������´���������ʱΪ��������¼�����Զ���¼��ֱ�ӽ���OA���档")?></p> 
								<p>3.<?=_("��¼�ɹ�֮�������ܹ�ʹ��ͨ��OA�ƶ������Эͬ�칫��")?></p>
								<p>4.<?=_("����·��˵����ġ�OA��������ť����OA��ҳ�档�����΢Ѷ����ť����΢Ѷ����ҳ�棬������ͬ�½��н�����")?></p>
								<p>5.<?=_("�����Ե���ֻ��Ĳ˵����ܼ���ѡ�����µ�¼���˳���")?></p>
								<p>6.<?=_("OA��ϸ����ʹ�÷�����������ͨ��OAʹ��˵����")?></p>
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
//2012/6/1 0:34:51 lp 1���Ӻ��Զ�����
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
		$('#login_dev_cookie').removeClass('active').html('�ѹر�');
	},
	open: function(){
		setCookie('TD_MOBILE_DEVICE', this.cookie_value, { path: '/' });
		$('#login_dev_cookie').addClass('active').html('�ѿ���');
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
