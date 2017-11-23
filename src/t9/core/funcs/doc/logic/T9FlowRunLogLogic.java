package t9.core.funcs.doc.logic;

import java.sql.Connection;
import java.util.Date;

import t9.core.funcs.doc.data.T9DocRun;
import t9.core.funcs.doc.data.T9DocFlowRunLog;
import t9.core.util.db.T9ORM;

public class T9FlowRunLogLogic {
  /**
   * 
   * 保存日志
   */
  public void saveLog(T9DocFlowRunLog runLog , Connection conn) throws Exception{
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
    T9DocFlowRunLog runLog = new T9DocFlowRunLog();
    runLog.setRunId(runId);
    runLog.setPrcsId(prcsId);
    runLog.setFlowPrcs(flowPrcs);
    runLog.setUserId(userId);
    runLog.setType(logType);
    runLog.setContent(content);
    runLog.setIp(ip);
    runLog.setTime(new Date());
    T9FlowRunLogic runLogic = new T9FlowRunLogic();
    T9DocRun flowRun = runLogic.getFlowRunByRunId(runId ,conn);
    if(flowRun != null){
      runLog.setRunName(flowRun.getRunName());
      runLog.setFlowId(flowRun.getFlowId());
    }
    this.saveLog(runLog , conn);
  }
}
