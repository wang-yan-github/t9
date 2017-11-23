package t9.core.funcs.system.data;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import t9.core.funcs.dept.data.T9Department;
import t9.core.funcs.person.data.T9UserPriv;
import t9.core.util.db.T9ORM;

public class T9DepartmentCache {
  private static Map<Integer , T9Department> departmentMap = new HashMap();
  private static List<T9Department> departmentList = new ArrayList();
  public static Map<Integer , T9Department> getDepartmentMap() {
    return departmentMap;
  }
  public static List<T9Department> getDepartmentList() {
    return departmentList;
  }
  public static List<T9Department> getDepartmentListCache(Connection conn) throws Exception {
    if (departmentList.isEmpty()) {
      T9ORM orm = new T9ORM();
      departmentList = orm.loadListSingle(conn, T9Department.class, new HashMap<String, String>());
    }
    return departmentList;
  }
  public static T9Department getDepartmentCache(Connection conn , Integer s) throws Exception {
    T9Department d = departmentMap.get(s);
    if (d == null) {
      Map<String,Integer> query = new HashMap<String,Integer>();
      query.put("SEQ_ID", s);
      T9ORM r = new T9ORM();
      d = (T9Department) r.loadObjSingle(conn, T9Department.class,
          query);
      if (d != null) {
        departmentMap.put(s , d);
      }
    }
    return d;
  }
  public static T9Department getParentDepartmentCache(Connection conn , Integer s) throws Exception {
    T9Department d = getDepartmentCache( conn ,  s);
    if (d != null && d.getDeptParent() != 0) {
      T9Department d2 = getDepartmentCache( conn ,  d.getDeptParent()); 
      return d2;
    }
    if (d.getDeptParent() == 0 ) {
      return new T9Department();
    }
    return d;
  }
  
  public static void removeAll() {
    departmentMap.clear();
    departmentList.clear();
  }
}
