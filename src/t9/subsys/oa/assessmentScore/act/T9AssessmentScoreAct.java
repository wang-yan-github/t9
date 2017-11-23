package t9.subsys.oa.assessmentScore.act;

import java.math.BigDecimal;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.subsys.oa.assessmentScore.logic.T9AssessmentScoreLogic;
import t9.subsys.oa.coefficient.logic.T9CoefficientLogic;

public class T9AssessmentScoreAct {
  public static final String attachmentFolder = "assessmentScore";
  private T9AssessmentScoreLogic logic = new T9AssessmentScoreLogic();
  private T9CoefficientLogic cof = new T9CoefficientLogic();
  
  public String getMonthScore(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      String userId = request.getParameter("userId");
      String year = request.getParameter("year");
      String month = request.getParameter("month");
      StringBuffer sb = new StringBuffer();
      Date date = new Date();
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      String dateStr = dateFormat.format(date);
      int yearNow = Integer.parseInt(dateStr.substring(0,4));
      int monthNow = Integer.parseInt(dateStr.substring(5,7));
      if(yearNow >= Integer.parseInt(year)){
        double reduceAttendScore = 0;
        double directorScore = 0;
        double staffScore = 0;
        double monthScore = 0;
        if(monthNow >= Integer.parseInt(month)){
          reduceAttendScore = this.logic.getAttendScore(dbConn, year, month, userId, person);  //获取月考勤分数
          directorScore = this.logic.getDirectorScore(dbConn, year, month, userId);            //处长月考核分数
          staffScore = this.logic.getMonthScoreLogic(dbConn, year, month, userId);             //月奖惩分
          monthScore = this.logic.getMonthTotalScore(dbConn, year, month, reduceAttendScore, staffScore, directorScore);  //月考核分
          sb.append("{");
          sb.append("monthScore:\"" +  monthScore + "\"");
          sb.append(",directorScore:\"" + directorScore+ "\"");
          sb.append(",attendScore:\"" + reduceAttendScore + "\"");
          sb.append(",staffScore:\"" + staffScore + "\"");
          sb.append("},");
        }else{
          sb.append("{");
          sb.append("monthScore:\"" +  0.0 + "\"");
          sb.append(",directorScore:\"" + 0.0 + "\"");
          sb.append(",attendScore:\"" + 0.0 + "\"");
          sb.append(",staffScore:\"" + 0.0 + "\"");
          sb.append("},");
        }
      }else{
          sb.append("{");
          sb.append("monthScore:\"" + 0.0 + "\"");
          sb.append(",directorScore:\"" + 0.0 + "\"");
          sb.append(",attendScore:\"" + 0.0 + "\"");
          sb.append(",staffScore:\"" + 0.0 + "\"");
          sb.append("},");
      }
      sb.deleteCharAt(sb.length() - 1);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功添加"); 
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String getAttendScore(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      String userId = request.getParameter("userId");
      String year = request.getParameter("year");
      String month = request.getParameter("month");
      StringBuffer sb = new StringBuffer();
      Date date = new Date();
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      String dateStr = dateFormat.format(date);
      int yearNow = Integer.parseInt(dateStr.substring(0,4));
      int monthNow = Integer.parseInt(dateStr.substring(5,7));
      if(yearNow >= Integer.parseInt(year)){
        double reduceAttendScore = 0;
        double directorScore = 0;
        double staffScore = 0;
        double monthScore = 0;
        if(monthNow >= Integer.parseInt(month)){
          reduceAttendScore = this.logic.getAttendScore(dbConn, year, month, userId, person);  //获取月考勤分数
          sb.append("{");
          sb.append("monthScore:\"" +  monthScore + "\"");
          sb.append(",directorScore:\"" + directorScore+ "\"");
          sb.append(",attendScore:\"" + reduceAttendScore + "\"");
          sb.append(",staffScore:\"" + staffScore + "\"");
          sb.append("},");
        }else{
          sb.append("{");
          sb.append("monthScore:\"" +  0.0 + "\"");
          sb.append(",directorScore:\"" + 0.0 + "\"");
          sb.append(",attendScore:\"" + 0.0 + "\"");
          sb.append(",staffScore:\"" + 0.0 + "\"");
          sb.append("},");
        }
      }else{
          sb.append("{");
          sb.append("monthScore:\"" + 0.0 + "\"");
          sb.append(",directorScore:\"" + 0.0 + "\"");
          sb.append(",attendScore:\"" + 0.0 + "\"");
          sb.append(",staffScore:\"" + 0.0 + "\"");
          sb.append("},");
      }
      sb.deleteCharAt(sb.length() - 1);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功添加"); 
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  
  
  public String getYearScore(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      String userId = request.getParameter("userId");
      String year = request.getParameter("year");
      String month = request.getParameter("month");
      StringBuffer sb = new StringBuffer();
      Date date = new Date();
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      String dateStr = dateFormat.format(date);
      int yearNow = Integer.parseInt(dateStr.substring(0,4));
      if(yearNow >= Integer.parseInt(year)){
        double chiefCof = cof.getChiefScoreLogic(dbConn);  //处长主关分系数        double monthCof = cof.getMonthScoreLogic(dbConn);
        double yearCof = cof.getYearScoreLogic(dbConn);
        double awardCof = cof.getAwardScoreLogic(dbConn);
        double directorScore = this.logic.getDirectorYearScore(dbConn, year, month, userId);            //年终处长考核分数
        double monthTotalScore = this.logic.getYearTotalScore(dbConn, year, userId, person);            //12个月的月考核总分
        double staffScore = this.logic.getStaffYearScoreLogic(dbConn, year, userId);                    //年奖惩分
        double noCheckMonth = this.logic.getNoCheckInfo(dbConn, year);                                  //参加考核的月份        BigDecimal avg = T9Utility.divide(monthTotalScore, noCheckMonth, 2);
        double yearScore = T9Utility.parseDouble(avg)*monthCof + directorScore*yearCof + staffScore;    //年终考核总分
        sb.append("{");
        sb.append("yearScore:\"" + yearScore + "\"");
        sb.append(",monthScoreAvg:\"" + monthTotalScore/noCheckMonth + "\"");
        sb.append(",directorScore:\"" + directorScore + "\"");
        sb.append(",staffScore:\"" + staffScore + "\"");
        sb.append("},");
      }else{
        sb.append("{");
        sb.append("yearScore:\"" + 0.0 + "\"");
        sb.append(",monthScoreAvg:\"" + 0.0 + "\"");
        sb.append(",directorScore:\"" + 0.0 + "\"");
        sb.append(",staffScore:\"" + 0.0 + "\"");
        sb.append("},");
      }
      sb.deleteCharAt(sb.length() - 1);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功添加"); 
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

}
