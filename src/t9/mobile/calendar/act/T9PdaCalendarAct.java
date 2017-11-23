package t9.mobile.calendar.act;

import java.sql.Connection;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.calendar.data.T9Calendar;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.mobile.calendar.logic.T9PdaCalendarLogic;
import t9.mobile.util.T9MobileString;
import t9.mobile.util.T9MobileUtility;

public class T9PdaCalendarAct {
    public String getCalendar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Connection dbConn = null;
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
            dbConn = requestDbConn.getSysDbConn();
            String ATYPE = request.getParameter("ATYPE");
            String A = request.getParameter("A");
            String data = "";
            /**
             * refreshList 接口 常规接口 提供查询等方法
             */
            if (ATYPE != null && "refreshList".equals(ATYPE)) {
                if (A != null && "loadList".equals(A)) {
                    String CUR_DATE = T9Utility.getDateTimeStr(new Date());
                    if (CUR_DATE == null || "".equals(CUR_DATE)) {
                        CUR_DATE = "0000-00-00";
                    }
                    String _strDate = CUR_DATE.substring(0, 10);
                    String _stime = _strDate + " " + "00:00:00";
                    String _etime = _strDate + " " + "23:59:59";
                    StringBuffer sb = new StringBuffer();
                    sb.append("SELECT * from CALENDAR where USER_ID='" + person.getSeqId() + "' and (");
                    sb.append(T9DBUtility.getDateFilter("CAL_TIME", _stime, ">="));
                    sb.append(") and (");
                    sb.append(T9DBUtility.getDateFilter("CAL_TIME", _etime, "<="));
                    sb.append(") order by SEQ_ID desc");
                    T9PdaCalendarLogic calendarLogic = new T9PdaCalendarLogic();
                    String sql2 = "SELECT * from AFFAIR where USER_ID='" + person.getSeqId() + "' and "
                            + T9DBUtility.getDateFilter("BEGIN_TIME", T9Utility.getCurDateTimeStr(), "<=")
                            + " order by REMIND_TIME";
                    data = calendarLogic.getCalendar(dbConn, sb.toString(), sql2);
                }
                //T9MobileUtility.output(response, data);
                T9MobileUtility.output(response, T9MobileUtility.getResultJson(1, null, data));
                return null;
            }
        } catch (Exception ex) {
            throw ex;
        }
        return null;
    }

    /**
     * 添加日程
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public String addCalendar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Connection dbConn = null;
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
            dbConn = requestDbConn.getSysDbConn();
            /**
             * 根据这个 id 来判断是修改 还是删除
             */
            String q_id = request.getParameter("q_id");
            T9PdaCalendarLogic tcl = new T9PdaCalendarLogic();

            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat sf1 = new SimpleDateFormat("yyyy-MM-dd");
            /**
             * 今天的日期
             */
            String nowDate = sf1.format(new Date());
            /**
             * 开始时间
             */
            Timestamp t = new Timestamp(new Date().getTime());
            Timestamp t2 = new Timestamp(new Date().getTime());
            String CAL_TIME = request.getParameter("cal_time");
            if (T9MobileString.isEmpty(CAL_TIME)) {
                t2 = t;
            } else {
                CAL_TIME = nowDate + " " + CAL_TIME + ":00";
                t2 = new Timestamp(sf.parse(CAL_TIME).getTime());
            }
            /**
             * 结束时间
             */
            String END_TIME = request.getParameter("end_time");

            if (T9MobileString.isEmpty(END_TIME)) {
                t = null;
            } else {
                END_TIME = nowDate + " " + END_TIME + ":00";
                t = new Timestamp(sf.parse(END_TIME).getTime());
            }

            /**
             * 类型
             */
            String cal_type = request.getParameter("cal_type");
            cal_type = T9MobileString.showNull(cal_type, "");
            /**
             * 等级
             */
            String cal_level = request.getParameter("cal_level");
            cal_level = T9MobileString.showNull(cal_level, "");
            /**
             * 内容
             */
            String CONTENT = request.getParameter("content");
            CONTENT = T9MobileString.showNull(CONTENT, "");
            /**
             * 这里如果 seq_id 不为空 说明是做更新操作
             */
            if (q_id != null && !"".equals(q_id)) {
                // 执行更新 操作
                T9Calendar tc = new T9Calendar();
                tc.setCalLevel(cal_level);
                tc.setCalType(cal_type);
                tc.setContent(CONTENT);
                tc.setUserId(String.valueOf(person.getSeqId()));
                tc.setOverStatus("0");
                tc.setManagerId("0");
                tc.setSeqId(Integer.parseInt(q_id));
                tcl.updateCalendar(response,dbConn, tc, t2, t);
            } else {
                T9Calendar tc = new T9Calendar();
                tc.setCalLevel(cal_level);
                tc.setCalType(cal_type);
                tc.setContent(CONTENT);
                tc.setUserId(String.valueOf(person.getSeqId()));
                tc.setOverStatus("0");
                tc.setManagerId("0");
                tcl.addCalendar(response,dbConn, tc, t2, t);
            }
        } catch (Exception ex) {
            throw ex;
        }

        return null;
    }

}
