package t9.core.funcs.portal.util.rules;

public class T9LinkRule extends T9ModulesRule {
  public static final String TYPE = "link";
  
  public T9LinkRule (String[] title, String[] href) {
    super();
    attribute.put("type", TYPE);
    map.put("text", title);
    map.put("href", href);
  }
  
  public T9LinkRule (String title, String href) {
    super();
    attribute.put("type", TYPE);
    map.put("text", new String[]{title});
    map.put("href", new String[]{href});
  }
}