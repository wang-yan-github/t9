<form action="submit.php"  method="post" name="form1" onsubmit="return false;">
	<div class="read_detail write_mail">
		收件人：                     
		<span id="contactlist_1_result" class="contactlist_result">
			</span>
		<input type="text" class="noborderInput searchInit" id="TO_NAME1" name="TO_NAME1" value=""/>
		<!--<a href="javascript:void(0)" class="add_btn write_mail_add_btn"></a>-->
	</div>
	<div id="contactlist_1" class="contactlist">
		<div id="wrapper_plist" class="wrapper wrapper_contact hasshadow" style="display:none;"><div id="scroller_plist1" class="scroller"><ul id="contactlist_1_result_ul" class="comm-list sideBarSubList contact-list"></ul></div></div>	
	</div>
	
	<div class="read_detail write_mail">
		抄送：                     
		<span id="contactlist_3_result" class="contactlist_result">
		</span>
		<input type="text" class="noborderInput searchInit" id="TO_NAME3" name="TO_NAME3" value=""/>
	</div>
	<div id="contactlist_3" class="contactlist">
		<div id="wrapper_plist" class="wrapper wrapper_contact hasshadow" style="display:none;"><div id="scroller_plist3" class="scroller"><ul id="contactlist_3_result_ul" class="comm-list sideBarSubList contact-list"></ul></div></div>
	</div>
	
	<div class="read_detail">
		<table class="tfromTable">
			<tr>
				<td style="width:100px;">外部邮箱地址：</td>
				<td><input type="email" class="noborderInput w100" id="TO_NAME2" name="TO_NAME2" value="" placeholder="多个邮箱请使用逗号分隔,如:  abc@abc.com,abcd@abcd.com"/></td>
			</tr>
		</table>
	</div>
	
	<div class="read_detail">
		<table class="tfromTable">
			<tr>
				<td style="width:50px;">主题：</td>
				<td><input type="text" class="noborderInput w100" id="SUBJECT" name="SUBJECT" value="" /></td>
			</tr>
		</table>
	</div>
	<textarea id="CONTENT" class="noborderTextarea" name="CONTENT" rows="8" cols="10" wrap="on">


发自Pad HD版.
	</textarea>
</form>