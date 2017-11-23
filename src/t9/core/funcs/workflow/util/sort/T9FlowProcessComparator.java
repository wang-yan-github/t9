package t9.core.funcs.workflow.util.sort;

import java.util.Comparator;

import t9.core.funcs.workflow.data.T9FlowProcess;

public class T9FlowProcessComparator implements Comparator{
  public int compare(Object arg0, Object arg1) {
    // TODO Auto-generated method stub
    T9FlowProcess fp1 = (T9FlowProcess) arg0;
    T9FlowProcess fp2 = (T9FlowProcess) arg1;
    if( fp1.getPrcsId() < fp2.getPrcsId()){
      return 0;
    }
    return 1;
  }
  
}
