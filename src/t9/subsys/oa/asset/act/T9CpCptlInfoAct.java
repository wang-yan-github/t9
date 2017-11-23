package t9.subsys.oa.asset.act;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import t9.core.data.T9RequestDbConn;
import t9.subsys.oa.asset.data.T9CpCptlInfo;
import t9.subsys.oa.asset.logic.T9CpCptlInfoLogic;
import t9.subsys.oa.asset.logic.T9CpCptlRecordLogic;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;

public class T9CpCptlInfoAct {
 
  /**
   *查询所有(分页)通用列表显示数据
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String listSelect(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
    //将表单form1映射到T9Test实体类
      //T9ORM orm = new T9ORM();orm映射数据库
      T9CpCptlInfo cptlInfo = new T9CpCptlInfo();

      String cptlNo = request.getParameter("cptlNo");
      String valMax = request.getParameter("cptlValMax");
      String cptlName = request.getParameter("cptlName");
      String cptlSpec = request.getParameter("cptlSpec");
      String val = request.getParameter("cptlVal");
      String listDate = request.getParameter("listDate");
      String getDate = request.getParameter("getDate");
      String keeper = request.getParameter("keeper");
      String safekeeping = request.getParameter("safekeeping");
      String remark = request.getParameter("remark");
      
      double cptMax = 0 ;
      if (!T9Utility.isNullorEmpty(val)) {
        double cptlVal = Double.parseDouble(val);
        cptlInfo.setCptlVal(cptlVal);
      }
      if (!T9Utility.isNullorEmpty(valMax)) {
        cptMax = Double.parseDouble(valMax);
      }
      if (!T9Utility.isNullorEmpty(listDate)) {
        cptlInfo.setListDate(Date.valueOf(listDate));
        
      }
      if (!T9Utility.isNullorEmpty(getDate)) {
        cptlInfo.setGetDate(Date.valueOf(getDate));
      }

      cptlInfo.setCptlName(cptlName);
      cptlInfo.setCptlNo(cptlNo);
      cptlInfo.setCptlSpec(cptlSpec);
      cptlInfo.setKeeper(keeper);
      cptlInfo.setSafekeeping(safekeeping);
      cptlInfo.setRemark(remark);
      
      T9CpCptlInfoLogic gift = new T9CpCptlInfoLogic();
      String data = gift.listSelect(dbConn,request.getParameterMap(),cptlInfo,cptMax);
      PrintWriter pw = response.getWriter();
      pw.println(data);
      pw.flush();
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;
  }
  /**
   * 删除数据
   * @param request
   * @param response
   * @throws Exception
   */
  public String getDelete(HttpServletRequest request,
      HttpServletResponse response)throws Exception {
    Connection dbConn = null;
    try {   
      //数据库的连接
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      //将表单form1映射到T9Test实体类
      T9ORM orm = new T9ORM();//orm映射数据库
      int seqId = Integer.parseInt(request.getParameter("seqId"));
      T9CpCptlInfo cp = new T9CpCptlInfo();
      T9CpCptlRecordLogic record = new T9CpCptlRecordLogic();
      cp.setSeqId(seqId);
      orm.deleteSingle(dbConn,cp);
      record.getDelete(dbConn,seqId);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "删除数据成功！");
    }catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "删除数据失败");
      throw e;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 查找数据
   * @param request
   * @param response
   * @throws Exception
   */
  public String getSelect(HttpServletRequest request,
      HttpServletResponse response)throws Exception {
    Connection dbConn = null;
    try {   
      //数据库的连接
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      //将表单form1映射到T9Test实体类
      T9ORM orm = new T9ORM();//orm映射数据库
      int seqId = Integer.parseInt(request.getParameter("seqId"));
      T9CpCptlInfo cp = new T9CpCptlInfo();
      cp.setSeqId(seqId);
      T9CpCptlInfo cpcp = (T9CpCptlInfo)orm.loadObjSingle(dbConn,T9CpCptlInfo.class,seqId);
      
      request.setAttribute("cpcp",cpcp);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询数据成功！");
    }catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询数据失败");
      throw e;
    }
    return "/subsys/oa/asset/manage/detai.jsp";
  }
  
  /**
   *查询所有(分页)通用列表显示数据
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String assetSelect(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {   
      //数据库的连接
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      //将表单form1映射到T9Test实体类
      //T9ORM orm = new T9ORM();orm映射数据库
      T9CpCptlInfo cptlInfo = new T9CpCptlInfo();
      String seqId2 = request.getParameter("SEQ_ID");
      String cptlNo = request.getParameter("CPTL_NO");
      String cptlName = request.getParameter("CPTL_NAME");
      String cptlSpec = request.getParameter("CPTL_SPEC");
      String typeId = request.getParameter("TYPE_ID");
      String useState = request.getParameter("USE_STATE");
      String useFor = request.getParameter("USE_FOR");
      String cpreFlag = request.getParameter("cpreFlag");

      int seqId = 0 ;
      if (!T9Utility.isNullorEmpty(seqId2)) {
        seqId = Integer.parseInt(seqId2);
        cptlInfo.setSeqId(seqId);
      } 
      cptlInfo.setCptlName(cptlName);
      cptlInfo.setCptlNo(cptlNo);
      cptlInfo.setCptlSpec(cptlSpec);
      cptlInfo.setTypeId(typeId);
      cptlInfo.setUseState(useState);
      cptlInfo.setUseFor(useFor);

      T9CpCptlInfoLogic gift = new T9CpCptlInfoLogic();
      String data = gift.assetSelect(dbConn,request.getParameterMap(),cptlInfo,cpreFlag);
      PrintWriter pw = response.getWriter();
      pw.println(data);
      pw.flush();
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;
  }
  
  /**
   * 查询数据
   * @param request
   * @param response
   * @throws Exception
   */
  public String querySelect(HttpServletRequest request,
      HttpServletResponse response)throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9CpCptlInfo cptlInfo = new T9CpCptlInfo();

      String cptlNo = request.getParameter("cptlNo");
      String valMax = request.getParameter("cptlValMax");
      String cptlName = request.getParameter("cptlName");
      String cptlSpec = request.getParameter("cptlSpec");
      String val = request.getParameter("cptlVal");
      String listDate = request.getParameter("listDate");
      String getDate = request.getParameter("getDate");
      String keeper = request.getParameter("keeper");
      String safekeeping = request.getParameter("safekeeping");
      String remark = request.getParameter("remark");

      double cptMax = 0 ;
      if (!T9Utility.isNullorEmpty(val)) {
        double cptlVal = Double.parseDouble(val);
        cptlInfo.setCptlVal(cptlVal);
      }
      if (!T9Utility.isNullorEmpty(valMax)) {
        cptMax= Double.parseDouble(valMax);
      }
      if (!T9Utility.isNullorEmpty(listDate)) {
        cptlInfo.setListDate(Date.valueOf(listDate));
        
      }
      if (!T9Utility.isNullorEmpty(getDate)) {
        cptlInfo.setGetDate(Date.valueOf(getDate));
      }
      cptlInfo.setCptlName(cptlName);
      cptlInfo.setCptlNo(cptlNo);
      cptlInfo.setCptlSpec(cptlSpec);
      cptlInfo.setKeeper(keeper);
      cptlInfo.setSafekeeping(safekeeping);
      cptlInfo.setRemark(remark);
      
      T9CpCptlInfoLogic gift = new T9CpCptlInfoLogic();
      String data = gift.querySelect(dbConn,request.getParameterMap(),cptlInfo,cptMax);
      PrintWriter pw = response.getWriter();
      pw.println(data);
      pw.flush();
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;
  }

  /**
   * 查询数据
   * @param request
   * @param response
   * @throws Exception
   */
  public String applySelect(HttpServletRequest request,
      HttpServletResponse response)throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9CpCptlInfoLogic gift = new T9CpCptlInfoLogic();
      String data = gift.applySelect(dbConn,request.getParameterMap());
      PrintWriter pw = response.getWriter();
      pw.println(data);
      pw.flush();
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;
  }
  
  /**
   *查询所有(分页)通用列表显示数据
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String cpcpSelect(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      String name = String.valueOf(person.getSeqId());
      T9CpCptlInfoLogic gift = new T9CpCptlInfoLogic();
      String data = gift.cpcpSelect(dbConn,request.getParameterMap(),name);
      PrintWriter pw = response.getWriter();
      pw.println(data);
      pw.flush();
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;
  }
  /**
   *查询所有ID串名字
   */
  public String userName(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqId = request.getParameter("seqId");
      T9CpCptlInfoLogic gift = new T9CpCptlInfoLogic();
      String data = gift.getName(dbConn,seqId);
      request.setAttribute(T9ActionKeys.RET_DATA,"\"" + data + "\"");
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询成功！");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   *查询所有部门串名字
   */
  public String userDept(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqId = request.getParameter("seqId");
      T9CpCptlInfoLogic gift = new T9CpCptlInfoLogic();
      String data = gift.getDept(dbConn,seqId);
      request.setAttribute(T9ActionKeys.RET_DATA,"\"" + data + "\"");
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询成功！");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 添加数据
   * @param request
   * @param response
   * @throws Exception
   */
  public String add(HttpServletRequest request,
      HttpServletResponse response)throws Exception {
    Connection dbConn = null;
    try {   
      //数据库的连接
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9ORM orm = new T9ORM();//orm映射数据库
      T9CpCptlInfo cpCptlInfo = (T9CpCptlInfo)T9FOM.build(request.getParameterMap());
      orm.saveSingle(dbConn, cpCptlInfo);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "增加成功！");
    }catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "增加失败");
      throw e;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 查找数据
   * @param request
   * @param response
   * @throws Exception
   */
  public String getSelect2(HttpServletRequest request,
      HttpServletResponse response)throws Exception {
    Connection dbConn = null;
    try {   
      //数据库的连接
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      //将表单form1映射到T9Test实体类
      T9ORM orm = new T9ORM();//orm映射数据库
      int seqId = Integer.parseInt(request.getParameter("seqId"));
      T9CpCptlInfo cp = new T9CpCptlInfo();
      cp.setSeqId(seqId);
      T9CpCptlInfo cpcp = (T9CpCptlInfo)orm.loadObjSingle(dbConn,T9CpCptlInfo.class,seqId);
      request.setAttribute("cpcp",cpcp);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询数据成功！");
    }catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询数据失败");
      throw e;
    }
    return "/subsys/oa/asset/manage/updateAsset.jsp";
  }
  /**
   * 根据ID修改数据
   * @param request
   * @param response
   * @throws Exception
   */
  public String editAsset(HttpServletRequest request,
      HttpServletResponse response)throws Exception {
    Connection dbConn = null;
    try {   
      //数据库的连接
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      //将表单form1映射到T9Test实体类
      T9ORM orm = new T9ORM();//orm映射数据库
      T9CpCptlInfo cp = (T9CpCptlInfo)T9FOM.build(request.getParameterMap());
      orm.updateSingle(dbConn,cp);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "修改数据成功！");
    }catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "修改数据失败");
      throw e;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 根据ID修改数据
   * @param request
   * @param response
   * @throws Exception
   */
  public String selectID(HttpServletRequest request,
      HttpServletResponse response)throws Exception {
    Connection dbConn = null;
    try {   
      //数据库的连接
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      //将表单form1映射到T9Test实体类
      T9ORM orm = new T9ORM();//orm映射数据库
      int seqId = Integer.parseInt(request.getParameter("seqId"));
      T9CpCptlInfo cp = (T9CpCptlInfo)orm.loadObjSingle(dbConn, T9CpCptlInfo.class, seqId);
      request.setAttribute("cp",cp);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "修改数据成功！");
    }catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "修改数据失败");
      throw e;
    }
    return "/subsys/oa/asset/query/apply.jsp";
  }
}
