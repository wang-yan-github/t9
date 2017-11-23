package t9.core.funcs.diary.act;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9DbRecord;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.diary.data.T9Diary;
import t9.core.funcs.diary.data.T9DiaryCont;
import t9.core.funcs.diary.data.T9DiaryLock;
import t9.core.funcs.diary.logic.T9DiaryLogic;
import t9.core.funcs.doc.util.T9WorkFlowUtility;
import t9.core.funcs.email.logic.T9InnerEMailLogic;
import t9.core.funcs.jexcel.util.T9JExcelUtil;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.workflow.logic.T9MoreOperateLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.db.T9ORM;
import t9.core.util.file.T9FileUploadForm;
import t9.core.util.form.T9FOM;
/**
 * 工作日志
 * @author TTlang
 *
 */
public class T9DiaryAct{
  private static Logger log = Logger.getLogger("t9.core.funcs.diary.act.T9DiaryAct");

  /**
   * 工作日志保存 ajax方式
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String saveByAjax(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      int userId = person.getSeqId();
      
      T9DiaryLogic dl = new T9DiaryLogic();
      dl.saveLogic(dbConn, userId, request.getParameterMap());
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功添加数据");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 工作日志保存 ajax方式
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String updateByAjax(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      int userId = person.getSeqId();
      
      T9DiaryLogic dl = new T9DiaryLogic();
      dl.updateLogic(dbConn, userId, request.getParameterMap());
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功添加数据");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 附件上传
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String uploadFile(HttpServletRequest request,
      HttpServletResponse response) throws Exception {

    T9FileUploadForm fileForm = new T9FileUploadForm();
    String data = "";
    try {
      fileForm.parseUploadRequest(request);
    } catch (Exception e) {
      data = "{type:1}";
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, data);
      return "/core/inc/rtuploadfile.jsp";
    }
    
    Map<String, String> attr = null;
    String attrId = (fileForm.getParameter("attachmentId")== null )? "":fileForm.getParameter("attachmentId");
    String attrName = (fileForm.getParameter("attachmentName")== null )? "":fileForm.getParameter("attachmentName");
    try{
      T9DiaryLogic dl = new T9DiaryLogic();
      attr = dl.fileUploadLogic(fileForm, T9DiaryCont.ATT_PATCH);
      Set<String> keys = attr.keySet();
      for (String key : keys){
        String value = attr.get(key);
        if(attrId != null && !"".equals(attrId)){
          if(!(attrId.trim()).endsWith(",")){
            attrId += ",";
          }
          if(!(attrName.trim()).endsWith("*")){
            attrName += "*";
          }
        }
        attrId += key + ",";
        attrName += value + "*";
      }
      data = "{type:0,attrId:\"" + attrId + "\",attrName:\"" + attrName + "\"}";
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "文件上传成功");
      request.setAttribute(T9ActionKeys.RET_DATA, data);

    } catch (Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "文件上传失败");
      throw e;
    }
    return "/core/inc/rtuploadfile.jsp";
  }
  /**
   * 列出当前用户最新的十条工作日志
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String lastTen(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      int userId = person.getSeqId();
      T9DiaryLogic dl = new T9DiaryLogic();
      List<T9Diary> diaryList = dl.getLastTenEntryByUserId(dbConn, userId,1);
      StringBuffer data = dl.toJson(dbConn, diaryList);
 
      //System.out.println("最近十条员工日志DIARY:" + data.toString());
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, data.toString());
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
/**
 * 取得指定时间的日志
 * @param request
 * @param response
 * @return
 * @throws Exception
 */
  public String listDiaryByDate(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      int userId = person.getSeqId();
      String dateStr = request.getParameter("DiaDateDiary");
      T9DiaryLogic dl = new T9DiaryLogic();
      List<T9Diary> diaryList = dl.getLastByDate(dbConn, userId, T9Utility.parseDate(dateStr));
      StringBuffer data = dl.toJson(dbConn, diaryList);
 
      //System.out.println("当前用户/当前时间DIARY:" + data.toString());
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, data.toString());
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 取得指定ID日志信息
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
    public String getDiaDiaryById(HttpServletRequest request,
        HttpServletResponse response) throws Exception {
      Connection dbConn = null;
      try {
        T9RequestDbConn requestDbConn = (T9RequestDbConn) request
            .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
        dbConn = requestDbConn.getSysDbConn();
        String idStr = request.getParameter("diaId");
        T9ORM orm = new T9ORM();
        T9Diary dia = (T9Diary) orm.loadObjSingle(dbConn, T9Diary.class, Integer.parseInt(idStr));
        T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
        if (dia.getUserId() != person.getSeqId() 
            && !T9WorkFlowUtility.findId(dia.getReaders(), String.valueOf(person.getSeqId()))) {
          String readers =T9Utility.null2Empty(dia.getReaders()) ;
          if ("".equals(readers) || readers.endsWith(",")) {
            readers += person.getSeqId() ;
          } else {
            readers += "," + person.getSeqId() ;
          }
          dia.setReaders(readers);
          orm.updateSingle(dbConn, dia);
        }
        StringBuffer dia2Json = T9FOM.toJson(dia);
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_DATA, dia2Json.toString());
      } catch (Exception ex) {
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
        throw ex;
      }
      return "/core/inc/rtjson.jsp";
    }
/**
 * 取得指定ID日志信息
 * @param request
 * @param response
 * @return
 * @throws Exception
 */
  public String deleteDia(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String idStr = request.getParameter("diaIds");
      
      T9DiaryLogic dl = new T9DiaryLogic();
      dl.deleteDiaryLogic(dbConn, idStr);
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
/**
 * 取得指定ID日志信息
 * @param request
 * @param response
 * @return
 * @throws Exception
 */
  public String isLock(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String dateStr = request.getParameter("date");
      
      T9DiaryLogic dl = new T9DiaryLogic();
      T9DiaryLock diaLock = dl.getLock(dbConn);
      String data = "0";
      boolean islock = diaLock == null ? false : diaLock.isLock(T9Utility.parseDate(dateStr));
      if(islock){
        data = "1";
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
   * 列出指定用户最新的十条工作日志
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String lastTenByUser(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String userIdStr = request.getParameter("userId");
      int userId = Integer.parseInt(userIdStr);
      T9DiaryLogic dl = new T9DiaryLogic();
      List<T9Diary> diaryList = dl.getLastTenEntryByUserId(dbConn, userId,2);
      StringBuffer data = dl.toJson(dbConn, diaryList);
 
      //System.out.println("指定用户最新的最近十条员工日志DIARY:" + data.toString());
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, data.toString());
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 用户管理范围的十篇员工日志
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String lastTenByAllUser(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      T9DiaryLogic dl = new T9DiaryLogic();
      String modeulId = T9DiaryCont.MODUEL_ID;
      int privNoFlag = T9DiaryCont.PRIV_NO_FLAG;
      List<T9Diary> diaryList = dl.getLastTenEntryBySer(dbConn, person,modeulId , privNoFlag , 10);
      StringBuffer data = dl.toJson(dbConn, diaryList);
 
      //System.out.println("用户管理范围的最近十条员工日志DIARY:" + data.toString());
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, data.toString());
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String desktopDiary(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      T9DiaryLogic dl = new T9DiaryLogic();
      String length = request.getParameter("length");
      int intLength = 10 ;
      if (!T9Utility.isNullorEmpty(length)) {
        intLength = Integer.parseInt(length);
      }
      String modeulId = T9DiaryCont.MODUEL_ID;
      int privNoFlag = T9DiaryCont.PRIV_NO_FLAG;
      List<T9Diary> diaryList = dl.getLastTenEntryBySer(dbConn, person,modeulId , privNoFlag , intLength);
      StringBuffer data = dl.toJson2(dbConn, diaryList,person);
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, data.toString());
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String desktopDiaryPriv(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      T9MoreOperateLogic logic = new T9MoreOperateLogic();
      List<String> priv = logic.getUserPriv(dbConn, person.getUserPriv() , person.getUserPrivOther());
      String str = "{";
      if (logic.hasModulePriv(priv, "0228")) {
        str += "my:1," ;
      } else {
        str += "my:0," ;
      }
      if (logic.hasModulePriv(priv, "0518")) {
        str += "priv:1" ;
      } else {
        str += "priv:0" ;
      }
      str += "}";
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, str);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String desktopMyDiary(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      T9DiaryLogic dl = new T9DiaryLogic();
      String length = request.getParameter("length");
      int intLength = 10 ;
      if (!T9Utility.isNullorEmpty(length)) {
        intLength = Integer.parseInt(length);
      }
      List<T9Diary> diaryList = dl.getMyDiary(dbConn, person, intLength);
      StringBuffer data = dl.toJson2(dbConn, diaryList , person);
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, data.toString());
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 取得当前用户已被评论的日志
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String listCommentedDiary(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      int userId = person.getSeqId();
      T9DiaryLogic dl = new T9DiaryLogic();
      String data = dl.getCommentDiary(dbConn, userId);
 
      //System.out.println("用户管理范围的最近十条员工日志DIARY:" + data);
      
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
   * 查询日志
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String searchDiary(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
//      String userIdStr = request.getParameter("userId");
//      int userId = Integer.parseInt(userIdStr);
      
      T9DiaryLogic dl = new T9DiaryLogic();
      String data = dl.toSearchData(dbConn,request.getParameterMap());
      
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
  public String searchDiarySelf(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
//      String userIdStr = request.getParameter("userId");
//      int userId = Integer.parseInt(userIdStr);
      T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      int userId = person.getSeqId();
      T9DiaryLogic dl = new T9DiaryLogic();
      String data = dl.toSearchData(dbConn,request.getParameterMap(),userId);
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
   * 查询（公共事务）员工日志查询
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String searchDiaryForInfo(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");

//      String userIdStr = request.getParameter("userId");
//      int userId = Integer.parseInt(userIdStr);
      
      T9DiaryLogic dl = new T9DiaryLogic();
      String data = dl.toSearchDataForInfo(dbConn, request.getParameterMap(),person,T9DiaryCont.MODUEL_ID,T9DiaryCont.PRIV_NO_FLAG);
      
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
   * 取得指定日志的共享范围
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getShare(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();

      String diaIdStr = request.getParameter("diaId");
      int diaId = Integer.parseInt(diaIdStr);
      T9DiaryLogic dl = new T9DiaryLogic();
      String data = dl.getShareLogic(dbConn, diaId);
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
   * 设置指定日志的共享范围
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String setShare(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();

      String diaIdStr = request.getParameter("diaId");
      String toId = request.getParameter("toId");
      int diaId = Integer.parseInt(diaIdStr);
      T9DiaryLogic dl = new T9DiaryLogic();
      dl.setShareLogic(dbConn, diaId, toId);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "共享范围设定成功！");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 取得指定日志的共享范围
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
      String data  = "";
      String userIdStr = request.getParameter("userId");
      if(userIdStr != null && !"".equals(userIdStr)){
      int userId = Integer.parseInt(userIdStr);
      T9DiaryLogic dl = new T9DiaryLogic();
       data = dl.getUserNameLogic(dbConn, userId);
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
  public String getDeptName(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();

      String deptIdStr = request.getParameter("deptId");
      int deptId = Integer.parseInt(deptIdStr);
      T9DiaryLogic dl = new T9DiaryLogic();
      String data = dl.getDeptNameLogic(dbConn, deptId);
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
   * 是否被评论
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String isComment(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();

      String diaIdStr = request.getParameter("diaId");
      int diaId = Integer.parseInt(diaIdStr);
      T9DiaryLogic dl = new T9DiaryLogic();
      int count =  dl.isCommentLogic(dbConn, diaId);
      String data = String.valueOf(count);
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
   * 列出所有的共享日志
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String listShareDiary(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      T9DiaryLogic dl = new T9DiaryLogic();
      String data = dl.getShareDiary(dbConn, person.getSeqId());
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, data );
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw  e;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 保存阅读了此日志的人员
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String reader(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      T9DiaryLogic dl = new T9DiaryLogic();

      String diaIdStr = request.getParameter("diaId");
      int diaId = Integer.parseInt(diaIdStr);
      dl.setReader(dbConn,person.getSeqId(), diaId);
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw  e;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 显示阅读人员
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String showReader(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      T9DiaryLogic dl = new T9DiaryLogic();

      String diaIdStr = request.getParameter("diaId");
      int diaId = Integer.parseInt(diaIdStr);
      String data = dl.showReader(dbConn, diaId);
      
      //System.out.println(data);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw  e;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 显示阅读人员
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String loadDiaryById(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9DiaryLogic dl = new T9DiaryLogic();
      String diaIds = request.getParameter("diaId");
      String data = dl.getDiaryById(dbConn, diaIds);
      //System.out.println(data);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw  e;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String getUserInfo(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      T9DiaryLogic dl = new T9DiaryLogic();
      String data = dl.getUserInFo(dbConn, person.getSeqId(), person.getUserName(), person.getUserPriv());
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw  e;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String exportExcel(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    OutputStream ops = null;
    Connection conn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      conn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      String fileName = URLEncoder.encode("工作日志.xls","UTF-8");
      fileName = fileName.replaceAll("\\+", "%20");
      response.setHeader("Cache-control","private");
      response.setContentType("application/vnd.ms-execl");
      response.setHeader("Accept-Ranges","bytes");
      response.setHeader("Cache-Control","maxage=3600");
      response.setHeader("Pragma","public");
      response.setHeader("Content-disposition","attachment; filename=\"" + fileName + "\"");
      ops = response.getOutputStream();
      T9DiaryLogic dial = new T9DiaryLogic();
      ArrayList<T9DbRecord > dbL = dial.toExportDiaData(conn,request.getParameterMap(),person.getSeqId());
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
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getSubject(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String diaIdStr = request.getParameter("diaId");
      int diaId = Integer.valueOf(diaIdStr);
      T9ORM orm = new T9ORM();
      T9Diary diary = (T9Diary) orm.loadObjSingle(dbConn, T9Diary.class, diaId);
      String data = T9Utility.encodeSpecial(diary.getSubject());
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, "\"" + data + "\"");
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw  e;
    }
    return "/core/inc/rtjson.jsp";
  }
}
