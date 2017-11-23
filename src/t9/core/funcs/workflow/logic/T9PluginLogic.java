package t9.core.funcs.workflow.logic;

import java.sql.Connection;
import java.util.Date;
import java.util.Map;

import t9.core.data.T9DbRecord;
import t9.core.data.T9PageDataList;
import t9.core.data.T9PageQueryParam;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.workflow.data.T9FlowRunData;
import t9.core.funcs.workflow.util.T9FlowRunUtility;
import t9.core.funcs.workflow.util.T9WorkFlowUtility;
import t9.core.load.T9PageLoader;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.form.T9FOM;

public class T9PluginLogic {
  public StringBuffer getSelectFlowData( Connection conn, Map request , String findStr , String flowId, String field , T9Person user)
  throws Exception { 
StringBuffer resualt = new StringBuffer();
try {
  String query2 = "select FLOW_RUN.RUN_ID , RUN_NAME,";
  field = T9WorkFlowUtility.getOutOfTail(field, "`");
  String[] fields = field.split("`");
  
  for (int i = 0 ;i < fields.length ; i++) {
    if ("[文号]".equals(fields[i])
        || "[流水号]".equals(fields[i])) {
      continue;
    }
    query2 += "1,";
  }
  if (query2.endsWith(",")) {
    query2 =  T9WorkFlowUtility.getOutOfTail(query2);
  }
  query2 += " FROM FLOW_RUN  where  FLOW_RUN.FLOW_ID =" + flowId + " " + this.getRunId(user.getSeqId());
  
  String[] value = (String[])request.get("runName");
  if (value != null && value.length > 0){
    if (!T9Utility.isNullorEmpty(value[0])) {
      query2 += " and RUN_NAME like  '%" + T9Utility.encodeLike(value[0]) + "%' " + T9DBUtility.escapeLike() ;
    }
  }
  value = (String[])request.get("runId");
  if (value != null && value.length > 0){
    if (!T9Utility.isNullorEmpty(value[0])) {
      query2 += " and FLOW_RUN.RUN_ID =  '" + T9Utility.encodeLike(value[0]) + "' " ;
    }
  }
  
  query2 += " order by FLOW_RUN.RUN_ID";
  T9FlowRunUtility util = new T9FlowRunUtility();
  T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request,T9PageQueryParam.class,null);
  T9PageDataList pageDataList = T9PageLoader.loadPageList(conn,queryParam,query2);
  for (int i = 0 ;i < pageDataList.getRecordCnt() ; i++) {
    T9DbRecord record = pageDataList.getRecord(i);
    int runId = T9Utility.cast2Long(record.getValueByName("runId")).intValue();
    String runName = (String)record.getValueByName("runName");
    String values = "";
    for (int j = 0 ;j < fields.length ; j++) {
      String fi = fields[j];
      
      if ("[文号]".equals(fi)
          || "[流水号]".equals(fi) 
          || !T9Utility.isInteger(fi)) {
        if ("[文号]".equals(fi)) {
          values += runName + "++";
        }
        if ("[流水号]".equals(fi)) {
          values += runId + "++";
        }
        continue;
      }
      T9FlowRunData data  = util.getFlowRunData(conn, runId,Integer.parseInt(fi) , Integer.parseInt(flowId));
      String v = "";
      if (data != null) {
        v = data.getItemData();
        v = v.replace("\n", "&&&&");
      } 
      record.updateField("DATA_" + fi,  v);
      values += v + "++"; 
    }
    record.addField("value", values);
  }
  resualt.append(pageDataList.toJson());
} catch (Exception ex) {
  ex.printStackTrace();
  throw ex;
}
return resualt;
}
  public String getRunId(int userId) {
    String myRunId  = " select  DISTINCT(FLOW_RUN.RUN_ID) from FLOW_RUN_PRCS where  USER_ID=" + userId + " and PRCS_FLAG <> 5 ";
    return " and FLOW_RUN.RUN_ID IN (" + myRunId + ")";
  }
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
