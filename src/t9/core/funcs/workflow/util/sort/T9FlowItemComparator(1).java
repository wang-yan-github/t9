package t9.core.funcs.workflow.util.sort;

import java.util.Comparator;

import t9.core.funcs.workflow.data.T9FlowFormItem;

public class T9FlowItemComparator implements Comparator {

  public int compare(Object arg0, Object arg1) {
    // TODO Auto-generated method stub
    T9FlowFormItem item = (T9FlowFormItem) arg0;
    T9FlowFormItem item1 = (T9FlowFormItem) arg1;
    if(item.getItemId() < item1.getItemId()){
      return 0;
    }
    return 1;
  }

}
