package test.cy.code.act;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import t9.core.data.T9RequestDbConn;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.db.T9StringFormat;
import t9.core.util.form.T9FOM;
import t9.core.data.T9PageDataListNew;
import t9.core.data.T9PageQueryParamNew;
import t9.core.load.T9PageLoaderNew;
import test.cy.code.T9City1;

public class T9City1Act {
  public String show(HttpServletRequest request, HttpServletResponse response) throws Exception {
    request.setCharacterEncoding("utf-8");
    Connection dbConn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    String seqId = request.getParameter("seqId");
    String data = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      Object obj = null;
      T9ORM orm = new T9ORM();
      obj = orm.loadObjSingle(dbConn, T9City1.class, Integer.parseInt(seqId));
      if(obj == null) {
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, "不存在此条数据！");
        return "/core/inc/rtjson.jsp";
      }
      StringBuffer sb = T9FOM.toJson(obj);
      String sql = " select '' "
      					 + " from CITY1 d "
								 + " order by d.SEQ_ID DESC ";
      ps = dbConn.prepareStatement(sql);
      rs = ps.executeQuery();
      if (rs.next()) {
        sb.deleteCharAt(sb.length() - 1);
                
        sb.append("}");
      }
      data = sb.toString();
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    } finally {
      T9DBUtility.close(ps, rs, null);
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String deleteField(HttpServletRequest request, HttpServletResponse response) throws Exception {
    String seqIdStr = request.getParameter("seqId");   
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);   
      dbConn = requestDbConn.getSysDbConn();
      T9ORM orm = new T9ORM();
      String seqIdArr[] = seqIdStr.split(",");
      for(String seqId : seqIdArr){
	      Map<String, String> map = new HashMap<String, String>();
	      map.put("seqId", seqId);
	      orm.deleteSingle(dbConn, "city1", map);
	    }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"删除成功");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String addField(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);     
      dbConn = requestDbConn.getSysDbConn();
      Object obj = T9FOM.build(request.getParameterMap());
      T9ORM orm = new T9ORM();
      orm.saveSingle(dbConn, obj);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"添加数据成功");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String updateField(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);   
      dbConn = requestDbConn.getSysDbConn();
      Object obj = T9FOM.build(request.getParameterMap());
      T9ORM orm = new T9ORM();
      orm.updateSingle(dbConn, obj);        
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"编辑数据成功");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";    
  }
  
  public String getList(HttpServletRequest request, HttpServletResponse response) throws Exception {
  	Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String sql = " select '' , d.seq_id, d.city_no, d.city_name "
      					 + " from CITY1 d "
								 + " order by d.SEQ_ID DESC ";
      T9PageQueryParamNew queryParam = (T9PageQueryParamNew) T9FOM.build(request.getParameterMap());
      T9PageDataListNew pageDataList = T9PageLoaderNew.loadPageList(dbConn, queryParam, sql);
      String d = pageDataList.toJson();
      PrintWriter pw = response.getWriter();
      pw.println(d);
      pw.flush();
      pw.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
  
  public String getCodeSelect(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    String fkTableName2 = request.getParameter("fkTableName2");
    String fkFilterName = request.getParameter("fkFilterName");
    String codeClass = request.getParameter("codeClass");
    String fkNameFieldName2 = request.getParameter("fkNameFieldName2");
    try {
      StringBuffer sb = new StringBuffer("[");
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String sql = " SELECT h.seq_id, h." + T9StringFormat.format(fkNameFieldName2)
                 + " FROM " + fkTableName2 + " h "
                 + " where h." + T9StringFormat.format(fkFilterName) + " = '" + codeClass + "' ";
      ps = dbConn.prepareStatement(sql);
      rs = ps.executeQuery();
      while (rs.next()) {
        sb.append("{\"seqId\":\""+rs.getString("seq_id")+"\"");
        sb.append(",\"codeName\":\""+rs.getString(T9StringFormat.format(fkNameFieldName2))+"\"},");
      }
      if(sb.length() > 3){
        sb.deleteCharAt(sb.length() - 1);
      }
      sb.append("]");
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    } finally {
      T9DBUtility.close(ps, rs, null);
    }
    return "/core/inc/rtjson.jsp";
  }
}
