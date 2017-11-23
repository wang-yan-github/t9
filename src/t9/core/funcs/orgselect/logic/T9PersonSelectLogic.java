package t9.core.funcs.orgselect.logic;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import t9.core.funcs.person.data.T9Person;
import t9.core.util.db.T9ORM;

public class T9PersonSelectLogic {
  /**
   * 根据部门Id取得人员列表
   * @param dbConn
   * @param deptId
   * @return
   * @throws Exception
   */
  public List<T9Person> getPersonsByDept(Connection dbConn, int deptId) throws Exception{
    List<T9Person> list = new ArrayList();
    Map filters = new HashMap();
    filters.put("DEPT_ID", deptId);
    T9ORM orm = new T9ORM();
    list  = orm.loadListSingle(dbConn ,T9Person.class , filters);
    return  list;
  }
}
