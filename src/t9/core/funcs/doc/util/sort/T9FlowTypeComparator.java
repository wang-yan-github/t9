package t9.core.funcs.doc.util.sort;

import java.util.Comparator;

import t9.core.funcs.doc.data.T9DocFlowType;

public class T9FlowTypeComparator implements Comparator{
  public int compare(Object arg0, Object arg1) {
    // TODO Auto-generated method stub
    T9DocFlowType fp1 = (T9DocFlowType) arg0;
    T9DocFlowType fp2 = (T9DocFlowType) arg1;
    if( fp1.getFlowNo() < fp2.getFlowNo()){
      return 0;
    }
    return 1;
  }
}