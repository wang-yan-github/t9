package t9.subsys.inforesouce.act;



import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Out;
import t9.subsys.inforesouce.logic.T9MateValueLogic;

/**
 * 值域
 * @author qwx110
 *
 */
public class T9MateValueAct{
  private T9MateValueLogic mvlogic = new T9MateValueLogic();
/**
 * 删除值域
 * @param request
 * @param response
 * @return
 * @throws Exception
 */
  public String deleteMateValue(HttpServletRequest request, HttpServletResponse response)throws Exception{
    Connection dbConn = null;
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR); 
    dbConn = requestDbConn.getSysDbConn();
    String seqIda = request.getParameter("seqIda");
    String seqIdb = request.getParameter("seqIdb");
    String number = request.getParameter("number");
    try{
      int falg = mvlogic.updateMate(dbConn, Integer.parseInt(seqIda), Integer.parseInt(seqIdb));
      //T9Out.println(falg);
    } catch (Exception e){   
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
      throw e;
    }
    return "/t9/subsys/inforesouce/act/T9MateElementAct/selectvalue.act?seqid="+seqIdb+"&&number="+number;
  }  
}
