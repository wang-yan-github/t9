(function($) {
  var IGrid = {
    initialize: function() {
      this.el = $('<div></div>');
      this.el.attr('class', this.cls);
      
      var t = this;
      if (this.url) {
        $.post(this.url, this.param, function(data) {
          t.data = T9.parseJson(data);
          t.initRows();
        }, 'text');
      }
      else {
        this.initRows()
      }
      
      if (this.renderTo) {
        this.renderTo.append(el);
      }
    },
    initRows: function() {
      var ul = $('<ul></ul>');
      ul.appendTo(this.el);
      var render = this.rowRender;
      $.each(this.data, function(i, e) {
        var li = $('<li></li>');
        var liContent = render(i, e);
        li.html(liContent);
        li.appendTo(ul);
      });
    },
    render: function(el) {
      if (el) {
        this.el.appendTo(el);
      }
    },
    setStyle: function(css) {
      this.el.css(css);
    }
  };
  
  T9.Grid = function(cfg) {
    var opts = {
      'title': '',
      'data': '',
      'cls': '',
      'url': '',
      'param': {},
      'rowRender': $.noop,
      'renderTo': null
    };
    
    $.extend(true, this, opts, cfg);
    this.initialize();
  };
  $.extend(T9.Grid.prototype, IGrid);
  T9.register['grid'] = T9.Grid;
  
  var IImage = {
    initialize: function() {
      this.el = $('<div style="text-align:center;"></div>');
      this.el.attr('class', this.cls);
      var a = $('<a href="javascript:void(0)" style="display:block;"></a>');
      a.click(this.listeners.click);
      
      var img = $('<img></img>');
      if (this.path) {
        img.attr('src', this.path);
        a.append(img);
      }
      var describe = '';
      if (this.describe) {
        describe = this.describe;
      }
      
      this.el.append(a).append(describe);
      
      if (this.renderTo) {
        this.renderTo.append(el);
      }
    },
    render: function(el) {
      if (el) {
        this.el.appendTo(el);
      }
    }
  };
  
  T9.Image = function(cfg) {
    var opts = {
      'title': '',
      'path': '',
      'cls': '',
      'listeners': {
        'click': $.noop,
      },
      'describe': '',
      'listeners': {}
    };
    
    $.extend(true, this, opts, cfg);
    this.initialize();
  };
  $.extend(T9.Image.prototype, IImage);
  T9.register['image'] = T9.Image;
  
  var ICustom = {
    initialize: function() {
      this.el = $('<div></div>');
      this.el.attr('class', this.cls);
      
      var t = this;
      if (this.url) {
        $.post(this.url, this.param, function(data) {
          t.data = T9.parseJson(data);
          t.el.append(t.renderCmp(t.data));
          if (t.renderTo) {
            t.renderTo.append(el);
          }
        }, 'text');
      }
      
    },
    render: function(el) {
      if (el) {
        this.el.appendTo(el);
      }
    }
  };
  
  T9.Custom = function(cfg) {
    var opts = {
      title: '',
      url: '',
      param: {
      },
      data: null,
      renderCmp: $.noop
    };
    
    $.extend(true, this, opts, cfg);
    this.initialize();
  };
  $.extend(T9.Custom.prototype, ICustom);
  T9.register['custom'] = T9.Custom;
})(jQuery);