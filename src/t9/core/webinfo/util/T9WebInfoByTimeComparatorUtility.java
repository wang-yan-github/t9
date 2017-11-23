package t9.core.webinfo.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

import t9.core.webinfo.dto.T9WebInfo;

public class T9WebInfoByTimeComparatorUtility implements Comparator{

  @Override
  public int compare(Object arg0, Object arg1) {
    // TODO Auto-generated method stub
    Date date1 = null;
    Date date2 = null;
    try {
      T9WebInfo webInfo1 = (T9WebInfo)arg0;
      T9WebInfo webInfo2 = (T9WebInfo)arg1;
      DateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    
      date1 = f.parse(webInfo1.getWebInfoDate());
      date2 = f.parse(webInfo2.getWebInfoDate());
      if(date1.after(date2)){
        return 0;
      }
    } catch (ParseException e) {
      // TODO Auto-generated catch block
       e.printStackTrace();
    } 
    return 1;
  }
  
}
