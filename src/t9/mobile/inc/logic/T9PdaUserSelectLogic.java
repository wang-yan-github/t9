package t9.mobile.inc.logic;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.doc.util.T9WorkFlowUtility;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.system.act.T9SystemAct;
import t9.core.funcs.system.act.adapter.T9LoginAdapter;
import t9.core.funcs.system.act.filters.T9PasswordValidator;
import t9.core.funcs.system.interfaces.data.T9SysPara;
import t9.core.funcs.system.logic.T9SystemLogic;
import t9.core.funcs.workflow.util.T9FlowRunUtility;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.servlet.T9SessionListener;
import t9.core.util.T9Out;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.mobile.util.T9MobileConfig;
import t9.mobile.util.T9MobileString;
import t9.mobile.util.T9MobileUtility;

public class T9PdaUserSelectLogic {
  public void getDept(Connection dbConn, String parentId , int level , StringBuffer sb) throws Exception {
    // TODO Auto-generated method stub
    String query = "SELECT seq_id, dept_name FROM department WHERE dept_parent = '"+parentId+"' ORDER BY dept_no ";
    Statement stmt = null;
    ResultSet rs = null;
    level = level + 1;
    List<Map> list = new ArrayList();
    try { 
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(query);
      while(rs.next()){
        int deptId = rs.getInt("seq_id");
        String deptName = rs.getString("dept_name");
        
        Map map = new HashMap();
        map.put("dept_id", deptId+"");
        map.put("dept_name", deptName);
        map.put("rating", level+"");
        list.add(map);
      }
    }catch (Exception ex) {
        throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, null);
    }
    for (Map m : list) {
      sb.append(T9MobileUtility.mapToJson(m)).append(",");
      this.getDept(dbConn, (String)m.get("dept_id"), level, sb);
    }
  }
  public String getUser(Connection dbConn, T9Person person, String q_ID) throws Exception {
    // TODO Auto-generated method stub
    //$UsersInfo = array();
    if (q_ID.endsWith(",")) {
      q_ID = q_ID.substring(0, q_ID.length() - 1);
    }
    
    if(T9MobileString.isEmpty(q_ID)){
    	q_ID = "0";
    }
    int count = 0 ;
    StringBuffer sb = new StringBuffer("[");
    
    String query = "SELECT PERSON.SEQ_ID, PERSON.user_id, PERSON.user_name, USER_PRIV.priv_name ,department.dept_name "
       + " FROM PERSON "
       + " INNER JOIN department ON department.SEQ_ID = PERSON.dept_id"
       + " INNER JOIN USER_PRIV ON PERSON.USER_PRIV = USER_PRIV.SEQ_ID"
       + " WHERE PERSON.dept_id in ("+q_ID+") ORDER BY USER_PRIV.priv_no,PERSON.user_no desc,PERSON.user_name";
    Statement stmt = null;
    ResultSet rs = null;
    List<Map> list = new ArrayList();
    try { 
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(query);
      while(rs.next()){
        String user_uid = rs.getString("SEQ_ID");
        String user_id = rs.getString("user_id");
        String priv_name = rs.getString("priv_name");
        String dept_name = rs.getString("dept_name");
        String user_name = rs.getString("user_name");
        
        Map map = new HashMap();
        map.put("user_uid", user_uid);
        map.put("user_id", user_id);
        map.put("priv_name", T9Utility.encodeSpecial(priv_name));
        map.put("dept_name", T9Utility.encodeSpecial(dept_name));
        map.put("user_name", T9Utility.encodeSpecial(user_name));
        sb.append(T9MobileUtility.mapToJson(map)).append(",");
        count++;
      }
    }catch (Exception ex) {
        throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, null);
    }
    if (count >0) {
      sb.deleteCharAt(sb.length() - 1);
    } else {
      return "";
    }
    sb.append("]");
    return sb.toString();
  }
  public String select(Connection dbConn, String kWORD, String dATA_TYPE) throws Exception {
    // TODO Auto-generated method stub
    StringBuffer sb = new StringBuffer("[");
    String query = "SELECT PERSON.SEQ_ID,USER_ID,AUATAR,USER_NAME,PERSON.DEPT_ID,PRIV_NAME from PERSON,USER_PRIV,DEPARTMENT where PERSON.DEPT_ID!=0 and (PERSON.USER_ID like '%"+kWORD+"%' or PERSON.BYNAME like '%"+kWORD+"%' or PERSON.USER_NAME like '%"+kWORD+"%') and PERSON.DEPT_ID=DEPARTMENT.SEQ_ID and PERSON.USER_PRIV=USER_PRIV.SEQ_ID order by PRIV_NO,USER_NO DESC,USER_NAME ";
    Statement stmt = null;
    ResultSet rs = null;
    List<Map> list = new ArrayList();
    int count = 0 ;
    String html = "";
    try { 
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(query);
      while(rs.next()){
        String user_uid = rs.getString("SEQ_ID");
        String USER_ID = rs.getString("USER_ID");
        
        String priv_name = rs.getString("PRIV_NAME");
        int deptId = rs.getInt("DEPT_ID");
        String dept_name = T9MobileUtility.getLongDept(dbConn, deptId);
        String user_name = rs.getString("USER_NAME");
        String AVATAR = rs.getString("AUATAR");
        
        
        if ("html".equals(dATA_TYPE)) {
          html += "<li class=\"\" q_id=\""+user_uid+"\" q_name=\""+user_name+"\" q_user_id=\""+USER_ID+"\">"
              + " <h3>"+user_name+"（"+priv_name+"）</h3>"
              + " <p class=\"grapc\">部门：("+dept_name+")&nbsp;</p>"
              + "<span class=\"ui-icon-rarrow\"></span>"
              +"</li> ";
        } else {
          Map map = new HashMap();
          map.put("user_uid", user_uid);
          map.put("user_id", USER_ID);
          map.put("priv_name", T9Utility.encodeSpecial(priv_name));
          map.put("dept_name", T9Utility.encodeSpecial(dept_name));
          map.put("user_name", T9Utility.encodeSpecial(user_name));
          sb.append(T9MobileUtility.mapToJson(map)).append(",");
        }
        count++;
      }
    }catch (Exception ex) {
        throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, null);
    }
    if ("html".equals(dATA_TYPE)) {
      return html;
    }
    if (count >0) {
      sb.deleteCharAt(sb.length() - 1);
    } else {
      return "";
    }
    sb.append("]");
    return sb.toString();
  }
}
