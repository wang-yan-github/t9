package t9.subsys.oa.vehicle.act;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import t9.core.autorun.T9AutoRun;
import t9.core.funcs.sms.data.T9SmsBack;
import t9.core.funcs.sms.logic.T9SmsUtil;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;

public class T9VehicleAutoService extends T9AutoRun {
  public void doTask() {
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      String sql = " SELECT seq_id, v_num, insurance_date, before_day, last_insurance_date, insurance_flag "
                 + " FROM vehicle v "
                 + " where insurance_DATE is not null "
                 + " and before_day is not null "
                 + " and before_day > 0 ";
      
      Connection conn = getRequestDbConn().getSysDbConn();
      ps = conn.prepareStatement(sql);
      rs = ps.executeQuery();
      String seqId = "";
      
      while(rs.next()){
        Date insuranceDate = rs.getDate("insurance_date");
        int beforeDay = rs.getInt("before_day");
        
        Date date = new Date();
        insuranceDate.setYear( date.getYear());
        Date remindDate = T9Utility.getDayBefore(insuranceDate, beforeDay);
        
        if(date.after(remindDate) && date.before(insuranceDate)){
          Date lastInsuranceDate = rs.getDate("last_insurance_date");
          int insuranceFlag = rs.getInt("insurance_flag");
          if(lastInsuranceDate != null && date.after(T9Utility.getDayAfter(lastInsuranceDate, 200))){
            seqId += rs.getInt("seq_id") + ",";
          }
          else if(insuranceFlag == 0){
            seqId += rs.getInt("seq_id") + ",";
          }
        }
      }
      if(!T9Utility.isNullorEmpty(seqId)){
        seqId = seqId.substring(0, seqId.length() - 1);
      
        sql = " SELECT OPERATOR_ID FROM vehicle_operator ";
        ps = conn.prepareStatement(sql);
        rs = ps.executeQuery();
        String toId = "";
        if(rs.next()){
          toId = rs.getString("OPERATOR_ID");
        }
        
        String remindUrl = "/subsys/oa/vehicle/manage/vehicleInfo.jsp?seqId=" + seqId;
        String smsContent = "请查看车辆保险提示信息！";
        this.doSmsBackTime(conn, smsContent, 1, toId, "9", remindUrl, new Date());
        
        sql = " update vehicle set last_insurance_DATE = ?,insurance_flag = 0 where seq_id in (" + seqId + ") ";
        ps = conn.prepareStatement(sql);
        ps.setTimestamp(1, T9Utility.parseTimeStamp());
        ps.executeUpdate();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    finally{
      T9DBUtility.close(ps, rs, null);
    }
  }
  
  /**
   * 短信提醒(带时间)
   * 
   * @param conn
   * @param content
   * @param fromId
   * @param toId
   * @param type
   * @param remindUrl
   * @param sendDate
   * @throws Exception
   */
  public static void doSmsBackTime(Connection conn, String content, int fromId, String toId, String type, String remindUrl, Date sendDate)
      throws Exception {
    T9SmsBack sb = new T9SmsBack();
    sb.setContent(content);
    sb.setFromId(fromId);
    sb.setToId(toId);
    sb.setSmsType(type);
    sb.setRemindUrl(remindUrl);
    sb.setSendDate(sendDate);
    T9SmsUtil.smsBack(conn, sb);
  }
}
