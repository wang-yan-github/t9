package t9.core.funcs.doc.receive.data;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import t9.core.funcs.doc.util.T9WorkFlowConst;
import t9.core.global.T9SysProps;
import t9.core.util.T9Utility;

public class T9DocConst{
   public static String filePath = T9SysProps.getAttachPath() + File.separator+  T9WorkFlowConst.MODULE;
   public static String configPath = T9WorkFlowConst.MODULE_CONTEXT_PATH + "/workflowUtility/doc_config.properties";
   public static String remindType = "70";
   /**
    * 收文类型
    */
   public static String SECRET_GRADE = "SECRET_GRADE";
   public static String DOC_TYPE = "REC_DOC_TYPE";
   public static String DOC_RECEIVE_FLOWTYPE = "DOC_RECEIVE_FLOWTYPE";
   public static String DOC_TITLE = "DOC_TITLE";
   public static String DOC_RECEIVE_FLOW_SORT = "DOC_RECEIVE_FLOW_SORT";
   public static String DOC_SEND_FLOW_SORT = "DOC_SEND_FLOW_SORT";
   public static String SEND_DOC_NUM = "SEND_DOC_NUM";
   public static String DOC_SEND_UNIT = "DOC_SEND_UNIT";
   
   
   public static String getProp(String webrootPath , String key) throws Exception {
     Properties p = new Properties();
     String strs = "";
     try {
       p.load(new InputStreamReader(new FileInputStream(new File(webrootPath + configPath)) , "UTF-8"));
       strs = p.getProperty(key);
     } catch (Exception e) {
       throw e;
     } 
     return strs;
   }
   public static String[] parseStr2Arr(String data){
     if(!T9Utility.isNullorEmpty(data)){
       String[] d = data.split(",");
       return d;
     }
     return null;
   }
}
