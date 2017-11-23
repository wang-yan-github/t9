package t9.subsys.inforesouce.act;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.office.ntko.logic.T9NtkoLogic;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.personfolder.data.T9FileSort;
import t9.core.funcs.personfolder.logic.T9PersonFolderLogic;
import t9.core.funcs.system.filefolder.logic.T9FileSortLogic;
import t9.core.funcs.system.netdisk.data.T9Netdisk;
import t9.core.funcs.system.netdisk.logic.T9NetdiskLogic;
import t9.core.funcs.workflow.util.T9WorkFlowUtility;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.global.T9SysProps;
import t9.core.util.T9Utility;
import t9.subsys.inforesouce.logic.T9BeachDownLoadFileLogic;


/**
 * 批量下载，批量转存
 * 调用的类有：T9BeachDownLoadFileLogic,T9NtkoLogic
 * @see t9.subsys.inforesouce.logic.T9BeachDownLoadFileLogic
 * @see t9.core.funcs.office.ntko.logic.T9NtkoLogic
 * @author Administrator
 *
 */
public class T9BeachDownLoadFileAct{
  /**
   * 信息资源管理中批量下载
   * 调用T9BeachDownLoadFileLogic的toZipInfoMapFile方法生成文件的map
   * 调用T9NtkoLogic的zip进行打包成zip格式
   * @see t9.subsys.inforesouce.logic.T9BeachDownLoadFileLogic#toZipInfoMapFile(Connection, T9Person)
   * @see t9.core.funcs.office.ntko.logic.T9NtkoLogic#zip(Map, OutputStream)
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String beanchDownload(HttpServletRequest request, HttpServletResponse response)throws Exception{
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR); 
    Connection dbConn = null;
    try{
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      String downName = "文件管理中心.zip";
      String fileName = URLEncoder.encode(downName, "UTF-8");
      OutputStream ops = null;
      fileName = fileName.replaceAll("\\+", "%20");
      response.setHeader("Cache-control", "private");
      response.setContentType("application/vnd.ms-excel");
      response.setHeader("Accept-Ranges", "bytes");
      response.setHeader("Content-disposition", "attachment; filename=\"" + fileName + "\"");
      ops = response.getOutputStream();
      T9BeachDownLoadFileLogic downLogic = new T9BeachDownLoadFileLogic();
      T9NtkoLogic nl = new T9NtkoLogic();
      Map<String, InputStream> map = downLogic.toZipInfoMapFile(dbConn,  user);
      nl.zip(map, ops);
      ops.flush();
    } catch (Exception ex){
      String message = T9WorkFlowUtility.Message(ex.getMessage(),1);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, message);
      throw ex;  
    } 
    return null;
  }
  
  
  
  /**
   * 获取转存到个人文件柜的根目录树信息(copy自个人文件柜转存)
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getPersonFolderInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
    String seqIdStr = request.getParameter("seqId");
    int seqId = 0;
    if (seqIdStr != null) {
      seqId = Integer.parseInt(seqIdStr);
    }
    // 获取登录用户信息
    T9Person loginUser = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
    int loginUserSeqId = loginUser.getSeqId();

    List<Map<String, String>> returnList = new ArrayList<Map<String, String>>();
    T9PersonFolderLogic logic = new T9PersonFolderLogic();
    String sortType = "4";

    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();

      int inIt = 0;
      if (seqId != 0) {
        inIt = 1;
      }

      T9FileSort fileSort = logic.getFileSortInfoById(dbConn, String.valueOf(seqId));
      int parentIdStr = 0;
      if (fileSort != null) {
        parentIdStr = fileSort.getSortParent();
      }

      String[] filters = { "SORT_PARENT=" + seqId + " and SORT_TYPE='" + sortType + "' order by SORT_NO,SORT_NAME" };
      List<T9FileSort> parentList = logic.getFileSorts(dbConn, filters);
      if (seqId == 0) {
        Map<String, String> defaulMap = new HashMap<String, String>();
        defaulMap.put("seqId", String.valueOf(0));
        defaulMap.put("sortName", "根目录");
        returnList.add(defaulMap);

      }

      if (parentList != null && parentList.size() > 0) {
        boolean userFlag = false;
        for (T9FileSort fileFolder : parentList) {
          String userPrivs = logic.getUserId(dbConn, fileFolder.getSeqId(), "USER_ID");
          userFlag = logic.getUserIdStr(loginUserSeqId, userPrivs, dbConn);

          if (userFlag) {
            Map<String, String> sortMap = new HashMap<String, String>();

            sortMap.put("seqId", String.valueOf(fileFolder.getSeqId()));
            sortMap.put("sortName", fileFolder.getSortName());
            sortMap.put("sortParent", String.valueOf(fileFolder.getSortParent()));
            returnList.add(sortMap);

          }
        }
      }
      request.setAttribute("inIt", inIt);
      request.setAttribute("seqId", seqId);
      request.setAttribute("parentId", parentIdStr);

      request.setAttribute("fileSortList", returnList);

    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
    }

    return "/subsys/inforesource/savefile/personFolderList.jsp";
  }
  

  /**
   * 个人文件柜转存
   * <br>调用T9BeachDownLoadFileLogic的transferFolder进行转存
   * <br>
   * @param request
   * @param response
   * @see t9.subsys.inforesouce.logic.T9BeachDownLoadFileLogic#transferFolder(Connection, T9Person, int, int, String)
   * @return "/core/inc/rtjson.jsp"
   * @throws Exception
   */
  public String transferFolder(HttpServletRequest request, HttpServletResponse response) throws Exception {
    String seqIdStr = request.getParameter("checkId");
    int seqId = 0;
    if (seqIdStr != null) {
      seqId = Integer.parseInt(seqIdStr);
    }
    T9BeachDownLoadFileLogic logic = new T9BeachDownLoadFileLogic();
    String separator = File.separator;   
    String folderPath = T9SysProps.getAttachPath() + separator + "file_folder";

    // 获取登录用户信息
    T9Person loginUser = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
    Connection dbConn = null;
    try {
      T9RequestDbConn requesttDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requesttDbConn.getSysDbConn();
      boolean flag = logic.transferFolder(dbConn, loginUser, seqId, 1, folderPath);
      if (flag) {
        request.setAttribute(T9ActionKeys.RET_MSRG, "文件转存成功！");
      } else {
        request.setAttribute(T9ActionKeys.RET_MSRG, "文件转存失败！");
      }
      String data = "{flag:\"" + flag + "\"}";
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, data);

    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 获取转存到公共文件柜的根目录信息（copy自公共文件柜转存）
   * 
   * @param request
   * @param response
   * @return "/subsys/inforesource/savefile/folder1.jsp"
   * @throws Exception
   */
  public String getFolderInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
    String seqIdStr = request.getParameter("seqId");
    String parentIdStr = request.getParameter("parentId");
    String attachId = request.getParameter("attachId");
    String attachName = request.getParameter("attachName");
    String backFlag = request.getParameter("backFlag");
    String module = request.getParameter("module");

    int parentId = 0;
    int seqId = 0;
    if (seqIdStr != null) {
      seqId = Integer.parseInt(seqIdStr);
    }
    if (parentIdStr != null) {
      parentId = Integer.parseInt(parentIdStr);
    }
    if (backFlag == null) {
      backFlag = "";
    }
    if (module == null) {
      module = "";
    }

    // 获取登录用户信息
    T9Person loginUser = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
    int loginUserSeqId = loginUser.getSeqId();
    int loginUserDeptId = loginUser.getDeptId();
    String loginUserRoleId = loginUser.getUserPriv();

    boolean userFlag = false;
    boolean roleFlag = false;
    boolean deptFlag = false;

    boolean newUserFlag = false;
    boolean newRoleFlag = false;
    boolean newDeptFlag = false;

    List<Map<String, String>> returnList = new ArrayList<Map<String, String>>();
    Map map = new HashMap();
    T9FileSortLogic fileSortLogic = new T9FileSortLogic();
    t9.core.funcs.system.filefolder.data.T9FileSort fileSort = new t9.core.funcs.system.filefolder.data.T9FileSort();
    List<t9.core.funcs.system.filefolder.data.T9FileSort> list = new ArrayList<t9.core.funcs.system.filefolder.data.T9FileSort>();

    int inIt = 0;
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();

      int sortPar = 0;

      if ("back".equals(backFlag)) {
        if (parentId == 0 && seqId == 0) {
          parentId = 0;
          seqId = 0;
        } else {
          map.put("SORT_PARENT", parentId);
          list = fileSortLogic.getFileSorts(dbConn, map);
          fileSort = fileSortLogic.getFileSortInfoById(dbConn, parentIdStr);
          if (fileSort != null) {
            inIt = 1;
            sortPar = fileSort.getSeqId();
            if (fileSort.getSortParent() == 0) {
              parentId = 0;
              seqId = 0;
            } else {
              parentId = fileSort.getSortParent();
              seqId = fileSort.getSeqId();
            }
          } else {
            seqId = 0;
            inIt = 1;
          }
        }
      }
      //System.out.println(seqId == 0 && sortPar == 0);

      if (seqId == 0 && sortPar == 0) {
        
//        map.put("SORT_PARENT", parentId);
//        list = fileSortLogic.getFileSorts(dbConn, map);
        
        String[] condition = { " SORT_PARENT=" + parentId + " AND (SORT_TYPE !='4' or SORT_TYPE is null)  order by SORT_NO,SORT_NAME" };
        list = fileSortLogic.getFileFilderInfo(dbConn, condition);
        
      } else if (seqId != 0) {
        map.put("SORT_PARENT", seqId);
        list = fileSortLogic.getFileSorts(dbConn, map);
        fileSort = fileSortLogic.getFileSortInfoById(dbConn, String.valueOf(seqId));
        parentId = fileSort.getSortParent();
        seqId = fileSort.getSeqId();
      }

      if (list.size() != 0) {
        for (t9.core.funcs.system.filefolder.data.T9FileSort sort : list) {
          map.put("SEQ_ID", sort.getSeqId());
          t9.core.funcs.system.filefolder.data.T9FileSort fileSort2 = fileSortLogic.getFileSortInfoById(dbConn, map);

          String userPrivs = fileSortLogic.selectManagerIds(dbConn, fileSort2, "USER_ID");
          String rolePrivs = fileSortLogic.getRoleIds(dbConn, fileSort2, "USER_ID");
          String deptPrivs = fileSortLogic.getDeptIds(dbConn, fileSort2, "USER_ID");

          String newUserPrivs = fileSortLogic.selectManagerIds(dbConn, fileSort2, "NEW_USER");
          String newRolePrivs = fileSortLogic.getRoleIds(dbConn, fileSort2, "NEW_USER");
          String newDeptPrivs = fileSortLogic.getDeptIds(dbConn, fileSort2, "NEW_USER");

          userFlag = fileSortLogic.getUserIdStr(loginUserSeqId, userPrivs, dbConn);
          roleFlag = fileSortLogic.getRoleIdStr(loginUserRoleId, rolePrivs, dbConn);
          deptFlag = fileSortLogic.getDeptIdStr(loginUserDeptId, deptPrivs, dbConn);

          newUserFlag = fileSortLogic.getUserIdStr(loginUserSeqId, newUserPrivs, dbConn);
          newRoleFlag = fileSortLogic.getRoleIdStr(loginUserRoleId, newRolePrivs, dbConn);
          newDeptFlag = fileSortLogic.getDeptIdStr(loginUserDeptId, newDeptPrivs, dbConn);

          int visitFlag = 0;
          int newFlag = 0;
          if (userFlag || roleFlag || deptFlag) {
            visitFlag = 1;
          }
          if (newUserFlag || newRoleFlag || newDeptFlag) {
            newFlag = 1;
          }
          if (visitFlag==1 ) {
            
            
            Map<String, String> sortMap = new HashMap<String, String>();
            sortMap.put("seqId", String.valueOf(sort.getSeqId()));
            sortMap.put("sortName", sort.getSortName());
            sortMap.put("sortParent", String.valueOf(sort.getSortParent()));
            sortMap.put("visitFlag", String.valueOf(visitFlag));
            sortMap.put("newFlag", String.valueOf(newFlag));
            returnList.add(sortMap);
          }

        }

      }
      
      
      request.setAttribute("attachId", attachId);
      request.setAttribute("attachName", attachName);
      request.setAttribute("module", module);
      
      request.setAttribute("seqId", seqId);
      request.setAttribute("parentId", parentId);
      request.setAttribute("inIt", inIt);

      request.setAttribute("fileSortList", returnList);
      
      
      // seqId=sortPar;
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/subsys/inforesource/savefile/folder1.jsp";
  }


  /**
   * 获取转存到网络硬盘的根目录信息(copy自t9.core.funcs.system.netdisk.act.T9NetdiskAct)<br>
   * @param request
   * @param response
   * @return "/subsys/inforesource/savefile/netdisk.jsp"
   * @throws Exception
   */
  public String getNetDiskList(HttpServletRequest request, HttpServletResponse response) throws Exception {

    String seqIdStr = request.getParameter("seqId");
    String diskPath = request.getParameter("diskPath");
    String parentPath = request.getParameter("parentPath");
    String attachId = request.getParameter("attachId");
    String attachName = request.getParameter("attachName");
    String returnFlag = request.getParameter("returnFlag");
    String module = request.getParameter("module");

    int seqId = 0;
    if (seqIdStr != null && !"".equals(seqIdStr)) {
      seqId = Integer.parseInt(seqIdStr);
    }

    if (diskPath == null) {
      diskPath = "";
    }
    if (parentPath == null) {
      parentPath = "";
    }
    if (returnFlag == null) {
      returnFlag = "";
    }
    if (module == null) {
      module = "";
    }

    // 获取登录用户信息
    T9Person loginUser = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
    int loginUserSeqId = loginUser.getSeqId();
    int loginUserDeptId = loginUser.getDeptId();
    String loginUserRoleId = loginUser.getUserPriv();

    boolean visitUserFlag = false;
    boolean visitRoleFlag = false;
    boolean visitDeptFlag = false;

    boolean newUserFlag = false;
    boolean newRoleFlag = false;
    boolean newDeptFlag = false;

    T9NetdiskLogic logic = new T9NetdiskLogic();
    List list = new ArrayList();
    List<Map<String, String>> returnList = new ArrayList<Map<String, String>>();

    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();

      if ("back".equals(returnFlag.trim())) {
        T9Netdisk netdisk = logic.getNetdiskInfoById(dbConn, seqId);
        String filePath = T9Utility.null2Empty(netdisk.getDiskPath());
        File file = new File(filePath);
        File file2 = new File(diskPath);
        if (file.exists() && file2.exists()) {
          if (file.getAbsolutePath().equals(file2.getAbsolutePath())) {
            diskPath = "";
          } else {
            String parentPathStr = diskPath.replace('\\', '/');
            int pathStr = parentPathStr.lastIndexOf('/') - 1;
            if (pathStr != -1) {
              diskPath = parentPathStr.substring(0, parentPathStr.lastIndexOf('/'));
              diskPath = diskPath + "/";
            }
          }
        }
      }

      if ("".equals(diskPath)) {
        list = logic.getNetdiskFolderInfo(dbConn);
        if (list != null && list.size() != 0) {
          for (int i = 0; i < list.size(); i++) {
            T9Netdisk netdisk = (T9Netdisk) list.get(i);
            Map diskMap = new HashMap();
            diskMap.put("SEQ_ID", netdisk.getSeqId());

            String visitUserStr = logic.selectManagerIds(dbConn, diskMap, "USER_ID");
            String visitRoleStr = logic.getRoleIds(dbConn, diskMap, "USER_ID");
            String visitDeptStr = logic.getDeptIds(dbConn, diskMap, "USER_ID");

            String newUserStr = logic.selectManagerIds(dbConn, diskMap, "NEW_USER");
            String newRoleStr = logic.getRoleIds(dbConn, diskMap, "NEW_USER");
            String newDeptStr = logic.getDeptIds(dbConn, diskMap, "NEW_USER");

            visitUserFlag = logic.getUserIdStr(loginUserSeqId, visitUserStr, dbConn);
            visitRoleFlag = logic.getRoleIdStr(loginUserRoleId, visitRoleStr, dbConn);
            visitDeptFlag = logic.getDeptIdStr(loginUserDeptId, visitDeptStr, dbConn);

            newUserFlag = logic.getUserIdStr(loginUserSeqId, newUserStr, dbConn);
            newRoleFlag = logic.getRoleIdStr(loginUserRoleId, newRoleStr, dbConn);
            newDeptFlag = logic.getDeptIdStr(loginUserDeptId, newDeptStr, dbConn);

            int visitFlag = 0;
            int newFlag = 0;
            if (visitUserFlag || visitRoleFlag || visitDeptFlag) {
              visitFlag = 1;
            }
            if (newUserFlag || newRoleFlag || newDeptFlag) {
              newFlag = 1;
            }

            if (visitFlag == 1 && newFlag == 1) {
              Map<String, String> map = new HashMap<String, String>();
              map.put("seqId", String.valueOf(netdisk.getSeqId()));
              map.put("diskName", netdisk.getDiskName());
              map.put("diskPath", T9Utility.encodeSpecial(netdisk.getDiskPath()));
              map.put("newUser", netdisk.getNewUser());
              map.put("managerUser", netdisk.getManageUser());
              map.put("userId", netdisk.getUserId());
              map.put("diskNo", String.valueOf(netdisk.getDiskNo()));
              map.put("spaceLimit", String.valueOf(netdisk.getSpaceLimit()));
              map.put("orderBy", netdisk.getOrderBy());
              map.put("ascDesc", netdisk.getAscDesc());
              map.put("downUser", netdisk.getDownUser());
              returnList.add(map);

            }

          }
        }
      } else {
        File file = new File(diskPath);
        if (file.exists()) {
          parentPath = file.getAbsolutePath().replace('\\', '/');
          File[] files = file.listFiles();
          for (int i = 0; i < files.length; i++) {
            File f = files[i];
            if (f.isDirectory()) {
              Map<String, String> map = new HashMap<String, String>();
              map.put("seqId", String.valueOf(seqId));
              map.put("diskName", f.getName());
              map.put("diskPath", f.getAbsolutePath().replace('\\', '/'));
              returnList.add(map);
            }

          }
        }
      }

      request.setAttribute("attachId", attachId);
      request.setAttribute("attachName", attachName);
      request.setAttribute("module", module);

      request.setAttribute("seqId", seqId);
      request.setAttribute("diskPath", diskPath);
      request.setAttribute("parentPath", parentPath);

      request.setAttribute("diskList", returnList);

    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }

    return "/subsys/inforesource/savefile/netdisk.jsp";
  }

  /**
   * 网络硬盘转存<br>
   * 调用T9BeachDownLoadFileLogic的transferNetdisk进行转存
   * @see t9.subsys.inforesouce.logic.T9BeachDownLoadFileLogic#transferNetdisk(Connection, String, T9Person, int)
   * @param request
   * @param response
   * @return "/core/inc/rtjson.jsp"
   * @throws Exception
   */
  public String transferNetdisk(HttpServletRequest request, HttpServletResponse response) throws Exception {
    String diskPath = request.getParameter("diskPath"); // d:/bjfaoitc/
  
    if (diskPath == null) {
      diskPath = "";
    }
  
    T9BeachDownLoadFileLogic logic = new T9BeachDownLoadFileLogic();
    Connection dbConn = null;
    try {
      T9RequestDbConn requesttDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requesttDbConn.getSysDbConn();
      T9Person loginUser = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      String data = logic.transferNetdisk(dbConn, diskPath, loginUser,1);
      request.setAttribute(T9ActionKeys.RET_MSRG, "文件转存完毕！");
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }

    return "/core/inc/rtjson.jsp";
  }

}
