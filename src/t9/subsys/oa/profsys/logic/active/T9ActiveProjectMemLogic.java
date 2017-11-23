package t9.subsys.oa.profsys.logic.active;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

import org.apache.log4j.Logger;

import t9.core.data.T9PageDataList;
import t9.core.data.T9PageQueryParam;
import t9.core.load.T9PageLoader;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.form.T9FOM;
import t9.subsys.oa.profsys.data.T9ProjectMem;

public class T9ActiveProjectMemLogic {
  private static Logger log = Logger.getLogger("t9.subsys.oa.profsys.act.T9ActiveProjectMenLogic");
  /**
   * 分页列表
   * @param conn
   * @param request
   * @return
   * @throws Exception
   */
  public String toSearchData(Connection conn,Map request,String projId,String projStatus) throws Exception{
    String sql = "select pm.SEQ_ID,pm.MEM_NUM,pm.MEM_POSITION,pm.MEM_NAME,pm.MEM_SEX,"
      + "pm.UNIT_NUM,pm.UNIT_NAME,pm.UNIT_MAN_NUM,pm.INCLUDE_FN,pm.ATTACHMENT_ID,pm.ATTACHMENT_NAME"
      +" from PROJECT_MEM pm "
      +" where pm.PROJ_ID = " + projId  + " and pm.PROJ_MEM_TYPE = '2'" ;
    T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request);
    T9PageDataList pageDataList = T9PageLoader.loadPageList(conn,queryParam,sql);
    return pageDataList.toJson();
  }

  /***
   * 根据条件查询数据的projId
   * @return
   * @throws Exception 
   */
  public static String memSeqId(Connection dbConn,T9ProjectMem mem) throws Exception {
    String sql = "select SEQ_ID,PROJ_ID,PROJ_CREATOR"
      +",PROJ_DATE,MEM_NUM,MEM_ROLE,MEM_NAME"
      + ",MEM_SEX,MEM_NATION,MEM_NATIVE_PLACE"
      +",MEM_BIRTHPLACE,MEM_BIRTH,MEM_ID_NUM"
      +",MEM_PHONE,MEM_MAIL,MEM_FAX,MEM_ADDRESS,MEM_NOTE"
      +",UNIT_NUM,INCLUDE_FN,UNIT_NAME,UNIT_MAN_NUM"
      +",ATTACHMENT_ID,ATTACHMENT_NAME,PROJ_MEM_TYPE,MEM_POSITION"
      + " from PROJECT_MEM where PROJ_MEM_TYPE ='" + mem.getProjMemType() + "'";
    if (!T9Utility.isNullorEmpty(mem.getMemNum())) {
      sql += " and MEM_NUM like '%" + T9Utility.encodeLike(mem.getMemNum()) + "%' " + T9DBUtility.escapeLike();
    }
    if (!T9Utility.isNullorEmpty(mem.getMemPosition())) {
      sql += " and " + T9DBUtility.findInSet(mem.getMemPosition(), "MEM_POSITION");
    }
    if (!T9Utility.isNullorEmpty(mem.getMemName())) {
      sql += " and MEM_NAME like '%" + T9Utility.encodeLike(mem.getMemName()) + "%' " + T9DBUtility.escapeLike();
    }
    if (!T9Utility.isNullorEmpty(mem.getUnitNum())) {
      sql += " and UNIT_NUM like '%" + T9Utility.encodeLike(mem.getUnitNum()) + "%' " + T9DBUtility.escapeLike();
    }
    if (!T9Utility.isNullorEmpty(mem.getMemSex())) {
      sql += " and MEM_SEX='" + mem.getMemSex() + "'";
    }
    if (!T9Utility.isNullorEmpty(mem.getUnitName())) {
      sql += " and UNIT_NAME like '%" + T9Utility.encodeLike(mem.getUnitName()) + "%' " + T9DBUtility.escapeLike();
    }
    PreparedStatement ps = null;
    ResultSet rs = null;
    ps = dbConn.prepareStatement(sql);
    rs = ps.executeQuery();
    String projId = "";
    while (rs.next()) {
      projId += rs.getString("PROJ_ID") + ",";
    }
    if (!T9Utility.isNullorEmpty(projId)) {
      projId = projId.substring(0,projId.length() - 1);
    }
    return projId;
  }
  /**
   * 项目活动
   * @param conn
   * @param request
   * @return
   * @throws Exception
   */
  public static String profsysSelectActiveMem(Connection conn,Map request,String projId,String projMemType) throws Exception{
   if (T9Utility.isNullorEmpty(projId)) {
     projId = "0";
   }
    String sql = "select p.SEQ_ID,p.PROJ_NUM,ba.BUDGET_ITEM,p.DEPT_ID,dep.DEPT_NAME"
      + ",pn.USER_NAME,ci.CLASS_DESC,p.PROJ_START_TIME,p.PROJ_END_TIME,p.P_YX"
      + ",p.P_TOTAL,p.PRINT_STATUS,p.PROJ_ACTIVE_TYPE,p.BUDGET_ID,p.PROJ_TYPE,p.PROJ_LEADER"
      + " from PROJECT p left outer join DEPARTMENT dep on p.DEPT_ID = dep.SEQ_ID"
      + " left outer join CODE_ITEM ci on p.PROJ_ACTIVE_TYPE = ci.SEQ_ID "
      + " left outer join BUDGET_APPLY ba on p.BUDGET_ID = ba.SEQ_ID "
      + " left outer join PERSON pn on p.PROJ_LEADER = pn.SEQ_ID "
      + " where p.PROJ_TYPE = '" + projMemType + "' and p.SEQ_ID in (" + projId + ")";
    T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request);
    T9PageDataList pageDataList = T9PageLoader.loadPageList(conn,queryParam,sql);
    return pageDataList.toJson();
  }
}
