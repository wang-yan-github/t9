package t9.core.funcs.workflow.logic;

import java.sql.Connection;
import java.util.Date;

import t9.core.funcs.workflow.data.T9FlowRun;
import t9.core.funcs.workflow.data.T9FlowRunLog;
import t9.core.util.db.T9ORM;

public class T9FlowRunLogLogic {
  /**
   * 
   * 保存日志
   */
  public void saveLog(T9FlowRunLog runLog , Connection conn) throws Exception{
    T9ORM orm = new T9ORM();
    orm.saveSingle(conn, runLog);
  }
  /**
   * 写入日志
   * @param runId
   * @param prcsId
   * @param flowPrcs
   * @param userId
   * @param logType
   * @param content
   * @param ip
   * @throws Exception
   */
  public void runLog(int runId , int prcsId , int flowPrcs , int userId , int logType , String content,String ip ,Connection conn) throws Exception{
    T9FlowRunLog runLog = new T9FlowRunLog();
    runLog.setRunId(runId);
    runLog.setPrcsId(prcsId);
    runLog.setFlowPrcs(flowPrcs);
    runLog.setUserId(userId);
    runLog.setType(logType);
    runLog.setContent(content);
    runLog.setIp(ip);
    runLog.setTime(new Date());
    T9FlowRunLogic runLogic = new T9FlowRunLogic();
    T9FlowRun flowRun = runLogic.getFlowRunByRunId(runId ,conn);
    if(flowRun != null){
      runLog.setRunName(flowRun.getRunName());
      runLog.setFlowId(flowRun.getFlowId());
    }
    this.saveLog(runLog , conn);
  }
}
