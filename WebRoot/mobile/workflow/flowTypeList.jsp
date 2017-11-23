<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/mobile/workflow/header.jsp" %>
<%
    Map r = (Map)request.getAttribute("r");
    List<Map> list_sort = (List)r.get("list_sort");
    String sortNameTmp = (String)r.get("sortNameTmp");
    String sortId = (String)r.get("sortId");
    int parentId = (Integer)r.get("parentId");
%>
<script type="text/javascript">
mui.init();
var p = "<%=request.getSession().getId()%>";
(function($) {
	$(document).on("tap", "#flow1",function(){//我的申请
        detailWindow = layer.open({
            type: 2,
            title: false,
            shadeClose: false,
            shade: 0.8,
            closeBtn: false,
            area: ['100%', '100%'],
            content: contextPath + "/t9/mobile/workflow/act/T9PdaApplyAct/index.act?sessionid="+p
        });
    });
	
	$(document).on("tap", "#flow2",function(){//待办流程
		detailWindow = layer.open({
	        type: 2,
	        title: false,
	        shadeClose: false,
	        shade: 0.8,
	        closeBtn: false,
	        area: ['100%', '100%'],
	        content: contextPath + '/t9/mobile/workflow/act/T9PdaWorkflowIndexAct/index.act?sessionid='+p+'&source=2'
	    });
	});
	
	$(document).on("tap", "#flow3",function(){//已办流程
        detailWindow = layer.open({
            type: 2,
            title: false,
            shadeClose: false,
            shade: 0.8,
            closeBtn: false,
            area: ['100%', '100%'],
            content: contextPath + '/mobile/workflow/index_flow_finished.jsp?sessionid='+p
        });
    });
	
	$(document).on("tap", "#flow4",function(){//工作查询
        detailWindow = layer.open({
            type: 2,
            title: false,
            shadeClose: false,
            shade: 0.8,
            closeBtn: false,
            area: ['100%', '100%'],
            content: contextPath + '/mobile/workflow/searchflow.jsp?sessionid='+p
        });
    });
})(mui);
</script>
<body>
<div class="hk-workapproval">
    <!--页面头部-->
    <header id="header" class="mui-bar mui-bar-nav">
        <h1 class="mui-title">工作审批</h1>
    </header>
    <!--顶部MENU-->
    <div class="hk-menu">
        <div class="vux-flexbox">
            <div class="vux-flexbox-item">
                <div class="hk-menu-item" id="flow1">
                    <img src="<%=contextPath %>/mobile/workflow/images/comm/icon_work_top_01@2x.png" alt="">
                    <p>我的申请</p>
                </div>
            </div>
            <div class="vux-flexbox-item">
                <div class="hk-menu-item" id="flow2">
                    <img src="<%=contextPath %>/mobile/workflow/images/comm/icon_work_top_02@2x.png" alt="">
                    <p>待办流程</p>
                </div>
            </div>
            <div class="vux-flexbox-item">
                <div class="hk-menu-item" id="flow3">
                    <img src="<%=contextPath %>/mobile/workflow/images/comm/icon_work_top_03@2x.png" alt="">
                    <p>已办流程</p>
                </div>
            </div>
            <div class="vux-flexbox-item">
                <div class="hk-menu-item" id="flow4">
                    <img src="<%=contextPath %>/mobile/workflow/images/comm/icon_work_top_04@2x.png" alt="">
                    <p>工作查询</p>
                </div>
            </div>
        </div>
    </div>
    <!--流程-->
    <div class="mui-content" style="padding-top:1px;">
    <!--横线8px-->
    <div class="mui-card" style="margin:10px 0;font-size:15px;">
    <ul class="mui-table-view">
    <%
    int k = 0;
    for (Map m : list_sort) {
        String title = (String)m.get("title");
        String json =(String) m.get("json");
        Integer key = (Integer)m.get("key");
        boolean isFolder = (Boolean)m.get("isFolder");
        boolean isLazy = (Boolean)m.get("isLazy");
        List<Map> list_type = (List)m.get("list_type");
    %>
            <li class="mui-table-view-cell mui-collapse <%if(k==0){%>mui-active<% }%>" style="padding:10px 15px;">
                <a class="mui-navigate-right" href="#"><%=title%></a>
                <div class="mui-collapse-content" style="padding:0 0;">
                    <%
	                for(int i = 0; i < list_type.size(); i++){
	                    Map map_ = list_type.get(i);
	                    String title_ = (String)map_.get("title");
	                    Integer key_ = (Integer)map_.get("key");
	                    String attachmentId = (String)map_.get("attachmentId");
	                    String attachmentName = (String)map_.get("attachmentName");
	                    String imgPath = contextPath + "/mobile/workflow/images/comm/icon_work_xz_06@2x.png";
	                    if (attachmentId != null && !attachmentId.trim().equals("")) {
	                        String attachPath = T9SysProps.getString(T9SysPropKeys.ATTACH_FILES_PATH);
	                        if (attachPath == null) {
	                            attachPath = File.separator + "attach";
	                        }
	                        String[] str = attachmentId.replace(",", "").split("_");
	                        String path = str[0] + File.separator + str[1] + "_" + attachmentName.substring(0, attachmentName.length() - 1);
	                        imgPath = File.separator + attachPath + File.separator + "news" + File.separator + path;
	                    }
		            %>
		                <div class="mui-input-row">
	                        <div class="hk-process-item" q_id="<%=key_%>" onclick="getFlowNew('<%=key_%>')";>
	                            <img src="<%=imgPath%>" alt="" style="width:40px; height:40px;">
	                            <span><%=title_%></span>
	                        </div>
		                </div>
		            <%
		            }
		            %>
                </div>
            </li>
    <%
        k++;
    }
    %>
    </ul>
    </div>
    </div>
</div>
</body>
</html>
