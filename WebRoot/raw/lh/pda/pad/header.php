<?
	require_once 'auth.php';
	require_once 'inc/funcs.php';
	ob_clean();
?>
<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=gbk">
	<meta name="viewport" content="width=device-width, minimum-scale=1, maximum-scale=1">
	<meta name="apple-mobile-web-app-status-bar-style" content="black" />
	<meta name="format-detection" content="telephone=no" /> 
	<title><?=$LOGIN_IE_TITLE?></title>
   <script type="text/javascript" src="./js/jquery-1.8.0.min.js.gz"></script>
	<script type="text/javascript" src="./js/iscroll.min.js.gz"></script>
	<link rel="stylesheet"  href="./style/pad.css?v=2012.7.2" />
	<script type="text/javascript">
<? 
   echo "var td_lang = {};
         td_lang.pda = {
            msg_1:'"._("���޸�����Ϣ")."',
            msg_2:'"._("������...")."',
            msg_3:'"._("ҳ����ش���")."',
            msg_4:'"._("����ˢ��...")."',
            msg_5:'"._("�ͷ�����ˢ��...")."',
            msg_6:'"._("�������ظ���...")."',
            msg_7:'"._("�ͷż��ظ���...")."',
            msg_8:'"._("��ȫ���������")."',
            msg_9:'"._("��ȡ������...")."',
         };";
?>
   var C_VER = "<?=$_SESSION['C_VER']?>";
   var P_VER = "<?=$P_VER?>";
	var P = p = "<?=$P?>";
   var isIDevice = (/iphone|ipad/gi).test(navigator.appVersion);
</script>
</head>