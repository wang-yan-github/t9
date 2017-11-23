package t9.subsys.inforesouce.act;
import t9.core.global.T9Const;
import t9.core.global.T9BeanKeys;
import java.sql.Connection;
import t9.core.util.T9Utility;
import t9.core.global.T9ActionKeys;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.workflow.util.T9WorkFlowUtility;
import t9.subsys.inforesouce.data.T9Kengine;
import t9.subsys.inforesouce.logic.T9AFileMateLogic;
import t9.subsys.inforesouce.logic.T9MateTypeLogic;
import t9.subsys.inforesouce.util.T9FileMateConstUtil;

/**
 * 查找文件的主题词，组织机构名，人名，地名等<br>
 * 调用T9FileMateConstUtil 常量类、T9MateTypeLogic、T9AFileMateLogic等类
 * @see t9.subsys.inforesouce.util.T9FileMateConstUtil
 * @see t9.subsys.inforesouce.logic.T9AFileMateLogic
 * @see t9.subsys.inforesouce.logic.T9AFileMateLogic
 * @author qwx110
 *
 */
public class T9AFileMateAct{
  
  /**
   * <fieldset>
   * <legend>
   * 查找文件的主题词，组织机构名，人名，地名等<br></legend>
   * <p>通过文件id返回文件的主题词，组织机构名，人名，地名；<br>
   * 调用T9MateTypeLogic的findName返回主题词，组织机构名，人名，地名等元数据的编号<br>
   * 调用T9AFileMateLogic的findString方法返回文件对应的主题词，组织机构名，人名，地名等
   * </p>
   * <fieldset>
   * @see t9.subsys.inforesouce.logic.T9MateTypeLogic#findName(Connection, String)
   * @see t9.subsys.inforesouce.logic.T9AFileMateLogic#findString(int, String, String, String, String, String, Connection)
   * @param request
   * @param response
   * @return "/core/inc/rtjson.jsp"
   * @throws Exception
   */
  public String findAmate(HttpServletRequest request, HttpServletResponse response) throws Exception{
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
    Connection dbConn = null;
   
    int seqId;
    String userName = null ;
    String areaName = null;
    String org = null;
    String subJect= null;
    String keyWord = null;
    try{
      dbConn = requestDbConn.getSysDbConn();
      T9MateTypeLogic logic = new T9MateTypeLogic();
      T9AFileMateLogic alogic = new T9AFileMateLogic();
      String attachmentId = request.getParameter("attachmentId");//文件Id
      seqId = logic.findKengine(dbConn, attachmentId);    
      userName = logic.findName(dbConn, T9FileMateConstUtil.userName);
      areaName = logic.findName(dbConn, T9FileMateConstUtil.areaName);
      org = logic.findName(dbConn, T9FileMateConstUtil.Org);
      subJect = logic.findName(dbConn, T9FileMateConstUtil.subJect);
      keyWord = logic.findName(dbConn, T9FileMateConstUtil.keyWord);
      T9Kengine ki = alogic.findString(seqId,userName,areaName,org,subJect,keyWord,dbConn);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功返回摘要内容");
      request.setAttribute(T9ActionKeys.RET_DATA,  toJson(ki) );
    } catch (Exception ex){
      String message = T9WorkFlowUtility.Message(ex.getMessage(),1);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, message);
      throw ex;
    } 
    return "/core/inc/rtjson.jsp";
  }
  
  public String toJson(T9Kengine engine){
    StringBuffer sb = new StringBuffer();
    sb.append("{");
    if(engine != null){
       if(!T9Utility.isNullorEmpty(engine.getUserName())){
         sb.append("name:").append("\"").append(T9Utility.encodeSpecial(subAString(engine.getUserName()))).append("\"").append(",");
       }else{
         sb.append("name:").append("''").append(",");
       }
       if(!T9Utility.isNullorEmpty(engine.getAreaName())){
         sb.append("area:").append("\"").append(T9Utility.encodeSpecial(subAString(engine.getAreaName()))).append("\"").append(",");
       }else{
         sb.append("area:").append("''").append(",");
       }
       if(!T9Utility.isNullorEmpty(engine.getOrgName())){
         sb.append("orge:").append("\"").append(T9Utility.encodeSpecial(subAString(engine.getOrgName()))).append("\"");
       }else{
         sb.append("orge:").append("''");
       }
    }
    sb.append("}");
    return sb.toString();
  }
  
  public String subAString(String befor){
    if(!T9Utility.isNullorEmpty(befor)){
      String[] after = befor.split(",");
      if(after.length < 3){
        return befor;
      }else{
        for(int i=0; i<3; i++){
          String temp= "";
          if(i < 2){
            temp += after[i] + ",";
          }else{
            temp += after[i];
          }
          return temp;
        }
      }
    }
    return null;
  }
  
  /**
   * 返回文件名和moudle
   * @param request
   * @param response
   * @see t9.subsys.inforesouce.logic.T9AFileMateLogic#findFileNameAndMoudle(Connection, String)
   * @return "/core/inc/rtjson.jsp"
   * @throws Exception
   */
  public String findFileNameAndMoudle(HttpServletRequest request, HttpServletResponse response) throws Exception{
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
    Connection dbConn = null;
    try{
      dbConn = requestDbConn.getSysDbConn();
      String attachmentId = request.getParameter("attachmentId");//文件Id
      T9AFileMateLogic alogic = new T9AFileMateLogic();
      String param = alogic.findFileNameAndMoudle(dbConn, attachmentId);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);     
      request.setAttribute(T9ActionKeys.RET_DATA,  param );
    } catch (Exception ex){
      String message = T9WorkFlowUtility.Message(ex.getMessage(),1);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, message);
      throw ex;
    } 
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 返回文件的人名地名组织机构名
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getAFileMate(HttpServletRequest request, HttpServletResponse response)throws Exception{
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
    Connection dbConn = null;
    try{
      dbConn = requestDbConn.getSysDbConn();
      String attachmentId = request.getParameter("attachmentId");//文件Id
      T9AFileMateLogic alogic = new T9AFileMateLogic();
      String json = alogic.mateJson(dbConn, attachmentId);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "");
      request.setAttribute(T9ActionKeys.RET_DATA,  json );
    } catch (Exception ex){
      String message = T9WorkFlowUtility.Message(ex.getMessage(),1);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, message);
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
}
