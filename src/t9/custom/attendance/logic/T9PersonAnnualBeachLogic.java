package t9.custom.attendance.logic;

import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import t9.core.util.T9Utility;
import t9.core.util.db.T9ORM;
import t9.custom.attendance.data.T9PersonAnnualPara;
/**
 * 年休假批量设置
 * @author Administrator
 *
 */
public class T9PersonAnnualBeachLogic{
  
  /**
   * 年休假批量设置
   * @param dbConn
   * @param userIds
   * @param annualDays
   * @param changeDate
   * @throws Exception
   */
  public void insertBeanch(Connection dbConn, String userIds, int annualDays, Date changeDate ) throws Exception{
    if(!T9Utility.isNullorEmpty(userIds)){
      String[] userId = userIds.split(",");
      if(userId.length > 0){
         for(int i=0; i<userId.length; i++){
           T9PersonAnnualPara leave = new T9PersonAnnualPara();
           leave.setUserId(userId[i]);
           leave.setAnnualDays(annualDays);
           leave.setChangeDate(changeDate);
           int k = findPersonAnnualById(dbConn, userId[i]);
           if(k != 0){//如果存在则更新
             leave.setSeqId(k);
             updatePersonAnnual(dbConn, leave);
           }else{
             savePersonAnnual(dbConn, leave);
           }
         }
      }
    }
  }
  
  public void updatePersonAnnual(Connection dbConn,T9PersonAnnualPara leave) throws Exception {
    T9ORM orm = new T9ORM();
    orm.updateSingle(dbConn, leave);
  }
  
  public void savePersonAnnual(Connection dbConn,T9PersonAnnualPara leave) throws Exception {
    T9ORM orm = new T9ORM();
    orm.saveSingle(dbConn, leave);
  }
  
  public int findPersonAnnualById(Connection dbConn, String userId) throws Exception{
    T9ORM orm = new T9ORM();
    Map<String, String> map = new HashMap<String, String>();
    map.put("USER_ID", userId);
    T9PersonAnnualPara obj = (T9PersonAnnualPara)orm.loadObjSingle(dbConn, T9PersonAnnualPara.class, map);
    if(obj != null){
      return obj.getSeqId();
    }
    return 0;
  }
}
