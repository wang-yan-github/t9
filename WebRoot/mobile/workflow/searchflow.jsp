<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@  page import="java.util.Map" %>
<%@  page import="java.util.List" %>
<%@  page import="t9.core.util.T9Utility" %>
<%@  page import="java.util.HashMap" %>
<%@  page import="t9.mobile.util.T9MobileUtility" %>
<%@  page import="t9.mobile.util.T9MobileConfig" %>
<%@ include file="/mobile/workflow/header.jsp" %>
<script type="text/javascript">
var p = "<%=request.getParameter("sessionid")%>";
</script>
<style>
    .mui-card .mui-control-content {
        padding: 10px;
    }
    .mui-control-content {
        height:500px;
    }
    .active span{
        font-weight:bold;
        font-size: 18px;
    }
    .received span{
        font-weight:bold;
        font-size: 18px;
    }
    .mui-table-view-cell p{
        font-size: 16px;
        padding-top:5px; 
    }
    
    .mui-bar-nav ~ .mui-content .mui-pull-top-pocket{
        top:0px;
    }
    
    .mui-bar~.mui-content .mui-fullscreen {
        top: 44px;
        height: auto;
    }
    .mui-pull-top-tips {
        position: absolute;
        top: -20px;
        left: 50%;
        margin-left: -25px;
        width: 40px;
        height: 40px;
        border-radius: 100%;
        z-index: 1;
    }
    .mui-bar~.mui-pull-top-tips {
        top: 24px;
    }
    .mui-pull-top-wrapper {
        width: 42px;
        height: 42px;
        display: block;
        text-align: center;
        background-color: #efeff4;
        border: 1px solid #ddd;
        border-radius: 25px;
        background-clip: padding-box;
        box-shadow: 0 4px 10px #bbb;
        overflow: hidden;
    }
    .mui-pull-top-tips.mui-transitioning {
        -webkit-transition-duration: 200ms;
        transition-duration: 200ms;
    }
    .mui-pull-top-tips .mui-pull-loading {
        /*-webkit-backface-visibility: hidden;
        -webkit-transition-duration: 400ms;
        transition-duration: 400ms;*/
        
        margin: 0;
    }
    .mui-pull-top-wrapper .mui-icon,
    .mui-pull-top-wrapper .mui-spinner {
        margin-top: 7px;
    }
    .mui-pull-top-wrapper .mui-icon.mui-reverse {
        /*-webkit-transform: rotate(180deg) translateZ(0);*/
    }
    .mui-pull-bottom-tips {
        text-align: center;
        background-color: #efeff4;
        font-size: 15px;
        line-height: 40px;
        color: #777;
    }
    .mui-pull-top-canvas {
        overflow: hidden;
        background-color: #fafafa;
        border-radius: 40px;
        box-shadow: 0 4px 10px #bbb;
        width: 40px;
        height: 40px;
        margin: 0 auto;
    }
    .mui-pull-top-canvas canvas {
        width: 40px;
    }
    .mui-slider-indicator.mui-segmented-control {
        background-color: #efeff4;
    }
</style>
<body>
<header id="header" class="mui-bar mui-bar-nav">
    <div class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left"></div>
    <h1 class="mui-title">工作查询</h1>
</header>
<div class="mui-content">
    <div class="mui-input-row mui-search">
        <input type="search" class="mui-input-clear" id="searchInput" onkeyup="enterSearch(event)" placeholder="查询的流水号/名称/文号">
    </div>
    <div id="item1" class="mui-control-content mui-scroll-wrapper mui-active">
        <div class="mui-scroll">
            <ul class="mui-table-view"></ul>
        </div>
    </div>
</div>
</body>
</html>
<script>
mui.init({
    pullRefresh: {
        container: '#item1',
        down: {
            contentrefresh: '正在加载...',
            callback: function() {
            	var self = this;
                setTimeout(function() {
                	var ul = self.element.querySelector('.mui-table-view');
                    while(ul.hasChildNodes()) {//当table下还存在子节点时 循环继续
                        ul.removeChild(ul.firstChild);
                    }
                    var frg = createFragment('down');
                    if(frg){
                        ul.insertBefore(frg, ul.firstChild);
                    }
                    mui('#item1').pullRefresh().endPulldownToRefresh();
                }, 1000);
            }
        },
        up: {
            contentrefresh: '正在加载...',
            callback: function() {
                setTimeout(function() {
                    var ul = document.body.querySelector('.mui-table-view');
                    var frg = createFragment('up');
                    if(frg){
                        ul.appendChild(frg);
                    }
                    mui('#item1').pullRefresh().endPullupToRefresh();
                }, 1000);
            }
        }
    },
    keyEventBind: { backbutton: true }
});
mui.back = function(){
    closeWin();
}
function closeWin(){
    var index = parent.layer.getFrameIndex(window.name); //获取窗口索引
    parent.layer.close(index);
}

function createFragment(types) {
    var name = $("#searchInput").val();
    var fragment = document.createDocumentFragment();
    var tempData;
    var url = "<%=contextPath%>/t9/mobile/workflow/act/T9PdaSearchAct/searchList.act";
    if(types == 'down'){//下拉
        $.ajax({url,  type: "GET",async: false, data:{'SEARCH_NAME':name, "sessionid":p},
            success: function(data){
                if(data && data != "NONEWDATA") {
                    tempData = data;
                }else{
                    mui.toast(nonewdata);
                }
            }
        });
    }else{//上拉
        var lastedId =  jQuery("#item1").find("li:last").attr("q_id");
        if(!lastedId) return null;
        $.ajax({url,  type: "GET",async: false, data:{'SEARCH_NAME':name, "sessionid":p, "LASTEDID": lastedId},
            success: function(data){
                if(data && data != "NONEWDATA") {
                    tempData = data;
                }else{
                    mui.toast(nonewdata);
                }
            }
        });
    }
    if(tempData != null){
        var json_temp = eval(tempData);
        var json = eval(json_temp[0].list);
        var li;
        var temp;
        for (var i = 0; i < json.length; i++) {
            temp = json[i];
            li = document.createElement('li');
            li.className = 'mui-table-view-cell '+temp.CLASS;
            li.setAttribute("q_id", temp.SEQ_ID);
            li.setAttribute("q_run_id", temp.runId);
            li.setAttribute("q_flow_id", temp.flowId);
            li.setAttribute("q_prcs_id", temp.prcsId);
            li.setAttribute("q_flow_prcs", temp.flowPrcs);
            li.setAttribute("q_op_flag", temp.opFlag);
            li.innerHTML = "<span>"+temp.runName+"</span>";
            li.innerHTML += "<p>["+temp.flowName+"]&nbsp;&nbsp;"+temp.prcsName+" "+ temp.opFlagDesc +"</p>";
            li.innerHTML += "<p>[发起人："+temp.userName+"]&nbsp;&nbsp;["+temp.state+"]</p>";
            fragment.appendChild(li);
        }
    }
    return fragment;
}

(function($) {
    $('#item1').scroll({
        indicators: true //是否显示滚动条
    });
    jQuery(".mui-control-content").css("height", (document.documentElement.clientHeight-100) + "px");
    
    $(document).on("tap", ".mui-table-view-cell",function(){
        var q_run_id = this.getAttribute("q_run_id");
        var q_flow_id = this.getAttribute("q_flow_id");
        var q_prcs_id = this.getAttribute("q_prcs_id");
        var q_flow_prcs = this.getAttribute("q_flow_prcs");
        openFlowDetail(q_run_id, q_flow_id, q_prcs_id, q_flow_prcs, 3, p);
        //editWorkFlow(q_run_id, q_flow_id, q_prcs_id, q_flow_prcs, 3, p)
    });
    
    //阻尼系数
    var deceleration = mui.os.ios?0.003:0.0009;
    $('.mui-scroll-wrapper').scroll({
        bounce: false,
        indicators: true, //是否显示滚动条
        deceleration:deceleration
    });
})(mui);
    
/**查询**/
function enterSearch(e) {
    if(e.keyCode == 13) {
    	//var name = $("#searchInput").val();
        $("#searchInput").blur();
        mui('#item1').pullRefresh().pulldownLoading();
    }
}

/**查询**/
function reSearch() {
    mui('#item1').pullRefresh().pulldownLoading();
}
</script>