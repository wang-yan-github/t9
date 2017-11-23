package t9.subsys.oa.addworkfee.logic;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.subsys.oa.addworkfee.data.T9Festival;

/**
 * 节假日
 * @author Administrator
 *
 */
public class T9FestivalLogic{
  
  /**
   * 增加节假日
   * @param conn
   * @param fest
   * @return
   * @throws Exception
   */
  public int addT9Festival(Connection conn, T9Festival fest) throws Exception{
    String sql = " insert into ADD_FESTIVAL(YEAR_FLAG, NAME, BEGIN_DATA, END_DATA) values(?, ?, ?, ?)";
    PreparedStatement ps = null;
    int k = 0;
  try {
      ps = conn.prepareStatement(sql);
      ps.setInt(1, fest.getYear());
      ps.setString(2, fest.getFeName());
      ps.setDate(3, new Date(fest.getBeginDate().getTime()));
      ps.setDate(4, new Date(fest.getEndDate().getTime()));
      k = ps.executeUpdate();
  } catch (Exception ex) {
    throw ex;
  } finally {
     T9DBUtility.close(ps, null, null);
  }
    return k;
  }
  
  public int delT9Festival(){
    return 0;
  }
  
  public int changeT9Festival(){
    return 0;
  }
  
  /**
   * 查找某一年节假日
   * @param conn
   * @param doc
   * @param user
   * @throws SQLException 
   */
  public List<T9Festival> findFestival(Connection conn, String date) throws SQLException{
    String sql = "select SEQ_ID, YEAR_FLAG, NAME, BEGIN_DATA, END_DATA, FTYPE from ADD_FESTIVAL where year_flag=? order by BEGIN_DATA asc" ;
    PreparedStatement ps = null;
    ResultSet rs = null;
    List<T9Festival> dosc = new ArrayList<T9Festival>();
    String year = T9Utility.getCurDateTimeStr("yyyy");
    try{
      if(date != null ){
        year = date;
      }
      ps = conn.prepareStatement(sql);
      ps.setInt(1, Integer.parseInt(year));
      rs = ps.executeQuery();
      while(rs.next()){
        T9Festival fest = new T9Festival();
        fest.setSeqId(rs.getInt("SEQ_ID"));
        fest.setYear(rs.getInt("YEAR_FLAG"));
        fest.setFeName(rs.getString("NAME"));
        fest.setBeginDate((java.util.Date)rs.getObject("BEGIN_DATA"));
        fest.setEndDate((java.util.Date)rs.getObject("END_DATA"));
        fest.setFeType(rs.getInt("FTYPE"));
        dosc.add(fest);
      }
    } catch (SQLException e){
      throw e;
    }
    return dosc;
  }
  
  /**
   * 删除一个日期
   * @param conn
   * @param doc
   * @param user
   * @throws Exception 
   */
  public int delFestival(Connection conn, int seqId) throws Exception{
    String sql = "delete from ADD_FESTIVAL where seq_id=" + seqId ;
    PreparedStatement ps = null;
    int ok =0;
    try{
      ps = conn.prepareStatement(sql);
      ok = ps.executeUpdate();
    } catch (Exception e){
      throw e;
    }
    return ok;
  }
  
  /**
   * 查找某一节假日
   * @param conn
   * @param doc
   * @param user
   * @throws SQLException 
   */
  public T9Festival findFestival(Connection conn, int date) throws SQLException{
    String sql = "select SEQ_ID, YEAR_FLAG, NAME, BEGIN_DATA, END_DATA, FTYPE from ADD_FESTIVAL where SEQ_ID=?" ;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try{
     
      ps = conn.prepareStatement(sql);
      ps.setInt(1, date);
      rs = ps.executeQuery();
      if(rs.next()){
        T9Festival fest = new T9Festival();
        fest.setSeqId(rs.getInt("SEQ_ID"));
        fest.setYear(rs.getInt("YEAR_FLAG"));
        fest.setFeName(rs.getString("NAME"));
        fest.setBeginDate((java.util.Date)rs.getObject("BEGIN_DATA"));
        fest.setEndDate((java.util.Date)rs.getObject("END_DATA"));
        fest.setFeType(rs.getInt("FTYPE"));
        return fest;
      }
    } catch (SQLException e){
      throw e;
    }
    return null;
  }
  
  /**
   * 返回所有的年份
   * @param conn
   * @return
   * @throws SQLException
   */
  public List<Integer> findYearList(Connection conn)throws SQLException{
    String sql = "select DISTINCT YEAR_FLAG from ADD_FESTIVAL order by YEAR_FLAG desc" ;
    PreparedStatement ps = null;
    ResultSet rs = null;
    List<Integer> years = new ArrayList<Integer>();
    try{
      ps = conn.prepareStatement(sql);
      rs = ps.executeQuery();
      while(rs.next()){
        years.add(rs.getInt("YEAR_FLAG"));
      }
    } catch (SQLException e){
      throw e;
    }
    return years;
  }
  

  /**
   * 返回所有的年份
   * @param conn
   * @return
   * @throws SQLException
   */
  public int updateFestival(Connection conn,  T9Festival fest)throws SQLException{
    String sql = "update add_festival set YEAR_FLAG=?, NAME=?, BEGIN_DATA=?, END_DATA=? where SEQ_ID=?" ;
    PreparedStatement ps = null;
    int k =0;
    try{
      ps = conn.prepareStatement(sql);
      ps.setInt(1, fest.getYear());
      ps.setString(2, fest.getFeName());
      ps.setDate(3, new java.sql.Date(fest.getBeginDate().getTime()));
      ps.setDate(4, new java.sql.Date(fest.getEndDate().getTime()));
      ps.setInt(5, fest.getSeqId());
       k = ps.executeUpdate();
    } catch (SQLException e){
      throw e;
    }
    return k;
  }
}
