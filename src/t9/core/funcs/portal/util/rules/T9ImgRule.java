package t9.core.funcs.portal.util.rules;


public class T9ImgRule extends T9ModulesRule {
  public static final String TYPE = "img";
  
  public T9ImgRule (String title[], String src[]) {
    super();
    attribute.put("type", TYPE);
    map.put("text", title);
    map.put("src", src);
  }
  
  public T9ImgRule (String title[],  String src[], String link[]) {
    super();
    attribute.put("type", TYPE);
    map.put("text", title);
    map.put("src", src);
    map.put("href", link);
  }
  
  public T9ImgRule (String title, String src) {
    super();
    attribute.put("type", TYPE);
    map.put("text", new String[]{title});
    map.put("src", new String[]{src});
  }
  
  public T9ImgRule (String title,  String src, String link) {
    super();
    attribute.put("type", TYPE);
    map.put("text", new String[]{title});
    map.put("src", new String[]{src});
    map.put("href", new String[]{link});
  }
}