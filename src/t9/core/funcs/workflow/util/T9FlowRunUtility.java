package t9.core.funcs.workflow.util;

import java.sql.Clob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import t9.core.funcs.dept.data.T9Department;
import t9.core.funcs.dept.logic.T9DeptLogic;
import t9.core.funcs.mobilesms.logic.T9MobileSms2Logic;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.sms.data.T9SmsBack;
import t9.core.funcs.sms.logic.T9SmsUtil;
import t9.core.funcs.workflow.data.T9FlowFormItem;
import t9.core.funcs.workflow.data.T9FlowRunData;
import t9.core.funcs.workflow.data.T9FlowType;
import t9.core.funcs.workflow.logic.T9AttachmentLogic;
import t9.core.funcs.workflow.logic.T9FlowRunLogic;
import t9.core.funcs.workflow.logic.T9FlowTypeLogic;
import t9.core.funcs.workflow.logic.T9FormVersionLogic;
import t9.core.funcs.workflow.logic.T9WorkflowSave2DataTableLogic;
import t9.core.util.T9Out;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
public class T9FlowRunUtility {
  private List<T9FlowRunData> frdList = null;
  public int getFlowId(Connection conn , String flowName) throws Exception {
    String query = "select seq_id from flow_type where flow_name='" + flowName + "' ";
    Statement stm = null; 
    ResultSet rs = null; 
    int flowId = 0;
    try { 
      stm = conn.createStatement(); 
      rs = stm.executeQuery(query); 
      if (rs.next()){ 
        flowId = rs.getInt("seq_id");
      } 
    } catch(Exception ex) { 
      throw ex; 
    } finally { 
      T9DBUtility.close(stm, rs, null); 
    } 
    return flowId;
  }
  public int getFlowId(Connection conn , int runId) throws Exception {
    String query = "select flow_id from flow_run where run_id='" + runId + "' ";
    Statement stm = null; 
    ResultSet rs = null; 
    int flowId = 0;
    try { 
      stm = conn.createStatement(); 
      rs = stm.executeQuery(query); 
      if (rs.next()){ 
        flowId = rs.getInt("flow_id");
      } 
    } catch(Exception ex) { 
      throw ex; 
    } finally { 
      T9DBUtility.close(stm, rs, null); 
    } 
    return flowId;
  }
  public String getChildRun(Connection conn , int runId) throws Exception {
    String queryUser = "select RUN_ID,RUN_NAME FROM FLOW_RUN  WHERE PARENT_RUN='"+ runId +"'";
    Statement stm6 = null; 
    ResultSet rs6 = null; 
    StringBuffer sb = new StringBuffer();
    sb.append("[");
    int count = 0 ;
    try { 
      stm6 = conn.createStatement(); 
      rs6 = stm6.executeQuery(queryUser); 
      while (rs6.next()){
        int runId2 = rs6.getInt("RUN_ID");
        String runName = rs6.getString("RUN_NAME");
        sb.append("{runId:").append(runId2);
        sb.append(",runName:\"").append(T9Utility.encodeSpecial(runName)).append("\"},");
        count++;
      } 
    } catch(Exception ex) { 
      throw ex; 
    } finally { 
      T9DBUtility.close(stm6, rs6, null); 
    }
    if (count > 0) {
      sb.deleteCharAt(sb.length() - 1);
    }
    sb.append("]");
    return sb.toString();
  }
  public T9Person queryPerson(Connection conn , String prcsOpUser) throws Exception {
    String queryUser = "select USER_NAME , DEPT_ID , USER_PRIV from PERSON where SEQ_ID = '" + prcsOpUser + "'";
    Statement stm6 = null; 
    ResultSet rs6 = null; 
    T9Person p = new T9Person();
    try { 
      stm6 = conn.createStatement(); 
      rs6 = stm6.executeQuery(queryUser); 
      if (rs6.next()){ 
        p.setDeptId(rs6.getInt("DEPT_ID"));
        p.setUserName(rs6.getString("USER_NAME"));
        p.setUserPriv(rs6.getString("USER_PRIV"));
      } 
    } catch(Exception ex) { 
      throw ex; 
    } finally { 
      T9DBUtility.close(stm6, rs6, null); 
    }
    return p;
  }
  public Map getRunData(int runId , Connection conn , int fromId , int flowId , String queryItem) throws Exception{
    //查询相关表单
    Map parenValue = new HashMap();
    T9FormVersionLogic lo = new T9FormVersionLogic();
    T9FlowRunUtility logic = new T9FlowRunUtility();
    
    int versionNo = lo.getVersionNo(conn, runId);
    int formId = logic.getFormId(conn, flowId);
    int formSeqId = lo.getFormSeqId(conn, versionNo, formId);
    
    if (!T9WorkFlowUtility.isSave2DataTable()){
      String query1 = "select TITLE , ITEM_DATA FROM FLOW_FORM_ITEM , FLOW_RUN_DATA where FLOW_FORM_ITEM.ITEM_ID =  FLOW_RUN_DATA.ITEM_ID " 
        + " AND FLOW_RUN_DATA.RUN_ID = '" + runId + "' AND FLOW_FORM_ITEM.FORM_ID='" + formSeqId + "' ";
      if (!"".equals(queryItem)) {
        queryItem = T9WorkFlowUtility.getInStr(queryItem);
        query1 +=  " AND FLOW_FORM_ITEM.TITLE IN (" + queryItem + ")";
      }
      Statement stm5 = null; 
      ResultSet rs5 = null; 
      try { 
        stm5 = conn.createStatement(); 
        rs5 = stm5.executeQuery(query1); 
        while (rs5.next()){ 
          String title = rs5.getString("title");
          String value = rs5.getString("ITEM_DATA");
          parenValue.put(title, value);
        } 
      } catch(Exception ex) { 
        throw ex; 
      } finally { 
        T9DBUtility.close(stm5, rs5, null); 
      }  
    } else {
      String query1 = "select TITLE , ITEM_ID , CLAZZ FROM FLOW_FORM_ITEM  where  FLOW_FORM_ITEM.FORM_ID='" + formSeqId + "' ";
      if (!"".equals(queryItem)) {
        queryItem = T9WorkFlowUtility.getInStr(queryItem);
        query1 +=  " AND FLOW_FORM_ITEM.TITLE IN (" + queryItem + ")";
      }
      Map map = new HashMap();
      Statement stm5 = null; 
      ResultSet rs5 = null; 
      try { 
        stm5 = conn.createStatement(); 
        rs5 = stm5.executeQuery(query1); 
        while (rs5.next()){ 
          String clazz = rs5.getString("CLAZZ");
          if ("DATE".equals(clazz) || "USER".equals(clazz)) {
            continue;
          }
          String title = rs5.getString("TITLE");
          int itemId = rs5.getInt("ITEM_ID");
          map.put(itemId, title);
        } 
      } catch(Exception ex) { 
        throw ex; 
      } finally { 
        T9DBUtility.close(stm5, rs5, null); 
      } 
      String tableName = T9WorkflowSave2DataTableLogic.FORM_DATA_TABLE_PRE+ flowId  + "_" + formSeqId;
      String query ="select * from " + tableName + " where RUN_ID = "+ runId;
      Statement stm1 = null; 
      ResultSet rs1 = null; 
      try { 
        stm1 = conn.createStatement(); 
        rs1 = stm1.executeQuery(query); 
        if (rs1.next()){ 
          Set<Integer> keys = map.keySet();
          for (Integer b : keys) {
            String key = "DATA_" + b;
            String data = rs1.getString(key);
            String title =(String)map.get(b);
            parenValue.put(title, data);
          }
        } 
      } catch(Exception ex) { 
        throw ex; 
      } finally { 
        T9DBUtility.close(stm1, rs1, null); 
      }
    }
    return parenValue;
  }
  public int createNewWork(Connection conn , int flowId , String prcsOpUser , String prcsUser  , int parentRunId , String allowBack  , String relation , int parentFlowId) throws Exception {
    //--- 新建工作 自动编号---
    Timestamp time =  new Timestamp(new Date().getTime());
    
  //查询是否为重名的
    T9FlowRunLogic frl = new T9FlowRunLogic();
    //如果没有指定runName
    T9FlowTypeLogic flowTypeLogic = new T9FlowTypeLogic();
    T9FlowType flowType = flowTypeLogic.getFlowTypeById(flowId , conn);
    T9Person user = this.queryPerson(conn, prcsOpUser);
    String runName = frl.getRunName(flowType, user , conn , true) ;
    String attachmentNewId = "";
    String attachmentName = "";
     if (!T9Utility.isNullorEmpty(allowBack) && !"0".equals(allowBack)) { 
       String query = "SELECT ATTACHMENT_ID,ATTACHMENT_NAME FROM FLOW_RUN WHERE RUN_ID='"+ parentRunId +"'";
       Statement stm1 = null; 
       ResultSet rs1 = null; 
       try { 
         stm1 = conn.createStatement(); 
         rs1 = stm1.executeQuery(query); 
         if (rs1.next()){ 
           String attachmentId = rs1.getString("ATTACHMENT_ID");
           attachmentName = rs1.getString("ATTACHMENT_NAME");
           if (!T9Utility.isNullorEmpty(attachmentId)) {
             T9AttachmentLogic logic = new T9AttachmentLogic();
             attachmentNewId = logic.copyAttach(attachmentId, attachmentName);
           }
         } 
       } catch(Exception ex) { 
         throw ex; 
       } finally { 
         T9DBUtility.close(stm1, rs1, null); 
       }
     }
   //查出最的runId
     T9ORM orm = new T9ORM();
     String query = "select MAX(RUN_ID) max FROM FLOW_RUN";
     int runId = 0;
     Statement stm = null;
     ResultSet rs = null;
     try {
       stm = conn.createStatement();
       rs = stm.executeQuery(query);
       if(rs.next()){
       //根据最大的构建 新的runId
         runId = rs.getInt("max") + 1;
       }
     } catch(Exception ex) {
       throw ex;
     } finally {
       T9DBUtility.close(stm, rs, null); 
     }
     
     //对工作名称进行处理


     runName = runName.replaceAll("\n", "");
     runName = runName.replaceAll("\r", "");
     runName = runName.replaceAll("\\{RUN\\}", String.valueOf(runId));
     //新建 一个工作实例     T9FormVersionLogic l = new T9FormVersionLogic();
     query = "insert into FLOW_RUN (RUN_ID "
       + " ,RUN_NAME "
       + " ,FLOW_ID "
       + " ,BEGIN_USER "
       + " ,BEGIN_TIME,PARENT_RUN,ATTACHMENT_ID,ATTACHMENT_NAME , FORM_VERSION) values ("+ runId 
       +",?,"+ flowType.getSeqId() 
       +","+ prcsOpUser 
       +",? "
       + ",'"+parentRunId+"'"
       + ", '"+attachmentNewId+"'"
       + ", ? , '"+ l.getMaxVersion(conn, flowType.getFormSeqId())+"')";
     PreparedStatement stm2 = null;
     try {
       stm2 = conn.prepareStatement(query);
       stm2.setString(1, runName);
       stm2.setTimestamp(2, time);
       stm2.setString(3, attachmentName);
       stm2.executeUpdate();
     } catch(Exception ex) {
       throw ex;
     } finally {
       T9DBUtility.close(stm2, null, null); 
     }
     
     //新建工作的第一个步骤     if (prcsUser == null) {
       prcsUser = "";
     }
     String[] users = prcsUser.split(",");
     for (String us : users) {
       if (!T9Utility.isNullorEmpty(us)) {
         int opFlag = 0;
         if(us.equals(prcsOpUser) )
           opFlag = 1;
         query = "insert into FLOW_RUN_PRCS (RUN_ID  "
           + " , PRCS_ID  "
           + " ,USER_ID  "
           + " ,PRCS_FLAG  "
           + " ,FLOW_PRCS  "
           + " ,CREATE_TIME , OP_FLAG) VALUES (" + runId + " , 1, " + us + ",'1','1',? , '"+opFlag+"')";
         
         PreparedStatement stm3 = null;
         try {
           stm3 = conn.prepareStatement(query);
           stm3.setTimestamp(1, time);
           stm3.executeUpdate();
         } catch(Exception ex) {
           throw ex;
         } finally {
           T9DBUtility.close(stm3, null, null); 
         }
       }
     }
     relation = (relation == null) ? "" : relation; 
     String[] relations = relation.split(",");
     Map relationMap = new HashMap();
     String queryItem = "";
     for(int i = 0 ;i < relations.length ; i++){
       String ss = relations[i];
       if(!"".equals(ss.trim())) {
         String parentField = ss.substring(0 , ss.indexOf("=>")).trim();
         String childField = ss.substring(ss.indexOf("=>") + 2).trim();
         relationMap.put(childField, parentField);
         queryItem += "'" + parentField + "',";
       } 
     }
     if (queryItem.endsWith(",")) {
       queryItem = queryItem.substring(0 , queryItem.length() - 1);
     }
     Map parenValue = this.getRunData(parentRunId, conn, this.getFormId(conn, parentFlowId), parentFlowId, queryItem);
     
     //查询表单字段信息
     Map formItemQuery = new HashMap();
     formItemQuery.put("FORM_ID", flowType.getFormSeqId());
     List<T9FlowFormItem> list = orm.loadListSingle(conn, T9FlowFormItem.class , formItemQuery);
     //实例 化一个T9FlowRunData列表并且添加到数据库
     
     String fieldName = "";
     String fieldVal = "";
     Map valueMap  = new HashMap();
     int count = 2 ;
     for(T9FlowFormItem tmp : list){
       String clazz = tmp.getClazz();
       String tag = tmp.getTag();
       String title = tmp.getTitle();
       String content = tmp.getContent();
       String itemData = "";
       if("DATE".equals(clazz) || "USER".equals(clazz)){
         continue;
       }
       if("INPUT".equals(tag) && content.indexOf("checkbox") != -1){
         Pattern pattern = Pattern.compile("\\s+[Cc][Hh][Ee][Cc][Kk][Ee][Dd]");  
         Matcher matcher = pattern.matcher(content); 
         if (matcher.find()) {
           itemData = "on";
         } else {
           itemData = "";
         }
       }else if(!"SELECT".equals(tag) && !"LIST_VIEW".equals(clazz)){
         itemData = tmp.getValue() == null ? "" : tmp.getValue();
         itemData = itemData.replaceAll("\"", "");
         if("{宏控件}".equals(itemData)){
           itemData = "";
         }
         if ("MODULE".equals(clazz)) {
           itemData = "";
         }
       }else{
         itemData = "";
       }
       String parentItem  = (String)relationMap.get(title);
       if (!T9Utility.isNullorEmpty(parentItem)) {
         String val = (String)parenValue.get(parentItem);
         if (val != null) {
           itemData = val;
         }
       }
       if (!T9WorkFlowUtility.isSave2DataTable()){
         T9FlowRunData runData = new T9FlowRunData();
         runData.setItemData(itemData);
         runData.setItemId(tmp.getItemId());
         runData.setRunId(runId);
         orm.saveSingle(conn, runData);
       } else {
         count++;
         fieldName += ",DATA_" + tmp.getItemId() ;
         fieldVal += ",?";
         valueMap.put(count, itemData);
       }
     }
     if (T9WorkFlowUtility.isSave2DataTable()){
       String tableName = T9WorkflowSave2DataTableLogic.FORM_DATA_TABLE_PRE + flowType.getSeqId() + "_" + flowType.getFormSeqId() ;
       String del = "DELETE FROM " + tableName  + " WHERE RUN_ID = " + runId;
       T9WorkFlowUtility.updateTableBySql(del, conn);
       PreparedStatement stm4 = null;
       String query2 = "insert into " +tableName
         + " (RUN_ID , RUN_NAME , BEGIN_USER, BEGIN_TIME " + fieldName + ") "
         + "VALUES ("  + runId + ", ? ," + user.getSeqId() + ", ? " + fieldVal + ")";
       try {
         stm4 = conn.prepareStatement(query2);
         stm4.setString(1, runName);
         stm4.setTimestamp(2, time);
         Set<Integer> keys = valueMap.keySet();
         for (int b : keys) {
           String itemData = (String)valueMap.get(b);
           stm4.setString(b, itemData);
         }
         stm4.executeUpdate();
       } catch(Exception ex) {
         throw ex;
       } finally {
         T9DBUtility.close(stm4, null, null); 
       }
     }
     
     
    return runId; 
  }
  public int createNewWork(Connection conn , int flowId , T9Person user, Map map) throws Exception{
    //查询是否为重名的
    T9FlowRunLogic frl = new T9FlowRunLogic();
    //如果没有指定runName
    String runName = null;
    T9FlowTypeLogic flowTypeLogic = new T9FlowTypeLogic();
    T9FlowType flowType = flowTypeLogic.getFlowTypeById(flowId , conn);
    if(runName == null){
      runName = frl.getRunName(flowType, user , conn , false ) ;
    }
    Set<String> ss = map.keySet();
    for (String key : ss) {
      String[] aItem = (String[])map.get(key);
      String itemData =  "";
      if (aItem != null) {
        itemData =  aItem[0];
      }
      runName = runName.replace("${"+ key +"}", itemData);
    }
    
    //重名
    if(!frl.isExist(runName, flowId , conn)){ 
      int runId = frl.createNewWork(user, flowType, runName , conn);
      Map queryItem = new HashMap();
      queryItem.put("FORM_ID", flowType.getFormSeqId());
      T9ORM orm = new T9ORM();
      List<T9FlowFormItem> list = orm.loadListSingle(conn, T9FlowFormItem.class, queryItem);
      String dataField = "";
      int count = 0;
      Map dataMap = new HashMap();
      
      for(T9FlowFormItem tmp : list){
        int itemId = tmp.getItemId();
        String[] aItem = (String[])map.get(tmp.getTitle());
        String itemData =  "";
        if (aItem != null) {
          itemData =  aItem[0];
          if (!T9WorkFlowUtility.isSave2DataTable()){
            Map queryMap = new HashMap();
            queryMap.put("RUN_ID", runId);
            queryMap.put("ITEM_ID", itemId);
            T9FlowRunData flowRunData = (T9FlowRunData) orm.loadObjSingle(conn, T9FlowRunData.class, queryMap);
            if(flowRunData != null){
              flowRunData.setItemData((itemData == null ? "" : itemData));
              orm.updateSingle(conn, flowRunData);
            }else{
              flowRunData =  new T9FlowRunData();
              flowRunData.setItemId(itemId);
              flowRunData.setRunId(runId);
              flowRunData.setItemData((itemData == null ? "" : itemData));
              orm.saveSingle(conn, flowRunData);
            }
          } else {
            String t = "DATA_" + itemId;
            dataField += t + "=?,";
            count++;
            dataMap.put(count, itemData);
          }
        }
      }
      
      if (T9WorkFlowUtility.isSave2DataTable()){
        dataField = T9WorkFlowUtility.getOutOfTail(dataField);
        if (!T9Utility.isNullorEmpty(dataField)) {
          T9FormVersionLogic lo = new T9FormVersionLogic();
          T9FlowRunUtility logic = new T9FlowRunUtility();
          
          int versionNo = lo.getVersionNo(conn, runId);
          int formId = logic.getFormId(conn, flowId);
          int formSeqId = lo.getFormSeqId(conn, versionNo, formId);
          String tableName = T9WorkflowSave2DataTableLogic.FORM_DATA_TABLE_PRE+ flowId  + "_" + formSeqId;
          
          String update = "update " +tableName + " set "
             + dataField 
             + " where RUN_ID=" + runId;
          PreparedStatement stm4 = null;
          try {
            stm4 = conn.prepareStatement(update);
            Set<Integer> keys = dataMap.keySet();
            for (int b : keys) {
              String itemData = (String)dataMap.get(b);
              stm4.setString(b, itemData);
            }
            stm4.executeUpdate();
          } catch(Exception ex) {
            throw ex;
          } finally {
            T9DBUtility.close(stm4, null, null); 
          }
        }
      }
      
      
      return runId;
    } else {
      return 0;
    }
  }
  public int createNewWork(Connection conn , int flowId , T9Person user, String attachmentId , String attachmentName, Map map) throws Exception{
  //查询是否为重名的
    T9FlowRunLogic frl = new T9FlowRunLogic();
    //如果没有指定runName
    String runName = null;
    T9FlowTypeLogic flowTypeLogic = new T9FlowTypeLogic();
    T9FlowType flowType = flowTypeLogic.getFlowTypeById(flowId , conn);
    if(runName == null){
      runName = frl.getRunName(flowType, user , conn , false ) ;
    }
    //重名
    if(!frl.isExist(runName, flowId , conn)){ 
      int runId = frl.createNewWork(user, flowType, runName , conn );
      Map queryItem = new HashMap();
      queryItem.put("FORM_ID", flowType.getFormSeqId());
      T9ORM orm = new T9ORM();
      List<T9FlowFormItem> list = orm.loadListSingle(conn, T9FlowFormItem.class, queryItem);
      String dataField = "";
      int count = 0;
      Map dataMap = new HashMap();
      for(T9FlowFormItem tmp : list){
        int itemId = tmp.getItemId();
        String itemData =  (String)map.get(tmp.getTitle());
        
        if (!T9WorkFlowUtility.isSave2DataTable()){
          if (itemData != null) {
            Map queryMap = new HashMap();
            queryMap.put("RUN_ID", runId);
            queryMap.put("ITEM_ID", itemId);
            T9FlowRunData flowRunData = (T9FlowRunData) orm.loadObjSingle(conn, T9FlowRunData.class, queryMap);
            if(flowRunData != null){
              flowRunData.setItemData((itemData == null ? "" : itemData));
              orm.updateSingle(conn, flowRunData);
            }else{
              flowRunData =  new T9FlowRunData();
              flowRunData.setItemId(itemId);
              flowRunData.setRunId(runId);
              flowRunData.setItemData((itemData == null ? "" : itemData));
              orm.saveSingle(conn, flowRunData);
            }
          }
        }  else {
          String t = "DATA_" + itemId;
          dataField += t + "=?,";
          count++;
          dataMap.put(count, itemData);
        }
      }
      if (T9WorkFlowUtility.isSave2DataTable()){
        dataField = T9WorkFlowUtility.getOutOfTail(dataField);
        if (!T9Utility.isNullorEmpty(dataField)) {
          T9FormVersionLogic lo = new T9FormVersionLogic();
          T9FlowRunUtility logic = new T9FlowRunUtility();
          
          int versionNo = lo.getVersionNo(conn, runId);
          int formId = logic.getFormId(conn, flowId);
          int formSeqId = lo.getFormSeqId(conn, versionNo, formId);
          String tableName = T9WorkflowSave2DataTableLogic.FORM_DATA_TABLE_PRE+ flowId  + "_" + formSeqId;
          
          String update = "update " + tableName + " set "
             + dataField 
             + " where RUN_ID=" + runId;
          PreparedStatement stm4 = null;
          try {
            stm4 = conn.prepareStatement(update);
            Set<Integer> keys = dataMap.keySet();
            for (int b : keys) {
              String itemData = (String)dataMap.get(b);
              stm4.setString(b, itemData);
            }
            stm4.executeUpdate();
          } catch(Exception ex) {
            throw ex;
          } finally {
            T9DBUtility.close(stm4, null, null); 
          }
        }
      }
      
      if (!T9Utility.isNullorEmpty(attachmentId)) {
        String query = "update FLOW_RUN set "
          + " ATTACHMENT_ID = '" + attachmentId + "'"
          + " ,ATTACHMENT_NAME= ?"
          + " where RUN_ID = " + runId;
        PreparedStatement stm2 = null;
        try {
          stm2 = conn.prepareStatement(query);
          stm2.setString(1, attachmentName);
          stm2.executeUpdate();
        } catch(Exception ex) {
          throw ex;
        } finally {
          T9DBUtility.close(stm2, null, null); 
        }
      }
      return runId;
    } else {
      return 0;
    }
  }
  
  public int getFormId(Connection conn , int flowId) throws Exception {
    String query = "select form_seq_id from flow_type where seq_id=" + flowId;
    Statement stm = null; 
    ResultSet rs = null; 
    int formId = 0;
    try { 
      stm = conn.createStatement(); 
      rs = stm.executeQuery(query); 
      if (rs.next()){ 
        formId = rs.getInt("form_seq_id");
      } 
    } catch(Exception ex) { 
      throw ex; 
    } finally { 
      T9DBUtility.close(stm, rs, null); 
    } 
    return formId;
  }
  public T9FlowRunData getFlowRunData(Connection conn, int flowId , int runId , String title) throws Exception {
    int formId = this.getFormId(conn, flowId);
    T9FormVersionLogic lo = new T9FormVersionLogic();
    
    int versionNo = lo.getVersionNo(conn, runId);
    int formSeqId = lo.getFormSeqId(conn, versionNo, formId);
    int itemId = this.getItemId(conn, formSeqId, title);
    return this.getFlowRunData(conn, runId, itemId , flowId);
  }
  public T9FlowRunData getFlowRunData(Connection conn, int runId , int itemId , int flowId) throws Exception {
    T9FlowRunData flowRunData = null;
    if (!T9WorkFlowUtility.isSave2DataTable()){
      String query2 = "select SEQ_ID , ITEM_DATA from FLOW_RUN_DATA where RUN_ID=" + runId + " and item_id=" + itemId;
      Statement stm1 = null;
      ResultSet rs1 = null;
      try {
        stm1 = conn.createStatement();
        rs1 = stm1.executeQuery(query2);
        String itemData = "";
        if (rs1.next()) {
           itemData = rs1.getString("ITEM_DATA");
           int seqId = rs1.getInt("SEQ_ID");
           flowRunData = new T9FlowRunData();
           flowRunData.setItemId(itemId);
           flowRunData.setRunId(runId);
           flowRunData.setSeqId(seqId);
           flowRunData.setItemData(itemData);
           return flowRunData;
        } else {
          return null;
        }
      } catch (Exception ex) {
        throw ex;
      } finally {
        T9DBUtility.close(stm1, rs1, null);
      }
    } else {
      flowRunData = new T9FlowRunData();
      flowRunData.setItemId(itemId);
      flowRunData.setRunId(runId);
      String key = "DATA_" + itemId;
      
      
      T9FormVersionLogic lo = new T9FormVersionLogic();
      T9FlowRunUtility logic = new T9FlowRunUtility();
      
      int versionNo = lo.getVersionNo(conn, runId);
      int formId = logic.getFormId(conn, flowId);
      int formSeqId = lo.getFormSeqId(conn, versionNo, formId);
      String tableName = T9WorkflowSave2DataTableLogic.FORM_DATA_TABLE_PRE+ flowId  + "_" + formSeqId;
      
      
      String query ="select "+ key +" from " + tableName + " where RUN_ID = "+ runId;
      Statement stm1 = null; 
      ResultSet rs1 = null; 
      try { 
        stm1 = conn.createStatement(); 
        rs1 = stm1.executeQuery(query); 
        if (rs1.next()){ 
          String data = rs1.getString(key);
          flowRunData.setItemData(data);
        } 
      } catch(Exception ex) { 
        throw ex; 
      } finally { 
        T9DBUtility.close(stm1, rs1, null); 
      }
    }
    return flowRunData;
  }
  public String getData(Connection conn, int flowId , int runId , String title) throws Exception {
    String value = null;
    T9FlowRunData rd = this.getFlowRunData(conn, flowId, runId, title);
    if (rd != null) {
      value = rd.getItemData();
    }
    return value;
  }
  public int getItemId(Connection conn  , int formId , String title) throws Exception {
    String query1 = "select item_id from flow_form_item where form_id=" + formId + " and title = '" + title + "' ";
    Statement stm1 = null; 
    ResultSet rs1 = null; 
    int itemId = 0;
    try { 
      stm1 = conn.createStatement(); 
      rs1 = stm1.executeQuery(query1); 
      if (rs1.next()){ 
        itemId = rs1.getInt("item_id");
      } 
    } catch(Exception ex) { 
      throw ex; 
    } finally { 
      T9DBUtility.close(stm1, rs1, null); 
    }
    return itemId;
  }
  public String getField(Connection conn ,int flowId ,int formId , String valueField) throws Exception{
    if (formId == 0) {
      formId = this.getFormId(conn, flowId);
    }
    String[] ss = valueField.split(",");
    String value = "";
    for (String tmp : ss) {
      value += this.getItemId(conn, formId, tmp) + ",";
    }
    return value;
  }
  /**
   * 判断经办人是否存在
   * @param conn
   * @param runId
   * @param prcsId
   * @param flowPrcs
   * @param toId
   * @return
   * @throws Exception 
   */
  public boolean isExistUser(Connection conn , int runId , int prcsId , int flowPrcs , int toId) throws Exception {
    String query = "select TOP_FLAG,PARENT FROM FLOW_RUN_PRCS WHERE RUN_ID='"+runId+"' AND PRCS_ID='"+prcsId+"' and USER_ID='"+ toId +"'";
    if (flowPrcs > 0) {
      query += " and flow_prcs = '" + flowPrcs + "'";
    }
    Statement stm1 = null; 
    ResultSet rs1 = null; 
    try { 
      stm1 = conn.createStatement(); 
      rs1 = stm1.executeQuery(query); 
      if (rs1.next()){ 
        return true;
      } 
    } catch(Exception ex) { 
      throw ex; 
    } finally { 
      T9DBUtility.close(stm1, rs1, null); 
    }
    return false;
  }
  public String getRunNameById(int runId , Connection conn) throws Exception {
    String query = "select RUN_NAME from FLOW_RUN where RUN_ID = " + runId; 
    Statement stm = null; 
    ResultSet rs = null; 
    String runName = "";
    try { 
      stm = conn.createStatement(); 
      rs = stm.executeQuery(query); 
      if (rs.next()){ 
        runName = rs.getString("RUN_NAME");
      } 
    } catch(Exception ex) { 
      throw ex; 
    } finally { 
      T9DBUtility.close(stm, rs, null); 
    } 
    return runName;
  }
  
  public static Timestamp getPrcsDeliverTime(Connection conn , int runId , int prcsId) throws Exception {
    int prePrcsId = prcsId -1; 
    String query1 = "select DELIVER_TIME from FLOW_RUN_PRCS WHERE RUN_ID='"+runId+"' AND PRCS_ID='"+ prePrcsId +"' AND OP_FLAG='1'";
    Statement stm2 = null; 
    ResultSet rs2 = null; 
    try { 
      stm2 = conn.createStatement(); 
      rs2 = stm2.executeQuery(query1); 
      if (rs2.next()){ 
        return rs2.getTimestamp("DELIVER_TIME");
      } 
    } catch(Exception ex) { 
      throw ex; 
    } finally { 
      T9DBUtility.close(stm2, rs2, null); 
    }
    return null;
  }
  
  /**
   * 扫描所有流程的每一个步骤，把各个流程中，设置了超时时限的步骤信息放到数组$REMIND_ARRAY里
   * $REMIND_ARRAY形如：
   *  1=>1,2,|10,11,  
   *  2=>3,5|18,21,
   *  ……
   * @throws Exception 
   */
  public void setTimeOutFlag(Connection conn , String contextPath) throws Exception {
    Map remindPara = this.getParaValue(conn);
    String flowRemindBefore = (String)remindPara.get("FLOW_REMIND_BEFORE");
    String flowRemindAfter = (String)remindPara.get("FLOW_REMIND_AFTER");
    String mobile = (String)remindPara.get("FLOW_MOBILE_REMIND");
    //超时提醒（工作流参数设置）
    int bIndex = flowRemindBefore.length() - 1;
    int aIndex = flowRemindAfter.length() - 1;
    char cBeforeUnit = flowRemindBefore.charAt(bIndex);
    char cAfterUnit = flowRemindAfter.charAt(aIndex);
    String sBefore = flowRemindBefore.substring(0, bIndex);
    String sAfter = flowRemindAfter.substring(0, aIndex);
    int beforeUnit = this.getTimeUnit(cBeforeUnit);
    int afterUnit = this.getTimeUnit(cAfterUnit);
    int before = 0;
    int after = 0 ;
    if (T9Utility.isInteger(sBefore)) {
      before = Integer.parseInt(sBefore);
    }
    if (T9Utility.isInteger(sAfter)) {
      after = Integer.parseInt(sAfter);
    }
    Map remindMap = new HashMap();
    int flowIdOld = 0;
    String query = "select  FLOW_SEQ_ID AS FLOW_ID,PRCS_ID,TIME_OUT, TIME_OUT_TYPE from FLOW_PROCESS where (TIME_OUT<>'' and TIME_OUT is not null) ORDER BY FLOW_ID";
    Statement stm = null; 
    ResultSet rs = null; 
    String prcsStr = "";
    String timeOutStr = "";
    String timeOutTypeStr = "";
    try { 
      stm = conn.createStatement(); 
      rs = stm.executeQuery(query); 
      while (rs.next()){ 
        int flowId = rs.getInt("FLOW_ID");
        int prcsId = rs.getInt("PRCS_ID");
        String timeOut = rs.getString("TIME_OUT");
        String TIME_OUT_TYPE = rs.getString("TIME_OUT_TYPE");
        if (flowIdOld != 0 && flowIdOld != flowId) {
          if (prcsStr.endsWith(",")) {
            prcsStr = prcsStr.substring(0, prcsStr.length() - 1 );
          }
          if (timeOutStr.endsWith(",")) {
            timeOutStr = timeOutStr.substring(0, timeOutStr.length() - 1 );
          }
          if (timeOutTypeStr.endsWith(",")) {
            timeOutTypeStr = timeOutTypeStr.substring(0, timeOutTypeStr.length() - 1 );
          }
          
          if (!"".equals(prcsStr) && !"".equals(timeOutStr)) {
            String tmp = prcsStr + "|" + timeOutStr;
            remindMap.put(String.valueOf(flowIdOld), tmp);
          }
          prcsStr = timeOutStr = timeOutTypeStr = "";
        }
        prcsStr += prcsId + ",";
        timeOutStr += timeOut + ",";
        timeOutTypeStr += TIME_OUT_TYPE + ",";
        flowIdOld = flowId;
      } 
    } catch(Exception ex) { 
      throw ex; 
    } finally { 
      T9DBUtility.close(stm, rs, null); 
    } 
    if (!"".equals(prcsStr) && !"".equals(timeOutStr) && !"".equals(timeOutTypeStr)) {
      String tmp = prcsStr + "|" + timeOutStr + "|" + timeOutTypeStr;
      remindMap.put(String.valueOf(flowIdOld), tmp);
    }
    //为空时．返回
    if (remindMap.isEmpty()) {
      return ;
    }
    Set<String> keys = remindMap.keySet();
    for (String key : keys) {
      
      String value = (String)remindMap.get(key);
      String[] aValue = value.split("\\|");
      
      String[] aPrcs = aValue[0].split(",");
      String prcsArray = aValue[0];
      if (prcsArray.endsWith(",")) {
        prcsArray = prcsArray.substring(0 , prcsArray.length() -1);
      }
      String[] aTimeOut = aValue[1].split(",");
      String[] aTimeOutStr = aValue[2].split(",");
      
      Map prcsTimeOut = new HashMap();
      Map prcsTimeOutStr = new HashMap();
      for (int i = 0 ;i < aPrcs.length ; i ++) {
        String prcsId = aPrcs[i];
        String timeOut = aTimeOut[i];
        prcsTimeOut.put(prcsId, timeOut);
        prcsTimeOutStr.put(prcsId, aTimeOutStr[i]);
      }
      query =  "SELECT  a.RUN_ID,USER_ID,PRCS_FLAG,PRCS_ID,FLOW_PRCS,b.BEGIN_USER,RUN_NAME "
         + " from FLOW_RUN_PRCS  a,FLOW_RUN  b WHERE "
         + " a.RUN_ID=b.RUN_ID AND "
         + " b.FLOW_ID='"+key+"' AND "
         + "  a.FLOW_PRCS in (" + prcsArray + ") and  "
         + "  b.DEL_FLAG='0' AND  "
         + "  a.PRCS_FLAG in ('1','2') AND "
         + "  a.OP_FLAG='1'";
      
      Statement stm1 = null; 
      ResultSet rs1 = null; 
      try { 
        stm1 = conn.createStatement(); 
        rs1 = stm1.executeQuery(query); 
        while (rs1.next()) {
          int runId = rs1.getInt("RUN_ID");
          int userId = rs1.getInt("USER_ID");
          int prcsId = rs1.getInt("PRCS_ID");
          int flowPrcs = rs1.getInt("FLOW_PRCS");
          int beginUser = rs1.getInt("BEGIN_USER") ;
          String runName = rs1.getString("RUN_NAME");
          String timeOut = (String)prcsTimeOut.get(String.valueOf(prcsId));
          String timeOutStr2 = (String)prcsTimeOutStr.get(String.valueOf(prcsId));
          
          Timestamp beginTime = null;
          if (prcsId != 1 && "1".equals(timeOutStr2)) {
            beginTime = T9FlowRunUtility.getPrcsDeliverTime(conn, runId, prcsId);
          } else {
            String query1 = "select PRCS_TIME from FLOW_RUN_PRCS WHERE RUN_ID='"+runId+"' AND PRCS_ID='"+prcsId+"' AND OP_FLAG='1'";
            Statement stm2 = null; 
            ResultSet rs2 = null; 
            try { 
              stm2 = conn.createStatement(); 
              rs2 = stm2.executeQuery(query1); 
              if (rs2.next()){ 
                beginTime = rs2.getTimestamp("PRCS_TIME");
              } 
            } catch(Exception ex) { 
              throw ex; 
            } finally { 
              T9DBUtility.close(stm2, rs2, null); 
            }
          }
          beginTime = beginTime != null ? beginTime : new Timestamp(new Date().getTime());
          long timeUsed = new Date().getTime() - beginTime.getTime();
          String remindUrl = "/core/funcs/workflow/flowrun/list/inputform/index.jsp?runId=" + runId + "&prcsId=" + prcsId + "&flowPrcs=" + flowPrcs + "&flowId=" + key ;
          float iTimeOut = 0 ;
          if (!T9Utility.isNullorEmpty(timeOut)) {
            iTimeOut = Float.parseFloat(timeOut);
          }
          if (timeUsed > iTimeOut * 3600000 ) {
            if ((timeUsed - iTimeOut * 3600000) < after * afterUnit) {
              String smsContent = "您有待办工作已经超过办理时限，请火速办理!流水号:" + runId + " 工作名称:" + runName;
              this.remindUser(conn, smsContent, beginUser, ""+ userId, remindUrl);
              if (!T9Utility.isNullorEmpty(mobile) && "1".equals(mobile)) {
                T9MobileSms2Logic ms2l = new T9MobileSms2Logic(); 
                ms2l.remindByMobileSms(conn, ""+ userId,beginUser, smsContent, null);
              }
            }
            T9WorkFlowUtility.updateTable("FLOW_RUN_PRCS", "TIME_OUT_FLAG = '1'", "RUN_ID='"+runId+"' AND PRCS_ID='"+prcsId+"' AND FLOW_PRCS= '"+flowPrcs+"' and USER_ID='"+userId+"'", conn);
          } else {
            //预警提醒
             if ((timeUsed - iTimeOut * 3600000) > before * beforeUnit && timeUsed < iTimeOut * 3600000) {
               String smsContent = "您有待办工作即将超过办理时限，请迅速办理!流水号:" + runId + " 工作名称：" + runName;
               this.remindUser(conn, smsContent, beginUser, ""+ userId, remindUrl);
               if (!T9Utility.isNullorEmpty(mobile) && "1".equals(mobile)) {
                 T9MobileSms2Logic ms2l = new T9MobileSms2Logic(); 
                 ms2l.remindByMobileSms(conn, ""+ userId,beginUser, smsContent, null);
               }
             }
             T9WorkFlowUtility.updateTable("FLOW_RUN_PRCS", " TIME_OUT_FLAG = '0' ", "RUN_ID='"+runId+"' AND PRCS_ID='"+prcsId+"' AND FLOW_PRCS= '"+flowPrcs+"' and USER_ID='"+userId+"'", conn);
          }
        }
      } catch(Exception ex) { 
        throw ex; 
      } finally { 
        T9DBUtility.close(stm1, rs1, null); 
      }
    }
  }
  public void remindUser(Connection conn,String smsContent , int fromId , String toId , String remindUrl) throws Exception {
    T9SmsBack sb = new T9SmsBack();
    sb.setSmsType("7");
    sb.setContent(smsContent);
    sb.setFromId(fromId);
    sb.setToId(toId);
    sb.setRemindUrl(remindUrl);
    T9SmsUtil.smsBack(conn, sb);
  }
  public int getTimeUnit(char unit) {
    switch (unit) {
      case 'd':return 86400;
      case 'h':return 3600;
      case 'm':return 60;
      case 's':return 1;
    }
    return 1;
  }
  public Map getParaValue (Connection conn) throws Exception {
    Map map = new HashMap();
    String query = "select PARA_NAME, PARA_VALUE from SYS_PARA where PARA_NAME IN('FLOW_REMIND_BEFORE','FLOW_REMIND_AFTER','FLOW_MOBILE_REMIND')";
    Statement stm = null;
    ResultSet rs = null;
    try {
      stm = conn.createStatement();
      rs = stm.executeQuery(query);
      while (rs.next()){
        String paraName = rs.getString("PARA_NAME");
        String value = T9WorkFlowUtility.clob2String(rs.getClob("PARA_VALUE"));
        if (value == null) {
          value = "";
        }
        map.put(paraName, value);
      }
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, rs, null); 
    }
    return map;
  }
  public String getMetadataItem(int flowPrcs , int flowId , Connection conn) throws Exception {
    String query = "select metadata_item from flow_process where flow_seq_id=" + flowId + " and prcs_id=" + flowPrcs;
    Statement stm = null;
    ResultSet rs = null;
    try {
      stm = conn.createStatement();
      rs = stm.executeQuery(query);
      if (rs.next()){
        String metadataItem = rs.getString("metadata_item");
        if (metadataItem == null) {
          metadataItem = "";
        }
        return metadataItem;
      }
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, rs, null); 
    }
    return "";
  }
  public void getMetadata(int flowId , int runId  , int flowPrcs ,Connection conn) throws Exception{
    int formId = this.getFormId(conn, flowId);
    String metadataItem = this.getMetadataItem(flowPrcs, flowId, conn);
    String[] aMeta = metadataItem.split(",");
    String newMetaStr = "";
    for (String tmp : aMeta) {
      newMetaStr = "'" + tmp + "',";
    }
    if (newMetaStr.endsWith(",")) {
      newMetaStr = newMetaStr.substring(0, newMetaStr.length() - 1);
    }
    if ("".equals(newMetaStr)) {
      return ;
    }
    String query = "select metadata,item_id from flow_form_item where title in (" + newMetaStr + ") and form_id=" + formId;
    Statement stm = null;
    ResultSet rs = null;
    try {
      stm = conn.createStatement();
      rs = stm.executeQuery(query);
      while (rs.next()){
        int itemId = rs.getInt("item_id");
        String metadata = rs.getString("metadata");
        if (!T9Utility.isNullorEmpty(metadata)) {
          String itemData = this.getData(runId, itemId, conn , flowId);
          //元数据，提取.
        }
      }
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, rs, null); 
    }
  }
  public String getData(int runId , int itemId , Connection conn , int flowId) throws Exception {
    if (!T9WorkFlowUtility.isSave2DataTable()){
      String query = "select item_data from flow_run_data where item_id=" + itemId + " and run_id=" + runId;
      Statement stm = null;
      ResultSet rs = null;
      try {
        stm = conn.createStatement();
        rs = stm.executeQuery(query);
        if (rs.next()){
          Clob cItemData = rs.getClob("item_data");
          String itemData = T9WorkFlowUtility.clob2String(cItemData);
          return itemData;
        }
      } catch(Exception ex) {
        throw ex;
      } finally {
        T9DBUtility.close(stm, rs, null); 
      }
    } else {
      String key = "DATA_" + itemId;
      T9FormVersionLogic lo = new T9FormVersionLogic();
      T9FlowRunUtility logic = new T9FlowRunUtility();
      
      int versionNo = lo.getVersionNo(conn, runId);
      int formId = logic.getFormId(conn, flowId);
      int formSeqId = lo.getFormSeqId(conn, versionNo, formId);
      String tableName = T9WorkflowSave2DataTableLogic.FORM_DATA_TABLE_PRE+ flowId  + "_" + formSeqId;
      
      String query ="select "+ key +" from " +tableName + " where RUN_ID = "+ runId;
      Statement stm1 = null; 
      ResultSet rs1 = null; 
      try { 
        stm1 = conn.createStatement(); 
        rs1 = stm1.executeQuery(query); 
        if (rs1.next()){ 
          String data = rs1.getString(key);
          return data;
        } 
      } catch(Exception ex) { 
        throw ex; 
      } finally { 
        T9DBUtility.close(stm1, rs1, null); 
      }
    }
    return "";
  }
  public static String updateModule(String module , HttpServletRequest map , Connection conn ,String moduleId) {
    String moduleClassName = "t9.user."+ module;
    try {
      Class moduleClass = Class.forName(moduleClassName);
      T9IWFModulePlugin moduleHandler = (T9IWFModulePlugin) moduleClass.newInstance();
      moduleId = moduleHandler.saveOrUpdate( map, conn, moduleId);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return moduleId;
  }
  public static void main(String[] args) {
//      T9Out.println("------------设置成功:" + min);
      setCondition(3000 , 2000) ;
   //   T9Out.println("------------设置成功:" + max);
   // }
  }
  public static void setSql(List<Map> list ,Connection conn) throws SQLException {
    for (Map map : list) {
      String prcsIn = (String)map.get("prcsIn");
      String prcsOut = (String)map.get("prcsOut");
      int seqId = (Integer)map.get("id");
      String sql = "UPDATE FLOW_PROCESS set PRCS_IN = ? , PRCS_OUT = ? where SEQ_ID = " + seqId;
      PreparedStatement stm2 = conn.prepareStatement(sql);
      stm2.setString(1, prcsIn);
      stm2.setString(2, prcsOut);
      int i = stm2.executeUpdate();
      if (i != 0) {
        T9Out.println("设置成功:" + seqId);
      }
    }
    conn.commit();
  }
  public static void setCondition(int max , int min) {
    Connection conn = null;
    try {
      Class.forName("oracle.jdbc.driver.OracleDriver");
      conn = DriverManager.getConnection("jdbc:oracle:thin:@192.168.0.120:1521:orcl","TD_OA5" , "test");
      Statement stm = null;
      ResultSet rs = null;
      String query = "select SEQ_ID , PRCS_IN , PRCS_OUT from FLOW_PROCESS where  seq_id < " + max + " and seq_id > " + min;
      List<Map> map =new ArrayList<Map>();
      try {
        stm = conn.createStatement();
        rs = stm.executeQuery(query);
        while (rs.next()){
          int seqId = rs.getInt("SEQ_ID");
          String prcsIn = rs.getString("PRCS_IN");
          String prcsOut = rs.getString("PRCS_OUT");
          boolean is = false;
          if (prcsIn != null) {
            prcsIn = prcsIn.replace("\n", ",");
            prcsIn = prcsIn.replace("\r", "");
            is = true;
          }
          if (prcsOut != null) {
            prcsOut = prcsOut.replace("\n", ",");
            prcsOut = prcsOut.replace("\r", "");
            is = true;
          }
          if (is) {
            Map map1 = new HashMap();
            map1.put("id", seqId);
            map1.put("prcsIn", prcsIn);
            map1.put("prcsOut", prcsOut);
            map.add(map1);
          }
        }
      } catch(Exception ex) {
        throw ex;
      } finally {
        T9DBUtility.close(stm, rs, null); 
      }
      setSql(map, conn);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        conn.close();
      } catch (SQLException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }
}             
