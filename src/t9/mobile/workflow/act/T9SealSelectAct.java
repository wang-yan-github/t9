package t9.mobile.workflow.act;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.auth.T9DigestUtility;
import t9.core.util.db.T9DBUtility;
import t9.mobile.util.T9MobileString;
import t9.mobile.util.T9MobileUtility;
import t9.mobile.workflow.logic.T9PdaTurnLogic;

public class T9SealSelectAct {
	public String data(HttpServletRequest request, 
      HttpServletResponse response) throws Exception {
    Connection conn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      conn = requestDbConn.getSysDbConn();
      
      T9PdaTurnLogic logic = new T9PdaTurnLogic();
      String authData = request.getParameter("authData");
      
      String md5_check = T9DigestUtility.md5Hex(authData.getBytes());
      int rsCount = T9MobileUtility.resultSetCount(conn, "select 1 FROM MOBILE_DEVICE WHERE MD5_CHECK='"+md5_check+"'");
      
      if(rsCount == 1 ){
        /**
         * 这里说明已经找到 且只有一条数据 说明只有一个 设备 这就对了
         */
        String devId = T9MobileUtility.getDateByField(conn, "MOBILE_DEVICE", "SEQ_ID", "MD5_CHECK='"+md5_check+"'");
        if(!T9MobileString.isEmpty(devId)){
          StringBuffer sb = new StringBuffer();
          sb.append("select * from MOBILE_SEAL where ");
          sb.append(T9DBUtility.findInSet(devId, "DEVICE_LIST"));
          request.setAttribute("list",this.getSealListBySql(conn, sb.toString()));
        }
      }
      
      String SEAL_CUR_ITEM = request.getParameter("SEAL_CUR_ITEM");
      String SEAL_ITEM_CHECK = request.getParameter("SEAL_ITEM_CHECK");
      
      request.setAttribute("SEAL_CUR_ITEM", SEAL_CUR_ITEM);
      request.setAttribute("SEAL_ITEM_CHECK", SEAL_ITEM_CHECK);
    }  catch (Exception ex) {
       throw ex;
    }
    String sid = request.getSession().getId();
    return "/mobile/workflow/sealselect.jsp?sessionid=" + sid;
  }
	public List getSealListBySql(Connection dbConn,String sql) throws Exception{
    try {
      PreparedStatement ps = null;
      ResultSet rs = null;
      List list = new ArrayList();
      try {
        ps = dbConn.prepareStatement(sql);
        rs = ps.executeQuery();
        while (rs.next()) {
          Map ms = new HashMap();
          ms.put("id", rs.getInt("SEQ_ID"));
          ms.put("name",rs.getString("SEAL_NAME"));
          list.add(ms);
        }
      } catch (Exception e) {
        throw e;
      } finally {
        T9DBUtility.close(ps, rs, null);
      }
      return list;
    } catch (Exception e) {
      throw e;
    }
 }
}
