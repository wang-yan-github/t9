package t9.mobile.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import t9.core.util.db.T9DBUtility;

public class T9QuickQuery {
	/**
	 * 通过select count(1)得到记录总数
	 * @param conn
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	public static int getCount(Connection conn,String sql) throws Exception{
		int c = 0;
		Statement stmt = null;
		ResultSet rs = null;
		try{
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			if(rs.next()){
				c = rs.getInt(1);
			}
		}catch(Exception e){
			throw e;
		}finally{
			T9DBUtility.close(stmt, rs, null);
		}
		return c;
	}
	
	/**
	 * 通过游标获取总数
	 * @param conn
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	public static  int getCountByCursor(Connection conn,String sql) throws Exception{
		int c = 0;
		Statement stmt = null;
		ResultSet rs = null;
		try{
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
			rs = stmt.executeQuery(sql);
			rs.last();
			c = rs.getRow();
		}catch(Exception e){
			throw e;
		}finally{
			T9DBUtility.close(stmt, rs, null);
		}
		return c;
	}
	public static  int getCountByCursor(Connection conn,String sql, String CURRITERMS) throws Exception{
    int c = 0;
    Statement stmt = null;
    ResultSet rs = null;
    int c2 = T9MobileUtility.getCURRITERMS(CURRITERMS);
    int j = 0;
    try{
      stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
      rs = stmt.executeQuery(sql);
      while (rs.next()) {
        if (c < c2 ) {
          c++;
          continue;
        }
         
        if (c >= T9MobileConfig.PAGE_SIZE + c2)
          break;
        j++;
      }
    }catch(Exception e){
      throw e;
    }finally{
      T9DBUtility.close(stmt, rs, null);
    }
    return j;
  }
	/**
   * 快速获取查询list
   * @param conn
   * @param sql
   * @return
   * @throws Exception
   */
  public static  List<Map<String,String>> quickQueryList(Connection conn,String sql , String CURRITERMS) throws Exception{
    Statement stmt = null;
    ResultSet rs = null;
    List<String> metaDataNames = new ArrayList<String>();
    ResultSetMetaData rsmd = null;
    Map<String,String> datas = null;
    int c = T9MobileUtility.getCURRITERMS(CURRITERMS);
    int j = 0;
    
    List<Map<String,String>> list = new ArrayList<Map<String,String>>();
    try{
      stmt = conn.createStatement();
      rs = stmt.executeQuery(sql);
      rsmd = rs.getMetaData();
      for(int i=1;i<=rsmd.getColumnCount();i++){
        metaDataNames.add(rsmd.getColumnLabel(i));
      }
      while(rs.next()){
        if (j < c ) {
          j++;
          continue;
        }
        if (j >= T9MobileConfig.PAGE_SIZE + c)
          break;
        
        
        datas = new HashMap<String,String>();
        for(String name:metaDataNames){
          datas.put(name, rs.getString(name));
        }
        list.add(datas);
        
        j++;
      }
    }catch(Exception e){
      throw e;
    }finally{
      T9DBUtility.close(stmt, rs, null);
    }
    return list;
  }
	/**
	 * 快速获取查询list
	 * @param conn
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	public static  List<Map<String,String>> quickQueryList(Connection conn,String sql) throws Exception{
		Statement stmt = null;
		ResultSet rs = null;
		List<String> metaDataNames = new ArrayList<String>();
		ResultSetMetaData rsmd = null;
		Map<String,String> datas = null;
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		try{
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			rsmd = rs.getMetaData();
			for(int i=1;i<=rsmd.getColumnCount();i++){
				metaDataNames.add(rsmd.getColumnLabel(i));
			}
			while(rs.next()){
				datas = new HashMap<String,String>();
				for(String name:metaDataNames){
					datas.put(name, rs.getString(name));
				}
				list.add(datas);
			}
		}catch(Exception e){
			throw e;
		}finally{
			T9DBUtility.close(stmt, rs, null);
		}
		return list;
	}
	
	/**
	 * 快速获取一条记录
	 * @param conn
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	public static Map<String,String> quickQuery(Connection conn,String sql) throws Exception{
		Statement stmt = null;
		ResultSet rs = null;
		List<String> metaDataNames = new ArrayList<String>();
		ResultSetMetaData rsmd = null;
		Map<String,String> datas = new HashMap();
		try{
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			rsmd = rs.getMetaData();
			for(int i=1;i<=rsmd.getColumnCount();i++){
				metaDataNames.add(rsmd.getColumnLabel(i));
			}
			if(rs.next()){
				for(String name:metaDataNames){
					datas.put(name, rs.getString(name));
				}
			}
		}catch(Exception e){
			throw e;
		}finally{
			T9DBUtility.close(stmt, rs, null);
		}
		return datas;
	}
	
	/**
	 * 更新与保存方法
	 * @param conn
	 * @param sql
	 * @throws Exception
	 */
	public static void update(Connection conn,String sql)throws Exception{
		Statement stmt = null;
		try{
			stmt = conn.createStatement();
			stmt.executeUpdate(sql);
		}catch(Exception e){
			throw e;
		}finally{
			T9DBUtility.close(stmt, null, null);
		}
	}
}
