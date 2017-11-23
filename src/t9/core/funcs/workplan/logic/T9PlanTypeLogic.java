package t9.core.funcs.workplan.logic;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import t9.core.funcs.workplan.data.T9PlanType;
import t9.core.util.db.T9DBUtility;

public class T9PlanTypeLogic {
  private static Logger log = Logger.getLogger("t9.core.funcs.workplan.act.T9PlanWorkAct");
  /***
   * 查询数据
   * @return
   * @throws Exception 
   */
  public List<T9PlanType> selectType(Connection dbConn) throws Exception {
    ResultSet rs = null;
    PreparedStatement stmt = null ; 
    T9PlanType planType = null;
    List<T9PlanType> list = new ArrayList<T9PlanType>();
    String sql = "select SEQ_ID,TYPE_NAME,TYPE_NO from plan_type order by TYPE_NO";
    try {
      stmt = dbConn.prepareStatement(sql);
      rs = stmt.executeQuery();
      while (rs.next()) {
        planType = new T9PlanType(); 
        planType.setSeqId(rs.getInt("SEQ_ID"));
        planType.setTypeName(rs.getString("TYPE_NAME"));
        planType.setTypeNO(rs.getInt("TYPE_NO"));
        list.add(planType);
      }
    }catch (Exception e) {
      throw e;
    }finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return list;  
  }
  /***
   * 增加数据
   * @return
   * @throws Exception 
   */
  public void addType(Connection dbConn,T9PlanType type) throws Exception {
    PreparedStatement stmt = null ; 
    String sql = "insert into plan_type(TYPE_NAME,TYPE_NO) values(?,?)";
    try {
      stmt = dbConn.prepareStatement(sql);
      stmt.setString(1,type.getTypeName().replaceAll("\"","'"));
      stmt.setInt(2,type.getTypeNO());
      stmt.executeUpdate();
    }catch (Exception e) {
      throw e;
    }finally {
      T9DBUtility.close(stmt, null, log);
    }
  }
  /***
   * 删除数据
   * @return
   * @throws Exception 
   */
  public void deleteType(Connection dbConn,int seqId) throws Exception {
    PreparedStatement stmt = null ; 
    String sql = "delete from plan_type where seq_id=?";
    try {
      stmt = dbConn.prepareStatement(sql);
      stmt.setInt(1,seqId);
      stmt.executeUpdate();
    }catch (Exception e) {
      throw e;
    }finally {
      T9DBUtility.close(stmt, null, log);
    }
  }
  /***
   * 删除所有数据
   * @return
   * @throws Exception 
   */
  public void deleteTypeAll(Connection dbConn) throws Exception {
    PreparedStatement stmt = null ; 
    String sql = "delete from plan_type";
    try {
      stmt = dbConn.prepareStatement(sql);
      stmt.executeUpdate();
    }catch (Exception e) {
      throw e;
    }finally {
      T9DBUtility.close(stmt, null, log);
    }
  }
  /***
   *修改数据
   * @return
   * @throws Exception 
   */
  public void updateType(Connection dbConn,T9PlanType type) throws Exception {
    PreparedStatement stmt = null ; 
    String sql = "update plan_type set TYPE_NAME=?,TYPE_NO=? where seq_id=?";
    try {
      stmt = dbConn.prepareStatement(sql);
      stmt.setString(1,type.getTypeName().replaceAll("\"","'"));
      stmt.setInt(2,type.getTypeNO());
      stmt.setInt(3,type.getSeqId());
      stmt.executeUpdate();
    }catch (Exception e) {
      throw e;
    }finally {
      T9DBUtility.close(stmt, null, log);
    }
  }
  /***
   *根据ID查询数据
   * @return
   * @throws Exception 
   */
  public T9PlanType selectId(Connection dbConn,int seqId) throws Exception {
    ResultSet rs = null;
    PreparedStatement stmt = null ; 
    T9PlanType planType = null;
    String sql = "select SEQ_ID,TYPE_NAME,TYPE_NO from plan_type where seq_id=?";
    try {
      stmt = dbConn.prepareStatement(sql);
      stmt.setInt(1,seqId);
      rs = stmt.executeQuery();
      if (rs.next()) {
        planType = new T9PlanType(); 
        planType.setSeqId(rs.getInt("SEQ_ID"));
        planType.setTypeName(rs.getString("TYPE_NAME"));
        planType.setTypeNO(rs.getInt("TYPE_NO"));
      }
    }catch (Exception e) {
      throw e;
    }finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return planType;  
  }
  /***
   *根据ID查询数据
   * @return
   * @throws Exception 
   */
  public T9PlanType selectTypeName(Connection dbConn,String TypeName) throws Exception {
    ResultSet rs = null;
    PreparedStatement stmt = null ; 
    T9PlanType planType = null;
    String sql = "select TYPE_NAME from plan_type where TYPE_NAME=?";
    try {
      stmt = dbConn.prepareStatement(sql);
      stmt.setString(1,TypeName);
      rs = stmt.executeQuery();
      if (rs.next()) {
        planType = new T9PlanType(); 
        planType.setTypeName(rs.getString("TYPE_NAME"));
      }
    }catch (Exception e) {
      throw e;
    }finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return planType;  
  }
}
