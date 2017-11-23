package t9.subsys.inforesouce.docmgr.act;

import java.net.URLDecoder;
import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.global.T9SysProps;
import t9.core.util.T9Utility;
import t9.core.util.file.T9FileUploadForm;
import t9.subsys.inforesouce.docmgr.data.T9DocReceive;
import t9.subsys.inforesouce.docmgr.logic.T9DocReceiveLogic;
import t9.subsys.inforesouce.docmgr.logic.T9DocSmsLogic;

/**
 * 收文管理
 * @author Administrator
 *
 */
public class T9DocReceiveAct{
  
  public String gotoIndex(HttpServletRequest request, HttpServletResponse response)throws Exception{
    T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");
    request.setAttribute("user", user);
    return "/subsys/inforesource/docmgr/docreceve/newdoc.jsp";
  }
  
  public String gotoReadIndex(HttpServletRequest request, HttpServletResponse response)throws Exception{
    T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");
    request.setAttribute("user", user);
    return "/subsys/inforesource/docmgr/docreceve/readdocindex.jsp";
  }
  
  /**
   * 新建收文
   * @param request
   * @param response
   * @return
   * @throws Exception 
   */
  public String addT9DocReceive(HttpServletRequest request, HttpServletResponse response) throws Exception{
    
    T9DocReceiveLogic  docLogic = new T9DocReceiveLogic();
    Connection dbConn = null;
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
    try{
      dbConn = requestDbConn.getSysDbConn();
      T9DocReceive doc = new T9DocReceive();
      String ftype = request.getParameter("ftype");
      String docNo = request.getParameter("docNo");
      String resDate = request.getParameter("startTime");
      String fromUnits = request.getParameter("fromUnits");
      String oppDocNo = request.getParameter("oppDocNo");
      String title = request.getParameter("title");
      String copies = request.getParameter("copies");
      String confLevel = request.getParameter("confLevel");
      String instruct = request.getParameter("instruct");
      String recipient = request.getParameter("recipient");
      String docType = request.getParameter("docType");
      String sponsor = request.getParameter("deptId");
      String personId = request.getParameter("user");
      String alarm = request.getParameter("alarm");
      String attachName = request.getParameter("attachmentName");
      String attachId = request.getParameter("attachmentId");

      int userId = Integer.parseInt(personId);
      doc.setDocNo(docNo);
      doc.setResDate(T9Utility.parseDate(resDate));
      doc.setFromUnits(fromUnits);
      doc.setOppDocNo(oppDocNo);
      doc.setTitle(title);
      doc.setCopies(Integer.parseInt(copies));
      doc.setConfLevel(Integer.parseInt(confLevel));
      doc.setInstruct(instruct);
      doc.setRecipient(recipient);
      doc.setDocType(Integer.parseInt(docType));
      doc.setSponsor(sponsor);
      doc.setUserId(userId);
      doc.setAttachNames(attachName);
      doc.setAttachIds(attachId);
      if("1".equalsIgnoreCase(ftype)){
        doc.setSendStauts(0);
      }else{
        doc.setSendStauts(1);
      }
      docLogic.insertBeanChT9DocReceive(dbConn, doc);
      T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");//获得登陆用户
      String content = user.getUserName() + "提醒：请签收您的收文!收文文号:" + docNo;  
      String url =  gotoReadIndex(request, response);
      if(!T9Utility.isNullorEmpty(alarm)){
        T9DocSmsLogic.sendSms(user, dbConn, content, url, recipient, null);
      }
      request.setAttribute("msg", "新建成功！");
    } catch (Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
      throw e; 
    }
    return "/subsys/inforesource/docmgr/docreceve/msgBox.jsp";
  }
  
  /**
   * (已)未发收文
   * @param request
   * @param response
   * @return
   * @throws Exception 
   */
  public String faDocReceive(HttpServletRequest request, HttpServletResponse response) throws Exception{
    T9DocReceiveLogic  docLogic = new T9DocReceiveLogic();
    Connection dbConn = null;
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
    try{
      T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      dbConn = requestDbConn.getSysDbConn();
      String ftype  = request.getParameter("ftype");
      String column = request.getParameter("colum");  //需要排序的列
      String asc = request.getParameter("asc");      //升序还是降序
      int typeId = 0;
      if(T9Utility.isNullorEmpty(ftype)){
        typeId = 0;    //未发收文
      }else{
        typeId = Integer.parseInt(ftype);  //已发收文
      }
      List<T9DocReceive>docs =  docLogic.faDocReceive(dbConn,  user, typeId, column, asc);
      if(docs.size() == 0){
        request.setAttribute("msg", "没有符合的记录!");
        return "/subsys/inforesource/docmgr/docreceve/msgBox.jsp";
      }
      request.setAttribute("docs", docs);
      request.setAttribute("ftype", ftype);
      request.setAttribute("column", column);
      request.setAttribute("asc", asc);
    } catch (Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
      throw e; 
    } 
    return "/subsys/inforesource/docmgr/docreceve/havedoc.jsp";
  }
  
  /**
   * 未分法的更新为分发的
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String updateDocReceive(HttpServletRequest request, HttpServletResponse response)throws Exception{
    T9DocReceiveLogic  docLogic = new T9DocReceiveLogic();
    Connection dbConn = null;
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
    
    try{
      dbConn = requestDbConn.getSysDbConn();
      String seqId = request.getParameter("seqId");
      String userId = request.getParameter("userId");//签收人id
      if(T9Utility.isNullorEmpty(seqId)){
        seqId = "0";
      }
      if(T9Utility.isNullorEmpty(userId)){
        userId = "0";
      }
      int keyId = Integer.parseInt(seqId);
      int wUserId = Integer.parseInt(userId);
      int ok = docLogic.updateDocReceive(dbConn, keyId, wUserId);
      if(ok == 0){
        request.setAttribute("msg", "确认签收失败");
        return "/subsys/inforesource/docmgr/docreceve/msgBox.jsp";
      }
    } catch (Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
      throw e; 
    }
    request.setAttribute("msg", "确认签收成功");
    return "/subsys/inforesource/docmgr/docreceve/msgBox.jsp";
  }
  
  /**
   * (已)未阅读收文
   * @param request
   * @param response
   * @return
   * @throws Exception 
   */
  public String readDocReceive(HttpServletRequest request, HttpServletResponse response) throws Exception{
    T9DocReceiveLogic  docLogic = new T9DocReceiveLogic();
    Connection dbConn = null;
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
    try{
      T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      dbConn = requestDbConn.getSysDbConn();
      String ftype  = request.getParameter("ftype");
      int typeId = 0;
      if(T9Utility.isNullorEmpty(ftype)){
        typeId = 0;    //未发收文
      }else{
        typeId = Integer.parseInt(ftype);  //已发收文
      }
      List<T9DocReceive>docs =  docLogic.myReadDocReceive(dbConn, user, typeId);
      if(docs.size() == 0){
        request.setAttribute("msg", "没有符合的记录!");
        return "/subsys/inforesource/docmgr/docreceve/msgBox.jsp";
      }
      request.setAttribute("docs", docs);
      request.setAttribute("ftype", ftype);
    } catch (Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
      throw e; 
    } 
    return "/subsys/inforesource/docmgr/docreceve/writedoc.jsp";
  }
  /**
   * (已)未阅读收文

   * @param request
   * @param response
   * @return
   * @throws Exception 
   */
  public String readDocReceive1(HttpServletRequest request, HttpServletResponse response) throws Exception{
    T9DocReceiveLogic  docLogic = new T9DocReceiveLogic();
    Connection dbConn = null;
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
    try{
      T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      dbConn = requestDbConn.getSysDbConn();
      String ftype  = request.getParameter("ftype");
      String hasEnd = request.getParameter("hasEnd");
      boolean isEnd = false;
      if (!T9Utility.isNullorEmpty(hasEnd)) {
        isEnd = true;
      }
      int typeId = 0;
      if(T9Utility.isNullorEmpty(ftype)){
        typeId = 0;    //未发收文
      }else{
        typeId = Integer.parseInt(ftype);  //已发收文
      }
      List<T9DocReceive>docs =  docLogic.myReadDocReceive1(dbConn, user, typeId ,isEnd );
      if(docs.size() == 0){
        request.setAttribute("msg", "没有符合的记录!");
        return "/subsys/inforesource/docmgr/docreceve/msgBox.jsp";
      }
      request.setAttribute("docs", docs);
      request.setAttribute("ftype", ftype);
    } catch (Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
      throw e; 
    } 
    return "/subsys/inforesource/docmgr/docreceve/writedoc.jsp";
  }
  
  /**
   * 未分法的更新为分发的(确认签收)
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String updateReadDocReceive(HttpServletRequest request, HttpServletResponse response)throws Exception{
    T9DocReceiveLogic  docLogic = new T9DocReceiveLogic();
    Connection dbConn = null;
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
    
    try{
      dbConn = requestDbConn.getSysDbConn();
      String seqId = request.getParameter("seqId");
      if(T9Utility.isNullorEmpty(seqId)){
        seqId = "0";
      }
      int keyId = Integer.parseInt(seqId);
      int ok = docLogic.updateReadDocReceive(dbConn, keyId);
      if(ok == 0){
        request.setAttribute("msg", "签阅失败");
        return "/subsys/inforesource/docmgr/docreceve/msgBox.jsp";
      }
    } catch (Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
      throw e; 
    }
    request.setAttribute("msg", "签阅成功");
    return "/subsys/inforesource/docmgr/docreceve/msgBox.jsp";
  }
  
  /**
   * 提醒签收
   * @param request
   * @param response
   * @return
   */
  public String alarmToRead(HttpServletRequest request, HttpServletResponse response)throws Exception{
    Connection dbConn = null;
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
    
    try{
      dbConn = requestDbConn.getSysDbConn();
      String seqId = request.getParameter("seqId");
      String toId = request.getParameter("toId");
      String docNo = request.getParameter("docNo");
      docNo = URLDecoder.decode(docNo, T9Const.DEFAULT_CODE);
      T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");//获得登陆用户
      String content = user.getUserName() + "提醒：请签收您的收文!收文文号:" + docNo;  
      String url =  gotoReadIndex(request, response);
      T9DocSmsLogic.sendSms(user, dbConn, content, url, toId, null);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
    } catch (Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 批量确认签收
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String beanChConfirm(HttpServletRequest request, HttpServletResponse response)throws Exception{
    Connection dbConn = null;
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
    T9DocReceiveLogic  docLogic = new T9DocReceiveLogic();
    try{
      dbConn = requestDbConn.getSysDbConn();
     
      String seqIds = request.getParameter("seqIds");
      String userIds = request.getParameter("userIds");
      String[] seqId = seqIds.split(",");
      String[] userId = userIds.split(",");
      T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");//获得登陆用户
      docLogic.beanChConfirm(dbConn, seqId, userId);
      request.setAttribute("msg", "批量确认签收成功");
    } catch (Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
      throw e;
    }
    return "/subsys/inforesource/docmgr/docreceve/msgBox.jsp";
  }
  
  /**
   * 批量提醒
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String beanchAlarm(HttpServletRequest request, HttpServletResponse response)throws Exception{
    Connection dbConn = null;
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
    T9DocReceiveLogic  docLogic = new T9DocReceiveLogic();
    
    try{
      dbConn = requestDbConn.getSysDbConn();
      String[] seqId = request.getParameterValues("selAll");
      T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");//获得登陆用户
      List<T9DocReceive> docs = docLogic.beanChAlarm(dbConn,  seqId);
      if(docs != null){
        for(int i=0; i<docs.size(); i++){
          String content = user.getUserName() + "提醒：请签收您的收文!收文文号:" + docs.get(i).getDocNo();  
          String url =  gotoReadIndex(request, response);
          T9DocSmsLogic.sendSms(user, dbConn, content, url, docs.get(i).getRecipient(), null);
        }
      }
      if(docs == null || docs.size() == 0){
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute("msg", "没有需要提醒的人");
      }
      request.setAttribute("msg","批量提醒签收人成功");
    } catch (Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/subsys/inforesource/docmgr/docreceve/msgBox.jsp";
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

      T9DocReceiveLogic  docLogic = new T9DocReceiveLogic();

      boolean updateFlag = docLogic.delFloatFile(dbConn, attachId, attachName , seqId);
     
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
   * 单文件附件上传
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String uploadFile(HttpServletRequest request, HttpServletResponse response) throws Exception {
    T9FileUploadForm fileForm = new T9FileUploadForm();
    fileForm.parseUploadRequest(request);
    Map<String, String> attr = null;
    String attrId = (fileForm.getParameter("attachmentId")== null )? "":fileForm.getParameter("attachmentId");
    String attrName = (fileForm.getParameter("attachmentName")== null )? "":fileForm.getParameter("attachmentName");
    String data = "";
    try{
      T9DocReceiveLogic  docLogic = new T9DocReceiveLogic();
      attr = docLogic.fileUploadLogic(fileForm, T9SysProps.getAttachPath());
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
      //T9Out.println(data);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "文件上传成功");
      request.setAttribute(T9ActionKeys.RET_DATA, data);

    } catch (Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "文件上传失败");
      throw e;
    }
    return "/core/inc/rtuploadfile.jsp";
  }
  
  /**
   * 查找一个批办单
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String findDocFileAjax(HttpServletRequest request, HttpServletResponse response)throws Exception{
    try{
      Connection dbConn = null;
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      T9DocReceiveLogic  docLogic = new T9DocReceiveLogic();
      dbConn = requestDbConn.getSysDbConn();
      String seqId = request.getParameter("seqId");
      int id = Integer.parseInt(seqId);
      T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");//获得登陆用户
      
      String docJson = docLogic.myDocReceiveJson(dbConn, user, id);
      String deptName = docLogic.getDeptName(dbConn, user.getDeptId());
      StringBuffer sb = new StringBuffer();
      sb.append("[");
        sb.append(docJson).append(",");
        sb.append("{deptName:").append("'").append(T9Utility.encodeSpecial(deptName)).append("'}");
      sb.append("]");
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功");
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
    } catch (Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }
    
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 获得最大的收文编号
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getMaxOrderNo(HttpServletRequest request, HttpServletResponse response)throws Exception{
    try{
      Connection dbConn = null;
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      T9DocReceiveLogic  docLogic = new T9DocReceiveLogic();
      dbConn = requestDbConn.getSysDbConn();
      String typeId = request.getParameter("typeId");
      int id = Integer.parseInt(typeId);
      List<Integer> ints = docLogic.getMaxOrderNo(dbConn, id);
      int max = docLogic.getMaxOrderNo(ints);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功");
      request.setAttribute(T9ActionKeys.RET_DATA, String.valueOf(max));
    } catch (Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 修改收文
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String editDoc(HttpServletRequest request, HttpServletResponse response)throws Exception{
    try{
      Connection dbConn = null;
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      T9DocReceiveLogic  docLogic = new T9DocReceiveLogic();
      dbConn = requestDbConn.getSysDbConn();
      String seqId = request.getParameter("seqId");
      int id = Integer.parseInt(seqId);
      T9DocReceive doc = docLogic.getAdocById(dbConn, id);
      request.setAttribute("doc", doc);
    } catch (Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }
    return "/subsys/inforesource/docmgr/docreceve/edit.jsp";
  }
  
  /**
   * 更新
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String update(HttpServletRequest request, HttpServletResponse response)throws Exception{
    
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
    try{
      T9DocReceiveLogic  docLogic = new T9DocReceiveLogic();
      Connection dbConn = null;
      dbConn = requestDbConn.getSysDbConn();
      T9DocReceive doc = new T9DocReceive();
      String docNo = request.getParameter("docNo");
      String seqId = request.getParameter("docId");
      //String resDate = request.getParameter("startTime");
      String fromUnits = request.getParameter("fromUnits");
      String oppDocNo = request.getParameter("oppDocNo");
      String title = request.getParameter("title");
      String copies = request.getParameter("copies");
      String confLevel = request.getParameter("confLevel");
      String instruct = request.getParameter("instruct");
     // String recipient = request.getParameter("recipient");
      String docType = request.getParameter("docType");
      String sponsor = request.getParameter("deptId");
      String personId = request.getParameter("user");
      //String alarm = request.getParameter("alarm");
      String attachName = request.getParameter("attachmentName");
      String attachId = request.getParameter("attachmentId");

      int userId = Integer.parseInt(personId);
      doc.setSeq_id(Integer.parseInt(seqId));
      doc.setDocNo(docNo);
      //doc.setResDate(T9Utility.parseDate(resDate));
      doc.setFromUnits(fromUnits);
      doc.setOppDocNo(oppDocNo);
      doc.setTitle(title);
      doc.setCopies(Integer.parseInt(copies));
      doc.setConfLevel(Integer.parseInt(confLevel));
      doc.setInstruct(instruct);
      //doc.setRecipient(recipient);
      doc.setDocType(Integer.parseInt(docType));
      doc.setSponsor(sponsor);
      doc.setUserId(userId);
      doc.setAttachNames(attachName);
      doc.setAttachIds(attachId);
      
      //docLogic.insertBeanChT9DocReceive(dbConn, doc);
      docLogic.update(dbConn, doc);
      request.setAttribute("msg", "修改成功！");
    } catch (Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
      throw e; 
    }
    return "/subsys/inforesource/docmgr/docreceve/openMsg.jsp";
  }
  
  /**
   * 撤销（清空签收人和承办处，签收状态变为未签收状态）
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String cancel(HttpServletRequest request, HttpServletResponse response)throws Exception{
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      T9DocReceiveLogic  docLogic = new T9DocReceiveLogic();
      Connection dbConn = null;
      dbConn = requestDbConn.getSysDbConn();
      String seqId = request.getParameter("seqId");
      int id = Integer.parseInt(seqId);
      docLogic.updateStatus(dbConn, id);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "撤销成功");
    } catch (Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e; 
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 打印
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String print(HttpServletRequest request, HttpServletResponse response)throws Exception{
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      T9DocReceiveLogic  docLogic = new T9DocReceiveLogic();
      Connection dbConn = null;
      dbConn = requestDbConn.getSysDbConn();
      String printIds = request.getParameter("printIds");
      List<T9DocReceive>docs = null;
      if(T9Utility.isNullorEmpty(printIds)){
        printIds = "0";
      }else{
       docs =  docLogic.printDocs(dbConn, printIds);
      }
      request.setAttribute("docs", docs);
    } catch (Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
      throw e; 
    }
    return "/subsys/inforesource/docmgr/docreceve/print.jsp";
  }
}
