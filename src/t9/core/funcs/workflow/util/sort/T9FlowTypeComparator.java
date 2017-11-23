package t9.core.funcs.workflow.util.sort;

import java.util.Comparator;

import t9.core.funcs.workflow.data.T9FlowType;

public class T9FlowTypeComparator implements Comparator{
  public int compare(Object arg0, Object arg1) {
    // TODO Auto-generated method stub
    T9FlowType fp1 = (T9FlowType) arg0;
    T9FlowType fp2 = (T9FlowType) arg1;
    if( fp1.getFlowNo() < fp2.getFlowNo()){
      return 0;
    }
    return 1;
  }
}