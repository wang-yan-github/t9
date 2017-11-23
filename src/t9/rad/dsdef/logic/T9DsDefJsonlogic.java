package t9.rad.dsdef.logic;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.global.T9BeanKeys;
import t9.core.util.T9Out;
import t9.core.util.db.T9DBUtility;
import t9.rad.dsdef.act.T9DsDefAct;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
public class T9DsDefJsonlogic {
  /**
   * log
   */
  private static Logger log = Logger.getLogger("t9.core.act.action.T9TestAct");
  
  public StringBuffer toJson(Object obj) throws Exception {
    Class cla = obj.getClass();
    Field[] fields = cla.getDeclaredFields();
    StringBuffer json = new StringBuffer(" {");
    //System.out.println("fields.length------"+fields.length);
    for (int i = 0; i < fields.length; i++) {
      //System.out.println("3333333344: "+i);
      Field field = fields[i];
      String fieldName = field.getName();
      String stringLetter = fieldName.substring(0, 1).toUpperCase();
      String getName = "get" + stringLetter + fieldName.substring(1);
      Method getMethod = cla.getMethod(getName);
      Object value = getMethod.invoke(obj);
      if (obj == null) {
        json.append("null");
      }
      json.append("\"").append(fieldName).append("\"").append(":");
      //System.out.println(value+"-----value----"+i);
      if (value == null||value.equals("")) {
        json.append("\"").append("").append("\"");
      } else {
        json.append("\"").append(value).append("\"");
      }
      //json.append("\"").append(value).append("\"");
      if(i < fields.length-1){
        json.append(",");
      }
    }
    json.append("}");
    return json;
  }
}
