package t9.core.funcs.netdisk.act;

import java.io.File;
import java.net.URLDecoder;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9MapComparator;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.email.logic.T9InnerEMailUtilLogic;
import t9.core.funcs.mobilesms.logic.T9MobileSms2Logic;
import t9.core.funcs.netdisk.data.T9Netdisk;
import t9.core.funcs.netdisk.data.T9Page;
import t9.core.funcs.netdisk.logic.T9NetDiskLogic;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.sms.data.T9SmsBack;
import t9.core.funcs.sms.logic.T9SmsUtil;
import t9.core.funcs.system.syslog.logic.T9SysLogLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.global.T9LogConst;
import t9.core.global.T9SysProps;
import t9.core.util.T9Utility;
import t9.core.util.file.T9FileUploadForm;
import t9.core.util.file.T9FileUtility;

public class T9NetDiskAct {
	private static Logger log = Logger.getLogger("t9.core.funcs.netdisk.act.T9NetDiskAct");

	/**
	 * 新建文件夹	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String addFileFolder(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// request.setCharacterEncoding("utf-8");
		String path = request.getParameter("DISK_ID"); // d:/bjfaoitc/tdoa_cache/ssss/ssss
		String name = request.getParameter("FILE_NAME"); // aaa
		String seqIdStr = request.getParameter("seqId"); // 5
		String folderNamePath = path + "/" + name;

		String data = "";
		boolean flag = false;
		int sucuss = 1;
		int isExist = 1;

		String foldName = "";
		String returnDiskId = "";

		T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
			if (path != null && path.trim().length() > 0) {
				File file = new File(folderNamePath);
				if (!file.exists()) {
					if (file.mkdir()) {

						foldName = name;
						returnDiskId = file.getPath().replace('\\', '/'); // d:/bjfaoitc/~!@#$%^/bb

						request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
						request.setAttribute(T9ActionKeys.RET_MSRG, "创建 x成功！");
						// sucuss = "创建成功";
						flag = true;
					} else {
						// System.out.println("创建不成功！");
						sucuss = 0;
					}
				} else {
					// System.out.println("文件夹已存在");
					isExist = 0;
				}
			}
			if (flag) {
	      // 写入系统日志
	        String remark = "新建子文件夹，名称为：" + foldName ;
	        T9SysLogLogic.addSysLog(dbConn, T9LogConst.NET_DISK, remark, person.getSeqId(), request.getRemoteAddr());
	      }

			data = "{sucuss:\"" + sucuss + "\",isExist:\"" + isExist + "\",flag:\"" + flag + "\",seqId:\"" + seqIdStr + "\",nodeName:\"" + T9Utility.encodeSpecial(foldName)
					+ "\",returnDiskId:\"" + T9Utility.encodeSpecial(returnDiskId) + "\"}";
			request.setAttribute(T9ActionKeys.RET_DATA, data);
		} catch (Exception ex) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
			throw ex;
		}

		return "/core/inc/rtjson.jsp";
	}

	/**
	 * 重命名文件夹
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String renameFileFolder(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// String path = URLDecoder.decode(request.getParameter("DISK_ID"),
		// "UTF-8");
		String path = request.getParameter("diskId"); // d:/bjfaoitc/tdoa_cache/ssss/ssss/aaa/kkkk
		String name = request.getParameter("FILE_NAME");
		String seqIdStr = request.getParameter("seqId");

		int seqId = 0;
		if (seqIdStr != null) {
			seqId = Integer.parseInt(seqIdStr);
		}

		if (path == null || "".equals(path)) {
			path = "D:\\tmp";
		}
		String newPath = path.replace('\\', '/').substring(0, path.lastIndexOf('/')); // d:/bjfaoitc/tdoa_cache/ssss/ssss/aaa
		path = path.replace('\\', '/');
		T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();

			String data = "";
			boolean flag = false;
			int sucuss = 1;
			int isExist = 1;

			String foldName = "";
			String returnDiskId = "";
			String nameStr = "";

			File file = new File(path);
			File newFile = new File(newPath + "/" + name);
			if (!newFile.exists()) {
				if (file.renameTo(newFile)) {
					// System.out.println("重命名成功！");
					foldName = name;
					nameStr = file.getName();
					returnDiskId = newFile.getPath().replace('\\', '/');
					flag = true;

				} else {
					// System.out.println("重命名失败！");
					sucuss = 0;
				}
			} else {
				// System.out.println("文件夹已存在！");
				isExist = 0;

			}
			if (flag) {
        // 写入系统日志
          String remark = "重命名文件夹 " + nameStr + " 为：" + foldName ;
          T9SysLogLogic.addSysLog(dbConn, T9LogConst.NET_DISK, remark, person.getSeqId(), request.getRemoteAddr());
        }
			data = "{sucuss:\"" + sucuss + "\",isExist:\"" + isExist + "\",flag:\"" + flag + "\",seqId:\"" + seqId + "\",nodeName:\"" + T9Utility.encodeSpecial(foldName)
					+ "\",returnDiskId:\"" + T9Utility.encodeSpecial(returnDiskId) + "\"}";

			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "成功重命名文件夹");
			request.setAttribute(T9ActionKeys.RET_DATA, data);
		} catch (Exception ex) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
			throw ex;
		}
		// return "/core/funcs/netdisk/folder/result.jsp?seqId=" + seqId + "&idStr="
		// + path;
		return "/core/inc/rtjson.jsp";

	}

	/**
	 * 删除文件夹	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String delFileFolder(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String recycle = T9SysProps.getString("$MYOA_IS_RECYCLE");
		if (recycle == null) {
			recycle = "";
		}
		String recyclePath = T9SysProps.getAttachPath() + File.separator + "recycle" + File.separator + "netdisk"; // 文件回收站的路径

		String path = request.getParameter("DISK_ID");
		String seqId = request.getParameter("seqId");
		T9NetDiskLogic logic = new T9NetDiskLogic();

		if (path == null) {
			path = "";
		}
		T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();

			File filePath = new File(path);
			if (filePath != null && filePath.exists()) {
				if ("1".equals(recycle.trim())) {
					logic.deleteAllToRecycle(filePath, recyclePath);
				} else {
					T9FileUtility.deleteAll(path);

				}
			// 写入系统日志
        String remark = "删除文件夹：" + filePath.getName() ;
        T9SysLogLogic.addSysLog(dbConn, T9LogConst.NET_DISK, remark, person.getSeqId(), request.getRemoteAddr());
			}
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "成功删除文件夹");
			request.setAttribute(T9ActionKeys.RET_DATA, "{seqId:" + seqId + ", diskId:\"" + T9Utility.encodeSpecial(path) + "\"}");
		} catch (Exception ex) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
			throw ex;
		}
		return "/core/inc/rtjson.jsp";
	}

	/**
	 * 文件操作(复制、剪贴)
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String doAction(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String path = request.getParameter("DISK_ID");
		String netdisk_sub_url = request.getParameter("netdisk_sub_url"); // 全文检索用到
		if (netdisk_sub_url == null) {
			netdisk_sub_url = "";
		}
		if (path == null) {
			path = "";
		}

		if (!"".equals(path.trim())) {
			netdisk_sub_url = "";
		} else if (!"".equals(netdisk_sub_url.trim())) {
			path = "";
		}

		if (path == null) {
			path = "";
		}
		path = path.replace('\\', '/');
		String fileList = request.getParameter("FILE_LIST");
		String action = request.getParameter("NETDISK_ACTION");
		request.getSession().setAttribute("NETDISK_PATH", path);
		request.getSession().setAttribute("NETDISK_FILENAME", fileList);
		request.getSession().setAttribute("netdisk_sub_url", netdisk_sub_url);

		request.getSession().setAttribute("NETDISK_ACTION", action);
		request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
		request.setAttribute(T9ActionKeys.RET_MSRG, "执行成功");
		return "/core/inc/rtjson.jsp";
	}

	/**
	 * 重命名文件
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String renameFile(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String path = request.getParameter("diskId");
		String newName = request.getParameter("FILE_NAME");
		String oldName = request.getParameter("oldFileName");

		String seqIdStr = request.getParameter("seqId");

		int seqId = 0;
		if (seqIdStr != null) {
			seqId = Integer.parseInt(seqIdStr);
		}

		if (path == null) {
			path = "";
		}
		path = path.replace('\\', '/');

		if (path.endsWith(("*"))) {
			path = path.substring(0, path.length() - 1);
		}

		try {
			File file = new File(path + "/" + oldName);
			File newFile = new File(path + "/" + newName);

			int flag = 0;
			int sucuss = 1;
			int isExist = 0;
			String data = "";

			if (!newFile.exists()) {
				if (file.renameTo(newFile)) {
					// System.out.println("成功重命名文件");
					flag = 1;

				} else {
					// System.out.println("重命名文件失败");
					sucuss = 0;
				}

			} else {
				// System.out.println("文件已存在");
				isExist = 1;
			}

			data = "{sucuss:\"" + sucuss + "\",isExist:\"" + isExist + "\",flag:\"" + flag + "\",seqId:\"" + seqId + "\"}";

			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "成功重命名文件");
			request.setAttribute(T9ActionKeys.RET_DATA, data);

		} catch (Exception ex) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
			throw ex;
		}
		// return "/core/funcs/netdisk/result.jsp";
		return "/core/inc/rtjson.jsp";
	}

	/**
	 * 删除文件
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String delFile(HttpServletRequest request, HttpServletResponse response) throws Exception {
		T9InnerEMailUtilLogic emut = new T9InnerEMailUtilLogic();
		String recycle = T9SysProps.getString("$MYOA_IS_RECYCLE");
		if (recycle == null) {
			recycle = "";
		}

		String path = request.getParameter("DISK_ID");
		String fileList = request.getParameter("FILE_LIST");
		String netdisk_sub_url = request.getParameter("netdisk_sub_url");

		if (path == null) {
			path = "";
		}
		if (fileList == null) {
			fileList = "";
		}
		if (netdisk_sub_url == null) {
			netdisk_sub_url = "";
		}

		String recyclePath = T9SysProps.getAttachPath() + File.separator + "recycle" + File.separator + "netdisk"; // 文件回收站的路径
		T9Person loginUser = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
    int loginUserSeqId = loginUser.getSeqId();
		Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      boolean delFlag = false;
      String addLogInfo = "";
			String[] names = null;
			if (fileList != null) {
				names = fileList.split("\\*");
			}

			if (!"".equals(netdisk_sub_url.trim())) {
				String[] diskPatthArry = null;
				if (netdisk_sub_url != null) {
					diskPatthArry = netdisk_sub_url.split("\\*");
				}

				if (names.length != 0) {
					for (int i = 0; i < names.length; i++) {
						String filePath = diskPatthArry[i];
						if ("1".equals(recycle.trim())) {
							String rand = emut.getRandom();
							String newFileName = rand + "_" + names[i];
							T9FileUtility.xcopyFile(filePath + File.separator + names[i], recyclePath + File.separator + newFileName);
							addLogInfo = (filePath + File.separator + names[i]).replace("\\", "/");
              delFlag = true;
						} else {
							T9FileUtility.deleteAll(filePath + File.separator + names[i]);
							addLogInfo = (filePath + File.separator + names[i]).replace("\\", "/");
              delFlag = true;
						}
						if (delFlag) {
	            // 写入系统日志
	              String remark = "删除 " + addLogInfo ;
	              T9SysLogLogic.addSysLog(dbConn, T9LogConst.NET_DISK, remark, loginUserSeqId, request.getRemoteAddr());
	            }
					}
				}

			} else {
				if (names.length != 0) {
					for (int i = 0; i < names.length; i++) {
						if ("1".equals(recycle.trim())) {
							String rand = emut.getRandom();
							String fileName = rand + "_" + names[i];
							T9FileUtility.xcopyFile(path + File.separator + names[i], recyclePath + File.separator + fileName);
							addLogInfo = (path + File.separator + names[i]).replace("\\", "/");
              delFlag = true;
						} else {
							T9FileUtility.deleteAll(path + File.separator + names[i]);
							addLogInfo = (path + File.separator + names[i]).replace("\\", "/");
              delFlag = true;
						}
						if (delFlag) {
              // 写入系统日志
                String remark = "删除 " + addLogInfo ;
                T9SysLogLogic.addSysLog(dbConn, T9LogConst.NET_DISK, remark, loginUserSeqId, request.getRemoteAddr());
              }

					}
					request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
					request.setAttribute(T9ActionKeys.RET_MSRG, "成功删除文件夹");

				}

			}

		} catch (Exception ex) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
			throw ex;
		}
		return "/core/inc/rtjson.jsp";
	}

	/**
	 * 粘贴文件
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String pasteFile(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String srcPath = (String) request.getSession().getAttribute("NETDISK_PATH"); // d:/test/tdoa_cache
		String fileList = (String) request.getSession().getAttribute("NETDISK_FILENAME"); // sa要.gif*
		String action = (String) request.getSession().getAttribute("NETDISK_ACTION"); // copy
		String netdisk_sub_url = (String) request.getSession().getAttribute("netdisk_sub_url"); // 全文检索用到
		String path = request.getParameter("DISK_ID"); // d:/bjfaoitc/ 要保存的路径

		if (fileList == null) {
			fileList = "";
		}

		if (srcPath == null) {
			srcPath = "";
		}
		if (netdisk_sub_url == null) {
			netdisk_sub_url = "";
		}

		if (path == null) {
			path = "";
		}
		String desPath = path.replace('\\', '/');

		T9NetDiskLogic logic = new T9NetDiskLogic();

		try {

			String[] names = null;
			if (fileList != null) {
				names = fileList.split("\\*");
			}

			if (!"".equals(srcPath.trim())) {

				if ("cut".equals(action.trim())) {
					for (int i = 0; i < names.length; i++) {
						String fileName = names[i];
						String fileType = "";
						String nameTitle = "";

						if (fileName.lastIndexOf(".") != -1) {
							fileType = fileName.substring(fileName.lastIndexOf(".")); // .doc
							nameTitle = fileName.substring(0, fileName.lastIndexOf("."));
						}
						File file = new File(desPath + "/" + fileName);
						if (!file.exists()) {

							T9FileUtility.xcopyFile(srcPath + "/" + fileName, file.getAbsolutePath());
						} else {
							logic.cutEexistsFile(srcPath, desPath, fileName, nameTitle, fileType);

						}

					}
				} else {
					for (int i = 0; i < names.length; i++) {
						String fileName = names[i];

						String fileType = "";
						String nameTitle = "";
						if (fileName.lastIndexOf(".") != -1) {
							fileType = fileName.substring(fileName.lastIndexOf(".")); // .doc
							nameTitle = fileName.substring(0, fileName.lastIndexOf("."));
						}

						File file = new File(desPath + "/" + fileName);
						if (!file.exists()) {
							T9FileUtility.copyFile(srcPath + "/" + fileName, file.getAbsolutePath());
						} else {
							logic.copyEexistsFile(srcPath, desPath, fileName, nameTitle, fileType);
						}

					}
				}

			} else if (!"".equals(netdisk_sub_url.trim())) {
				String[] diskPatthArry = null;
				if (netdisk_sub_url != null) {
					diskPatthArry = netdisk_sub_url.split("\\*");
				}

				if ("cut".equals(action.trim())) {
					for (int i = 0; i < names.length; i++) {

						String fileName = names[i];
						String filePath = diskPatthArry[i];

						String fileType = "";
						String nameTitle = "";

						if (fileName.lastIndexOf(".") != -1) {
							fileType = fileName.substring(fileName.lastIndexOf(".")); // .doc
							nameTitle = fileName.substring(0, fileName.lastIndexOf("."));
						}
						File file = new File(desPath + "/" + fileName);
						if (!file.exists()) {

							T9FileUtility.xcopyFile(filePath + "/" + fileName, file.getAbsolutePath());
						} else {
							logic.cutEexistsFile(filePath, desPath, fileName, nameTitle, fileType);

						}

					}

				} else {

					for (int i = 0; i < names.length; i++) {
						String fileName = names[i];
						String filePath = diskPatthArry[i];

						String fileType = "";
						String nameTitle = "";
						if (fileName.lastIndexOf(".") != -1) {
							fileType = fileName.substring(fileName.lastIndexOf(".")); // .doc
							nameTitle = fileName.substring(0, fileName.lastIndexOf("."));
						}

						File file = new File(desPath + "/" + fileName);
						if (!file.exists()) {
							T9FileUtility.copyFile(filePath + "/" + fileName, file.getAbsolutePath());
						} else {
							logic.copyEexistsFile(filePath, desPath, fileName, nameTitle, fileType);
						}

					}

				}

			}

			request.getSession().setAttribute("NETDISK_PATH", null);
			request.getSession().setAttribute("netdisk_sub_url", null);
			request.getSession().setAttribute("NETDISK_FILENAME", null);
			request.getSession().setAttribute("NETDISK_ACTION", null);

			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "成功删除文件夹");
		} catch (Exception ex) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
			throw ex;
		}
		return "/core/inc/rtjson.jsp";
	}

	/**
	 * 取得磁盘所有信息
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String getNetDiskInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String path = request.getParameter("DISK_ID");
		String seqIdStr = request.getParameter("seqId");

		// String currNoStr = request.getParameter("currNo"); // 当前的页码
		String pageNoStr = request.getParameter("pageNo");
		String pageSizeStr = request.getParameter("pageSize");

		String orderBy = request.getParameter("field");
		String ascDesc = request.getParameter("ascDescFlag");

		if (orderBy == null) {
			orderBy = "";
		}
		if (ascDesc == null) {
			ascDesc = "";
		}

		// 获取登录用户信息
		T9Person loginUser = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
		T9NetDiskLogic logic = new T9NetDiskLogic();

		int seqId = 0;
		if (seqIdStr != null) {
			seqId = Integer.parseInt(seqIdStr);
		}

		int currNo = 1;
		if (T9Utility.isNullorEmpty(pageNoStr)) {
			currNo = 1;
		} else {
			currNo = Integer.parseInt(pageNoStr);
		}

		if (path == null || "".equals(path)) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			return "/core/inc/rtjson.jsp";
		}
		StringBuffer tmp = new StringBuffer("[");

		boolean isHave = false;
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();

		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			T9Netdisk diNetdisk = logic.getFileSortInfoById(dbConn, seqIdStr);

			if ("".equals(orderBy)) {
				orderBy = T9Utility.null2Empty(diNetdisk.getOrderBy());

			}
			if ("".equals(ascDesc)) {
				ascDesc = T9Utility.null2Empty(diNetdisk.getAscDesc());
			}

			if ("taille".equals(orderBy) || "SIZE".equals(orderBy)) {
				orderBy = "SIZE";

			} else if ("mod".equals(orderBy) || "TIME".equals(orderBy)) {
				orderBy = "TIME";

			} else if ("type".equals(orderBy) || "TYPE".equals(orderBy)) {
				orderBy = "TYPE";

			} else {
				orderBy = "NAME";
			}

			String diskPath = T9Utility.null2Empty(diNetdisk.getDiskPath());
			File file2 = new File(diskPath);

			path = path.replace('\\', '/'); // D:\aa\\
			File file = new File(path); // D:\aa

			int rootDir = 0; // 用于判断是根目录还是子目录,1为根目录
			if (file2.getPath().equals(file.getPath())) {
				rootDir = 1;
			}

			// 获取系统设置的大小			int spaceLimit = diNetdisk.getSpaceLimit();

			long returnSize = 0; // 返回的大小
			long limitSize = 0; // 系统设置大小M转换为b
			long folderTotalSize = 0;
			if (spaceLimit != 0) {
				limitSize = logic.transFolderSize(spaceLimit);
				folderTotalSize = logic.getFolderSize(file2);
			}
			returnSize = limitSize - folderTotalSize;
			if (spaceLimit == 0) {
				returnSize = 0;
			} else if (returnSize < 0) {
				returnSize = 1;
			}
			//
			// File[] files = file.listFiles();
			// int j = 0;
			// if (files != null && files.length != 0) {
			// for (int i = 0; i < files.length; i++) {
			// File subfile = files[i];
			// if (!subfile.isDirectory()) {
			//
			// Map m = new HashMap();
			//
			// String fileType =
			// T9FileUtility.getFileExtName(subfile.getAbsolutePath());
			// String typeName = logic.getFileTypeName(fileType);
			// boolean isOffice = logic.is_office("." + fileType);
			// String officeFlag = "0";
			// if (isOffice) {
			// officeFlag = "1";
			// }
			//
			// long fileSize = subfile.length();
			//
			// m.put("fileName", subfile.getName());
			// m.put("fileSpace", String.valueOf(fileSize));
			// m.put("fileType", typeName);
			// m.put("officeFlag", officeFlag);
			// m.put("rootDir", String.valueOf(rootDir));
			// m.put("filePath", subfile.getAbsolutePath().replace('\\', '/'));
			// m.put("fileModifyTime", T9Utility.getDateTimeStr(new
			// Date(subfile.lastModified())));
			// list.add(m);
			// j++;
			// }
			// }
			// }
			//
			list = logic.getfileInforByList(dbConn, loginUser, diNetdisk, path);
			if (list != null && list.size() > 0) {

				if ("NAME".equals(orderBy.trim())) {

					if ("1".equals(ascDesc.trim())) {
						T9Utility.sortDesc(list, "fileName", T9MapComparator.TYPE_STR);
					} else {
						T9Utility.sortAsc(list, "fileName", T9MapComparator.TYPE_STR);
					}

				} else if ("SIZE".equals(orderBy.trim())) {
					if ("1".equals(ascDesc.trim())) {
						T9Utility.sortDesc(list, "fileSpace", T9MapComparator.TYPE_LONG);
					} else {
						T9Utility.sortAsc(list, "fileSpace", T9MapComparator.TYPE_LONG);
					}
				} else if ("TYPE".equals(orderBy.trim())) {
					if ("1".equals(ascDesc.trim())) {
						T9Utility.sortDesc(list, "fileType", T9MapComparator.TYPE_STR);
					} else {
						T9Utility.sortAsc(list, "fileType", T9MapComparator.TYPE_STR);
					}
				} else if ("TIME".equals(orderBy.trim())) {
					if ("1".equals(ascDesc.trim())) {
						T9Utility.sortDesc(list, "fileModifyTime", T9MapComparator.TYPE_STR);
					} else {
						T9Utility.sortAsc(list, "fileModifyTime", T9MapComparator.TYPE_STR);
					}
				}

			}

			long count = list.size();
			int pageSize = 20;// 一个页面显示的数目
			if (pageSizeStr != null) {
				pageSize = Integer.parseInt(pageSizeStr);
			}

			T9Page page = new T9Page(pageSize, count, (currNo + 1));
			long first = page.getFirstResult();
			long last = page.getLastResult();
			for (int i = (int) first; i < last; i++) {
				Map<String, String> map = list.get(i);

				String fileSizeString = map.get("fileSpace");
				long fileSizeLong = Long.parseLong(fileSizeString);

				tmp.append("{");
				tmp.append("fileName:\"" + T9Utility.encodeSpecial(map.get("fileName")) + "\"");
				tmp.append(",fileSpace:\"" + logic.transformSize(fileSizeLong) + "\"");
				tmp.append(",fileType:\"" + map.get("fileType") + "\"");
				tmp.append(",officeFlag:\"" + map.get("officeFlag") + "\"");
				tmp.append(",rootDir:\"" + map.get("rootDir") + "\"");
				tmp.append(",flolderSize:" + returnSize); // 返回文件夹大小单位为B
				tmp.append(",filePath:\"" + T9Utility.encodeSpecial(map.get("filePath")) + "\"");
				tmp.append(",fileModifyTime:\"" + map.get("fileModifyTime") + "\"");

				tmp.append(",totalRecord:\"" + count + "\"");
				tmp.append(",pageNo:\"" + currNo + "\"");
				tmp.append(",pageSize:\"" + pageSize + "\"");
				tmp.append(",ascDesc:\"" + ascDesc + "\"");
				tmp.append(",orderBy:\"" + orderBy + "\"");

				tmp.append("},");
				isHave = true;
			}
			if (isHave) {
				tmp.deleteCharAt(tmp.length() - 1);
				tmp.append("]");
			} else {
				tmp.append("]");
			}

			// request.getSession().setAttribute("FILE_COUNT", new Integer(j));
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "成功取出数据");
			request.setAttribute(T9ActionKeys.RET_DATA, tmp.toString());
		} catch (Exception ex) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
			throw ex;
		}
		return "/core/inc/rtjson.jsp";
	}

	/**
	 * 获取树形结构信息
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String getTreebyFileSystem(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String idStr = request.getParameter("id");
		String path = request.getParameter("DISK_ID");
		String seqIdStr = request.getParameter("seqId");

		String id = "0";
		int seqId = 0;
		if (!"".equals(seqIdStr) && seqIdStr != null) {
			seqId = Integer.parseInt(seqIdStr);
		}
		if (idStr != null && !"".equals(idStr)) {
			id = idStr;
		}
		if (!"".equals(idStr) && idStr != null && !"0".equals(idStr) && "".equals(path)) {
			idStr = idStr.replace('\\', '/');
		}
		if ("0".equals(idStr) && path != null && !"".equals(path)) {
			idStr = path.replace('\\', '/');
		}
		if (idStr != null && !"".equals(idStr) && path != null && !"".equals(path)) {
			idStr = idStr.replace('\\', '/');
		}

		T9NetDiskLogic diskLogic = new T9NetDiskLogic();
		List<T9Netdisk> list = new ArrayList<T9Netdisk>();
		StringBuffer sb = new StringBuffer("[");

		// 获取登录用户信息
		T9Person loginUser = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
		// int loginUserSeqId = loginUser.getSeqId();
		// int loginUserDeptId = loginUser.getDeptId();
		// String loginUserRoleId = loginUser.getUserPriv();
		// boolean userFlag = false;
		// boolean roleFlag = false;
		// boolean deptFlag = false;

		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			boolean isHave = false;
			
			boolean userDefined = false;//自定义菜单
			if (!T9Utility.isNullorEmpty(path) && seqId!=0) {
				userDefined = true;
			}
			if (userDefined) {
				if ("0".equalsIgnoreCase(id)) {
					T9Netdisk netdisk = diskLogic.getNetdiskInfoById(dbConn, seqId);
					if (netdisk != null) {
						String filePath = T9Utility.null2Empty(netdisk.getDiskPath());
						boolean userIdPriv = diskLogic.getUserIdAccessPriv(dbConn, loginUser, netdisk);
						boolean manageUserPriv = diskLogic.getManageAccessPriv(dbConn, loginUser, netdisk);
						if (userIdPriv || manageUserPriv) {
							if (!T9Utility.isNullorEmpty(filePath.trim())) {
								int isHaveChild = 0;
								File file = new File(filePath.trim());
								if (file != null) {
									File[] files = file.listFiles();
									if (files != null && files.length != 0) {
										for (File subFile : files) {
											if (subFile.isDirectory()) {
												isHaveChild = 1;
												break;
											}
										}
									}
								}
								sb.append("{");
								sb.append("nodeId:\"" + T9Utility.encodeSpecial(T9Utility.null2Empty(netdisk.getDiskPath()).replace('\\', '/')) + "\"");
								sb.append(",name:\"" + T9Utility.encodeSpecial(T9Utility.null2Empty(netdisk.getDiskName())) + "\"");
								sb.append(",isHaveChild:" + isHaveChild);
								sb.append(",extData:\"" + netdisk.getSeqId() + "\"");
								sb.append("},");
								isHave = true;
							}
						}
						if (isHave) {
							sb.deleteCharAt(sb.length() - 1);
						}
						sb.append("]");
					}else {
						sb.append("]");
					}
				}else {
					File file = new File(id);
					if (file!=null) {
						File[] files = file.listFiles();
						if (files != null && files.length > 0) {
							for (int i = 0; i < files.length; i++) {
								File subFile = files[i];
								if (subFile.isDirectory() && !"tdoa_cache".equalsIgnoreCase(subFile.getName().trim())) {
									int isHaveChild = 0;
									File[] subFiles = subFile.listFiles();
									if (subFiles != null) {
										for (int j = 0; j < subFiles.length; j++) {
											File ssFile = subFiles[j];
											if (ssFile.isDirectory()) {
												isHaveChild = 1;
												break;
											}
										}
									}
									String extData = "";
									sb.append("{");
									sb.append("nodeId:\"" + subFile.getAbsolutePath().replace('\\', '/') + "\"");
									sb.append(",name:\"" + T9Utility.encodeSpecial(subFile.getName()) + "\"");
									sb.append(",isHaveChild:" + isHaveChild + "");
									sb.append(",extData:\"" + seqId + "\"");
									sb.append("},");
									isHave = true;
								}
							}
						}
						if (isHave == true) {
							sb.deleteCharAt(sb.length() - 1);
							
						} 
						sb.append("]");
					}else {
						sb.append("]");
					}
				}
			}else {
				if (T9Utility.isNullorEmpty(path) && "0".equalsIgnoreCase(id)) {				
					list = diskLogic.getNetDisksInfo(dbConn);
					if (list.size() > 0) {
//						sb.append("[");
						// 遍历数据库中的目录
						for (T9Netdisk disk : list) {
							String filePath = T9Utility.null2Empty(disk.getDiskPath());
							// T9FileSortLogic fileSortLogic = new T9FileSortLogic();
							// Map map = new HashMap();
							// map.put("SEQ_ID", disk.getSeqId());
							// String userPrivs = diskLogic.getUserIds(dbConn, map, "USER_ID");
							// String rolePrivs = diskLogic.getRoleIds(dbConn, map, "USER_ID");
							// String deptPrivs = diskLogic.getDeptIds(dbConn, map, "USER_ID");
							// a
							// userFlag = fileSortLogic.getUserIdStr(loginUserSeqId, userPrivs,
							// dbConn);
							// roleFlag = fileSortLogic.getRoleIdStr(loginUserRoleId, rolePrivs,
							// dbConn);
							// deptFlag = fileSortLogic.getDeptIdStr(loginUserDeptId, deptPrivs,
							// dbConn);
							boolean userIdPriv = diskLogic.getUserIdAccessPriv(dbConn, loginUser, disk);
							boolean manageUserPriv = diskLogic.getManageAccessPriv(dbConn, loginUser, disk);

							if (userIdPriv || manageUserPriv) {
								if (!T9Utility.isNullorEmpty(filePath.trim())) {
									int isHaveChild = 0;
									File file = new File(filePath.trim());
									if (file != null) {
										File[] files = file.listFiles();
										if (files != null && files.length != 0) {
											for (File subFile : files) {
												if (subFile.isDirectory()) {
													isHaveChild = 1;
													break;
												}
											}
										}
									}
									sb.append("{");
									sb.append("nodeId:\"" + T9Utility.encodeSpecial(T9Utility.null2Empty(disk.getDiskPath()).replace('\\', '/')) + "\"");
									sb.append(",name:\"" + T9Utility.encodeSpecial(T9Utility.null2Empty(disk.getDiskName())) + "\"");
									sb.append(",isHaveChild:" + isHaveChild);
									sb.append(",extData:\"" + disk.getSeqId() + "\"");
									sb.append("},");
									isHave = true;
								}
							}
						}
						if (isHave) {
							sb.deleteCharAt(sb.length() - 1);
						}
						sb.append("]");
					} else {
						sb.append("]");
					}
				} else if (path != null && !"".equals(idStr)) {
					File file = new File(idStr);
					if (file!=null) {
						File[] files = file.listFiles();
//						sb.append("[");
						if (files != null && files.length > 0) {
							for (int i = 0; i < files.length; i++) {
								File subFile = files[i];
								if (subFile.isDirectory() && !"tdoa_cache".equalsIgnoreCase(subFile.getName().trim())) {
									int isHaveChild = 0;
									File[] subFiles = subFile.listFiles();
									if (subFiles != null) {
										for (int j = 0; j < subFiles.length; j++) {
											File ssFile = subFiles[j];
											if (ssFile.isDirectory()) {
												isHaveChild = 1;
												break;
											}
										}
									}
									String extData = "";
									// String imgAddress =
									// "/t9/core/styles/style1/img/dtree/folder.gif";
									sb.append("{");
									sb.append("nodeId:\"" + subFile.getAbsolutePath().replace('\\', '/') + "\"");
									sb.append(",name:\"" + T9Utility.encodeSpecial(subFile.getName()) + "\"");
									sb.append(",isHaveChild:" + isHaveChild + "");
									sb.append(",extData:\"" + seqId + "\"");
									// tmp.append(",imgAddress:\"" + imgAddress + "\"");
									sb.append("},");
									isHave = true;
								}
							}
						}
						if (isHave == true) {
							sb.deleteCharAt(sb.length() - 1);
							
						} 
						sb.append("]");
					}else {
						sb.append("]");
					}
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
	 * 构造单个对象,上传单个文件、批量文件	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String uploadFile(HttpServletRequest request, HttpServletResponse response) throws Exception {
		T9FileUploadForm fileForm = new T9FileUploadForm();
		String contextPath = request.getContextPath();
    try {
      fileForm.parseUploadRequest(request);
    } catch (Exception e) {
      response.sendRedirect(contextPath + "/core/funcs/netdisk/uploadFailse.jsp");
      return null;
    }
		String path = fileForm.getParameter("DISK_ID");
		String seqIdStr = request.getParameter("seqId");
		String smsPerson = fileForm.getParameter("smsPerson");
		String mobileSmsPerson = fileForm.getParameter("mobileSmsPerson");

		if (path == null) {
			path = request.getParameter("DISK_ID");
		}
		if (path == null || "0".equals(path)) {
			path = new String("D:"+ File.separator +"tmp");
		}

		if (smsPerson == null) {
			smsPerson = "";
		}
		if (mobileSmsPerson == null) {
			mobileSmsPerson = "";
		}

		String fileName = "";

		// // 获取登录用户信息
		T9Person loginUser = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
		int loginUserSeqId = loginUser.getSeqId();
		int loginUserDeptId = loginUser.getDeptId();
		String loginUserRoleId = loginUser.getUserPriv();

		T9NetDiskLogic logic = new T9NetDiskLogic();

		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();

			String filePath = path.replace('\\', '/');
			String fileExists = fileForm.getExists(filePath);
			Iterator<String> iKeys = fileForm.iterateFileFields();
			String addLogInfo = "";
			while (iKeys.hasNext()) {
				String fieldName = iKeys.next();
				fileName = fileForm.getFileName(fieldName);
				if (T9Utility.isNullorEmpty(fileName)) {
					continue;
				}
				File file = new File(filePath+"/" + fileName);
				if (file!=null && !file.exists()) {
					fileForm.saveFile(fieldName, filePath + File.separator + fileName);
					addLogInfo = file.getPath().replace("\\", "/");
				}else {
					StringBuffer buffer = new StringBuffer();
					String fileType = "";
					String nameTitle = "";
					if (fileName.lastIndexOf(".") != -1) {
						fileType = fileName.substring(fileName.lastIndexOf(".")); // .doc
						nameTitle = fileName.substring(0, fileName.lastIndexOf("."));
					}
					logic.uploadEexistsFile(buffer,filePath, fileName, nameTitle, fileType);
					String newFileName = buffer.toString();
					if (!T9Utility.isNullorEmpty(newFileName)) {
						fileForm.saveFile(fieldName, filePath + File.separator + newFileName.trim());
						
						File file2 = new File(filePath + "/" + newFileName.trim());
		        fileName = newFileName.trim();
		        if (file2!=null) {
		          addLogInfo = file2.getPath().replace("\\", "/");
		        }
						
					}
				}
				 // 写入系统日志
			  String remark = "上传 " + addLogInfo ;
			  T9SysLogLogic.addSysLog(dbConn, T9LogConst.NET_DISK, remark, loginUserSeqId, request.getRemoteAddr());
			}

			// 短信提醒
			// T9SmsUtil sms=new T9SmsUtil();
			T9SmsBack sms = new T9SmsBack();
			String loginName = logic.getPersonNamesByIds(dbConn, String.valueOf(loginUserSeqId));
			String smsContent = loginName + " 在网络硬盘【" + filePath.replace('\\', '/') + "】 上传文件:" + fileName;
			String remindUrl = "/core/funcs/netdisk/fileList.jsp?smsRemindFlag=1&seqId=" + seqIdStr + "&DISK_ID=" + filePath;
		//	URLEncoder.encode(filePath,"UTF-8");
			if ("allPrivPerson".equals(smsPerson)) {

				Map map = new HashMap();
				map.put("SEQ_ID", Integer.parseInt(seqIdStr));

				T9Netdisk netdisk = logic.getFileSortInfoById(dbConn, seqIdStr);
				String deptIdStr = logic.getDeptIds(dbConn, map, "USER_ID");
				String roleIdStr = logic.getRoleIds(dbConn, map, "USER_ID");
				String personIdStr = logic.getUserIds(dbConn, map, "USER_ID");

				if (!"".equals(personIdStr)) {
					personIdStr += ",";
				}
				
				String deptPersonIdStr ="";
				String rolePersonIdStr ="";
				if (!T9Utility.isNullorEmpty(deptIdStr)) {
					deptPersonIdStr = logic.getDeptPersonIds(deptIdStr, dbConn);
				}
				if (!T9Utility.isNullorEmpty(roleIdStr)) {
					rolePersonIdStr = logic.getRolePersonIds(roleIdStr, dbConn);
				}
				String allPersonIdStr = personIdStr + deptPersonIdStr + rolePersonIdStr;

				String allpersonStr = "";
				ArrayList al = new ArrayList();
				String[] arr = allPersonIdStr.split(",");
				for (int i = 0; i < arr.length; i++) {
					if (al.contains(arr[i]) == false) {
						al.add(arr[i]);
						allpersonStr += arr[i] + ",";
					}
				}
				if (allpersonStr != null && !"".equals(allpersonStr)) {
					sms.setFromId(loginUserSeqId);
					sms.setToId(allpersonStr.trim());
					sms.setContent(smsContent);
					sms.setSendDate(T9Utility.parseTimeStamp());
					sms.setSmsType(T9LogConst.NET_DISK);
					sms.setRemindUrl(remindUrl);
					T9SmsUtil.smsBack(dbConn, sms);
				}
			} else if (!"".equals(smsPerson)) {
				sms.setFromId(loginUserSeqId);
				sms.setToId(smsPerson);
				sms.setContent(smsContent);
				sms.setSendDate(T9Utility.parseTimeStamp());
				sms.setSmsType(T9LogConst.NET_DISK);
				sms.setRemindUrl(remindUrl);
				T9SmsUtil.smsBack(dbConn, sms);

			}

			// 手机短信提醒mobileSmsPerson
			String mobileSmsContent = loginName + " 在网络硬盘【" + filePath.replace('\\', '/') + "】 上传文件:" + fileName;
			T9MobileSms2Logic mobileSms = new T9MobileSms2Logic();
			if ("allPrivPerson".equals(mobileSmsPerson.trim())) {
				Map map = new HashMap();
				map.put("SEQ_ID", Integer.parseInt(seqIdStr));

				T9Netdisk netdisk = logic.getFileSortInfoById(dbConn, seqIdStr);
				String deptIdStr = logic.getDeptIds(dbConn, map, "USER_ID");
				String roleIdStr = logic.getRoleIds(dbConn, map, "USER_ID");
				String personIdStr = logic.getUserIds(dbConn, map, "USER_ID");

				if (!"".equals(personIdStr)) {
					personIdStr += ",";
				}
				
				String deptPersonIdStr ="";
				String rolePersonIdStr ="";
				if (!T9Utility.isNullorEmpty(deptIdStr)) {
					deptPersonIdStr = logic.getDeptPersonIds(deptIdStr, dbConn);
				}
				if (!T9Utility.isNullorEmpty(roleIdStr)) {
					rolePersonIdStr = logic.getRolePersonIds(roleIdStr, dbConn);
				}
				String allPersonIdStr = personIdStr + deptPersonIdStr + rolePersonIdStr;

				String allpersonStr = "";
				ArrayList al = new ArrayList();
				String[] arr = allPersonIdStr.split(",");
				for (int i = 0; i < arr.length; i++) {
					if (al.contains(arr[i]) == false) {
						al.add(arr[i]);
						allpersonStr += arr[i] + ",";
					}
				}
				if (allpersonStr != null && !"".equals(allpersonStr)) {
					mobileSms.remindByMobileSms(dbConn, allpersonStr, loginUserSeqId, mobileSmsContent, null);
				}

			} else if (!"".equals(mobileSmsPerson.trim())) {
				mobileSms.remindByMobileSms(dbConn, mobileSmsPerson, loginUserSeqId, mobileSmsContent, null);
			}
			request.setAttribute("diskPath", path);
			request.setAttribute("seqId", seqIdStr);
		} catch (Exception ex) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
			throw ex;
		}
		return "/core/funcs/netdisk/uploadResult.jsp";
	}

	/**
	 * 通过id获取该共享目录的所有权限信息	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String getPrivInfoById(HttpServletRequest request, HttpServletResponse response) throws Exception {
		T9NetDiskLogic logic = new T9NetDiskLogic();
		String pathId = request.getParameter("DISK_ID");
		String seqId = request.getParameter("seqId");
		// 获取登录用户信息
		T9Person loginUser = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
		int loginUserSeqId = loginUser.getSeqId();
		int loginUserDeptId = loginUser.getDeptId();
		String loginUserRoleId = loginUser.getUserPriv();

		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();

			T9NetDiskLogic diskLogic = new T9NetDiskLogic();

			// T9Netdisk diNetdisk = logic.getFileSortInfoById(dbConn, seqId);

			Map map = new HashMap();
			map.put("SEQ_ID", Integer.parseInt(seqId));
			String data = logic.getVisiPriv(dbConn, map, loginUserSeqId, loginUserDeptId, loginUserRoleId ,loginUser.getUserPrivOther() , loginUser.getDeptIdOther() , pathId);

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
	 * 根据seqId得到路径
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String getFiilePathBySeqId(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String seqIdStr = request.getParameter("seqId");

		int seqId = 0;
		if (seqIdStr != null && !"".equals(seqIdStr)) {
			seqId = Integer.parseInt(seqIdStr);
		}

		T9NetDiskLogic logic = new T9NetDiskLogic();
		Connection dbConn;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();

			T9Netdisk netdisk = logic.getNetdiskInfoById(dbConn, seqId);
			String diskPath = netdisk.getDiskPath() == null ? "" : netdisk.getDiskPath();

			String data = "{folderPath:\"" + diskPath + "\" }";

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
	 * 全文检索
	 * 
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String queryNetdiskInfoById(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String seqIdStr = request.getParameter("seqId"); // 5
		String pathIdStr = URLDecoder.decode(request.getParameter("DISK_ID"), "UTF-8"); // d:/bjfaoitc/~!@#$%^/aaacccwwww
		String fileNameStr = URLDecoder.decode(request.getParameter("fileName"), "UTF-8"); // aaaa
		String keyStr = request.getParameter("key"); // cccc

		int seqId = 0;
		if (seqIdStr != null && !"".equals(seqIdStr)) {
			seqId = Integer.parseInt(seqIdStr);
		}

		if (pathIdStr == null) {
			pathIdStr = "";
		}

		Map<Object, Object> map = new HashMap<Object, Object>();
		map.put("seqId", seqId);
		map.put("pathIdStr", pathIdStr);
		map.put("fileNameStr", fileNameStr);
		map.put("keyStr", keyStr);

		T9Person loginUser = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
		T9NetDiskLogic logic = new T9NetDiskLogic();

		Connection dbConn;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();

			boolean isHave = false;
			StringBuffer buffer = new StringBuffer("[");

			List<Map<Object, Object>> fileContentsList = logic.getGlobalFileContentsByList(dbConn, map, loginUser);

			if (fileContentsList != null && fileContentsList.size() != 0) {

				for (Map<Object, Object> fileContentMap : fileContentsList) {

					int dbSeqId = (Integer) fileContentMap.get("dbSeqId");
					String urlString = (String) fileContentMap.get("urlString");
					String filePath = (String) fileContentMap.get("filePath");
					String absolutePath = (String) fileContentMap.get("absolutePath");
					int officeFlag = (Integer) fileContentMap.get("officeFlag");
					String fileName = (String) fileContentMap.get("fileName");
					String fileSize = (String) fileContentMap.get("fileSize");
					String fileModifyTime = (String) fileContentMap.get("fileModifyTime");
					String typeName = (String) fileContentMap.get("typeName");

					int userPriv = (Integer) fileContentMap.get("userPriv");
					int managePriv = (Integer) fileContentMap.get("managePriv");
					int downPriv = (Integer) fileContentMap.get("downPriv");

					buffer.append("{");

					buffer.append("dbSeqId:\"" + dbSeqId + "\"");
					buffer.append(",urlString:\"" + urlString + "\"");
					buffer.append(",filePath:\"" + filePath + "\"");
					buffer.append(",absolutePath:\"" + absolutePath + "\"");
					buffer.append(",officeFlag:\"" + officeFlag + "\"");
					buffer.append(",fileName:\"" + fileName + "\"");
					buffer.append(",fileSize:\"" + fileSize + "\"");
					buffer.append(",fileModifyTime:\"" + fileModifyTime + "\"");
					buffer.append(",typeName:\"" + typeName + "\"");

					buffer.append(",userPriv:" + userPriv);
					buffer.append(",managePriv:" + managePriv);
					buffer.append(",downPriv:" + downPriv);

					buffer.append("},");
					isHave = true;

				}
				if (isHave) {
					buffer.deleteCharAt(buffer.length() - 1);
				}
				buffer.append("]");

			} else {
				buffer.append("]");
			}

			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "成功取出数据");
			request.setAttribute(T9ActionKeys.RET_DATA, buffer.toString());
		} catch (Exception ex) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
			throw ex;
		}

		return "/core/inc/rtjson.jsp";
	}

	/**
	 * 取得要显示的路径名	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String showPathName(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String seqIdStr = request.getParameter("seqId");
		String fromDiskPath = request.getParameter("diskPath");
		int seqId = 0;
		if (seqIdStr != null) {
			seqId = Integer.parseInt(seqIdStr);
		}
		if (T9Utility.isNullorEmpty(fromDiskPath)) {
			fromDiskPath = "";
		}
		T9NetDiskLogic logic = new T9NetDiskLogic();
		Connection dbConn;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();

			if (fromDiskPath.trim().replace("\\", "/").indexOf("/") != -1) {
				fromDiskPath = fromDiskPath.trim().substring(fromDiskPath.indexOf("/") + 1, fromDiskPath.length());

				if (!fromDiskPath.trim().endsWith("/")) {
					fromDiskPath += "/";
				}
			}
			T9Netdisk netdisk = logic.getNetdiskInfoById(dbConn, seqId);
			String diskName ="";
			if (netdisk!=null) {
				diskName=T9Utility.null2Empty(netdisk.getDiskName());
			}
			String returnName = diskName + "/" + fromDiskPath;
			String data = "{diskPathNameStr:\"" + T9Utility.encodeSpecial(returnName) + "\"}";
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_DATA, data);
		} catch (Exception ex) {
			request.setAttribute(T9ActionKeys.RET_STATE, "要显示的路径名失败!");
			request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
			throw ex;
		}
		return "/core/inc/rtjson.jsp";
	}
	
	/**
	 * 读取txt/text文件内容
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String getInfoFromText(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String filePath = request.getParameter("filePath");
		StringBuffer buffer = new StringBuffer();
		try {
			T9NetDiskLogic logic = new T9NetDiskLogic();
			
			String str = logic.getInfoFromTextLogic(buffer,filePath);
			String data = "{textData:\""+  T9Utility.encodeHtml(str) + "\"}";
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "成功取出数据");
			request.setAttribute(T9ActionKeys.RET_DATA, data);
		} catch (Exception ex) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
		}
		return "/core/inc/rtjson.jsp";
	}
	
	/**
	 * 保存txt/text文件内容
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String saveInfoToText(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String filePath = request.getParameter("filePath");
		String textContent = request.getParameter("textContent");
		if (T9Utility.isNullorEmpty(filePath)) {
			filePath = "";
		}
		if (T9Utility.isNullorEmpty(textContent)) {
			textContent = "";
		}
		try {
//			T9NetDiskLogic logic = new T9NetDiskLogic();
			T9FileUtility.storBytes2File(filePath, textContent.getBytes("gbk") );
			
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "成功取出数据");
		} catch (Exception ex) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
		}
		return "/core/inc/rtjson.jsp";
	}
	
	
	
	
	
	

}
