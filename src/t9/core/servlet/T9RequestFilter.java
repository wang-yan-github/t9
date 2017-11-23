package t9.core.servlet;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.mobile.util.T9MobileUtility;

/**
 * 处理Request的过滤器
 * 
 * @author yzq
 * 
 */
public class T9RequestFilter implements Filter {
    // 编码
    protected String encoding = null;
    // 过滤器配置

    protected FilterConfig filterConfig = null;

    /**
     * 释放资源
     */
    public void destroy() {
        this.encoding = null;
        this.filterConfig = null;
    }

    private Map<String, String> decryptMap(ServletRequest request) {
        Map<String, String> paramMap = new HashMap<String, String>();
        Map<String, String[]> map_ = request.getParameterMap();
        if (map_ != null && map_.size() > 0) {
            for (String key_ : map_.keySet()) {
                String[] values = map_.get(key_);
                for (int i = 0; i < values.length; i++) {
                    String value = values[i];
                    paramMap.put(key_, value);
                }
            }
        }
        return paramMap;
    }

    /**
     * 执行过滤器
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpSession session = ((HttpServletRequest) request).getSession();
        String qUri = ((HttpServletRequest) request).getRequestURI();
        if (qUri.endsWith("/")) {
            qUri += "index.jsp";
        }
        // System.out.println(new Date() + "====" + qUri);
        if (qUri.endsWith(".act") && !qUri.endsWith("/remindCheck2.act")
                && !qUri.endsWith("/updateOnlineStatus.act") && !qUri.endsWith("/queryUserCount.act")) {
            System.out.println(new Date() + "====" + qUri);
            System.out.println("params==" + decryptMap(request));
            System.out.println();
        }

        /**
         * 验证是否mWebView已经登录
         */
        if ((qUri.endsWith(".jsp") || qUri.endsWith(".act"))
                && !T9ServletUtility.isLoginAction((HttpServletRequest) request)) {
            String sessionId = request.getParameter("sessionid");
            // }
            if (!T9Utility.isNullorEmpty(sessionId)) {
                session = (HttpSession) T9SessionListener.getSessaionContextMap().get(sessionId);
                if (session == null) {
                    try {
                        T9MobileUtility.output((HttpServletResponse) response,
                                T9MobileUtility.getResultJson(0, "登录失效", null));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return;
                }
                T9Person person = (T9Person) session.getAttribute(T9Const.LOGIN_USER);
                if (person == null) {
                    try {
                        T9MobileUtility.output((HttpServletResponse) response,
                                T9MobileUtility.getResultJson(0, "登录失效", null));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return;
                }
                ((HttpServletRequest) request).getSession().setAttribute(T9Const.LOGIN_USER, person);
            } else {
                if (qUri.contains("/mobile")) {// 手机端的操作--手机端必须带sessionid
                    try {
                        T9MobileUtility.output((HttpServletResponse) response,
                                T9MobileUtility.getResultJson(0, "登录失效", null));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return;
                }
            }
            if (!T9ServletUtility.isValidSession(session, "LOGIN_USER")) {
                T9ServletUtility.forward("/core/inc/sessionerror.jsp", (HttpServletRequest) request,
                        (HttpServletResponse) response);
                return;
            }
        }

        if (qUri.endsWith("t9/core/funcs/sms/act/T9SmsAct/remindCheck.act")) {
            Long lastOptTime = (Long) session.getAttribute("LAST_OPT_TIME");
            Long lockSec = (Long) session.getAttribute("OFFLINE_TIME_MIN");

            // 设置了空闲强制自动离线时间并且上次操作时间不为空时判断是否过期
            if (lockSec > 0 && lastOptTime != null && lastOptTime + lockSec < new Date().getTime()) {

                request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
                request.setAttribute(T9ActionKeys.RET_MSRG, "空闲自动离线");
                request.setAttribute(T9ActionKeys.RET_DATA, "-1");
                T9ServletUtility.forward("/core/inc/rtjson.jsp", (HttpServletRequest) request,
                        (HttpServletResponse) response);
                return;
            }
        } else {
            session.setAttribute("LAST_OPT_TIME", new Date().getTime());
        }

        // request.setCharacterEncoding(encoding);
        response.setCharacterEncoding(encoding);
        chain.doFilter(request, response);
    }

    /**
     * 过滤器初始化
     */
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
        this.encoding = filterConfig.getInitParameter("encoding");
    }

    /**
     * 取得编码
     * 
     * @return
     */
    protected String getEncoding() {
        return (this.encoding);
    }
}
