package t9.core.funcs.specialflag.act;


import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.specialflag.data.T9SpecialFlag;
import t9.core.funcs.specialflag.logic.T9SpecialFlagLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;

public class T9SpecialFlagAct {
  private static Logger log = Logger.getLogger("t9.core.funcs.specialflag.act.T9SpecialFlagAct");
  
  T9SpecialFlagLogic flagLogic = new T9SpecialFlagLogic();
  
  public String getSpecialFlag(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    request.setCharacterEncoding("utf-8");
    Connection dbConn = null;
    String seqId = request.getParameter("seqId");
    String data = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
  
      T9SpecialFlag flag = null;
      
      T9ORM orm = new T9ORM();
      flag = (T9SpecialFlag)orm.loadObjSingle(dbConn, T9SpecialFlag.class, Integer.parseInt(seqId));
      if(flag == null) {
        flag = new T9SpecialFlag();
      }
      
      StringBuffer sb = new StringBuffer("[");
      sb.append("{");
      sb.append("flagSort:\"" + flag.getFlagSort() + "\"");
      sb.append(",flagCode:\"" + flag.getFlagCode() + "\"");
      sb.append(",flagDesc:\"" + flag.getFlagDesc() + "\"");
      sb.append("}");
      sb.append("]");
      data = sb.toString();
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String getMaxFlagCode(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    request.setCharacterEncoding("utf-8");
    Connection dbConn = null;
    String flagCode = request.getParameter("");
    String sort = request.getParameter("sort");
    String data = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      int flagCodeMax = 0;
      flagCodeMax = flagLogic.getMaxFlagCode(dbConn, sort);
  
      StringBuffer sb = new StringBuffer("[");
      sb.append("{");
      sb.append("code:\"" + flagCodeMax + "\"");
      sb.append("}");
      sb.append("]");
      data = sb.toString();
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, data);

    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String deleteSpecialFlag(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String seqId = request.getParameter("seqId");   
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);   
      dbConn = requestDbConn.getSysDbConn();
      
      Map map = new HashMap();
      map.put("seqId", seqId);
      
      T9ORM orm = new T9ORM();
      orm.deleteSingle(dbConn, "specialFlag", map);
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功删除标记");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String addSpecialFlag(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    String flagCode = null;
    String flagDesc = null;
    String flagSort = null;
    int num = 0;
    String data = null;
    try {
      T9RequestDbConn requestDbConn 
                      = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);     
      dbConn = requestDbConn.getSysDbConn();
      
      T9SpecialFlag flag = (T9SpecialFlag)T9FOM.build(request.getParameterMap());
      
      flagCode = flag.getFlagCode();
      if(flagCode == null || "".equals(flagCode)) {
        return "/core/inc/rtjson.jsp";
      }
      
      flagDesc = flag.getFlagDesc();
      if(flagDesc == null || "".equals(flagDesc)) {
        return "/core/inc/rtjson.jsp";
      }
      
      flagSort = flag.getFlagSort();
      num = flagLogic.selectFlag(dbConn, flagCode, flagSort);
      if(num >= 1) {       
        StringBuffer sb = new StringBuffer("[");
        sb.append("{");
        sb.append("num:\"" + num + "\"");
        sb.append("}");
        sb.append("]");
        data = sb.toString();
        
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG, "标记编号重复, 标记编号不能重复");
        request.setAttribute(T9ActionKeys.RET_DATA, data);
        return "/core/inc/rtjson.jsp";
      }else if(num == 0) {
        StringBuffer sb = new StringBuffer("[");
        sb.append("{");
        sb.append("num:\"" + num + "\"");
        sb.append("}");
        sb.append("]");
        data = sb.toString();
        
        T9ORM orm = new T9ORM();
        orm.saveSingle(dbConn, flag);
        
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG,"成功添加标记");
        request.setAttribute(T9ActionKeys.RET_DATA, data);
      }
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String updateSpecialFlag(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    String flagCodeOld = request.getParameter("flagCodeOld");
    String flagSort = null;
    String flagCode = null;
    String flagDesc = null;
    int num = 0;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);   
      dbConn = requestDbConn.getSysDbConn();
      
      T9SpecialFlag flag = (T9SpecialFlag)T9FOM.build(request.getParameterMap());
           
      flagCode = flag.getFlagCode();
      if(flagCode == null || "".equals(flagCode)) {
        return "/core/inc/rtjson.jsp";
      }
      
      flagDesc = flag.getFlagDesc();
      if(flagDesc == null || "".equals(flagDesc)) {
        return "/core/inc/rtjson.jsp";
      }
      
      T9ORM orm = new T9ORM();
      if(flagCodeOld.equals(flagCode)) {
        orm.updateSingle(dbConn, flag);        
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG,"成功编辑标记");
        return "/core/inc/rtjson.jsp";
      }
      
      flagSort = flag.getFlagSort();
      num = flagLogic.selectFlag(dbConn, flagCode, flagSort);
      if(num >= 1) {
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG, "标记编号重复, 标记编号不能重复");
        return "/core/inc/rtjson.jsp";
      }
      orm.updateSingle(dbConn, flag);

      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功编辑标记");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";    
  }
}
