package t9.core.funcs.workflow.praser;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.workflow.data.T9FlowFormItem;
import t9.core.funcs.workflow.data.T9FlowRunData;
import t9.core.funcs.workflow.logic.T9FlowRunLogic;
import t9.core.funcs.workflow.logic.T9FormVersionLogic;
import t9.core.funcs.workflow.logic.T9WorkflowSave2DataTableLogic;
import t9.core.funcs.workflow.util.T9FlowRunUtility;
import t9.core.funcs.workflow.util.T9WorkFlowUtility;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;

public class T9PraseData2FormUtility {

  public List<T9FlowRunData> tableData2FlowRunData(Connection conn, int flowId, int runId,
      List<T9FlowFormItem> itemList) throws Exception {
    // TODO Auto-generated method stub
    List<T9FlowRunData>  frdList = new ArrayList<T9FlowRunData>();
    T9FormVersionLogic lo = new T9FormVersionLogic();
    T9FlowRunUtility logic = new T9FlowRunUtility();
    int versionNo = lo.getVersionNo(conn, runId);
    int formId = logic.getFormId(conn, flowId);
    int formSeqId = lo.getFormSeqId(conn, versionNo, formId);
    String tableName = T9WorkflowSave2DataTableLogic.FORM_DATA_TABLE_PRE+ flowId  + "_" + formSeqId;
    
    
    
    String query = "select * from " + tableName  + " where RUN_ID =" + runId;
    Statement stm = null;
    ResultSet rs = null;
    try {
      stm = conn.createStatement();
      rs =  stm.executeQuery(query);
      if (rs.next()) {
        for (T9FlowFormItem item : itemList) {
          String clazz = item.getClazz();
          if ("DATE".equals(clazz) || "USER".equals(clazz)) {
            continue;
          }
          T9FlowRunData rd = new T9FlowRunData();
          rd.setRunId(runId);
          rd.setItemData(rs.getString("DATA_" + item.getItemId()));
          rd.setItemId(item.getItemId());
          frdList.add(rd);
        }
      } 
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, rs, null); 
    }
    return frdList;
  }
  public static String getRealValue(List<T9FlowRunData> frdList, T9FlowFormItem item){
    String realValue = "";
    for (T9FlowRunData flowRunData : frdList) {
      if (item.getItemId() == flowRunData.getItemId()) {
        //$ITEM_VALUE = str_replace(array('"','<','>'),array("&quot;","&lt;","&gt;"),$ITEM_VALUE);
        //可能会进行以上语句的处理
        if(flowRunData.getItemData() != null){
          realValue = flowRunData.getItemData();
        }
      }
    }
    return realValue;
  }
  /**
   * sql宏控件-替换函数-工作流运行的时候
   * @param conn
   * @param user
   * @param dataStr
   * @param runId
   * @return
   * @throws Exception
   */
  public static String replaceSql(Connection conn , T9Person user , String dataStr , int runId ,List<T9FlowFormItem>  fiList , List<T9FlowRunData> frdList) throws Exception {
    dataStr = replaceSql(conn , user, dataStr);
    dataStr  = dataStr.replaceAll("\\[SYS_RUN_ID\\]", String.valueOf(runId));
    for (T9FlowFormItem fi : fiList) {
      String name = fi.getTitle();
      String value = getRealValue(frdList , fi);
      dataStr = dataStr.replace("["+ name +"]", value);
    }
    return dataStr; 
  }
  /**
   * sql宏控件-替换函数-表单预览
   * @param conn
   * @param user
   * @param dataStr
   * @return
   * @throws Exception
   */
  public static String replaceSql(Connection conn , T9Person user , String dataStr   ) throws Exception {
    String  query ="select PRIV_NO from USER_PRIV where SEQ_ID=" + user.getUserPriv();
    Statement stm = null;
    ResultSet rs = null ;
    int loginPrivNo = 0 ;
    try {
      stm = conn.createStatement();
      rs = stm.executeQuery(query);
      if (rs.next()) {
        loginPrivNo = rs.getInt("PRIV_NO");
      }
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, rs, null); 
    }
    dataStr  = dataStr.replaceAll("`", "'");
    dataStr  = dataStr.replace("》", ">");
    
    dataStr  = dataStr.replaceAll("&#13;&#10;", " ");
    dataStr  = dataStr.replaceAll("\\[SYS_USER_ID\\]", String.valueOf(user.getSeqId()));
    dataStr  = dataStr.replaceAll("\\[SYS_DEPT_ID\\]", String.valueOf(user.getDeptId()));
    dataStr  = dataStr.replaceAll("\\[SYS_PRIV_ID\\]", String.valueOf(user.getUserPriv()));
    dataStr = dataStr.replaceAll("\\[SYS_PRIV_NO\\]",String.valueOf(loginPrivNo));
    return dataStr; 
  }
  public static String mobileSeal(T9FlowFormItem item, List<T9FlowFormItem> itemList , String value , int flowId , int runId) {
    // TODO Auto-generated method stub
      String itemCheck = "";
      String datafld = item.getDatafld();
      String ids = "";
      for (T9FlowFormItem item2 : itemList) {
        String title2 = item2.getTitle();
        String clazz2 = item2.getClazz();
        int itemId2 = item2.getItemId();
        if ("DATE".equals(clazz2) || "USER".equals(clazz2)) {
          continue;
        }
        if (T9WorkFlowUtility.findId(datafld, title2)) {
          itemCheck += "DATA_" + itemId2 + ",";
          ids += itemId2 + ",";
        }
      }
      
        
      String sealImgSrc = "/t9/t9/mobile/workflow/act/T9SealDataShowAct/data.act?FLOW_ID="+flowId+"&RUN_ID="+runId+"&ITEM_ID="+item.getItemId()+"&CHECK_FIELD="+ids;
      
      String out = "";
      if(!T9Utility.isNullorEmpty(value) && !value.equals(item.getValue()))
      {
        out = "<div style=''>";
        out += "<img style='width:200px;height:200px;' src='"+sealImgSrc+"' />";
        out += "<input type=\"text\" name='"+ item.getName() +"'  id='"+ item.getName() +"' value='"+value+"' style='display:none' />";
        out += "</div>";
      }
    return out;
  }
}
