<?
include_once("../header.php");
include_once("inc/utility_all.php");
ob_clean();

if($FLAG==1)
{
   $SUBJECT="Re:".$SUBJECT;
   $TO_NAME1=$FROM_NAME;
   $GO_BACK = "read.php?P=".$P."&EMAIL_ID=".$EMAIL_ID;
}
else
{
   $GO_BACK = "index.php?P=".$P;
}
?>
<body onLoad="document.form1.TO_NAME1.focus();">
<div data-role="page" id="email-write-page">
   
   <div data-role="header" data-position="inline" data-theme="b">
      <a class="ButtonBack" data-icon="arrow-l" data-ajax="false" data-transition="<?=$deffect[DeviceAgent()]['flip']?>" href="<?=$GO_BACK?>"><?=_("����")?></a>
      <h1><?=_("д���ʼ�")?></h1>
      <a data-icon="arrow-r" href="javascript:void(0)" id="send_email_btn" data-theme="c" data-role="button" data-theme="c" data-iconpos="left" ><?=_("����")?></a>
   </div>

   <div data-role="content" id="email-send">
      <form action="submit.php"  method="post" name="form1">
         <p><?=_("�ڲ�������������")?></p>
         <input type="text" id="TO_NAME1" name="TO_NAME1" value="<?=$TO_NAME1?>"/>
         <p><?=_("�ⲿ�����˵�ַ��")?></p>
         <input type="text" id="TO_NAME2" name="TO_NAME2" value="<?=$TO_NAME2?>" />
         <p><?=_("�ʼ����⣺")?></p>
         <input type="text" id="SUBJECT" name="SUBJECT" value="<?=$SUBJECT?>" />
         <p><?=_("�ʼ����ݣ�")?></p>
         <textarea id="CONTENT" name="CONTENT" rows="5" wrap="on"></textarea>
         <input type="hidden" name="P" value="<?=$P?>" />
      </form>
   </div>
</div>
<script type="text/javascript">
   $("#send_email_btn").live("tap onclick",function(){
      var P = "<?=$P?>";
      var TO_NAME1 = $("#TO_NAME1").val();
      var TO_NAME2 = $("#TO_NAME2").val();
      var SUBJECT = $("#SUBJECT").val();
      var CONTENT = $("#CONTENT").val();
     
      $.post("submit.php",{P: P,TO_NAME1: TO_NAME1, TO_NAME2: TO_NAME2, SUBJECT: SUBJECT, CONTENT: CONTENT},function(data){
         $.mobile.loadingMessage = data;
         $.mobile.showPageLoadingMsg();
         setTimeout(function(){
            $.mobile.hidePageLoadingMsg();
            if(data=="<?=_('�ʼ����ͳɹ�')?>" || data == "<?=_('�ⲿ�ʼ����ͳɹ�')?>"){
               location.href='<?=$GO_BACK?>';   
            }
         },2000);   
      }); 
   });
</script>
</body>
</html>