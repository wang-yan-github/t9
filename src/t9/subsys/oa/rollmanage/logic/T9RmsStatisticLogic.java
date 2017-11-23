package t9.subsys.oa.rollmanage.logic;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;

import org.apache.log4j.Logger;

import t9.core.data.T9PageDataList;
import t9.core.data.T9PageQueryParam;
import t9.core.funcs.person.data.T9Person;
import t9.core.load.T9PageLoader;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.form.T9FOM;

public class T9RmsStatisticLogic {
  private static Logger log = Logger.getLogger("t9.subsys.oa.rollmanage.logic.T9RmsRollLogic.java");

  /**
   * 查看文件
   * @param dbConn
   * @param request
   * @param person
   * @return
   * @throws Exception
   */
   public String getRmsFileJosn(Connection dbConn, Map request, T9Person person) throws Exception {
     String sql="SELECT SEQ_ID," +
                     "FILE_CODE," +
                     "FILE_TITLE," +
                     "SECRET," +
                     "SEND_UNIT," +
                     "SEND_DATE," +
                     "URGENCY from RMS_FILE where not ROLL_ID = 0 and ( DEL_USER = '' or DEL_USER is null )";

     T9PageQueryParam queryParam = (T9PageQueryParam) T9FOM.build(request);
     T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn, queryParam, sql);
     return pageDataList.toJson();
   }
   
   /**
    * 获取借阅次数
    * @param dbConn
    * @param seqId
    * @return
    * @throws Exception
    */
   public long getRmsLendCount(Connection dbConn, int seqId)
   throws Exception {
     Statement stmt = null;
     ResultSet rs = null;
     long count = 0;
     try {
       stmt = dbConn.createStatement();
       String sql = "SELECT count(*) FROM RMS_LEND WHERE FILE_ID = " + seqId;
       rs = stmt.executeQuery(sql);
       if (rs.next()) {
         count = rs.getLong(1);
       }
     } catch (Exception ex) {
       throw ex;
     } finally {
       T9DBUtility.close(stmt, rs, log);
     }
    return count;
  }
   
   /**
    * 获取文件个数
    * @param dbConn
    * @param seqId
    * @return
    * @throws Exception
    */
   public long getRmsRollCount(Connection dbConn, int seqId)
   throws Exception {
     Statement stmt = null;
     ResultSet rs = null;
     long count = 0;
     try {
       stmt = dbConn.createStatement();
       String sql = "SELECT count(*) FROM RMS_FILE WHERE ROLL_ID = " + seqId + " and (DEL_USER is null or DEL_USER = '')";
       rs = stmt.executeQuery(sql);
       if (rs.next()) {
         count = rs.getLong(1);
       }
     } catch (Exception ex) {
       throw ex;
     } finally {
       T9DBUtility.close(stmt, rs, log);
     }
    return count;
  }
   
   public String getRmsStatisticJson(Connection dbConn, Map request, String seqId) throws Exception {
     String sql  = "select "
                + "RMS_ROLL.SEQ_ID"
                + ",RMS_ROLL.ROLL_CODE"
                + ",RMS_ROLL.ROLL_NAME"
                + ",RMS_ROLL.ROOM_ID"
                + ",RMS_ROLL.CATEGORY_NO"
                + ",RMS_ROLL.CERTIFICATE_KIND"
                + ",RMS_ROLL.SECRET"
                + ",RMS_ROLL.STATUS"
                + " from RMS_ROLL left join RMS_ROLL_ROOM on RMS_ROLL.ROOM_ID = RMS_ROLL_ROOM.SEQ_ID where 1=1";
     if(!T9Utility.isNullorEmpty(seqId)){
       sql = sql + " and RMS_ROLL_ROOM.SEQ_ID=" + Integer.parseInt(seqId);
     }
     T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request);
     T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn,queryParam,sql);
     return pageDataList.toJson();
   }
}
