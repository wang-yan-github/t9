package t9.core.funcs.person.logic;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import t9.core.data.T9DbRecord;
import t9.core.data.T9PageDataList;
import t9.core.data.T9PageQueryParam;
import t9.core.funcs.jexcel.util.T9CSVUtil;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.person.data.T9SecureKey;
import t9.core.global.T9Const;
import t9.core.load.T9PageLoader;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.file.T9FileUploadForm;
import t9.core.util.form.T9FOM;

public class T9SecureCardLogic {

  
  public boolean isExist(Connection conn , String keySn) throws Exception{
    Statement stmt = null;
    ResultSet rs = null;
    try {
      String queryStr = "select 1 from secure_key where KEY_SN = '"+keySn+"'"; 
      stmt = conn.createStatement();
      rs = stmt.executeQuery(queryStr);
      if(rs.next()){      
        return true;
      }
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stmt, rs, null);
    }
    return false;
  }
  
  /**
   * 动态密保卡 通用列表
   * 
   * @param dbConn
   * @param request
   * @param person
   * @return
   * @throws Exception
   */
  public String getSecureCard(Connection dbConn, Map request, T9Person person) throws Exception {
    try {
      String sql = " SELECT sk.SEQ_ID, sk.KEY_SN, p.USER_ID, p.USER_NAME, d.DEPT_NAME, sk.KEY_INFO "
                 + " FROM secure_key sk "
                 + " LEFT JOIN person p ON p.KEY_SN = sk.KEY_SN "
                 + " LEFT JOIN department d ON d.SEQ_ID = p.DEPT_ID ";
      T9PageQueryParam queryParam = (T9PageQueryParam) T9FOM.build(request);
      T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn, queryParam, sql);
      return pageDataList.toJson();
    } catch (Exception e) {
      throw e;
    }
  }
  
  public void bindUser(Connection conn, String userId, String keySn) throws Exception{
    Statement stmt = null;
    try {
      String queryStr = " update person set KEY_SN = '"+keySn+"' where SEQ_ID ="+userId; 
      stmt = conn.createStatement();
      stmt.executeUpdate(queryStr);
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stmt, null, null);
    }
  }
  
  public void deleteSecureCard(Connection conn, String seqIdStrs) throws Exception{
    Statement stmt = null;
    ResultSet rs = null;
    if (T9Utility.isNullorEmpty(seqIdStrs)) {
      seqIdStrs = "";
    }
    try {
      String seqIdArry[] = seqIdStrs.split(",");
      if (!"".equals(seqIdArry) && seqIdArry.length > 0) {
        for (String seqId : seqIdArry) {
          String userId = "0";
          String queryStr = " SELECT SEQ_ID FROM person p where KEY_SN = (SELECT KEY_SN FROM secure_key s WHERE s.SEQ_ID = "+seqId+") "; 
          stmt = conn.createStatement();
          rs = stmt.executeQuery(queryStr);
          if(rs.next()){
            userId = rs.getString("SEQ_ID");
          }
          queryStr = " update person set KEY_SN = '' where SEQ_ID ="+userId; 
          stmt = conn.createStatement();
          stmt.executeUpdate(queryStr);
          
          queryStr = " delete from secure_key where SEQ_ID ="+seqId; 
          stmt = conn.createStatement();
          stmt.executeUpdate(queryStr);
        }
      }
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stmt, rs, null);
    }
  }
  
  
  public void unBindSecureCard(Connection conn, String seqIdStrs) throws Exception{
    Statement stmt = null;
    ResultSet rs = null;
    if (T9Utility.isNullorEmpty(seqIdStrs)) {
      seqIdStrs = "";
    }
    try {
      String seqIdArry[] = seqIdStrs.split(",");
      if (!"".equals(seqIdArry) && seqIdArry.length > 0) {
        for (String seqId : seqIdArry) {
          String userId = "0";
          String queryStr = " SELECT SEQ_ID FROM person p where KEY_SN = (SELECT KEY_SN FROM secure_key s WHERE s.SEQ_ID = "+seqId+") "; 
          stmt = conn.createStatement();
          rs = stmt.executeQuery(queryStr);
          if(rs.next()){
            userId = rs.getString("SEQ_ID");
          }
          queryStr = " update person set KEY_SN = '' where SEQ_ID ="+userId; 
          stmt = conn.createStatement();
          stmt.executeUpdate(queryStr);
        }
      }
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stmt, rs, null);
    }
  }
  
  public String getKeySn(Connection conn , String seqId) throws Exception{
    Statement stmt = null;
    ResultSet rs = null;
    try {
      String queryStr = "select KEY_INFO from secure_key where SEQ_ID = '"+seqId+"'"; 
      stmt = conn.createStatement();
      rs = stmt.executeQuery(queryStr);
      if(rs.next()){      
        return rs.getString("KEY_INFO");
      }
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stmt, rs, null);
    }
    return "";
  }
  
  public T9SecureKey getKeyInfo(Connection conn, T9Person person) throws Exception{
    
    Statement stmt = null;
    ResultSet rs = null;
    int seqId = 0;
    if(person != null){
      seqId = person.getSeqId();
    }
    try {
      String queryStr = "select SEQ_ID , KEY_SN , KEY_INFO from secure_key where KEY_SN = (select KEY_SN from person where seq_id = "+seqId+")"; 
      stmt = conn.createStatement();
      rs = stmt.executeQuery(queryStr);
      if(rs.next()){  
        T9SecureKey secureKey = new T9SecureKey();
        secureKey.setSeqId(rs.getInt("SEQ_ID"));
        secureKey.setKeySn(rs.getString("KEY_SN"));
        secureKey.setKeyInfo(rs.getString("KEY_INFO"));
        return secureKey;
      }
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stmt, rs, null);
    }
    return null; 
  }
  
  public void setKeyInfoByKeySn(Connection conn, T9SecureKey secureCard) throws Exception{
    Statement stmt = null;
    try {
      String queryStr = " update secure_key set KEY_INFO = '"+secureCard.getKeyInfo()+"' where KEY_SN ="+secureCard.getKeySn(); 
      stmt = conn.createStatement();
      stmt.executeUpdate(queryStr);
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stmt, null, null);
    }
  }
  
  /**
   * 导入CSV批量绑定密码卡
   * 
   * @param dbConn
   * @param fileForm
   * @param person
   * @param buffer
   * @return
   * @throws Exception
   */
  public int impSecureCardInfoToCsv(Connection dbConn, T9FileUploadForm fileForm, T9Person person) throws Exception {
    Map<Object, Object> returnMap = new HashMap<Object, Object>();
    PreparedStatement stmt = null;
    String sql = "  ";
    int count = 0;
    try {
      InputStream is = fileForm.getInputStream();
      ArrayList<T9DbRecord> dbRecords = T9CSVUtil.CVSReader(is, T9Const.CSV_FILE_CODE);
      String userName = "";
      String keySn = "";
      for (T9DbRecord record : dbRecords) {
        int fieldCount = record.getFieldCnt();
        if (fieldCount > 0) {
          for (int i = 0; i < fieldCount; i++) {

            String keyName = record.getNameByIndex(i);
            String value = (String) record.getValueByIndex(i);
            if ("用户名".equals(keyName.trim())) {
              userName = value;
            }
            if("卡号".equals(keyName.trim())){
              keySn = value;
            }
            
            if(i%2 == 1){
              sql = " update person set KEY_SN = '"+keySn+"' where USER_NAME = '"+userName+"' ";
              stmt = dbConn.prepareStatement(sql);
              count = count + stmt.executeUpdate();
            }
          }
        }
      }
      return count;
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(stmt, null, null);
    }
  }
}
