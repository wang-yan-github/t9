package t9.core.funcs.message.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import t9.core.autorun.T9AutoRun;
import t9.core.funcs.system.ispirit.n12.org.logic.T9IsPiritLogic;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;


public class smsAuto extends T9AutoRun{
  private static final Logger log = Logger.getLogger("t9.core.funcs.message.data.smsAuto");

  //定时发送提醒  以及 微讯
  @Override
 
  public void doTask() throws Exception {
    Connection conn =getRequestDbConn().getSysDbConn();
    isRemindSms(conn);  //检查提醒事务
    isRemindMessage(conn);  //检查微讯
  }
  
  /**
   * 是否有为提醒的
   * @param conn
   * @param userId
   * @return
   * @throws Exception
   */
  public void isRemindSms(Connection conn) throws Exception{
    int result = 0;
    PreparedStatement ps = null;
    ResultSet rs = null;
    String dateFiler = T9DBUtility.getDateFilter("T0.SEND_TIME", T9Utility.getDateTimeStr(new Date()), "<=");
    String dbDateFremind = T9DBUtility.getDateFilter("T1.REMIND_TIME", T9Utility.getCurDateTimeStr(), " <= ");
    String sql = "SELECT to_id FROM SMS T1 ,SMS_BODY T0 WHERE  REMIND_FLAG = '1' AND  T1.BODY_SEQ_ID= T0.SEQ_ID  " +
        "and DELETE_FLAG in (0, 2) " +
        "AND " + dateFiler +
        " AND (T1.REMIND_TIME IS NULL OR " + dbDateFremind + ")";

    try{
      ps = conn.prepareStatement(sql);
      rs = ps.executeQuery();
      while(rs.next()){
        String uid = rs.getString(1);
        if(uid.compareToIgnoreCase("1")>=0){
          
           T9IsPiritLogic.setUserSmsRemind(uid);
        }
        //log.info("***"+uid);
          
      }
    } catch (Exception e){
      e.printStackTrace();
      throw e;
    } finally {
      T9DBUtility.close(ps, rs, null);
    }
   
  }

  
  /**
   * 是否有微讯
   * @param conn
   * @param userId
   * @return
   * @throws Exception
   */
  public void isRemindMessage(Connection conn) throws Exception{
    int result = 0;
    PreparedStatement ps = null;
    ResultSet rs = null;
    String dateFiler = T9DBUtility.getDateFilter("T0.SEND_TIME", T9Utility.getDateTimeStr(new Date()), "<=");
    String dbDateFremind = T9DBUtility.getDateFilter("T1.REMIND_TIME", T9Utility.getCurDateTimeStr(), " <= ");
   try{
   String sql = "SELECT to_id FROM MESSAGE T1 ,MESSAGE_BODY T0 WHERE  REMIND_FLAG = '1' AND  T1.BODY_SEQ_ID= T0.SEQ_ID  " +
      "and DELETE_FLAG in (0, 2) " +
      "AND " + dateFiler +
      " AND (T1.REMIND_TIME IS NULL OR " + dbDateFremind + ")";
      ps = conn.prepareStatement(sql);
      rs = ps.executeQuery();
      while(rs.next()){
        String uid = rs.getString(1);
        if(uid.compareToIgnoreCase("1")>=0){
           T9IsPiritLogic.setUserMessageRemind(uid);
        }
   //   System.out.println(uid);
        
      }
      
    } catch (Exception e){
      e.printStackTrace();
      throw e;
    } finally {
      T9DBUtility.close(ps, rs, null);
    }
   
  }

 
}
