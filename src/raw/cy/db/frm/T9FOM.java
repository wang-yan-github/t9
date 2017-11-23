package raw.cy.db.frm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import t9.core.util.T9ReflectUtility;
import t9.core.util.T9Utility;

public class T9FOM {
  /**
   * 从表单创建DTO对象
   * 
   * @param request
   * @return
   * @throws Exception
   */
  public static Map<String, Object> build(Map request, String postFix)
      throws Exception {

    Map<String, Object> tableInfo = new HashMap<String, Object>();
    Set keys = request.entrySet();
    for (Object obj : keys) {
      String key = (String) obj;
      String value = (String) request.get(key);
      // 传主表的名称
    }
    return tableInfo;
  }

  /**
   * 转换成Json对象字符串
   * 
   * @param obj
   * @return
   * @throws Exception
   */
  public static StringBuffer map2Json(Map<String, Object> rsp) throws Exception {
    StringBuffer rtBuf = new StringBuffer("{");
    String value = "";
    String fieldName = "";
    Object[] keys = rsp.keySet().toArray();
    for (int i = 0; i < keys.length; i++) {
      String key = (String) keys[i];
      Object obj = rsp.get(key);
      fieldName = key + i;
      if (Map.class.isInstance(obj)) {
        Map list = (Map) obj;
        String listName = fieldName + "List";
        rtBuf.append(listName + " : [");
        Object[] map = list.keySet().toArray();
        for (int k = 0; k < map.length; k++) {
          rtBuf.append("{");
          String kk = (String) map[k];
          Map sub = (Map) list.get(kk);
          Object[] subKeys = sub.keySet().toArray();
          for (int j = 0; j < subKeys.length; j++) {
            String subKey = (String) subKeys[j];
            Object fieldValue = sub.get(subKey);
            String subFieldName = fieldName + subKey + k;
            if (int.class.isInstance(fieldValue)
                || Integer.class.isInstance(fieldValue)
                || double.class.isInstance(fieldValue)
                || Double.class.isInstance(fieldValue)) {
              value = T9Utility.null2Empty(fieldValue.toString());
            } else {
              if (fieldValue == null) {
                value = "\"" + T9Utility.null2Empty(null) + "\"";
              } else {
                value = "\"" + T9Utility.null2Empty(fieldValue.toString())
                    + "\"";
              }
            }
            rtBuf.append(subFieldName).append(":").append(value);
            if (j < subKeys.length - 1) {
              rtBuf.append(",");
            }
          }
          rtBuf.append("}");
          if (k < list.size() - 1) {
            rtBuf.append(",");
          }
        }
        rtBuf.append("]");
        if (i < keys.length - 1) {
          rtBuf.append(",");
        }
      }
    }
    rtBuf.append("}");
    return rtBuf;
  }

  public static StringBuffer map2Json(String prx, Map sub, int index) {
    StringBuffer rtBuf = new StringBuffer("{");
    String value = "";
    Object[] subKeys = sub.keySet().toArray();
    for (int j = 0; j < subKeys.length; j++) {
      String subKey = (String) subKeys[j];
      Object fieldValue = sub.get(subKey);
      String subFieldName = prx + subKey + index;
      if (int.class.isInstance(fieldValue)
          || Integer.class.isInstance(fieldValue)
          || double.class.isInstance(fieldValue)
          || Double.class.isInstance(fieldValue)) {
        value = T9Utility.null2Empty(fieldValue.toString());
      } else {
        if (fieldValue == null) {
          value = "\"" + T9Utility.null2Empty(null) + "\"";
        } else {
          value = "\"" + T9Utility.null2Empty(fieldValue.toString()) + "\"";
        }
      }
      rtBuf.append(subFieldName).append(":").append(value);
      if (j < subKeys.length - 1) {
        rtBuf.append(",");
      }
    }
    rtBuf.append("}");
    return rtBuf;
  }
}
