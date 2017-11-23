package t9.plugins.workflow;

import java.sql.Connection;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.workflow.util.T9FlowRunUtility;
import t9.core.funcs.workflow.util.T9IWFPlugin;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.subsys.oa.giftProduct.instock.data.T9GiftInstock;
import t9.subsys.oa.giftProduct.instock.logic.T9GiftInstockLogic;
import t9.subsys.oa.giftProduct.outstock.data.T9GiftOutstock;
import t9.subsys.oa.giftProduct.outstock.logic.T9GiftOutstockLogic;

public class T9GiftOutstockAfterPlugin  implements T9IWFPlugin{
  /**
   * 节点执行前执行

   * @param request
   * @param response
   * @return
   * @throws Exception 
   */
  public String before(HttpServletRequest request, HttpServletResponse response) throws Exception {
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
    
      String giftId =  wf.getData(conn, flowId, runId, "礼品ID");
      String transQty =  wf.getData(conn, flowId, runId, "礼品数量");
 
      if(giftId==null||giftId.equals("")){
        String returnStr = "礼品不能为空！";
        return returnStr;
      }
      if(!T9Utility.isNumber(transQty)){
        String returnStr = "礼品数量必须为数字！";
        return returnStr;
      }
      if(giftId!=null&&!giftId.equals("")&&T9Utility.isInteger(giftId)){
        T9GiftInstockLogic instockLogic = new T9GiftInstockLogic();
        T9GiftInstock instock = instockLogic.selectGiftInstockById(conn, Integer.parseInt(giftId));
        //int useGiftQty = instockLogic.selectGiftQty(conn, Integer.parseInt(giftId));
        if(instock.getGiftQty()<Integer.parseInt(transQty)){
          return "领用数量大于库存数量，请仔细操作!";
        }
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
      String operator =  wf.getData(conn, flowId, runId, "领用人");
      String transDate=  wf.getData(conn, flowId, runId, "领用日期");
      String transUser =  wf.getData(conn, flowId, runId, "操作者");
      String giftId =  wf.getData(conn, flowId, runId, "礼品ID");;
      String transQty =  wf.getData(conn, flowId, runId, "礼品数量");
      String transUses =  wf.getData(conn, flowId, runId, "礼品用途");
      String transMemo =  wf.getData(conn, flowId, runId, "领用备注");
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = user.getSeqId();
      Date curDate = new Date();
      int transQtyInt = 0;
       //判断是否合格
      if(giftId==null||giftId.equals("")){
        String returnStr = "礼品不能为空！";
        return returnStr;
      }
      if(!T9Utility.isNumber(transQty)){
        String returnStr = "礼品数量必须为数字！";
        return returnStr;
      }
      if(giftId!=null&&!giftId.equals("")&&T9Utility.isInteger(giftId)){
        T9GiftInstockLogic instockLogic = new T9GiftInstockLogic();
        T9GiftInstock instock = instockLogic.selectGiftInstockById(conn, Integer.parseInt(giftId));
       // int useGiftQty = instockLogic.selectGiftQty(conn, Integer.parseInt(giftId));
        if(instock.getGiftQty()<Integer.parseInt(transQty)){
          return "领用数量大于库存数量，请仔细操作!";
        }
      }
      
      
      T9GiftInstockLogic instockLogic = new T9GiftInstockLogic();
      T9GiftOutstockLogic outstockLogic = new T9GiftOutstockLogic();
      if(operator!=null&&!operator.equals("")&&giftId!=null&&!giftId.equals("")){
        if(!transQty.equals("")&&T9Utility.isInteger(transQty)){
          transQtyInt = Integer.parseInt(transQty);
        }
        T9GiftOutstock giftOutstock = new T9GiftOutstock();
        giftOutstock.setGiftId(Integer.parseInt(giftId));
        giftOutstock.setOperator(String.valueOf(userId));
        giftOutstock.setTransDate(curDate);
        giftOutstock.setTransUser(String.valueOf(userId));
        giftOutstock.setTransQty(transQtyInt);
        giftOutstock.setTransUses(transUses);
        giftOutstock.setTransMemo(transMemo);
        giftOutstock.setRunId(runId);
        int seqId = outstockLogic.addGiftOutstock(conn, giftOutstock);
        
        //库存减去
        instockLogic.updateInstockById(conn, giftId, transQtyInt);
      }
    } catch(Exception ex) {
      throw ex;
    }
    
    //System.out.println("------------结束啦");
    return null;
  }
}
