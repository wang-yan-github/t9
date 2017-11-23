package t9.mobile.user_info;

import java.sql.Connection;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.mobile.util.T9MobileConfig;
import t9.mobile.util.T9MobileUtility;
import t9.mobile.util.T9QuickQuery;

public class T9PdaUserinfoAct {
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
		         query = "SELECT PERSON.SEQ_ID,SEX,AUATAR,USER_ID,USER_NAME,PERSON.DEPT_ID,PRIV_NAME from PERSON,USER_PRIV,DEPARTMENT where DEPARTMENT.SEQ_ID!=0 and PERSON.USER_PRIV=USER_PRIV.SEQ_ID and PERSON.DEPT_ID=DEPARTMENT.SEQ_ID order by PRIV_NO,USER_NO,USER_NAME  ";     
		      }else if("getNew".equals(A))
		      {
		    	 T9MobileUtility.output(response, "NONEWDATA");
		         return null;
		      }else
		      {
		         
		         query = "SELECT PERSON.SEQ_ID,SEX,AUATAR,USER_ID,USER_NAME,PERSON.DEPT_ID,PRIV_NAME from PERSON,USER_PRIV,DEPARTMENT where DEPARTMENT.SEQ_ID!=0 and PERSON.USER_PRIV=USER_PRIV.SEQ_ID and PERSON.DEPT_ID=DEPARTMENT.SEQ_ID order by PRIV_NO,USER_NO,USER_NAME  ";
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
		         String UID=data.get("SEQ_ID");
		         String USER_NAME=data.get("USER_NAME");
		         String PRIV_NAME=data.get("PRIV_NAME");
		         String deptId = data.get("DEPT_ID");
		         String SEX=data.get("SEX");
		         String DEPT_LONG_NAME=T9MobileUtility.getLongDept(conn, Integer.parseInt(deptId));
				
				   if("0".equals(SEX))
				      SEX="男";
				   else
				      SEX="女";
		         
				  jsonData +="{\"q_id\":\""+UID+"\",";
				  jsonData +="\"user_name\":\""+T9Utility.encodeSpecial(USER_NAME)+"\",";
				  jsonData +="\"priv_name\":\""+T9Utility.encodeSpecial(PRIV_NAME)+"\",";
				  jsonData +="\"dept_long_name\":\""+T9Utility.encodeSpecial(DEPT_LONG_NAME)+"\",";
				  jsonData +="\"sex\":\""+T9Utility.encodeSpecial(SEX)+"\"},";
		      }
		      
		      jsonData = jsonData.substring(0,jsonData.length()-1);
		      T9MobileUtility.output(response, "["+jsonData+"]");
		      return null;
		   }else if("getUserInfo".equals(ATYPE))
		   {
		      String UID = request.getParameter("Q_ID");
		      
		      query = "SELECT * from PERSON where SEQ_ID = '"+UID+"'";

		      Map<String,String> data = T9QuickQuery.quickQuery(conn, query);
		      if(data!=null)
		      {
		         String USER_NAME=data.get("USER_NAME");
		         String USER_PRIV=data.get("USER_PRIV");
		         String DEPT_ID=data.get("DEPT_ID");
		         String SEX=data.get("SEX");
		         String TEL_NO_DEPT=data.get("TEL_NO_DEPT");
		         String MOBIL_NO=T9Utility.null2Empty(data.get("MOBIL_NO"));
		         String EMAIL=T9Utility.null2Empty(data.get("EMAIL"));
		         String MOBIL_NO_HIDDEN=data.get("MOBIL_NO_HIDDEN");
		         String OICQ_NO=T9Utility.null2Empty(data.get("OICQ_NO"));
		         String REMARK=T9Utility.null2Empty(data.get("REMARK"));
		         String AVATAR = data.get("AUATAR");
		         String DEPT_LONG_NAME=T9MobileUtility.getLongDept(conn, Integer.parseInt(DEPT_ID));
		         
		         String query1 = "SELECT * from USER_PRIV where SEQ_ID='"+USER_PRIV+"'";
				 
		         Map<String,String> data1 = T9QuickQuery.quickQuery(conn, query1);
		      
				   if(data1!=null)
				        USER_PRIV=data1.get("PRIV_NAME");
				
				  DEPT_LONG_NAME="";
				 
				 String SEX_SHOW = "0".equals(SEX)? "男" : "女";
				 
				 jsonData +="{\"avatar\":\""+T9Utility.encodeSpecial(T9MobileUtility.showAvatar(AVATAR, SEX))+"\",";
				  jsonData +="\"user_name\":\""+T9Utility.encodeSpecial(USER_NAME)+"\",";
				  jsonData +="\"sex\":\""+T9Utility.encodeSpecial(SEX_SHOW)+"\",";
				  jsonData +="\"dept_long_name\":\""+T9Utility.encodeSpecial(DEPT_LONG_NAME)+"\",";
				  jsonData +="\"user_priv\":\""+T9Utility.encodeSpecial(USER_PRIV)+"\",";
				  jsonData +="\"oicq_no\":\""+T9Utility.encodeSpecial(OICQ_NO)+"\",";
				  jsonData +="\"tel_no_dept\":\""+T9Utility.encodeSpecial(TEL_NO_DEPT)+"\",";
				  jsonData +="\"mobil_no\":\""+("1".equals(MOBIL_NO_HIDDEN)?"不公开":MOBIL_NO)+"\",";
				  jsonData +="\"email\":\""+T9Utility.encodeSpecial(EMAIL)+"\",";
				  jsonData +="\"remark\":\""+T9Utility.encodeSpecial(REMARK)+"\"}";
		      }
		      T9MobileUtility.output(response, jsonData);
		   }
    }  catch (Exception ex) {
      throw ex;
   }
   return null;
	}
}
