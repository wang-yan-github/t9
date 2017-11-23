package t9.core.funcs.autorunmgr.act;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.autorun.T9AutoRunConfig;
import t9.core.autorun.T9AutoRunThread;
import t9.core.funcs.autorunmgr.logic.T9AutoRunManager;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9Const;
import t9.core.util.form.T9FOM;

/**
 * 后台服务进程
 * @author tulaike
 *
 */
public class T9AutoRunManagerAct {
  /**
   * 取得所有后台服务的配置
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getAutoRunCfgs(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    try {
      T9AutoRunManager arm  =  new T9AutoRunManager();
      String data  = arm.getAutoRunCfgList2Json().toString();
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      ex.printStackTrace();
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 根据名称取得后台服务的配置
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getAutoRunCfg(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    try {
      String cfgname = request.getParameter("cfgname");
      T9AutoRunManager arm  =  new T9AutoRunManager();
      String data  = "";
      data = T9FOM.toJson(arm.getAutoRunCfgByName(cfgname)).toString();
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 根据名称取得后台服务的配置
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String deleteAutoRunCfg(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    try {
      String cfgname = request.getParameter("cfgname");
      T9AutoRunManager arm  =  new T9AutoRunManager();
      arm.deletePropertiesByName(cfgname);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "删除后台服务成功!");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 根据名称修改一个配置
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String updateAutoRunCfg(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    try {
      String cfgname = request.getParameter("cfgname");
      T9AutoRunManager arm  =  new T9AutoRunManager();
      T9AutoRunConfig arc = (T9AutoRunConfig) T9FOM.build(request.getParameterMap(), T9AutoRunConfig.class, null);
      arm.updatePropertiesByName(cfgname, arc);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "修改后台服务成功!");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 怎加一个后台服务配置
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String addAutoRunCfg(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    try {
      T9AutoRunManager arm  =  new T9AutoRunManager();
      T9AutoRunConfig arc = (T9AutoRunConfig) T9FOM.build(request.getParameterMap(), T9AutoRunConfig.class, null);
      String cfgname = "autoRunTask" + arm.getAutoRunName();
      arm.updatePropertiesByName(cfgname, arc);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "注册服务添加成功!");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 得到主线程的服务
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getMainThreadStatus(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    try {
      T9AutoRunManager arm  =  new T9AutoRunManager();
      String data = arm.getMainThreadStatus();
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 执行主线程
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String execMainThread(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    try {
      String type = request.getParameter("type");
      T9AutoRunManager arm  =  new T9AutoRunManager();
      arm.execMainThreadStatus(Integer.parseInt(type));
      String msrg = "服务启动成功!";
      if("1".equals(type)){
        msrg = "服务停止成功!";
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, msrg);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 执行子线程
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String execSubThread(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    try {
      String id = request.getParameter("subid");
      T9AutoRunManager arm  =  new T9AutoRunManager();
      int flag = arm.execSubThreadStatus(id);
      String msrg = "";
      if(flag == 0){
        msrg = "后台子服务执行成功!";
      }else if(flag == 2){
        msrg = "该服务正在运行!";
      }else if(flag == 1){
        msrg = "没有找到该服务!";
      }else if(flag == 4){
        msrg = "主服务已停止,请启动主服务后再执行此操作!";
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, msrg);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String checkClassIsInvalidity(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    try {
      String cls = request.getParameter("cls");
      T9AutoRunManager arm  =  new T9AutoRunManager();
      int flag = arm.checkClassIsInvalidity(cls);
      String data = "{isIncalidity:\"" + flag + "\"}";
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
}
