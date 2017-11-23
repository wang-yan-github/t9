package t9.plugins.workflow;
import java.sql.Connection;
import java.text.SimpleDateFormat;
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
import t9.core.util.db.T9ORM;
import t9.subsys.oa.asset.data.T9CpCptlInfo;
import t9.subsys.oa.asset.data.T9CpCptlRecord;
import t9.subsys.oa.asset.logic.T9CpCptlInfoLogic;

public class T9AssetReturn  implements T9IWFPlugin{
  /**
   * 返库单
   * 
   * 
   * */
  public String after(HttpServletRequest request, HttpServletResponse response) {
    return null;
  }

  public String before(HttpServletRequest request, HttpServletResponse response) throws Exception{
    String mage = "";
    try {
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      Connection dbConn = requestDbConn.getSysDbConn();
      String flowIdStr = request.getParameter("flowId");
      String runIdStr = request.getParameter("runId");
      String prcsIdStr = request.getParameter("prcsId");
      String flowPrcsStr = request.getParameter("flowPrcs");

      int runId = Integer.parseInt(runIdStr);
      int prcsId = Integer.parseInt(prcsIdStr);
      int flowId = Integer.parseInt(flowIdStr);
      int flowPrcs = Integer.parseInt(flowPrcsStr);

      T9FlowRunUtility wf = new T9FlowRunUtility();
      T9FlowRunData rd =  wf.getFlowRunData(dbConn, flowId, runId, "固定资产名称");
      String seqId = "0";
      if (rd != null) {
         seqId = rd.getItemData();//序列号
      }
      if (!T9Utility.isInteger(seqId)) {
        seqId = "0";
      }
      T9FlowRunData rd2 =  wf.getFlowRunData(dbConn, flowId, runId, "返库数量");
      String cptlQty = rd2.getItemData();//实际填入的数
      if (!T9Utility.isInteger(cptlQty)) {
        cptlQty = "0";
      }
      T9FlowRunData rd3 =  wf.getFlowRunData(dbConn, flowId, runId, "使用部室负责人");
      String cpreKeeper = rd3.getItemData();//使用部室负责人
      T9FlowRunData rd4 =  wf.getFlowRunData(dbConn, flowId, runId, "使用部室专管员");
      String keeper = rd4.getItemData();//使用部室专管员
      String userName = "";
      if(!T9Utility.isNullorEmpty(keeper)) {
        userName = keeper;
      }
      T9FlowRunData rd5 =  wf.getFlowRunData(dbConn, flowId, runId, "备注");
      String cpreMemo = rd5.getItemData();//备注
      boolean flage = true;
      int num =0;
      try {
        num = Integer.parseInt(cptlQty);
      } catch (Exception ex) {
        flage = false;
      }
      if (num > 0 && flage && num < 100) {
        T9CpCptlInfo cp = new T9CpCptlInfo();
        cp.setSeqId(Integer.parseInt(seqId));
        cp.setCptlQty(Integer.parseInt(cptlQty));
        cp.setKeeper(userName);
        cp.setRemark(cpreMemo);

        T9CpCptlInfoLogic cpLogic = new T9CpCptlInfoLogic();
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        String newDate = sf.format(new Date());
        T9CpCptlRecord record = new T9CpCptlRecord();

        record.setCptlId(Integer.parseInt(seqId));
        record.setRunId(runId);
        record.setCpreUser(userName);
        record.setCpreDate(java.sql.Date.valueOf(newDate));
        record.setCpreRecorder(userName);
        record.setCpreFlag("2");
        record.setCpreQty(Integer.parseInt(cptlQty));         
        record.setCpreKeeper(cpreKeeper);
        record.setCpreMemo(cpreMemo);
        record.setDeptId(0);

        cpLogic.udpateAsset(dbConn,cp);
        cpLogic.assetRunId(dbConn,record);
        mage = null;
      }
      if (!flage) {
        mage = "返库单数量不是数字或数量过大!";
      }
      if ((num <= 0 || num >= 100) && flage) {
        mage = "实际填入的数量过大或不能为0!";
      }
    } catch (Exception e) {
      throw e;
    }
    return mage;
  }

}
