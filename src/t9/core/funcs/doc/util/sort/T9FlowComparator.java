package t9.core.funcs.doc.util.sort;

import java.util.Comparator;
import java.util.Map;

public class T9FlowComparator implements Comparator {
  private String groupFld;
  private String groupSort;
  
  public T9FlowComparator(String groupFld , String groupSort){
    this.groupFld = groupFld;
    this.groupSort = groupSort;
  }
  
  public int compare(Object arg0, Object arg1) {
    // TODO Auto-generated method stub
    Map flow = (Map) arg0;
    Map flow2 = (Map) arg1;
    if (flow == null || flow2 == null) {
      return 0;
    }
    if ("runId".equals(groupFld)) {
      String runIds =(String) flow.get("runId");
      int runId = Integer.parseInt(runIds);
      String runId2s =(String) flow2.get("runId");
      int runId2 = Integer.parseInt(runId2s);
      if ("ASC".equals(groupSort)) {
        if (runId > runId2) {
          return 1;
        } else {
          return 0;
        }
      } else {
        if (runId < runId2) {
          return 1;
        } else {
          return 0;
        }
      }
    } else {
      String str =(String) flow.get(groupFld);
      String str2 =(String) flow2.get(groupFld);
      if ("ASC".equals(groupSort)) {
        if (str.compareTo(str2) > 0) {
          return 1;
        } else {
          return 0;
        }
      } else {
        if (str.compareTo(str2) < 0) {
          return 1;
        } else {
          return 0;
        }
      }
    }
  }

}
