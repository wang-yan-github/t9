package t9.core.funcs.doc.logic;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;
import java.util.Properties;

import t9.core.data.T9DbRecord;
import t9.core.data.T9PageDataList;
import t9.core.data.T9PageQueryParam;
import t9.core.funcs.doc.util.T9WorkFlowConst;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.doc.data.T9DocFlowHook;
import t9.core.load.T9PageLoader;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;

public class T9FlowHookLogic {
  public String configPath = T9WorkFlowConst.MODULE_CONTEXT_PATH + "/workflowUtility/flow_hook_config.properties";
  public String getHook(Connection conn,int hid , String webrootPath)  throws Exception{
    String query = "select * from "+ T9WorkFlowConst.FLOW_HOOK +" where SEQ_ID =  " + hid;
    Statement stm = null;
    ResultSet rs = null;
    StringBuffer sb = new StringBuffer();
    String module = "";
    int flowId = 0;
    try {
      stm = conn.createStatement();
      rs = stm.executeQuery(query);
      if (rs.next()){
        sb.append("{");
        module = rs.getString("HMODULE");
        sb.append("module:\"" + T9Utility.encodeSpecial(module) + "\"");
        sb.append(",status:\"" + rs.getString("STATUS") + "\"");
        sb.append(",name:\"" +  rs.getString("HNAME")  + "\"");
        sb.append(",desc:\"" +  rs.getString("HDESC") + "\"");
        flowId =  rs.getInt("FLOW_ID");
        sb.append(",flowId:" +flowId);
        sb.append(",map:\"" +  T9Utility.null2Empty(rs.getString("MAP")) + "\"");
        sb.append(",condition:\"" +  T9Utility.null2Empty(rs.getString("CONDITION")) + "\"");
        sb.append(",conditionSet:\"" + T9Utility.null2Empty(rs.getString("CONDITION_SET")) + "\"");
        sb.append(",system:\"" + rs.getString("SYSTEM")+ "\"");
        sb.append(",plugin:\"" + T9Utility.null2Empty(rs.getString("plugin"))+ "\"");
        sb.append("}");
      }
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, rs, null); 
    }
    String query2 = "SELECT FLOW_TYPE.SEQ_ID,FLOW_NAME,FLOW_TYPE from "+ T9WorkFlowConst.FLOW_TYPE +","+ T9WorkFlowConst.FLOW_SORT +" where FLOW_TYPE.FLOW_SORT=FLOW_SORT.SEQ_ID order by SORT_NO,FLOW_NO";
    Statement stm2 = null;
    ResultSet rs2 = null;
    sb.append(",flows:[");
    int count = 0 ;
    try {
      stm2 = conn.createStatement();
      rs2 = stm2.executeQuery(query2);
      while (rs2.next()){
        sb.append("{");
        sb.append("flowName:\"" + T9Utility.encodeSpecial(rs2.getString("FLOW_NAME")) + "\"");
        sb.append(",flowId:\"" + rs2.getString("SEQ_ID") + "\"");
        sb.append("},");
        count++ ;
      }
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm2, rs2, null); 
    }
    if (count > 0) {
      sb.deleteCharAt(sb.length() - 1);
    }
    sb.append("]");
    sb.append(",module:");
    Properties p = new Properties();
    p.load(new InputStreamReader(new FileInputStream(new File(webrootPath + this.configPath)) , "UTF-8"));
    String keyValue = p.getProperty(module);
    if (!T9Utility.isNullorEmpty(keyValue)) {
      sb.append(keyValue);
    } else {
      sb.append("{}");
    }
    
    String query3 = "select FORM_SEQ_ID FROM "+ T9WorkFlowConst.FLOW_TYPE +" WHERE SEQ_ID =" + flowId;
    T9FlowTypeLogic flowTypelogic = new T9FlowTypeLogic();
    T9FlowFormLogic ffLogic = new T9FlowFormLogic();
    int formId2 = flowTypelogic.getIntBySeq(query3, conn) ;
    String formItem = ffLogic.getTitle(conn, formId2);
    sb.append(",formItem:\"" +T9Utility.encodeSpecial(formItem) + "\"");
    return sb.toString();
  }
  public void addHookLogic(Connection conn,Map request,T9Person person)  throws Exception{

    
    String hmodule = request.get("hmodule") == null ? null : ((String[]) request.get("hmodule"))[0];
    String status = request.get("status") == null ? null : ((String[]) request.get("status"))[0];
    String hname = request.get("hname") == null ? null : ((String[]) request.get("hname"))[0];
    String hdesc = request.get("hdesc") == null ? null : ((String[]) request.get("hdesc"))[0];
    String plugin = request.get("plugin") == null ? null : ((String[]) request.get("plugin"))[0];

   
    try{
      T9DocFlowHook hook=new T9DocFlowHook();
      hook.setSystem("0");
      hook.setHmodule(hmodule);
      hook.setStatus(Integer.parseInt(status));
      hook.setHname(hname);
      hook.setHdesc(hdesc);
      hook.setPlugin(plugin);
      T9ORM orm=new T9ORM();
      orm.saveSingle(conn, hook);
    }catch(Exception e){
      throw e;
    }
    
  }
  
  public String getHookJsonLogic(Connection dbConn, Map request, T9Person person ,String webrootPath ) throws Exception {
    try {
      String sql = " select c1.seq_id,c1.hmodule, c1.hname, c1.hdesc, c1.flow_id, c1.map, c1.plugin,c1.status,c1.system,1 "
                 + " from "+ T9WorkFlowConst.FLOW_HOOK +" c1 "
                 + " ORDER BY c1.SEQ_ID desc ";
      T9PageQueryParam queryParam = (T9PageQueryParam) T9FOM.build(request);
      T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn, queryParam, sql);
      Properties p = new Properties();
      p.load(new InputStreamReader(new FileInputStream(new File(webrootPath + this.configPath)) , "UTF-8"));
      for (int i = 0 ;i < pageDataList.getRecordCnt() ; i++) {
        T9DbRecord record = pageDataList.getRecord(i);
        String hmodule = (String)record.getValueByName("hmodule");
        String keyValue = p.getProperty(hmodule);
        if (!T9Utility.isNullorEmpty(keyValue)) {
          record.addField("mapName", keyValue);
        } else {
          record.addField("mapName", "{}");
        }
      }
      return pageDataList.toJson();
    } catch (Exception e) {
      throw e;
    }
  }
  
  
  public String getFlowNameLogic(Connection dbConn,String flowId) throws Exception {
   Statement stmt=null;
   ResultSet rs=null;
   String flowName="";
    try {
      String sql ="select flow_name from "+ T9WorkFlowConst.FLOW_TYPE +" where seq_id='"+flowId+"'";
      stmt=dbConn.createStatement();
      rs=stmt.executeQuery(sql);
      if(rs.next()){
        flowName=rs.getString("flow_name");
      }  
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(stmt, rs, null);
    }
      return flowName;
  }
  
  public void deleteHookLogic(Connection dbConn,String seqId) throws Exception {
    Statement stmt=null;
     try {
        String sql="delete from "+ T9WorkFlowConst.FLOW_HOOK +" where seq_id='"+seqId+"'";
        stmt=dbConn.createStatement();
        stmt.executeUpdate(sql);
     } catch (Exception e) {
       throw e;
     } finally {
       T9DBUtility.close(stmt, null, null);
     }
    
   }
  
}
