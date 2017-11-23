package t9.core.funcs.news.act;

import java.io.File;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;
import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.email.logic.T9InnerEMailLogic;
import t9.core.funcs.news.data.T9News;
import t9.core.funcs.news.data.T9NewsCont;
import t9.core.funcs.news.logic.T9FindNewaImageLogic;
import t9.core.funcs.news.logic.T9NewsManageLogic;
import t9.core.funcs.news.logic.T9NewsMetaLogic;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.system.syslog.logic.T9SysLogLogic;
import t9.core.funcs.workflow.util.T9WorkFlowUtility;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.global.T9SysPropKeys;
import t9.core.global.T9SysProps;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.file.T9FileUploadForm;
import t9.core.util.form.T9FOM;
/**
 * 新闻管理
 * @author qwx110
 *
 */
public class T9NewsHandleAct {
  private static Logger log = Logger.getLogger("t9.core.funcs.dept.act");
  private T9SysLogLogic logLogic = new T9SysLogLogic();
  private T9NewsManageLogic newsManageLogic = new T9NewsManageLogic();
  /**
   * 新建、修改新闻时进入的页面
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String beforeAddNews(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9NewsManageLogic newsManageLogic = new T9NewsManageLogic();
      String data = newsManageLogic.beforeAddNews(dbConn,person);
      //T9Out.println(data);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
      throw ex;
    }
    //return "/core/funcs/dept/deptinput.jsp";
    //?deptParentDesc=+deptParentDesc
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 新建新闻，publish 为0标示保存，1标示发布
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String addNews(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    
    T9FileUploadForm fileForm = new T9FileUploadForm();
    fileForm.parseUploadRequest(request);
    Map paramMap = fileForm.getParamMap();
    int newSeqId = 0;
    String publish = null;
    try{
      publish = ((String[]) paramMap.get("publish"))[0];
    }catch(Exception e){
      publish = (String) paramMap.get("publish");
    }
    T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9NewsManageLogic newsManageLogic = new T9NewsManageLogic();
      newSeqId = newsManageLogic.saveMailLogic(dbConn, fileForm,person,T9SysProps.getAttachPath());
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      if("0".equals(publish)){
        request.setAttribute(T9ActionKeys.RET_MSRG, "新闻保存成功");
      }
      String typeId = (String)paramMap.get("typeId");
      if("1".equals(publish)){  //发布状态        T9FindNewaImageLogic  newImageLogic = new T9FindNewaImageLogic();
        if( !T9Utility.isNullorEmpty(typeId) && String.valueOf(newImageLogic.getImageTypeId(dbConn)).equalsIgnoreCase(typeId)){ //是图片新闻
          String fileId = (String)paramMap.get("attachmentId");
          String fileName = (String)paramMap.get("attachmentName");
          String module = "news";
          String zhaiYao = (String)paramMap.get("content");
          String cntent = zhaiYao;
          if(zhaiYao.length() > 100){
             zhaiYao = zhaiYao.substring(0,100)+" ...";
          }
          T9NewsMetaLogic newsmatelogic = new T9NewsMetaLogic();
          //把 --*-cntent--*--返给袁工，返回人名，地名，组织机构名，          String  useInfoResSubsys = T9SysProps.getProp("useInfoResSubsys");
          if ("1".equals(useInfoResSubsys)) {
            Map<String, String> dataMap = new HashMap<String, String>();
            dataMap.put(newsmatelogic.findNumber(dbConn,T9NewsCont.PERSON_NAME), "张三 李四");
            dataMap.put(newsmatelogic.findNumber(dbConn, T9NewsCont.ADDRESS_NAME), "北京 朝阳");
            dataMap.put(newsmatelogic.findNumber(dbConn, T9NewsCont.ORG_NAME), "村委会 报社");
            if(!T9Utility.isNullorEmpty(fileId)){
              String[] pic = fileId.split(",");
              String[] picName = fileName.split("[*]");
              for(int i=0; i<pic.length; i++){
                if(isPicture(picName[i])){
                  String filePath = T9SysProps.getAttachPath() + File.separator
                  + module + File.separator + pic[i].substring(0, 4) + File.separator + pic[i].substring(5) + "_" + picName[i];
                  newsmatelogic.insertMainData(dbConn, pic[i], filePath, dataMap, zhaiYao, "2", newSeqId); //把图片保存到文件中心,把返回人名，地名，组织机构名  保存到属性表中
                }
              }
            }
          }
        }
        request.setAttribute(T9ActionKeys.RET_MSRG, "新闻发布成功");
      }
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
      throw ex;
    }
    //return "/core/funcs/dept/deptinput.jsp";
    //?deptParentDesc=+deptParentDesc
    request.setAttribute(T9ActionKeys.RET_METHOD, T9ActionKeys.RET_METHOD_REDIRECT);
    return "/core/funcs/news/manage/newsSaveOk.jsp?publish="+publish;
  }
  
  /**
   * 判读是不是图片
   * @param picName 图片名字 如:aa.jpg 带后缀名
   * @return
   */
  public boolean isPicture(String picName){
    String[] imageType = {"gif","jpg","jpeg","png","bmp","iff","jp2","jpx","jb2","jpc","xbm","wbmp"};
    for(int i=0; i<imageType.length; i++){
       if(picName.endsWith("."+imageType[i])){
         return true;
       }
    }    
    return false;
  }
  
  /**
   * 附件上传
   * @param request
   * @param response
   * @return
   * @throws Exception 
   */
  public String fileLoad(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    PrintWriter pw = null;
    request.setCharacterEncoding(T9Const.DEFAULT_CODE);
    response.setCharacterEncoding(T9Const.DEFAULT_CODE);
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
        .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9FileUploadForm fileForm = new T9FileUploadForm();
      fileForm.parseUploadRequest(request);
      T9NewsManageLogic newsManageLogic = new T9NewsManageLogic();
      StringBuffer sb = newsManageLogic.uploadMsrg2Json(fileForm, T9SysProps.getAttachPath());
      String data = "{'state':'0','data':" + sb.toString() + "}";
      pw = response.getWriter();
      pw.println(data.trim());
      pw.flush();
    }catch(Exception e){
      pw = response.getWriter();
      pw.println("{'state':'1'}".trim());
      pw.flush();
    } finally {
      pw.close();
    }
    return null;
  }
  
  /**
   * 单文件附件上传
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
public String uploadFile(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
  try{
    T9FileUploadForm fileForm = new T9FileUploadForm();
    fileForm.parseUploadRequest(request);
    Map<String, String> attr = null;
    String attrId = (fileForm.getParameter("attachmentId")== null )? "":fileForm.getParameter("attachmentId");
    String attrName = (fileForm.getParameter("attachmentName")== null )? "":fileForm.getParameter("attachmentName");
    String data = "";
    T9NewsManageLogic newsManageLogic = new T9NewsManageLogic();
    attr = newsManageLogic.fileUploadLogic(fileForm, T9SysProps.getAttachPath());
    Set<String> keys = attr.keySet();
    for (String key : keys){
      String value = attr.get(key);
      if(attrId != null && !"".equals(attrId)){
        if(!(attrId.trim()).endsWith(",")){
          attrId += ",";
        }
        if(!(attrName.trim()).endsWith("*")){
          attrName += "*";
        }
      }
      attrId += key + ",";
      attrName += value + "*";
    }
    data = "{attrId:\"" + attrId + "\",attrName:\"" + attrName + "\"}";
    request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
    request.setAttribute(T9ActionKeys.RET_MSRG, "文件上传成功");
    request.setAttribute(T9ActionKeys.RET_DATA, data);
  }catch (SizeLimitExceededException ex) {
    request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
    request.setAttribute(T9ActionKeys.RET_MSRG, "文件上传失败：文件需要小于" + T9SysProps.getInt(T9SysPropKeys.MAX_UPLOAD_FILE_SIZE) + "兆");
  } catch (Exception e){
    request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
    request.setAttribute(T9ActionKeys.RET_MSRG, "文件上传失败");
  }
  return "/core/inc/rtuploadfile.jsp";
}
  /**
   * 存草稿

   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String saveNewsByUp(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection conn = null;
    T9FileUploadForm fileForm = new T9FileUploadForm();
    fileForm.parseUploadRequest(request);
    String urlAdd = request.getParameter("urlAdd");
    T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
    int bId = -1 ;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
        .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      conn = requestDbConn.getSysDbConn();
      T9InnerEMailLogic ieml = new T9InnerEMailLogic();
      T9NewsManageLogic newsManageLogic = new T9NewsManageLogic();
      bId = newsManageLogic.savettachMailLogic(conn, fileForm,person.getSeqId(),T9SysProps.getAttachPath());
      request.setAttribute("bId", bId);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "新闻保存成功！");
    }catch(Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "新闻保存失败！" + e.getMessage());
      request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
      e.printStackTrace();
    }
    return "/core/funcs/news/manage/newsAdd.jsp?seqId="+ bId;
  }
 
  /**
   * 查询出某个类型下的新闻列表和加载管理新闻页面时的方法
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getnewsManagerList(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    T9NewsManageLogic newsManageLogic = new T9NewsManageLogic();
    Connection dbConn = null;
    String type = request.getParameter("type");//下拉框中类型
    String ascDesc = request.getParameter("ascDesc");//升序还是降序
    String field = request.getParameter("field");//排序的字段
    String showLenStr = request.getParameter("showLength");//每页显示长度
    String pageIndexStr = request.getParameter("pageIndex");//页码数
//    String loginUserId = request.getParameter("loginUserId");
    T9Person loginUser = null;
     loginUser = (T9Person)request.getSession().getAttribute("LOGIN_USER");
    try {
      String data = "";
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
//      String postPriv = loginUser.getPostPriv();
//      String userPriv = loginUser.getUserPriv();
//      String userSeqId = Integer.toString(loginUser.getSeqId());
//      totalItems = (String)request.getSession().getAttribute("totalItems");
      
//      String queryCountStr = "";
//      String querynewsStr = "";
//      if("".equals(totalItems) || totalItems==null) {
//        //角色是“OA管理员”，从新闻表里取所有的新闻（能看到并管理所有的新闻）
//        if("1".equals(userPriv)||"1".equals(postPriv)) {
//          queryCountStr = "SELECT count(*) from news where 1=1";
//        }else {
//        //角色不是“OA管理员”，则只能看到自己发布的新闻
//           queryCountStr = "SELECT count(*) from news where FROM_ID='"+ userSeqId + "'";
//        }
//        //如果指定了一个新闻类型，则仅查询指定类型的新闻
//        if(!"0".equals(type)) {
//          queryCountStr = queryCountStr + " and TYPE_ID='" + type + "'";
//        }
//        stmt = dbConn.createStatement();
//        rs = stmt.executeQuery(queryCountStr);
//        if(rs.next()) {
//          totalItems = rs.getString(1);
//          request.getSession().setAttribute("totalItems", totalItems);
//        }
//      }
      
 //     if("0".equals(totalItems)) {
 //       request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
 //       request.setAttribute(T9ActionKeys.RET_MSRG, "无已发布的公告通知！");
 //       return "/core/inc/rtjson.jsp";
 //     }
      
      if("".equals(ascDesc)) {
        ascDesc = "1";
      }
      
      data = newsManageLogic.getnewsManagerList(dbConn, loginUser, type,
             ascDesc, field, Integer.parseInt(showLenStr), Integer.parseInt(pageIndexStr),request);
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception ex) {
      String message = T9WorkFlowUtility.Message(ex.getMessage(),1);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, message);
      throw ex;
    }
    //return "/core/funcs/dept/deptinput.jsp";
    //?deptParentDesc=+deptParentDesc
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 改变新闻状态，终止，生效
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String changeState(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    
    T9NewsManageLogic newsManageLogic = new T9NewsManageLogic();
    String seqId = request.getParameter("seqId");//seqId
    String inEnd = request.getParameter("isEnd");
    String showLenStr = request.getParameter("showLength");//每页显示长度
    String pageIndexStr = request.getParameter("pageIndex");//页码数
//    String loginUserId = request.getParameter("loginUserId");
    T9Person loginUser = null;
    Connection dbConn = null;
    boolean success = false;
     loginUser = (T9Person)request.getSession().getAttribute("LOGIN_USER");
     try {
       T9RequestDbConn requestDbConn = (T9RequestDbConn) request
           .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
       dbConn = requestDbConn.getSysDbConn();
       success = newsManageLogic.changeState(dbConn, loginUser, Integer.parseInt(showLenStr), Integer.parseInt(pageIndexStr),seqId,inEnd);
      
    request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
    request.setAttribute(T9ActionKeys.RET_MSRG,"终止生效状态已修改");
  } catch (Exception ex) {
    String message = T9WorkFlowUtility.Message(ex.getMessage(),1);
    request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
    request.setAttribute(T9ActionKeys.RET_MSRG, message);
    request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
    throw ex;
  }
  //return "/core/funcs/dept/deptinput.jsp";
  //?deptParentDesc=+deptParentDesc
  return "/core/funcs/news/manage/newsList.jsp";
  }
  
  /**
   * 删除loginUserId用户的所有的新闻
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String deleteAllnews(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
    String loginUserPriv = person.getUserPriv();
    String postPriv = person.getPostPriv();
    int loginUserId = person.getSeqId();
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9NewsManageLogic newsManageLogic = new T9NewsManageLogic();
      boolean success =newsManageLogic.deleteAllnews(dbConn, Integer.toString(loginUserId), loginUserPriv, postPriv);
      
      
      PrintWriter pw = response.getWriter();    
      String rtData = "{rtState:'0', rtMsrg:'成功删除'}";
      pw.println(rtData);    
      pw.flush();
      /*request.setAttribute(T9ActionKeys.RET_STATE, "0");
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功删除数据");*/
    } catch (Exception ex) {
      PrintWriter pw = response.getWriter();    
      String rtData = "{rtState:'1', rtMsrg:'删除失败'}";
      pw.println(rtData);    
      pw.flush();
      throw ex;
    }
    //return "/core/funcs/dept/deptinput.jsp";
    //?deptParentDesc=+deptParentDesc
    return null;
  }
  
  /**
   * 删除
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String deleteNewById(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    String deleteStr = request.getParameter("seqId");
    T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
    String loginUserPriv = person.getUserPriv();
    String postPriv = person.getPostPriv();
    int loginUserId = person.getSeqId();
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9NewsManageLogic newsManageLogic = new T9NewsManageLogic();
      boolean success =newsManageLogic.deleteChecknews(dbConn, Integer.toString(loginUserId), loginUserPriv, postPriv, deleteStr);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功删除数据");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    //return "/core/funcs/dept/deptinput.jsp";
    //?deptParentDesc=+deptParentDesc
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 删除所选择的新闻
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String deleteCheckNews(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    String pageIndex = request.getParameter("pageIndex");
    String showLength = request.getParameter("showLength");
    String deleteStr = request.getParameter("delete_str");
    T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
    String loginUserPriv = person.getUserPriv();
    String postPriv = person.getPostPriv();
    int loginUserId = person.getSeqId();
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9NewsManageLogic newsManageLogic = new T9NewsManageLogic();
      boolean success =newsManageLogic.deleteChecknews(dbConn, Integer.toString(loginUserId), loginUserPriv, postPriv, deleteStr);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功删除数据");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    //return "/core/funcs/dept/deptinput.jsp";
    //?deptParentDesc=+deptParentDesc
    return "/core/inc/rtjson.jsp";
  }

  /**
   * 新闻查询结果页面
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String queryNews(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    String data = "";
    int pageIndex = 1;
    int showLength = 10;
    T9News news = (T9News) T9FOM.build(request.getParameterMap());
    String pageIndexStr = request.getParameter("pageIndex");
    String showLengthStr = request.getParameter("showLength");
    if(!"".equals(pageIndexStr)&&pageIndexStr!=null){
      pageIndex = Integer.parseInt(pageIndexStr);
    }
    if(!"".equals(showLengthStr)&&showLengthStr!=null){
      showLength = Integer.parseInt(showLengthStr);
    }
    String beginDate = request.getParameter("beginDate");
    String endDate = request.getParameter("endDate");
    String clickCountMin = request.getParameter("clickCountMin");
    String clickCountMax =request.getParameter("clickCountMax");
    T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9NewsManageLogic newsManageLogic = new T9NewsManageLogic();
      data = newsManageLogic.queryNews(dbConn,news,person,beginDate,endDate,clickCountMin,clickCountMax,showLength,pageIndex);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception ex) {
      String message = T9WorkFlowUtility.Message(ex.getMessage().substring(ex.getMessage().lastIndexOf(":")+1,ex.getMessage().length()),1);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, message);    
      throw ex;
    }    
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 修改新闻，此时seqId不为空
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String editNews(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    T9News news = null;
    String data = "";
    String seqId = request.getParameter("seqId");
    T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
    
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9ORM orm = new T9ORM();
      news = (T9News)orm.loadObjSingle(dbConn, T9News.class, Integer.parseInt(seqId));
      
      if (news == null) {
        news = new T9News();
      }else if (!T9Utility.null2Empty(news.getFormat()).equals("2")) {
      	byte[] byteContent = news.getCompressContent();
      	if (byteContent == null) {
      		news.setContent("");
      	}else {
      	  news.setContent(new String(byteContent, "UTF-8"));
      	}
      }
      
      data = news.toJSON();
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception ex) {
      String message = T9WorkFlowUtility.Message(ex.getMessage(),1);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, message);
      throw ex;
    }
    //return "/core/funcs/dept/deptinput.jsp";
    //?deptParentDesc=+deptParentDesc
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 查看新闻查阅情况，先把查看的用户的部门全部列出来，
   * 如果有人查看放入已读人员，没有查看的用户放入未读人员
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String showReader(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    T9News news = null;
    String seqId = request.getParameter("seqId");
    String displayAll = request.getParameter("displayAll");
    T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
    
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9NewsManageLogic newsManageLogic = new T9NewsManageLogic();
      String data = newsManageLogic.showReader(dbConn, seqId,displayAll);
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception ex) {
      String message = T9WorkFlowUtility.Message(ex.getMessage(),1);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, message);
      throw ex;
    }
    //return "/core/funcs/dept/deptinput.jsp";
    //?deptParentDesc=+deptParentDesc
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 获取新闻的类型
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getnewsType(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    T9News news = null;
    StringBuffer sb = new StringBuffer();
    sb.append("{");
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      int typeNum = 0;
      String getTypeSql = "select SEQ_ID,CLASS_DESC from CODE_ITEM where CLASS_NO='NEWS'";
      Statement typeSt = dbConn.createStatement();
      ResultSet typeRs = typeSt.executeQuery(getTypeSql);
      sb.append("typeData:[");
      while(typeRs.next()){
        typeNum ++;
        sb.append("{");
        sb.append("typeId:\"" + typeRs.getInt("SEQ_ID") + "\"");
        sb.append(",typeDesc:\"" + typeRs.getString("CLASS_DESC") + "\"");
        sb.append("},");
      }
      if(typeNum >0) {
        sb.deleteCharAt(sb.length() - 1); 
        }
      sb.append("]");
      sb.append("}");
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
    } catch (Exception ex) {
      String message = T9WorkFlowUtility.Message(ex.getMessage(),1);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, message);
      throw ex;
    }
    //return "/core/funcs/dept/deptinput.jsp";
    //?deptParentDesc=+deptParentDesc
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 浮动菜单文件删除
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String delFloatFile(HttpServletRequest request, HttpServletResponse response) throws Exception {
    String attachId = request.getParameter("attachId");
    String attachName = request.getParameter("attachName");
    String sSeqId = request.getParameter("seqId");
    //T9Out.println(sSeqId);
    if (attachId == null) {
      attachId = "";
    }
    if (attachName == null) {
      attachName = "";
    }
    int seqId = 0 ;
    if (sSeqId != null && !"".equals(sSeqId)) {
      seqId = Integer.parseInt(sSeqId);
    }
    Connection dbConn = null;
    try {
      T9RequestDbConn requesttDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requesttDbConn.getSysDbConn();

      T9NewsManageLogic newsManageLogic = new T9NewsManageLogic();

      boolean updateFlag = newsManageLogic.delFloatFile(dbConn, attachId, attachName , seqId);
     
      String isDel="";
      if (updateFlag) {
        isDel ="isDel"; 

      }
      String data = "{updateFlag:\"" + isDel + "\"}";
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "删除成功!");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }

    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 根据查询条件删除数据
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String deleteNewsBySel(HttpServletRequest request, HttpServletResponse response)throws Exception {
    Connection dbConn = null;
    T9News news = (T9News) T9FOM.build(request.getParameterMap());
    String beginDate = request.getParameter("beginDate");
    String endDate = request.getParameter("endDate");
    String clickCountMin = request.getParameter("clickCountMin");
    String clickCountMax =request.getParameter("clickCountMax");
    T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();      
      newsManageLogic.deleteSelNew(dbConn, news, person, beginDate, endDate, clickCountMin, clickCountMax);      
      int count = newsManageLogic.getCount();
      request.setAttribute("count",count);
    } catch (Exception ex) {      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());  
      request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
      throw ex;
    }    
    return "/core/funcs/news/manage/msg.jsp";
  }
}
