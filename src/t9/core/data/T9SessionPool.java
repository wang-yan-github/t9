package t9.core.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import t9.core.global.T9ActionKeys;
import t9.core.global.T9Const;
import t9.core.global.T9SysPropKeys;
import t9.core.global.T9SysProps;
import t9.core.servlet.T9ServletUtility;
import t9.core.util.T9Utility;

/**
 * 会话状态存储池
 * @author yzq
 *
 */
public class T9SessionPool {
  /**
   * 是否执行释放资源线程检查
   */
  private static boolean isRelease = false;
  /**
   * 释放资源检查线程
   */
  private static Thread releaseThread = null;
  /**
   * session的生命周期分钟数
   */
  private static long SESSION_VALID_TIME = T9SysProps.getLong(T9SysPropKeys.SESSION_VALID_MINUTS) * T9Const.DT_MINIT;
  /**
   * 会话状态存储哈希表
   */
  private static Map<String, Map<String, Object>> sessionPool = Collections.synchronizedMap(new HashMap<String, Map<String, Object>>());
  /**
   * 会话最后访问时间，用于释放存储空间
   */
  private static Map<String, Long> visitTimePool = new HashMap<String, Long>();

  static {
    startReleaseThread();
  }
  /**
   * 启动释放资源的线程
   */
  private static void startReleaseThread() {
    stopReleaseThread();
    releaseThread = new Thread(new Runnable() {
      public void run() {
        while (isRelease) {
          try {
            Thread.sleep(T9Const.DT_MINIT);
            T9SessionPool.releaseSpace();
          }catch(Exception ex) {
          }
        }
      }});
    isRelease = true;
    releaseThread.start();
  }
  /**
   * 停止释放资源的线程
   */
  public static void stopReleaseThread() {
    isRelease = false;
    if (releaseThread != null) {
      try {
        releaseThread.interrupt();
        releaseThread = null;
      }catch(Exception ex) {        
      }
    }
  }
  /**
   * 释放不再使用的空间
   */
  public static void releaseSpace() {
    if (sessionPool == null || visitTimePool == null) {
      return;
    }
    Iterator<String> iKeys = sessionPool.keySet().iterator();
    List<String> removeKeyList = new ArrayList<String>();
    long currTime = System.currentTimeMillis();
    while (iKeys.hasNext()) {
      String key = iKeys.next();
      Long visitTime = visitTimePool.get(key);
      if (currTime - visitTime > SESSION_VALID_TIME) {
        removeKeyList.add(key);
      }
    }
    iKeys = removeKeyList.iterator();
    while (iKeys.hasNext()) {
      String key = iKeys.next();
      sessionPool.remove(key);
      visitTimePool.remove(key);
    }
  }
  /**
   * 取得对象属性
   * @param sessionId
   * @param attName
   * @return
   */
  public static Object getObject(HttpServletRequest request, String attName) throws Exception {
    return getObject(getSessionId(request), attName);
  }
  
  /**
   * 取得字符串属性
   * @param sessionId
   * @param attName
   * @return
   */
  public static String getString(HttpServletRequest request, String attName) throws Exception {
    return getString(getSessionId(request), attName);
  }
  /**
   * 取得字符串属性
   * @param sessionId
   * @param attName
   * @return
   */
  public static String getString(String sessionId, String attName) {
    return (String)getObject(sessionId, attName);
  }
  /**
   * 取得对象属性
   * @param sessionId
   * @param attName
   * @return
   */
  public static Object getObject(String sessionId, String attName) {
    if (T9Utility.isNullorEmpty(sessionId) || T9Utility.isNullorEmpty(attName)) {
      return null;
    }
    Map session = sessionPool.get(sessionId);
    if (session == null) {
      return null;
    }
    visitTimePool.put(sessionId, new Long(System.currentTimeMillis()));
    return session.get(attName);
  }
  /**
   * 取得会话Id
   * @param request
   * @return
   * @throws Exception
   */
  private static String getSessionId(HttpServletRequest request) throws Exception {
    String sessionId = T9ServletUtility.getCookieValue(request, T9ActionKeys.T9_SESSION_ID);
    if (T9Utility.isNullorEmpty(sessionId)) {
      sessionId = request.getParameter(T9ActionKeys.T9_SESSION_ID);
    }else {
      HttpSession currSession = request.getSession(false);
      if (currSession != null) {
        sessionId = currSession.getId();
      }
    }
    return sessionId;
  }
  /**
   * 新增加属性
   * @param sessionId
   */
  public static void setAttribute(String sessionId, String attrName, String attrValue) {
    Map session = sessionPool.get(sessionId);
    if (session == null) {
      session = new HashMap();
      sessionPool.put(sessionId, session);
      visitTimePool.put(sessionId, new Long(System.currentTimeMillis()));
    }
    session.put(attrName, attrValue);
  }

  /**
   * 删除属性
   * @param sessionId
   */
  public static void removeAttribute(String sessionId, String attrName) {
    Map session = sessionPool.get(sessionId);
    if (session == null) {
      return;
    }
    session.remove(attrName);
  }
  /**
   * 删除session
   * @param sessionId
   */
  public static void removeSession(String sessionId) {
    sessionPool.remove(sessionId);
  }
  /**
   * 删除session
   * @param sessionId
   */
  public static void removeSession(HttpServletRequest request) throws Exception {
    sessionPool.remove(getSessionId(request));
  }
}
