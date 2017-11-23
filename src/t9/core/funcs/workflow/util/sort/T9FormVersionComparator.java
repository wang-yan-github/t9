package t9.core.funcs.workflow.util.sort;

import java.util.Comparator;

import t9.core.funcs.workflow.data.T9FlowFormType;

public class T9FormVersionComparator implements Comparator {
  public int compare(Object arg0, Object arg1) {
    T9FlowFormType ft1 = (T9FlowFormType) arg0;
    T9FlowFormType ft2 = (T9FlowFormType) arg0;
    
    if (ft1.getVersionNo() > ft2.getVersionNo()) {
      return 1;
    }
    return 0;
  }

}
