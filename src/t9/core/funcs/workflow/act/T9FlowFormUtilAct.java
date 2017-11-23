package t9.core.funcs.workflow.act;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.dept.data.T9Department;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.workflow.data.T9FlowFormType;
import t9.core.funcs.workflow.util.T9FlowFormLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;
import t9.rad.velocity.T9velocityUtil;

public class T9FlowFormUtilAct {
  private static Logger log = Logger.getLogger("t9.core.funcs.workflow.act.T9FlowFormUtilAct");
  public String autoProduce(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    String module = request.getParameter("module");
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      int i = module.lastIndexOf(".");
      String packageName = "t9.user." + module.substring(0, i);
      String path = "D:/project/t9/src/" + packageName.replace(".", "/");
      String className = module.substring(i+1);
      Map map = new HashMap();
      map.put("className", className);
      map.put("packageName", packageName);
      map.put("fileName", className + ".java");
      File file = new File(path);
      if (!file.exists()){
        file.mkdirs();
      }
      String fileName = module.substring(0, i).replace(".", "/");
      String editpath = "/user/" + fileName + "/edit.jsp";
      String printpath = "/user/" + fileName + "/print.jsp";
      map.put("editPath", editpath);
      map.put("printPath", printpath);
      T9velocityUtil.velocity(map, path , "module.vm", "D:/project/t9/src/t9/core/funcs/workflow/util/vm");
      String contextPath = "D:/project/t9/webroot/t9";
      editpath = contextPath + "/user/" + fileName;
      map = new HashMap();
      map.put("fileName", "edit.jsp");
      velocity(map, editpath , "moduleJsp.vm", "D:/project/t9/src/t9/core/funcs/workflow/util/vm");
      map = new HashMap();
      map.put("fileName", "print.jsp");
      velocity(map, editpath , "moduleJsp.vm", "D:/project/t9/src/t9/core/funcs/workflow/util/vm");
       
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功返回结果！");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

  public static void velocity(Map request, String url, String templateName,
      String templateUrl){
    // 获取模板引擎
    VelocityEngine ve = new VelocityEngine();
    // 模板文件所在的路径
    // String path = System.getProperty("user.dir") + SUB_PATH;
    // String path = System.getProperty("user.dir") + SUB_PATH;
    //templateUrl = "D:\\project\\t9\\templates\\db";
    // 设置参数
    //System.out.println(templateUrl);
    ve.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, templateUrl);
    // 处理中文问题
    ve.setProperty(Velocity.INPUT_ENCODING, "GBK");
    ve.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
    Writer wt = null;
    try{
      // 初始化模板

      ve.init();
      // 获取模板(template.vm)
      Template template = ve.getTemplate(templateName);
      // 获取上下文

      VelocityContext root = new VelocityContext();
      // 把数据填入上下文
      // String className = ((String[])request.get("className"))[0];
      
      Iterator<String> keyIter = request.keySet().iterator();
      while (keyIter.hasNext()){
        String key = keyIter.next();
        Object value = null;
        try {
          value = ((String[])request.get(key))[0];
        }catch (Exception e){
            value = request.get(key);
        }
        root.put(key, value);
      }
      // 输出路径
      String outpath = url + File.separator + request.get("fileName");
      File dir = new File(url);
      if (!dir.exists()){
        dir.mkdirs();
      }
      File file = new File(outpath);
      if (!file.exists()){
        file.createNewFile();
      }
      FileOutputStream fs = new FileOutputStream(file);
      wt = new PrintWriter(fs);
      template.merge(root, wt);
      wt.flush();
    } catch (Exception e){
      e.printStackTrace();
    } finally{
      if (wt != null){
        try{
          wt.close();
        } catch (IOException e){
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    }
  }
}
