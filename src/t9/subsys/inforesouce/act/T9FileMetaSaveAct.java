package t9.subsys.inforesouce.act;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.global.T9SysProps;
import t9.core.util.T9Utility;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.workflow.util.T9WorkFlowUtility;
import t9.subsys.inforesouce.data.T9Kengine;
import t9.subsys.inforesouce.data.T9SignFile;
import t9.subsys.inforesouce.db.T9MetaDbHelper;
import t9.subsys.inforesouce.logic.T9MateTypeLogic;
import t9.subsys.inforesouce.util.T9FileMateConstUtil;
import t9.subsys.inforesouce.util.T9OutURLUtil;
import t9.subsys.inforesouce.util.T9StringUtil;
/**
 * 保存文件的元数据的类<br>
 * 主要功能如下：<br>保存文件的元数据<br>获得文件的元数据<br>
 * 根据元数据查找文件列表<br>存入文件的摘要<br>从库中取摘要<br>知识搜索引擎<br>
 * 获取摘要词 
 * @author qwx110
 */
public class T9FileMetaSaveAct{
  /**
   * 保存文件的元数据<br>
   * <p>
   * 调用T9MateTypeLogic的mateLogic方法 取出元数据的编号<br>
   * 调用T9MetaDbHelper的updateMetadata方法 把元数据保存到数据库中
   * </p>
   * @see t9.subsys.inforesouce.logic.T9MateTypeLogic#findNumberId(Connection, String)
   * @see t9.subsys.inforesouce.db.T9MetaDbHelper#updateMetadata(Connection, String, String, Map, String, String, int)
   * @param request
   * @param response
   * @return ajax页面
   * @throws Exception
   */
  public String saveFileMeta(HttpServletRequest request, HttpServletResponse response)throws Exception{
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR); 
    Connection dbConn = null;
    T9MateTypeLogic mateLogic = new T9MateTypeLogic();
    try{
      dbConn = requestDbConn.getSysDbConn();    
      String ids = request.getParameter("ids");
      ids = ids.substring(ids.indexOf(",")+1, ids.length());
      String fileId = request.getParameter("attachmentId");
      String fileName = request.getParameter("attachmentName");
      String module = request.getParameter("moudle");
      String filePath = T9SysProps.getAttachPath() + "\\"
        + module + "\\" + fileId.substring(0, 4) + "\\" + fileId.substring(5) + "_" + fileName;
      String zhaiYao = request.getParameter("abstracts");
      Map<String, String> map = T9StringUtil.toMap(ids);
      String basePath = T9SysProps.getString("signFileServiceUrl");
      String url = basePath + "/TitleSign/TitleSignFile?FILE_ID=" + fileId;
      T9MetaDbHelper helper = new T9MetaDbHelper();
      helper.updateMetadata(dbConn, fileId, filePath, map, zhaiYao, "1", 0);
      String json = T9OutURLUtil.getContent(url);
      //String sign = retNewStr(titleSign);
      Map<String, String> mates = paramTitleSign(json);
      String number = mateLogic.findNumberId(dbConn, T9FileMateConstUtil.userName);
      String areaNumber = mateLogic.findNumberId(dbConn, T9FileMateConstUtil.areaName);
      String orgNumber = mateLogic.findNumberId(dbConn, T9FileMateConstUtil.Org);
      String subNumber = mateLogic.findNumberId(dbConn, T9FileMateConstUtil.subJect);
      if(!T9Utility.isNullorEmpty(mates.get("personNames"))){       
        map.put(jugeNumberId(number), mates.get("personNames"));
      }
      if(!T9Utility.isNullorEmpty(mates.get("addrNames"))){       
        map.put(jugeNumberId(areaNumber), mates.get("addrNames"));
      }
      if(!T9Utility.isNullorEmpty(mates.get("orgNames"))){       
        map.put(jugeNumberId(orgNumber), mates.get("orgNames"));
      }
      if(!T9Utility.isNullorEmpty(mates.get("keywords"))){       
        map.put(jugeNumberId(subNumber), mates.get("keywords"));
      }
      helper.updateMetadata(dbConn, fileId, filePath, map, zhaiYao, "1", 0);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"保存成功！");     
    } catch (Exception ex){
      String message = T9WorkFlowUtility.Message(ex.getMessage(),1);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, message);
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * <fieldset>
   * <legend>获得文件的元数据</legend>
   * 返回一个文件的元数据
   * 调用T9MetaDbHelper的loadFile方法返回文件T9SignFile对象,通过调用json2AString方法截取这个文件<br>的元数据部分，返回到页面显示出来
   * </fieldset>
   * @see t9.subsys.inforesouce.db.T9MetaDbHelper#loadFile(Connection, String, int)
   * @see t9.subsys.inforesouce.data.T9SignFile
   * @param request
   * @param response
   * @return "/core/inc/rtjson.jsp"
   * @throws Exception
   */
  public String findFileMate(HttpServletRequest request, HttpServletResponse response)throws Exception{
   
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR); 
    Connection dbConn = null;
    try{
      dbConn = requestDbConn.getSysDbConn();
      T9MetaDbHelper helper = new T9MetaDbHelper();
      String guidFilter = request.getParameter("guid");
      T9SignFile afile = helper.loadFile(dbConn, guidFilter, 1);      
      String str = "";
      if(afile != null){
        str = afile.toJson();    
      }
      String jstr  = json2AString(str);
      if(afile != null){
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);      
        request.setAttribute(T9ActionKeys.RET_DATA, jstr);
      }
    } catch (Exception ex){
      String message = T9WorkFlowUtility.Message(ex.getMessage(),1);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, message);
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * <fieldset>
   * <legend>查找文件列表</legend>
   * <p>
   *  先点左边得先看右边的树有没有选中的，如果有选中的则把值代过来<br>
   *  先点右边的树先看有没有点击左边的列表，如果点击了，则把值带过来<br>
   *  调用T9MetaDbHelper的searchFileList方法返回文件列表
   * </p>
   * </fieldset>
   * @param request
   * @param response
   * @see t9.subsys.inforesouce.db.T9MetaDbHelper#searchFileList(Connection, List, Map)
   * @return   "/core/inc/rtjson.jsp"
   * @throws Exception
   */
  public String findFileList(HttpServletRequest request, HttpServletResponse response)throws Exception{
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR); 
    try{
      Connection dbConn = null;
      dbConn = requestDbConn.getSysDbConn();
      
      T9MetaDbHelper helper = new T9MetaDbHelper();
      List<String> moduleList = new ArrayList<String>();
      String modules = request.getParameter("modules");             //从左侧带过来的类型，如notify,news,mails等的标志
      if(!T9Utility.isNullorEmpty(modules)){
        String[] module = modules.split(",");
        for(int i=0; i<module.length; i++){
          if(!T9Utility.isNullorEmpty(module[i])){
            moduleList.add(module[i]);
          }
        }
      }
      String metas = request.getParameter("nodes");                //从右侧带过来的值串，如：M12-M23-123,M23-asdfd,M43-,等的值      metas = T9Utility.decodeURL(metas);
      Map<String, String> metaFilters = null;
      if(!T9Utility.isNullorEmpty(metas)){
        metaFilters = T9StringUtil.toMap(metas.trim());
      }
      List<T9SignFile> filse = helper.searchFileList(dbConn, moduleList, metaFilters);   //文件列表       
      String jsons = "[]";
      jsons = toJson(filse);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);      
      request.setAttribute(T9ActionKeys.RET_DATA, jsons);      
    } catch (Exception ex){
      String message = T9WorkFlowUtility.Message(ex.getMessage(),1);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, message);
      throw ex;
    }    
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 存入文件的摘要<br>
   * <p>把文件的id和摘要存入库中，调用T9MateTypeLogic的saveAbstract方法</p>
   * @see t9.subsys.inforesouce.logic.T9MateTypeLogic#saveAbstract(Connection, String, String)
   * @param request
   * @param response
   * @return "/core/inc/rtjson.jsp"
   * @throws Exception
   */
  public String saveAbstract(HttpServletRequest request, HttpServletResponse response)throws Exception{
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR); 
    Connection dbConn = null;
    T9MateTypeLogic mateLogic = new T9MateTypeLogic();
    String fileId = request.getParameter("fileId");
    String zhaiYao = request.getParameter("content");
    try{
      dbConn = requestDbConn.getSysDbConn();
      int ok = mateLogic.saveAbstract(dbConn, zhaiYao, fileId);
      if(ok != 0){
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);  
        request.setAttribute(T9ActionKeys.RET_MSRG,"保存成功！");
      }
    } catch (Exception ex){
      String message = T9WorkFlowUtility.Message(ex.getMessage(),1);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, message);
      throw ex;
    } 
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 从库中取摘要
   * <p>根据文件id返回文件的摘要，调用t9.subsys.inforesouce.logic.T9MateTypeLogic的findAbstract方法返回摘要</p>
   * @see t9.subsys.inforesouce.logic.T9MateTypeLogic#findAbstract(Connection, String)
   * @param request
   * @param response
   * @return "/core/inc/rtjson.jsp"
   * @throws Exception
   */
  public String findAbstract(HttpServletRequest request, HttpServletResponse response)throws Exception{
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR); 
    Connection dbConn = null;
    T9MateTypeLogic mateLogic = new T9MateTypeLogic();
    String fileId = request.getParameter("fileId"); 
    String titleContent = "";
    try{
      dbConn = requestDbConn.getSysDbConn();
     
        titleContent = mateLogic.findAbstract(dbConn, fileId);      
        if(T9Utility.isNullorEmpty(titleContent)){
          titleContent = "暂无摘要！";
        }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);  
      request.setAttribute(T9ActionKeys.RET_DATA,"\""+titleContent+"\"");
    } catch (Exception ex){
      String message = T9WorkFlowUtility.Message(ex.getMessage(),1);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, message);
      throw ex;
    } 
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 截取一个json串的metAttrs部分，拼成M12-23,M34-sdff,M2-的形式
   * @param json 传入的json串
   * @return String
   */
  private   String json2AString(String jsonPre){
    String keyValue = "";
    Set<String> keys = new HashSet<String>();
    if(!T9Utility.isNullorEmpty(jsonPre)){  
    String json = jsonPre.replaceAll("MEX", "M");
    String parax = json.substring(json.indexOf("metAttrs")+"metAttrs".length()+1)
                   .replaceAll("\\{", "").replaceAll("\\}", "");
    String[] px = parax.split("M");    
    
    if(px!=null && px.length>=1){
      for(int i=0; i<px.length; i++){ 
        if(!T9Utility.isNullorEmpty((px[i]))){
          String newStr =  "M"+px[i];
          String[] vals  = newStr.split(":");       
          String key = vals[0];
          keys.add(key);
         if(vals!=null && vals.length>1){
            String[] key2 = vals[1].replaceAll("\"", "").split(",");
            if(key2.length != 0){
              for(int k=0; k<key2.length; k++){
                if(!T9Utility.isNullorEmpty(key2[k])){                 
                  keyValue += key+"-"+key2[k] +",";
                }          
              }
            }else{
              keyValue += key+"-,";
            }
          }
        }
      }
    } 
    }
    String[] value = keyValue.split(",");
    Iterator it = keys.iterator();
    List<String> list = new ArrayList<String>();
    while(it.hasNext()){
      String aKay = (String)it.next();
      String newKeyStr = "";
      for(int a=0; a<value.length; a++){
        if(!T9Utility.isNullorEmpty(value[a]) && value[a].indexOf(aKay+"-") != -1){
          newKeyStr +=  value[a].split("-")[1] + " ";
        }
      }
      list.add(aKay +"-" + newKeyStr);
    }
    keyValue = "";
    for(int n = 0; n<list.size(); n++){
      keyValue += list.get(n);
      if(n<list.size()-1){
        keyValue += ",";
      }
    }
    return "\""+keyValue+"\"";
  } 
 
  /**
   * 工具方法--把一个list转化为json
   * @param filse 文件的list 
   * @return String
   * @throws Exception
   */
  private String toJson(List<T9SignFile> filse ) throws Exception{
    StringBuffer sb = new StringBuffer();
    sb.append("["); 
      if( filse!=null ){
       for(int i=0; i<filse.size(); i++){
         if( i<filse.size()-1 ){
           sb.append(filse.get(i).toJson()).append(",");  
         }else{
           sb.append(filse.get(i).toJson()); 
         }
       }
      }
    sb.append("]");
    return sb.toString();
  }
  
 private static String compStr(String str){
   if(!T9Utility.isNullorEmpty(str)){
     int start = str.indexOf("[")==-1?0:str.indexOf("[")+1;
     int end = str.lastIndexOf("]")==-1?0:str.lastIndexOf("]");
     if(start * end != 0){
       return str.substring(start, end);
     }
   }
   return null;
 }
 
 /**
  * 
  * @param request
  * @param response
  * @return
  * @throws Exception
  */
 public String getFullText(HttpServletRequest request, HttpServletResponse response)throws Exception{
   T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR); 
   Connection dbConn = null;
   T9MateTypeLogic mateLogic = new T9MateTypeLogic();
   String fileId = request.getParameter("fileId"); 
   String titleContent = "";
   try{
     dbConn = requestDbConn.getSysDbConn();
     request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);  
     request.setAttribute(T9ActionKeys.RET_DATA,"\""+titleContent+"\"");
   } catch (Exception ex){
     String message = T9WorkFlowUtility.Message(ex.getMessage(),1);
     request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
     request.setAttribute(T9ActionKeys.RET_MSRG, message);
     throw ex;
   } 
   return "/core/inc/rtjson.jsp";
 }
 /**
  * <fieldset>
  * <legend>知识搜索引擎</legend>
  * <p>通过文件id超找这个文件的人名，地名，组织机构，主题词，关键词等的元数据信息，并返回相关文件<br>
  * 首先需要传入文件的attachmentId，通过attachmentId查找人名，地名，组织机构，主题词，关键词等<br>
  * 调用袁工接口GetRelationArticleList返回相关文件列表，需要给袁工传文件id</p>
  * </fieldset>
  * @param request
  * @param response
  * @return  "/subsys/inforesource/knowlege.jsp"
  * @throws Exception
  */
  public String getKengine(HttpServletRequest request, HttpServletResponse response)throws Exception{
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
    Connection dbConn = null;
    T9Person loginperson = (T9Person) request.getSession().getAttribute(
    "LOGIN_USER");
     List list = new ArrayList();
     String attachmentId = request.getParameter("attachmentId");//文件Id
     System.out.println(attachmentId);
     String basePath = T9SysProps.getString("signFileServiceUrl");//主题标引服务地址
     String url = basePath + "/TitleSign/GetRelationArticleList?FILE_ID=" + attachmentId;//调用接口
     String files = null;
     try{
         dbConn = requestDbConn.getSysDbConn();
         T9MateTypeLogic logic = new T9MateTypeLogic();
         int seqId = logic.findKengine(dbConn, attachmentId);
         
         String userName = logic.findName(dbConn, T9FileMateConstUtil.userName);
         String areaName = logic.findName(dbConn, T9FileMateConstUtil.areaName);
         String org = logic.findName(dbConn, T9FileMateConstUtil.Org);
         String subJect = logic.findName(dbConn, T9FileMateConstUtil.subJect);
         String keyWord = logic.findName(dbConn, T9FileMateConstUtil.keyWord);
         
         T9Kengine ki = logic.findString(seqId,userName,areaName,org,subJect,keyWord,dbConn);
         request.setAttribute("ki", ki);
         
         //获得相关文档
         
           try{
             files = T9OutURLUtil.getContent(url);
           }catch(Exception e){
             request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
             request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
             throw e;
           }
          request.setAttribute("files", files);
          request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
         request.setAttribute(T9ActionKeys.RET_MSRG, "成功返回知识搜索内容");
         request.setAttribute(T9ActionKeys.RET_DATA, "");
    }catch(Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    
    return "/subsys/inforesource/knowlege.jsp";
  }
  /**
   * 获取摘要词 
   * @param request
   * @param response
   * @return  "/core/inc/rtjson.jsp"
   * @throws Exception
   */
  public String getzhaiYao(HttpServletRequest request, HttpServletResponse response)throws Exception{
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
    Connection dbConn = null;
    T9Person loginperson = (T9Person) request.getSession().getAttribute(
    "LOGIN_USER");
    String str = "";
    String fileId = request.getParameter("fileId");
     try{
         dbConn = requestDbConn.getSysDbConn();
         T9MateTypeLogic logic = new T9MateTypeLogic();
         str = logic.findzhaiYao(fileId, dbConn);
         request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
         request.setAttribute(T9ActionKeys.RET_MSRG, "成功返回摘要内容");
         request.setAttribute(T9ActionKeys.RET_DATA, "\""+ str +"\"");
     }catch(Exception ex){
       request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
       request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
       throw ex;
     }
     return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 筛选出json中主题词部分
   * @param json
   * @return
   */
  private String retNewStr(String json){
    String tt = compStr(json);  
    String nn = tt.replaceAll("\\{", "").replaceAll("\\}", "");
    String[] dd = nn.split(",");
    String ddd = "";
    for(int k=0; k<dd.length; k++){
      if(dd[k].contains("\"Keyword\":")){
        ddd += dd[k].split(":")[1] +" ";
      }
    }
    return ddd.replaceAll("\"", "");
  }
  
  /**
   * 解析出人名，地名，组织机构名，主题词
   * @param json
   * @return
   */
  private Map<String, String> paramTitleSign(String json){
    Map<String, String> mates = new HashMap<String, String>();
    String ttt = json.substring(json.indexOf("\"rtData\"")+"\"rtData\"".length()+1).replaceAll("\\{", "").replaceAll("\\}", "");
    String keywords = ttt.substring(ttt.indexOf("\"keywords\":"), ttt.indexOf("\"PersonNames\":")) ;
    String keyValue = keywords.split(":")[1].replaceAll("\\[", "").replaceAll("\\]", "").replaceAll(",", " ").replaceAll("\"", ""); //主题词
    mates.put("keywords", keyValue);
    String personNames = ttt.substring(ttt.indexOf("\"PersonNames\":"), ttt.indexOf("\"AddrNames\":")) ;
    String perNames = personNames.split(":")[1].replaceAll("\\[", "").replaceAll("\\]", "").replaceAll(",", " ").replaceAll("\"", ""); //人名
    mates.put("personNames", perNames);
    
    String addrNames = ttt.substring(ttt.indexOf("\"AddrNames\":"), ttt.indexOf("\"OrgNames\":")) ;
    String addName = addrNames.split(":")[1].replaceAll("\\[", "").replaceAll("\\]", "").replaceAll(",", " ").replaceAll("\"", ""); //地名
    mates.put("addrNames", addName);
    
    String orgNames = ttt.substring(ttt.indexOf("\"OrgNames\":")) ;
    String oName = orgNames.split(":")[1].replaceAll("\\[", "").replaceAll("\\]", "").replaceAll(",", " ").replaceAll("\"", ""); //机构名
    mates.put("orgNames", oName);
    
    return mates;
  }
  
  /**
   * 重新设置编号
   * @param numbreId
   * @return
   */
  private String jugeNumberId(String numbreId){
    if(T9Utility.isNullorEmpty(numbreId)){
      return "";
    }
    numbreId = numbreId.replace("M", "").replace("MEX", "");
    int n = Integer.parseInt(numbreId);
    if(n<=100){
      return "M"+numbreId;
    }else{
      return "MEX" + numbreId;
    }
  }
  
 //test 
  public  static void main(String[] args){
    String json = "{\"rtState\":0,\"rtData\":[{\"KeyID\":\"11873\",\"Keyword\":\"军制、军队管理\"},{\"KeyID\":\"11874\",\"Keyword\":\"作战力量\"},{\"KeyID\":\"11875\",\"Keyword\":\"国防力量\"},{\"KeyID\":\"11876\",\"Keyword\":\"国防军\"},{\"KeyID\":\"11877\",\"Keyword\":\"同盟\"}]}";
    String tt = compStr(json);  
    String nn = tt.replaceAll("\\{", "").replaceAll("\\}", "");
    String[] dd = nn.split(",");
    String ddd = "";
    for(int k=0; k<dd.length; k++){
      if(dd[k].contains("\"Keyword\":")){
        ddd += dd[k].split(":")[1] +" ";
      }
    }
    System.out.println(ddd.replaceAll("\"", ""));
  }
}
