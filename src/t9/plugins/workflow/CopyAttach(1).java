package t9.plugins.workflow;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.workflow.data.T9FlowRunData;
import t9.core.funcs.workflow.data.T9FlowType;
import t9.core.funcs.workflow.util.T9FlowRunUtility;
import t9.core.funcs.workflow.util.T9IWFPlugin;
import t9.core.funcs.workflow.util.T9WorkFlowUtility;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9SysProps;
import t9.core.util.T9Guid;
import t9.core.util.db.T9DBUtility;
import t9.core.util.file.T9FileUtility;

/**
 * 工作流插件接口
 * @author yzq
 *
 */
public class CopyAttach implements T9IWFPlugin{
  /**
   * 节点执行前执行
   * @param request
   * @param response
   * @return
   * @throws Exception 
   */
  public String before(HttpServletRequest request, HttpServletResponse response) throws Exception {
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      Connection conn = requestDbConn.getSysDbConn();
      String runIdStr = request.getParameter("runId");
      copyAttah(  conn , runIdStr ,  "c:\\");
    } catch(Exception ex) {
      throw ex;
    }
    return null;
  }
  public void copyAttah( Connection conn ,String runId , String toPath) throws Exception {
    String query = "select ATTACHMENT_ID , ATTACHMENT_NAME from FLOW_RUN where RUN_ID = " + runId;
    Statement stm = null;
    ResultSet rs = null;
    String attachmentId = "";
    String attachmentName = "";
    try {
      stm = conn.createStatement();
      rs = stm.executeQuery(query);
      while (rs.next()) {
        attachmentId = rs.getString("ATTACHMENT_ID");
        attachmentName = rs.getString("ATTACHMENT_NAME");
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, rs, null);
    }
    this.copyAttach(attachmentId, attachmentName, toPath);
  }
  public static String filePath = T9SysProps.getAttachPath() + "\\workflow";
  public void copyAttachSingle(String attId , String attName , String toPath) throws Exception {
    int index = attId.indexOf("_");
    String hard = "";
    String str = "";
    if (index > 0) {
      hard = attId.substring(0, index);
      str = attId.substring(index + 1);
    } else {
      hard = "all";
      str = attId;
    }
    String path = filePath + "\\"  + hard + "\\" + str + "_" + attName;
    File  category = new File(toPath) ;
    if (!category.exists()) {
      category.mkdirs();
    }
    if (!toPath.endsWith("\\")) {
      toPath += "\\";
    }
    toPath += attName;
    T9FileUtility.copyFile(path, toPath);
  }
  public void copyAttach(String attId , String attName, String toPath) throws Exception {
    String newAttId = "";
    if (attId == null) {
      return ;
    }
    String[] attIds = attId.split(",");
    String[] attNames = attName.split("\\*");
    for(int i = 0 ;i < attIds.length ;i ++){
      String tmp = attIds[i];
      if ("".equals(tmp)) {
        continue;
      }
      String attN = attNames[i];
      this.copyAttachSingle(tmp, attN , toPath);
    }
  }
  /**
   * 节点执行完毕执行
   * @param request
   * @param response
   * @return
   */
  public String after(HttpServletRequest request, HttpServletResponse response) throws Exception {
    //System.out.println("------------结束啦");
    return null;
  }
}
