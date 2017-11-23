package t9.core.act;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;

public class T9ORMAct {
  /**
   * log
   */
  private static Logger log = Logger
                                .getLogger("t9.core.act.T9ORMAct");

  public String loadData(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    //System.out.println(T9FOM.buildList(request.getParameterMap()));
    return null;
  }

  public String update(HttpServletRequest request, HttpServletResponse response)
      throws Exception {
    //System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      Map paramMap = new HashMap(request.getParameterMap());
      String mainTable = ((String[]) paramMap.get("mainTable"))[0];
      paramMap.remove("mainTable");
      Map formInfo = T9FOM.buildMap(paramMap);
      T9ORM orm = new T9ORM();
      orm.updateComplex(dbConn, mainTable, formInfo);
      String ms = "";
      for (Object obj : paramMap.keySet()) {
        String[] values = (String[]) paramMap.get(obj);
        ms += obj + " = " + values[0] + "  ";
      }
      //System.out.println("maps :" + ms);
      //System.out.println("mm : " + formInfo);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "加载代码失败" + ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

  public String delete(HttpServletRequest request, HttpServletResponse response)
      throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      Map paramMap = request.getParameterMap();
      String mainTable = ((String[]) paramMap.get("mainTable"))[0];
      paramMap.remove("mainTable");
      Map formInfo = T9FOM.buildMap(paramMap);
      T9ORM orm = new T9ORM();
      orm.deleteComplex(dbConn, mainTable, formInfo);
      String ms = "";
      for (Object obj : paramMap.keySet()) {
        String[] values = (String[]) paramMap.get(obj);
        ms += obj + " = " + values[0] + "  ";
      }
      //System.out.println("maps :" + ms);
      //System.out.println("mm : " + formInfo);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "加载代码失败" + ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

  public String add(HttpServletRequest request, HttpServletResponse response)
      throws Exception {

    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
      Map paramMap = new HashMap(request.getParameterMap());
      String mainTable = ((String[]) paramMap.get("mainTable"))[0];
      paramMap.remove("mainTable");
      Map fieldInfo = T9FOM.buildMap(paramMap);
      //System.out.println("map ========================================= "+fieldInfo);
      T9ORM orm = new T9ORM();
      orm.saveComplex(dbConn, mainTable, fieldInfo);
      String ms = "";
      for (Object obj : paramMap.keySet()) {
        String[] values = (String[]) paramMap.get(obj);
        ms += obj + " = " + values[0] + "  ";
      }
      //System.out.println("maps :" + ms);
      //System.out.println("mm : " + fieldInfo);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "加载代码失败" + ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
}
