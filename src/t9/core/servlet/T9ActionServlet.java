package t9.core.servlet;

import java.io.IOException;
import java.lang.reflect.Method;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.autorun.T9AutoRunThread;
import t9.core.data.T9DataSources;
import t9.core.data.T9RequestDbConn;
import t9.core.data.T9SessionPool;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.load.T9ConfigLoader;
import t9.core.util.T9Utility;

public class T9ActionServlet extends HttpServlet {
    private static Logger log = Logger.getLogger("yzq.t9.core.servlet.TDCActionServlet");

    /**
     * 系统初始化
     */
    public void init() throws ServletException {
        super.init();
        try {
            String rootPath = getServletContext().getRealPath("/");
            T9ConfigLoader.loadInit(rootPath);
        } catch (Exception ex) {
            ex.printStackTrace();
            log.debug(ex.getMessage(), ex);
        }
    }

    /**
     * 释放资源
     */
    public void destroy() {
        System.out.println("T9 start destroy...");
        log.debug("T9 start destroy...");
        T9SessionPool.stopReleaseThread();
        T9DataSources.closeConnPool();
        T9AutoRunThread.stopRun();
        System.out.println("T9 end destroy.");
        log.debug("T9 end destroy.");
    }

    /**
     * <p>
     * Process an HTTP "GET" request.
     * </p>
     * 
     * @param request
     *            The servlet request we are processing
     * @param response
     *            The servlet response we are creating
     * 
     * @exception IOException
     *                if an input/output error occurs
     * @exception ServletException
     *                if a servlet exception occurs
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException,
            ServletException {

        response.setHeader("PRagma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        request.setAttribute(T9ActionKeys.ACT_CTX_PATH,
                T9ServletUtility.getWebAppDir(this.getServletContext()));
        String className = request.getParameter(T9ActionKeys.ACT_CLASS);
        String methodName = request.getParameter(T9ActionKeys.ACT_METHOD);
        if (T9Utility.isNullorEmpty(className)) {
            String qUri = request.getRequestURI();
            qUri = qUri.substring(request.getContextPath().length() + 1);
            int tmpIndex = qUri.lastIndexOf(".");
            if (tmpIndex > 0) {
                int tmpIndex2 = qUri.lastIndexOf("/");
                if (tmpIndex2 > 0) {
                    className = qUri.substring(0, tmpIndex2);
                    methodName = qUri.substring(tmpIndex2 + 1, tmpIndex);
                }
            }
        }
        String rtType = T9ServletUtility.getParam(request, T9ActionKeys.RET_TYPE);
        String rtUrl = null;
        if (rtType.equals(T9Const.RET_TYPE_XML)) {
            rtUrl = "/core/inc/rtxml.jsp";
        } else {
            rtUrl = "/core/inc/rtjson.jsp";
        }
        T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
        try {
            if (T9Utility.isNullorEmpty(className)) {
                request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
                request.setAttribute(T9ActionKeys.RET_MSRG, "没有传递处理类名称");
                T9ServletUtility.forward(rtUrl, request, response);
                return;
            }
            if (T9Utility.isNullorEmpty(methodName)) {
                request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
                request.setAttribute(T9ActionKeys.RET_MSRG, "没有传递处理方法名称");
                T9ServletUtility.forward(rtUrl, request, response);
                return;
            }
            Class classObj = Class.forName(className.replace("/", "."));
            Class[] paramTypeArray = new Class[] { HttpServletRequest.class, HttpServletResponse.class };
            Method methodObj = classObj.getMethod(methodName, paramTypeArray);
            String forWardUrl = (String) methodObj.invoke(classObj.newInstance(), new Object[] { request,
                    response });
            if (requestDbConn != null) {
                requestDbConn.commitAllDbConns();
            }
            if (!T9Utility.isNullorEmpty(forWardUrl)) {
                String retMethod = (String) request.getAttribute(T9ActionKeys.RET_METHOD);
                if (T9Utility.isNullorEmpty(retMethod)) {
                    T9ServletUtility.forward(forWardUrl, request, response);
                } else {
                    String contextPath = request.getContextPath();
                    if (T9Utility.isNullorEmpty(contextPath)) {
                        contextPath = "/t9";
                    }
                    response.sendRedirect(contextPath + forWardUrl);
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
            log.debug(t.getMessage(), t);
            if (requestDbConn != null) {
                requestDbConn.rollbackAllDbConns();
            }
            if (!response.isCommitted()) {
                String forwardPath = (String) request.getAttribute(T9ActionKeys.FORWARD_PATH);// T9ActionKeys.FORWARD_PATH);
                if (forwardPath == null) {
                    forwardPath = rtUrl;
                }
                T9ServletUtility.forward(forwardPath, request, response);
            }
        } finally {
        }
    }

    /**
     * <p>
     * Process an HTTP "POST" request.
     * </p>
     * 
     * @param request
     *            The servlet request we are processing
     * @param response
     *            The servlet response we are creating
     * 
     * @exception IOException
     *                if an input/output error occurs
     * @exception ServletException
     *                if a servlet exception occurs
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException,
            ServletException {

        doGet(request, response);
    }
}
