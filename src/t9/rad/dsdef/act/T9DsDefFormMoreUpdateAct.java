package t9.rad.dsdef.act;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import t9.core.data.T9DsField;
import t9.core.data.T9DsTable;
import t9.core.data.T9RequestDbConn;
import t9.core.global.T9BeanKeys;
import t9.rad.dsdef.logic.T9DsDefLogic;

public class T9DsDefFormMoreUpdateAct {
  private static Logger log = Logger.getLogger("t9.rad.dsdef.logic.T9DsDefAct");

  public Object build(HttpServletRequest request, String classField,int idName)
      throws Exception {

    Class classTypeField = Class.forName(classField);
    String tableNo = request.getParameter("tableNoDiv");
    T9DsField obj = (T9DsField) classTypeField.newInstance();
    Field[] fields = classTypeField.getDeclaredFields();
    Object valueSet = null;

    
    for (int j = 1; j <= idName; j++) {
      for (int i = 0; i < fields.length; i++) {
        Field field = fields[i];
        Object objo;
        String strg = "get";
        if (field.getType().equals(Boolean.TYPE)) {
          strg = "is";
        }
        String fieldN = field.getName();
        String valueStr = request.getParameter(fieldN + "_" + j);

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

        Method getMethod = classTypeField.getMethod(getName);
        Method setMethod = classTypeField.getMethod(setName,
            new Class[] { field.getType() });

        valueSet = setMethod.invoke(obj, new Object[] { objo });
        Object value = getMethod.invoke(obj);

        //System.out.println(fieldN + " : " + value);

      }

      T9DsDefLogic ddl = new T9DsDefLogic();
      Connection dbConn = null;
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      ddl.DsDefUpdateField(dbConn, tableNo, obj);
      //ddl.DsDefInsertZ(dbConn, tableNo, obj);
    }
    return obj;
  }
  // public static void main(String args[]) throws Exception{
  // Class clzz = Class.forName("t9.core.data.T9DsField");
  // Method setMethod = clzz.getMethod("setFieldPrecision", new
  // Class[]{Integer.class});
  // Object obj = clzz.newInstance();
  // setMethod.invoke(obj, new Object[] { null });
  // T9DsField t = (T9DsField)obj;
  // if(t.getDefaultValue()==null){
  // System.out.print("dd");
  // }
  //   
  // }
}
