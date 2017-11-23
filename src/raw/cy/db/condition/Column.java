package raw.cy.db.condition;

public class Column {
  private String field;
  private String oprator;
  private Object value;
  private Object value2;
  public String getField() {
    return field;
  }
  public void setField(String field) {
    this.field = field;
  }
  public String getOprator() {
    return oprator;
  }
  public void setOprator(String oprator) {
    this.oprator = oprator;
  }
  public Object getValue() {
    return value;
  }
  public void setValue(Object value) {
    this.value = value;
  }
  public Object getValue2() {
    return value2;
  }
  public void setValue2(Object value2) {
    this.value2 = value2;
  }
  
}
