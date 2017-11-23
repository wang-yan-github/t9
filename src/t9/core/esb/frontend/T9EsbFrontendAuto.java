package t9.core.esb.frontend;

import java.io.File;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.log4j.Logger;

import t9.core.autorun.T9AutoRun;
import t9.core.esb.common.T9UploadTask;
import t9.core.esb.common.util.ClientPropertiesUtil;
import t9.core.esb.common.util.T9EsbUtil;
import t9.core.esb.frontend.data.T9EsbUploadTask;
import t9.core.esb.frontend.logic.T9EsbFrontendLogic;

public class T9EsbFrontendAuto extends T9AutoRun {
  private static final Logger log = Logger.getLogger("t9.core.esb.frontend.T9EsbFrontendAuto");

  public void doTask() {
    try {
      final HttpHost host = ClientPropertiesUtil.getHttpHost();
      if (host == null) {
        
      }
      
      T9EsbFrontendLogic logic = new T9EsbFrontendLogic();
      List<T9EsbUploadTask> list  = logic.getUploadFieldTaskByStatus( "1,-1");
      if (list.size() < 0) {
        T9EsbUtil.debug("ua-无自动上传的任务");
      }
      for (T9EsbUploadTask t : list) {
        //是否已经在上传中
        synchronized(T9EsbFrontend.loc) {
          boolean uploading  =  T9EsbFrontend.nowTask.contains(t.getGuid());
          if (t.getStatus() == 1 && uploading) {
            T9EsbUtil.debug("ua-任务:"+ t.getGuid()  +"已经在上传了");
            continue;
          }
          T9EsbFrontend.nowTask.add(t.getGuid());
        }
        File file = new File(t.getFileName());
        if (!file.exists()) {
          T9EsbUtil.debug("ua-任务:"+ t.getGuid()  +"的文件未找到");
          synchronized(T9EsbFrontend.loc) {
            T9EsbFrontend.nowTask.remove(t.getGuid());
          }
          continue;
        }
        try {  
            T9UploadTask upload = new T9UploadTask(host, file, t.getToId(), t.getGuid() , t.optGuid , t.message);
            boolean flag =  upload.initialize(T9EsbFrontend.getHc());
            //表示已上传成功,或初史化失败失败
            if (flag) {
              T9EsbUtil.debug("ua-任务:"+ t.getGuid()  +"完成初始化，" );
              //修改传送状态为传送中
              upload.transfer(T9EsbFrontend.getHc());
              T9EsbUtil.debug("ua-任务:"+ t.getGuid()  +"完成上传传输，" );
              upload.complete(T9EsbFrontend.getHc() );
              T9EsbUtil.debug("ua-任务:"+ t.getGuid()  +"完成上传，" );
            } else {
              T9EsbUtil.debug("ua-任务:"+ t.getGuid()  +"初始化失败，" );
            }
          } catch (Exception e) {
            e.printStackTrace();
            T9EsbUtil.debug("ua-任务:"+ t.getGuid()  +"上传报错："  + e.getMessage() );
            logic.updateStatus(t.getGuid(), "-1");
          } finally {
            synchronized(T9EsbFrontend.loc) {
              T9EsbFrontend.nowTask.remove(t.getGuid());
            }
          }
        }
    } catch (Exception e) {
      T9EsbUtil.debug("ua-自动上传线程报错："  + e.getMessage());
      log.debug(e.getMessage(),e);
    } 
  }
}
