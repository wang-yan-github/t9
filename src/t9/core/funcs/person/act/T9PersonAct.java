package t9.core.funcs.person.act;

import java.io.File;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import t9.core.data.T9DbRecord;
import t9.core.data.T9RequestDbConn;
import t9.core.exps.T9InvalidParamException;
import t9.core.funcs.dept.data.T9Department;
import t9.core.funcs.dept.logic.T9DeptLogic;
import t9.core.funcs.diary.logic.T9MyPriv;
import t9.core.funcs.diary.logic.T9PrivUtil;
import t9.core.funcs.jexcel.util.T9CSVUtil;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.person.data.T9UserPriv;
import t9.core.funcs.person.logic.T9PersonLogic;
import t9.core.funcs.setdescktop.mypriv.logic.T9MyprivLogic;
import t9.core.funcs.system.ispirit.n12.org.act.T9IsPiritOrgAct;
import t9.core.funcs.system.syslog.logic.T9SysLogLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.global.T9LogConst;
import t9.core.global.T9SysProps;
import t9.core.module.oa.logic.T9OaSyncLogic;
import t9.core.module.org_select.logic.T9OrgSelectLogic;
import t9.core.module.report.logic.T9PersonSyncLogic;
import t9.core.module.report.logic.T9ReportSyncLogic;
import t9.core.servlet.T9SessionListener;
import t9.core.util.SignProvider;
import t9.core.util.T9Utility;
import t9.core.util.auth.T9PassEncrypt;
import t9.core.util.auth.T9RegistUtility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.file.T9FileUploadForm;
import t9.core.util.form.T9FOM;

public class T9PersonAct {
    T9PersonLogic personLogic = new T9PersonLogic();

    /**
     * 系统日志：获取部门DEPT_ID,DEPT_NAME,DEPT_PARENT
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public String getPersonLog(Connection dbConn, int deptId) throws Exception {
        String data = "";
        T9DeptLogic dl = new T9DeptLogic();
        data = dl.getDeptNameLogic(dbConn, deptId);
        return data;
    }

    /**
     * 获取新建部门SEQ_ID（用于系统日志）
     * 
     * @param dbConn
     * @param deptId
     * @return
     * @throws Exception
     */

    public String getPersonAddSeq(Connection dbConn) throws Exception {
        String data = "";
        T9DeptLogic dl = new T9DeptLogic();
        data = dl.getDeptAddSeqLogic(dbConn);
        return data;
    }

    /**
     * 判断是否注册
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     *             data = 1 注册， 0 未注册
     */

    public String getRegistOrg(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            // 软件是否已经注册
            String webInfoPath = T9SysProps.getWebInfPath();
            String keyPath = webInfoPath + File.separator + "config";
            String data = "";
            // if(T9Utility.isNullorEmpty(T9RegistProps.getProp(T9AuthKeys.REGIST_ORG
            // + ".t9"))){
            if (!SignProvider.verify(keyPath, "publicKey.dat", "license.dat")) {
                data = "0";
            } else {
                data = "1";
            }
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
            request.setAttribute(T9ActionKeys.RET_DATA, "\"" + data + "\"");
        } catch (Exception ex) {
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
            request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
            throw ex;
        }
        return "/core/inc/rtjson.jsp";
    }

    /**
     * 获取系统用户数量，并判断是否能新建用户
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public String getRegistNum(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Connection dbConn = null;
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            dbConn = requestDbConn.getSysDbConn();
            T9PersonLogic pl = new T9PersonLogic();
            String data = "";
            int permitUserCnt = T9RegistUtility.getUserCnt();
            if (pl.getNotLoginNum(dbConn) < permitUserCnt) {
                data = "1";
            } else {
                data = "0";
            }
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
            request.setAttribute(T9ActionKeys.RET_DATA, "\"" + data + "\"");
        } catch (Exception ex) {
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
            request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
            throw ex;
        }
        return "/core/inc/rtjson.jsp";
    }

    /**
     * 非注册版，控制30用户
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public String getNoRegistNum(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Connection dbConn = null;
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            dbConn = requestDbConn.getSysDbConn();
            T9PersonLogic pl = new T9PersonLogic();
            String data = "";
            int permitUserCnt = T9RegistUtility.getUserCnt();

            if (pl.getNotLoginNum(dbConn) < permitUserCnt) {
                data = "1";
            } else {
                data = "0";
            }
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
            request.setAttribute(T9ActionKeys.RET_DATA, "\"" + data + "\"");
        } catch (Exception ex) {
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
            request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
            throw ex;
        }
        return "/core/inc/rtjson.jsp";
    }

    /**
     * 新建用户
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */

    public String addPerson(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Connection dbConn = null;
        int num = 0;
        Map map = new HashMap();
        String userId = "";
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            dbConn = requestDbConn.getSysDbConn();
            String sql = "insert into person (";
            String values = " (";
            T9Person person = (T9Person) request.getSession().getAttribute("LOGIN_USER");
            String openedAutoSelect = request.getParameter("openedAutoSelect");
            int deptId = 0;
            String userName = "";
            String password = request.getParameter("password");
            if (T9Utility.isNullorEmpty(password)) {
                password = "";
            }
            String sex = request.getParameter("sex");
            if (T9Utility.isNullorEmpty(sex)) {
                sex = "0";
            }
            String emailCapacitys = request.getParameter("emailCapacity");
            int emailCapacity = 0;

            if (T9Utility.isNullorEmpty(emailCapacitys)) {
                emailCapacity = 100;
            } else if (T9Utility.isInteger(request.getParameter("emailCapacity"))) {
                emailCapacity = Integer.parseInt(request.getParameter("emailCapacity"));
            }
            String folderCapacity = request.getParameter("folderCapacity");
            if (!T9Utility.isInteger(folderCapacity)) {
                folderCapacity = "100";
            }
            if (openedAutoSelect.trim().equals("1")) {
                userId = request.getParameter("userId");
                userId = userId.replace("\\", "").replace("\"", "").replace("\'", "").replace("\r", "")
                        .replace("\n", "");
                userName = request.getParameter("userName");
                userName = userName.replace("\\", "").replace("\"", "").replace("\'", "").replace("\r", "")
                        .replace("\n", "");
                String userPriv = request.getParameter("userPriv");
                String userPrivOther = request.getParameter("role");
                String postPriv = request.getParameter("postPriv");
                String postDept = request.getParameter("postDeptId");
                String deptIdOther = request.getParameter("dept");
                int dutyType = 0;
                try {
                    dutyType = Integer.parseInt(request.getParameter("dutyType"));
                } catch (Exception ex) {
                }
                int deptIdLoca = 0;
                try {
                    deptIdLoca = Integer.parseInt(request.getParameter("deptIdLoca"));
                } catch (Exception ex) {
                }

                if (request.getParameter("deptId").equals("")) {
                    deptId = deptIdLoca;
                } else {
                    deptId = Integer.parseInt(request.getParameter("deptId"));
                }
                num = personLogic.selectPerson(dbConn, userId);
                if (num >= 1) {
                    request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
                    request.setAttribute(T9ActionKeys.RET_MSRG, "用户名重复, 用户名不能重复");
                    return "/core/inc/rtjson.jsp";
                }

                map.put("userId", userId);
                map.put("userName", userName);
                map.put("userPriv", userPriv);
                map.put("postPriv", postPriv);
                map.put("deptId", deptId);
                map.put("postDept", postDept);
                map.put("userPrivOther", userPrivOther);
                map.put("deptIdOther", deptIdOther);
                map.put("dutyType", dutyType);
            }
            String openedFlowDispatch = request.getParameter("openedFlowDispatch");
            if (openedFlowDispatch.trim().equals("1")) {
                String userNoStr = request.getParameter("userNo");
                int userNo = 10;
                if (T9Utility.isNullorEmpty(userNoStr)) {
                    userNo = 10;
                } else if (T9Utility.isInteger(userNoStr)) {
                    userNo = Integer.parseInt(request.getParameter("userNo"));
                }
                // int canbroadcast =
                // Integer.parseInt(request.getParameter("canbroadcast"));
                String notLogin = request.getParameter("notLogin");
                if (T9Utility.isNullorEmpty(notLogin)) {
                    notLogin = "0";
                } else {
                    notLogin = "1";
                }
                String notViewUser = request.getParameter("notViewUser");
                if (T9Utility.isNullorEmpty(notViewUser)) {
                    notViewUser = "0";
                } else {
                    notViewUser = "1";
                }
                String notViewTable = request.getParameter("notViewTable");
                if (T9Utility.isNullorEmpty(notViewTable)) {
                    notViewTable = "0";
                } else {
                    notViewTable = "1";
                }
                String useingKey = request.getParameter("useingKey");
                if (T9Utility.isNullorEmpty(useingKey)) {
                    useingKey = "0";
                } else {
                    useingKey = "1";
                }
                map.put("notLogin", notLogin);
                map.put("notViewUser", notViewUser);
                map.put("notViewTable", notViewTable);
                map.put("useingKey", useingKey);
                map.put("userNo", userNo);
                int canbroadcast = Integer.parseInt(request.getParameter("canbroadcast"));
                map.put("canbroadcast", canbroadcast);
                int imRange = Integer.parseInt(request.getParameter("imRange"));
                map.put("imRange", imRange);
            } else {
                map.put("notLogin", "0");
                map.put("notViewUser", "0");
                map.put("notViewTable", "0");
                map.put("useingKey", "0");
            }

            String openedWarnDispatch = request.getParameter("openedWarnDispatch");
            if (openedWarnDispatch.trim().equals("1")) {
                String webmailNums = request.getParameter("webmailNum");
                int webmailNum = 0;
                if (T9Utility.isNullorEmpty(webmailNums)) {
                    webmailNum = 0;
                } else if (T9Utility.isInteger(webmailNums)) {
                    webmailNum = Integer.parseInt(request.getParameter("webmailNum"));
                }
                String webmailCapacitys = request.getParameter("webmailCapacity");
                int webmailCapacity = 0;
                if (T9Utility.isNullorEmpty(webmailCapacitys)) {
                    webmailCapacity = 0;
                } else if (T9Utility.isInteger(webmailCapacitys)) {
                    webmailCapacity = Integer.parseInt(request.getParameter("webmailCapacity"));
                }
                String bindIp = request.getParameter("bindIp");
                String remark = request.getParameter("remark");

                map.put("webmailNum", webmailNum);
                map.put("webmailCapacity", webmailCapacity);
                map.put("bindIp", bindIp);
                map.put("remark", remark);
            }

            String openedOtherDispatch = request.getParameter("openedOtherDispatch");
            if (openedOtherDispatch.trim().equals("1")) {
                String byname = request.getParameter("byname");
                if (!T9Utility.isNullorEmpty(byname)) {
                    byname = byname.replace("\\", "\\\\").replace("\"", "").replace("\'", "")
                            .replace("\r", "").replace("\n", "");
                }
                String birthday = request.getParameter("birthday");
                String theme = request.getParameter("theme");
                String mobilNo = request.getParameter("mobilNo");
                String mobilNoHidden = request.getParameter("mobilNoHidden");
                if (T9Utility.isNullorEmpty(mobilNoHidden)) {
                    mobilNoHidden = "0";
                } else {
                    mobilNoHidden = "1";
                }
                String email = request.getParameter("email");
                String telNoDept = request.getParameter("telNoDept");

                map.put("byname", byname);
                map.put("birthday", birthday);
                map.put("theme", theme);
                map.put("mobilNo", mobilNo);
                map.put("mobilNoHidden", mobilNoHidden);
                map.put("email", email);
                map.put("telNoDept", telNoDept);
            }
            map.put("sex", sex);
            map.put("password", T9PassEncrypt.encryptPass(password));
            map.put("emailCapacity", emailCapacity);
            map.put("folderCapacity", folderCapacity);
            map.put("auatar", "default.gif");
            map.put("shortcut", "0204,020802,0216,0217,0224,0228,0232,0220,");
            T9ORM orm = new T9ORM();
            orm.saveSingle(dbConn, "person", map);

            String deptName = personLogic.getDeptNameLogic(dbConn, deptId);
            String remark = "[" + deptName + "]" + userName + ",USER_ID=" + userId;
            T9SysLogLogic.addSysLog(dbConn, T9LogConst.ADD_USER, remark, person.getSeqId(),
                    request.getRemoteAddr());

            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
            request.setAttribute(T9ActionKeys.RET_MSRG, "成功添加人员");

            // 生成org.xml文件
            T9IsPiritOrgAct.getOrgDataStream(dbConn);
            if (T9ReportSyncLogic.hasSync) {

                int max = T9ReportSyncLogic.getMax(dbConn, "select max(SEQ_ID) FROM PERSON");

                T9Person o = (T9Person) orm.loadObjSingle(dbConn, T9Person.class, max);
                if (o != null) {
                    Connection reportConn = T9ReportSyncLogic.getReportConn();
                    T9PersonSyncLogic logic = new T9PersonSyncLogic();
                    logic.addPerson(o, reportConn);
                    if (reportConn != null) {
                        reportConn.close();
                    }
                }
            }
            if (T9OaSyncLogic.hasSync) {
                int max = T9ReportSyncLogic.getMax(dbConn, "select max(SEQ_ID) FROM PERSON");
                T9Person o = (T9Person) orm.loadObjSingle(dbConn, T9Person.class, max);
                if (o != null) {
                    Connection oaConn = T9OaSyncLogic.getOAConn();
                    t9.core.module.oa.logic.T9PersonSyncLogic logic = new t9.core.module.oa.logic.T9PersonSyncLogic();
                    logic.addPerson(o, oaConn);
                    if (oaConn != null) {
                        oaConn.close();
                    }
                }
            }
        } catch (Exception ex) {
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
            request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
            throw ex;
        }
        return "/core/inc/rtjson.jsp";
    }

    /**
     * 编辑用户
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */

    public String updatePerson(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Connection dbConn = null;
        try {
            T9Person login = (T9Person) request.getSession().getAttribute("LOGIN_USER");
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            dbConn = requestDbConn.getSysDbConn();
            Map map = new HashMap();
            String userId = "";
            String userName = "";
            int seqId = Integer.parseInt(request.getParameter("seqId"));
            int deptId = 0;
            String openedAutoSelect = request.getParameter("openedAutoSelect");
            if (openedAutoSelect.trim().equals("1")) {
                // userId = request.getParameter("userId");
                // userId = userId.replace("\\", "").replace("\"",
                // "").replace("\'", "").replace("\r", "").replace("\n", "");
                userName = request.getParameter("userName");
                userName = userName.replace("\\", "").replace("\"", "").replace("\'", "").replace("\r", "")
                        .replace("\n", "");
                String userPriv = request.getParameter("userPriv");
                String userPrivOther = request.getParameter("role");
                String postPriv = request.getParameter("postPriv");
                String deptIdStr = request.getParameter("deptIdStr");
                if (request.getParameter("deptId").equals(deptIdStr.trim())) {
                    deptId = Integer.parseInt(request.getParameter("deptId"));
                } else {
                    if ("".equals(deptIdStr.trim())) {
                        // deptId = 0;
                    } else {
                        deptId = Integer.parseInt(deptIdStr);
                    }
                }
                // System.out.println(deptId+"UIUIIUIUIUIU");
                String postDept = request.getParameter("postDeptId");
                // System.out.println(postDept);
                String deptIdOther = request.getParameter("dept");
                int dutyType = 0;
                if (T9Utility.isNullorEmpty(request.getParameter("dutyType"))) {
                    dutyType = 0;
                } else {
                    dutyType = Integer.parseInt(request.getParameter("dutyType"));
                }
                // map.put("userId" , T9DBUtility.escapeLike(userId));
                map.put("userName", userName);
                map.put("userPriv", userPriv);
                map.put("postPriv", postPriv);
                map.put("deptId", deptId);
                map.put("postDept", postDept);
                map.put("userPrivOther", userPrivOther);
                map.put("deptIdOther", deptIdOther);
                map.put("dutyType", dutyType);
            }
            String openedFlowDispatch = request.getParameter("openedFlowDispatch");
            if (openedFlowDispatch.trim().equals("1")) {
                // System.out.println(request.getParameter("userNo")+"TTTTTTT1111");
                int userNo = 10;
                if (T9Utility.isInteger(request.getParameter("userNo"))) {
                    userNo = Integer.parseInt(request.getParameter("userNo"));
                }
                // int canbroadcast =
                // Integer.parseInt(request.getParameter("canbroadcast"));
                String notLogin = request.getParameter("notLogin");
                String notLoginFlag = request.getParameter("notLoginFlag");
                if (T9Utility.isNullorEmpty(notLogin)) {
                    T9PersonLogic pl = new T9PersonLogic();
                    // 软件是否已经注册
                    String webInfoPath = T9SysProps.getWebInfPath();
                    String keyPath = webInfoPath + File.separator + "config";
                    if (!SignProvider.verify(keyPath, "publicKey.dat", "license.dat")) {
                        // if
                        // (T9Utility.isNullorEmpty(T9RegistProps.getProp(T9AuthKeys.REGIST_ORG
                        // + ".t9"))) {
                        int permitUserCntNo = T9RegistUtility.getUserCnt();
                        if (pl.getNotLoginNum(dbConn) < permitUserCntNo) {
                            notLogin = "0";
                        } else {
                            if ("0".equals(notLoginFlag)) {
                                notLogin = "0";
                            } else {
                                request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
                                request.setAttribute(T9ActionKeys.RET_MSRG,
                                        "已经达到系统的最大授权用户数(30)，不能再增加允许登录OA用户");
                                return "/core/inc/rtjson.jsp";
                            }
                        }
                    } else {
                        int permitUserCnt = T9RegistUtility.getUserCnt();
                        if (pl.getNotLoginNum(dbConn) < permitUserCnt) {
                            notLogin = "0";
                        } else {
                            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
                            request.setAttribute(T9ActionKeys.RET_MSRG,
                                    "已经达到系统的最大授权用户数(" + T9RegistUtility.getUserCnt() + ")，不能再增加允许登录OA用户");
                            return "/core/inc/rtjson.jsp";
                        }
                    }

                } else {
                    notLogin = "1";
                }

                String notViewUser = request.getParameter("notViewUser");
                if (T9Utility.isNullorEmpty(notViewUser)) {
                    notViewUser = "0";
                } else {
                    notViewUser = "1";
                }
                String notViewTable = request.getParameter("notViewTable");
                if (T9Utility.isNullorEmpty(notViewTable)) {
                    notViewTable = "0";
                } else {
                    notViewTable = "1";
                }
                String useingKey = request.getParameter("useingKey");
                if (T9Utility.isNullorEmpty(useingKey)) {
                    useingKey = "0";
                } else {
                    useingKey = "1";
                }
                map.put("notLogin", notLogin);
                map.put("notViewUser", notViewUser);
                map.put("notViewTable", notViewTable);
                map.put("useingKey", useingKey);
                map.put("userNo", userNo);
                int canbroadcast = Integer.parseInt(request.getParameter("canbroadcast"));
                map.put("canbroadcast", canbroadcast);
                int imRange = Integer.parseInt(request.getParameter("imRange"));
                map.put("imRange", imRange);
            }

            String openedWarnDispatch = request.getParameter("openedWarnDispatch");
            if (openedWarnDispatch.trim().equals("1")) {
                int emailCapacity = 0;
                if (T9Utility.isInteger(request.getParameter("emailCapacity"))) {
                    emailCapacity = Integer.parseInt(request.getParameter("emailCapacity"));
                }
                String folderCapacity = request.getParameter("folderCapacity");
                int webmailNum = 0;
                if (T9Utility.isNullorEmpty(request.getParameter("webmailNum"))) {
                    webmailNum = 0;
                } else if (T9Utility.isInteger(request.getParameter("webmailNum"))) {
                    webmailNum = Integer.parseInt(request.getParameter("webmailNum"));
                }
                int webmailCapacity = 0;
                if (T9Utility.isNullorEmpty(request.getParameter("webmailCapacity"))) {
                    webmailCapacity = 0;
                } else if (T9Utility.isInteger(request.getParameter("webmailCapacity"))) {
                    webmailCapacity = Integer.parseInt(request.getParameter("webmailCapacity"));
                }
                String bindIp = request.getParameter("bindIp");
                String remark = request.getParameter("remark");

                map.put("emailCapacity", emailCapacity);
                map.put("folderCapacity", folderCapacity);
                map.put("webmailNum", webmailNum);
                map.put("webmailCapacity", webmailCapacity);
                map.put("bindIp", bindIp);
                map.put("remark", remark);
            }

            String openedOtherDispatch = request.getParameter("openedOtherDispatch");
            if (openedOtherDispatch.trim().equals("1")) {
                String byname = request.getParameter("byname");
                if (!T9Utility.isNullorEmpty(byname)) {
                    byname = byname.replace("\\", "\\\\").replace("\"", "").replace("\'", "")
                            .replace("\r", "").replace("\n", "");
                }
                String sex = request.getParameter("sex");
                String birthday = request.getParameter("birthday");
                String theme = request.getParameter("theme");
                String mobilNo = request.getParameter("mobilNo");
                String mobilNoHidden = request.getParameter("mobilNoHidden");
                if (T9Utility.isNullorEmpty(mobilNoHidden)) {
                    mobilNoHidden = "0";
                } else {
                    mobilNoHidden = "1";
                }
                String email = request.getParameter("email");
                String telNoDept = request.getParameter("telNoDept");

                map.put("byname", byname);
                map.put("sex", sex);
                map.put("birthday", birthday);
                map.put("theme", theme);
                map.put("mobilNo", mobilNo);
                map.put("mobilNoHidden", mobilNoHidden);
                map.put("email", email);
                map.put("telNoDept", telNoDept);
            }
            map.put("seqId", seqId);
            T9ORM orm = new T9ORM();
            orm.updateSingle(dbConn, "person", map);
            updateDeptInHR(dbConn, seqId, String.valueOf(deptId));

            String deptNameStr = personLogic.getDeptNameLogic(dbConn, deptId);
            String remarks = "[" + deptNameStr + "]" + userName + ",USER_ID=" + userId;
            T9SysLogLogic.addSysLog(dbConn, T9LogConst.EIDT_USER, remarks, login.getSeqId(),
                    request.getRemoteAddr());
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
            request.setAttribute(T9ActionKeys.RET_MSRG, "成功修改人员");

            Map<String, HttpSession> users = T9SessionListener.getSessaionContextMap();

            for (String key : users.keySet()) {
                HttpSession se = users.get(key);
                try {
                    if (se != null) {
                        T9Person p = (T9Person) se.getAttribute("LOGIN_USER");
                        if (p != null && p.getSeqId() == seqId) {
                            T9Person o = (T9Person) orm.loadObjSingle(dbConn, T9Person.class, seqId);
                            se.setAttribute("LOGIN_USER", o);
                        }
                    }
                } catch (Exception ex) {
                }
            }

            // 生成org.xml文件
            T9IsPiritOrgAct.getOrgDataStream(dbConn);
            if (T9ReportSyncLogic.hasSync) {
                T9Person o = (T9Person) orm.loadObjSingle(dbConn, T9Person.class, seqId);
                Connection reportConn = T9ReportSyncLogic.getReportConn();
                T9PersonSyncLogic logic = new T9PersonSyncLogic();
                logic.editPerson(o, reportConn);
                if (reportConn != null) {
                    reportConn.close();
                }
            }
            if (T9OaSyncLogic.hasSync) {
                Connection oaConn = T9OaSyncLogic.getOAConn();
                T9Person o = (T9Person) orm.loadObjSingle(dbConn, T9Person.class, seqId);
                t9.core.module.oa.logic.T9PersonSyncLogic logic = new t9.core.module.oa.logic.T9PersonSyncLogic();
                logic.editPerson(o, oaConn);
                if (oaConn != null) {
                    oaConn.close();
                }
            }
        } catch (Exception ex) {
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
            request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
            throw ex;
        }
        return "/core/inc/rtjson.jsp";
    }

    private void updateDeptInHR(Connection dbConn, int seqId, String deptId) {
        try {
            Class<?> classObj = Class.forName("t9.subsys.oa.hr.manage.staffInfo.logic.T9HrStaffInfoLogic");
            Class<?>[] paramTypeArray = new Class[] { Connection.class, int.class, String.class };
            Method methodObj = classObj.getMethod("updateDeptId", paramTypeArray);
            methodObj.invoke(classObj.newInstance(), new Object[] { dbConn, seqId, deptId });

        } catch (ClassNotFoundException e) {

        } catch (Exception e) {

        }
    }

    private void syncDept(Connection dbConn) {
        try {
            Class<?> classObj = Class.forName("t9.subsys.oa.hr.manage.staffInfo.logic.T9HrStaffInfoLogic");
            Class<?>[] paramTypeArray = new Class[] { Connection.class };
            Method methodObj = classObj.getMethod("syncDept", paramTypeArray);
            methodObj.invoke(classObj.newInstance(), new Object[] { dbConn });

        } catch (ClassNotFoundException e) {

        } catch (Exception e) {

        }
    }

    public String listInDutyPerson(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = request.getParameter("id");
        Connection dbConn = null;
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);

            dbConn = requestDbConn.getSysDbConn();
            ArrayList<T9Person> personList = null;
            Map map = new HashMap();
            map.put("DEPT_ID", id);
            T9ORM orm = new T9ORM();
            personList = (ArrayList<T9Person>) orm.loadListSingle(dbConn, T9Person.class, map);
            request.setAttribute("personList", personList);
        } catch (Exception ex) {
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
            request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
            throw ex;
        }
        return "/core/funcs/person/indutypersonlist.jsp";
    }

    public String getOffDutyPerson(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Connection dbConn = null;
        String seqId = request.getParameter("seqId");
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            dbConn = requestDbConn.getSysDbConn();
            T9Person person = null;
            StringBuffer data = null;
            T9ORM orm = new T9ORM();
            person = (T9Person) orm.loadObjSingle(dbConn, T9Person.class, Integer.parseInt(seqId));
            if (person == null) {
                person = new T9Person();
            }
            // System.out.println(seqId);
            data = T9FOM.toJson(person);
            // System.out.println(data.toString()+"XXXXXXXXXXXXXXXXXXXXXXXXXXXx");
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
            request.setAttribute(T9ActionKeys.RET_MSRG, "成功取出数据");
            request.setAttribute(T9ActionKeys.RET_DATA, data.toString());
        } catch (Exception ex) {
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
            request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
            throw ex;
        }
        return "/core/inc/rtjson.jsp";
    }

    public String selectPerson(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String userId = request.getParameter("userId");
        Connection dbConn = null;
        int num = 0;
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            dbConn = requestDbConn.getSysDbConn();
            String data = null;
            num = personLogic.selectPerson(dbConn, userId);
            // System.out.println("++++++++++++++++++===============num:" +
            // num);
            if (num >= 1) {
                StringBuffer sb = new StringBuffer("[");
                sb.append("{");
                sb.append("num:\"" + num + "\"");
                sb.append("}");
                sb.append("]");
                data = sb.toString();
            } else if (num == 0) {
                StringBuffer sb = new StringBuffer("[");
                sb.append("{");
                sb.append("num:\"" + num + "\"");
                sb.append("}");
                sb.append("]");
                data = sb.toString();
            }
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
            request.setAttribute(T9ActionKeys.RET_MSRG, "成功取出数据");
            request.setAttribute(T9ActionKeys.RET_DATA, data);
        } catch (Exception ex) {
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
            request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
            throw ex;
        }
        return "/core/inc/rtjson.jsp";
    }

    public String selectPersonName(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String byName = request.getParameter("byname");
        Connection dbConn = null;
        int num = 0;
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            dbConn = requestDbConn.getSysDbConn();
            T9Person user = (T9Person) request.getSession().getAttribute("LOGIN_USER");// 获得登陆用户
            String data = null;
            T9MyprivLogic logic = new T9MyprivLogic();
            // num = personLogic.selectPerson(dbConn, byName);
            if (user.getUserId().equals(byName)) {
                num = 1;
            }
            if (logic.checkByName(dbConn, byName) > 0) {
                num = 1;
            }
            if (num == 1) {
                StringBuffer sb = new StringBuffer("[");
                sb.append("{");
                sb.append("num:\"" + num + "\"");
                sb.append("}");
                sb.append("]");
                data = sb.toString();
            } else if (num == 0) {
                StringBuffer sb = new StringBuffer("[");
                sb.append("{");
                sb.append("num:\"" + num + "\"");
                sb.append("}");
                sb.append("]");
                data = sb.toString();
            }
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
            request.setAttribute(T9ActionKeys.RET_MSRG, "成功取出数据");
            request.setAttribute(T9ActionKeys.RET_DATA, data);
        } catch (Exception ex) {
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
            request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
            throw ex;
        }
        return "/core/inc/rtjson.jsp";
    }

    public String getTree(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Connection dbConn = null;
        String idStr = request.getParameter("id");
        int id = 0;
        if (idStr != null && !"".equals(idStr.trim())) {
            id = Integer.parseInt(idStr);
        }
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            dbConn = requestDbConn.getSysDbConn();

            HashMap map = new HashMap();
            map.put("DEPT_PARENT", id);
            T9ORM orm = new T9ORM();
            StringBuffer sb = new StringBuffer("[");
            List<T9Department> deptlist = orm.loadListSingle(dbConn, T9Department.class, map);
            for (int i = 0; i < deptlist.size(); i++) {
                T9Department dept = deptlist.get(i);
                sb.append("{");
                sb.append("nodeId:\"" + dept.getSeqId() + "\"");
                sb.append(",name:\"" + dept.getDeptName() + "\"");
                sb.append(",isHaveChild:" + IsHaveChild(request, response, String.valueOf(dept.getSeqId())));
                sb.append(",imgAddress:\"" + request.getContextPath()
                        + "/core/styles/style1/img/dtree/node_dept.gif\"");
                sb.append("},");
            }
            List personList = new ArrayList();
            personList.add("person");
            String[] filters = new String[] { "DEPT_ID = " + id };
            map = (HashMap) orm.loadDataSingle(dbConn, personList, filters);

            List<Map> list = (List<Map>) map.get("PERSON");
            for (Map m : list) {
                sb.append("{");
                sb.append("nodeId:\"r" + m.get("seqId") + "\"");
                sb.append(",name:\"" + m.get("userName") + "\"");
                sb.append(",isHaveChild:" + 0);
                sb.append(",imgAddress:\"" + request.getContextPath()
                        + "/core/styles/style1/img/dtree/0-1.gif\"");
                sb.append("},");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append("]");
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
            request.setAttribute(T9ActionKeys.RET_MSRG, "成功返回结果！");
            request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
        } catch (Exception ex) {
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
            request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
            throw ex;
        }
        return "/core/inc/rtjson.jsp";
    }

    public String userState(HttpServletRequest request, HttpServletResponse response, int userId)
            throws Exception {
        Connection dbConn = null;
        String url = null;
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            dbConn = requestDbConn.getSysDbConn();
            T9PersonLogic pl = new T9PersonLogic();
            String userState = pl.getUserStateImg(dbConn, userId);
            if (userState.trim().equals("1")) {
                url = "/core/styles/style1/img/dtree/0-1.gif\"";
            } else {
                url = "/core/styles/style1/img/dtree/0-2.gif\"";
            }
        } catch (Exception ex) {
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
            request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
            throw ex;
        }
        return url;
    }

    public int IsHaveChild(HttpServletRequest request, HttpServletResponse response, String id)
            throws Exception {
        Connection dbConn = null;
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            dbConn = requestDbConn.getSysDbConn();
            T9ORM orm = new T9ORM();
            Map map = new HashMap();
            map.put("DEPT_PARENT", id);
            // 判断是否有子部门
            List<T9Department> list = orm.loadListSingle(dbConn, T9Department.class, map);
            // 判断本部门是否有人

            // System.out.println(list.size() + "=FGHJT");
            String[] str = { "DEPT_ID =" + id };
            List<T9Person> personList = orm.loadListSingle(dbConn, T9Person.class, str);
            if (list.size() > 0 || personList.size() > 0) {
                return 1;
            } else {
                return 0;
            }
            // List<T9Department> list = orm.loadListSingle(dbConn,
            // T9Department.class, map);
            // if(list.size() > 0){
            // return 1;
            // }else{
            // return 0;
            // }
        } catch (Exception ex) {
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
            request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
            throw ex;
        }
    }

    public String getUserPrivByNo(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Connection dbConn = null;
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            dbConn = requestDbConn.getSysDbConn();
            T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
            String loginUserPriv = person.getUserPriv();
            String data = null;
            List<T9UserPriv> userPrivList = null;
            StringBuffer sb = new StringBuffer("[");
            T9ORM orm = new T9ORM();
            int privNo = Integer.parseInt(T9PersonLogic.getPrivNoStr(dbConn, loginUserPriv));
            String[] filters = null;
            if (!person.isAdminRole()) {
                filters = new String[] { "PRIV_NO > " + privNo + " and not SEQ_ID = 1 order by PRIV_NO ASC" };
            } else {
                filters = new String[] { "1=1 order by PRIV_NO ASC" };
            }
            userPrivList = orm.loadListSingle(dbConn, T9UserPriv.class, filters);
            for (int i = 0; i < userPrivList.size(); i++) {
                T9UserPriv userPriv = userPrivList.get(i);
                sb.append("{");
                sb.append("privNo:\"" + userPriv.getSeqId() + "\"");
                sb.append(",privName:\"" + userPriv.getPrivName() + "\"");
                sb.append("},");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append("]");
            data = sb.toString();
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
            request.setAttribute(T9ActionKeys.RET_MSRG, "成功取出数据");
            request.setAttribute(T9ActionKeys.RET_DATA, data);
        } catch (Exception ex) {
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
            request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
            throw ex;
        }
        return "/core/inc/rtjson.jsp";
    }

    public String getUserPriv(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Connection dbConn = null;
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            dbConn = requestDbConn.getSysDbConn();
            String data = null;
            List<T9UserPriv> userPrivList = null;
            StringBuffer sb = new StringBuffer("[");
            T9ORM orm = new T9ORM();
            userPrivList = orm.loadListSingle(dbConn, T9UserPriv.class, new HashMap());
            for (int i = 0; i < userPrivList.size(); i++) {
                T9UserPriv userPriv = userPrivList.get(i);
                sb.append("{");
                sb.append("seqId:\"" + userPriv.getSeqId() + "\"");
                sb.append(",privName:\"" + userPriv.getPrivName() + "\"");
                sb.append("},");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append("]");
            data = sb.toString();
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
            request.setAttribute(T9ActionKeys.RET_MSRG, "成功取出数据");
            request.setAttribute(T9ActionKeys.RET_DATA, data);
        } catch (Exception ex) {
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
            request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
            throw ex;
        }
        return "/core/inc/rtjson.jsp";
    }

    public String getGradeTree(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Connection dbConn = null;
        String idStr = request.getParameter("id");
        int id = 0;
        if (idStr != null && !"".equals(idStr.trim())) {
            id = Integer.parseInt(idStr);
        }
        T9OrgSelectLogic logic = new T9OrgSelectLogic();
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            dbConn = requestDbConn.getSysDbConn();
            T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
            StringBuffer sb = new StringBuffer("[");
            T9ORM orm = new T9ORM();
            HashMap map = new HashMap();
            map.put("DEPT_PARENT", id);
            String[] querys = new String[2];
            int login = person.getDeptId();
            ArrayList<T9Department> deptList = new ArrayList();
            if (person.getPostPriv().equals("0")) {
                querys = new String[] { "SEQ_ID = " + login };
                deptList = (ArrayList<T9Department>) orm.loadListSingle(dbConn, T9Department.class, querys);
            } else {
                T9Department dept = logic.getDeptParentId(dbConn, person.getDeptId());
                if (id == 0) {
                    querys[0] = " DEPT_PARENT = " + dept.getDeptParent();
                } else {
                    querys[0] = "DEPT_PARENT=" + id;
                }
                deptList = (ArrayList<T9Department>) orm.loadListSingle(dbConn, T9Department.class, querys);
            }
            // ArrayList<T9Department> deptListStr = null;
            // deptList = (ArrayList<T9Department>)orm.loadListSingle(dbConn,
            // T9Department.class, querys);
            for (int i = 0; i < deptList.size(); i++) {
                T9Department depts = deptList.get(i);
                sb.append("{");
                sb.append("nodeId:\"" + depts.getSeqId() + "\"");
                sb.append(",name:\"" + depts.getDeptName() + "\"");
                sb.append(",isHaveChild:" + IsHaveSon(request, response, String.valueOf(depts.getSeqId()))
                        + "");
                sb.append(",imgAddress:\"" + request.getContextPath()
                        + "/core/styles/style1/img/dtree/node_dept.gif\"");
                sb.append("},");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append("]");
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
            request.setAttribute(T9ActionKeys.RET_MSRG, "成功返回结果！");
            request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
        } catch (Exception ex) {
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
            request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
            throw ex;
        }
        return "/core/inc/rtjson.jsp";
    }

    public int IsHaveSon(HttpServletRequest request, HttpServletResponse response, String id)
            throws Exception {
        Connection dbConn = null;
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            dbConn = requestDbConn.getSysDbConn();
            T9ORM orm = new T9ORM();
            Map map = new HashMap();
            map.put("DEPT_PARENT", id);
            List<T9Department> list = orm.loadListSingle(dbConn, T9Department.class, map);
            if (list.size() > 0) {
                return 1;
            } else {
                return 0;
            }
        } catch (Exception ex) {
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
            request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
            throw ex;
        }
    }

    /**
     * 闲置状态：是否登陆过
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public String getLoginUser(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            T9Person person = (T9Person) request.getSession().getAttribute("LONGIN_USER");
            // person = new T9Person();
            // person.setUserName("ss");
            // / person.setUserPriv("xitong");
            // person.setUserId("1");
            if (person != null) {
                request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
                request.setAttribute(T9ActionKeys.RET_MSRG, "成功返回结果！");
                request.setAttribute(T9ActionKeys.RET_DATA, person.toJSON());
            } else {
                request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
                request.setAttribute(T9ActionKeys.RET_MSRG, "未登陆");
            }

        } catch (Exception ex) {
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
            request.setAttribute(T9ActionKeys.RET_MSRG, "未登陆");
            throw ex;
        }
        return "/core/inc/rtjson.jsp";
    }

    public String getUserByDept(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String sDeptId = request.getParameter("deptId");
        Connection dbConn = null;
        try {
            T9PersonLogic pl = new T9PersonLogic();
            List<T9Person> list = pl.getPersonByDept(Integer.parseInt(sDeptId), dbConn);
            StringBuffer sb = new StringBuffer("[");
            if (list.size() > 0) {
                for (T9Person tmp : list) {
                    sb.append(tmp.toJsonSimple());
                }
                sb.deleteCharAt(sb.length() - 1);
            }
            sb.append("]");
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
            request.setAttribute(T9ActionKeys.RET_MSRG, "成功返回结果！");
            request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
        } catch (Exception ex) {
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
            request.setAttribute(T9ActionKeys.RET_MSRG, "未登陆");
            throw ex;
        }
        return "/core/inc/rtjson.jsp";
    }

    /**
     * 管理用户列表
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */

    public String getManagePersonList(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        Connection dbConn = null;
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            dbConn = requestDbConn.getSysDbConn();
            T9Person person = (T9Person) request.getSession().getAttribute("LOGIN_USER");
            String deptId = request.getParameter("deptId");
            T9PersonLogic dl = new T9PersonLogic();
            String data = "";
            /*
             * if(person.isAdminRole() || postPriv.equals("1")){ data =
             * dl.getManagePersonList(dbConn,request.getParameterMap(), deptId,
             * deptIdOther, loginUserPriv, isOaAdmin, deptIdLogin, postDept,
             * postPriv); }else{ if(dl.findId(postDept, deptId) ||
             * dl.exitDeptId(deptIdLogin, deptId)){ data =
             * dl.getManagePersonList(dbConn,request.getParameterMap(), deptId,
             * deptIdOther, loginUserPriv, isOaAdmin, deptIdLogin, postDept,
             * postPriv); }else{ data = "{totalRecord:0,pageData:[]}"; } }
             */
            data = dl.getManagePersonList(dbConn, request.getParameterMap(), deptId, person);
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
     * 获取部门名称
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */

    public String getDeptName(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Connection dbConn = null;
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            dbConn = requestDbConn.getSysDbConn();
            String deptIdStr = request.getParameter("deptId");
            int deptId = Integer.parseInt(deptIdStr);
            T9PersonLogic dl = new T9PersonLogic();
            String data = dl.getDeptNameLogic(dbConn, deptId);
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
            request.setAttribute(T9ActionKeys.RET_DATA, "\"" + data + "\"");
        } catch (Exception ex) {
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
            request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
            throw ex;
        }
        return "/core/inc/rtjson.jsp";
    }

    /**
     * 获取部门名称(带权限)
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */

    public String getPostPrivDept(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Connection dbConn = null;
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            dbConn = requestDbConn.getSysDbConn();
            T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
            String postPriv = person.getPostPriv();
            int loginDeptId = person.getDeptId();
            int deptId = Integer.parseInt(request.getParameter("deptId"));
            String postDept = person.getPostDept();
            StringBuffer sb = new StringBuffer("[");
            T9MyPriv mp = T9PrivUtil.getMyPriv(dbConn, person, null, 2);
            boolean isPriv = T9PrivUtil.isDeptPriv(dbConn, deptId, mp, person);

            sb.append("{");
            sb.append("loginDeptId:\"" + loginDeptId + "\"");
            sb.append(",postPriv:\"" + postPriv + "\"");
            sb.append(",isPriv:" + isPriv);
            sb.append(",postDept:\"" + postDept + "\"");
            sb.append("}");
            sb.append("]");
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
            request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
        } catch (Exception ex) {
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
            request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
            throw ex;
        }
        return "/core/inc/rtjson.jsp";
    }

    /**
     * 获取用户名称
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */

    public String getUserName(HttpServletRequest request, HttpServletResponse response) throws Exception {
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
            request.setAttribute(T9ActionKeys.RET_DATA, "\"" + data + "\"");
        } catch (Exception ex) {
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
            request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
            throw ex;
        }
        return "/core/inc/rtjson.jsp";
    }

    /**
     * 获取角色名称
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */

    public String getRoleName(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Connection dbConn = null;
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            dbConn = requestDbConn.getSysDbConn();
            String roleIdStr = request.getParameter("roleId");
            int roleId = Integer.parseInt(roleIdStr);
            T9PersonLogic dl = new T9PersonLogic();
            String data = dl.getRoleNameLogic(dbConn, roleId);
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
            request.setAttribute(T9ActionKeys.RET_DATA, "\"" + data + "\"");
        } catch (Exception ex) {
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
            request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
            throw ex;
        }
        return "/core/inc/rtjson.jsp";
    }

    /**
     * 考勤排班类型
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */

    public String getDutyType(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Connection dbConn = null;
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            dbConn = requestDbConn.getSysDbConn();
            String dutyIdStr = request.getParameter("dutyId");
            int dutyId = Integer.parseInt(dutyIdStr);
            T9PersonLogic dl = new T9PersonLogic();
            String data = dl.getDutyTypeLogic(dbConn, dutyId);
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
            request.setAttribute(T9ActionKeys.RET_DATA, "\"" + data + "\"");
        } catch (Exception ex) {
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
            request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
            throw ex;
        }
        return "/core/inc/rtjson.jsp";
    }

    /**
     * 删除
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public String deleteUser(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Connection dbConn = null;
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            dbConn = requestDbConn.getSysDbConn();
            T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
            String loginUserId = person.getUserId();
            String sumStrs = request.getParameter("sumStrs");
            String remark = "";
            T9SysLogLogic.addSysLog(dbConn, T9LogConst.DELETE_USER, remark, person.getSeqId(),
                    request.getRemoteAddr());

            T9PersonLogic pl = new T9PersonLogic();
            pl.deleteAll(dbConn, sumStrs, loginUserId);
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
            request.setAttribute(T9ActionKeys.RET_MSRG, "成功取出数据");

            // 生成org.xml文件
            T9IsPiritOrgAct.getOrgDataStream(dbConn);

        } catch (Exception ex) {
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
            request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
            throw ex;
        }
        return "/core/inc/rtjson.jsp";
    }

    /**
     * 清空密码
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */

    public String clesrUserPassword(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        Connection dbConn = null;
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            dbConn = requestDbConn.getSysDbConn();
            T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
            String remark = "";
            String seqIdStrs = request.getParameter("seqIdStrs");
            T9PersonLogic pl = new T9PersonLogic();
            pl.clearPassword(dbConn, seqIdStrs);
            String[] seqIds = seqIdStrs.split(",");
            for (int i = 0; i < seqIds.length; i++) {
                String seqIdStr = seqIds[i];
                String deptId = personLogic.getDeptIdLogic(dbConn, Integer.parseInt(seqIdStr));
                String deptName = personLogic.getDeptNameLogic(dbConn, Integer.parseInt(deptId));
                String userName = personLogic.getUserNameLogic(dbConn, Integer.parseInt(seqIdStr));
                String userId = personLogic.getUserIdLogic(dbConn, Integer.parseInt(seqIdStr));
                remark += "[" + T9Utility.encodeSpecial(deptName) + "]" + userName + ",USER_ID=" + userId
                        + "<br>";
            }
            T9SysLogLogic.addSysLog(dbConn, T9LogConst.REMOVE_PASSWORD_BY_ADMIN, remark, person.getSeqId(),
                    request.getRemoteAddr());

            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
            request.setAttribute(T9ActionKeys.RET_MSRG, "成功取出数据");
        } catch (Exception ex) {
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
            request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
            throw ex;
        }
        return "/core/inc/rtjson.jsp";
    }

    /**
     * 清空在线时长
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */

    public String clearVisitTime(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Connection dbConn = null;
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            dbConn = requestDbConn.getSysDbConn();
            String seqIdStrs = request.getParameter("seqIdStrs");
            T9PersonLogic pl = new T9PersonLogic();
            pl.clearVisitTime(dbConn, seqIdStrs);
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
            request.setAttribute(T9ActionKeys.RET_MSRG, "成功取出数据");
        } catch (Exception ex) {
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
            request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
            throw ex;
        }
        return "/core/inc/rtjson.jsp";
    }

    /**
     * 判断用户是否有密码
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */

    public String getPassword(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Connection dbConn = null;
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            dbConn = requestDbConn.getSysDbConn();
            String deptIdStr = request.getParameter("deptId");
            int deptId = Integer.parseInt(deptIdStr);
            T9PersonLogic dl = new T9PersonLogic();
            String data = dl.getPassword(dbConn, deptId);
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
            request.setAttribute(T9ActionKeys.RET_DATA, "\"" + data + "\"");
        } catch (Exception ex) {
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
            request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
            throw ex;
        }
        return "/core/inc/rtjson.jsp";
    }

    /**
     * 判断用户是否登录过
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */

    public String getNotLogin(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Connection dbConn = null;
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            dbConn = requestDbConn.getSysDbConn();
            String deptIdStr = request.getParameter("deptId");
            int deptId = Integer.parseInt(deptIdStr);
            T9PersonLogic dl = new T9PersonLogic();
            String data = dl.getNotLogin(dbConn, deptId);
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
            request.setAttribute(T9ActionKeys.RET_DATA, "\"" + data + "\"");
        } catch (Exception ex) {
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
            request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
            throw ex;
        }
        return "/core/inc/rtjson.jsp";
    }

    public String getUserIdCol(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Connection dbConn = null;
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            dbConn = requestDbConn.getSysDbConn();
            String deptIdStr = request.getParameter("deptId");
            int seqId = Integer.parseInt(deptIdStr);
            List<Map> list = new ArrayList();
            T9ORM orm = new T9ORM();
            HashMap map = null;
            StringBuffer sb = new StringBuffer("[");
            String[] filters = new String[] { "SEQ_ID=" + seqId };
            List funcList = new ArrayList();
            funcList.add("person");
            map = (HashMap) orm.loadDataSingle(dbConn, funcList, filters);
            list.addAll((List<Map>) map.get("PERSON"));
            if (list.size() > 1) {
                for (Map ms : list) {
                    sb.append("{");
                    sb.append("userId:\"" + ms.get("userId") + "\"");
                    sb.append(",password:\"" + (ms.get("password") == null ? "" : ms.get("password")) + "\"");
                    sb.append(",notLogin:\"" + (ms.get("notLogin") == null ? "" : ms.get("notLogin")) + "\"");
                    sb.append("},");
                }
                sb.deleteCharAt(sb.length() - 1);
            } else {
                for (Map ms : list) {
                    sb.append("{");
                    sb.append("userId:\"" + ms.get("userId") + "\"");
                    sb.append(",password:\"" + (ms.get("password") == null ? "" : ms.get("password")) + "\"");
                    sb.append(",notLogin:\"" + (ms.get("notLogin") == null ? "" : ms.get("notLogin")) + "\"");
                    sb.append("}");
                }
            }
            sb.append("]");
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
            request.setAttribute(T9ActionKeys.RET_MSRG, "成功取出数据");
            request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
        } catch (Exception ex) {
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
            request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
            throw ex;
        }
        return "/core/inc/rtjson.jsp";
    }

    /**
     * 获取密码为空用户显示为红色，禁止登录用户显示为灰色 的判断条件
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */

    public String getMenuPara(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Connection dbConn = null;
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            dbConn = requestDbConn.getSysDbConn();
            String deptIdStr = request.getParameter("deptId");
            int seqId = Integer.parseInt(deptIdStr);
            T9PersonLogic orgLogic = new T9PersonLogic();
            T9Person org = null;
            String data = null;
            org = orgLogic.getMenuPara(dbConn, seqId);
            if (org == null) {
                org = new T9Person();
            }
            data = T9FOM.toJson(org).toString();
            // System.out.println(data+"klklkl");
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
            request.setAttribute(T9ActionKeys.RET_MSRG, "成功取出数据");
            request.setAttribute(T9ActionKeys.RET_DATA, data);
        } catch (Exception ex) {
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
            request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
            throw ex;
        }
        return "/core/inc/rtjson.jsp";
    }

    /**
     * 查询结果列表
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */

    public String getSearchPersonList(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        Connection dbConn = null;
        ArrayList<T9Person> personList = null;
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            dbConn = requestDbConn.getSysDbConn();
            T9Person person = (T9Person) request.getSession().getAttribute("LOGIN_USER");

            String loginUserPriv = person.getUserPriv();
            String postDepts = person.getPostDept();
            String loginpostPriv = person.getPostPriv();
            // int LoginUserId = Integer.parseInt(person.getUserId());
            int loginUserDept = person.getDeptId();
            int LoginUserId = person.getSeqId();
            StringBuffer sb = new StringBuffer("[");
            // String deptId = request.getParameter("deptId");
            boolean isAdminRole = person.isAdminRole();
            String userId = request.getParameter("userId");
            userId = T9DBUtility.escapeLike(userId);
            String userName = request.getParameter("userName");
            userName = T9DBUtility.escapeLike(userName);
            String byname = request.getParameter("byname");
            byname = T9DBUtility.escapeLike(byname);
            String sex = request.getParameter("sex");
            String deptId = request.getParameter("deptId");
            String userPriv = request.getParameter("userPriv");
            String postPriv = request.getParameter("postPriv");
            String notLogin = request.getParameter("notLogin");
            String notViewUser = request.getParameter("notViewUser");
            String notViewTable = request.getParameter("notViewTable");
            String dutyType = request.getParameter("dutyType");
            String lastVisitTime = request.getParameter("lastVisitTime");
            T9PersonLogic dl = new T9PersonLogic();
            // boolean a = T9PrivUtil.isDeptPriv(dbConn, deptId, postPriv,
            // postDept, LoginUserId, loginUserDept);
            personList = dl.getSearchPersonList(dbConn, request.getParameterMap(), userId, userName, byname,
                    sex, deptId, userPriv, postPriv, notLogin, notViewUser, notViewTable, dutyType,
                    lastVisitTime, loginUserPriv, isAdminRole);
            int flag = 0;
            String isAdmin = "";
            for (int i = 0; i < personList.size(); i++) {
                T9Person address = personList.get(i);
                if (!T9PrivUtil.isDeptPriv(dbConn, address.getDeptId(), loginpostPriv, postDepts,
                        LoginUserId, loginUserDept)) {
                    flag++;
                    continue;
                }
                String passwordStr = "password";
                if (address.getPassword() == null) {
                    passwordStr = "";
                } else if (T9PassEncrypt.isValidPas("", address.getPassword())) {
                    passwordStr = "";
                }
                sb.append("{");
                sb.append("seqId:\"" + address.getSeqId() + "\"");
                sb.append(",userId:\"" + address.getUserId() + "\"");
                sb.append(",deptId:\"" + address.getDeptId() + "\"");
                sb.append(",password:\"" + passwordStr + "\"");
                if (address.isAdmin()) {
                    isAdmin = "isAdmin";
                    sb.append(",isAdmin:\"" + isAdmin + "\"");
                } else {
                    sb.append(",isAdmin:\"" + isAdmin + "\"");
                }
                // T9PassEncrypt.isValidPas(pwd, person.getPassword().trim());
                sb.append(",notLogin:\"" + address.getNotLogin() + "\"");
                sb.append(",userName:\"" + (address.getUserName() == null ? "" : address.getUserName())
                        + "\"");
                sb.append(",userPriv:\"" + (address.getUserPriv() == null ? "" : address.getUserPriv())
                        + "\"");
                sb.append(",postPriv:\"" + (address.getPostPriv() == null ? "" : address.getPostPriv())
                        + "\"");
                sb.append("},");
            }
            sb.deleteCharAt(sb.length() - 1);
            if (personList.size() == 0 || flag == personList.size()) {
                sb = new StringBuffer("[");
            }
            sb.append("]");
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
            request.setAttribute(T9ActionKeys.RET_MSRG, "成功取出数据");
            request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
        } catch (Exception ex) {
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
            request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
            throw ex;
        }
        return "/core/inc/rtjson.jsp";
    }

    /**
     * 获取管理范围
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */

    public String getPostPriv(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Connection dbConn = null;
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            dbConn = requestDbConn.getSysDbConn();
            T9Person person = (T9Person) request.getSession().getAttribute("LOGIN_USER");
            String loginUserId = person.getUserId();
            T9PersonLogic pl = new T9PersonLogic();
            String data = pl.getPostPrivLogic(dbConn, loginUserId);
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
            request.setAttribute(T9ActionKeys.RET_DATA, "\"" + data + "\"");
        } catch (Exception ex) {
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
            request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
            throw ex;
        }
        return "/core/inc/rtjson.jsp";
    }

    public String getPostPrivOther(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Connection dbConn = null;
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            dbConn = requestDbConn.getSysDbConn();
            T9Person person = (T9Person) request.getSession().getAttribute("LOGIN_USER");
            int loginDeptId = person.getDeptId();
            T9PersonLogic pl = new T9PersonLogic();
            String data = pl.getPostPrivOtherLogic(dbConn, loginDeptId);
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
            request.setAttribute(T9ActionKeys.RET_DATA, "\"" + data + "\"");
        } catch (Exception ex) {
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
            request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
            throw ex;
        }
        return "/core/inc/rtjson.jsp";
    }

    /**
     * 用户批量设置
     */
    public String addSet(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Connection dbConn = null;

        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            dbConn = requestDbConn.getSysDbConn();
            T9Person person = (T9Person) request.getSession().getAttribute("LOGIN_USER");
            String type = "19";
            String roleDesc = request.getParameter("roleDesc");
            String deptDesc = request.getParameter("deptDesc");
            String userDesc = request.getParameter("userDesc");
            String role = request.getParameter("role");
            String dept = request.getParameter("dept");
            String user = request.getParameter("user");

            String myTableLeft = request.getParameter("myTableLeft");
            String myTableRight = request.getParameter("myTableRight");
            String shortcut = request.getParameter("shortcut");
            // String privId1 = request.getParameter("privId1");
            String privId1 = "";
            String userPriv = request.getParameter("userPriv");
            String deptId = request.getParameter("deptId");
            String theme = request.getParameter("theme");
            String bkground = request.getParameter("bkground");
            String menuType = request.getParameter("menuType");
            String smsOn = request.getParameter("smsOn");
            String callSound = request.getParameter("callSound");
            String panel = request.getParameter("panel");
            String dutyType = request.getParameter("dutyType");
            String pass1 = request.getParameter("pass1");
            String emailCapacity = request.getParameter("emailCapacity");
            String folderCapacity = request.getParameter("folderCapacity");
            String webmailNum = request.getParameter("webmailNum");
            String webmailCapacity = request.getParameter("webmailCapacity");
            String remark = "";
            String setStr = "";
            if (!"".equals(deptDesc.trim())) {
                remark += "部门：" + deptDesc + "<br>";
            }
            if (!"".equals(roleDesc.trim())) {
                remark += "角色：" + roleDesc + "<br>";
            }
            if (!"".equals(userDesc.trim())) {
                remark += "人员：" + userDesc + "<br>";
            }

            if (!myTableLeft.trim().equals("")) {
                setStr += "MYTABLE_LEFT='" + myTableLeft + "',";
            }
            if (!myTableRight.trim().equals("")) {
                setStr += "MYTABLE_RIGHT='" + myTableRight + "',";
            }
            if (!shortcut.trim().equals("")) {
                setStr += "SHORTCUT='" + shortcut + "',";
            }
            if (!userPriv.trim().equals("")) {
                setStr += "USER_PRIV='" + userPriv + "',";
            }
            if (!deptId.trim().equals("")) {
                setStr += "DEPT_ID='" + deptId + "',";
            }
            if (!theme.trim().equals("")) {
                setStr += "THEME='" + theme + "',";
            }
            if (!bkground.trim().equals("")) {
                if (bkground.trim().equals("0")) {
                    bkground = "";
                }
                setStr += "BKGROUND='" + bkground + "',";
            }
            if (!menuType.trim().equals("")) {
                setStr += "MENU_TYPE='" + menuType + "',";
            }
            if (!smsOn.trim().equals("")) {
                setStr += "SMS_ON='" + smsOn + "',";
            }
            if (!callSound.trim().equals("")) {
                setStr += "CALL_SOUND='" + callSound + "',";
            }
            if (!panel.trim().equals("")) {
                setStr += "PANEL='" + panel + "',";
            }
            if (!dutyType.trim().equals("")) {
                setStr += "DUTY_TYPE='" + dutyType + "',";
            }
            if (!emailCapacity.trim().equals("")) {
                setStr += "EMAIL_CAPACITY='" + emailCapacity + "',";
            }
            if (!folderCapacity.trim().equals("")) {
                setStr += "FOLDER_CAPACITY='" + folderCapacity + "',";
            }
            if (!webmailNum.trim().equals("")) {
                setStr += "WEBMAIL_NUM='" + webmailNum + "',";
            }
            if (!webmailCapacity.trim().equals("")) {
                setStr += "WEBMAIL_CAPACITY='" + webmailCapacity + "',";
            }
            if (!pass1.trim().equals("")) {

                setStr += "PASSWORD='" + T9PassEncrypt.encryptPass(pass1) + "',";
            }
            T9SysLogLogic.addSysLog(dbConn, type, remark, person.getSeqId(), request.getRemoteAddr());
            T9PersonLogic pl = new T9PersonLogic();
            pl.getSearchSet(dbConn, dept, role, user, setStr, privId1);
            syncDept(dbConn);
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
            // request.setAttribute(T9ActionKeys.RET_DATA,"\"" + data + "\"");

            // 生成org.xml文件
            T9IsPiritOrgAct.getOrgDataStream(dbConn);

        } catch (Exception ex) {
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
            request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
            throw ex;
        }
        return "/core/inc/rtjson.jsp";
    }

    /**
     * 批量个性设置日志列表
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */

    public String getSetLogList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Connection dbConn = null;
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            dbConn = requestDbConn.getSysDbConn();
            T9Person person = (T9Person) request.getSession().getAttribute("LOGIN_USER");
            int loginUserId = person.getSeqId();
            T9PersonLogic dl = new T9PersonLogic();
            String data = dl.getSetLogList(dbConn, request.getParameterMap(), loginUserId);
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
     * 获得密码长度范围
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */

    public String getPasswordLength(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        Connection dbConn = null;
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            dbConn = requestDbConn.getSysDbConn();
            List<Map> list = new ArrayList();
            T9ORM orm = new T9ORM();
            HashMap map = null;
            StringBuffer sb = new StringBuffer("[");
            String[] filters = new String[] { "PARA_NAME='SEC_PASS_MIN' or PARA_NAME='SEC_PASS_MAX' or PARA_NAME='SEC_PASS_SAFE'" };
            List funcList = new ArrayList();
            funcList.add("sysPara");
            map = (HashMap) orm.loadDataSingle(dbConn, funcList, filters);
            list.addAll((List<Map>) map.get("SYS_PARA"));
            if (list.size() > 1) {
                for (Map ms : list) {
                    sb.append("{");
                    sb.append("paraName:\"" + ms.get("paraName") + "\"");
                    sb.append(",paraValue:\"" + ms.get("paraValue") + "\"");
                    sb.append("},");
                }
                sb.deleteCharAt(sb.length() - 1);
            } else {
                for (Map ms : list) {
                    sb.append("{");
                    sb.append("paraName:\"" + ms.get("paraName") + "\"");
                    sb.append(",paraValue:\"" + ms.get("paraValue") + "\"");
                    sb.append("}");
                }
            }
            sb.append("]");
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
            request.setAttribute(T9ActionKeys.RET_MSRG, "成功取出密码长度范围");
            request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
        } catch (Exception ex) {
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
            request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
            throw ex;
        }
        return "/core/inc/rtjson.jsp";
    }

    /**
     * 获取空密码用户
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */

    public String getNoPassUser(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Connection dbConn = null;
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            dbConn = requestDbConn.getSysDbConn();
            T9PersonLogic pl = new T9PersonLogic();
            String userIdStr = pl.getNoPassUserId(dbConn);
            String userNameStr = pl.getNoPassUserName(dbConn);
            StringBuffer sb = new StringBuffer("[");
            sb.append("{");
            sb.append("userId:\"" + userIdStr.substring(0, userIdStr.length() - 1) + "\"");
            sb.append(",userName:\"" + userNameStr.substring(0, userNameStr.length() - 1) + "\"");
            sb.append("}");
            sb.append("]");
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
            request.setAttribute(T9ActionKeys.RET_MSRG, "成功空密码用户");
            request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
        } catch (Exception ex) {
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
            request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
            throw ex;
        }
        return "/core/inc/rtjson.jsp";
    }

    /**
     * 是否启用UserKey
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */

    public String getUserKey(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Connection dbConn = null;
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            dbConn = requestDbConn.getSysDbConn();
            String seqIdStr = request.getParameter("seqId");
            int seqId = Integer.parseInt(seqIdStr);
            T9PersonLogic pl = new T9PersonLogic();
            String data = pl.getUserKey(dbConn, seqId);
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
            request.setAttribute(T9ActionKeys.RET_DATA, "\"" + data + "\"");
        } catch (Exception ex) {
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
            request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
            throw ex;
        }
        return "/core/inc/rtjson.jsp";
    }

    /**
     * update PERSON表中的keySn
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */

    public String updateUserKey(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Connection dbConn = null;
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            dbConn = requestDbConn.getSysDbConn();
            int seqId = Integer.parseInt(request.getParameter("seqId"));
            String keySn = request.getParameter("keySn");
            Map m = new HashMap();
            m.put("seqId", seqId);
            m.put("keySn", keySn);
            T9ORM orm = new T9ORM();
            orm.updateSingle(dbConn, "person", m);
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
            request.setAttribute(T9ActionKeys.RET_MSRG, "keySn修改");
        } catch (Exception ex) {
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
            request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
            throw ex;
        }
        return "/core/inc/rtjson.jsp";
    }

    public String getUserInformation(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        Connection dbConn = null;
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            dbConn = requestDbConn.getSysDbConn();
            String seqIdStr = request.getParameter("seqId");
            int seqId = Integer.parseInt(seqIdStr);
            T9PersonLogic pl = new T9PersonLogic();
            String data = pl.getUserInformation(dbConn, seqId);
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
            request.setAttribute(T9ActionKeys.RET_DATA, "\"" + data + "\"");
        } catch (Exception ex) {
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
            request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
            throw ex;
        }
        return "/core/inc/rtjson.jsp";
    }

    public void addUsbKeyLog(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Connection dbConn = null;
        T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
        T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
        try {
            dbConn = requestDbConn.getSysDbConn();
            String remark = "";
            T9SysLogLogic.addSysLog(dbConn, T9LogConst.USER_KEY_AUTHFAILURES, remark, person.getSeqId(),
                    request.getRemoteAddr());
        } catch (T9InvalidParamException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public String getMaxPrivNo(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Connection dbConn = null;
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            dbConn = requestDbConn.getSysDbConn();
            T9PersonLogic pl = new T9PersonLogic();
            String maxPrivNo = pl.getMaxPrivNoLogic(dbConn);
            String data = pl.getMaxUserPrivLogic(dbConn, maxPrivNo);
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
            request.setAttribute(T9ActionKeys.RET_DATA, "\"" + data + "\"");
        } catch (Exception ex) {
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
            request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
            throw ex;
        }
        return "/core/inc/rtjson.jsp";
    }

    /**
     * 选择角色
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */

    public String getRoles(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String privNoFlagStr = request.getParameter("privNoFlag");
        if (!T9Utility.isNullorEmpty(privNoFlagStr)) {
        }
        Connection dbConn = null;
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            dbConn = requestDbConn.getSysDbConn();
            T9PersonLogic logic = new T9PersonLogic();
            T9Person loginUser = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
            String loginUserPriv = loginUser.getUserPriv();
            int privNo = Integer.parseInt(T9PersonLogic.getPrivNoStr(dbConn, loginUserPriv));
            List<T9UserPriv> list = null;
            list = logic.getRoleList(dbConn, privNo, loginUser);

            StringBuffer sb = new StringBuffer();
            for (T9UserPriv up : list) {
                sb.append(up.getJsonSimple());
            }
            if (list.size() > 0) {
                sb.deleteCharAt(sb.length() - 1);
            }
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
            request.setAttribute(T9ActionKeys.RET_MSRG, "get Success");
            request.setAttribute(T9ActionKeys.RET_DATA, "[" + sb.toString() + "]");
        } catch (Exception ex) {
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
            request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
            throw ex;
        }
        return "/core/inc/rtjson.jsp";
    }

    /**
     * 导出到EXCEL表格中
     * 
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public String exportToExcel(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setCharacterEncoding(T9Const.CSV_FILE_CODE);
        Connection conn = null;
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            conn = requestDbConn.getSysDbConn();
            T9Person person = (T9Person) request.getSession().getAttribute("LOGIN_USER");

            String loginUserPriv = person.getUserPriv();
            // String deptId = request.getParameter("deptId");
            boolean isAdminRole = person.isAdminRole();
            String userId = request.getParameter("userId");
            userId = T9DBUtility.escapeLike(userId);
            String userName = request.getParameter("userName");
            userName = T9DBUtility.escapeLike(userName);
            String byname = request.getParameter("byname");
            byname = T9DBUtility.escapeLike(byname);
            String sex = request.getParameter("sex");
            String deptId = request.getParameter("deptId");
            String userPriv = request.getParameter("userPriv");
            String postPriv = request.getParameter("postPriv");
            String notLogin = request.getParameter("notLogin");
            String notViewUser = request.getParameter("notViewUser");
            String notViewTable = request.getParameter("notViewTable");
            String dutyType = request.getParameter("dutyType");
            String lastVisitTime = request.getParameter("lastVisitTime");

            String fileName = URLEncoder.encode("OA用户.csv", "UTF-8");
            fileName = fileName.replaceAll("\\+", "%20");
            response.setHeader("Cache-control", "private");
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Cache-Control", "maxage=3600");
            response.setHeader("Pragma", "public");
            response.setHeader("Accept-Ranges", "bytes");
            response.setHeader("Content-disposition", "attachment; filename=\"" + fileName + "\"");
            T9PersonLogic ieml = new T9PersonLogic();
            ArrayList<T9DbRecord> dbL = ieml.toExportPersonData(conn, request.getParameterMap(), userId,
                    userName, byname, sex, deptId, userPriv, postPriv, notLogin, notViewUser, notViewTable,
                    dutyType, lastVisitTime, loginUserPriv, isAdminRole);
            ;
            T9CSVUtil.CVSWrite(response.getWriter(), dbL);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
        return null;
    }

    public String importPerson(HttpServletRequest request, HttpServletResponse response) throws Exception {
        InputStream is = null;
        Connection dbConn = null;
        String data = null;
        int isCount = 0;
        int updateCount = 0;
        String message = "";
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            dbConn = requestDbConn.getSysDbConn();
            T9FileUploadForm fileForm = new T9FileUploadForm();
            fileForm.parseUploadRequest(request);
            is = fileForm.getInputStream();
            ArrayList<T9DbRecord> drl = T9CSVUtil.CVSReader(is, T9Const.CSV_FILE_CODE);

            StringBuffer sb = new StringBuffer("[");
            T9PersonLogic dl = new T9PersonLogic();
            int count = dl.getUserConut(dbConn);
            int availableCnt = T9RegistUtility.getUserCnt() - count;
            if (count < 0) {
                data = "获取用户数失败!";
            } else if (drl.size() > availableCnt) {
                data = "导入失败: 剩余可用用户数" + availableCnt + ", 导入用户超出用户数限制!";
            } else {
                String deptName = "";
                String userId = "";
                String userName = "";
                String role = "";
                String userNo = "";
                String password = "";
                String postPriv = "";
                String postPrivStr = "";
                String infoStr = "";
                String sex = "";
                String brithday = "";
                String byName = "";
                String mobilNo = "";
                String bindIp = "";
                String telNoDept = "";
                String faxNoDept = "";
                String addHome = "";
                String postNoHome = "";
                String telNoHome = "";
                String email = "";
                String oicq = "";
                String msn = "";
                String seqIdStr = "";
                String color = "red";
                int seqId = 0;

                String remark = "成功导入人员：";
                boolean hasSucess = false;
                T9Person person = (T9Person) request.getSession().getAttribute("LOGIN_USER");
                Map map = new HashMap();
                for (int i = 0; i < drl.size(); i++) {
                    deptName = (String) drl.get(i).getValueByName("部门");
                    userId = (String) drl.get(i).getValueByName("用户名");
                    if (T9Utility.isNullorEmpty(userId)) {
                        continue;
                    }
                    userName = (String) drl.get(i).getValueByName("姓名");
                    if (T9Utility.isNullorEmpty(userName)) {
                        continue;
                    }
                    role = (String) drl.get(i).getValueByName("角色");
                    userNo = (String) drl.get(i).getValueByName("用户排序号");
                    postPriv = (String) drl.get(i).getValueByName("管理范围");
                    sex = (String) drl.get(i).getValueByName("性别");
                    brithday = (String) drl.get(i).getValueByName("生日");
                    byName = (String) drl.get(i).getValueByName("别名");
                    mobilNo = (String) drl.get(i).getValueByName("手机");
                    bindIp = (String) drl.get(i).getValueByName("IP");
                    telNoDept = (String) drl.get(i).getValueByName("工作电话");
                    faxNoDept = (String) drl.get(i).getValueByName("工作传真");
                    addHome = (String) drl.get(i).getValueByName("家庭地址");
                    postNoHome = (String) drl.get(i).getValueByName("邮编");
                    telNoHome = (String) drl.get(i).getValueByName("家庭电话");
                    email = (String) drl.get(i).getValueByName("E-mail");
                    oicq = (String) drl.get(i).getValueByName("QQ");
                    msn = (String) drl.get(i).getValueByName("MSN");
                    password = (String) drl.get(i).getValueByName("PASSWORD");

                    seqIdStr = (String) drl.get(i).getValueByName("ID");
                    if (!T9Utility.isNullorEmpty(seqIdStr)) {
                        seqId = Integer.parseInt(seqIdStr);
                    }
                    if (T9Utility.isNullorEmpty(userId)) {
                        color = "red";
                        infoStr = "导入失败,用户名为空";
                        sbStrJson(sb, deptName, userId, userName, role, userNo, postPriv, infoStr, color);
                        continue;
                    }
                    if (dl.existsDeptNameIsMultiple(dbConn, deptName)) {
                        color = "red";
                        infoStr = "导入失败,部门名称 " + deptName + " 在系统中存在多个";
                        sbStrJson(sb, deptName, userId, userName, role, userNo, postPriv, infoStr, color);
                        continue;
                    } else if (dl.existsDepartment(dbConn, deptName)) {
                        color = "red";
                        if (T9Utility.isNullorEmpty(deptName)) {
                            deptName = "";
                        }
                        infoStr = "导入失败,部门名称 " + deptName + " 在系统中不存在";
                        sbStrJson(sb, deptName, userId, userName, role, userNo, postPriv, infoStr, color);
                        continue;
                    }
                    if (dl.existsUserId(dbConn, userId, seqId)) {
                        color = "red";
                        infoStr = "导入失败,用户ID重复";
                        sbStrJson(sb, deptName, userId, userName, role, userNo, postPriv, infoStr, color);
                        continue;
                    }
                    if (dl.existsRole(dbConn, role)) {
                        color = "red";
                        if (T9Utility.isNullorEmpty(role)) {
                            role = "";
                        }
                        infoStr = "导入失败，角色 " + role + " 不存在";
                        sbStrJson(sb, deptName, userId, userName, role, userNo, postPriv, infoStr, color);
                        continue;
                    }
                    if ("女".equals(sex)) {
                        sex = "1";
                    } else {
                        sex = "0";
                    }
                    if ("全体".equals(postPriv)) {
                        postPrivStr = "1";
                    } else if ("指定部门".equals(postPriv)) {
                        postPrivStr = "2";
                    } else {
                        postPrivStr = "0";
                    }
                    int deptId = dl.getDeptIdLogic(dbConn, deptName);
                    String roleName = role;
                    role = String.valueOf(dl.getUserPrivIdLogic(dbConn, role));
                    map.put("deptId", deptId);
                    map.put("userId", userId);
                    map.put("userName", userName);
                    map.put("userPriv", role);
                    map.put("userNo", userNo);
                    map.put("sex", sex);
                    map.put("postPriv", postPrivStr);
                    map.put("birthday", brithday);
                    map.put("byname", byName);
                    map.put("mobilNo", mobilNo);
                    map.put("bindIp", bindIp);
                    map.put("telNoDept", telNoDept);
                    map.put("faxNoDept", faxNoDept);
                    map.put("addHome", addHome);
                    map.put("postNoHome", postNoHome);
                    map.put("telNoHome", telNoHome);
                    map.put("email", email);
                    map.put("oicq", oicq);
                    map.put("msn", msn);
                    map.put("notLogin", "0");
                    // map.put("postDept" , postDept);
                    // map.put("userPrivOther" , userPrivOther);
                    // map.put("deptIdOther" , deptIdOther);

                    // map.put("notLogin", notLogin);
                    // map.put("notViewUser", notViewUser);
                    // map.put("notViewTable", notViewTable);
                    // map.put("useingKey", useingKey);

                    // map.put("webmailNum" , webmailNum);
                    // map.put("webmailCapacity", webmailCapacity);
                    // map.put("remark" , remark);
                    // map.put("mobilNoHidden", mobilNoHidden);

                    String auatar = "1";
                    map.put("auatar", auatar);
                    String callSound = "1";
                    map.put("callSound", callSound);
                    int dutyType = 1;
                    map.put("dutyType", dutyType);
                    String smsOn = "1";
                    map.put("smsOn", smsOn);
                    String theme = "2";
                    map.put("theme", theme);
                    if (T9Utility.isNullorEmpty(password)) {
                        password = "";
                        map.put("password", T9PassEncrypt.encryptPass(password));
                    } else {
                        map.put("password", T9PassEncrypt.encryptPass(password));
                    }
                    int emailCapacity = 100;
                    int folderCapacity = 100;
                    map.put("emailCapacity", emailCapacity);
                    map.put("folderCapacity", folderCapacity);

                    T9ORM orm = new T9ORM();
                    if (dl.existsUserId2(dbConn, userId)) {
                        updateCount++;
                        infoStr = "用户名" + userName + " 已存在，其信息得到更新";
                        color = "red";
                        sbStrJson(sb, deptName, userId, userName, roleName, userNo, postPriv, infoStr, color);
                        T9Person persons = dl.getPersonById(userId, dbConn);
                        map.put("seqId", persons.getSeqId());
                        orm.updateSingle(dbConn, "person", map);
                        if (T9ReportSyncLogic.hasSync) {
                            T9Person o = (T9Person) orm.loadObjSingle(dbConn, T9Person.class,
                                    persons.getSeqId());
                            if (o != null) {
                                Connection reportConn = T9ReportSyncLogic.getReportConn();
                                T9PersonSyncLogic logic = new T9PersonSyncLogic();
                                logic.editPerson(o, reportConn);
                                if (reportConn != null) {
                                    reportConn.close();
                                }
                            }
                        }
                        if (T9OaSyncLogic.hasSync) {
                            T9Person o = (T9Person) orm.loadObjSingle(dbConn, T9Person.class,
                                    persons.getSeqId());
                            if (o != null) {
                                Connection oaConn = T9OaSyncLogic.getOAConn();
                                t9.core.module.oa.logic.T9PersonSyncLogic logic = new t9.core.module.oa.logic.T9PersonSyncLogic();
                                logic.editPerson(o, oaConn);
                                if (oaConn != null) {
                                    oaConn.close();
                                }
                            }
                        }
                    } else {
                        isCount++;
                        infoStr = "成功";
                        color = "black";
                        sbStrJson(sb, deptName, userId, userName, roleName, userNo, postPriv, infoStr, color);
                        orm.saveSingle(dbConn, "person", map);
                        if (T9ReportSyncLogic.hasSync) {
                            int max = T9ReportSyncLogic.getMax(dbConn, "select max(SEQ_ID) FROM PERSON");
                            T9Person o = (T9Person) orm.loadObjSingle(dbConn, T9Person.class, max);
                            if (o != null) {
                                Connection reportConn = T9ReportSyncLogic.getReportConn();
                                T9PersonSyncLogic logic = new T9PersonSyncLogic();
                                logic.addPerson(o, reportConn);
                                if (reportConn != null) {
                                    reportConn.close();
                                }
                            }
                        }
                        if (T9OaSyncLogic.hasSync) {
                            int max = T9ReportSyncLogic.getMax(dbConn, "select max(SEQ_ID) FROM PERSON");
                            T9Person o = (T9Person) orm.loadObjSingle(dbConn, T9Person.class, max);
                            if (o != null) {
                                Connection oaConn = T9OaSyncLogic.getOAConn();
                                t9.core.module.oa.logic.T9PersonSyncLogic logic = new t9.core.module.oa.logic.T9PersonSyncLogic();
                                logic.addPerson(o, oaConn);
                                if (oaConn != null) {
                                    oaConn.close();
                                }
                            }
                        }
                        remark += userName + ",";
                        hasSucess = true;
                    }
                }
                if (sb.charAt(sb.length() - 1) == ',') {
                    sb.deleteCharAt(sb.length() - 1);
                }
                sb.append("]");
                data = sb.toString();
                request.setAttribute("contentList", data);
                if (hasSucess) {
                    T9SysLogLogic.addSysLog(dbConn, "6", remark, person.getSeqId(), request.getRemoteAddr());
                }
                request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
                request.setAttribute(T9ActionKeys.RET_MSRG, "成功取出数据");
                request.setAttribute(T9ActionKeys.RET_DATA, data);

                // 生成org.xml文件
                T9IsPiritOrgAct.getOrgDataStream(dbConn);
            }
        } catch (Exception ex) {
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
            request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
            // ex.printStackTrace();
            message = ex.getMessage();
            throw ex;
        }
        return "/core/funcs/person/importPerson.jsp?data=" + message + "&isCount=" + isCount
                + "&updateCount=" + updateCount;
    }

    public String add(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Connection dbConn = null;
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            dbConn = requestDbConn.getSysDbConn();
            Statement stm2 = null;
            ResultSet rs2 = null;
            String updatePwd = "SELECT USER_ID from PERSON WHERE USER_ID <> 'admin'";
            try {
                stm2 = dbConn.createStatement();
                rs2 = stm2.executeQuery(updatePwd);
                while (rs2.next()) {
                    String userId = rs2.getString("USER_ID");
                    update(dbConn, userId);
                }
            } catch (Exception ex) {
                throw ex;
            } finally {
                T9DBUtility.close(stm2, rs2, null);
            }
        } catch (Exception ex) {
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
            request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
            throw ex;
        }
        return null;
    }

    public void update(Connection conn, String userId) throws Exception {
        Statement stm2 = null;
        ResultSet rs2 = null;
        String userId1 = userId;
        if (userId.length() == 1) {
            userId1 = "000" + userId;
        } else if (userId.length() == 2) {
            userId1 = "00" + userId;
        } else if (userId.length() == 3) {
            userId1 = "0" + userId;
        } else if (userId.length() == 4) {
            return;
        }
        String updatePwd = "update  PERSON set USER_ID = '" + userId1 + "' WHERE USER_ID = '" + userId + "'";
        try {
            stm2 = conn.createStatement();
            stm2.executeUpdate(updatePwd);
        } catch (Exception ex) {
            throw ex;
        } finally {
            T9DBUtility.close(stm2, rs2, null);
        }
    }

    public String importPersonPwd(HttpServletRequest request, HttpServletResponse response) throws Exception {
        InputStream is = null;
        Connection dbConn = null;
        String data = "";
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            dbConn = requestDbConn.getSysDbConn();
            T9FileUploadForm fileForm = new T9FileUploadForm();
            fileForm.parseUploadRequest(request);
            is = fileForm.getInputStream();
            ArrayList<T9DbRecord> drl = T9CSVUtil.CVSReader(is, T9Const.CSV_FILE_CODE);

            StringBuffer sb = new StringBuffer("[");
            String remark = "成功导入人员pwd：";
            for (int i = 0; i < drl.size(); i++) {
                String pwd = T9Utility.null2Empty((String) drl.get(i).getValueByName("登录密码")).trim();
                String userId = T9Utility.null2Empty((String) drl.get(i).getValueByName("行员号(登录用户名)")).trim();
                Statement stm2 = null;
                ResultSet rs2 = null;
                String updatePwd = "update PERSON SET PASSWORD = '" + pwd + "' where USER_ID='" + userId
                        + "'";
                try {
                    stm2 = dbConn.createStatement();
                    int ii = stm2.executeUpdate(updatePwd);
                    if (ii == 1) {
                        data += userId + "<br/>";
                    } else {
                        data += "<span style='color:red'>" + userId + "</span><br/>";
                    }
                } catch (Exception ex) {
                    throw ex;
                } finally {
                    T9DBUtility.close(stm2, rs2, null);
                }
            }
            request.setAttribute("data", data);
        } catch (Exception ex) {
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
            request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
            // ex.printStackTrace();
            throw ex;
        }
        return "/core/funcs/person/importPersonPwd.jsp";
    }

    public String sbStrJson(StringBuffer sb, String deptName, String userId, String userName, String role,
            String userNo, String postPriv, String infoStr, String color) {
        sb.append("{");
        sb.append("deptName:\"" + (deptName == null ? "" : deptName) + "\"");
        sb.append(",userId:\"" + (userId == null ? "" : userId) + "\"");
        sb.append(",userName:\"" + (userName == null ? "" : userName) + "\"");
        sb.append(",role:\"" + (role == null ? "" : role) + "\"");
        sb.append(",userNo:\"" + (userNo == null ? "" : userNo) + "\"");
        sb.append(",postPriv:\"" + (postPriv == null ? "" : postPriv) + "\"");
        sb.append(",info:\"" + (infoStr == null ? "" : infoStr) + "\"");
        sb.append(",color:\"" + (color == null ? "" : color) + "\"");
        sb.append("},");
        return sb.toString();
    }
}
