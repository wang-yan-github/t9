package t9.rad.dsdef.act;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import t9.core.data.T9DsTable;
import t9.core.data.T9RequestDbConn;
import t9.core.global.T9BeanKeys;
import t9.rad.dsdef.logic.T9DsDefLogic;

public class T9DsDefFormUpdateAct {
  private static Logger log = Logger.getLogger("t9.rad.dsdef.logic.T9DsDefAct");

  public Object build(HttpServletRequest request, String classTable) throws Exception {
    Connection dbConn = null;
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request
        .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
    dbConn = requestDbConn.getSysDbConn();
    Class classTypeTable = Class.forName(classTable);
    String tableNo = request.getParameter("tableNoDiv");
    String tableNoNew = request.getParameter("tableNo");
   int idN = Integer.parseInt(request.getParameter("id"));
//    for(int x=1;x<=idN;x++){
//      //int seqId = x;
//      String dseqId = request.getParameter("seqid"+"_"+x);
//      System.out.println(dseqId+":"+"mmmmmmmmmmmmmmmmmmmmmmmm");
//    }
//    String tableNo1  = request.getParameter("tableNo");
//    T9DsDefFormMoreAct dm = new T9DsDefFormMoreAct();
//    String classField = (String) request.getParameter("T9DsField");
//    System.out.println(classField+"yyyyyyyyyyyyyyyyyyyy");
//    dm.build(request, classField, idN);
 //   System.out.println(tableNo+"++++++++");
    
    T9DsTable obj = (T9DsTable) classTypeTable.newInstance();
    Field[] fields = classTypeTable.getDeclaredFields();
    Object valueSet = null;
    for (int i = 0; i < fields.length; i++) {
      Field field = fields[i];
      Object objo=null;
      String strg = "get";
      if (field.getType().equals(Boolean.TYPE)) {
        strg = "is";
      }
      String fieldN = field.getName();
      //System.out.println(fieldN+"lllllllllllllllllllllllll");
      String valueStr = request.getParameter(fieldN);
      //System.out.println(valueStr+"ddddddddddddddddddd");
      
      if (field.getType().equals(Integer.TYPE)) {
        if (valueStr.equals("")) {
          objo = new Integer(0);
        } else {
          objo = Integer.valueOf(valueStr);
        }
      } else if (field.getType().equals(Float.TYPE)) {
        objo = Float.valueOf(valueStr);
      } else if (field.getType().equals(Double.TYPE)) {
        objo = Double.valueOf(valueStr);
      } else {
        objo = valueStr;
      }
      String stringLetter = fieldN.substring(0, 1).toUpperCase();
      String getName = strg + stringLetter + fieldN.substring(1);
      String setName = "set" + stringLetter + fieldN.substring(1);
      Method getMethod = classTypeTable.getMethod(getName);
      //System.out.println(getMethod);
      Method setMethod = classTypeTable.getMethod(setName, new Class[] { field
          .getType() });
      valueSet = setMethod.invoke(obj, new Object[] { objo });
      Object value = getMethod.invoke(obj);
      //System.out.println(fieldN + " : " + value);
    }
    T9DsDefLogic ddl = new T9DsDefLogic();
    ddl.DsDefUpdateTable(dbConn, tableNo, obj, tableNoNew);
    //ddl.DsDefInsert(dbConn, obj);
    //ddl.DsDefInsertZ(dbConn, tableNo, obj);
    return obj;
  }
}
