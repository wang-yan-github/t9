package test.core.act;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9DsTable;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.system.data.T9SysFunction;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import test.core.util.db.TestDbUtil;

public class T9ORMTestAct{
  
  /**
   * 构造单个对象
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String testError(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    int i = 0;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
      System.out.println("testError start");
      Map funcQuery = new HashMap();

      funcQuery.put("MENU_ID", "1012");
      T9ORM t = new T9ORM();
      for (i = 0; i < 1000; i++) {
        T9SysFunction functionTmp = (T9SysFunction)t.loadObjSingle(dbConn , T9SysFunction.class , funcQuery);
//        ps = dbConn.prepareStatement("select FUNC_NAME from sys_function where MENU_id=?");
//        ps.setString(1, "1036");
//        rs = ps.executeQuery();
//        if (rs.next()) {
//          System.out.println(i + ">>" + rs.getString(1));
//        }
        //ps.close();
      }

      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "登录成功");
    } catch (Exception e) {
      e.printStackTrace();
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "登录失败>>" + i);
    }finally {
      T9DBUtility.close(ps, rs, null);
    }
    return "/core/inc/rtjson.jsp";
  }

  /**
   * 构造单个对象
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String testSingle(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      dbConn = TestDbUtil.getConnection(false, "TEST");
    } catch (Exception e) {
      e.printStackTrace();
    }
    T9ORM orm = new T9ORM();
    Object o = orm.loadObjSingle(dbConn, T9DsTable.class, 144);
    System.out.println(o.toString());
    PrintWriter pw = response.getWriter();
    pw.print("{\"data\":'" + o.toString() + "'}");
    return null;
  }
  
  /**
   * 构造单个对象

   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String testMulDetl(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      dbConn = TestDbUtil.getConnection(false, "TEST");
    } catch (Exception e) {
      e.printStackTrace();
    }
    T9ORM orm = new T9ORM();
    Object o = orm.loadObjComplex(dbConn, T9DsTable.class, 144);
    System.out.println(o.toString());
    PrintWriter pw = response.getWriter();
    pw.print("{\"data\":'" + o.toString() + "'}");
    return null;
  }

}
