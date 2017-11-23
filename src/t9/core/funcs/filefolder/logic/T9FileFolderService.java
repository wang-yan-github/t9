package t9.core.funcs.filefolder.logic;

import java.io.File;
import java.sql.Connection;
import java.util.Map;

import javax.xml.rpc.ParameterMode;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;

import sun.misc.BASE64Decoder;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.file.T9FileUtility;
import t9.core.util.form.T9FOM;
import t9.user.api.core.db.T9DbconnWrap;


public class T9FileFolderService {
  public String getFileList(String userId , String pwd, String fileId) throws Exception {
    T9DbconnWrap dbUtil = new T9DbconnWrap();
    Connection conn = null;
    String returnStr = "";
    try {
      conn = dbUtil.getSysDbConn();
      T9FileFolderLogic logic = new T9FileFolderLogic();
      String pwds = logic.userPwd(conn, userId);
      if (!logic.checkUser(userId, pwd, pwds)) {
        return "用户名或密码不正确！";
      }
      if (T9Utility.isNullorEmpty(fileId)) {
        fileId = "0";
      }
      returnStr = logic.getFileSort(conn, fileId);
    }catch(Exception ex) {
      ex.printStackTrace();
    } finally {
      T9DBUtility.closeDbConn(conn, null);
    }
    return returnStr;
  }
  public String getFile(String userId , String pwd, String fileId) {
    T9DbconnWrap dbUtil = new T9DbconnWrap();
    Connection conn = null;
    String returnStr = "";
    try {
      conn = dbUtil.getSysDbConn();
      T9FileFolderLogic logic = new T9FileFolderLogic();
      String pwds = logic.userPwd(conn, userId);
      if (!logic.checkUser(userId, pwd, pwds)) {
        return "用户名或密码不正确！";
      }
      if (T9Utility.isNullorEmpty(fileId)) {
        fileId = "0";
      }
      returnStr = logic.getFile(conn, fileId);
    }catch(Exception ex) {
      ex.printStackTrace();
    } finally {
      T9DBUtility.closeDbConn(conn, null);
    }
    return returnStr;
  }
  private static final String WS_PATH = "http://192.168.0.101:88/t9/services/T9FileFolderService";
  public static void main(String[] args) {
    try {
      String serviceUrl = WS_PATH;
      Service service = new Service(); 
      Call call = (Call) service.createCall(); 
      call.setTargetEndpointAddress(new java.net.URL(serviceUrl)); 
      call.setOperationName("getFile");
      call.addParameter("userId", XMLType.XSD_STRING, ParameterMode.IN); 
      call.addParameter("pwd", XMLType.XSD_STRING, ParameterMode.IN);
      call.addParameter("fileId", XMLType.XSD_STRING, ParameterMode.IN);
      call.setReturnType(XMLType.XSD_STRING); 
      String ret = (String) call.invoke(new Object[] {"admin", "" , "2"});
      Map map = T9FOM.json2Map(ret);
      String aa = (String) map.get("attachments");
      String aa2 = (String) map.get("attachmentName");
       
      String[] names = aa2.split("\\*");
      String[] as = aa.split(",");
      
      String path = "D:\\test" + File.separator;
      for (int i = 0 ;i < as.length ;i++) {
        byte[] bs = new BASE64Decoder().decodeBuffer(as[i]);
        String name = names[i];
        String path1 = path +  name;
        T9FileUtility.storBytes2File(path1, bs);
      }
    } catch (Exception e) {
      e.printStackTrace();
      //log.error("getFileList - 调用web服务异常,异常信息:" + e.getMessage());
    }
  }
}
