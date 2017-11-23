package t9.core.funcs.system.sealmanage.act;

import java.io.PrintWriter;
import java.sql.Connection;
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
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.dept.logic.T9DeptLogic;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.person.logic.T9PersonLogic;
import t9.core.funcs.system.sealmanage.data.T9Seal;
import t9.core.funcs.system.sealmanage.data.T9SealLog;
import t9.core.funcs.system.sealmanage.logic.T9SealLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.auth.T9PassEncrypt;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;
public class T9SealAct {
  private static Logger log = Logger.getLogger("t9.core.funcs.system.sealmanage.T9SealManageAct");

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
      String sealId = request.getParameter("sealId");
      String sealName = request.getParameter("sealName");
      sealName = T9DBUtility.escapeLike(sealName);
      String beginTime = request.getParameter("beginTime");
      String endTime = request.getParameter("endTime");
      String data = "";
      T9SealLogic sl = new T9SealLogic();
      if(!T9Utility.isNullorEmpty(sealId) || !T9Utility.isNullorEmpty(sealName) || !T9Utility.isNullorEmpty(beginTime) || !T9Utility.isNullorEmpty(endTime)){
        data = sl.getSearchList(dbConn,request.getParameterMap(), sealId, sealName, beginTime, endTime);
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
      String sealId = request.getParameter("sealId");
      String sealName = request.getParameter("sealName");
      String beginTime = request.getParameter("beginTime");
      String endTime = request.getParameter("endTime");
      T9SealLogic sl = new T9SealLogic();
      String data = sl.getSearchList(dbConn,request.getParameterMap(), sealId, sealName, beginTime, endTime);
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
  
  public String updateSealPriv(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    Map map = new HashMap();
    Map mapSealLog = new HashMap();
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);     
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int loginSeqId = person.getSeqId();
      String loginSeqIds = String.valueOf(loginSeqId);
      T9SealLogic sl = new T9SealLogic();
      T9SealLog slLog = new T9SealLog();
        String seqId = request.getParameter("seqId");
        String userStr = request.getParameter("userStr");
        String userName = request.getParameter("userName");
        
        map.put("seqId" , seqId);
        map.put("userStr" , userStr);
       
      T9ORM orm = new T9ORM();
      orm.updateSingle(dbConn, "seal", map);
      
      Calendar cal = Calendar.getInstance();
      java.text.SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");        
      String logTime = sdf.format(cal.getTime());
      
      String sealId = sl.getSealId(dbConn, Integer.parseInt(seqId));
//      map.put("sealId" , sealId);
//      map.put("logType" , "setseal");
//      map.put("logTime" , new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(logTime));
//      map.put("result" , "授权给:"+userName);
//      map.put("ipAdd" , request.getRemoteAddr());
//      map.put("userId" , loginSeqId);
      
      slLog.setsId(sealId);
      slLog.setLogType("setseal");
      slLog.setLogTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(logTime));
      slLog.setResult("授权给:"+userName);
      slLog.setIpAdd(request.getRemoteAddr());
      slLog.setUserId(loginSeqIds);
      //orm.saveSingle(dbConn , "sealLog" , map);
      orm.saveSingle(dbConn, slLog);
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
   * 获取用户名称
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String getUserName(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String data = "";
      String userIdStr = request.getParameter("userId");
      T9SealLogic dl = new T9SealLogic();
      if(!T9Utility.isNullorEmpty(userIdStr)){
        data = dl.getUserNameLogic(dbConn, userIdStr);
      }
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA,"\"" + data + "\"");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String getSealPriv(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    String userName = "";
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      String seqIds = request.getParameter("seqId");
      int seqId = Integer.parseInt(seqIds);
      List<Map> list = new ArrayList();
      T9ORM orm = new T9ORM();
      HashMap map = null;
      StringBuffer sb = new StringBuffer("[");
      String[] filters = new String[]{"SEQ_ID="+seqId};
      List funcList = new ArrayList();
      funcList.add("seal");
      map = (HashMap)orm.loadDataSingle(dbConn, funcList, filters);
      list.addAll((List<Map>) map.get("SEAL"));
      T9PersonLogic logic = new T9PersonLogic();
      if(list.size() > 1){
        for(Map ms : list){
          sb.append("{");
          sb.append("seqId:\"" + ms.get("seqId") + "\"");
          String ss =( ms.get("userStr") == null ) ? "" : (String)ms.get("userStr");
          sb.append(",userStr:\"" + (ms.get("userStr") == null ? "" : ms.get("userStr")) + "\"");
          String name = logic.getNameBySeqIdStr(ss, dbConn);
          sb.append(",userName:\"" + name + "\"");
          sb.append("},");
        }
        sb.deleteCharAt(sb.length() - 1); 
      }else{
        for(Map ms : list){
          sb.append("{");
          sb.append("seqId:\"" + ms.get("seqId") + "\"");
          sb.append(",userStr:\"" + (ms.get("userStr") == null ? "" : ms.get("userStr")) + "\"");
          String ss =( ms.get("userStr") == null ) ? "" : (String)ms.get("userStr");
          String name = logic.getNameBySeqIdStr(ss, dbConn);
          sb.append(",userName:\"" + name + "\"");
          sb.append("}");
        }
      }    
      sb.append("]");
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
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
   * 获取印章后四位ID
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String getCounter(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String data = "";
      String SEAL_ID = request.getParameter("SEAL_ID");
      T9SealLogic dl = new T9SealLogic();
      if(!T9Utility.isNullorEmpty(SEAL_ID)){
        data = dl.getCounterLogic(dbConn, SEAL_ID);
      }
      //System.out.println(data+"NMNMNMNMNMN");
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA,"\"" + data + "\"");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String addSeal(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    Map map = new HashMap();
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);     
      dbConn = requestDbConn.getSysDbConn();
      T9Seal sls = new T9Seal();
      String sealId = request.getParameter("sealId");
      String sealName = request.getParameter("sealName");
      //String certStr = request.getParameter("cetrStr");
      //sealName = T9Utility.encodeSpecial(sealName);
//      if(!T9Utility.isNullorEmpty(sealName)){
//        sealName = sealName.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "").replace("\n", "");
//      }
      String sealData = request.getParameter("sealData");
      //System.out.println(sealData);
      String deptId = request.getParameter("deptId");
      Calendar cal = Calendar.getInstance();        
      java.text.SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");        
      String createTime = sdf.format(cal.getTime());    
   
      sls.setSealId(sealId);
      sls.setSealName(sealName);
      //sls.setCertStr(certStr);
      sls.setSealData(sealData);
      sls.setDeptId(deptId);
      sls.setCreateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(createTime));
       
      T9ORM orm = new T9ORM();
      //orm.saveSingle(dbConn , "seal" , map);
      orm.saveSingle(dbConn , sls);
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
      int seqId = Integer.parseInt(seqIdStr);
      String data = "";
      T9SealLogic sl = new T9SealLogic();
      data = sl.getShowInfo(dbConn, seqId);
      //System.out.println(data+"JKJKLK");
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA,"\"" + data + "\"");
      
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
}
