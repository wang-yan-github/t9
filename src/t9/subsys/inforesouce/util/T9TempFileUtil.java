package t9.subsys.inforesouce.util;

import javax.servlet.http.HttpServletRequest;

import t9.core.global.T9Const;
import t9.core.util.file.T9FileUtility;


public class T9TempFileUtil{ 

  private static final T9TempFileUtil cu= new T9TempFileUtil();
  
  public synchronized String readContent(HttpServletRequest request, String FILE_PATH) throws Exception{    
    String sp = System.getProperty("file.separator");
    String path = request.getSession().getServletContext().getRealPath(sp)+ "subsys" + sp + "inforesource" +sp;   
    
    String temp = T9FileUtility.loadLine2Buff(path + FILE_PATH, T9Const.DEFAULT_CODE).toString();
   
    return temp;
  }
  
  public static T9TempFileUtil getInstance(){
    return cu;
  }
}
