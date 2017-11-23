<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header.jsp" %>
<%
String sessionToken = (String)session.getAttribute("sessionToken");
%>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="renderer" content="webkit">
    <title>OA智能平台</title>
    <link href="plug-in-ui/hplus/css/bootstrap.min.css?v=3.3.6" rel="stylesheet">
    <link href="plug-in-ui/hplus/css/font-awesome.min.css?v=4.4.0" rel="stylesheet">
    <link rel="stylesheet" href="plug-in/ace/assets/css/font-awesome.min.css" />
    <!--[if IE 7]>
    <link rel="stylesheet" href="plug-in/ace/assets/css/font-awesome-ie7.min.css" />
    <![endif]-->
    
    <!-- 全局js -->
	<script src="plug-in-ui/hplus/js/jquery.min.js?v=2.1.4"></script>
	
	<script src="plug-in-ui/hplus/js/bootstrap.min.js?v=3.3.6"></script>
    <script src="plug-in-ui/hplus/js/plugins/metisMenu/jquery.metisMenu.js"></script>
    <script src="plug-in-ui/hplus/js/plugins/slimscroll/jquery.slimscroll.min.js"></script>
    <script src="plug-in-ui/hplus/js/plugins/layer/layer.min.js"></script>
    
    <!-- 自定义js -->
    <script src="plug-in-ui/hplus/js/hplus.js?v=4.1.0"></script>
    <!--右键菜单-->
    <script type="text/javascript" src="plug-in/hplus/jquery-smartMenu.js"></script>
    <script type="text/javascript" src="plug-in/hplus/contabs.js"></script>
    <script type="text/javascript" src="plug-in/mutiLang/zh-cn.js"></script>
    <link rel="stylesheet" href="plug-in/tools/css/metrole/common.css" type="text/css"></link>
    <link rel="stylesheet" href="plug-in/ace/css/font-awesome.css" type="text/css"></link>
    <script type="text/javascript" src="plug-in/lhgDialog/lhgdialog.min.js?skin=metrole"></script>
    <script type="text/javascript" src="plug-in/ace/js/bootstrap-tab.js"></script>
    <script type="text/javascript" src="plug-in/layer/layer.js"></script>
    <script type="text/javascript" src="plug-in/tools/curdtools_zh-cn.js"></script>
    <script type="text/javascript" src="plug-in/tools/easyuiextend.js"></script>
    <script type="text/javascript" src="plug-in/jquery-plugs/hftable/jquery-hftable.js"></script>
    <script type="text/javascript" src="plug-in/tools/json2.js" ></script>
    <!-- 第三方插件 -->
    <script src="plug-in-ui/hplus/js/plugins/pace/pace.min.js"></script>
    <!-- Sweet alert -->
    <script src="plug-in-ui/hplus/js/plugins/sweetalert/sweetalert.min.js"></script>
    <script src="plug-in/jquery-plugs/storage/jquery.storageapi.min.js"></script>
    <!-- 弹出TAB -->
    <script type="text/javascript" src="plug-in/hplus/hplus-tab.js"></script>
    
    <script type="text/javascript" src="js/jquery-migrate-1.2.1.js"></script>
    <!-- Sweet Alert -->
    <link href="plug-in-ui/hplus/css/plugins/sweetalert/sweetalert.css" rel="stylesheet">
    <link href="plug-in-ui/hplus/css/animate.css" rel="stylesheet">
    <link href="plug-in-ui/hplus/css/style.css?v=4.1.0" rel="stylesheet">
    <!--右键菜单-->
    <link href="plug-in/hplus/smartMenu.css" rel="stylesheet">
    
    <script type="text/javascript" src="js/calendar.js"></script>
    <script type="text/javascript" src="js/index.js"></script>
	<style>
	    .tt{
            width: 20px;
            height: 15px;
            background-repeat: no-repeat; //属性设置是否及如何重复背景图像
            background-position: bottom;   //背景位置居中
            background: cover;     /*background-size的cover特定值会保持图像本身的宽高比例，将图片缩放到正好完全覆盖定义背景的区域。*/
            display: inline-block;  //变成块状，这个属性一定要加，要不然不显示。因为<i>标签不是块级元素
        }
	    .mytable{background-image:url(<%=contextPath%>/core/styles/imgs/menuIcon/mytable.gif);}
	    .workflow{background-image:url(<%=contextPath%>/core/styles/imgs/menuIcon/workflow.gif);}
	    .erp{background-image:url(<%=contextPath%>/core/styles/imgs/menuIcon/erp.gif);}
	    .comm{background-image:url(<%=contextPath%>/core/styles/imgs/menuIcon/comm.gif);}
	    .hrms{background-image:url(<%=contextPath%>/core/styles/imgs/menuIcon/hrms.gif);}
	    .edit{background-image:url(<%=contextPath%>/core/styles/imgs/menuIcon/edit.gif);}
	    .info_query{background-image:url(<%=contextPath%>/core/styles/imgs/menuIcon/info_query.gif);}
	    .sys{background-image:url(<%=contextPath%>/core/styles/imgs/menuIcon/sys.gif);}
	    .second{background-image:url(<%=contextPath%>/core/funcs/display/img/org.gif);}
	</style>
	<script type="text/javascript">
    function initMenu() {
      var url = contextPath + '/t9/core/funcs/system/act/T9SystemAct/listMenu.act';
      $.ajax({
        type: "GET",
        async: false,
        dataType: "text",
        url: url,
        success: function(text){
          var json = eval("("+text+")");
          if (json.rtState == '0') {
              var menus = json.rtData.menu;
              var obj = $("#side-menu");
              for(var i = 0; i < menus.length; i++){
                  var childs = menus[i].childes;
                  var str = "";
                  str += '<li>';
                  str += '<a href="#" class="">';
                  var iconCl = menus[i].icon.split("\.")[0];
                  str += '    <i class="tt '+iconCl+'"></i>';
                  str += '    <span class="menu-text">'+menus[i].text+'</span>';
                  str += '    <span class="fa arrow"></span>';
                  str += '</a>';
                  if(childs){
                      str += '<ul  class="nav nav-second-level collapse" aria-expanded="false">';
                      var childJson = eval("("+childs+")");
                      for(var j = 0; j < childJson.length; j++){
                          var leaf = childJson[j].leaf;
                          var childJson_ = childJson[j].children;
                          str += '    <li> ';
                          if(leaf == 0 && childJson_){
                              str += '<a href="#" class="" >';
                              var iconClc = childJson[j].icon.split("\.")[0];
                              str += '    <i class="tt second"></i>';
                              str += '    <span class="menu-text">'+childJson[j].text+'</span>';
                              str += '    <span class="fa arrow"></span>';
                              str += '</a>';
                              str += '<ul  class="nav nav-third-level collapse" aria-expanded="false">';
                              for(var k = 0; k < childJson_.length; k++){
                                  str += '    <li> ';
                                  str += '<a class="J_menuItem" href="'+childJson_[k].url+'"><span class="menu-text" ">'+childJson_[k].text+'</span></a>';
                                  str += '   </li>';
                              }
                              str += '</ul>';
                          }else{
                              str += '<a class="J_menuItem" href="'+childJson[j].url+'"><span class="menu-text" ">'+childJson[j].text+'</span></a>';
                          }
                          str += '   </li>';
                      }
                      str += '</ul>';
                  }
                  str += '</li>';
                  obj.append(str);
              }
          }
        }
      });
    }
    
    $(document).ready(function() {
       // window['main-body-desktop'].location.href = "";
    });
</script> 
</head>
<body class="fixed-sidebar full-height-layout gray-bg skin-1" style="overflow:hidden">
<div id="wrapper">
    <!--左侧导航开始-->
    <nav class="navbar-default navbar-static-side" role="navigation" style="z-index: 1991;">
        <div class="nav-close"><i class="fa fa-times-circle"></i></div>
        <div class="sidebar-collapse">
            <ul class="nav" id="side-menu">
                <li class="nav-header" style="width: 50%!important;">
                    <div class="dropdown profile-element">
                        <span><img alt="image" class="img-circle" src="plug-in/login/images/jeecg-aceplus.png" width="100%"/></span>
                    </div>
                    <div class="logo-element">OA</div>
                </li>   
                <script type="text/javascript">
                    initMenu();
                </script>
            </ul>
        </div>
    </nav>
    <!--左侧导航结束-->
    <!--右侧部分开始-->
    <div id="page-wrapper" class="gray-bg dashbard-1" style="overflow: hidden; ">
        <div class="row border-bottom">
            <nav class="navbar navbar-static-top" role="navigation" style="margin-bottom: 0">
                <div class="navbar-header" style="height: 60px;">
                    <!--
                    <a class="navbar-minimalize minimalize-styl-2 btn btn-primary" href="#"><i class="fa fa-bars"></i> </a>
                    -->
                </div>
                <ul class="nav navbar-top-links navbar-right">
                    <li class="dropdown hidden-xs">
                        <a href="javascript:addOneTab('我的任务', 'taskController.do?goTaskListTab', 'default');">
                            <i class="fa fa-bell" style="color:#EE6B6B"></i> 待办(<span id="taskCount">0</span>)
                        </a>
                    </li>
                    <li class="dropdown">
                        <a href="javascript:logout()" class="roll-nav roll-right J_tabExit"><i class="fa fa fa-sign-out"></i> 退出</a>
                    </li>
                </ul>
            </nav>
        </div>
        <div class="row content-tabs">
            <button class="roll-nav roll-left J_tabLeft"><i class="fa fa-backward"></i>
            </button>
            <nav class="page-tabs J_menuTabs">
                <div class="page-tabs-content">
                    <a href="javascript:;" class="active J_menuTab" data-id="mainPage">首页</a>
                </div>
            <button class="roll-nav roll-right J_tabRight"><i class="fa fa-forward"></i>
            </button>
            <div class="btn-group roll-nav roll-right">
                <button class="dropdown J_tabClose" data-toggle="dropdown">关闭操作<span class="caret"></span></button>
                <ul role="menu" class="dropdown-menu dropdown-menu-right">
                    <li class="J_tabShowActive"><a>定位当前选项卡</a></li>
                    <li class="divider"></li>
                    <li class="J_tabCloseAll"><a>关闭全部选项卡</a></li>
                    <li class="J_tabCloseOther"><a>关闭其他选项卡</a></li>
                </ul>
            </div>
        </div>
        <div class="row J_mainContent" id="content-main" style="margin-left:-13px;">
            <iframe class="J_iframe" id="main-body-desktop" name="main-body-desktop" data-id="mainPage" src="<%=contextPath %>/core/frame/2/desktop.jsp" frameborder="0" style="width:100%;height:100%;left:10%;top: 100px;"></iframe>
        </div>
        <!--
        <div class="footer">
            <div class="pull-right"></div>
        </div>
        -->
    </div>
</div>
<!--右侧部分结束-->
    
</body>
</html>
