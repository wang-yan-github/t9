package t9.pda.userInfo.act;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.global.T9BeanKeys;
import t9.core.util.db.T9DBUtility;
import t9.pda.userInfo.data.T9PdaUserInfo;

public class T9PdaUserInfoAct {

  public void search(HttpServletRequest request, HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try{
      String userName = request.getParameter("userName");
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
      String sql = " SELECT p.USER_NAME , up.PRIV_NAME , p.USER_PRIV , p.DEPT_ID , d.DEPT_NAME ,"
      		       + " p.SEX , p.TEL_NO_DEPT , p.MOBIL_NO , p.EMAIL , p.MOBIL_NO_HIDDEN "
                 + " from person p "
                 + " join USER_PRIV up on p.USER_PRIV = up.SEQ_ID "
                 + " join DEPARTMENT d on p.DEPT_ID = d.SEQ_ID "
                 + " where p.USER_NAME like '%"+userName+"%' and p.DEPT_ID!='0' ";
      
      List<T9PdaUserInfo> list = new ArrayList<T9PdaUserInfo>();
      ps = dbConn.prepareStatement(sql);
      rs = ps.executeQuery();
      while(rs.next()) {
        T9PdaUserInfo userInfo = new T9PdaUserInfo();
        userInfo.setUserName(rs.getString("USER_NAME"));
        userInfo.setPrivName(rs.getString("PRIV_NAME"));
        userInfo.setUserPriv(rs.getString("USER_PRIV"));
        userInfo.setDeptId(rs.getString("DEPT_ID"));
        userInfo.setDeptName(rs.getString("DEPT_NAME"));
        userInfo.setSex(rs.getString("SEX"));
        userInfo.setTelNoDept(rs.getString("TEL_NO_DEPT"));
        userInfo.setMobilNo(rs.getString("MOBIL_NO"));
        userInfo.setEmail(rs.getString("EMAIL"));
        userInfo.setMobilNoHidden(rs.getString("MOBIL_NO_HIDDEN"));
        list.add(userInfo);
      }
      request.setAttribute("userInfos", list);
    }
    catch(Exception ex){
      ex.printStackTrace();
    }
    finally{
      T9DBUtility.close(ps, rs, null);
    }
    request.getRequestDispatcher("/pda/userInfo/search.jsp").forward(request, response);
    return;
  }
}
