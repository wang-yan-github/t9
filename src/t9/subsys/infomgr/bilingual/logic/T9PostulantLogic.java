package t9.subsys.infomgr.bilingual.logic;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import t9.core.funcs.person.data.T9Person;
import t9.core.util.T9Out;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.subsys.infomgr.bilingual.data.T9Bilingual;
import t9.subsys.infomgr.bilingual.data.T9Postulant;
import t9.subsys.infomgr.bilingual.data.T9PostulantInfo;

public class T9PostulantLogic {
        
  private static Logger log = Logger.getLogger("t9.core.subsys.bilingual.logic");
 
  /**
   * 增加一条记录
   * @param conn
   * @return
   * @throws Exception
   */
  public void add(Connection conn,T9Postulant postulant) throws Exception {
    
    try {
      T9ORM orm = new T9ORM();
      orm.saveSingle(conn, postulant);
    } catch(Exception ex) {
      throw ex;
    } finally {
      
    }
  }
  
  public void addInfo(Connection dbConn, String postulantId, String languageKind, String languageLevel, String amount) throws Exception {
    
    try {
      T9ORM orm = new T9ORM();
      Map map = new HashMap();
      
      map.put("amount", Integer.parseInt(amount));
      map.put("languageKind", languageKind);
      map.put("languageLevel", languageLevel);
      map.put("postulantId", postulantId);
      
      orm.saveSingle(dbConn , "postulantInfo" , map);
    } catch(Exception ex) {
      throw ex;
    } finally {
    }
  }
  
  /**
   * 修改一条记录
   * @param conn
   * @return
   * @throws Exception
   */
  public void modify(Connection conn,T9Postulant postulant) throws Exception {
    try {
      T9ORM orm = new T9ORM();
      orm.updateSingle(conn, postulant);
    } catch(Exception ex) {
      throw ex;
    } finally {
      
    }
  }
  
  public void modifyInfo(Connection dbConn, String postulantId, String languageKind, String languageLevel, String amount) throws Exception {
    try {
      T9ORM orm = new T9ORM();
      Map map = new HashMap();
      
      map.put("amount", Integer.parseInt(amount));
      map.put("languageKind", languageKind);
      map.put("languageLevel", languageLevel);
      map.put("postulantId", postulantId);
      
      orm.updateSingle(dbConn , "postulantInfo" , map);
    } catch(Exception ex) {
      throw ex;
    } finally {
    }
  }
  
  /**
   * 查询一条记录
   * @param conn
   * @return
   * @throws Exception
   */
  public T9Postulant detail(Connection conn,int seqId) throws Exception {
    
    try {
      T9ORM orm = new T9ORM();
      return (T9Postulant)orm.loadObjSingle(conn, T9Postulant.class, seqId);
    } catch(Exception ex) {
      throw ex;
    } finally {
      
    }
  }
  
  
  /**
   * 查询一条记录

   * @param conn
   * @return
   * @throws Exception
   */
  public T9PostulantInfo detailInfo(Connection conn,int seqId) throws Exception {
    
    try {
      T9ORM orm = new T9ORM();
      return (T9PostulantInfo)orm.loadObjSingle(conn, T9PostulantInfo.class, seqId);
    } catch(Exception ex) {
      throw ex;
    } finally {
      
    }
  }
  
  public ArrayList<T9PostulantInfo> getPostulantInfo(Connection conn, String postulantId) throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    ArrayList<T9PostulantInfo> orgList = new ArrayList<T9PostulantInfo>();
    T9PostulantInfo org = null;
    try {
      String queryStr = "select SEQ_ID, AMOUNT, LANGUAGE_KIND, LANGUAGE_LEVEL from POSTULANT_INFO WHERE POSTULANT_ID = '" + postulantId + "'";
      
      stmt = conn.createStatement();
      rs = stmt.executeQuery(queryStr);
      while (rs.next()) {
        org = new T9PostulantInfo();
        org.setSeqId(rs.getInt("SEQ_ID"));
        org.setAmount(rs.getInt("AMOUNT"));
        org.setLanguageKind(rs.getString("LANGUAGE_KIND"));
        org.setLanguageLevel(rs.getString("LANGUAGE_LEVEL"));
        orgList.add(org);
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return orgList;
  }
  /**
   * 更新记录标志位
   * @param conn
   * @return
   * @throws Exception
   */
  public void updateFlag(Connection conn, int seqId, int flag) throws Exception {
    PreparedStatement ps = null;
    try {
      String sql = "update POSTULANT" +
      		" set FLAG = ?" +
      		" where SEQ_ID = ?";
      
      ps = conn.prepareStatement(sql);
      ps.setInt(1, flag);
      ps.setInt(2, seqId);
      ps.executeUpdate();
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(ps, null, log);
    }
  }
  
  private int seqId;
  private String name;
  private int amount;
  private String languageKind;
  private String languageLevel;
  private String introduce;
  private String principal;
  private String principalDuty;
  private String principalUnit;
  private String principalTel;
  private String principalAdd;
  private String principalContact;
  private String services;
  private String servicesOther;
  private String serveTimeWeekday;
  private String serveTimeWeekend;
  private String recordSource;
  private String flag;
  /**
   * 查询记录(桌面模块使用)
   * @param conn
   * @return
   * @throws Exception
   */
  public List<Map<String,String>> queryModule(Connection conn) throws Exception {
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      String sql = "select SEQ_ID" +
      		",NAME" +
      		",PRINCIPAL" +
      		",PRINCIPAL_TEL" +
      		" from POSTULANT" +
      		" where FLAG = 0" +
      		" order by SEQ_ID desc";
      
      ps = conn.prepareStatement(sql);
      rs = ps.executeQuery();
      
      List<Map<String,String>> list = new ArrayList<Map<String,String>>();
      
      for (int i = 0; i < 10 && rs.next(); i++){
        Map<String,String> map = new HashMap<String,String>();
        
        map.put("seqId", String.valueOf(rs.getInt("SEQ_ID")));
        map.put("name", rs.getString("NAME"));
        map.put("principal", rs.getString("PRINCIPAL"));
        map.put("principalTel", rs.getString("PRINCIPAL_TEL"));
        
        list.add(map);
      }
      return list;
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(ps, null, log);
    }
  }
  
  /**
   * 删除一条记录
   * @param conn
   * @return
   * @throws Exception
   */
  public void delete(Connection conn,int seqId) throws Exception {
    
    try {
      T9ORM orm = new T9ORM();
      orm.deleteSingle(conn, T9Postulant.class, seqId);
    } catch(Exception ex) {
      throw ex;
    } finally {
      
    }
  }
  
  public void deleteInfo(Connection conn, String postulantId) throws Exception {
    String sql = "DELETE FROM POSTULANT_INFO WHERE POSTULANT_ID = '" + postulantId + "'";
    PreparedStatement pstmt = null;
    try {
      pstmt = conn.prepareStatement(sql);
      pstmt.executeUpdate();
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(pstmt, null, null);
    }
  }
  
  /**
   * 获取公共字段postulantId
   * @param conn
   * @param userId
   * @return
   * @throws Exception
   */
  
  public String getPostulantIdLogic(Connection conn , int seqId) throws Exception{
    String result = "";
    String sql = " select POSTULANT_ID from POSTULANT where SEQ_ID = " + seqId ;
    PreparedStatement ps = null;
    ResultSet rs = null ;
    try {
      ps = conn.prepareStatement(sql);
      rs = ps.executeQuery();
      if(rs.next()){
        String toId = rs.getString(1);
        if(toId != null){
          result = toId;
        }
      }
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(ps, rs, log);
    }
    return result;
  }
}
