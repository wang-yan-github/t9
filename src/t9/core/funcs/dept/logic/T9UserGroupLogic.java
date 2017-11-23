package t9.core.funcs.dept.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;
import t9.core.funcs.workflow.data.T9FlowProcess;
import t9.core.util.db.T9DBUtility;

public class T9UserGroupLogic {
  private static Logger log = Logger.getLogger("t9.core.funcs.dept.act");
 
  /**
   * 删除公共自定义组记录
   * @param conn
   * @param seqId
   * @throws Exception
   */
  
  public void deleteUserGroup(Connection conn, String seqId) throws Exception {
    String sql = "DELETE FROM USER_GROUP WHERE SEQ_ID IN(" + seqId + ")";
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
}
