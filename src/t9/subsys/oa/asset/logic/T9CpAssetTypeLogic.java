package t9.subsys.oa.asset.logic;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import t9.core.util.db.T9DBUtility;
import t9.subsys.oa.asset.data.T9CpAssetType;
import t9.subsys.oa.asset.data.T9CpCptlInfo;

public class T9CpAssetTypeLogic {
  private static Logger log = Logger.getLogger("t9.core.funcs.asset.act.T9CpAssetTypeAct");


  public static List<T9CpAssetType> specList(Connection dbConn) {
    ResultSet rs = null;
    PreparedStatement stmt = null ;
    T9CpAssetType cp = null;
    List<T9CpAssetType> list = new ArrayList<T9CpAssetType>();
    String sql = "SELECT SEQ_ID,TYPE_NAME,TYPE_NO from CP_ASSET_TYPE order by TYPE_NO";
    try {
      stmt = dbConn.prepareStatement(sql);
      rs = stmt.executeQuery();
      while (rs.next()) {
        cp =  new T9CpAssetType();
        cp.setSeqId(rs.getInt("SEQ_ID"));
        cp.setTypeName(rs.getString("TYPE_NAME"));
        cp.setTypeNo(rs.getInt("TYPE_NO"));
        list.add(cp);
      }
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }finally {
      T9DBUtility.close(stmt,null,log);
    } 
    return list;
  }

  public static T9CpAssetType cptlSpec(Connection dbConn,String typeName) {
    ResultSet rs = null;
    PreparedStatement stmt = null ;
    T9CpAssetType cp = null;
    String sql = "SELECT SEQ_ID,TYPE_NAME from CP_ASSET_TYPE where TYPE_NAME=?";
    try {
      stmt = dbConn.prepareStatement(sql);
      stmt.setString(1,typeName);
      rs = stmt.executeQuery();
      if (rs.next()) {
        cp =  new T9CpAssetType();
        cp.setSeqId(rs.getInt("SEQ_ID"));
        cp.setTypeName(rs.getString("TYPE_NAME"));
      }
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }finally {
      T9DBUtility.close(stmt,null,log);
    } 
    return cp;
  }

  /***
   * 增加数据
   * @return
   * @throws Exception 
   */
  public static void addType(Connection dbConn,T9CpAssetType type) throws Exception {
    PreparedStatement stmt = null ; 
    String sql = "insert into CP_ASSET_TYPE(TYPE_NAME,TYPE_NO) values(?,?)";
    try {
      stmt = dbConn.prepareStatement(sql);
      stmt.setString(1,type.getTypeName());
      stmt.setInt(2,type.getTypeNo());
      stmt.executeUpdate();
    }catch (Exception e) {
      throw e;
    }finally {
      T9DBUtility.close(stmt, null, log);
    }
  }
  /**
   * 取得最大的seqId
   * @param conn
   * @param tableName
   * @return
   * @throws Exception
   */
  public static int getMaxSeqId(Connection conn) throws Exception{
    int result = 0;
    String sql = "select max(SEQ_ID) from CP_ASSET_TYPE";
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = conn.prepareStatement(sql);
      rs = ps.executeQuery();
      if(rs.next()){
        result = rs.getInt(1);
      }
    } catch (Exception e) {
      throw e;
    } finally{
      T9DBUtility.close(ps, rs, log);
    }
    return result;
  }

  /***
   *根据ID查询数据
   * @return
   * @throws Exception 
   */
  public T9CpAssetType selectTypeName(Connection dbConn,String TypeName) throws Exception {
    ResultSet rs = null;
    PreparedStatement stmt = null ; 
    T9CpAssetType planType = null;
    String sql = "select TYPE_NAME from CP_ASSET_TYPE where TYPE_NAME=?";
    try {
      stmt = dbConn.prepareStatement(sql);
      stmt.setString(1,TypeName);
      rs = stmt.executeQuery();
      if (rs.next()) {
        planType = new T9CpAssetType(); 
        planType.setTypeName(rs.getString("TYPE_NAME"));
      }
    }catch (Exception e) {
      throw e;
    }finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return planType;  
  }
  
  //删除所有
  public static void deleteAll (Connection dbConn) throws SQLException {
    PreparedStatement stmt = null ; 
    String sql = "delete from CP_ASSET_TYPE";
    try {
      stmt = dbConn.prepareStatement(sql);
      stmt.executeUpdate();
    } catch (SQLException e) {
      throw e;
    }finally {
      T9DBUtility.close(stmt,null, log);
    }
  }
  
  //查询,故意存放
  public static T9CpCptlInfo getAsset (Connection dbConn,int seqId) throws SQLException {
    PreparedStatement stmt = null ;
    ResultSet rs = null;
    T9CpCptlInfo type = null;

    String sql = "select cp.cptl_no as cptlNo,cp.cptl_name as cptlName,"
      + " cp.cptl_spec as cptlSpec,re.dept_id as deptId,re.cpre_qty as cpreQty,"
      + " re.cpre_place as cprePlace,re.cpre_memo as cpreMemo,"
      + " re.cpre_reason as cpreReason,re.cpre_flag as cpreFlag from cp_cptl_record re "
      + " left outer join cp_cptl_info cp on cp.SEQ_ID=re.cptl_id where re.seq_id=?";
    try {
      stmt = dbConn.prepareStatement(sql);
      stmt.setInt(1,seqId);
      rs = stmt.executeQuery();
      if (rs.next()) {
        type = new T9CpCptlInfo();
        type.setSeqId(seqId);
        type.setCptlNo(rs.getString("cptlNo"));
        type.setCptlName(rs.getString("cptlName"));
        type.setCptlSpec(rs.getString("cptlSpec"));
        type.setUseDept(rs.getString("deptId"));//领用部门
        type.setCptlQty(rs.getInt("cpreQty"));//数量
        type.setRemark(rs.getString("cpreMemo"));//备注
        type.setUseFor(rs.getString("cpreFlag"));//领用单，返库单
        type.setSafekeeping(rs.getString("cprePlace"));//地点
        type.setKeeper(rs.getString("cpreReason"));//原因
      }
    } catch (SQLException e) {
      throw e;
    }finally {
      T9DBUtility.close(stmt,null, log);
    }
    return type;
  }
}
