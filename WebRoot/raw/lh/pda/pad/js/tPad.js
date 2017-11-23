;(function(win, jQuery){

	win.tPad = win.tPad || {};
	
	$.extend(win.tPad, {
	
		SideSelector: '#sideContentArea',
		MainSelector: '#mainContentArea',
		gesturestart: function(e){
			win.tPad.gesture = {
				pageX: e.pageX,
				pageY: e.pageY,
				layerX: e.layerX,
				layerY: e.layerY
			};
			if(e.target.nodeType==1){
				return false;
			}
			
			// console.log(e);
			// window.e = e;
			// for(var i in e){
				// $('body').prepend('<p>'+i+':'+e[i]+'</p>');
			// }
		},
		gesturechange: function(e){
			if( !'gesture' in win.tPad ){
				return;
			}
			
			var gesture = win.tPad.gesture,
			d = e.pageX - gesture.pageX;
			
			if(Math.abs(d) < 100){
				return;
			}
			if(d>0){
				win.tPad.changeLayout('side');
				//$('body').prepend('<p>pageX:'+e['pageX']+'</p>');
				return false;
			}else{
				win.tPad.changeLayout('');
				return false;
			}			
		},
		gestureend: function(e){
			delete win.tPad.gesture;
		},

		changeLayout: function(type){
			var l = this.layout;
			switch (type){
				case 'none':
				
				break;
				case 'side':
				
					$(this.SideSelector).attr('class','layout-side');
						
					$(this.MainSelector).attr('class', ( !l || l=='side' ) ? 'layout-side' : 'slide-out-r');
					
				break;
				
				case 'main':
				
					$(this.SideSelector).attr('class', ( !l || l=='main' ) ? '': 'slide-out-l');
					
					$(this.MainSelector).attr('class', ( !l || l=='side' ) ? '': 'layout-main');
					
				break;
				case 'both':
				default:
				
					$(this.SideSelector).attr('class', ( !l || l=='main' ) ? 'slide-in-r': 'layout-both');
					
					$(this.MainSelector).attr('class', ( !l || l=='side' ) ? 'layout-both': 'layout-both');
					
				break;
			}
		
			this.layout = type;
		},
		
		multi: {
			init: function(id){
				id = id || 'multi';
				this.lastActiveId = $('#mainheader').find('[id^="mainheader_"]:visible').attr('id').substr(11);
				$('#mainheader_' + this.lastActiveId).hide();
				//$('#mainContentPage_'+this.lastActiveId).hide();
				this.header = $('#mainheader_' + id).show();
				this.content = $('#mainContentPage_' + id).show();
				this.getOverlay().show();
			},
			getOverlay: function(){
				var $overlay = $('.overlay',this.content);
				$overlay = $overlay.size() ? $overlay : $('<div class="overlay"></div>').appendTo(this.content);
				$overlay.css('background','rgba(0,0,0,0.5)');
				return $overlay;
			},
			destory: function(){
				this.removeAll();
				this.close();
				$('#mainheader_' + this.lastActiveId).show();
				$('#mainContentPage_' + this.lastActiveId).show();
				$('.preview-box-wrapper', this.content).empty();
				delete this.header;
				delete this.content;
				delete this.lastActiveId;
			},
			open: function(){
				this.header && this.header.show();
				this.content && this.content.show();
			},
			close: function(){
				this.getOverlay().hide();
				this.header && this.header.hide();
				this.content && this.content.hide();
			},
			serializeId: function(){
				
				$('.slideout',this.content).remove();
				var result = [];
				$('.preview-box',this.content).each(function(){
					var id = this.id && this.id.toString();
					id && result.push( id.substr(17) );
				});
				return result;
			},
			add: function(id){
				var wrapper = $('.preview-box-wrapper', this.content);
				id = id || 'auto' + Math.floor( Math.random() * 1000 );
				var d = $('<div><div class="preview-box-mask"></div><div class="preview-box-content"></div></div>')
					.attr({ 'class': 'preview-box', 'id': 'preview-box-item-' + id })
					.css('-webkit-transform','rotate(' + ['+','-'][Math.round(Math.random())]	+ Math.round(Math.random()*5) +'deg)')
					.appendTo(wrapper);
			
				this.set(d,id);	
					
				$('.slideout',wrapper).remove();
			},
			remove: function(id){
				var wrapper = $('.preview-box-wrapper', this.content);
				$('#preview-box-item-' + id).addClass('slideout');
				
			},
			removeAll: function(){
				$('.preview-box',this.content).addClass('slideout');
			},
			set: function(d,email_id){
				$.get('email/read.php',{'EMAIL_ID': email_id},function(msg){
					$('.preview-box-content',d).html(msg);
				});
			}
		},
		overlay: {				//全局遮罩
			show: function(){
				var overlay = $('#g-overlay');
				overlay = overlay.size() ? overlay : $('<div id="g-overlay" class="overlay"></div>').appendTo('body');
				overlay.css('background','rgba(0,0,0,0.5)').fadeIn(500);
			},
			hide: function(){
				$('#g-overlay').fadeOut(500);
			}
		},
		PopPanel: (function(exports, $, undefined){
			var PopPanel = function(el){
				
				this.el = el instanceof $ ? el : $(el);
				
				this.init();
			};
			PopPanel.prototype = {
				constructor: PopPanel,
				init: function(){
				
				
					return this;
				},
				destory: function(){
					return this;
				},
				open: function(){
					this.closeMon && clearTimeout(this.closeMon);
					tPad.overlay.show();
					this.el.show().removeClass('slide-out-b').addClass('slide-in-b');
					return this;
				},
				close: function(){
					var el = this.el;
					el.removeClass('slide-in-b').addClass('slide-out-b');
					tPad.overlay.hide();
					this.closeMon = setTimeout(function(){el.hide();},1000);
					return this;
				},
				setHeader: function(s){
					this.getHeader().html(s || '');
					return this;
				},
				getHeader: function(){
					return $('.header', this.el);
				},
				setTitle: function(t){
					this.getTitle().html(t || '');
					return this;
				},
				getTitle: function(){
					return $('.header .t', this.el);
				},
				setScroller: function(s){
					this.getScroller().html(s || '');
					return this;
				},
				getScroller: function(){
					return $('.scroller', this.el);
				},
				showLoading: function(s){
					var $loading = $('.ui-loader', this.el);
					if($loading.size() == 0){
						$loading = $('<div class="ui-loader loading" ><span class="ui-icon ui-icon-loading"></span><h1>加载中...</h1></div>');
						$('.wrapper', this.el).append($loading);
					}
					if(typeof s !== 'undefined'){
						$loading.find('h1').text(s);
					}
					$loading.fadeIn();
				},
				hideLoading: function(){
					$('.ui-loader', this.el).fadeOut();
				},
				ajax: function(url, data, onSuccess, config){
					var me = this;

					config = $.extend({
						type: 'get',
						beforeSend : $.noop,
						error: $.noop,
						complete: $.noop								
					},config || {});
					
					$.ajax({
						url: url, 
						data: data,
						type: config.type,
						beforeSend: function(){
						
							var func = config.beforeSend;
							
							me.showLoading();
							
							if(func() === false){
								return false;
							}
						},
						error: function(){
						
							me.setScroller('<p class="no_msg">出错了。</p>')
						},
						success: function(msg){
							var func = typeof onSuccess === 'function' ? onSuccess : $.noop;
							
							if(func() === false){
								return false;
							}
							me.setScroller(msg);
						},
						complete: function(){
							var func = config.complete;
							me.hideLoading();
							if(func() === false){
								return false;
							}
						}
					});
				}

			};

			return PopPanel;
			
		})(this, jQuery)
	
	});
	
	
	

})(window, jQuery);