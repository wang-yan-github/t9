package t9.core.funcs.doc.util.sort;

import java.util.Comparator;

import t9.core.funcs.doc.data.T9DocFlowRunFeedback;

public class T9FeedbackComparator implements Comparator {

  public int compare(Object arg0, Object arg1) {
    // TODO Auto-generated method stub
    T9DocFlowRunFeedback feedback = (T9DocFlowRunFeedback) arg0;
    T9DocFlowRunFeedback feedback1 = (T9DocFlowRunFeedback) arg1;
    if( feedback.getEditTime().compareTo(feedback1.getEditTime()) > 0 ){
      return 0;
    }
    return 1;
  }

}
