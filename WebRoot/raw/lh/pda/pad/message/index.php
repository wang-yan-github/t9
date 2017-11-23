<?
	include_once("header.php");
	include_once("user.php");
	include_once("inc/utility_all.php");
	include_once("inc/department.php");
	
   $MSG_USER_LIST = array();
   $MSG_LIST = array();
   $MSG_COUNT = array();
   $new_msg = $style = '';
   $query = "SELECT FROM_UID,TO_UID,REMIND_FLAG,SEND_TIME,CONTENT from message where (TO_UID='$LOGIN_UID' or FROM_UID='$LOGIN_UID') and DELETE_FLAG!='1' order by SEND_TIME DESC";
   $cursor= exequery($connection,$query);
   $rc = mysql_affected_rows();
   while($ROW = mysql_fetch_array($cursor))
   {
      $FROM_UID = $ROW['FROM_UID'];
      $TO_UID = $ROW['TO_UID'];
      $SEND_TIME = $ROW['SEND_TIME'];
      $REMIND_FLAG = $ROW['REMIND_FLAG'];
      $CONTENT = $ROW['CONTENT'];
      
      //计算给我发的新的未读消息条数
      if(($TO_UID == $LOGIN_UID) && ($REMIND_FLAG == 1))
      {
         if(!$MSG_COUNT["USER_".$FROM_UID])
            $MSG_COUNT["USER_".$FROM_UID] = 0;
         $MSG_COUNT["USER_".$FROM_UID]++;
      }
         
      if(($TO_UID == $LOGIN_UID) and (!array_key_exists("USER_".$FROM_UID,$MSG_USER_LIST))){
         $MSG_USER_LIST["USER_".$FROM_UID] = 1;
         $MSG_LIST["USER_".$FROM_UID] = array($FROM_UID,$SEND_TIME,($REMIND_FLAG == 1 ? 1 : 0),$CONTENT);         
      }
      
      if(($FROM_UID == $LOGIN_UID) and (!array_key_exists("USER_".$TO_UID,$MSG_USER_LIST))){
         $MSG_USER_LIST["USER_".$TO_UID] = "USER_".$TO_UID;
         $MSG_LIST["USER_".$TO_UID] = array($TO_UID,$SEND_TIME,0,$CONTENT);      
      }
      if(count($MSG_LIST) >= $PAGE_SIZE) break;
   }
   
   $TOTAL_ITEMS = count($MSG_LIST);
?>
<body>
<script type="text/javascript">
var stype = "message";
var nonewdata = "<?=_('没有新的微讯')?>";
var newdata = "<?=_('%s个新微讯')?>";
var writedata = "<?=_('点击这里填写短信内容...')?>";
var prePageId;

/* --- 自定义参数 ---*/
var nomoredata_1 = false;
var noshowPullUp_1 = <? echo ($PAGE_SIZE >= $TOTAL_ITEMS) ? "true" : "false"; ?>;

function pageInit(page_id)
{
   if(page_id == 1)
   {
      tiScroll_1 = new $.tiScroll({"page_type":"side","nomoredata": nomoredata_1, "noshowPullUp":noshowPullUp_1, "onPullDown": pullDown_message});
      tiScroll_1.init();
   }
   if(page_id == 2)
   {
      //stopGetNewSms();
      tiScroll_2 = new $.tiScroll({"page_id": 2,"page_type":"side", "listType": "readonly"});
      tiScroll_2.init();
   }
   if(page_id == 3)
   {
      startGetNewDialog();
      tiScroll_3 = new $.tiScroll({"page_id": 3, "listType": "readonly"});
      tiScroll_3.init();
		tiScroll_3.getOIScroll().wrapper.style.bottom = '43px';
   }
   if(page_id == 4)
   {
      tiScroll_4 = new $.tiScroll({"page_id": 4, "listType": "readonly"});
      tiScroll_4.init();
   }
   if(page_id == 5)
   {
      tiScroll_5 = new $.tiScroll({"page_id": 5,"page_type":"side", "listType": "readonly"});
      tiScroll_5.init();
      
      tSearch5 = new $.tSearch2({'page_id':5, input:"#USER_NAME", list:"#contactlist_1"});
      tSearch5.init();
   }
}
</script> 

<div id="sideContentArea">
<?
   $tHeadData = array(
      "1" => array(
         "l" => array("class" => "","event" => "showMultiSend()", "title" => "群发"),
         "c" => array("title" => "我的微讯"),
         "r" => array("class" => "","event" => "contactList();", "title" => "联系人")
      ),
      "2" => array(
         "l" => array("class" => "","event" => "tiScroll_2.hide();tiScroll_1.show();pageInit(1);", "title" => "返回"),
         "c" => array("title" => "常用联系人")
      ),
      "5" => array(
         "l" => array("class" => "","event" => "hideMultiSend();", "title" => "返回"),
         "c" => array("title" => "选择收信人")
      )
   );
?>
<?=buildSiderHead($tHeadData);?>
<?=buildMessage();?>
<?=buildSideProLoading();?>


   <!-- all of folders -->

	<div id="sideContentPage_1" class="sideContentPage">
		<div id="sideContentWrapper_1" class="wrapper">
			<div id="contentScroller_1" class="scroller">
         <?=buildPullDown()?>
         <ul class="main-comm-list preViewList comm-pic-list" id="sms-list">
      <?
         if($TOTAL_ITEMS > 0)
         {
            foreach($MSG_LIST as $k => $v)
            {
               if($v[2] == 1)
               {
                  $Class = " active";
               }else{
                  $Class = "";$unread = "";
               }
               
               if($USER_ARRAY[$v[0]]['NAME']=="")
                  continue;
      ?>
      			<li class="<?=$fix_for_pad['list-li-style'].$Class?>"<?=$unread?> q_id="<?=$v[0]?>" q_name="<?=$USER_ARRAY[$v[0]]['NAME']?>">
                     <img src="<?=showAvatar($USER_ARRAY[$v[0]]['AVATAR'],$USER_ARRAY[$v[0]]['SEX'])?>" class="ui-li-thumb"/>
                     <h3><span class="time"><?=timeintval($v[1])?></span><?=$USER_ARRAY[$v[0]]['NAME']?></h3>
                     <p class="content"><?=strip_tags($v[3])?>&nbsp;</p>
                     <?
                        if(array_key_exists("USER_".$v[0],$MSG_COUNT))
            				{
            				   echo '<span class="ui-icon-num">'.$MSG_COUNT['USER_'.$v[0]].'</span>';
            			   }
                     ?>
                  </a>
      			</li>
      <?    }//while
            echo "</ul>";
         }else{
            echo "</ul>";    
            echo "<div class='no_msg'>"._("暂无新微讯！")."</div>";
         }  
      ?>    
         <?=buildPullUp();?> 
         </div>      
      </div>
   </div>
   
   <!-- page of contactList -->

	<div id="sideContentPage_2" class="sideContentPage" style="display:none;">
		<div id="sideContentWrapper_2" class="wrapper">
			<div id="contentScroller_2" class="scroller">

         </div>
      </div>
   </div> 

	   <!-- page of search input -->
   <div id="sideContentPage_5" class="sideContentPage tlist" style="display:none;">
      <div id="sideContentWrapper_5" class="wrapper">
         <div id="contentScroller_5" class="scroller">
            <div id="search_box">
               <div id="input_box">
                  <input type="text" id="USER_NAME" name="USER_NAME" value="" autocapitalize="off" autocorrect="off"/>   
               </div>
            </div>
            <ul class="comm-list sideBarSubList" id="contactlist_1"></ul>       
         </div>      
      </div>
   </div>
	
</div>
   

 
<div id="mainContentArea">	
<?
   $tHeadData = array(

      "3" => array(
       //  "l" => array("class" => "","event" => "HideDialogMsg(3);reback_fix(3);", "title" => "返回"),
         "c" => array("title" => "")
      ),
      "4" => array(
         "l" => array("class" => "","event" => "hideMultiSend();", "title" => "返回"),
         "c" => array("title" => "微讯群发"),
         "r" => array("title" => "发送", "event" => "mutiSendMsg()")
      )
   );
?>

   <?=buildMainHead($tHeadData);?>
   <?=buildMessage();?>
   <?=buildMainProLoading();?>

   <!-- page of dialogList -->	
	<div id="mainContentPage_3" class="mainContentPage" style="display:none;">
		<div id="mainContentWrapper_3" class="wrapper ms_dialogList">
			<div id="contentScroller_3" class="scroller shadowscroller">

         </div>
      </div>
		<div id="footer" class="footer" >
			<div id="footer_3">
				<table class="blankTable" cellspacing="0" cellpadding="0">
					<tr>
						<td class="ltd"><input type="text" id="msg_content" name="msg_content" value="" autocapitalize="off" autocorrect="off"/></td>
						<td class="rtd"><span id="msg_send_btn" class="udf_btn" onclick="send_msg()"><span><?=_("发送")?></span></span></td> 
					</tr>
				</table>
			</div>      
		</div>
   </div>
   
   <!-- page of mutiSend -->
   <div id="mainContentPage_4" class="mainContentPage tcontent" style="display:none;">
      <div id="mainContentWrapper_4" class="wrapper ms_mutiSendPage">
         <div id="contentScroller_4" class="scroller">
            <div class="read_detail" id="ms_mutiSendDom">
                  <?=_("收信人：")?>
                  <span id="contactlist_1_result" class="contactlist_result"></span>
                  <a href="javascript:void(0)" id="mutiSendAdd" class="add_btn mutiSend_add_btn"></a>
            </div>
            <div class="read_detail tform" id="ms_mutiSendContent">
               <textarea id="MUTISEND_CONTENT" class="noborderTextarea" name="MUTISEND_CONTENT" rows="<?=Ag("iPhone") ? "12" : "8"?>" cols="10" wrap="on" onclick="checkWord(this,'click');" onblur="checkWord(this,'blur');" style="background-color:#efefef;" autocapitalize="off" autocorrect="off"><?=_("点击这里填写短信内容...")?></textarea>
            </div>
         </div>
      </div>
   </div>
   

</div> 


<script type="text/javascript">
tPad.changeLayout();
pageInit(1);

//短信列表定时刷新
var timer_msg_mon = null;
timer_msg_mon = window.setInterval(msg_mon, monInterval.MSG_LIST_REF_SEC*1000);

//对话界面定时刷新
var timer_dialog_mon = null;

/* -- 发送信息 -- */
function send_msg()
{
   var msg = $.trim($("#msg_content").val());
	var FROM_UID = $("#dialog_form").val();  
	if(msg==""){ $("#msg_content").focus();return;}
	$.ajax({
		type: "POST",
		url: "/pda/pad/inc/getdata.php",
		data: {"A":"sendSignleMsg", "STYPE": stype, "TO_UID": FROM_UID, "MSG":msg},
		cache: false,
		success: function(m){
			var oiScroll_3 = tiScroll_3.getOIScroll();
			$('#mycust-dialogue-list').append(m);
			$('#mycust-dialogue-list').find('div.mycust-list:hidden').show();
			oiScroll_3.refresh();
			oiScroll_3.scrollTo(0,oiScroll_3.maxScrollY,500);
			$('#msg_content').val("").focus();
		}
	});
}

//自定义下拉刷新功能，截断插件自带刷新
function pullDown_message()
{
   $.get(
      "/attachment/new_sms/" + loginUser.uid + ".sms",
      {'now': new Date().getTime()},
      function(data)
      {
   		if(data=="01" || data=="11")
   		{
   			$.get(
					"/pda/pad/inc/getdata.php",
					{"A":"getNewListSmsNumAndContent","STYPE": stype ,"TO_UID":loginUser.uid},
					function(m)
					{
						if(m == "NONEWDATA")
						{
						   showMessage(nonewdata);
						}else{
							if($('.no_msg').length > 0) 
							   $('.no_msg').hide();
                     showMessage(sprintf(newdata,m));
                     msg_mon();
						}
					}
				);
   		}
   		tiScroll_1.refresh();
   		return;
   	}
   ); 
	return false;
}

//显示常用联系人
function contactList()
{
	pageInit(2);
   tiScroll_1.hide();
	tiScroll_2.show();
   $.ajax({
      type: 'GET',
      url: 'message/contact.php',
      cache: false,
      data: {'P': '<?=$P?>'},
      beforeSend: function(){
        // $.ProLoading.show();          
      },
      success: function(data){
       //  $.ProLoading.hide();
         $('.scroller', tiScroll_2.getElement()).empty().append(data);
         tiScroll_2.getElement().show(); 
			tiScroll_2.getOIScroll().refresh();
      },
      error: function(data){
      //   $.ProLoading.hide();  
         showMessage("<?=_('获取失败')?>");
      }
   });
     
}

//获取对话
function getDialogList(UID,NAME)
{
	pageInit(3);
	tiScroll_3.getMainData({
      url: 'message/msg.php',
      data: {'P': '<?=$P?>','FROM_UID': UID},
		onSuccess: function(data){
		
				$("#mainheader_3").show().find(".t").text(NAME);
				tiScroll_3.getOIScroll().scroller.innerHTML = '<div id="my-dialogList">'+data+'</div>';
				tiScroll_3.getElement().show();		
				tiScroll_3.refresh();
			   tiScroll_3.getOIScroll().scrollTo(0, tiScroll_3.getOIScroll().maxScrollY,500);
				return false;
		}
	
	});

/*    $.ajax({
      type: 'GET',
      url: 'msg.php',
      cache: true,
      data: {'P': '<?=$P?>','FROM_UID': UID},
      beforeSend: function(){
         $.ProLoading.show();          
      },
      success: function(data){
         $.ProLoading.hide();
         $("#page_3 > #wrapper_3 > #scroller_3 > #my-dialogList").empty().append(data);
         $("#page_3").show('fast',function(){
            $("#footer").show();
            $("#footer_3").show();
            pageInit(3);
            oiScroll_3.scrollToElement($("#mycust-dialogue-list div.mycust-list:last")[0],500);
         });
         $("#header_1").hide();
         $("#header_3").find(".t").text(NAME);
         $("#header_3").show();
      },
      error: function(data){
         $.ProLoading.hide();  
         showMessage("<?=_('获取失败')?>");
      }
   }); */
}

//隐藏footer
function HideDialogMsg(from){
   $("#footer_"+from).hide();
   $("#footer").hide();   
}

//短信列表页面点击事件
$("#sms-list li,#contactList li").die().live("click tap",function(){
   prePageId = 1;
	var $$a = $(this);
   getDialogList($$a.attr("q_id"),$$a.attr("q_name"));
   	
   //如果有短信点击后则取消高亮显示
   if($$a.hasClass("active"))
   {
      $$a.removeClass("active");
      $$a.find(".ui-icon-num").remove();   
   }
   
});
$(".preViewList li").die().live("click tap",function(){
		$(".preViewList li").removeClass("activebg");
		$(this).addClass("activebg");
});

//修正后的返回跳转
function reback_fix(from){
   if(prePageId){
      reback(from,prePageId);
      if(prePageId == 1){
         startGetNewMsg();
      }
      stopGetNewDialog();
   }
}

//联系人页面点击
/* $("#contactList li").live("click tap",function(){
   prePageId = 2;
   $$a = $(this);
   UID = $$a.attr("q_id");
   NAME = $$a.attr("q_name");
   $.ajax({
      type: 'GET',
      url: 'msg.php',
      cache: true,
      data: {'P': '<?=$P?>','FROM_UID': UID},
      beforeSend: function(){
         $.ProLoading.show();          
      },
      success: function(data){
         $.ProLoading.hide();
         $("#page_3 > #wrapper_3 > #scroller_3 > #my-dialogList").empty().append(data);
         $("#page_3").show('fast',function(){
            $("#footer").show();
            $("#footer_3").show();
            pageInit(3);
			   tiScroll_3.getOIScroll().scrollTo(0,oiScroll_3.maxScrollY,500);
         });
         $("#header_2").hide();
         $("#header_3").find(".t").text(NAME);
         $("#header_3").show();
      },
      error: function(data){
         $.ProLoading.hide();  
         showMessage("<?=_('获取失败')?>");
      }
   });
}); */

/* -- 短信列表自动刷新 -- */
function msg_mon()
{
	var oiScroll_3 = tiScroll_3.getOIScroll();
	$.ajax({
      type: 'GET',
      url: '/attachment/new_sms/' + loginUser.uid + '.sms',
      data: {'now': new Date().getTime()},
      success: function(data)
      {
   		if(data=="01" || data=="11")
   		{
   			$.ajax({
					type: "GET",
					url: "/pda/pad/inc/getdata.php",
					data: {"A":"refreshList","STYPE": stype ,"TO_UID":loginUser.uid},
					cache: true,
					success: function(m)
					{
						if(m && m!="NO")
						{
							if($('.no_msg').length > 0) 
							   $('.no_msg').hide();
							$("#sms-list").empty().append(m);
							oiScroll_3.refresh();
							oiScroll_3.scrollTo(0, oiScroll_3.maxScrollY, 500);
						}else{
							$("#sms-list").empty();
							$('.no_msg').show();
						}
					}
				});
   		}
   	}
   });
}

//跳转页面后停止获取新消息
function stopGetNewMsg()
{
   if(window.timer_msg_mon)
   {
      window.clearInterval(timer_msg_mon);
      timer_msg_mon = null;
   }   
}

function startGetNewMsg()
{
   stopGetNewMsg();
   timer_msg_mon = window.setInterval(msg_mon, monInterval.MSG_LIST_REF_SEC*1000);      
}

// 当处于对话界面时，定时获取对话
function startGetNewDialog()
{
   timer_dialog_mon = window.setInterval(dialog_mon, monInterval.MSG_DIOG_REF_SEC*1000);      
}

function stopGetNewDialog()
{
   if(window.timer_dialog_mon)
   {
      window.clearInterval(timer_dialog_mon);
      timer_dialog_mon = null;
   }   
}

function dialog_mon()
{
   var FROM_UID = $("#dialog_form").val();
	$.ajax({
      type: 'GET',
      url: '/attachment/new_sms/' + loginUser.uid + '.sms',
      data: {'now': new Date().getTime()},
      success: function(data)
      {
   		if(data=="01" || data=="11")
   		{
   			$.ajax({
					type: "GET",
					url: "/pda/pad/inc/getdata.php",
					data: {"A":"getSingleNewMsg","STYPE": stype ,"TO_UID": FROM_UID},
					cache: false,
					success: function(m)
					{
						var oiScroll_3 = tiScroll_3.getOIScroll();
						if(m == "NO") return;
						if(m!=""){
							$('#mycust-dialogue-list').append(m);
							$('#mycust-dialogue-list').find('div.mycust-list:hidden').show();
            			oiScroll_3.refresh();
            			oiScroll_3.scrollTo(0,oiScroll_3.maxScrollY,500);
						}
					}
				});
   		}
   	}
   });
}
// -- 显示群发面板

function showMultiSend(){
	pageInit(4);
	tiScroll_4.show();
	pageInit(5);
	tiScroll_5.show();
}
function hideMultiSend(){
	tiScroll_1.show();
	tiScroll_3.show();
	pageInit(1);
}
// -- 群发
function mutiSendMsg()
{
   var $$selectDom = $("#contactlist_1_result em"),
   $$contentDom = $("#MUTISEND_CONTENT"),
   content = $$contentDom.val();
   if(($$selectDom.length > 0) && (content!=writedata) && (content!=""))
   {
      var _oSelect_uid = '';
      $$selectDom.each(function(){
         _oSelect_uid += $(this).attr("uid") + ",";
      });
      
      $.ajax({
   		type: "POST",
   		url: "/pda/inc/getdata.php",
   		data: {"A":"mutisend", "STYPE": stype, "TO_UID": _oSelect_uid, "CONTENT":content},
   		cache: false,
   		beforeSend: function(){
   		   $.ProMainLoading.show("<?=_('发送中...')?>");
   		},
   		success: function(m){
   		   if(m == "+OK")
   		   {
      		   $.ProMainLoading.hide();
      			showMessage("<?=_('发送成功！')?>", $('#mainContentArea'));
      			resetMutiSend();
      		}else{
      		   alert(m);   
      		}
   		}
   	});
   
   }else if((content == writedata) || (writedata == "")){
      showMessage("<?=_('写点什么吧？')?>", $('#mainContentArea'));
      return;     
   }else{
      showMessage("<?=_('请先添加收信人')?>", $('#mainContentArea'));
      return;   
   }
}

function resetMutiSend(){
   $("#contactlist_1_result").empty();
   $("#MUTISEND_CONTENT").val(writedata);
   $("#USER_NAME").val("");
}

$("#mutiSendAdd").live("tap click",function(e){
   e.stopPropagation();
   $("#header_4").hide();
   $("#header_5").show();
   $("#page_5").show('fast',function(){pageInit(5);});
});

$("#ms_mutiSendDom").live("click",function(){
   $("#mutiSendAdd").click();  
});

function checkWord(obj,evt)
{
   if(evt == "click")
   {
      if(obj.value == writedata)
         obj.value = "";    
   }else{
      if(obj.value == "")
         obj.value = writedata;   
   }  
}

$("#contactlist_1>li").die().live("tap click",function(){
   $$appendDom = $("#contactlist_1_result");
   $(this).addClass("iScrollHover");
   o = $(this);
   setTimeout(function(){o.removeClass("iScrollHover");},500);
   var _oSelect_name = $(this).attr("q_name");
   var _oSelect_uid = $(this).attr("q_id");
   var _oSelect_user_id = $(this).attr("q_user_id");
   var _selected = false;
   
   if($$appendDom.html()!="")
   {
      $$appendDom.find("em").each(function()
      {
         var uid = $(this).attr("uid");
         if(_oSelect_uid == uid){
            _selected = true;
            return false;
         } 
      });      
   }
   
   if(!_selected)
   {
      $$appendDom.append("<em uid='"+_oSelect_uid+"' userid='"+_oSelect_user_id+"'>" + _oSelect_name +"<span></span></em>");
      showMessage("<?=_('添加成功！')?>");
      return;
   }else{
      showMessage("<?=_('已经选择了该人：）')?>");
      return;      
   }
});

$("#contactlist_1_result em").live("tap click",function(e){
   e.stopPropagation();
   if(!$(this).hasClass("active"))
   {
      $("#contactlist_1_result em").removeClass("active");
      $("#contactlist_1_result em span").animate({width: '0', paddingLeft: '0', marginLeft: '0'},200);
      $(this).addClass("active");
      $(this).find("span").animate({width: '13', paddingLeft: '5', marginLeft: '10'},200);
   }else{
      $(this).removeClass("active");
      $(this).find("span").animate({width: '0', paddingLeft: '0', marginLeft: '0'},100);
   }
   return; 
});

$("#contactlist_1_result em span").live("tap click",function(e){
   e.stopPropagation();
   $(this).parent("em").remove();
   return;     
});

//$('#sms-list>li:first').click();

</script>
</body>
</html>
