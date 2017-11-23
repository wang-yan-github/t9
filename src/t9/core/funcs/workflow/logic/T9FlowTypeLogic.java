package t9.core.funcs.workflow.logic;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;

import t9.core.funcs.dept.logic.T9DeptLogic;
import t9.core.funcs.diary.logic.T9DiaryUtil;
import t9.core.funcs.news.data.T9NewsCont;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.person.logic.T9PersonLogic;
import t9.core.funcs.person.logic.T9UserPrivLogic;
import t9.core.funcs.workflow.data.T9FlowFormType;
import t9.core.funcs.workflow.data.T9FlowProcess;
import t9.core.funcs.workflow.data.T9FlowRun;
import t9.core.funcs.workflow.data.T9FlowRunFeedback;
import t9.core.funcs.workflow.data.T9FlowSort;
import t9.core.funcs.workflow.data.T9FlowType;
import t9.core.funcs.workflow.util.T9FlowRunUtility;
import t9.core.funcs.workflow.util.T9WorkFlowUtility;
import t9.core.funcs.workflow.util.sort.T9FlowTypeComparator;
import t9.core.global.T9SysProps;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.file.T9FileUploadForm;

public class T9FlowTypeLogic {
	public static String filePath = T9SysProps.getAttachPath() + File.separator + "news";
	//多文件上传时用到的----
	  /**
	   * 浮动菜单文件删除
	   * 
	   * @param dbConn
	   * @param attId
	   * @param attName
	   * @param contentId
	   * @throws Exception
	   */
	  public boolean delFloatFile(Connection dbConn, String attId, String attName, int seqId) throws Exception {
	    boolean updateFlag = false;
	    if (seqId != 0) {
	      T9ORM orm = new T9ORM();
	      T9FlowType flowType = (T9FlowType)orm.loadObjSingle(dbConn, T9FlowType.class, seqId);
	      String[] attIdArray = {};
	      String[] attNameArray = {};
	      String attachmentId = flowType.getAttachmentId();
	      String attachmentName = flowType.getAttachmentName();
	      //T9Out.println("attachmentId"+attachmentId+"--------attachmentName"+attachmentName);
	      if (!"".equals(attachmentId.trim()) && attachmentId != null && attachmentName != null) {
	        attIdArray = attachmentId.trim().split(",");
	        attNameArray = attachmentName.trim().split("\\*");
	      }
	      String attaId = "";
	      String attaName = "";
	  
	      for (int i = 0; i < attIdArray.length; i++) {
	        if (attId.equals(attIdArray[i])) {
	          continue;
	        }
	        attaId += attIdArray[i] + ",";
	        attaName += attNameArray[i] + "*";
	      }
	      //T9Out.println("attaId=="+attaId+"--------attaName=="+attaName);
	      flowType.setAttachmentId(attaId.trim());
	      flowType.setAttachmentName(attaName.trim());
	      orm.updateSingle(dbConn, flowType);
	    }
	  //处理文件
	    String[] tmp = attId.split("_");
	    String path = filePath + File.separator  + tmp[0] + File.separator + tmp[1] + "_" + attName;
	    File file = new File(path);
	    if(file.exists()){
	      file.delete();
	    } else {
	      //兼容老的数据
	      String path2 = filePath + File.separator  + tmp[0] + File.separator + tmp[1] + "." + attName;
	      File file2 = new File(path2);
	      if(file2.exists()){
	        file2.delete();
	      }
	    }
	    updateFlag=true;
	    return updateFlag;
	  }
	
  public ArrayList<T9FlowSort> getFlowSortList(Connection conn) throws Exception{
    T9ORM orm = new T9ORM();
    HashMap map = null;
    HashMap flowSortMap = null;
    ArrayList<T9FlowSort> flowSortList = flowSortList = new ArrayList<T9FlowSort>();
         
    List sortList = new ArrayList();
    sortList.add("flowSort");
    String[] filters = new String[]{"SORT_PARENT = 0 order by SORT_NO"};
    map = (HashMap)orm.loadDataSingle(conn, sortList, filters);
    List<HashMap> list = (List<HashMap>) map.get("FLOW_SORT");
    
    for(int i = 0; i < list.size(); i++) {
      HashMap mapList = list.get(i);
      T9FlowSort sort = null;
      if(mapList.get("haveChild").equals("1")) {
        sort = new T9FlowSort();
        sort.setSeqId((Integer)mapList.get("seqId"));
        sort.setSortNo((Integer)mapList.get("sortNo"));
        sort.setSortName((String)mapList.get("sortName"));
        sort.setSortParent((Integer)mapList.get("sortParent"));
        sort.setHaveChild((String)mapList.get("haveChild"));
        sort.setDeptId((Integer)mapList.get("deptId"));
        flowSortList.add(sort);
               
        int seqId = (Integer)mapList.get("seqId");
        String[] rules = new String[]{"SORT_PARENT = " + seqId + " order by SORT_NO"};
        
        List sortListNext = new ArrayList();
        sortListNext.add("flowSort");
        
        flowSortMap = (HashMap)orm.loadDataSingle(conn, sortListNext, rules);
        List<HashMap> sortMapList = (List<HashMap>) flowSortMap.get("FLOW_SORT");
        for(int j = 0; j < sortMapList.size(); j++) {
          HashMap mapListNext = sortMapList.get(j);
          sort = new T9FlowSort();
          sort.setSeqId((Integer)mapListNext.get("seqId"));
          sort.setSortNo((Integer)mapListNext.get("sortNo"));
          sort.setSortName((String)mapListNext.get("sortName"));
          sort.setSortParent((Integer)mapListNext.get("sortParent"));
          sort.setHaveChild((String)mapListNext.get("haveChild"));
          sort.setDeptId((Integer)mapListNext.get("deptId"));
          flowSortList.add(sort);
        }         
      }else {
        sort = new T9FlowSort();
        sort.setSeqId((Integer)mapList.get("seqId"));
        sort.setSortNo((Integer)mapList.get("sortNo"));
        sort.setSortName((String)mapList.get("sortName"));
        sort.setSortParent((Integer)mapList.get("sortParent"));
        sort.setHaveChild((String)mapList.get("haveChild"));
        sort.setDeptId((Integer)mapList.get("deptId"));
        flowSortList.add(sort);
      }
    }
    return  flowSortList;
  }
  
  public ArrayList<T9FlowFormType> getFlowFormTypeList(Connection conn) throws Exception{
    T9ORM orm = new T9ORM();
    ArrayList<T9FlowFormType> typeList = null;
    Map m = null;
    typeList = (ArrayList<T9FlowFormType>)orm.loadListSingle(conn, T9FlowFormType.class, m);
    return typeList;
  }
  
  public void saveFlowType(Connection conn, Map map) throws Exception{
    T9ORM orm = new T9ORM();
    orm.saveSingle(conn, "flowType", map);
  }
  public T9FlowType getFlowTypeById(int flowTypeId , Connection conn) throws Exception{
    T9ORM orm = new T9ORM();
    T9FlowType ft = (T9FlowType) orm.loadObjSingle(conn, T9FlowType.class, flowTypeId);
    return ft;
  }
  public List<T9FlowType> getFlowTypeList(Connection conn) throws Exception{
    Map m = null;
    T9ORM orm = new T9ORM();
    List<T9FlowType> list = orm.loadListSingle(conn, T9FlowType.class, m);
    return list;
  }
  public List<Map> getFlowTypeListByType(Connection conn , int type ) throws Exception{
    String query  = "select SEQ_ID , flow_name from FLOW_TYPE WHERE FLOW_TYPE = '"+ type +"' order by flow_no";
    Statement stm = null;
    ResultSet rs = null;
    List<Map> list = new ArrayList();
    try {
      stm = conn.createStatement();
      rs = stm.executeQuery(query);
      while (rs.next()) {
        int seqId = rs.getInt("SEQ_ID");
        String flowName = rs.getString("flow_name");
        Map ft = new HashMap();
        ft.put("seqId", seqId);
        ft.put("flowName", flowName);
        list.add(ft);
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, rs, null);
    }
    return list;
  }
  
  public List<T9FlowType> getFlowTypeList(int sortId , Connection conn) throws Exception{
    Map map = new HashMap();
    map.put("FLOW_SORT", sortId);
    T9ORM orm = new T9ORM();
    List<T9FlowType> list = orm.loadListSingle(conn, T9FlowType.class, map);
    Collections.sort(list,new T9FlowTypeComparator());
    return list;
  }
  public List<T9FlowType> getFlowTypeList(String sortId , Connection conn) throws Exception {
    List<T9FlowType> list =  new ArrayList();
    sortId = T9WorkFlowUtility.getOutOfTail(sortId);
    String tmp = " flow_SORT in (" + sortId + ") ";
    if (sortId.indexOf(",") == -1) {
      tmp = " flow_sort = " + sortId;
    } 
    String query  = "select SEQ_ID ,FORM_SEQ_ID,FLOW_NO, flow_name , flow_Type , NEW_USER ,query_User,query_User_Dept,manage_User,manage_User_Dept from FLOW_TYPE WHERE "+ tmp +" order by flow_no";
    Statement stm = null;
    ResultSet rs = null;
    try {
      stm = conn.createStatement();
      rs = stm.executeQuery(query);
      while (rs.next()) {
        T9FlowType ft = new T9FlowType();
        int seqId = rs.getInt("SEQ_ID");
        String flowName = rs.getString("flow_name");
        String flowType = rs.getString("flow_Type");
        String newUser = rs.getString("NEW_USER");
        String queryUser = rs.getString("query_User");
        String queryUserDept = rs.getString("query_User_Dept");
        String manageUser = rs.getString("manage_User");
        String manageUserDept = rs.getString("manage_User_Dept");
        int formSeqId = rs.getInt("FORM_SEQ_ID");
        ft.setFlowType(flowType);
        ft.setFormSeqId(formSeqId);
        ft.setFlowName(flowName);
        ft.setSeqId(seqId);
        ft.setNewUser(newUser);
        ft.setQueryUser(queryUser);
        ft.setQueryUserDept(queryUserDept);
        ft.setManageUser(manageUser);
        ft.setManageUserDept(manageUserDept);
        list.add(ft);
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, rs, null);
    }
    return list;
  }
  public List<T9FlowType> getFlowTypeListByDeptId(int sortId , T9Person user , Connection conn) throws Exception{
    Map map = new HashMap();
    map.put("FLOW_SORT", sortId);
    T9ORM orm = new T9ORM();
    List<T9FlowType> list = orm.loadListSingle(conn, T9FlowType.class, map);
    Collections.sort(list,new T9FlowTypeComparator());
    return list;
  }
  public void saveFlowType(T9FlowType ft , Connection conn) throws Exception{
    T9ORM orm = new T9ORM();
    orm.saveSingle(conn, ft);
  }
  public void updateFlowType(T9FlowType ft , Connection conn) throws Exception{
    T9ORM orm = new T9ORM();
    orm.updateSingle(conn, ft);
  }
  public void delFlowType(T9FlowType ft , Connection conn) throws Exception{
    T9ORM orm = new T9ORM();
    this.empty(ft.getSeqId(), conn);
    //删除委托规则
    //this.deleteTable("FLOW_RULE", "FLOW_ID=" + , conn)
    //要删 除其它表
    T9FlowProcessLogic fp = new T9FlowProcessLogic();
    fp.delFlowProcessByFlowId(ft.getSeqId() , conn);
    orm.deleteSingle(conn, ft);
    if (T9WorkFlowUtility.isSave2DataTable()) {
      T9WorkflowSave2DataTableLogic logic1 = new T9WorkflowSave2DataTableLogic();
      
      T9FlowFormLogic l = new T9FlowFormLogic();
      String formSeqIds = l.getIdByForm(conn, ft.getFormSeqId());
      String[] ss = formSeqIds.split(",");
      for (String s : ss) {
        if (!T9Utility.isNullorEmpty(s)) {
          String tableName = "" +T9WorkflowSave2DataTableLogic.FORM_DATA_TABLE_PRE + ft.getSeqId() + "_" + s;
          logic1.dropTable(conn , tableName);
        }
      }
    }
    
  }
  public int getWorkCountByFlowId(int flowId , Connection conn) throws Exception{
    String query = "SELECT COUNT(*) count FROM FLOW_RUN WHERE FLOW_ID = " + flowId;
    int count = this.getIntBySeq(query, conn) ;
    return count ;
  }
  public int getIntBySeq(String sql , Connection conn) throws Exception{
    int count = 0 ;
    Statement stm = null;
    ResultSet rs = null;
    try {
      stm = conn.createStatement();
      rs = stm.executeQuery(sql);
      if(rs.next()){
        count = rs.getInt(1);
      }
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, rs, null); 
    }
    return count ;
  }
  public int getDelWorkCountByFlowId(int flowId , Connection conn) throws Exception{
    String query = "SELECT COUNT(*) count FROM FLOW_RUN WHERE FLOW_ID = " + flowId +" and DEL_FLAG ='1'" ;
    int count = this.getIntBySeq(query, conn) ;
    return count ;
  }
  public String getNewPriv(int flowId , Connection conn) throws Exception {
    String query = "select "
      + " NEW_USER"
      + " FROM FLOW_TYPE WHERE"
      + " SEQ_ID=" + flowId;
    String newUser = "||";
    Statement stm = null;
    ResultSet rs = null;
    try {
      stm = conn.createStatement();
      rs = stm.executeQuery(query);
      if (rs.next() 
          && rs.getString("NEW_USER") != null) {
        newUser = rs.getString("NEW_USER");
      }
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, rs, null); 
    }
    
    String[] priv = newUser.split("\\|");
    
    String userId = "";
    String dept = "";
    String role = "";
    if (priv.length > 0) {
      userId = priv[0] ;
    }
    if (priv.length > 1) {
      dept = priv[1] ;
    }
    if (priv.length > 2) {
      role = priv[2] ;
    }
    StringBuffer sb = new StringBuffer();
    sb.append("{");
    sb.append("userId:'" + userId + "'");
    sb.append(",dept:'" + dept + "'");
    sb.append(",role:'" + role + "'");
    String deptName = "";
    if ("0".equals(dept)) {
      deptName = "全体部门";
    } else {
      T9DeptLogic deptLogic = new T9DeptLogic();
      deptName = deptLogic.getNameByIdStr(dept, conn);
    }
    T9PersonLogic personLogic = new T9PersonLogic();
    
    T9UserPrivLogic userPrivLogic = new T9UserPrivLogic();
    sb.append(",userDesc:'" + personLogic.getNameBySeqIdStr( userId, conn) + "'");
    sb.append(",deptDesc:'" +  deptName + "'");
    sb.append(",roleDesc:'" + userPrivLogic.getNameByIdStr(role , conn) + "'");
    sb.append("}");
    return sb.toString();
  }
  public void updateNewPriv(int flowId , String userId , String dept , String role , Connection conn ) throws Exception {
    String newPriv = userId + "|" + dept + "|" + role;
    String query = "update flow_type set new_user='" + newPriv + "' where seq_id=" + flowId; 
    Statement stm = null;
    try {
      stm = conn.createStatement();
      stm.executeUpdate(query);
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, null, null); 
    }
  }
 
  public void empty(int flowId , Connection conn) throws Exception {
    T9ORM orm = new T9ORM();
    Map map = new HashMap();
    map.put("FLOW_ID", flowId);
    List<T9FlowRun> flowRunList = orm.loadListSingle(conn, T9FlowRun.class, map);
    T9AttachmentLogic logic = new T9AttachmentLogic();
    //删除流程实例 
    for (T9FlowRun tmp : flowRunList) {
      String attachmentId = tmp.getAttachmentId();
      String attachmentName = tmp.getAttachmentName();
      logic.deleteAttachments(attachmentId, attachmentName);
       //删除会签意见
      Map mapFeedback = new HashMap();
      mapFeedback.put("RUN_ID", tmp.getRunId());
      List<T9FlowRunFeedback> feedbackList = orm.loadListSingle(conn, T9FlowRunFeedback.class, mapFeedback);
      for (T9FlowRunFeedback tmpF : feedbackList) {
        String attachmentId2 = tmp.getAttachmentId();
        String attachmentName2 = tmp.getAttachmentName();
        logic.deleteAttachments(attachmentId2, attachmentName2);
        orm.deleteSingle(conn, tmpF);
      }
      
       //删除流程数据
      T9WorkFlowUtility.deleteTable("FLOW_RUN_DATA", "RUN_ID=" + tmp.getRunId(), conn);
      //删除流程步骤
      T9WorkFlowUtility.deleteTable("FLOW_RUN_PRCS", "RUN_ID=" + tmp.getRunId(), conn);
      T9WorkFlowUtility.deleteTable("FLOW_RUN_LOG", "RUN_ID=" + tmp.getRunId(), conn);
      orm.deleteSingle(conn, tmp);
      
    }
    if (T9WorkFlowUtility.isSave2DataTable()) {
      T9WorkflowSave2DataTableLogic logic2 = new T9WorkflowSave2DataTableLogic();
      
      T9FlowFormLogic l = new T9FlowFormLogic();
      T9FlowRunUtility util = new T9FlowRunUtility();
      int formId = util.getFormId(conn, flowId);
      String formSeqIds = l.getIdByForm(conn, formId );
      String[] ss = formSeqIds.split(",");
      for (String s : ss) {
        if (!T9Utility.isNullorEmpty(s)) {
          String tableName = "" +T9WorkflowSave2DataTableLogic.FORM_DATA_TABLE_PRE + flowId + "_" + s;
          logic2.emptyTable(conn, tableName);
        }
      }
    }
    
  }
  
  public static void main(String[] args) throws Exception {
//    Connection dbConn = null;
//    dbConn = TestDbUtil.getConnection(false, "TD_OA");
//    String user = "jw";
//    T9FlowTypeLogic f = new T9FlowTypeLogic();
//    String value = f.getUserSeqId(user, dbConn);
//    //System.out.println(value);
  }
 /**
  * 校验
  * @param flowId
  * @param conn
  * @return [{isError:true , id:'prcsUserCheck',desc:['ddddd','ddddd','ddddd']}
  ,{isError:true , id:'prcsToCheck',desc:['ddddd','ddddd','ddddd']}
  ,{isError:false , id:'writableFieldCheck'}
  ,{isError:true , id:'condFormulaCheck',desc:['ddddd','ddddd','ddddd']}]
 * @throws Exception 
  */
  public String checkFlowType(int flowId , Connection conn) throws Exception {
    // TODO Auto-generated method stub
    StringBuffer sb = new StringBuffer();
    sb.append("[");
    sb.append(this.checkPrcsUser(flowId, conn) + ",");
    
    sb.append("]");
    return sb.toString();
  }
  /**
   * 校验经办人
   * @param flowId
   * @param conn
   * @return
   * @throws Exception 
   */
  public String checkPrcsUser(int flowId , Connection conn) throws Exception {
    StringBuffer sb = new StringBuffer();
    String noPrcsUserStr = "";
    String query = "select PRCS_ID " 
      + " , PRCS_NAME " 
      + "  from FLOW_PROCESS where  " 
      + " FLOW_SEQ_ID= " + flowId
      + " and (PRCS_USER='' or PRCS_USER = NULL ) " 
      + " and (PRCS_PRIV=''  or PRCS_PRIV = NULL )" 
      + " and (PRCS_DEPT=''  or PRCS_DEPT = NULL ) " 
      + " order by PRCS_ID";
    Statement stm = null;
    ResultSet rs = null;
    try {
      stm = conn.createStatement();
      rs = stm.executeQuery(query);
      while (rs.next()){
        int prcsId = rs.getInt("PRCS_ID");
        String prcsName = rs.getString("PRCS_NAME");
        noPrcsUserStr += "第" + prcsId + "步[" + prcsName +"],";
      }
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, rs, null); 
    }
    sb.append("{");
    sb.append("id:'prcsUserCheck'");
    if ("".equals(noPrcsUserStr)) {
      sb.append(",isError:false");
    } else {
      sb.append(",isError:true");
      sb.append(",desc:'" + noPrcsUserStr + "'");
    }
    sb.append("}");
    return sb.toString();
  }
  /**
   * 检查流程的每个步骤，查看下一步指定方面是否存在问题
   * @param flowId
   * @param conn
   * @throws Exception
   */
  public String checkNodePrcsTo(int flowId , Connection conn) throws Exception {
    StringBuffer sb = new StringBuffer();
  
    sb.append("}");
    return sb.toString();
  }
  public String checkWritableField(int flowId , Connection conn) throws Exception {
    StringBuffer sb = new StringBuffer();
    
    sb.append("}");
    return sb.toString();
  }
  public String getCloneMsg (int flowId , Connection conn) throws Exception {
    StringBuffer sb = new StringBuffer();
    String query = "select "
      + " FLOW_NO"
      + ",FLOW_NAME"
      + " from FLOW_TYPE where" 
      + " SEQ_ID=" + flowId ;
    
    String flowName = "";
    String flowNo = "";
    Statement stm = null;
    ResultSet rs = null;
    try {
      stm = conn.createStatement();
      rs = stm.executeQuery(query);
      if (rs.next()){
        flowName = rs.getString("FLOW_NAME");
        flowNo = rs.getString("FLOW_NO");
      }
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, rs, null); 
    }
    sb.append("{");
    sb.append("flowName:'" + flowName + "'");
    sb.append(",flowNo:'" + flowNo + "'");
    sb.append("}");
    return sb.toString();
  }
  public void clone(int flowId , String flowName , String sFlowNo , Connection conn) throws Exception {
    T9ORM orm = new T9ORM();
    T9FlowType flowType = (T9FlowType) orm.loadObjSingle(conn, T9FlowType.class, flowId);
    flowType.setSeqId(0);
    flowType.setFlowName(flowName);
    int flowNo = 0 ;
    if (sFlowNo != null && !"".equals(sFlowNo)) {
      flowNo = Integer.parseInt(sFlowNo);
    }
    flowType.setFlowNo(flowNo);
    orm.saveSingle(conn, flowType);
    
    int flowSeqId = 0;
    String query = "select max(SEQ_ID) as max from FLOW_TYPE";
    Statement stm = null;
    ResultSet rs = null;
    try {
      stm = conn.createStatement();
      rs = stm.executeQuery(query);
      if (rs.next()){
        flowSeqId = rs.getInt("max");
      }
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, rs, null); 
    }
    if ("1".equals(flowType.getFlowType())) {
      T9FlowProcessLogic logic = new T9FlowProcessLogic();
      List<T9FlowProcess> list = logic.getFlowProcessByFlowId(flowId, conn);
      for (T9FlowProcess fp : list) {
        fp.setSeqId(0);
        fp.setFlowSeqId(flowSeqId);
        orm.saveSingle(conn, fp);
      }
    }
    if (T9WorkFlowUtility.isSave2DataTable()) {
      String tableName = T9WorkflowSave2DataTableLogic.FORM_DATA_TABLE_PRE + flowSeqId + "_" + flowType.getFormSeqId() ;
      T9WorkflowSave2DataTableLogic logic = new T9WorkflowSave2DataTableLogic();
      logic.createTable(conn, tableName, flowType.getFormSeqId() + "");
    }
  }
  /**
   * 取得对应formId的表单名
   * @param formId
   * @param conn
   * @return
   * @throws Exception
   */
  public String getFormName(int formId , Connection conn) throws Exception {
    String formName = "";
    Statement stm = null;
    ResultSet rs = null;
    try {
      stm = conn.createStatement();
      String query = "select FORM_NAME FROM FLOW_FORM_TYPE WHERE SEQ_ID=" + formId;
      rs = stm.executeQuery(query);
      if (rs.next()){
        formName = rs.getString("FORM_NAME");
      }
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, rs, null); 
    }
    return formName;
  }
  /**
   * 取得对应FlowType的名
   * @param formId
   * @param conn
   * @return
   * @throws Exception
   */
  public String getFlowTypeName(int flowId , Connection conn) throws Exception {
    String flowName = "";
    Statement stm = null;
    ResultSet rs = null;
    try {
      stm = conn.createStatement();
      String query = "select FLOW_NAME FROM FLOW_TYPE WHERE SEQ_ID=" + flowId;
      rs = stm.executeQuery(query);
      if (rs.next()){
        flowName = rs.getString("FLOW_NAME");
      }
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, rs, null); 
    }
    return flowName;
  }
  /**
   * 取得流程相关信息，返回一个xml形式的字符串
   * @param flowId
   * @param conn
   * @return
   * @throws Exception
   */
  public String getFlowTypeMsg(int flowId , Connection conn) throws Exception {
    StringBuffer sb = new StringBuffer();
    String query = "select * from FLOW_TYPE where SEQ_ID=" + flowId;
    Statement stm = null;
    ResultSet rs = null;
    try {
      stm = conn.createStatement();
      rs = stm.executeQuery(query);
      if (rs.next()){
        sb.append("<BaseInfo>\r\n");
        ResultSetMetaData rsmd =  rs.getMetaData();
        for (int i = 1 ; i <= rsmd.getColumnCount() ; i ++ ) {
          String value = rs.getString(i);
          if (value == null) {
            value = ""; 
          }
          String colName = rsmd.getColumnName(i);
          if ("SEQ_ID".equals(colName)) {
            colName = "FLOW_ID";
          }
          if ("FORM_SEQ_ID".equals(colName)) {
            colName = "FORM_ID";
          }
          sb.append("<");
          sb.append(colName);
          sb.append(">");
          sb.append("<![CDATA[");
          sb.append(value);
          sb.append("]]></");
          sb.append(colName);
          sb.append(">\r\n");
        }
        sb.append("</BaseInfo>\r\n");
      }
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, rs, null); 
    }
    return sb.toString();
  }
  /**
   * 取得相关步骤的信息
   * @param flowId
   * @param conn
   * @return
   * @throws Exception
   */
  public String getFlowProcMsg(int flowId , Connection conn) throws Exception {
    StringBuffer sb = new StringBuffer();
    String query = "select * from FLOW_PROCESS where FLOW_SEQ_ID=" + flowId + " order by PRCS_ID";
    Statement stm = null;
    ResultSet rs = null;
    try {
      stm = conn.createStatement();
      rs = stm.executeQuery(query);
      while (rs.next()){
        sb.append("<Process>\r\n");
        ResultSetMetaData rsmd =  rs.getMetaData();
        for (int i = 1 ; i <= rsmd.getColumnCount() ; i ++ ) {
          String value = rs.getString(i);
          if (value == null) {
            value = ""; 
          }
          String colName = rsmd.getColumnName(i);
          if ("SEQ_ID".equals(colName)) {
            colName = "ID";
          }
          if ("FLOW_SEQ_ID".equals(colName)) {
            colName = "FLOW_ID";
          }
          sb.append("<");
          sb.append(colName);
          if ("AUTO_USER".equals(colName)) {
            sb.append(" isNotPerson='1'");
          }
          sb.append(">");
          sb.append("<![CDATA[");
          if ("CONDITION_DESC".equals(colName)) {
            value = value.replace("\n", "&#13;");
          }
          sb.append(value);
          sb.append("]]></");
          sb.append(colName);
          sb.append(">\r\n");
        }
        sb.append("</Process>\r\n");
      }
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, rs, null); 
    }
    return sb.toString();
  }
  public void importFlow(Element root , int flowId , boolean isUserOn , Connection conn) throws Exception {
    Element flowMsg = root.element("BaseInfo");
    String userStr = "MANAGE_USER,QUERY_USER,NEW_USER,MANAGE_USER_DEPT,QUERY_USER_DEPT,EDIT_PRIV,DEPT_ID,PRCS_USER,PRCS_DEPT,PRCS_PRIV,AUTO_USER_OP,AUTO_USER,MAIL_TO";
    String useUserStr = "PRCS_USER,AUTO_USER_OP,AUTO_USER,MAIL_TO";
    String privStr = "MANAGE_USER,QUERY_USER,NEW_USER,MANAGE_USER_DEPT,QUERY_USER_DEPT,EDIT_PRIV";
    List<Element> iterator = flowMsg.elements();
    String query = "update FLOW_TYPE set ";
    for (Element el : iterator){
      String name = T9Utility.null2Empty(el.getName());
      if (name.equals("FLOW_ID") 
          || name.equals("FLOW_NAME")
          || name.equals("FLOW_SORT")
          || name.equals("FORM_ID")
          || name.equals("FORCE_PRE_SET")
          || name.equals("ATTACHMENT_ID")
          || name.equals("VIEW_USER")
          || name.equals("ATTACHMENT_NAME")
          || name.equals("VIEW_PRIV")
          || name.equals("IS_VERSION")
      ) {
        continue; 
      }
      if (!isUserOn && T9WorkFlowUtility.findId(userStr, name)) {
        continue;
      }
      if (el.getText()== null || "".equals(el.getText().trim())) {
        query += " " + el.getName() + "=null,";
      } else {
        String value = el.getText();
        if (isUserOn && T9WorkFlowUtility.findId(privStr, name)) {
          value = this.getUserSeqId2(value, conn);
        }
        query += " " + el.getName() + "='" + value + "',";
      }
    }
    query = query.substring(0, query.length() - 1);
    query += " where SEQ_ID =" + flowId;
    
    this.upateBySql(query, conn);
    query = "delete from FLOW_PROCESS where FLOW_SEQ_ID=" + flowId ;
    this.upateBySql(query, conn);
    
    List<Element> rowList = root.elements("Process");
    for (Element node : rowList) {
      List<Element> child = node.elements();
      String nameStr = "";
      String valueStr = "";
      for (Element el : child){
        String name = el.getName();
        String value = el.getText();
        String isNotPersonStr  = T9Utility.null2Empty(el.attributeValue("isNotPerson"));
        boolean isNotPerson = false;
        if ("1".equals(isNotPersonStr)) {
          isNotPerson = true;
        }
        if (name.equals("TIME_OUT_MODIFY")
            || name.equals("TIME_OUT_ATTEND")
            || name.equals("RELATION_IN") 
            || name.equals("RELATION_OUT")
             || name.equals("PRCS_TYPE")
             ||name.equals("PLUGIN_SAVE")
             || name.equals("CONTROL_MODE")
             || name.equals("VIEW_PRIV")
             || name.equals("IS_SYSTEM")
        ) {
          continue;
        }
        if (!isUserOn && T9WorkFlowUtility.findId(userStr, name)) {
          continue;
        }
        if ("ATTACH_EDIT_PRIV".equals(name)) {
          continue;
        }
        if ("FLOW_ID".equals(name)) {
          value = String.valueOf(flowId);
          name = "FLOW_SEQ_ID";
        }
        if ("ID".equals(name)) {
          continue;
        }
        nameStr += name + ",";
        if (value== null || "".equals(value.trim())) {
          valueStr += "null,";
        } else {
          if (isUserOn && T9WorkFlowUtility.findId(useUserStr, name) && !isNotPerson) {
            value = this.getUserSeqId(value, conn);
          }
          if ("PRCS_DEPT".equals(name) && "ALL_DEPT".equals(value)) {
            value = "0";
          }
          if ("CONDITION_DESC".equals(name)) {
            value = value.replace("&#13;", "\n");
          }
          value = value.replace("'", "''");
          valueStr += "'" + value + "',";
        }
      }
      valueStr = valueStr.substring(0, valueStr.length() - 1);
      nameStr = nameStr.substring(0, nameStr.length() - 1);
      String tmp = "insert into FLOW_PROCESS (" + nameStr +") values ("+ valueStr +")";
      this.upateBySql(tmp, conn);
    }
  }
  public String getUserSeqId(String userIds , Connection conn) throws Exception {
    String str = "";
    String[] persons = userIds.split(",");
    for (String tmp : persons) {
      if (T9Utility.isInteger(tmp)) {
        String query = "select 1 from PERSON where seq_ID = " + tmp;
        Statement stm = null;
        ResultSet rs = null;
        try {
          stm = conn.createStatement();
          rs = stm.executeQuery(query);
          if (rs.next()) {
            str += tmp + ",";
          }
        } catch(Exception ex) {
          throw ex;
        } finally {
          T9DBUtility.close(stm, rs, null); 
        }
      } else {
        String query = "select SEQ_ID from PERSON where USER_ID = '" + tmp + "'";
        Statement stm = null;
        ResultSet rs = null;
        try {
          stm = conn.createStatement();
          rs = stm.executeQuery(query);
          if (rs.next()) {
            int seqId = rs.getInt("SEQ_ID");
            str += seqId + ",";
          }
        } catch(Exception ex) {
          throw ex;
        } finally {
          T9DBUtility.close(stm, rs, null); 
        }
      }
    }
    if (str.endsWith(",")) {
      str = str.substring(0 , str.length() - 1);
    }
    return str;
  }
  public  String getUserSeqId2(String privStr , Connection conn) throws Exception{
    String result = "";
    if (privStr == null || "".equals(privStr)){
      return privStr;
    }
    String [] arra = privStr.split("\\|");
    String user = "";
    String priv = "";
    String dept = "";
    if (arra.length == 1 ) {
      user = arra[0];
    } else if (arra.length >= 2 ) {
      user = arra[0];
      dept = arra[1];
      if (arra.length == 3) {
        priv = arra[2];
      }
      if ("ALL_DEPT".equals(dept)) {
        dept = "0";
      }
    } 
    user = this.getUserSeqId(user, conn);
    result = user + "|" + dept + "|" + priv;
    return result;
  }
  public void upateBySql(String sql , Connection conn) throws Exception {
    Statement stm = null;
    try {
      stm = conn.createStatement();
      stm.executeUpdate(sql);
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, null, null); 
    }
  }
  public void trans(Connection conn ,String flowStr , String toId , String userId , String beginRun , String endRun) throws Exception {
    if (flowStr.endsWith(",")) {
      flowStr = flowStr.substring(0, flowStr.length() - 1);
    }
    String query = "select RUN_ID FROM FLOW_RUN WHERE FLOW_ID IN ("+flowStr+") ";
    if (!T9Utility.isNullorEmpty(beginRun)) {
      query += " and RUN_ID >= " + beginRun ;
    }
    if (!T9Utility.isNullorEmpty(endRun)) {
      query += " and RUN_ID <= " + endRun ;
    }
    
    String runIds = "";
    Statement stm = null;
    ResultSet rs = null;
    try {
      stm = conn.createStatement();
      rs = stm.executeQuery(query);
      while (rs.next()) {
        String runId = String.valueOf(rs.getInt("RUN_ID"));
        if (!T9WorkFlowUtility.findId(runIds, runId)) {
          runIds += runId + ",";
        }
      }
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, rs, null); 
    }
     
    if (!T9Utility.isNullorEmpty(runIds)) {
      if (runIds.endsWith(",")) {
        runIds = runIds.substring(0, runIds.length() - 1);
      }
      String update = "UPDATE FLOW_RUN_PRCS set FROM_USER = USER_ID,USER_ID='"+toId+"' WHERE USER_ID='"+userId+"' AND RUN_ID IN(" + runIds +")";
      this.upateBySql(update, conn);
    }
  }

  public List<T9FlowType> getFlowTypeList(String sortId, Connection conn,
      String flowOther) throws Exception {
    // TODO Auto-generated method stub
    List<T9FlowType> list =  new ArrayList();
    sortId = T9WorkFlowUtility.getOutOfTail(sortId);
    String query  = "select * from FLOW_TYPE WHERE flow_SORT in (" + sortId + ") and FREE_OTHER = '"+ flowOther  +"' order by flow_no";
    Statement stm = null;
    ResultSet rs = null;
    try {
      stm = conn.createStatement();
      rs = stm.executeQuery(query);
      while (rs.next()) {
        T9FlowType ft = new T9FlowType();
        int seqId = rs.getInt("SEQ_ID");
        String flowName = rs.getString("flow_name");
        String flowType = rs.getString("flow_Type");
        String newUser = rs.getString("NEW_USER");
        String queryUser = rs.getString("query_User");
        String queryUserDept = rs.getString("query_User_Dept");
        String manageUser = rs.getString("manage_User");
        String manageUserDept = rs.getString("manage_User_Dept");
        ft.setFlowType(flowType);
        ft.setFlowName(flowName);
        ft.setSeqId(seqId);
        ft.setNewUser(newUser);
        ft.setQueryUser(queryUser);
        ft.setQueryUserDept(queryUserDept);
        ft.setManageUser(manageUser);
        ft.setManageUserDept(manageUserDept);
        list.add(ft);
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, rs, null);
    }
    return list;
  }
  public String getQueryItem(String flowId , Connection conn ) throws Exception {
    StringBuffer sb = new StringBuffer("{");
    String query = "select * from FLOW_TYPE where SEQ_ID=" + flowId;
    int formId = 0;
    int sortId = 0;
    String queryItem = "";
    Statement stm = null;
    ResultSet rs = null;
    try {
      stm = conn.createStatement();
      rs = stm.executeQuery(query);
      if (rs.next()) {
        formId = rs.getInt("FORM_SEQ_ID");
        sortId = rs.getInt("FLOW_SORT");
        queryItem = rs.getString("QUERY_ITEM");
        if (queryItem == null) {
          queryItem = "";
        }
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, rs, null);
    }
    T9FlowFormLogic ffLogic = new T9FlowFormLogic();
    String formItem = ffLogic.getTitle(conn, formId);
    formItem += ",[B@]";
    sb.append("sortId:" + sortId);
    sb.append(",queryItem:'" + queryItem + "'");
    sb.append(",formItem:'" + formItem + "'");
    sb.append("}");
    return sb.toString();
  }

  public void setQueryItem(String flowId, String queryItem, Connection conn) throws Exception {
    // TODO Auto-generated method stub
    if (queryItem == null) {
      queryItem = "";
    }
    String query = "update FLOW_TYPE set query_item='" +queryItem +"' where SEQ_ID=" + flowId;
    Statement stm = null;
    try {
      stm = conn.createStatement();
      stm.executeUpdate(query);
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, null, null);
    }
  }

  public int getFlowTypeSeqId(Connection dbConn) throws Exception {
    // TODO Auto-generated method stub
    String query = "select max(SEQ_ID) from FLOW_TYPE";
    int seqId = 0;
    Statement stm = null;
    ResultSet rs = null;
    try {
      stm = dbConn.createStatement();
      rs = stm.executeQuery(query);
      if (rs.next()) {
        seqId = rs.getInt(1);
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, rs, null);
    }
    return seqId;
  }

  /**
   * 处理上传附件，返回附件id，附件名称


  //点击单文件上传时调用的方法

   * @param request  HttpServletRequest
   * @param 
   * @return Map<String, String> ==> {id = 文件名}
   * @throws Exception 
   */
  public Map<String, String> fileUploadLogic(T9FileUploadForm fileForm,
      String pathPx) throws Exception {
    Map<String, String> result = new HashMap<String, String>();
    String filePath = pathPx;
    try {
      Calendar cld = Calendar.getInstance();
      int year = cld.get(Calendar.YEAR) % 100;
      int month = cld.get(Calendar.MONTH) + 1;
      String mon = month >= 10 ? month + "" : "0" + month;
      String hard = year + mon;
      Iterator<String> iKeys = fileForm.iterateFileFields();
      while (iKeys.hasNext()) {
        String fieldName = iKeys.next();
        String fileName = fileForm.getFileName(fieldName).replaceAll("\\'", "");
        String fileNameV = fileName;
        //T9Out.println(fileName+"*************"+fileNameV);
        if (T9Utility.isNullorEmpty(fileName)) {
          continue;
        }
        String rand = T9DiaryUtil.getRondom();
        fileName = rand + "_" + fileName;
        
        while (T9DiaryUtil.getExist(filePath + File.separator + hard, fileName)) {
          rand = T9DiaryUtil.getRondom();
          fileName = rand + "_" + fileName;
        }
        result.put(hard + "_" + rand, fileNameV);
        fileForm.saveFile(fieldName, filePath + File.separator + T9NewsCont.MODULE + File.separator + hard + File.separator + fileName);
      }
    } catch (Exception e) {
      throw e;
    }
    return result;
  }
}

