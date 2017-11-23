package t9.subsys.oa.profsys.logic.active;

import java.sql.Connection;
import java.util.Map;

import t9.core.data.T9PageDataList;
import t9.core.data.T9PageQueryParam;
import t9.core.load.T9PageLoader;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.form.T9FOM;

public class T9AactiveProjectLogic {
  /**
   * 分页列表
   * @param conn
   * @param request
   * @return
   * @throws Exception
   */
  public String toSearchData(Connection conn,Map request,String projType,String projNum
      ,String projActiveType,String projStartTime1,String projStartTime2,
      String projGropName,String projVisitType,String projEndTime1,String projEndTime2,
      String projLeader,String deptId,String managerStr,String projStatus) throws Exception{
    String sql = "select p.SEQ_ID,p.PROJ_NUM,p.PROJ_GROUP_NAME,p.DEPT_ID,dep.DEPT_NAME"
      + ",pn.USER_NAME,ci.CLASS_DESC,p.PROJ_START_TIME,p.PROJ_END_TIME,p.P_YX,p.P_TOTAL,p.PRINT_STATUS"
      + " from PROJECT p left outer join DEPARTMENT dep on p.DEPT_ID = dep.SEQ_ID"
      + " left outer join CODE_ITEM ci on p.PROJ_ACTIVE_TYPE = ci.SEQ_ID "
      + " left outer join PERSON pn on p.PROJ_LEADER = pn.SEQ_ID where p.PROJ_TYPE = '" + projType + "'";
    if(!T9Utility.isNullorEmpty(projStatus)){
      if(projStatus.equals("0")){
        sql = sql + " and (p.PROJ_STATUS = '0' or p.PROJ_STATUS is null)";
      }else{
        sql = sql + " and p.PROJ_STATUS = '" + projStatus + "'";
      }
    }
    if(!T9Utility.isNullorEmpty(managerStr)){
      sql = sql + " and p.DEPT_ID " + managerStr;
    }
    if(!T9Utility.isNullorEmpty(projNum)){
      sql = sql + " and p.PROJ_NUM like '%" + T9DBUtility.escapeLike(projNum) + "%'" + T9DBUtility.escapeLike() ;
    }
    if(!T9Utility.isNullorEmpty(projActiveType)){
      sql = sql + " and p.PROJ_ACTIVE_TYPE = '" + projActiveType + "'";
    }
    
    if(!T9Utility.isNullorEmpty(projStartTime1)){
      sql = sql + " and " + T9DBUtility.getDateFilter("p.PROJ_START_TIME",projStartTime1, ">=");
    }
    if(!T9Utility.isNullorEmpty(projStartTime2)){
      sql = sql + " and " + T9DBUtility.getDateFilter("p.PROJ_START_TIME",projStartTime2 + " 23:59:59", "<=");
    }
    if(!T9Utility.isNullorEmpty(projGropName)){
      sql = sql + " and p.PROJ_GROUP_NAME like '%" + T9DBUtility.escapeLike(projGropName) + "%'" + T9DBUtility.escapeLike() ;
    }
    if(!T9Utility.isNullorEmpty(projVisitType)){
      sql = sql + " and p.PROJ_VISIT_TYPE ='" + projVisitType + "'";
    }
    
    if(!T9Utility.isNullorEmpty(projEndTime1)){
      sql = sql + " and " + T9DBUtility.getDateFilter("p.PROJ_END_TIME",projEndTime1, ">=");
    }
    if(!T9Utility.isNullorEmpty(projStartTime2)){
      sql = sql + " and " + T9DBUtility.getDateFilter("p.PROJ_END_TIME",projEndTime2 + " 23:59:59", "<=");
    }
    if(!T9Utility.isNullorEmpty(projLeader)){
      sql = sql + " and p.PROJ_LEADER = '" + projLeader + "'";
    }
    if(!T9Utility.isNullorEmpty(deptId)){
      sql = sql + " and p.DEPT_ID in(" + deptId + ")";
    }
    T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request);
    T9PageDataList pageDataList = T9PageLoader.loadPageList(conn,queryParam,sql);
    return pageDataList.toJson();
  }
}
