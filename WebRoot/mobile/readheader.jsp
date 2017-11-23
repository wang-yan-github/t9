<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <style>
/* mobile 阅读单页 */
body{
    background: #ffffff;
}
body .container{
    margin: 10px;
    padding: 0px;
}
.read_title strong{
    color: #888;
    font-weight: bold;
}
</style>
<script>
$(function(){
    $('body').delegate('a.pda_attach', 'click', function(e){
        var target = this;   
        if( P_VER == '6' && target.getAttribute('is_image') == 1){
            e.preventDefault();
            window.Android.ShowPic(target.href, target.childNodes[0].childNodes[0]);
        }else if(P_VER == '5'){
			e.preventDefault();
			document.location = "message:" +  target.getAttribute('is_image') + ":" + target.getAttribute('_href');
		}
    });
});
</script>
    