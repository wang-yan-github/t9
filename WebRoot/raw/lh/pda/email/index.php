<?
   include_once("../header.php");
   include_once("inc/utility_all.php");
   $query = "SELECT count(*) from EMAIL,EMAIL_BODY where EMAIL_BODY.BODY_ID=EMAIL.BODY_ID and BOX_ID=0 and TO_ID='$LOGIN_USER_ID' and SEND_FLAG='1' and (DELETE_FLAG='' or DELETE_FLAG='0' or DELETE_FLAG='2')";
   $TOTAL_ITEMS = resultCount($query);
?>
<body>
<script type="text/javascript">
var Scroll_plist;
var stype = "email";
var p = "<?=$P?>";
var nonewdata = "<?=_('没有新邮件')?>";
var newdata = "<?=_('%s封新邮件')?>";

var nowriteReceiver = "<?=_('收件人不能为空')?>";
var errorWebAddress = "<?=_('外部邮箱格式不对')?>";

/* --- 自定义参数 ---*/
var nomoredata_1 = false;
var noshowPullUp_1 = <? echo ($PAGE_SIZE >= $TOTAL_ITEMS) ? "true" : "false"; ?>;

function pageInit(page_id){
   if(page_id == 1)
   {
      tiScroll_1 = new $.tiScroll({"nomoredata": nomoredata_1, "noshowPullUp":noshowPullUp_1});
      tiScroll_1.init();
   }
   
   if(page_id == 2)
   {
      //zoom页面重新计算附件的标题
      fixZoomPageAttachSize(2);
      
      tiScroll_2 = new $.tiScroll({"page_id": 2, "listType": "readonly"});
      tiScroll_2.init();
   }
   
   if(page_id == 3)
   {
      tiScroll_3 = new $.tiScroll({"page_id": 3, "listType": "readonly"});
      tiScroll_3.init();
   }
   
   if(page_id == "attach_read")
   {
      tiScroll_attach_read = new $.tiScroll({"page_id": "attach_read", "listType": "attach_show"});
      tiScroll_attach_read.init();      
   }
}
</script>   
<?

   //导航数据
   $tHeadData = array(
      "1" => array(
         "l" => array("class" => "","event" => "gohome();", "title" => "首页"),
         "c" => array("title" => "电子邮件"),
         "r" => array("class" => "", "event" => "writeEmail();", "title" => "写邮件")
      ),
      "2" => array(
         "l" => array("class" => "","event" => "reback(2,1);", "title" => "返回"),
         "c" => array("title" => "阅读邮件"),
         "r" => array("class" => "", "event" => "replyEmail();", "title" => "回复")
      ),
      "3" => array(
         "l" => array("class" => "","event" => "reback_fix(3);", "title" => "返回"),
         "c" => array("title" => "写邮件"),
         "r" => array("class" => "", "event" => "sendEmail();", "title" => "发送")
      ),
      "4" => array(
         "l" => array("class" => "","event" => "reback(4,3);", "title" => "返回"),
         "c" => array("title" => "选择收信人")
      ),
      "attach_read" => array(
         "l" => array("class" => "","event" => "reback(\"attach_read\",2);", "title" => "返回"),
         "c" => array("title" => "查看附件")
      )
   ); 

?>
   
   <?=buildHead($tHeadData);?>
   
   <?=buildMessage();?>
   
   <?=buildProLoading();?>
   
   <div id="page_1" class="pages tlist">         
      <div id="wrapper_1" class="wrapper">
         	<div id="scroller_1" class="scroller">
               <?=buildPullDown();?>
               <ul class="comm-list" id="email-list">
               <?
                  if($TOTAL_ITEMS > 0)
                  {
                     //============================ 邮件列表 =======================================
                     $CUR_DATE=date("Y-m-d",time());
                     $COUNT=0;
                     $query = "SELECT EMAIL_ID,FROM_ID,SUBJECT,READ_FLAG,from_unixtime(SEND_TIME) as SEND_TIME,CONTENT,IMPORTANT,ATTACHMENT_ID,ATTACHMENT_NAME,USER.USER_NAME from EMAIL,EMAIL_BODY left join USER on EMAIL_BODY.FROM_ID=USER.USER_ID where EMAIL_BODY.BODY_ID=EMAIL.BODY_ID and BOX_ID=0 and TO_ID='$LOGIN_USER_ID' and SEND_FLAG='1' and (DELETE_FLAG='' or DELETE_FLAG='0' or DELETE_FLAG='2') order by SEND_TIME desc limit 0 ,$PAGE_SIZE";
                     $cursor= exequery($connection,$query);
                     while($ROW=mysql_fetch_array($cursor))
                     {
                        $COUNT++;
                        $EMAIL_ID=$ROW["EMAIL_ID"];
                        $FROM_ID=$ROW["FROM_ID"];
                        $SUBJECT=$ROW["SUBJECT"];
                        $SEND_TIME=$ROW["SEND_TIME"];
                        $IMPORTANT=$ROW["IMPORTANT"];
                        $ATTACHMENT_ID=$ROW["ATTACHMENT_ID"];
                        $ATTACHMENT_NAME=$ROW["ATTACHMENT_NAME"];
                        $FROM_NAME=$ROW["USER_NAME"];
                        $CONTENT = $ROW["CONTENT"];
                        $READ_FLAG = $ROW["READ_FLAG"];
                        
                        $SUBJECT=htmlspecialchars($SUBJECT);
                        if($FROM_NAME == "")
                           $FROM_NAME=$FROM_ID;
                     
                        if($IMPORTANT=='0' || $IMPORTANT=="")
                           $IMPORTANT_DESC="";
                        else if($IMPORTANT=='1')
                           $IMPORTANT_DESC="<font color=red>"._("重要")."</font>";
                        else if($IMPORTANT=='2')
                           $IMPORTANT_DESC="<font color=red>"._("非常重要")."</font>";
                           
                        if($SUBJECT=="")
                           $SUBJECT = _("无标题");
                           
                        if($READ_FLAG!=1)
                        {
                           $Class = " active";
                        }else{
                           $Class = "";
                        }
                     ?>
                        <li class="<?=$fix_for_pad['list-li-style'].$Class?>" q_id="<?=$EMAIL_ID?>">
                              <h3><?=$SUBJECT?> <?=$IMPORTANT_DESC?></h3>
                              <p class="grapc"><?=$FROM_NAME?> - <?=timeintval(strtotime($SEND_TIME))?></p>
                              <? 
                                 if($ATTACHMENT_ID != "" && $ATTACHMENT_NAME != "")
                                 {
                              ?>
                                 <span class="iconbtn attach_icon"></span>
                              <?
                                 }
                              ?>
                                 <span class="ui-icon-rarrow"></span>
                        </li>
               <?
                     }//while
                     echo '</ul>';
                  }else{
                     echo '</ul>';
                     echo '<div class="no_msg">'._("暂无邮件！").'</div>';
                  }
               ?>	 
            		<?=buildPullUp();?>
         	</div>
      </div>
   </div>
   
   <!-- page of read email -->
   <div id="page_2" class="pages tcontent tzoom" style="display:none;">
      <div id="wrapper_2" class="wrapper">
         <div id="scroller_2" class="scroller"></div>      
      </div>
   </div>
   
   <!-- page of write email -->
   <div id="page_3" class="pages tcontent" style="display:none;">
      <div id="wrapper_3" class="wrapper">
         <div id="scroller_3" class="scroller write_mail_scroller">

               <form action="submit.php"  method="post" name="form1" onsubmit="return false;">
                  <div class="read_detail write_mail">
                     <?=_("内部收信人姓名：")?>
                     <span id="contactlist_1_result" class="contactlist_result"></span>
                     <input type="text" class="noborderInput searchInit" id="TO_NAME1" name="TO_NAME1" value=""/>
                     <!--<a href="javascript:void(0)" class="add_btn write_mail_add_btn"></a>-->
                  </div>
                  <div id="contactlist_1" class="contactlist"></div>
                  
                  <div class="read_detail write_mail">
                     <?=_("抄送：")?>
                     <span id="contactlist_3_result" class="contactlist_result"></span>
                     <input type="text" class="noborderInput searchInit" id="TO_NAME3" name="TO_NAME3" value=""/>
                  </div>
                  <div id="contactlist_3" class="contactlist"></div>
                  
                  <div class="read_detail">
                     <table class="tfromTable">
                        <tr>
                           <td style="width:100px;"><?=_("外部邮箱地址：")?></td>
                           <td><input type="text" class="noborderInput w100" id="TO_NAME2" name="TO_NAME2" value="" /></td>
                        </tr>
                     </table>
                  </div>
                  
                  <div class="read_detail">
                     <table class="tfromTable">
                        <tr>
                           <td style="width:50px;"><?=_("主题：")?></td>
                           <td><input type="text" class="noborderInput w100" id="SUBJECT" name="SUBJECT" value="" /></td>
                        </tr>
                     </table>
                  </div>
                  <textarea id="CONTENT" class="noborderTextarea" name="CONTENT" rows="8" cols="10" wrap="on"></textarea>
                  <input type="hidden" name="P" value="<?=$P?>" />
                  <input type="hidden" id="emailType" name="emailType" value="" />
               </form>

         </div>      
      </div>
   </div>
   
   <!-- page of list contact person -->
   <div id="page_4" class="pages tlist" style="display:none;">
      <div id="wrapper_4" class="wrapper">
         <div id="scroller_4" class="scroller">
            <div id="search_box">
               <div id="input_box">
                  <input type="text" id="search_name" name="search_name" value="" destDom="contactlist_4" />   
               </div>
            </div>
            <ul class="comm-list" id="contactlist_4"></ul>        
         </div>      
      </div>
   </div>

   <!-- page of attach_file -->
   <div id="page_attach_read" class="pages tcontent" style="display:none;">
      <div id="wrapper_attach_read" class="wrapper">
         <div id="scroller_attach_read" class="scroller" style="width:100%;height:100%;">
            <div id="layer" style="position: absolute;left: 0; top: 0;height: 100%;width:100%;"></div>
            <iframe id="file_iframe" name="file_iframe" class="attach_iframe" src=""></iframe> 
         </div>      
      </div>
   </div>

<script type="text/javascript" src="/pda/js/udf-1.1.js"></script>
<script type="text/javascript">
$(document).ready(function(){
   pageInit(1);
});

function getEmailContent(obj,email_id){
   $.ajax({
         type: 'GET',
         url: 'read.php',
         cache: true,
         data: {'P': '<?=$P?>','EMAIL_ID': email_id},
         beforeSend: function(){
            $.ProLoading.show();   
         },
         success: function(data){
            obj.removeClass("active");
            $.ProLoading.hide();
            $("#page_2 > #wrapper_2 > #scroller_2").empty().append(data);
            $("#page_2").show('fast',function(){pageInit(2);});
            $("#header_1").hide();
            $("#header_2").show();
         }
   });
}

$("#email-list li").live("click tap",function(){
   $$a = $(this);
   getEmailContent($$a,$$a.attr("q_id"));
});

$(".read_attach > a.pda_attach").live("tap click",function(){
   readAttach($(this));
});

function writeEmail(){
   $("#emailType").val("new");
   $("#contactlist_1_result").empty();
   $("#header_1").hide();
   $("#header_3").show();
   $("#page_3").show('fast',function(){pageInit(3);});
   $("#page_3 #TO_NAME1").focus();     
}

function replyEmail(){
   $("#emailType").val("reply");
   $("#header_2").hide();
   $("#header_3").show();
   $("#page_3").show('fast',function(){pageInit(3);});
   $("#contactlist_1_result").empty().append($("#replyTo").html());
   $("#page_3 #TO_NAME1").focus();    
}

function reback_fix(local){
   var emailType = $("#emailType").val();
   if(emailType == "new")
      to = 1;
   else
      to = 2;
   reback(local,to);  
}

tSearch1 = new $.tSearch({input:"#TO_NAME1",list:"#contactlist_1",appendDom:"#contactlist_1_result"});
tSearch1.init();

tSearch2 = new $.tSearch({input:"#TO_NAME3",list:"#contactlist_3",appendDom:"#contactlist_3_result"});
tSearch2.init();

function sendEmail()
{
   var P = "<?=$P?>";
   var TO_ID = CS_ID = '';
   var WEBMAIL = $("#TO_NAME2").val();
   var TO_NAME3 = $("#TO_NAME3").val();
   var SUBJECT = $("#SUBJECT").val();
   var CONTENT = $("#CONTENT").val();
   
   //收件人user_id
   $("#contactlist_1_result em").each(function(){
      TO_ID += $(this).attr("userid") + ",";   
   });
   
   //抄送user_id 
   $("#contactlist_3_result em").each(function(){
      CS_ID += $(this).attr("userid") + ",";   
   });
   
   //检验收件人
   if(TO_ID=="")
   {
      showMessage(nowriteReceiver);
      $("#TO_NAME1").focus();
      return;      
   }
   
   //检验外部收件人
   if(WEBMAIL!="" && !isEmail(WEBMAIL))
   {
      showMessage(errorWebAddress);
      $("#TO_NAME2").focus();
      return;
   }

   $.ajax({
      type: 'POST',
      url: 'submit.php',
      cache: true,
      data: {"P": P, "TO_ID": TO_ID, "CS_ID": CS_ID, "WEBMAIL": WEBMAIL, "SUBJECT": SUBJECT, "CONTENT": CONTENT},
      beforeSend: function()
      {
         $.ProLoading.show("<?=_('发送中...')?>");   
      },
      success: function(data)
      {
         if(data=="<?=_('邮件发送成功')?>" || data == "<?=_('外部邮件发送成功')?>")
         {
            $.ProLoading.show("<?=_('发送成功')?>");
            setTimeout(function(){
               $.ProLoading.hide();
               var emailType = $("#emailType").val();
               resetEmail();
               if(emailType == "new")
                  reback(3,1);
               else if(emailType == "reply")
                  reback(3,2);
            },1000)
         }
   	}
   });   
}

function resetEmail(){
   $("#contactlist_1_result").empty();
   $("#contactlist_3_result").empty();
   $("#TO_NAME2").val("");
   $("#TO_NAME3").val("");
   $("#SUBJECT").val("");
   $("#CONTENT").val("");   
}
</script>
</body>
</html>