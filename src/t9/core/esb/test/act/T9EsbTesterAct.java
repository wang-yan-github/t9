package t9.core.esb.test.act;

import java.io.File;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.text.NumberFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.rpc.ParameterMode;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;

import t9.core.data.T9RequestDbConn;
import t9.core.esb.common.util.ClientPropertiesUtil;
import t9.core.esb.common.util.T9EsbUtil;
import t9.core.esb.frontend.T9EsbFrontend;
import t9.core.esb.server.user.data.TdUser;
import t9.core.esb.test.logic.T9EsbTesterLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.global.T9SysProps;
import t9.core.load.T9ConfigLoader;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;

public class T9EsbTesterAct {
  public static final String TEST_FOLDER = "E:\\esb\\test";
  
  public String test(HttpServletRequest request, HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
//      long start = System.currentTimeMillis();
//      new T9EsbTesterAct().test(new String[] {"192.168.0.102:8089", "pjn-pc:8089"}, 1000 * 60 * 2);
//      long uEnd = System.currentTimeMillis();
//      
//      new T9EsbTesterLogic().stat(dbConn, new File("e:\\esb\\log.txt"), start, uEnd);
//      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
//      request.setAttribute(T9ActionKeys.RET_MSRG, "测试成功");
      
      
      
      String folder = request.getParameter("folder");
      String time = request.getParameter("time");
      String to = request.getParameter("toId");
      String log = request.getParameter("log");
      
      if (T9Utility.isNullorEmpty(to)) {
        to = "ALL_USERS";
      }
      
      File testFolder;
      if (T9Utility.isNullorEmpty(folder)) {
        testFolder = new File(TEST_FOLDER);
      }
      else {
        testFolder = new File(folder);
      }
      
      int ms = 0;
      try {
        ms = (int)(Float.parseFloat(time) * 60 * 1000);
      } catch (NumberFormatException e) {
        ms = 30 * 1000;
      }
      
      T9EsbTesterLogic logic = new T9EsbTesterLogic();
      
      if (!T9Utility.isNullorEmpty(log)) {
        logic.test(dbConn, new File("D:\\log.log"), testFolder, to, 1, ms);
      }
      else {
        logic.test(testFolder, to, ms);
      } 
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "测试成功");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String clear(HttpServletRequest request, HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9EsbTesterLogic logic = new T9EsbTesterLogic();
      logic.clearDB(dbConn);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "测试成功");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  
  public String compute(HttpServletRequest request, HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9EsbTesterLogic logic = new T9EsbTesterLogic();
      int downloadCount = logic.getDownloadNo(dbConn);
      int uploadCount = logic.getUploadNo(dbConn);
      int uploadedCount = logic.getUploadSuccessfulNo(dbConn);
      int downloadedCount = logic.getDownloadSuccessfulNo(dbConn);

      String data = String.format("{\"uc\": \"%d\", \"udc\": \"%d\", \"dc\": \"%d\", \"ddc\": \"%d\"}"
          , uploadCount
          , uploadedCount
          , downloadCount
          , downloadedCount);
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "测试成功");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public void test(String[] clients, final long time) throws InterruptedException {
    ThreadPoolExecutor pool = (ThreadPoolExecutor)Executors.newFixedThreadPool(10);
    for (final String s : clients) {
      if (!T9Utility.isNullorEmpty(s)) {
        Runnable r = new Runnable() {
          public void run() {
            try {
              String serviceUrl = "http://" + s + "/t9/services/OAWebservice";
              Service service = new Service(); 
              Call call = (Call) service.createCall(); 
              call.setTargetEndpointAddress(new java.net.URL(serviceUrl)); 
              call.setOperationName("test"); 
              call.addParameter("time", XMLType.XSD_LONG, ParameterMode.IN); 
              call.setReturnType(XMLType.XSD_BOOLEAN); 
              boolean ret = false;
              ret = (Boolean) call.invoke(new Object[] {time});
              T9EsbUtil.println(ret ? "调用客户端测试程序成功" : "调用客户端测试程序失败");
            } catch (Exception e) {
              e.printStackTrace();
            }
          }
        };
        pool.submit(r);
      }
    }
    
    pool.shutdown();
    while(!pool.awaitTermination(60, TimeUnit.SECONDS)) {
      Thread.sleep(1000);
    }
  }
  
  public static void main(String[] args) throws Exception {
    String installPath = "D:\\project\\t9";
    //加载数据库配置信息compressJs
    String sysConfFile = installPath + "\\webroot\\t9\\WEB-INF\\config\\sysconfig.properties";
    T9SysProps.setProps(T9ConfigLoader.loadSysProps(sysConfFile));
    String selfConfFile = installPath + "\\webroot\\t9\\WEB-INF\\config\\selfconfig.properties";
    T9SysProps.addProps(T9ConfigLoader.loadSysProps(selfConfFile));
    
    T9DBUtility db = new T9DBUtility();
    Connection dbConn = db.getConnection(false, "T9");
    long start = System.currentTimeMillis();
    new T9EsbTesterAct().test(new String[] {"192.168.0.102:8089", "pjn-pc:8089", "192.168.0.155"}, 1000 * 60 * 2);
    long uEnd = System.currentTimeMillis();
    new T9EsbTesterLogic().stat(dbConn, new File("e:\\esb\\log.txt"), start, uEnd);
    System.out.println("----");
    
  }
}
