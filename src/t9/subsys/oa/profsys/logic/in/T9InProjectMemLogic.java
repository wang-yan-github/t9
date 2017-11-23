package t9.subsys.oa.profsys.logic.in;

import java.sql.Connection;
import java.util.Map;

import org.apache.log4j.Logger;

import t9.core.data.T9PageDataList;
import t9.core.data.T9PageQueryParam;
import t9.core.load.T9PageLoader;
import t9.core.util.form.T9FOM;

public class T9InProjectMemLogic {
  private static Logger log = Logger.getLogger("t9.subsys.oa.profsys.act.T9ProjectMenLogic");
  /**
   * 分页列表
   * @param conn
   * @param request
   * @return
   * @throws Exception
   */
  public String toSearchData(Connection conn,Map request,String projId,String projStatus) throws Exception{
    String sql = "select pm.SEQ_ID,pm.MEM_NUM,pm.MEM_POSITION,pm.MEM_NAME,pm.MEM_SEX,pm.MEM_BIRTH,pm.MEM_ID_NUM,"
      + "pm.MEM_PHONE,pm.MEM_MAIL,pm.MEM_FAX,pm.MEM_ADDRESS,pm.ATTACHMENT_ID,pm.ATTACHMENT_NAME"
      +" from PROJECT_MEM pm "
      +" where pm.PROJ_ID = " + projId  + " and pm.PROJ_MEM_TYPE = '0'" ;
    T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request);
    T9PageDataList pageDataList = T9PageLoader.loadPageList(conn,queryParam,sql);
    return pageDataList.toJson();
  }
}
