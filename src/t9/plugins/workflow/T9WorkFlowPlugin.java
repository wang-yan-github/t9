package t9.plugins.workflow;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.dept.data.T9Department;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.workflow.data.T9FlowRunData;
import t9.core.funcs.workflow.data.T9FlowRunPrcs;
import t9.core.funcs.workflow.util.T9FlowRunUtility;
import t9.core.funcs.workflow.util.T9IWFPlugin;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;

/**
 * 工作流插件接口
 * @author yzq
 *
 */
public class T9WorkFlowPlugin implements T9IWFPlugin{
  /**
   * 节点执行前执行
   * @param request
   * @param response
   * @return
   * @throws Exception 
   */
  public String before(HttpServletRequest request, HttpServletResponse response) throws Exception {
    String mage = "";
    try {
      //T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      Connection conn = requestDbConn.getSysDbConn();
      String flowIdStr = request.getParameter("flowId");
      String runIdStr = request.getParameter("runId");
      String prcsIdStr = request.getParameter("prcsId");
      String flowPrcsStr = request.getParameter("flowPrcs");
      
      int runId = Integer.parseInt(runIdStr);
      int prcsId = Integer.parseInt(prcsIdStr);
      //int flowId = Integer.parseInt(flowIdStr);
      int flowPrcs = Integer.parseInt(flowPrcsStr);
      
      String query = "select * from FLOW_RUN_PRCS where RUN_ID =" + runId + " and PRCS_ID=" + prcsId + " and FLOW_PRCS=" + flowPrcs + " and PRCS_FLAG in ('3','4') ";
      Statement stm = null;
      ResultSet rs = null; 
      int count = 0;
      try {
        stm = conn.createStatement();
        rs = stm.executeQuery(query);
        while(rs.next()){
          count++;
        }
      } catch(Exception ex) {
        throw ex;
      } finally {
        T9DBUtility.close(stm, rs, null); 
      }
      if (count > 1) {
        mage = null;
      } else {
        mage = "还有人未办理完毕请稍后！";
      }
    } catch(Exception ex) {
      throw ex;
    }
    return mage;
  }
  /**
   * 节点执行完毕执行
   * @param request
   * @param response
   * @return
   */
  public String after(HttpServletRequest request, HttpServletResponse response) {
    //System.out.println("------------结束啦");
    return null;
  }
}
