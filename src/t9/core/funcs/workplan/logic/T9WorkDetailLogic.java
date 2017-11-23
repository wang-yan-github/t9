package t9.core.funcs.workplan.logic;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import t9.core.funcs.workplan.data.T9WorkDetail;
import t9.core.global.T9Const;
import t9.core.global.T9SysPropKeys;
import t9.core.global.T9SysProps;
import t9.core.util.db.T9DBUtility;

public class T9WorkDetailLogic {
  private static Logger log = Logger.getLogger("t9.core.funcs.workplan.act.T9PlanWorkAct");
  /***
   * 根据ID查询数据
   * @return
   * @throws Exception 
   */
  public List<T9WorkDetail> selectDetail(Connection dbConn,int seqId) throws Exception {
    ResultSet rs = null;
    PreparedStatement stmt = null ; 
    T9WorkDetail detail = null;
    List<T9WorkDetail> list = new ArrayList<T9WorkDetail>();                                                                  
    String sql = null;
    String dbms = T9SysProps.getString(T9SysPropKeys.DBCONN_DBMS);
    if (dbms.equals(T9Const.DBMS_SQLSERVER)) {
      sql = "select " +
      "SEQ_ID," +
      "PLAN_ID," +
      "WRITE_TIME," +
      "PROGRESS," +
      "[PERCENT]," +
      "TYPE_FLAG," +
      "WRITER," +
      "ATTACHMENT_ID," +
      "ATTACHMENT_NAME" +
      " from work_detail where PLAN_ID=? and TYPE_FLAG=? order by seq_id";
    }else {
      sql = "select " +
      "SEQ_ID," +
      "PLAN_ID," +
      "WRITE_TIME," +
      "PROGRESS," +
      "PERCENT," +
      "TYPE_FLAG," +
      "WRITER," +
      "ATTACHMENT_ID," +
      "ATTACHMENT_NAME" +
      " from work_detail where PLAN_ID=? and TYPE_FLAG=? order by seq_id";
    }
    try {
      stmt = dbConn.prepareStatement(sql);
      stmt.setInt(1, seqId);
      stmt.setString(2, "1");
      rs = stmt.executeQuery();
      while (rs.next()) {
        detail = new T9WorkDetail(); 
        detail.setSeqId(rs.getInt("SEQ_ID"));
        detail.setPlanId(rs.getString("PLAN_ID"));
        detail.setWriteTime(rs.getDate("WRITE_TIME"));
        detail.setProgress(rs.getString("PROGRESS"));
        detail.setPercent(rs.getInt("PERCENT"));
        detail.setTypeFlag(rs.getString("TYPE_FLAG"));
        detail.setWriter(rs.getString("WRITER"));
        detail.setAttachmentId(rs.getString("ATTACHMENT_ID"));
        detail.setAttachmentName(rs.getString("ATTACHMENT_NAME"));
        list.add(detail);
      }
    }catch (Exception e) {
      throw e;
    }finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return list;  
  }
  /***
   * ADD数据
   * @return
   * @throws Exception 
   */
  public void addDetail(Connection dbConn,T9WorkDetail detail) throws Exception {
    PreparedStatement stmt = null ;                                                                 
    String sql = null;
    String dbms = T9SysProps.getString(T9SysPropKeys.DBCONN_DBMS);
    if (dbms.equals(T9Const.DBMS_SQLSERVER)) {
      sql = "insert into work_detail(" 
        + "PLAN_ID,"
        + "WRITE_TIME,"
        + "PROGRESS,"
        + "[PERCENT],"
        + "TYPE_FLAG,"
        + "WRITER,"
        + "ATTACHMENT_ID,"
        + "ATTACHMENT_NAME) values(?,?,?,?,?,?,?,?)";
    }else {
      sql = "insert into work_detail(" 
        + "PLAN_ID,"
        + "WRITE_TIME,"
        + "PROGRESS,"
        + "PERCENT,"
        + "TYPE_FLAG,"
        + "WRITER,"
        + "ATTACHMENT_ID,"
        + "ATTACHMENT_NAME) values(?,?,?,?,?,?,?,?)";
    }
    try {
      stmt = dbConn.prepareStatement(sql);
      stmt.setString(1, detail.getPlanId());
      stmt.setDate(2,detail.getWriteTime());
      stmt.setString(3, detail.getProgress());
      stmt.setInt(4,detail.getPercent());
      stmt.setString(5, detail.getTypeFlag());
      stmt.setString(6, detail.getWriter());
      stmt.setString(7, detail.getAttachmentId());
      stmt.setString(8, detail.getAttachmentName());
      stmt.executeUpdate();
    }catch (Exception e) {
      throw e;
    }finally {
      T9DBUtility.close(stmt, null, log);
    } 
  }
  /***
   * detele数据
   * @return
   * @throws Exception 
   */
  public void deteleDetail(Connection dbConn,int seqId) throws Exception {
    PreparedStatement stmt = null ;                                                                 
    String sql = "delete from work_detail where SEQ_ID=?";
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
   * 根据ID查询数据
   * @return
   * @throws Exception 
   */
  public T9WorkDetail selectId(Connection dbConn,int seqId) throws Exception {
    ResultSet rs = null;
    PreparedStatement stmt = null ; 
    T9WorkDetail detail = null;                                                                
    String sql = null;
    String dbms = T9SysProps.getString(T9SysPropKeys.DBCONN_DBMS);
    if (dbms.equals(T9Const.DBMS_SQLSERVER)) {
      sql = "select " +
      "SEQ_ID," +
      "PLAN_ID," +
      "WRITE_TIME," +
      "PROGRESS," +
      "[PERCENT]," +
      "TYPE_FLAG," +
      "WRITER," +
      "ATTACHMENT_ID," +
      "ATTACHMENT_NAME" +
      " from work_detail where SEQ_ID=?";
    }else {
      sql = "select " +
      "SEQ_ID," +
      "PLAN_ID," +
      "WRITE_TIME," +
      "PROGRESS," +
      "PERCENT," +
      "TYPE_FLAG," +
      "WRITER," +
      "ATTACHMENT_ID," +
      "ATTACHMENT_NAME" +
      " from work_detail where SEQ_ID=?";
    }
    try {
      stmt = dbConn.prepareStatement(sql);
      stmt.setInt(1, seqId);
      rs = stmt.executeQuery();
      if (rs.next()) {
        detail = new T9WorkDetail(); 
        detail.setSeqId(rs.getInt("SEQ_ID"));
        detail.setPlanId(rs.getString("PLAN_ID"));
        detail.setWriteTime(rs.getDate("WRITE_TIME"));
        detail.setProgress(rs.getString("PROGRESS"));
        detail.setPercent(rs.getInt("PERCENT"));
        detail.setTypeFlag(rs.getString("TYPE_FLAG"));
        detail.setWriter(rs.getString("WRITER"));
        detail.setAttachmentId(rs.getString("ATTACHMENT_ID"));
        detail.setAttachmentName(rs.getString("ATTACHMENT_NAME"));
      }
    }catch (Exception e) {
      throw e;
    }finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return detail;  
  }
  /***
   * 根据ID查询数据
   * @return
   * @throws Exception 
   */
  public void updateDetail(Connection dbConn,T9WorkDetail detail) throws Exception {
    PreparedStatement stmt = null ;                                                                
    String sql = "update work_detail set "
      + "WRITE_TIME=?,"
      + "PROGRESS=?,"
      + "WRITER=?,"
      + "ATTACHMENT_ID=?,"
      + "ATTACHMENT_NAME=?" 
      + " where SEQ_ID=?";
    try {
      stmt = dbConn.prepareStatement(sql);
      stmt.setDate(1, detail.getWriteTime());
      stmt.setString(2,detail.getProgress());
      stmt.setString(3,detail.getWriter());
      stmt.setString(4, detail.getAttachmentId());
      stmt.setString(5, detail.getAttachmentName());
      stmt.setInt(6,detail.getSeqId());
      stmt.executeUpdate();
    }catch (Exception e) {
      throw e;
    }finally {
      T9DBUtility.close(stmt, null, log);
    }  
  }
  /***
   * 根据ID查询数据
   * @return
   * @throws Exception 
   */
  public void updateDetailId(Connection dbConn,T9WorkDetail detail) throws Exception {
    PreparedStatement stmt = null ;                                                                
    String sql = null;
    String dbms = T9SysProps.getString(T9SysPropKeys.DBCONN_DBMS);
    if (dbms.equals(T9Const.DBMS_SQLSERVER)) {
      sql = "update work_detail set "
        + "WRITE_TIME=?,"
        + "PROGRESS=?,"
        + "WRITER=?,"
        + "ATTACHMENT_ID=?,"
        + "ATTACHMENT_NAME=?,"
        + "[PERCENT]=?"
        + " where SEQ_ID=?";
    }else {
      sql = "update work_detail set "
        + "WRITE_TIME=?,"
        + "PROGRESS=?,"
        + "WRITER=?,"
        + "ATTACHMENT_ID=?,"
        + "ATTACHMENT_NAME=?,"
        + "PERCENT=?"
        + " where SEQ_ID=?";
    }
    try {
      stmt = dbConn.prepareStatement(sql);
      stmt.setDate(1, detail.getWriteTime());
      stmt.setString(2,detail.getProgress());
      stmt.setString(3,detail.getWriter());
      stmt.setString(4, detail.getAttachmentId());
      stmt.setString(5, detail.getAttachmentName());
      stmt.setInt(6, detail.getPercent());
      stmt.setInt(7,detail.getSeqId());
      stmt.executeUpdate();
    }catch (Exception e) {
      throw e;
    }finally {
      T9DBUtility.close(stmt, null, log);
    }  
  }
  /***
   * 根据ID查询数据
   * @return
   * @throws Exception 
   */
  public List<T9WorkDetail> selectDetailId(Connection dbConn,int seqId) throws Exception {
    ResultSet rs = null;
    PreparedStatement stmt = null ; 
    T9WorkDetail detail = null;
    List<T9WorkDetail> list = new ArrayList<T9WorkDetail>();                                                                  
    String sql = null;
    String dbms = T9SysProps.getString(T9SysPropKeys.DBCONN_DBMS);
    if (dbms.equals(T9Const.DBMS_SQLSERVER)) {
      sql = "select " +
      "SEQ_ID," +
      "PLAN_ID," +
      "WRITE_TIME," +
      "PROGRESS," +
      "[PERCENT]," +
      "TYPE_FLAG," +
      "WRITER," +
      "ATTACHMENT_ID," +
      "ATTACHMENT_NAME" +
      " from work_detail where PLAN_ID=? and TYPE_FLAG=? order by seq_id asc";
    }else {
      sql = "select " +
      "SEQ_ID," +
      "PLAN_ID," +
      "WRITE_TIME," +
      "PROGRESS," +
      "PERCENT," +
      "TYPE_FLAG," +
      "WRITER," +
      "ATTACHMENT_ID," +
      "ATTACHMENT_NAME" +
      " from work_detail where PLAN_ID=? and TYPE_FLAG=? order by seq_id asc";
    }
    try {
      stmt = dbConn.prepareStatement(sql);
      stmt.setInt(1, seqId);
      stmt.setString(2, "0");
      rs = stmt.executeQuery();
      while (rs.next()) {
        detail = new T9WorkDetail(); 
        detail.setSeqId(rs.getInt("SEQ_ID"));
        detail.setPlanId(rs.getString("PLAN_ID"));
        detail.setWriteTime(rs.getDate("WRITE_TIME"));
        detail.setProgress(rs.getString("PROGRESS"));
        detail.setPercent(rs.getInt("PERCENT"));
        detail.setTypeFlag(rs.getString("TYPE_FLAG"));
        detail.setWriter(rs.getString("WRITER"));
        detail.setAttachmentId(rs.getString("ATTACHMENT_ID"));
        detail.setAttachmentName(rs.getString("ATTACHMENT_NAME"));
        list.add(detail);
      }
    }catch (Exception e) {
      throw e;
    }finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return list;  
  }
  
  /***
   * 根据ID查询数据
   * @return
   * @throws Exception 
   */
  public int sunNum(Connection dbConn,int percnt,int seqId) throws Exception {
    ResultSet rs = null;
    PreparedStatement stmt = null ; 
    int sun =0;
    String sql = null;
    String dbms = T9SysProps.getString(T9SysPropKeys.DBCONN_DBMS);
    if (dbms.equals(T9Const.DBMS_SQLSERVER)) {
      sql = "select [PERCENT] from work_detail where WRITER='" + percnt + "' and plan_id=" + seqId;
    }else {
      sql = "select PERCENT from work_detail where WRITER='" + percnt + "' and plan_id=" + seqId;
    }
    try {
      stmt = dbConn.prepareStatement(sql);
      rs = stmt.executeQuery();
      while (rs.next()) {
        sun += rs.getInt("PERCENT");
      }
    }catch (Exception e) {
      throw e;
    }finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return sun;  
  }
  /***
   * 根据ID查询数据
   * @return
   * @throws Exception 
   */
  public int sunNum2(Connection dbConn,int percnt,int seqId) throws Exception {
    ResultSet rs = null;
    PreparedStatement stmt = null ; 
    int sun =0;
    String sql = "select PERCENT from work_detail where WRITER='" + percnt + "' and plan_id=" + seqId + " order by seq_id asc";
    String dbms = T9SysProps.getString(T9SysPropKeys.DBCONN_DBMS);
    if (dbms.equals(T9Const.DBMS_SQLSERVER)) {
      sql = "select [PERCENT] from work_detail where WRITER='" + percnt + "' and plan_id=" + seqId + " order by seq_id asc";
    }else {
      sql = "select PERCENT from work_detail where WRITER='" + percnt + "' and plan_id=" + seqId + " order by seq_id asc";
    }
    try {
      stmt = dbConn.prepareStatement(sql);
      rs = stmt.executeQuery();
      while (rs.next()) {
        sun = rs.getInt("PERCENT");
      }
    }catch (Exception e) {
      throw e;
    }finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return sun;  
  }
  /***
   * 根据ID查询数据
   * @return
   * @throws Exception 
   */
  public int maxSunNum(Connection dbConn,int seqId) throws Exception {
    ResultSet rs = null;
    PreparedStatement stmt = null ; 
    int sun =0;
    String sql = null;
    String dbms = T9SysProps.getString(T9SysPropKeys.DBCONN_DBMS);
    if (dbms.equals(T9Const.DBMS_SQLSERVER)) {
      sql = "select max([PERCENT]),writer from work_detail where plan_id='" + seqId +"' GROUP BY writer";
    }else {
      sql = "select max(PERCENT),writer from work_detail where plan_id='" + seqId +"' GROUP BY writer";
    }
    try {
      stmt = dbConn.prepareStatement(sql);
      rs = stmt.executeQuery();
      while (rs.next()) {
        sun += rs.getInt(1);
      }
    }catch (Exception e) {
      throw e;
    }finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return sun;  
  }
}
