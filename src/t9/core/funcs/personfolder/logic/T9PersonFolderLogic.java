package t9.core.funcs.personfolder.logic;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import t9.core.funcs.email.logic.T9InnerEMailUtilLogic;
import t9.core.funcs.personfolder.data.T9FileContent;
import t9.core.funcs.personfolder.data.T9FileSort;
import t9.core.global.T9SysProps;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.file.T9FileUtility;

public class T9PersonFolderLogic {
	private static Logger log = Logger.getLogger("t9.core.funcs.personfolder.logic");

	public List<T9FileSort> getFileSorts(Connection dbConn, Map map) throws Exception {
		T9ORM orm = new T9ORM();
		return orm.loadListSingle(dbConn, T9FileSort.class, map);
	}

	public List<T9FileSort> getFileSorts(Connection dbConn, String[] filters) throws Exception {
		T9ORM orm = new T9ORM();
		return orm.loadListSingle(dbConn, T9FileSort.class, filters);
	}

	/**
	 * 判断是否有子级文件夹,考虑权限。
	 * 
	 * 
	 * @param dbConn
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public int isHaveChild(Connection dbConn, int id) throws Exception {
		T9ORM orm = new T9ORM();
		Map map = new HashMap();
		map.put("SORT_PARENT", id);
		List<T9FileSort> list = orm.loadListSingle(dbConn, T9FileSort.class, map);
		if (list.size() > 0) {
			return 1;
		} else {
			return 0;
		}
	}

	/**
	 * 查询文件夹信息
	 * 
	 * @param dbConn
	 * @param seqId
	 * @return
	 * @throws Exception
	 */
	public T9FileSort getFolderInfoById(Connection dbConn, int seqId) throws Exception {
		T9ORM orm = new T9ORM();
		return (T9FileSort) orm.loadObjSingle(dbConn, T9FileSort.class, seqId);
	}

	/**
	 * 递归获取文件夹名路径
	 * 
	 * @param dbConn
	 * @param seqId
	 * @return
	 * @throws Exception
	 */
	public void getSortNamePath(Connection dbConn, int seqId, StringBuffer buffer) throws Exception {
		T9FileSort fileSort = getSortNameById(dbConn, seqId);
		int sortParent = 0;
		String sortName = "";

		if (fileSort != null) {
			sortParent = fileSort.getSortParent();
			sortName = fileSort.getSortName();
			// 处理特殊字符
			sortName = sortName.replaceAll("[\\\\/:*?\"<>|]", "");
			sortName = sortName.replaceAll("[\n-\r]", "<br>");
			sortName = sortName.replace("\"", "\\\"");

		}
		buffer.append(sortName + "/,");
		boolean flag = isHaveSortParent(dbConn, sortParent);
		if (flag) {
			getSortNamePath(dbConn, sortParent, buffer);
		}
	}

	/**
	 * 判断是否还有子级文件夹
	 * 
	 * 
	 * @param dbConn
	 * @param sortParent
	 * @return
	 * @throws Exception
	 */
	public boolean isHaveSortParent(Connection dbConn, int sortParent) throws Exception {
		Boolean flag = false;
		T9ORM orm = new T9ORM();
		Map map = new HashMap();
		map.put("SEQ_ID", sortParent);
		List<T9FileSort> list = orm.loadListSingle(dbConn, T9FileSort.class, map);
		if (list.size() > 0) {
			flag = true;
		}
		return flag;
	}

	/**
	 * 查询文件夹信息
	 * 
	 * 
	 * @param dbConn
	 * @param seqId
	 * @return
	 * @throws Exception
	 */
	public T9FileSort getSortNameById(Connection dbConn, int seqId) throws Exception {
		T9ORM orm = new T9ORM();
		return (T9FileSort) orm.loadObjSingle(dbConn, T9FileSort.class, seqId);
	}

	/**
	 * 获得userId字段值(没","和"|")
	 * 
	 * @param dbConn
	 * @param seqId
	 * @param action
	 * @return
	 * @throws Exception
	 */
	public String getUserId(Connection dbConn, int seqId, String action) throws Exception {
		// T9ORM orm = new T9ORM();
		T9FileSort fileFolder = getFolderInfoById(dbConn, seqId);
		String ids = T9Utility.null2Empty(fileFolder.getUserId());
		if (ids == null) {
			ids = "";
		}
		return ids;
	}

	/**
	 * 获取访问权限：根据ids串返回与登录的seqId比较判断是否相等，返回boolean类型。
	 * 
	 * @param ids
	 * @return
	 * @throws Exception
	 * @throws Exception
	 */
	public boolean getUserIdStr(int userSeqId, String ids, Connection dbConn) throws Exception, Exception {
		boolean flag = false;
		if (ids != null && !"".equals(ids.trim())) {
			String[] aId = ids.split(",");
			for (String tmp : aId) {
				if (!"".equals(tmp)) {
					if (Integer.parseInt(tmp) == userSeqId) {
						flag = true;
					}
				}
			}
		}
		return flag;
	}

	public void saveFileSortInfo(Connection dbConn, T9FileSort fileSort) {
		T9ORM orm = new T9ORM();
		try {
			orm.saveSingle(dbConn, fileSort);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取最大的SeqId值
	 * 
	 * 
	 * @param dbConn
	 * @return
	 */
	public T9FileSort getMaxSeqId(Connection dbConn) {
		// String sql="select MAX(SEQ_ID) from file_sort";
		String sql = "select SEQ_ID,SORT_NAME from FILE_SORT where SEQ_ID=(select MAX(SEQ_ID) from FILE_SORT ) ";
		T9FileSort fileSort = null;
		int seqId = 0;
		String sortName = "";
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = dbConn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				fileSort = new T9FileSort();
				seqId = rs.getInt("SEQ_ID");
				sortName = rs.getString("SORT_NAME");
				fileSort.setSeqId(seqId);
				fileSort.setSortName(sortName);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			T9DBUtility.close(ps, rs, log);
		}
		return fileSort;
	}

	public T9FileSort getFileSortInfoById(Connection dbConn, Map map) throws NumberFormatException, Exception {
		T9ORM orm = new T9ORM();

		return (T9FileSort) orm.loadObjSingle(dbConn, T9FileSort.class, map);
	}

	public void updateSingleObj(Connection dbConn, T9FileSort fileSort) throws Exception {
		T9ORM orm = new T9ORM();
		orm.updateSingle(dbConn, fileSort);
	}

	public T9FileSort getFileSortInfoById(Connection dbConn, String seqId) throws NumberFormatException, Exception {
		T9ORM orm = new T9ORM();

		return (T9FileSort) orm.loadObjSingle(dbConn, T9FileSort.class, Integer.parseInt(seqId));
	}

	/**
	 * 根据seqid串返回一个名字串
	 * 
	 * @param ids
	 * @return
	 * @throws Exception
	 * @throws Exception
	 */
	public String getNameBySeqIdStr(String ids, Connection conn) throws Exception, Exception {
		String names = "";
		if (ids != null && !"".equals(ids.trim())) {
			if (ids.endsWith(",")) {
				ids = ids.substring(0, ids.length() - 1);
			}
			String query = "select USER_NAME from PERSON where SEQ_ID in (" + ids + ")";
			Statement stm = null;
			ResultSet rs = null;
			try {
				stm = conn.createStatement();
				rs = stm.executeQuery(query);
				while (rs.next()) {
					names += rs.getString("USER_NAME") + ",";
				}
			} catch (Exception ex) {
				throw ex;
			} finally {
				T9DBUtility.close(stm, rs, null);
			}
		}
		if (names.endsWith(",")) {
			names = names.substring(0, names.length() - 1);
		}
		return names;
	}

	/**
	 * 得到不是本次登录用户id串
	 * 
	 * @param dbConn
	 * @param loginUserDeptId
	 * @param ids
	 * @return
	 */
	public String getNoLoginIdStr(Connection dbConn, int loginUserSeqId, String ids) {
		String idstr = "";
		if (ids != null && !"".equals(ids)) {
			String[] aId = ids.split(",");
			for (String tmp : aId) {
				if (!"".equals(tmp.trim())) {
					if (Integer.parseInt(tmp) != loginUserSeqId) {
						idstr += tmp + ",";
					}
				} else if (!"".equals(tmp.trim()) && "0".equals(tmp.trim())) {

					idstr += tmp + ",";

				}
			}
		}

		if (!"".equals(idstr)) {
			idstr = idstr.substring(0, idstr.length() - 1);
		}

		return idstr.trim();
	}

	/**
	 * 递归删除文件夹及下的所有文件信息
	 * 
	 * @param dbConn
	 * @param fileSort
	 * @throws Exception
	 */
	public void delFileSortInfoById(Connection dbConn, T9FileSort fileSort) throws Exception {
		T9PersonFileContentLogic contentLogic = new T9PersonFileContentLogic();
		String separator = File.separator;
		String filePath = T9SysProps.getAttachPath() + separator + "file_folder" + separator;

		String seqIdStrs = "";

		T9ORM orm = new T9ORM();
		Map map = new HashMap();
		map.put("SORT_PARENT", fileSort.getSeqId());
		List<T9FileSort> fileSortList = orm.loadListComplex(dbConn, T9FileSort.class, map);

		Map contentMap = new HashMap();
		contentMap.put("SORT_ID", fileSort.getSeqId());
		List<T9FileContent> fileContents = new ArrayList<T9FileContent>();
		fileContents = contentLogic.getFileContentsInfo(dbConn, contentMap);
		if (fileContents != null && fileContents.size() > 0) {
			for (int i = 0; i < fileContents.size(); i++) {
				T9FileContent content = fileContents.get(i);
				seqIdStrs += content.getSeqId() + ",";
			}
			contentLogic.delFile(dbConn, seqIdStrs.substring(0, seqIdStrs.length() - 1), filePath);
		}

		orm.deleteSingle(dbConn, fileSort);
		for (int i = 0; i < fileSortList.size(); i++) {
			delFileSortInfoById(dbConn, fileSortList.get(i));
		}
	}

	/**
	 * 剪贴更新上级节点信息
	 */
	public void updateFolderInfoById(Connection dbConn, int parentId, int seqId,Map<Object,Object> nodeNameMap) throws Exception {
		 T9ORM orm = new T9ORM();
		PreparedStatement ps = null;
		String sql = "UPDATE FILE_SORT SET SORT_PARENT=?,SORT_NAME=? where SEQ_ID=? ";
		try {
			T9FileSort fileSort2= (T9FileSort) orm.loadObjSingle(dbConn, T9FileSort.class, seqId);
			String newSortName = "";
			String fileSort2Name = "";
			if (fileSort2!=null) {
				fileSort2Name = T9Utility.null2Empty(fileSort2.getSortName());
			}
			boolean haveFlag = this.isExistFile(dbConn, parentId, fileSort2Name);
			if (haveFlag) {
				StringBuffer buffer = new StringBuffer();
				this.copyExistFile(dbConn, buffer, parentId, fileSort2Name);
				newSortName = buffer.toString().trim();
			}else {
				newSortName = T9Utility.null2Empty(fileSort2Name);
			}
			ps = dbConn.prepareStatement(sql);
			ps.setInt(1, parentId);
			ps.setString(2, newSortName);
			ps.setInt(3, seqId);
			ps.executeUpdate();
			nodeNameMap.put("sortName", newSortName);
			
			
		} catch (Exception e) {
			throw e;
		}finally{
			T9DBUtility.close(ps, null, log);
		}
	}

	/**
	 * 得到本级以及其所有子文件夹的对象以及每个文件夹属于第几级
	 * 
	 * @param dbConn
	 * @param seqId
	 * @return
	 * @throws Exception
	 */
	public List getAllFolderList(Connection dbConn, int seqId, int parentId, List listTemp,Map<Object,Object> nodeNameMap, int maxSeqId) throws Exception {

		String seqIdString = "";
		T9ORM orm = new T9ORM();
		Map map = new HashMap();
		//System.out.println(seqId);
		map.put("SORT_PARENT", seqId);
		List<T9FileSort> list = new ArrayList<T9FileSort>();
		list = orm.loadListSingle(dbConn, T9FileSort.class, map);
		if (seqId > maxSeqId) {
			return list;
		}

		int newParent = this.getChildFolder(dbConn, seqId, parentId, listTemp,nodeNameMap); // 返回新文件夹的seqId，

		T9PersonFileContentLogic contentLogic = new T9PersonFileContentLogic();
		Map contentMap = new HashMap();
		contentMap.put("SORT_ID", seqId);

		List<T9FileContent> fileContents = contentLogic.getFileContentsInfo(dbConn, contentMap);
		if (fileContents != null && fileContents.size() > 0) {
			for (int i = 0; i < fileContents.size(); i++) {
				T9FileContent content = fileContents.get(i);
				seqIdString += content.getSeqId() + ",";
			}
			copyAllFile(dbConn, newParent, seqIdString.substring(0, seqIdString.length() - 1));
		}

		for (int i = 0; i < list.size(); i++) {
			T9FileSort dimension = list.get(i);
			getAllFolderList(dbConn, dimension.getSeqId(), newParent, listTemp,nodeNameMap, maxSeqId);
		}
		return listTemp;
	}

	/**
	 * 以点击粘贴文件夹的信息为准，创建一个新的文件夹
	 * 
	 * @param dbConn
	 * @param seqId
	 * @param parentId
	 * @param listTemp
	 * @return
	 * @throws Exception
	 */
	public int getChildFolder(Connection dbConn, int seqId, int parentId, List listTemp, Map<Object,Object> nodeNameMap) throws Exception {
		T9ORM orm = new T9ORM();

		// 如果sortParentId=0时,查数据要条件,file_type=4,userId=loginUser

		// orm.loadObjComplex(dbConn, cls, filters)

		T9FileSort fileSort = (T9FileSort) orm.loadObjSingle(dbConn, T9FileSort.class, parentId); // 获取点击粘贴时的文件夹对象信息		T9FileSort fileSort2 = (T9FileSort) orm.loadObjSingle(dbConn, T9FileSort.class, seqId); // 获取点击复制时的文件夹对象信息		String newSortName = "";
		String fileSort2Name = "";
		if (fileSort2!=null) {
			fileSort2Name = T9Utility.null2Empty(fileSort2.getSortName());
		}
		boolean haveFlag = this.isExistFile(dbConn, parentId, fileSort2Name);
		if (haveFlag) {
			StringBuffer buffer = new StringBuffer();
			this.copyExistFile(dbConn, buffer, parentId, fileSort2Name);
			newSortName = buffer.toString().trim();
		}else {
			newSortName = T9Utility.null2Empty(fileSort2Name);
		}
		if (parentId == 0) {
			T9FileSort sort = new T9FileSort();
			sort.setSortParent(parentId);
			sort.setSortName(newSortName);
			sort.setUserId(String.valueOf(fileSort2.getUserId()));
			sort.setSortType("4");
			orm.saveSingle(dbConn, sort);
		} else {
			fileSort.setSortParent(parentId);
			fileSort.setSortName(newSortName);
			orm.saveSingle(dbConn, fileSort);
		}
		T9FileSort maxfiSort = getMaxSeqId(dbConn);
		listTemp.add(maxfiSort.getSeqId());
		nodeNameMap.put("sortName", newSortName);
		return maxfiSort.getSeqId();
	}

	/**
	 * 级联复制文件
	 * 
	 * @param dbConn
	 * @param seqIdStrs
	 * @param filePath
	 * @throws Exception
	 * @throws NumberFormatException
	 */
	public void copyAllFile(Connection dbConn, int sortId, String seqIdStrs) throws NumberFormatException, Exception {
		String separator = File.separator;
		String filePath = T9SysProps.getAttachPath() + separator + "file_folder" + separator;

		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyMM");
		String currDate = format.format(date);
		// String newAttaName = "";
		// String newAttaId = "";

		T9InnerEMailUtilLogic emut = new T9InnerEMailUtilLogic();
		String rand = emut.getRandom();

		T9ORM orm = new T9ORM();
		T9PersonFileContentLogic contentLogic = new T9PersonFileContentLogic();
		String[] seqIdStr = seqIdStrs.split(",");
		if (!"".equals(seqIdStrs) && seqIdStrs.split(",").length > 0) {
			for (String seqId : seqIdStr) {
				String newAttaId = "";
				String newAttaName = "";
				boolean isHave = false;
				T9FileContent fileContent = contentLogic.getFileContentInfoById(dbConn, Integer.parseInt(seqId));
				String attachmentId = T9Utility.null2Empty(fileContent.getAttachmentId());
				String attachmentName = T9Utility.null2Empty(fileContent.getAttachmentName());
				// String[] attIdArray = attachmentId.split(",");
				// String[] attNameArray = attachmentName.split("\\*");

				String[] attIdArray = {};
				String[] attNameArray = {};
				if (attachmentId != null && attachmentName != null) {
					attIdArray = attachmentId.split(",");
					attNameArray = attachmentName.split("\\*");

				}

				for (int i = 0; i < attIdArray.length; i++) {
					Map<String, String> map = contentLogic.getFileName(attIdArray[i], attNameArray[i]);
					if (map.size() != 0) {
						Set<String> set = map.keySet();
						// 遍历Set集合
						for (String keySet : set) {
							String key = keySet;
							String keyValue = map.get(keySet);
							String attaIdStr = contentLogic.getAttaId(keySet);
							String newAttName = rand + "_" + keyValue;
							String fileNameValue = attaIdStr + "_" + keyValue;
							String fileFolder = contentLogic.getFilePathFolder(key);

							String oldFileNameValue = attaIdStr + "." + keyValue;

							File file = new File(filePath + File.separator + fileFolder + File.separator + fileNameValue);
							File oldFile = new File(filePath + File.separator + fileFolder + File.separator + oldFileNameValue);

							if (file.exists()) {
								T9FileUtility.copyFile(file.getAbsolutePath(), filePath + File.separator + currDate + File.separator + newAttName);
								newAttaId += currDate + "_" + rand + ",";
								newAttaName += keyValue + "*";
								isHave = true;
							} else if (oldFile.exists()) {
								T9FileUtility.copyFile(oldFile.getAbsolutePath(), filePath + File.separator + currDate + File.separator + newAttName);
								newAttaId += currDate + "_" + rand + ",";
								newAttaName += keyValue + "*";
								isHave = true;
							}

							// String[] fileList = file.list();
							// if (fileList != null && fileList.length > 0) {
							// for (String fileStr : fileList) {
							// // 遍历文件夹目录下的文件
							// if (fileStr.equals(fileNameValue.trim())) {
							// T9FileContent content = new T9FileContent();
							// newAttaId = currDate + "_" + rand + ",";
							// newAttaName = rand + "_" + keyValue;
							// T9FileUtility.copyFile(file + File.separator + fileStr,
							// filePath + File.separator + currDate + File.separator +
							// newAttaName);
							// content.setAttachmentId(newAttaId);
							// content.setAttachmentName(keyValue + "*");
							// content.setSendTime(T9Utility.parseTimeStamp());
							// content.setSortId(sortId);
							// content.setSubject(fileContent.getSubject());
							// orm.saveSingle(dbConn, content);
							//
							// break;
							// }
							// }
							// }

						}

					}

				}

				if (isHave) {
					T9FileContent content = new T9FileContent();
					content.setAttachmentId(newAttaId.trim());
					content.setAttachmentName(newAttaName.trim());
					content.setSendTime(T9Utility.parseTimeStamp());
					content.setSortId(sortId);
					content.setSubject(fileContent.getSubject());
					orm.saveSingle(dbConn, content);

				} else {
					// T9FileContent content = new T9FileContent();
					fileContent.setSortId(sortId);
					fileContent.setSendTime(T9Utility.parseTimeStamp());
					orm.saveSingle(dbConn, fileContent);

				}

			}

		}

	}

	/**
	 * 验证文件夹名是否存在（新建）
	 * 
	 * @param dbConn
	 * @param seqId
	 * @param loginUserSeqId
	 * @param folderName
	 * @return
	 */
	public boolean checkFolderName(Connection dbConn, int seqId, int loginUserSeqId, String folderName) {
		boolean isHave = false;
		String sortType = "4";
		boolean userFlag = false;

		try {

			if (seqId == 0) {
				String[] filters = { "SORT_PARENT=" + seqId + " and SORT_TYPE=" + sortType };
				List<T9FileSort> parentList = this.getFileSorts(dbConn, filters);
				if (parentList != null && parentList.size() > 0) {
					for (T9FileSort fileFolder : parentList) {
						String userId = this.getUserId(dbConn, fileFolder.getSeqId(), "USER_ID");
						userFlag = this.getUserIdStr(loginUserSeqId, userId, dbConn);
						if (userFlag) {
							String sortNameString = T9Utility.null2Empty(fileFolder.getSortName());
							if (folderName.trim().equals(sortNameString.trim())) {
								isHave = true;
								break;
							}

						}
					}

				}

			} else {
				String[] filters = { "SORT_PARENT=" + seqId };
				List<T9FileSort> parentList = this.getFileSorts(dbConn, filters);
				if (parentList != null && parentList.size() > 0) {
					for (T9FileSort fileFolder : parentList) {
						String sortNameString = T9Utility.null2Empty(fileFolder.getSortName());
						if (folderName.trim().equals(sortNameString.trim())) {
							isHave = true;
							break;
						}

					}

				}

			}

		} catch (Exception e) {
			//System.out.println(e.getMessage());
		}

		return isHave;

	}

	/**
	 * 验证文件夹名是否存在（编辑）
	 * 
	 * @param dbConn
	 * @param seqId
	 * @param loginUserSeqId
	 * @param folderName
	 * @return
	 * @throws Exception 
	 */
	public boolean checkEditFolder(Connection dbConn, int seqId, int loginUserSeqId, String folderName) throws Exception {
		boolean isHave = false;
		String sortType = "4";
		boolean userFlag = false;

		try {

			T9FileSort fileSort = this.getFolderInfoById(dbConn, seqId);
			int sortParentId = fileSort.getSortParent();

			if (sortParentId == 0) {
				String[] filters = { "SORT_PARENT=" + sortParentId + " and SORT_TYPE=" + sortType };
				List<T9FileSort> parentList = this.getFileSorts(dbConn, filters);
				if (parentList != null && parentList.size() > 0) {
					for (T9FileSort fileFolder : parentList) {
						String userId = this.getUserId(dbConn, fileFolder.getSeqId(), "USER_ID");
						userFlag = this.getUserIdStr(loginUserSeqId, userId, dbConn);
						if (userFlag) {
							int sortId = fileFolder.getSeqId();
							String sortNameString = T9Utility.null2Empty(fileFolder.getSortName());
							if (sortId != seqId) {
								if (folderName.trim().equals(sortNameString.trim())) {
									isHave = true;
									break;
								}
							}

						}
					}

				}

			} else {
				String[] filters = { "SORT_PARENT=" + sortParentId };
				List<T9FileSort> parentList = this.getFileSorts(dbConn, filters);
				if (parentList != null && parentList.size() > 0) {
					for (T9FileSort fileFolder : parentList) {
						int sortId = fileFolder.getSeqId();
						String sortNameString = T9Utility.null2Empty(fileFolder.getSortName());
						if (sortId != seqId) {
							if (folderName.trim().equals(sortNameString.trim())) {
								isHave = true;
								break;
							}
						}
					}
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return isHave;
	}
	
	/**
	 * 判断库是否已有文件夹
	 * 
	 * @param dbConn
	 * @param sortId
	 * @param subject
	 * @return
	 * @throws Exception
	 */
	public boolean isExistFile(Connection dbConn, int sortParentId, String sortName) throws Exception {
		boolean flag = false;
		int counter = 0;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "select count(SEQ_ID) from FILE_SORT where SORT_PARENT =? and SORT_NAME=?";
		try {
			stmt = dbConn.prepareStatement(sql);
			stmt.setInt(1, sortParentId);
			stmt.setString(2, sortName);
			rs = stmt.executeQuery();
			if (rs.next()) {
				counter = rs.getInt(1);
			}
			if (counter > 0) {
				flag = true;
			}
		} catch (Exception e) {
			throw e;
		} finally {
			T9DBUtility.close(stmt, rs, log);
		}
		return flag;
	}
	/**
	 * 文件夹里已存在的处理方法
	 * 
	 * @param dbConn
	 * @param buffer
	 * @param sortId
	 * @param subject
	 * @throws Exception
	 */
	public void copyExistFile(Connection dbConn, StringBuffer buffer, int sortParentId, String sortName) throws Exception {
		try {
			String newFileName = sortName + " - 复件";
			boolean isHave = this.isExistFile(dbConn, sortParentId, newFileName);
			if (!isHave) {
				buffer.append(newFileName);
			} else {
				copyExistFile(dbConn, buffer, sortParentId, newFileName);
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	
	

}
