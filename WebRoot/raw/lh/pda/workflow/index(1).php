<?
   include_once("../header.php");
   include_once("inc/utility_all.php");

   $query = "SELECT count(*) from FLOW_RUN_PRCS,FLOW_RUN,FLOW_TYPE WHERE FLOW_RUN_PRCS.RUN_ID=FLOW_RUN.RUN_ID and FLOW_RUN.FLOW_ID=FLOW_TYPE.FLOW_ID and USER_ID='$LOGIN_USER_ID' and PRCS_FLAG < '3' and DEL_FLAG=0";
   $TOTAL_ITEMS = resultCount($query);
?>
<body>
<script type="text/javascript">
var stype = "workflow";
var p = "<?=$P?>";
var pre_page = 0;
var fileReadPage = 1;
var nonewdata = "<?=_('û���¹���')?>";
var newdata = "<?=_('%s���¹���')?>";
var noeditpriv = "<?=_('�ް���Ȩ��')?>";
var nosubeditpriv = "<?=_('�޾���Ȩ��')?>";
var noreadflowpriv = "<?=_('�޲鿴��Ȩ��')?>";
var nosignflowpriv = "<?=_('�޻�ǩȨ��')?>";
var norightnextprcs = "<?=_('û�з�����������һ����')?>";
var nosetnewprcs = "<?=_('������δ������һ����')?>";
var workcomplete = "<?=_('�����ѽ���')?>";
var workdonecomplete = "<?=_('�����������')?>";
var workhasnotgoback = "<?=_('�����˻ش˹���')?>";
var workhasgoback = "<?=_('�����Ѿ�����')?>";
var notselectedstep = "<?=_('��ѡ����˲���')?>";
var workhasturnnext = "<?=_('������ת����һ��')?>";
var signisnotempty = "<?=_('��ǩ�������Ϊ��')?>";
var signsuccess = "<?=_('��ǩ�������ɹ�')?>";
var formsuccess = "<?=_('������ɹ�')?>";
var getfature = "<?=_('��ȡʧ��')?>";
var error = "<?=_('���ݲ�ȫδ��ת��')?>";
var errorzbisnotnull = "<?=_('��%s�������˲���Ϊ��')?>";
var errorblisnotnull = "<?=_('��%s�������˲���Ϊ��')?>";
var nocreatepriv = "<?=_('û�и������½�Ȩ�ޣ�����OA����Ա��ϵ')?>";
var noflowlist = "<?=_('�˷���û�����̣�')?>";
var norunname = "<?=_('����/�ĺŲ���Ϊ�գ�')?>";
var noprefix = "<?=_('ǰ׺����Ϊ�գ�')?>";
var nosuffix = "<?=_('��׺����Ϊ�գ�')?>";
var namerepeat = "<?=_('����Ĺ�������/�ĺ���֮ǰ�Ĺ����ظ������������á�')?>";
var nocreaterun = "<?=_('�½�����ʧ�ܣ������´�����')?>";
var nocreaterunpriv = "<?=_('�޿ɰ�������Ȩ�ޣ�')?>";
var g_pre_page = 1;
var g_now_page = 1;

var q_run_id = 0;
var q_flow_id = 0; 
var q_prcs_id = 0;
var q_flow_prcs = 0;
var q_op_flag = 1;

var now_sort = 0;
var parent = 0;
var force_pre_set = 0;			//�Ƿ�ǿ��ǰ��׺

/* --- �Զ������ ---*/
var nomoredata_1 = false;
var noshowPullUp_1 = <? echo ($PAGE_SIZE >= $TOTAL_ITEMS) ? "true" : "false"; ?>;

var nomoredata_15 = false;
var noshowPullUp_15 = true;

var PullUpEvt_15 = "pullUp_search_list";
var PullDownEvt_15 = "pullDown_search_list";

function pageInit(page_id)
{
   if(page_id == 1)
   {
      tiScroll_1 = new $.tiScroll({"nomoredata": nomoredata_1, "noshowPullUp":noshowPullUp_1});
      tiScroll_1.init();
   }
   if(page_id == 2)
   {
      tiScroll_2 = new $.tiScroll({"page_id": 2, "listType": "readonly"});
      tiScroll_2.init();
   }
   if(page_id == 3)
   {
      tiScroll_3 = new $.tiScroll({"page_id": 3, "listType": "readonly"});
      tiScroll_3.init();
   }
   if(page_id == 4)
   {
      tiScroll_4 = new $.tiScroll({"page_id": 4, "listType": "readonly"});
      tiScroll_4.init();
   }
   if(page_id == 5)
   {
      tiScroll_5 = new $.tiScroll({"page_id": 5, "listType": "readonly"});
      tiScroll_5.init();
   }
   if(page_id == 6)
   {
      tiScroll_6 = new $.tiScroll({"page_id": 6, "listType": "readonly"});
      tiScroll_6.init();
   }
   if(page_id == 7)
   {
      tiScroll_7 = new $.tiScroll({"page_id": 7, "listType": "readonly"});
      tiScroll_7.init();
   }
   if(page_id == 8)
   {
      tiScroll_8 = new $.tiScroll({"page_id": 8, "listType": "readonly"});
      tiScroll_8.init();
   }
   if(page_id == 9)
   {
      tiScroll_9 = new $.tiScroll({"page_id": 9, "listType": "readonly"});
      tiScroll_9.init();
   }
   if(page_id == 10)
   {
      tiScroll_10 = new $.tiScroll({"page_id": 10, "listType": "readonly"});
      tiScroll_10.init();
   }
   if(page_id == 11)
   {
      tiScroll_11 = new $.tiScroll({"page_id": 11, "listType": "readonly"});
      tiScroll_11.init();
   }
   if(page_id == 12)
   {
      tiScroll_12 = new $.tiScroll({"page_id": 12, "listType": "readonly"});
      tiScroll_12.init();
   }
   if(page_id == 13)
   {
      tiScroll_13 = new $.tiScroll({"page_id": 13, "listType": "readonly"});
      tiScroll_13.init();
   }
   if(page_id == 14)
   {
      tiScroll_14 = new $.tiScroll({"page_id": 14, "listType": "readonly"});
      tiScroll_14.init();
   }
   if(page_id == 15)
   {
      tiScroll_15 = new $.tiScroll({"page_id": 15,"nomoredata": nomoredata_15, "noshowPullUp":noshowPullUp_15,"PullUpEvt":PullUpEvt_15, "PullDownEvt":PullDownEvt_15});
      tiScroll_15.init();
   }
   if(page_id == "attach_read")
   {
      tiScroll_attach_read = new $.tiScroll({"page_id": "attach_read", "listType": "attach_show"});
      tiScroll_attach_read.init();      
   }
}
</script>    
<?

   //��������
   $tHeadData = array(
      "1" => array(
         "l" => array("class" => "","event" => "gohome();", "title" => "��ҳ"),
         "c" => array("title" => "������"),
		 "r" => array("class" => "","event" => "showMenu(\"list_opts\");", "title" => "����"),
      ),
      "2" => array(//����
         "l" => array("class" => "","event" => "reback(g_now_page,g_pre_page);", "title" => "����"),
         "c" => array("title" => "��������"),
         "r" => array("class" => "", "event" => "showMenu(\"edit_opts\");", "title" => "����")
      ),
      "3" => array(//��
         "l" => array("class" => "","event" => "reback(g_now_page,g_pre_page);", "title" => "����"),
         "c" => array("title" => "�鿴��"),
         "r" => array("class" => "", "event" => "showMenu(\"form_opts\");", "title" => "����")
      ),
      "4" => array(//��ǩ
         "l" => array("class" => "","event" => "reback(4,pre_page);", "title" => "����"),
         "c" => array("title" => "��ǩ"),
         "r" => array("class" => "saveSign", "event" => "", "title" => "")
      ),
      "5" => array(//ת������
         "l" => array("class" => "","event" => "reback(5,pre_page);", "title" => "����"),
         "c" => array("title" => "ת����һ��"),
         "r" => array("class" => "", "event" => "goOnWorkFlow();", "title" => "����")
      ),
      "6" => array(//ת��ѡ��
         "l" => array("class" => "","event" => "reback(6,5);", "title" => "����"),
         "c" => array("title" => "ת��ѡ��"),
         "r" => array("class" => "", "event" => "turnUserWorkFlow();", "title" => "�ύ")
      ),
      "7" => array(//�����б�
         "l" => array("class" => "","event" => "reback(7,1);", "title" => "����"),
         "c" => array("title" => "�������")
      ),
      "8" => array(//�����
         "l" => array("class" => "","event" => "reback(8,1);", "title" => "����"),
         "c" => array("title" => "�����"),
         "r" => array("class" => "", "event" => "showMenu(\"save_opts\");", "title" => "����")
      ),
      "9" => array(//�鿴ԭʼ��
         "l" => array("class" => "","event" => "reback(9,2);", "title" => "����"),
         "c" => array("title" => "ԭʼ���鿴"),
      ),
      "10" => array(//�鿴ԭʼ��
      		"l" => array("class" => "","event" => "reback(10,2);", "title" => "����"),
      		"c" => array("title" => "����"),
      		"r" => array("class" => "", "event" => "goOnSelBackWorkFlow();", "title" => "ȷ��")
      ),
      "11" => array(//�½�����
      		"l" => array("class" => "","event" => "reback(11,1);", "title" => "����"),
      		"c" => array("title" => "�½�����"),
      ),
      "12" => array(//�½�����
      		"l" => array("class" => "","event" => "new_reback();", "title" => "����"),
      		"c" => array("title" => "�½�����"),
      ),
      "13" => array(//�½�����
      		"l" => array("class" => "","event" => "reback(13, 12);", "title" => "����"),
      		"c" => array("title" => "�½�����"),
      		"r" => array("class" => "", "event" => "gotoWork(\"new_save\");", "title" => "ȷ��")
      ),
      "14" => array(//������ѯ
      		"l" => array("class" => "","event" => "reback(14, 1);", "title" => "����"),
      		"c" => array("title" => "������ѯ"),
      		"r" => array("class" => "", "event" => "gotoWork(\"search_list\");", "title" => "ȷ��")
      ),
      "15" => array(//�鿴ԭʼ��
      		"l" => array("class" => "","event" => "reback(15,14);", "title" => "����"),
      		"c" => array("title" => "��ѯ���"),
      ),
      "attach_read" => array(
         "l" => array("class" => "","event" => "reback(\"attach_read\",g_pre_page);", "title" => "����"),
         "c" => array("title" => "�鿴����")
      )
   ); 

?>
   
   <?=buildHead($tHeadData);?>
   <?=buildMessage();?>
   <?=buildProLoading();?>
   
   <span class="mutiMenuLayer" style="display: none;">
		<div class="mutiMenu">
			<em></em>
			<div class="opts"></div>
		</div>
	</span>

	<!-- list of workflow -->
	<div id="page_1" class="pages tlist">
		<div id="wrapper_1" class="wrapper">
			<div id="scroller_1" class="scroller">
         <?=buildPullDown();?>
         <ul class="comm-list" id="workflow_list">
      <?
         if($TOTAL_ITEMS > 0)
         {
            $query = "SELECT * from FLOW_RUN_PRCS,FLOW_RUN,FLOW_TYPE WHERE FLOW_RUN_PRCS.RUN_ID=FLOW_RUN.RUN_ID and FLOW_RUN.FLOW_ID=FLOW_TYPE.FLOW_ID and USER_ID='$LOGIN_USER_ID' and DEL_FLAG=0 and PRCS_FLAG<'3' order by FLOW_RUN_PRCS.CREATE_TIME desc limit 0,$PAGE_SIZE";
            $cursor= exequery($connection,$query);
            while($ROW=mysql_fetch_array($cursor))
            {
               $PRCS_ID = $ROW["PRCS_ID"];
               $RUN_ID = $ROW["RUN_ID"];
               $FLOW_ID = $ROW["FLOW_ID"];
               $PRCS_FLAG = $ROW["PRCS_FLAG"];
               $FLOW_PRCS = $ROW["FLOW_PRCS"];
               $OP_FLAG = $ROW["OP_FLAG"];
               $CREATE_TIME = $ROW["CREATE_TIME"];
            
                if($OP_FLAG=="1")
                   $OP_FLAG_DESC=_("����");
                else
                	 $OP_FLAG_DESC=_("��ǩ");
            
               if($PRCS_FLAG=="1")
               {
                  //LP 2012/4/18 10:58:25 ����δ���չ�����������
                  $STATUS=_("δ����");
                  $Class = " active";
               }else if($PRCS_FLAG=="2")
               {
                  $STATUS=_("�ѽ���");
                  $Class = "";
               }
            
               $query = "SELECT FLOW_ID,RUN_NAME from FLOW_RUN WHERE RUN_ID='$RUN_ID'";
               $cursor1= exequery($connection,$query);
               if($ROW=mysql_fetch_array($cursor1))
               {
                  $FLOW_ID=$ROW["FLOW_ID"];
                  $RUN_NAME=$ROW["RUN_NAME"];
               }
            
               $query = "SELECT FLOW_NAME,FLOW_TYPE from FLOW_TYPE WHERE FLOW_ID='$FLOW_ID'";
               $cursor1= exequery($connection,$query);
               if($ROW=mysql_fetch_array($cursor1))
               {
                  $FLOW_NAME=$ROW["FLOW_NAME"];
                  $FLOW_TYPE=$ROW["FLOW_TYPE"];
               }
            
               if($FLOW_TYPE=="1")
               {
                 $query = "SELECT PRCS_NAME,FEEDBACK from FLOW_PROCESS WHERE FLOW_ID='$FLOW_ID' AND PRCS_ID='$FLOW_PRCS'";
                 $cursor1= exequery($connection,$query);
                 if($ROW=mysql_fetch_array($cursor1))
                 {
                    $PRCS_NAME=sprintf(_("��%s����"), $PRCS_ID).$ROW["PRCS_NAME"];
                    $FEEDBACK=$ROW["FEEDBACK"];
                 }
               }
               else
               {
                 $PRCS_NAME=sprintf(_("��%s��"), $PRCS_ID);
                 $FEEDBACK = 0;
               }
      ?>
            <li class="<?=$fix_for_pad['list-li-style'].$Class?>"
						q_id="<?=$CREATE_TIME?>" q_run_id="<?=$RUN_ID?>"
						q_flow_id="<?=$FLOW_ID?>" q_prcs_id="<?=$PRCS_ID?>"
						q_flow_prcs="<?=$FLOW_PRCS?>" q_op_flag="<?=$OP_FLAG?>">
						<h3>[<?=$RUN_ID?>] - <?=$FLOW_NAME?> - <?=$RUN_NAME?></h3>
						<p class="grapc"><?=$PRCS_NAME?> <?=$OP_FLAG_DESC?></p> <span
						class="ui-icon-rarrow"></span>
					</li>
         <?
            }//while
            echo '</ul>';
         }else{
            echo '</ul>';
            echo '<div class="no_msg">'._("���޴�����������").'</div>';
         }
         ?>	 
            <?=buildPullUp();?>
         
			
			
			
			</div>
		</div>
	</div>

	<!-- page of edit workflow -->
	<div id="page_2" class="pages tcontent" style="display: none;">
		<div id="wrapper_2" class="wrapper tform_wrapper">
			<div id="scroller_2" class="scroller"></div>
		</div>
	</div>

	<!-- page of read form -->
	<div id="page_3" class="pages tcontent" style="display: none;">
		<div id="wrapper_3" class="wrapper">
			<div id="scroller_3" class="scroller"></div>
		</div>
	</div>

	<!-- page of sign -->
	<div id="page_4" class="pages tcontent" style="display: none;">
		<div id="wrapper_4" class="wrapper">
			<div id="scroller_4" class="scroller"></div>
		</div>
	</div>

	<!-- page of turn1 -->
	<div id="page_5" class="pages tcontent" style="display: none;">
		<div id="wrapper_5" class="wrapper tform_wrapper">
			<div id="scroller_5" class="scroller"></div>
		</div>
	</div>

	<!-- page of turn2 -->
	<div id="page_6" class="pages tcontent" style="display: none;">
		<div id="wrapper_6" class="wrapper tform_wrapper">
			<div id="scroller_6" class="scroller"></div>
		</div>
	</div>

	<!-- page of end -->
	<div id="page_7" class="pages tlist" style="display: none;">
		<div id="wrapper_7" class="wrapper">
			<div id="scroller_7" class="scroller"></div>
		</div>
	</div>

	<!-- page of save form -->
	<div id="page_8" class="pages tlist" style="display: none;">
		<div id="wrapper_8" class="wrapper">
			<div id="scroller_8" class="scroller"></div>
		</div>
	</div>

	<!-- page of form -->
	<div id="page_9" class="pages tcontent tzoom" style="display: none;">
		<div id="wrapper_9" class="wrapper">
			<div id="scroller_9" class="scroller"></div>
		</div>
	</div>

	<!-- page of sel_back -->
	<div id="page_10" class="pages tcontent" style="display: none;">
		<div id="wrapper_10" class="wrapper tform_wrapper">
			<div id="scroller_10" class="scroller"></div>
		</div>
	</div>

	<!-- page of new_flow -->
	<div id="page_11" class="pages tlist" style="display: none;">
		<div id="wrapper_11" class="wrapper">
			<div id="scroller_11" class="scroller">
				<ul class="comm-list comm-pic-list">

				</ul>
			</div>
		</div>
	</div>

	<!-- page of new_flow -->
	<div id="page_12" class="pages tlist" style="display: none;">
		<div id="wrapper_12" class="wrapper">
			<div id="scroller_12" class="scroller">
				<ul class="comm-list comm-pic-list">

				</ul>
			</div>
		</div>
	</div>
	
	<div id="page_13" class="pages tcontent" style="display: none;">
		<div id="wrapper_13" class="wrapper tform_wrapper">
			<div id="scroller_13" class="scroller"></div>
		</div>
	</div>
	<div id="page_14" class="pages tcontent" style="display: none;">
		<div id="wrapper_14" class="wrapper tform_wrapper">
			<div id="scroller_14" class="scroller"></div>
		</div>
	</div>
	<div id="page_15" class="pages tlist" style="display: none;">
		<div id="wrapper_15" class="wrapper">
			<div id="scroller_15" class="scroller">
				<?=buildPullDown();?>
         	<ul class="comm-list" id="search_list">
         	
         	</ul>
         	<?=buildPullUp();?>
			</div>
		</div>
	</div>

	<!-- page of attach_file -->
	<div id="page_attach_read" class="pages tcontent"
		style="display: none;">
		<div id="wrapper_attach_read" class="wrapper">
			<div id="scroller_attach_read" class="scroller"
				style="position: relative; width: 100%; height: 100%;">
				<div id="layer"
					style="position: absolute; left: 0; top: 0; height: 100%; width: 100%;"></div>
				<iframe id="file_iframe" name="file_iframe" class="attach_iframe"
					src=""></iframe>
			</div>
		</div>
	</div>
	
	<div id="list_opts" class="list_opts" style="display:none;">
	   <span class="new_flow"><?=_('�½�����')?></span>
	   <span class="search_flow"><?=_('������ѯ')?></span>   
   </div>

	<script type="text/javascript" src="/pda/js/udf-1.1.js"></script>
	<script type="text/javascript" src="/pda/js/workflow.js"></script>
	<script type="text/javascript">
function triggerFlowEvt(){
   $(".read_flow").bind("tap click",function(){gotoWork('form');});
   $(".stop_flow").bind("tap click",function(e){gotoWork('stop'); });
   $(".save_flow").bind("tap click",function(e){gotoWork('save'); });
   $(".sign_flow").bind("tap click",function(e){gotoWork('sign'); });
   $(".turn_flow").bind("tap click",function(e){gotoWork('turn'); });
   $(".new_flow").bind("tap click",function(e){gotoWork('new'); });
   $(".search_flow").bind("tap click",function(e){gotoWork('search'); });
   $(".sign_save_flow").bind("tap click",function(e){gotoWork('sign_save_flow'); });
   $(".continueEdit_flow").bind("tap click",function(e){gotoWork('continueEdit'); });
   $(".show_original_form").bind("tap click",function(e){gotoWork('show_original'); });
   $(".sel_flow").bind("tap click",function(e){gotoWork('sel'); });
   $("#overlay").bind("click",function(e){
      e.stopPropagation();
      if($(this).hasClass("overlayGray"))
         $.mutiMenu.hide();
   })   
}

function gotoWork(WORK_TYPE)
{   
   if(WORK_TYPE == "form")
   {
      getflowContent(); 
   }else if(WORK_TYPE == "turn")
   {
      turnWorkFlow();
   }else if(WORK_TYPE == "sign")
   {
      signWorkFlow();
   }else if(WORK_TYPE == "save")
   {
      saveWorkFlow();   
   }else if(WORK_TYPE == "continueEdit")
   {
      continueEditFlow();   
   }else if(WORK_TYPE == "stop")
   {
      stopWorkFlow();   
   }else if(WORK_TYPE == "sign_save_flow"){
   	saveSignWorkFlow();		
   }else if(WORK_TYPE == "show_original"){
   	showOriginalForm();
   }
   else if(WORK_TYPE == "sel"){
      selWorkFlow();
   }
   else if(WORK_TYPE == "new"){
	   newFlow();
   }
   else if(WORK_TYPE == "search"){
	   searchFlow();
	}
   else if(WORK_TYPE == "search_list"){
	   searchFlowList();
	}
   else if(WORK_TYPE == "new_save"){
			saveNewWorkFlow();
	}
   $.mutiMenu.hide();
}

function showMenu(opts_type)
{
   if($("#"+opts_type).length > 0)
   {
      var menu = $("#"+opts_type).html();
      $.mutiMenu.init(menu);
      $.mutiMenu.show();
      triggerFlowEvt();
   }   
}

//�����ز���
function new_reback()
{
	if(now_sort == parent)
		return ;
	if(parent == 0){
		reback(12,11);
		now_sort = 0;
		return ;
	}
	getFlowNewlist(parent);
}

//2012/4/25 16:26:41 lp �Զ�������ˢ�¹��ܣ��ضϲ���Դ�ˢ��

$("a.pda_attach").live("tap click",function(){
   readAttach($(this));
});
</script>
</body>
</html>