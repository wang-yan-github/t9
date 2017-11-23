package test.cy;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RefPojo {
 private List<String> slist;
 private List<Date> dlist;
 private ArrayList<String> adlist;
 private List<RefPc> rlist;
 private String[] s;
 private RefPc r;
public String[] getS() {
  return s;
}
public void setS(String[] s) {
  this.s = s;
}
public List<String> getSlist() {
  return slist;
}
public void setSlist(List<String> slist) {
  this.slist = slist;
}
public List<Date> getDlist() {
  return dlist;
}
public void setDlist(List<Date> dlist) {
  this.dlist = dlist;
}
public void setR(RefPc r) {
  this.r = r;
}
public RefPc getR() {
  return r;
}

public void setRlist(List<RefPc> rlist) {
  this.rlist = rlist;
}
public List<RefPc> getRlist() {
  return rlist;
}
public void setAdlist(List<String> adlist) {
  this.adlist = (ArrayList<String>) adlist;
}
public ArrayList<String> getAdlist() {
  return adlist;
}
 
}
