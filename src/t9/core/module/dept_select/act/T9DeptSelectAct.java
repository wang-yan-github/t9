package t9.core.module.dept_select.act;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.dept.data.T9Department;
import t9.core.funcs.modulepriv.data.T9ModulePriv;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;


public class T9DeptSelectAct
{
  private static Logger log = Logger.getLogger("t9.core.module.dept_select.act");
/*
  public String getTree(HttpServletRequest request, 
  	HttpServletResponse response)throws Exception
  {
    String idStr = request.getParameter("id");
    int id = 0;
    if(idStr != null && !"".equals(idStr))
    {
      id = Integer.parseInt(idStr);
    }
    response.setCharacterEncoding("UTF-8");
    response.setContentType("text/xml");
    response.setHeader("Cache-Control", "no-cache");
    PrintWriter out = response.getWriter();
    out.print("<?xml version=\'1.0\' encoding=\'utf-8'?>");
    out.print("<menus>");
    Connection dbConn = null;
    try
    {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request
      	.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9ORM orm = new T9ORM();
      Map map = new HashMap();
      map.put("deptId", id);
      List<T9Person> list = orm.loadListSingle(dbConn, T9Person.class, map);
      for(T9Person d : list)
      {
        out.print("<menu>");
        out.print("<id>" + d.getSeqId() + "</id>");
        out.print("<name>" + d.getUserId() + "</name>");
        out.print("<parentId>" + d.getDeptId() + "</parentId>");
        out.print("<isHaveChild>0</isHaveChild>");
        out.print("</menu>");
      }
      map.remove("deptId");
      map.put("deptParent", id);
      List<T9Department> deptList = orm.loadListSingle(dbConn, T9Department.class, map);
      for(T9Department t : deptList)
      {
        out.print("<menu>");
        out.print("<id>" + t.getSeqId() + "</id>");
        out.print("<name>" + t.getDeptName() + "</name>");
        out.print("<parentId>" + t.getDeptParent() + "</parentId>");
        out.print("<isHaveChild>" + IsHaveChild(request, response, String.valueOf(t.getSeqId())) + "</isHaveChild>");
        out.print("</menu>");      
      }    
      out.print("<parentNodeId>" + id + "</parentNodeId>");
      out.print("<count>" + (list.size()+deptList.size()) + "</count>");
      out.print("</menus>");
      out.flush();
      out.close();
      //dbConn.close();
    }
    catch(Exception ex)
    {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;
  }
*/
  public int IsHaveChild(HttpServletRequest request,
      HttpServletResponse response, int id)throws Exception
  {
    Connection dbConn = null;
    try
    {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9ORM orm = new T9ORM();
      Map map = new HashMap();
      map.put("DEPT_PARENT", id);
      List<T9Department> list = orm.loadListSingle(dbConn, T9Department.class, map);
      if(list.size() > 0)
      {
        return 1;
      }
      else
      {
        return 0;
      }
    }
    catch (Exception ex)
    {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
  }
  
  public String getTree(HttpServletRequest request, 
  		HttpServletResponse response) throws Exception
  {
		String idStr = request.getParameter("DEPT_PAR_ID");
		int id = 0;
		if (idStr != null && !"".equals(idStr))
		{
		  id = Integer.parseInt(idStr);
		}
		Connection dbConn = null;
		try
		{
		  T9RequestDbConn requestDbConn = (T9RequestDbConn) request
		      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
		  dbConn = requestDbConn.getSysDbConn();
		  T9ORM orm = new T9ORM();
		  Map map = new HashMap();
		  map.put("DEPT_PARENT", id);
		  List<T9Department> list = orm.loadListSingle(dbConn, T9Department.class, map);
		  StringBuffer buf = new StringBuffer("[");
		  for (T9Department d : list)
		  {
		    int nodeId = d.getSeqId();
		    String name = d.getDeptName();
		    int isHaveChild = IsHaveChild(request, response, d.getSeqId());
		    String extData = "";
		    String imgAddress = "/t9/core/styles/style1/img/dtree/node_dept.gif";
		    buf.append("{");
		    buf.append("nodeId:\"" + nodeId + "\"");
		    buf.append(",name:\"" + name + "\"");
		    buf.append(",isHaveChild:" + isHaveChild + "");
		    buf.append(",extData:\"" + extData + "\"");
		    buf.append(",imgAddress:\"" + imgAddress + "\"");
		    buf.append("},");
		  }
		  buf.deleteCharAt(buf.length() - 1);
		  buf.append("]");
		  request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
		  request.setAttribute(T9ActionKeys.RET_MSRG, "成功返回结果！");
		  request.setAttribute(T9ActionKeys.RET_DATA, buf.toString());
		}
		catch (Exception ex)
		{
		  request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
		  request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
		  throw ex;
		}
		return "/core/inc/rtjson.jsp";
}

  public String selectDept(HttpServletRequest request,
      HttpServletResponse response) throws Exception
  {
    response.setContentType("text/html;charset=UTF-8");
    request.setCharacterEncoding("UTF-8");
    String id = request.getParameter("DEPT_PAR_ID");
    String TO_ID = request.getParameter("TO_ID");
    String TO_NAME = request.getParameter("TO_NAME");
    String MODULE_ID = request.getParameter("MODULE_ID");
    String USER_SEQ_ID = request.getParameter("USER_SEQ_ID");
    String USER_DEPT = request.getParameter("USER_DEPT");
    String deptLocal = new String(request.getParameter("deptLocal").getBytes("ISO-8859-1"), "UTF-8");
    try
    {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      Connection dbConn = requestDbConn.getSysDbConn();
      T9ORM orm = new T9ORM();

      //System.out.println("datadebug>>>>>>>>>>>>>>>" + MODULE_ID + "==========" + USER_SEQ_ID);
      String[] query = {"MODULE_ID=" + MODULE_ID, "USER_SEQ_ID=" + USER_SEQ_ID};
      ArrayList<T9ModulePriv> modulePriv = (ArrayList<T9ModulePriv>)orm.loadListSingle(dbConn, T9ModulePriv.class, query);
     	T9ModulePriv tmp = (T9ModulePriv)modulePriv.get(0);
     	String deptPriv = "0";//tmp.getDeptPriv();
     	//System.out.println("datadebug>>>>>>>>>>>>>>>" + tmp.getDeptPriv());
     	query[0] = "DEPT_ID=" + id;
     	if(deptPriv == null || deptPriv.equals("0"))
     	{
     		if(USER_DEPT.equals(id))
     		{
     			query[1] = "1=1";
     		}
     		else
     		{
       		query[1] = "1=0";
     		}
     	}
     	else if(deptPriv.equals("1"))
     	{
     		query[1] = "1=1";
     	}
     	else if(deptPriv.equals("2"))
     	{
     		query[1] = "DEPT_ID IN (" + tmp.getDeptId() + ")";
     	}
     	else
     	{
     		query[1] = "1=1";
     	}      
      ArrayList<T9Person> personList = (ArrayList<T9Person>)orm.loadListSingle(dbConn, T9Person.class, query);
      request.setAttribute("PERSON_LIST", personList);
    }
    catch(Exception ex)
    {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "submit failed");
      throw ex;
    }
    return "/core/module/user_select/user.jsp?TO_ID=" + TO_ID + "&TO_NAME=" + TO_NAME + "&LOCAL=" + deptLocal;
  }
  public String getDept(HttpServletRequest request, 
  		HttpServletResponse response) throws Exception
  {
		String idStr = request.getParameter("DEPT_PAR_ID");
		int id = 0;
		if (idStr != null && !"".equals(idStr))
		{
		  id = Integer.parseInt(idStr);
		}
    String TO_ID = request.getParameter("TO_ID");
    String TO_NAME = request.getParameter("TO_NAME");
    String MODULE_ID = request.getParameter("MODULE_ID");
    String USER_SEQ_ID = request.getParameter("USER_SEQ_ID");
    String USER_DEPT = request.getParameter("USER_DEPT");
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
     	String deptPriv = tmp.getDeptPriv();
     	//System.out.println("datadebug>>>>>>>>>>>>>>>" + tmp.getDeptPriv());
     	query[0] = "DEPT_PARENT=" + id;
     	if(deptPriv == null || deptPriv.equals("0"))
     	{
     		query[1] = "SEQ_ID=" + USER_DEPT;
     	}
     	else if(deptPriv.equals("1"))
     	{
     		query[1] = "1=1";
     	}
     	else if(deptPriv.equals("2"))
     	{
     		query[1] = "SEQ_ID IN (" + tmp.getDeptId() + ")";
     	}
     	else
     	{
     		query[1] = "1=1";
     	}
      ArrayList<T9Department> deptList = (ArrayList<T9Department>)orm.loadListSingle(dbConn, T9Department.class, query);
      request.setAttribute("DEPT_LIST", deptList);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功取出数据");
    }
    catch(Exception ex)
    {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/module/dept_select/dept_list.jsp?TO_ID=" + TO_ID + "&TO_NAME=" + TO_NAME;
  }

}
