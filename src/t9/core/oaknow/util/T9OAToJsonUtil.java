package t9.core.oaknow.util;
import java.util.List;

import t9.core.oaknow.data.T9CategoriesType;
import t9.core.oaknow.data.T9OAAsk;
import t9.core.util.T9Out;
/**
 * 转换为json
 * @author qwx110
 *
 */
public class T9OAToJsonUtil{
  
  @SuppressWarnings("unchecked")
  public static String toJson(List<T9OAAsk> list){
    StringBuffer sb = new StringBuffer();
    sb.append("[");
    if(list != null){
      for(int i=0; i<list.size(); i++){        
        if(i != list.size()-1){
          sb.append(((T9OAAsk)list.get(i)).toString()).append(",");
        }else{
          sb.append(((T9OAAsk)list.get(list.size()-1)).toString());
        }
      }
    }
    sb.append("]");
    //T9Out.println(sb.toString()+">>>>");
    return sb.toString();
  }
  
  public static String toJsonTwo(List<T9CategoriesType> list){
    StringBuffer sb = new StringBuffer();
    sb.append("[");
      if(list != null && list.size() != 0){
         for(int i=0; i < list.size(); i++){
            if(i < list.size()-1){
               sb.append(list.get(i).toString()).append(",");
            }else{
              sb.append(list.get(list.size()-1).toString());
            }
         }
      }
    sb.append("]");
    //T9Out.println(sb.toString()+"****************");
    return sb.toString();
  }
}
