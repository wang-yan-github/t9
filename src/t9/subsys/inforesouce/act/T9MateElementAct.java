package t9.subsys.inforesouce.act;

import java.sql.Connection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Out;
import t9.subsys.inforesouce.data.T9MateValue;
import t9.subsys.inforesouce.logic.T9MateElementLogic;


public class T9MateElementAct {
  private static Logger log = Logger.getLogger("t9.core.funcs.system.syslog.act.T9SysLogAct");
  
  public String addelement(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    //System.out.println("ss");
    
    
     String boardNo = request.getParameter("BOARD_NO"); //编号
     int num = Integer.parseInt(boardNo);
     if(num >= 100){
       boardNo = "MEX"+boardNo;
     }else{
       boardNo = "M"+boardNo;
     }
    // int boardNo = Integer.parseInt(board);
     String cnName = request.getParameter("cn_NAME");
     String enName = request.getParameter("en_NAME");
     String defineText = request.getParameter("define_TEXT");
     String aimText = request.getParameter("aim_TEXT"); 
     String constraint = request.getParameter("constraint");//约束性
     String repeat = request.getParameter("repeat");
     String element_type = request.getParameter("element_type");
     String typeId = request.getParameter("typeId");
     String pd = request.getParameter("pId");
     String []eleType = request.getParameterValues("eleType");
     String elementType = "";
     for(int i = 0; i<eleType.length; i++){
          elementType += eleType[i]+",";
     }
     
     int pid = Integer.parseInt(pd);
     //System.out.println(boardNo+"::"+cnName+"::"+enName+"::"+defineText+"::"+aimText+"::"+constraint+"::"+repeat+"::"+element_type+"::"+typeId);
    Connection dbConn = null;
    try {
     //System.out.println("ddddd");
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
        T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
        T9MateElementLogic element= new T9MateElementLogic();
      element.addelement(boardNo,cnName,enName,defineText,aimText,constraint,repeat,element_type,typeId,pid,elementType,dbConn,person);
     /* T9SysLogSaveLogic save = new T9SysLogSaveLogic();
      String OkandSory="";
      if(person.isAdmin()){
       OkandSory =  save.getSaveLog(dbConn, person);
      }
  */
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
     }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
      throw ex;
    }
    return "/t9/subsys/inforesouce/act/T9MateTypeAct/findMata.act?defalutType=1";
  }
  // 增加自定义值域
  
  public String addvalue(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    //System.out.println("ss");
  String  valueId = request.getParameter("value_id");
 
  String valueName =  request.getParameter("value_name");  
 
  Connection dbConn = null;
  String seqId = request.getParameter("seqId");
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
        T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
        T9MateElementLogic element= new T9MateElementLogic();
        element.addValueRange(dbConn,Integer.parseInt(seqId),valueId, valueName, person);
        
       /* T9SysLogSaveLogic save = new T9SysLogSaveLogic();
      String OkandSory="";
      if(person.isAdmin()){
       OkandSory =  save.getSaveLog(dbConn, person);
      }
  */
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
     }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
      throw ex;
    }
    // return null;/t9/core/inforesource/define.jsp
    return "/t9/subsys/inforesouce/act/T9MateElementAct/selectvalue.act?seqid="+seqId+"&&number="+valueId;
  }
  
  /**
   * 查询值域 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
 
  public String selectvalue(HttpServletRequest request,
      HttpServletResponse response) throws Exception {   
      Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
        T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
        T9MateElementLogic element= new T9MateElementLogic();
        String seqId = request.getParameter("seqid");
        String number = request.getParameter("number");
        
      List<T9MateValue> va  =   element.selectvalue(Integer.parseInt(seqId),dbConn, person);
      request.setAttribute("va", va);
       /* T9SysLogSaveLogic save = new T9SysLogSaveLogic();
      String OkandSory="";
      if(person.isAdmin()){
       OkandSory =  save.getSaveLog(dbConn, person);
      }
  */
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute("seqId", seqId);
      request.setAttribute("number", number);
     }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
      throw ex;
    }
     return "/subsys/inforesource/define.jsp";
  }
  
  
  public String updatevalue(HttpServletRequest request,
      HttpServletResponse response) throws Exception {   
      Connection dbConn = null;
      String pId="";
      String number="";
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
        T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
        T9MateElementLogic element= new T9MateElementLogic();
        number = request.getParameter("valueId");
      //  T9Out.println(valueId+"uuuuuuuuuuuu");
        String valueName = request.getParameter("valueName");
        String aid = request.getParameter("aid");
         pId = request.getParameter("pId");
       element.updatevalue(Integer.parseInt(aid), number,valueName, dbConn, person);
     // request.setAttribute("va", va);
       /* T9SysLogSaveLogic save = new T9SysLogSaveLogic();
      String OkandSory="";
      if(person.isAdmin()){
       OkandSory =  save.getSaveLog(dbConn, person);
      }
  */
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute("seqId", number);
     }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
      throw ex;
    }
     return "/t9/subsys/inforesouce/act/T9MateElementAct/selectvalue.act?seqid="+pId+"&&number="+number;
  }
  
  /**
   * 判断编号是否存在
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String isExitNos(HttpServletRequest request, HttpServletResponse response)throws Exception{
    
    T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
    Connection dbConn = null;
    try{
      dbConn = requestDbConn.getSysDbConn();
      T9MateElementLogic element= new T9MateElementLogic();
      String nos = request.getParameter("nos");
      String seqId = request.getParameter("seqId");
      boolean isExit = element.isExitNos(dbConn, nos, seqId);
      if(isExit){//存在编码
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);        
        request.setAttribute(T9ActionKeys.RET_DATA, "1");
      }else{   //不存在编码
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);        
        request.setAttribute(T9ActionKeys.RET_DATA, "0");
      }
    } catch (Exception e){
      throw e;
    }
    return "/core/inc/rtjson.jsp";
  }
  
}
