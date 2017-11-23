package t9.core.funcs.doc.logic;

import java.sql.Connection;
import java.util.Date;
import java.util.Map;

import t9.core.data.T9DbRecord;
import t9.core.data.T9PageDataList;
import t9.core.data.T9PageQueryParam;
import t9.core.funcs.person.data.T9Person;
import t9.core.load.T9PageLoader;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.form.T9FOM;

public class T9PluginLogic {
  public StringBuffer getSelectData( Connection conn, Map request , String findStr , String sql)
  throws Exception { 
StringBuffer resualt = new StringBuffer();
String query = "";
try {
  if (!T9Utility.isNullorEmpty(findStr)) {
    String[] items = findStr.split(",");
    for (String tmp : items) {
      if (!T9Utility.isNullorEmpty(tmp)){
        String[] value = (String[])request.get(tmp);
        if (value != null && value.length > 0){
          if (!T9Utility.isNullorEmpty(value[0])) {
            query += " and " + tmp + " like '%" + T9Utility.encodeLike(value[0]) + "%' " + T9DBUtility.escapeLike() ;
          }
        }
      }
    }
  }
  sql += query;
  T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request,T9PageQueryParam.class,null);
  T9PageDataList pageDataList = T9PageLoader.loadPageList(conn,queryParam,sql);
  for (int i = 0 ;i < pageDataList.getRecordCnt() ; i++) {
    T9DbRecord record = pageDataList.getRecord(i);
    String values = "";
    int fieldCnt = record.getFieldCnt();
    for (int j= 0 ;j < fieldCnt; j++) {
      Object colObj =  record.getValueByIndex(j);
      String val = "";
      if (colObj != null) {
        Class fieldType = colObj.getClass(); 
        if (Integer.class.equals(fieldType)) {        
          val = String.valueOf(((Integer)colObj).intValue());
        }else if (Long.class.equals(fieldType)) {        
          val = String.valueOf(((Long)colObj).longValue());
        }else if (Double.class.equals(fieldType)) {        
          val = T9Utility.getFormatedStr(((Double)colObj).doubleValue(), T9Utility.WITHOUTGROUP);
        }else if (Date.class.equals(fieldType)) {
          val =  T9Utility.getDateTimeStr((Date)colObj);
        }else {
          if (colObj == null) {
            val = "";
          }else {
            String tmpStr = T9Utility.null2Empty(colObj.toString());
            tmpStr = tmpStr.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "").replace("\n", "").replace("\'", "\\\'");
            val = tmpStr;
          }
        }
        values += val + "++";
      }
      record.addField("value", values);
    }
  }
  resualt.append(pageDataList.toJson());
} catch (Exception ex) {
  ex.printStackTrace();
  throw ex;
}
return resualt;
}
}
