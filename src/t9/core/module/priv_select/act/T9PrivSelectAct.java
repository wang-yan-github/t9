package t9.core.module.priv_select.act;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.dept.data.T9Department;
import t9.core.funcs.modulepriv.data.T9ModulePriv;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.person.data.T9UserPriv;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9Const;
import t9.core.util.db.T9ORM;

public class T9PrivSelectAct
{
	private static Logger log = Logger.getLogger("t9.core.module.priv_select.act");
	
	public String getPriv(HttpServletRequest request,
			HttpServletResponse response) throws Exception
	{
		response.setContentType("text/html;charset=UTF-8");
		request.setCharacterEncoding("UTF-8");
    String TO_ID = request.getParameter("TO_ID");
    String TO_NAME = request.getParameter("TO_NAME");
    String MODULE_ID = request.getParameter("MODULE_ID");
    String USER_SEQ_ID = request.getParameter("USER_SEQ_ID");
    String USER_PRIV = request.getParameter("USER_PRIV");
		try
		{
			T9RequestDbConn requestDbConn = (T9RequestDbConn)request
				.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			Connection dbConn = requestDbConn.getSysDbConn();
			T9ORM orm = new T9ORM();

      //System.out.println("datadebug>>>>>>>>>>>>>>>" + MODULE_ID + "==========" + USER_SEQ_ID);
      String[] query = {"MODULE_ID=" + MODULE_ID, "USER_SEQ_ID=" + USER_SEQ_ID};
      ArrayList<T9ModulePriv> modulePriv = (ArrayList<T9ModulePriv>)orm.loadListSingle(dbConn, T9ModulePriv.class, query);
     	T9ModulePriv tmp = (T9ModulePriv)modulePriv.get(0);
     	String deptPriv = tmp.getRolePriv();
     	//System.out.println("datadebug>>>>>>>>>>>>>>>" + tmp.getDeptPriv());
     	if(deptPriv.equals("0"))
     	{
     		query[0] = "PRIV_NO>" + USER_PRIV;
     	}
     	else if(deptPriv.equals("1"))
     	{
     		query[0] = "PRIV_NO>=" + USER_PRIV;
     	}
     	else if(deptPriv.equals("2"))
     	{
     		query[0] = "1=1";
     	}
     	else
     	{
     		query[0] = "PRIV_NO IN (" + tmp.getPrivId() + ")";
     	}
     	query[1] = "1=1";
      ArrayList<T9UserPriv> userPrivList = (ArrayList<T9UserPriv>)orm.loadListSingle(dbConn, T9UserPriv.class, query);
			request.setAttribute("USER_PRIV", userPrivList);
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "查询成功");
		}
		catch(Exception ex)
		{
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, "查询失败");
			throw ex;
		}
		return "/core/module/priv_select/index.jsp?TO_ID=" + TO_ID + "&TO_NAME=" + TO_NAME;
	}
	public String getUserPriv(HttpServletRequest request,
			HttpServletResponse response) throws Exception
	{
		response.setContentType("text/html;charset=UTF-8");
		request.setCharacterEncoding("UTF-8");
    String TO_ID = request.getParameter("TO_ID");
    String TO_NAME = request.getParameter("TO_NAME");
		try
		{
			T9RequestDbConn requestDbConn = (T9RequestDbConn)request
				.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			Connection dbConn = requestDbConn.getSysDbConn();
			T9ORM orm = new T9ORM();
			ArrayList<T9UserPriv> userPrivList = (ArrayList<T9UserPriv>)orm
				.loadListSingle(dbConn, T9UserPriv.class, new HashMap());
      StringBuffer data = new StringBuffer("[");
      Iterator item = userPrivList.iterator();
      while(item.hasNext())
      {
      	T9UserPriv userPriv = (T9UserPriv)item.next();
        data.append("{");
        data.append("privName:\"" + userPriv.getPrivName() + "\"");
        //System.out.println(">>>>>>>>>>>>>+++++++++>>>>>>>>>>>" + userPriv.getPrivName());
        data.append(",privNo:\"" + userPriv.getPrivNo() + "\"");
        data.append("},");
      }
      if(data.lastIndexOf(",") == (data.length() - 1))
      {
      	data.deleteCharAt(data.length() - 1);
      }
      data.append("]");
      //System.out.println(data.toString());
			request.setAttribute(T9ActionKeys.RET_DATA, data.toString());
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "查询成功");
		}
		catch(Exception ex)
		{
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, "查询失败");
			throw ex;
		}
		return "/core/inc/rtjson.jsp";
	}
	public String selectPriv(HttpServletRequest request,
			HttpServletResponse response) throws Exception
	{
		response.setContentType("text/html;charset=UTF-8");
		request.setCharacterEncoding("UTF-8");
		String PRIV_ID = request.getParameter("PRIV_ID");
    String TO_ID = request.getParameter("TO_ID");
    String TO_NAME = request.getParameter("TO_NAME");
    String MODULE_ID = request.getParameter("MODULE_ID");
    String USER_SEQ_ID = request.getParameter("USER_SEQ_ID");
    String USER_PRIV = request.getParameter("USER_PRIV");
    String PRIV_NAME = new String(request.getParameter("PRIV_NAME").getBytes("ISO-8859-1"), "UTF-8");
		if(PRIV_ID.equals("4"))
		{
			PRIV_ID = "121";
		}
		else if(PRIV_ID.equals("5"))
		{
			PRIV_ID = "123";
		}
		else if(PRIV_ID.equals("2"))
		{
			PRIV_ID = "72";
		}
		else
		{
			PRIV_ID = "97";
		}
		try
		{
			T9RequestDbConn requestDbConn = (T9RequestDbConn)request
				.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			Connection dbConn = requestDbConn.getSysDbConn();
			T9ORM orm = new T9ORM();

      //System.out.println("new1debug>>>>>>>>>>>>>>>" + MODULE_ID + "==========" + USER_SEQ_ID);
      String[] query = {"MODULE_ID=" + MODULE_ID, "USER_SEQ_ID=" + USER_SEQ_ID};
      ArrayList<T9ModulePriv> modulePriv = (ArrayList<T9ModulePriv>)orm.loadListSingle(dbConn, T9ModulePriv.class, query);
     	T9ModulePriv tmp = (T9ModulePriv)modulePriv.get(0);
     	String rolePriv = "0";//tmp.getRolePriv();
     	//System.out.println("new1debug>>>>>>>>>>>>>>>" + rolePriv);
     	USER_PRIV = "100";
     	if(rolePriv.equals("0"))
     	{
     		query[0] = "USER_PRIV<" + USER_PRIV;
     	}
     	else if(rolePriv.equals("1"))
     	{
     		query[0] = "USER_PRIV<=" + USER_PRIV;
     	}
     	else if(rolePriv.equals("2"))
     	{
     		query[0] = "1=1";
     	}
     	else
     	{
     		query[0] = "USER_PRIV IN (" + tmp.getPrivId() + ")";
     	}
     	query[1] = "USER_PRIV=" + PRIV_ID;

			ArrayList<T9Person> personList = (ArrayList<T9Person>)orm
				.loadListSingle(dbConn, T9Person.class, query);
			request.setAttribute("PERSON_LIST", personList);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询成功");
		}
		catch(Exception ex)
		{
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, "查询失败");
			throw ex;
		}
		return "/core/module/user_select/user.jsp?TO_ID=" + TO_ID + "&TO_NAME=" + TO_NAME + "&LOCAL=" + PRIV_NAME;
	}
}
