package t9.subsys.oa.profsys.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.apache.log4j.Logger;
import t9.core.funcs.calendar.logic.T9CalendarLogic;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.subsys.oa.profsys.data.T9ProjectMem;

public class T9ProjectMemLogic {
  private static Logger log = Logger.getLogger("t9.subsys.oa.profsys.act.T9ProjectMemLogic");
  public int addMem(Connection dbConn, T9ProjectMem mem) throws Exception {
    T9ORM orm = new T9ORM();
    orm.saveSingle(dbConn, mem);
    return T9CalendarLogic.getMaSeqId(dbConn, "PROJECT_MEM");
  }
  public void updateMem(Connection dbConn,  T9ProjectMem mem) throws Exception {
    T9ORM orm = new T9ORM();
    orm.updateSingle(dbConn, mem);
    //return T9CalendarLogic.getMaSeqId(dbConn, "PROJECT");
  }
  public T9ProjectMem getMemById(Connection dbConn, String seqId) throws Exception {
    T9ORM orm = new T9ORM();
    T9ProjectMem mem = (T9ProjectMem) orm.loadObjSingle(dbConn, T9ProjectMem.class, Integer.parseInt(seqId));
    return mem;
  }
  public void deleteMemById(Connection dbConn,  String seqId) throws Exception {
    T9ORM orm = new T9ORM();
    orm.deleteSingle(dbConn, T9ProjectMem.class, Integer.parseInt(seqId));
  }
  /***
   * 根据条件查询数据的projId
   * @return
   * @throws Exception 
   */
  public static String queryMemToProjId(Connection dbConn,String memNum,String memPosition,String memName,String memSex,String memBirth,String memIdNum,String projMemType) throws Exception {
    String sql = "select PROJ_ID from PROJECT_MEM where PROJ_MEM_TYPE ='" + projMemType + "'";
    if (!T9Utility.isNullorEmpty(memNum)) {
      sql += " and MEM_NUM like '%" + T9Utility.encodeLike(memNum) + "%' " + T9DBUtility.escapeLike();
    }
    if (!T9Utility.isNullorEmpty(memPosition)) {
      sql += " and " + T9DBUtility.findInSet(memPosition, "MEM_POSITION");
    }
    if (!T9Utility.isNullorEmpty(memName)) {
      sql += " and MEM_NAME like '%" + T9Utility.encodeLike(memName) + "%' " + T9DBUtility.escapeLike();
    }
    if (!T9Utility.isNullorEmpty(memIdNum)) {
      sql += " and MEM_ID_NUM like '%" + T9Utility.encodeLike(memIdNum) + "%' " + T9DBUtility.escapeLike();
    }
    if (!T9Utility.isNullorEmpty(memSex)) {
      sql += " and MEM_SEX='" + memSex + "'";
    }
    if (!T9Utility.isNullorEmpty(memBirth)) {
      String str =  T9DBUtility.getDateFilter("MEM_BIRTH", memBirth, "=");
      sql += " and " + str;
    }
    sql = sql + " group by PROJ_ID";
    PreparedStatement ps = null;
    ResultSet rs = null;
    ps = dbConn.prepareStatement(sql);
    rs = ps.executeQuery();
    String projId = "";
    while (rs.next()) {
      if(!T9Utility.isNullorEmpty(rs.getString("PROJ_ID"))){
        projId += rs.getString("PROJ_ID") + ",";
      }
      
    }
    if (!T9Utility.isNullorEmpty(projId)) {
      projId = projId.substring(0,projId.length() - 1);
    }
    return projId;
  }

}
