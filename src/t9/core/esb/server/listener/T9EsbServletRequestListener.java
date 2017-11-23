package t9.core.esb.server.listener;

import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import t9.core.esb.server.user.data.TdUser;
import t9.core.util.T9Guid;
import t9.core.util.T9Utility;

/**
 * 请求监听者
 * @author yzq
 *
 */
public class T9EsbServletRequestListener implements ServletRequestListener {
  private static Logger log = Logger.getLogger("esb");
  public void requestInitialized(ServletRequestEvent sre) {
    HttpServletRequest request = (HttpServletRequest)sre.getServletRequest();
    ServletContext sc = sre.getServletContext();
    String listenUser = T9Utility.null2Empty((String)sc.getAttribute("listenUser"));
    if (!T9Utility.isNullorEmpty(listenUser)) {
      HttpSession session = request.getSession();
      TdUser user = (TdUser)session.getAttribute("ESB_LOGIN_USER");
      if (user != null && listenUser.equals(user.getUserCode())) {
        StringBuffer sb = new StringBuffer();
        String url = request.getRequestURI();
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm:ss.SSS");
        String requestGuid = "";
        try {
          requestGuid = T9Guid.getRawGuid();
        } catch (NoSuchAlgorithmException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        } 
        String dateStr = sdf.format(date);
        sb.append("{\n");
        sb.append("标示:" + requestGuid + "\n");
        sb.append("用户:" + user.getUserName() + "\n");
        sb.append("地址:" + url + "\n");
        sb.append("时间:" + dateStr + "\n");
        sb.append("头信息:\n");
        Enumeration e = request.getHeaderNames(); 
        while(e.hasMoreElements()){   
          String a = (String)e.nextElement();  
          sb.append("  " + a + ":" + request.getHeader(a) + "\n");
        }
        sb.append("内容:\n");
        this.getParameterMap(request, sb);
        sb.append("}\n");
        request.setAttribute("requestGuid", requestGuid);
        request.setAttribute("startTime", date.getTime());
        request.setAttribute("startTimeStr", dateStr);
        //System.out.println(sb.toString());
        log.debug(sb.toString());
      }
    }
  }
  public void requestDestroyed(ServletRequestEvent sre) {    
    HttpServletRequest request = (HttpServletRequest)sre.getServletRequest();
    ServletContext sc = sre.getServletContext();
    String listenUser = T9Utility.null2Empty((String)sc.getAttribute("listenUser"));
    if (!T9Utility.isNullorEmpty(listenUser)) {
      HttpSession session = request.getSession();
      TdUser user = (TdUser)session.getAttribute("ESB_LOGIN_USER");
      if (user != null && listenUser.equals(user.getUserCode())) {
        String requestGuid = (String) request.getAttribute("requestGuid");
        String startTimeStr = (String) request.getAttribute("startTimeStr");
        long start = (Long)request.getAttribute("startTime");
        long end = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm:ss.SSS");
        String dateStr = sdf.format(new Date(end));
        
        StringBuffer sb = new StringBuffer();
        sb.append("{\n");
        sb.append("标示:" + requestGuid + "\n");
        sb.append("开始:" + startTimeStr + "\n");
        sb.append("结束:" + dateStr + "\n");
        sb.append("耗时:" + (end - start) + "毫秒\n");
        sb.append("}\n");
        request.removeAttribute("requestGuid");
        request.removeAttribute("startTime");
        request.removeAttribute("startTimeStr");
        log.debug(sb.toString());
        //System.out.println(sb.toString());
      }
    }
  }
  public static void getParameterMap(HttpServletRequest request , StringBuffer sb ){
    // 参数Map
    Map properties = request.getParameterMap();
    // 返回值Map
    Iterator entries = properties.entrySet().iterator();
    Map.Entry entry;
    String name = "";
    String value = "";
    while (entries.hasNext()) {
      entry = (Map.Entry) entries.next();
      name = (String) entry.getKey();
      Object valueObj = entry.getValue();
      if(null == valueObj){
        value = "";
      }else if(valueObj instanceof String[]){
        String[] values = (String[])valueObj;
        for(int i=0;i<values.length;i++){
          value = values[i] + ",";
        }
        value = value.substring(0, value.length()-1);
      }else{
        value = valueObj.toString();
      }
      sb.append("  " + name + ":" + value + "\n");
    }
  }
}
