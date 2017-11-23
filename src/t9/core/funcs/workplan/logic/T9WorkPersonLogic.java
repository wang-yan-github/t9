package t9.core.funcs.workplan.logic;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import t9.core.funcs.workplan.data.T9WorkPerson;
import t9.core.util.db.T9DBUtility;
public class T9WorkPersonLogic {
  private static Logger log = Logger.getLogger("t9.core.funcs.workplan.act.T9PlanWorkAct");
  /***
   * 根据ID查询数据
   * @return
   * @throws Exception 
   */
  public List<T9WorkPerson> selectPerson(Connection dbConn,int seqId,String name) throws Exception {
    ResultSet rs = null;
    PreparedStatement stmt = null ; 
    T9WorkPerson person = null;
    List<T9WorkPerson> list = new ArrayList<T9WorkPerson>();                                                                  
    String sql = "select "
      + "SEQ_ID,"
      + "PLAN_ID,"
      + "PUSER_ID,"
      + "PBEGEI_DATE,"
      + "PEND_DATE,"
      + "PPLAN_CONTENT,"
      + "PUSE_RESOURCE,"
      + "ATTACHMENT_ID,"
      + "ATTACHMENT_NAME"
      +" from work_person where PLAN_ID=? and PUSER_ID=? order by SEQ_ID"; 
    try {
      stmt = dbConn.prepareStatement(sql);
      stmt.setInt(1, seqId);
      stmt.setString(2,name);
      rs = stmt.executeQuery();
      while (rs.next()) {
        person = new T9WorkPerson(); 
        person.setSeqId(rs.getInt("SEQ_ID"));
        person.setPlanId(rs.getInt("PLAN_ID"));
        person.setPuserId(rs.getString("PUSER_ID"));
        person.setPbegeiDate(rs.getDate("PBEGEI_DATE"));
        person.setPendDate(rs.getDate("PEND_DATE"));
        person.setPplanContent(rs.getString("PPLAN_CONTENT"));
        person.setPuseResource(rs.getString("PUSE_RESOURCE"));
        person.setAttachmentId(rs.getString("ATTACHMENT_ID"));
        person.setAttachmentName(rs.getString("ATTACHMENT_NAME"));
        list.add(person);
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
  public void addPerson(Connection dbConn,T9WorkPerson person) throws Exception {
    PreparedStatement stmt = null ; 
    String sql = "insert into work_person(PLAN_ID,PUSER_ID,PBEGEI_DATE,PEND_DATE,PPLAN_CONTENT,PUSE_RESOURCE,ATTACHMENT_ID,ATTACHMENT_NAME)" +
    		" values(?,?,?,?,?,?,?,?)";
    try {
      stmt = dbConn.prepareStatement(sql);
      stmt.setInt(1, person.getPlanId());
      stmt.setString(2,person.getPuserId());
      stmt.setDate(3,person.getPbegeiDate());
      stmt.setDate(4,person.getPendDate());
      stmt.setString(5,person.getPplanContent());
      stmt.setString(6,person.getPuseResource());
      stmt.setString(7,person.getAttachmentId());
      stmt.setString(8,person.getAttachmentName());
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
  public void deletePerson(Connection dbConn,int seqId) throws Exception {
    PreparedStatement stmt = null ; 
    String sql = "delete from work_person where seq_id=?";
    try {
      stmt = dbConn.prepareStatement(sql);
      stmt.setInt(1, seqId);
      stmt.executeUpdate();
    }catch (Exception e) {
      throw e;
    }finally {
      T9DBUtility.close(stmt, null, log);
    }
  }
  /***
   * 查询数据
   * @return
   * @throws Exception 
   */
  public T9WorkPerson selectId(Connection dbConn,int seqId) throws Exception {
    ResultSet rs = null;
    PreparedStatement stmt = null ; 
    T9WorkPerson person = null;                                                                 
    String sql = "select "
      + "SEQ_ID,"
      + "PLAN_ID,"
      + "PUSER_ID,"
      + "PBEGEI_DATE,"
      + "PEND_DATE,"
      + "PPLAN_CONTENT,"
      + "PUSE_RESOURCE,"
      + "ATTACHMENT_ID,"
      + "ATTACHMENT_NAME"
      +" from work_person where SEQ_ID=?"; 
    try {
      stmt = dbConn.prepareStatement(sql);
      stmt.setInt(1, seqId);
      rs = stmt.executeQuery();
      while (rs.next()) {
        person = new T9WorkPerson(); 
        person.setSeqId(rs.getInt("SEQ_ID"));
        person.setPlanId(rs.getInt("PLAN_ID"));
        person.setPuserId(rs.getString("PUSER_ID"));
        person.setPbegeiDate(rs.getDate("PBEGEI_DATE"));
        person.setPendDate(rs.getDate("PEND_DATE"));
        person.setPplanContent(rs.getString("PPLAN_CONTENT"));
        person.setPuseResource(rs.getString("PUSE_RESOURCE"));
        person.setAttachmentId(rs.getString("ATTACHMENT_ID"));
        person.setAttachmentName(rs.getString("ATTACHMENT_NAME"));
      }
    }catch (Exception e) {
      throw e;
    }finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return person;
  }
  /***
   * 修改数据
   * @return
   * @throws Exception 
   */
  public void updatePerson(Connection dbConn,T9WorkPerson person) throws Exception {
    PreparedStatement stmt = null ;                                                                
    String sql = "update work_person "
      + "set PBEGEI_DATE=?,"
      + "PEND_DATE=?,"
      + "PPLAN_CONTENT=?,"
      + "PUSE_RESOURCE=?,"
      + "ATTACHMENT_ID=?,"
      + "ATTACHMENT_NAME=?"
      + " where SEQ_ID=?"; 
    try {
      stmt = dbConn.prepareStatement(sql);
      stmt.setDate(1,person.getPbegeiDate());
      stmt.setDate(2,person.getPendDate());
      stmt.setString(3,person.getPplanContent());
      stmt.setString(4,person.getPuseResource());
      stmt.setString(5,person.getAttachmentId());
      stmt.setString(6,person.getAttachmentName());
      stmt.setInt(7,person.getSeqId());
      stmt.executeUpdate();
    }catch (Exception e) {
      throw e;
    }finally {
      T9DBUtility.close(stmt, null, log);
    }
  }
  
}
