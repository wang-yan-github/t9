package t9.core.funcs.system.active.act;

import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;
import org.apache.log4j.Logger;

import t9.core.data.T9DbRecord;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.dept.logic.T9DeptLogic;
import t9.core.funcs.jexcel.util.T9CSVUtil;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.person.logic.T9PersonLogic;
import t9.core.funcs.person.logic.T9UserPrivLogic;
import t9.core.funcs.system.address.logic.T9AddressLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.global.T9SysProps;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.file.T9FileUploadForm;
public class T9ActiveSetAct {

  public String getActiveSet(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      Statement stmt = null;
      ResultSet rs = null;
      String userIds = "";
      int seqId = 0;
      try {
        stmt = dbConn.createStatement();
        String sql = "select SEQ_ID , PARA_VALUE from SYS_PARA where PARA_NAME = 'ACTIVE_SET_USER'";
        rs = stmt.executeQuery(sql);
        if (rs.next()) {
          userIds = T9Utility.null2Empty(rs.getString("PARA_VALUE"));
          seqId = rs.getInt("SEQ_ID");
        }
      } catch (Exception ex) {
        throw ex;
      } finally {
        T9DBUtility.close(stmt, rs, null);
      }
      T9PersonLogic logic = new T9PersonLogic();
      String userName = T9Utility.encodeSpecial(logic.getNameBySeqIdStr(userIds, dbConn));
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"添加成功");
      request.setAttribute(T9ActionKeys.RET_DATA,"{userId:'"+userIds+"',userName:\""+ userName +"\",seqId:'"+ seqId +"'}");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String setActive(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      String userIds = T9Utility.null2Empty(request.getParameter("userId"));
      String seqId = request.getParameter("seqId");
      String sql = "insert into SYS_PARA (PARA_NAME , PARA_VALUE) VALUES ('ACTIVE_SET_USER','"+ userIds +"')";
      if (!T9Utility.isNullorEmpty(seqId)
          && !"0".equals(seqId)) {
        sql = "update SYS_PARA set PARA_VALUE = '" + userIds + "' where PARA_NAME = 'ACTIVE_SET_USER'"; 
      }
      Statement stmt = null;
      try {
        stmt = dbConn.createStatement();
        stmt.executeUpdate(sql);
      } catch (Exception ex) {
        throw ex;
      } finally {
        T9DBUtility.close(stmt, null, null);
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
}
