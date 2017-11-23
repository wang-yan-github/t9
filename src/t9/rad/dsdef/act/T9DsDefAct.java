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
import t9.core.util.db.T9DTJ;
import t9.core.util.db.T9ORM;
import t9.rad.dsdef.logic.T9DsDefJsonlogic;
import t9.rad.dsdef.logic.T9DsDefLogic;
import test.core.util.db.TestDbUtil;

import java.lang.reflect.Field;

public class T9DsDefAct {
  private static Logger log = Logger.getLogger("t9.rad.dsdef.logic.T9DsDefAct");

  public String testMethod(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    try {
      Connection dbConn = null;
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      response.setCharacterEncoding(T9Const.DEFAULT_CODE);
      PrintWriter out = null;
      try {
        out = response.getWriter();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      int total = 0;
      String tableNo = request.getParameter("tableNo");
      T9DsTable st = new T9DsTable();
      T9DsDefJsonlogic json = new T9DsDefJsonlogic();
      T9DsDefLogic ddl = new T9DsDefLogic();
      List list = ddl.selectTable(dbConn, total);
      total = ddl.getTotles();
      // List list1 = ddl.selectTableField(dbConn, tableNo);
      Object ob = null;
      StringBuffer jsons = new StringBuffer(" { \"total\":" + total + ","
          + "\"records\":[");
      for (Iterator its = list.iterator(); its.hasNext();) {
        ob = its.next();
        try {
          String s = (json.toJson(ob)).toString();
          jsons.append(s);
          if (its.hasNext()) {
            jsons.append(",");
          }
        } catch (Exception e) {
          // TODO Auto-generated catch block
          request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
          request.setAttribute(T9ActionKeys.RET_MSRG, "submit failed");
          throw e;
        }
      }
      jsons.append("]}");
      //System.out.println(jsons.toString().trim());
      out.println(jsons.toString().trim());
      out.flush();
      out.close();
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "submit failed");
      throw ex;
    }
    return null;
  }

  public String insertDsDef(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    try {
      Connection dbConn = null;
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String tableNo = request.getParameter("tableNo");
      T9DsDefLogic ddl = new T9DsDefLogic();
      if (ddl.existsTableNo(dbConn, tableNo, "0")) {
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG, "表编码不能重复");
        return "/core/inc/rtjson.jsp";
      } else {
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功");
      }
      String classTable = (String) request.getParameter("T9DsTable");
      T9DsDefFormAct ds = new T9DsDefFormAct();
      Object obj = ds.build(request, classTable, tableNo);
      String classField = (String) request.getParameter("T9DsField");
      int idName = Integer.parseInt(request.getParameter("id"));
      T9DsDefFormMoreAct dm = new T9DsDefFormMoreAct();
      dm.build(request, classField, idName);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "新增加成功");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "submit failed");
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

  /**
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String existsTableNo(HttpServletRequest request, HttpServletResponse response) throws Exception{
    try {
      Connection dbConn = null;
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String tableNo = request.getParameter("tableNo");
      String seqId = request.getParameter("seqId");
      T9DsDefLogic ddl = new T9DsDefLogic();
      boolean isExists = ddl.existsTableNo(dbConn, tableNo, seqId);
      String isExistStr = "0";
      if(isExists){
        isExistStr = "1";
      }
      String data = "{\"isExistsTableNo\":\"" + isExistStr + "\"}";
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "submit failed");
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String deleteDsDef(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    try {
      Connection dbConn = null;
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String tableSeqId = request.getParameter("seqId");
      String tableNoF = request.getParameter("tableNoF");
      int obj = Integer.parseInt(request.getParameter("seqId"));
      T9DsDefLogic dsdef = new T9DsDefLogic();
      dsdef.deleteDsDef(tableSeqId, dbConn, tableNoF);
      T9ORM orm = new T9ORM();
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "submit failed");
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

  public String updateDsDef(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String classTable = (String) request.getParameter("T9DsTable");
      
      T9DsDefFormUpdateAct ds = new T9DsDefFormUpdateAct();
      // 删除子表
      String tableNoDiv = request.getParameter("tableNoDiv");
      T9DsDefLogic td = new T9DsDefLogic();
      td.delete(tableNoDiv, dbConn);
      // 修改主表
      Object obj = ds.build(request, classTable);
      int idN = Integer.parseInt(request.getParameter("id"));
      T9DsDefFormMoreAct dm = new T9DsDefFormMoreAct();
      String classField = (String) request.getParameter("T9DsField");
      // 插入子表
      dm.build(request, classField, idN);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "修改成功");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "submit failed");
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

  public String listDsDef(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    // 1.得到tabName,pageNum,pageRows
    response.setCharacterEncoding("UTF-8");
    // 2.通过tabName,pageNum,pageRows得到json数据
    String tabNo = request.getParameter("tabNo");
    String pageNumStr = request.getParameter("pageNum");
    String pageRowsStr = request.getParameter("pageRows");
    int pageNum = Integer.parseInt(pageNumStr);
    int pageRows = Integer.parseInt(pageRowsStr);
    //System.out.println(pageNum);
    T9DTJ dtj = new T9DTJ();
    try {
      T9ORM t = new T9ORM();
      //System.out.println("ddddd");
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      Connection dbConn = requestDbConn.getSysDbConn();
      String[] filters = new String[] { "TABLE_NO = '" + tabNo + "'" };
//      StringBuffer d = dtj.dataToJson(dbConn, tabNo, pageNum, pageRows);
  //    T9DsTable dsTable = (T9DsTable) t.loadObj(dbConn, T9DsTable.class,
   //       filters);
      dbConn.close();
      PrintWriter pw = response.getWriter();
  //    pw.println(d.toString());
      pw.flush();
      pw.close();
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    // 3.将json数据输出到前端    return null;
  }
}
