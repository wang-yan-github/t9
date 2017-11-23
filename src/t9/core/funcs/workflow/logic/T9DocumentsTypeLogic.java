package t9.core.funcs.workflow.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import t9.core.data.T9DbRecord;
import t9.core.data.T9PageDataList;
import t9.core.data.T9PageQueryParam;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.workflow.data.T9DocWord;
import t9.core.funcs.workflow.data.T9DocumentsType;
import t9.core.load.T9PageLoader;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;
import t9.subsys.oa.hr.recruit.filter.data.T9HrRecruitFilter;

public class T9DocumentsTypeLogic {

  public void addDocumentsTypeInfo(Connection dbConn, Map map, T9Person person) throws Exception{
    
    T9DocumentsType documentsType = new T9DocumentsType();
    documentsType.setDocumentsName((String)map.get("documentsName"));
    documentsType.setFlowType(Integer.parseInt((String)map.get("flowType")));
    documentsType.setDocumentsFont((String)map.get("documentsFont"));
    documentsType.setDocumentsWordModel((String)map.get("documentsWordModel"));
    T9ORM orm = new T9ORM();
    orm.saveSingle(dbConn, documentsType);
  }
  
  /**
   * 文件类型 通用列表
   * 
   * @param dbConn
   * @param request
   * @param person
   * @return
   * @throws Exception
   */
  public String getDocumentsTypeList(Connection dbConn, Map request, T9Person person) throws Exception {
    try {
      String sql = " SELECT SEQ_ID, documents_name, flow_type, documents_font, documents_word_model FROM documents_type ";
      T9PageQueryParam queryParam = (T9PageQueryParam) T9FOM.build(request);
      T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn, queryParam, sql);
      return pageDataList.toJson();
    } catch (Exception e) {
      throw e;
    }
  }
  
  /**
   * 文件类型
   * 
   * @param conn
   * @param seqId
   * @return
   * @throws Exception
   */
  public T9DocumentsType getDocumentsTypeDetail(Connection conn, int seqId) throws Exception {
    try {
      T9ORM orm = new T9ORM();
      return (T9DocumentsType) orm.loadObjSingle(conn, T9DocumentsType.class, seqId);
    } catch (Exception ex) {
      throw ex;
    }
  }
  
  /**
   * 文件类型信息
   * 
   * @param dbConn
   * @param fileForm
   * @param person
   * @throws Exception
   */
  public void updateDocumentsTypeInfo(Connection dbConn, Map map, T9Person person) throws Exception {
    T9DocumentsType documentsType = new T9DocumentsType();
    documentsType.setSeqId(Integer.parseInt((String)map.get("seqId")));
    documentsType.setDocumentsName((String)map.get("documentsName"));
    documentsType.setFlowType(Integer.parseInt((String)map.get("flowType")));
    documentsType.setDocumentsFont((String)map.get("documentsFont"));
    documentsType.setDocumentsWordModel((String)map.get("documentsWordModel"));
    T9ORM orm = new T9ORM();
    orm.updateSingle(dbConn, documentsType); 
  }
  
  /**
   * 删除文件类型
   * 
   * @param dbConn
   * @param seqIdStr
   * @throws Exception
   */
  public void deleteFileLogic(Connection dbConn, String seqIdStr) throws Exception {
    T9ORM orm = new T9ORM();
    if (T9Utility.isNullorEmpty(seqIdStr)) {
      seqIdStr = "";
    }
    try {
      String seqIdArry[] = seqIdStr.split(",");
      if (!"".equals(seqIdArry) && seqIdArry.length > 0) {
        for (String seqId : seqIdArry) {
          T9DocumentsType documentsType = (T9DocumentsType) orm.loadObjSingle(dbConn, T9DocumentsType.class, Integer.parseInt(seqId));
          
          // 删除数据库信息
          orm.deleteSingle(dbConn, documentsType);
        }
      }
    } catch (Exception e) {
      throw e;
    }
  }
  
  /**
   * 员工关怀查询
   * 
   * @param dbConn
   * @param request
   * @param map
   * @param person
   * @return
   * @throws Exception
   */
  public String queryDocumentsTypeList(Connection dbConn, Map request, Map map, T9Person person) throws Exception {
    String documentsName = (String) map.get("documentsName");
    String flowType = (String) map.get("flowType");
    String documentsFont = (String) map.get("documentsFont");
    String documentsWordModel = (String) map.get("documentsWordModel");
    String conditionStr = "";
    String sql = "";
    try {
      if (!T9Utility.isNullorEmpty(documentsName)) {
        conditionStr = " and documents_name like '%" + T9DBUtility.escapeLike(documentsName) + "%'";
      }
      if (!T9Utility.isNullorEmpty(flowType)) {
        conditionStr = " and flow_type ='" + flowType + "'";
      }
      if (!T9Utility.isNullorEmpty(documentsFont)) {
        conditionStr += " and " + T9DBUtility.findInSet(documentsFont, "documents_font");
      }
      if (!T9Utility.isNullorEmpty(documentsWordModel)) {
        conditionStr = " and documents_word_model like '%" + T9DBUtility.escapeLike(documentsWordModel) + "%'";
      }
      sql = " SELECT documents_type.SEQ_ID, documents_name, flow_name, documents_font, documents_word_model FROM documents_type , flow_type "
          + " where documents_type.flow_type = flow_type.seq_id " + conditionStr + " ORDER BY SEQ_ID desc";
      T9PageQueryParam queryParam = (T9PageQueryParam) T9FOM.build(request);
      T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn, queryParam, sql);
      return pageDataList.toJson();
    } catch (Exception e) {
      throw e;
    }
  }
  
  /**
   * 查询ById
   * 
   * @param dbConn
   * @return
   * @throws Exception
   */
  public static String getDocumentsTypeById(Connection dbConn, String seqIdStr) throws Exception {
    
    String docWordStr = "";
    T9ORM orm = new T9ORM();
    if (T9Utility.isNullorEmpty(seqIdStr)) {
      seqIdStr = "";
    }
    try {
      String seqIdArry[] = seqIdStr.split(",");
      if (!"".equals(seqIdArry) && seqIdArry.length > 0) {
        for (String seqId : seqIdArry) {
          T9DocWord docWord = (T9DocWord) orm.loadObjSingle(dbConn, T9DocWord.class, Integer.parseInt(seqId));
          if (docWord != null) {
            docWordStr = docWordStr + docWord.getDwName() + ",";
          }
        }
      }
    } catch (Exception e) {
      throw e;
    }
    if(docWordStr.endsWith(",")){
      return docWordStr.substring(0, docWordStr.length()-1);
    }
    else{
      return docWordStr;
    }
  }
  
  public List<T9DocWord> getDocumentsTypeListSelect(Connection dbConn, T9Person person, String condition) throws Exception{
    List<T9DocWord> list = new ArrayList<T9DocWord>();
    PreparedStatement stmt = null;
    ResultSet rs = null;
    String sql = " SELECT SEQ_ID, DW_NAME, INDEX_STYLE FROM doc_word ";
    if(!T9Utility.isNullorEmpty(condition) && !condition.equals("undefined")){
      sql = sql + " where DW_NAME like '%" + condition + "%' ";
    }
    try {
      stmt = dbConn.prepareStatement(sql);
      rs = stmt.executeQuery();
      int counter = 0;
      while(rs.next() && ++counter<50){
        T9DocWord docWord = new T9DocWord();
        docWord.setSeqId(rs.getInt("SEQ_ID"));
        docWord.setDwName(rs.getString("DW_NAME"));
        list.add(docWord);
      }
    } catch (Exception e) {
      throw e;
    }finally{
      T9DBUtility.close(stmt, rs, null);
    }
    return list;
  }
}
