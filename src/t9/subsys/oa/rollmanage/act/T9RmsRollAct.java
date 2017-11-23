package t9.subsys.oa.rollmanage.act;


import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

import t9.core.data.T9DbRecord;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.address.data.T9Address;
import t9.core.funcs.dept.logic.T9DeptLogic;
import t9.core.funcs.jexcel.util.T9CSVUtil;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.person.logic.T9PersonLogic;
import t9.core.funcs.person.logic.T9UserPrivLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;
import t9.subsys.oa.rollmanage.data.T9RmsRoll;
import t9.subsys.oa.rollmanage.logic.T9RmsRollLogic;
public class T9RmsRollAct {
  private T9RmsRollLogic logic = new T9RmsRollLogic();
  private static Logger log = Logger.getLogger("t9.core.act.action.T9TestAct");

  /**
   * 新建案卷
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String addRoll(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      Map<String,String[]> map = request.getParameterMap();
      Date time = new Date();
      T9RmsRoll rmsRoll = (T9RmsRoll) T9FOM.build(map, T9RmsRoll.class, "");
      rmsRoll.setAddUser(String.valueOf(person.getSeqId()));
      rmsRoll.setAddTime(time);
      rmsRoll.setStatus("0");
      this.logic.add(dbConn, rmsRoll);
   
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK); 
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功添加"); 
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR); 
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage()); 
      throw e; 
    }
    
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 所属部门下拉框
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

      T9DeptLogic deptLogic = new T9DeptLogic();
      String data = "";
      data = deptLogic.getDeptTreeJson(0, dbConn);
      if(T9Utility.isNullorEmpty(data)){
        data = "[]";
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 获取卷库管理列表
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getRmsRollJosn(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      String seqId = request.getParameter("seqId");
      String flag = request.getParameter("flag");
      String data = this.logic.getRmsRollJson(dbConn,request.getParameterMap(), person, seqId , flag);
      PrintWriter pw = response.getWriter();
      pw.println(data);
      pw.flush();
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;
  }
  
  public String getRmsRollDetail(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    
    String seqId = request.getParameter("seqId");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9RmsRoll rmsRoll = (T9RmsRoll)this.logic.getRmsRollDetail(dbConn, Integer.parseInt(seqId));
      
      if (rmsRoll == null){
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR); 
        request.setAttribute(T9ActionKeys.RET_MSRG, "案卷不存在");
        return "/core/inc/rtjson.jsp";
      }
      StringBuffer data = T9FOM.toJson(rmsRoll);
      T9PersonLogic logic = new T9PersonLogic();
      
      String userName = T9Utility.encodeSpecial(logic.getNameBySeqIdStr(T9Utility.null2Empty(rmsRoll.getPrivUser()), dbConn));
      T9DeptLogic logic2 =  new T9DeptLogic();
      String deptName = T9Utility.encodeSpecial(logic2.getNameByIdStr(T9Utility.null2Empty(rmsRoll.getPrivDept()), dbConn));
      T9UserPrivLogic logic3 =  new T9UserPrivLogic();
      String roleName = T9Utility.encodeSpecial(logic3.getNameByIdStr(T9Utility.null2Empty(rmsRoll.getPrivRole()), dbConn));
      
      data.append(",").append("privUserName:\"").append(userName).append("\"");
      data.append(",").append("privDeptName:\"").append(deptName).append("\"");
      data.append(",").append("privRoleName:\"").append(roleName).append("\"");
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK); 
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询成功"); 
      request.setAttribute(T9ActionKeys.RET_DATA, data.toString()); 
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR); 
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage()); 
      throw e; 
    }
    return "/core/inc/rtjson.jsp";
  }
  
  
  /**
   * 编辑案卷
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String update(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      Map<String,String[]> map = request.getParameterMap();
      T9RmsRoll rmsRoll = (T9RmsRoll) T9FOM.build(map, T9RmsRoll.class, "");
      //rmsRollRoom.setModUser(modUser)
      this.logic.updateRmsRoll(dbConn, rmsRoll);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK); 
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功添加"); 
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR); 
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage()); 
      throw e; 
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 删除一条案卷记录
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String deleteSingle(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    String seqId = request.getParameter("seqId");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
      this.logic.updateRmsFile(dbConn, Integer.parseInt(seqId));
      this.logic.deleteSingle(dbConn, Integer.parseInt(seqId));
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK); 
      request.setAttribute(T9ActionKeys.RET_MSRG, "删除成功"); 
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR); 
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage()); 
      throw e; 
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String deleteContact(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqIdStr = request.getParameter("sumStrs");
      this.logic.updateAllRoll(dbConn, seqIdStr);
      this.logic.deleteAllRoll(dbConn, seqIdStr);
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
   * 获取所属卷库
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getRmsRollRoomName(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqIdStr = request.getParameter("seqId");
      int seqId = Integer.parseInt(seqIdStr);
      String data = this.logic.getRmsRollRoomNameLogic(dbConn, seqId);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA,"\"" + T9Utility.encodeSpecial(data) + "\"");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 更改案卷状态
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String updateStatusFlag(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      String seqIdStr = request.getParameter("seqId");
      int seqId = Integer.parseInt(seqIdStr);
      String statusFlag = request.getParameter("statusFlag");
      String data = this.logic.getRmsRollRoomNameLogic(dbConn, seqId);
      if(T9Utility.isNullorEmpty(statusFlag)){
        statusFlag = "0";
      }else{
        if("0".equals(statusFlag)){
          statusFlag = "1";
        }else if("1".equals(statusFlag)){
          statusFlag = "0";
        }else{
          statusFlag = "0";
        }
      }
      Map m =new HashMap();
      Date time = new Date();
      m.put("seqId", seqId);
      m.put("status", statusFlag);
      m.put("modUser", String.valueOf(person.getSeqId()));
      m.put("modTime", T9Utility.getCurDateTimeStr());
      T9ORM orm = new T9ORM();
      orm.updateSingle(dbConn, "rmsRoll", m);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA,"\"" + data + "\"");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 获取小编码表内容
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getCodeName(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String classCode = request.getParameter("classCode");
      String classNo = request.getParameter("classNo");
      String data = this.logic.getCodeNameLogic(dbConn, classCode, classNo);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA,"\"" + data + "\"");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 取得文件列表
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getRmsFileJosn(HttpServletRequest request, HttpServletResponse response) throws Exception {

    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int rollId = Integer.parseInt(request.getParameter("seqId"));
      String data = this.logic.getRmsFileJosn(dbConn, request.getParameterMap(), person, rollId);

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
  
  /**
   * 查询出select中的案卷
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getRmsRollSelect(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9ORM orm = new T9ORM();
      HashMap map = null;
      List<Map> list = new ArrayList();
      StringBuffer sb = new StringBuffer("[");
      
      String[] filters2 = new String[]{"STATUS = 0"};
      List funcList = new ArrayList();
      funcList.add("rmsRoll");
      map = (HashMap)orm.loadDataSingle(dbConn, funcList, filters2);
      list.addAll((List<Map>) map.get("RMS_ROLL"));
      int flag = 0;
      for(Map ms : list){
        String rollName = (String) ms.get("rollName");
        if(!T9Utility.isNullorEmpty(rollName)){
          rollName = T9Utility.encodeSpecial(rollName);
        }
        String rollCode = (String) ms.get("rollCode");
        if(!T9Utility.isNullorEmpty(rollName)){
          rollCode = T9Utility.encodeSpecial(rollCode);
        }
        sb.append("{");
        sb.append("seqId:\"" + ms.get("seqId") + "\"");
        sb.append(",rollCode:\"" + (ms.get("rollCode") == null ? "" : rollCode) + "\"");
        sb.append(",rollName:\"" + (ms.get("rollName") == null ? "" : rollName) + "\"");
        sb.append("},");
      }
      if(sb.length() > 1){
        sb.deleteCharAt(sb.length() - 1); 
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
  public String needApprove(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int rollId = Integer.parseInt(request.getParameter("rollId"));
      boolean flag = this.logic.isNeedApprove(dbConn, rollId, person);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, String.valueOf(flag));
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 查询出select中的案卷
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getRmsRollSelect2(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      StringBuffer sb = new StringBuffer("[");
      
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      String sql = "";
      if (person.isAdminRole()) {
        sql = "select * from RMS_ROLL where STATUS = '0'";
      } else {
        sql = "select * from RMS_ROLL where STATUS = '0' AND DEPT_ID = '" + person.getDeptId() + "'";
      }
      PreparedStatement ps = null;
      ResultSet rs = null;
      try {
        ps = dbConn.prepareStatement(sql);
        rs = ps.executeQuery() ;
        while (rs.next()) {
          String rollName =   T9Utility.encodeSpecial(T9Utility.null2Empty(rs.getString("ROLL_NAME")));
          String rollCode =  T9Utility.encodeSpecial(T9Utility.null2Empty(rs.getString("ROLL_CODE")));
          int seqId = rs.getInt("SEQ_ID");
          sb.append("{");
          sb.append("seqId:\"" + seqId + "\"");
          sb.append(",rollCode:\"" + rollCode + "\"");
          sb.append(",rollName:\"" + rollName + "\"");
          sb.append("},");
        }
      } catch (Exception e) {
        throw e;
      } finally {
        T9DBUtility.close(ps, rs, null);
      }
      if(sb.length() > 1){
        sb.deleteCharAt(sb.length() - 1); 
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
   * 移卷转至
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String changeRmsRollSelect(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqStr = request.getParameter("seqIds");
      int rollId = Integer.parseInt(request.getParameter("rollId"));
      this.logic.changeRmsRollSelect(dbConn, seqStr, rollId);
      
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 导出到EXCEL表格中


   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String exportToExcel(HttpServletRequest request,HttpServletResponse response) throws Exception{
    response.setCharacterEncoding(T9Const.CSV_FILE_CODE);
    Connection conn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      conn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      String seqIdStr = request.getParameter("seqIdStr");
      String fileName = URLEncoder.encode("文件档案.csv","UTF-8");
      fileName = fileName.replaceAll("\\+", "%20");
      response.setHeader("Cache-control","private");
      response.setContentType("application/vnd.ms-excel");
      response.setHeader("Accept-Ranges","bytes");
      response.setHeader("Cache-Control","maxage=3600");
      response.setHeader("Pragma","public");
      response.setHeader("Content-disposition","attachment; filename=\"" + fileName + "\"");
      ArrayList<T9DbRecord > dbL = this.logic.toExportRmsFileData(conn, seqIdStr);
      T9CSVUtil.CVSWrite(response.getWriter(), dbL);
    } catch (Exception ex) {
      ex.printStackTrace();
      throw ex;
    }
    return null;
  }
  
  public String getSearchRmsRoll(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    ArrayList<T9Address> addressList = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      String rollCode = T9DBUtility.escapeLike(request.getParameter("rollCode"));
      String rollName = T9DBUtility.escapeLike(request.getParameter("rollName"));
      String roomId = T9DBUtility.escapeLike(request.getParameter("roomId"));
      String years = T9DBUtility.escapeLike(request.getParameter("years"));
      String beginDate0 = T9DBUtility.escapeLike(request.getParameter("beginDate0"));
      String beginDate1 = T9DBUtility.escapeLike(request.getParameter("beginDate1"));
      String endDate0 = T9DBUtility.escapeLike(request.getParameter("endDate0"));
      String endDate1 = T9DBUtility.escapeLike(request.getParameter("endDate1"));
      String secret = T9DBUtility.escapeLike(request.getParameter("secret"));
      String deadline0 = T9DBUtility.escapeLike(request.getParameter("deadline0"));
      String deadline1 = T9DBUtility.escapeLike(request.getParameter("deadline1"));
      String categoryNo = T9DBUtility.escapeLike(request.getParameter("categoryNo"));
      String catalogNo = T9DBUtility.escapeLike(request.getParameter("catalogNo"));
      String archiveNo = T9DBUtility.escapeLike(request.getParameter("archiveNo"));
      String boxNo = T9DBUtility.escapeLike(request.getParameter("boxNo"));
      String microNo = T9DBUtility.escapeLike(request.getParameter("microNo"));
      String certificateKind = T9DBUtility.escapeLike(request.getParameter("certificateKind"));
      String certificateStart0 = T9DBUtility.escapeLike(request.getParameter("certificateStart0"));
      String certificateStart1 = T9DBUtility.escapeLike(request.getParameter("certificateStart1"));
      String certificateEnd0 = T9DBUtility.escapeLike(request.getParameter("certificateEnd0"));
      String certificateEnd1 = T9DBUtility.escapeLike(request.getParameter("certificateEnd1"));
      String rollPage0 = T9DBUtility.escapeLike(request.getParameter("rollPage0"));
      String rollPage1 = T9DBUtility.escapeLike(request.getParameter("rollPage1"));
      String deptId = T9DBUtility.escapeLike(request.getParameter("deptId"));
      String remark = T9DBUtility.escapeLike(request.getParameter("remark"));
      String data = "";
      data = this.logic.getRmsRollSearchJson(dbConn, request.getParameterMap(), person, rollCode, rollName, roomId, years, beginDate0, beginDate1, endDate0, endDate1,
          secret, deadline0, deadline1, categoryNo, catalogNo, archiveNo, boxNo, microNo, certificateKind, certificateStart0, certificateStart1, rollPage0, rollPage1,
          deptId, remark, certificateEnd0, certificateEnd1);
     
      PrintWriter pw = response.getWriter();
      pw.println(data);
      pw.flush();
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;
  }
  
  
}
