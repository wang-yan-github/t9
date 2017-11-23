package t9.core.module.report.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

import t9.core.funcs.person.data.T9UserPriv;
import t9.core.funcs.person.logic.T9UserPrivLogic;
import t9.core.util.db.T9DBUtility;

public class T9UserPrivSyncLogic {
  public void editUserPriv(T9UserPriv o , Connection conn) throws Exception {
    String cols = "update USER_PRIV set PRIV_NAME=?, PRIV_NO=?, FUNC_ID_STR=? where USER_PRIV = ?";
    PreparedStatement stm = null;
    try {
      stm = conn.prepareStatement(cols);
      stm.setInt(4, o.getSeqId());
      stm.setString(1, o.getPrivName());
      stm.setInt(2, o.getPrivNo());
      stm.setString(3, o.getFuncIdStr());
      stm.executeUpdate();
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, null, null); 
    }
  }
  public void addUserPriv(T9UserPriv o , Connection conn) throws Exception {
    String cols = "insert into USER_PRIV (USER_PRIV, PRIV_NAME, PRIV_NO, FUNC_ID_STR) values (?,?,?,?)";
    PreparedStatement stm = null;
    try {
      stm = conn.prepareStatement(cols);
      stm.setInt(1, o.getSeqId());
      stm.setString(2, o.getPrivName());
      stm.setInt(3, o.getPrivNo());
      stm.setString(4, o.getFuncIdStr());
      stm.executeUpdate();
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, null, null); 
    }
  }
  public void syncUserPriv(Connection conn , Connection reportConn) throws Exception {
    T9UserPrivLogic logic = new T9UserPrivLogic();
    List list = logic.getRoleList(conn);
    this.delUserPriv(reportConn);
    for (Object u : list) {
      this.addUserPriv((T9UserPriv)u, reportConn);
    }
  }

  public void delUserPriv( Connection conn) throws Exception {
    String cols = "delete from  USER_PRIV" ;
    PreparedStatement stm = null;
    try {
      stm = conn.prepareStatement(cols);
      stm.executeUpdate();
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, null, null); 
    }
  }
  public void delUserPriv(int userPriv , Connection conn) throws Exception {
    String cols = "delete from  USER_PRIV where USER_PRIV=" + userPriv;
    PreparedStatement stm = null;
    try {
      stm = conn.prepareStatement(cols);
      stm.executeUpdate();
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, null, null); 
    }
  }
}
