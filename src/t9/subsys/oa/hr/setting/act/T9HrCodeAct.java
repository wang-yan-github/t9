package t9.subsys.oa.hr.setting.act;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;
import t9.subsys.oa.hr.setting.data.T9HrCode;
import t9.subsys.oa.hr.setting.logic.T9HrCodeLogic;

public class T9HrCodeAct {
  /**
   * 新建
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String addCode(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
      String codeName = request.getParameter("codeName");
      String codeNo = request.getParameter("codeNo");
      String codeOrder = request.getParameter("codeOrder");
      String codeFlag = request.getParameter("codeFlag");
      String parentNo = request.getParameter("parentNo");//seqId
      T9HrCodeLogic codeLogic = new T9HrCodeLogic();
      if(T9Utility.isInteger(parentNo)){
        T9HrCode code = codeLogic.getCodeById(dbConn, parentNo) ;
        if(code != null && !T9Utility.isNullorEmpty(code.getCodeNo())){
          parentNo = code.getCodeNo();
        }else{
          parentNo = "";
        }
      }else{
        parentNo = "";
      }
      if(T9Utility.isNullorEmpty(codeFlag)){
        codeFlag = "0";
      }
  

      String errer = "";
      if(codeLogic.checkCodeNo(dbConn, codeNo,"")){
        errer = "1";
      }else{
        T9HrCode code = new T9HrCode();
        code.setCodeName(codeName);
        code.setCodeNo(codeNo);
        code.setCodeOrder(codeOrder);
        code.setParentNo(parentNo);
        code.setCodeFlag(codeFlag);
        codeLogic.addCode(dbConn, code);
      }
      String data = "{errer:\"" + errer + "\"}";
      request.setAttribute(T9ActionKeys.RET_DATA,data);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加数据成功！");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  
  /**
   * 新建下一级code
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String addChildCode(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
      String codeName = request.getParameter("codeName");
      String codeNo = request.getParameter("codeNo");
      String codeOrder = request.getParameter("codeOrder");
      String codeFlag = request.getParameter("codeFlag");
      String parentNo = request.getParameter("parentNo");//seqId
      T9HrCodeLogic codeLogic = new T9HrCodeLogic();
      if(T9Utility.isInteger(parentNo)){
        T9HrCode code = codeLogic.getCodeById(dbConn, parentNo) ;
        if(code != null && !T9Utility.isNullorEmpty(code.getCodeNo())){
          parentNo = code.getCodeNo();
        }else{
          parentNo = "";
        }
      }else{
        parentNo = "";
      }
      if(T9Utility.isNullorEmpty(codeFlag)){
        codeFlag = "";
      }else{
        codeFlag = "1";
      }
      String errer = "";
      if(codeLogic.checkCodeNo(dbConn, codeNo,parentNo,"")){
        errer = "1";
      }else{
        T9HrCode code = new T9HrCode();
        code.setCodeName(codeName);
        code.setCodeNo(codeNo);
        code.setCodeOrder(codeOrder);
        code.setParentNo(parentNo);
        code.setCodeFlag(codeFlag);
        codeLogic.addCode(dbConn, code);
      }
      String data = "{errer:\"" + errer + "\"}";
      request.setAttribute(T9ActionKeys.RET_DATA,data);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加数据成功！");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 更新
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String updateCode(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
      String seqId = request.getParameter("seqId");
      String codeName = request.getParameter("codeName");
      String codeNo = request.getParameter("codeNo");
      String codeOrder = request.getParameter("codeOrder");
      String codeFlag = request.getParameter("codeFlag");
      String parentNo = request.getParameter("parentNo");
      if(T9Utility.isNullorEmpty(parentNo)){
        parentNo = "";
      }
      if(T9Utility.isNullorEmpty(codeFlag)){
        codeFlag = "0";
      }
      T9HrCodeLogic codeLogic = new T9HrCodeLogic();

      String errer = "";
      if(codeLogic.checkCodeNo(dbConn, codeNo,seqId)){
        errer = "1";
      }else{
        if(T9Utility.isInteger(seqId)){
   /*       T9HrCode code = new T9HrCode();
          code.setSeqId(Integer.parseInt(seqId));
          code.setCodeName(codeName);
          code.setCodeNo(codeNo);
          code.setCodeOrder(codeOrder);
          code.setParentNo(parentNo);
          code.setCodeFlag(codeFlag);*/
          T9HrCode code = codeLogic.getCodeById(dbConn, seqId);
          String oldCodeNo = "";
          if(code != null && !T9Utility.isNullorEmpty(code.getCodeNo())){
            oldCodeNo = code.getCodeNo();
          }
          codeLogic.updateCode(dbConn, seqId,codeNo, codeName, codeOrder, codeFlag);
          codeLogic.updateChildCode(dbConn, oldCodeNo, codeNo);//更新子代码类别
        }
      }
      String data = "{errer:\"" + errer + "\"}";
      request.setAttribute(T9ActionKeys.RET_DATA,data);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加数据成功！");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 更新
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String updateChildCode(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
      String seqId = request.getParameter("seqId");
      String codeName = request.getParameter("codeName");
      String codeNo = request.getParameter("codeNo");
      String codeOrder = request.getParameter("codeOrder");
      String codeFlag = request.getParameter("codeFlag");
      String parentNo = request.getParameter("parentNo");
      T9HrCodeLogic codeLogic = new T9HrCodeLogic();
      if(T9Utility.isInteger(parentNo)){
        T9HrCode code = codeLogic.getCodeById(dbConn, parentNo) ;
        if(code != null && !T9Utility.isNullorEmpty(code.getCodeNo())){
          parentNo = code.getCodeNo();
        }else{
          parentNo = "";
        }
      }else{
        parentNo = "";
      }
      if(T9Utility.isNullorEmpty(codeFlag)){
        codeFlag = "0";
      }


      String errer = "";
      if(codeLogic.checkCodeNo(dbConn, codeNo,parentNo,seqId)){
        errer = "1";
      }else{
        if(T9Utility.isInteger(seqId)){
          T9HrCode code = codeLogic.getCodeById(dbConn, seqId);
          String oldCodeNo = "";
          if(code != null && !T9Utility.isNullorEmpty(code.getCodeNo())){
            oldCodeNo = code.getCodeNo();
          }
          codeLogic.updateCode(dbConn, seqId,codeNo, codeName, codeOrder, codeFlag);
        }
      }
      String data = "{errer:\"" + errer + "\"}";
      request.setAttribute(T9ActionKeys.RET_DATA,data);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加数据成功！");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 得到ById
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String selectCodeById(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
      String seqId = request.getParameter("seqId");
      T9HrCodeLogic codeLogic = new T9HrCodeLogic();
      String data = "";
      if(T9Utility.isInteger(seqId)){
        T9HrCode code = codeLogic.getCodeById(dbConn, seqId);
        if(code != null){
          data = T9FOM.toJson(code).toString();
        }
      }
      if(data.equals("")){
        data = "{}";
      }
      request.setAttribute(T9ActionKeys.RET_DATA,data);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加数据成功！");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 得到所有的父级代码表
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String selectCode(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
      String[] str = {"PARENT_NO = '' or PARENT_NO is null"};
      String seqId = request.getParameter("seqId");
      T9HrCodeLogic codeLogic = new T9HrCodeLogic();
      String data = "[";
      List<T9HrCode> codeList = new ArrayList<T9HrCode>();
      codeList = codeLogic.getCode(dbConn, str);
      for (int i = 0; i < codeList.size(); i++) {
        T9HrCode code = codeList.get(i);
        data = data + T9FOM.toJson(code) + ",";
      }
      if(codeList.size()>0){
        data = data.substring(0, data.length()-1);
      }
      data = data + "]";
      request.setAttribute(T9ActionKeys.RET_DATA,data);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加数据成功！");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 根据父级得到子集代码表
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String selectChildCode(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
      String parentNo = request.getParameter("parentNo");
      if(T9Utility.isNullorEmpty(parentNo)){
        parentNo = "";
      }
      String seqId = request.getParameter("seqId");
      T9HrCodeLogic codeLogic = new T9HrCodeLogic();
      String data = "[";
      List<T9HrCode> codeList = new ArrayList<T9HrCode>();
      codeList = codeLogic.getChildCode(dbConn, parentNo);
      for (int i = 0; i < codeList.size(); i++) {
        T9HrCode code = codeList.get(i);
        data = data + T9FOM.toJson(code) + ",";
      }
      if(codeList.size()>0){
        data = data.substring(0, data.length()-1);
      }
      data = data + "]";
      request.setAttribute(T9ActionKeys.RET_DATA,data);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加数据成功！");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 删除
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String deleteCode(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
      String seqId = request.getParameter("seqId");
      T9HrCodeLogic codeLogic = new T9HrCodeLogic();
      if(T9Utility.isInteger(seqId)){
        codeLogic.delCodeById(dbConn, seqId);
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加数据成功！");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
}
