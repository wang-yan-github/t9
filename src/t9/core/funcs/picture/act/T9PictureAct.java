package t9.core.funcs.picture.act;

import java.awt.Image;
import java.io.File;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.sanselan.Sanselan;

import t9.core.data.T9MapComparator;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.picture.data.T9Picture;
import t9.core.funcs.picture.logic.T9PictureLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.global.T9SysProps;
import t9.core.oaknow.util.T9StringUtil;
import t9.core.util.T9Utility;
import t9.core.util.file.T9FileUploadForm;
import t9.core.util.file.T9FileUtility;

public class T9PictureAct {

	/**
	 * 获取图片目录信息
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String getPicFolderInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {

		T9PictureLogic logic = new T9PictureLogic();
		StringBuffer sb = new StringBuffer();

		// 获取登录用户信息
		T9Person loginUser = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
		int loginUserSeqId = loginUser.getSeqId();
		int loginUserDeptId = loginUser.getDeptId();
		String loginUserRoleId = loginUser.getUserPriv();

		// boolean userFlag = false;
		// boolean roleFlag = false;
		// boolean deptFlag = false;

		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			boolean isHave = false;


			List<T9Picture> list = logic.getPicFolderInfo(dbConn);
			if (list.size() > 0) {
				sb.append("[");
				for (T9Picture picture : list) {
					Map map = new HashMap();
					map.put("SEQ_ID", picture.getSeqId());

					String toDdeptIdStr = "";
					String toPrivIdStr = "";
					String toUserIdStr = "";
					if (!"".equals(picture.getToDeptId()) && picture.getToDeptId() != null) {
						toDdeptIdStr = picture.getToDeptId();
					}
					if (!"".equals(picture.getToPrivId()) && picture.getToPrivId() != null) {
						toPrivIdStr = picture.getToPrivId();
					}
					if (!"".equals(picture.getToUserId()) && picture.getToUserId() != null) {
						toUserIdStr = picture.getToUserId();
					}

					boolean toDeptIdFlag = logic.getDeptIdPriv(loginUserDeptId, toDdeptIdStr);
					boolean toPrivIdFlag = logic.getPrivate(Integer.parseInt(loginUserRoleId), toPrivIdStr);
					boolean toUserIdFlag = logic.getPrivate(loginUserSeqId, toUserIdStr);

					if (toDeptIdFlag || toPrivIdFlag || toUserIdFlag) {
						sb.append("{");
						sb.append("seqId:\"" + picture.getSeqId() + "\"");
						sb.append(",picName:\"" + T9Utility.encodeSpecial(picture.getPicName()) + "\"");
						sb.append(",picPath:\"" + picture.getPicPath() + "\"");
						sb.append(",toDeptId:\"" + picture.getToDeptId() + "\"");
						sb.append(",toPrivId:\"" + picture.getToPrivId() + "\"");
						sb.append(",toUserId:\"" + picture.getToUserId() + "\"");
						sb.append(",privStr:\"" + picture.getPrivStr() + "\"");
						sb.append(",delPrivStr:\"" + picture.getDelPrivStr() + "\"");
						sb.append("},");
						isHave = true;
					}
				}
				if (isHave) {
					sb.deleteCharAt(sb.length() - 1);
				}

				sb.append("]");

			} else {
				sb.append("[]");
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
	 * 通过id获取目录下的所有文件信息
	 * 
	 * 
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String getPicInfoById(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String currNoStr = request.getParameter("currNo"); // 当前的页码
		String picIdStr = request.getParameter("seqId");
		String subDir = request.getParameter("subDir"); // 子文件夹路径 cc/aa/aa

		String orderBy = request.getParameter("field");
		String ascDesc = request.getParameter("ascDescFlag");

		if (subDir == null) {
			subDir = "";
		}
		if (subDir.trim().startsWith("/")) {
			subDir = subDir.trim().substring(subDir.indexOf("/") + 1, subDir.length());
		}
		if (T9Utility.isNullorEmpty(orderBy)) {
			orderBy = "NAME";
		}
		if (T9Utility.isNullorEmpty(ascDesc)) {
		  if (orderBy.equalsIgnoreCase("TIME")) {
		    ascDesc = "DESC";
		  }else {
		    ascDesc = "ASC";
		  }
		}

		int picId = 0;
		if (picIdStr != null && !"".equals(picIdStr.trim())) {
			picId = Integer.parseInt(picIdStr);
		}

		int currNo = 1;
		if (T9StringUtil.isEmpty(currNoStr)) {
			currNo = 1;
		} else {
			currNo = Integer.parseInt(currNoStr);
		}

		T9PictureLogic logic = new T9PictureLogic();
		Map map = new HashMap();
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		T9Picture picture = new T9Picture();

		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();

			map.put("SEQ_ID", picId);
			picture = logic.getPicFolderInfoById(dbConn, map);

			String showDir = ""; // 显示当前目录路径
			String dirPath = ""; // 文件夹目录路径
			if (picture != null) {
				dirPath = picture.getPicPath() + "/" + subDir;
				showDir = picture.getPicName() + "/" + subDir;

				if (showDir.endsWith("/")) {
					showDir = showDir.substring(0, showDir.lastIndexOf("/"));
				}
			}

			String noFolderFlag = "";

			String isPath = "";
			String isImage = "^.*?(\\.(png|gif|jpg|bmp|PNG|GIF|JPG|BMP))$";

			if (dirPath != null && !"".equals(dirPath.trim())) {
				File file = new File(dirPath);
				if (file != null && file.exists()) {
					File[] files = file.listFiles();
					if (files != null && files.length > 0) {
						for (File f : files) {
							if (f.isDirectory()) {
								isPath = "isDir";
							} else {
								String fileNameStr = f.getName();
								String lastName = "";
								if (fileNameStr.lastIndexOf(".") != -1) {
									lastName = fileNameStr.substring(fileNameStr.lastIndexOf("."));
								}
								if (lastName.matches(isImage)) {
									isPath = "isImage";
								} else {
									isPath = "isFile";
								}
							}

							String fileType = T9FileUtility.getFileExtName(f.getAbsolutePath());

							Map m = new HashMap();
							long fileSize = 0;
							if (!"isDir".equals(isPath)) {
								fileSize = f.length();
							}

							if (!"tdoa_cache".equals(f.getName())) {
								m.put("picName", f.getName());
								m.put("seqId", String.valueOf(picId));
								m.put("picPath", file.getPath());
								m.put("isPath", isPath);
								m.put("lastModify", T9Utility.getDateTimeStr(new Date(f.lastModified())));
								m.put("length", String.valueOf(fileSize));
								m.put("fileType", fileType);
								list.add(m);
							}

						}

					}

				} else {
					noFolderFlag = "noFolder";
				}

			} else {
				noFolderFlag = "noFolder";
			}

			if (list != null && list.size() > 0) {

				if ("NAME".equals(orderBy.trim())) {

					if ("1".equals(ascDesc.trim())) {
						T9Utility.sortDesc(list, "picName", T9MapComparator.TYPE_STR);
					} else {
						T9Utility.sortAsc(list, "picName", T9MapComparator.TYPE_STR);
					}

				} else if ("SIZE".equals(orderBy.trim())) {
					if ("1".equals(ascDesc.trim())) {
						T9Utility.sortDesc(list, "length", T9MapComparator.TYPE_LONG);
					} else {
						T9Utility.sortAsc(list, "length", T9MapComparator.TYPE_LONG);
					}
				} else if ("TYPE".equals(orderBy.trim())) {
					if ("1".equals(ascDesc.trim())) {
						T9Utility.sortDesc(list, "fileType", T9MapComparator.TYPE_STR);
					} else {
						T9Utility.sortAsc(list, "fileType", T9MapComparator.TYPE_STR);
					}
				} else if ("TIME".equals(orderBy.trim())) {
					if ("1".equals(ascDesc.trim())) {
						T9Utility.sortDesc(list, "lastModify", T9MapComparator.TYPE_STR);
					} else {
						T9Utility.sortAsc(list, "lastModify", T9MapComparator.TYPE_STR);
					}
				}

			}
			
			long count = list.size();
			int pageSize = 35;// 一个页面显示的数目
			T9Page page = new T9Page(pageSize, count, currNo);
			long first = page.getFirstResult();
			long last = page.getLastResult();
			List<Map<String, String>> list2 = new ArrayList<Map<String, String>>();
			for (int i = (int) first; i < last; i++) {
				Map<String, String> map2 = new HashMap<String, String>();
				map2 = list.get(i);
				String nameString = map2.get("picName");
				String picPathStr = map2.get("picPath");

				String fileSize = "";

				String fileLeng = (String) map2.get("length");
				fileSize = logic.transformSize(Long.parseLong(fileLeng));

				map2.put("length", fileSize);

				if (nameString.matches(isImage)) {
					// String path = picPathStr  + File.separator + nameString;
					boolean flagStr = logic.createCache(picPathStr, nameString);
				}
				list2.add(map2);
			}
			request.setAttribute("picList", list2);
			request.setAttribute("page", page);
			request.setAttribute("showDir", showDir);
			request.setAttribute("subDir", subDir);
			request.setAttribute("noFolderFlag", noFolderFlag);
			request.setAttribute("picFilePath", dirPath);

		} catch (Exception e) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
			throw e;
		}
		return "/core/funcs/picture/picture.jsp?seqId=" + picId + "&ascDescFlag=" + ascDesc + "&field=" + orderBy;
	}
	
/**
 * 取得图片的大小
 * @param request
 * @param response
 * @return
 * @throws Exception
 */
public String getPicDimension(HttpServletRequest request, HttpServletResponse response) throws Exception {
  String fileNameSrver = request.getParameter(T9ActionKeys.UPLOAD_FILE_NAME_SERVER);
  String filePath = fileNameSrver.replace("/", "\\");
  if (filePath.indexOf(":") != 1) {
    filePath = T9SysProps.getWebPath() + fileNameSrver.replace("/", "\\");
  }
  try {
    File newFile = new File(filePath);
    Image imgSrc = null; // 读入文件,构造Image对象
    try {
      imgSrc = ImageIO.read(newFile);
    } catch (Exception ex) {
      imgSrc = Sanselan.getBufferedImage(newFile);
    }
    int width = imgSrc.getWidth(null); // 得到源图宽
    int height = imgSrc.getHeight(null);
	  request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
    request.setAttribute(T9ActionKeys.RET_MSRG, "成功返回结果！");
    request.setAttribute(T9ActionKeys.RET_DATA, "{width:" + String.valueOf(width) + ", height:" + String.valueOf(height) + "}");
  } catch (Exception ex) {
    request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
    request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
    throw ex;
  }

  return "/core/inc/rtjson.jsp";
}

	/**
	 * 获取显示单张图片信息
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String showPicInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
		T9PictureLogic logic = new T9PictureLogic();
		String subDir = request.getParameter("fileDir");
		String orderBy = request.getParameter("viewType");
		String ascDesc = request.getParameter("ascDesc");
		String seqIdStr = request.getParameter("seqId");

		if (subDir == null) {
			subDir = "";
		}

		if (orderBy == null) {
			orderBy = "";
		}
		if (ascDesc == null) {
			ascDesc = "";
		}
		
		int seqId=0;
		if (seqIdStr!=null) {
			seqId=Integer.parseInt(seqIdStr);
		}

		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();

			T9Picture picture = logic.getPicInfoById(dbConn, seqId);
			String picPath = "";
			if (picture!=null) {
				picPath = picture.getPicPath() + "/" + subDir;
			}

			StringBuffer sb = new StringBuffer("[");
			String isImage = "^.*?(\\.(png|gif|jpg|bmp|PNG|GIF|JPG|BMP))$";
			// String
			// isImage="^.*?(\\.(gif|jpg|png|swf|swc|tiff|bmp|iff|jp2|jpx|jb2|jpc|xbm|wbmp))$";
			String isPath = "";
			boolean flag = false;
			if (picPath != null && picPath.trim().length() > 0) {
				File file = new File(picPath.trim());
				if (file.exists()) {
					File[] files = file.listFiles();

					for (File f : files) {
						if (f.isDirectory()) {
							isPath = "isDir";
						} else {
							isPath = "isFile";
						}
						Map m = new HashMap();
						if (!"tdoa_cache".equals(f.getName())) {
							if (f.getName().matches(isImage)) {
//								String filePath = f.getPath();
//								File newFile = new File(filePath);
//								Image imgSrc = null; // 读入文件,构造Image对象
//								try {
//									imgSrc = ImageIO.read(newFile);
//								} catch (Exception ex) {
//									imgSrc = Sanselan.getBufferedImage(newFile);
//								}
//								int width = imgSrc.getWidth(null); // 得到源图宽
//								int height = imgSrc.getHeight(null);
//
								long fileSize = 0;
								if (!"isDir".equals(isPath)) {
									fileSize = f.length();
								}

								String fileType = T9FileUtility.getFileExtName(f.getAbsolutePath());

								m.put("picName", f.getName());
								m.put("fileSize", String.valueOf(fileSize));
								m.put("fileTime", T9Utility.getDateTimeStr(new Date(f.lastModified())));
								m.put("imgWidth", String.valueOf(0));
								m.put("imgHeight", String.valueOf(0));
								m.put("fileType", fileType);
								list.add(m);
							}
						}
					}
				}
			}

			if (list != null && list.size() > 0) {

				if ("NAME".equals(orderBy.trim())) {

					if ("1".equals(ascDesc.trim())) {
						T9Utility.sortDesc(list, "picName", T9MapComparator.TYPE_STR);
					} else {
						T9Utility.sortAsc(list, "picName", T9MapComparator.TYPE_STR);
					}

				} else if ("SIZE".equals(orderBy.trim())) {
					if ("1".equals(ascDesc.trim())) {
						T9Utility.sortDesc(list, "fileSize", T9MapComparator.TYPE_LONG);
					} else {
						T9Utility.sortAsc(list, "fileSize", T9MapComparator.TYPE_LONG);
					}
				} else if ("TYPE".equals(orderBy.trim())) {
					if ("1".equals(ascDesc.trim())) {
						T9Utility.sortDesc(list, "fileType", T9MapComparator.TYPE_STR);
					} else {
						T9Utility.sortAsc(list, "fileType", T9MapComparator.TYPE_STR);
					}
				} else if ("TIME".equals(orderBy.trim())) {
					if ("1".equals(ascDesc.trim())) {
						T9Utility.sortDesc(list, "fileTime", T9MapComparator.TYPE_STR);
					} else {
						T9Utility.sortAsc(list, "fileTime", T9MapComparator.TYPE_STR);
					}
				}

				for (int i = 0; i < list.size(); i++) {
					Map<String, String> map2 = new HashMap<String, String>();
					map2 = list.get(i);
					String nameString = map2.get("picName");

					long fileSizeStr = Long.parseLong(map2.get("fileSize"));
					String fileTimeStr = map2.get("fileTime");
					String imgWidthStr = map2.get("imgWidth");
					String imgHeightStr = map2.get("imgHeight");

					sb.append("{");
					sb.append("picName:\"" + T9Utility.encodeSpecial(nameString) + "\"");
					sb.append(",fileSize:\"" + fileSizeStr + "\"");
					sb.append(",fileTime:\"" + fileTimeStr + "\"");
					sb.append(",imgWidth:\"" + imgWidthStr + "\"");
					sb.append(",imgHeight:\"" + imgHeightStr + "\"");
					sb.append("},");
					flag = true;
				}
				if (flag) {
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
	 * 上传批量文件
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String uploadFile(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String subDir = request.getParameter("subDir");
		String seqIdStr = request.getParameter("seqId");

		int seqId = 0;
		if (seqIdStr != null && !"".equals(seqIdStr.trim())) {
			seqId = Integer.parseInt(seqIdStr);
		}

		if (subDir == null) {
			subDir = "";
		}

		T9PictureLogic logic = new T9PictureLogic();

		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			T9Picture picture = logic.getPicInfoById(dbConn, seqId);
			String picPath = "";
			if (picture != null) {
				picPath = T9Utility.null2Empty(picture.getPicPath()) + "/" + subDir;
			}

			T9FileUploadForm fileForm = new T9FileUploadForm();
			fileForm.parseUploadRequest(request);
			String fileExists = fileForm.getExists(picPath);

			if (fileExists != null) {
				response.setCharacterEncoding(T9Const.DEFAULT_CODE);
				response.setContentType("text/html;charset=UTF-8");
				PrintWriter pw = response.getWriter();
				pw.println("-ERR 文件\"" + fileExists + "\"已经存在！");
				pw.flush();
				return null;
			}

			Iterator<String> iKeys = fileForm.iterateFileFields();
			while (iKeys.hasNext()) {
				String fieldName = iKeys.next();
				String fileName = fileForm.getFileName(fieldName);
				if (T9Utility.isNullorEmpty(fileName)) {
					continue;
				}
				File file = new File(picPath+"/" + fileName);
				if (file!=null && !file.exists()) {
					fileForm.saveFile(fieldName, picPath + File.separator + fileName);
				}else {
					StringBuffer buffer = new StringBuffer();
					String fileType = "";
					String nameTitle = "";
					if (fileName.lastIndexOf(".") != -1) {
						fileType = fileName.substring(fileName.lastIndexOf(".")); // .doc
						nameTitle = fileName.substring(0, fileName.lastIndexOf("."));
					}
					logic.uploadEexistsFile(buffer,picPath, fileName, nameTitle, fileType);
					String newFileName = buffer.toString().trim();
					if (!T9Utility.isNullorEmpty(newFileName)) {
						fileForm.saveFile(fieldName, picPath  + File.separator +newFileName);
					}
				}
//				fileForm.saveFile(fieldName, picPath  + File.separator + fileName);
			}

			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "成功返回结果！");
		} catch (Exception ex) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
			throw ex;
		}
		return "/core/inc/rtjson.jsp";
	}

	/**
	 * 上传单个文件
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String uploadSingleFile(HttpServletRequest request, HttpServletResponse response) throws Exception {
		T9FileUploadForm fileForm = new T9FileUploadForm();
		fileForm.parseUploadRequest(request);

		String subDir = fileForm.getParameter("fileSubDir");
		String seqIdStr = request.getParameter("seqId");

		String ascDescFlag = fileForm.getParameter("ascDescFlag");
		String field = fileForm.getParameter("field");

		int seqId = 0;
		if (seqIdStr != null) {
			seqId = Integer.parseInt(seqIdStr);
		}

		T9PictureLogic logic = new T9PictureLogic();

		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			T9Picture picture = logic.getPicInfoById(dbConn, seqId);
			String picPath = "";
			if (picture != null) {
				picPath = T9Utility.null2Empty(picture.getPicPath()) + "/" + subDir;
			}

			if (picPath != null) {
				Iterator<String> iKeys = fileForm.iterateFileFields();
				while (iKeys.hasNext()) {
					String fieldName = iKeys.next();
					String fileName = fileForm.getFileName(fieldName);
					if (T9Utility.isNullorEmpty(fileName)) {
						continue;
					}
					File file = new File(picPath+"/" + fileName);
					if (file!=null && !file.exists()) {
						fileForm.saveFile(fieldName, picPath  + File.separator +fileName);
					}else {
						StringBuffer buffer = new StringBuffer();
						String fileType = "";
						String nameTitle = "";
						if (fileName.lastIndexOf(".") != -1) {
							fileType = fileName.substring(fileName.lastIndexOf(".")); // .doc
							nameTitle = fileName.substring(0, fileName.lastIndexOf("."));
						}
						logic.uploadEexistsFile(buffer,picPath, fileName, nameTitle, fileType);
						String newFileName = buffer.toString();
						if (!T9Utility.isNullorEmpty(newFileName)) {
							fileForm.saveFile(fieldName, picPath + File.separator + newFileName.trim());
						}
					}
				}
			}

			request.setAttribute("seqId", seqId);
			request.setAttribute("subDir", subDir);
			request.setAttribute("ascDescFlag", ascDescFlag);
			request.setAttribute("field", field);

			// request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			// request.setAttribute(T9ActionKeys.RET_MSRG, "成功返回结果！");

		} catch (Exception ex) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
			throw ex;
		}

		// return "/core/funcs/picture/loadPicture.jsp?seqId=" + seqId + "&subDir="
		// + subDir + "&ascDescFlag=" + ascDescFlag
		// + "&field=" + field;

		// return
		// "/t9/core/funcs/picture/act/T9PictureAct/getPicInfoById.act?seqId=" +
		// seqId + "&subDir=" + subDir + "&ascDescFlag=" + ascDescFlag
		// + "&field=" + field;

		return "/core/funcs/picture/picUploadResult.jsp";
	}

	/**
	 * 新建子文件夹
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String newFolder(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String subDir = request.getParameter("subDir"); // 路径
		String folderName = request.getParameter("folderName"); // 修改后的文件夹名
		String seqIdStr = request.getParameter("seqId");
		int seqId = 0;
		if (seqIdStr != null && !"".equals(seqIdStr.trim())) {
			seqId = Integer.parseInt(seqIdStr);
		}

		String data = "";
		boolean flag = false;
		String sucuss = "";
		String isExist = "";

		T9PictureLogic logic = new T9PictureLogic();

		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			T9Picture picture = logic.getPicInfoById(dbConn, seqId);
			if (picture != null) {
				subDir = picture.getPicPath() + "/" + subDir + "/" + folderName;
			}

			if (subDir != null && subDir.trim().length() > 0) {
				File file = new File(subDir);
				if (!file.exists()) {
					if (file.mkdir()) {
						request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
						request.setAttribute(T9ActionKeys.RET_MSRG, "创建成功！");
						sucuss = "创建成功";
						flag = true;
					} else {
						sucuss = "创建不成功";
					}
				} else {
					isExist = "文件夹已存在";
				}

			}

			data = "{sucuss:\"" + sucuss + "\",isExist:\"" + isExist + "\",flag:\"" + flag + "\"}";
			request.setAttribute(T9ActionKeys.RET_DATA, data);
		} catch (Exception ex) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
			throw ex;
		}

		return "/core/inc/rtjson.jsp";
	}

	/**
	 * 文件夹重命名
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String folderRename(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String subDir = request.getParameter("subDir");
		String folderName = request.getParameter("folderName");
		String seqIdStr = request.getParameter("seqId");

		int seqId = 0;
		if (seqIdStr != null && !"".equals(seqIdStr.trim())) {
			seqId = Integer.parseInt(seqIdStr);
		}

		if (subDir == null) {
			subDir = "";
		}
		if (folderName == null) {
			folderName = "";
		}
		String newSubDir = "";
		if (subDir.lastIndexOf("/") != -1) {
			newSubDir = subDir.substring(0, subDir.lastIndexOf("/")) + "/" + folderName;
		} else {
			newSubDir = folderName;
		}

		String data = "";
		boolean flag = false;
		String isExist = "";

		T9PictureLogic logic = new T9PictureLogic();
		String newFolderPath = "";
		String curDirPath = "";
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			T9Picture picture = logic.getPicInfoById(dbConn, seqId);
			if (picture != null) {
				newFolderPath = picture.getPicPath() + "/" + newSubDir; // 新的路径
				curDirPath = picture.getPicPath() + "/" + subDir; // 原来的路径			}
			String returnSubDir = "";
			if (folderName != null && curDirPath != null && curDirPath.trim().length() > 0) {
				File newFile = new File(newFolderPath);
				File file = new File(curDirPath);
				if (newFile!=null && file!=null) {
					if (!newFile.exists()) {
						file.renameTo(newFile);
						returnSubDir = newSubDir;
						request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
						request.setAttribute(T9ActionKeys.RET_MSRG, "成功重命名文件夹");
						flag = true;
					}
				}
			}
			data = "{subDir:\"" + T9Utility.encodeSpecial(returnSubDir) + "\",isExist:\"" + isExist + "\",flag:\"" + flag + "\"}";
			request.setAttribute(T9ActionKeys.RET_DATA, data);
		} catch (Exception ex) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
			throw ex;
		}
		return "/core/inc/rtjson.jsp";
	}

	/**
	 * 删除文件夹级其下的文件
	 * 
	 * 
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String delFolder(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String subDir = request.getParameter("subDir"); // cc/dd2
		String seqIdStr = request.getParameter("seqId");

		int seqId = 0;
		if (seqIdStr != null && !"".equals(seqIdStr.trim())) {
			seqId = Integer.parseInt(seqIdStr);
		}

		String newSubDir = "";
		if (subDir.lastIndexOf("/") != -1) {
			newSubDir = subDir.substring(0, subDir.lastIndexOf("/"));
		}

		boolean flag = false;
		T9PictureLogic logic = new T9PictureLogic();

		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			T9Picture picture = logic.getPicInfoById(dbConn, seqId);
			if (picture != null) {
				subDir = picture.getPicPath() + "/" + subDir;
			}

			if (subDir != null && subDir.trim().length() > 0) {
				T9FileUtility.deleteAll(subDir);
				flag = true;
			}

			String data = "{subDir:\"" + newSubDir + "\",flag:\"" + flag + "\"}";
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "成功删除文件夹");
			request.setAttribute(T9ActionKeys.RET_DATA, data);
		} catch (Exception ex) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
			throw ex;
		}

		return "/core/inc/rtjson.jsp";
	}

	/**
	 * 返回上一级目录
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String comeBack(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String subDir = request.getParameter("subDir"); // c/dd2
		String seqIdStr = request.getParameter("seqId");

		if (subDir == null) {
			subDir = "";
		}

		int seqId = 0;
		if (seqIdStr != null && !"".equals(seqIdStr)) {
			seqId = Integer.parseInt(seqIdStr);
		}

		T9PictureLogic logic = new T9PictureLogic();

		boolean flag = false;
		if (!"".equals(subDir.trim())) {
			flag = true;
		}

		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();

			if (subDir.lastIndexOf('/') != -1) {
				subDir = subDir.substring(0, subDir.lastIndexOf('/'));
			} else {
				subDir = "";
			}
			String data = "{subDir:\"" + subDir + "\",flag:\"" + flag + "\",seqId:\"" + seqId + "\"}";
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "成功返回");
			request.setAttribute(T9ActionKeys.RET_DATA, data);
		} catch (Exception ex) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
			throw ex;
		}
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
		String subDir = request.getParameter("subDir"); // d:/cc/dd2
		String delNameStr = request.getParameter("fileStr"); // 文件名，以*号区分
		String seqIdStr = request.getParameter("seqId");

		int seqId = 0;
		if (seqIdStr != null && !"".equals(seqIdStr.trim())) {
			seqId = Integer.parseInt(seqIdStr);
		}

		T9PictureLogic logic = new T9PictureLogic();

		String fileList = URLDecoder.decode(delNameStr);
		String[] names = null;
		String isImage = "^.*?(\\.(png|gif|jpg|bmp|PNG|GIF|JPG|BMP))$";
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			T9Picture picture = logic.getPicInfoById(dbConn, seqId);
			String picPath = "";
			if (picture != null) {
				picPath = picture.getPicPath() + "/" + subDir;
			}

			if (picPath != null && picPath.trim().length() > 0) {
				if (fileList != null && !"".equals(fileList)) {
					names = fileList.substring(0, fileList.length() - 1).split("\\*");
				}
				if (names.length != 0) {
					for (int i = 0; i < names.length; i++) {
						T9FileUtility.deleteAll(picPath + "/" + names[i]);
						if (names[i].matches(isImage)) {
							T9FileUtility.deleteAll(picPath + "/tdoa_cache/" + names[i]);
						}
					}
				}
				request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
				request.setAttribute(T9ActionKeys.RET_MSRG, "成功返回");
			}

		} catch (Exception ex) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
			throw ex;
		}
		return "/core/inc/rtjson.jsp";
	}

	/**
	 * 获取图片上传、管理权限信息
	 * 
	 * 
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String getPrivInfoById(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String seqId = request.getParameter("seqId");
		String subDir = request.getParameter("subDir");

		// String fileDirPath = "";

		// 获取登录用户信息
		T9Person loginUser = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
		int loginUserSeqId = loginUser.getSeqId();
		int loginUserDeptId = loginUser.getDeptId();
		String loginUserRoleId = loginUser.getUserPriv();

		T9PictureLogic logic = new T9PictureLogic();
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			// T9PictureLogic logic = new T9PictureLogic();

			Map map = new HashMap();
			map.put("SEQ_ID", Integer.parseInt(seqId));
			String data = logic.getPrivate(dbConn, map, loginUserSeqId, loginUserDeptId, loginUserRoleId, subDir);

			// System.out.println("data:" + data);
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
	public String getPicFolderPathBySeqId(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String seqIdStr = request.getParameter("seqId");

		int seqId = 0;
		if (seqIdStr != null && !"".equals(seqIdStr)) {
			seqId = Integer.parseInt(seqIdStr);
		}

		T9PictureLogic logic = new T9PictureLogic();

		Connection dbConn;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();

			T9Picture picture = logic.getPicInfoById(dbConn, seqId);
			String picPath = picture.getPicPath() == null ? "" : picture.getPicPath();

			String data = "{folderPath:\"" + picPath + "\" }";

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

	public static void main(String[] args) {
		String folderPath = "E:/ee";

		File file = new File(folderPath);
		// System.out.println(file.getName());
		// System.out.println(file.getPath());

		File[] files = file.listFiles();
		String subDir = "";
		String fileName = "";
		for (File f : files) {
			// System.out.println(f.getPath());
			// System.out.println("fileName========="+f.getName()+"===");
			if (f.isDirectory()) {
				// subDir=f.getName();
				// System.out.println("directory==="+subDir);
				// System.out.println("directory==="+f.getPath());
			} else {
				// fileName=file.getName()+"  ";
				// System.out.println("fileName==="+fileName);
			}
			// System.out.println("subDir:"+subDir+"===fileName==="+fileName);
		}

	}

}
