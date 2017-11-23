package t9.core.funcs.system.act;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.person.data.T9SecureKey;
import t9.core.funcs.person.data.T9UserOnline;
import t9.core.funcs.person.data.T9UserPriv;
import t9.core.funcs.person.logic.T9SecureCardLogic;
import t9.core.funcs.system.act.adapter.T9LoginAdapter;
import t9.core.funcs.system.act.filters.T9BindIpValidator;
import t9.core.funcs.system.act.filters.T9ExistUserValidator;
import t9.core.funcs.system.act.filters.T9ForbidLoginValidator;
import t9.core.funcs.system.act.filters.T9InitialPwValidator;
import t9.core.funcs.system.act.filters.T9IpRuleValidator;
import t9.core.funcs.system.act.filters.T9PasswordValidator;
import t9.core.funcs.system.act.filters.T9PwExpiredValidator;
import t9.core.funcs.system.act.filters.T9RepeatLoginValidator;
import t9.core.funcs.system.act.filters.T9RetryLoginValidator;
import t9.core.funcs.system.act.filters.T9UsbkeyValidator;
import t9.core.funcs.system.act.filters.T9VerificationCodeValidator;
import t9.core.funcs.system.data.T9LoginUsers;
import t9.core.funcs.system.data.T9Menu;
import t9.core.funcs.system.data.T9MenuCache;
import t9.core.funcs.system.data.T9UserPrivCache;
import t9.core.funcs.system.logic.T9SystemLogic;
import t9.core.funcs.system.security.data.T9Security;
import t9.core.funcs.system.security.logic.T9SecurityLogic;
import t9.core.funcs.system.syslog.logic.T9SysLogLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.global.T9LogConst;
import t9.core.global.T9SysPropKeys;
import t9.core.global.T9SysProps;
import t9.core.servlet.T9SessionListener;
import t9.core.util.SignProvider;
import t9.core.util.T9Guid;
import t9.core.util.T9Out;
import t9.core.util.T9Utility;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;

public class T9SystemAct {

    private static String sp = System.getProperty("file.separator");
    private static Logger log = Logger.getLogger("t9.core.funcs.system.act.T9SystemAct");
    // 定义菜单图片的文件夹路径常量
    public final static String IMAGE_PATH = "/core/styles/imgs/menuIcon/";
    public final static String WEBOS_IMAGE_PATH = T9SysProps.getString(T9SysPropKeys.ROOT_DIR) + sp
            + T9SysProps.getString(T9SysPropKeys.WEB_ROOT_DIR) + sp
            + T9SysProps.getString(T9SysPropKeys.JSP_ROOT_DIR) + sp + "core" + sp + "frame" + sp + "webos"
            + sp + "styles" + sp + "icons" + sp;
    public final static String DEFAULT_PATH = "/core/funcs/";
    public final static String HOME_CLASSIC = "/core/frame/2/index.jsp";
    public final static String HOME_WEBOS = "/core/frame/webos/index.jsp";
    public final static String HOME_TDOA = "/core/frame/5/index.jsp";
    public final static String HOME_CLASSIC_3 = "/core/frame/3/index.jsp";

    /**
     * 系统登录方法
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public String prepareLoginIn(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Connection dbConn = null;
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            dbConn = requestDbConn.getSysDbConn();
            T9SystemLogic logic = new T9SystemLogic();
            Map<String, String> map = logic.getSysPara(dbConn);

            int loginKey = 0;
            int keyUser = 0;
            int verificationCode = 0;
            try {
                loginKey = Integer.parseInt(map.get("LOGIN_KEY"));
            } catch (Exception e) {

            }

            try {
                keyUser = Integer.parseInt(map.get("SEC_KEY_USER"));
            } catch (Exception e) {

            }

            try {
                verificationCode = Integer.parseInt(map.get("VERIFICATION_CODE"));
            } catch (Exception e) {

            }

            String title = logic.getIETitle(dbConn);

            if (T9Utility.isNullorEmpty(title)) {
                title = T9SysProps.getString("productName");
            }

            request.setAttribute("ieTitle", title);
            request.setAttribute("secKeyUser", String.valueOf(keyUser));
            request.setAttribute("useUsbKey", String.valueOf(loginKey));
            request.setAttribute("verificationCode", String.valueOf(verificationCode));
        } catch (Exception ex) {
            request.setAttribute("secKeyUser", "0");
            request.setAttribute("useUsbKey", "0");
            request.setAttribute("verificationCode", "0");
        }
        return "/login.jsp";
    }

    /**
     * 系统登录方法
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public String prepareLoginInIM(HttpServletRequest request, HttpServletResponse response) throws Exception {
        prepareLoginIn(request, response);
        return "/core/frame/ispirit/login.jsp";
    }

    /**
     * 系统登录方法
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public String prepareLoginInWebos(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        prepareLoginIn(request, response);
        return "/login2.jsp";
    }

    /**
     * 系统登录方法
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public String doLoginIn(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Connection dbConn = null;
        long start1 = System.currentTimeMillis();
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            dbConn = requestDbConn.getSysDbConn();
            String url = new String(request.getRequestURL());
            T9SystemLogic logic = new T9SystemLogic();
            long start = System.currentTimeMillis();
            Map<String, String> map = logic.getSysPara(dbConn);
            long end = System.currentTimeMillis();
            T9Out.debug("map:" + (end - start));
            String keyUser = T9Utility.null2Empty(request.getParameter("KEY_USER"));
            String caUser = T9Utility.null2Empty(request.getParameter("CA_USER"));

            /**
             * OA公文交换单点登录---》不需要校验密码处理-----syl
             */
            // String GW_JH_TYPE = request.getParameter("GW_JH_TYPE") == null ?
            // "" :request.getParameter("GW_JH_TYPE");
            /**
             * 结束----修改下面密码验证代码处理
             */

            String userName = request.getParameter("userName");
            if (T9Utility.isNullorEmpty(userName) && !T9Utility.isNullorEmpty(keyUser)) {
                userName = keyUser;
            }

            T9Person person = null;
            if (T9Utility.isNullorEmpty(userName)) {
                userName = "";
            } else {
                start = System.currentTimeMillis();
                person = logic.queryPerson(dbConn, userName);
                end = System.currentTimeMillis();
                T9Out.debug("queryPerson:" + (end - start));
            }

            String pwd = request.getParameter("pwd");
            // 是否开启动态密码卡验证
            T9SecurityLogic orgLogic = new T9SecurityLogic();
            start = System.currentTimeMillis();
            T9Security security = orgLogic.getSecritySecureKey(dbConn);
            end = System.currentTimeMillis();
            T9Out.debug("getSecritySecureKey:" + (end - start));
            // 该用户是否绑定动态密码卡
            T9SecureCardLogic secureCardLogic = new T9SecureCardLogic();
            start = System.currentTimeMillis();
            T9SecureKey secureKey = secureCardLogic.getKeyInfo(dbConn, person);
            end = System.currentTimeMillis();
            T9Out.debug("getKeyInfo:" + (end - start));

            if ("1".equals(security.getParaValue()) && secureKey != null) {
                if (request.getParameter("pwd").length() > 6) {
                    pwd = request.getParameter("pwd").substring(0, request.getParameter("pwd").length() - 6);
                }
            } else {
                pwd = request.getParameter("pwd");
            }
            if (pwd == null) {
                pwd = null;
            }

            String useingKey = "";
            if ("1".equals(map.get("LOGIN_KEY")) && person != null) {
                useingKey = T9Utility.null2Empty(person.getUseingKey());
            }

            if (!"1".equals(useingKey.trim())) {
                userName = request.getParameter("userName");
                if (userName == null) {
                    userName = "";
                }
            }

            if (T9Utility.isNullorEmpty(userName)) {
                userName = "";
            } else {
                // 如果用户禁止usbkey登陆,用户名必须从form表单中取
                // person = logic.queryPerson(dbConn, userName);
            }
            // 登录验证
            start = System.currentTimeMillis();
            T9LoginAdapter loginAdapter = new T9LoginAdapter(request, person);
            end = System.currentTimeMillis();
            T9Out.debug("loginAdapter:" + (end - start));
            try {
                // 软件是否已经注册
                String webInfoPath = T9SysProps.getWebInfPath();
                String keyPath = webInfoPath + File.separator + "config";
                if (SignProvider.verify(keyPath, "publicKey.dat", "license.dat")) {
                    if (!SignProvider.validDomain(request)) {
                        // 域名非法
                        Map<String, String> rtMap = new HashMap<String, String>();
                        rtMap.put("code", "13");
                        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
                        request.setAttribute(T9ActionKeys.RET_DATA, T9FOM.toJson(rtMap).toString());
                        request.setAttribute(T9ActionKeys.RET_MSRG, "域名非法，请联系厂家！");
                        return "/core/inc/rtjson.jsp";
                    }
                    // 已经注册（在判断有效期）
                    // 先验证版本（试用版30天，收费版）
                    if (SignProvider.type != null) {
                        if (SignProvider.type.equals("free")) {

                        } else if (SignProvider.type.equals("oem")) {
                            try {
                                if (!SignProvider.checkExpireValidDate()) {
                                    // 已经过期
                                    Map<String, String> rtMap = new HashMap<String, String>();
                                    rtMap.put("code", "13");
                                    request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
                                    request.setAttribute(T9ActionKeys.RET_DATA, T9FOM.toJson(rtMap)
                                            .toString());
                                    request.setAttribute(T9ActionKeys.RET_MSRG, "软件已经到期，请联系厂家！");
                                    return "/core/inc/rtjson.jsp";
                                }
                            } catch (Exception e) {
                                // 序列号错误
                                Map<String, String> rtMap = new HashMap<String, String>();
                                rtMap.put("code", "13");
                                request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
                                request.setAttribute(T9ActionKeys.RET_DATA, T9FOM.toJson(rtMap).toString());
                                request.setAttribute(T9ActionKeys.RET_MSRG, "序列号验证错误，请联系厂家！");
                                return "/core/inc/rtjson.jsp";
                            }
                        } else if (SignProvider.type.equals("trail")) {
                            // 验证是否免费到期
                            if (!SignProvider.checkTrailVersion()) {
                                // 试用到期
                                Map<String, String> rtMap = new HashMap<String, String>();
                                rtMap.put("code", "13");
                                request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
                                request.setAttribute(T9ActionKeys.RET_DATA, T9FOM.toJson(rtMap).toString());
                                request.setAttribute(T9ActionKeys.RET_MSRG, "软件试用到期，请联系厂家！");
                                return "/core/inc/rtjson.jsp";
                            }
                        } else {
                            // 序列号错误
                            Map<String, String> rtMap = new HashMap<String, String>();
                            rtMap.put("code", "13");
                            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
                            request.setAttribute(T9ActionKeys.RET_DATA, T9FOM.toJson(rtMap).toString());
                            request.setAttribute(T9ActionKeys.RET_MSRG, "序列号验证错误，请联系厂家！");
                            return "/core/inc/rtjson.jsp";
                        }
                    } else {
                        // 序列号错误
                        Map<String, String> rtMap = new HashMap<String, String>();
                        rtMap.put("code", "13");
                        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
                        request.setAttribute(T9ActionKeys.RET_DATA, T9FOM.toJson(rtMap).toString());
                        request.setAttribute(T9ActionKeys.RET_MSRG, "序列号验证错误，请联系厂家！");
                        return "/core/inc/rtjson.jsp";
                    }
                } else {
                    // 序列号非法
                    Map<String, String> rtMap = new HashMap<String, String>();
                    rtMap.put("code", "13");
                    request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
                    request.setAttribute(T9ActionKeys.RET_DATA, T9FOM.toJson(rtMap).toString());
                    request.setAttribute(T9ActionKeys.RET_MSRG, "序列号非法，请联系厂家！");
                    return "/core/inc/rtjson.jsp";
                }
            } catch (Exception e) {
                // 没有序列号
                Map<String, String> rtMap = new HashMap<String, String>();
                rtMap.put("code", "13");
                request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
                request.setAttribute(T9ActionKeys.RET_DATA, T9FOM.toJson(rtMap).toString());
                request.setAttribute(T9ActionKeys.RET_MSRG, "系统没有注册，请联系厂家！");
                return "/core/inc/rtjson.jsp";
            }
            // 验证用户是否存在
            start = System.currentTimeMillis();
            if (!loginAdapter.validate(new T9ExistUserValidator())) {
                return "/core/inc/rtjson.jsp";
            }
            end = System.currentTimeMillis();
            T9Out.debug("T9ExistUserValidator:" + (end - start));

            // 验证Ip规则
            start = System.currentTimeMillis();
            if (!loginAdapter.validate(new T9IpRuleValidator())) {
                return "/core/inc/rtjson.jsp";
            }
            end = System.currentTimeMillis();
            T9Out.debug("T9IpRuleValidator:" + (end - start));

            // 验证用户绑定Ip
            start = System.currentTimeMillis();
            if (!loginAdapter.validate(new T9BindIpValidator())) {
                return "/core/inc/rtjson.jsp";
            }
            end = System.currentTimeMillis();
            T9Out.debug("T9BindIpValidator:" + (end - start));

            // 验证用户是否禁止登陆
            start = System.currentTimeMillis();
            if (!loginAdapter.validate(new T9ForbidLoginValidator())) {
                return "/core/inc/rtjson.jsp";
            }
            end = System.currentTimeMillis();
            T9Out.debug("T9BindIpValidator:" + (end - start));

            start = System.currentTimeMillis();
            // 验证用户是否重复登陆
            if (!loginAdapter.validate(new T9RepeatLoginValidator())) {
                return "/core/inc/rtjson.jsp";
            }
            end = System.currentTimeMillis();
            T9Out.debug("T9RepeatLoginValidator:" + (end - start));

            if ("1".equals(useingKey.trim()) && "1".equals(map.get("LOGIN_KEY"))) {

                // 当用户使用Usbkey登陆时,进行usbkey验证
                if (!loginAdapter.validate(new T9UsbkeyValidator())) {
                    return "/core/inc/rtjson.jsp";
                }
            } else if (caUser != null && "1".equals(caUser.trim())) {
                // 如果是ca用户则放行，默认其合法
            } else {
                start = System.currentTimeMillis();
                if (!loginAdapter.validate(new T9VerificationCodeValidator())) {
                    return "/core/inc/rtjson.jsp";
                }
                end = System.currentTimeMillis();
                T9Out.debug("T9VerificationCodeValidator:" + (end - start));
                start = System.currentTimeMillis();
                if (!loginAdapter.validate(new T9RetryLoginValidator())) {
                    return "/core/inc/rtjson.jsp";
                }
                end = System.currentTimeMillis();
                T9Out.debug("T9RetryLoginValidator:" + (end - start));
                start = System.currentTimeMillis();
                if (!loginAdapter.validate(new T9PasswordValidator(request.getParameter("pwd")))) {
                    return "/core/inc/rtjson.jsp";
                }
                end = System.currentTimeMillis();
                T9Out.debug("T9PasswordValidator:" + (end - start));

            }

            // 调用登陆成功的处理
            start = System.currentTimeMillis();
            String loginType = request.getParameter("CLIENT");
            this.loginSuccess(dbConn, person, request, response, loginType);
            end = System.currentTimeMillis();
            T9Out.debug("loginSuccess:" + (end - start));
            start = System.currentTimeMillis();

            try {
                start = System.currentTimeMillis();
                Cookie cookie = reportSSO(person, pwd, request);
                end = System.currentTimeMillis();
                T9Out.debug("cookie1:" + (end - start));
                if (cookie != null) {
                    start = System.currentTimeMillis();
                    response.addCookie(cookie);
                    end = System.currentTimeMillis();
                    T9Out.debug("cookie2:" + (end - start));
                }

                start = System.currentTimeMillis();
                Cookie cookieOA = reportOA(person, pwd, request);
                end = System.currentTimeMillis();
                T9Out.debug("cookie3:" + (end - start));
                if (cookieOA != null) {
                    start = System.currentTimeMillis();
                    response.addCookie(cookieOA);
                    end = System.currentTimeMillis();
                    T9Out.debug("cookie4:" + (end - start));
                }
            } catch (Exception ex) {
            }
            end = System.currentTimeMillis();
            T9Out.debug("Cookie:" + (end - start));

            start = System.currentTimeMillis();
            // 验证密码是否过期,是否需要修改
            if (!loginAdapter.validate(new T9PwExpiredValidator())) {
                return "/core/inc/rtjson.jsp";
            }

            end = System.currentTimeMillis();
            T9Out.debug("T9PwExpiredValidator:" + (end - start));
            start = System.currentTimeMillis();
            // 验证时候需要修改初始密码
            if (!loginAdapter.validate(new T9InitialPwValidator())) {
                return "/core/inc/rtjson.jsp";
            }
            end = System.currentTimeMillis();
            T9Out.debug("T9InitialPwValidator:" + (end - start));

            start = System.currentTimeMillis();
            String menuType = person.getMenuType();
            String saveUserName = map.get("SEC_USER_MEM");

            if (menuType == null || "".equals(menuType.trim())) {
                menuType = "1";
            }

            if (saveUserName == null || "".equals(saveUserName.trim())) {
                saveUserName = "1";
            }

            Map<String, String> rtMap = new HashMap<String, String>();
            rtMap.put("saveUserName", saveUserName);
            rtMap.put("menuType", menuType);
            rtMap.put("userName", person.getUserName());
            rtMap.put("deptId", String.valueOf(person.getDeptId()));
            rtMap.put("seqId", String.valueOf(person.getSeqId()));
            String sessionToken = (String) request.getSession().getAttribute("sessionToken");
            rtMap.put("sessionToken", sessionToken);
            String classic = "3";// (String)
                                 // T9FOM.json2Map(person.getParamSet()).get("classicHome");
            if ("3".equals(classic)) {
                rtMap.put("homeAddress", HOME_CLASSIC_3);
            } else if ("1".equals(classic)) {
                rtMap.put("homeAddress", HOME_CLASSIC);
            } else if ("0".equals(classic)) {
                rtMap.put("homeAddress", HOME_WEBOS);
            } else if ("2".equals(classic)) {
                rtMap.put("homeAddress", HOME_TDOA);
            } else {
                String style = map.get("DEFAULT_INTERFACE_STYLE");
                if ("1".equals(style)) {
                    rtMap.put("homeAddress", HOME_WEBOS);
                } else if ("2".equals(style)) {
                    rtMap.put("homeAddress", HOME_TDOA);
                } else {
                    rtMap.put("homeAddress", HOME_CLASSIC);
                }
            }
            end = System.currentTimeMillis();
            T9Out.debug("aaa:" + (end - start));

            start = System.currentTimeMillis();
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
            request.setAttribute(T9ActionKeys.RET_DATA, T9FOM.toJson(rtMap).toString());
            request.setAttribute(T9ActionKeys.RET_MSRG, "成功");
            end = System.currentTimeMillis();
            T9Out.debug("aaa2:" + (end - start));
        } catch (Exception ex) {
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
            request.setAttribute(T9ActionKeys.RET_MSRG, "登录失败");
            throw ex;
        }
        long end1 = System.currentTimeMillis();
        T9Out.debug("doLoginIn:" + (end1 - start1));
        return "/core/inc/rtjson.jsp";
    }

    private void simpleSSO(HttpServletRequest request, T9Person person, String originalPwd) {

    }

    /**
     * 登录成功的处理
     * 
     * @param conn
     * @param person
     * @param request
     * @throws Exception
     */
    public void loginSuccess(Connection conn, T9Person person, HttpServletRequest request,
            HttpServletResponse response, String loginType) throws Exception {

        // 获取用户当前的session,如果不存在就生成一个新的session
        HttpSession session = request.getSession(true);
        T9SystemLogic logic = new T9SystemLogic();
        long start = System.currentTimeMillis();
        logic.updateLastVisitInfo(conn, person.getSeqId(), request.getRemoteAddr());

        long end = System.currentTimeMillis();
        T9Out.debug("updateLastVisitInfo:" + (end - start));
        // 记录登陆的时间

        person.setLastVisitTime(new Date());

        // 判断用户是否已经登录
        if (session.getAttribute("LOGIN_USER") == null) {
            // 添加登陆成功的系统日志
            start = System.currentTimeMillis();
            T9SysLogLogic.addSysLog(conn, T9LogConst.LOGIN, "登录成功", person.getSeqId(),
                    request.getRemoteAddr());
            end = System.currentTimeMillis();
            T9Out.debug("addSysLog:" + (end - start));
            start = System.currentTimeMillis();
            setUserInfoInSession(person, session, request.getRemoteAddr(), request);
            end = System.currentTimeMillis();
            T9Out.debug("addSysLog:" + (end - start));
            start = System.currentTimeMillis();
            this.addOnline(conn, person, String.valueOf(session.getAttribute("sessionToken")));
            end = System.currentTimeMillis();
            T9Out.debug("addSysLog:" + (end - start));
        } else {
            T9Person loginPerson = (T9Person) session.getAttribute("LOGIN_USER");
            // 判断map中是否已经存在
            if (T9SessionListener.getSessaionContextMap().get(session.getId()) == null) {
                // 销毁session
                session.invalidate();
                // 重新调用登录成功的处理
                loginSuccess(conn, person, request, response, loginType);
            } else {
                if (loginPerson.getSeqId() != person.getSeqId()) {
                    // 销毁session
                    session.invalidate();
                    // 重新调用登录成功的处理
                    loginSuccess(conn, person, request, response, loginType);
                }
            }
        }
        if (T9Utility.isNullorEmpty(loginType)) {
            loginType = "0";
        }
        T9SessionListener.getUserStateMap().put(person.getSeqId(), loginType);

        // 登录后检查今天是否需要提醒短信，
        // 如果有则提醒，并且修改最后一次提醒时间为今天(周期性事物)
        start = System.currentTimeMillis();
        try {
            Class<?> classObj = Class.forName("t9.core.funcs.calendar.act.T9AffairAct");
            Class<?>[] paramTypeArray = new Class[] { HttpServletRequest.class, HttpServletResponse.class };
            Method methodObj = classObj.getMethod("selectAffairRemindByToday", paramTypeArray);
            methodObj.invoke(classObj.newInstance(), new Object[] { request, response });

        } catch (ClassNotFoundException e) {

        } catch (Exception e) {

        }
        end = System.currentTimeMillis();
        T9Out.debug("addSysLog:" + (end - start));
    }

    /**
     * 单点登录到报表系统
     * 
     * @param person
     * @param originalPw
     * @param request
     * @return
     */
    private Cookie reportSSO(T9Person person, String originalPw, HttpServletRequest request) {
        String port = T9SysProps.getProp("REPORT_SSO_PORT");
        // 获取不到REPORT_SSO_PORT参数则不执行单点登录
        if (T9Utility.isNullorEmpty(port)) {
            return null;
        }
        String url = request.getRequestURL().toString();
        if (T9Utility.isNullorEmpty(url)) {
            return null;
        }
        String host = url.substring(0, url.indexOf("/t9/t9/"));
        if (T9Utility.isNullorEmpty(host)) {
            return null;
        }
        // 避免http://的冒号

        int tmpIndex = host.indexOf(":", 5);
        if (tmpIndex > 0) {
            host = host.substring(0, tmpIndex);
        }

        return simpleSSO(person, originalPw, port, "/logincheck.php");
    }

    /**
     * 单点登录到OA系统
     * 
     * @param person
     * @param originalPw
     * @param request
     * @return
     */
    private Cookie reportOA(T9Person person, String originalPw, HttpServletRequest request) {
        String port = T9SysProps.getProp("OA_SSO_PORT");
        // 获取不到REPORT_SSO_PORT参数则不执行单点登录
        if (T9Utility.isNullorEmpty(port)) {
            return null;
        }
        String url = request.getRequestURL().toString();
        if (T9Utility.isNullorEmpty(url)) {
            return null;
        }
        String host = url.substring(0, url.indexOf("/t9/t9/"));
        if (T9Utility.isNullorEmpty(host)) {
            return null;
        }
        // 避免http://的冒号

        int tmpIndex = host.indexOf(":", 5);
        if (tmpIndex > 0) {
            host = host.substring(0, tmpIndex);
        }

        return simpleSSO(person, originalPw, port, "/logincheck.php");
    }

    private Cookie simpleSSO(T9Person person, String originalPw, String port, String loginUrl) {
        HttpClient httpclient = new HttpClient();
        String ssoUrl = "http://127.0.0.1" + ":" + port + loginUrl;
        PostMethod method = new PostMethod(ssoUrl);
        method.addParameter("USERNAME", person.getUserId());
        method.addParameter("PASSWORD", originalPw);
        method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
        method.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "gbk");
        try {
            int statusCode = httpclient.executeMethod(method);
            if (statusCode != HttpStatus.SC_OK) {
                return null;
            }
            for (org.apache.commons.httpclient.Cookie c : httpclient.getState().getCookies()) {
                if ("PHPSESSID".equals(c.getName())) {
                    Cookie cookie = new Cookie(c.getName(), c.getValue());
                    cookie.setPath("/");
                    return cookie;
                }
            }
        } catch (HttpException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
        return null;
    }

    /**
     * 在session中设置用户信息
     * 
     * @param person
     * @param session
     */
    public void setUserInfoInSession(T9Person person, HttpSession session, String ip,
            HttpServletRequest request) throws Exception {

        String sessionToken = T9Guid.getRawGuid();
        session.setAttribute("LOGIN_USER", person);
        session.setAttribute("sessionToken", sessionToken);
        session.setAttribute("LOGIN_IP", ip);
        session.setAttribute("STYLE_INDEX", getStyleIndex(request));

        String lockSecStr = T9SysProps.getString("$OFFLINE_TIME_MIN");
        Long lockSec = null;
        try {
            lockSec = Long.valueOf(Integer.parseInt(lockSecStr) * 60 * 1000);
        } catch (Exception e) {
            lockSec = Long.valueOf(0);
        }
        session.setAttribute("OFFLINE_TIME_MIN", lockSec);
    }

    /**
     * 退出登录
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public String doLogout(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG, "成功");
        return "/core/inc/rtjson.jsp";
    }

    /**
     * 获取ietitle
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public String getIeTitle(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Connection dbConn = null;

        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            dbConn = requestDbConn.getSysDbConn();

            T9SystemLogic logic = new T9SystemLogic();
            String title = logic.getIETitle(dbConn);
            if (T9Utility.isNullorEmpty(title)) {
                title = T9SysProps.getString("productName");
            }

            title = "\"" + title + "\"";
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
            request.setAttribute(T9ActionKeys.RET_MSRG, "成功");
            request.setAttribute(T9ActionKeys.RET_DATA, title);
        } catch (Exception ex) {
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
            request.setAttribute(T9ActionKeys.RET_MSRG, "登录失败");
            throw ex;
        }
        return "/core/inc/rtjson.jsp";
    }

    /**
     * 获取系统菜单(未完成应用)
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public String listMenu(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Connection dbConn = null;
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            dbConn = requestDbConn.getSysDbConn();
            T9Person person = (T9Person) request.getSession().getAttribute("LOGIN_USER");
            List<String> menuSet = T9SystemAct.listUserMenu(dbConn, person);
            T9SystemLogic logic = new T9SystemLogic();
            // 1.一次展开一个一级菜单
            int type = logic.queryMenuExpandType(dbConn);
            StringBuffer sb = new StringBuffer("{expandType:");
            sb.append(type);
            sb.append(",menu:[");
            String menuStr = menuSet.toString();
            for (String id : menuSet) {
                if (id.length() == 2) {
                    T9Menu menu2 = T9MenuCache.getMenuCache(dbConn, Integer.parseInt(id));
                    if (menu2 == null) {
                        continue;
                    }
                    // 判断是否有下级节点
                    if (!menuStr.matches(".*[ ,\\[]" + menu2.getId() + "\\d+.*")) {
                        menu2.setLeaf(1);
                    }
                    String menuExpand = person.getMenuExpand();
                    if (menuExpand != null && id.equals(menuExpand.trim())) {
                        menu2.setExpand(1);
                    }
                    menu2.setChildes(lazyLoadJson(menuSet, dbConn, id, request.getContextPath(), request)
                            .toString());
                    // System.out.println(lazyLoadJson(menuSet, dbConn, id,
                    // request.getContextPath(), request));
                    sb.append(T9FOM.toJson(menu2));
                    sb.append(",");
                }
            }
            if (sb.charAt(sb.length() - 1) == ',') {
                sb.deleteCharAt(sb.length() - 1);
            }
            sb.append("]}");
            // System.out.println(sb.toString());
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
            request.setAttribute(T9ActionKeys.RET_MSRG, "成功");
            request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
        } catch (Exception ex) {
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
            request.setAttribute(T9ActionKeys.RET_MSRG, "登录失败");
            throw ex;
        }
        return "/core/inc/rtjson.jsp";
    }

    /**
     * 惰性加载菜单
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public String lazyLoadMenu(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String parent = request.getParameter("parent");

        Connection dbConn = null;
        try {
            // 在获取二级三级菜单时,没有传递一级菜单id返回异常信息
            if (parent == null || "".equals(parent)) {
                request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
                request.setAttribute(T9ActionKeys.RET_MSRG, "没有传递上级菜单id");
                return "/core/inc/rtjson.jsp";
            }

            T9Person person = (T9Person) request.getSession().getAttribute("LOGIN_USER");

            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            dbConn = requestDbConn.getSysDbConn();

            // 获取当前用户有权限查看的菜单List
            List<String> menuSet = T9SystemAct.listUserMenu(dbConn, person);
            if (!menuSet.contains(parent)) {
                request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
                request.setAttribute(T9ActionKeys.RET_MSRG, "该目录无访问权限");
                return "/core/inc/rtjson.jsp";
            }

            StringBuffer sb = lazyLoadJson(menuSet, dbConn, parent, request.getContextPath(), request);

            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
            request.setAttribute(T9ActionKeys.RET_MSRG, "成功");
            request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
        } catch (Exception ex) {
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
            request.setAttribute(T9ActionKeys.RET_MSRG, "登录失败");
            throw ex;
        }
        return "/core/inc/rtjson.jsp";
    }

    /**
     * 返回所有用户有权限的菜单List 其中包括用户辅助角色有权限的菜单
     * 
     * @param dbConn
     * @param person
     * @return
     * @throws Exception
     */
    public static List<String> listUserMenu(Connection dbConn, T9Person person) throws Exception {
        String privOther = "";
        if (person != null) {
            privOther = person.getUserPrivOther();
        }
        T9ORM t = new T9ORM();
        List<T9UserPriv> priv = new ArrayList<T9UserPriv>();
        if (privOther != null && !"".equals(privOther.trim())) {
            privOther = privOther.trim();
            for (String s : privOther.split(",")) {
                s = s.trim();
                if ("".equals(s.trim())) {
                    continue;
                }
                T9UserPriv up = T9UserPrivCache.getUserPrivCache(dbConn, s);
                if (up != null) {
                    priv.add(up);
                }
            }
        }
        String userPriv = "";
        if (person != null) {
            userPriv = person.getUserPriv();
        }
        if (userPriv == null || "".equals(userPriv.trim())) {
            userPriv = "";
        }
        T9UserPriv up = T9UserPrivCache.getUserPrivCache(dbConn, userPriv.trim());
        if (up != null) {
            priv.add(up);
        }
        // 用户主角色/辅助角色的菜单去除重复
        HashSet<String> menuSet = new HashSet<String>();
        for (T9UserPriv p : priv) {
            menuSet.addAll(Arrays.asList(T9Utility.null2Empty(p.getFuncIdStr()).split(",")));
        }
        HashSet<String> addSet = new HashSet<String>(menuSet);
        for (String s : menuSet) {
            s = s.trim();
            if (s.length() > 4) {
                addSet.add(s.substring(0, 4));
            } else if (s.length() > 2) {
                addSet.add(s.substring(0, 2));
            }
        }
        List<String> list = new ArrayList<String>(addSet);
        // 根据菜单的排序字段把菜单排序
        Collections.sort(list, new Comparator<String>() {
            public int compare(String arg0, String arg1) {
                if (T9Utility.isNullorEmpty(arg0)) {
                    arg0 = "";
                }
                if (T9Utility.isNullorEmpty(arg1)) {
                    arg1 = "";
                }
                return arg0.compareTo(arg1);
            }
        });
        return list;
    }

    /**
     * 生成菜单json数据的递归函数
     * 
     * @param menuSet
     * @param dbConn
     * @param parent
     * @param contextPath
     * @return
     * @throws Exception
     */
    private StringBuffer lazyLoadJson(List<String> menuSet, Connection dbConn, String parent,
            String contextPath, HttpServletRequest request) throws Exception {

        String menuStr = menuSet.toString();
        StringBuffer sb = new StringBuffer("[");
        String iconFolder = request.getParameter("iconFolder");

        for (String id : menuSet) {
            if (id.matches(parent + "\\d{2}")) {
                T9Menu menu = T9MenuCache.getSysFunction(dbConn, Integer.parseInt(id));
                if (menu == null) {
                    continue;
                }
                if (T9Utility.isNullorEmpty(iconFolder)) {
                    parseMenuIcon(menu);
                } else {
                    parseMenuIcon(menu, iconFolder);
                }
                // 判断是否有下级节点
                if (!menuStr.matches(".*[ ,\\[]" + menu.getId() + "\\d+.*")) {
                    // 没有下级菜单的情况,设置菜单为叶子节点
                    menu.setLeaf(1);
                    menu.setUrl(T9SystemAct.parseMenuUrl(menu.getUrl(), contextPath, request));
                    sb.append(T9FOM.toJson(menu));
                } else {
                    sb.append(T9FOM.toJson(menu));
                    sb.deleteCharAt(sb.length() - 1);
                    sb.append(",children:");

                    // 当存在下级节点时再次调用此函数,将下级菜单添加到children中

                    sb.append(lazyLoadJson(menuSet, dbConn, id, contextPath, request));
                    sb.append("}");
                }
                sb.append(",");
            }
        }

        if (sb.charAt(sb.length() - 1) == ',') {
            sb.deleteCharAt(sb.length() - 1);
        }

        sb.append("]");
        return sb;
    }

    /**
     * 设置用户在线
     * 
     * @param conn
     * @param person
     * @param sessionToken
     * @throws Exception
     */
    private void addOnline(Connection conn, T9Person person, String sessionToken) throws Exception {
        T9UserOnline online = new T9UserOnline();

        online.setSessionToken(sessionToken);
        online.setLoginTime(new Date());
        online.setUserId(person.getSeqId());
        T9SystemLogic logic = new T9SystemLogic();
        Map<String, String> map = logic.getSysPara(conn);
        int state = logic.queryUserOnline(conn, person.getSeqId());
        if (state > 0) {
            online.setUserState(String.valueOf(state));
            person.setOnStatus(String.valueOf(state));
        } else if ("0".equals(map.get("SEC_ON_STATUS"))) {
            online.setUserState("1");
            person.setOnStatus("1");
        } else {
            if (person.getOnStatus() == null) {
                person.setOnStatus("1");
            }
            online.setUserState(person.getOnStatus());
        }

        logic.addOnline(conn, online);
    }

    /**
     * 登陆通元网站
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public String loginOtherSys(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Connection dbConn = null;

        try {

            String bindOtherServer = T9SysProps.getString("BIND_USERS_OTHERS");

            if (bindOtherServer == null || !"1".equals(bindOtherServer)) {
                request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
                request.setAttribute(T9ActionKeys.RET_MSRG, "系统参数未设置绑定其他系统!");
                return "/core/inc/rtjson.jsp";
            }

            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            dbConn = requestDbConn.getSysDbConn();

            T9Person person = (T9Person) request.getSession().getAttribute("LOGIN_USER");

            T9LoginUsers loginUsers = new T9LoginUsers();
            HttpSession session = request.getSession();
            T9SystemLogic logic = new T9SystemLogic();
            String userId = logic.queryBindId(dbConn, person.getSeqId());
            if (userId == null) {
                request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
                request.setAttribute(T9ActionKeys.RET_MSRG, "用户未绑定其他系统账号!");
                return "/core/inc/rtjson.jsp";
            }

            String sessionToken = String.valueOf(session.getAttribute("sessionToken"));
            loginUsers.setSessionToken(sessionToken);
            loginUsers.setRoleId("");
            loginUsers.setUserId(userId);

            loginUsers.setLoginTime(new Date());

            try {
                logic.addLoginUser(dbConn, loginUsers);
            } catch (Exception e) {
                request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
                request.setAttribute(T9ActionKeys.RET_MSRG, "其他系统集成用户登录失败!");
                return "/core/inc/rtjson.jsp";
            }

            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
            request.setAttribute(T9ActionKeys.RET_DATA, "\"" + sessionToken + "\"");
            request.setAttribute(T9ActionKeys.RET_MSRG, "成功");
        } catch (Exception ex) {
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
            request.setAttribute(T9ActionKeys.RET_MSRG, "登录失败");
            throw ex;
        }
        return "/core/inc/rtjson.jsp";
    }

    public String getLoginBg(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Connection dbConn = null;
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            dbConn = requestDbConn.getSysDbConn();
            T9SystemLogic logic = new T9SystemLogic();
            String path = logic.getLoginBg(dbConn);
            String sp = System.getProperty("file.separator");
            path = T9SysProps.getAttachPath() + sp + "system" + sp + path;
            File file = new File(path);
            if (file.exists() && file.isFile()) {
                FileInputStream fis = new FileInputStream(file);
                response.setContentType("image/" + path.replaceAll(".*\\.", ""));
                OutputStream out = response.getOutputStream();
                byte[] b = new byte[1024];
                int i = 0;
                while ((i = fis.read(b)) > 0) {
                    out.write(b, 0, i);
                }
                out.flush();
            } else {
                String template = T9SystemAct.queryTemplate(request);
                return template = "/core/templates/" + template + "/img/login_bg.jpg";
            }
        } catch (Exception ex) {
            String template = T9SystemAct.queryTemplate(request);
            return template = "/core/templates/" + template + "/img/login_bg.jpg";
        }
        return "";
    }

    public static int getStyleIndex(HttpServletRequest request) {
        try {
            T9Person person = (T9Person) request.getSession().getAttribute("LOGIN_USER");
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            Connection dbConn = requestDbConn.getSysDbConn();

            int styleIndex = T9SystemLogic.getStyleIndex(dbConn);

            if (styleIndex == 0) {
                if (person != null) {
                    styleIndex = Integer.parseInt(person.getTheme());

                    if (styleIndex == 0) {
                        styleIndex = Integer.parseInt(person.getTheme());
                    }
                } else {
                    styleIndex = 1;
                }
            }
            return styleIndex;
        } catch (Exception e) {
            return 1;
        }
    }

    public static String queryTemplate(HttpServletRequest request) {
        Connection dbConn = null;
        String template = "default";
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            dbConn = requestDbConn.getSysDbConn();
            template = T9SystemLogic.queryTemplate(dbConn);
            if (template == null || "".equals(template.trim())) {
                template = "default";
            }
        } catch (Exception e) {

        }
        return template;
    }

    /**
     * 解析目录url的格式,比如http:// | ftp:// | javascript: 等的情况处理
     * 
     * @param url
     * @param contextPath
     * @return
     */
    public static String parseMenuUrl(String url, String contextPath, HttpServletRequest request) {

        if (url.startsWith("/")) {
            return contextPath + url;
        } else if (url.toLowerCase().startsWith("javascript:")) {
            return url;
        } else if (url.contains("://")) {
            return url;
        } else if (url.startsWith("@1/")) {
            // erp菜单使用
            return "/t9erp" + url.replaceFirst("@1", "");
        } else if (url.startsWith("@2/")) {
            // 报表的路径问题

            String u = request.getRequestURL().toString();
            if (T9Utility.isNullorEmpty(u)) {
                return url;
            }
            String host = u.substring(0, u.indexOf("/t9/t9/"));
            if (T9Utility.isNullorEmpty(host)) {
                return url;
            }
            String port = T9SysProps.getProp("REPORT_SSO_PORT");
            if (T9Utility.isNullorEmpty(port)) {
                return url;
            }
            int tmpInt = host.indexOf(":", 5);
            if (tmpInt > 0) {
                host = host.substring(0, tmpInt);
            }
            return host + ":" + port + "/" + url.replaceFirst("@2/", "");
        } else if (url.startsWith("@3/")) {
            // 报表的路径问题

            String u = request.getRequestURL().toString();
            if (T9Utility.isNullorEmpty(u)) {
                return url;
            }
            String host = u.substring(0, u.indexOf("/t9/t9/"));
            if (T9Utility.isNullorEmpty(host)) {
                return url;
            }
            String port = T9SysProps.getProp("OA_SSO_PORT");
            if (T9Utility.isNullorEmpty(port)) {
                return url;
            }
            int tmpInt = host.indexOf(":", 5);
            if (tmpInt > 0) {
                host = host.substring(0, tmpInt);
            }
            return host + ":" + port + "/" + url.replaceFirst("@3/", "");
        } else {
            return contextPath + T9SystemAct.DEFAULT_PATH + url;
        }
    }

    /**
     * 解析目录url的格式,比如http:// | ftp:// | javascript: 等的情况处理
     * 
     * @param url
     * @param contextPath
     * @return
     */
    public static String parseMenuUrl(String url, String contextPath) {
        return parseMenuUrl(url, contextPath, null);
    }

    /**
     * 当菜单的图标不存在时,赋给一个默认的图标
     * 
     * @param menu
     */
    public static void parseMenuIcon(T9Menu menu) {
        parseMenuIcon(menu, WEBOS_IMAGE_PATH);
    }

    public static void parseMenuIcon(T9Menu menu, String folder) {
        if (menu != null) {
            String iconSrc = menu.getIcon();
            if (folder.startsWith("/")) {
                folder = T9SysProps.getWebPath() + folder;
            }
            File icon = new File(folder + iconSrc);
            if (!icon.exists() || !icon.isFile()) {
                menu.setIcon("default.png");
            }
        }
    }
}