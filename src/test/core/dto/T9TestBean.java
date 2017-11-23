package test.core.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class T9TestBean {
  private int field0 = 0;
  private Integer field1 = null;
  private double field2 = 0;
  private Double field3 = null;
  private String field4 = null;
  private Date field5 = null;
  private java.sql.Date field6 = null;
  private List<T9InnerBean1> bean1List = new ArrayList<T9InnerBean1>();
  
  public void addBean1(T9InnerBean1 bean) {
    bean1List.add(bean);
  }
  public Iterator<T9InnerBean1> itBean1() {
    return bean1List.iterator();
  }

  public int getField0() {
    return field0;
  }
  public void setField0(int field0) {
    this.field0 = field0;
  }
  public Integer getField1() {
    return field1;
  }
  public void setField1(Integer field1) {
    this.field1 = field1;
  }
  public double getField2() {
    return field2;
  }
  public void setField2(double field2) {
    this.field2 = field2;
  }
  public Double getField3() {
    return field3;
  }
  public void setField3(Double field3) {
    this.field3 = field3;
  }
  public String getField4() {
    return field4;
  }
  public void setField4(String field4) {
    this.field4 = field4;
  }
  public Date getField5() {
    return field5;
  }
  public void setField5(Date field5) {
    this.field5 = field5;
  }
  public java.sql.Date getField6() {
    return field6;
  }
  public void setField6(java.sql.Date field6) {
    this.field6 = field6;
  }
}
