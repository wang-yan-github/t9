package t9.core.funcs.deptnotify.act;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.deptnotify.data.T9DeptNotify;
import t9.core.funcs.deptnotify.logic.T9NotifyAuditingLogic;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.workflow.util.T9WorkFlowUtility;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.db.T9ORM;

public class T9NotifyAuditingAct {
    private static Logger log = Logger.getLogger("t9.core.funcs.dept.act");

    /**
     * 待审批的公告
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public String getUnAuditedList(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        Connection dbConn = null;
        Statement st = null;
        ResultSet rs = null;
        T9DeptNotify notify = null;
        String data = "";
        int notifyAuditingSingle = 0;
        String type = request.getParameter("type");// 下拉框中类型
        String ascDesc = request.getParameter("ascDesc");// 升序还是降序
        String field = request.getParameter("field");// 排序的字段

        String showLenStr = request.getParameter("showLength");// 每页显示长度
        String pageIndexStr = request.getParameter("pageIndex");// 页码数

        if ("".equals(ascDesc)) {
            ascDesc = "1";
        }
        T9Person person = (T9Person) request.getSession().getAttribute(
                "LOGIN_USER");
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            dbConn = requestDbConn.getSysDbConn();
            String queryPapaSql = "select PARA_VALUE from SYS_PARA where PARA_NAME='NOTIFY_AUDITING_SINGLE'";
            st = dbConn.createStatement();
            rs = st.executeQuery(queryPapaSql);
            if (rs.next()) {
                String papaValue = rs.getString("PARA_VALUE");
                if (!"1".equals(papaValue)) {
                    request.setAttribute(T9ActionKeys.RET_STATE,
                            T9Const.RETURN_ERROR);
                    request.setAttribute(T9ActionKeys.RET_MSRG,
                            "请在系统管理中设置审批参数!");
                    return "/core/inc/rtjson.jsp";
                } else {
                    String queryAuditerSql = "select PARA_VALUE from SYS_PARA where PARA_NAME='NOTIFY_AUDITING_ALL'";
                    Statement stmt = null;
                    ResultSet rss = null;
                    stmt = dbConn.createStatement();
                    rss = stmt.executeQuery(queryAuditerSql);
                    if (rss.next()) {
                        String papaValues = rss.getString(1);
                        if (papaValues != null && !"".equals(papaValues)) {
                            String[] papaValuess = papaValues.split(",");
                            for (int j = 0; j < papaValuess.length; j++) {
                                String papaValuetemp = papaValuess[j];
                                if (papaValuetemp.equals(Integer
                                        .toString(person.getSeqId()))) {
                                    notifyAuditingSingle = 1;
                                    break;
                                }
                            }
                        }

                    }
                }
            }

            if (notifyAuditingSingle == 1) {
                T9NotifyAuditingLogic notifyAuditingLogic = new T9NotifyAuditingLogic();
                data = notifyAuditingLogic.getUnAuditedList(dbConn, person,
                        type, ascDesc, field, Integer.parseInt(showLenStr),
                        Integer.parseInt(pageIndexStr));
                request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
                request.setAttribute(T9ActionKeys.RET_MSRG, "成功取出数据");
                request.setAttribute(T9ActionKeys.RET_DATA, data);
            } else {
                request.setAttribute(T9ActionKeys.RET_STATE,
                        T9Const.RETURN_ERROR);
                request.setAttribute(T9ActionKeys.RET_MSRG, "您没有审批权限！");
                return "/core/inc/rtjson.jsp";
            }
        } catch (Exception ex) {
            String message = T9WorkFlowUtility.Message(ex.getMessage(), 1);
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
            request.setAttribute(T9ActionKeys.RET_MSRG, message);
            throw ex;
        }
        return "/core/inc/rtjson.jsp";
    }

    // 批准
    public String beforeAuditingPass(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        Connection dbConn = null;
        String seqId = request.getParameter("seqId");
        T9Person person = (T9Person) request.getSession().getAttribute(
                "LOGIN_USER");
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            dbConn = requestDbConn.getSysDbConn();
            String data = "";
            T9NotifyAuditingLogic notifyAuditingLogic = new T9NotifyAuditingLogic();
            data = notifyAuditingLogic.beforeAuditingPass(dbConn, seqId);
            // T9Out.println("&&&&&&&"+data);
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
            request.setAttribute(T9ActionKeys.RET_MSRG, "成功取出数据");
            request.setAttribute(T9ActionKeys.RET_DATA, data);
        } catch (Exception ex) {
            String message = T9WorkFlowUtility.Message(ex.getMessage(), 1);
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
            request.setAttribute(T9ActionKeys.RET_MSRG, message);
            throw ex;
        }
        return "/core/inc/rtjson.jsp";
    }

    public String operation(HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        T9NotifyAuditingLogic notifyAuditingLogic = new T9NotifyAuditingLogic();
        String seqId = request.getParameter("seqId");// seqId
        String reason = request.getParameter("reason");//
        String top = request.getParameter("top");
        String topDays = request.getParameter("topDays");
        String sendTime = request.getParameter("sendTime");
        String beginDate = request.getParameter("beginDate");
        String endDate = request.getParameter("endDate");
        String operation = request.getParameter("operation");// 操作
        String mailRemind = request.getParameter("mailRemind");// 操作
        // T9Out.println(mailRemind);

        T9Person loginUser = null;
        Connection dbConn = null;
        T9ORM orm = new T9ORM();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        loginUser = (T9Person) request.getSession().getAttribute("LOGIN_USER");
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            dbConn = requestDbConn.getSysDbConn();
            T9DeptNotify notify = (T9DeptNotify) orm.loadObjSingle(dbConn,
                    T9DeptNotify.class, Integer.parseInt(seqId));
            if (!"".equals(reason) && reason != null) {
                notify.setReason(reason);
            }
            if (!"".equals(top) && top != null) {
                notify.setTop(top);
            }
            if (!"".equals(topDays) && topDays != null) {
                notify.setTopDays(Integer.parseInt(topDays));
            }
            if (!"".equals(sendTime) && sendTime != null) {
                notify.setSendTime(sdf1.parse(sendTime));
            }
            if (!"".equals(beginDate) && beginDate != null) {
                notify.setBeginDate(sdf.parse(beginDate));
            }
            if (!"".equals(endDate) && endDate != null) {
                notify.setEndDate(sdf.parse(endDate));
            }
            notifyAuditingLogic.operation(dbConn, loginUser, notify, operation,
                    mailRemind);

            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
            request.setAttribute(T9ActionKeys.RET_MSRG, "终止生效状态已修改");
        } catch (Exception ex) {
            String message = T9WorkFlowUtility.Message(ex.getMessage(), 1);
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
            request.setAttribute(T9ActionKeys.RET_MSRG, message);
            throw ex;
        }
        // return "/core/funcs/dept/deptinput.jsp";
        // ?deptParentDesc=+deptParentDesc
        return "/core/funcs/deptnotify/auditing/unaudited.jsp";
    }

    // 已审批公告列表

    public String getAuditedList(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        Connection dbConn = null;
        Statement st = null;
        ResultSet rs = null;
        T9DeptNotify notify = null;
        String data = "";
        int notifyAuditingSingle = 0;
        String type = request.getParameter("type");// 下拉框中类型
        String ascDesc = request.getParameter("ascDesc");// 升序还是降序
        String field = request.getParameter("field");// 排序的字段

        String showLenStr = request.getParameter("showLength");// 每页显示长度
        String pageIndexStr = request.getParameter("pageIndex");// 页码数

        if ("".equals(ascDesc)) {
            ascDesc = "1";
        }
        T9Person person = (T9Person) request.getSession().getAttribute(
                "LOGIN_USER");
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            dbConn = requestDbConn.getSysDbConn();

            T9NotifyAuditingLogic notifyAuditingLogic = new T9NotifyAuditingLogic();
            data = notifyAuditingLogic.getAuditedList(dbConn, person, type,
                    ascDesc, field, Integer.parseInt(showLenStr),
                    Integer.parseInt(pageIndexStr));
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
            request.setAttribute(T9ActionKeys.RET_MSRG, "成功取出数据");
            request.setAttribute(T9ActionKeys.RET_DATA, data);

        } catch (Exception ex) {
            String message = T9WorkFlowUtility.Message(ex.getMessage(), 1);
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
            request.setAttribute(T9ActionKeys.RET_MSRG, message);
            throw ex;
        }
        return "/core/inc/rtjson.jsp";
    }
}
