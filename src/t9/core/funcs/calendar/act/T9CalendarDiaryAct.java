package t9.core.funcs.calendar.act;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.calendar.data.T9Calendar;
import t9.core.funcs.calendar.logic.T9CalendarDiaryLogic;
import t9.core.funcs.diary.data.T9Diary;
import t9.core.funcs.diary.logic.T9DiaryLogic;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.form.T9FOM;

public class T9CalendarDiaryAct {
  /**
   * 新建日程安排
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String addCalendarDiary(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn(); 
      T9Calendar calendar = new T9Calendar();
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = user.getSeqId();
      String calendarId = request.getParameter("calendarId");
      String diaryId = request.getParameter("diaryId");

      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功！");
      //request.setAttribute(T9ActionKeys.RET_DATA, "data");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
 /**
  * 查询此日程 相关联的所有日志
  * @param request
  * @param response
  * @return
  * @throws Exception
  */
  public String selectDiary(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn(); 
      String seqId = request.getParameter("seqId");
      String data = "[";
      T9CalendarDiaryLogic cdLogic = new T9CalendarDiaryLogic();
      T9DiaryLogic dl = new T9DiaryLogic();
      if(seqId!=null&&!seqId.equals("")){
        String diaryIds = cdLogic.selectDiaryId(dbConn, seqId);
        if(diaryIds!=null&&!diaryIds.equals("")){
          ArrayList<T9Diary> diaryList = dl.getDiaryListById(dbConn, diaryIds);
          for (int i = 0; i < diaryList.size(); i++) {
           data = data + T9FOM.toJson(diaryList.get(i))+",";
          }
          if(diaryList.size()>0){
            data = data.substring(0, data.length()-1);
          }
        }
      }
      data = data + "]";
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功！");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
/**
 * 判断今天是否写过日程日志，如果写了则查出来
 * @param request
 * @param response
 * @return
 * @throws Exception
 */
 public String selectDiaryByDate(HttpServletRequest request,
     HttpServletResponse response) throws Exception {
   Connection dbConn = null;
   try {
     T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
     dbConn = requestDbConn.getSysDbConn(); 
     String data = "[";
     String date = request.getParameter("date");
     T9CalendarDiaryLogic cdLogic = new T9CalendarDiaryLogic();
     T9DiaryLogic dl = new T9DiaryLogic();
     if(date!=null&&!date.equals("")){
       String diaryIds = cdLogic.selectDiaryIdByDate(dbConn,date);
       if(diaryIds!=null&&!diaryIds.equals("")){
         ArrayList<T9Diary> diaryList = dl.getDiaryListById(dbConn, diaryIds);
         for (int i = 0; i < diaryList.size(); i++) {
          data = data + T9FOM.toJson(diaryList.get(i))+",";
         }
         if(diaryList.size()>0){
           data = data.substring(0, data.length()-1);
         }
       }
      
     }
     data = data + "]";
     request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
     request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功！");
     request.setAttribute(T9ActionKeys.RET_DATA, data);
   }catch(Exception ex) {
     request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
     request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
     throw ex;
   }
   return "/core/inc/rtjson.jsp";
 }
}
