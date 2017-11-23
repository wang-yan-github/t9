package t9.core.funcs.workflow.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.workflow.data.T9FlowFormItem;
import t9.core.funcs.workflow.data.T9FlowFormType;
import t9.core.funcs.workflow.data.T9FlowRun;
import t9.core.funcs.workflow.data.T9FlowRunData;
import t9.core.funcs.workflow.data.T9FlowType;
import t9.core.funcs.workflow.praser.T9PraseData2FormEdit;
import t9.core.funcs.workflow.util.T9FlowRunUtility;
import t9.core.funcs.workflow.util.T9PrcsRoleUtility;
import t9.core.funcs.workflow.util.T9WorkFlowUtility;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
public class T9FormEditLogic {
  /**
   * 取得编辑界面的相关信息
   * @param loginUser
   * @param runId
   * @param remoteAddr
   * @param dbConn
   * @return
   * @throws Exception 
   */
  public String getEditMsg(T9Person user, int runId, String ip,
      Connection conn , String imgPath) throws Exception {
    T9ORM orm = new T9ORM();
    T9FlowTypeLogic flowTypeLogic =  new T9FlowTypeLogic();
    T9FlowRunLogic run =  new T9FlowRunLogic();
    T9FlowRun flowRun = run.getFlowRunByRunId(runId , conn);
    T9FlowType flowType = flowTypeLogic.getFlowTypeById(flowRun.getFlowId() , conn);
    //查出表单
    T9FormVersionLogic logic5  = new T9FormVersionLogic();
    int formId = logic5.getFormSeqId(conn, flowRun.getFormVersion(), flowType.getFormSeqId());
    T9FlowFormType fft = (T9FlowFormType) orm.loadObjSingle(conn, T9FlowFormType.class, formId);
    //查询表单字段信息
    Map formItemQuery = new HashMap();
    
    formItemQuery.put("FORM_ID", formId);
    List<T9FlowFormItem> list = orm.loadListSingle(conn, T9FlowFormItem.class , formItemQuery);
    Map runDataQuery = new HashMap();
    runDataQuery.put("RUN_ID", runId);
    List<T9FlowRunData> frdList = orm.loadListSingle(conn, T9FlowRunData.class , runDataQuery);
    
    //设置宏标记
    String formMsg = "";
    if (list.size() > 0) {
      String modelShort = run.analysisAutoFlag(flowRun, flowType, fft, conn,imgPath);
      T9PraseData2FormEdit pdf = new T9PraseData2FormEdit();
      formMsg  = pdf.parseForm(user
          , modelShort
          , flowType
          , frdList
          , list
          , ip
          , conn
          , runId);
      formMsg = formMsg.replaceAll("\'", "\\\\'");
      formMsg = formMsg.replaceAll("\\\n", "");
    }
    String js = (fft == null || fft.getScript() == null) ? "" : fft.getScript();
    String css = ( fft == null || fft.getCss() == null) ? "" : fft.getCss();
    js = js.replaceAll("\'", "\\\\'");
    js = js.replaceAll("[\n-\r]", "");
    css = css.replaceAll("\'", "\\\\'");
    css = css.replaceAll("[\n-\r]", "");
    
    String ff = flowType.getFlowType();
    String doc = flowType.getFlowDoc();
    StringBuffer sb = new StringBuffer();
    sb.append("{formMsg:'" + formMsg + "'");
    sb.append(",js:'" + js + "'");
    sb.append(",css:'" + css + "'");
    String runName = flowRun.getRunName();
    runName = T9WorkFlowUtility.getRunName(runName);
    sb.append(",runName:'" + runName + "'");
    sb.append(",flowType:" + flowType.getFlowType());
    sb.append(",flowDoc:" + flowType.getFlowDoc());
    sb.append("}");
    return sb.toString();
  }

  public boolean hasEditRight(int flowId, T9Person user , Connection conn) throws Exception {
    // TODO Auto-generated method stub
    if (user.isAdmin()) {
      return true;
    }
    String query = "select EDIT_PRIV from FLOW_TYPE where SEQ_ID=" + flowId;
    String editPriv = "";
    Statement stm = null;
    ResultSet rs = null;
    try {
      stm = conn.createStatement();
      rs = stm.executeQuery(query);
      if (rs.next()) {
        editPriv = rs.getString("EDIT_PRIV");
      }
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, rs, null); 
    }
    T9PrcsRoleUtility pr = new T9PrcsRoleUtility();
    boolean flag = pr.checkPriv(user, editPriv);
    return flag;
  }
  /**
   * 保存表单
   * @param user
   * @param flowId
   * @param runId
   * @param prcsId
   * @param flowPrcs
   * @param map
   * @return
   * @throws Exception 
   */
  public String saveFormData(T9Person user , int flowId 
      , int runId  , HttpServletRequest request, Connection conn) throws Exception{
    T9ORM orm = new T9ORM();
    T9FlowType flowType = (T9FlowType) orm.loadObjSingle(conn, T9FlowType.class, flowId);
    Map queryItem = new HashMap();
    
    T9FormVersionLogic lo = new T9FormVersionLogic();
    int versionNo = lo.getVersionNo(conn, runId);
    int formId = flowType.getFormSeqId();
    int formSeqId = lo.getFormSeqId(conn, versionNo, formId);
    
    queryItem.put("FORM_ID", formSeqId);
    List<T9FlowFormItem> list = orm.loadListSingle(conn, T9FlowFormItem.class, queryItem);
    String dataField = "";
    int count = 0;
    Map dataMap = new HashMap();
    for(T9FlowFormItem tmp : list){
      int itemId = tmp.getItemId();
      
      String itemData = request.getParameter("DATA_" + itemId);
      String clazz = tmp.getClazz();
      /*
      if ("MODULE".equals(clazz)) {
        String module = tmp.getValue();
        if (flowRunData != null) {
          itemData = flowRunData.getItemData();
        }
        itemData = T9FlowRunUtility.updateModule(module, request , conn, itemData);
      }*/
      if (itemData == null) {
        continue;
      }
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
      }   else {
        String t = "DATA_" + itemId;
        dataField += t + "=?,";
        count++;
        dataMap.put(count, itemData);
      }
    }
    if (T9WorkFlowUtility.isSave2DataTable()){
      dataField = T9WorkFlowUtility.getOutOfTail(dataField);
      if (!T9Utility.isNullorEmpty(dataField)) {
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
    return null;
  }
  
}
