package t9.core.funcs.system.attendance.logic;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.person.logic.T9PersonLogic;
import t9.core.funcs.system.attendance.data.T9AttendManager;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;

public class T9AttendManagerLogic {
  private static Logger log = Logger.getLogger("t9.core.act.action.T9SysMenuLog");
  public void add_updateManager(Connection dbConn, T9AttendManager manager,Map map) throws Exception {
    T9ORM orm = new T9ORM();
    if(checkManagerIsnull(dbConn,map)){
      deleteManager(dbConn);
    }
    orm.saveSingle(dbConn, manager);  
  }
  public void deleteManager(Connection dbConn) throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    String sql = "delete from ATTEND_MANAGER";
    //System.out.println(sql);
    try {
      stmt = dbConn.createStatement();
      stmt.executeUpdate(sql);
    }catch(Exception ex) {
       throw ex;
    }finally {
      T9DBUtility.close(stmt, rs, log);
    }
  }
  /*
   * 得到id的字符串
   */
  public String selectManagerIds(Connection dbConn,Map map) throws Exception {
    T9ORM orm = new T9ORM();
    String ids = "";
    ArrayList<T9AttendManager> managerList = (ArrayList<T9AttendManager>) orm.loadListSingle(dbConn, T9AttendManager.class, map); 
    for (int i = 0; i < managerList.size(); i++) {
      T9AttendManager manager = managerList.get(i);
      if(manager.getManagers()!=null){
        ids = ids + manager.getManagers();
      }
    }
    //System.out.println(ids);
    return ids;
  }
  public boolean checkManagerIsnull(Connection dbConn,Map map) throws Exception {
    T9ORM orm = new T9ORM();
    ArrayList<T9AttendManager> managerList = (ArrayList<T9AttendManager>) orm.loadListSingle(dbConn, T9AttendManager.class, map);  
    if(managerList.size()>0){
      return true;
    }
    return false;
  }
  /*
   * 根据id字符串得到name字符串
   */
  public String getNamesByIds(Connection dbConn,Map map)throws Exception{
    String names = "";
    T9PersonLogic tpl = new T9PersonLogic();
    String ids = selectManagerIds(dbConn,map);
    //System.out.println(ids);
    names = tpl.getNameBySeqIdStr(ids , dbConn);
    return names;
  }
  /*
   * 根据id字符串得到name字符串

   */
  public  List<T9Person>  getPersonByIds(Connection dbConn,Map map)throws Exception{
    T9PersonLogic tpl = new T9PersonLogic();
    String ids = selectManagerIds(dbConn,map);
    List<T9Person> personList = new ArrayList<T9Person>();
    T9ORM orm = new T9ORM();
    if(!ids.equals("")){
      String[] str = {"SEQ_ID in (" + ids + ")"};
      personList =orm.loadListSingle(dbConn, T9Person.class, str );
    }
    return personList;
  }
}
