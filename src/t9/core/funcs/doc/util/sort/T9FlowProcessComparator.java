package t9.core.funcs.doc.util.sort;

import java.util.Comparator;

import t9.core.funcs.doc.data.T9DocFlowProcess;

public class T9FlowProcessComparator implements Comparator{
  public int compare(Object arg0, Object arg1) {
    // TODO Auto-generated method stub
    T9DocFlowProcess fp1 = (T9DocFlowProcess) arg0;
    T9DocFlowProcess fp2 = (T9DocFlowProcess) arg1;
    if( fp1.getPrcsId() < fp2.getPrcsId()){
      return 0;
    }
    return 1;
  }
  
}
