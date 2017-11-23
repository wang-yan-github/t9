package t9.core.oaknow.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import t9.core.util.T9Out;

public class T9DateFormatUtil{
  public static String dateFormat(Date date){
    if(date != null){
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      String ds = sdf.format(date);
      //T9Out.println(ds.toString());
      return ds.toString();
    }else{
      return "";
    }    
  }
}
