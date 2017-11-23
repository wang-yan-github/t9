package t9.plugins.workflow;
import java.sql.Connection;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.sql.ResultSet;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.dept.data.T9Department;
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
public class T9Asset implements T9IWFPlugin{
  /**
   * 领用单
   * 
   * 
   * */
  public String after(HttpServletRequest request, HttpServletResponse response) {
    return null;
  }

  public String before(HttpServletRequest request, HttpServletResponse response) throws Exception {
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
      String seqId = rd.getItemData();//序列号

      T9FlowRunData rd2 =  wf.getFlowRunData(dbConn, flowId, runId, "领用数量");
      String cptlQty2 = rd2.getItemData();//实际填入的数量
      T9FlowRunData rd3 =  wf.getFlowRunData(dbConn, flowId, runId, "现有数量");
      String cptlQty = rd3.getItemData();//实际存在的数量 

      T9FlowRunData rd5 =  wf.getFlowRunData(dbConn, flowId, runId, "原因");
      String cpreMemo = rd5.getItemData();
      
      String useUser = "";//person.getUserName();//当前申请人      String sql=" select begin_user from flow_run where run_id='"+runId+"'";
      Statement stmt=dbConn.createStatement();
      ResultSet rs= stmt.executeQuery(sql);
      if(rs.next()){
        useUser=rs.getString("begin_user");
      }
      
      T9ORM orm = new T9ORM();//orm映射数据库
      T9Person perName = (T9Person)orm.loadObjComplex(dbConn,T9Person.class,Integer.parseInt(useUser));
      T9Department dep = (T9Department)orm.loadObjComplex(dbConn, T9Department.class, perName.getDeptId());

      boolean flage = true;
      int num =0;
      try {
        num = Integer.parseInt(cptlQty2);
      } catch (Exception ex) {
        flage = false;
      }
      if (flage) {
        if (Integer.parseInt(cptlQty) >= Integer.parseInt(cptlQty2)) {
          T9CpCptlInfo cp = new T9CpCptlInfo();
          cp.setSeqId(Integer.parseInt(seqId));
          cp.setCptlQty(Integer.parseInt(cptlQty2));
          cp.setUseUser(perName.getUserName());
          cp.setUseDept( dep.getDeptName());
          /////////////////////
          SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
          String newDate = sf.format(new Date());
          T9CpCptlInfoLogic cpLogic = new T9CpCptlInfoLogic();

          T9CpCptlRecord record = new T9CpCptlRecord();
          record.setCptlId(Integer.parseInt(seqId));
          record.setRunId(runId);
          record.setCpreUser(perName.getUserName());
          record.setCpreDate(java.sql.Date.valueOf(newDate));
          record.setCpreRecorder(perName.getUserId());
          record.setCpreFlag("1");
          record.setCpreQty(Integer.parseInt(cptlQty2));         
          record.setCpreMemo(cpreMemo);
          record.setDeptId(perName.getDeptId());

          cpLogic.asset(dbConn,cp);
          cpLogic.assetRunId(dbConn,record);
          mage = null;
        }else {
          mage = "实际申请数量大于现有数据";
        }
      }else {
        mage = "返库单数量不是数字或数量过大!";
      }
    } catch(Exception ex) {
      throw ex;
    }
    return mage;
  }

}
