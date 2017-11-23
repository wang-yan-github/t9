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
import t9.core.data.T9PageDataListNew;
import t9.core.data.T9PageQueryParamNew;
import t9.core.data.T9RequestDbConn;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.load.T9PageLoaderNew;
import t9.core.util.T9Out;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;
import t9.rad.dsdef.logic.T9DsDefJsonlogic;
import t9.rad.dsdef.logic.T9DsDefLogic;
import java.lang.reflect.Field;

public class T9DsDefMoreAct {
  private static Logger log = Logger.getLogger("t9.rad.dsdef.logic.T9DsDefMoreAct");

  public String testMethod(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String tableNo = request.getParameter("tableNo");
      T9DsDefJsonlogic json = new T9DsDefJsonlogic();
      T9DsDefLogic ddl = new T9DsDefLogic();
      List list1 = ddl.selectTableField(dbConn, tableNo);
      Object ob = null;
      StringBuffer jsons = new StringBuffer(" { \"total\":"+100+","+"\"records\":[");        
       for (Iterator its = list1.iterator(); its.hasNext();) {
        ob = its.next();
        String s = (json.toJson(ob)).toString();
        jsons.append(s);
        if(its.hasNext()){
          jsons.append(",");
        }
      }
      jsons.append("]}");
      PrintWriter out = response.getWriter();
      out.println(jsons.toString().trim());                           
      out.flush();
      out.close();
     //System.out.println(jsons.toString());
     /*// out.flush();
      String classTable = (String) request.getParameter("T9DsTable");
      T9DsDefFormAct ds = new T9DsDefFormAct();
      Object obj = ds.build(request, classTable);
      String classField = (String) request.getParameter("T9DsField");
      int idName = Integer.parseInt(request.getParameter("id"));
      T9DsDefFormMoreAct dm = new T9DsDefFormMoreAct();
      dm.build(request, classField, idName);
      String tableNo = request.getParameter("tableNo");
      int fieldNo = Integer.parseInt(request.getParameter("fieldNo"));
      T9DsDefLogic dsdef = new T9DsDefLogic();
      dsdef.delete(fieldNo, dbConn);
      dsdef.selectTable(dbConn);
      dsdef.selectTableField(dbConn, tableNo);
*/
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "submit failed");

      throw ex;
    }
  /*  return "/rad/dsdef/jsp/success.jsp";
*/
    // fieldNo, fieldName, fieldDesc,
    // fkTableNo, fkTableNo2, fkRelaFieldNo, fkNameFieldNo, fkFilter,
    // codeClass, defaultValue, formatMode, formatRule, errorMsrg,
    // fieldPrecision, fieldScale, dataType, isPrimKey, isIdentity,
    // displayLen, isMustFill

    // T9DBUtility dbUtil = new T9DBUtility();
    // dbConn = dbUtil.getConnection(false, "sampledb");
    return null;
  }
  
  public String testMethod2(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String tableNo = request.getParameter("tableNo");
      String sql = " SELECT SEQ_ID, TABLE_NO, FIELD_NO, FIELD_NAME, PROP_NAME, FIELD_DESC, FK_TABLE_NO"
      		       + " , FK_TABLE_NO2, FK_RELA_FIELD_NO, FK_NAME_FIELD_NO, FK_FILTER, CODE_CLASS, DEFAULT_VALUE"
      		       + " , FORMAT_MODE, FORMAT_RULE, ERROR_MSRG, FIELD_PRECISION, FIELD_SCALE, DATA_TYPE, IS_IDENTITY"
      		       + " , DISPLAY_LEN, IS_MUST_FILL, IS_PRIMARY_KEY, FK_NAME_FIELD_NO2 "
      		       + " FROM ds_field d "
      		       + " WHERE d.TABLE_NO = '" + tableNo + "'"
      		       + " ORDER BY FIELD_NO asc ";
      T9PageQueryParamNew queryParam = (T9PageQueryParamNew) T9FOM.build(request.getParameterMap());
      T9PageDataListNew pageDataList = T9PageLoaderNew.loadPageList(dbConn, queryParam, sql);
      String d = pageDataList.toJson();
      PrintWriter pw = response.getWriter();
      pw.println(d);
      pw.flush();
      pw.close();
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "submit failed");
      throw ex;
    }
    return null;
  }
  
  public String editDsField(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);   
      dbConn = requestDbConn.getSysDbConn();
      Object obj = T9FOM.build(request.getParameterMap());
      T9ORM orm = new T9ORM();
      orm.updateSingle(dbConn, obj);        
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"编辑数据成功");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";  
  }
}
