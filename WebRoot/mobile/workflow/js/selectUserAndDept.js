function getJsonRs(url,param,async,callback){
	if(!param) param = {};
    var jsonObj = null;
    jQuery.ajax({
		type:"post",
		dataType:"html",
		url:url,
		data:param,
		async:(async?async:false),
		success:function(data){
			jsonObj = eval("("+data+")");
			if(callback){
				callback(jsonObj);
		    }
		},
		error:function(XMLHttpRequest, textStatus, errorThrown){
			jsonObj = {rtState:"1",rtMsg:"Ajax Request Error"};
		}
	});
	return jsonObj;
}

var selected;
var selectedDesc;
var ids;
var desc;
var title1;
var title2;
(function($){
	var curNode;
	$.fn.JTree=function(opt){
		var obj = $(this);
		if(opt=='options'){
			return obj[0];
		}
	}
	
	window.JTree=function(jso){
		var initUrl = jso.initUrl;//初始化url
		var params = jso.params;//初始化url
		var renderTo = jso.renderTo;//渲染对象
		var expand = jso.expand;//展开事件
		var mousedown = jso.mousedown;//点击事件

		var rootDiv = $("<div></div>");
		$("#"+renderTo).html("").append(rootDiv);
		
		//初始化
		this.init=function(){
			curNode = rootDiv;
			var json = getJsonRs(initUrl,params);
			if(json.rtState=="0"){
				var datas = json.rtData;
				for(var i=0;i<datas.length;i++){
					addNode(datas[i]);
				}
			}
		}
		
		var addNode = function(data){
			var nodeModel = data;
			var child = curNode.next();
			if(!child || child.attr('class')!='jtree-child'){
				child = $("<div class='jtree-child'></div>").insertAfter(curNode);
			}
			var cur = $("<div opened='0'></div>");
			var extra = $("<a style='margin-left:18px'></a>");
			var text = $("<a href='javascript:void(0)'><img src='"+data.imgAddress+"' />&nbsp;&nbsp;"+data.name+"</a>");
			cur.append(extra).append(text);
			if(data.isHaveChild!=0){
				extra.css({marginLeft:0});
				extra.html("<img src='/t9/core/styles/style1/img/dtree/plus.gif'>");
				extra.click(function(){
					curNode = cur;
					var next = cur.next();
					if(!next || next.attr('class')!='jtree-child'){
						next = undefined;
					}
					if(cur.attr('opened')=='0'){
						extra.html("<img src='/t9/core/styles/style1/img/dtree/minus.gif'>");
						if(next) next.show();
						cur.attr('opened','1');
					}else{
						extra.html("<img src='/t9/core/styles/style1/img/dtree/plus.gif'>");
						if(next) next.hide();
						cur.attr('opened','0');
					}
					if(expand){
						expand(nodeModel);
						if(curNode.next().attr('class')=='jtree-child'){
							return;
						}else{
							var url = $("#"+renderTo)[0].url;
							if(!url){
								return;
							}
							var json = getJsonRs(url,{});
							if(json.rtState=="0"){
								var datas = json.rtData;
								for(var i=0;i<datas.length;i++){
									addNode(datas[i]);
								}
							}
						}
					}
				});
			}
			text.click(function(){
				curNode = cur;
				if(mousedown){
					mousedown(nodeModel);
				}
			});
			child.append(cur);
		}
	}
})(jQuery);


function SelectDept(selected, selectedDesc){
	//window.oiScroll.scrollTo(0,-10000,1,true);
	jQuery("#treeDiv").show();
	jQuery("#forms").hide();
	jQuery("#left").html("");
	jQuery("#center").html("");
	jQuery("#right").html("");
	
	window.selected = document.getElementById(selected);
	window.selectedDesc = document.getElementById(selectedDesc);
	title1 = "部门列表";
	title2 = "已选部门";
	ids = new Array();
	desc = new Array();

	var sp = window.selected.value.split(",");
	for(var i=0;i<sp.length;i++){
		if(sp[i]!=""){
			ids.push(sp[i]);
		}
	}
	sp = window.selectedDesc.value.split(",");
	for(var i=0;i<sp.length;i++){
		if(sp[i]!=""){
			desc.push(sp[i]);
		}
	}
	
	var url = contextPath+"/t9/core/funcs/orgselect/act/T9DeptSelectAct/getDeptTree.act";
	var tree = new JTree({
		initUrl:url,
		params:{id:0},
		renderTo:"left",
		expand:function(node){
			jQuery("#left").JTree('options').url=contextPath+"/t9/core/funcs/orgselect/act/T9DeptSelectAct/getDeptTree.act?id="+node.nodeId;
		},
		mousedown:function(node){
			jQuery("#center").html("<input type='button' value='返回顶层部门' onclick='renderTopDept()' />");
			var url = contextPath+"/t9/core/funcs/orgselect/act/T9DeptSelectAct/getDeptTree.act?id="+node.nodeId;
			var json = getJsonRs(url,{});
			if(json.rtState=="0"){
				var datas = json.rtData;
				renderDept(datas);
			}
		}
	});
	tree.init();
	renderTopDept();
	renderSelected();
}

function renderTopDept(){
	var url = contextPath+"/t9/core/funcs/orgselect/act/T9DeptSelectAct/getDeptTree.act";
	var json = getJsonRs(url,{});
	if(json.rtState=="0"){
		var datas = json.rtData;
		renderDept(datas);
	}
}

function SelectUser(selected,selectedDesc){
	//window.oiScroll.scrollTo(0,-10000,1,true);
	jQuery("#treeDiv").show();
	jQuery("#forms").hide();
	jQuery("#left").html("");
	jQuery("#center").html("");
	jQuery("#right").html("");
	
	window.selected = document.getElementById(selected);
	window.selectedDesc = document.getElementById(selectedDesc);
	title1 = "人员列表";
	title2 = "已选人员";
	ids = new Array();
	desc = new Array();

	var sp = window.selected.value.split(",");
	for(var i=0;i<sp.length;i++){
		if(sp[i]!=""){
			ids.push(sp[i]);
		}
	}
	sp = window.selectedDesc.value.split(",");
	for(var i=0;i<sp.length;i++){
		if(sp[i]!=""){
			desc.push(sp[i]);
		}
	}
	
	var url = contextPath+"/t9/core/funcs/orgselect/act/T9DeptSelectAct/getDeptTree.act";
	var tree = new JTree({
		initUrl:url,
		params:{id:0},
		renderTo:"left",
		expand:function(node){
			jQuery("#left").JTree('options').url=contextPath+"/t9/core/funcs/orgselect/act/T9DeptSelectAct/getDeptTree.act?id="+node.nodeId;
		},
		mousedown:function(node){
			var url = contextPath+"/t9/core/module/org_select/act/T9OrgSelect2Act/getPersonsByDept.act?deptId="+node.nodeId;
			var json = getJsonRs(url,{});
			if(json.rtState=="0"){
				var datas = json.rtData;
				renderUser(datas);
			}
		}
	});
	tree.init();
	renderSelected();
}

function renderUser(datas){
	var html = "<table style='width:100%;text-align:center'>";
	html+="<tr style='background:#F8FFBA;border:1px solid gray'><td>"+title1+"</td></tr>";
	for(var i=0;i<datas.length;i++){
		var data = datas[i];
		var style = "background:none";
		if(findInSet(data.userId,ids)!=-1){
			style = "background:#f0f0f0";
		}
		html+="<tr id='tr"+data.userId+"' style=\""+style+"\" onclick=\"selectItem(this,"+data.userId+",'"+data.userName+"')\"><td style='border:1px solid gray'>"+data.userName+"</td></tr>";
	}
	html += "</table>";
	jQuery("#center").html(html);
	renderSelected();
}

function renderDept(datas){
	var html = "<table style='width:100%;text-align:center'>";
	html+="<tr style='background:#F8FFBA;border:1px solid gray'><td>"+title1+"<br/><input type='button' value='返回顶层部门' onclick='renderTopDept()' /></td></tr>";
	for(var i=0;i<datas.length;i++){
		var data = datas[i];
		var style = "background:none";
		if(findInSet(data.nodeId,ids)!=-1){
			style = "background:#f0f0f0";
		}
		html+="<tr id='tr"+data.nodeId+"' style=\""+style+"\" onclick=\"selectItem(this,"+data.nodeId+",'"+data.name+"')\"><td style='border:1px solid gray'>"+data.name+"</td></tr>";
	}
	html += "</table>";
	jQuery("#center").html(html);
	renderSelected();
}

function renderSelected(){
	var html = "<table style='width:100%;text-align:center'>";
	html+="<tr style='background:#F8FFBA;border:1px solid gray'><td>"+title2+"</td></tr>";
	for(var i=0;i<ids.length;i++){
		var id = ids[i];
		var name = desc[i];
		html+="<tr style=\"background:#f0f0f0\" onclick=\"cancelSelectItem("+id+",this)\"><td style='border:1px solid gray'>"+name+"</td></tr>";
	}
	html += "</table>";
	jQuery("#right").html(html);
}

function selectItem(obj,id,desc0){
	var index = findInSet(id,ids);
	if(index!=-1){
		ids.splice(index,1);
		desc.splice(index,1);
		obj.style.background="none";
	}else{
		ids.push(id);
		desc.push(desc0);
		obj.style.background="#f0f0f0";
	}
	selected.value = ids;
	selectedDesc.value = desc;
	renderSelected();
}

function cancelSelectItem(id,obj){
	var tr = document.getElementById('tr'+id);
	if(tr){
		tr.onclick(tr,id);
	}else{
		var index = findInSet(id,ids);
		ids.splice(index,1);
		desc.splice(index,1);
	}
	selected.value = ids;
	selectedDesc.value = desc;
	jQuery(obj).remove();
}

function findInSet(id,arr){
	for(var i=0;i<arr.length;i++){
		if((arr[i]+"")==(id+"")){
			return i;
		}
	}
	return -1;
}
