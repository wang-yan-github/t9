package t9.core.funcs.system.mobilesms.act;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9DbRecord;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.address.data.T9Address;
import t9.core.funcs.jexcel.util.T9JExcelUtil;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.person.logic.T9PersonLogic;
import t9.core.funcs.system.mobilesms.data.T9Sms2;
import t9.core.funcs.system.mobilesms.data.T9Sms2Priv;
import t9.core.funcs.system.mobilesms.data.T9Sms3;
import t9.core.funcs.system.mobilesms.logic.T9MobileSmsLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;
public class T9MobileSmsAct {
  private static Logger log = Logger.getLogger("t9.core.funcs.system.mobilesms.T9MobileSmsAct");
  
  /**
   * 被提醒权限
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String getRemindPriv(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    String userName = "";
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      List<Map> list = new ArrayList();
      T9ORM orm = new T9ORM();
      Map filters = new HashMap();
      filters = null;
      String data = null;
      List< T9Sms2Priv> intface = orm.loadListSingle(dbConn, T9Sms2Priv.class, filters);
      if(intface.size() == 0){
        data = T9FOM.toJson(new T9Sms2Priv()).toString();
      }else{
        data = T9FOM.toJson(intface.get(0)).toString();
      }
      
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
  
  /**
   * 编辑被提醒权限
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String updateRemindPriv(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9MobileSmsLogic orgLogic = new T9MobileSmsLogic();
      int seqId = Integer.parseInt(request.getParameter("seqId"));
      String remindPriv = request.getParameter("remindPriv");
      Map m =new HashMap();
      
      if(seqId != 0){
        m.put("seqId", seqId);
      }
      m.put("remindPriv", remindPriv);
      T9ORM orm = new T9ORM();
      if(seqId != 0){
        orm.updateSingle(dbConn, "sms2Priv", m);
      }else{
        orm.saveSingle(dbConn, "sms2Priv", m);
      }
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"被提醒权限已修改");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 外发权限
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String getOutPriv(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    String userName = "";
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      List<Map> list = new ArrayList();
      T9ORM orm = new T9ORM();
      Map filters = new HashMap();
      filters = null;
      String data = null;
      List< T9Sms2Priv> intface = orm.loadListSingle(dbConn, T9Sms2Priv.class, filters);
      if(intface.size() == 0){
        data = T9FOM.toJson(new T9Sms2Priv()).toString();
      }else{
        data = T9FOM.toJson(intface.get(0)).toString();
      }
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
  
  /**
   * 编辑外发权限
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String updateOutPriv(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9MobileSmsLogic orgLogic = new T9MobileSmsLogic();
      int seqId = Integer.parseInt(request.getParameter("seqId"));
      String outPriv = T9Utility.null2Empty(request.getParameter("outPriv"));
      String outToSelf = request.getParameter("outToSelf");
      if(outToSelf.trim().equals("true")){
        outToSelf = "1";
      }else{
        outToSelf = "0";
      }
      Map m =new HashMap();
      if(seqId != 0){
        m.put("seqId", seqId);
      }
      m.put("outPriv", outPriv);
      m.put("outToSelf", outToSelf);
      T9ORM orm = new T9ORM();
      if(seqId != 0){
        orm.updateSingle(dbConn, "sms2Priv", m);
      }else{
        orm.saveSingle(dbConn, "sms2Priv", m);
      }
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"外发权限已修改");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 提醒权限
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String getSms2RemindPriv(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    String userName = "";
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      List<Map> list = new ArrayList();
      T9ORM orm = new T9ORM();
      Map filters = new HashMap();
      filters = null;
      String data = null;
      List< T9Sms2Priv> intface = orm.loadListSingle(dbConn, T9Sms2Priv.class, filters);
      if(intface.size() == 0){
        data = T9FOM.toJson(new T9Sms2Priv()).toString();
      }else{
        data = T9FOM.toJson(intface.get(0)).toString();
      }
      //System.out.println(data);
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
  
  /**
   * 编辑提醒权限
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String updateSms2RemindPriv(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9MobileSmsLogic orgLogic = new T9MobileSmsLogic();
      int seqId = Integer.parseInt(request.getParameter("seqId"));
      String sms2RemindPriv = request.getParameter("sms2RemindPriv");
      Map m =new HashMap();
      if(seqId != 0){
        m.put("seqId", seqId);
      }
      m.put("sms2RemindPriv", sms2RemindPriv);
      T9ORM orm = new T9ORM();
      if(seqId != 0){
        orm.updateSingle(dbConn, "sms2Priv", m);
      }else{
        orm.saveSingle(dbConn, "sms2Priv", m);
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"提醒权限已修改");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String getTypePriv(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      List<Map> list = new ArrayList();
      T9ORM orm = new T9ORM();
      Map filters = new HashMap();
      filters = null;
      String data = null;
      List< T9Sms2Priv> intface = orm.loadListSingle(dbConn, T9Sms2Priv.class, filters);
      
      if(intface.size() == 0){
        data = T9FOM.toJson(new T9Sms2Priv()).toString();
      }else{
        data = T9FOM.toJson(intface.get(0)).toString();
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA,data);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 获取模块权限
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String getTypePrivConfig(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String pos = request.getParameter("pos");
    T9Person user = (T9Person) request.getSession().getAttribute("LOGIN_USER");// 获得登陆用户
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      List<Map> list = new ArrayList();
      List<Map> listd = new ArrayList();
      T9ORM orm = new T9ORM();
      HashMap map = null;
      HashMap mapd = null;
      String seqIdStr = "";
      String typePrivStr = request.getParameter("typePriv");
      StringBuffer selectedSb = new StringBuffer("selected:[");
      StringBuffer disselectedSb = new StringBuffer("{disselected:[");
      
      T9MobileSmsLogic msl = new T9MobileSmsLogic();
      String seqIds = msl.getSysCodeSeqId(dbConn, typePrivStr);
      if(!T9Utility.isNullorEmpty(seqIds)){
        seqIdStr = seqIds.substring(0, seqIds.length()-1);
      }else{
        seqIdStr = "-1";
      }
      
      String[] filters = new String[]{"CLASS_NO = 'SMS_REMIND' and SEQ_ID IN (" + seqIdStr + ") "};
      List funcList = new ArrayList();
      funcList.add("codeItem");
      map = (HashMap)orm.loadDataSingle(dbConn, funcList, filters);
      list.addAll((List<Map>) map.get("CODE_ITEM"));
      for(Map ms : list){
        selectedSb.append("{");
        selectedSb.append("value:\"" + ms.get("classCode") + "\"");
        selectedSb.append(",text:\"" + ms.get("classDesc") + "\"");
        selectedSb.append("},");
      }
      
      String[] filtersd = new String[]{"CLASS_NO = 'SMS_REMIND' and (not SEQ_ID IN (" + seqIdStr + ")) "};
      List funcListd = new ArrayList();
      funcListd.add("codeItem");
      mapd = (HashMap)orm.loadDataSingle(dbConn, funcListd, filtersd);
      listd.addAll((List<Map>) mapd.get("CODE_ITEM"));
      for(Map msd : listd){
        disselectedSb.append("{");
        disselectedSb.append("value:\"" + msd.get("classCode") + "\"");
        disselectedSb.append(",text:\"" + msd.get("classDesc") + "\"");
        disselectedSb.append("},");
      }
      if (selectedSb.charAt(selectedSb.length() - 1) == ',') {
        selectedSb.deleteCharAt(selectedSb.length() - 1);
      }
      if (disselectedSb.charAt(disselectedSb.length() - 1) == ',') {
        disselectedSb.deleteCharAt(disselectedSb.length() - 1);
      }
      disselectedSb.append("],");
      selectedSb.append("]}");
      StringBuffer sb = new StringBuffer(disselectedSb);
      sb.append(selectedSb);
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功设置桌面属性");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

  /**
   * 设置模块权限
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String setTypePrivConfig(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    int seqId = Integer.parseInt(request.getParameter("seqId"));
    String selectValue = request.getParameter("selectValue");
    //System.out.println(selectValue+"HKJH");
    T9Person user = (T9Person) request.getSession().getAttribute("LOGIN_USER");// 获得登陆用户
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      // selectValue属性为cancel时,代表用户未设置任何信息      T9ORM t = new T9ORM();
      Map m =new HashMap();
      if(!"cancel".equals(selectValue.trim())){
        if(seqId != 0){
          m.put("seqId", seqId);
        }
        m.put("typePriv", selectValue);
        if(seqId != 0){
          t.updateSingle(dbConn, "sms2Priv", m);
        }else{
          t.saveSingle(dbConn, "sms2Priv", m);
        }
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功设置桌面属性");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 短信接收管理列表
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String getReceiveSearchList(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      String phone = request.getParameter("phone");
      phone = T9DBUtility.escapeLike(phone);
      String content = request.getParameter("content");
      content = T9DBUtility.escapeLike(content);
      String beginDate = request.getParameter("beginDate");
      String endDate = request.getParameter("endDate");
      T9MobileSmsLogic dl = new T9MobileSmsLogic();
      String data = dl.getManagePersonList(dbConn,request.getParameterMap(), phone, content, beginDate, endDate);
      
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
   * 获取PERSON表中用户姓名和手机号是否公开的标记
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String getMobilNo3(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    String userName = "";
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      List<Map> list = new ArrayList();
      T9ORM orm = new T9ORM();
      HashMap map = null;
      String mobilNo = request.getParameter("mobilNo");
      StringBuffer sb = new StringBuffer("[");
      String[] filters = new String[]{"MOBIL_NO like '%" + mobilNo + "%'" + T9DBUtility.escapeLike()};
      List funcList = new ArrayList();
      funcList.add("person");
      map = (HashMap)orm.loadDataSingle(dbConn, funcList, filters);
      list.addAll((List<Map>) map.get("PERSON"));
      for(Map ms : list){
        sb.append("{");
        sb.append("userName:\"" + ms.get("userName") + "\"");
        sb.append(",mobilNoHidden:\"" + ms.get("mobilNoHidden") + "\"");
        sb.append("},");
      }
      if (sb.charAt(sb.length() - 1) == ',') {
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
   * 获取PERSON(人员)表中MOBIL_NO
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String getPersonPhone(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    String userName = "";
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String mobilNo = request.getParameter("mobilNo");
      List<Map> list = new ArrayList();
      T9ORM orm = new T9ORM();
      //String[] filters = new String[]{"MOBIL_NO like '%" + mobilNo + "%'"};
      String data = null;
      //List< T9Person> mobileNoList = orm.loadListSingle(dbConn, T9Person.class, filters);
      T9MobileSmsLogic msLogic = new T9MobileSmsLogic();
      List<T9Person> mobileNoList = msLogic.getMobileSmsFunc(dbConn, mobilNo);
      if(mobileNoList.size() == 0){
        data = "{userName:''}";
      }else{
        data = T9FOM.toJson(mobileNoList.get(0)).toString();
      }
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
  
  /**
   * 获得Address(通讯簿)表中的MOBIL_NO
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String getAddressPhone(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    String userName = "";
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String mobilNo = request.getParameter("mobilNo");
      List<Map> list = new ArrayList();
      T9ORM orm = new T9ORM();
      String data = null;
      //String[] filterAddress = new String[]{"MOBIL_NO like '%" + mobilNo + "%'"};
      //List< T9Address> addressList = orm.loadListSingle(dbConn, T9Address.class, filterAddress);
      T9MobileSmsLogic msLogic = new T9MobileSmsLogic();
      List<T9Address> addressList = msLogic.getAddressPsnName(dbConn, mobilNo);
      if (addressList.size() == 0) {
        data = "{psnName:''}";
      } else {
        data = T9FOM.toJson(addressList.get(0)).toString();
      }
      //System.out.println(data+"RTYUIdd");
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
  
  /**
   * 获取SMS3表中的phone
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String getSms3MobilNo(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    String userName = "";
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      List<Map> list = new ArrayList();
      int seqId = Integer.parseInt(request.getParameter("seqId"));
      T9ORM orm = new T9ORM();
      String data = null;
      String[] sms3Str = new String[]{"SEQ_ID="+seqId};
      List< T9Sms3> sms3List = orm.loadListSingle(dbConn, T9Sms3.class, sms3Str);
      if (sms3List.size() == 0) {
        data = "{phone:''}";
      } else {
        data = T9FOM.toJson(sms3List.get(0)).toString();
      }
      //System.out.println(data+"RTYUIdd");
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
  
  /**
   * 短信接收管理批量删除
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String deleteSelect(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String sumStrs = request.getParameter("sumStrs");
      T9MobileSmsLogic msl = new T9MobileSmsLogic();
      msl.deleteAll(dbConn, sumStrs);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功删除数据");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 短信接收管理当前页面删除
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String deleteReceiveManage(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      String phone = request.getParameter("phone");
      phone = T9DBUtility.escapeLike(phone);
      String content = request.getParameter("content");
      content = T9DBUtility.escapeLike(content);
      String beginDate = request.getParameter("beginDate");
      String endDate = request.getParameter("endDate");
      T9MobileSmsLogic dl = new T9MobileSmsLogic();
      dl.deleteReceiveManage(dbConn,request.getParameterMap(), phone, content, beginDate, endDate);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功删除数据");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

  /**
   * 短信接收管理列表
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String getSendSearchList(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      String sendFlag = request.getParameter("sendFlag");
      String phone = request.getParameter("phone");
      phone = T9DBUtility.escapeLike(phone);
      String user = request.getParameter("user");
      String content = request.getParameter("content");
      content = T9DBUtility.escapeLike(content);
      String beginDate = request.getParameter("beginDate");
      String endDate = request.getParameter("endDate");
      T9MobileSmsLogic dl = new T9MobileSmsLogic();
      String data = dl.getSendSearchList(dbConn,request.getParameterMap(), sendFlag, phone, content, beginDate, endDate, user);
      //System.out.println(data+"KLKLKLKLKL");
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
   * 获取发信人名称
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
      String userIdStr = request.getParameter("userId");
      int userId = Integer.parseInt(userIdStr);
      T9PersonLogic dl = new T9PersonLogic();
      String data = dl.getUserNameLogic(dbConn, userId);
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
   * 获取SMS2表中的phone
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String getSms2MobilNo(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    String userName = "";
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      List<Map> list = new ArrayList();
      int seqId = Integer.parseInt(request.getParameter("seqId"));
      T9ORM orm = new T9ORM();
      String data = null;
      //List< T9Sms2> sms3List = orm.loadListSingle(dbConn, T9Sms2.class, sms3Str);
      T9MobileSmsLogic msLogic = new T9MobileSmsLogic();
      List< T9Sms2> sms2List = msLogic.getSms2Phone(dbConn, seqId);
      if (sms2List.size() == 0) {
        data = "{phone:''}";
      } else {
        data = T9FOM.toJson(sms2List.get(0)).toString();
      }
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
  
  /**
   * 短信发送管理删除
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String deleteSendManage(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String user = request.getParameter("user");
      String phone = request.getParameter("phone");
      phone = T9DBUtility.escapeLike(phone);
      String content = request.getParameter("content");
      content = T9DBUtility.escapeLike(content);
      String beginDate = request.getParameter("beginDate");
      String endDate = request.getParameter("endDate");
      T9MobileSmsLogic dl = new T9MobileSmsLogic();
      dl.deleteSendManage(dbConn,request.getParameterMap(), phone, content, beginDate, endDate, user);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功删除数据");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 手机短信查询结果批量删除
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String deleteSelectSms2(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String sumStrs = request.getParameter("sumStrs");
      T9MobileSmsLogic msl = new T9MobileSmsLogic();
      msl.deleteSelectSms2(dbConn, sumStrs);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功删除数据");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 手机短信发送统计列表
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String getReportSearchList(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      String beginDate = request.getParameter("beginDate");
      String endDate = request.getParameter("endDate");
      T9MobileSmsLogic dl = new T9MobileSmsLogic();
      String data = dl.getReportSearchList(dbConn,request.getParameterMap(), beginDate, endDate);
      
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
  
  public String getReportDeptSearchList(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String beginDate = request.getParameter("beginDate");
      String endDate = request.getParameter("endDate");
      //System.out.println(beginDate);
      T9MobileSmsLogic dl = new T9MobileSmsLogic();
      String data = dl.getReportDeptSearchList1(dbConn, beginDate, endDate);
      //System.out.println(data+"NBHYGUHJKJKMN");
      //PrintWriter pw = response.getWriter();
      //pw.println(data);
      //pw.flush();
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 获取发送成功记录
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String getSendSuccess(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String fromId = request.getParameter("seqId");
      String beginDate = request.getParameter("beginDate");
      String endDate = request.getParameter("endDate");
      T9MobileSmsLogic msl = new T9MobileSmsLogic();
      String data = msl.getSendSuccess(dbConn, fromId, beginDate, endDate);
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
   * 获取未发送记录
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String getSendNo(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String fromId = request.getParameter("seqId");
      String beginDate = request.getParameter("beginDate");
      String endDate = request.getParameter("endDate");
      T9MobileSmsLogic msl = new T9MobileSmsLogic();
      String data = msl.getSendNo(dbConn, fromId, beginDate, endDate);
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
   * 获取发送超时记录
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String getsendTimeOut(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String fromId = request.getParameter("seqId");
      String beginDate = request.getParameter("beginDate");
      String endDate = request.getParameter("endDate");
      T9MobileSmsLogic msl = new T9MobileSmsLogic();
      String data = msl.getsendTimeOut(dbConn, fromId, beginDate, endDate);
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
   * 删除该用户的发送记录 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String deleteSendSign(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String sumStrs = request.getParameter("sumStrs");
      T9MobileSmsLogic msl = new T9MobileSmsLogic();
      msl.deleteSendSign(dbConn, sumStrs);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功删除数据");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String getDeptName(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqIdStr = request.getParameter("seqId");
      int seqId = Integer.parseInt(seqIdStr);
      String beginDate = request.getParameter("beginDate");
      String endDate = request.getParameter("endDate");
      T9MobileSmsLogic msl = new T9MobileSmsLogic();
      String data = msl.getDeptNameLogic(dbConn, seqId);
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
   * 导出到EXCEL表格中

   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String exportToExcel(HttpServletRequest request,HttpServletResponse response) throws Exception{
    OutputStream ops = null;
    Connection conn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      conn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      String fileName = URLEncoder.encode("手机短信接收记录.xls","UTF-8");
      fileName = fileName.replaceAll("\\+", "%20");
      response.setHeader("Cache-control","private");
      response.setContentType("application/vnd.ms-excel");
      response.setHeader("Accept-Ranges","bytes");
      response.setHeader("Cache-Control","maxage=3600");
      response.setHeader("Pragma","public");
      response.setHeader("Content-disposition","attachment; filename=\"" + fileName + "\"");
      ops = response.getOutputStream();
      T9MobileSmsLogic ieml = new T9MobileSmsLogic();
      ArrayList<T9DbRecord > dbL = ieml.toExportDeptData(conn);
      T9JExcelUtil.writeExc(ops, dbL);
    } catch (Exception ex) {
      ex.printStackTrace();
      throw ex;
    } finally {
      ops.close();
    }
    return null;
  }
  
  /**
   * 导出到EXCEL表格中   手机短信发送统计(按人员)


   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String exportToExcelSmsPerson(HttpServletRequest request,HttpServletResponse response) throws Exception{
    OutputStream ops = null;
    Connection conn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      conn = requestDbConn.getSysDbConn();
      String beginDate = request.getParameter("beginDate");
      String endDate = request.getParameter("endDate");
      T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      String fileName = URLEncoder.encode("手机短信发送统计(按人员).xls","UTF-8");
      fileName = fileName.replaceAll("\\+", "%20");
      response.setHeader("Cache-control","private");
      response.setContentType("application/vnd.ms-excel");
      response.setHeader("Accept-Ranges","bytes");
      response.setHeader("Cache-Control","maxage=3600");
      response.setHeader("Pragma","public");
      response.setHeader("Content-disposition","attachment; filename=\"" + fileName + "\"");
      ops = response.getOutputStream();
      T9MobileSmsLogic ieml = new T9MobileSmsLogic();
      ArrayList<T9DbRecord > dbL = ieml.exportToExcelSmsPerson(conn, beginDate, endDate);
      
      T9JExcelUtil.writeExc(ops, dbL);
    } catch (Exception ex) {
      ex.printStackTrace();
      throw ex;
    } finally {
      ops.close();
    }
    return null;
  }
  
  /**
   * 导出到EXCEL表格中   手机短信发送统计(按部门)


   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String exportToExcelSmsDept(HttpServletRequest request,HttpServletResponse response) throws Exception{
    OutputStream ops = null;
    Connection conn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      conn = requestDbConn.getSysDbConn();
      String beginDate = request.getParameter("beginDate");
      String endDate = request.getParameter("endDate");
      T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      String fileName = URLEncoder.encode("手机短信发送统计(按部门).xls","UTF-8");
      fileName = fileName.replaceAll("\\+", "%20");
      response.setHeader("Cache-control","private");
      response.setContentType("application/vnd.ms-excel");
      response.setHeader("Accept-Ranges","bytes");
      response.setHeader("Cache-Control","maxage=3600");
      response.setHeader("Pragma","public");
      response.setHeader("Content-disposition","attachment; filename=\"" + fileName + "\"");
      ops = response.getOutputStream();
      T9MobileSmsLogic ieml = new T9MobileSmsLogic();
      ArrayList<T9DbRecord > dbL = ieml.exportToExcelSmsDept(conn, beginDate, endDate);
      
      T9JExcelUtil.writeExc(ops, dbL);
    } catch (Exception ex) {
      ex.printStackTrace();
      throw ex;
    } finally {
      ops.close();
    }
    return null;
  }
}
