<?
	
	require_once 'inc/td_config.php';
	
   //2012/2/16 14:58:31 lp ����PDA΢Ѷ׼ȷ�ж�
   if($_SESSION["P_VER"]){
      $C['msg_type'] = $_SESSION["P_VER"];          
   }else{
      $C['msg_type'] = 3;      
   }
   
   //��ҳͼ������ˢ��ʱ��
   $C['MAIN_PAGE_REF_SEC'] = 60;
	
	//�б�ҳ����ˢ��ʱ��
	$C['MSG_LIST_REF_SEC'] = $SMS_REF_SEC;
	
	//����ˢ��ʱ��
	$C['MSG_DIOG_REF_SEC'] = 5;
	
	//��ȡ��ѯֵʱ��
	$C['SEARCH_REF_SEC'] = 1;
	
	//iPad�ϵ��Ż�
	$C['optimizeiPad']['sms-list-content-li'] = 'foripad';
	$C['optimizeiPad']['sms-list-show-num'] = 20;
	$C['optimizeiPad']['sms-diog-show-num'] = 8;
	
	//pda����ÿҳ���ٸ�ͼ��
   $PAGE_APP_COUNT = 9;
   
	//����ipad�ϵ��Ż�
	$C['optimizeiPad']['list-li-style'] = 'foripad';
	$C['optimizeiPad']['list-show-num'] = 10;
	
?>