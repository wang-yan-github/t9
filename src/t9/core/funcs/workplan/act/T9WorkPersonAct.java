package t9.core.funcs.workplan.act;
import java.sql.Connection;
import java.sql.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.mobilesms.logic.T9MobileSms2Logic;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.sms.data.T9SmsBack;
import t9.core.funcs.sms.logic.T9SmsUtil;
import t9.core.funcs.system.selattach.util.T9SelAttachUtil;
import t9.core.funcs.workplan.data.T9WorkPerson;
import t9.core.funcs.workplan.data.T9WorkPlan;
import t9.core.funcs.workplan.data.T9WorkPlanCont;
import t9.core.funcs.workplan.logic.T9WorkLogic;
import t9.core.funcs.workplan.logic.T9WorkPersonLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Out;
import t9.core.util.T9Utility;
import t9.core.util.form.T9FOM;

public class T9WorkPersonAct {
  /***
   * 根据ID查询数据
   * @return
   * @throws Exception 
   */
  public String selectPerson(HttpServletRequest request,
      HttpServletResponse response) throws  Exception {
    Connection dbConn = null;
    try {
      //数据库的连接
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      //实列化业务层
      T9WorkLogic logic = new T9WorkLogic();
      T9WorkPersonLogic personLogic = new T9WorkPersonLogic();
      //使用request.getParameter接受页面的值
      int seqId = Integer.parseInt(request.getParameter("seqId"));
      String name = request.getParameter("name");
      
      List<T9WorkPerson> list = personLogic.selectPerson(dbConn,seqId,name);
      T9WorkPlan plan = logic.selectId(dbConn, seqId);
      request.setAttribute("plan", plan);
      request.setAttribute("name", name);
      request.setAttribute("person", list);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询数据成功！");
      //orm.saveSingle(dbConn, T9Work_Plan.class);//保存数据
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询数据失败");
      throw e;
    } finally {
    }
    return "/core/funcs/workplan/show/resource.jsp";
  }
  
  /***
   * 根据ID查询数据
   * @return
   * @throws Exception 
   */
  public String person(HttpServletRequest request,
      HttpServletResponse response) throws  Exception {
    Connection dbConn = null;
    try {
      //数据库的连接
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      //实列化业务层
      T9WorkLogic logic = new T9WorkLogic();
      T9WorkPersonLogic personLogic = new T9WorkPersonLogic();
      //使用request.getParameter接受页面的值
      int seqId = Integer.parseInt(request.getParameter("seqId"));
      String name = request.getParameter("name");
      
      List<T9WorkPerson> list = personLogic.selectPerson(dbConn,seqId,name);
      T9WorkPlan plan = logic.selectId(dbConn, seqId);
      request.setAttribute("plan", plan);
      request.setAttribute("name", name);
      request.setAttribute("person", list);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询数据成功！");
      //orm.saveSingle(dbConn, T9Work_Plan.class);//保存数据
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询数据失败");
      throw e;
    } finally {
    }
    return "/core/funcs/workplan/show/resource_diary.jsp";
  }
  /***
   * 根据ID查询数据
   * @return
   * @throws Exception 
   */
  public String selectPerson2(HttpServletRequest request,
      HttpServletResponse response) throws  Exception {
    Connection dbConn = null;
    try {
      //数据库的连接
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      //实列化业务层
      T9WorkPersonLogic personLogic = new T9WorkPersonLogic();
      //使用request.getParameter接受页面的值
      int seqId = Integer.parseInt(request.getParameter("seqId"));
      String name = request.getParameter("name");
      List<T9WorkPerson> list = personLogic.selectPerson(dbConn,seqId,name);
      
      //定义数组将数据保存到Json中
      String data = "[";
      T9WorkPerson person = new T9WorkPerson(); 
      for (int i = 0; i < list.size(); i++) {
        person = list.get(i);
        data = data + T9FOM.toJson(person).toString()+",";
      }
      if(list.size()>0){
        data = data.substring(0, data.length()-1);
      }
      data = data.replaceAll("\\n", "");
      data = data.replaceAll("\\r", "");
      data = data + "]";
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询数据成功！");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
      //orm.saveSingle(dbConn, T9Work_Plan.class);//保存数据
     //request.setAttribute("person", list); 
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询数据失败");
      throw e;
    } finally {
    }
    return "/core/inc/rtjson.jsp";
  }
  /***
   * 根据ID添加数据
   * @return
   * @throws Exception 
   */
  public String addPerson(HttpServletRequest request,
      HttpServletResponse response) throws  Exception {
    Connection dbConn = null;
    try {
      //数据库的连接
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      //实列化业务层
      T9WorkPersonLogic personLogic = new T9WorkPersonLogic();
      T9WorkPerson person = new T9WorkPerson();
      T9Person persons = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      //使用request.getParameter接受页面的值
      String attachmentId = request.getParameter("attachmentId");
      String attachmentName = request.getParameter("attachmentName");
      String smsflag3 = request.getParameter("sms2flag");
      T9SelAttachUtil sel = new T9SelAttachUtil(request, T9WorkPlanCont.MODULE);
      String attachNewId = sel.getAttachIdToString(",");
      String attachNewName = sel.getAttachNameToString("*");
      if(!"".equals(attachNewId) && !"".equals(attachmentId) &&  !attachmentId.trim().endsWith(",")){
        attachmentId += ",";
      }
      attachmentId += attachNewId;
      if(!"".equals(attachNewName) && !"".equals(attachmentName)  && !attachmentName.trim().endsWith("*")){
        attachmentName += "*";
      }
      attachmentName += attachNewName;
      
      String sqlId = String.valueOf(request.getParameter("sqlId"));
      String sqlName = String.valueOf(request.getParameter("sqlName"));
      
      String smsflag = request.getParameter("smsflag");
      if (smsflag.equals("true")) {
        T9SmsBack sb = new T9SmsBack();
        sb.setSmsType("12");
        sb.setContent("有新的计划任务增加，请查看.");
        sb.setSendDate(new java.util.Date());
        sb.setFromId(persons.getSeqId());
        sb.setToId(sqlName);
        sb.setRemindUrl("/t9/core/funcs/workplan/act/T9WorkPersonAct/person.act?seqId=" + sqlId + "&name=" + sqlName + "&openFlag=1&openWidth=800&openHeight=550");
        T9SmsUtil.smsBack(dbConn, sb);
      }
      if (smsflag3.equals("true")) {
        T9MobileSms2Logic sb = new T9MobileSms2Logic();
        sb.remindByMobileSms(dbConn,sqlName,person.getSeqId(),"有新的计划任务增加，请查看:",new java.util.Date());
      }
      
      person.setPlanId(Integer.parseInt(request.getParameter("PLAN_ID")));
      person.setPuserId(request.getParameter("PUSER_ID"));
      person.setAttachmentId(attachmentId);
      person.setAttachmentName(attachmentName);
      person.setPplanContent(request.getParameter("PPLAN_CONTENT"));
      person.setPuseResource(request.getParameter("PUSE_RESOURCE"));
      person.setPbegeiDate(Date.valueOf(request.getParameter("statrTime")));
      person.setPendDate(Date.valueOf(request.getParameter("endTime")));
      
      personLogic.addPerson(dbConn, person);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加数据成功！");
      //orm.saveSingle(dbConn, T9Work_Plan.class);//保存数据
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加数据失败");
      throw e;
    } finally {
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /***
   * 根据ID删除数据
   * @return
   * @throws Exception 
   */
  public String deletePerson(HttpServletRequest request,
      HttpServletResponse response) throws  Exception {
    Connection dbConn = null;
    int planId;
    String name;
    try {
      //数据库的连接
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      //实列化业务层
      T9WorkPersonLogic personLogic = new T9WorkPersonLogic();
      //使用request.getParameter接受页面的值
      planId = Integer.parseInt(request.getParameter("planId"));
      name = request.getParameter("name");
      
      personLogic.deletePerson(dbConn, Integer.parseInt(request.getParameter("seqId")));
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "删除数据成功！");
      //orm.saveSingle(dbConn, T9Work_Plan.class);//保存数据
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "删除数据失败");
      throw e;
    } finally {
    }
    return "/t9/core/funcs/workplan/act/T9WorkPersonAct/selectPerson.act?seqId=" + planId + "&name=" + name;
  }
  /***
   * 根据ID查询数据
   * @return
   * @throws Exception 
   */
  public String selectId(HttpServletRequest request,
      HttpServletResponse response) throws  Exception {
    Connection dbConn = null;
    try {
      //数据库的连接
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      //实列化业务层
      T9WorkPersonLogic personLogic = new T9WorkPersonLogic();
      //使用request.getParameter接受页面的值
      int seqId = Integer.parseInt(request.getParameter("seqId"));
      String name = String.valueOf(request.getParameter("name"));
      
      T9WorkPerson  person = personLogic.selectId(dbConn,seqId);
      request.setAttribute("person",person);
      request.setAttribute("name",name);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询数据成功！");
      //orm.saveSingle(dbConn, T9Work_Plan.class);//保存数据
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询数据失败");
      throw e;
    } finally {
    }
    return "/core/funcs/workplan/show/modify_resource.jsp";
  }
  public String updatePerson(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    String name;
    int planId;
    try {
      //数据库的连接
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      //实列化业务层
      T9WorkPersonLogic personLogic = new T9WorkPersonLogic();
      T9WorkPerson person = new T9WorkPerson();
      //使用request.getParameter接受页面的值
      name = request.getParameter("PUSER_ID");
      planId = Integer.parseInt(request.getParameter("PLAN_ID"));
      String attachmentId = request.getParameter("attachmentId");
      String attachmentName = request.getParameter("attachmentName");
//      if (T9Utility.isNullorEmpty(attachmentName)) {
//        attachmentName = request.getParameter("attachmentName2");
//      }
//      if (T9Utility.isNullorEmpty(attachmentId)) {
//        attachmentId = request.getParameter("attachmentId2");
//      }
      
      T9SelAttachUtil sel = new T9SelAttachUtil(request, T9WorkPlanCont.MODULE);
      String attachNewId = sel.getAttachIdToString(",");
      String attachNewName = sel.getAttachNameToString("*");
      if(!"".equals(attachNewId) && !T9Utility.isNullorEmpty(attachmentId) && !attachmentId.trim().endsWith(",")){
        attachmentId += ",";
      }
      attachmentId += attachNewId;
      if(!"".equals(attachNewName) && !T9Utility.isNullorEmpty(attachmentName) && !attachmentName.trim().endsWith("*")){
        attachmentName += "*";
      }
      attachmentName += attachNewName;
      
      person.setAttachmentName(attachmentName);
      person.setAttachmentId(attachmentId);
      person.setPplanContent(request.getParameter("PPLAN_CONTENT"));
      person.setPuseResource(request.getParameter("PUSE_RESOURCE"));
      person.setPbegeiDate(Date.valueOf(request.getParameter("statrTime")));
      person.setPendDate(Date.valueOf(request.getParameter("endTime")));
      person.setSeqId(Integer.parseInt(request.getParameter("seqId2")));
      
      personLogic.updatePerson(dbConn, person);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "修改数据成功！");
      //orm.saveSingle(dbConn, T9Work_Plan.class);//保存数据
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "修改数据失败");
      throw e;
    } finally {
    }
    return "/t9/core/funcs/workplan/act/T9WorkPersonAct/selectPerson.act?seqId=" + planId + "&name=" + name;
    
  }
  /***
   * 根据ID查询,修改,附件删除数据
   * @return
   * @throws Exception 
   */
  public String updatePerson2(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    String name;
    int planId;
    try {
      //数据库的连接
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      //实列化业务层
      T9WorkPersonLogic personLogic = new T9WorkPersonLogic();
      T9WorkPerson person = new T9WorkPerson();
      //使用request.getParameter接受页面的值
      name = request.getParameter("PUSER_ID");
      planId = Integer.parseInt(request.getParameter("PLAN_ID"));
      String attachmentId = request.getParameter("attachmentId");
      String attachmentName = request.getParameter("attachmentName");
//      if (T9Utility.isNullorEmpty(attachmentName)) {
//        attachmentName = request.getParameter("attachmentName2");
//      }
//      if (T9Utility.isNullorEmpty(attachmentId)) {
//        attachmentId = request.getParameter("attachmentId2");
//      }
      
      T9SelAttachUtil sel = new T9SelAttachUtil(request, T9WorkPlanCont.MODULE);
      String attachNewId = sel.getAttachIdToString(",");
      String attachNewName = sel.getAttachNameToString("*");
      if(!"".equals(attachNewId) && !T9Utility.isNullorEmpty(attachmentId) && !attachmentId.trim().endsWith(",")){
        attachmentId += ",";
      }
      attachmentId += attachNewId;
      if(!"".equals(attachNewName) && !T9Utility.isNullorEmpty(attachmentName) && !attachmentName.trim().endsWith("*")){
        attachmentName += "*";
      }
      attachmentName += attachNewName;
      
      person.setAttachmentName(attachmentName);
      person.setAttachmentId(attachmentId);
      person.setPplanContent(request.getParameter("PPLAN_CONTENT"));
      person.setPuseResource(request.getParameter("PUSE_RESOURCE"));
      person.setPbegeiDate(Date.valueOf(request.getParameter("statrTime")));
      person.setPendDate(Date.valueOf(request.getParameter("endTime")));
      person.setSeqId(Integer.parseInt(request.getParameter("seqId2")));
      
      personLogic.updatePerson(dbConn, person);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "修改数据成功！");
      //orm.saveSingle(dbConn, T9Work_Plan.class);//保存数据
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "修改数据失败");
      throw e;
    } finally {
    }
    return "/core/inc/rtjson.jsp";
  }
}
