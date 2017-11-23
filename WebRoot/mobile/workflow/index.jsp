<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/mobile/workflow/header.jsp" %>
<%
int total_flow = (Integer)request.getAttribute("total_flow");
%>
<script type="text/javascript">
    mui.init({keyEventBind: { backbutton: true }});
    mui.back = function(){
        closeWin();
    }
    var p = "<%=request.getSession().getId()%>";
    
    function closeWin(){
    	var index = parent.layer.getFrameIndex(window.name); //获取窗口索引
    	parent.layer.close(index);
    }
    
</script>
<style>
    body {
      margin: 0;
      padding: 0;
      background-color: #efeff4;
    }
    .mui-table-view-cell {
        padding:13px 13px;
    }
    
    a{
        color:#007aff;
    }
</style>
<body>
<header class="mui-bar mui-bar-nav">
    <h1 class="mui-title">待办中心</h1>
</header>
<div class="mui-content">
    <div class="mui-card">
        <ul class="mui-table-view">
            <li class="mui-table-view-cell" id="item1">
                <a class="mui-navigate-right">
                    <span class="mui-badge mui-badge-danger"><%=total_flow %></span>
                    <div style="line-height:30px;padding-left:35px;background: url(<%=contextPath %>/mobile/workflow/images/comm/icon_index_01@2x.png) no-repeat;background-size: 35px 35px;border-radius:10%;">流程待办
                    </div>
                </a>
            </li>
            <li class="mui-table-view-cell" id="item2">
                <a class="mui-navigate-right">
                    <span class="mui-badge mui-badge-purple">5</span>
                    <div style="line-height:30px;padding-left:35px;background: url(<%=contextPath %>/mobile/workflow/images/comm/icon_index_01@2x.png) no-repeat;background-size: 35px 35px;border-radius:10%;">新闻待看
                    </div>
                </a>
            </li>
            <li class="mui-table-view-cell" id="item3">
                <a class="mui-navigate-right">
                    <span class="mui-badge mui-badge-warning">5</span>
                    <div style="line-height:30px;padding-left:35px;background: url(<%=contextPath %>/mobile/workflow/images/comm/icon_index_01@2x.png) no-repeat;background-size: 35px 35px;border-radius:10%;">公告待看
                    </div>
                </a>
            </li>
            <li class="mui-table-view-cell" id="item4">
                <a class="mui-navigate-right">
                    <span class="mui-badge mui-badge-warning">5</span>
                    <div style="line-height:30px;padding-left:35px;background: url(<%=contextPath %>/mobile/workflow/images/comm/icon_index_01@2x.png) no-repeat;background-size: 35px 35px;border-radius:10%;">日程待看
                    </div>
                </a>
            </li>
        </ul>
    </div>
</div>
</body>
</html>
<script>
$(document).on("tap", ".mui-table-view-cell",function(){
    var cell_id = this.getAttribute("id");
    if(cell_id == 'item1'){
        openFlowDBList(p, 1);
    }
});
</script>