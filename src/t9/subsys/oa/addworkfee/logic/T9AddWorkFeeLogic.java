package t9.subsys.oa.addworkfee.logic;

import java.sql.Connection;
import java.util.List;

import t9.core.util.T9Utility;
import t9.subsys.oa.addworkfee.data.T9Calendary;
import t9.subsys.oa.addworkfee.data.T9ChangeRest;
import t9.subsys.oa.addworkfee.data.T9DateJuge;
import t9.subsys.oa.addworkfee.data.T9Festival;

/**
 * 加班费计算
 * @author Administrator
 *
 */
public class T9AddWorkFeeLogic{
  
  private T9ChangeRestLogic restLogic = new T9ChangeRestLogic();
  private T9FestivalLogic festLogic = new T9FestivalLogic();
  private T9RoleBaseFeeLogic feeLogic = new T9RoleBaseFeeLogic();
  private T9OndutyLogic dutyLogic = new T9OndutyLogic();
  
  /**
   * 计算加班费 1.平时， 2.周末  3.节假日
   * @param conn
   * @param date
   * @return
   * @throws Exception 
   */
  public String accountAddWorkFee(Connection conn, String date, String begin, String end, int roleId) throws Exception{
    String year = "";
    if(T9Utility.isNullorEmpty(date)){
      year = T9Utility.getCurDateTimeStr("yyyy");
    }else{
      year = date.substring(0,date.indexOf("-"));
    }
    double cha = T9Calendary.getHourDiff(begin, end, T9Calendary.PATTEN_SECOND);
    List<T9Festival> festList = festLogic.findFestival(conn, year); //某一年所有的节假日    boolean isFest = T9Calendary.isFestival(festList, date, T9Calendary.PATTERN); //是节假日
    boolean  isWeek = T9Calendary.isWeekend(date, "yyyy-MM-dd");        //是周末    List<T9ChangeRest> restList = restLogic.findChangeRestList(conn, year); //某一年的所有的调休
    boolean isRest = T9Calendary.isChangeRest(restList, date, "yyyy-MM-dd");//是调休    T9DateJuge ju = new T9DateJuge();
    ju.setTimeDiff(cha);
    double fee = 0;
    if(isFest){
      ju.setDateType(3);
      fee = feeLogic.getMoney(conn, 3, roleId) * cha;
    }else if(isWeek){
      if(isRest){
        ju.setDateType(1);
        fee = feeLogic.getMoney(conn, 1, roleId) * cha;
      }else{
        ju.setDateType(2);
        fee = feeLogic.getMoney(conn, 2, roleId) * cha;
      }
    }else{
      if(isRest){
        ju.setDateType(2);
        fee = feeLogic.getMoney(conn, 2, roleId) * cha;
      }
      ju.setDateType(1);
      fee = feeLogic.getMoney(conn, 1, roleId) * cha;
    }
    ju.setTotalFee(T9Calendary.round(fee, null));
    return ju.toJson();
  }
  
  /**
   * 计算值班费 1.平时， 2.周末  3.节假日

   * @param conn
   * @param date
   * @return
   * @throws Exception 
   */
  public String accountAddDutyFee(Connection conn, String date, String begin, String end, int roleId) throws Exception{
    String year = "";
    if(T9Utility.isNullorEmpty(date)){
      year = T9Utility.getCurDateTimeStr("yyyy");
    }else{
      year = date.substring(0,date.indexOf("-"));
    }
    double cha = T9Calendary.getHourDiff(begin, end, T9Calendary.PATTEN_SECOND);
    List<T9Festival> festList = festLogic.findFestival(conn, year); //某一年所有的节假日    boolean isFest = T9Calendary.isFestival(festList, date, T9Calendary.PATTERN); //是节假日
    boolean  isWeek = T9Calendary.isWeekend(date, "yyyy-MM-dd");        //是周末    List<T9ChangeRest> restList = restLogic.findChangeRestList(conn, year); //某一年的所有的调休
    boolean isRest = T9Calendary.isChangeRest(restList, date, "yyyy-MM-dd");//是调休    T9DateJuge ju = new T9DateJuge();
    ju.setTimeDiff(cha);
    double fee = 0;
    if(isFest){
      ju.setDateType(3);
      fee = dutyLogic.getMoney(conn, 3, roleId) * cha;
    }else if(isWeek){
      if(isRest){
        ju.setDateType(1);
        fee = dutyLogic.getMoney(conn, 1, roleId) * cha;
      }else{
        ju.setDateType(2);
        fee = dutyLogic.getMoney(conn, 2, roleId) * cha;
      }
    }else{
      if(isRest){
        ju.setDateType(2);
        fee = dutyLogic.getMoney(conn, 2, roleId) * cha;
      }
      ju.setDateType(1);
      fee = dutyLogic.getMoney(conn, 1, roleId) * cha;
    }
    ju.setTotalFee(T9Calendary.round(fee, null));
    return ju.toJson();
  }
}
