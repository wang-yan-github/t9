package t9.core.funcs.wizardtool.data;

import java.util.Map;

public class T9Grid {
  public void loadToHtml(String id , String parameters
      , StringBuffer sb ,Map libMap){
    libMap.put("T9Grid", "/rad/grid/grid.css,/rad/grid/grid.js");
    sb.append("\nvar " + id + " = new T9Grid(); \n" + id + ".create(" + parameters + ");"); 
  }
}
