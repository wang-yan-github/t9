package t9.core.module.org_select.data;

import java.util.Comparator;
import java.util.Map;

import t9.core.funcs.person.data.T9UserOnline;

public class T9DeptCoparator implements Comparator<T9UserOnline> {
  private Map<Integer, String> deptNoMap = null;
  
  public T9DeptCoparator(Map<Integer, String> deptNoMap) {
    this.deptNoMap = deptNoMap;
  }
  public int compare(T9UserOnline u1, T9UserOnline u2) {
    String deptNo1 = deptNoMap.get(u1.getUserId());
    String deptNo2 = deptNoMap.get(u2.getUserId());
    
    return deptNo1.compareTo(deptNo2);
  }
}
