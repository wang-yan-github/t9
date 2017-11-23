<!doctype html>
<html>
<head>
    <title>Demo</title>
    <meta charset="UTF-8">
</head>
<body>
<!-- http://127.168.5.200:8080    服务器地址  
/t9/t9/mobile/attach/act/T9PdaAttachmentAct/upload.act    接口地址
sessionid    会话Id
<input type="hidden" name="moudle" value="notify"/>    value=模版名称
-->
<form method="post" action="http://127.168.5.200:8080/t9/t9/mobile/attach/act/T9PdaAttachmentAct/upload.act?sessionid=AAC6BA73280A2631F1FE83B92D2398F6" enctype="multipart/form-data">  

         <input type="file" name="file"/>  

         <input type="hidden" name="moudle" value="notify"/>

         <input type="submit"/> 

</form>
</body>
</html>