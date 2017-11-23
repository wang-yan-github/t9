package t9.subsys.oa.asset.logic;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import t9.subsys.oa.asset.data.T9CpCptlInfo;
import t9.subsys.oa.asset.data.T9CpCptlRecord;
import t9.core.data.T9PageDataList;
import t9.core.data.T9PageQueryParam;
import t9.core.load.T9PageLoader;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.form.T9FOM;

public class T9CpCptlInfoLogic {
  private static Logger log = Logger.getLogger("t9.core.funcs.asset.act.T9CpCptlInfoAct");
  /***
   * 根据条件查询数据,通用列表显示数据,实现分页
   * @return
   * @throws Exception 
   */
  public String assetSelect(Connection dbConn,Map request,T9CpCptlInfo cp,String cpreFlag) throws Exception {
    String sql = "select cp.SEQ_ID,cp.CPTL_NO,cpa.TYPE_NAME,cp.CPTL_NAME,cp.TYPE_ID,cp.USE_STATE,cp.USE_FOR,"
      + "cp.CPTL_VAL,cp.CPTL_QTY,cp.CREATE_DATE,cp.SAFEKEEPING,cp.KEEPER,cp.REMARK,cp.LIST_DATE,cp.CPTL_SPEC,"
      + "cp.NO_DEAL,cp.IN_FINANCE,cp.USE_USER,cp.AFTER_INDATE,cp.GET_DATE,cp.USE_DEPT from cp_cptl_info cp"
      + " left outer join CP_ASSET_TYPE cpa on cpa.SEQ_ID=cp.CPTL_SPEC where 1=1 ";
    if (cpreFlag.equals("1")) {
      sql += " and (cp.USE_USER is null or cp.USE_USER='' ) "; 
    }
    if (cpreFlag.equals("2")) {
      sql += " and (cp.USE_USER is not null or cp.USE_USER<> '' ) ";  
    }
    if (!T9Utility.isNullorEmpty(cp.getCptlNo())) {
      //String cptlNo = cp.getCptlNo().replaceAll("'","''").replace("\\", "\\\\").replace("%","\\%").replace("_", "\\_").replace("％", "\\％");
      sql += " and cp.CPTL_NO like '%" + T9DBUtility.escapeLike(cp.getCptlNo()) + "%' " + T9DBUtility.escapeLike();
    }
    if (!T9Utility.isNullorEmpty(cp.getCptlName())) {
      //String cptlName = cp.getCptlName().replaceAll("'","''").replace("\\", "\\\\").replace("%","\\%").replace("_", "\\_").replace("％", "\\％");
      sql += " and cp.CPTL_NAME like '%" + T9DBUtility.escapeLike(cp.getCptlName()) + "%' " + T9DBUtility.escapeLike();
    }
    if (!T9Utility.isNullorEmpty(cp.getCptlSpec())) {
      //String cptlSpec = cp.getCptlSpec().replaceAll("'","''").replace("\\", "\\\\").replace("%","\\%").replace("_", "\\_").replace("％", "\\％");
      sql += " and cp.CPTL_SPEC like '%" + T9DBUtility.escapeLike(cp.getCptlSpec()) + "%' " + T9DBUtility.escapeLike();
    }
    if (cp.getSeqId() > 0) {
      sql += " and cp.SEQ_ID like '%" + cp.getSeqId() + "%'";
    }
    if (!T9Utility.isNullorEmpty(cp.getTypeId())) {
      // String typeId = cp.getTypeId().replaceAll("'","''").replace("\\", "\\\\").replace("%","\\%").replace("_", "\\_").replace("％", "\\％");
      sql += " and cp.TYPE_ID like '%" + T9DBUtility.escapeLike(cp.getTypeId()) + "%' " + T9DBUtility.escapeLike();
    }
    if (!T9Utility.isNullorEmpty(cp.getUseState())) {
      //String useState = cp.getUseState().replaceAll("'","''").replace("\\", "\\\\").replace("%","\\%").replace("_", "\\_").replace("％", "\\％");
      sql += " and cp.USE_STATE like '%" + T9DBUtility.escapeLike(cp.getUseState()) + "%' " + T9DBUtility.escapeLike();
    }
    if (!T9Utility.isNullorEmpty(cp.getUseFor())) {
      //String useFor = cp.getUseFor().replaceAll("'","''").replace("\\", "\\\\").replace("%","\\%").replace("_", "\\_").replace("％", "\\％");
      sql += " and cp.USE_FOR like '%" + T9DBUtility.escapeLike(cp.getUseFor()) + "%' " + T9DBUtility.escapeLike();
    }
    sql += "  order by cp.SEQ_ID desc ";
    T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request);
    T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn,queryParam,sql);
    return pageDataList.toJson();
  }
  /***
   * 根据条件修改数据
   * @return
   * @throws Exception 
   */
  public void asset(Connection dbConn,T9CpCptlInfo cp) throws Exception {
    ResultSet rs = null;
    PreparedStatement stmt = null ;
    try {
      String seqId = selectDept(dbConn,cp.getUseDept());
      String useId = selectUser(dbConn,cp.getUseUser());
      String sql = "update cp_cptl_info set USE_DEPT='" + seqId + "'"
      + ",USE_USER='"+ useId + "',USE_STATE='3',NO_DEAL='1',IN_FINANCE='1'  where SEQ_ID=" + cp.getSeqId();
      stmt = dbConn.prepareStatement(sql);
      stmt.executeUpdate();
    } catch (Exception e) {
      //System.out.println(e.getMessage());
    } finally {
      T9DBUtility.close(stmt,rs,log);
    }
  }
  
  /***
   * 根据查询数据，部门ID
   * @return
   * @throws Exception 
   */
  public String selectDept(Connection dbConn,String deptName) throws Exception {
    ResultSet rs = null;
    String seqId = "";
    PreparedStatement stmt = null ;
    try {
      String sql = "select seq_id,dept_name from department where dept_name=?";
      stmt = dbConn.prepareStatement(sql);
      stmt.setString(1,deptName);
      rs = stmt.executeQuery();
      if (rs.next()) {
        seqId = rs.getString("seq_id");
      }
    } catch (Exception e) {
      //System.out.println(e.getMessage());
    } finally {
      T9DBUtility.close(stmt,rs,log);
    }
    return seqId;
  }
  
  /***
   * 根据查询数据，用户Id
   * @return
   * @throws Exception 
   */
  public String selectUser(Connection dbConn,String useName) throws Exception {
    ResultSet rs = null;
    String seqId = "";
    PreparedStatement stmt = null ;
    try {
      String sql = "select seq_id,user_name from person where user_name=?";
      stmt = dbConn.prepareStatement(sql);
      stmt.setString(1,useName);
      rs = stmt.executeQuery();
      if (rs.next()) {
        seqId = rs.getString("seq_id");
      }
    } catch (Exception e) {
      //System.out.println(e.getMessage());
    } finally {
      T9DBUtility.close(stmt,rs,log);
    }
    return seqId;
  }

  /***
   * 根据条件修改数据
   * @return
   * @throws Exception 
   */
  public void udpateAsset(Connection dbConn,T9CpCptlInfo cp) throws Exception {
    ResultSet rs = null;
    PreparedStatement stmt = null ;
    try {
      String keeper = selectUser(dbConn,cp.getKeeper());
      String sql = "update cp_cptl_info set "
        + "USE_DEPT='',USE_USER='',USE_STATE='1',NO_DEAL='1',IN_FINANCE='1' ";
      if (!T9Utility.isNullorEmpty(cp.getKeeper())) {
        sql += ",KEEPER='" + keeper + "'";
      }
      if (!T9Utility.isNullorEmpty(cp.getRemark())) {
        sql += ",REMARK='" + cp.getRemark().replace("'", "''") + "'";
      }
      sql += "  where SEQ_ID=" + cp.getSeqId();
      stmt = dbConn.prepareStatement(sql);
      stmt.executeUpdate();
    } catch (Exception e) {
      //System.out.println(e.getMessage());
    } finally {
      T9DBUtility.close(stmt,rs,log);
    }
  }

  /***
   * 增加领用单
   * @return
   * @throws Exception 
   */
  public void assetRunId(Connection dbConn,T9CpCptlRecord record) throws Exception {
    ResultSet rs = null;
    PreparedStatement stmt = null ;
    String user = selectUser(dbConn,record.getCpreUser());//名字
    String keeper = selectUser(dbConn,record.getCpreKeeper());//名字
    try { 
      String sql = "insert into cp_cptl_record(CPTL_ID,CPRE_QTY,CPRE_USER,"
        + "CPRE_DATE,CPRE_RECORDER,CPRE_FLAG,RUN_ID,CPRE_KEEPER,CPRE_MEMO,DEPT_ID)values(?,?,?,?,?,?,?,?,?,?)";
      stmt = dbConn.prepareStatement(sql);
      stmt.setInt(1,record.getCptlId());
      stmt.setInt(2,record.getCpreQty());
      stmt.setString(3,user);
      stmt.setDate(4,record.getCpreDate());
      stmt.setString(5,user);
      stmt.setString(6,record.getCpreFlag());
      stmt.setInt(7,record.getRunId());
      stmt.setString(8,keeper);
      stmt.setString(9,record.getCpreMemo());
      stmt.setInt(10,record.getDeptId());
      stmt.executeUpdate();
    } catch (Exception e) {
      //System.out.println(e.getMessage());
    } finally {
      T9DBUtility.close(stmt,rs,log);
    }
  }

  public static List<T9CpCptlInfo> nameList(Connection dbConn,String useFlag) {
    ResultSet rs = null;
    PreparedStatement stmt = null ;
    T9CpCptlInfo cpInfo = null;
    List<T9CpCptlInfo> list = new ArrayList<T9CpCptlInfo>();
    String sql = "SELECT SEQ_ID,CPTL_NAME,CPTL_NO,CPTL_SPEC from cp_cptl_info where 1=1";
    if (useFlag.equals("1")) {
      sql += " and USE_USER is null or USE_USER='' ";
    }
    if (useFlag.equals("2")) {
      sql += " and (USE_USER is not null or USE_USER<> '' ) ";
    }
    try {
      stmt = dbConn.prepareStatement(sql);
      rs = stmt.executeQuery();
      while (rs.next()) {
        cpInfo =  new T9CpCptlInfo();
        cpInfo.setSeqId(rs.getInt("SEQ_ID"));
        cpInfo.setCptlName(rs.getString("CPTL_NAME"));
        cpInfo.setCptlNo(rs.getString("CPTL_NO"));
        cpInfo.setCptlSpec(rs.getString("CPTL_SPEC"));
        list.add(cpInfo);
      }
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }finally {
      T9DBUtility.close(stmt,null,log);
    } 
    return list;
  }

  /***
   * 根据条件查询数据,通用列表显示数据,实现分页
   * @return
   * @throws Exception 
   */
  public String cpcpSelect(Connection dbConn,Map request,String name) throws Exception {
    String sql = "select cp.SEQ_ID,cp.CPTL_NO,cp.CPTL_NAME,cp.CPTL_VAL,cp.CPTL_QTY,cpa.TYPE_NAME,"
      + "cp.LIST_DATE,cp.TYPE_ID,cp.CREATE_DATE,"
      + "cp.SAFEKEEPING,cp.KEEPER,cp.REMARK,cp.NO_DEAL,cp.IN_FINANCE,cp.USE_USER,cp.USE_STATE,cp.USE_FOR,"
      + "cp.AFTER_INDATE,cp.GET_DATE,cp.USE_DEPT,cp.CPTL_SPEC from cp_cptl_info cp"
      + " left outer join CP_ASSET_TYPE cpa on cpa.SEQ_ID=cp.CPTL_SPEC where cp.USE_USER='" + name + "'"
      + "  order by cp.LIST_DATE desc ";
    T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request);
    T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn,queryParam,sql);
    return pageDataList.toJson();
  }
  /***
   * 根据条件查询数据,通用列表显示数据,实现分页
   * @return
   * @throws Exception 
   */
  public String querySelect(Connection dbConn,Map request,T9CpCptlInfo cp,double getCptlValMax) throws Exception {
    String sql = "select cp.SEQ_ID,cp.CPTL_NO,cp.CPTL_NAME,cp.CPTL_VAL,cp.CPTL_QTY,cpa.TYPE_NAME,"
      + "cp.LIST_DATE,cp.USE_USER,cp.TYPE_ID,cp.CREATE_DATE,"
      + "cp.SAFEKEEPING,cp.KEEPER,cp.REMARK,cp.NO_DEAL,cp.IN_FINANCE,cp.USE_STATE,cp.USE_FOR,"
      + "cp.AFTER_INDATE,cp.GET_DATE,cp.USE_DEPT,cp.CPTL_SPEC from cp_cptl_info cp"
      + " left outer join CP_ASSET_TYPE cpa on cpa.SEQ_ID=cp.CPTL_SPEC where 1=1 ";
    if (!T9Utility.isNullorEmpty(cp.getCptlNo())) {
      sql += " and cp.CPTL_NO like '%" + T9DBUtility.escapeLike(cp.getCptlNo()) + "%' " + T9DBUtility.escapeLike();
    }
    if (!T9Utility.isNullorEmpty(cp.getCptlName())) {
      sql += " and cp.CPTL_NAME like '%" + T9DBUtility.escapeLike(cp.getCptlName()) + "%' " + T9DBUtility.escapeLike();
    }
    if (!T9Utility.isNullorEmpty(cp.getCptlSpec())) {
      sql += " and cp.CPTL_SPEC='" + cp.getCptlSpec() + "'";
    }
    if (cp.getCptlVal() > 0) {
      sql += " and cp.CPTL_VAL >= " + cp.getCptlVal();
    }
    if (getCptlValMax > 0) {
      sql += " and cp.CPTL_VAL <= " + getCptlValMax;
    }
    if (cp.getListDate() != null) {
      String str =  T9DBUtility.getDateFilter("cp.LIST_DATE", T9Utility.getDateTimeStr(cp.getListDate()), ">=");
      sql += " and " + str;
    }
    if (cp.getGetDate() != null) {
      String str =  T9DBUtility.getDateFilter("cp.LIST_DATE", T9Utility.getDateTimeStr(cp.getGetDate()), "<=");
      sql += " and " + str;
    }
    if (!T9Utility.isNullorEmpty(cp.getKeeper())) {
      sql += " and cp.KEEPER like '%" + T9DBUtility.escapeLike(cp.getKeeper()) + "%' " + T9DBUtility.escapeLike();
    }
    if (!T9Utility.isNullorEmpty(cp.getSafekeeping())) {
      sql += " and cp.SAFEKEEPING like '%" + T9DBUtility.escapeLike(cp.getSafekeeping()) + "%' " + T9DBUtility.escapeLike();
    }
    if (!T9Utility.isNullorEmpty(cp.getRemark())) {
      sql += " and cp.REMARK like '%" + T9DBUtility.escapeLike(cp.getRemark()) + "%' " + T9DBUtility.escapeLike();
    }
    sql += "  order by cp.LIST_DATE desc ";
    
    T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request);
    T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn,queryParam,sql);
    return pageDataList.toJson();
  }
  

  /***
   * 根据条件查询数据,通用列表显示数据,实现分页
   * @return
   * @throws Exception 
   */
  public String applySelect(Connection dbConn,Map request) throws Exception {
    String sql = "select cp.SEQ_ID,cp.CPTL_NO,cp.CPTL_NAME,cp.CPTL_VAL,cp.CPTL_QTY,cpa.TYPE_NAME,"
      + "cp.LIST_DATE,cp.TYPE_ID,cp.CREATE_DATE,"
      + "cp.SAFEKEEPING,cp.KEEPER,cp.REMARK,cp.NO_DEAL,cp.IN_FINANCE,cp.USE_USER,cp.USE_STATE,cp.USE_FOR,"
      + "cp.AFTER_INDATE,cp.GET_DATE,cp.USE_DEPT,cp.CPTL_SPEC from cp_cptl_info cp"
      + " left outer join CP_ASSET_TYPE cpa on cpa.SEQ_ID=cp.CPTL_SPEC where cp.USE_USER is null or cp.USE_USER='' "
      + "  order by cp.LIST_DATE desc ";
    T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request);
    T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn,queryParam,sql);
    return pageDataList.toJson();
  }
  
  /***
   * 根据条件查询数据,通用列表显示数据,实现分页
   * @return
   * @throws Exception 
   */
  public String listSelect(Connection dbConn,Map request,T9CpCptlInfo cp,double getCptlValMax) throws Exception {
    String sql = "select cp.SEQ_ID,cp.CPTL_NO,cp.CPTL_NAME,cp.CPTL_VAL,cp.CPTL_QTY,cpa.TYPE_NAME,"
      + "cp.LIST_DATE,cp.USE_USER,cp.TYPE_ID,cp.CREATE_DATE,"
      + "cp.SAFEKEEPING,cp.KEEPER,cp.REMARK,cp.NO_DEAL,cp.IN_FINANCE,cp.USE_STATE,cp.USE_FOR,"
      + "cp.CPTL_SPEC,cp.AFTER_INDATE,cp.GET_DATE,cp.USE_DEPT from cp_cptl_info cp"
      + " left outer join CP_ASSET_TYPE cpa on cpa.SEQ_ID=cp.CPTL_SPEC where 1=1 ";
    if (!T9Utility.isNullorEmpty(cp.getCptlNo())) {
      //sql += " and bu.BUDGET_ITEM like '%" + T9Utility.encodeLike(chargeItem) + "%' " + T9DBUtility.escapeLike();
     // String useState = cp.getCptlNo().replaceAll("'","''").replace("\\", "\\\\").replace("%","\\%").replace("_", "\\_").replace("％", "\\％");
      //sql += " and cp.CPTL_NO like '%" + useState + "%' ";
      sql += " and cp.CPTL_NO like '%" + T9DBUtility.escapeLike(cp.getCptlNo()) + "%' " + T9DBUtility.escapeLike();
    }
    if (!T9Utility.isNullorEmpty(cp.getCptlName())) {
      sql += " and cp.CPTL_NAME like '%" + T9DBUtility.escapeLike(cp.getCptlName()) + "%' " + T9DBUtility.escapeLike();
    }
    if (!T9Utility.isNullorEmpty(cp.getCptlSpec())) {
      sql += " and CPTL_SPEC='" + cp.getCptlSpec() + "'";
    }
    if (cp.getCptlVal() > 0) {
      sql += " and cp.CPTL_VAL >= " + cp.getCptlVal();
    }
    if (getCptlValMax > 0) {
      sql += " and cp.CPTL_VAL <= " + getCptlValMax;
    }    
    if (cp.getListDate() != null) {
      String str =  T9DBUtility.getDateFilter("cp.LIST_DATE", T9Utility.getDateTimeStr(cp.getListDate()), ">=");
      sql += " and " + str;
    }
    if (cp.getGetDate() != null) {
      String str =  T9DBUtility.getDateFilter("cp.LIST_DATE", T9Utility.getDateTimeStr(cp.getGetDate()), "<=");
      sql += " and " + str;
    }
    
    if (!T9Utility.isNullorEmpty(cp.getKeeper())) {
      sql += " and cp.KEEPER like '%" + T9DBUtility.escapeLike(cp.getKeeper()) + "%' " + T9DBUtility.escapeLike();
    }
    if (!T9Utility.isNullorEmpty(cp.getSafekeeping())) {
      sql += " and cp.SAFEKEEPING like '%" + T9DBUtility.escapeLike(cp.getSafekeeping()) + "%' " + T9DBUtility.escapeLike();
    }
    if (!T9Utility.isNullorEmpty(cp.getRemark())) {
      sql += " and cp.REMARK like '%" + T9DBUtility.escapeLike(cp.getRemark()) + "%' " + T9DBUtility.escapeLike();
    }
    sql += "  order by cp.LIST_DATE desc ";

    T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request);
    T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn,queryParam,sql);
    return pageDataList.toJson();
  }
  //seqId串转换成NAME串
  public String getName(Connection dbConn,String seqId) throws Exception {
    if (T9Utility.isNullorEmpty(seqId)) {
      seqId = "0";
    }
    String sql = "select seq_id,user_name from  person where seq_id =" + seqId;
    PreparedStatement ps = null;
    ResultSet rs = null;
    String name = "";
    try{
      ps = dbConn.prepareStatement(sql);
      rs = ps.executeQuery();
      while (rs.next()) {
        name += rs.getString("user_name");
      }
    }catch (Exception e) {
      throw e;
    }finally {
      T9DBUtility.close(ps, rs, null);
    }
    return name;
  }
  
  //seqId串转换成NAME串
  public String getDept(Connection dbConn,String seqId) throws Exception {
    if (T9Utility.isNullorEmpty(seqId)) {
      seqId = "0";
    }
    String sql = "select seq_id,dept_name from department where seq_id =" + seqId;
    PreparedStatement ps = null;
    ResultSet rs = null;
    String name = "";
    try{
      ps = dbConn.prepareStatement(sql);
      rs = ps.executeQuery();
      while (rs.next()) {
        name += rs.getString("dept_name");
      }
    }catch (Exception e) {
      throw e;
    }finally {
      T9DBUtility.close(ps, rs, null);
    }
    return name;
  }
}
