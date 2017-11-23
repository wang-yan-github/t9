package t9.core.module.org_select.act;

import java.util.Comparator;

import t9.core.funcs.dept.data.T9Department;
import t9.core.util.T9Utility;

public class T9DepartmentComparator implements Comparator {
  public int compare(Object arg0, Object arg1) {
    // TODO Auto-generated method stub
    T9Department d1 = (T9Department)arg0;
    T9Department d2 = (T9Department)arg1;
    String deptNo1 = d1.getDeptNo();
    String deptNo2 = d2.getDeptNo();
    if (T9Utility.isInteger(deptNo1) 
        && T9Utility.isInteger(deptNo2)) {
      int dept1 = Integer.parseInt(deptNo1);
      int dept2 = Integer.parseInt(deptNo2);
      if (dept1 == dept2) {
        String deptName1 = d1.getDeptName();
        String deptName2 = d2.getDeptName();
        if (deptName2.compareTo(deptName1) > 0) {
          return 0;
        } else {
          return 1;
        }
      }
      if (dept1 < dept2) {
        return 0;
      } else {
        return 1;
      }
    }
    return 1;
  }

}
