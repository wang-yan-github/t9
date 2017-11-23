package t9.subsys.oa.addworkfee.act;

import java.sql.Connection;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import t9.core.data.T9RequestDbConn;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.subsys.oa.addworkfee.data.T9RoleBaseFee;
import t9.subsys.oa.addworkfee.logic.T9RoleBaseFeeLogic;

/**
 * 按照不同的角色添加不同的加班费基数
 * @author Administrator
 *
 */
public class T9RoleBaseFeeAct{
  
  /**
   * 增加一个加班费基数
   * @param request
   * @param response
   * @return
   * @throws Throwable
   */
   public String addRoleBaseFee(HttpServletRequest request, HttpServletResponse response) throws Throwable{
     try{
       Connection dbConn = null;
       T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
       dbConn = requestDbConn.getSysDbConn();
       String roleIds = request.getParameter("roleId");
       String normal = request.getParameter("normaladd");
       String festival = request.getParameter("festivaladd");
       String weekadd = request.getParameter("weekadd");
       String baseadd = request.getParameter("baseAdd");
       T9RoleBaseFee abf = new T9RoleBaseFee();
       abf.setNormalAdd(Double.parseDouble(normal));
       abf.setFestivalAdd(Double.parseDouble(festival));
       abf.setWeekAdd(Double.parseDouble(weekadd));
       abf.setBaseAdd(Double.parseDouble(baseadd));
       abf.setRoleIds(roleIds);
       T9RoleBaseFeeLogic logic = new T9RoleBaseFeeLogic();
       logic.addT9RoleBaseFee(dbConn, abf);
       List<T9RoleBaseFee> fees = logic.findT9RoleBaseFeeList(dbConn);
       request.setAttribute("fees", fees);
    } catch (Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
      throw e;
    }
    return "/subsys/oa/addworkfee/rolebasefee.jsp";
   }
   
   /**
    * 查找所有的加班费基数
    * @param request
    * @param response
    * @return
    * @throws Exception
    */
   public String findRoleBaseFee(HttpServletRequest request, HttpServletResponse response) throws Exception{
     Connection dbConn = null;
     T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
     try{
      dbConn = requestDbConn.getSysDbConn();
      T9RoleBaseFeeLogic logic = new T9RoleBaseFeeLogic();
      List<T9RoleBaseFee> fees = logic.findT9RoleBaseFeeList(dbConn);
      request.setAttribute("fees", fees); 
    } catch (Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
      throw e;
    }
    return "/subsys/oa/addworkfee/rolebasefee.jsp";
   }
   
   /**
    * 编辑加班费
    * @param request
    * @param response
    * @return
    * @throws Exception
    */
   public String editRoleBaseFee(HttpServletRequest request, HttpServletResponse response)throws Exception{
     Connection dbConn = null;
     T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
     try{
      dbConn = requestDbConn.getSysDbConn();
      T9RoleBaseFeeLogic logic = new T9RoleBaseFeeLogic();
      String seq_id = request.getParameter("seqId");
      int k = 0;
      if(!T9Utility.isNullorEmpty(seq_id)){
        k = Integer.parseInt(seq_id);
      }
      T9RoleBaseFee fee = logic.findT9RoleBaseFee(dbConn, k);
      List<T9RoleBaseFee> fees = logic.findT9RoleBaseFeeList(dbConn);
      request.setAttribute("fees", fees); 
      request.setAttribute("fee", fee); 
    } catch (Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
      throw e;
    }
    return "/subsys/oa/addworkfee/rolebasefee.jsp";
   }
   
   /**
    * 删除
    * @param request
    * @param response
    * @return
    * @throws Exception
    */
   public String delRoleBaseFee(HttpServletRequest request, HttpServletResponse response)throws Exception{
     try{
       String seq_id = request.getParameter("seqId");
       Connection dbConn = null;
       T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
       dbConn = requestDbConn.getSysDbConn();
       T9RoleBaseFeeLogic logic = new T9RoleBaseFeeLogic();
       logic.delT9RoleBaseFee(dbConn, Integer.parseInt(seq_id));
       List<T9RoleBaseFee> fees = logic.findT9RoleBaseFeeList(dbConn);
       request.setAttribute("fees", fees);
    } catch (Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
      throw e;
    }
    return "/subsys/oa/addworkfee/rolebasefee.jsp";
   }
   
   /**
    * 更新
    * @param request
    * @param response
    * @return
    * @throws Exception
    */
   public String updatRoleBaseFee(HttpServletRequest request, HttpServletResponse response)throws Exception{
     try{
       Connection dbConn = null;
       T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
       dbConn = requestDbConn.getSysDbConn();
       String roleId = request.getParameter("roleId");
       String normal = request.getParameter("normaladd");
       String festival = request.getParameter("festivaladd");
       String weekadd = request.getParameter("weekadd");
       String baseadd = request.getParameter("baseAdd");
       String seqId = request.getParameter("seqId");
       T9RoleBaseFee abf = new T9RoleBaseFee();
       abf.setNormalAdd(Double.parseDouble(normal));
       abf.setFestivalAdd(Double.parseDouble(festival));
       abf.setWeekAdd(Double.parseDouble(weekadd));
       abf.setBaseAdd(Double.parseDouble(baseadd));
       abf.setRoleId(Integer.parseInt(roleId));
       abf.setSeqId(Integer.parseInt(seqId));
       T9RoleBaseFeeLogic logic = new T9RoleBaseFeeLogic();
       logic.updatT9RoleBaseFee(dbConn, abf);
       List<T9RoleBaseFee> fees = logic.findT9RoleBaseFeeList(dbConn);
       request.setAttribute("fees", fees);
    } catch (Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
      throw e;
    }
    return "/subsys/oa/addworkfee/rolebasefee.jsp";
   }
}
