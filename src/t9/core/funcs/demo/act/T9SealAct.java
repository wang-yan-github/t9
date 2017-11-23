package t9.core.funcs.demo.act;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.demo.logic.T9SealLogic;
import t9.core.funcs.dept.logic.T9DeptLogic;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.person.logic.T9PersonLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.auth.T9PassEncrypt;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;
import t9.subsys.shtest.SealLogic;
public class T9SealAct {
  private static Logger log = Logger.getLogger("t9.core.funcs.system.ADsealmanage.T9SealManageAct");

  /**
   * 印章权限管理列表
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String getSealList(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      
      String sealName = request.getParameter("sealName");
      sealName = T9DBUtility.escapeLike(sealName);
      String beginTime = request.getParameter("beginTime");
      String endTime = request.getParameter("endTime");
      String data = "";
      T9SealLogic sl = new T9SealLogic();
      if(!T9Utility.isNullorEmpty(sealName)
          || !T9Utility.isNullorEmpty(beginTime)
          || !T9Utility.isNullorEmpty(endTime)){
        data = sl.getSearchList(dbConn,request.getParameterMap(), sealName, beginTime, endTime);
      }else{
        data = sl.getSealList(dbConn,request.getParameterMap());
      }
      PrintWriter pw = response.getWriter();
      pw.println(data);
      pw.flush();
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;
  }

  
  public String getSearchList(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      String sealName = request.getParameter("sealName");
      String beginTime = request.getParameter("beginTime");
      String endTime = request.getParameter("endTime");
      T9SealLogic sl = new T9SealLogic();
      String data = sl.getSearchList(dbConn,request.getParameterMap(), sealName, beginTime, endTime);
      PrintWriter pw = response.getWriter();
      pw.println(data);
      pw.flush();
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;
  }
  
  public String addPerson(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    int num = 0;
    Map map = new HashMap();
    String userIdOld = "";
    String userId = "";
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);     
      dbConn = requestDbConn.getSysDbConn();
      
        String userName = request.getParameter("userName");
        String userPriv = request.getParameter("userPriv");
        String userPrivOther = request.getParameter("role");
        
        map.put("userId" , userId);
        map.put("userName" , userName);
        map.put("userPriv" , userPriv);
       
      T9ORM orm = new T9ORM();
      orm.saveSingle(dbConn , "sealLog" , map);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功添加人员");
      
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
 
  
  
  
  
  
  /**
   * 删除
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String deleteSeal(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int seqId = person.getSeqId();
      String sumStrs = request.getParameter("sumStrs");
      T9SealLogic pl = new T9SealLogic();
      pl.deleteSeal(dbConn, sumStrs);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
 
 
  /**
   * 根据用户的管理权限得到所有部门（考勤统计）
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String selectDeptToAttendance(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
      //int deptId = Integer.parseInt(request.getParameter("deptId"));
      int userId = user.getSeqId();
      String userPriv = user.getUserPriv();// 角色
      String postpriv = user.getPostPriv();// 管理范围
      String postDept = user.getPostDept();// 管理范围指定部门
      int userDeptId = user.getDeptId();
      T9DeptLogic deptLogic = new T9DeptLogic();
      boolean isAdminRole = user.isAdminRole();
      boolean isAdmin = user.isAdmin();
      String userDeptName = deptLogic.getNameByIdStr(
          String.valueOf(userDeptId), dbConn);
      String data = "";
      if (T9Utility.isNullorEmpty(postpriv)) {
        String[] postDeptArray = {String.valueOf(userDeptId)};
        data = "[" + deptLogic.getDeptTreeJson(0, dbConn, postDeptArray)
            + "]";
      } else {
        if (postpriv.equals("0")) {
          // data = "[{text:\"" + userDeptName + "\",value:" + userDeptId + "}]";
            String[] postDeptArray = {String.valueOf(userDeptId)};
            data = "[" + deptLogic.getDeptTreeJson(0, dbConn, postDeptArray)
                + "]";
         }
         if (postpriv.equals("1")) {
           data = deptLogic.getDeptTreeJson(0, dbConn);
         }
         if (postpriv.equals("2")) {
           if (postDept == null || postDept.equals("")) {
             data = "[]";
           } else {
             String[] postDeptArray = postDept.split(",");
             data = "[" + deptLogic.getDeptTreeJson(0, dbConn, postDeptArray)
                 + "]";

           }
         }
      }
      //System.out.println(data+"KJKJKJ");
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, String.valueOf(userDeptId)
          + "," + postpriv);
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String getShowInfo(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    Map map = new HashMap();
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);     
      dbConn = requestDbConn.getSysDbConn();
      String seqIdStr = request.getParameter("seqId");
      //System.out.println("seqId:"+seqIdStr);
      int seqId = Integer.parseInt(seqIdStr);
      String data = "";
      T9SealLogic sl = new T9SealLogic();
      data = sl.getShowInfo(dbConn, seqId);
      BASE64Decoder decoder = new BASE64Decoder();
      byte[] bytes = decoder.decodeBuffer(data);
     
      StringBuffer sb2 = new StringBuffer();
      data = new String(bytes);
     // System.out.println("原始数据："+data);
      data = data.replaceAll("\n","");
      data = data.replaceAll("\r","");
     // System.out.println("这是解密数据；"+data);
      
      
      
      
      String sSealDate = null;
      sSealDate = "";
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA,data);
      
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String getSealPriv(HttpServletRequest request,
	      HttpServletResponse response) throws Exception {
	    Connection dbConn = null;
	    try {
	      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
	      dbConn = requestDbConn.getSysDbConn();
	      String seqIds = request.getParameter("seqId");
	      int seqId = Integer.parseInt(seqIds);
	     T9SealLogic t9Logic = new T9SealLogic();
	     String dList =  t9Logic.getdevicelist(dbConn, seqId);
	      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
	      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
	      request.setAttribute(T9ActionKeys.RET_DATA, dList);
	    }catch(Exception ex) {
	      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
	      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
	      throw ex;
	    }
	    return "/core/inc/rtjson.jsp";
	  }
  public String getDevice(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      ResultSet rs = null ;
      String sql = "select m.SEQ_ID as SEQ_ID, DEVICE_NAME ,USER_NAME from mobile_device m left outer join person p on m.uid = p.seq_id  where DEVICE_TYPE = '1'";
      PreparedStatement ps = null;
      StringBuffer sb = new StringBuffer("[");
      int i = 0 ;
      try {
        ps = dbConn.prepareStatement(sql);
        rs = ps.executeQuery();
        while(rs.next()){
         String DEVICE_NAME = T9Utility.encodeSpecial(rs.getString("DEVICE_NAME"));
          String SEQ_ID =  rs.getString("SEQ_ID");
          String USER_NAME =  T9Utility.encodeSpecial(rs.getString("USER_NAME"));
          i++;
          sb.append("{").append("DEVICE_NAME").append(":\"").append(DEVICE_NAME).append("\",SEQ_ID:").append(SEQ_ID).append(",USER_NAME:\"").append(USER_NAME).append("\"},");
        }
      } catch (Exception e) {
        throw e;
      } finally {
        T9DBUtility.close(ps, rs, null);
      }
      if (i > 0) {
        sb.deleteCharAt(sb.length() -1);
      }
      sb.append("]");
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功添加");
      request.setAttribute(T9ActionKeys.RET_DATA,sb.toString());
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String updateSealPriv(HttpServletRequest request,
	      HttpServletResponse response) throws Exception {
	    Connection dbConn = null;
	    try {
	      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
	      dbConn = requestDbConn.getSysDbConn();
	      String seqIds = request.getParameter("seqId");
	      String userStr = request.getParameter("userStr");
	      int seqId = Integer.parseInt(seqIds);
	   String sql = "UPDATE MOBILE_SEAL  SET DEVICE_LIST ='"+userStr+"'" +"WHERE SEQ_ID = "+seqId;
	   	SealLogic.updateSql(dbConn, sql);
	   	request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
	      request.setAttribute(T9ActionKeys.RET_MSRG,"成功添加");
	      
	    }catch(Exception ex) {
	      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
	      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
	      throw ex;
	    }
	    return "/core/inc/rtjson.jsp";
	  }
}
