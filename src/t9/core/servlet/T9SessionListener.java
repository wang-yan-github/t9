package t9.core.servlet;

import java.sql.Connection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Random;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.system.logic.T9SystemLogic;
import t9.core.funcs.system.logic.T9SystemService;
import t9.core.funcs.system.syslog.logic.T9SysLogLogic;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9LogConst;
import t9.core.util.db.T9DBUtility;

/**
 * Session监听
 * 
 * @author yzq
 * 
 */
public class T9SessionListener implements HttpSessionListener {
    private static HashMap sessionContextMap = new HashMap();
    private static HashMap userStateMap = new HashMap();

    public static HashMap getSessaionContextMap() {
        return sessionContextMap;
    }

    public static HashMap getUserStateMap() {
        return userStateMap;
    }

    /**
     * session创建
     */
    public void sessionCreated(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        // 向session中添加6位随机数字
        Random random = new Random();
        int randomInt = random.nextInt(999999);
        while (randomInt < 100000) {
            randomInt = random.nextInt(999999);
        }
        session.setAttribute("RANDOM_NUMBER", Integer.valueOf(randomInt));
        sessionContextMap.put(session.getId(), session);
    }

    /**
     * session销毁
     */
    public void sessionDestroyed(HttpSessionEvent event) {
        HttpSession session = event.getSession();

        // HttpServletRequest request = (HttpServletRequest)
        // session.getAttribute(T9BeanKeys.CURR_REQUEST);
        Boolean isRequest = (Boolean) session.getAttribute(T9BeanKeys.CURR_REQUEST_FLAG);

        T9Person person = (T9Person) session.getAttribute("LOGIN_USER");

        // 当request不为null时,是用户点击注销销毁session
        if (isRequest != null) {
            // T9RequestDbConn requestDbConn = (T9RequestDbConn)
            // request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            T9SystemLogic logic = new T9SystemLogic();
            try {
                T9RequestDbConn requestDbConn = new T9RequestDbConn("");
                Connection dbConn = null;
                try {
                    dbConn = requestDbConn.getSysDbConn();
                    if (person != null) {
                        String currRequestAddress = (String) session
                                .getAttribute(T9BeanKeys.CURR_REQUEST_ADDRESS);
                        // 系统日志-退出登录
                        T9SysLogLogic.addSysLog(dbConn, T9LogConst.LOGOUT, "退出系统", person.getSeqId(),
                                currRequestAddress);
                        // 以sessionToken作为引索引,删除USER_ONLINE中信息
                        logic.deleteOnline(dbConn, String.valueOf(session.getAttribute("sessionToken")));
                    }
                    dbConn.commit();
                } catch (Exception ex) {
                    try {
                        dbConn.rollback();
                    } catch (Exception ex2) {
                    }
                    throw ex;
                } finally {
                    requestDbConn.closeAllDbConns();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // 当request为null时,session是自动过期
            try {
                if (person != null) {

                    // 以sessionToken作为引索引,删除USER_ONLINE中信息
                    String delSql = "delete" + " from USER_ONLINE" + " where SESSION_TOKEN = '"
                            + String.valueOf(session.getAttribute("sessionToken")) + "'";
                    synchronized (T9SystemService.onlineSync) {
                        T9DBUtility.executeUpdate(delSql);
                    }

                    String sql = "insert into SYS_LOG (USER_ID, TIME, IP, TYPE, REMARK)"
                            + " values(%d, %s,'%s','%s','%s')";

                    String insSql = String.format(sql, person.getSeqId(), T9DBUtility.currDateTime(),
                            session.getAttribute("LOGIN_IP"), T9LogConst.LOGOUT, "");

                    T9DBUtility.executeUpdate(insSql);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 处理在线时长
        try {
            if (person != null) {
                // 登陆的秒数

                long time = (new Date().getTime() - person.getLastVisitTime().getTime()) / 1000;

                long online = person.getOnLine();
                online += time;
                String sql = "update PERSON" + " set ON_LINE = " + online + " where SEQ_ID = "
                        + person.getSeqId();
                T9DBUtility.executeUpdate(sql);

                userStateMap.remove(person.getSeqId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Enumeration<String> en = session.getAttributeNames();
        while (en.hasMoreElements()) {
            session.removeAttribute((String) en.nextElement());
        }
        if (session != null) {
            sessionContextMap.remove(session.getId());
        }
    }
}
