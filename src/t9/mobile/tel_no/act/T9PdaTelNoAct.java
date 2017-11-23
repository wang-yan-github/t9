package t9.mobile.tel_no.act;

import java.sql.Connection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.db.T9DBUtility;
import t9.mobile.util.T9MobileConfig;
import t9.mobile.util.T9MobileUtility;
import t9.mobile.util.T9QuickQuery;

public class T9PdaTelNoAct {
	public String data(HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		
	  Connection conn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      conn = requestDbConn.getSysDbConn();
		String A = request.getParameter("A");
		String ATYPE = request.getParameter("ATYPE");
		String CURRITERMS = request.getParameter("CURRITERMS");
		
		String query = "";
		String jsonData = "";
		
		if("refreshList".equals(ATYPE))
		   {
		      
		      if("loadList".equals(A))
		      {
		         query = "SELECT PERSON.SEQ_ID as UID,SEX,USER_ID,USER_NAME,PERSON.DEPT_ID,PRIV_NAME from PERSON,USER_PRIV,DEPARTMENT where DEPARTMENT.SEQ_ID!=0 and PERSON.USER_PRIV=USER_PRIV.SEQ_ID and PERSON.DEPT_ID=DEPARTMENT.SEQ_ID order by PRIV_NO,USER_NO,USER_NAME  ";     
		      }else if("getNew".equals(A))
		      {
		    	T9MobileUtility.output(response, "NONEWDATA");
		        return null;    
		      }else
		      {
		         query = "SELECT PERSON.SEQ_ID as UID,SEX,USER_ID,USER_NAME,PERSON.DEPT_ID,PRIV_NAME from PERSON,USER_PRIV,DEPARTMENT where DEPARTMENT.SEQ_ID!=0 and PERSON.USER_PRIV=USER_PRIV.SEQ_ID and PERSON.DEPT_ID=DEPARTMENT.SEQ_ID order by PRIV_NO,USER_NO,USER_NAME  "; 
		         int count = T9QuickQuery.getCountByCursor(conn, query , CURRITERMS);
		         if(count == 0)
		         {
		        	 T9MobileUtility.output(response, "NOMOREDATA");
				     return null;        
		         }
		      }
		      
		      List<Map<String,String>> list = T9QuickQuery.quickQueryList(conn, query , CURRITERMS);
		      for(Map<String,String> data:list)
		      {
		    	  String UID=data.get("UID");
		    	  String USER_NAME=data.get("USER_NAME");
		    	  String PRIV_NAME=data.get("PRIV_NAME");
		    	  String DEPT_ID=data.get("DEPT_ID");
		    	  String SEX=data.get("SEX");
		    	  String DEPT_LONG_NAME=T9MobileUtility.getLongDept(conn, Integer.parseInt(DEPT_ID));
            
				   if("0".equals(SEX))
				      SEX="男";
				   else
					   SEX="女";
				   
		         jsonData +="{q_id:\""+UID+"\",";
		         jsonData +="user_name:\""+USER_NAME+"\",";
		         jsonData +="priv_name:\""+PRIV_NAME+"\",";
		         jsonData +="dept_long_name:\""+DEPT_LONG_NAME+"\",";
		         jsonData +="sex:\""+SEX+"\"},";
		      }
		      
		      jsonData = jsonData.substring(0,jsonData.length()-1);
		      T9MobileUtility.output(response, "["+jsonData+"]");
		   }else if("getTelNoList".equals(ATYPE))
		   {
		      String AREA = request.getParameter("AREA");
		      String TEL_NO = request.getParameter("TEL_NO");
		      String POST_NO = request.getParameter("POST_NO");
		      
		      query = "SELECT * from POST_TEL where 1=1 ";
		      if(!"".equals(AREA))
		         query += " and (CITY like '%"+AREA+"%' or COUNTY like '%"+AREA+"%' or TOWN like '%"+AREA+"%')";
		      if(!"".equals(TEL_NO))
		         query+= " and TEL_NO like '%"+TEL_NO+"%'";
		      if(!"".equals(POST_NO))
		         query+= " and POST_NO like '%"+POST_NO+"%'";
		      
		      int count = T9QuickQuery.getCountByCursor(conn, query);
		      
		      if(count == 0)
		      {
		    	 T9MobileUtility.output(response, "NOFINDDATA");
		         return null;
		      }
		       
		      List<Map<String,String>> list2 = T9QuickQuery.quickQueryList(conn,query );
		     
		      for(Map<String,String> data:list2)
		      {
		         String NO = data.get("NO");
		         String PROVINCE=data.get("PROVINCE");
		         String CITY=data.get("CITY");
		         String COUNTY =data.get("COUNTY");
		         String TOWN=data.get("TOWN");
		         TEL_NO=data.get("TEL_NO");
		         POST_NO=data.get("POST_NO");
		         
		         jsonData +="{province:\""+PROVINCE+"\",";
		         jsonData +="city:\""+CITY+"\",";
		         jsonData +="county:\""+COUNTY+"\",";
		         jsonData +="town:\""+TOWN+"\",";
		         jsonData +="tel_no:\""+TEL_NO+"\",";
		         jsonData +="post_no:\""+POST_NO+"\"},";
		         
		      }
		      
		      jsonData = jsonData.substring(0,jsonData.length()-1);
		      T9MobileUtility.output(response, "["+jsonData+"]");
		      return null;
		   }else if("getDetail".equals(ATYPE)){
		   	String ADD_ID = request.getParameter("Q_ID");
			   query = "SELECT * from ADDRESS where ADD_ID = '"+ADD_ID+"'";
				Map<String,String> d = T9QuickQuery.quickQuery(conn, query);
				if(d!=null)
				{
				  String GROUP_ID=d.get("SEQ_ID");
				  String PSN_NAME=d.get("PSN_NAME");
				  String SEX=d.get("SEX");
				  String BIRTHDAY=d.get("BIRTHDAY");
			
				  String NICK_NAME=d.get("NICK_NAME");
				  String MINISTRATION=d.get("MINISTRATION");
				  String MATE=d.get("MATE");
				  String CHILD=d.get("CHILD");
			
				  String DEPT_NAME=d.get("DEPT_NAME");
				  String ADD_DEPT=d.get("ADD_DEPT");
				  String POST_NO_DEPT=d.get("POST_NO_DEPT");
				  String TEL_NO_DEPT=d.get("TEL_NO_DEPT");
				  String FAX_NO_DEPT=d.get("FAX_NO_DEPT");
			
				  String ADD_HOME=d.get("ADD_HOME");
				  String POST_NO_HOME=d.get("POST_NO_HOME");
				  String TEL_NO_HOME=d.get("TEL_NO_HOME");
				  String MOBIL_NO=d.get("MOBIL_NO");
				  String BP_NO=d.get("BP_NO");
				  String EMAIL=d.get("EMAIL");
				  String OICQ_NO=d.get("OICQ_NO");
				  String ICQ_NO=d.get("ICQ_NO");
				  String PSN_NO=d.get("PSN_NO");
				  String NOTES=d.get("NOTES");
			    
				
				   String query1 = "select GROUP_NAME from ADDRESS_GROUP where SEQ_ID='"+GROUP_ID+"'";
				   Map<String,String> data = T9QuickQuery.quickQuery(conn, query1);
				   String GROUP_NAME = "";
				   if(data!=null)
				      GROUP_NAME=data.get("GROUP_NAME");
				   if("0".equals(GROUP_ID))
				      GROUP_NAME="默认";
				      
			      GROUP_NAME = "["+GROUP_NAME+"]";
				
			      switch(Integer.parseInt(SEX))
			      {
			         case 0:SEX="男";break;
			         case 1:SEX="女";break;
			      }
			      
			      jsonData +="{group_name:\""+GROUP_NAME+"\",";
		         jsonData +="psn_name:\""+PSN_NAME+"\",";
		         jsonData +="sex:\""+SEX+"\",";
		         jsonData +="dept_long_name:\""+""+"\",";
		         jsonData +="dept_name:\""+DEPT_NAME+"\",";
		         jsonData +="ministration:\""+MINISTRATION+"\",";
		         jsonData +="birthday:\""+BIRTHDAY+"\",";
		         jsonData +="tel_no_dept:\""+TEL_NO_DEPT+"\",";
		         jsonData +="fax_no_dept:\""+FAX_NO_DEPT+"\",";
		         jsonData +="tel_no_home:\""+TEL_NO_HOME+"\",";
		         jsonData +="mobil_no:\""+MOBIL_NO+"\",";
		         jsonData +="nick_name:\""+NICK_NAME+"\",";
		         jsonData +="oicq_no:\""+OICQ_NO+"\",";
		         jsonData +="icq_no:\""+ICQ_NO+"\",";
		         jsonData +="email:\""+EMAIL+"\",";
		         jsonData +="mate:\""+MATE+"\",";
		         jsonData +="child:\""+CHILD+"\",";
		         jsonData +="dept_name:\""+DEPT_NAME+"\",";
		         jsonData +="add_dept:\""+ADD_DEPT+"\",";
		         jsonData +="post_no_dept:\""+POST_NO_DEPT+"\",";
		         jsonData +="add_home:\""+ADD_HOME+"\",";
		         jsonData +="post_no_home:\""+POST_NO_HOME+"\",";
		         jsonData +="tel_no_home:\""+TEL_NO_HOME+"\",";
		         jsonData +="notes:\""+NOTES+"\"}";
			   }
				
			  T9MobileUtility.output(response, jsonData);
		      return null;
		  	}
    }  catch (Exception ex) {
      throw ex;
   }
   return null;
	}
	
	private boolean find_id(String s,String t){
		String sp[] = s.split(",");
		for(String id:sp){
			if(id.equals(t)){
				return true;
			}
		}
		return false;
	}
}
