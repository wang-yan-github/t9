package t9.plugins.workflow;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.workflow.data.T9FlowRunData;
import t9.core.funcs.workflow.util.T9FlowRunUtility;
import t9.core.funcs.workflow.util.T9IWFPlugin;
import t9.core.funcs.workflow.util.T9WorkFlowUtility;
import t9.core.global.T9BeanKeys;

/**
 * 工作流插件接口
 * @author yzq
 *
 */
public class MyPlugin implements T9IWFPlugin{
  /**
   * 节点执行前执行   * @param request
   * @param response
   * @return
   * @throws Exception 
   */
  public String before(HttpServletRequest request, HttpServletResponse response) throws Exception {
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      Connection conn = requestDbConn.getSysDbConn();
      //流程id
      String flowIdStr = request.getParameter("flowId");
      //工作id即流水号
      String runIdStr = request.getParameter("runId");
      //实际步骤号,即现在执行到几步
      String prcsIdStr = request.getParameter("prcsId");
      //设计步骤号,即现在执行所在的流程步骤
      String flowPrcsStr = request.getParameter("flowPrcs");
      //用户选择的步骤号(不是实际步骤号),这时步骤号是以逗号分割的(并发时用户可能先多个步骤)，如：2,
      String prcsChoose = request.getParameter("prcsChoose");
      int runId = Integer.parseInt(runIdStr);
      int prcsId = Integer.parseInt(prcsIdStr);
      int flowId = Integer.parseInt(flowIdStr);
      int flowPrcs = Integer.parseInt(flowPrcsStr);
    } catch(Exception ex) {
      throw ex;
    }
    return "必须是数字";
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
