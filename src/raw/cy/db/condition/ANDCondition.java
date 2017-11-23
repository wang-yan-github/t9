package raw.cy.db.condition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import t9.core.util.db.T9StringFormat;


public class ANDCondition implements ConditionI{
  
  private Map filedValue = new HashMap();
  private Map filedIndex = new HashMap();
  private String taken = " and ";
  private Column column = null;
  private List<Column> columns = new ArrayList<Column>();
  public Map getIndexforField() {
    
    return filedIndex;
  }
  public Map getValueforField() {
    
    return filedValue;
  }
  public void setConditions(String fieldName, String oprator, Object value) {
      filedValue.put(fieldName, value);
      column = new Column();
      column.setField(fieldName);
      column.setOprator(oprator);
      column.setValue(value);
      columns.add(column);
  }
  public void setConditions(String fieldName, Object value) {
    filedValue.put(fieldName, value);
    column = new Column();
    column.setField(fieldName);
    column.setOprator("=");
    column.setValue(value);
    columns.add(column);
  }
  public String toSqlString(int index) {
    String value = "";
    Iterator<Column> it = columns.iterator();
    while(it.hasNext()){
      Column c = it.next();
      value +=T9StringFormat.format(c.getField())+" "+c.getOprator()+" ? ";
      filedIndex.put(index, c.getField());
      index++;
      if(it.hasNext()){
        value+=taken;
      }
    }
    return value;
    
  }
}
