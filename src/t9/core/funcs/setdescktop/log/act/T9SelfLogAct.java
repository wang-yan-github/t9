package t9.core.funcs.setdescktop.log.act;

import java.io.PrintWriter;
import java.sql.Connection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.util.form.T9FOM;
import t9.core.data.T9PageDataList;
import t9.core.data.T9PageQueryParam;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.setdescktop.fav.logic.T9FavLogic;
import t9.core.funcs.system.url.data.T9Url;
import t9.core.funcs.system.url.logic.T9UrlLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.global.T9SysProps;
import t9.core.load.T9PageLoader;
import t9.core.menu.data.T9SysMenu;

public class T9SelfLogAct {
  private T9FavLogic logic = new T9FavLogic();
  
  public String getPage(HttpServletRequest request, 
      HttpServletResponse response) throws Exception { 

    Connection dbConn = null; 
    try { 
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn(); 
      
      T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");//获得登陆用户
      
      String sql = "";
      String dbms = T9SysProps.getProp("db.jdbc.dbms");
      if (dbms.equals("sqlserver")) {
        sql = "select top 20 SEQ_ID" +
        ",(select USER_NAME from PERSON where SEQ_ID = S.USER_ID)" +
        ",TIME" +
        ",IP" +
        ",TYPE" +
        ",REMARK" +
        " from SYS_LOG S" +
        " where USER_ID =" + user.getSeqId() +
        " order by TIME desc";
      }
      else if (dbms.equals("mysql")){
        sql = "select SEQ_ID" +
        ",(select USER_NAME from PERSON where SEQ_ID = S.USER_ID)" +
        ",TIME" +
        ",IP" +
        ",TYPE" +
        ",REMARK" +
        " from SYS_LOG S" +
        " where USER_ID =" + user.getSeqId() +
        " order by TIME desc" +
        " limit 20";
      }else if (dbms.equals("oracle")){
        sql = "select SEQ_ID" +
        ",(select USER_NAME from PERSON where SEQ_ID = S.USER_ID)" +
        ",TIME" +
        ",IP" +
        ",TYPE" +
        ",REMARK" +
        " from SYS_LOG S" +
        " where USER_ID =" + user.getSeqId() +
        " order by TIME desc";
      }
      
      T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request.getParameterMap()); 
      T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn, 
      queryParam, 
      sql);
      
      PrintWriter pw = response.getWriter(); 
      pw.println(pageDataList.toJson()); 
      pw.flush(); 
  
      return null; 
    }catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR); 
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage()); 
      throw e; 
    } 
  }
}