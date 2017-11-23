package t9.cms.service.logic;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.rpc.ParameterMode;

import org.apache.axis.MessageContext;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;
import org.apache.axis.transport.http.HTTPConstants;

import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.person.data.T9UserOnline;
import t9.core.funcs.system.act.T9SystemAct;
import t9.core.funcs.system.logic.T9SystemLogic;
import t9.core.funcs.system.syslog.logic.T9SysLogLogic;
import t9.core.global.T9LogConst;
import t9.core.util.T9Utility;
import t9.user.api.core.db.T9DbconnWrap;

public class T9CMSLoginService{
	  /**
	   * 单点登陆服务
	   * @param userName
	   * @param passWord
	   * @return
	   * @throws Exception
	   */
   	  public String  doLogin(String userName,String passWord) throws Exception{
		Connection dbConn = null;
	    T9DbconnWrap dbUtil = new T9DbconnWrap();
		dbConn = dbUtil.getSysDbConn();
		T9SystemLogic logic = new T9SystemLogic();
		T9Person  person = logic.queryPerson(dbConn, userName);
		String name=this.getUserName(dbConn, userName);
		int flag=this.validate(dbConn, userName, passWord);
		MessageContext mc = MessageContext.getCurrentContext();//创建request，response 对象
		HttpServletRequest request = (HttpServletRequest)mc.getProperty(HTTPConstants.MC_HTTP_SERVLETREQUEST);
		HttpServletResponse response =(HttpServletResponse)mc.getProperty(HTTPConstants.MC_HTTP_SERVLETRESPONSE);

		//验证用户是否存在
		if(T9Utility.isNullorEmpty(name)) 
		{
			//response.sendRedirect("http://www.baidu.com");
			return "-1";
		}
		//验证用户名密码是否正确
		if(flag==1){
     		this.loginSuccess(dbConn, person, request, response);//设置session
    	   // response.sendRedirect("/t9/cms/station/manage.jsp");
     		return userName;
		}else{
			//response.sendRedirect("http://www.baidu.com");
			return "1";
		}
	}
   	  /**
   	   * 查询栏目接口
   	   * 
   	   * @param conn
   	   * @return
   	   * @throws Exception
   	   */
   	  public String queryColumn(String  conn) throws Exception{
  		Connection dbConn = null;
	    T9DbconnWrap dbUtil = new T9DbconnWrap();
		dbConn = dbUtil.getSysDbConn();
		PreparedStatement ps=null;
		ResultSet rs=null;
		PreparedStatement ps1=null;
		ResultSet rs1=null;
		String data="";
		//String sql="select c.seq_id,c.column_name,c.station_id,c.parent_id,s.station_name from cms_column c join cms_station s on c.station_id=s.seq_id";
		String sql="select seq_id,station_name from cms_station";
		StringBuffer sb=new StringBuffer("[");
		try{
			ps=dbConn.prepareStatement(sql);
			rs=ps.executeQuery();
			while(rs.next()){
				sb.append("{\"stationId\":\""+rs.getString(1)+"\",");
				sb.append("\"stationName\":\""+rs.getString(2)+"\",");
				sb.append("\"column\":[");
				String sqlStr="select seq_id,column_name from cms_column where station_id="+rs.getString(1);
				ps1=dbConn.prepareStatement(sqlStr);
				rs1=ps1.executeQuery();
				while(rs1.next()){
					sb.append("{\"columnId\":\""+rs1.getString(1)+"\",");
					sb.append("\"columnName\":\""+rs1.getString(2)+"\"},");
				}
			   if(sb.length()>3){
				   sb.deleteCharAt(sb.length()-1);
			   }
			   sb.append("]},");
			}
			if(sb.length()>3){
				sb.deleteCharAt(sb.length()-1);
			}
			sb.append("]");
			data=sb.toString();
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return data;//页面获取值方式：rtJson.rtData[0].column[0].columnId
   	  }
	/**
	 * 验证用户是否存在
	 * @param conn
	 * @param userName
	 * @return
	 */
	public String getUserName(Connection conn,String userName){
	    PreparedStatement ps = null;
	    ResultSet rs = null;
	    String name="";
		String sql="select user_id from person where user_id='"+userName+"'";
		try
		{
			ps=conn.prepareStatement(sql);
			rs=ps.executeQuery();
			if(rs.next()){
				name=rs.getString(1);
			}
			else
				name="";
		}catch(Exception e	){
			e.printStackTrace();
		}
		return name;
	}
	
	
	/**
	 * 验证用户名密码是否正确
	 * @param conn
	 * @param userName
	 * @param passWord
	 * @return
	 */
	public int validate(Connection conn,String userName,String passWord){
	    PreparedStatement ps = null;
	    ResultSet rs = null;
	    int flag=0;
	    String sql="select * from person where user_id='"+userName+"' and password='"+passWord+"'";
	    try{
	    	ps=conn.prepareStatement(sql);
	    	rs=ps.executeQuery();
	    	if(rs.next()){
	    		flag=1;
	    	}else
	    		flag=0;
	    }catch(Exception e){
	    	e.printStackTrace();
	    }
	    return flag;
	}
	
	//登陆成功
	 private void loginSuccess(Connection conn, T9Person person, HttpServletRequest request, HttpServletResponse response) throws Exception{
		    
		    //获取用户当前的session,如果不存在就生成一个新的session
		    T9SystemAct loginSuccess=new T9SystemAct();
		    HttpSession session = request.getSession(true);
		    T9SystemLogic logic = new T9SystemLogic();
		    logic.updateLastVisitInfo(conn, person.getSeqId(), request.getRemoteAddr());
		    //记录登陆的时间

		    person.setLastVisitTime(new Date());
		    
		    //判断用户是否已经登录
		    if (session.getAttribute("LOGIN_USER") == null){
		      //添加登陆成功的系统日志
		      T9SysLogLogic.addSysLog(conn, T9LogConst.LOGIN, "登录成功", person.getSeqId(), request.getRemoteAddr());
		      
		      loginSuccess.setUserInfoInSession(person, session, request.getRemoteAddr(), request);
		      this.addOnline(conn, person, String.valueOf(session.getAttribute("sessionToken")));
		    }else {
		      T9Person loginPerson = (T9Person)session.getAttribute("LOGIN_USER");
		      //如果是新用户登录时,销毁原有的session
		      if (loginPerson.getSeqId() != person.getSeqId()){
		        //销毁session
		        session.invalidate();
		        //重新调用登录成功的处理

		        loginSuccess(conn, person, request, response);
		      }
		    }
	 }
	 
	 private void addOnline(Connection conn, T9Person person, String sessionToken) throws Exception{
		    T9UserOnline online = new T9UserOnline();
		    
		    online.setSessionToken(sessionToken);
		    online.setLoginTime(new Date());
		    online.setUserId(person.getSeqId());
		    T9SystemLogic logic = new T9SystemLogic();
		    Map<String,String> map = logic.getSysPara(conn);
		    int state = logic.queryUserOnline(conn, person.getSeqId());
		    if (state > 0){
		      online.setUserState(String.valueOf(state));
		      person.setOnStatus(String.valueOf(state));
		    }
		    else if ("0".equals(map.get("SEC_ON_STATUS"))){
		      online.setUserState("1");
		      person.setOnStatus("1");
		    }
		    else{
		      if (person.getOnStatus() == null) {
		        person.setOnStatus("1");
		      }
		      online.setUserState(person.getOnStatus());
		    }
		    
		    logic.addOnline(conn, online);
		  }
	 
	 public static void main(String args[]) throws Exception {
		//测试栏目查询
       try {
		      String serviceUrl = "http://localhost:90/t9/services/T9CMSLoginService?wsdl";
		      Service service = new Service(); 
		      Call call = (Call) service.createCall(); 
		      call.setTargetEndpointAddress(new java.net.URL(serviceUrl)); 
		      call.setOperationName("queryColumn");
		      call.addParameter("conn", XMLType.XSD_STRING, ParameterMode.IN); 
		    //  call.addParameter("passWord", XMLType.XSD_STRING, ParameterMode.IN); 
		      call.setReturnType(XMLType.XSD_STRING); 
		      String ret=(String)call.invoke(new Object[] {"admin"});
  	          //System.out.println(ret);
		    } catch (Exception e) {
		      System.out.println("config - 调用web服务异常,异常信息:" + e.getMessage());
		    }

		 //测试单点登陆
		/* try {
		      String serviceUrl = "http://localhost:80/t9/services/T9CMSLoginService?wsdl";
		      Service service = new Service(); 
		      Call call = (Call) service.createCall(); 
		      call.setTargetEndpointAddress(new java.net.URL(serviceUrl)); 
		      call.setOperationName("doLogin");
		      call.addParameter("userName", XMLType.XSD_STRING, ParameterMode.IN); 
		      call.addParameter("passWord", XMLType.XSD_STRING, ParameterMode.IN); 
		      call.setReturnType(XMLType.XSD_STRING); 
		      String ret=(String) call.invoke(new Object[] {"admin","$1$ds7pLIHi$PWwIcw1EW1WJQEybAJgk11"});
		      System.out.println(ret);
		     } catch (Exception e) {
		      System.out.println("config - 调用web服务异常,异常信息:" + e.getMessage());
		    }*/
		  }   
	 
}