package t9.rad.dsdef.act;

import java.sql.Connection;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import t9.core.data.T9DsTable;
import t9.core.data.T9RequestDbConn;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Out;
import t9.core.util.db.T9DBUtility;
import t9.rad.dsdef.logic.T9DsDefJsonlogic;
import t9.rad.dsdef.logic.T9DsDefLogic;
import java.lang.reflect.Field;

public class T9DsDefUpdateAct {
  private static Logger log = Logger.getLogger("t9.rad.dsdef.logic.T9DsDefUpdateAct");
 
  public String testMethod(HttpServletRequest request,
      HttpServletResponse response) throws Exception {

    try {

      Connection dbConn = null;
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
     
      String tableNo = request.getParameter("tableNoDiv");
      String tableNoField = request.getParameter("tableNo");
      //System.out.println(tableNoField+"5656565656565");
      
      String classTable = (String) request.getParameter("T9DsTable");
      //System.out.println(classTable);
      T9DsDefFormUpdateAct ds = new T9DsDefFormUpdateAct();
    //删除子表
      String tableNoDiv = request.getParameter("tableNoDiv");
      //System.out.println(tableNoDiv+"5656565656565222222222222222222");
      T9DsDefLogic td = new T9DsDefLogic();
      //td.delete(tableNoDiv, dbConn);
      td.delete(tableNoDiv, dbConn);
      
      
      //修改主表
      Object obj = ds.build(request, classTable);
      //System.out.println("gggggggggggggggggggggggggggggggggggggg");
      
      
      int idN = Integer.parseInt(request.getParameter("id"));
      //System.out.println(idN+"zzzzzzzzzzzzz");
      String tableNo1  = request.getParameter("tableNo");
      T9DsDefFormMoreAct dm = new T9DsDefFormMoreAct();
      String classField = (String) request.getParameter("T9DsField");
      //System.out.println(classField+"yyyyyyyyyyyyyyyyyyyy");
      dm.build(request, classField, idN);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "修改成功");
      //String classField = (String) request.getParameter("T9DsField");
      //T9DsDefFormMoreUpdateAct dm = new T9DsDefFormMoreUpdateAct();
      //dm.build(request, classField, idName);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "submit failed");
      throw ex;
    }
    //return "/rad/dsdef/jsp/success.jsp";
    return "/core/inc/rtjson.jsp";
    //return "/raw/cy/gridDebug.html";
  }
}
