{"id":"portal3","cls":"work-space","xtype":"container","layout":"columnlayout","layoutCfg":{"sortConnect":true,"cellpadding":3,"droppable":{"accept":".portlet-source","greedy":true,"dropStop":function(event, ui, cmp) {
        var opts = null;
        var el = ui.draggable;
        var id = el.attr('id').replace('btn', '');
        var sawdow = $('#sawdow' + id);
        sawdow.children('input').attr('checked', true);
        if (el.data('panel')) {
          addPanel(el.data('panel'), id, cmp, opts);
          el.draggable('disable');
          el.parent().addClass('port-sawdow-selected');
        }
        else if (el.data('layout')) {
          addLayout(el.data('layout'), id, cmp);
        }
      },"drop":null}},"draggable":undefined,"width":"100%","height":"auto","items":[{"id":"portal4","xtype":"container","layout":"autolayout","layoutCfg":{},"draggable":undefined,"width":"100%","height":"100%","columnWidth":0.37,"items":[]},{"id":"portal5","xtype":"container","layout":"autolayout","layoutCfg":{},"draggable":undefined,"width":"100%","height":"100%","columnWidth":0.31,"items":[]},{"id":"portal6","xtype":"container","layout":"autolayout","layoutCfg":{},"draggable":undefined,"width":"100%","height":"100%","columnWidth":0.28,"items":[]}]}