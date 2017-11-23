package t9.core.funcs.doc.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.doc.data.T9DocFlowType;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;


public class T9FlowHookUtility {
  private String PLUGINPACKAGE = "t9.plugins.workflow.system";
  public String runHook(Connection conn, T9Person user  ,Map dataMap,  String module ) throws Exception {
    String query = "select * from "+ T9WorkFlowConst.FLOW_HOOK +" where hmodule='"+ module +"' and status >0";
    Statement stm = null; 
    ResultSet rs = null; 
    Map arrayData = new HashMap();
    int runId = 0;
    try { 
      stm = conn.createStatement(); 
      rs = stm.executeQuery(query); 
      if (rs.next()){ 
        int seqId = rs.getInt("SEQ_ID");
        int flowId = rs.getInt("FLOW_ID");
        String hName = rs.getString("hname");
        String hmodule = rs.getString("hmodule");
        String map = rs.getString("map");
        map = T9Utility.null2Empty(map);
        map = map.trim();
        String[] maps = map.split(",");
        for (String s : maps) {
          if (!"".equals(s)) {
            String[] keyValue = s.split("=>");
            if (dataMap.containsKey(keyValue[0])) {
              arrayData.put(keyValue[1], dataMap.get(keyValue[0]));
            }
          }
        }
        String attachmentId = T9Utility.null2Empty((String)dataMap.get("ATTACHMENT_ID"));
        String attachmentName = T9Utility.null2Empty((String)dataMap.get("ATTACHMENT_NAME"));
        String moduleSrc =(String) dataMap.get("MODULE_SRC");
        String moduleDesc =(String) dataMap.get("MODULE_DESC");
        String keyId = (String)dataMap.get("KEY");
        String field = (String)dataMap.get("FIELD");
        String newAttachmentId = T9WorkFlowUtility.copyAttach(attachmentId, attachmentName, moduleSrc, moduleDesc);
        T9FlowRunUtility util = new T9FlowRunUtility();
        runId = util.createNewWork(conn, flowId, user, newAttachmentId, attachmentName, arrayData) ;
        String query1 = "insert into "+ T9WorkFlowConst.FLOW_RUN_HOOK +" (run_id,module,field,key_id) values('"+runId+"','"+module+"','"+field+"','"+keyId+"')";
        T9WorkFlowUtility.updateTableBySql(query1, conn);
        return T9WorkFlowConst.MODULE_CONTEXT_PATH + "/flowrun/list/turn/turnnext.jsp?runId=" + runId + "&flowId=" + flowId + "&prcsId=1&flowPrcs=1";
      } 
    } catch(Exception ex) { 
      throw ex; 
    } finally { 
      T9DBUtility.close(stm, rs, null); 
    } 
    return "";
  }
  public String runHookPlugin(Connection conn , int runId) throws Exception {
    String query = "SELECT * from "+ T9WorkFlowConst.FLOW_HOOK +" a left outer join "+ T9WorkFlowConst.FLOW_RUN_HOOK +" b on a.hmodule = b.module where b.run_id='"+runId+"'";
    Statement stm = null; 
    ResultSet rs = null; 
    Map arrayData = new HashMap();
    try { 
      stm = conn.createStatement(); 
      rs = stm.executeQuery(query); 
      if (rs.next()){ 
        int seqId = rs.getInt("SEQ_ID");
        String condition = rs.getString("condition");
        String conditionSet = rs.getString("condition_set");
        String plugin = rs.getString("plugin");
        
        T9TurnConditionUtility tu = new T9TurnConditionUtility();
        String notPass = "";
        String query2 = "SELECT FORM_SEQ_ID from "+ T9WorkFlowConst.FLOW_TYPE +" FLOW_TYPE, "+ T9WorkFlowConst.FLOW_RUN +" FLOW_RUN WHERE RUN_ID=" + runId + " AND FLOW_RUN.FLOW_ID = FLOW_TYPE.SEQ_ID";
        int formId = 0;
        Statement stm2 = null; 
        ResultSet rs2 = null; 
        try { 
          stm2 = conn.createStatement(); 
          rs2 = stm2.executeQuery(query2); 
          if (rs2.next()) {
            formId = rs2.getInt("FORM_SEQ_ID");
          }
        } catch(Exception ex) {
          throw ex;
        } finally {
          T9DBUtility.close(stm2, rs2, null); 
        }
        //------------------------------------------- 转出条件检查 ----------------------------------
        T9TurnConditionUtility turnUtility = new T9TurnConditionUtility();
        Map formData = turnUtility.getForm(formId, runId, conn);
        if (!T9Utility.isNullorEmpty(condition)) {
          notPass = tu.checkCondition(formData, condition, conditionSet);
        }
        Statement stm3 = null; 
        ResultSet rs3 = null; 
        String map = "";
        String query3 = "select map from "+ T9WorkFlowConst.FLOW_HOOK +" where SEQ_ID='"+seqId+"'";
        try { 
          stm3 = conn.createStatement(); 
          rs3 = stm3.executeQuery(query3); 
          if (rs3.next()) {
            map = T9Utility.null2Empty(rs3.getString("map"));
          }
        } catch(Exception ex) {
          throw ex;
        } finally {
          T9DBUtility.close(stm3, rs3, null); 
        }
        String[] mapArray = map.split(",");
        Map arrayHandler = new HashMap();
        for (String ss : mapArray) {
          if (!T9Utility.isNullorEmpty(ss)) {
            String[] items = ss.split("=>");
            if (formData.containsKey(items[1])) {
              arrayHandler.put(items[0], formData.get(items[1]));
            }
          }
        }
        boolean agree = false;
        if ("setOk".equals(notPass) || "".equals(notPass)) {
          agree = true;
        }
        T9IWFHookPlugin  pluginObj = null;
        if (plugin != null
            && !"".equals(plugin)) {
          String className = PLUGINPACKAGE + "." + plugin;
          try{
            pluginObj = (T9IWFHookPlugin) Class.forName(className).newInstance();
            if (pluginObj != null) {
              String str = pluginObj.execute( conn , runId  ,  arrayHandler , formData ,agree);
            }
          } catch(ClassNotFoundException ex){
          }
        }
      } 
    } catch(Exception ex) { 
      throw ex; 
    } finally { 
      T9DBUtility.close(stm, rs, null); 
    } 
    return "";
  }
  public int getFlowId(Connection conn , String field ,String keyId) throws Exception{
    String query  = "select run_id from "+ T9WorkFlowConst.FLOW_RUN_HOOK +" where field='"+ field + "' and key_id='"+keyId+"'";
    Statement stm = null; 
    ResultSet rs = null; 
    int runId = 0;
    try { 
      stm = conn.createStatement(); 
      rs = stm.executeQuery(query); 
      if (rs.next()){ 
        runId = rs.getInt("run_id");
      } 
    } catch(Exception ex) { 
      throw ex; 
    } finally { 
      T9DBUtility.close(stm, rs, null); 
    } 
    return runId;
  }
  public int isRunHook(Connection conn , String field ,String keyId) throws Exception{
    String query  = "select run_id from "+ T9WorkFlowConst.FLOW_RUN_HOOK +" where field='"+ field + "' and key_id='"+keyId+"'";
    Statement stm = null; 
    ResultSet rs = null; 
    int runId = 0;
    try { 
      stm = conn.createStatement(); 
      rs = stm.executeQuery(query); 
      if (rs.next()){ 
        runId = rs.getInt("run_id");
      } 
    } catch(Exception ex) { 
      throw ex; 
    } finally { 
      T9DBUtility.close(stm, rs, null); 
    } 
    return runId;
  }
  public void deleteHook(Connection dbConn , String field , String keyId) throws Exception {
    int runId = this.isRunHook(dbConn, field, keyId);
    if (runId != 0) {
      String delete = "delete from "+ T9WorkFlowConst.FLOW_RUN_HOOK +" where  field='"+field+"' and key_id='"+keyId+"'";
      T9WorkFlowUtility.updateTableBySql(delete, dbConn);
    }
  }
  public int getMax(Connection conn , String sql) throws Exception {
    Statement stm = null; 
    ResultSet rs = null; 
    int max = 0;
    try { 
      stm = conn.createStatement(); 
      rs = stm.executeQuery(sql); 
      if (rs.next()){ 
        max = rs.getInt(1);
      } 
    } catch(Exception ex) { 
      throw ex; 
    } finally { 
      T9DBUtility.close(stm, rs, null); 
    } 
    return max;
  }
}
