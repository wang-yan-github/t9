package t9.custom.attendance.logic;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import t9.core.util.db.T9ORM;
import t9.custom.attendance.data.T9PersonAnnualPara;

public class T9PersonAnnualParaLogic {
  public void addAnnualLeavePara(Connection dbConn, T9PersonAnnualPara leave) throws Exception {
    T9ORM orm = new T9ORM();
    orm.saveSingle(dbConn, leave);  
  }
  public void updateAnnualLeavePara(Connection dbConn,T9PersonAnnualPara leave) throws Exception {
    T9ORM orm = new T9ORM();
    orm.updateSingle(dbConn, leave);
  }
  public List<T9PersonAnnualPara>  selectAnnualLeavePara(Connection dbConn,String[] str) throws Exception {
    List<T9PersonAnnualPara> leaveList = new ArrayList<T9PersonAnnualPara>();
    T9ORM orm = new T9ORM();
    leaveList = orm.loadListSingle(dbConn, T9PersonAnnualPara.class, str);
    return leaveList;
  }
}
