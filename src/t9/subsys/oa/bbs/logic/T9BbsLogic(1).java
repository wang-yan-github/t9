package t9.subsys.oa.bbs.logic;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.system.interfaces.data.T9SysPara;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;

public class T9BbsLogic {

  public String getBbsUrlLogic(Connection conn,T9Person person)throws Exception{
    Statement stmt=null;
    ResultSet rs=null;
    String data="";
    try{
      String sql="select * from sys_para where para_name='bbs_url'";
      stmt=conn.createStatement();
      rs=stmt.executeQuery(sql);
      String url="";
      if(rs.next()){
        url=rs.getString("para_value");
      }
      if(T9Utility.isNullorEmpty(url)){
        url="";
      }
      data="{url:'"+url+"',userId:'"+person.getUserId()+"',userName:'"+person.getUserName()+"',email:'"+person.getEmail()+"',birth:'"+person.getBirthday()+"',role:'"+person.getUserPriv()+"'}";
    }catch(Exception ex){
      ex.printStackTrace();
    }
    finally{
      T9DBUtility.close(stmt, rs, null);
    }
    
    return data;
  }
  
  
  
  public void saveBbsUrlLogic(Connection conn,String url)throws Exception{
    Statement stmt=null;
    ResultSet rs=null;
    String data="";
    try{
      T9ORM orm=new T9ORM();
      T9SysPara param=new T9SysPara(); 
      String sql="select * from sys_para where para_name='bbs_url'";
      stmt=conn.createStatement();
      rs=stmt.executeQuery(sql);
      if(rs.next()){
       param.setSeqId(rs.getInt("seq_id"));
       param.setParaName(rs.getString("para_name"));
       param.setParaValue(url);
       orm.updateSingle(conn, param);
      }
      param.setParaName("bbs_url");
      param.setParaValue(url);
      orm.saveSingle(conn, param);
     }catch(Exception ex){
      ex.printStackTrace();
    }
    finally{
      T9DBUtility.close(stmt, rs, null);
    }
    
  
  }
  
}
