package test.cy;

import java.util.ArrayList;
import java.util.List;

public class RefPc {
  private int i;
  private ArrayList<RefPojo2> slist;
  private List<Pojo1> slist2;
  public void setSlist(ArrayList<RefPojo2> slist) {
    this.slist = slist;
  }

  public ArrayList<RefPojo2> getSlist() {
    return slist;
  }

  public void setSlist2(List<Pojo1> slist2) {
    this.slist2 = slist2;
  }

  public List<Pojo1> getSlist2() {
    return slist2;
  }

  public void setI(int i) {
    this.i = i;
  }

  public int getI() {
    return i;
  }

}
