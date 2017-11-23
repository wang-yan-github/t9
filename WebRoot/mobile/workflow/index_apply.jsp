<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/mobile/workflow/header.jsp" %>
<%
    Map n = (Map)request.getAttribute("n");
    int total_items = (Integer)n.get("total_items");
    List<Map> list = (List)n.get("list");
    
    Map n2 = (Map)request.getAttribute("n2");
    int total_items2 = (Integer)n2.get("total_items");
    List<Map> list2 = (List)n2.get("list");
%>
<script type="text/javascript">
    mui.init();
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
    .mui-table-view-cell p{
        font-size: 16px;
        padding-top:5px; 
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
<header class="mui-bar mui-bar-nav">
    <a class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left"></a>
    <h1 class="mui-title">我的申请</h1>
</header>
<div class="mui-content">
    <div style="padding: 10px 10px;">
        <div id="segmentedControl" class="mui-segmented-control">
            <a class="mui-control-item mui-active" href="#item1">审批中</a>
            <a class="mui-control-item" href="#item2">已完成</a>
        </div>
    </div>
    <div class="mui-slider-group" id="slider" class="mui-slider mui-fullscreen">
        <div id="item1" class="mui-slider-item mui-control-content mui-scroll-wrapper mui-active">
            <div class="mui-scroll">
                <ul class="mui-table-view">
                    <%
		                for (Map m : list) {
		            %>
		                <li class="mui-table-view-cell"
		                    q_id="<%=m.get("SEQ_ID") %>" q_run_id="<%=(Integer)m.get("RUN_ID") %>"
		                    q_flow_id="<%=(Integer)m.get("FLOW_ID") %>" q_prcs_id="<%=(Integer)m.get("PRCS_ID") %>"
		                    q_flow_prcs="<%=(Integer)m.get("FLOW_PRCS") %>" q_op_flag="<%=(Integer)m.get("OP_FLAG") %>">
		                    <span>
		                    <%=(String)m.get("RUN_NAME") %>
		                    </span>
		                    <p>
		                        [<%=(String)m.get("FLOW_NAME") %>]&nbsp;&nbsp;<%=(String)m.get("PRCS_NAME") %><br/>
		                        [发起时间：<%=(String)m.get("BEGIN_TIME") %>]<br/>
		                        [主办人：<%=(String)m.get("USER_NAME") %>]
		                    </p> 
		                </li>
		            <%
		                }
		            %>
                </ul>
            </div>
        </div>
        <div id="item2" class="mui-control-content mui-slider-item mui-scroll-wrapper">
            <div class="mui-scroll">
	            <ul class="mui-table-view">
	                <%
                        for (Map m : list2) {
                    %>
                        <li class="mui-table-view-cell"
                            q_id="<%=m.get("SEQ_ID") %>" q_run_id="<%=(Integer)m.get("RUN_ID") %>"
                            q_flow_id="<%=(Integer)m.get("FLOW_ID") %>" q_prcs_id="<%=(Integer)m.get("PRCS_ID") %>"
                            q_flow_prcs="<%=(Integer)m.get("FLOW_PRCS") %>" q_op_flag="<%=(Integer)m.get("OP_FLAG") %>">
                            <span>
                            <%=(String)m.get("RUN_NAME") %>
                            </span>
                            <p>
                                [<%=(String)m.get("FLOW_NAME") %>]&nbsp;&nbsp;<%=(String)m.get("PRCS_NAME") %> <%=(String)m.get("OP_FLAG_DESC") %><br/>
                                [结束时间：<%=(String)m.get("END_TIME") %>]<br/>
                                [主办人：<%=(String)m.get("USER_NAME") %>]
                            </p> 
                        </li>
                    <%
                        }
                    %>
	            </ul>
            </div>
        </div>
    </div>
</div>
</body>
</html>
<script>
	mui.init({keyEventBind: { backbutton: true }});
	mui.back = function(){
	    closeWin();
	}
	function closeWin(){
        var index = parent.layer.getFrameIndex(window.name); //获取窗口索引
        parent.layer.close(index);
    }
    (function($) {
        $('#item1').scroll({
            indicators: true //是否显示滚动条
        });
        $('#item2').scroll({
            indicators: true //是否显示滚动条
        });
        jQuery(".mui-control-content").css("height", (document.documentElement.clientHeight-115) + "px")
        
        $(document).on("tap", ".mui-table-view-cell",function(){
            var q_run_id = this.getAttribute("q_run_id");
            var q_flow_id = this.getAttribute("q_flow_id");
            var q_prcs_id = this.getAttribute("q_prcs_id");
            var q_flow_prcs = this.getAttribute("q_flow_prcs");
            openFlowDetail(q_run_id, q_flow_id, q_prcs_id, q_flow_prcs, 3, p);
        });
    })(mui);
</script>
<script>
    (function($) {
        //阻尼系数
        var deceleration = mui.os.ios?0.003:0.0009;
        $('.mui-scroll-wrapper').scroll({
            bounce: false,
            indicators: true, //是否显示滚动条
            deceleration:deceleration
        });
        $.ready(function() {
            //循环初始化所有下拉刷新，上拉加载。
            $.each(document.querySelectorAll('.mui-slider-group .mui-scroll'), function(index, pullRefreshEl) {
                $(pullRefreshEl).pullToRefresh({
                    down: {//下拉
                        callback: function() {
                            var self = this;
                            setTimeout(function() {
                                var ul = self.element.querySelector('.mui-table-view');
                                while(ul.hasChildNodes()) {//当table下还存在子节点时 循环继续
                                    ul.removeChild(ul.firstChild);
                                }
                                var frg = createFragment('down', index+1);
                                if(frg){
                                    ul.insertBefore(frg, ul.firstChild);
                                }
                                self.endPullDownToRefresh();
                            }, 1000);
                        }
                    },
                    up: {
                        callback: function() {
                            var self = this;
                            setTimeout(function() {
                                var ul = self.element.querySelector('.mui-table-view');
                                var frg = createFragment('up', index+1);
                                if(frg){
                                    ul.appendChild(frg);
                                }
                                self.endPullUpToRefresh();
                            }, 1000);
                        }
                    }
                });
            });
            function createFragment(types, index) {
                var fragment = document.createDocumentFragment();
                var tempData;
                var type = 1;
                var url = "<%=contextPath%>/t9/mobile/workflow/act/T9PdaApplyAct/data.act";
                if(types == 'down'){//下拉
                    $.ajax({url, type:"GET", async:false, data:{spType:index, "sessionid":p},
                        success: function(data){
                            if(data && data != "NONEWDATA") {
                                tempData = data;
                            }else{
                                mui.toast(nonewdata);
                            }
                        }
                    });
                }else{//上拉
                    var lastedId =  jQuery("#item"+index).find("li:last").attr("q_id");
                    if(!lastedId) return null;
                    $.ajax({url,  type:"GET", async:false, data:{spType:index, "sessionid":p, "LASTEDID": lastedId},
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
                    //jQuery("#total_item"+index).html(json_temp[0].total_items);
                    var li;
                    var temp;
                    for (var i = 0; i < json.length; i++) {
                        temp = json[i];
                        li = document.createElement('li');
                        li.className = "mui-table-view-cell";
                        li.setAttribute("q_id", temp.SEQ_ID);
                        li.setAttribute("q_run_id", temp.RUN_ID);
                        li.setAttribute("q_flow_id", temp.FLOW_ID);
                        li.setAttribute("q_prcs_id", temp.PRCS_ID);
                        li.setAttribute("q_flow_prcs", temp.FLOW_PRCS);
                        li.setAttribute("q_op_flag", temp.OP_FLAG);
                        var str = "<span>"+temp.RUN_NAME+"</span>";
                        str += "<p>["+temp.FLOW_NAME+"]&nbsp;&nbsp;"+temp.PRCS_NAME +"<br/>";
                        if(index == 1){
                        	str += "[发起时间："+temp.BEGIN_TIME+"]<br/>";
                        	str += "[主办人："+temp.USER_NAME+"]";
                        }else{
                        	str += "[结束时间："+temp.END_TIME+"]<br/>";
                            str += "[主办人："+temp.USER_NAME+"]";
                        }
                        str += "</p>";
                        li.innerHTML = str;
                        fragment.appendChild(li);
                    }
                }
                return fragment;
            }
        });
    })(mui);
</script>