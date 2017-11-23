<?
   include_once("pda/config.php");
	
	function Ag($device){
		return (bool)strpos($_SERVER['HTTP_USER_AGENT'],$device);
	}
	
	function DeviceAgent(){
		$agent = strtolower($_SERVER['HTTP_USER_AGENT']);
		if(strpos($agent,'mac')){
			return "IOS";
		}else if(strpos($agent,'android')){
			return "Android";	
		}else{
			return "unknow";	
		}
	}
	
   if(Ag("iPad")){$fix_for_pad = $C['optimizeiPad'];}
   
   //Ĭ��ÿҳ��ʾ��������
   $PAGE_SIZE = Ag("iPad") ? $fix_for_pad['list-show-num'] : 7;
	
	//��ȡ�û���ͷ��
   function showAvatar($avatar,$sex){
	   return avatar_path($avatar,$sex);	
   }
   
   // 2012/6/1 1:52:38 lp ���绷��
   function showNetType($c_type){
      return $c_type == "mobile" ? "2G/3G" : "Wifi";   
   }
   
   // 2012/2/23 0:17:54 lp ����ҳ�浼��
   function buildHead($data)
   {
      if(!is_array($data))
         return;
      
      $html = '<div id="header">';
      foreach($data as $k => $v)
      {
         $style = "";
         if(isset($v['display'])){
            if($v['display'] == 'none')
            {
               $style = " style='".$v["style"]."display:none;'";
            }else{
               $style = " style='".$v["style"]."'";
            }
         }else{
            if($k!="1") $style = " style='".$v["style"]."display:none;'";
         }
         $html.= '<div id="header_'.$k.'"'.$style.'>';
         
         if($v["l"])
         { 
            $v["l"]["class"] == "" ? $lclass = "" : $lclass = " ".$v["l"]["class"];
            $v["l"]["event"] == "" ? $levent = "" : $levent = "onclick='".$v["l"]["event"]."'";   
            $html.=  '<span class="lcbtn'.$lclass.'" '.$levent.'>
                        <span>'._($v["l"]["title"]).'</span>
                     </span>';
         }
         
         if($v["c"])
         {
            $html.=  '<span class="t">'._($v["c"]["title"]).'</span>';   
         }
         
         if($v["r"])
         {
            if(isset($v["r"]["display"]))
            {
               if($v["r"]["display"] == 'none')
               {
                  $style = " style='".$v["r"]["style"]."display:none;'";
               }else{
                  $style = " style='".$v["r"]["style"]."'";
               }
            }else{
               $style = '';   
            }               
            $v["r"]["class"] == "" ? $rclass = "" : $rclass = " ".$v["r"]["class"];
            $v["r"]["event"] == "" ? $levent = "" : $levent = "onclick='".$v["r"]["event"]."'"; 
            $html.=  '<span class="combtn rbtn'.$rclass.'" '.$levent.$style.'>
                        <span>'._($v["r"]["title"]).'</span>
                     </span>'; 
         }   
         $html.= '</div>';
      }
      $html.= '</div>';
      
      return $html;
   }
   
   // 2012/2/23 0:17:54 lp ����������ȡ����
   function buildPullDown()
   {
      return '<div class="pullDown">
                  <span class="pullWrapper">
                     <span class="pullDownIcon"></span>
                     <span class="pullDownLabel">'._("����ˢ��...").'</span>
               	</span>
            </div>';
   }
   
   // 2012/2/23 0:17:54 lp ����������ȡ����
   function buildPullUp()
   {
      return '<div class="pullUp">
                  <span class="pullWrapper">
                     <span class="pullUpIcon"></span>
                     <span class="pullUpLabel">'._("�������ظ���...").'</span>
               	</span>
            </div>';
   }
   
   // 2012/3/7 14:26:41 lp ������ʾ��Ϣ
   function buildMessage()
   {
      return '<div id="message">
                  <div id="blank" class="transparent_class"></div>
                  <div id="text"></div>
               </div>';   
   }
   
   // 2012/3/26 16:45:11 lp ����ҳ��LoadingЧ��
   function buildProLoading()
   {
      return '<div id="overlay"></div>
               <div class="ui-loader loading">
                  <span class="ui-icon ui-icon-loading"></span>
                  <h1>'._("������...").'</h1>   
               </div>';   
   }
   
   //2012/2/27 1:21:40 lp �ж��Ƿ���ֵ
   function resultCount($sql){
      global $connection;
      $count = 0;
      $cursor = exequery($connection,$sql);
      if($ROW=mysql_fetch_array($cursor))
         $count = $ROW[0];
         
      return $count;        
   }
   //2012/3/27 13:21:40 zy ͳ������
   function resultCountByROW($sql){
      global $connection;
      $count = 0;
      $cursor = exequery($connection,$sql);
      $count=mysql_num_rows($cursor);
         
      return $count;        
   }
   
   function file_icon($ATTACHMENT_NAME){
      $file_ext = explode(".",$ATTACHMENT_NAME);
      $file_ext = $file_ext[1];
      
      $pic = "file.png";
      switch($file_ext)
      {
         case "txt":$pic = "file.png";break;
      }
      return "../style/images/".$pic;
   }

   //2012/5/18 1:57:21 lp �ճ̰�����ɫ
   function cal_level_desc_fix($level="")
   {
      switch($level)
      {
         case "1": return sprintf("<span style='color:#ff0000'>[%s]</span>",_("��Ҫ/����"));
         case "2": return sprintf("<span style='color:#ff9933'>[%s]</span>",_("��Ҫ/������"));
         case "3": return sprintf("<span style='color:#00aa00'>[%s]</span>",_("����Ҫ/����"));
         case "4": return sprintf("<span style='color:#6f7274'>[%s]</span>",_("����Ҫ/������"));
         default : return "";
      }
   }
   
   $CAL_LEVEL_ARRAY = array(
      "1" => _("��Ҫ/����"),
      "2" => _("��Ҫ/������"),
      "3" => _("����Ҫ/����"),
      "4" => _("����Ҫ/������")
   );
?>