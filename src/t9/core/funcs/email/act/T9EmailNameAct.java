package t9.core.funcs.email.act;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.dept.logic.T9DeptLogic;
import t9.core.funcs.email.data.T9EmailBox;
import t9.core.funcs.email.logic.T9EmailBoxLogic;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.person.data.T9UserPriv;
import t9.core.funcs.person.logic.T9UserPrivLogic;
import t9.core.funcs.workflow.util.T9WorkFlowUtility;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;

public class T9EmailNameAct{
  public String getName(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
    int userId = person.getSeqId();
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      Connection dbConn = requestDbConn.getSysDbConn();
      PreparedStatement ps = null;
      ResultSet rs = null;
      String  isUse = "1";
      String name = "";
      int seqId = 0;
      try {
        ps = dbConn.prepareStatement("select * from email_name where USER_ID = '" + userId + "'");
        rs = ps.executeQuery();
        if(rs.next()){
           name =  T9Utility.encodeSpecial(T9Utility.null2Empty(rs.getString("NAME")));
           isUse = rs.getString( "IS_USE");
          if (isUse ==null) {
            isUse = "1";
          }
          seqId = rs.getInt("SEQ_ID");
        }
      } catch (Exception e) {
        throw e;
      } finally{
        T9DBUtility.close(ps, rs, null);
      }
      String str =  "{name:\""+ name +"\" , isUse:\""+ isUse +"\" , nameId :\""+ seqId +"\"}";
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, str);
    } catch(Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "失败: " + e.getMessage());
    }
    return "/core/inc/rtjson.jsp";
  }
  public String getName2(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
    int userId = person.getSeqId();
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      Connection dbConn = requestDbConn.getSysDbConn();
      PreparedStatement ps = null;
      ResultSet rs = null;
      String  isUse = "1";
      String name = "";
      int seqId = 0;
      try {
        ps = dbConn.prepareStatement("select * from email_name where USER_ID = '" + userId + "'");
        rs = ps.executeQuery();
        if(rs.next()){
           name =  T9Utility.encodeSpecial(T9Utility.null2Empty(rs.getString("NAME")));
           isUse = rs.getString( "IS_USE");
          if (isUse ==null) {
            isUse = "1";
          }
          seqId = rs.getInt("SEQ_ID");
        }
      } catch (Exception e) {
        throw e;
      } finally{
        T9DBUtility.close(ps, rs, null);
      }
      name = this.setName(dbConn, name, person);
      String str =  "{name:\""+ name +"\" , isUse:\""+ isUse +"\" , nameId :\""+ seqId +"\"}";
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, str);
    } catch(Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "失败: " + e.getMessage());
    }
    return "/core/inc/rtjson.jsp";
  }
  public String saveName(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    try{
      T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      int userId = person.getSeqId();
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      Connection dbConn = requestDbConn.getSysDbConn();
      PreparedStatement ps = null;
      ResultSet rs = null;
      String  isUse = T9Utility.null2Empty(request.getParameter("IS_USE"));
      if ("".equals(isUse)) {
        isUse = "0";
      }
      String name =T9Utility.null2Empty(request.getParameter("NAME")) ;
      String nameId = request.getParameter("NAME_ID");
      if (!"0".equals(nameId) 
          && !"".equals(nameId)) {
        try {
          ps = dbConn.prepareStatement("update email_name set NAME = ? , IS_USE = ?  where SEQ_ID = '" + nameId + "'");
          ps.setString(1, name);
          ps.setString(2, isUse);
          ps.executeUpdate();
        } catch (Exception e) {
          throw e;
        } finally{
          T9DBUtility.close(ps, rs, null);
        }
      } else {
        try {
          ps = dbConn.prepareStatement("insert into email_name (USER_ID ,NAME , IS_USE) values (? , ? , ?)");
          ps.setInt(1, userId);
          ps.setString(2, name);
          ps.setString(3, isUse);
          ps.executeUpdate();
        } catch (Exception e) {
          throw e;
        } finally{
          T9DBUtility.close(ps, rs, null);
        }
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "保存成功！");
    } catch(Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "保存失败: " + e.getMessage());
    }
    return "/core/inc/rtjson.jsp";
  }
  public String setName(Connection conn , String str ,T9Person loginUser ) throws Exception {
    T9UserPrivLogic userPrivLogic = new T9UserPrivLogic();
    T9DeptLogic dept = new T9DeptLogic();
    int deptId = loginUser.getDeptId();
    String deptName = dept.getNameById(deptId, conn);
    StringBuffer sb = new StringBuffer();
    dept.getDeptNameLong(conn, deptId, sb);
    String longName = sb.toString();
    if (longName.endsWith("/")) {
      longName = longName.substring(0, longName.length() - 1);
    }
    T9UserPriv role = userPrivLogic.getRoleById(Integer.parseInt(loginUser.getUserPriv()) , conn);
    SimpleDateFormat df = new  SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Date date = new Date();
    String dateStr = df.format(date);
    String[] aDate = dateStr.split(" ");

    String curYear = aDate[0].split("-")[0];
    String curMon =  aDate[0].split("-")[1];
    String curDay =  aDate[0].split("-")[2];
    String curHour = aDate[1].split(":")[0];
    String curMinite = aDate[1].split(":")[1];
    String curSecond = aDate[1].split(":")[2];
    
    str = str.replaceAll("\\{Y\\}", curYear);
    str = str.replaceAll("\\{M\\}", curMon);
    str = str.replaceAll("\\{D\\}", curDay);
    str = str.replaceAll("\\{H\\}", curHour);
    str = str.replaceAll("\\{I\\}", curMinite);
    str = str.replaceAll("\\{S\\}", curSecond);
    str = str.replaceAll("\\{U\\}", loginUser.getUserName());
    str = str.replaceAll("\\{SD\\}", deptName);
    str = str.replaceAll("\\{R\\}", role.getPrivName());
    str = str.replaceAll("\\{LD\\}", longName);
    return str;
  }
}
