package t9.core.funcs.doc.logic;

import java.io.File;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.doc.util.T9WorkFlowConst;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.doc.data.T9DocFlowFormItem;
import t9.core.funcs.doc.data.T9DocFlowFormType;
import t9.core.funcs.doc.data.T9DocRun;
import t9.core.funcs.doc.data.T9DocFlowRunData;
import t9.core.funcs.doc.data.T9DocFlowRunPrcs;
import t9.core.funcs.doc.data.T9DocFlowType;
import t9.core.funcs.doc.util.T9FlowRunUtility;
import t9.core.funcs.doc.util.T9PraseData2FormEdit;
import t9.core.funcs.doc.util.T9PrcsRoleUtility;
import t9.core.funcs.doc.util.T9WorkFlowUtility;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.global.T9SysProps;
import t9.core.util.T9Guid;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.file.T9FileUploadForm;
import t9.core.util.file.T9FileUtility;
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
    T9DocRun flowRun = run.getFlowRunByRunId(runId , conn);
    T9DocFlowType flowType = flowTypeLogic.getFlowTypeById(flowRun.getFlowId() , conn);
    //查出表单
    T9DocFlowFormType fft = (T9DocFlowFormType) orm.loadObjSingle(conn, T9DocFlowFormType.class, flowType.getFormSeqId());
    //查询表单字段信息
    Map formItemQuery = new HashMap();
    formItemQuery.put("FORM_ID", flowType.getFormSeqId());
    List<T9DocFlowFormItem> list = orm.loadListSingle(conn, T9DocFlowFormItem.class , formItemQuery);
    Map runDataQuery = new HashMap();
    runDataQuery.put("RUN_ID", runId);
    List<T9DocFlowRunData> frdList = orm.loadListSingle(conn, T9DocFlowRunData.class , runDataQuery);
    
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
    String query = "select EDIT_PRIV from "+ T9WorkFlowConst.FLOW_TYPE +" where SEQ_ID=" + flowId;
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
    T9DocFlowType flowType = (T9DocFlowType) orm.loadObjSingle(conn, T9DocFlowType.class, flowId);
    Map queryItem = new HashMap();
    queryItem.put("FORM_ID", flowType.getFormSeqId());
    List<T9DocFlowFormItem> list = orm.loadListSingle(conn, T9DocFlowFormItem.class, queryItem);
    for(T9DocFlowFormItem tmp : list){
      int itemId = tmp.getItemId();
      Map queryMap = new HashMap();
      queryMap.put("RUN_ID", runId);
      queryMap.put("ITEM_ID", itemId);
      T9DocFlowRunData flowRunData = (T9DocFlowRunData) orm.loadObjSingle(conn, T9DocFlowRunData.class, queryMap);
      String itemData = request.getParameter("DATA_" + itemId);
      String clazz = tmp.getClazz();
      if ("MODULE".equals(clazz)) {
        String module = tmp.getValue();
        if (flowRunData != null) {
          itemData = flowRunData.getItemData();
        }
        itemData = T9FlowRunUtility.updateModule(module, request , conn, itemData);
      }
      if (itemData == null) {
        continue;
      }
      if(flowRunData != null){
        flowRunData.setItemData((itemData == null ? "" : itemData));
        orm.updateSingle(conn, flowRunData);
      }else{
        flowRunData =  new T9DocFlowRunData();
        flowRunData.setItemId(itemId);
        flowRunData.setRunId(runId);
        flowRunData.setItemData((itemData == null ? "" : itemData));
        orm.saveSingle(conn, flowRunData);
      }
    }
    return null;
  }
  
}
