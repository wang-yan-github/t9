package t9.core.funcs.setdescktop.setports.act;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.setdescktop.setports.logic.T9PersonInfoLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.form.T9FOM;

public class T9PersonInfoAct {
  
  private T9PersonInfoLogic logic = new T9PersonInfoLogic();
  
  public String getPersonInfo(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
      T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      
      t9.core.funcs.setdescktop.setports.data.T9Person pi = logic.getPersonInfo(dbConn, user.getSeqId());
      request.setAttribute(T9ActionKeys.RET_DATA, T9FOM.toJson(pi).toString());
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功查询桌面属性");
    }catch(Exception ex) {
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
        throw ex;
    }
    
    return "/core/inc/rtjson.jsp";
  }
  
  public String updatePersonInfo(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
      
      if(request.getSession().getAttribute("LOGIN_USER") == null){
        return null;
      }
      t9.core.funcs.setdescktop.setports.data.T9Person pi = (t9.core.funcs.setdescktop.setports.data.T9Person)T9FOM.build(request.getParameterMap());
      
      T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      
      if(user.getSeqId() != pi.getSeqId()){
        return null;
      }
      pi.setMobilNoHidden("on".equals(pi.getMobilNoHidden())?"1":"0");
      
      logic.updatePersonInfo(dbConn, pi);
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功修改");
      
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    
    return "/core/inc/rtjson.jsp";
  }

}
