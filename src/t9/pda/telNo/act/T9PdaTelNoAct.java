package t9.pda.telNo.act;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.global.T9BeanKeys;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.pda.telNo.data.T9PostTel;

public class T9PdaTelNoAct {

  public void search(HttpServletRequest request, HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try{
      String area = request.getParameter("area");
      String telNo = request.getParameter("telNo");
      String postNo = request.getParameter("postNo");
      
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
      StringBuffer sql = new StringBuffer(" SELECT PROVINCE , CITY , COUNTY , TOWN , TEL_NO , POST_NO from POST_TEL where 1=1 ");
      if(!T9Utility.isNullorEmpty(area)){
        sql.append(" and (CITY like '%"+area+"%' or COUNTY like '%"+area+"%' or TOWN like '%"+area+"%') ");
      }
      if(!T9Utility.isNullorEmpty(telNo)){
        sql.append(" and TEL_NO like '%"+telNo+"%' ");
      }
      if(!T9Utility.isNullorEmpty(postNo)){
        sql.append(" and POST_NO like '%"+postNo+"%' ");
      }
      
      List<T9PostTel> list = new ArrayList<T9PostTel>();
      ps = dbConn.prepareStatement(sql.toString());
      rs = ps.executeQuery();
      while(rs.next()) {
        T9PostTel postTel = new T9PostTel();
        postTel.setProvince(rs.getString("PROVINCE"));
        postTel.setCity(rs.getString("CITY"));
        postTel.setCounty(rs.getString("COUNTY"));
        postTel.setTown(rs.getString("TOWN"));
        postTel.setTelNo(rs.getString("TEL_NO"));
        postTel.setPostNo(rs.getString("POST_NO"));
        list.add(postTel);
      }
      request.setAttribute("postTels", list);
    }
    catch(Exception ex){
      ex.printStackTrace();
    }
    finally{
      T9DBUtility.close(ps, rs, null);
    }
    request.getRequestDispatcher("/pda/telNo/search.jsp").forward(request, response);
    return;
  }
}
