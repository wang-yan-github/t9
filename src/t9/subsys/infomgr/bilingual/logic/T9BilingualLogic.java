package t9.subsys.infomgr.bilingual.logic;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;

import t9.core.util.T9Out;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.subsys.infomgr.bilingual.data.T9Bilingual;

public class T9BilingualLogic {
        
  private static Logger log = Logger.getLogger("t9.core.subsys.bilingual.act");
 
  /**
   * 增加一条记录
   * @param conn
   * @return
   * @throws Exception
   */
  public void addBilingual(Connection conn,T9Bilingual bi) throws Exception{
    
    try{
      T9ORM orm = new T9ORM();
      orm.saveSingle(conn, bi);
    }catch(Exception ex) {
      throw ex;
    }finally {
    }
  }
  
  public void modifyBilingual(Connection conn,T9Bilingual bi) throws Exception{
    
    try{
      T9ORM orm = new T9ORM();
      orm.updateSingle(conn, bi);
    }catch(Exception ex) {
      throw ex;
    }finally {
    }
  }
  
  /**
   * 删除一条记录
   * @param conn
   * @param seqId
   * @return
   * @throws Exception
   */
  public boolean deleteRecord(Connection conn, int seqId) throws Exception{
    PreparedStatement ps = null;
    ResultSet rs = null;
    
    try{
      String sql = "delete from BILINGUAL" +
      		" where SEQ_ID = ?";
      ps = conn.prepareStatement(sql);
      ps.setInt(1, seqId);
      return ps.executeUpdate() > 0;
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(ps, rs, log);
    }
  }
  
  /**
   * 启用/不启用
   * @param conn
   * @param seqId
   * @return
   * @throws Exception
   */
  public boolean setEnable(Connection conn, int seqId,String enable) throws Exception{
    PreparedStatement ps = null;
    ResultSet rs = null;
    
    try{
      String sql = "update BILINGUAL" +
      		" set ENABLE = ?" +
      		" where SEQ_ID = ?";
      ps = conn.prepareStatement(sql);
      ps.setString(1, enable);
      ps.setInt(2, seqId);
      return ps.executeUpdate() > 0;
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(ps, rs, log);
    }
  }
  
  public T9Bilingual queryRecord(Connection conn,int seqId) throws Exception{
    try{
      T9ORM orm = new T9ORM();
      return (T9Bilingual)orm.loadObjSingle(conn, T9Bilingual.class, seqId);
    }catch(Exception ex) {
      throw ex;
    }finally {
    }
  }
}
