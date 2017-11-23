package t9.core.funcs.system.resManage.act;

import java.io.File;
import java.io.FileFilter;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import t9.core.funcs.portal.act.T9PortAct;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9Const;
import t9.core.global.T9SysProps;
import t9.core.util.T9Utility;
import t9.core.util.file.T9FileUtility;

/**
 * 
 * @author pjn
 * 
 */
public class T9LogAct {
  private static Logger log = Logger
    .getLogger("t9.core.funcs.system.resManage.act.T9LogAct");
  private String sp = File.separator;
  
  /**
   * 下载日志文件的统一方法
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String download(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    try {
      String root = T9SysProps.getRootPath();
      String webroot = T9SysProps.getWebPath();
      String type = request.getParameter("type");
      List<String> files = new ArrayList<String>();
      if ("T9".equalsIgnoreCase(type)) {
        addT9LogFiles(root, webroot, files);
        download(response, files, "t9-log");
        renameT9Logs(root, webroot, files);
      }
      else if ("IM".equalsIgnoreCase(type)) {
        addImLogFiles(root, webroot, files);
        download(response, files, "IM-log");
      }
      else if ("webserver".equalsIgnoreCase(type)) {
        addWebServerFiles(root, webroot, files);
        download(response, files, "webserver-log");
      }
      else if ("all".equalsIgnoreCase(type)) {
        addT9LogFiles(root, webroot, files);
        addImLogFiles(root, webroot, files);
        addWebServerFiles(root, webroot, files);
        download(response, files, "all-log");
        renameT9Logs(root, webroot, files);
      }
    } catch (Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "";
  }
  
  /**
   * 打包下载的方法
   * @param response
   * @param files
   * @param name
   * @throws Exception
   */
  private void download(HttpServletResponse response, List<String> files, String name) throws Exception {
    T9PortAct port = new T9PortAct();
    SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");//设置日期格式
    response.setHeader("Content-disposition", "attachment; filename=" + name + "-"
        + df.format(new Date()) + ".zip");
    response.setHeader("Cache-Control",
    "must-revalidate, post-check=0, pre-check=0,private, max-age=0");
    response.setHeader("Content-Type", "application/octet-stream");
    response.setHeader("Content-Type", "application/force-download");
    response.setHeader("Pragma", "public");
    response.setHeader("Cache-Control","maxage=3600");
    response.setHeader("Pragma","public");
    response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
    OutputStream out = response.getOutputStream();
    port.zip(out, files);
    out.flush();
  }
  
  /**
   * 把debug下的最新log加入到文件数组中
   * 两个文件都叫做debug.log,这里把文件改了名字
   * @param root
   * @param webroot
   * @param files
   * @throws Exception 
   */
  private void addT9LogFiles(String root, String webroot, List<String> files) throws Exception {
    String t9Path = webroot + sp + "debug" + sp + "t9" + sp;
    String yzqPath = webroot + sp + "debug" + sp + "yzq" + sp;
    String esbPath = webroot + sp + "debug" + sp + "esb" + sp;
    File t9 = new File(t9Path + "debug.log");
    File yzq = new File(yzqPath + "debug.log");
    File esb = new File(esbPath + "debug.log");
    
    File nt9 = new File(t9Path + "t9.log");
    File nyzq = new File(yzqPath + "yzq.log");
    File esbF = new File(esbPath + "esb.log");
    T9FileUtility.copyFile(t9.getAbsolutePath(), nt9.getAbsolutePath());
    T9FileUtility.copyFile(yzq.getAbsolutePath(), nyzq.getAbsolutePath());
    T9FileUtility.copyFile(esb.getAbsolutePath(), esbF.getAbsolutePath());
    files.add(t9Path + "t9.log");
    files.add(yzqPath + "yzq.log");
    files.add(esbPath + "esb.log");
  }
  
  /**
   * 把im的最新log加到文件数组中
   * @param root
   * @param webroot
   * @param files
   */
  private void addImLogFiles(String root, String webroot, List<String> files) {
    String imPath = root + sp + "IM" + sp + "OfficeIm" + sp;
    File im = new File(imPath);
    long max = 0;
    String newFile = null;
    if (im != null && im.exists() && im.isDirectory()) {
      for (File f : im.listFiles()) {
        if (max < f.lastModified()) {
          max = f.lastModified();
          newFile = f.getAbsolutePath();
        }
      }
      if (!T9Utility.isNullorEmpty(newFile)) {
        files.add(newFile);
      }
    }
  }
  
  /**
   * 把tomcat的最新logs加入到文件数组中
   * @param root
   * @param webroot
   * @param files
   */
  private void addWebServerFiles(String root, String webroot, List<String> files) {
    String wsPath = root + sp + "tomcat" + sp + "logs";
    final File f = new File(wsPath);
    File[] fs = f.listFiles(new FileFilter() {
      public boolean accept(File arg0) {
        if (arg0.getName().startsWith("stdout") || arg0.getName().startsWith("stderr")) {
          for (File t : f.listFiles()) {
            if (t.getName().startsWith(arg0.getName().substring(0, 4)) && t.lastModified() > arg0.lastModified()) {
              return false;
            }
          }
          return true;
        }
        else {
          return false;
        }
      }
    });
    if (fs != null && fs.length > 0) {
      for (File t : fs) {
        files.add(t.getAbsolutePath());
      }
    }
  }
  
  /**
   * 把debug下的文件名字改回去
   * @param root
   * @param webroot
   * @param files
   */
  private void renameT9Logs(String root, String webroot, List<String> files) {
    String t9Path = webroot + sp + "debug" + sp + "t9" + sp;
    String yzqPath = webroot + sp + "debug" + sp + "yzq" + sp;
    File nt9 = new File(t9Path + "t9.log");
    File nyzq = new File(yzqPath + "yzq.log");
    nt9.deleteOnExit();
    nyzq.deleteOnExit();
  }
}
