<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="t9.core.funcs.person.data.T9Person" %>
<%@ page import="t9.core.global.T9SysProps" %>
<%@ include file="/core/inc/header.jsp" %>
<%
  String userIdStr = request.getParameter("userIdStr");
  if (userIdStr == null) {
    userIdStr = "";
  }
  //System.out.println(userIdStr);
  T9Person loginUser = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
  boolean isAdmin = loginUser.isAdminRole();
%>
<table id="privInfo" class="TableBlock" width="95%" align="center">
  <tr>
    <td nowrap class="TableData">用户排序号：</td>
    <td nowrap class="TableData">
      <input type="text" id="userNo" name="userNo" class="BigInput" size="10" value="10" onkeyup="value=value.replace(/[^\d]/g,'')" onbeforepaste="clipboardData.setData('text',clipboardData.getData('text').replace(/[^\d]/g,''))">&nbsp;
                用于同角色用户的排序
    </td>
  </tr> 
  
  <tr>
    <td nowrap class="TableData">访问控制：</td>
    <td nowrap class="TableData">
    <% if (!"admin".equals(userIdStr)) { %>
      <input type="checkbox" name="notLogin" id="notLogin" ><label for="notLogin">禁止登录OA系统</label>&nbsp;
      <input type="checkbox" name="notViewUser" id="notViewUser" ><label for="notViewUser">禁止查看用户列表</label>&nbsp;
      <input type="checkbox" name="notViewTable" id="notViewTable" ><label for="notViewTable">禁止显示桌面</label>
    <% } %>
      <input type="checkbox" name="useingKey" id="useingKey" ><label for="useingKey">使用USB KEY登录</label>
    </td>
  </tr>
  <tr>
   <td nowrap class="TableData" width="120">即时通讯使用范围：</td>
    <td nowrap class="TableData">
        <select name="imRange" id="imRange" class="BigSelect">
          <option value="0" >本机构</option>
          <option value="1" selected>不限制</option>
          <option value="2" >禁止使用</option>
        </select>
    </td>
   </tr>
   <tr>
    <td nowrap class="TableData" width="120">即时通讯群发权限：</td>
    <td nowrap class="TableData">
      <input type="text" onkeyup="value=value.replace(/[^\d-]/g,'')" onbeforepaste="clipboardData.setData('text',clipboardData.getData('text').replace(/[^\d-]/g,''))" name="canbroadcast" id="canbroadcast"  size="10" value=50>人&nbsp;
        请输入数字，-1为不限制     </td>
   </tr>
</table>

