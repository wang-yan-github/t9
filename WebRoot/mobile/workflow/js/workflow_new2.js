function gotoWork(WORK_TYPE){
    if(WORK_TYPE == "form"){
        getflowContent(); 
    }else if(WORK_TYPE == "turn"){
        turnWorkFlow();
    }else if(WORK_TYPE == "sign"){
        signWorkFlow();
    }else if(WORK_TYPE == "save"){
        saveWorkFlow();   
    }else if(WORK_TYPE == "continueEdit"){
        continueEditFlow();   
    }else if(WORK_TYPE == "stop"){
        stopWorkFlow();   
    }else if(WORK_TYPE == "sign_save_flow"){
        saveSignWorkFlow();     
    }else if(WORK_TYPE == "show_original"){
        showOriginalForm();
    }else if(WORK_TYPE == "sel"){
        selWorkFlow();
    } else if(WORK_TYPE == "new"){
       newFlow();
    }else if(WORK_TYPE == "search"){
       searchFlow();
    }else if(WORK_TYPE == "search_list"){
       searchFlowList();
    }else if(WORK_TYPE == "new_save"){
        saveNewWorkFlow();
    }
}

//新建流程-创建流水号
function getFlowNew(FLOW_ID){
    $.ajax({
        type: 'GET',
        url : contextPath + '/t9/mobile/workflow/act/T9PdaNewFlowAct/newEdit.act',
        cache: true,
        data: {'sessionid': p,'FLOW_ID': FLOW_ID},
        success: function(data){
            var json = eval(data);
            var runName = json[0].runName;
            var flowId = json[0].flowId;
            var AUTO_NEW = 1;
            saveNewWorkFlow(runName, flowId, AUTO_NEW)
        },
        error: function(data){
        	mui.toast(data);
        }
    });
}

/**保存工作流**/
function saveNewWorkFlow(runName, flowId, AUTO_NEW) {
    $.ajax({
        type: 'GET',
        url : contextPath + '/t9/mobile/workflow/act/T9PdaNewFlowAct/newSubmit.act',
        cache: false,
        async: false,
        data: {'sessionid': p,'FLOW_ID': flowId, 'RUN_NAME':runName},
        success: function(data){
            if(data == "NORUNNAME"){
                showFlowMessage(norunname);
                return;
            }else if(data == "NAMEREPEAT"){
                showFlowMessage(namerepeat);
                return;
           }else if(data == "NOCREATERUN"){
                showFlowMessage(nocreaterun);
                return;
           }else{
                var json = eval(data);
                var q_run_id = json[0].q_run_id; 
                var q_flow_id = json[0].q_flow_id;
                var q_prcs_id = json[0].q_prcs_id;//处理步骤顺序，不是流程设置的顺序号
                var q_flow_prcs = json[0].q_flow_prcs;//步骤顺序号
                editWorkFlow(q_run_id, q_flow_id, q_prcs_id, q_flow_prcs, 0);
            }
        },
        error: function(data){
        	mui.toast(data);
        }
    });
}

//refreshFlag:是否刷新父页面
//主办工作
function editWorkFlow(q_run_id, q_flow_id, q_prcs_id, q_flow_prcs, refreshFlag, pp){
	if(pp){
		p = pp;
	}
    $.ajax({
        type: 'GET',
        url : contextPath + '/t9/mobile/workflow/act/T9PdaHandlerAct/checkPriv.act',
        cache: false,
        data: {'sessionid': p, 'RUN_ID': q_run_id,'FLOW_ID': q_flow_id,'PRCS_ID': q_prcs_id,'FLOW_PRCS': q_flow_prcs},
        success: function(data){
            if(data == "NOEDITPRIV"){
            	if(refreshFlag && refreshFlag==3){
            		mui.toast(noeditpriv);
            	}else{
            		showFlowMessage(noeditpriv);
            	}
                return;
            }else{
            	if(refreshFlag && refreshFlag==3){
            		openFlowDetail(q_run_id, q_flow_id, q_prcs_id, q_flow_prcs, refreshFlag, p);
            	}else{
            		openFlowForm(q_run_id, q_flow_id, q_prcs_id, q_flow_prcs, refreshFlag, p);
            	}
                //jQuery(".hk-workapproval").hide();
                //jQuery("#flowFormEdit .mui-content").empty().append(data);
                //jQuery("#flowFormEdit").show();
            }
        },
        error: function(data){
        	mui.toast(data);
        }
    });
}

function openFlowDetail(q_run_id, q_flow_id, q_prcs_id, q_flow_prcs, refreshFlag, p){
	detailWindow = layer.open({
	    type: 2,
	    title: false,
	    shadeClose: false,
	    shade: 0.8,
	    closeBtn: false,
	    area: ['100%', '100%'],
	    content: contextPath + '/t9/mobile/workflow/act/T9PdaSearchAct/detail.act?sessionid='+p+'&refreshFlag='+refreshFlag+'&sessionid='+p+"&RUN_ID="+q_run_id+"&FLOW_ID="+q_flow_id+"&PRCS_ID="+q_prcs_id+"&FLOW_PRCS="+q_flow_prcs
	});
}

function openFlowDBList(p, source){
	detailWindow = layer.open({
	    type: 2,
	    title: false,
	    shadeClose: false,
	    shade: 0.8,
	    closeBtn: false,
	    area: ['100%', '100%'],
	    content: contextPath + '/t9/mobile/workflow/act/T9PdaWorkflowIndexAct/index.act?source='+source+'&sessionid='+p
	});
}

function openFlowForm(q_run_id, q_flow_id, q_prcs_id, q_flow_prcs, refreshFlag, p){
	detailWindow = layer.open({
	    type: 2,
	    title: false,
	    shadeClose: false,
	    shade: 0.8,
	    closeBtn: false,
	    area: ['100%', '100%'],
	    content: contextPath + '/t9/mobile/workflow/act/T9PdaHandlerAct/edit.act?refreshFlag='+refreshFlag+'&sessionid='+p+"&RUN_ID="+q_run_id+"&FLOW_ID="+q_flow_id+"&PRCS_ID="+q_prcs_id+"&FLOW_PRCS="+q_flow_prcs
	});
}

/****************************************************/
//修复chrome下单击触发两次的bug by 
function fixDbClick(e){
   var last_click_timer = this.getAttribute('_last_click_timer_'),
   this_click_timer = (new Date).getTime();
   if(this_click_timer - last_click_timer < 1000){       
      e.stopPropagation();
      return false;
   }
   this.setAttribute('_last_click_timer_', this_click_timer);
}

// 检测中文
function isChineseChar(str){
   var reg = /[\u4E00-\u9FA5\uF900-\uFA2D]$/;
   return reg.test(str);  
}

function showFlowMessage(str){
	if(str){
		mui.toast(str);
	}
	//jQuery("#flowFormEdit").hide();
    //jQuery("#flowChooseStep").hide();
    //jQuery("#flowChooseUser").hide();
    //jQuery("#flowBack").hide();
	setTimeout("closeWin();", 900);
    //jQuery(".hk-workapproval").show();
}

//保存表单
function saveWorkFlow(a){
    var data = $("#edit_from").serialize();
    $.ajax({
        type: 'POST',
        url: contextPath + '/t9/mobile/workflow/act/T9PdaHandlerAct/editSubmit.act',
        cache: false,
        async: false,
        data: data + "&sessionid="+p,
        beforeSend: function(){
           //$.ProLoading.show();
        },
        success: function(data){
            if(a) return;//转交保存
            mui.toast(data);
        },
        error: function(data){
            mui.toast(data);
        }
    });
}

function turnWorkFlow(){
    //lp 2012/4/23 15:14:40 转交时增加保存
    saveWorkFlow('ISFORMTURN'); 
    var flow_type = $("input[name='FLOW_TYPE']").val();
    var url = flow_type == 2 ? contextPath + '/t9/mobile/workflow/act/T9PdaTurnAct/turnUser.act' : contextPath + '/t9/mobile/workflow/act/T9PdaTurnAct/turn.act';
    //TOP_FLAG = $("input[name='TOP_FLAG']").val(); 
    $.ajax({
        type: 'GET',
        url: url,
        cache: false,
        data: {'sessionid': p, 'RUN_ID': q_run_id,'FLOW_ID': q_flow_id,'FLOW_TYPE':flow_type,'PRCS_ID': q_prcs_id,'FLOW_PRCS': q_flow_prcs},
        beforeSend: function(){
        },
        success: function(data){
           if(data == "NOEDITPRIV"){
               showFlowMessage(noeditpriv);
               //delete_flow();
               return;  
           }else if(data == "NOSIGNFLOWPRIV"){
               showFlowMessage(nosignflowpriv);
               //delete_flow();
               return;   
           }else if(data == "NORIGHTNEXTPRCS"){
               showFlowMessage(norightnextprcs);
               return;   
           }else if(data == "NOSETNEWPRCS"){
               showFlowMessage(norightnextprcs);
               return; 
           }else{
               //jQuery(".hk-workapproval").hide();
               jQuery("#flowFormEdit").hide();
               if(flow_type == 1){//固定流程
                   jQuery("#flowChooseStep .mui-content").empty().append(data);
                   jQuery("#flowChooseStep").show();
               }else{
                   //自由流程，直接选人
            	   jQuery("#flowChooseStep .mui-content").empty();
            	   jQuery("#flowChooseStep").hide();
            	   jQuery("#flowChooseUser .mui-content").empty().append(data);
                   jQuery("#flowChooseUser").show();
               }
           }
        },
        error: function(data){
        	mui.toast(data);
        }
    });
}

/**
 * 获取选择下一步骤界面内容（进入选人界面）
 */
function goOnWorkFlow(){
	var btnArray = ['取消', '确定'];
    //判断是否设置了强制转交
    var NOT_ALL_FINISH = $("input[name='NOT_ALL_FINISH']").val(); //未办理完毕的经办人
    var TURN_PRIV = $("input[name='TURN_PRIV']").val(); //强制转交
    if(TURN_PRIV != 1 && NOT_ALL_FINISH !=""){
    	mui.toast("经办人["+NOT_ALL_FINISH+"]尚未办理完毕，不能结束流程！");
        return;
    }
    if(TURN_PRIV == 1 && NOT_ALL_FINISH !=""){
    	mui.confirm('经办人['+NOT_ALL_FINISH+']尚未办理完毕，确认要结束流程吗？', '提示', btnArray, function(e) {
			if (e.index == 1) {
				action = $("input[name='turn_action']").val();
			    var prcs_id_next = $("input[name='NEW_PRCS_ID_NEXT']").val();
			    var flow_type = $("input[name='FLOW_TYPE']").val();
			    if((prcs_id_next == '' || typeof(prcs_id_next) == 'undefined') &&  flow_type != 2){
			    	mui.toast("请选择下一步骤"); 
			    	return ;
			    }
			    $.ajax({
			        type: 'GET',
			        url: action,
			        cache: false,
			        data: {'sessionid': p, 'RUN_ID': q_run_id,'FLOW_ID': q_flow_id,'PRCS_ID': q_prcs_id,'FLOW_PRCS': q_flow_prcs, 'PRCS_ID_NEXT': prcs_id_next, 'FLOW_TYPE': flow_type},
			        beforeSend: function(){
			            //$.ProLoading.show();
			        },
			        success: function(data){
			            if(data == "NONEXTPRCS"){
			                showFlowMessage(nonextprcs);
			                return;   
			            }else if(data == "NOEDITPRIV"){
			                showFlowMessage(noeditpriv);
			                return;  
			            }else if(data == "NOSIGNFLOWPRIV"){
			                showFlowMessage(nosignflowpriv);
			                return;
			            }else if(data == "WORKCOMPLETE"){
			                showFlowMessage(workcomplete);
			                //delete_flow();
			                return;   
			            }
			            if(prcs_id_next == 0){
			            	mui.toast(data); 
			            }else{
			                //jQuery(".hk-workapproval").hide();
			                jQuery("#flowFormEdit").hide();
			                jQuery("#flowChooseStep").hide();
			                jQuery("#flowChooseUser .mui-content").empty().append(data);
			                jQuery("#flowChooseUser").show();
			            }
			        },
			        error: function(data){
			            //$.ProLoading.hide();  
			        	mui.toast(getfature);
			        }
			    });
			} else {
				return;
			}
		});
        /**if(confirm('经办人['+NOT_ALL_FINISH+']尚未办理完毕，确认要结束流程吗？')){
        }else{
            return ;
        }**/
    }else{
    	action = $("input[name='turn_action']").val();
	    var prcs_id_next = $("input[name='NEW_PRCS_ID_NEXT']").val();
	    var flow_type = $("input[name='FLOW_TYPE']").val();
	    if((prcs_id_next == '' || typeof(prcs_id_next) == 'undefined') &&  flow_type != 2){
	    	mui.toast("请选择下一步骤"); 
	    	return ;
	    }
	    $.ajax({
	        type: 'GET',
	        url: action,
	        cache: false,
	        data: {'sessionid': p, 'RUN_ID': q_run_id,'FLOW_ID': q_flow_id,'PRCS_ID': q_prcs_id,'FLOW_PRCS': q_flow_prcs, 'PRCS_ID_NEXT': prcs_id_next, 'FLOW_TYPE': flow_type},
	        beforeSend: function(){
	            //$.ProLoading.show();
	        },
	        success: function(data){
	            if(data == "NONEXTPRCS"){
	                showFlowMessage(nonextprcs);
	                return;   
	            }else if(data == "NOEDITPRIV"){
	                showFlowMessage(noeditpriv);
	                return;  
	            }else if(data == "NOSIGNFLOWPRIV"){
	                showFlowMessage(nosignflowpriv);
	                return;
	            }else if(data == "WORKCOMPLETE"){
	                showFlowMessage(workcomplete);
	                //delete_flow();
	                return;   
	            }
	            if(prcs_id_next == 0){
	            	mui.toast(data); 
	            }else{
	                //jQuery(".hk-workapproval").hide();
	                jQuery("#flowFormEdit").hide();
	                jQuery("#flowChooseStep").hide();
	                jQuery("#flowChooseUser .mui-content").empty().append(data);
	                jQuery("#flowChooseUser").show();
	            }
	        },
	        error: function(data){
	            //$.ProLoading.hide();  
	        	mui.toast(getfature);
	        }
	    });
    }
}

/**
 * 获取选择下一步骤办理人界面内容
 */
function turnUserWorkFlow(){
    var prcs_id_next = $("input[name='NEW_PRCS_ID_NEXT']").val();
    var preset = $("input[name='PRESET']").val();
    var NOT_ALL_FINISH = $("input[name='NOT_ALL_FINISH_NEXT']").val(); //未办理完毕的经办人
    var TURN_PRIV = $("input[name='TURN_PRIV']").val(); //强制转交
    var FLOW_TYPE = $("input[name='FLOW_TYPE']").val(); 
    if(prcs_id_next == "") {
    	mui.toast(error);
        return;
    }else{
        prcs_id_next = decodeURIComponent(prcs_id_next);   //添加URL解码，兼容部分浏览器  
    }
    var POST_STR = "RUN_ID="+q_run_id+"&FLOW_ID="+q_flow_id+"&PRCS_ID="+q_prcs_id+"&FLOW_PRCS="+q_flow_prcs+"&PRCS_ID_NEXT="+prcs_id_next+"&PRESET="+preset + "&FLOW_TYPE=" + FLOW_TYPE;
    var prcs_id_next_arr = prcs_id_next.split(",");
    var _continue = true;
    var _error_step = 0;
    $.each(prcs_id_next_arr, function(key, val){
        if(val){
            var _zbems = $("#USER_ZB_" + val).find("em");
            var _cbems = $("#USER_CB_" + val).find("em");
            if($("#TOP_DEFAULT_" + val).val() != undefined){
                POST_STR += "&TOP_DEFAULT_" + val + "=" + $("#TOP_DEFAULT_" + val).val();
            }
            //判断是否允许主办为空的情况
            if(eval("typeof(allow_zb_isnull_"+val+") !=\"undefined\"")) {
                if(eval("allow_zb_isnull_"+val+" == '0'")) {
                    if(_zbems.length == 0) {
                        _continue = false;
                        _error_step = val;
                        errorblmsg = errorzbisnotnull;
                    }
                }else{
                    if(_zbems.length == 0 && _cbems.length == 0) {
                        _continue = false;
                        _error_step = val;
                        errorblmsg = errorblisnotnull;
                    }
                }
            }
            if(_zbems.length > 0) {
                //新版拼接主办人字符串
                POST_STR += "&PRCS_USER_OP_" + val + "=" + $("#USER_ZB_" + val).find("em").attr("uid");
            }
            //新版经办人拼接
            var PRCS_USER_TMP = "";
            if(_cbems.length > 0) {
                $("#USER_CB_" + val).find("em").each(function() {
                    PRCS_USER_TMP += $(this).attr("uid") + ",";
                });
                POST_STR += "&PRCS_USER_" + val + "=" + PRCS_USER_TMP;
            }
        }
    });
    if(!_continue) {
    	mui.toast(sprintf(errorblmsg,_error_step));
        return;   
    }
    if(TURN_PRIV != 1 && NOT_ALL_FINISH !=""){
    	mui.toast("经办人["+NOT_ALL_FINISH+"]尚未办理完毕，不能转交流程！");
        return;
    }
    if(TURN_PRIV == 1 && NOT_ALL_FINISH !=""){
        /**if(confirm('经办人['+NOT_ALL_FINISH+']尚未办理完毕，确认要转交下一步骤吗？')){
        	
        }else{
            return ;
        }**/
        var btnArray = ['取消', '确定'];
        mui.confirm('经办人['+NOT_ALL_FINISH+']尚未办理完毕，确认要结束流程吗？', '提示', btnArray, function(e) {
			if (e.index == 1) {
				$.ajax({
			        type: 'POST',
			        url: contextPath + '/t9/mobile/workflow/act/T9PdaTurnAct/turnSubmit.act',
			        cache: false,
			        data: POST_STR + "&sessionid=" + p,
			        beforeSend: function(){
			            //$.ProLoading.show();
			        },
			        success: function(data){
			            //$.ProLoading.hide();
			            if(data == "NOEDITPRIV"){
			                showFlowMessage(noeditpriv);
			                //return;  
			            }else if(data == "NOSIGNFLOWPRIV"){
			                showFlowMessage(nosignflowpriv);
			                //return;   
			            }else if(data == "WORKCOMPLETE"){
			                showFlowMessage(workcomplete);
			                //delete_flow();
			                //return;   
			            }else if(data == "WORKHASTURNNEXT"){
			                showFlowMessage(workhasturnnext);
			                //delete_flow();
			                //return; 
			            } else {
			            	mui.toast(data);
			            	return;
			            	//alert(data);
			            }
			            //jQuery(".hk-workapproval").show();
			            //jQuery("#flowChooseStep").hide();
			            //jQuery("#flowChooseUser").hide();
			        },
			        error: function(data){
			            //$.ProLoading.hide();  
			        	mui.toast(getfature);
			        }
			    });
			} else {
				return ;
			}
		});
    }else{
    	$.ajax({
	        type: 'POST',
	        url: contextPath + '/t9/mobile/workflow/act/T9PdaTurnAct/turnSubmit.act',
	        cache: false,
	        data: POST_STR + "&sessionid=" + p,
	        beforeSend: function(){
	        },
	        success: function(data){
	            if(data == "NOEDITPRIV"){
	                showFlowMessage(noeditpriv);
	                //return;  
	            }else if(data == "NOSIGNFLOWPRIV"){
	                showFlowMessage(nosignflowpriv);
	                //return;   
	            }else if(data == "WORKCOMPLETE"){
	                showFlowMessage(workcomplete);
	            }else if(data == "WORKHASTURNNEXT"){
	                showFlowMessage(workhasturnnext);
	            } else {
	            	mui.toast(data);
	            	return;
	            }
	        },
	        error: function(data){
	        	mui.toast(getfature);
	        }
	    });
    }
}

function sprintf(){
    var arg = arguments,str = arg[0] || '',i, n;
    for (i = 1, n = arg.length; i < n; i++) {
        str = str.replace(/%s/, arg[i]);
    }
    return str;
}

/**结束流程**/ 
function stopWorkFlow(){ 
   $.ajax({
      type: 'GET',
      url: contextPath + '/t9/mobile/workflow/act/T9PdaTurnAct/stop.act',
      cache: false,
      data: {'sessionid': p, 'RUN_ID': q_run_id,'FLOW_ID': q_flow_id,'PRCS_ID': q_prcs_id,'FLOW_PRCS': q_flow_prcs},
      beforeSend: function(){
         //$.ProLoading.show();
      },
      success: function(data){
         //$.ProLoading.hide();
         if(data == "NOSUBEDITPRIV"){
        	 showFlowMessage(nosubeditpriv);
            //delete_flow();
            return;   
         }else if(data == "WORKDONECOMPLETE"){
        	 showFlowMessage(workdonecomplete);
            //delete_flow();
            return;
         }else if(data == "TURNNEXT"){
        	 showFlowMessage(workdonecomplete);
            setTimeout(function(){turnWorkFlow();},2000);
            return;   
         }
      },
      error: function(data){
         //$.ProLoading.hide();  
    	  mui.toast(getfature);
      }
   });
}

/*****************************************/
//获取回退页面
function selWorkFlow() {
  $.ajax({
    type : 'GET',
    url: contextPath + '/t9/mobile/workflow/act/T9PdaBackAct/backPage.act',
    cache : false,
    data : {
      'sessionid': p,
      'RUN_ID' : q_run_id,
      'FLOW_ID' : q_flow_id,
      'PRCS_ID' : q_prcs_id,
      'FLOW_PRCS' : q_flow_prcs,
    },
    beforeSend : function() {
    	//$.ProLoading.show();
    },
    success : function(data) {
    	//$.ProLoading.hide();
    	//jQuery(".hk-workapproval").hide();
        jQuery("#flowFormEdit").hide();
        jQuery("#flowChooseStep").hide();
        jQuery("#flowChooseUser").hide();
        jQuery("#flowBack .mui-content").empty().append(data);
        jQuery("#flowBack").show();
    },
    error : function(data) {
      //$.ProLoading.hide();
    	mui.toast("获取失败");
    }
  });
}

//执行回退操作
function goOnSelBackWorkFlow(){
  // 会签内容
  var CONTENT = $("#CONTENT_BACK").val();
  // 回退步骤
  var sel_back_prcs = "";
  $("input[name='PRCS']").each(function(i) {
    if (this.checked == true) {
      sel_back_prcs = this.value;
    }
  });
  if (sel_back_prcs == "") {
	  mui.toast(notselectedstep);
	  return;
  }
  $.ajax({
    type : 'GET',
    url: contextPath + '/t9/mobile/workflow/act/T9PdaBackAct/goback.act',
    cache : false,
    data : {
      'sessionid': p,
      'RUN_ID' : q_run_id,
      'FLOW_ID' : q_flow_id,
      'PRCS_ID' : q_prcs_id,
      'FLOW_PRCS' : q_flow_prcs,
      'FLOW_PRCS_LAST' : sel_back_prcs,
      'CONTENT' : CONTENT,
    },
    beforeSend : function() {
      //$.ProLoading.show();
    },
    success : function(data) {
      //$.ProLoading.hide();
      if (data == "WORKHASNOTGOBACK") {
    	  showFlowMessage(workhasnotgoback);
    	  return;
      } else if (data == "WORKHASGOBACK") {
    	    showFlowMessage(workhasgoback);
    	    //$("#page_10").hide();
            //delete_flow();
            return;
      }
    },
    error : function(data) {
    	//$.ProLoading.hide();
    	mui.toast(getfature);
    }
  });
}