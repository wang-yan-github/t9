package t9.subsys.oa.fillRegister.attendScore.act;

import java.io.PrintWriter;
import java.sql.Connection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.form.T9FOM;
import t9.subsys.oa.fillRegister.attendScore.data.T9AttendScore;
import t9.subsys.oa.fillRegister.attendScore.logic.T9AttendScoreLogic;


public class T9AttendScoreAct {
  public static final String attachmentFolder = "attendScore";
  private T9AttendScoreLogic logic = new T9AttendScoreLogic();
  
  public String addRecord(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
     
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      Map<String,String[]> map = request.getParameterMap();
      T9AttendScore record = (T9AttendScore) T9FOM.build(map, T9AttendScore.class, "");
      String stUserId = record.getAssessingOfficer();
      String[] staffUserIdStr = stUserId.split(",");
      for(int i = 0; i < staffUserIdStr.length; i++){
//        record.setCreateUserId(String.valueOf(person.getSeqId()));
//        record.setCreateDeptId(person.getDeptId());
//        record.setAbroadUserId(staffUserIdStr[i]);
        this.logic.add(dbConn, record);
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK); 
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功添加"); 
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR); 
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage()); 
      throw e; 
    }
    
    return "/core/inc/rtjson.jsp";
  }
  
}
