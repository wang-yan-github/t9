package t9.subsys.inforesouce.docmgr.data;

import t9.core.global.T9SysProps;
import t9.core.util.T9Utility;

public class T9DocConst{
   public static String filePath = T9SysProps.getAttachPath() + "doc";
   /**
    * 收文类型
    */
   public static String MODULE = "doc";
   /**
    * 密级
    */
   public static String SECRET_GRADE = "['非密','秘密','机密','绝密']";
   
   public static String DOC_TYPE = "['收电','收信','阅文','传真']";
   
   public static String constName = getName()[0];
   
   public static String centerName = getName()[1];
   
   public static String[] getName() {
     String[] constArray = {"公文批办" , "发文"};
     String prop = T9SysProps.getProp("DOC_RECEIVE_FLOWTYPE");
     if (T9Utility.isNullorEmpty(prop)) {
       return constArray;
     } 
     String[] a = prop.split(",");
     if (a.length >  0) {
       constArray[0] = a[0];
     }
     if (a.length >  1) {
       constArray[1] = a[1];
     }
     return constArray;
   }
}
