package raw.cy.db.condition;

import java.util.Map;

public interface ConditionI {
  
 public void setConditions(String fieldName,String Oprator,Object value);
 
 public void setConditions(String fieldName,Object value);
 
 public String toSqlString(int index);
 
 public Map getIndexforField();
 
 public Map getValueforField();
 
}
