<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header.jsp" %>
<%@ page import="t9.core.data.T9RequestDbConn" %>
<%@ page import="t9.core.global.T9BeanKeys"%>
<%@ page import="t9.core.funcs.person.data.T9Person" %>
<%@ page import="t9.core.util.db.T9ORM" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="javax.servlet.http.Cookie" %>
<%
 
 /*  String Cookie_Str=request.getParameter("cs");
  Cookie cookie = new Cookie("userName","");
  response.addCookie(cookie);
 */
  String url=request.getParameter("url");
  String uid=request.getParameter("uid");
if(!"".equals(url)){
  T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
  if(person==null){
  person =new T9Person();
  if( null==uid || uid.length()<1) {
    person.setUserName("aa");
  }else{
    T9ORM orm = new T9ORM();
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
    Connection dbConn = requestDbConn.getSysDbConn();
    person=(T9Person)orm.loadObjSingle(dbConn, T9Person.class, Integer.parseInt(uid));
      
  }
  request.getSession().setAttribute(T9Const.LOGIN_USER,person);
 }
}

%>

<script type="text/javascript">
var url='<%=url%>';
location.href=url;
</script>
