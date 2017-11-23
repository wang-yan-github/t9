package t9.subsys.oa.asset.act;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import t9.core.data.T9RequestDbConn;
import t9.subsys.oa.asset.data.T9CpCptlInfo;
import t9.subsys.oa.asset.data.T9CpCptlRecord;
import t9.subsys.oa.asset.data.T9Cpcptl;
import t9.subsys.oa.asset.logic.T9CpAssetTypeLogic;
import t9.subsys.oa.asset.logic.T9CpCptlInfoLogic;
import t9.subsys.oa.asset.logic.T9CpCptlRecordLogic;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;

public class T9CpCptlRecordAct {
  /**
   * 查询分类代码
   * @param request
   * @param response
   * @throws Exception
   */
  public String cpTypeId(HttpServletRequest request,
      HttpServletResponse response)throws Exception {
    Connection dbConn = null;
    try {   
      //数据库的连接
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      //无条件，返回list
      List<T9CpCptlInfo> list = T9CpCptlRecordLogic.typeList(dbConn);
      //遍历返回的list，将数据保存到Json中
      String data = "[";

      T9CpCptlInfo type = new T9CpCptlInfo(); 
      for (int i = 0; i < list.size(); i++) {
        type = list.get(i);
        data = data + T9FOM.toJson(type).toString()+",";
      }
      if(list.size()>0){
        data = data.substring(0, data.length()-1);
      }
      data = data.replaceAll("\\n", "");
      data = data.replaceAll("\\r", "");
      data = data + "]";
      //将遍历数据保存到request
      //System.out.println(data);

      request.setAttribute(T9ActionKeys.RET_DATA, data);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询成功！");
    }catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询失败");
      throw e;
    }
    return "/core/inc/rtjson.jsp";
  }

  /**
   * 查询名称代码
   * @param request
   * @param response
   * @throws Exception
   */
  public String nameList(HttpServletRequest request,
      HttpServletResponse response)throws Exception {
    Connection dbConn = null;
    try {   
      //数据库的连接
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      //无条件，返回list
      String useFlag = request.getParameter("useFlag");
      List<T9CpCptlInfo> list = T9CpCptlInfoLogic.nameList(dbConn, useFlag); 
      //遍历返回的list，将数据保存到Json中
      String data = "[";

      T9CpCptlInfo type = new T9CpCptlInfo(); 
      for (int i = 0; i < list.size(); i++) {
        type = list.get(i);
        data = data + T9FOM.toJson(type).toString()+",";
      }
      if(list.size()>0){
        data = data.substring(0, data.length()-1);
      }
      data = data.replaceAll("\\n", "");
      data = data.replaceAll("\\r", "");
      data = data + "]";
      //将遍历数据保存到request
      //System.out.println(data);

      request.setAttribute(T9ActionKeys.RET_DATA, data);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询成功！");
    }catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询失败");
      throw e;
    }
    return "/core/inc/rtjson.jsp";
  }

  /**
   * 查询分类代码
   * @param request
   * @param response
   * @throws Exception
   */
  public String useStateList(HttpServletRequest request,
      HttpServletResponse response)throws Exception {
    Connection dbConn = null;
    try {   
      //数据库的连接
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      //无条件，返回list
      List<T9CpCptlInfo> list = T9CpCptlRecordLogic.useStateList(dbConn);
      //遍历返回的list，将数据保存到Json中
      String data = "[";

      T9CpCptlInfo type = new T9CpCptlInfo(); 
      for (int i = 0; i < list.size(); i++) {
        type = list.get(i);
        data = data + T9FOM.toJson(type).toString()+",";
      }
      if(list.size()>0){
        data = data.substring(0, data.length()-1);
      }
      data = data.replaceAll("\\n", "");
      data = data.replaceAll("\\r", "");
      data = data + "]";
      //将遍历数据保存到request
      //System.out.println(data);

      request.setAttribute(T9ActionKeys.RET_DATA, data);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询成功！");
    }catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询失败");
      throw e;
    }
    return "/core/inc/rtjson.jsp";
  }

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
      //数据库的连接
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      //将表单form1映射到T9Test实体类
      //T9ORM orm = new T9ORM();orm映射数据库
      T9CpCptlInfo cptlInfo = new T9CpCptlInfo();
      T9CpCptlRecord cptlRecord = new T9CpCptlRecord();

      String cpreFlag = request.getParameter("CPRE_FLAG");
      String cptlName = request.getParameter("CPTL_NAME");
      String cptlSpec = request.getParameter("CPTL_SPEC");
      String deptId2 = request.getParameter("DEPT_ID");
      String typeId = request.getParameter("TYPE_ID");
      String useFor = request.getParameter("USE_FOR"); 
      String useState = request.getParameter("USE_STATE");
      String useUser = request.getParameter("USE_USER");

      //      param = "CPRE_FLAG=" + cpreFlag + "&CPTL_NAME=" + cptlName 
      //      + "&CPTL_SPEC=" + cptlSpec + "&DEPT_ID=" + deptId2 + "&TYPE_ID=" + typeId
      //      + "&USE_FOR=" + useFor + "&USE_STATE=" + useState + "&USE_USER=" + useUser;

      int deptId = 0;
      if (!T9Utility.isNullorEmpty(deptId2)) {
        deptId = Integer.parseInt(deptId2);
        cptlRecord.setDeptId(deptId);
      }
      cptlRecord.setCpreFlag(cpreFlag);
      cptlInfo.setCptlName(cptlName);
      cptlInfo.setCptlSpec(cptlSpec);
      cptlInfo.setTypeId(typeId);
      cptlInfo.setUseFor(useFor);
      cptlInfo.setUseState(useState);
      cptlInfo.setUseUser(useUser);
      T9CpCptlRecordLogic cpcp = new T9CpCptlRecordLogic();
      String data = cpcp.listSelect(dbConn,request.getParameterMap(),cptlInfo,cptlRecord);
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
      //无条件，返回list
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      T9ORM orm = new T9ORM();//orm映射数据库
      T9CpCptlRecord record = (T9CpCptlRecord) T9FOM.build(request.getParameterMap());
      orm.saveSingle(dbConn,record);

      T9CpCptlRecordLogic cpre = new T9CpCptlRecordLogic();
      T9CpCptlInfo info = new T9CpCptlInfo();
      if (record.getCpreFlag().equals("1")) {
        info.setUseState("3");
        info.setUseDept(request.getParameter("deptId"));
        info.setUseUser(String.valueOf(person.getSeqId()));
      }
      if (record.getCpreFlag().equals("2")) {
        info.setUseState("1");
        info.setUseDept("");
        info.setUseUser("");
      }
      info.setSeqId(record.getCptlId());
      cpre.getUpdate(dbConn,info);

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
      T9CpCptlRecord cp = new T9CpCptlRecord();
      cp.setSeqId(seqId);
      orm.deleteSingle(dbConn,cp);
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
   * 查询级联数据
   * @param request
   * @param response
   * @throws Exception
   */
  public String getSelectID(HttpServletRequest request,
      HttpServletResponse response)throws Exception {
    Connection dbConn = null;
    try {   
      //数据库的连接
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      int seqId = Integer.parseInt(String.valueOf(request.getParameter("seqId")));
      T9CpCptlRecordLogic recordLogic = new T9CpCptlRecordLogic();

      List<T9Cpcptl> list = recordLogic.getCpreFlag1(dbConn,seqId);
      List<T9Cpcptl> list2 = recordLogic.getCpreFlag2(dbConn,seqId);
      String infoName = recordLogic.getName(dbConn, seqId);

      request.setAttribute("list",list);
      request.setAttribute("list2",list2);
      request.setAttribute("infoName",infoName);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询数据成功！");
      //request.setAttribute(T9ActionKeys.RET_DATA, userJson);
    }catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询数据失败");
      throw e;
    }
    return "/subsys/oa/asset/manage/cpreDetai.jsp";
  }

  /**
   * 查询数据
   * @param request
   * @param response
   * @throws Exception
   */
  public String getAssetId(HttpServletRequest request,
      HttpServletResponse response)throws Exception {
    Connection dbConn = null;
    try {   
      //数据库的连接
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      int seqId = Integer.parseInt(String.valueOf(request.getParameter("seqId")));
      T9CpCptlInfo type = T9CpAssetTypeLogic.getAsset(dbConn,seqId);
      request.setAttribute("type", type);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询数据成功！");
      //request.setAttribute(T9ActionKeys.RET_DATA, userJson);
    }catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询数据失败");
      throw e;
    }
    return "/subsys/oa/asset/manage/record/updateQuery.jsp";
  }
  /**
   *修改数据
   * @throws Exception 
   *
   **/
  public String editAsset(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    //数据库的连接
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request
    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
    try {
      dbConn = requestDbConn.getSysDbConn();
      T9CpCptlRecord record = new T9CpCptlRecord();
      record.setSeqId(Integer.parseInt(request.getParameter("seqId")));
      record.setDeptId(Integer.parseInt(request.getParameter("deptId")));
      record.setCpreMemo(request.getParameter("cpreMemo"));
      record.setCpreReason(request.getParameter("cpreReason"));
      record.setCprePlace(request.getParameter("cprePlace"));
      T9CpCptlRecordLogic.editAsset(dbConn,record);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "修改数据成功！");
      //request.setAttribute(T9ActionKeys.RET_DATA, userJson);
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "修改数据失败");
      throw e;
    }
    return "/core/inc/rtjson.jsp";

  }
}
