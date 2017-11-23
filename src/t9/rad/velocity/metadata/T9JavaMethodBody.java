package t9.rad.velocity.metadata;

import java.util.HashMap;
import java.util.Map;

public class T9JavaMethodBody {

  private String bodyName ;
  private Map<String, Object> bodyArg = new HashMap<String, Object>();
  public String getBodyName() {
    return bodyName;
  }
  public void setBodyName(String bodyName) {
    this.bodyName = bodyName;
  }
  public Map<String, Object> getBodyArg() {
    return bodyArg;
  }
  public void setBodyArg(Map<String, Object> bodyArg) {
    this.bodyArg = bodyArg;
  }
  @Override
  public String toString() {
    return "T9JavaMethodBody [bodyArg=" + bodyArg + ", bodyName=" + bodyName
        + "]";
  }
  
  private T9JavaMethodBody(String bodyName,String key,Object value) {
    this.bodyName = bodyName;
    this.bodyArg.put(key, value);
  }
  
  public T9JavaMethodBody put(String key, Object value) {
    this.bodyArg.put(key, value);
    return this;
  }
  
  public T9JavaMethodBody() {
    // TODO Auto-generated constructor stub
  }
  
  private T9JavaMethodBody(String bodyName) {
    this.bodyName = bodyName;
  }
  
  public static T9JavaMethodBody get(String bodyName, String key, Object value) {
    return new T9JavaMethodBody(bodyName, key, value);
  } 
  
  public static T9JavaMethodBody get(String bodyName) {
    return new T9JavaMethodBody(bodyName);
  } 
}
