<?
	require_once 'inc/auth.php';
	ob_clean();
	require_once 'header.php';
	require_once 'func.php';
?>
<div data-role="page" data-theme="b" id="select-user">
	<div data-role="header" class="ui-btn-up-b" data-theme="b">
		<h1><?=_("添加收信人")?></h1>
		<a data-icon="right" href="muti_send.php?#write-sms-page" data-theme="b" data-ajax="true" data-rel="back" data-transition="<?=$deffect[DeviceAgent()]['flip']?>"><?=_("确定")?></a>
	</div><!-- /header -->
	<div data-role="content" id="contact-list-content">
			<div data-role="fieldcontain" class="mycust-contactsearch">
				<span class="mycust-contactsearch-block clear">
					<input type="search" name="password" id="search" value="<?=_("点击搜索")?>" data-theme="c"/>
				</span>
			</div>		
			<div data-role="fieldcontain" id="contacts-list">
			 	<fieldset data-role="controlgroup" id="contacts-list-fieldset"></fieldset>
			</div>
			<div class="mycust-loading" style="display:none;"></div>
			<div style="text-align:center;">
				<a href="muti_send.php?#write-sms-page" data-inline="true" data-role="button" data-rel="back" data-ajax="true" data-theme="b"  data-icon="check"><?=_("确定")?></a>       
				<a href="muti_send.php?#write-sms-page" data-inline="true" data-role="button" data-rel="back" data-ajax="true" data-theme="c" data-icon="back"><?=_("返回")?></a>  	
			</div>
	</div>
</div>