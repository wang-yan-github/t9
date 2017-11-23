package t9.core.funcs.system.dimension.act;

import java.io.PrintWriter;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.system.dimension.data.T9Dimension;
import t9.core.funcs.system.dimension.logic.T9DimensionLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;

public class T9DimensionAct {
  private static Logger log = Logger.getLogger("t9.core.funcs.system.dimension.act.T9DTreeAct");

  /**
   * 新建维度

   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String addDemensionInfo(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    T9DimensionLogic fileSortLogic = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      fileSortLogic = new T9DimensionLogic();

      T9Dimension fileSort = (T9Dimension) T9FOM.build(request.getParameterMap());
      fileSortLogic.saveFileSortInfo(dbConn, fileSort);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功添加数据");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

  /**
   * 取得所有维度信息
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getDimensionInfo(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    int sortParent = 0;
    T9DimensionLogic dimensionLogic = new T9DimensionLogic();

    StringBuffer sb = new StringBuffer();
    List<T9Dimension> fileSorts = new ArrayList<T9Dimension>();
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      //System.out.println("dbConn>>>>>>>>>>>>>>..." + dbConn);
      Map map = new HashMap();
      map.put("SORT_PARENT", sortParent);
      String[] str = {"SORT_PARENT =" + sortParent + " order by SORT_NO"};

      fileSorts = dimensionLogic.getDimensionInfo(dbConn, str);

      if(fileSorts.size()>0){
        sb.append("[");
        for (int i = 0; i < fileSorts.size(); i++) {
          T9Dimension dimension = fileSorts.get(i);
          String sortNo = dimension.getSortNo() == null ? "" : dimension
              .getSortNo();
          
          sb.append("{");
          sb.append("sqlId:\"" + dimension.getSeqId() + "\"");
          sb.append(",sortParent:\"" + dimension.getSortParent() + "\"");
          sb.append(",sortNo:\"" + sortNo + "\"");
          sb.append(",sortName:\"" + dimension.getSortName() + "\"");
          sb.append(",sortType:\"" + dimension.getSortType() + "\"");
          sb.append(",deptId:\"" + dimension.getDeptId() + "\"");
          sb.append(",userId:\"" + dimension.getUserId() + "\"");
          sb.append(",newUser:\"" + dimension.getNewUser() + "\"");
          sb.append(",manageUser:\"" + dimension.getManageUser() + "\"");
          sb.append(",DownUser:\"" + dimension.getDownUser() + "\"");
          sb.append(",shareUser:\"" + dimension.getShareUser() + "\"");
          sb.append(",owner:\"" + dimension.getOwner() + "\"");
          sb.append("},");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("]");        
      }else {
        sb.append("[]");
      }

      // data=T9FOM.toJson(mettingRoom).toString();
      //System.out.println("sb>>>>>>>>>>" + sb);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

  /**
   * 取得所有维度信息

   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getParentDimensionInfo(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    int sortParent = 0;
    T9DimensionLogic dimensionLogic = new T9DimensionLogic();

    StringBuffer sb = new StringBuffer();
    List<T9Dimension> fileSorts = new ArrayList<T9Dimension>();
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      //System.out.println("dbConn>>>>>>>>>>>>>>..." + dbConn);
      Map map = new HashMap();
      map.put("SORT_PARENT", sortParent);
      String[] str = {"SORT_PARENT =" + sortParent + " order by SORT_NO"};

      fileSorts = dimensionLogic.getDimensionInfo(dbConn, str);

      if(fileSorts.size()>0){
        sb.append("[");
        for (int i = 0; i < fileSorts.size(); i++) {
          T9Dimension dimension = fileSorts.get(i);
          String sortNo = dimension.getSortNo() == null ? "" : dimension
              .getSortNo();
          
          sb.append("{");
          sb.append("sqlId:\"" + dimension.getSeqId() + "\"");
    /*      sb.append(",sortParent:\"" + dimension.getSortParent() + "\"");
          sb.append(",sortNo:\"" + sortNo + "\"");*/
          sb.append(",title:\"" + dimension.getSortName() + "\"");
 /*         sb.append(",sortType:\"" + dimension.getSortType() + "\"");
          sb.append(",deptId:\"" + dimension.getDeptId() + "\"");
          sb.append(",userId:\"" + dimension.getUserId() + "\"");
          sb.append(",newUser:\"" + dimension.getNewUser() + "\"");
          sb.append(",manageUser:\"" + dimension.getManageUser() + "\"");
          sb.append(",DownUser:\"" + dimension.getDownUser() + "\"");
          sb.append(",shareUser:\"" + dimension.getShareUser() + "\"");
          sb.append(",owner:\"" + dimension.getOwner() + "\"");*/
          sb.append("},");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("]");        
      }else {
        sb.append("[]");
      }

      // data=T9FOM.toJson(mettingRoom).toString();
      //System.out.println("sb>>>>>>>>>>" + sb);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

  /**
   * 通过id获取信息
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getDimensionInfoById(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    T9DimensionLogic fileSortLogic = new T9DimensionLogic();
    StringBuffer sb = new StringBuffer("[");
    String seqId = request.getParameter("seqId");
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      //System.out.println("dbConn>>>>>>>>>>>>>>..." + dbConn + "    seqId>>>>"
//          + seqId);
      T9Dimension fileSort = fileSortLogic.getFileSortInfoById(dbConn, seqId);
      String sortNo = fileSort.getSortNo() == null ? "" : fileSort.getSortNo();
      sb.append("{");
      sb.append("sqlId:\"" + fileSort.getSeqId() + "\"");
      sb.append(",sortParent:\"" + fileSort.getSortParent() + "\"");
      sb.append(",sortNo:\"" + sortNo + "\"");
      sb.append(",sortName:\"" + fileSort.getSortName() + "\"");
      sb.append(",sortType:\"" + fileSort.getSortType() + "\"");
      sb.append(",deptId:\"" + fileSort.getDeptId() + "\"");
      sb.append(",userId:\"" + fileSort.getUserId() + "\"");
      sb.append(",newUser:\"" + fileSort.getNewUser() + "\"");
      sb.append(",manageUser:\"" + fileSort.getManageUser() + "\"");
      sb.append(",DownUser:\"" + fileSort.getDownUser() + "\"");
      sb.append(",shareUser:\"" + fileSort.getShareUser() + "\"");
      sb.append(",owner:\"" + fileSort.getOwner() + "\"");
      sb.append("},");
      sb.append("]");

      // String data=T9FOM.toJson(fileSort).toString();
      //System.out.println("T9FOM.toJson(mettingRoom).toString()>>>>>>>>>>" + sb);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());

    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 通过id获取该维度的所有权限信息

   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getPrivteById(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    T9DimensionLogic fileSortLogic = new T9DimensionLogic();
    StringBuffer sb = new StringBuffer("[");
    String seqId = request.getParameter("seqId");
    //获取登录用户信息
    T9Person loginUser = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
    int loginUserSeqId=loginUser.getSeqId();
    int loginUserDeptId=loginUser.getDeptId();
    String loginUserRoleId=loginUser.getUserPriv();
    
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
//      T9FileSort fileSort = fileSortLogic.getFileSortInfoById(dbConn, seqId);
      Map map=new HashMap();
      map.put("SEQ_ID", Integer.parseInt(seqId));
      String  data=fileSortLogic.getVisiPriv(dbConn,map,loginUserSeqId,loginUserDeptId,loginUserRoleId);
      
      // String data=T9FOM.toJson(fileSort).toString();
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, data);

    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  

  /**
   * 通过id递归获取维度
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getSortNameById(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    T9DimensionLogic fileSortLogic = new T9DimensionLogic();
    int seqId = Integer.parseInt(request.getParameter("seqId"));
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      //System.out.println("dbConn>>>>>>>>>>>>>>..." + dbConn + "    seqId>>>>"
//          + seqId);
      StringBuffer buffer = new StringBuffer();
      fileSortLogic.getSortNamePath(dbConn, seqId, buffer);
      String sortName = buffer.toString();
      String sortNames[] = sortName.split(",");
      StringBuffer sb = new StringBuffer();
      for (int i = sortNames.length - 1; i >= 0; i--) {
        sb.append(sortNames[i]);
      }
      sb.deleteCharAt(sb.length() - 1);

      String data = "[{sortName:\"" + sb.toString() + "\"}]";
      T9ORM orm = new T9ORM();
      T9Dimension dimension = (T9Dimension) orm.loadObjSingle(dbConn, T9Dimension.class, seqId);
      // System.out.println("data>>>>:"+data);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, String.valueOf(dimension.getSortParent()));
      request.setAttribute(T9ActionKeys.RET_DATA, data);

    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

  /**
   * 更新编辑子维度信息
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String updateDimensionInfoById(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    T9DimensionLogic dimensionLogic = new T9DimensionLogic();
    int seqId = Integer.parseInt(request.getParameter("seqId"));
    //System.out.println("Integer.parseInt(request.getParameter()>>>>>" + seqId);
    String sortNo = request.getParameter("sortNo");
    String sortName = request.getParameter("sortName");

    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      Map map = new HashMap();
      map.put("seqId", seqId);
      map.put("sortNo", sortNo);
      map.put("sortName", sortName);
      dimensionLogic.updateEditFileSort(dbConn, map);
      T9Dimension dimension = dimensionLogic.getSortNameById(dbConn, seqId);
      int nodeId = dimension.getSeqId();
      String folderName = dimension.getSortName();
      String date = "[{nodeId:\"" + nodeId + "\",sortName:\"" + folderName
          + "\" }]";
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "更新成功");
      request.setAttribute(T9ActionKeys.RET_DATA, date);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

  /**
   * 删除信息
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String delDimensionInfoById(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    int seqId = Integer.parseInt(request.getParameter("seqId"));
    Connection dbConn = null;
    T9Dimension dimension = null;
    T9DimensionLogic dimensionLogic = new T9DimensionLogic();
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      //System.out.println("dbConn>>>>>>>>>>del>>>>" + dbConn);
      dimension = new T9Dimension();
      dimension.setSeqId(seqId);
      dimensionLogic.delDimensionInfoById(dbConn, dimension);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "数据删除成功！");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

  /**
   * 获取树形结构信息,用于权限设置用，不考虑权限。

   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getTree(HttpServletRequest request, HttpServletResponse response)
      throws Exception {
    String idStr = request.getParameter("id");
    String sortIdStr = request.getParameter("seqId");
    int id = 0;
    if (idStr != null && !"".equals(idStr)) {
      id = Integer.parseInt(idStr);
    }  

    Connection dbConn = null;
    T9DimensionLogic fileSortLogic = new T9DimensionLogic();
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      Map map = new HashMap();
      StringBuffer sb = new StringBuffer("[");
      if (sortIdStr != null && !"".equals(sortIdStr) && id == 0) {
        T9Dimension fileSort = fileSortLogic.getFileSortInfoById(dbConn, sortIdStr);
        if (fileSort!=null) {
          int seqId = fileSort.getSeqId();
          String sortName = fileSort.getSortName();
          int isHaveChild = fileSortLogic
          .isHaveChild(dbConn, fileSort.getSeqId());
          String extData = "";
          // String imgAddress = "/t9/core/styles/style1/img/dtree/node_dept.gif";
          sb.append("{");
          sb.append("nodeId:\"" + seqId + "\"");
          sb.append(",name:\"" + sortName + "\"");
          sb.append(",isHaveChild:" + isHaveChild + "");
          sb.append(",extData:\"" + extData + "\"");
          // sb.append(",imgAddress:\"" + imgAddress + "\"");
          sb.append("},");
          sb.deleteCharAt(sb.length() - 1);           
        }
        
      } else {
        map.put("SORT_PARENT", id);         
        List<T9Dimension> list = fileSortLogic.getFileSorts(dbConn, map);        
        if (list.size()>0) {
          for (T9Dimension fileSort : list) {            
            int seqId = fileSort.getSeqId();
            String sortName = fileSort.getSortName();
            int isHaveChild = fileSortLogic
            .isHaveChild(dbConn, fileSort.getSeqId());
            String extData = "";
            // String imgAddress = "/t9/core/styles/style1/img/dtree/node_dept.gif";
            sb.append("{");
            sb.append("nodeId:\"" + seqId + "\"");
            sb.append(",name:\"" + sortName + "\"");
            sb.append(",isHaveChild:" + isHaveChild + "");
            sb.append(",extData:\"" + extData + "\"");
            // sb.append(",imgAddress:\"" + imgAddress + "\"");
            sb.append("},");
            
          } 
          sb.deleteCharAt(sb.length() - 1);           
        }
      }      
           
      sb.append("]");
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功返回结果！");
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 获取文件夹树形结构信息，考虑是否有权限，有权限才能显示

   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getPrivTree(HttpServletRequest request, HttpServletResponse response)
      throws Exception {
    String idStr = request.getParameter("id");
    String sortIdStr = request.getParameter("seqId");
    int id = 0;
    if (idStr != null && !"".equals(idStr)) {
      id = Integer.parseInt(idStr);
    }
    //获取登录用户信息
    T9Person loginUser = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
    int loginUserSeqId=loginUser.getSeqId();
    int loginUserDeptId=loginUser.getDeptId();
    String loginUserRoleId=loginUser.getUserPriv();

    Connection dbConn = null;
    T9DimensionLogic fileSortLogic = new T9DimensionLogic();
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      Map map = new HashMap();
      StringBuffer sb = new StringBuffer();
      
      if (sortIdStr != null && !"".equals(sortIdStr) && id == 0) {
        T9Dimension fileSort = fileSortLogic.getFileSortInfoById(dbConn, sortIdStr);
        if (fileSort!=null) {
          sb.append("[");
          int seqId = fileSort.getSeqId();
          String sortName = fileSort.getSortName();
          int isHaveChild = fileSortLogic
          .isHaveChild(dbConn, fileSort.getSeqId());
          String extData = "";
          sb.append("{");
          sb.append("nodeId:\"" + seqId + "\"");
          sb.append(",name:\"" + sortName + "\"");
          sb.append(",isHaveChild:" + isHaveChild + "");
          sb.append(",extData:\"" + extData + "\"");
          sb.append("},");
          sb.deleteCharAt(sb.length() - 1); 
          sb.append("]");
        }else if (fileSort==null) {
          sb.append("[]");
        }        
        
      } else {
        map.put("SORT_PARENT", id);         
        List<T9Dimension> list = fileSortLogic.getFileSorts(dbConn, map);
        boolean userFlag=false;
        boolean roleFlag=false;
        boolean deptFlag=false;
        
        boolean ownerUserFlag=false;
        boolean ownerRoleFlag=false;
        boolean ownerDeptFlag=false;
        
        
        if (list.size()>0) {
          sb.append("[");
          Map map2=new HashMap();
          boolean isHave = false;
          for (T9Dimension fileSort : list) {
            int seqId = fileSort.getSeqId();
            map2.put("SEQ_ID", seqId);
            String userPrivs=fileSortLogic.selectManagerIds(dbConn, map2, "USER_ID");
            String rolePrivs=fileSortLogic.getRoleIds(dbConn, map2, "USER_ID");
            String deptPrivs=fileSortLogic.getDeptIds(dbConn, map2, "USER_ID");
            
            String ownerUserPrivs=fileSortLogic.selectManagerIds(dbConn, map2, "OWNER");
            String ownerRolePrivs=fileSortLogic.getRoleIds(dbConn, map2, "OWNER");
            String ownerDeptPrivs=fileSortLogic.getDeptIds(dbConn, map2, "OWNER");
            
            
            userFlag=fileSortLogic.getUserIdStr(loginUserSeqId, userPrivs, dbConn);
            roleFlag=fileSortLogic.getRoleIdStr(loginUserRoleId,rolePrivs,dbConn);
            deptFlag=fileSortLogic.getDeptIdStr(loginUserDeptId, deptPrivs, dbConn);
            
            ownerUserFlag=fileSortLogic.getUserIdStr(loginUserSeqId, ownerUserPrivs, dbConn);
            ownerRoleFlag=fileSortLogic.getRoleIdStr(loginUserRoleId,ownerRolePrivs,dbConn);
            ownerDeptFlag=fileSortLogic.getDeptIdStr(loginUserDeptId, ownerDeptPrivs, dbConn);
            
           if(ownerUserFlag==true||ownerRoleFlag==true||ownerDeptFlag==true){
             String sortName = fileSort.getSortName();
             int isHaveChild = fileSortLogic.isHaveChild(dbConn, fileSort.getSeqId(),loginUserSeqId,loginUserRoleId,loginUserDeptId);
             String extData = "";
             sb.append("{");
             sb.append("nodeId:\"" + seqId + "\"");
             sb.append(",name:\"" + sortName + "\"");            
             sb.append(",isHaveChild:" + isHaveChild + "");                 
             sb.append(",extData:\"" + extData + "\"");
             sb.append("},");              
             isHave = true;
           }else if (userFlag==true||roleFlag==true||deptFlag==true) {
             String sortName = fileSort.getSortName();
             int isHaveChild = fileSortLogic.isHaveChild(dbConn, fileSort.getSeqId(),loginUserSeqId,loginUserRoleId,loginUserDeptId);
             String extData = "";
             sb.append("{");
             sb.append("nodeId:\"" + seqId + "\"");
             sb.append(",name:\"" + sortName + "\"");            
             sb.append(",isHaveChild:" + isHaveChild + "");                 
             sb.append(",extData:\"" + extData + "\"");
             sb.append("},");              
             isHave = true;
          }
                     
          }
          if (isHave) {
            sb.deleteCharAt(sb.length() - 1);
          }
          sb.append("]"); 
        }else {
          sb.append("[]");
        }
      }
      
           
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功返回结果！");
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  

  /**
   * 添加子文件夹
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String addSubFolderInfo(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    T9DimensionLogic fileSortLogic = null;
    String seqId = request.getParameter("seqId");
    //System.out.println("seqId>>>>>>>>>" + seqId);
    int sortParent = 0;
    if (seqId != null) {
      sortParent = Integer.parseInt(seqId);
    }
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      fileSortLogic = new T9DimensionLogic();

      T9Dimension fileSort = (T9Dimension) T9FOM.build(request.getParameterMap());
      T9Dimension sort=fileSortLogic.getFileSortInfoById(dbConn, seqId);
      fileSort.setSortParent(sortParent);
      fileSort.setNewUser(sort.getNewUser());
      fileSort.setUserId(sort.getUserId());
      fileSort.setManageUser(sort.getManageUser());
      fileSort.setDownUser(sort.getDownUser());
      fileSort.setShareUser(sort.getShareUser());
      fileSort.setOwner(sort.getOwner());
      
      
      
      fileSortLogic.saveFileSortInfo(dbConn, fileSort);
      T9Dimension fileSort2 = new T9Dimension();
      fileSort2 = fileSortLogic.getMaxSeqId(dbConn);
      int nodeId = fileSort2.getSeqId();
      String sortName = fileSort2.getSortName();
      String date = "[{nodeId:\"" + nodeId + "\",sortName:\"" + sortName
          + "\" }]";
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功添加数据");
      request.setAttribute(T9ActionKeys.RET_DATA, date);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

  /**
   * 根据id设置访问权限
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String setVisitById(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    T9DimensionLogic fileSortLogicc = new T9DimensionLogic();
    String sortId = request.getParameter("seqId");
    String override=request.getParameter("override");
    String action="USER_ID";
    int seqId = 0;
    if (sortId != null) {
      seqId = Integer.parseInt(sortId);
    }
    //System.out.println("FileSort__seqId>>>>>>>>>>>>>" + seqId);
    String userId = request.getParameter("userId");
    if (userId.replaceAll("|", "").length() == 0) {
      userId = "";
    }
    //System.out.println("userId>>>>>" + userId);

    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      Map map = new HashMap();
      map.put("userId", userId);
      map.put("seqId", seqId);

      if ("override".equals(override)) {
        fileSortLogicc.updateVisitOverride(dbConn,seqId,userId,action);        
      }else {
        fileSortLogicc.updateVisitById(dbConn, map);        
      }

      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "更新成功！");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

  /**
   * 根据id设置管理权限
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String setManageUserById(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    T9DimensionLogic fileSortLogicc = new T9DimensionLogic();
    String sortId = request.getParameter("seqId");
    String override=request.getParameter("override");
    String action="MANAGE_USER";
    int seqId = 0;
    if (sortId != null) {
      seqId = Integer.parseInt(sortId);
    }
    String manageUser = request.getParameter("manageUser");
    if (manageUser.replaceAll("|", "").length() == 0) {
      manageUser = "";
    }
    //System.out.println("manageUser>>>>>" + manageUser);

    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      Map map = new HashMap();
      map.put("seqId", seqId);
      map.put("manageUser", manageUser);

      if ("override".equals(override)) {
        fileSortLogicc.updateVisitOverride(dbConn,seqId,manageUser,action);     
      }else {
        fileSortLogicc.updateManageUserById(dbConn, map);        
      }

      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "更新成功！");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

  /**
   * 根据id设置新建权限
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String setNewUserById(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    T9DimensionLogic fileSortLogicc = new T9DimensionLogic();
    String sortId = request.getParameter("seqId");
    String override=request.getParameter("override");
    String action="NEW_USER";
    int seqId = 0;
    if (sortId != null) {
      seqId = Integer.parseInt(sortId);
    }
    String newUser = request.getParameter("createId");
    if (newUser.replaceAll("|", "").length() == 0) {
      newUser = "";
    }
    //System.out.println("newUser>>>>>" + newUser);

    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();

      Map map = new HashMap();
      map.put("seqId", seqId);
      map.put("newUser", newUser);     
      
      if ("override".equals(override)) {
        fileSortLogicc.updateVisitOverride(dbConn,seqId,newUser,action);     
      }else {
        fileSortLogicc.updateNewUserById(dbConn, map);    
      }

      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "更新成功！");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

  /**
   * 根据id设置下载权限
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String setDownLoadById(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    T9DimensionLogic fileSortLogicc = new T9DimensionLogic();
    String sortId = request.getParameter("seqId");
    String override=request.getParameter("override");
    String action="DOWN_USER";
    int seqId = 0;
    if (sortId != null) {
      seqId = Integer.parseInt(sortId);
    }
    String downUser = request.getParameter("downLoadId");
    if (downUser.replaceAll("|", "").length() == 0) {
      downUser = "";
    }
    //System.out.println("downUser>>>>>" + downUser);

    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();

      Map map = new HashMap();
      map.put("seqId", seqId);
      map.put("downUser", downUser);           
      if ("override".equals(override)) {
        fileSortLogicc.updateVisitOverride(dbConn,seqId,downUser,action);     
      }else {
        fileSortLogicc.updateDownLoadById(dbConn, map); 
      }

      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "更新成功！");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

  /**
   * 根据id设置所有者权限

   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String setOwnerById(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    T9DimensionLogic fileSortLogicc = new T9DimensionLogic();
    String sortId = request.getParameter("seqId");
    String override=request.getParameter("override");
    String action="OWNER";
    int seqId = 0;
    if (sortId != null) {
      seqId = Integer.parseInt(sortId);
    }
    String owner = request.getParameter("ownerId");
    if (owner.replaceAll("|", "").length() == 0) {
      owner = "";
    }
    //System.out.println("downUser>>>>>" + owner);

    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();

      Map map = new HashMap();
      map.put("seqId", seqId);
      map.put("owner", owner);
      
      if ("override".equals(override)) {
        fileSortLogicc.updateVisitOverride(dbConn,seqId,owner,action);     
      }else {
        fileSortLogicc.updateOwnerById(dbConn, map); 
      }

      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "更新成功！");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

  /**
   * 获取人员id名字串

   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getPersonIdStr(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn;
    String seqIdStr = request.getParameter("seqId");
    String action = request.getParameter("action");
    int seqId = 0;
    if (seqIdStr != null && !"".equals(seqIdStr)) {
      seqId = Integer.parseInt(seqIdStr);
    }
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9DimensionLogic sortLogic = new T9DimensionLogic();
      Map map = new HashMap();
      map.put("SEQ_ID", seqId);
      String ids = sortLogic.selectManagerIds(dbConn, map, action);
      String names = "";
      //System.out.println(ids);
      if (!ids.equals("")) {
        names = sortLogic.getNamesByIds(dbConn, map, action);
      }
      String data = "{user:\"" + ids + "\",userDesc:\"" + names + "\"}";
      //System.out.println(data);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "更新成功！");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

  /**
   * 获取所有人员的id名字串

   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getAllPersonIdStr(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn;
    String seqIdStr = request.getParameter("seqId");
    // String action = request.getParameter("action");
    int seqId = 0;
    if (seqIdStr != null && !"".equals(seqIdStr)) {
      seqId = Integer.parseInt(seqIdStr);
    }
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9DimensionLogic sortLogic = new T9DimensionLogic();
      String data=sortLogic.getSortName(dbConn, seqId);
      
      //System.out.println(data);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "更新成功！");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

  /**
   * 获取角色人员id名字串

   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getRoleIdStr(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn;
    String seqIdStr = request.getParameter("seqId");
    String action = request.getParameter("action");
    int seqId = 0;
    if (seqIdStr != null && !"".equals(seqIdStr)) {
      seqId = Integer.parseInt(seqIdStr);
    }
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9DimensionLogic sortLogic = new T9DimensionLogic();
      Map map = new HashMap();
      map.put("SEQ_ID", seqId);
      String ids = sortLogic.getRoleIds(dbConn, map, action);
      String names = "";
      //System.out.println(ids);
      if (!ids.equals("")) {
        names = sortLogic.getRoleNamesByIds(dbConn, map, action);
      }
      String data = "{role:\"" + ids + "\",roleDesc:\"" + names + "\"}";
      //System.out.println(data);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "更新成功！");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

  /**
   * 获取部门人员id名字串

   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getDeptIdStr(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn;
    String seqIdStr = request.getParameter("seqId");
    String action = request.getParameter("action");
    int seqId = 0;
    if (seqIdStr != null && !"".equals(seqIdStr)) {
      seqId = Integer.parseInt(seqIdStr);
    }
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9DimensionLogic sortLogic = new T9DimensionLogic();
      Map map = new HashMap();
      map.put("SEQ_ID", seqId);
      String ids = sortLogic.getDeptIds(dbConn, map, action);
      String names = "";
      //System.out.println(ids);
      if (!ids.equals("")) {
        names = sortLogic.getDeptByIds(dbConn, map, action);
      }
      String data = "{dept:\"" + ids + "\",deptDesc:\"" + names + "\"}";
      //System.out.println(data);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "更新成功！");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

  /**
   * 复制文件夹

   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String copyFolderById(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    String seqId = request.getParameter("folderId");
    String action = request.getParameter("action");
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      //System.out.println("dbConn>>>>>>>>>>>>>>..." + dbConn + "    seqId>>>>"
//          + seqId);
      
      request.getSession().setAttribute("action", action);
      request.getSession().setAttribute("seqId", seqId);      

      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功取出数据");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

  /**
   * 粘贴文件夹

   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String pasteFolder(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    T9DimensionLogic fileSortLogic = new T9DimensionLogic();
    String action = (String) request.getSession().getAttribute("action");
    String seqId = (String) request.getSession().getAttribute("seqId"); 
    String pasteSeqId = request.getParameter("pasteSeqId");
    //System.out.println(pasteSeqId);
    //System.out.println(seqId);
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Dimension fileSort1 = fileSortLogic.getFileSortInfoById(dbConn, pasteSeqId);          
      //fileSortLogic.saveFileSortInfo(dbConn, fileSort1);      
      T9Dimension fileSort3 = fileSortLogic.getFileSortInfoById(dbConn, seqId);   
      //fileSort3 = fileSortLogic.getMaxSeqId(dbConn);
      String data = "";
      if ("cut".equals(action)) {
        T9Dimension fileSort2 = new T9Dimension();
//        fileSort2.setSeqId(seqId);
        fileSort2.setSeqId(Integer.parseInt(seqId));
        //fileSortLogic.delDimensionInfoById(dbConn, fileSort2);
        fileSortLogic.updateVisitById(dbConn, pasteSeqId, Integer.parseInt(seqId));
        int isHaveChild = fileSortLogic.isHaveChild(dbConn, Integer.parseInt(seqId));
        
        data = "[{nodeId:\"" + fileSort3.getSeqId() + "\",isHaveChild:\""+isHaveChild+"\",sortName:\"" + fileSort3.getSortName()
            + "\",seqId:\"" + seqId + "\",action:\"" + action + "\" }]";

      } else if ("copy".equals(action)) {
        //级联查询本维度和所有的子维度的信息
        List listTemp = new ArrayList();
        T9Dimension maxDimension = fileSortLogic.getMaxSeqId(dbConn);
        int maxSeqId = maxDimension.getSeqId();
        List dimensionList = fileSortLogic.selectDimension(dbConn, Integer.parseInt(seqId),Integer.parseInt(pasteSeqId),listTemp,maxSeqId);
        //进行级联添加维度
        int isHaveChild = fileSortLogic.isHaveChild(dbConn, Integer.parseInt(seqId));
        data = "[{nodeId:\"" + dimensionList.get(0)+  "\",isHaveChild:\""+isHaveChild+"\",sortName:\"" + fileSort3.getSortName()
            + "\",action:\"" + action + "\" }]";

      }
      request.getSession().setAttribute("action", "");
      request.getSession().setAttribute("seqId",""); 

      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功粘贴数据");
      request.setAttribute(T9ActionKeys.RET_DATA, data);

    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

  /**
   * 批量设置权限
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String setBatchPriv(HttpServletRequest request,HttpServletResponse response)throws Exception{
    Connection dbConn=null;
    String seqString=request.getParameter("seqId");
    String setIdStr=request.getParameter("idStr");
    String check=request.getParameter("check");
    String opt=request.getParameter("opt");
    
    String checks=check.substring(0,check.length()-1);
    int seqId=0;
    if (seqString!="") {
      seqId=Integer.parseInt(seqString);
    }
    
    try {
      T9RequestDbConn requestDbConn=(T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn=requestDbConn.getSysDbConn();
      T9DimensionLogic logic=new T9DimensionLogic();
      if ("addPriv".equals(opt)) {
        if (checks!="") {
          String[] checkStrs=checks.split(",");
          for(int i=0;i<checkStrs.length;i++){
            //System.out.println(checkStrs[i]);            
            if ("USER_ID".equals(checkStrs[i])) {
              logic.updateVisitOverride(dbConn,seqId,setIdStr,"USER_ID");               
            }
            if ("MANAGE_USER".equals(checkStrs[i])) {
              logic.updateVisitOverride(dbConn,seqId,setIdStr,"MANAGE_USER");               
            }
            if ("NEW_USER".equals(checkStrs[i])) {
              logic.updateVisitOverride(dbConn,seqId,setIdStr,"NEW_USER");               
            }
            if ("DOWN_USER".equals(checkStrs[i])) {
              logic.updateVisitOverride(dbConn,seqId,setIdStr,"DOWN_USER");               
            }
            if ("OWNER".equals(checkStrs[i])) {
              logic.updateVisitOverride(dbConn,seqId,setIdStr,"OWNER");               
            }            
          }          
        }      
        
      }else if("delPriv".equals(opt)){
        if (checks!="") {
          String[] checkStrs=checks.split(",");
          for(int i=0;i<checkStrs.length;i++){
            //System.out.println(checkStrs[i]);            
            if ("USER_ID".equals(checkStrs[i])) {
              logic.updateVisitOverride(dbConn,seqId,"","USER_ID");               
            }
            if ("MANAGE_USER".equals(checkStrs[i])) {
              logic.updateVisitOverride(dbConn,seqId,"","MANAGE_USER");               
            }
            if ("NEW_USER".equals(checkStrs[i])) {
              logic.updateVisitOverride(dbConn,seqId,"","NEW_USER");               
            }
            if ("DOWN_USER".equals(checkStrs[i])) {
              logic.updateVisitOverride(dbConn,seqId,"","DOWN_USER");               
            }
            if ("OWNER".equals(checkStrs[i])) {
              logic.updateVisitOverride(dbConn,seqId,"","OWNER");               
            }            
          }          
        } 
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "批量设置权限成功");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }    
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 获取转存到公共文件柜的根目录信息
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getFolderInfo(HttpServletRequest request,HttpServletResponse response) throws Exception{
    String seqIdStr=request.getParameter("seqId");
    String parentIdStr=request.getParameter("parentId");
    String attachId=request.getParameter("attachId");
    String attachName=request.getParameter("attachName");
    String backFlag=request.getParameter("backFlag");
    
    
    int parentId=0;
    int seqId=0;
    if (seqIdStr!=null) {
      seqId=Integer.parseInt(seqIdStr);
    }
    if (parentIdStr!=null) {
      parentId=Integer.parseInt(parentIdStr);
    }
    if (backFlag==null) {
      backFlag="";
    }
        
    // 获取登录用户信息
    T9Person loginUser = (T9Person) request.getSession().getAttribute(
        T9Const.LOGIN_USER);
    int loginUserSeqId = loginUser.getSeqId();
    int loginUserDeptId = loginUser.getDeptId();
    String loginUserRoleId = loginUser.getUserPriv();
    
    boolean userFlag = false;
    boolean roleFlag = false;
    boolean deptFlag = false;
    
    boolean newUserFlag = false;
    boolean newRoleFlag = false;
    boolean newDeptFlag = false;
    
    List<Map<String, String>> returnList =new ArrayList<Map<String,String>>();
    Map map=new HashMap();
    T9DimensionLogic fileSortLogic=new T9DimensionLogic();
    T9Dimension fileSort=new T9Dimension();
    List<T9Dimension> list=new ArrayList<T9Dimension>();
   
    int inIt=0;
    Connection dbConn=null;
    try {      
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
      int sortPar=0;
      
      if("back".equals(backFlag)){ 
        if (parentId==0&&seqId==0) {
          parentId=0;
          seqId=0;
        }else {
          map.put("SORT_PARENT", parentId);
          list = fileSortLogic.getFileSorts(dbConn, map);
          fileSort=fileSortLogic.getFileSortInfoById(dbConn, parentIdStr);
          if (fileSort!=null) {
            inIt=1;
            sortPar = fileSort.getSeqId();
            if (fileSort.getSortParent()==0) {
              parentId=0;
              seqId=0;
            }else {
              parentId=fileSort.getSortParent();
              seqId=fileSort.getSeqId();              
            }            
          }else {
            seqId=0;
            inIt=1;
          }         
        }       
      }
      //System.out.println(seqId==0&&sortPar==0);
      
      if (seqId==0&&sortPar==0) {
        map.put("SORT_PARENT", parentId);   
        list = fileSortLogic.getFileSorts(dbConn, map);        
        
      }else if(seqId!=0) {        
        map.put("SORT_PARENT", seqId);   
        list = fileSortLogic.getFileSorts(dbConn, map); 
        fileSort=fileSortLogic.getFileSortInfoById(dbConn, String.valueOf(seqId));
        parentId=fileSort.getSortParent();
        seqId=fileSort.getSeqId();        
      } 
      
      if (list.size()!=0) {
        for(T9Dimension sort:list){
          map.put("SEQ_ID", sort.getSeqId());
          String userPrivs=fileSortLogic.selectManagerIds(dbConn, map, "USER_ID");
          String rolePrivs=fileSortLogic.getRoleIds(dbConn, map, "USER_ID");
          String deptPrivs=fileSortLogic.getDeptIds(dbConn, map, "USER_ID");
          
          String newUserPrivs=fileSortLogic.selectManagerIds(dbConn, map, "NEW_USER");
          String newRolePrivs=fileSortLogic.getRoleIds(dbConn, map, "NEW_USER");
          String newDeptPrivs=fileSortLogic.getDeptIds(dbConn, map, "NEW_USER");
          
          userFlag=fileSortLogic.getUserIdStr(loginUserSeqId, userPrivs, dbConn);
          roleFlag=fileSortLogic.getRoleIdStr(loginUserRoleId,rolePrivs,dbConn);
          deptFlag=fileSortLogic.getDeptIdStr(loginUserDeptId, deptPrivs, dbConn);
          
          newUserFlag=fileSortLogic.getUserIdStr(loginUserSeqId, newUserPrivs, dbConn);
          newRoleFlag=fileSortLogic.getRoleIdStr(loginUserRoleId,newRolePrivs,dbConn);
          newDeptFlag=fileSortLogic.getDeptIdStr(loginUserDeptId, newDeptPrivs, dbConn);
          
          int visitFlag = 0;
          int newFlag = 0;
          if (userFlag || roleFlag || deptFlag) {
            visitFlag=1;
          }
          if (newUserFlag || newRoleFlag || newDeptFlag) {
            newFlag=1;
          }
          
          
          
          
          Map<String, String> sortMap=new HashMap<String, String>();
          sortMap.put("seqId", String.valueOf(sort.getSeqId()));
          sortMap.put("sortName", sort.getSortName());
          sortMap.put("sortParent", String.valueOf(sort.getSortParent()));
          sortMap.put("visitFlag",String.valueOf(visitFlag));
          sortMap.put("newFlag",String.valueOf(newFlag));
          returnList.add(sortMap);
          
        }
        
      }
      
      
      request.setAttribute("fileSortList", returnList);      
      //seqId=sortPar;
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }    
    return "/core/funcs/savefile/folder1.jsp?seqId="+ seqId + "&attachId" + attachId + "&attachName="+attachName +"&parentId=" + parentId + "&inIt=" + inIt;
  }
  public String getSubDimensionInfo(HttpServletRequest request,HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn=(T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn=requestDbConn.getSysDbConn();
      String seqId = request.getParameter("seqId");
      T9DimensionLogic logic=new T9DimensionLogic();
      String[] str = {"SORT_PARENT = " + seqId};
      List<T9Dimension> dimensionList = logic.getDimensionInfo(dbConn, str);
      String data = "[";
      for (int i = 0; i < dimensionList.size(); i++) {
        T9Dimension dimension = dimensionList.get(i);
        data = data + "{text:\"" + dimension.getSortName() + "\",leaf:true,checked: false,seqId:" + dimension.getSeqId() + "},"; 
      }
      if(dimensionList.size()>0){
        data = data.substring(0, data.length()-1);
      }
      data = data + "]";
      PrintWriter pw = response.getWriter();
      pw.println(data);
      pw.flush();
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "批量设置权限成功");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }    
    return "";
  }
  

  public static void main(String[] args) {
    // String string = "aaa,|&!}sdfs";
    // String[] a = string.split("\\|");
    // System.out.println(a.length);
    String actions[] = new String[] { "USER_ID", "MANAGE_USER", "NEW_USER",
        "DOWN_USER" };
    for (int i = 0; i < actions.length; i++) {
      // System.out.println("actions["+i+"]"+actions[i]);
      if ("USER_ID".equals(actions[i])) {
        //System.out.println(actions[i]);
      }

    }

  }
}
