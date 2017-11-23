package t9.core.funcs.workflow.util;

import java.sql.Connection;
import java.util.Map;

/**
 * 工作流业务引擎插件接口
 * @author yzq
 *
 */
public interface T9IWFHookPlugin {
  /**
   * 工作流结束时执行
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception 
   */
  public String execute(Connection conn , int runId  , Map arrayHandler , Map formData , boolean  agree ) throws Exception;
}
