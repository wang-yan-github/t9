<?php
include_once("../header.php");
include_once("inc/utility_all.php");

ob_clean();
?>
<div class="container">
   <div class="tform tformshow">
   		<form action="new_submit.php" method="post" name="form1">
   			<div class="read_detail read_detail_header"><?=_("填写该工作的名称或文号")?></div>
   			<div class="read_detail">
					<input type="text" name="SEARCH_NAME" style="width:80%">
   			</div>
   		</form>
   	</div>
</div>