package t9.rad.devmgr.act;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9Props;
import t9.core.dto.T9CodeLoadParam;
import t9.core.dto.T9CodeLoadParamSet;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.form.T9FOM;
import t9.rad.devmgr.global.T9RadDevMgrConst;
import t9.rad.devmgr.util.T9DocInfoUtility;

public class T9SubSysListAct {
  /**
   * 加载下拉框数据
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String loadList(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    try {
      T9CodeLoadParamSet paramSet = (T9CodeLoadParamSet)T9FOM.build(request.getParameterMap());
      String ctxPath = (String)request.getAttribute(T9ActionKeys.ACT_CTX_PATH);
      
      T9CodeLoadParam param = paramSet.getParam();
      if (param == null) {
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, "没有传递必要的参数");
        return "/core/inc/rtjson.jsp";
      }
      String basePath = request.getParameter("basePath");
      if (T9Utility.isNullorEmpty(basePath)) {
        basePath = "rad\\devmgr\\";
      }else {
        basePath = basePath.replaceAll("/", "\\");
      }
      String subsysPath = ctxPath + basePath;
      List<T9Props> subsysList = T9DocInfoUtility.loadInfoList(subsysPath);
      if (T9Utility.isNullorEmpty(param.getValue()) && subsysList.size() > 0) {
        param.setValue(subsysList.get(0).get(T9RadDevMgrConst.ENTRY_DIR));
      }
      String rtJson = T9DocInfoUtility.toSelectJson(subsysList,
          param.getCntrlId(), param.getValue());
      
      request.setAttribute(T9ActionKeys.RET_DATA, rtJson);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "加载子系统列表失败" + ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
}
