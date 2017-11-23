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

public class T9GiftInstockAfterPlugin implements T9IWFPlugin{
  /**
   * 节点执行前执行

   * @param request
   * @param response
   * @return
   */
  public String before(HttpServletRequest request, HttpServletResponse response)  throws Exception {
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
      T9FlowRunData rd1 =  wf.getFlowRunData(conn, flowId, runId, "礼品名称");
      String giftName = rd1.getItemData();
      T9FlowRunData rd2 =  wf.getFlowRunData(conn, flowId, runId, "上缴部门");
      String deptId= rd2.getItemData();
      T9FlowRunData rd3 =  wf.getFlowRunData(conn, flowId, runId, "礼品类别");
      String giftType = rd3.getItemData();
      T9FlowRunData rd4 =  wf.getFlowRunData(conn, flowId, runId, "计量单位");
      String giftUnit = rd4.getItemData();
      
      T9FlowRunData rd5 =  wf.getFlowRunData(conn, flowId, runId, "单价");
      String giftPrice = rd5.getItemData();
      T9FlowRunData rd6 =  wf.getFlowRunData(conn, flowId, runId, "数量");
      String giftQty= rd6.getItemData();
      T9FlowRunData rd7 =  wf.getFlowRunData(conn, flowId, runId, "供应商");
      String giftSupplier = rd7.getItemData();
      T9FlowRunData rd8 =  wf.getFlowRunData(conn, flowId, runId, "经手人");
      String giftCreator = rd8.getItemData();
      
      T9FlowRunData rd9 =  wf.getFlowRunData(conn, flowId, runId, "保管员");
      String giftKeeper = rd9.getItemData();
      T9FlowRunData rd10 =  wf.getFlowRunData(conn, flowId, runId, "备注");
      String giftMemo= rd10.getItemData();
      T9FlowRunData rd11 =  wf.getFlowRunData(conn, flowId, runId, "礼品来源");
      String giftDesc = rd11.getItemData();
      T9GiftInstockAct giftInstockAct = new T9GiftInstockAct();
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = user.getSeqId();
      Date curDate = new Date();
      T9GiftInstock giftInstock =  new T9GiftInstock(); //(T9GiftInstock) T9FOM.build(request.getParameterMap());
      giftInstock.setCreateDate(curDate);
      if(giftType!=null&&!giftType.equals("")){
        if(deptId!=null&&!deptId.equals("")){
          giftInstock.setDeptId(Integer.parseInt(deptId));
        }
        if(giftName!=null){
          giftInstock.setGiftName(giftName);
        }
        if(giftType!=null){
          giftInstock.setGiftType(giftType);
        }
        if(giftUnit!=null){
          giftInstock.setGiftUnit(giftUnit);
        }
        if(giftPrice!=null&&!giftPrice.equals("")&&T9Utility.isNumber(giftPrice)){
          giftInstock.setGiftPrice(Double.parseDouble(giftPrice));
        }
        if(giftQty!=null&&!giftQty.equals("")&&T9Utility.isInteger(giftQty)){
          giftInstock.setGiftQty(Integer.parseInt(giftQty));
        }
        if(giftSupplier!=null){
          giftInstock.setGiftSupplier(giftSupplier);
        }
        if(giftKeeper!=null){
          giftInstock.setGiftKeeper(giftKeeper);
        }
        if(giftMemo!=null){
          giftInstock.setGiftMemo(giftMemo);
        }
        if(giftDesc!=null){
          giftInstock.setGiftDesc(giftDesc);
        }
        giftInstock.setGiftCreator(String.valueOf(userId));
        giftInstock.setRunId(runId);
        giftInstockAct.addGiftInstockWork(request, response,giftInstock);
      }
     
    } catch(Exception ex) {
      throw ex;
    }
    
    //System.out.println("------------结束啦");
    return null;
  }
}
