package t9.plugins.workflow;

import java.sql.Connection;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.workflow.data.T9FlowRunData;
import t9.core.funcs.workflow.util.T9FlowRunUtility;
import t9.core.funcs.workflow.util.T9IWFPlugin;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.subsys.oa.giftProduct.instock.act.T9GiftInstockAct;
import t9.subsys.oa.giftProduct.instock.data.T9GiftInstock;

public class T9GiftInstockBeforePlugin implements T9IWFPlugin{
  /**
   * 节点执行前执行

   * @param request
   * @param response
   * @return
   */
  public String before(HttpServletRequest request, HttpServletResponse response)throws Exception {
    //System.out.println("------------开始啦");
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      Connection conn = requestDbConn.getSysDbConn();
      String flowIdStr = request.getParameter("flowId");
      String runIdStr = request.getParameter("runId");
      String prcsIdStr = request.getParameter("prcsId");
      String flowPrcsStr = request.getParameter("flowPrcs");
    
      int runId = Integer.parseInt(runIdStr);
      int prcsId = Integer.parseInt(prcsIdStr);
      int flowId = Integer.parseInt(flowIdStr);
      int flowPrcs = Integer.parseInt(flowPrcsStr);
     
      T9FlowRunUtility wf = new T9FlowRunUtility();
    
      T9FlowRunData rd5 =  wf.getFlowRunData(conn, flowId, runId, "单价");
      String giftPrice = rd5.getItemData();
      T9FlowRunData rd6 =  wf.getFlowRunData(conn, flowId, runId, "数量");
      String giftQty= rd6.getItemData();
      if(!T9Utility.isNumber(giftPrice)){
        return "礼品的单价应为数字类型！";
      }
      if(!T9Utility.isInteger(giftQty)){
        return "礼品的数量应为整数类型！";
      }
    } catch(Exception ex) {
      throw ex;
    }
    return null;
  }
  /**
   * 节点执行完毕执行
   * @param request
   * @param response
   * @return
   * @throws Exception 
   */
  public String after(HttpServletRequest request, HttpServletResponse response) throws Exception {
 
    
    //System.out.println("------------结束啦");
    return null;
  }
}
