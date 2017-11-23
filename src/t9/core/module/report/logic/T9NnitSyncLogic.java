package t9.core.module.report.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.List;

import t9.core.funcs.org.data.T9Organization;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;

public class T9NnitSyncLogic {
  public void editNuit(T9Organization o, Connection conn) throws Exception {
    String cols = "update unit set UNIT_NAME=?, TELEPHONE=?, MAX=?, POSTCODE=?, ADDRESS=?, WEBSITE=?, EMAIL=?, SIGN_IN_USER=?, ACCOUNT=? where SEQ_ID = ?";
    PreparedStatement stm = null;
    try {
      stm = conn.prepareStatement(cols);
      stm.setString(1, o.getUnitName());
      stm.setString(2, o.getTelephone());
      stm.setString(3, o.getMax());
      stm.setString(4, o.getPostcode());
      stm.setString(5, o.getAddress());
      stm.setString(6, o.getWebsite());
      stm.setString(7, o.getEmail());
      stm.setString(8, o.getSignInUser());
      stm.setString(9, o.getAccount());
      stm.setInt(10, o.getSeqId());
      stm.executeUpdate();
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, null, null); 
    }
  }
  public void addNuit(T9Organization o , Connection conn) throws Exception {
    String cols = "insert into unit (SEQ_ID, UNIT_NAME, TELEPHONE, MAX, POSTCODE, ADDRESS, WEBSITE, EMAIL, SIGN_IN_USER, ACCOUNT) values (?,?,?,?,?,?,?,?,?,?)";
    PreparedStatement stm = null;
    try {
      stm = conn.prepareStatement(cols);
      stm.setInt(1, o.getSeqId());
      stm.setString(2, o.getUnitName());
      stm.setString(3, o.getTelephone());
      stm.setString(4, o.getMax());
      stm.setString(5, o.getPostcode());
      stm.setString(6, o.getAddress());
      stm.setString(7, o.getWebsite());
      stm.setString(8, o.getEmail());
      stm.setString(9, o.getSignInUser());
      stm.setString(10, o.getAccount());
      stm.executeUpdate();
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, null, null); 
    }
  }
  public void synNunit(Connection conn , Connection reportDbConn) throws Exception {
    this.delNuit(reportDbConn);
    T9ORM orm = new T9ORM();
    List<T9Organization> list = orm.loadListSingle(conn, T9Organization.class, new HashMap());
    if (list.size()  > 0 ) {
      this.addNuit(list.get(0), reportDbConn);
    }
  }
  public void delNuit( Connection conn) throws Exception {
    String cols = "delete from unit";
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
