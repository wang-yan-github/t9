package t6.fis.funcs.trnsperiod.act;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.global.T9ActionKeys;
import t9.core.global.T9Const;

public class T9TrnsPeriodAct {
  private static Logger log = Logger.getLogger("t6.fis.funcs.trnsperiod.logic.T9TrnsPeriodLogic");
  
  /**
   * 初始化通用转账模板信息
   * <ol>
   * <li>用公共分页的方法查询TRNSVDESC表中所有内容(方便删除和修改时用数据还要重新查，影响效率)</li>
   * <li>以列表的形式显示有效信息</li>
   * </ol>
   * @author ych
   * @param request list
   * @param response 列表信息
   * @return
   * @throws Exception
   * **/
  public String trnsmodelMain(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    try {
      //TODO
      //
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "");
    } catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  } 
  /**
   * 增加通用转账模板
   * <ol>
   * <li></li>
   * <li></li>
   * <li></li>
   * </ol>
   * @author ych
   * @param request 
   * @param response
   * @return
   * @throws Exception
   * **/
  public String trnsmodelInsertSave(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    try {
      //TODO
      //
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "");
    } catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  } 
  /**
   * 删除通用转账模板
   * <ol>
   * <li></li>
   * <li></li>
   * <li></li>
   * </ol>
   * @author ych
   * @param request
   * @param response 
   * @return
   * @throws Exception
   * **/
  public String trnsmodelDelete(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    try {
      //TODO
      //
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "");
    } catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  } 
  /**
   * 修改通用转账模板
   * <ol>
   * <li></li>
   * <li></li>
   * </ol>
   * @author ych
   * @param request 
   * @param response
   * @return
   * @throws Exception
   * **/
  public String trnsmodelUpdateSave(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    try {
      //TODO
      //
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "");
    } catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  } 
}
