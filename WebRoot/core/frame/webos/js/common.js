/**
 * 初始化门户函数
 * @param windowCtn           门户容器
 * @param panelList           模块的数组
 * @param callback            初始化完毕的回调函数
 * @return
 */
function initPortal(ctnInfo, callback, data) {
  var id = ctnInfo.id;
  panelList = ctnInfo && ctnInfo.panels || [];
  ctnInfo.ctn = ctnInfo.ctn || null;
  var url = contextPath + "/t9/core/funcs/portal/act/T9PortalAct/listPorts.act";
  
  if (data) {
    success(data);
  }
  else {
    $.ajax({
      type: "POST",
      dataType: "text",
      url: url,
      data: {
        id: id
      },
      async: false,
      error: error,
      success: function(text) {
        var json = T9.parseJson(text);
        if (json.rtState == '0') {
          var data = json.rtData;
          success(data);
        }
        else {
          error();
        }
      }
    });
  }
  
  function error() {
    ctnInfo.ctn && ctnInfo.ctn.destroy();
    ctnInfo.ctn = new T9.Container({
      cls: 'work-space',
      renderTo: '#desktop',
      height: 'auto',
      location: true
    });
    callback && callback(ctnInfo.ctn);
  }
  
  function success(data) {
    if (!data || !data.layoutPath) {
      error();
      return;
    }
    $.ajax({
      type: "POST",
      async: false,
      dataType: "text",
      url: data.layoutPath,
      error: error,
      success: function(d) {
        var cfg = T9.parseJson(d);
        cfg.cls = 'work-space';
        cfg.style = {
          'overflow-y': 'auto',
          'overflow-x': 'hidden'
        };
        cfg.renderTo = '#desktop';
        ctnInfo.ctn && ctnInfo.ctn.destroy();
        cfg.listeners = {
          'initComponent': {
            'after': function(e, t) {
            }
          }
        };
        cfg.location = true;
        cfg.height = 'auto';
        cfg.width = '100%';
        ctnInfo.ctn = new T9.Container(cfg);
        
        if (!data.records || !data.records.length) {
          callback && callback(ctnInfo.ctn);
        }
        
        $.each(data.records, function(i, e) {
          var file = e.file;
          var id = e.id;

          //userPort.push(id);
          var title = e.file.replace(/.js/, "");
          var el = $("<div></div>");
          $.ajax({
            url: contextPath + "/t9/core/funcs/portal/act/T9PortalAct/viewPort.act",
            data: {"file":file},
            dataType: 'text',
            success: function(d) {
              var cfg = T9.parseJson(d);
              //cfg.tbar = tools;
              cfg.id = id;
              if (cfg.moreLink) {
                cfg.tbar.push({
                  id: 'more',
                  handler: function(e, target, panel) {
                    alert('查看更多链接' + cfg.moreLink);
                  }
                });
              }
              cfg.sort = e.sortNo;
              cfg.width = e.width || cfg.width;
              cfg.height = e.height || cfg.height;
              
              if (cfg.width && (!isNaN(cfg.width * 1))) {
                cfg.width += "px";
              }
              
              if (cfg.height && (!isNaN(cfg.height * 1))) {
                cfg.height += "px";
              }
              
              cfg.left = e.posX || 0;
              cfg.top = e.posY || 0;
              cfg.pid = e.parentCmp;
              
              if (T9.isCmp(T9.getCmp(e.parentCmp))) {
                var panel = new T9.Panel(cfg);
                panel.addClass("port");
                panelList.push(panel);
                T9.getCmp(e.parentCmp).addItems(panel);
              }
              if (i + 1 >= data.records.length) {
                callback && callback(ctnInfo.ctn);
              }
            },
            async: false
          });
        });
      }
    });
  }
}

var userinfo = {};

/**
 * 查询用户信息
 * @return
 */
function queryInfo(callback) {
  var url = contextPath + '/t9/core/funcs/setdescktop/userinfo/act/T9UserinfoAct/queryInfo.act';
  $.ajax({
    type: "GET",
    dataType: "text",
    url: url,
    success: function(text){
      var json = T9.parseJson(text);
      if (json.rtState == '0') {
        $.extend(userinfo, json.rtData);
        callback && callback();
      }
      else {
        
      }
    }
  });
}


/**
 * 更换桌面背景
 * @param index           数字:更换指定背景
 *                        reset:重置背景
 *                        save:保存当前背景
 * @param isSave          是否保存桌面背景
 * @return
 */
function changeBg(index, isSave) {
  if (!isNaN(index * 1)) {
    this.index = index;
    var url = "url(" + contextPath + "/core/frame/webos/styles/wallpapers/" + index + ".jpg)"
    $('body, .north').css({
      'background-image': url
    });
  }
  else if (index == 'reset') {
    if (creator) {
      if (this.index != creator.units.userInfo.data.desktopBg || 0) {
        changeBg(creator.units.userInfo.data.desktopBg || 0);
      }
    }
    else {
      if (this.index != userinfo.desktopBg || 0) {
        changeBg(userinfo.desktopBg || 0);
      }
    }
  }
  else if ((/\.(jpg|JPG|gif|GIF|png|PNG)$/).test(index)) {
    this.index = index;
    var url = "url(" + contextPath + "/core/frame/webos/styles/wallpapers/" + index + ")"
    $('body, .north').css({
      'background-image': url
    });
  }
  
  if (index == 'save' || isSave) {
    var url = contextPath + "/t9/core/funcs/setdescktop/userinfo/act/T9UserinfoAct/updateBackground.act";
    $.post(url, {
      background: this.index
    }, function(t) {
    });
  }
}


var creator = {
  initialize: function(units) {
    this.loader.load(units);
  },
  loader: {
    url: contextPath + '/t9/core/frame/act/T9WebosAct/queryInitInfo.act',
    param: {
    },
    load: function(units) {
      $.ajax({
        type: 'POST',
        url: this.url,
        data: this.param,
        success: function(data) {
          var json = T9.parseJson(data);
          if (json.rtState == '0') {
            var data = json.rtData;
            $.each(creator.units, function(k, v) {
              if (v && (!units || $.inArray(k, units) >= 0)) {
                v.data = data[k];
                v.handle && v.handle();
              }
            });
          }
          else {
            alert("WEBOS初始化失败!");
          }
        }
      });
    }
  },
  units: {
    browserTitle: {
      defaultTitle: productName,
      handle: function() {
        document.title = this.data || this.defaultTitle;
      }
    },
    userInfo: {
      handle: function() {
        if (this.data.fx == "0") {
          T9.fx(false);
        }
        initUserInfo(this.data);
      }
    },
    background: {
      handle: function() {
        if (!this.data || this.data == "null") {
          changeBg(0);
        }
        else {
          changeBg(this.data);
        }
      }
    },
    onlineAmount: {
      handle: function() {
        $('#onlineAmount button').html((this.data.amount || 0) + '人在线');
        var self = this;
        setInterval(self.reload, this.data.onlineRefStr * 1000 || 3600);
      },
      reload: function() {
        var url = contextPath + "/t9/core/funcs/setdescktop/syspara/act/T9SysparaAct/queryUserCount.act";
        $.ajax({
          type: "GET",
          dataType: "text",
          url: url,
          success: function(text){
            var json = T9.parseJson(text);
            if (json.rtState == '0') {
              $('#onlineAmount button').html(json.rtData + '人在线');
            }
          }
        });
      }
    },
    portal: {
      handle: function() {
        initPortal(ctnInfo, this.callback, this.data);
      },
      reload:function() {
        $(".work-space").fadeOut(function() {
          initPortal(ctnInfo);
        });
      }
    },
    smsPara: {
      smsAlertTimer: null,
      handle: function() {
        var self = this;
        setTimeout(function() {
          sms.initialize({
            type: "webos",
            msCallCount: self.data.smsCallCount,
            smsInterval: self.data.smsInterval,
            smsRef: self.data.smsRef,
            smsOn: creator.units.userInfo.data.smsOn,
            callSound: creator.units.userInfo.data.callSound,
            userId: creator.units.userInfo.data.seqId
          });
        }, 1000);
      }
    },
    otherPara: {
      handle: function() {
        
      }
    },
    logoutMsg: {
      handle: function() {
        window.alertMsrg = this.data;
      }
    },
    bannerInfo: {
      handle: function() {
        if (this.data.hideLogo) {
          if (this.data.hideLogo == "0") {
            var img = $('<img></img>');
            img.attr("src", contextPath + "/t9/core/frame/act/T9WebosAct/queryHeaderImg.act");
            img.show();
            $('.logo').append(img);
          }
          if (this.data.bannerText) {
            $('.logo').append(this.data.bannerText);
            $('.logo').attr("style", this.data.bannerFont);
          }
        }
        else {
          var img = $('<img></img>');
          img.attr("src", contextPath + "/t9/core/frame/act/T9WebosAct/queryHeaderImg.act");
          img.show();
          $('.logo').append(img);
        }
      }
    },
    regist: {
      handle: function() {
        if (this.data.hasRegisted == "0") {
        	var versionType = this.data.versionType;
        	var str = '<b>系统提示:&nbsp;</b><font color="red"> 本软件尚未注册, 免费试用剩余&nbsp;' + this.data.remainDays + '&nbsp;天</font>&nbsp;&nbsp;' +
            '<a class="reg-link" style="color: blue;" href="javascript:void(0)">注册</a>' +
            '&nbsp;&nbsp;'
        	if(versionType != null && versionType=='free'){
        		str = '<b>系统提示:&nbsp;</b><font color="red">免费版</font>&nbsp;&nbsp;<a class="reg-link" style="color: blue;" href="javascript:void(0)">注册</a>&nbsp;&nbsp;';
        	}
          var tips = new T9.Window({
            //title: "系统提示",
            height: 20,
            width: "500px",
            tbar: [],
            cmpCls: "jq-tip",
            draggable: false,
            layer: "lower",
            bottom: 35,
            left: "50%",
            style: {
              "margin-left": "-250px",
              position: "absolute"
            },
            containment: ".work-space",
            html: str
         
          });
          //tips.el.find('a.tria-link').attr("href", orgSecondSite + "?machineCode=" + this.data.machineCode);
          tips.el.find('a.reg-link').click(function() {
            openUrl({
              url: contextPath + "/core/funcs/system/info/index.jsp",
              text: '系统信息'
            });
          });
          tips.show();
        }
      }
    }
  }
};