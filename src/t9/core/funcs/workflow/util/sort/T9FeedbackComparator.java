package t9.core.funcs.workflow.util.sort;

import java.util.Comparator;

import t9.core.funcs.workflow.data.T9FlowRunFeedback;

public class T9FeedbackComparator implements Comparator {

  public int compare(Object arg0, Object arg1) {
    // TODO Auto-generated method stub
    T9FlowRunFeedback feedback = (T9FlowRunFeedback) arg0;
    T9FlowRunFeedback feedback1 = (T9FlowRunFeedback) arg1;
    if( feedback.getEditTime().compareTo(feedback1.getEditTime()) > 0 ){
      return 0;
    }
    return 1;
  }

}
