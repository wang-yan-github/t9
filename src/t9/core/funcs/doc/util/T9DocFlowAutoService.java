package t9.core.funcs.doc.util;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import t9.core.autorun.T9AutoRun;
import t9.core.funcs.doc.data.T9DocFlowRunPrcs;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
/**
 * 公文后台服务

 * @author liuhan
 *
 */
public class T9DocFlowAutoService extends T9AutoRun {
  private static final Logger log = Logger.getLogger("t9.core.funcs.doc.util.T9DocFlowAutoService");

  /**
   *  未接收公文，一天之后如果还没接收，要自动转为待办
   */
  public void doTask() {
    try {
      Connection conn = getRequestDbConn().getSysDbConn();
      this.setStatus(conn);
    } catch (Exception e) {
      e.printStackTrace();
      log.debug(e.getMessage(),e);
    }
  }
  public List<T9DocFlowRunPrcs> getAllPrcs(Connection conn) throws Exception {
    String query = "select SEQ_ID,PRCS_FLAG , RUN_ID , TOP_FLAG , OP_FLAG , USER_ID , PRCS_ID , FLOW_PRCS , PARENT , CREATE_TIME from doc_flow_run_prcs where prcs_flag = '1'";
    Statement stm = null;
    ResultSet rs = null;
    List<T9DocFlowRunPrcs> list = new ArrayList();
    long now = System.currentTimeMillis();
    long dayTime = 24 * 60 * 60 * 1000;
    try {
      stm = conn.createStatement();
      rs = stm.executeQuery(query);
      while (rs.next()){
        String prcsFlag = rs.getString("PRCS_FLAG");
        int seqId = rs.getInt("SEQ_ID");
        int runId = rs.getInt("RUN_ID");
        String topFlag = rs.getString("TOP_FLAG");
        String opFlag = rs.getString("OP_FLAG");
        int userId = rs.getInt("USER_ID");
        int prcsId = rs.getInt("PRCS_ID");
        int flowPrcs = rs.getInt("FLOW_PRCS");
        String parent = rs.getString("PARENT");
        Timestamp time = rs.getTimestamp("CREATE_TIME");
        long timeLong = time.getTime();
        
        if ( (now - timeLong) > dayTime ) {
          T9DocFlowRunPrcs doc = new T9DocFlowRunPrcs();
          doc.setPrcsFlag(prcsFlag);
          doc.setSeqId(seqId);
          doc.setRunId(runId);
          doc.setTopFlag(topFlag);
          doc.setOpFlag(opFlag);
          doc.setUserId(userId);
          doc.setParent(parent);
          doc.setFlowPrcs(flowPrcs);
          doc.setPrcsId(prcsId);
          doc.setCreateTime(new Date(time.getTime()));
          list.add(doc);
        }
      }
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, rs, null); 
    }
    return list;
  }
  public void updateToPrcsFlag(Connection conn , int seqId ) throws Exception {
    PreparedStatement stm = null;
    try {
      stm = conn.prepareStatement("update doc_flow_run_prcs set PRCS_FLAG = '2' , PRCS_TIME = ? WHERE SEQ_ID = " + seqId);
      stm.setTimestamp(1, new Timestamp(new Date().getTime()));
      stm.executeUpdate();
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, null, null); 
    }
  }
  /**
   *  未接收公文，一天之后如果还没接收，要自动转为待办
   * @throws Exception 
   */
  public void setStatus(Connection conn) throws Exception {
    // TODO Auto-generated method stub
    List<T9DocFlowRunPrcs> list = this.getAllPrcs(conn);
    for (T9DocFlowRunPrcs runProcess : list) {
      int userId = runProcess.getUserId();
      int runId = runProcess.getRunId();
      int prcsId = runProcess.getPrcsId();
      
      this.updateToPrcsFlag(conn, runProcess.getSeqId());
      if( "1".equals(runProcess.getTopFlag())
          && "1".equals(runProcess.getOpFlag())){
        String query = "update "+ T9WorkFlowConst.FLOW_RUN_PRCS +" set OP_FLAG=0 WHERE "
          + " USER_ID<>'" + userId +"'  "
          + " AND RUN_ID='" + runId + "'  "
          + " AND PRCS_ID='" + runProcess.getPrcsId() + "'  "
          + " AND FLOW_PRCS='"+ runProcess.getFlowPrcs() +"'";
       T9WorkFlowUtility.updateTableBySql(query, conn);
       //防止list里面存在其它人同一个步骤的信息，上面改了数据库后，但list里面没有修改
       break;
      }
      //修改上一步骤状态为已经办理完毕
      int oldPrcsId = prcsId - 1;
      String query = "update "+ T9WorkFlowConst.FLOW_RUN_PRCS +" set PRCS_FLAG='4' WHERE "
            + " RUN_ID='"+runId+"'  "
            + " AND PRCS_ID='"+oldPrcsId+"'";
      if(!"0".equals(runProcess.getParent()) 
          && !T9Utility.isNullorEmpty(runProcess.getParent()))
         query +=" AND FLOW_PRCS IN ("+ runProcess.getParent() +")";
      T9WorkFlowUtility.updateTableBySql(query, conn);
    }
  }
}
