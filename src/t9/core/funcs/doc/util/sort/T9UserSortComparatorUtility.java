package t9.core.funcs.doc.util.sort;

import java.util.Comparator;

import t9.core.funcs.person.data.T9Person;


public class T9UserSortComparatorUtility implements Comparator{
  private String formUserStr;
  public T9UserSortComparatorUtility(String formUserStr){
    this.formUserStr = formUserStr;
  }
  public int compare(Object arg0, Object arg1) {
    // TODO Auto-generated method stub
    T9Person p = (T9Person) arg0;
    T9Person p2 = (T9Person) arg1;
    int j = formUserStr.indexOf(p.getUserName());
    int k = formUserStr.indexOf(p2.getUserName());
    if( k > j){
      return 0;
    }
    return 1;
  }
  
}