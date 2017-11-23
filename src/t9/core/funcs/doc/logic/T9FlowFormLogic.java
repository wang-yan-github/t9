package t9.core.funcs.doc.logic;

import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import t9.core.funcs.doc.util.T9WorkFlowConst;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.doc.data.T9DocFlowFormItem;
import t9.core.funcs.doc.data.T9DocFlowFormType;
import t9.core.funcs.doc.util.T9FlowFormUtility;
import t9.core.funcs.doc.util.T9WorkFlowUtility;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
public class T9FlowFormLogic {
  private static Logger log = Logger.getLogger("t9.core.funcs.person.act");
  
  public Map selectFlowForm(Connection dbConn, int seqId, String str) throws Exception {
    Map map = new HashMap();
    //String[] nameStr = str;
    T9DocFlowFormType form = null;
    Statement stmt = null;
    ResultSet rs = null;
    String print = "";
    String sql = "SELECT "
                + str 
                + " FROM "+ T9WorkFlowConst.FLOW_FORM_TYPE +" WHERE SEQ_ID = " 
                + seqId;
    try {
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(sql);
      while (rs.next()) {
        //form = new T9DocFlowFormType();
        //form.setSeqId(rs.getInt("SEQ_ID"));
        //form.setFormName(rs.getString("PRINT_MODEL"));
        print = rs.getString(str.trim());
        map.put(str.trim(), print);
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return map;
  }
  
  public Map selectFlowForm(Connection dbConn, int seqId, String[] str) throws Exception {
    Map map = new HashMap();
    //String[] nameStr = str;
    T9DocFlowFormType form = null;
    Statement stmt = null;
    ResultSet rs = null;
    String print = "";
    String fields = "";
    for (String string : str){
      if("".equals(fields)){
        fields +=  string;
      }else {
        fields += "," + string;

      }
    }
    String sql = "SELECT "
                + fields 
                + " FROM "+ T9WorkFlowConst.FLOW_FORM_TYPE +" WHERE SEQ_ID = " 
                + seqId;
    //System.out.println(sql);
    try {
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(sql);
      while (rs.next()) {
        //form = new T9DocFlowFormType();
        //form.setSeqId(rs.getInt("SEQ_ID"));
        //form.setFormName(rs.getString("PRINT_MODEL"));
        for (String string : str){
          print = rs.getString(string);
          map.put(string, print);
        }
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    //System.out.println(map);
    return map;
  }
  
  public void updateFlowForm(Connection dbConn, int seqId, String[] names, String[] values) throws Exception{
    String buffer = "";
    for (String  nameStr : names){
      if(!"".equals(buffer)){
        buffer += ",";
      }
      buffer +=  nameStr  + "= ? ";
    }
    PreparedStatement stmt = null;
    ResultSet rs = null;
    String sql = "UPDATE "+ T9WorkFlowConst.FLOW_FORM_TYPE +" SET " 
                + buffer
                + " WHERE SEQ_ID = ?";
    //System.out.println(sql+":sql llllllllllllllll");
    try{
      stmt = dbConn.prepareStatement(sql);
      for (int i = 0; i < values.length ; i++){
        stmt.setString(i + 1, values[i]);
      }
      stmt.setInt(values.length + 1, seqId);
      stmt.executeUpdate();
    }catch(Exception ex){
      throw ex;
    }finally{
      T9DBUtility.close(stmt, rs, log);
    }
    T9FlowFormUtility ffu = new T9FlowFormUtility();
    ffu.cacheForm(seqId, dbConn);
  }
  public List<T9DocFlowFormItem> formToMap(Connection dbConn, int seqId) throws Exception{
    List<T9DocFlowFormItem> list = new ArrayList();
    Statement stmt = null;
    ResultSet rs = null;
    String sql = "select TITLE , ITEM_ID , CLAZZ , NAME from "+ T9WorkFlowConst.FLOW_FORM_ITEM +" where FORM_ID =" + seqId + " order by ITEM_ID";
    try {
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(sql);
      while (rs.next()) {
        T9DocFlowFormItem item = new T9DocFlowFormItem();
        String title = rs.getString("TITLE");
        item.setTitle(title);
        String clazz = rs.getString("CLAZZ");
        item.setClazz(clazz);
        int itemId = rs.getInt("ITEM_ID");
        item.setItemId(itemId);
        String name = rs.getString("NAME");
        item.setName(name);
        list.add(item);
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return list;
  }
  public String getTitle(Connection dbConn, int seqId) throws Exception{
    List<T9DocFlowFormItem> hs = formToMap(dbConn, seqId);
    String result = "";
    for (T9DocFlowFormItem item : hs) {
      String val = item.getTitle();
      String clazz = item.getClazz();
      if ("DATE".equals(clazz) || "USER".equals(clazz)) {
        continue;
      }
      if(!"".equals(result)){
        result += ",";
      }
      result += val;
    }
    return result;
  }
  public String getFormJson(Connection dbConn, int seqId) throws Exception{
    List<T9DocFlowFormItem> hs = formToMap(dbConn, seqId);
    
    StringBuffer sb = new StringBuffer("[");
    if (hs != null) {
      int count = 0;
      for (T9DocFlowFormItem item : hs) {
        String val = item.getTitle();
        String clazz = item.getClazz();
        if ("DATE".equals(clazz) || "USER".equals(clazz)) {
          continue;
        }
        count ++ ;
        sb.append("{");
        int id = item.getItemId();
        sb.append("id:'" + id + "'");
        sb.append(",title:'" + val + "'");
        sb.append("},");
      }
      if (count > 0) {
        sb.deleteCharAt(sb.length() - 1);
      }
    }
    sb.append("]");
    return sb.toString();
  }
  public  String getFlowFormTypeOption(Connection conn ,T9Person u) throws Exception{
    String query = "select " 
      + " FORM_NAME"
      + " , SEQ_ID"
      + " , DEPT_ID "
      + " from "+ T9WorkFlowConst.FLOW_FORM_TYPE;
    StringBuffer sb = new StringBuffer("[");
    int count = 0 ;
    Statement stm = null;
    ResultSet rs = null;
    try{
      stm = conn.createStatement();
      rs = stm.executeQuery(query);
      T9WorkFlowUtility w = new T9WorkFlowUtility();
      while(rs.next()){
        int deptId = rs.getInt("DEPT_ID");
        if (!w.isHaveRight(deptId, u, conn)) {
          continue;
        }
        String formName = rs.getString("FORM_NAME");
        int seqId = rs.getInt("SEQ_ID");
        sb.append("{");
        sb.append("value:" + seqId + ",");
        sb.append("text:'" + formName + "'");
        sb.append("},");
        count++ ;
      }
    }catch(Exception e){
      throw e;
    }finally{
      T9DBUtility.close(stm, rs, log);
    }
    if (count > 0 ) {
      sb.deleteCharAt(sb.length() - 1);
    }
    sb.append("]");
    return sb.toString();
  }
  public String getMetaDataItem(int formId , Connection conn) throws Exception {
    StringBuffer sb = new StringBuffer();
    String query = "select title , metadata_name from "+ T9WorkFlowConst.FLOW_FORM_ITEM +" where form_Id =" + formId + " and metadata_name is not null";
    Statement stm = null;
    ResultSet rs = null;
    int count = 0 ;
    try{
      stm = conn.createStatement();
      rs = stm.executeQuery(query);
      while(rs.next()){
        String title = rs.getString("title");
        String metadataName = rs.getString("metadata_name");
        
        sb.append("{");
        sb.append("value:'" + title + "',");
        sb.append("text:'" + title +"---"+ metadataName  + "'");
        sb.append("},");
        count++ ;
      }
    }catch(Exception e){
      throw e;
    }finally{
      T9DBUtility.close(stm, rs, log);
    }
    if (count > 0) {
      sb.deleteCharAt(sb.length() - 1);
    }
    return sb.toString();
  }
  /**
   * 表单管理
   *  by cy
   * @param dbConn
   * @return
   * @throws Exception
   */
  public List<T9DocFlowFormType> getFlowFormType(Connection dbConn,String seqIds) throws Exception{
    List<T9DocFlowFormType> list = null;
    T9ORM orm = new T9ORM();
    if("".equals(seqIds)){
      return new ArrayList<T9DocFlowFormType>();
    }
    String[] filters = new String[]{" SEQ_ID IN (" + seqIds + ")"};
    list =  orm.loadListSingle(dbConn, T9DocFlowFormType.class, filters);
    return list;
  }
  /**
   * 表单管理得到表单Id
   *  by cy 
   * @param dbConn
   * @param seqIds
   * @return
   * @throws Exception
   */
  public String getIdBySort(Connection dbConn,int sortId) throws Exception{
    String sql = "";
    if(sortId == 0){
      sql =  "SELECT SEQ_ID FROM "+ T9WorkFlowConst.FLOW_FORM_TYPE +" WHERE SEQ_ID NOT IN( SELECT FORM_SEQ_ID FROM "+ T9WorkFlowConst.FLOW_TYPE +" )";
    }else{
      sql = "SELECT SEQ_ID FROM "+ T9WorkFlowConst.FLOW_FORM_TYPE +" WHERE SEQ_ID IN( SELECT FORM_SEQ_ID FROM "+ T9WorkFlowConst.FLOW_TYPE +" WHERE FLOW_SORT = " + sortId + ")";
    }
    String result = "";
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    try{
      pstmt = dbConn.prepareStatement(sql);
      rs = pstmt.executeQuery();
      while(rs.next()){
        if(!"".equals(result)){
          result += ",";
        }
        int fsi = rs.getInt(1);
        result += fsi;
      }
    }catch(Exception e){
      e.printStackTrace();
      throw e;
    }finally{
      T9DBUtility.close(pstmt, rs, log);
    }
    return result;
  }
  public StringBuffer flowFormType2Json(Connection dbConn ,int sortId , T9Person u) throws Exception{
    StringBuffer sb = new StringBuffer("[");
    StringBuffer field = new StringBuffer();

    String seqIds = getIdBySort(dbConn, sortId);
    ArrayList<T9DocFlowFormType> list = (ArrayList<T9DocFlowFormType>) getFlowFormType(dbConn, seqIds);
    T9WorkFlowUtility w = new T9WorkFlowUtility();
    for (T9DocFlowFormType fft : list){
      int deptId = fft.getDeptId();
      if (!w.isHaveRight(deptId, u, dbConn)) {
        continue;
      }
      if(!"".equals(field.toString())){
        field.append(",");
      }
      field.append("{\"seqId\":'").append(fft.getSeqId()).append("'")
           .append(",").append("\"formName\":'").append(fft.getFormName()).append("'").append(",noDelete:" + this.isExistFlowRun(fft.getSeqId(), dbConn)).append("}");
    }
    sb.append(field).append("]");
    return sb;
  }
  
  /**
   * 是否存在流程
   * @param formId
   * @param conn
   * @return
   * @throws Exception
   */
  public boolean isExistFlowRun(int formId , Connection conn) throws Exception{
    String query  = "SELECT 1 from "+ T9WorkFlowConst.FLOW_TYPE +" where FORM_SEQ_ID="+ formId;
    Statement pstmt = null;
    ResultSet rs = null;
    try{
      pstmt = conn.createStatement();
      rs = pstmt.executeQuery(query);
      if (rs.next()) {
        return true;
      }
    }catch(Exception e){
      throw e;
    }finally{
      T9DBUtility.close(pstmt, rs, log);
    }
    return false;
  }
  public StringBuffer search(Connection dbConn ,String searchKey , T9Person u) throws Exception{
    String userPrivOther = u.getUserPrivOther();
    StringBuffer sb = new StringBuffer("[");
    StringBuffer field = new StringBuffer();
    T9ORM orm = new T9ORM();
    T9WorkFlowUtility w = new T9WorkFlowUtility();
    String query = " FORM_NAME LIKE '%" +  T9Utility.encodeLike(searchKey)  + "%' "  + T9DBUtility.escapeLike();
     String[] filters = new String[]{query};
    ArrayList<T9DocFlowFormType> list =  (ArrayList<T9DocFlowFormType>) orm.loadListSingle(dbConn, T9DocFlowFormType.class, filters);
    for (T9DocFlowFormType fft : list){
      int deptId = fft.getDeptId();
      if (!w.isHaveRight(deptId, u, dbConn)) {
        continue;
      }
      if(!"".equals(field.toString())){
        field.append(",");
      }
      String name = fft.getFormName();
      name = name.replaceAll("<", "&lt");
      name = name.replaceAll(">", "&gt");
      field.append("{\"seqId\":'").append(fft.getSeqId()).append("'")
           .append(",").append("\"formName\":'").append(name).append("'").append(",noDelete:" + this.isExistFlowRun(fft.getSeqId(), dbConn)).append("}");
    }
    sb.append(field).append("]");
    return sb;
  }
  /**
   * 删除表单
   * @param seqId
   * @param conn
   * @throws Exception
   */
  public void deleteForm (int seqId , Connection conn) throws Exception {
    String query1 = "delete from "+ T9WorkFlowConst.FLOW_FORM_TYPE +" where SEQ_ID = " + seqId;
    T9WorkFlowUtility.updateTableBySql(query1, conn);
    String query = "delete from "+ T9WorkFlowConst.FLOW_FORM_ITEM +" where FORM_ID = " + seqId;
    Statement stm = null;
    try{
      stm = conn.createStatement();
      stm.executeUpdate(query);
    }catch(Exception e){
      e.printStackTrace();
      throw e;
    }finally{
      T9DBUtility.close(stm, null , log);
    }
  }
  /**
   * 取出印章
   * @param seqId
   * @param conn
   * @throws Exception
   */
  public String getSeals (int userId , Connection conn) throws Exception {
    String query = "select SEQ_ID,SEAL_ID,SEAL_NAME,USER_STR from SEAL ORDER BY CREATE_TIME DESC ";
    StringBuffer sb = new StringBuffer();
    sb.append("[");
    int count = 0 ;
    Statement stm = null;
    ResultSet rs = null;
    try{
      stm = conn.createStatement();
      rs = stm.executeQuery(query);
      while (rs.next()) {
        String userStr = rs.getString("USER_STR");
        if (T9WorkFlowUtility.findId(userStr, String.valueOf(userId))) {
          count ++ ;
          int id = rs.getInt("SEQ_ID");
          String sealId = rs.getString("SEAL_ID");
          String sealName = rs.getString("SEAL_NAME");
          sb.append("{");
          sb.append("id:" + id);
          sb.append(",sealId:'" + sealId + "'");
          sb.append(",sealName:'" + sealName + "'");
          sb.append("},");
        }
      }
    }catch(Exception e){
      throw e;
    }finally{
      T9DBUtility.close(stm, rs , log);
    }
    if (count > 0) {
      sb.deleteCharAt(sb.length() - 1);
    }
    sb.append("]");
    return sb.toString();
  }
  /**
   * 取出印章数据
   * @param seqId
   * @param conn
   * @throws Exception
   */
  public byte[] getSealData (int id , Connection conn) throws Exception {
    String query = "select SEAL_DATA from SEAL WHERE SEQ_ID=" + id;
    String sealData = "";
    byte[] bt = null;
    Statement stm = null;
    ResultSet rs = null;
    try{
      stm = conn.createStatement();
      rs = stm.executeQuery(query);
      if (rs.next()) {
        Clob str =  rs.getClob("SEAL_DATA");
        sealData = T9WorkFlowUtility.clob2String(str);
        if (sealData == null) {
          sealData =  "err";
          bt = new byte[1];
        } else {
          sun.misc.BASE64Decoder decoder = new sun.misc.BASE64Decoder();
          bt = decoder.decodeBuffer(sealData);
        }
      }
    }catch(Exception e){
      throw e;
    }finally{
      T9DBUtility.close(stm, rs , log);
    }
    return bt;
  }
 
}
