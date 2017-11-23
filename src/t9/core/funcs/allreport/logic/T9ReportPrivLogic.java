package t9.core.funcs.allreport.logic;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import t9.core.funcs.allreport.data.T9AllReportPriv;
import t9.core.funcs.person.data.T9Person;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.file.T9FileUploadForm;

public class T9ReportPrivLogic {

	/**
	 * 报表权限设置
	 * @param conn
	 * @param rId
	 * @return
	 * @throws Exception
	 */
	public String getReportPrivByRidLogic(Connection conn,String rId)  throws Exception{
	     Statement stmt=null;
	     ResultSet rs=null;
	     String data="";
	     try{
	       String sql="select * from all_report_priv where r_id="+rId;
	       //System.out.println(sql);
	       stmt=conn.createStatement();
	       rs=stmt.executeQuery(sql);
	       while(rs.next()){
	        int seqId=rs.getInt("seq_id"); 
	        String userStr=rs.getString("user_str");
	        //String deptStr=rs.getString("dept_str");
	        //userStr=this.getUserNamesbyIds(conn,userStr);
	        //deptStr=this.getDeptNamesbyIds(conn,deptStr);
	       //data+="{seqId:'"+seqId+"',userStr:'"+userStr+"',deptStr:'"+deptStr+"'}";
	       //data+="{seqId:'"+seqId+"',userStr:'"+this.getUserNamesbyIds(conn,userStr)+"',dept:'"+deptStr+"',deptStr:'"+this.getDeptNamesbyIds(conn,deptStr)+"'}";
	       data+="{seqId:'"+seqId+"',userIds:'"+userStr+"',userStr:'"+this.getUserNamesbyIds(conn,userStr)+"'}";
	       //data+="{seqId:'"+seqId+"',userIds:'"+userStr+"',userStr:'"+this.getUserNamesbyIds(conn,userStr)+"',dept:'"+deptStr+"',deptStr:'"+this.getDeptNamesbyIds(conn,deptStr)+"'}";
	       data+=",";
	       }

	     }catch(Exception e){
	       throw e;
	     }finally{
	       T9DBUtility.close(stmt, rs, null);
	     }
	     if(data.endsWith(",")){
	       data=data.substring(0,data.length()-1);
	     }
	     return data;
	   }
	
	public String getUserNamesbyIds(Connection conn,String ids) throws Exception{
	     String names="";
	     String id[]=ids.split(",");
	     for(int i=0;i<id.length;i++){
	       if(!"".equals(id[i])){
	       names+=this.getUserNameById(conn,id[i]);
	       names+=",";
	     }
	    }
	     if(names.endsWith(",")){
	       names=names.substring(0,names.length()-1);
	     }
	     return names;
	   }
	public String getUserNameById(Connection conn,String id) throws Exception{
	     Statement stmt=null;
	     ResultSet rs=null;
	     String name="";
	     try {
	       String sql="select user_name from person where seq_id="+id;
	      stmt=conn.createStatement();
	      rs=stmt.executeQuery(sql);
	      if(rs.next()){
	        name=rs.getString("user_name");
	      }
	    } catch (SQLException e) {
	      // TODO Auto-generated catch block
	      throw e;
	    }finally{
	      T9DBUtility.close(stmt, rs, null);
	    }

	     return name;
	   }

	
	/*public String getDeptNamesbyIds(Connection conn, String ids){
	     String names="";
	     if("ALL_DEPT".equals(ids)) {
	       return "全体部门";
	     }else if("DEPT".equals(ids)){
	       return "本部门";
	     }
	     else {
	      String dept[]=ids.split(",");
	      for(int i=0;i<dept.length;i++){
	        if(!"".equals(dept[i])){
	        names+=getDeptNameById(conn,dept[i]);
	        names+=",";
	       }
	      } 
	    }
	     if(names.endsWith(",")){
	       names=names.substring(0,names.length()-1);
	     }
	     return names;
}
	 public String getDeptNameById(Connection conn,String id){
	     Statement stmt=null;
	     ResultSet rs=null;
	     String name="";
	     try {
	       String sql="select dept_name from department where seq_id="+id;
	      stmt=conn.createStatement();
	      rs=stmt.executeQuery(sql);
	      if(rs.next()){
	        name=rs.getString("dept_name");
	      }
	    } catch (SQLException e) {
	      // TODO Auto-generated catch block
	      e.printStackTrace();
	    }finally{
	      T9DBUtility.close(stmt, rs, null);
	    }

	     return name;
	   }*/
	 
	 /**
	  * 删除权限
	  */
	 public void delReportPrivByIdLogic(Connection conn,String rId) throws Exception{
	     Statement stmt=null;
	     ResultSet rs=null;
	     try {
	       T9ORM orm = new T9ORM();
	       T9AllReportPriv report = (T9AllReportPriv) orm.loadObjSingle(conn,T9AllReportPriv.class, Integer.parseInt(rId));
	       orm.deleteSingle(conn,report);
	    } catch (SQLException e) {
	      // TODO Auto-generated catch block
	      e.printStackTrace();
	    }finally{
	      T9DBUtility.close(stmt, rs, null);
	    }

	   }
	 /**
	  * 添加权限
	  */
	 public void addReportPrivLogic(Connection conn,T9FileUploadForm fileForm,T9Person person)  throws Exception{

		   String rid = fileForm.getParameter("rid");
		   String user_str = fileForm.getParameter("userId");
		   //String dept_str = fileForm.getParameter("deptvalue");

		   try{
		     T9AllReportPriv priv=new T9AllReportPriv();
		     priv.setRId(Integer.parseInt(rid));
		     priv.setUserStr(user_str);
		     //priv.setDeptStr(dept_str);
		     T9ORM orm=new T9ORM();
		     orm.saveSingle(conn, priv);
		   }catch(Exception e){
		     throw e;
		   }
		   
		 }
	 /**
	  * 编辑权限
	  */
	 public void updateReportPrivLogic(Connection conn,String seqId,String userstr, T9Person person)  throws Exception{

		   
		   try{
		     T9ORM orm=new T9ORM();
		     T9AllReportPriv priv=new T9AllReportPriv();
		     priv =(T9AllReportPriv)orm.loadObjComplex(conn, T9AllReportPriv.class,Integer.parseInt(seqId));
		     //priv.setSeqId(seq_Id);
		     priv.setUserStr(userstr);
		     //priv.setDeptStr(deptstr);
		     orm.updateSingle(conn, priv);
		   }catch(Exception e){
		     throw e;
		   }
		   
		 }
	 public String getReportPrivByPidLogic(Connection conn,String id)  throws Exception{
     Statement stmt=null;
     ResultSet rs=null;
     String data="";
     try{
       String sql="select * from all_report_priv where seq_id="+id;
       stmt=conn.createStatement();
       rs=stmt.executeQuery(sql);
       while(rs.next()){
        int seqId=rs.getInt("seq_id"); 
        String userStr=rs.getString("user_str");
        //String deptStr=rs.getString("dept_str");
        //data+="{seqId:'"+seqId+"',userIds:'"+userStr+"',userStr:'"+this.getUserNamesbyIds(conn,userStr)+"',dept:'"+deptStr+"',deptStr:'"+this.getDeptNamesbyIds(conn,deptStr)+"'}";
        data+="{seqId:'"+seqId+"',userIds:'"+userStr+"',userStr:'"+this.getUserNamesbyIds(conn,userStr)+"'}";
       data+=",";
       }

     }catch(Exception e){
       throw e;
     }finally{
       T9DBUtility.close(stmt, rs, null);
     }
     if(data.endsWith(",")){
       data=data.substring(0,data.length()-1);
     }
     return data;
   }
}
