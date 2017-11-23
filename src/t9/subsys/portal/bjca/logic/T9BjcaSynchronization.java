package t9.subsys.portal.bjca.logic;

import java.sql.Connection;

import com.bjca.uums.client.bean.LoginInformation;
import com.bjca.uums.client.bean.PersonInformation;

import t9.subsys.portal.bjca.util.T9BjcaUtil;
import t9.user.api.core.db.T9DbconnWrap;
/**
 * Bjca数据同步接口
 * @author Think
 *
 */
public class T9BjcaSynchronization {

  /**
   * Bjca数据同步方法
   * @param operateID 操作符
   * @param operateCode 编码(用户的32位编码)
   * @param operateType(类型，一般不需要考虑)
   * @return
   */
  public boolean SynchronizedUserInfo(int operateID, String operateCode,
      String operateType)throws Exception{
    T9DbconnWrap dbUtil = new T9DbconnWrap();
    boolean result = false;
    Connection conn = null;
    try {
      conn = dbUtil.getSysDbConn();
      if (operateID == 11 || operateID == 12 || operateID == 13) {
        // 增加用户
        if(operateID == 11 || operateID == 12){
          PersonInformation personInfo = T9BjcaUtil.getPersonInformationById(operateCode);
          LoginInformation loginInfo = T9BjcaUtil.getLoginUserInfoByUserId(operateCode);
          //System.out.println(T9BjcaUtil.isExisPerson(conn, operateCode));
          if(T9BjcaUtil.isExisPerson(conn, operateCode) != -1 || T9BjcaUtil.isExisPersonByAdmin(conn, loginInfo.getLoginName()) != -1){
            T9BjcaUtil.modifyUser(conn, personInfo, loginInfo);
          }else{
            T9BjcaUtil.addUser(conn, personInfo, loginInfo);
          }
        }else{
          if(T9BjcaUtil.isExisPerson(conn, operateCode) != -1){
            T9BjcaUtil.deleteUser(conn, operateCode);
          }
        }
        result = true;
      }else if(operateID == 41 || operateID == 42 || operateID == 43){
        // 增加部门
        if(operateID == 41 || operateID == 42){
         
          if(T9BjcaUtil.getT9DeptIdBySynDeptCode(conn, operateCode) != -1){
            T9BjcaUtil.modifyDept(conn, operateCode);
          }else{
            T9BjcaUtil.addDept(conn, operateCode);
          }
        }else{
          if(T9BjcaUtil.getT9DeptIdBySynDeptCode(conn, operateCode) != -1){
            T9BjcaUtil.deleteDept(conn, operateCode);
          }
        }
        result = true;
      }
      conn.commit();
    } catch (Exception e) {
      conn.rollback();
      throw e;
    } finally {
      dbUtil.closeAllDbConns();
    }
    return result;
  }
}
