package t9.user.taiji.system;

import java.sql.Connection;
import java.sql.SQLException;

import t9.user.api.core.db.T9DbconnWrap;

public class T9SystemLogService {
//  public String[][] getLogByDate(String beginDate,String endDate){
//    Connection dbConn = null;
//    try {
//      dbConn = T9DbHelp.getSysDbConn();
//      T9FlowRunLogLogic logic = new T9FlowRunLogLogic();
//      String[][] list = logic.getLogByDate(dbConn, beginDate, endDate, 0, 0);
//      return list;
//    } catch (Exception e) {
//      e.printStackTrace();
//    } finally {
//      if (dbConn != null) {
//        try {
//          dbConn.close();
//        } catch (SQLException e) {
//          e.printStackTrace();
//        }
//      }
//    }
//    return null;
//  }
  
  public String[][] getLogByDate(int start,int length, String beginDate,String endDate){
    T9DbconnWrap dbUtil = new T9DbconnWrap();
    Connection dbConn = null;
    try {      
      dbConn = dbUtil.getSysDbConn();
      T9SystemLogLogic logic = new T9SystemLogLogic();
      String[][] list = logic.getLogByDate(dbConn, beginDate, endDate, start, length);
      return list;
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      dbUtil.closeAllDbConns();
    }
    return null;
  }
  public int getLogCountByDate(String beginDate,String endDate){
    T9DbconnWrap dbUtil = new T9DbconnWrap();
    Connection dbConn = null;
    int result = 0 ;
    try {
      dbConn = dbUtil.getSysDbConn();
      T9SystemLogLogic logic = new T9SystemLogLogic();
      result = logic.getLogCountByDate(dbConn, beginDate, endDate);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      dbUtil.closeAllDbConns();
    }
    return result;
  }
}
