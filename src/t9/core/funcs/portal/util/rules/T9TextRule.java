package t9.core.funcs.portal.util.rules;

public class T9TextRule extends T9ModulesRule {
  public static final String TYPE = "text";
  
  public T9TextRule (String[] text) {
    super();
    attribute.put("type", TYPE);
    map.put("text", text);
  }
  
  public T9TextRule (String text) {
    super();
    attribute.put("type", TYPE);
    map.put("text", new String[]{text});
  }
}