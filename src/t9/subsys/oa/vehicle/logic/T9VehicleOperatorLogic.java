package t9.subsys.oa.vehicle.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.person.logic.T9PersonLogic;
import t9.core.funcs.system.logic.T9SystemService;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.subsys.oa.vehicle.data.T9VehicleOperator;

public class T9VehicleOperatorLogic {
  private static Logger log = Logger.getLogger("t9.core.act.action.T9SysMenuLog");
  public void addOperator(Connection dbConn, T9VehicleOperator vcOperator) throws Exception {
    T9ORM orm = new T9ORM();
    orm.saveSingle(dbConn, vcOperator);  
  }
  public void updateOperator(Connection dbConn, T9VehicleOperator vcOperator) throws Exception {
    T9ORM orm = new T9ORM();
    orm.updateSingle(dbConn, vcOperator) ;
  }
  public ArrayList<T9VehicleOperator> selectOperator(Connection dbConn, Map map) throws Exception{
    T9ORM orm = new T9ORM();
    ArrayList<T9VehicleOperator> operatorList = (ArrayList<T9VehicleOperator>)orm.loadListSingle(dbConn, T9VehicleOperator.class, map);
    return operatorList;
  }

  public void updateOperator(Connection dbConn ,String operatorId,String opertorName) throws Exception{
    Statement stmt = null;
    String sql = "update VEHICLE_OPERATOR set OPERATOR_ID = '" + operatorId + "'";
    try {
      stmt = dbConn.createStatement();
      stmt.executeUpdate(sql);
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stmt, null, log);
    }
  }
  /*
   * 根据id字符串得到name字符串


   */
  public  List<T9Person>  getPersonByIds(Connection dbConn,Map map)throws Exception{
    T9PersonLogic tpl = new T9PersonLogic();
    String ids = getOperatorIds(dbConn);
    List<T9Person> personList = new ArrayList<T9Person>();
    T9ORM orm = new T9ORM();
    if(!ids.equals("")){
      String[] str = {"SEQ_ID in (" + ids + ")"};
      personList =orm.loadListSingle(dbConn, T9Person.class, str );
    }
    return personList;
  }

  public String getOperatorIds(Connection dbConn) throws Exception{
    Statement stmt = null;
    ResultSet rs = null;
    String ids = "";
    String sql = "select OPERATOR_ID from VEHICLE_OPERATOR ";
    try {
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(sql);
      if(rs.next()){
        if(!T9Utility.isNullorEmpty(rs.getString(1))){
          ids =rs.getString(1);
        }
      }
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stmt, null, log);
    }
    return ids;
  }
  /**
  * 调度人员ID串-lz
  * 
  * */
  public static String selectPerson(Connection dbConn) throws Exception{
    PreparedStatement stmt = null;
    ResultSet rs = null;
    String seqIdStr = "";//符合条件的ID串
    String nameStr = "";//名字串
    String seqId = selectId(dbConn);
//    and us.user_state=1
    if(T9Utility.isNullorEmpty(seqId)){
      seqId = "0";
    }
    synchronized(T9SystemService.onlineSync) {
    String sql = "select us.user_id as userId from user_online us where us.user_id in (" + seqId + ") GROUP by us.user_id ";
    try {
      stmt = dbConn.prepareStatement(sql);
      rs = stmt.executeQuery();
      while(rs.next()){
        seqIdStr += rs.getString("userId") + ",";
      }
      if (!T9Utility.isNullorEmpty(seqIdStr)) {
        seqIdStr = seqIdStr.substring(0,seqIdStr.length() - 1);
      }
      nameStr = getName(dbConn,seqIdStr);
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stmt, null, log);
    }
    dbConn.commit();
    }
    return nameStr;
  } 
  /**
   * 调度人员ID串-lz
   * 
   * */
  public static String getName(Connection dbConn,String seqId) throws Exception {
    if (T9Utility.isNullorEmpty(seqId)) {
      seqId = "0";
    }
    String sql = "select user_name from  person where seq_id in (" + seqId + ")";
    PreparedStatement ps = null;
    ResultSet rs = null;
    String name = "";
    try{
      ps = dbConn.prepareStatement(sql);
      rs = ps.executeQuery();
      while (rs.next()) {
        name += rs.getString("user_name") + ",";
      }
      if (name.length() > 0) {
        name = name.substring(0,name.length()-1);
      }
    }catch (Exception e) {
      throw e;
    }finally {
      T9DBUtility.close(ps, rs, null);
    }
    return name;
  }

  /**
   * 调度人员ID串-lz
   * 
   * */
  public static String selectId(Connection dbConn) throws Exception{
    PreparedStatement stmt = null;
    ResultSet rs = null;
    String seqIdStr = "";
    String sql = "select operator_id from vehicle_operator";
    try {
      stmt = dbConn.prepareStatement(sql);
      rs = stmt.executeQuery();
      if (rs.next()){
        seqIdStr = rs.getString("operator_id");
      }
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stmt, null, log);
    }
    return seqIdStr;
  }
}
